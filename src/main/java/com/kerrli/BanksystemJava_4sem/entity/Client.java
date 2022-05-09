package com.kerrli.BanksystemJava_4sem.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "birthdate")
    private Date birthdate;
    @Column(name = "passport")
    private String passport;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "passgiven")
    private String passgiven;
    @Column(name = "passcode")
    private String passcode;
    @Column(name = "passdate")
    private Date passdate;
    @Column(name = "sex")
    private String sex;
    @Column(name = "birthplace")
    private String birthplace;
    @Column(name = "reg")
    private String reg;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public String getPassport() {
        return passport;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassgiven() {
        return passgiven;
    }

    public String getPasscode() {
        return passcode;
    }

    public Date getPassdate() {
        return passdate;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public String getReg() {
        return reg;
    }
}
