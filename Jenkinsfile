echo '--------------------------------------------------------------------'
def extractProperties(obj) {
    obj.getClass()
       .declaredFields
       .findAll { !it.synthetic }
       .collectEntries { field ->
           [field.name, obj."$field.name"]
       }
}

node {
    echo '--------------------------------------------------------------------'
    
    println '--------------------------------------------------------------------'
}
/*
println '--------------------------------------------------------------------'
println scm

println '--------------------------------------------------------------------'
def result = [*:extractProperties(scm)]
println result
println '--------------------------------------------------------------------'
*/
