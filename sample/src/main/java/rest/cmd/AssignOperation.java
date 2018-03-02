package rest.cmd;

import java.util.Map;

public class AssignOperation implements Operation {
	public String keyword() {
		return "SET";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		Object val = ctx.get(Operation.RESULT);
		
		if(val instanceof String && ((String) val).charAt(0) == '"')
			val = val.toString().replaceAll("\"", "");
		
		ctx.put(expr, val);
		return val;
	}

}
