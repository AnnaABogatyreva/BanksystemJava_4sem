package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.repository.AccountDaoImpl;
import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;

import java.util.List;

public class AccountService {
    private AccountDaoImpl accountDao;

    public AccountService() {
        accountDao = new AccountDaoImpl();
        }

    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        return accountDao.createAccount(idClient, currencyCode, acc2p, descript);
    }

    public List getZeroAccountList(int idClient) {
        return accountDao.getZeroAccountList(idClient);
    }

    public String getSelectBlockLine(Account account) {
        return accountDao.getSelectBlockLine(account);
    }
}
