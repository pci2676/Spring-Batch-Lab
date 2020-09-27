# 메타데이터 테이블

## BATCH_JOB_INSTACNE

- Job Parameter 에 따라 생성되는 테이블이다.
- Job Parameter 는 Spring Batch가 실해될때 외부에서 받을 수 있는 파라미터이다.
- **같은 Batch Job 이라도 Job Parameter 가 다르면 새롭게 ROW 로 기록이 되고 같다면 기록되지 않는다.**
    - 단, 같은 Parameter 로 성공한 내역이 있으면 실행할 때 `JobInstanceAlreadyCompleteException` 가 발생한다.  

## BATCH_JOB_EXECUTION

- BATCH_JOB_INSTANCE 와 부모-자식의 관계를 보인다.
- BATCH_JOB_INSTANCE 가 성공/실패한 내역을 가지고 있다.