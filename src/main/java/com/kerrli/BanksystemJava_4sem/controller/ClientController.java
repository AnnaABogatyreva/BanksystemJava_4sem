package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientController {
    private ClientService clientService;

    public ClientController() {
        clientService = new ClientService();
    }

    @PostMapping("/oper/find_client_by_passport")
    public String findClientByPassport(@RequestParam String passport, Model model) {
        Client client = clientService.findClientByPassport(passport);
        if (client == null) {
            model.addAttribute("message_find_client", "Клиент не найден");
            return "oper";
        }
        model.addAttribute("client", client);
        model.addAttribute("client_name", client.getName());
        model.addAttribute("client_passport", client.getPassport());
        return "operwork";
    }

}
