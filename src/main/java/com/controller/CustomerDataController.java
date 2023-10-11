package com.controller;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import com.model.Customer;
import com.service.CustomerService;
import com.service.CustomerServiceimpl;

/**
 * It contains various methods related to customer data handling
 * 
 * @author VANDAN
 */
public class CustomerDataController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	@Wire
	Grid tableGrid;

	@Wire
	Paging paging;

	Customer customer;
	CustomerService customerService = new CustomerServiceimpl();
	ListModelList<Customer> customerList = new ListModelList<>();
	int currentPage = 0;
	int pageSize = 5;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		refreshCustomerData();
		tableGrid.setModel(customerList);
		paging.setPageSize(pageSize);
		paging.setActivePage(currentPage);
		String successParam = Executions.getCurrent().getParameter("saveSuccess");
		if (successParam != null) {
			if ("true".equals(successParam)) {
				showNotification("Customer data is added", "info");
			} else if ("false".equals(successParam)) {
				showNotification("Customer data is updated", "info");
			}
		}
	}

	boolean setBirthDate(Customer customer) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date parsedDate = new Date(dateFormat.parse(customer.getFormattedDateOfBirth()).getTime());
			customer.setBirthDate(parsedDate);
			return true;
		} catch (ParseException e) {
			// Handle date parsing exception
			return false;
		}
	}

	@Listen("onClick = #newData")
	public void redirectToCustomerForm() {
		Session sess = Sessions.getCurrent();
		Customer existingCustomer = (Customer) sess.getAttribute("customer");
		sess.removeAttribute("customer");
		if (existingCustomer != null) {
			sess.setAttribute("addMode", true);
		}
		Executions.sendRedirect("/crud_page/customerForm.zul");
	}

	public void showNotification(String message, String type) {
		Clients.showNotification(message, type, tableGrid, "top_center", 1500, true);
	}

	// Refreshes the displayed customer data in the grid
	public void refreshCustomerData() {
		try {
			List<Customer> allCustomers = customerService.listAllCustomer();
			int startIndex = currentPage * pageSize;
			int endIndex = Math.min(startIndex + pageSize, allCustomers.size());
			List<Customer> customers = allCustomers.subList(startIndex, endIndex);
			customerList.clear();
			customerList.addAll(customers);
			updatePagingInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePagingInfo() {
		int totalSize = customerService.listAllCustomer().size();
		paging.setTotalSize(totalSize);
		paging.setPageSize(pageSize);
		paging.setActivePage(currentPage);
	}

	@Listen("onPaging = #paging")
	public void onPaging() {
		currentPage = paging.getActivePage();
		refreshCustomerData();
	}

	@Listen("onUpdate = #windowCustomerData")
	public void onUpdate(ForwardEvent event) {
		Customer selectedCustomer = (Customer) event.getData();
		Session sess = Sessions.getCurrent();
		sess.setAttribute("customer", selectedCustomer);
		Executions.sendRedirect("/crud_page/customerForm.zul");
	}

	@Listen("onDelete = #windowCustomerData")
	public void deleteCustomer(final ForwardEvent event) {
		Customer customer = (Customer) event.getData();
		Messagebox.show("Are you sure want delete this " + customer.getFirstName() + " " + customer.getLastName() + "'s data?", "Confirmation message", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
			@Override
			public void onEvent(final Event evt) throws InterruptedException {
				if (Messagebox.ON_YES.equals(evt.getName())) {
					Customer deleteCustomer = (Customer) event.getData();
					List<Customer> result = customerService.deleteCustomer(deleteCustomer);
					showNotification("Customer delete successfully", "info");
					customerList.clear();
					customerList.addAll(result);
					int totalSize = customerService.listAllCustomer().size();
					int totalPages = (int) Math.ceil((double) totalSize / pageSize);
					// Check if the current page is beyond the total number of pages
					if (currentPage >= totalPages) {
						currentPage = Math.max(0, totalPages - 1);
					}
					refreshCustomerData();
					} else {
						showNotification("Customer data is safe", "warning");
					}
			}
		});
	}

}