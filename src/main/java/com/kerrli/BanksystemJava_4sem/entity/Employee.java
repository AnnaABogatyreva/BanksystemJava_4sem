package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.MessageDigest;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "login")
    private String login;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;

    public Employee() {
        this.login = null;
        this.name = null;
        this.password = null;
        this.role = null;
    }

    public Employee(String login, String name, String password, String role) {
        this.login = login;
        this.name = name;
        this.password = hashPassword(password);
        this.role = role;
    }

    public Employee(Employee employee) {
        this.login = employee.login;
        this.name = employee.name;
        this.password = employee.password;
        this.role = employee.role;
    }

    public static String hashPassword(String password) {
        String hash = "";
        try {
            MessageDigest md = null;
            byte[] bytesOfMessage = password.getBytes("UTF-8");
            md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            for (int n = 0; n < 16; n++)
                hash += String.format("%02x", theMD5digest[n]);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hash;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Сотрудник: " + name;
    }
}
