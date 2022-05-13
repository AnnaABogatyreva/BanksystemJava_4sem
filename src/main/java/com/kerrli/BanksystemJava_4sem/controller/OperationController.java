package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.OperationService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
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

    @PostMapping("/transaction_in")
    public String transactionIn(@RequestParam String debitAccountNum, @RequestParam String creditAccountNum,
                         @RequestParam double sum, HttpSession httpSession) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        try {
            operationService.transaction(creditAccountNum, debitAccountNum, sum, employee.getLogin());
            Lib.setAttribute(httpSession, "report_transaction_in", "Успешный перевод.");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_transaction_in", e.getMessage());
        }
        return "redirect:/operwork#transaction_in";
    }

}
