package com.dao;

import java.util.List;
import com.model.Customer;

/**
 * This interface defines the contract for performing operations related to
 * Customer data in the database.
 * 
 * @author VANDAN
 */
public interface CustomerDao {

	String saveCustomer(Customer customer);

	List<Customer> listAllCustomer();

	String updateCustomer(Customer customer);

	List<Customer> deleteCustomer(Customer customer);

}