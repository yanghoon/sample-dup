####
# TODO
# * $res == current(.)
# * one-time var : header, query, ...
# * shorten url : host list like zuul/ribbon
# * multi line
#
# [DONE]
# * url operation : append path/param, support jq, ...

## by custom func
## BUG : $res is not a last value
#"https://mytask.skcc.com" | set(base)
#{Authorization:"Basic MDc3MTM6ZGlkZ25z"} | set(headers)
#{maxResults: 10} | set(querys)

## by operation
SET .base = "https://mytask.skcc.com"
SET .headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
SET .querys = {maxResults: 10}

## WANT
#.base = "https://mytask.skcc.com"
#.headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
#.querys = {maxResults: 10}

##  get kanban board
GET /rest/agile/1.0/board
  $res | .values |= [ .[] | select(.type == "kanban") ]
  $res | .size = (.values | length)
SET .kanbans = $res

## get filter of random kanban board
#GET {$kanbans.values | rand | .self}/configuration
GET {$kanbans.values[0].self}/configuration
SET .conf = $res

## https://mytask.skcc.com/rest/api/2/filter/10412
SET .querys = {fields: "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"}
GET {$conf.filter.self}
GET {$res.searchUrl}
SET .issues = $res.issues

## exist about performance
## https://stackoverflow.com/questions/26666120/how-can-i-flatten-this-object-stream-without-creating-duplicate-objects
JQ $issues[] | {id, key, self}
JQ $res[] + ( $issues[] | .fields | {name: .summary, created, updated} )
#JQ $res[] + ( $issues[] | .fields.issuetype | {typeId: .id, typeName: .name} ) + ( $issues.issues[] | .fields.status | {status: .name} )
#JQ $res[] + ( $issues[] | .fields.status | {status: .name} )
