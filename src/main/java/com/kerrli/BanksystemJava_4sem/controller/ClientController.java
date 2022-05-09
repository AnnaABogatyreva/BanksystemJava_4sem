package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

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
    }

    @PostMapping("/create_client")
    public String createClient(@RequestParam String name, @RequestParam String phone, @RequestParam String passport,
                               @RequestParam(defaultValue = "") String email,
                               @RequestParam(defaultValue = "") String birthdateString,
                               @RequestParam(defaultValue = "") String address,
                               @RequestParam(defaultValue = "") String passgiven,
                               @RequestParam(defaultValue = "") String passcode,
                               @RequestParam(defaultValue = "") String passdateString,
                               @RequestParam(defaultValue = "") String sex,
                               @RequestParam(defaultValue = "") String birthplace,
                               @RequestParam(defaultValue = "") String reg,
                               HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_role", employee.getRole());
        model.addAttribute("employee_name", employee.getName());
        Date birthdate = Lib.parseDate(birthdateString);
        Date passdate = Lib.parseDate(passdateString);
        Client client = new Client(name, email, birthdate, passport, address, phone, passgiven,
                passcode, passdate, sex, birthplace, reg);
        try {
            clientService.createClient(client);
            model.addAttribute("message_create_client", "Клиент успешно создан.");
            return "oper";
        }
        catch (Exception e) {
            model.addAttribute("message_create_client", "Клиент не создан. " + e.getMessage());
            return "oper";
        }
    }
}
