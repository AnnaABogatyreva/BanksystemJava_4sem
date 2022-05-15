package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.CreditService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CreditController {
    private final CreditService creditService;

    public CreditController() {
        creditService = new CreditService();
    }

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @PostMapping("/create_credit")
    public String createCredit(@RequestParam String creditType, @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        Client client = (Client) httpSession.getAttribute("client");
        try {
            creditService.createCredit(creditType, sum, client.getId(), employee.getLogin());
            Lib.setAttribute(httpSession, "report_create_credit", "Кредит выдан.");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_create_credit", "Ошибка при выдаче кредита. " +
                    e.getMessage());
        }
        return "redirect:/operwork#create_credit";
    }

    @GetMapping("/graphcredit")
    public String creditGraph(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee", employee);
        String emprole = (String) httpSession.getAttribute("emprole");
        model.addAttribute("emprole", emprole);
        Client client = (Client) httpSession.getAttribute("client");
        model.addAttribute("client", client);
        Map map = (Map) httpSession.getAttribute("credit");
        model.addAttribute("credit", map);
        if (employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("operator") == 0) {
            Lib.moveAttributeToModel(httpSession, model);
            return "graphcredit";
        }
        return "redirect:/acc";
    }

    @PostMapping("/show_graph_credit")
    public String showGraphCredit(@RequestParam int creditId, HttpSession httpSession) {
        Map<String, Object> map = creditService.getCreditInfo(creditId);
        httpSession.setAttribute("credit", map);
        return "redirect:/graphcredit";
    }
}

