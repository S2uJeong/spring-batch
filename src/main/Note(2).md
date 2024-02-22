## 도메인 이해
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
