package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.repository.ClientDaoImpl;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import com.kerrli.BanksystemJava_4sem.service.OperationService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class OperationController {
    private OperationService operationService;

    public OperationController() {
        operationService = new OperationService();
    }

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/push_account")
    public String pushAccount(@RequestParam String accountNum,
                                @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            operationService.pushAccount(accountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_push_account", "Счет пополнен. ");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_push_account", "Пополнение не выполнено. " +
                    e.getMessage());
        }
        return "redirect:/operwork#push_account";
    }

    @PostMapping("/pop_account")
    public String popAccount(@RequestParam String accountNum,
                              @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            operationService.popAccount(accountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_pop_account", "Снятие средств выполнено. ");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_pop_account", "Снятие не выполнено. " +
                    e.getMessage());
        }
        return "redirect:/operwork#pop_account";
    }

    @PostMapping("/transaction_in")
    public String transactionIn(@RequestParam String debitAccountNum, @RequestParam String creditAccountNum,
                         @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            String report = operationService.operation(debitAccountNum, creditAccountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_transaction_in", report);
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_transaction_in", e.getMessage());
        }
        return "redirect:/operwork#transaction_in";
    }
    
    @PostMapping("/transaction_out")
    public String transactionOut(@RequestParam String debitAccountNum, @RequestParam String phone,
                                @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            Account creditAccount = operationService.getCreditAccountByPhone(debitAccountNum, phone);
            String report = operationService.operation(debitAccountNum, creditAccount.getAccountNum(),
                    sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_transaction_out", report);
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_transaction_out", e.getMessage());
        }
        return "redirect:/operwork#transaction_out";
    }
}
