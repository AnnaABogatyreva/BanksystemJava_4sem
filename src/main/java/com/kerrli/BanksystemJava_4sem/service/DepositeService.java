package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.DepositeTerm;
import com.kerrli.BanksystemJava_4sem.repository.DepositeDaoImpl;
import org.hibernate.Session;

import java.util.List;

public class DepositeService {
    private DepositeDaoImpl depositeDao;

    public DepositeService() {
        depositeDao = new DepositeDaoImpl();
    }

    public DepositeService(DepositeDaoImpl depositeDao) {
        this.depositeDao = depositeDao;
    }

    public List getDepositeList() {
        return depositeDao.getDepositeList();
    }

    public void createDeposite(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception {
        try {
            depositeDao.createDeposite(type, debitAccountNum, sum, loginEmployee);
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
