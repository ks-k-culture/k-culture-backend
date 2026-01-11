📊 백엔드 API 검증 결과

# 유저 플로우 API 상태 비고

1 회원가입 POST /api/auth/signup ✅ 정상
2 로그인 POST /api/auth/login ✅ 정상 JWT 토큰 발급
3 비밀번호 찾기 POST /api/auth/forgot-password ✅ 정상 이메일 발송 메시지
4 내 정보 조회 GET /api/users/me ✅ 정상 인증 필요
5 배우 목록 GET /api/actors ✅ 정상 데이터 0건
6 공지사항 GET /api/notices ✅ 정상 데이터 0건
7 작품구인 GET /api/jobs ✅ 정상 수정 후 정상
8 배우 프로필 GET /api/actors/me ❌ 오류 서버 내부 오류
9 AI 매칭추천 POST /api/actors/recommend ❌ 오류
