package main;

public class Customer {
    private int id;
    private String nom;
    private String email;
    private String phone;
    public Customer(int id, String nom, String email, String phone) {
        this.id = id;
        this.nom = nom;
        this.email=email;
        this.phone=phone;
    }
    int getId(){
        return id;
    }
    String getNom(){
        return nom;
    }
    String getEmail(){
        return email;
    }
    String getPhone(){
        return phone;
    }
}
