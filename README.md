# SPRING PLUS


# 🔍 사용자 닉네임 검색 속도 개선 프로젝트

## ✅ 목적
- 대용량 사용자 데이터(약 100만 건)를 대상으로 닉네임 기반 검색 속도 개선
- 다양한 성능 개선 기법을 적용하며 개선 전후의 속도 측정 및 비교

## 🧪 테스트 환경
- **DB**: MySQL  
- **Spring Boot**: `@GetMapping("/api/users/nickname/{nickname}")` 엔드포인트로 조회  
- **Postman**: 요청 소요 시간 측정  
- **데이터**: 1,000,000건의 더미 유저 데이터

## 📊 조회 속도 비교

| 단계 | 방법 | 소요 시간 | 비고 |
|------|------|-----------|------|
| 1️⃣ 기본 | 단순 JPA 쿼리 | `469ms` | 아무런 최적화 없이 전체 테이블 검색 |
| 2️⃣ 인덱싱 | `nickname`에 인덱스 추가 | `14ms` | `CREATE INDEX idx_nickname ON user(nickname);` |
| 3️⃣ 캐싱 | Caffeine Cache 적용 | `6ms` | 동일 닉네임 재조회 시 캐시에서 처리 |

## 🔽 Postman 결과 스크린샷

### 1. 기본 (469ms)
![Image](https://github.com/user-attachments/assets/92d7e0de-0110-47d7-b037-c488ff5c62c3)

### 2. 인덱스 적용 후 (14ms)
<img width="863" alt="Image" src="https://github.com/user-attachments/assets/ac478e97-239e-477d-939c-87ceb08f691d" />

### 3. 캐시 적용 후 (6ms)
<img width="835" alt="Image" src="https://github.com/user-attachments/assets/c094c188-4295-4a4c-92d6-5fa581c3b29e" />

## 🛠️ 개선 방법 상세

### 1️⃣ 기본 방식
```java
public User findByNickname(String nickname) {
    return userRepository.findByNickname(nickname)
        .orElseThrow(() -> new UserNotFoundException());
}
```

### 2️⃣ 인덱싱 적용
```
-- MySQL 인덱스 추가
CREATE INDEX idx_nickname ON user(nickname);
```

###3️⃣ Caffeine 캐시 적용
**설정**
```
// build.gradle
implementation 'com.github.ben-manes.caffeine:caffeine'
```
**캐시 구성 CacheConfig**
```
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("userByNickname");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10000));
        return cacheManager;
    }
}
```

**서비스 적용**
```
@Cacheable(value = "userByNickname", key = "#nickname")
public UserResponseDto getUserByNickname(String nickname) {
    User user = userRepository.findByNickname(nickname)
        .orElseThrow(() -> new UserNotFoundException());
    return new UserResponseDto(user);
}
```

### ✅ 결론

- 단순 조회만으로는 대용량 환경에서 성능 한계가 존재
- 인덱스 적용만으로도 극적인 개선 가능
- 캐싱까지 병행하면 정적 데이터 요청 시 성능 극대화 가능
