## error
# internal rest-api
RES-IF {jq-expr, res.status, body.result.code =! } {body...}
SET .query = {...}
CALL /404-common

## global variable
CALL /set-user-info
$seesion.user-name
$seesion.bookmark