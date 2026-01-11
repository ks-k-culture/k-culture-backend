# K-Culture Backend API 구현 계획서

> 마지막 업데이트: 2026-01-10
> OpenAPI 스펙: `docs/openapi.yaml`

---

## 📊 현재 구현 현황

### 진행률: 54% (26/48 API)

```
[██████████░░░░░░░░░░] 54%
```

---

## ✅ 구현 완료

### Auth (인증) - 7/7 ✅

- [x] `POST /api/auth/login` - 로그인
- [x] `POST /api/auth/signup` - 회원가입
- [x] `POST /api/auth/logout` - 로그아웃
- [x] `POST /api/auth/refresh` - 토큰 갱신
- [x] `POST /api/auth/forgot-password` - 비밀번호 찾기
- [x] `POST /api/auth/reset-password` - 비밀번호 재설정
- [x] `DELETE /api/auth/account` - 계정 삭제

### Filmography (필모그래피) - 5/5 ✅

- [x] `GET /api/actors/{actorId}/filmography` - 목록 조회
- [x] `GET /api/filmography/{filmographyId}` - 상세 조회
- [x] `POST /api/filmography` - 생성
- [x] `PUT /api/filmography/{filmographyId}` - 수정
- [x] `DELETE /api/filmography/{filmographyId}` - 삭제

### Showreels (쇼릴) - 4/4 ✅

- [x] `GET /api/actors/{actorId}/showreels` - 목록 조회
- [x] `POST /api/showreels` - 생성
- [x] `PUT /api/showreels/{showreelId}` - 수정
- [x] `DELETE /api/showreels/{showreelId}` - 삭제

### Projects (프로젝트) - 5/5 ✅

- [x] `GET /api/projects` - 목록 조회
- [x] `GET /api/projects/{projectId}` - 상세 조회
- [x] `POST /api/projects` - 생성
- [x] `PUT /api/projects/{projectId}` - 수정
- [x] `DELETE /api/projects/{projectId}` - 삭제

### Users (사용자) - 4/4 ✅

- [x] `GET /api/users/me` - 내 정보 조회
- [x] `PUT /api/users/profile` - 프로필 수정
- [x] `GET /api/users/settings/notifications` - 알림 설정 조회
- [x] `PUT /api/users/settings/notifications` - 알림 설정 수정

### Actors (배우) - 4/7 부분완료

- [x] `GET /api/actors` - 목록 조회
- [x] `GET /api/actors/{actorId}` - 상세 조회
- [x] `GET /api/actors/me` - 내 프로필 조회 (추가 구현)
- [x] `PUT /api/actors/me` - 내 프로필 수정 (추가 구현)

### Agencies (에이전시) - 2/3 부분완료

- [x] `GET /api/agencies/profile` - 프로필 조회
- [x] `PUT /api/agencies/profile` - 프로필 수정

---

## 📋 미구현 API 작업 목록

### Phase 1: 기존 도메인 보완 (난이도: 낮음)

#### 1.1 Characters (캐릭터) 컨트롤러 추가

> 엔티티/서비스 존재, 컨트롤러만 추가 필요

- [ ] `GET /api/projects/{projectId}/characters` - 프로젝트별 캐릭터 목록
- [ ] `POST /api/projects/{projectId}/characters` - 캐릭터 생성
- [ ] `PUT /api/characters/{characterId}` - 캐릭터 수정
- [ ] `DELETE /api/characters/{characterId}` - 캐릭터 삭제

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/project/controller/
└── CharacterController.java (신규)
```

#### 1.2 Upload (파일 업로드) 컨트롤러 추가

> FileStorageService 존재, 컨트롤러만 추가 필요

- [ ] `POST /api/upload/image` - 이미지 업로드
- [ ] `POST /api/upload/video` - 영상 업로드

**작업 내용:**

```
src/main/java/restapi/kculturebackend/infrastructure/storage/
├── UploadController.java (신규)
└── dto/
    ├── UploadImageResponse.java (신규)
    └── UploadVideoResponse.java (신규)
```

#### 1.3 Actors 추가 API

- [ ] `POST /api/actors/recommend` - AI 배우 추천
- [ ] `POST /api/actors/profile` - 배우 프로필 등록
- [ ] `GET /api/actors/{actorId}/portfolio` - 포트폴리오 다운로드
- [ ] `POST /api/actors/{actorId}/contact` - 배우 연락하기

#### 1.4 Agencies 추가 API

- [ ] `POST /api/agencies/profile` - 에이전시 프로필 등록

---

### Phase 2: 새 도메인 생성 (난이도: 중간)

#### 2.1 Favorites (찜 목록) 도메인

- [ ] `GET /api/favorites` - 찜 목록 조회
- [ ] `POST /api/favorites` - 찜 추가
- [ ] `DELETE /api/favorites/{favoriteId}` - 찜 삭제

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/favorite/
├── controller/
│   └── FavoriteController.java
├── dto/
│   ├── FavoriteResponse.java
│   └── CreateFavoriteRequest.java
├── entity/
│   ├── Favorite.java
│   └── FavoriteType.java (enum: actor, project)
├── repository/
│   └── FavoriteRepository.java
└── service/
    └── FavoriteService.java
```

#### 2.2 Notifications (알림) 도메인

- [ ] `GET /api/notifications` - 알림 목록 조회
- [ ] `PUT /api/notifications/{notificationId}/read` - 알림 읽음 처리
- [ ] `PUT /api/notifications/read-all` - 모든 알림 읽음 처리

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/notification/
├── controller/
│   └── NotificationController.java
├── dto/
│   └── NotificationResponse.java
├── entity/
│   ├── Notification.java
│   └── NotificationType.java (enum)
├── repository/
│   └── NotificationRepository.java
└── service/
    └── NotificationService.java
```

---

### Phase 3: 새 도메인 생성 (난이도: 높음)

#### 3.1 Jobs (작품구인) 도메인

- [ ] `GET /api/jobs` - 작품구인 목록 조회
- [ ] `GET /api/jobs/{jobId}` - 작품구인 상세 조회
- [ ] `POST /api/jobs` - 작품구인 등록
- [ ] `PUT /api/jobs/{jobId}` - 작품구인 수정
- [ ] `DELETE /api/jobs/{jobId}` - 작품구인 삭제

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/job/
├── controller/
│   └── JobController.java
├── dto/
│   ├── JobSummaryResponse.java
│   ├── JobDetailResponse.java
│   ├── CreateJobRequest.java
│   └── UpdateJobRequest.java
├── entity/
│   ├── Job.java
│   ├── JobStatus.java (enum: 모집중, 마감됨)
│   └── JobCategory.java (enum: 단편영화, 장편영화, 웹드라마, 광고, 뮤직비디오, 기타)
├── repository/
│   └── JobRepository.java
└── service/
    └── JobService.java
```

#### 3.2 Notices (공지사항) 도메인

- [ ] `GET /api/notices` - 공지사항 목록 조회
- [ ] `GET /api/notices/{noticeId}` - 공지사항 상세 조회

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/notice/
├── controller/
│   └── NoticeController.java
├── dto/
│   ├── NoticeSummaryResponse.java
│   └── NoticeDetailResponse.java
├── entity/
│   ├── Notice.java
│   └── NoticeType.java (enum: 일반공지, 업데이트, 이벤트, 점검)
├── repository/
│   └── NoticeRepository.java
└── service/
    └── NoticeService.java
```

#### 3.3 Dashboard (대시보드) 도메인

- [ ] `GET /api/dashboard/stats` - 대시보드 통계 조회

**작업 내용:**

```
src/main/java/restapi/kculturebackend/domain/dashboard/
├── controller/
│   └── DashboardController.java
├── dto/
│   ├── ActorDashboardStats.java
│   └── AgencyDashboardStats.java
└── service/
    └── DashboardService.java
```

---

## 🏗 프로젝트 아키텍처

```
src/main/java/restapi/kculturebackend/
├── common/                    # 공통 모듈
│   ├── dto/                   # ApiResponse, ErrorResponse, PaginationResponse
│   ├── entity/                # BaseEntity (createdAt, updatedAt)
│   └── exception/             # 예외 처리 (GlobalExceptionHandler)
│
├── config/                    # 설정
│   ├── JpaConfig.java
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
│
├── domain/                    # 도메인 모듈 (DDD 스타일)
│   ├── actor/                 ✅ 구현됨
│   ├── agency/                🔄 부분 구현
│   ├── auth/                  ✅ 구현됨
│   ├── project/               🔄 Characters 컨트롤러 필요
│   ├── user/                  ✅ 구현됨
│   ├── favorite/              ❌ 미구현
│   ├── job/                   ❌ 미구현
│   ├── notice/                ❌ 미구현
│   ├── notification/          ❌ 미구현
│   └── dashboard/             ❌ 미구현
│
├── infrastructure/            # 인프라 계층
│   └── storage/               ✅ FileStorageService 존재 (컨트롤러 필요)
│
└── security/                  # 보안
    ├── jwt/                   # JWT 토큰 처리
    └── UserDetailsServiceImpl.java
```

---

## 📝 도메인별 엔티티 스키마

### User (기존)

```java
- id: UUID
- email: String (unique)
- password: String
- name: String
- type: UserType (ACTOR, AGENCY)
- profileImage: String
- isActive: Boolean
```

### Favorite (신규)

```java
- id: UUID
- user: User (ManyToOne)
- targetId: UUID
- type: FavoriteType (ACTOR, PROJECT)
- createdAt: LocalDateTime
```

### Job (신규)

```java
- id: UUID
- user: User (ManyToOne) - 작성자
- category: JobCategory
- isPumasi: Boolean
- price: Integer (nullable)
- title: String
- description: String
- gender: String
- ageRange: String
- production: String
- workTitle: String
- shootingDate: String
- shootingLocation: String
- status: JobStatus
- views: Integer
- contactEmail: String
- contactPhone: String
- createdAt/updatedAt
```

### Notice (신규)

```java
- id: UUID
- type: NoticeType
- title: String
- content: String (TEXT)
- views: Integer
- createdAt/updatedAt
```

### Notification (신규)

```java
- id: UUID
- user: User (ManyToOne)
- type: NotificationType
- title: String
- message: String
- isRead: Boolean
- relatedId: UUID (nullable)
- createdAt: LocalDateTime
```

---

## 🔧 기술 스택

| 구분      | 기술              | 버전   |
| --------- | ----------------- | ------ |
| Framework | Spring Boot       | 4.0.1  |
| Language  | Java              | 17     |
| Database  | PostgreSQL        | -      |
| Cache     | Redis             | -      |
| ORM       | Hibernate/JPA     | -      |
| Auth      | JWT (jjwt)        | 0.12.3 |
| Docs      | SpringDoc OpenAPI | 2.8.9  |
| Test      | TestContainers    | 1.20.4 |
| Build     | Gradle            | -      |

---

## 🚀 작업 시작 명령어

```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 애플리케이션 실행
./gradlew bootRun

# Swagger UI 접속
# http://localhost:8080/swagger-ui.html
```

---

## 📌 작업 시 참고사항

1. **API 응답 형식**: `ApiResponse<T>` 래퍼 사용

   ```java
   {
     "success": true,
     "data": { ... }
   }
   ```

2. **페이징 응답**: `PaginationResponse<T>` 사용

   ```java
   {
     "content": [...],
     "page": 1,
     "limit": 10,
     "total": 100,
     "totalPages": 10
   }
   ```

3. **예외 처리**: `GlobalExceptionHandler`에서 통합 처리

   - `NotFoundException` → 404
   - `UnauthorizedException` → 401
   - `ForbiddenException` → 403
   - `ValidationException` → 400
   - `ConflictException` → 409

4. **인증 필요 API**: `@AuthenticationPrincipal User user` 파라미터 사용

5. **Swagger 문서화**: `@Tag`, `@Operation`, `@Parameter` 어노테이션 사용

---

## ✏️ 작업 로그

| 날짜       | 작업 내용                | 담당 |
| ---------- | ------------------------ | ---- |
| 2026-01-10 | 초기 분석 및 계획서 작성 | -    |
| -          | -                        | -    |
