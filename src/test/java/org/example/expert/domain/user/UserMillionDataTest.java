package org.example.expert.domain.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserMillionDataTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

	@Test
	@Transactional
	@Rollback(value = false)
	void 유저_100만명_생성(){
		List<User> users = new ArrayList<>();

		for (int i=1; i<=1000000; i++) {
			String nickname = "user_"+ UUID.randomUUID().toString();
			String email = "user_"+ i +"@example.com";
			String password = "password" + i;


			User user = new User(email,password, UserRole.USER);
			//private인 user에 직접 닉네임 할당
			ReflectionTestUtils.setField(user, "nickname", nickname);


			users.add(user);
			if(i%10000==0){
				userRepository.saveAll(users);
				em.flush();
				em.clear();
				users.clear();
				System.out.println(i+"명 생성 완료");
			}
		}

		// 남은 데이터 저장
		if (!users.isEmpty()) {
			userRepository.saveAll(users);
		}

		System.out.println("전체 유저 생성 완료");
	}
}
