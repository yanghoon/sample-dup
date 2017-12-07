####
# TODO
# * $res == current(.)
# * one-time var : header, query, ...  -->  exchange{property, message:{header, body} }
# * multi line
#
# [DONE]
# * url operation : append path/param, support jq, ... --> support jq (GET {jq-expr...}/remain?key=val#ctx.key
# * shorten url : host list like zuul/ribbon --> use ".site" variable

## by operation
SET .site = "https://mytask.skcc.com"
SET .headers = {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
SET .querys = {maxResults: 10}


##  get kanban board
GET /rest/agile/1.0/board
  $res | .values |= [ .[] | select(.type == "kanban") ]
  $res | .size = (.values | length)
SET .kanbans = $res

## get filter of random kanban board
GET {$kanbans.values[0].self}/configuration#conf


## https://mytask.skcc.com/rest/api/2/filter/10412
SET .querys = {fields: "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"}
GET {$conf.filter.self}
GET {$res.searchUrl}
SET .issues = $res.issues[:5]


## exist about performance
## https://stackoverflow.com/questions/26666120/how-can-i-flatten-this-object-stream-without-creating-duplicate-objects
JQ $issues[] | {id, key, self}
JQ $res[] + ( $issues[] | .fields | {name: .summary, created, updated} )
JQ $res[] + ( $issues[] | .fields.issuetype | {typeId: .id, typeName: .name} )
JQ $res[] + ( $issues[] | .fields.status | {status: .name} )


