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

### 동시성 문제 유즈케이스
[대기열]
- 한 명의 사용자가 대기열을 여러번 신청 하는 케이스
- 여러명의 사용자가 대기열을 동시에 신청 하는 케이스 (QUEUE_LIMIT)

[좌석 예약]
- 여러명의 사용자가 같은 좌석 예약 신청 하는 케이스

[포인트 충전]
- 포인트를 동시에 충전 하는 케이스
- 포인트를 동시에 사용 하는 케이스

기존의 구현은 REDIS가 아닌 RDB로 구현

대기열의 경우 토큰이 없는 경우에는 토큰에 대한 ROW가 존재 하지 않기 때문에 DB LOCK을 통한 제어가 불가능.

이러한 문제점을 개선하기 위해서는 대기열 테이블을 2개로 구성 하여 해결이 가능
ONGOING TABLE, WAIT TABLE로 나누어서 구성
1) 대기열에 신청 API를 호출하는 경우 WAIT TABLE에 등록
2) WAIT TABLE -> ONGOING TABLE 서버에서 스케쥴러를 통한 작업

RDB를 사용해서 대기열 동시성 문제를 처리하기 위해서는 테이블을 분리해야 하므로 관리 포인트가 늘어나기 때문에 Redis 분산락을 고려



