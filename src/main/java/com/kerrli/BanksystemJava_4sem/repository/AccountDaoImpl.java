package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class AccountDaoImpl implements AccountDao {
    private Session session;

    public AccountDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public AccountDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public String generateAccountNum(String acc2p, String currencyCode) {
        boolean transaction = LibTransaction.beginTransaction(session);
        int cnt;
        try {
            String queryString = "FROM AccountCnt WHERE acc2p = :acc2p AND currency = :currency";
            Query query = session.createQuery(queryString, AccountCnt.class);
            query.setParameter("acc2p", acc2p);
            query.setParameter("currency", currencyCode);
            AccountCnt accountCnt = (AccountCnt) query.getSingleResult();
            cnt = accountCnt.getCnt();
        }
        catch (Exception e) {
            cnt = 0;
        }
        // Структура банковского счета:
        // 40817 - счет второго порядка
        // 810 - валюта
        // 1 - проверочный код
        // 0000 - отделение банка (0000 - головной офис)
        // 0000001 - порядковый номер счета банка
        cnt++;
        String accountNum = acc2p + currencyCode + "20000" + String.format("%07d", cnt);
        AccountCnt accountCnt = new AccountCnt(acc2p, currencyCode, cnt);
        session.merge(accountCnt);
        LibTransaction.commitTransaction(session, transaction);
        return accountNum;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        boolean transaction = LibTransaction.beginTransaction(session);
        String accountNum = generateAccountNum(acc2p, currencyCode);
        String queryString = "SELECT COUNT(a) FROM Account a " +
                "WHERE idClient = :idClient AND closed IS NULL AND currency = :currency";
        Query query = session.createQuery(queryString);
        query.setParameter("idClient", idClient);
        query.setParameter("currency", currencyCode);
        Long cnt = (Long) query.list().get(0);
        int def = (cnt > 0) ? 0 : 1;
        Account account = new Account(idClient, accountNum, currencyCode, descript, null, def);
        session.merge(account);
        LibTransaction.commitTransaction(session, transaction);
        return account;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public double checkBalance(String accountNum) {
        String queryString = "SELECT b.sum, b.date FROM Balance b " +
                "WHERE b.accountNum = :accountNum ORDER BY b.date DESC";
        Query query = session.createQuery(queryString);
        query.setParameter("accountNum", accountNum);
        query.setMaxResults(1);
        List list = (List) query.list();
        double sum = 0;
        Date date = Lib.parseDate("1970-01-01");
        try {
            sum = (Double) list.get(0);
            date = (Date) list.get(1);
        }
        catch (Exception e) {}
        System.out.println("sum = " + sum + "\ndate = " + date); /// !!!
        queryString = "SELECT t FROM AccountType t WHERE acc2p = :acc2p";
        query = session.createQuery(queryString);
        query.setParameter("acc2p", accountNum.substring(0, 5));
        AccountType accountType = (AccountType) query.getSingleResult();
        int sign = accountType.getSign();
        sum *= sign;
        queryString = "SELECT -1 * SUM(o.sum) FROM Operation o " +
                "WHERE o.operDate > :operDate AND o.debitAccountNum = :debitAccountNum";
        query = session.createQuery(queryString);
        query.setParameter("operDate", Lib.addTime(date, "23:59:59"));
        query.setParameter("debitAccountNum", accountNum);
        try {
            sum += (Double) query.getSingleResult() * sign;
        }
        catch (Exception e) {}
        queryString = "SELECT 1 * SUM(o.sum) FROM Operation o " +
                "WHERE o.operDate > :operDate AND o.creditAccountNum = :creditAccountNum";
        query = session.createQuery(queryString);
        query.setParameter("operDate", Lib.addTime(date, "23:59:59"));
        query.setParameter("creditAccountNum", accountNum);
        try {
            sum += (Double) query.getSingleResult() * sign;
        }
        catch (Exception e) {}
        return Lib.roundSum(sum);
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getZeroAccountList(int idClient) {
        String queryString = "FROM Account a " +
                "WHERE a.closed IS NULL AND a.idClient = :idClient AND SUBSTRING(a.accountNum, 1, 5) IN ('40817')";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("idClient", idClient);
        List list = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Account account = (Account) list.get(i);
            double balance = checkBalance(account.getAccountNum());
            if (Math.abs(balance) < 0.005) {
                AccountExt accountExt = new AccountExt(account, getSelectBlockLine(account));
                res.add(accountExt);
            }

        }
        return res;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getAccountList(int idClient) {
        String queryString = "FROM Account a " +
                "WHERE a.closed IS NULL AND a.idClient = :idClient AND SUBSTRING(a.accountNum, 1, 5) IN ('40817')";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("idClient", idClient);
        List list = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Account account = (Account) list.get(i);
            AccountExt accountExt = new AccountExt(account, getSelectBlockLine(account));
            res.add(accountExt);
        }
        return res;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getBankAccountList() {
        String queryString = "FROM Account a " +
                "WHERE a.closed IS NULL AND a.idClient = 1 " +
                "AND SUBSTRING(a.accountNum, 1, 5) IN ('20202', '70601', '70606')";
        Query query = session.createQuery(queryString, Account.class);
        List list = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Account account = (Account) list.get(i);
            AccountExt accountExt = new AccountExt(account, getSelectBlockLine(account));
            res.add(accountExt);
        }
        return res;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public String getSelectBlockLine(Account account) {
        Currency currency = session.get(Currency.class, account.getCurrency());
        AccountType accountType = session.get(AccountType.class, account.getAcc2p());
        String res = "Счет №" + account.getAccountNum() + ": " + Lib.formatSum(checkBalance(account.getAccountNum())) +
                " " + currency.getIsoCode() + ", " + account.getDescript() + " (" + accountType.getDispType() + ")";
        return res;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public Account closeAccount(String accountNum) throws Exception {
        String queryString = "SELECT COUNT(c) FROM Credit c " +
                "WHERE c.currentAccountNum = :accountNum AND closedate IS NULL";
        Query query = session.createQuery(queryString, Long.class);
        query.setParameter("accountNum", accountNum);
        Long cnt = (Long) query.getSingleResult();
        if (cnt > 0) {
            throw new Exception("Счет привязан к действующему кредиту. ");
        }
        boolean transaction = LibTransaction.beginTransaction(session);
        double balance = checkBalance(accountNum);
        if (Math.abs(balance) < 0.005) {
            Account closeAccount = session.get(Account.class, accountNum);
            queryString = "SELECT COUNT(a) FROM Account a " +
                    "WHERE a.idClient = :idClient AND a.closed IS NULL AND a.currency = :currency";
            query = session.createQuery(queryString, Long.class);
            query.setParameter("idClient", closeAccount.getIdClient());
            query.setParameter("currency", closeAccount.getCurrency());
            Long cntAccount = (Long) query.getSingleResult();
            if (closeAccount.getDef() == 1 && cntAccount > 1) {
                queryString = "SELECT a FROM Account a " +
                        "WHERE a.idClient = :idClient AND a.closed IS NULL AND a.currency = :currency AND a.def = 0";
                query = session.createQuery(queryString, Account.class);
                query.setParameter("idClient", closeAccount.getIdClient());
                query.setParameter("currency", closeAccount.getCurrency());
                query.setMaxResults(1);
                Account account = (Account) query.getSingleResult();
                account.setDef(1);
                session.merge(account);
            }
            closeAccount.setDef(0);
            queryString = "FROM OperDate o WHERE o.current = 1";
            OperDate operDate = session.createQuery(queryString, OperDate.class).getSingleResult();
            closeAccount.setClosed(operDate.getOperDate());
            session.merge(closeAccount);
            LibTransaction.commitTransaction(session, transaction);
            return closeAccount;
        }
        else {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Остаток счета ненулевой.");
        }
    }
}
