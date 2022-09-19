# team13 - TODO List

> 2022.04.04 ~ 04.15 (2주)
- 👉 [Notion](https://cookie-giant-a00.notion.site/TODO-13-486eb309a0ee408182a9668159954d8f) 에서 더 자세한 진행 사항을 확인할 수 있습니다.

### 🧑🏻‍💻👩🏼‍💻 멤버(4)

|  | Nick | github ID | MBTI |
| --- | --- | --- | --- |
| BE | Lucid | [leejohy-0223](https://github.com/leejohy-0223) | INFJ |
|  | Tany | [juni8453](https://github.com/juni8453) | ISTP |
| iOS | Beck | [SangHwi-Back](https://github.com/SangHwi-Back) | ENTJ |
|  | Rosa | [Jinsujin](https://github.com/Jinsujin) | INFP |


### ✏️ API 문서
[Todo-List API](https://documenter.getpostman.com/view/16863241/UVyxPYis)

<br/>

## 주요 구현

<details>
<summary>1. Card Moving 로직 설계</summary>
<div markdown="1">

### 1. Card Moving 로직 설계

#### 1) Moving Concept

- Card에 boardIdx를 부여하고, inverval = 1000을 가지도록 하였다.

- **저장 시**
    - 보드에 최초 저장 : `boardIdx = 1000` 부여
    - 기존에 저장된 카드가 있는 경우 : 해당 보드의 `(max boardIdx) + 1000` 부여
    
- **이동 시**
    - 기본 정책 : 이동하고자 하는 보드 이름과, 넣고자 하는 위치 다음에 있는 카드 정보를 사용
    
    ```json
    {     
        "cardId": 이동하고자 하는 카드의 Id,
        "movedBoardName": 이동하고자 하는 보드 이름,
        "nextCardId": 넣고자 하는 위치 다음에 있는 카드의 정보
    }
    ```
    
    - `nextCardId` 인덱스의 -1 위치에 카드를 넣는다.
    - 이동하려는 보드가 빈 상태였을 경우, `nextCardId = -1`을 전달한다. 
      - 기본 값 `1000`을 할당받는다.
    - 이동하려는 위치가 맨 마지막일 경우, 다음 값이 없으므로 `nextCardId = -2`를 전달한다.
      - 해당 보드의 `(max boardIdx) + 1000` 를 할당받는다.
      
    
- **인덱스가 꽉 찬 경우**
  - 넣으려는 위치에 이미 boardIdx가 존재할 경우, 갱신을 통해 공간을 만들어줘야 한다.

<img width="582" alt="image" src="https://user-images.githubusercontent.com/62360009/190964350-33d3a456-a06a-4ae0-bf74-4a285de7e269.png">


batch update를 통해 1000 간격으로 갱신 후, 원래 cardId의 `갱신된 boardIdx - 1`의 위치에 넣는다.

<img width="730" alt="image" src="https://user-images.githubusercontent.com/62360009/190964394-3f09b5c8-8342-4930-8345-4996bc5f45f4.png">


#### 2) Moving 적용 예시

**상태 1 - To-Do, Done이 있으며, 양쪽 다 카드가 있는 상황**

---

<img width="632" alt="image" src="https://user-images.githubusercontent.com/62360009/190964433-6f65db82-c8ad-45ec-b410-33a76e1ddead.png">

- ***case 1 : To-do에 있던 cardId=2 카드를 Done의 boardIdx=2000 이전으로 이동 요청***

<img width="685" alt="image" src="https://user-images.githubusercontent.com/62360009/190964465-8a06d7da-4360-4152-a411-00a0b1466bbb.png">

- ***결과***

<img width="730" alt="image" src="https://user-images.githubusercontent.com/62360009/190964492-e26d9180-7c1b-488c-83ca-46f7d5e9c4b0.png">

- ***case 2 : 같은 보드 내에서 맨 위로 이동 요청***

<img width="607" alt="image" src="https://user-images.githubusercontent.com/62360009/190964544-86d38e53-7c7a-4acc-9c02-e6c3b05e2620.png">

- ***결과***

<img width="611" alt="image" src="https://user-images.githubusercontent.com/62360009/190964568-52c6b66a-4682-4caf-88c8-52e511fb022e.png">

- ***case 3 : 같은 보드 내에서 맨 아래로 이동 요청***

<img width="578" alt="image" src="https://user-images.githubusercontent.com/62360009/190964597-8df87103-dbbb-41ca-92c2-d3c41f67941b.png">

- ***결과***

<img width="623" alt="image" src="https://user-images.githubusercontent.com/62360009/190964619-b8b0d8fa-91ce-4030-bd72-b8875e9f531b.png">

<br/>

**상태 2 - 카드가 하나도 없는 상황**

---

***case & result : 보드에 최초 카드 하나 추가***

<img width="484" alt="image" src="https://user-images.githubusercontent.com/62360009/190964641-21297100-056e-4532-8761-00b292f4204d.png">

<br/>

**상태 3 - x001번째의 카드 앞에 놓는 것을 시도할 경우**

---

***case 1 : 다른 보드끼리 이동***

<img width="526" alt="image" src="https://user-images.githubusercontent.com/62360009/190964661-df168b85-3aa7-405c-aa26-76e9d4353695.png">

***결과***

<img width="650" alt="image" src="https://user-images.githubusercontent.com/62360009/190964682-cc32e946-3b4e-4cf5-926c-492318df191c.png">

***case 2 : 같은 보드끼리 이동***

<img width="560" alt="image" src="https://user-images.githubusercontent.com/62360009/190964698-0fbcce20-3624-4c3c-b70d-e4e6ae38a668.png">

***결과***

<img width="641" alt="image" src="https://user-images.githubusercontent.com/62360009/190964723-87d99981-5fad-406f-aa6e-e0cd16e2b81f.png">

</div>
</details>

<details>
<summary>2. 검증, 예외 처리 구현</summary>
<div markdown="1">

### 2. 검증, 예외 처리 구현

1. `클라이언트 요청` 에 대한 카드 데이터 검증 로직
    
    ```java
    1. CardCreateDto 클래스 데이터 검증
    
    @NotNull(message = "userId 값이 비었습니다.")
      private Integer userId;
    
    @NotBlank(message = "카드 제목을 입력해주세요.")
    @Size(min = 1, max = 30)
    	private String cardTitle;
    
    @NotBlank(message = "카드 내용을 입력해주세요.")
    @Size(min = 1, max = 500)
    	private String cardContent;
    
    @NotBlank(message = "boardName 값이 비었습니다.")
    	private String boardName;
    
    카드 생성 시 userId, cardTitle, cardContent, boardName
    요청 데이터를 검증합니다.
    
    <요구 사항>
    userId - 유저 아이디가 잘 들어왔는지 검증
    cardTitle - 제목이 잘 들어왔는지, 1~30 글자인지 검증
    cardContent - 내용이 잘 들어왔는지, 1~500 글자인지 검증
    boardName - 보드 이름이 잘 들어왔는지 검증
    
    -----
    
    2. CardPatchDto 클래스 데이터 검증
    
    @NotBlank(message = "카드 제목을 입력해주세요.")
    @Size(min = 1, max = 30)
      private final String cardTitle;
    
    @NotBlank(message = "카드 내용을 입력해주세요.")
    @Size(min = 1, max = 500)
      private final String cardContent;
    
    카드 수정 시 cardTitle, cardContent 요청 데이터를 검증합니다.
    
    cardTitle - 제목이 잘 들어왔는지, 1~30 글자인지 검증
    cardContent - 내용이 잘 들어왔는지, 1~500 글자인지 검증
    ```
    

1. 간단한 예외 처리 흐름
    - DB 에 1번, 2번 카드가 있다고 가정
    - 카드 수정 요청 발생 → `~/todolist/{cardId}` cardId 에는 1, 2 외에 다른 번호가 올 수 없음
    - 만약 1, 2 이외 다른 번호가 왔을 때 `NotFoundCardException` 커스텀 예외가 발생하며  아래
        
        `@ExceptionHandler` 어노테이션에 의해 만들어진 JSON 응답 객체를 클라이언트로 응답
        
    
    ```java
    @ExceptionHandler(NotFoundCardException.class)
        public ResponseEntity<RestResponse> NotFoundCardException(NotFoundCardException exception) {
            Map<String, String> map = new HashMap<>();
            map.put("errorFieldName", exception.getErrorFieldName());
            map.put("errorMessage", exception.getExceptionType().getMessage());
    
            RestResponse restResponse = RestResponse.of(map);
    
            return new ResponseEntity<>(restResponse, HttpStatus.BAD_REQUEST);
        }
    ```
    

1. 예외 발생 후 `JSON 응답 예시`
    
    ```json
    1. 카드를 찾을 수 없을 때 응답
    {
    	"errorMessage" : "해당 Card 를 찾을 수 없습니다. Card 번호를 확인해주세요."
    } 
    
    -----
    
    2. 해당 사용자가 등록한 카드가 없을 때 응답
    {
    	"errorMessage" : "해당 사용자가 등록한 카드가 없습니다."
    }
    
    -----
    
    3. Card 데이터 검증
    {
        "errorMessage": {
            "cardTitle": "크기가 1에서 30 사이여야 합니다"
            "cardContent": "카드 내용을 입력해주세요.",
        }
    }
    
    {
        "errorMessage": {
            "cardTitle": "크기가 1에서 30 사이여야 합니다"
            "cardContent": "크기가 1에서 500 사이여야 합니다"
        }
    }
    
    {
        "errorMessage": {
            "cardTitle": "카드 제목을 입력해주세요."
            "cardContent": "카드 내용을 입력해주세요."
        }
    }
    ```

</div>
</details>
