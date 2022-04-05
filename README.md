# team13 - TODO List

> 2022.04.04 ~ 04.15 (2주)
- 👉 [Notion](https://cookie-giant-a00.notion.site/TODO-13-486eb309a0ee408182a9668159954d8f) 에서 더 자세한 진행 사항을 확인할 수 있습니다.

### 🧑🏻‍💻👩🏼‍💻 멤버(4)

|  | Nick | github ID | MBTI |
| --- | --- | --- | --- |
| BE | Lucid | [leejohy-0223](https://github.com/leejohy-0223) | INFJ |
|  | Tany | [juni8453](https://github.com/juni8453) | ISTP |
| iOS | Beck | [SangHwi-Back](https://github.com/SangHwi-Back) | ENTJ |
|  | Rosa | [Jinsujin](https://github.com/Jinsujin) | INFP |

<br/>

## 폴더 구조
> Backend, iOS 같이 들어있는 저장소
> 

```
/ Root
  |
  |── Server
  |
  |── Client
        |── iOS
  
```

- **RootDir** ⇒ /
    - **BackEnd** ⇒ /Server
    - **FrontEnd ⇒** /Client
        - **iOS** ⇒ /Client/iOS/todo-list
        - **Android** ⇒ /Client/Android/todo-list


## Rules

### git flow

> 👉 참고 사이트: [우형 기술 블로그](https://techblog.woowahan.com/2553/)
> 
1. **main**(기준) 역할은 `team-13` 브랜치이다.
2. **main** 에서 개발할 브랜치인 `develop` 을 생성한다. 
    - PR 용으로 사용하는 iOS, BE develop 브랜치가 있다
    - 개발자들은 이 브랜치를 베이스로 개발한다
    - 예) BE 는 `develop-BE` 를 베이스로 기능개발 브랜치를 딴다.
3. 개발할 기능이 생기면 **issue** 를 작성한다
    1. **issue convention** 참고
4. 생성된 issue 고유번호로 feature 를 생성한다
    1. 예) issue 고유번호가 #1 인 경우 `feature-1`
5. hotfix
    1. 브랜치에 공통된 변경사항이 발생했을때 사용하는 브랜치 (예. 치명적 에러) 

```
team-13(main)
  |
  |── develop
         |── develop-iOS // **iOS용 PR**
	     |── feature-1
             |── feature-2
         |── develop-BE // **BE용 PR**
             |── feature-3
  |── hotfix
```

### issue

issue 를 쓸때 작업 흐름

1. issue 생성
    1. 작업에 대한 상세 내용
    2. 작업에 대한 작은 기능들을 체크박스로 readme 작성
2. 생성된 issue 번호로 feature 브랜치 따기 

```
제목: [FEAT] 기능제목
---

## 💡 issue
[FEAT] CollectionView 구현

## 📝 todo
[ ] 작업1
[ ] 작업2
[ ] 작업3
```

### commit

작업에 대한 간략한 키워드를 제목 가장 앞에 추가한다. 작업의 자세한 내용은 제목에서 한줄 띄우고 쓰도록 한다.

- `feat`: 새로운 기능을 추가할 경우
- `fix`: 버그를 고친 경우
- `docs`: 문서 수정한 경우
- `style`: 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우
- `refactor`: 프로덕션 코드 리팩터링
- `test`: 테스트 추가, 테스트 리팩터링 (프로덕션 코드 변경 없음)
- `chore`: 빌드 테스크 업데이트, 패키지 매니저 설정할 경우 (프로덕션 코드 변경 없음)

```
키워드: 작업 제목 (#이슈번호)
//공백//
자세한 내용1 내용내용내용내용
내용내용222222

예) 
feat: 컬렉션뷰 ViewController 에 적용 (#1)

Todo list 를 보여줄 컬렉션뷰를 ViewController 에 적용했다
```
