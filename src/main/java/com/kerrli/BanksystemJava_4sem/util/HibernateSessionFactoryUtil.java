package com.kerrli.BanksystemJava_4sem.util;

import com.kerrli.BanksystemJava_4sem.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public final class HibernateSessionFactoryUtil {
    @Autowired
    private static SessionFactory sessionFactory;

    @PostConstruct
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Employee.class);
                configuration.addAnnotatedClass(Client.class);
                configuration.addAnnotatedClass(Currency.class);
                configuration.addAnnotatedClass(Account.class);
                configuration.addAnnotatedClass(AccountCnt.class);
                configuration.addAnnotatedClass(Balance.class);
                configuration.addAnnotatedClass(AccountType.class);
                configuration.addAnnotatedClass(Operation.class);
                configuration.addAnnotatedClass(OperDate.class);
                configuration.addAnnotatedClass(Emprole.class);
                configuration.addAnnotatedClass(Converter.class);
                configuration.addAnnotatedClass(DepositTerm.class);
                configuration.addAnnotatedClass(Deposit.class);
                configuration.addAnnotatedClass(CapTerm.class);
                configuration.addAnnotatedClass(CreditTerm.class);
                configuration.addAnnotatedClass(Credit.class);
                StandardServiceRegistryBuilder builder =
                    new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e);
            }
        }
        return sessionFactory;
    }
}