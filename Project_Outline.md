# GDGoC Beginner Team1

## 01. 팀원 및 역할 분담

| 이름 | 역할 | 주요 담당 업무 |
|:---:|:---:|:---:|
| 김민서 | Lead, Frontend  | UI 개발, 페이지 라우팅, API 연동 |
| 김민지 | Frontend  | 컴포넌트 설계, 스타일링, 상태관리 |
| 김민제 | Backend | Spring 서버, DB 설계, API 개발 |
| 고예진 | Backend | 인증/권한, 상태 전이 로직, 배포 |

## 02. 프로젝트 주제 및 개요

- 프로젝트명 : GDGoC 스터디 게시판

- 프로젝트를 진행하는 이유 (문제 정의)
> - GDGoC 내에는 스터디 제도가 존재합니다.

> - 그러나 일부 소수 인원만 참여하는 현실이며, 새로운 멤버들은 기존 스터디에 합류하기 어렵고 부담을 느끼는 문제가 있다고 느꼈습니다.

> - 현재 Discord 모집 스레드와 공식 Notion에 스터디 정보가 명시되어 있지만, 이를 별도의 웹 서비스로 제공하면 접근성이 높아지고 신규/기존 멤버 모두 스터디 참여가 쉬워질 것이라 판단하여 이 프로젝트를 기획하게 되었습니다.

## 03. 기능 명세

- 스터디 모집글 게시판 형태 (모집/진행중/마감)

- 관리자/회원/비회원 구현
    ```
    - 관리자 : 게시글 및 회원 관리
    - 회원 : 게시글 작성 (스터디 모집/참여 가능)
    - 비회원 : 게시글 열람만 가능
    ```

## 04. 개발 및 협업 일정 계획

| 주차 | 주요 내용 및 목표 |
|:---:|:------------------|
| 1주차 | 프로젝트 기획, 역할 분담, 요구사항 정의, 환경설정 |
| 2주차 | 화면 설계, DB 모델링, 기본 구조 개발 시작 |
| 3주차 | FE/BE 주요 기능 개발, API 설계 및 연동 |
| 4주차 | 기능 구현 마무리, 예외 처리, UI 개선|
| 5주차 | 통합 테스트, 버그 수정, 코드 리뷰 및 리팩토링 |
| 6주차 | 배포, 발표 준비, 최종 문서화 및 마무리 |

## 05. 기술 스택 

#### **Frontend**
<a href = "https://skillicons.dev">
    <img src = "https://skillicons.dev/icons?i=react,nodejs,html,css,js"/>
</a>

#### **Backend**
<a href = "https://skillicons.dev">
    <img src = "https://skillicons.dev/icons?i=java,spring"/>
</a>

#### **Tools**
<a href = "https://skillicons.dev">
    <img src = "https://skillicons.dev/icons?i=vscode,github,git"/>
</a>

## 06. GitHub 협업 계획

### 1️⃣ 개발 규칙

- 모든 작업은 GitHub Issue를 작성하여 할 일을 명확히 공유하고, 팀원 간 검토를 거칩니다.

- 각자 자신의 이름 Branch에서 작업합니다.

- 작업 완료 후 main 브랜치로 Pull Request(PR)를 요청하며, 반드시 2명 이상의 코드 리뷰 및 승인(Approve)을 받아야 합니다.

- 모든 Issue와 PR은 미리 만들어둔 템플릿을 사용하여, 가이드라인에 맞게 작성합니다.

### 2️⃣ 변수명 및 클래스명 규칙

**Frontend**
- React/JavaScript: camelCase 사용
- CSS: BEM (Block__Element--Modifier) 방식 권장  

**Backend**
- DB 테이블/컬럼명: snake_case 사용
- Java 변수/메서드명: camelCase 사용

### 3️⃣ 커밋 컨벤션 및 라벨링 규칙

- Issue와 PR에는 아래 9개의 라벨을 상황에 맞게 사용합니다. 
  - 예시: `feature`, `frontend`, `fix` 등 상황에 맞게 선택

- 커밋 메시지는 feat, fix 등 타입을 사용하여 간결하게 작성합니다.
  - 예시: `feat: 스터디 모집글 작성 기능 추가`, `fix: 게시글 상태 전이 버그 수정`

| 라벨 | 설명 |
|:---------:|:-----------------|
| frontend  | FE 관련 작업     |
| backend   | BE 관련 작업     |
| feature   | 새로운 기능      |
| fix       | 버그 수정        |
| api       | 서버 API 통신    |
| docs      | 문서 수정        |
| refactor  | 코드 리팩토링    |
| test      | 테스트 코드 수정 |
| chore     | 자잘한 수정      |