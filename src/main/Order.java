package main;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private LocalDateTime date;
    private double amount;
    private int customer_id;
    private String status;
    public Order(int id,LocalDateTime date,double amount,int customer_id,String status){
        this.id=id;
        this.date=date;
        this.amount=amount;
        this.customer_id=customer_id;
        this.status=status;
    }

    public double getAmount() {
        return amount;
    }

    public int getCustomer_id() {
        return customer_id;
    }
    public int getId() {
        return id;
    }
    public LocalDateTime getDate() {
        return date;
    }
    public String getStatus() {
        return status;
    }

}
