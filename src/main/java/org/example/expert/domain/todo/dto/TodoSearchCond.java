package org.example.expert.domain.todo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Service;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
public class TodoSearchCond {
	private String titleKeyword;
	private LocalDateTime createdAfter; //시작일
	private LocalDateTime createdBefore; // 종료일
	private String nicknameKeyword;
}
