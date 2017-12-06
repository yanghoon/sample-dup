package rest.util;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {
	public static String str(Object val, String defaultVal){
		if(val instanceof String)
			return (String) val;
		
		if(val instanceof JsonNode)
			return JsonNode.class.cast(val).textValue();

		return defaultVal;
	}
}
