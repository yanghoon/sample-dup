###########
# TODO
# * $res == current(.)
# * one-time var : header, query, ...  -->  exchange{property, message:{header, body} }
#
# [DONE]
# * url operation : append path/param, support jq, ... --> support jq (GET {jq-expr...}/remain?key=val#ctx.key
# * shorten url : host list like zuul/ribbon --> use ".site" variable
# * multi line

## Environment Variables
SET .loopMaxCount = 4
SET .site = "https://mytask.skcc.com"
SET .headers = {Authorization:"Basic " + ("07713:didgns" | @base64)}

# merge pagination by nextPage URL
SET {all: [], users: {nextPage: "/rest/api/2/group/member?groupname=jira-users"} }
UNTIL $users.nextPage
  GET {$users.nextPage}#users
  SET .all = $all + [$res.values[] | {name, email:.emailAddress, displayName}]
END

# validation
SET .all_len = ($all | length)
SET .all_names = ([$all[] | .name] | unique)
SET .all_names_len = ($all_names | length)