# true
SET .key = "value"

UNTIL true
UNTIL $key
UNTIL {key:"value"}
UNTIL [{key:"value"}]

# false
SET .key = ""

UNTIL false
UNTIL $key
UNTIL $a
UNTIL {}
UNTIL []