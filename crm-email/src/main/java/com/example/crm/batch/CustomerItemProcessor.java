package com.example.crm.batch;

import org.springframework.batch.item.ItemProcessor;

import com.example.crm.dto.Customer;

public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer customer) throws Exception {
		// send email using soap service
		if (customer.isUseEmail())
			System.out.println("Sending email to " + customer);
		// send sms using external soap service
		if (customer.isUseSms())
			System.out.println("Sending sms to " + customer);
		return customer;
	}

}
