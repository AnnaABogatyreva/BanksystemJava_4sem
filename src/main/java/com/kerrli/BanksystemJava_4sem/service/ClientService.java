package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;
import com.kerrli.BanksystemJava_4sem.entity.Client;

public class ClientService {
    private ClientDaoImpl clientDao;

    public ClientService() {
        clientDao = new ClientDaoImpl();
    }

    public Client findClientByPhone(String phone) {
        return clientDao.findByPhone(phone);
    }

    public Client findClientByPassport(String passport) {
        return clientDao.findByPassport(passport);
    }

    public String createClient(Client templateClient) {
        try {
            clientDao.createClient(templateClient);
            return "";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public String updateClient(Client client) {
        try {
            clientDao.updateClient(client);
            return "";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
}
