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
            model.addAttribute("error_find_client", "Клиент не найден");
            return "oper";
        }
        httpSession.setAttribute("client", client);
        model.addAttribute("client_name", client.getName());
        model.addAttribute("client_phone", client.getPhone());
        model.addAttribute("client_passport", client.getPassport());
        model.addAttribute("client_passport", client.getPassport());
        model.addAttribute("client_email", client.getEmail());
        model.addAttribute("client_birthdate", client.getBirthdate());
        model.addAttribute("client_address", client.getAddress());
        model.addAttribute("client_passgiven", client.getPassgiven());
        model.addAttribute("client_passcode", client.getPasscode());
        model.addAttribute("client_passdate", client.getPassdate());
        model.addAttribute("client_sex", client.getSex());
        model.addAttribute("client_birthplace", client.getBirthdate());
        model.addAttribute("client_reg", client.getReg());
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
        String exception = clientService.createClient(client);
        if (exception.compareTo("") == 0) {
            model.addAttribute("report_create_client", "Клиент успешно создан.");
            return "oper";
        }
        else {
            model.addAttribute("error_create_client", "Клиент не создан. " + exception);
            return "oper";
        }
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
        model.addAttribute("employee_role", employee.getRole());
        model.addAttribute("employee_name", employee.getName());
        Date birthdate = Lib.parseDate(birthdateString);
        Date passdate = Lib.parseDate(passdateString);
        Client client = (Client) httpSession.getAttribute("client");
        Client editclient = new Client(client.getId(), name, email, birthdate, passport, address, phone, passgiven,
                passcode, passdate, sex, birthplace, reg);
        String exception = clientService.updateClient(editclient);
        if (exception.compareTo("") == 0) {
            httpSession.setAttribute("client", editclient);
            model.addAttribute("report_create_client", "Данные клиента успешно обновлены.");
        }
        else  {
            model.addAttribute("error_create_client", "Данные клиента не обновлены. " +
                    exception);
        }
        model.addAttribute("client_name", editclient.getName());
        model.addAttribute("client_phone", editclient.getPhone());
        model.addAttribute("client_passport", editclient.getPassport());
        model.addAttribute("client_passport", editclient.getPassport());
        model.addAttribute("client_email", editclient.getEmail());
        model.addAttribute("client_birthdate", editclient.getBirthdate());
        model.addAttribute("client_address", editclient.getAddress());
        model.addAttribute("client_passgiven", editclient.getPassgiven());
        model.addAttribute("client_passcode", editclient.getPasscode());
        model.addAttribute("client_passdate", editclient.getPassdate());
        model.addAttribute("client_sex", editclient.getSex());
        model.addAttribute("client_birthplace", editclient.getBirthdate());
        model.addAttribute("client_reg", editclient.getReg());
        return "operwork";
    }
}
