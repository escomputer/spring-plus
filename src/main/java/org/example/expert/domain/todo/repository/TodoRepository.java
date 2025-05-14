package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>,TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t where"
		+ " (:weather is null or t.weather = :weather) and"
		+ "(:startDate is null or t.modifiedAt >= :startDate) and"
		+ "(:endDate is null or t.modifiedAt <= :endDate)"
		+ "order by t.modifiedAt desc")
    Page<Todo> findAllByOrderByModifiedAtDesc(@Param("weather") String weather,
        @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);


}
