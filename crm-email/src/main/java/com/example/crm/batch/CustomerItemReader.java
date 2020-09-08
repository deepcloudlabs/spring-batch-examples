package com.example.crm.batch;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;

import com.example.crm.dto.Customer;

public class CustomerItemReader implements ItemReader<Customer> {
	private static final int IDENTITY = 0;
	private static final int FULLNAME = 1;
	private static final int EMAIL = 2;
	private static final int USE_EMAIL = 3;
	private static final int SMS = 4;
	private static final int USE_SMS = 5;
	private ItemReader<Customer> delegate;
	@Value("#{jobParameters['filename']}")
	private String filename;

	@Override
	public Customer read() throws Exception {
		if (Objects.isNull(delegate)) {
			var lines = Files.readAllLines(Paths.get("c:/tmp", filename));
			var customers = lines.stream().map(line -> line.split(",")).map(CustomerItemReader::toCustomer)
					.collect(Collectors.toList());
			delegate = new IteratorItemReader<>(customers);
		}
		return delegate.read();
	}

	private static Customer toCustomer(String[] line) {
		System.out.println(line[USE_SMS] + "," + line[USE_EMAIL]);
		var customer = new Customer();
		customer.setIdentity(line[IDENTITY]);
		customer.setFullname(line[FULLNAME]);
		customer.setEmail(line[EMAIL]);
		customer.setUseEmail(Boolean.parseBoolean(line[USE_EMAIL]));
		customer.setSms(line[SMS]);
		customer.setUseSms(Boolean.parseBoolean(line[USE_SMS]));
		return customer;
	}
}
