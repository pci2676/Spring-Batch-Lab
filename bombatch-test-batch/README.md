# 배치에서 테스트하는 방법을 정복하자
동욱님의 블로그를 통해 ^^

- JobLauncherTestUtils
  - Batch Job을 테스트 환경해서 실행해주는 유틸 클래스
  - launchJob 을 하면 JobExecution이 나온다.
  
- JobExecution
  - Job 을 실행한 다음 나오는 객체
  - 이 객체를 사용해서 Job 의 성공여부를 파악할 수 있다.