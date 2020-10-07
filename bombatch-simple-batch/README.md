# 동욱님의 배치 글을 보고 정리를 정리해서 내것으로 만들기 위한 프로젝트

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

## JobParameter, Scope

Spring Batch 에서 외부, 내부에서 파라미터를 받아서 사용할 수 있는데 이러한 파라미터를 JobParameter 라 부른다.  
JobParameter 를 사용하기 위한 조건은 Scope (JobScope, StepScope...)를 명시하는 것이다.  
JobParameter는 다음과 같이 받아올 수 있다.  
```java
@Value("#{jobparameter[<파라미터 이름>]}")
``` 
JobScope 는 Job 에서 Step 으로 넘기는 파라미터에 적용한다.
StepScope 는 Step 에서 Tasklet 혹은 Reader, Processor, Writer 로 넘기는 파라미터에 적용한다.  
스코프에 따라 Bean 을 생성하기 때문에 JobParamter 의 Late binding 이 가능해진다.  
그리고 Scope 에 따라 새롭게 Bean 을 생성하기 때문에 병렬 프로그래밍에서 안정성을 가져간다.   
Scope 를 가진 Bean 을 만든 경우 구현체를 반환하지 않으면 프록시 객체가 반환되는데 프록시 객체는 필요한 인터페이스가 없어서 NullPointerException 이 발생할 수 있다.

### 시스템 변수는 못 쓴다
시스템 변수는 `application.properties`과 `-D` 로 주는 옵션을 의미한다.  
- Spring Batch 는 시스템 변수로 JobParameter 관련 기능을 쓰지 못한다.
    - 특히 Late Binding 을 못함  
    - 일단 같은 JobParameter 로 두 번 실행하지 않음
    - 심지어 Spring Batch 가 자동으로 관리하는 Parameter 관련 메타 테이블 관리가 안됌
- Command Line 이 아닌 방법으로 Job 실행이 어렵다.
    - 시스템 변수를 동적으로 변화시키기가 어렵다는 말이다.  