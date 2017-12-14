
GET https://jsonplaceholder.typicode.com/posts
SET posts

JQ $posts[0].id
SET id

JQ $posts[$id]
SET posts

GET https://jsonplaceholder.typicode.com/posts/{id}/comments
JQ $result
SET comments

JQ $comments[] | {id,name,email,body} | .url="https://jsonplaceholder.typicode.com/comments/\(.id)"
SET comments

JQ $posts | .comments |= $comments