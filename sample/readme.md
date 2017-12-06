XXX는 JSON 형식의 RESTful 서비스 호출을 보다 간결하게 작성할 수 있도록, 관련 문법과 인터프리터를 포함한다.
JSON의 가공을 위해서 Jq Processor 문법을 지원한다. (오픈소스 Java Jq Processor 구현체를 포함)
아래는 XXX의 문법과 인터프리터 사용법을 설명한다.

# 문법
XXX는 각각의 라인을 순차적으로 실행한다. (인터프리터 형식)
각각의 라인은 아래의 구문 중 하나와 대응된다
1. 라인의 체일 첫 글자가 '#'인 경우는 주석으로 인식한다.
2. 대문자로 시작하는 Operation와 1개의 인자(argument)로 이루어진다.
3. 대문자 Operation으로 시작하지 않는 행은 Jq 문법으로 해석된다.

    OPERATION arguments...
    jq expr...

아래는 현재까지 지원되는 Operation의 목록이다

GET url : HTTP GET 요청을 수행한다.
SET jq-expr : jq-expr를 변수로 저장한다.
INSPEC : 현재의 변수 목록을 출력한다. 실행결과(res 변수)에 영향을 주지 않는다.


## 변수
### 변수 선언
변수의 선언을 위해서는 SET Operation을 활용한다. argument의 결과가 Json Object인 경우, 모든 필드를 변수로 각각 저장한다.
### 변수 참조
위에서 선언된 변수는 jq-expr에서 $name의 형태로 접근 가능하다.

아래는 URL을 변수로 선언하고, GET 요청에서 참조하는 예제이다

    #SET .url = "http://my.rest.com/api"
     SET {url:"http://my.rest.com/api"}
     GET {$url}/users

#### res 변수
XXX는 바로 직전 라인의 실행 결과를 res 변수에 저장하고 있다. 이를 통해 별도의 변수 선언없이, 이전 실행결과를 참조할 수 있다.
아래는 GET 요청의 결과에서 특정 필드만을 추출하여 별도의 변수로 저장하는 예제이다.

    GET http://my.rest.com/api/users
        $res | .[] | {id, displayName, gender}
    SET .user = $res



## REST 호출
### URL 조합
XXX는 REST 요청에 사용되는 URL 정보에 대한 가독성을 높이기 위해 다양한 방식의 URL 조합을 지원한다.

### site 변수
REST Operation에서는 argument가 http로 시작하지 않는 경우, site 변수를 argument의 앞에 자동으로 추가한다.

### headers 변수
REST 요청 시, headers변수의 내용을 Http Header에 추가한다.

### querys 변수
REST 요청 시, querys 변수의 내용을 query string으로 변환하여 추가한다.

### jq-expr을 활용한 URL
argument가 {jq-expr}의 형태로 시작하는 경우, jq-expr의 실행 결과를 URL로 사용하여 REST 요청을 수행한다.
