package com.example.crm.batch;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import javax.annotation.PreDestroy;

import org.springframework.batch.item.ItemWriter;

import com.example.crm.dto.Customer;

public class CustomerItemWriter implements ItemWriter<Customer>, Closeable {

	@Override
	@PreDestroy
	public void close() throws IOException {
	}

	@Override
	public void write(List<? extends Customer> customers) throws Exception {
		customers.forEach(customer -> System.out.println(customer + " is processed."));
	}

}
