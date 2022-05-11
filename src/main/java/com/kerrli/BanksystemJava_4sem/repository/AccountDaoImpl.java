package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.AccountCnt;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDaoImpl implements AccountDao {
    private Session session;

    public AccountDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    @Override
    public Session getSession() {
        return session;
    }

    public String generateAccountNum(String acc2p, String currencyCode) {
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
        return accountNum;
    }

    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        Transaction transaction = session.beginTransaction();
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
        transaction.commit();
        return account;
    }

    @Override
    public double checkBalance(String accountnum) {
        return 0;
    }
}
