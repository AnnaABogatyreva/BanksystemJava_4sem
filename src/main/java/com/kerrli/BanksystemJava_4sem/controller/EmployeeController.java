package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class EmployeeController {
    EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    @GetMapping("/index")
    public String index(HttpSession httpSession, Model model) {
        httpSession.removeAttribute("client");
        httpSession.removeAttribute("employee");
        return "index";
    }

    @GetMapping("/oper")
    public String oper(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_name", employee.getName());
        model.addAttribute("employee_role", employee.getRole());
        return "oper";
    }

    @GetMapping("/acc")
    public String acc(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_name", employee.getName());
        model.addAttribute("employee_role", employee.getRole());
        return "acc";
    }

    /*@GetMapping("/operwork")
    public String operwork(HttpSession httpSession, Model model) {
        Employee employee = (Employee) httpSession.getAttribute("employee");
        model.addAttribute("employee_name", employee.getName());
        model.addAttribute("employee_role", employee.getRole());
        Client client = (Client) httpSession.getAttribute("client");
        model.addAttribute("client_name", client.getName());
        model.addAttribute("client_passport", client.getPassport());
        return "acc";
    }*/

    @PostMapping("/signin")
    public String signin(@RequestParam String login, @RequestParam String password,
                         @RequestParam(name="message", required=false, defaultValue="") String message,
                         HttpSession httpSession, Model model) {
        boolean passOk = employeeService.checkPassword(login, password);
        if (passOk) {
            Employee employee = employeeService.findUser(login);
            httpSession.setAttribute("employee", employee);
            model.addAttribute("employee_name", employee.getName());
            model.addAttribute("employee_role", employee.getRole());
            if (employee.getRole().compareTo("admin") == 0 || employee.getRole().compareTo("oper") == 0)
                return "oper";
            else if (employee.getRole().compareTo("accountant") == 0)
                return "acc";
            else
                return "index";
        }
        else {
            model.addAttribute("message", "Неверный пароль");
            return "index";
        }
    }

    @PostMapping("/signout")
    public String signout(HttpSession httpSession) {
        //httpSession.removeAttribute("client");
        //httpSession.removeAttribute("employee");
        return "index";
    }
}
