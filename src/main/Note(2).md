# 도메인 이해

## Job
### JobLauncher 
- Job을 구동시키는 역할
- 파라미터로 Job 객체와 JobParameter 객체를 받는다.

### JobInstance
- Job-parameter 값 두개를 기준으로 중복생성이 불가하다.
- Job과 JobInstance 는 1:N의 관계이다.

### JobParameter
- key-value로 이루어진 map을 가짐
    ```java
      public class JobParametersBuilder {
      private Map<String, JobParameter> parameterMap;
      private JobExplorer jobExplorer;

      public JobParametersBuilder() {
          this.parameterMap = new LinkedHashMap();
      }

      public JobParametersBuilder(JobExplorer jobExplorer) {
          this.jobExplorer = jobExplorer;
          this.parameterMap = new LinkedHashMap();
      }

      public JobParametersBuilder(JobParameters jobParameters) {
          this(jobParameters, (JobExplorer)null);
      }
    }
    ```
- ParameterType 필드가 있어서 값은 Strig, Date, Long, Double 타입을 가질 수 있다.
    ```java
  // Class JobParameter
  public class JobParameter implements Serializable {
    private final Object parameter;
    private final ParameterType parameterType;
    private final boolean identifying;

    public JobParameter(String parameter, boolean identifying) {
        this.parameter = parameter;
        this.parameterType = JobParameter.ParameterType.STRING;
        this.identifying = identifying;
    }

    public JobParameter(Long parameter, boolean identifying) {
        this.parameter = parameter;
        this.parameterType = JobParameter.ParameterType.LONG;
        this.identifying = identifying;
    }
  
  // ......
  
      public static enum ParameterType {
      STRING,
      DATE,
      LONG,
      DOUBLE;
    
            private ParameterType() {
            }
      }
    }
    ```
    
- 하나의 Job에 존재할 수 있는 여러개의 인스턴스를 구분하기 위한 용도
      - JobParameters, JobInstance는 1:1 관계이다.
  
- 생성 및 바인딩
  - 방법1. 애플리케이션 실행 시 주입
  - 방법2. 코드로 생성 - JobParameterBuilder
  - 방법3. SpEL이용

- step.tasklet(StepContribution) -> StepContribution -> StepExecution -> JobExecution -> JobParameter

### JobExecution
- JobInstance에 대한 한번의 시도를 의미하는 객체로서 Job 실행중에 발생한 정보를 가지고 있다.
  - 시작시간, 종료시간, 상태(시작됨,완료,실패), 종료상태 속성을 가짐
- JobInstance과의 관계
  - JobExecution의  상태 결과가 Completed 이면, JobInstance 실행이 완료된 것으로 간주해서 재 실행이 불가함
  - JobExecution의  상태 결과가 FAILED 이면, JobInstance 실행이 완료되지 않은 것으로 간주해, 재실행이 가능하다.
    - JobParameter가 동일한 값으로 Job을 실행할지라도 JobInstance를 계속 실행 할 수 있다.
  - JobExecution의 실행 상태 결과가 Completed 될 때까지 하나의 JobInstance 내에서 여러번의 시도가 생길 수 있다.
    - JonInstance : JobExecution = 1 : M

## Step
### Step
- Job을 구성하는 독립적인 하나의 단계
- 실제 배치 처리를 정의하고 컨트롤하는데 필요한 모든 정보를 가지고 있는 도메인 객체
- 기본 구현체 (img/Step 클래스 구조.png)
  - `TaskletStep`
    - 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체 제어
  - `PartitionStep`
    - 멀티스레드 방식으로 Step을 여러 개로 분리해서 실행
  - `JobStep`
    - Step 내에서 Job을 실행하도록 한다. (체이닝식으로)
  - `FlowStep`
    - Step 내에서 Flow를 실행하도록 한다. 

### StepExecution
- Step에 대한 한번의 시도를 의미하는 객체로서 Job 실행중에 발생한 정보를 가지고 있다.
- Job이 재시작 하더라고 이미 성공적으로 완료된 Step은 실행되지 않고 실패한 Step만 실행한다.
  - 이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 StepExecution을 생성하지 않는다.
- StepExecution와 JobExecution은 1:M의 관계이며, 하나의 Job에 여러 개의 Step으로 구성했을 경우 
  각 StepExecution은 하나의 JobExecution을 부모로 가진다.

### StepContribution
- 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExcution 상태를 업데이트하는 도메인 객체
- 