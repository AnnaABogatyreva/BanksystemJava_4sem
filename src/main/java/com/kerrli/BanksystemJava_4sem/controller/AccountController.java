package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Currency;
import com.kerrli.BanksystemJava_4sem.service.AccountService;
import com.kerrli.BanksystemJava_4sem.service.ClientService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AccountController {
    private AccountService accountService;

    public AccountController() {
        accountService = new AccountService();
    }

    @PostMapping("/create_account")
    public String createAccount(@RequestParam String currencyCode, HttpSession httpSession) {
        int idClient = ((Client) httpSession.getAttribute("client")).getId();
        Account account = accountService.createAccount(idClient, currencyCode, "40817", "Счет физ. лица");
        Lib.setAttribute(httpSession, "report_create_account", "Счет успешно создан.");
        return "redirect:/operwork#create_account";
    }

    @PostMapping("/close_account")
    public String closeAccount(@RequestParam String accountNum, HttpSession httpSession) {
        try {
            Account account = accountService.closeAccount(accountNum);
            Lib.setAttribute(httpSession, "report_close_account", "Счет закрыт.");
        }
        catch (Exception e) {
            Lib.setAttribute(httpSession, "error_close_account", "Счет не закрыт. " + e.getMessage());
        }
        return "redirect:/operwork#close_account";
    }
}
