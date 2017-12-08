####
# TODO
# * $res == current(.)
# * one-time var : header, query, ...
# * url operation : append path/param, support jq, ...
# * shorten url : host list like zuul/ribbon
# * multi line

## by operation
SET .site  "https://mytask.skcc.com"
SET .headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
SET .querys = {maxResults: 10}
INSPEC


##  get kanban board
GET /rest/agile/1.0/board
  .values |= [ .[] | select(.type == "kanban") ]
  .size = (.values | length)
.kanbans = $res


## get filter of random kanban board
GET {$kanbans.values | rand | .self}/configuration
GET /rest/api/2/filter/{$res.filter.id}


## https://mytask.skcc.com/rest/api/2/filter/10412
.querys.fields = "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"
GET {$filter.searchUrl}


## exist about performance
## https://stackoverflow.com/questions/26666120/how-can-i-flatten-this-object-stream-without-creating-duplicate-objects
JQ $issues | .issues[] | {id, key, self}
JQ $res[] + ( $issues.issues[] | .fields | {name: .summary, created, updated} )


