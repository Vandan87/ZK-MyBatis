package com.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.dao.CustomerDao;
import com.dao.CustomerDaoimpl;
import com.model.Customer;

/**
 * This class provides mathods to interact with customer data and delegates the actual
 * database operations to CustomerDao.
 * 
 * @author VANDAN
 */
@Service("customerService")
public class CustomerServiceimpl implements CustomerService {

	CustomerDao customerDao = new CustomerDaoimpl();

	@Override
	public String saveOrUpdateCustomer(Customer customer) throws Exception {
		String response = null;
		// Check if the customer already han an ID; if yes, update the customer; otherwise, save a new customer.
		if (customer.getId() > 0) {
			response = customerDao.updateCustomer(customer);
		} else {
			response = customerDao.saveCustomer(customer);
		}
		return response;
	}

	@Override
	public List<Customer> listAllCustomer() {
		return customerDao.listAllCustomer();
	}

	@Override
	public List<Customer> deleteCustomer(Customer customer) {
		return customerDao.deleteCustomer(customer);
	}

}