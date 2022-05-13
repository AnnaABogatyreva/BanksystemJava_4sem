package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.repository.AccountDaoImpl;

import java.util.List;

public class AccountService {
    private AccountDaoImpl accountDao;

    public AccountService() {
        accountDao = new AccountDaoImpl();
    }

    public AccountService(AccountDaoImpl accountDao) {
        this.accountDao = accountDao;
    }

    public AccountDaoImpl getAccountDao() {
        return accountDao;
    }

    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        return accountDao.createAccount(idClient, currencyCode, acc2p, descript);
    }

    public List getZeroAccountList(int idClient) {
        return accountDao.getZeroAccountList(idClient);
    }

    public List getAccountList(int idClient) {
        return accountDao.getAccountList(idClient);
    }

    public List getBankAccountList() {
        return accountDao.getBankAccountList();
    }

    public Account closeAccount(String accountNum) throws Exception {
        return accountDao.closeAccount(accountNum);
    }
}
