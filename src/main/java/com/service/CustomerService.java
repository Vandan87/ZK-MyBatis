package com.service;

import java.util.List;
import com.model.Customer;

/**
 * This interface specifies the methods that a customer service implementation should provide.
 * 
 * @author VANDAN
 */
public interface CustomerService {

	public String saveOrUpdateCustomer(Customer customer) throws Exception;

	public List<Customer> deleteCustomer(Customer customer);

	public List<Customer> listAllCustomer();

}