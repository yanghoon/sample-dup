###########
# TODO
# * $res == current(.)
# * one-time var : header, query, ...  -->  exchange{property, message:{header, body} }
#
# [DONE]
# * url operation : append path/param, support jq, ... --> support jq (GET {jq-expr...}/remain?key=val#ctx.key
# * shorten url : host list like zuul/ribbon --> use ".site" variable
# * multi line

## ASSUME FROM EXTERNAL
SET .kid = 45

## Environment Variables
SET .site = "https://mytask.skcc.com"
SET .headers = {Authorization:"Basic " + ("07713:didgns" | @base64)}
SET .querys = {maxResults: 10}

## get filter from kanban config
GET /rest/agile/1.0/board/{kid}/configuration#conf

## https://jira.com/rest/api/2/filter/100
SET .querys = {fields: "summary,created,updated,issuetype,status,priority,project,assignee,customfield_10002,customfield_10004"}
GET {$conf.filter.self}

## get issues of kanban
GET {$res.searchUrl}
SET .issues = $res.issues


## group by names
""
$issues[] | .fields |
{
  user: .assignee.displayName//"Unknown",
  type: .issuetype.name,
  status:   .status.name,
  priority: .priority.name
}
""
SET .raw = $res

$raw | map(to_entries[]) | reduce .[] as $i ({}; .[$i.key][$i.value] += 1 )
SET .sum = $res

group_by(.user) [] | map(to_entries[]) | reduce .[] as $i ({}; .[$i.key][$i.value] += 1 ) | reduce . as $i ({}; .[ $i.user | to_entries[].key ] = $i )
group_by(.user) [] | map(to_entries[]) | reduce .[] as $i ({}; .[$i.key][$i.value] += 1 ) | { (.user|keys[0]) : . } 


## to avoid waste memory
#SET {issues: null, conf: null}