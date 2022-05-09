package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.EmployeeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("employee")
public class EmployeeController {
    EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    @PostMapping("/signin")
    public String signin(@RequestParam String login, @RequestParam String password,
                         @RequestParam(name="message", required=false, defaultValue="") String message,
                         Model model, HttpSession httpSession) {
        boolean passOk = employeeService.checkPassword(login, password);
        if (passOk) {
            Employee employee = employeeService.findUser(login);
            httpSession.setAttribute("employee", employee);
            model.addAttribute("employee_role", employee.getRole());
            model.addAttribute("employee_name", employee.getName());
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
    public String signout(@RequestParam String login, @RequestParam String password, Model model) {
        // дописать выход из профиля
        return "index";
    }
}
