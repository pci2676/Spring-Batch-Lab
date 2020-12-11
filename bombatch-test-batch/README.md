# 배치에서 테스트하는 방법을 정복하자
동욱님의 블로그를 통해 ^^

## 통합 테스트 
- JobLauncherTestUtils
  - Batch Job을 테스트 환경해서 실행해주는 유틸 클래스
  - launchJob 을 하면 JobExecution이 나온다.
  
- JobExecution
  - Job 을 실행한 다음 나오는 객체
  - 이 객체를 사용해서 Job 의 성공여부를 파악할 수 있다.
  
## 단위 테스트

### Reader

단위테스트는 Job, Step scope가 필요한지 아닌지로 구분이된다.

#### scope가 필요한 경우

```java
JobExecution jobExecution = jobLauncherTestUtils.launchStep("loadFileStep");
```

위 메서드를 이용해서 특정 스탭을 불러와서 사용하도록 할 수 있다.

---
  
여러 Job이 존재하는 경우 충돌이 발생하므로 @ContextConfiguration 를 이용해서 필요한 Job만 사용하도록 하자.

스프링4.1 버전 이전까지 아래와 같이 설정해줬어야 했는데
```java
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class,
    StepScopeTestExecutionListener.class })
```

이후 버전 부터 `@SpringBatchTest` 한방에 해결이된다.