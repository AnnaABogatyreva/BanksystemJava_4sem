package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;
import com.kerrli.BanksystemJava_4sem.entity.Client;

public class ClientService {
    private ClientDaoImpl clientDao;

    public ClientService() {
        clientDao = new ClientDaoImpl();
    }

    public ClientService(ClientDaoImpl clientDao) {
        this.clientDao = clientDao;
    }

    public ClientDaoImpl getClientDao() {
        return clientDao;
    }

    public Client findClientByPhone(String phone) throws Exception {
        return clientDao.findByPhone(phone);
    }

    public Client findClientByPassport(String passport) {
        return clientDao.findByPassport(passport);
    }

    public void createClient(Client templateClient) {
        clientDao.createClient(templateClient);
    }

    public void updateClient(Client client) {
        clientDao.updateClient(client);
    }
}
