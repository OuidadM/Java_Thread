package main.dao;

import main.Customer;

public interface CustomerDAO {
    public boolean customerExists(int id);
    public Customer getCustomer(int id);
}
