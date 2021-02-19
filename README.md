# 카카오페이 뿌리기 API 구현 

## 개발환경
 - Java 11
 - Spring Boot 2.3.8.RELEASE
 - JdbcTemplate
 - H2
 - Maven
 
## 핵심 문제 해결 전략
**성능에 대한 고민** <br>
   => 트래픽 증가로 인한 요청량 증가시 발생하는 DB 쿼리 수가 많아지면 성능 저하와 DB 병목현상의 원인이 될 수 있다. 이를 예방하기 위해 최대한 쿼리를 적게 던질 수 있는 방법을 생각해보았다.

   * 뿌리기 API - 뿌리기 1건당 뿌릴 인원 수 만큼 DB를 생성해야 하는 문제 
        * 뿌리기 1건당 뿌릴 인원 수가 많고 뿌리기 수 또한 증가할 경우, N번의 쿼리를 던지게 되므로 성능에 영향을 줄 수 있다고 판단하였다.
               
        * 해결: 이를 해소하고자 JdbcTemplate의 BatchInsert 기능을 이용. 설정된 batch size 만큼 모아서 한번에 쿼리를 보내도록 한다.
          뿌릴 인원수가 3명인 경우 insert 쿼리가 3번 날아갔는데 batchInsert 이용시 쿼리 1번으로 해결된다.
```
BatchInsert 사용전>
1:  INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,2,29,null,null,'02/20/2021 08:59:04.820')
2:  INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,2,23,null,null,'02/20/2021 08:59:04.820')
3:  INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,2,48,null,null,'02/20/2021 08:59:04.820')

BatchInsert 사용후>
INSERT INTO pickups(seq, scatter_seq, amount, user_seq, picked_at, create_at) VALUES (null,2,29,null,null,'02/20/2021 08:59:04.820'),(null,2,23,null,null,'02/20/2021 08:59:04.820'),(null,2,48,null,null,'02/20/2021 08:59:04.820')
```        
     
   * 받기 API
        - 필요한 정보를 얻기 위해 매 API 요청마다 최소 2번의 쿼리를 던져야 했으나 DB 조인을 이용하여 한번에 가져오도록 하였다.
          
   * 조회 API 
        - DB 조회시 자주 사용하는 필드에 대해서는 인덱스를 걸어 DB에 row가 많이 쌓일 경우를 대비하여 DB 조회 속도를 높이도록 하였다. 
 
## API 

### 뿌리기 API
Request
```
POST /api/scatters
X-USER-ID: ${userId}
X-ROOM-ID: ${roomId}

{
    "money": 100,
    "recipientNum": 3
}
```

Success
```
HTTP 200 OK
{
    "response": {
        "token": "cfB"
    },
    "success": true
}
```

Fail
```
HTTP 500 Internal Server Error
{
    "code": "SERVER_ERROR",
    "message": "시스템 오류가 발생하였습니다.",
    "timestamp": "2020-06-27T06:03:06.556993"
}
```

### 받기 API
Request
```
PUT /api/scatters/receive
X-USER-ID: ${userId}
X-ROOM-ID: ${roomId}

{
    "token": ${token}
}
```

Success
```
HTTP 200 OK
{
    "response": {
        "amount": 21
    },
    "success": true
}
```

Fail (일부만 표기)
```
{
    "errors": {
        "code": "SC003",
        "message": "본인이 만든 뿌린기는 받을 수 없습니다.",
        "status": 400
    },
    "success": false
}
```

### 조회 API
Request
```
GET /api/scatters/${token}
X-USER-ID: ${userId}
```

Success
```
HTTP 200 OK
{
    "response": {
        "createAt": "2021-02-20T08:04:15.204978",
        "totalCash": 100,
        "pickedCash": 48,
        "receiverList": [
            {
                "receivedAmount": 27,
                "receiverId": 106
            },
            {
                "receivedAmount": 21,
                "receiverId": 104
            }
        ]
    },
    "success": true
}
```

Fail
```
HTTP 404 Not Found
{
    "errors": {
        "code": "C003",
        "message": "요청하신 내용을 찾을 수 없습니다.",
        "status": 404
    },
    "success": false
}
```
```
HTTP 500 Internal Server Error
{
    "errors": {
        "code": "C001",
        "message": "알 수 없는 에러가 발생하였습니다.",
        "status": 500
    },
    "success": false
}
```
