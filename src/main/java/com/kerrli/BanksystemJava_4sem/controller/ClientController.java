package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Controller
public class ClientController {
    private ClientService clientService;

    public ClientController() {
        clientService = new ClientService();
    }

    @PostMapping("/operwork")
    public String findClientByPassport(@RequestParam String passport, HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_role", employee.getRole());
        model.addAttribute("employee_name", employee.getName());
        Client client = clientService.findClientByPassport(passport);
        if (client == null) {
            model.addAttribute("message_find_client", "Клиент не найден");
            return "oper";
        }
        httpSession.setAttribute("client", client);
        model.addAttribute("client_name", client.getName());
        model.addAttribute("client_passport", client.getPassport());
        return "operwork";

        /*Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_name", employee.getName());
        model.addAttribute("employee_role", employee.getRole());
        Client client = (Client) httpSession.getAttribute("client");
        model.addAttribute("client_name", client.getName());
        model.addAttribute("client_passport", client.getPassport());
        return "acc";*/
    }

}
