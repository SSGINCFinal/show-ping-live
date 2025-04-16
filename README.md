# 🛍 SSG I&C 최종 프로젝트 ShowPing팀

## 목차
- [프로젝트 개요](#📝-프로젝트-개요)
- [프로젝트 기획안](#📄-프로젝트-기획안)
- [멤버 및 역할 소개](#👥-멤버-및-역할-소개)
- [기술 스택](#💻-기술-스택)
- [프로젝트 설계](#📄-프로젝트-설계)
- [기타 문서](#📎-기타-문서)
- [구현 기능](#🧩-구현-기능)
- [시연 영상](#🎥-시연-영상)
- [수상 실적](#🏆-수상-실적)
</br><br>

## 📝 프로젝트 개요
<img src="https://github.com/user-attachments/assets/24e91d8b-0122-4425-af39-5403d555193f" width=640 height=360>
<img src="https://github.com/user-attachments/assets/4e7dc7f9-2af4-470d-b55d-f495588ca5e0" width=640 height=360>
<img src="https://github.com/user-attachments/assets/992672ea-ed6c-4797-85ac-5c5444dad589" width=640 height=360>
<img src="https://github.com/user-attachments/assets/a9390b45-b214-4d79-bbe6-64b64ef7615f" width=640 height=360>


## 📄 프로젝트 기획안
- [프로젝트 기획안](https://docs.google.com/document/d/1stluxmkzcYi-PUtaffVXQa2G5qr3XP-I/edit)



</br></br>

## 👥 멤버 및 역할 소개

| 이름                                           | 역할                                                                                                                  |
| --------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| [김대철](https://github.com/dckat) | 프로젝트 형상관리, 라이브 스트리밍 기능, VOD 기능 |
| [김창훈](https://github.com/C-H-Kim) | 팀장, 서버 인프라 구축, 라이브 스트리밍 기능 |
| [김주일](https://github.com/juil1-kim) | 스케줄러, 채팅 기능, 신고 관리 기능 |
| [박헌우](https://github.com/hunwoo0122) | 문서화 작업, 회원 기능, 보안 기능 |
| [조민호](https://github.com/0O000) | ERD 관리, 상품 기능, 장바구니 기능, 결제 기능 |

</br>
</br>

## 💻 기술 스택

### 🛠 Tech
- Java 17
- Spring Boot
- Docker
- AWS
- Apache Kafka
- Thymeleaf
- Spring Security
- JWT (JSON Web Token)
- Redis
- RTMP
- HLS
- WebRTC
- Kurento
- WebSocket
- NCP (NAVER Cloud Platform)
- Spring Batch

### 🗄 DB
- MySQL
- MongoDB

### 🧰 IDE
- IntelliJ IDEA
- MySQL Workbench

### 🤝 협업 툴
- Slack
- GitHub
- Notion
- Discord
- Trello
- Figma



</br><br>

## 📄 프로젝트 설계
- [요구사항 정의서](https://docs.google.com/spreadsheets/d/1B2Xh4MM-a8fpIrJ0o5u8PtvXWkOSMslSr4-mpu_N6tA/edit?gid=701501336#gid=701501336)

- [프로젝트 정의서](https://docs.google.com/spreadsheets/d/1WuRzZpiT6L6AvS-WqLMLbuonoKae5F2yX-TeyTS0Jm0/edit?gid=2108164687#gid=2108164687)

- [API 정의서](https://docs.google.com/spreadsheets/d/1a2va1yE9eO1GhXdlTX66Sbv8bIg_HpDYU70eke21O_0/edit?gid=1813995370#gid=1813995370)

- 유스케이스 다이어그램
 ![ShowPing_UseCase](https://github.com/user-attachments/assets/695afa9c-ae76-4cdb-9c9a-d2fba92a1354)

- ERD
![ShowPing_ERD](https://github.com/user-attachments/assets/515ba904-fd94-47ce-a671-68eec107dc32)

- 아키텍처 구조
![ShowPing_Architecture](https://github.com/user-attachments/assets/e46d40d6-cfcc-4991-9a7b-cadb4b69e35c)

- CI/CD 아키텍처
![image](https://github.com/user-attachments/assets/46448e6c-d0fd-4fe1-b3a1-3f0fb7fdf176)


</br><br>

## 📎 기타 문서
- [테스트 케이스 보고서](https://docs.google.com/spreadsheets/d/1WPUqP0UAcemfe37NDUGeEIVTZ0Rh04jSg2ZWEtxt6ok/edit?gid=1293478023#gid=1293478023)


## 🧩 구현 기능

### ✅ 회원 관리 기능
- **Spring Security + JWT** 기반 로그인 및 권한 인증 구현
- **Access Token + Refresh Token 구조**로 보안 강화
  - Refresh Token은 **Redis에 저장**되어 토큰 재발급 및 만료 관리
- 회원가입, 비밀번호 변경, 탈퇴 및 정보 수정 기능 제공
- 비밀번호 암호화: `BCryptPasswordEncoder`
- **TOTP(Time-based One Time Password)**를 이용한 **관리자 전용 2차 인증** 구현
  - Google Authenticator 앱과 연동하여 OTP 입력 방식 적용
- 비정상 접근 및 토큰 위조 대응 예외처리 적용
- OAuth를 활용한 소셜로그인 기능 

### 🛒 장바구니 및 결제 기능
- 사용자 장바구니 기능 및 수량 조절, 삭제 가능
- 카카오페이 API 연동으로 외부 결제 처리
- 결제 완료 시 Spring Batch를 통한 자동 정산
- 주문 내역 및 상태 변경 관리 기능 포함

### 📺 LIVE 기능
- WebRTC + Kurento를 활용한 실시간 방송 구현
- RTMP 송출 및 HLS 수신 방식 지원
- 방송 상태 저장 및 조회
- Kafka를 통해 방송 이벤트 처리

### 💬 Chatting 기능
- WebSocket + STOMP 기반 실시간 채팅 구현
- Pub/Sub을 활용한 메시지 분산 처리
- 채팅 신고 기능 및 관리자용 대시보드 제공
- 유저 입장/퇴장 로그 기록

### 🎬 VOD 관리 기능
- 방송 종료 시 VOD 자동 저장 및 관리
- AWS S3 저장소와 연동
- 다시보기 목록 조회 및 삭제 기능
- 썸네일 및 메타데이터 자동 처리


# 🎥 시연 영상

[회원 기능 시연](https://drive.google.com/file/d/1i4lRS6S8sLMfJmFsQ2xRUCx8Wq_VyhVD/view?usp=drive_link)

[상품 등록 및 결제 기능 장바구니 기능](https://drive.google.com/file/d/1Q8yVMdZh1H_YhIaJr6Ql7Ys2zXRn6Vns/view?usp=drive_link)

[라이브 목록](https://drive.google.com/file/d/1Tv_o1_1YjykuWc6C6YU_MEHlXvPOBqIA/view?usp=drive_link)

[라이브 송출, 상품 등록, 관리자 채팅](https://drive.google.com/file/d/1mBoVsOPUcwQEcQdbTlmautA39BhvUtnV/view?usp=drive_link)

[라이브 시청 및 채팅](https://drive.google.com/file/d/1mBVnLwW-fCfcUmgxhFVf6DryBCMiWnFd/view?usp=drive_link)

[신고 관리](https://drive.google.com/file/d/1GLM7hKx6dib64YYZS0uJ-RR97forXEAJ/view?usp=drive_link)

[vod 시연](https://drive.google.com/file/d/1vMz9gbg43u3xXhuP2JNPx7HzVnzwS98z/view?usp=drive_link)

[vod기능_시연](https://drive.google.com/file/d/15_bidqyn_1mZoWO_C3NM5oKlOTw68npt/view?usp=drive_link)

## 🎥 전체 시연 영상
[전체 시연](https://drive.google.com/file/d/1qRfUPvz5sc0aGOEi5frr-GHMHEO6eOMj/view?usp=drive_link)

## 🏆 수상 실적
- **SSG I&C 부트캠프 최우수 프로젝트 선정**
  - WebRTC, Redis, Kafka 등 다양한 기술을 조합해 실시간 커머스 환경을 안정적으로 구현한 점을 높이 평가받음
  - 제한된 4주라는 개발 기간 동안 **성과 중심의 기술 선택**과 팀워크를 통해 완성도 높은 결과물 도출
