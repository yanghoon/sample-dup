
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

#$exp1
#$exp2
#$exp3
#$exp4
#$exp5