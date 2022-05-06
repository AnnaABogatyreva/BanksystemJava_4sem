package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Table(name = "employee")
public class Employee {
    @Column(name = "login")
    private String login;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;

    public Employee() {}

    public Employee(String login, String password) {
        this.login = login;
        this.password = hashPassword(password);
    }

    public String hashPassword(String password) {
        String hash = "";
        try {
            MessageDigest md = null;
            byte[] bytesOfMessage = password.getBytes("UTF-8");
            md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            for (int n = 0; n < 16; n++)
                hash += String.format("%02x", theMD5digest[n]);
            System.out.println(hash);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hash;
    }

    @Override
    public String toString() {
        return "Сотрудник: " + name;
    }
}
