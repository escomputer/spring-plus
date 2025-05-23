package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.dto.TodoSearchCond;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TodoRepositoryCustom {
	Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

	Page<TodoSearchResponse> searchTodos(TodoSearchCond cond, Pageable pageable);
}

