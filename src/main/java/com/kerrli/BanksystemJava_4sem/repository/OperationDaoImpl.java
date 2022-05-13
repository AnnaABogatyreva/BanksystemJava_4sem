package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.AccountType;
import com.kerrli.BanksystemJava_4sem.entity.OperDate;
import com.kerrli.BanksystemJava_4sem.entity.Operation;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public class OperationDaoImpl implements OperationDao {
    private Session session;

    public OperationDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public OperationDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void transaction(String debitAccountNum, String creditAccountNum,
                            double sum, String loginEmployee) throws Exception {
        boolean transaction = LibTransaction.beginTransaction(session);
        sum = Lib.roundSum(sum);
        Account debitAccount = session.get(Account.class, debitAccountNum);
        Account creditAccount = session.get(Account.class, creditAccountNum);
        AccountType debitAccountType = session.get(AccountType.class, debitAccount.getAcc2p());
        AccountType creditAccountType = session.get(AccountType.class, creditAccount.getAcc2p());
        double debitBalance = new AccountDaoImpl(session).checkBalance(debitAccountNum);
        double creditBalance = new AccountDaoImpl(session).checkBalance(creditAccountNum);
        if (debitAccountNum.compareTo(creditAccountNum) == 0) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Выберите разные счета. ");
        }
        if (debitAccount == null || debitAccount.getClosed() != null) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Счет дебета " + debitAccountNum + " не существует или закрыт. ");
        }
        if (creditAccount == null || creditAccount.getClosed() != null) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Счет кредита " + creditAccountNum + " не существует или закрыт. ");
        }
        if (debitAccount.getCurrency().compareTo(creditAccount.getCurrency()) != 0) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Валюты счетов не совпадают: " +
                    debitAccount.getCurrency() + " != " + creditAccount.getCurrency() + ". ");
        }
        if (debitAccountType.getSign() > 0 && debitBalance < sum) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Недостаточно средств на счете " + debitAccountNum + ". ");
        }
        if (creditAccountType.getSign() < 0 && creditBalance < sum) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Недостаточно средств на счете " + creditAccountNum + ". ");
        }
        if (sum < 0.00) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception("Сумма проводки должна быть больше нуля: (" +
                    debitAccountNum + ", " + creditAccountNum + "): " + Lib.formatSum(sum) + ". ");
        }
        if (sum > 0.00) {
            String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
            OperDate operDate = session.createQuery(queryString, OperDate.class).getSingleResult();
            Operation operation = new Operation(debitAccountNum, creditAccountNum,
                    operDate.getOperDate(), sum, loginEmployee);
            session.merge(operation);
        }
        LibTransaction.commitTransaction(session, transaction);
    }
}
