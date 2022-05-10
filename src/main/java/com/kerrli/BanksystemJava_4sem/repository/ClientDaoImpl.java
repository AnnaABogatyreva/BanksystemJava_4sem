package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDaoImpl implements ClientDao {
    private Session session;

    public ClientDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Client findByPhone(String phone) {
        Transaction transaction = session.beginTransaction();
        String queryString = "SELECT c FROM Client c WHERE c.phone = :phone";
        Query query = session.createQuery(queryString, Client.class);
        query.setParameter("phone", phone);
        Client client = (Client) query.list().get(0);
        transaction.commit();
        return client;
    }

    @Override
    public Client findByPassport(String passport) {
        Transaction transaction = session.beginTransaction();
        String queryString = "SELECT c FROM Client c WHERE c.passport = :passport";
        Query query = session.createQuery(queryString, Client.class);
        query.setParameter("passport", passport);
        if (query.list().size() == 0) {
            return null;
        }
        Client client = (Client) query.list().get(0);
        transaction.commit();
        return client;
    }

    @Override
    public void createClient(Client templateClient) {
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(templateClient);
        transaction.commit();
    }

    @Override
    public void updateClient(Client client) {
        Transaction transaction = session.beginTransaction();
        session.merge(client);
        transaction.commit();
    }

}
