
SET .site = "https://jsonplaceholder.typicode.com"

GET /posts
SET ""
{
	post: $res[0],
	id: $res[0].id
}
""

GET /posts/{id}/comments
  $res | .[] | {id,name,email,body} | .url="https://jsonplaceholder.typicode.com/comments/\(.id)"
  $post | .comments |= $res[:1]