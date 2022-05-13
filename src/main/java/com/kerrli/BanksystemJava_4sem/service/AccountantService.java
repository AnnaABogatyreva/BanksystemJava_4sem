package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.AccountantDaoImpl;

import java.util.Date;

public class AccountantService {
    private AccountantDaoImpl accountantDao;

    public AccountantService() {
        accountantDao = new AccountantDaoImpl();
    }

    public AccountantService(AccountantDaoImpl accountantDao) {
        this.accountantDao = accountantDao;
    }

    public AccountantDaoImpl getAccountantDao() {
        return accountantDao;
    }

    public void setCourse(String currency, double buy, double cost, double sell) throws Exception {
        if (buy > cost || cost > sell) {
            throw new Exception("Заданы неверные курсы валюты. ");
        }
        accountantDao.setCourse(currency, buy, cost, sell);
    }

    public void changeOperDate(Date date) throws Exception {
        accountantDao.changeOperDate(date);
    }
}
