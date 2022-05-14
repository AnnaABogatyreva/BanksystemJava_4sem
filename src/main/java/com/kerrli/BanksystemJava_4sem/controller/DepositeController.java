package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.DepositeService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class DepositeController {
    private final DepositeService depositeService;

    public DepositeController() {
        depositeService = new DepositeService();
    }

    public DepositeController(DepositeService depositeService) {
        this.depositeService = depositeService;
    }

    @PostMapping("/create_deposite")
    public String createDeposite(@RequestParam String depositeType, @RequestParam String accountNum,
                          @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            depositeService.createDeposite(depositeType, accountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_create_deposite", "Вклад создан.");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_create_deposite", "Ошибка при открытии вклада. " +
                    e.getMessage());
        }
        return "redirect:/operwork#create_deposite";
    }
}
