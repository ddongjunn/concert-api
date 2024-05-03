### 브랜치 전략 : Github Flow

branches[ "master", develop", "feature-*"]

1. master 브랜치에서 develop 브랜치 분기
2. 기능 구현을 하는 경우 develop 브랜치에서 feature-* 브랜치를 분기
3. 개발이 끝난 feature-* 브랜치는 develop 브랜치로 merge (dev deploy)

   (develop 브랜치에 한 feature 당 하나의 커밋, 한 bugfix 당 하나의 커밋을 유지)
   - --squash 옵션을 사용하여 merge
   - git rebase의 interactive 모드를 사용
4. production 배포할 경우 develop 브랜치는 master 브랜치로 병합 (prod deploy)

#### feature
- 새로운 기능이나 버그 수정 작업을 수행하기 위한 브랜치
- 이 브랜치는 작업의 컨텍스트를 제공하고, 기능의 독립성을 보장
---
# 동시성 문제 유즈케이스

### Redisson 라이브러리를 이용하여 분산락 적용

Simple Lock의 경우 key 선점에 의한 lock 획득 실패 시, 비즈니스 로직을 수행하지 않기 때문에 포인트 로직에 맞지 않다고 판단하였고, Spin Lock의 경우 Lock을 획득하기 위해 지속적인 재시도로 인한 성능적인 부분을 고려하여
Redisson을 이용한 PUB/SUB 방식의 분산 lock 사용

Lettuce을 이용하여 분산락 구현 시 `setnx`,`setex`과 같은 명령어를 이용해 지속적으로 Redis에게 요청을 보내는 스핀락 방식으로 동작
요청이 많을수록 Redis가 받는 부하가 커지게 되지만 `Redisson`은 Pub/Sub 방식을 이용하기 때문에 락이 해제되면 락을 subcribe하는
클라이언트는 락이 해제되었다는 신호를 받고 락 획득을 시도

----
### 대기열
동시성 케이스
- 한 명의 사용자가 대기열을 여러번 신청 하는 케이스
- 여러명의 사용자가 대기열을 동시에 신청 하는 케이스 (QUEUE_LIMIT)

**AS-IS (RDB)**

대기열의 경우 토큰이 없는 경우에는 토큰에 대한 ROW가 존재 하지 않기 때문에 DB LOCK을 통한 제어가 불가능.
위 문제점을 해결 하기 위한 방안으로 대기열 테이블을 2개로 구성 하여 해결이 가능 (ONGOING TABLE, WAIT TABLE로 나누어서 구성)
1) 대기열에 신청 API를 호출하는 경우 WAIT TABLE에 등록
2) WAIT TABLE -> ONGOING TABLE 서버에서 스케쥴러를 통한 작업

-> RDB를 사용해서 대기열 동시성 문제를 처리하기 위해서는 테이블을 분리해야 하므로 관리 포인트가 늘어나기 때문에 Redis을 사용하여 대기열 로직 리팩토링

**TO-BE (REDIS)**

1) 대기열 신청시 Redis SortedSet을 사용한 Wait Queue에 순차적으로 넣음
2) 스케쥴러를 통해서 Wait Queue에 쌓인 유저들을 Redis Map 자료 구조를 사용한 Ongoing Queue로 이동
3) Ongoing Queue는 대기열 만료 시간 TTL을 사용하여 대기열 관리
4) 대기열 신청 API를 재호출 하는 경우 순번이 뒤로 밀려남
---
### 포인트 충전/사용
- 포인트를 동시에 충전 하는 케이스
- 포인트를 동시에 사용 하는 케이스

**AS-IS (RDB)**

Database Lock, 비관적 락을 사용하여 동시성 제어

**TO-BE (REDIS)**

Redisson 라이브러리를 사용하여 분산락(PUB/SUB) 사용

LOCK Key = LOCK : POINT : CHARGE : userId

LOCK Key = LOCK : POINT : USE : userId

---
### 좌석 예약
- 여러명의 사용자가 같은 좌석 예약 신청 하는 케이스

**AS-IS (RDB)**

Database Lock, 비관적 락을 사용하여 동시성 제어

**TO-BE (REDIS)**

Redisson 라이브러리를 사용하여 분산락(PUB/SUB) 사용

LOCK Key = LOCK : SEAT + seatId : CONCERT + concertOptionId

---
