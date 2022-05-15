package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.CreditDaoImpl;

import java.util.List;

public class CreditService {
    private final CreditDaoImpl creditDao;

    public CreditService() {
        creditDao = new CreditDaoImpl();
    }

    public CreditService(CreditDaoImpl creditDao) {
        this.creditDao = creditDao;
    }

    public List getCreditTermList() {
        return creditDao.getCreditTermList();
    }

    public List getCreditList(int idClient) {
        return creditDao.getCreditList(idClient);
    }
}
