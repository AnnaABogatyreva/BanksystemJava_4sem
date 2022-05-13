package com.kerrli.BanksystemJava_4sem.util;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class LibTransaction {
    public static boolean beginTransaction(Session session) {
        Transaction transaction;
        boolean canStopTransaction;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
            canStopTransaction = true;
        }
        else {
            transaction = session.getTransaction();
            canStopTransaction = false;
        }
        return canStopTransaction;
    }

    public static void commitTransaction(Session session, boolean canStopTransaction) {
        if (canStopTransaction)
            session.getTransaction().commit();
    }

    public static void rollbackTransaction(Session session, boolean canStopTransaction) {
        if (canStopTransaction)
            session.getTransaction().rollback();
    }
}
