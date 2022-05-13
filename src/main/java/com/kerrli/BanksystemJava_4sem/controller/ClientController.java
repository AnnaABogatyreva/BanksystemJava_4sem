package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.repository.AccountDaoImpl;
import com.kerrli.BanksystemJava_4sem.repository.CurrencyDaoImpl;
import com.kerrli.BanksystemJava_4sem.service.AccountService;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import com.kerrli.BanksystemJava_4sem.service.CurrencyService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ClientController {
    private ClientService clientService;

    public ClientController() {
        clientService = new ClientService();
    }

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/operwork")
    public String operwork(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        if (!(employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("operator") == 0)) {
            return "redirect:/acc";
        }
        model.addAttribute("employee", employee);
        String emprole = (String) httpSession.getAttribute("emprole");
        model.addAttribute("emprole", emprole);
        Client client = (Client) httpSession.getAttribute("client");
        model.addAttribute("client", client);
        Lib.moveAttributeToModel(httpSession, model);
        Session tempSession = clientService.getClientDao().getSession();
        List currencyList = new CurrencyService(new CurrencyDaoImpl(tempSession)).getCurrencyList();
        model.addAttribute("currencyList", currencyList);
        List zeroAccountList = new AccountService(new AccountDaoImpl(tempSession)).getZeroAccountList(client.getId());
        model.addAttribute("zeroAccountList", zeroAccountList);
        List accountList = new AccountService(new AccountDaoImpl(tempSession)).getAccountList(client.getId());
        model.addAttribute("accountList", accountList);
        return "operwork";
    }

    @PostMapping("/find_client_by_passport")
    public String findClientByPassport(@RequestParam String passport, HttpSession httpSession, Model model) {
        Client client = clientService.findClientByPassport(passport);
        if (client == null) {
            Lib.setAttribute(httpSession, "error_find_client", "Клиент не найден. ");
            return "redirect:/oper#find_client_by_passport";
        }
        httpSession.setAttribute("client", client);
        return "redirect:/operwork";
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
        Date birthdate = Lib.parseDate(birthdateString);
        Date passdate = Lib.parseDate(passdateString);
        Client client = new Client(name, email, birthdate, passport, address, phone, passgiven,
                passcode, passdate, sex, birthplace, reg);
        try {
            clientService.createClient(client);
            Lib.setAttribute(httpSession, "report_create_client", "Клиент успешно создан. ");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_create_client", "Клиент не создан. " + e.getMessage());
        }
        return "redirect:/oper#create_client";
    }

    @PostMapping("/edit_client")
    public String editClient(@RequestParam String name, @RequestParam String phone, @RequestParam String passport,
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
        Date birthdate = Lib.parseDate(birthdateString);
        Date passdate = Lib.parseDate(passdateString);
        Client client = (Client) httpSession.getAttribute("client");
        Client editclient = new Client(client.getId(), name, email, birthdate, passport, address, phone, passgiven,
                passcode, passdate, sex, birthplace, reg);
        try {
            clientService.updateClient(editclient);
            httpSession.setAttribute("client", editclient);
            Lib.setAttribute(httpSession, "report_create_client", "Данные клиента успешно обновлены. ");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_create_client", "Данные клиента не обновлены. " +
                    e.getMessage());
        }
        return "redirect:/operwork#edit_client";
    }
}
