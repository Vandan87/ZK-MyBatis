package com.mapper;

import java.util.List;
import com.model.Customer;

/**
 * This class interacts with the database using the CustomerMapper to perform CRUD operation on customer data.
 * 
 * @author VANDAN
 */
public interface CustomerMapper {

	List<Customer> getAllCustomer();

	void insertCustomer(Customer customer);

	void updateCustomer(Customer customer);

	void deleteCustomer(Customer customer);
	
}