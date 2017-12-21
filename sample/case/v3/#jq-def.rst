#def meld: . as $in | reduce (add|keys[]) as $k ({}; .[$k] = [$in[] | .[$k]]);

[{a:1,b:10}, {a:2,c:3}] | add