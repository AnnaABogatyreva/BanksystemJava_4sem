package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.repository.AccountDaoImpl;
import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;

public class AccountService {
    private AccountDaoImpl accountDao;

    public AccountService() {
        accountDao = new AccountDaoImpl();
        }

    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        return accountDao.createAccount(idClient, currencyCode, acc2p, descript);
    }
}
