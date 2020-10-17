# Chunk Oriented Tasklet
동욱님의 블로그 배치 포스팅 정복하기!
### Page Size vs Chunk Size
- Page Size 는 한번에 조회할 item 의 양
- Chunk Size 는 한번에 처리할 트랜잭션의 단위

즉, 작업을 처리하는 원자적 사이즈는 Chunk Size 이기 때문에 Chunk Size 와 Page Size 를 일치 시켜 주는 것이 성능상 보편적으로 좋다. 
만약 Page Size Chunk Size 보다 작으면 Chunk Size 를 만족할 때 까지 Page Read 가 발생하기 때문이다.

## ItemReader

- ItemReader 는 데이터를 읽어들이는 작업을 한다.
    - 데이터는 DB, File, XML, JSON 과 같은 데이터 소스를 의미한다.
        - 입력 데이터
        - 파일 데이터
        - DB 데이터
        - Java Message Service
        - Custom Reader
    - 이외의 데이터 소스를 읽어들이고 싶다면 Custom Reader 구현체를 만들자.
    
- ItemReader 의 구현체는 ItemStream 마커 인터페이스를 구현하고 있다.
    - ItemStream 은 주기적으로 상태를 저장, 오류 발생시 복원하는 역할을 한다.
        - ItemReader 의 상태를 저장하고 실패한 곳에서 다시 실행하도록 해준다.
    - 보통의 경우 ItemReader 를 구현할 일은 없지만 QueryDSL 같은 조회 프레임 워크를 사용하면 구현할 일이 생긴다.
        - 영속성 컨텍스트를 지원하지 않는 문제가 발생하기 때문이다.
        
- Cursor Item Reader
    - Cursor 는 하나의 Connection 을 이용해서 Batch 작업을 모두 수행하기 때문에 Database Connection Timeout 시간을 어유롭게 설정해야 한다.
        - 따라서 Batch 로직이 오래걸린다면 Paging Item Reader 를 사용하자.
        
- PagingItemReader 
    - 구현체를 구현할 때 쿼리에 Order 조건을 반드시 포함 하도록 하자
    - 그렇지 않으면 페이징 하면서 자기 마음대로 그때 그때 정렬을 다르게 하기 때문에 쿼리하는 결과가 달라진다.
    - Paging 은 PageSize 에 따라 적절하게 offset 과 limit 을 생성해준다.
    - Paging 을 사용하면서 특정 조건을 바꿔가는 배치를 작업할 때 조건이 바뀌면서 페이징에 따라 건너뛰는 row 가 발생한다.
        - 이때는 PagingReader 를 override 해서 getPage 를 0으로 고정해주면 된다.
            - 데이터가 적으면 Cursor 를 쓰는 것도 한가지 방법이다.

## Writer
- JdbcWriter 를 사용할 경우 Insert Query 에 Value 를 바인딩 하는 방식은 다음과 같다.
    - columnMapped : Map<String, Object> 로 쿼리 value 에 바인딩한다.
        - 전달받은 객체가 Map 이어야 사용할 수 있다.
    - beanMapped : 전달받은 Pojo 객체를 쿼리 value 에 바인딩한다.
- InitializingBean 을 구현하기 때문에 afterPropertiesSet 메서드를 이용해서 필요한 매개변수의 검증을 하면 좋다.
## Processor
- Processor 는 필수가 아니다.