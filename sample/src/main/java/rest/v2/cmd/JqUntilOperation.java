package rest.v2.cmd;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;

import net.thisptr.jackson.jq.exception.JsonQueryException;
import rest.cmd.LoopOperation;
import rest.util.LogUtil.Log;

public class JqUntilOperation extends JqOperation implements LoopOperation {
	public String keyword() {
		return "UNTIL";
	}
	
	public JqUntilOperation(){
		
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		Object res = null;
		boolean test = false;

		try {
			res = super.run(expr, ctx);
			
			if(res instanceof JsonNode){
				JsonNode node = JsonNode.class.cast(res);

				if(node.isBoolean())
					test = node.asBoolean();
				if(node.isNumber())
					test = node.asDouble(0.0d) != 0.0d;
				if(node.isTextual())
					test = !node.asText().isEmpty();

				if(node.isObject() || node.isArray())
					test = ContainerNode.class.cast(node).size() != 0;
			}
		} catch(JsonQueryException e){

		}

		Log.formatln(ctx, "[test=%s, val=%s, class=%s]", test, res, res.getClass().getSimpleName());
		
		return test ? Const.LOOP_TEST_TRUE : Const.LOOP_TEST_FALSE;
	}

	public boolean isFrom() {
		return true;
	}

	public boolean isTo() {
		return false;
	}
}