package com.controller;

import java.util.Calendar;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import com.model.Customer;
import com.service.CustomerService;
import com.service.CustomerServiceimpl;

/**
 * This is the controller class for handling customer-related actions in your
 * application.
 * 
 * @author Vandan
 */
public class CustomerController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	@Wire
	Intbox customerId;

	@Wire
	Textbox firstName;

	@Wire
	Textbox lastName;

	@Wire
	Datebox birthDate;

	@Wire
	Textbox mobileNumber;

	@Wire
	Textbox age;

	@Wire
	Textbox address;

	@Wire
	Textbox email;

	@Wire
	Grid tableGrid;

	@Wire
	Button saveCustomer, viewList, resetForm;

	@Wire
	Caption formCaption;

	Customer customer;

	CustomerService customerService = new CustomerServiceimpl();
	Customer originalCustomer;
	ListModelList<Customer> customerList = new ListModelList<>();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Session sessions = Sessions.getCurrent();
		customer = (Customer) (sessions).getAttribute("customer");
		if (customer != null) {
			// If cutomer exists, populate the form fields for editing
			customerId.setValue(customer.getId());
			firstName.setValue(customer.getFirstName());
			lastName.setValue(customer.getLastName());
			birthDate.setValue(customer.getBirthDate());
			mobileNumber.setValue(customer.getMobileNumber());
			age.setValue(customer.getAge());
			address.setValue(customer.getAddress());
			email.setValue(customer.getEmail());
			formCaption.setLabel("Customer edit form");
			saveCustomer.setLabel("Update");

			originalCustomer = new Customer();
			originalCustomer.setId(customer.getId());
			originalCustomer.setFirstName(customer.getFirstName());
			originalCustomer.setLastName(customer.getLastName());
			originalCustomer.setBirthDate(customer.getBirthDate());
			originalCustomer.setMobileNumber(customer.getMobileNumber());
			originalCustomer.setAge(customer.getAge());
			originalCustomer.setAddress(customer.getAddress());
			originalCustomer.setEmail(customer.getEmail());
		} else {
			// Check if you are in "save" mode
			Boolean saveMode = (Boolean) sessions.getAttribute("saveMode");
			if (saveMode != null && saveMode) {
				// You are in "save" mode
				clearForm();
				formCaption.setLabel("Customer Registration");
				saveCustomer.setLabel("Save");
				// Clear the "saveMode" session attribute
				sessions.removeAttribute("addMode");
			}
		}
		checkFieldsNotEmpty();
	}

	// Save or update customer data
	@Listen("onClick = #saveCustomer")
	public void saveOrUpdateCustomer() throws Exception {
		Customer customer = new Customer();
		try {
			if (validate()) {
				customer.setId(customerId.getValue());
				customer.setFirstName(firstName.getValue());
				customer.setLastName(lastName.getValue());
				customer.setBirthDate(birthDate.getValue());
				customer.setMobileNumber(mobileNumber.getValue());
				customer.setAge(age.getValue());
				customer.setAddress(address.getValue());
				customer.setEmail(email.getValue());
				String response = customerService.saveOrUpdateCustomer(customer);
				if ("save".equals(response)) {
					Executions.sendRedirect("/crud_page/customerData.zul?saveSuccess=true");
				} else {
					Executions.sendRedirect("/crud_page/customerData.zul?saveSuccess=false");
				}
				// Refresh the customer list after saving/updating
				List<Customer> result = customerService.listAllCustomer();
				customerList.clear();
				customerList.addAll(result);
				clearForm();
			}
		} catch (Exception exception) {
			System.err.println("Message : " + exception.getMessage());
		}
	}

	@Listen("onChange = #birthDate")
	public void calculateAge() {
		if (birthDate.getValue() != null) {
			int calculatedAge = calculateAge(birthDate.getValue());
			age.setValue(String.valueOf(calculatedAge));
		}
	}

	int calculateAge(java.util.Date birthDate) {
		Calendar dob = Calendar.getInstance();
		dob.setTime(birthDate);
		Calendar now = Calendar.getInstance();
		int countAge = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
		if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			countAge--;
		}
		return countAge;
	}

	@Listen("onClick = #resetForm")
	public void clearForm() {
		firstName.setValue("");
		lastName.setValue("");
		birthDate.setValue(null);
		mobileNumber.setValue(null);
		age.setValue(null);
		address.setValue("");
		email.setValue("");
	}

	@Listen("onClick = #viewList")
	public void redirectToCustomerDataPage() {
		String targetPageUrl = "/crud_page/customerData.zul";
		Executions.sendRedirect(targetPageUrl);
	}

	// All form field validation
	public boolean validate() throws Exception {
		try {
			if (firstName.getValue().isBlank() || !firstName.getValue().matches("^[a-zA-Z]*$")) {
				throw new Exception("Please provide valid first name");
			} else if (lastName.getValue().isBlank() || !lastName.getValue().matches("^[a-zA-Z]*$")) {
				throw new Exception("Please provide valid last name");
			} else if (birthDate.getValue() == null) {
				throw new Exception("Please provide birth date");
			} else if (!(mobileNumber.getValue().matches("^[0-9]{10}$"))) {
				throw new Exception("Please provide 10 numbers.");
			} else if (address.getValue().isBlank()) {
				throw new Exception("Please provide valid address");
			} else if (!email.getValue().matches("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
				throw new Exception("Please provide valid email address.");
			} else {
				int ageValue = Integer.parseInt(age.getValue());
				if (ageValue < 1 || ageValue > 110) {
					throw new Exception("Please provide a valid age between 1 and 110.");
				}
				return true;
			}
		} catch (Exception e) {
			Clients.showNotification(e.getMessage(), "warning", tableGrid, "top_center", 1000, true);
		}
		return false;
	}

	@Listen("onChange = #firstName, #lastName, #birthDate, #mobileNumber, #address, #email")
	public void checkFieldsNotEmpty() {
		saveCustomer.setVisible(false);
		boolean allFieldsFilled = !firstName.getValue().isBlank() && !lastName.getValue().isBlank()
				&& birthDate.getValue() != null && !mobileNumber.getValue().isBlank() && !address.getValue().isBlank()
				&& !email.getValue().isBlank();

		// Check if there are changes in the form fields compared to the original customer data
		if (originalCustomer != null) {
			boolean fieldsChanged = !firstName.getValue().equals(originalCustomer.getFirstName())
					|| !lastName.getValue().equals(originalCustomer.getLastName())
					|| !birthDate.getValue().equals(originalCustomer.getBirthDate())
					|| !mobileNumber.getValue().equals(originalCustomer.getMobileNumber())
					|| !address.getValue().equals(originalCustomer.getAddress())
					|| !email.getValue().equals(originalCustomer.getEmail());

			saveCustomer.setVisible(fieldsChanged && allFieldsFilled);
		} else {
			saveCustomer.setVisible(allFieldsFilled);
		}
	}

}