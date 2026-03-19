# Redis JWT Authentication System

Spring Boot 3 + Redis + JWT를 활용한 고성능 인증 시스템 예제입니다. Java 21의 가상 스레드(Virtual Threads)를 활용하여 동시성 성능을 최적화하였습니다.

## 🚀 주요 기능

- **JWT 기반 인증**: Access Token 및 Refresh Token을 활용한 보안 인증 프로세스.
- **Redis 세션 관리**: Refresh Token을 Redis에 저장하여 보안성과 확장성을 동시에 확보.
- **가상 스레드(Virtual Threads)**: Java 21의 새로운 기능을 통해 처리량 증대.
- **Thymeleaf 레이아웃**: 레이아웃 다이얼렉트를 활용한 프론트엔드 구조화.
- **RESTful API**: 표준 API 응답 형식을 따르는 백엔드 인터페이스.

## 🛠 기술 스택

- **Backend**: Java 21, Spring Boot 3.5.10
- **Security**: Spring Security, JJWT 0.12.6
- **Database/Cache**: Redis (Lettuce)
- **Frontend**: Thymeleaf, Vanilla JS, CSS
- **Build Tool**: Maven
- **Environment**: Virtual Threads Enabled

## 📂 프로젝트 구조

```
src/main/java/com/example/demo/
├── biz/          # 비즈니스 로직 (Auth, Member, Board 등)
├── common/       # 공통 DTO, 응답 객체
├── conf/         # 설정 파일 (Security, Redis)
├── exception/    # 전역 예외 처리
└── jwt/          # JWT 발급 및 필터 로직
```

## ⚙️ 설정 가이드

### 1. 전제 조건
- JDK 21 이상 설치
- Redis 서버 실행 중

### 2. application.yml 설정
`src/main/resources/application.yml` 파일에서 Redis 호스트와 JWT 시크릿 키를 환경에 맞게 수정하세요.

```yaml
spring:
  data:
    redis:
      host: YOUR_REDIS_HOST
      port: 6379
jwt:
  secret: YOUR_JWT_SECRET_KEY
```

## 📋 주요 API

| Method | Endpoint | Description | 
|--------|----------|-------------|
| POST | `/api/auth/login` | 로그인 및 토큰 발급 |
| POST | `/api/auth/logout` | 로그아웃 (토큰 무효화) |
| POST | `/api/auth/refresh` | Access Token 갱신 |

---
© 2026 redis_jwt. Created for learning and demo purposes.
