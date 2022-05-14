package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDao {
    Session getSession();

    @Query
    Client findByPhone(String phone) throws Exception;

    @Query
    Client findByPassport(String passport);

    @Query
    void createClient(Client templateClient);

    @Query
    void updateClient(Client client);
}
