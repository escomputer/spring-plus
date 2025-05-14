package org.example.expert.domain.log.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "log")
public class Log {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String action;
	private String details;
	private LocalDateTime createdAt;

	public Log() {

	}

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Log(String action, String details) {
		this.action = action;
		this.details = details;
		this.createdAt = LocalDateTime.now();
	}
}
