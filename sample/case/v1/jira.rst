
JQ {Authorization:"Basic MDc3MTM6ZGlkZ25z"}
SET headers

JQ {maxResults: 10}
SET querys

GET https://mytask.skcc.com/rest/agile/1.0/board
SET boards

## https://stedolan.github.io/jq/manual/#Assignment
## https://stackoverflow.com/questions/39500608/remove-all-null-values
JQ $result | .values |= [ .[] | select(.type == "kanban") ]
JQ $result | .size = (.values | length)
SET kanbans

JQ $result | .values[0].self + "/configuration"
SET url


GET {url}
SET board
JQ "https://mytask.skcc.com/rest/api/2/filter/\($result.filter.id)"
SET url

GET {url}
SET filter
JQ $result.searchUrl
SET url


JQ $querys | .fields = "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"
SET querys

GET {url}
SET issues

## exist about performance
## https://stackoverflow.com/questions/26666120/how-can-i-flatten-this-object-stream-without-creating-duplicate-objects
JQ $issues | .issues[] | {id, key, self}
JQ $result[] + ( $issues.issues[] | .fields | {name: .summary, created, updated} )
#JQ $result[] + ( $issues.issues[] | .fields.issuetype | {typeId: .id, typeName: .name} ) + ( $issues.issues[] | .fields.status | {status: .name} )
#JQ $result[] + ( $issues.issues[] | .fields.status | {status: .name} )


