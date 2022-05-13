package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;
import com.kerrli.BanksystemJava_4sem.repository.OperationDaoImpl;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;

public class OperationService {
    private OperationDaoImpl operationDao;

    public OperationService() {
        operationDao = new OperationDaoImpl();
    }

    public OperationService(OperationDaoImpl operationDao) {
        this.operationDao = operationDao;
    }

    public OperationDaoImpl getOperationDao() {
        return operationDao;
    }

    public void transaction(String debitAccountNum, String creditAccountNum,
                            double sum, String loginEmployee) throws Exception {
        operationDao.transaction(debitAccountNum, creditAccountNum, sum, loginEmployee);
    }

    public void convertation(String debitAccountNum, String creditAccountNum,
                             double sum, String loginEmployee) throws Exception {
        operationDao.convertation(debitAccountNum, creditAccountNum, sum, loginEmployee);
    }

    // Для двух пассивных счетов
    public String operation(String debitAccountNum, String creditAccountNum,
                            double sum, String loginEmployee) throws Exception {
        Account debitAccount = operationDao.getSession().get(Account.class, debitAccountNum);
        Account creditAccount = operationDao.getSession().get(Account.class, creditAccountNum);
        if (debitAccount.getCurrency().compareTo(creditAccount.getCurrency()) == 0) {
            try {
                transaction(creditAccountNum, debitAccountNum, sum, loginEmployee);
                return "Успешный перевод. ";
            }
            catch (Exception e) {
                throw new Exception("Перевод не выполнен. " + e.getMessage());
            }
        }
        else {
            try {
                convertation(debitAccountNum, creditAccountNum, sum, loginEmployee);
                return "Успешный перевод с конвертацией. ";
            }
            catch (Exception e) {
                throw new Exception("Перевод с конвертацией не выполнен. " + e.getMessage());
            }
        }
    }

    public void pushAccount(String accountNum, double sum, String loginEmployee) throws Exception {
        Account cassaAccount = operationDao.getBankAccount(accountNum, "20202%");
        operationDao.transaction(accountNum, cassaAccount.getAccountNum(), sum, loginEmployee);
    }

    public void popAccount(String accountNum, double sum, String loginEmployee) throws Exception {
        Account cassaAccount = operationDao.getBankAccount(accountNum, "20202%");
        operationDao.transaction(cassaAccount.getAccountNum(), accountNum, sum, loginEmployee);
    }

    public Account getCreditAccountByPhone(String debitAccountNum, String phone) throws Exception {
        Session session = operationDao.getSession();
        Account debitAccount = session.get(Account.class, debitAccountNum);
        ClientDaoImpl clientDao = new ClientDaoImpl(session);
        Client client = clientDao.findByPhone(phone);
        if (client.getId() == debitAccount.getIdClient()) {
            throw new Exception("Перевод себе недоступен по номеру телефона. ");
        }
        Account creditAccount = operationDao.getDefaultAccount(client, debitAccount.getCurrency());
        return creditAccount;
    }
}
