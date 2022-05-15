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
        return creditTerm.getDescript() + ", " + Lib.formatSum(creditTerm.getRate()) + "% годовых, сумма " +
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
}
