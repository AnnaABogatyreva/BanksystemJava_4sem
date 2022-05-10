package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDao {
    Session getSession();
    
    Client findByPhone(String phone);

    Client findByPassport(String passport);

    void createClient(Client templateClient);

    void updateClient(Client client);
}
