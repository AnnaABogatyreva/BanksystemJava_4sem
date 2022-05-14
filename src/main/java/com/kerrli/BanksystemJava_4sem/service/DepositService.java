package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.DepositDaoImpl;

import java.util.List;

public class DepositService {
    private final DepositDaoImpl depositDao;

    public DepositService() {
        depositDao = new DepositDaoImpl();
    }

    public DepositService(DepositDaoImpl depositDao) {
        this.depositDao = depositDao;
    }

    public List getDepositTermList() {
        return depositDao.getDepositTermList();
    }

    public List getDepositList(int idClient) {
        return depositDao.getDepositList(idClient);
    }

    public void createDeposit(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception {
        try {
            depositDao.createDeposit(type, debitAccountNum, sum, loginEmployee);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
