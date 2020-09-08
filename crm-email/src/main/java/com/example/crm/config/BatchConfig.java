package com.example.crm.config;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.crm.batch.CustomerItemProcessor;
import com.example.crm.batch.CustomerItemReader;
import com.example.crm.batch.CustomerItemWriter;
import com.example.crm.dto.Customer;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job crmEmailSmsNotificationJob;

	@Bean
	public ExecutorService executorService() {
		return Executors.newFixedThreadPool(10);
	}

	@Bean
	public Job crmEmailSmsNotification(Step taskletStep, Step chunkStep) {
		return jobBuilders.get("crmEmailSmsNotification").start(taskletStep).next(chunkStep).build();
	}

	@Bean
	public Step taskletStep(Tasklet tasklet) {
		return stepBuilders.get("taskletStep").tasklet(tasklet).build();
	}

	@Bean
	public Step chunkStep(ItemReader<Customer> reader, ItemProcessor<Customer, Customer> processor,
			ItemWriter<Customer> writer) {
		return stepBuilders.get("chunkStep").<Customer, Customer>chunk(20).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@StepScope
	@Bean
	public ItemReader<Customer> reader() {
		return new CustomerItemReader();
	}

	@StepScope
	@Bean
	public ItemProcessor<Customer, Customer> processor() {
		return new CustomerItemProcessor();
	}

	@StepScope
	@Bean
	public ItemWriter<Customer> writer() {
		return new CustomerItemWriter();
	}

	@Bean
	public Tasklet tasklet() {
		return (contribution, chunkContext) -> {
			return RepeatStatus.FINISHED;
		};
	}

	@SuppressWarnings("unchecked")
	@Bean
	public WatchService watchService(ExecutorService executorService) throws Exception {
		WatchService watcher = FileSystems.getDefault().newWatchService();
		executorService.submit(() -> {
			var dir = Paths.get("c:/tmp");
			try {
				dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
			for (;;) {

				WatchKey key;
				try {
					key = watcher.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						if (kind == StandardWatchEventKinds.OVERFLOW) {
							continue;
						}

						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = ev.context();
						if (!filename.toString().endsWith(".csv"))
							continue;
						var jobParametersBuilder = new JobParametersBuilder();
						jobParametersBuilder.addString("filename", filename.toString());
						jobParametersBuilder.addString("id", UUID.randomUUID().toString());
						jobLauncher.run(crmEmailSmsNotificationJob, jobParametersBuilder.toJobParameters());
					}
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
		});
		return watcher;
	}
}
