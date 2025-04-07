# SSG I&C 최종 프로젝트 ShowPing팀

## 목차
  * [프로젝트 개요](#📝-프로젝트-개요)
  * [멤버 및 역할 소개](#👥-멤버-및-역할-소개)
  * [기술 스텍](#💻-기술-스텍)
  * [프로젝트 설계](#📄-프로젝트-설계)
      - 요구사항 정의서
      - ERD
      - 프로젝트 구조
      - UseCase
- [구현기능](#구현기능)
  * [[회원 관련 기능]](#회원-관련-기능)
    + [회원가입](#회원가입)
    + [로그인](#로그인)
    + [비밀번호 찾기](#비밀번호-찾기)
    + [회원 정보 확인 / 수정](#회원-정보-확인--수정)
  * [[발주 관련 기능]](#발주-관련-기능)
    + [발주 등록](#발주-등록)
    + [발주 취소](#발주-취소)
    + [발주 내역 조회](#발주-내역-조회)
  * [[출고 관련 기능]](#출고-관련-기능)
    + [출고 등록](#출고-등록)
    + [출고 내역 조회](#출고-내역-조회)
    + [출고 완료 처리](#출고-완료-처리)
  * [[재고 관련 기능]](#재고-관련-기능)
    + [재고 조회](#재고-조회)
    + [재고 필터링 조회](#재고-필터링-조회)
  * [[대시보드 기능]](#대시보드-기능)
    + [카테고리별 판매량 차트](#카테고리별-판매량-차트)
    + [카테고리별 입고 대비 판매량 차트](#카테고리별-입고-대비-판매량-차트)
    + [일자별 판매량 차트](#일자별-판매량-차트)
    + [각 출고 상태 비율 차트](#각-출고-상태-비율-차트)
    + [년원일 총 판매 금액 차트](#년월일-총-판매-금액-카드)
  * [[시스템 관련 기능]](#시스템-관련-기능)
    + [지점 정보 조회 기능](#지점-정보-조회-기능)
    + [발주 내역서 OCR](#발주-내역서-ocr)
    + [지점 회원 관리](#지점-회원-관리)
- [시연연상](#시연-영상)
  * [[회원가입]](#회원가입-1)
  * [[지점직원 로그인 후 재고 조회]](#지점직원-로그인-후-재고-조회)
  * [[지점직원 정보수정]](#지점직원-정보수정)
  * [[대시보드]](#대시보드)
  * [[출고 관리]](#출고-관리)
  * [[지점 정보 조회]](#지점-정보-조회)
  * [[지점 회원 관리 / 발주 내역서 OCR]](#지점-회원-관리--발주-내역서-ocr)
  * [[발주 등록 / 취소 / 내역 조회]](#발주-등록--취소--내역-조회)
</br><br>

## 📝 프로젝트 개요
<img src="https://github.com/user-attachments/assets/558ede48-54c9-4db3-a317-2288082c2967" width=640 height=360>
<img src="https://github.com/user-attachments/assets/f715bbf4-4348-4afe-964e-6d310898e7d8" width=640 height=360>

</br></br>

## 👥 멤버 및 역할 소개

| 이름                                           | 역할                                                                                                                  |
| --------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| [김대철](https://github.com/gunny97h) | 회원가입, 로그인/로그아웃, 비밀번호 찾기, 회원 정보 확인/수정, 지점 회원 관리 |
| [김창훈](https://github.com/C-H-Kim) | 발표, 대시보드, 형상 관리 |
| [김주일](https://github.com/Queue-ri) | 팀장, 출고, 재고, OCR, 형상 관리, 버그 패치/파일 통합, UI 디자인 |
| [박헌우](https://github.com/phwoo1315) | 발주, 지점 정보 조회, UI 디자인 |
| [조민호](https://github.com/Jenius-95) | 발주, 지점 정보 조회, UI 디자인 |

</br>
</br>

## 💻 기술 스텍

- 언어

  <img src="https://img.shields.io/badge/Java17-007396?style=for-the-badge&logo=java&logoColor=white">

- 프레임워크

  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=spring boot&logoColor=white">

- 템플릿 엔진

  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">

- ORM

  <img src="https://img.shields.io/badge/MyBatis-000000?style=for-the-badge&logo=MyBatis&logoColor=white">

- DB

  <img src="https://img.shields.io/badge/My sql-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

- Open API

  <img src="https://img.shields.io/badge/Clovar-60C482?style=for-the-badge&logo=clovar&logoColor=white">
  <img src="https://img.shields.io/badge/Kakaomap-FFCD00?style=for-the-badge&logo=kakao&logoColor=white">

</br><br>

## 📄 프로젝트 설계
- [요구사항 정의서](https://docs.google.com/spreadsheets/d/1_6-NCzGmZEZd5BXc_wEngiWOverpghhMh5qpPRPSXLM/edit?usp=sharing)

- ERD
![NoJam](https://github.com/user-attachments/assets/4e82f20f-bd92-4c0b-a163-02af3d3ebc18)

- 프로젝트 구조
![프로젝트 구조_4팀(NoJam)_쇼핑몰 관리자 관점 어드민 및 쇼핑몰 개발](https://github.com/user-attachments/assets/975e28c7-8701-41c6-9c2a-0951fe1c8f57)

- UseCase
![UseCase_4팀(NoJam)_쇼핑몰 관리자 관점 어드민 및 쇼핑몰 개발](https://github.com/user-attachments/assets/103ae59e-a262-41ce-9c2f-21ced5c57c0b)

</br></br><br>

# 구현기능

## [회원 관련 기능]

### 회원가입
- 회원가입은 아이디 중복 체크 / 비밀번호 확인 일치 여부 / 이메일 인증을 해야만 가입이 가능합니다.
<br><br>

### 로그인
- 로그인 시 계정의 권한에 따라 사이드바의 메뉴가 달라집니다.
<br><br>

### 비밀번호 찾기
- 비밀번호 찾기는 랜덤 비밀번호가 이메일로 발송되며, 해당 비밀번호로 로그인 가능합니다.
<br><br>

### 회원 정보 확인 / 수정
- 로그인한 회원 정보를 확인할 수 있으며, 사용자명과 비밀번호를 변경할 수 있습니다.
<br><br>


## [발주 관련 기능]
발주 등록과 취소는 지점장 권한으로 로그인했을 때만 이용할 수 있습니다.<br>
발주 내역 조회는 지점장과 지점직원 권한 모두 이용 가능합니다.

### 발주 등록
- 좌측의 상품 목록에서 상품을 선택하고 수량을 입력하여 발주 목록에 추가할 수 있습니다.
- 발주 목록에 있는 상품 중 원하는 상품을 선택하여 삭제하거나 발주 신청할 수 있습니다.
<br><br>

### 발주 취소
- 발주 내역 중 원하는 내역을 선택하여 발주를 취소할 수 있습니다.
<br><br>

### 발주 내역 조회
- 발주 내역 전체를 조회할 수 있으며 필터링을 설정해 조회할 수 있습니다.
- 필터링 옵션을 선택해 검색어를 입력하여 조회할 수 있습니다.
- 옵션에는 발주 번호, 상품 번호, 상품명, 대분류, 소분류, 발주 상태가 있습니다.
<br><br>


## [출고 관련 기능]
출고 관련 기능은 본사 권한으로 로그인했을 때만 이용할 수 있습니다.

### 출고 등록
- 지점에서 신청한 발주 내역들이 보이며 출고 승인 혹은 출고 거절을 선택할 수 있습니다.
- 본사 재고가 발주 수량보다 적을 시 강조되어 표시되며 자동으로 출고 승인 버튼이 비활성화 됩니다.
<br><br>

### 출고 내역 조회
- 출고 승인된 발주 내역은 자동으로 출고 내역으로 생성됩니다.
- 출고 내역으로 생성될 때는 기본적으로 출고 중인 상태로 생성됩니다.
<br><br>

### 출고 완료 처리
- 출고 상태가 출고 중인 내역만 출력됩니다.
- 출고 완료 처리를 하면 출고 상태가 출고 중에서 출고 완료로 변경됩니다.
<br><br>


## [재고 관련 기능]

### 재고 조회
- 본사 계정으로 로그인 했을 때는 본사 재고를 조회할 수 있습니다.
- 지점 계정으로 로그인 했을 때는 해당 계정이 속한 지점의 재고를 조회할 수 있습니다.
<br><br>

### 재고 필터링 조회
- 필터링 옵션을 선택해 검색어를 입력하여 조회할 수 있습니다.
- 옵션에는 상품 번호, 상품명, 대분류, 소분류가 있습니다.
<br><br>


## [대시보드 기능]
![image](https://github.com/user-attachments/assets/09bdf799-3154-4c80-94ac-ea396f84a89b)
본사 계정으로 로그인 했을 때는 지점의 합산된 수치를 확인할 수도 있으며 지점을 선택해 해당 지점의 수치를 확인할 수 있습니다.<br>
지점 계정으로 로그인 했을 때는 해당 지점의 수치만 확인할 수 있습니다.

### 카테고리별 판매량 차트
- 카테고리에 대한 판매량을 확인할 수 있습니다.
<br><br>

### 카테고리별 입고 대비 판매량 차트
- 카테고리에 대한 입고량과 판매량을 확인할 수 있습니다.
<br><br>

### 일자별 판매량 차트
- 날짜를 선택하여 해당 날짜까지 일주일 간의 판매량을 확인할 수 있습니다.
- 날짜를 선택하지 않을 시 금일 날짜로 적용됩니다.
<br><br>

### 각 출고 상태 비율 차트
- 본사 계정으로 로그인 했을 때만 해당 차트를 확인할 수 있습니다.
- 전체 출고 내역에서의 출고 중과 출고 완료 비율을 확인할 수 있습니다.
<br><br>

### 년월일 총 판매 금액 카드
- 이번 년도, 이번 달, 금일 총 판매 금액을 확인할 수 있습니다.
<br><br>


## [시스템 관련 기능]
시스템 관련 기능은 본사 계정으로 로그인 했을 때만 이용할 수 있습니다.

### 지점 정보 조회 기능
![Image](https://github.com/user-attachments/assets/2cd17629-8bca-43ed-b963-5ea19e9c3179)
- 전체 지점이 지도와 테이블에 표시됩니다.
- 지도에 표시된 포인트를 선택 시 해당 지점의 정보를 확인할 수 있습니다.
- 테이블에 표시된 지점을 선택해도 해당 지점의 정보를 확인할 수 있습니다.
- 주소, 지점명으로 검색할 수 있습니다. 검색 시 지도에는 검색에 부합되는 지점만 표기됩니다.
<br><br>

### 발주 내역서 OCR
![Image](https://github.com/user-attachments/assets/e7cb2270-3d14-43e1-a9e4-e24ff9fcc9c7)
- 발주 내역서 이미지를 업로드하여 해당 발주 내역서에 있는 텍스트를 추출하고 발주 내역으로 등록할 수 있습니다.
- 해당 기능을 통해 발주 내역 등록 과정을 간소화하고 업무 프로세스를 증진시킬 수 있습니다.
<br><br>

### 지점 회원 관리
![Image](https://github.com/user-attachments/assets/cf91117f-2197-4966-bc80-c9e282dc0ded)
- 회원 목록을 확인하여 회원의 직급을 지점장 혹은 지점 직원으로 조정 가능합니다.
<br><br>

# 시연 영상

## [회원가입]
https://github.com/user-attachments/assets/18bb905d-b0c2-469f-9ecf-0407495e934e

## [지점직원 로그인 후 재고 조회]
https://github.com/user-attachments/assets/cc56762e-8fff-4a2f-84de-13a766ccf7e7

## [지점직원 정보수정]
https://github.com/user-attachments/assets/13cbe755-e55f-4863-9e44-eda0eaed4bb4

## [대시보드]
https://github.com/user-attachments/assets/6b1a3498-1e7c-4642-b61b-6e332daa08be

## [출고 관리]
https://github.com/user-attachments/assets/69240bb9-69b0-4327-9c49-557862a4bc18

## [지점 정보 조회]
https://github.com/user-attachments/assets/49dd600e-ccde-44cf-a24f-0574f107e411

## [지점 회원 관리 / 발주 내역서 OCR]
https://github.com/user-attachments/assets/136d5620-293c-4d65-bd0e-97cb1d84d788

## [발주 등록 / 취소 / 내역 조회]
https://github.com/user-attachments/assets/c843af95-a6ba-4120-8065-9c4d7b63b45c
