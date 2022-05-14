package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.entity.Emprole;
import com.kerrli.BanksystemJava_4sem.repository.AccountDaoImpl;
import com.kerrli.BanksystemJava_4sem.repository.CurrencyDaoImpl;
import com.kerrli.BanksystemJava_4sem.service.AccountService;
import com.kerrli.BanksystemJava_4sem.service.CurrencyService;
import com.kerrli.BanksystemJava_4sem.service.EmployeeService;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EmployeeController {
    EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/")
    public String start() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(HttpSession httpSession, Model model) {
        httpSession.removeAttribute("client");
        httpSession.removeAttribute("employee");
        Lib.moveAttributeToModel(httpSession, model);
        return "index";
    }

    @GetMapping("/oper")
    public String oper(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee", employee);
        String emprole = (String) httpSession.getAttribute("emprole");
        model.addAttribute("emprole", emprole);
        if (employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("operator") == 0) {
            Lib.moveAttributeToModel(httpSession, model);
            return "oper";
        }
        return "redirect:/acc";
    }

    @GetMapping("/acc")
    public String acc(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee", employee);
        String emprole = (String) httpSession.getAttribute("emprole");
        model.addAttribute("emprole", emprole);
        model.addAttribute("operdate", Lib.formatDate(employeeService.getOperDate(), "dd.MM.yyyy"));
        Session tempSession = employeeService.getEmployeeDao().getSession();
        List foreignCurrencyList = new CurrencyService(new CurrencyDaoImpl(tempSession)).getForeignCurrencyList();
        model.addAttribute("foreignCurrencyList", foreignCurrencyList);
        List bankAccountList = new AccountService(new AccountDaoImpl(tempSession)).getBankAccountList();
        model.addAttribute("bankAccountList", bankAccountList);
        if (employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("accountant") == 0) {
            Lib.moveAttributeToModel(httpSession, model);
            return "acc";
        }
        return "redirect:/oper";
    }

    @PostMapping("/signin")
    public String signin(@RequestParam String login, @RequestParam String password,
                         @RequestParam(name="message", required=false, defaultValue="") String message,
                         HttpSession httpSession, Model model) {
        boolean passOk = employeeService.checkPassword(login, password);
        if (passOk) {
            Employee employee = employeeService.findUser(login);
            Emprole emprole = (Emprole) employeeService.getEmployeeDao().getSession().get(Emprole.class,
                    employee.getRole());
            httpSession.setAttribute("emprole", emprole.getDescript());
            model.addAttribute("emprole", emprole.getDescript());
            httpSession.setAttribute("employee", employee);
            model.addAttribute("employee", employee);
            if (employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("operator") == 0)
                return "redirect:/oper";
            else if (employee.getRole().compareTo("accountant") == 0)
                return "redirect:/acc";
            else
                return "redirect:/index";
        }
        else {
            Lib.setAttribute(httpSession, "message", "Неверный пароль");
            return "redirect:/index";
        }
    }

    @PostMapping("/signout")
    public String signout(HttpSession httpSession) {
        httpSession.removeAttribute("client");
        httpSession.removeAttribute("employee");
        return "redirect:/index";
    }
}
