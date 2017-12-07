XXX는 JSON 형식의 RESTful 서비스 호출을 보다 간결하게 작성할 수 있도록, 관련 문법과 인터프리터를 포함한다.

JSON의 가공을 위해서 Jq Processor 문법을 지원한다. (오픈소스 Java Jq Processor 구현체를 포함)

아래는 XXX의 문법과 인터프리터 사용법을 설명한다.

# 문법
## 구문
각각의 라인은 순차적으로 실행한다. (인터프리터 형식)

라인은 아래의 구문 중 하나와 대응된다.
<dl>
  <dt>Comment<dt/>
  <dd>라인의 제일 첫 글자가 `#`인 경우는 주석으로 인식한다.</dd>

  <dt>Operation</dt>
  <dd>대문자로 구성(uppercase)된 연산자(Operation)와 1개의 인자(argument)로 이루어진다.</dd>
  
  <dt>JQ Expr</dt>
  <dd>Operation으로 시작하지 않는 행은 Jq 문법으로 해석된다.</dd>
</dl>

```
# Sample
OPERATION arguments...
jq expr...
```

### 연산(Operation)
아래는 현재까지 지원되는 Operation의 목록이다

| Operation | Argument as | Descroption | Note |
| - | -- | --- | ---- |
| GET | url | HTTP GET 요청을 수행한다. | |
| SET | jq-expr | jq-expr를 변수로 저장한다. | |
| INSPEC | | 현재의 변수 목록을 출력한다.실행결과(res 변수)에 영향을 주지 않는다. | |


## 변수
### 변수 선언
변수의 선언을 위해서는 `SET` Operation을 활용한다.

argument의 결과가 Json Object인 경우, 모든 필드를 변수로 각각 저장한다.
### 변수 참조
선언된 변수는 jq-expr에서 `$name`의 형태로 접근 가능하다.

아래는 url을 변수로 선언하고, GET 요청에서 참조하는 예제이다
```
#SET .url = "http://my.rest.com/api"
 SET {url:"http://my.rest.com/api"}
 GET {$url}/users
```

#### res 변수
이전 라인의 실행 결과는 `res` 변수에 저장된다.

이를 통해 별도의 변수 선언없이, 이전 실행결과를 참조할 수 있다.

아래는 `res` 변수를 활용하여, GET 응답의 필드를 추출하고 변수로 저장하는 예제이다.
```
GET http://my.rest.com/api/users
    $res | .[] | {id, displayName, gender}
SET .user = $res
```


## REST Operation
### URL 조합
REST Operation은 요청에 대한 가독성을 높이기 위해 URL을 간결하게 작성할 수 있는 조합방식을 지원한다.

<dl>
  <dt>site 변수</dt>
  <dd>URL(argument)가 http로 시작하지 않는 경우, `site` 변수를 URL(argument)의 앞에 자동으로 추가한다.</dd>

  <dt>headers 변수</dt>
  <dd>JSON Object 형식의 headers 변수 내용을 Http Header에 추가한다.</dd>

  <dt>querys 변수</dt>
  <dd>JSON Object 형식의 querys 변수 내용을 Http Query String으로 변환하여 추가한다.</dd>

  <dt>jq-expr</dt>
  <dd>URL(argument)가 `{jq-expr}`의 형태로 시작하는 경우, jq-expr의 실행 결과를 URL로 사용하여 REST 요청을 수행한다.</dd>
</dl>

#### 예제
```
# Full URL
GET https://jsonplaceholder.typicode.com/posts?id=1#exp1

# Site Varibale
SET .site = "https://jsonplaceholder.typicode.com"
GET /posts?id=2#exp2

# Http Headers
SET .headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
GET /posts?id=3#exp3

# Http Query String
SET .querys = {id: 4}
GET /posts#exp4

# Http Query String
SET .url = "https://jsonplaceholder.typicode.com/posts"
SET .querys = {id:5}
GET {$url}#exp5


#### Result
 + GET https://jsonplaceholder.typicode.com/posts?id=1#exp1
   GET https://jsonplaceholder.typicode.com/posts?id=1#exp1 HTTP/1.1

 + SET .site = "https://jsonplaceholder.typicode.com"
 + GET /posts?id=2#exp2
   GET https://jsonplaceholder.typicode.com/posts?id=2#exp2 HTTP/1.1

 + SET .headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
 + GET /posts?id=3#exp3
   GET https://jsonplaceholder.typicode.com/posts?id=3#exp3 HTTP/1.1

 + SET .querys = {id: 4}
 + GET /posts#exp4
   GET https://jsonplaceholder.typicode.com/posts?id=4#exp4 HTTP/1.1

 + SET .url = "https://jsonplaceholder.typicode.com/posts"
 + SET .querys = {id:5}
 + GET {$url}#exp5
   GET https://jsonplaceholder.typicode.com/posts?id=5#exp5 HTTP/1.1

context = 
{
    res_before = [{"userId":1,"id":4,"title":"eum et est occaecati","body":"ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit"}]
    res = [{"userId":1,"id":5,"title":"nesciunt quas odio","body":"repudiandae veniam quaerat sunt sed\nalias aut fugiat sit autem sed est\nvoluptatem omnis possimus esse voluptatibus quis\nest aut tenetur dolor neque"}]
    site = "https://jsonplaceholder.typicode.com"
    headers = {"Authorization":"Basic MDc3MTM6ZGlkZ25z"}
    querys = {"id":5}
    url = "https://jsonplaceholder.typicode.com/posts"
    exp1 = [{"userId":1,"id":1,"title":"sunt aut facere repellat provident occaecati excepturi optio reprehenderit","body":"quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"}]
    exp2 = [{"userId":1,"id":2,"title":"qui est esse","body":"est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"}]
    exp3 = [{"userId":1,"id":3,"title":"ea molestias quasi exercitationem repellat qui ipsa sit aut","body":"et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut"}]
    exp4 = [{"userId":1,"id":4,"title":"eum et est occaecati","body":"ullam et saepe reiciendis voluptatem adipisci\nsit amet autem assumenda provident rerum culpa\nquis hic commodi nesciunt rem tenetur doloremque ipsam iure\nquis sunt voluptatem rerum illo velit"}]
    exp5 = [{"userId":1,"id":5,"title":"nesciunt quas odio","body":"repudiandae veniam quaerat sunt sed\nalias aut fugiat sit autem sed est\nvoluptatem omnis possimus esse voluptatibus quis\nest aut tenetur dolor neque"}]
}
```
