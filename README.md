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



