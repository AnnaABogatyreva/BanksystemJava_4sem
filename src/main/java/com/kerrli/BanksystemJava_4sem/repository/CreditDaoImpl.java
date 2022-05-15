package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.entity.Currency;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CreditDaoImpl implements CreditDao {
    private final Session session;

    public CreditDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public CreditDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getCreditTermList() {
        return session.createQuery("FROM CreditTerm", CreditTerm.class).getResultList();
    }

    private String getSelectBlockLine(Credit credit) {
        CreditTerm creditTerm = session.get(CreditTerm.class, credit.getType());
        AccountDaoImpl accountDao = new AccountDaoImpl(session);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(credit.getOpenDate());
        calendar.add(Calendar.MONTH, creditTerm.getMonthCnt());
        Date endDate = calendar.getTime();
        Currency currency = session.get(Currency.class, "810");
        String queryString = "SELECT o FROM Operation o WHERE o.creditAccountNum = :account ORDER BY o.idOper";
        Query query = session.createQuery(queryString, Operation.class);
        query.setParameter("account", credit.getMainDutyAccountNum());
        query.setMaxResults(1);
        Operation operation = (Operation) query.getSingleResult();
        return "№" + credit.getId() + ": " + creditTerm.getDescript() + ", " +
                Lib.formatSum(creditTerm.getRate()) + "% годовых, сумма " +
                Lib.formatSum(operation.getSum()) + " " + currency.getIsoCode();
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getCreditList(int idClient) {
        String queryString = "FROM Credit WHERE idClient = :idClient AND closeDate IS NULL";
        Query query = session.createQuery(queryString, Credit.class);
        query.setParameter("idClient", idClient);
        List creditList = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < creditList.size(); i++) {
            Credit credit = (Credit) creditList.get(i);
            CreditExt creditExt = new CreditExt(credit, getSelectBlockLine(credit));
            res.add(creditExt);
        }
        return res;
    }

    private void buildCreditGraph(Credit credit) {
        CreditTerm creditTerm = session.get(CreditTerm.class, credit.getType());
        double creditSum = new AccountDaoImpl(session).checkBalance(credit.getMainDutyAccountNum());
        String queryString = "SELECT MAX(num) FROM CreditGraph WHERE id = :id";
        Query query = session.createQuery(queryString, Integer.class);
        query.setParameter("id", credit.getId());
        Integer num = (Integer) query.getSingleResult();
        if (num == null) num = 1;
        queryString = "SELECT o FROM OperDate o WHERE current = 1";
        OperDate tempDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        ArrayList<CreditGraph> graphList = new ArrayList<>();
        for (int i = 0; i < creditTerm.getMonthCnt(); i++) {
            CreditGraph creditGraph = new CreditGraph();
            creditGraph.setId(credit.getId());
            creditGraph.setNum(num.intValue());
            creditGraph.setPayDate(Lib.addMonths(tempDate.getOperDate(), i + 1));
            graphList.add(new CreditGraph(credit.getId(), num.intValue(),
                    Lib.addMonths(tempDate.getOperDate(), i + 1), 0, 0, 0));
        }
        int totalDays = Lib.diffDate(tempDate.getOperDate(), graphList.get(graphList.size() - 1).getPayDate());
        double tailSum = creditSum;
        for (int i = 0; i < graphList.size(); i++) {
            Date prevDate = (i == 0) ? tempDate.getOperDate() : graphList.get(i - 1).getPayDate();
            int monthDays = Lib.diffDate(prevDate, graphList.get(i).getPayDate());
            double percentSum = Lib.roundSum(tailSum * creditTerm.getRate() / 100 / 365 * monthDays);
            double mainDutySum = 0;
            if (i < graphList.size() - 1) {
                mainDutySum = Lib.roundSum(creditSum * monthDays / totalDays);
                tailSum -= mainDutySum;
            }
            else {
                mainDutySum = tailSum;
                tailSum = 0;
            }
            graphList.get(i).setMainDutySum(mainDutySum);
            graphList.get(i).setPercentSum(percentSum);
            session.merge(graphList.get(i));
        }
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public void createCredit(String type, double sum, int idClient, String loginEmployee) throws Exception {
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            Account currentAccountNum = new AccountDaoImpl(session).createAccount(idClient,
                    "810", "40817", "Счет кредита");
            Account mainDutyAccountNum = new AccountDaoImpl(session).createAccount(idClient,
                    "810", "45505", "Учет основного долга по КД");
            Account percentAccountNum = new AccountDaoImpl(session).createAccount(idClient,
                    "810", "47427", "Учет процентов по КД");
            Account overDutyAccountNum = new AccountDaoImpl(session).createAccount(idClient,
                    "810", "45815", "Учет просроченного долга по КД");
            Account overPercentAccountNum = new AccountDaoImpl(session).createAccount(idClient,
                    "810", "45915", "Учет просроченных процентов по КД");
            String queryString = "FROM OperDate WHERE current = 1";
            OperDate openDate = session.createQuery(queryString, OperDate.class).getSingleResult();
            Credit credit = new Credit(idClient, type, openDate.getOperDate(), null,
                    currentAccountNum.getAccountNum(), mainDutyAccountNum.getAccountNum(),
                    percentAccountNum.getAccountNum(), overDutyAccountNum.getAccountNum(),
                    overPercentAccountNum.getAccountNum(), null);
            credit = (Credit) session.merge(credit);
            currentAccountNum.setDescript(currentAccountNum.getDescript() + " №" + credit.getId());
            session.merge(currentAccountNum);
            new OperationDaoImpl(session).transaction(currentAccountNum.getAccountNum(),
                    mainDutyAccountNum.getAccountNum(), sum, loginEmployee);
            buildCreditGraph(credit);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw e;
        }
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public Map<String, Object> getCreditInfo(int id) {
        Credit credit = session.get(Credit.class, id);
        CreditTerm creditTerm = session.get(CreditTerm.class, credit.getType());
        Currency currency = session.get(Currency.class, "810");
        String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
        OperDate tempDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        Date lastUpDate = credit.getUpDate();
        if (lastUpDate == null) {
            queryString = "SELECT MAX(g.payDate) FROM CreditGraph g WHERE g.id = :id AND g.processed = 1";
            Query query = session.createQuery(queryString, Date.class);
            query.setParameter("id", id);
            lastUpDate = (Date) query.getSingleResult();
        }
        if (lastUpDate == null) {
            lastUpDate = credit.getOpenDate();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("upDate", Lib.formatDate(lastUpDate, "dd.MM.yyyy"));
        map.put("tailDays", Lib.diffDate(lastUpDate, tempDate.getOperDate()));
        map.put("mainDuty", new AccountDaoImpl(session).checkBalance(credit.getMainDutyAccountNum()));
        map.put("mainDutyStr", Lib.formatSum((Double) map.get("mainDuty")));
        map.put("percent", new AccountDaoImpl(session).checkBalance(credit.getPercentAccountNum()));
        map.put("percentStr", Lib.formatSum((Double) map.get("percent")));
        map.put("overDuty", new AccountDaoImpl(session).checkBalance(credit.getOverDutyAccountNum()));
        map.put("overDutyStr", Lib.formatSum((Double) map.get("overDuty")));
        map.put("overPercent", new AccountDaoImpl(session).checkBalance(credit.getOverPercentAccountNum()));
        map.put("overPercentStr", Lib.formatSum((Double) map.get("overPercent")));
        map.put("current", new AccountDaoImpl(session).checkBalance(credit.getCurrentAccountNum()));
        map.put("currentStr", Lib.formatSum((Double) map.get("current")));
        map.put("percent2", Lib.roundSum((Double) map.get("mainDuty") * creditTerm.getRate() / 100 / 365 *
                (Integer) map.get("tailDays")));
        map.put("percent2Str", Lib.formatSum((Double) map.get("percent2")));
        map.put("overPercent2", Lib.roundSum((Double) map.get("overDuty") * creditTerm.getOverdueRate() / 100 / 365 *
                (Integer) map.get("tailDays")));
        map.put("overPercent2Str", Lib.formatSum((Double) map.get("overPercent2")));
        map.put("total", (Double) map.get("mainDuty") + (Double) map.get("percent") + (Double) map.get("overDuty") +
                (Double) map.get("overPercent") + (Double) map.get("percent2") + (Double) map.get("overPercent2"));
        map.put("totalStr", Lib.formatSum((Double) map.get("total")));
        map.put("lackStr", Lib.formatSum((Double) map.get("total") - (Double) map.get("current")));
        map.put("id", credit.getId());
        map.put("openDate", Lib.formatDate(credit.getOpenDate(), "dd.MM.yyyy"));
        map.put("currentDate", Lib.formatDate(tempDate.getOperDate(), "dd.MM.yyyy"));
        map.put("descript", creditTerm.getDescript());
        queryString = "SELECT o FROM Operation o WHERE o.creditAccountNum = :account ORDER BY o.idOper";
        Query query = session.createQuery(queryString, Operation.class);
        query.setParameter("account", credit.getMainDutyAccountNum());
        query.setMaxResults(1);
        Operation operation = (Operation) query.getSingleResult();
        map.put("creditSum", Lib.formatSum(operation.getSum()));
        map.put("currency", "RUR");
        map.put("rate", Lib.formatSum(creditTerm.getRate()));
        map.put("overRate", Lib.formatSum(creditTerm.getOverdueRate()));
        queryString = "SELECT MAX(g.num) FROM CreditGraph g WHERE g.id = :id";
        query = session.createQuery(queryString, Integer.class);
        query.setParameter("id", id);
        Integer num = (Integer) query.getSingleResult();
        queryString = "SELECT g FROM CreditGraph g WHERE g.id = :id AND g.num = :num ORDER BY g.payDate";
        query = session.createQuery(queryString, CreditGraph.class);
        query.setParameter("id", id);
        query.setParameter("num", num);
        List table = query.getResultList();
        map.put("table", table);
        double sumMainDuty = 0;
        double sumPercent = 0;
        double sumRow = 0;
        for (Object o : table) {
            CreditGraph graph = (CreditGraph) o;
            sumMainDuty += graph.getMainDutySum();
            sumPercent += graph.getPercentSum();
            sumRow += graph.getMainDutySum() + graph.getPercentSum();
        }
        map.put("tableTotalMainDutyStr", Lib.formatSum(sumMainDuty));
        map.put("tableTotalPercentStr", Lib.formatSum(sumPercent));
        map.put("tableTotalRowStr", Lib.formatSum(sumRow));
        return map;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public void updateCredit(int creditId, Date newDate, String loginEmployee) throws Exception {
        Credit credit = session.get(Credit.class, creditId);
        CreditTerm creditTerm = session.get(CreditTerm.class, credit.getType());
        Account debitBankAccount = new OperationDaoImpl(session).getBankAccount(
                credit.getMainDutyAccountNum(), "70606%0001");
        Map map = getCreditInfo(creditId);
        List graphList = (List) map.get("table");
        List graphExtList = new ArrayList<CreditGraphExt>();
        for (int i = 0; i < graphList.size(); i++) {
            Date prevPayDate = (i == 0) ? credit.getOpenDate() : ((CreditGraph) graphList.get(i - 1)).getPayDate();
            CreditGraphExt creditGraphExt = new CreditGraphExt((CreditGraph) graphList.get(i), prevPayDate);
            if (creditGraphExt.getProcessed() == 0 && creditGraphExt.getPayDate().before(newDate))
                graphExtList.add(creditGraphExt);
        }
        if (graphExtList.size() == 0)
            return;
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            for (Object o : graphExtList) {
                CreditGraphExt graph = (CreditGraphExt) o;
                // начисление процентов по осн. долгу (по графику)
                if (graph.getPercentSum() > 0) {
                    new OperationDaoImpl(session).transaction(debitBankAccount.getAccountNum(),
                            credit.getPercentAccountNum(), graph.getPercentSum(), loginEmployee);
                }
                // начисление процентов на проср. долг (по сумме)
                double overDutySum = new AccountDaoImpl(session).checkBalance(credit.getOverDutyAccountNum());
                if (overDutySum > 0) {
                    double overDutySumPercent = Lib.roundSum(overDutySum * creditTerm.getOverdueRate() / 100 / 365 *
                            Lib.diffDate(graph.getPrevPayDate(), graph.getPayDate()));
                    new OperationDaoImpl(session).transaction(debitBankAccount.getAccountNum(),
                            credit.getOverPercentAccountNum(), overDutySumPercent, loginEmployee);
                }
                // порядок гашения: 1) проср. проценты, 2) проср. долг, 3) осн. проценты, 4) осн. долг
                double currentSum = new AccountDaoImpl(session).checkBalance(credit.getCurrentAccountNum());
                // 1) проср. проценты
                double overPercentSum = new AccountDaoImpl(session).checkBalance(credit.getOverPercentAccountNum());
                if (currentSum > 0 && overPercentSum > 0) {
                    double sum = Math.min(currentSum, overPercentSum);
                    new OperationDaoImpl(session).transaction(credit.getOverPercentAccountNum(),
                            credit.getCurrentAccountNum(), sum, loginEmployee);
                    currentSum -= sum;
                }
                // 2) проср. долг
                overDutySum = new AccountDaoImpl(session).checkBalance(credit.getOverDutyAccountNum());
                if (currentSum > 0 && overDutySum > 0) {
                    double sum = Math.min(currentSum, overDutySum);
                    new OperationDaoImpl(session).transaction(credit.getOverDutyAccountNum(),
                            credit.getCurrentAccountNum(), sum, loginEmployee);
                    currentSum -= sum;
                }
                // 3) осн. проценты
                double percentSum = new AccountDaoImpl(session).checkBalance(credit.getPercentAccountNum());
                if (percentSum > 0) {
                    double sum = Math.min(currentSum, percentSum);
                    if (sum > 0) {
                        new OperationDaoImpl(session).transaction(credit.getPercentAccountNum(),
                                credit.getCurrentAccountNum(), sum, loginEmployee);
                        percentSum -= sum;
                        currentSum -= sum;
                    }
                    // остались непогашенные проценты - переносим в просроченные
                    if (percentSum > 0) {
                        new OperationDaoImpl(session).transaction(credit.getPercentAccountNum(),
                                credit.getOverPercentAccountNum(), percentSum, loginEmployee);
                    }
                }
                // 4) основной долг (не весь, сумма из графика погашений)
                double dutySum = new AccountDaoImpl(session).checkBalance(credit.getMainDutyAccountNum());
                if (dutySum > 0) {
                    double sum = Math.min(currentSum, graph.getMainDutySum());
                    if (sum > 0) {
                        new OperationDaoImpl(session).transaction(credit.getMainDutyAccountNum(),
                                credit.getCurrentAccountNum(), sum, loginEmployee);
                    }
                    // перенос в просрочку
                    if (sum < graph.getMainDutySum()) {
                        double sum2 = Lib.roundSum(graph.getMainDutySum() - sum);
                        new OperationDaoImpl(session).transaction(credit.getMainDutyAccountNum(),
                                credit.getOverDutyAccountNum(), sum2, loginEmployee);
                    }
                }
                // поставим флаг в графике, что строка обработана, обновим дату посл. обновления в поле `update`
                CreditGraph creditGraph = new CreditGraph(graph);
                creditGraph.setProcessed(1);
                session.merge(creditGraph);
                credit.setUpDate(graph.getPayDate());
                session.merge(credit);
            } // for
            // если задолженность полностью погашена - автоматически закроем кредит
            map = getCreditInfo(creditId);
            if (Lib.roundSum((Double) map.get("total")) == 0) {
                closeCredit(creditId);
            }
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw e;
        }
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public void zeroAndCloseCredit(int creditId, String loginEmployee) throws Exception {
        // погашение задолженностей средствами с текущего счета
        Credit credit = session.get(Credit.class, creditId);
        if (credit.getCloseDate() != null)
            throw new Exception("Кредит №" + creditId + " закрыт " +
                    Lib.formatDate(credit.getCloseDate(), "dd.MM.yyyy"));
        Map map = getCreditInfo(creditId);
        double currentSum = new AccountDaoImpl(session).checkBalance(credit.getCurrentAccountNum());
        if (currentSum < (Double) map.get("total")) {
            throw new Exception("Недостаточно средств на тек. счете для полного погашения кредита №" +
                    creditId + ": " + Lib.formatSum((Double) map.get("total") - currentSum) + ". ");
        }
        Account debitBankAccount = new OperationDaoImpl(session).getBankAccount(
                credit.getMainDutyAccountNum(), "70606%0001");
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            // доначисление процентов и гашение остатков задолженности
            // гашение осн. долга
            if ((Double) map.get("mainDuty") > 0) {
                new OperationDaoImpl(session).transaction(credit.getMainDutyAccountNum(),
                        credit.getCurrentAccountNum(), (Double) map.get("mainDuty"), loginEmployee);
            }
            // доначисление процентов с даты посл. обновления
            if ((Double) map.get("percent2") > 0) {
                new OperationDaoImpl(session).transaction(debitBankAccount.getAccountNum(),
                        credit.getPercentAccountNum(), (Double) map.get("percent2"), loginEmployee);
            }
            // гашение процентов
            if ((Double) map.get("percent") + (Double) map.get("percent2") > 0) {
                new OperationDaoImpl(session).transaction(credit.getPercentAccountNum(),
                        credit.getCurrentAccountNum(),
                        Lib.roundSum((Double) map.get("percent") + (Double) map.get("percent2")), loginEmployee);
            }
            // гашение проср. долга
            if ((Double) map.get("overDuty") > 0) {
                new OperationDaoImpl(session).transaction(credit.getOverDutyAccountNum(),
                        credit.getCurrentAccountNum(), (Double) map.get("overDuty"), loginEmployee);
            }
            // доначисление проср. процентов с даты посл. обновления
            if ((Double) map.get("overPercent2") > 0) {
                new OperationDaoImpl(session).transaction(debitBankAccount.getAccountNum(),
                        credit.getOverPercentAccountNum(), (Double) map.get("overPercent2"), loginEmployee);
            }
            // гашение проср. процентов
            if ((Double) map.get("overPercent") + (Double) map.get("overPercent2") > 0) {
                new OperationDaoImpl(session).transaction(credit.getOverPercentAccountNum(),
                        credit.getCurrentAccountNum(),
                        Lib.roundSum((Double) map.get("overPercent") + (Double) map.get("overPercent2")), loginEmployee);
            }
            // непосредственно закрытие кредита
            closeCredit(creditId);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw e;
        }
    }

    @org.springframework.data.jpa.repository.Query
    private void closeCredit(int creditId) throws Exception {
        // закрытие кредита (всех счетов и договора, договор должен быть погашен)
        // проверка отсутствия задолженности
        Map map = getCreditInfo(creditId);
        if ((Double) map.get("total") > 0) {
            throw new Exception("По кредиту имеется задолженность в размере " +
                    Lib.formatSum((Double) map.get("total")) + ". ");
        }
        Credit credit = session.get(Credit.class, creditId);
        // закроем счета
        new AccountDaoImpl(session).closeAccount(credit.getMainDutyAccountNum());
        new AccountDaoImpl(session).closeAccount(credit.getPercentAccountNum());
        new AccountDaoImpl(session).closeAccount(credit.getOverDutyAccountNum());
        new AccountDaoImpl(session).closeAccount(credit.getOverPercentAccountNum());
        if (new AccountDaoImpl(session).checkBalance(credit.getCurrentAccountNum()) < 0.005) {
            new AccountDaoImpl(session).closeAccount(credit.getCurrentAccountNum());
        }
        // закроем сам договор
        String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
        OperDate tempDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        credit.setCloseDate(tempDate.getOperDate());
        session.merge(credit);
    }
}
