package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "converter")
public class Converter {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "currency")
    private String currency;
    @Column(name = "buy")
    private double buy;
    @Column(name = "cost")
    private double cost;
    @Column(name = "sell")
    private double sell;
    @Column(name = "dt")
    private Date date;
    @Column(name = "current")
    private int current;

    public Converter() {}

    public Converter(String currency, double buy, double cost, double sell, Date date, int current) {
        this.currency = currency;
        this.buy = buy;
        this.cost = cost;
        this.sell = sell;
        this.date = date;
        this.current = current;
    }

    public int getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public double getBuy() {
        return buy;
    }

    public double getCost() {
        return cost;
    }

    public double getSell() {
        return sell;
    }

    public Date getDate() {
        return date;
    }

    public int getCurrent() {
        return current;
    }
}
