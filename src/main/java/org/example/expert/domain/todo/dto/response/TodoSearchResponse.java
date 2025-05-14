package org.example.expert.domain.todo.dto.response;

public class TodoSearchResponse {
	private String title;
	private Long userCount;
	private Long commentCount;

	public TodoSearchResponse(String title, Long userCount, Long commentCount) {
		this.title = title;
		this.userCount = userCount;
		this.commentCount = commentCount;
	}
}
