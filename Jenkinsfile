def extractProperties(obj) {
    obj.getClass()
       .declaredFields
       .findAll { !it.synthetic }
       .collectEntries { field ->
           [field.name, obj."$field.name"]
       }
}

def result = [*:extractProperties(scm)]

println result
