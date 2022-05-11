package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDao {
    Session getSession();

    String generateAccountNum(String acc2p, String currencyCode);

    Account createAccount(int idClient, String currencyCode, String acc2p, String descript);

    double checkBalance(String accountnum);
}
