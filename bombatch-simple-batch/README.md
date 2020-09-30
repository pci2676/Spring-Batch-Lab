## 메타데이터 테이블

### BATCH_JOB_INSTACNE

- Job Parameter 에 따라 생성되는 테이블이다.
- Job Parameter 는 Spring Batch가 실해될때 외부에서 받을 수 있는 파라미터이다.
- **같은 Batch Job 이라도 Job Parameter 가 다르면 새롭게 ROW 로 기록이 되고 같다면 기록되지 않는다.**
    - 단, 같은 Parameter 로 성공한 내역이 있으면 실행할 때 `JobInstanceAlreadyCompleteException` 가 발생한다.  

### BATCH_JOB_EXECUTION

- BATCH_JOB_INSTANCE 와 부모-자식의 관계를 보인다.
- BATCH_JOB_INSTANCE 가 성공/실패한 내역을 가지고 있다.

## Parameter 설정

```yml
spring:
  batch:
    job:
      names: ${job.name:NONE}
```
- job.name 을 파라미터로 주입받지 않으면 NONE 이 전달되면서 어떠한 batch Job 도 실행되지 않는다.
- parameter 는 `--job.name=<job name>` 형식으로 전달하면 된다.

## Conditional Job Flow

- `.on()` : `ExitStatus` 의 `switch` 문이다. `*`로 하면 `true` 처럼 동작한다.
- `.to()` : `on()` 결과에 따라 어떠한 step 을 진행할지 정한다.
- `.from()` : 이벤트 리스너와 같은 역할 `.to()` 와 연결하여 사용한다고 보면된다.
- `end()` : `FlowBuilder` 를 반환하거나 `FlowBuilder` 를 종료하거나

`on()` 이 캐치하는 값은 `ExitStatus` 임을 기억하자

- `BatchStatus` 는 Job, Step 의 실행결과를 Spring 에 기록할 때 사용하는 Enum
- `ExitStatus` 는 Step의 실행 후 상태 값