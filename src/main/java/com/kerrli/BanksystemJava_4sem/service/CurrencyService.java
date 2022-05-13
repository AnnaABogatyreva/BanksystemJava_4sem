package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.CurrencyDaoImpl;

import java.util.List;

public class CurrencyService {
    private CurrencyDaoImpl currencyDao;

    public CurrencyService() {
        currencyDao = new CurrencyDaoImpl();
    }

    public CurrencyService(CurrencyDaoImpl currencyDao) {
        this.currencyDao = currencyDao;
    }

    public CurrencyDaoImpl getCurrencyDao() {
        return currencyDao;
    }

    public List getCurrencyList() {
        return currencyDao.getCurrencyList();
    }

    public List getForeignCurrencyList() {
        return currencyDao.getForeignCurrencyList();
    }
}
