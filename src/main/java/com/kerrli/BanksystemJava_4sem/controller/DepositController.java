package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.DepositService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class DepositController {
    private final DepositService depositService;

    public DepositController() {
        depositService = new DepositService();
    }

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/create_deposit")
    public String createDeposit(@RequestParam String depositType, @RequestParam String accountNum,
                          @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            depositService.createDeposit(depositType, accountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_create_deposit", "Вклад создан.");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_create_deposit", "Ошибка при открытии вклада. " +
                    e.getMessage());
        }
        return "redirect:/operwork#create_deposit";
    }

    @PostMapping("/close_deposit")
    public String closeDeposit(@RequestParam int depositId, @RequestParam String accountNum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            depositService.closeDeposit(depositId, accountNum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_close_deposit", "Вклад закрыт. ");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_close_deposit", "Ошибка закрытия вклада. " +
                    e.getMessage());
        }
        return "redirect:/operwork#close_deposit";
    }
}
