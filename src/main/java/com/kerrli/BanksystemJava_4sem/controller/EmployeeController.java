package com.kerrli.BanksystemJava_4sem.controller;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.service.EmployeeService;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmployeeController {
    EmployeeService employeeService;

    public EmployeeController() {
        employeeService = new EmployeeService();
    }

    @GetMapping("/signin")
    public String signin(@RequestParam String login, @RequestParam String password,
                         @RequestParam(name="message", required=false, defaultValue="") String message, Model model) {
        boolean passOk = employeeService.checkPassword(login, password);
        if (passOk) {
            Employee employee = employeeService.findUser(login);
            model.addAttribute("employee", employee);
            model.addAttribute("emp_role", employee.getRole());
            model.addAttribute("emp_name", employee.getName());
            System.out.println(model.getAttribute("auth_employee"));
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

    @GetMapping("/signout")
    public String signout(@RequestParam String login, @RequestParam String password, Model model) {
        // дописать выход из профиля
        return "index";
    }
}
