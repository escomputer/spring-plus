package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.TodoSearchCond;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Todo> findByIdWithUser(Long todoId) {

		QTodo todo = QTodo.todo;
		QUser user = QUser.user;

		Todo result = queryFactory.selectFrom(todo)
			.join(todo.user, user)
			.fetchJoin()
			.where(todo.id.eq(todoId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public Page<TodoSearchResponse> searchTodos(TodoSearchCond cond, Pageable pageable) {

		//qclass
		QTodo todo = QTodo.todo;
		QUser user = QUser.user;
		QComment comment = QComment.comment;
		QManager manager = QManager.manager;

		//projections로 필요한 필드만 반환
		List<TodoSearchResponse> content = queryFactory.select(
				Projections.constructor(TodoSearchResponse.class, todo.title, manager.countDistinct().coalesce(0L),
					comment.countDistinct().coalesce(0L)
				))
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(manager.user, user)
			.leftJoin(todo.comments, comment)
			.where(titleContains(cond.getTitleKeyword(), todo),
				createdBetween(cond.getCreatedAfter(), cond.getCreatedBefore(), todo),
				nicknameContains(cond.getNicknameKeyword(), user))
			.groupBy(todo.id, todo.title)
			.orderBy(todo.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(todo.count())
			.from(todo)
			.leftJoin(todo.managers, manager)
			.leftJoin(manager.user, user)
			.leftJoin(todo.comments, comment)
			.where(
				titleContains(cond.getTitleKeyword(), todo),
				createdBetween(cond.getCreatedAfter(), cond.getCreatedBefore(), todo),
				nicknameContains(cond.getNicknameKeyword(), user)

			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);

	}

	//keyword로만 닉네임 구분하는 booleanexpression
	private BooleanExpression nicknameContains(String nicknameKeyword, QUser user) {
		return nicknameKeyword != null ? user.nickname.containsIgnoreCase(nicknameKeyword) : null;

	}

	//생성일 범위
	private BooleanExpression createdBetween(LocalDateTime createdAfter, LocalDateTime createdBefore, QTodo todo) {

		if (createdAfter == null && createdBefore == null)
			return null;
		if (createdAfter == null)
			return todo.createdAt.loe(createdBefore); //loe <=
		if (createdBefore == null)
			return todo.createdAt.goe(createdAfter); //goe >=
		return todo.createdAt.between(createdAfter, createdBefore);
	}

	private BooleanExpression titleContains(String titleKeyword, QTodo todo) {
		return titleKeyword != null ? todo.title.containsIgnoreCase(titleKeyword) : null;
	}

}
