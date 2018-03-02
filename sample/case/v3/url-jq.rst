
SET {username:"aaa", apiKey: "bbb"}
SET .site = "https://\($username):\($apiKey)@jsonplaceholder.typicode.com/rest/v3"
#SET .site = "https://{username}:{apiKey}@jsonplaceholder.typicode.com"

#GET /SoftLayer_Account.json#hws
#
#SET .site = .site + "/SoftLayer_Hardware_Server"
#GET /{$hws[0].vid}.json#hw
#GET /{$hw.vid}/NetworkComponents/{$hw.net}.json

SET ""
.querys = {
    "parameters" : [
        {
            "name" : "example.org",
            "resourceRecords" : [
                {
                    "type" : "a",
                    "host" : "@",
                    "data" : "127.0.0.1"
                }
            ]
        }
    ]
}
""
#POST /SoftLayer_Dns_Domain.json