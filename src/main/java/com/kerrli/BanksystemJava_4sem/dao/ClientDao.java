package com.kerrli.BanksystemJava_4sem.dao;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import org.hibernate.Session;

public interface ClientDao {
    Session getSession();
    
    Client findByPhone(String phone);

    Client findByPassport(String passport);
}
