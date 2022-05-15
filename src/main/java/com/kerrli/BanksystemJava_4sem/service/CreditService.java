package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.CreditDaoImpl;

import java.util.List;
import java.util.Map;

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

    public void createCredit(String type, double sum, int idClient, String loginEmployee) throws Exception {
        creditDao.createCredit(type, sum, idClient, loginEmployee);
    }

    public Map<String, Object> getCreditInfo(int id) {
        return creditDao.getCreditInfo(id);
    }
}
