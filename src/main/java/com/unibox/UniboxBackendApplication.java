package com.unibox;

import com.unibox.model.Complaint;
import com.unibox.repository.ComplaintRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UniboxBackendApplication {

	public static void main(String[] args) {

		SpringApplication.run(UniboxBackendApplication.class, args);

	}
	@Bean
	public CommandLineRunner demo(ComplaintRepository complaintRepository) {
		return (args) -> {
			System.out.println("=== Complaints in DB on startup ===");
			for (Complaint c : complaintRepository.findAll()) {
				System.out.println("Complaint ID: " + c.getId() + ", Desc: " + c.getDescription() + ", Status: " + c.getStatus());
			}
			System.out.println("==================================");
		};
	}
}
		