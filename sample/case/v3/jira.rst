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
SET .site = "https://jira.com"
SET .headers = {Authorization:"Basic " + ("admin:xxxx" | @base64)}
SET .querys = {maxResults: 10}


##  get kanban board
GET /rest/agile/1.0/board
  $res | .values |= [ .[] | select(.type == "kanban") ]
  $res | .size = (.values | length)
SET .kanbans = $res

## get filter of random kanban board
GET {$kanbans.values[0].self}/configuration#conf


## https://jira.com/rest/api/2/filter/100
SET .querys = {fields: "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"}
GET {$conf.filter.self}
GET {$res.searchUrl}
SET .issues = $res.issues[:5]


## exist about performance
## https://stackoverflow.com/questions/26666120/how-can-i-flatten-this-object-stream-without-creating-duplicate-objects
#JQ $issues[] | {id, key, self}
#JQ $res[] + ( $issues[] | .fields | {name: .summary, created, updated} )
#JQ $res[] + ( $issues[] | .fields.issuetype | {typeId: .id, typeName: .name} )
#JQ $res[] + ( $issues[] | .fields.status | {statusId:.id, statusName: .name} )
#JQ $res[] + ( $issues[] | .fields.priority | {priorityId:.id, priorityName: .name} )
#JQ $res[] + ( $issues[] | .fields.assignee | {displayName, key} )

#""
#$issues[] |
#{
#  id,
#  key,
#  self
#} 
#+
#{
#  name: .fields.summary,
#  created: .fields.created,
#  updated: .fields.updated,
#  typeId: .fields.issuetype.id,
#  typeName: .fields.issuetype.name
#}
#+
#{
#  statusId:     .fields.status.id,
#  statusName:   .fields.status.name,
#  priorityId:   .fields.priority.id,
#  priorityName: .fields.priority.name
#}
#""


$issues[] | { id, key, self } 
""
$res[] + $issues[] | .fields |
  {
    name:     .summary,
    created:  .created,
    updated:  .updated,
    typeId:   .issuetype.id,
    typeName: .issuetype.name
  }
  +
  {
    statusId:     .status.id,
    statusName:   .status.name,
    priorityId:   .priority.id,
    priorityName: .priority.name
  }
""

#reduce $res[] as $i ({}; . as $in | ($i.statusName) as $key | . + {($key): $in[$key]} )

## working at jq.play (https://jqplay.org/jq)
#reduce $res[] as $i ({}; ["status", $i.statusName] as $p | setpath($p; $i.storypoint//1 + getpath($p) ) )