package com.dao;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;
import com.mapper.CustomerMapper;
import com.model.Customer;
import com.util.MyBatisUtil;

/**
 * This class interacts with the database using the CustomerMapper to perform
 * CRUD operation on customer data.
 * 
 * @author VANDAN
 */
@Repository("customerDao")
public class CustomerDaoimpl implements CustomerDao {

	public List<Customer> listAllCustomer() {
		// Open a new database session
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		CustomerMapper customerMapper = session.getMapper(CustomerMapper.class);
		List<Customer> customerList = customerMapper.getAllCustomer();
		session.commit();
		session.close();
		return customerList;
	}

	public String saveCustomer(Customer customer) {
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		CustomerMapper customerMapper = session.getMapper(CustomerMapper.class);
		customerMapper.insertCustomer(customer);
		session.commit();
		session.close();
		return "save";
	}

	public String updateCustomer(Customer customer) {
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		CustomerMapper customerMapper = session.getMapper(CustomerMapper.class);
		customerMapper.updateCustomer(customer);
		session.commit();
		session.close();
		return "update";
	}

	public List<Customer> deleteCustomer(Customer customer) {
		SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession();
		CustomerMapper customerMapper = session.getMapper(CustomerMapper.class);
		customerMapper.deleteCustomer(customer);
		session.commit();
		session.close();
		return listAllCustomer();
	}

}