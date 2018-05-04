echo '--------------------------------------------------------------------'
def extractProperties(obj) {
    obj.getClass()
       .declaredFields
       .findAll { !it.synthetic }
       .collectEntries { field ->
           [field.name, obj."$field.name"]
       }
}

println '--------------------------------------------------------------------'

node {
    echo '--------------------------------------------------------------------'
    println '--------------------------------------------------------------------'
    // https://github.com/jenkinsci/script-security-plugin/blob/master/src/main/resources/org/jenkinsci/plugins/scriptsecurity/sandbox/whitelists/blacklist
    //println scm.properties
}
/*
println '--------------------------------------------------------------------'
println scm

println '--------------------------------------------------------------------'
def result = [*:extractProperties(scm)]
println result
println '--------------------------------------------------------------------'
*/
