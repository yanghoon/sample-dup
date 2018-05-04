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
    println extractProperties(scm)
}
/*
println '--------------------------------------------------------------------'
println scm

println '--------------------------------------------------------------------'
def result = [*:extractProperties(scm)]
println result
println '--------------------------------------------------------------------'
*/
