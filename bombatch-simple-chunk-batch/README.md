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
## Processor
- Processor 는 필수가 아니다.