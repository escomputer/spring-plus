package org.example.expert.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
@EnableJpaAuditing
public class PersistenceConfig {

	@PersistenceContext
	private EntityManager em;

	@Bean
	public JPAQueryFactory jpaQueryFactory(){
		return new JPAQueryFactory(em);
	}
}
