package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.OperationDaoImpl;

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
}
