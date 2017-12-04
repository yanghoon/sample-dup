package rest.cmd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;

public class JqOperation implements Operation{
	public String keyword() {
		return "JQ";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		// https://github.com/eiiches/jackson-jq
		JsonQuery q = JsonQuery.compile(expr);
		Scope scope = new Scope(new MapScope(ctx));
		List<JsonNode> result = q.apply(scope, NullNode.getInstance());
		
		if(result != null && result.size() == 1)
			return String.valueOf(result.get(0));

		return result.toString();
	}

	private static class MapScope extends Scope {
		private Map<String, Object> ctx;

		@SuppressWarnings("deprecation")
		public MapScope(Map<String, Object> ctx) {
			this.ctx = ctx;
		}

		public JsonNode getValue(String name) {
			Object val = ctx.get(name);
			
			if(val instanceof JsonNode)
				return (JsonNode) val;
			
			if(val == null)
				return super.getValue(name);

			try {
				return getObjectMapper().readTree(String.valueOf(val));
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}
