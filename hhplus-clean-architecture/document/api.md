### POST /lectures/{lectureId}/apply/{userId}
Success Response
```json
{
  "status": "SUCCESS",
  "message": "성공"
}
```
Error Response
```json
{
  "status": "FAIL",
  "message": "이미 신청한 강의 입니다."
}
```
### GET /lectures/{lectureId}/check/{userId}
Success Response
```json
{
  "status": "SUCCESS",
  "message": "성공"
}
```
 Error Response
```json
{
  "status": "FAIL",
  "message": "실패"
}
```

### GET /lectures
Success Response
```json
{
  "정보": {
    "현재 신청 가능한 강의 수": 3
  },
  "강의목록": [
    {
      "강의 코드": 1,
      "강의명": "spring",
      "현재 수강 인원": 1,
      "신청 가능 인원": 30,
      "강의 시작일": "2024-03-28 17:44:07"
    },
    {
      "강의 코드": 3,
      "강의명": "java",
      "현재 수강 인원": 0,
      "신청 가능 인원": 30,
      "강의 시작일": "2024-03-27 17:44:07"
    },
    {
      "강의 코드": 4,
      "강의명": "tdd",
      "현재 수강 인원": 0,
      "신청 가능 인원": 30,
      "강의 시작일": "2024-03-26 17:44:07"
    }
  ]
}
```