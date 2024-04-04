## Concert API
| Method | URI                                          | Description           |
|-----|----------------------------------------------|-----------------------|
| POST | [/concert/waiting/register](#-1.-대기열-등록)     | 대기열 등록                |              
| GET | [/concert/waiting/status/{userId}](#-2.-대기열-상태-조회) | 대기열 상태 조회             |
| GET | [/concert/reservation/dates](#-3.-예약-가능한-콘서트-조회) | 예약 가능한 콘서트와 날짜 리스트 조회 |
| GET | [/concert/{concertId}/reservation/seats](#-4.-콘서트-좌석-조회) | 예약 가능한 좌석 정보 리스트 조회   |
| POST | [/concert/reservation](#-5.-콘서트-좌석-예약)       | 콘서트 예약                |
|POST| [/concert/payment](#-6.-결재)                  | 좌석 결제                 |
|PATCH| [/point](#-7.-포인트-충전)                        | 포인트 충전                |
|GET| [/point/{userId}](#-8.-포인트-조회)               | 포인트 조회                |
--------------------------------------------------------------------------
### 1. 대기열 등록 
콘서트 예약을 위한 대기자 명단에 사용자를 등록합니다.

| Method | URI | Description         |
|-----|-----|---------------------|
| POST |/concert/waiting/register| 대기열 등록|    
<b>Request</b>
```https request
  curl -X POST https://{SERVER_URL}/concert/waiting/register \
  -H "Content-Type: application/json" \
  -d '{ \
        "userId": {userId} \ 
      }'
```
<b>Response</b>
```json
{
  "wait_number": 5
}
```
```json
{
  "code": "ALREADY_WAITING_USER",
  "message": "이미 대기중인 사용자"
}
```
--------------------------------------------------------------------------
### 2. 대기열 상태 조회
대기자 명단에 있는 사용자의 상태(대기, 진행, 완료)를 조회합니다.

| Method | URI | Description        |
|-----|-----|--------------------|
| GET |/concert/waiting/status/{userId}| 대기열 상태 조회|
<b>Request</b>
```https request
  curl -X GET https://{SERVER_URL}/concert/waiting/status/{userId} 
```
<b>Response</b>

status - WAIT(대기), ONGOING(진행), DONE(만료)
```json
{
  "code": "WAIT",
  "message": "고객님의 순번까지 N명 남았습니다."
}
```
```json
{
  "code": "ALREADY_GOING_USER",
  "message": "현재 고객님 순번입니다. 만료시간 : yyyy-mm-dd hh:mm:ss"
}
```
--------------------------------------------------------------------------
### 3. 예약 가능한 콘서트 조회
예약 가능한 콘서트를 조회합니다.

| Method | URI | Description         |
|-----|-----|---------------------|
| GET |/concert/reservation/dates| 예약 가능한 콘서트와 날짜 리스트 조회 |
<b>Request</b>
```https request
  curl -X GET https://{SERVER_URL}/concert/reservation/dates
```
<b>Response</b>
```json
{
  "code": "INVALID_WAIT_INFORMATION",
  "message": "대기열 만료"
}
```
```json
[
  {
    "concert_id": 1,
    "concert_name": "다나카 내한 공연［SAYONARA... TANAKA",
    "concert_singer": "다나카",
    "concert_venue": "서강대 메리홀 대극장",
    "concert_start_date": "2024-04-26 20:00:00"
    
  },
  {
    "concert_id": 2,
    "concert_name": "2024 성시경의 축가 콘서트",
    "concert_singer": "성시경",
    "concert_venue": "연세대학교 노천극장",
    "concert_start_date": "2024-09-10 19:30:00"
  },
  {
    "concert_id": 3,
    "concert_name": "황영웅 대전콘서트 〈봄날의 고백>",
    "concert_singer": "황영웅",
    "concert_venue": "대전 컨벤션센터 제2전시장",
    "concert_start_date": "2024-05-25 17:00:00"
  }
]
```
--------------------------------------------------------------------------
### 4. 콘서트 좌석 조회
특정 콘서트의 예약 가능한 콘서트 좌석을 조회합니다.

| Method | URI | Description         |
|-----|-----|---------------------|
| GET |/concert/{concertId}/reservation/seats| 예약 가능한 좌석 정보 리스트 조회 |
<b>Request</b>
```https request
  curl -X GET https://{SERVER_URL}/concert/{concertId}/reservation/seats
```
<b>Response</b>
```json
{
  "code": "INVALID_WAIT_INFORMATION",
  "message": "대기열 만료"
}
```
```json
{
  "seats": [
    {
      "seat_id": 1,
      "seat_number": 1,
      "price": 5000
    },
    {
      "seat_id": 23,
      "seat_number": 16,
      "price": 5000
    },
    {
      "seat_id": 29,
      "seat_number": 17,
      "price": 5000
    },
    {
      "seat_id": 59,
      "seat_number": 25,
      "price": 7500
    },
    {
      "seat_id": 61,
      "seat_number": 48,
      "price": 10000
    },
    {
      "seat_id": 89,
      "seat_number": 50,
      "price": 10000
    }
  ]
}
```
--------------------------------------------------------------------------
### 5. 콘서트 좌석 예약
콘서트 좌석을 임시 예약 요청하며, 예약 완료 시 5분간 임시 배정됩니다.

| Method | URI | Description |
|-----|-----|------------|
| POST |/concert/reservation| 콘서트 예약   |

<b>Request</b>
```https request
  curl -X POST https://{SERVER_URL}/concert/reservation \
  -H "Content-Type: application/json" \
  -d '{ \
        "userId": {userId}, \
        "seatId": {seatId} 
      }'
```
<b>Response</b>
```json
{
  "code": "INVALID_WAIT_INFORMATION",
  "message": "대기열 만료"
}
```
```json
{
  "code": "ALREADY_RESERVATION",
  "message": "이미 예약된 좌석"
} 
```
```json
{
  "code": "SUCCESS",
  "message": "좌석 임시 배정 완료, 만료시간 : yyyy-mm-dd hh:mm:ss"
} 
```
--------------------------------------------------------------------------
### 6. 결재
임시 예약한 콘서트 좌석을 결제요청 합니다.

| Method | URI | Description           |
|-----|-----|-----------------------|
|POST|/concert/payment| 좌석 결제                 |
<b>Request</b>
```https request
  curl -X PATCH https://{SERVER_URL}/concert/payment \
  -H "Content-Type: application/json" \
  -d '{ \
        "userId": "{userId}", \ 
        "seatId": {seatId} \
      }'
```
<b>Response</b>
```json
{
  "code": "INVALID_WAIT_INFORMATION",
  "message": "대기열 만료"
}
```
```json
{
  "code": "SEAT_UNAVAILABLE",
  "message": "예약된 좌석이 없습니다."
} 
```
```json
{
  "code": "INSUFFICIENT_POINTS",
  "insufficient_point": 3000,
  "point": 2000,
  "message": "포인트 부족"
} 
```
```json
{
  "status": "SUCCESS",
  "message": "{concertStartDate} | {concertName} | {seatNo}"
} 
```
--------------------------------------------------------------------------
### 6. 포인트 충전
포인트를 충전합니다.

| Method | URI | Description           |
|-----|-----|-----------------------|
|PATCH|/point| 포인트 충전                |
<b>Request</b>
```https request
  curl -X PATCH https://{SERVER_URL}/point \
  -H "Content-Type: application/json" \
  -d '{ \
        "userId": "{userId}", \ 
        "point": {point} \
      }'
```
<b>Response</b>
```json
{
  "status": "SUCCESS",
  "charge_point": 1000, 
  "point": 5000
}
```
--------------------------------------------------------------------------
### 7. 포인트 조회
포인트를 조회합니다.

| Method | URI | Description           |
|-----|-----|-----------------------|
|GET|/point/{userId}| 포인트 조회                |
<b>Request</b>
```https request
  curl -X GET https://{SERVER_URL}/point/{userId}
```
<b>Response</b>
```json
{
  "status": "SUCCESS",
  "point": 2000
}
```
--------------------------------------------------------------------------