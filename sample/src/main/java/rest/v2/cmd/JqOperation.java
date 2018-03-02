package rest.v2.cmd;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.Lists;

import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import net.thisptr.jackson.jq.internal.javacc.JsonQueryParser;
import rest.cmd.Operation;

public class JqOperation implements Operation {
	public static final String JQ = "JQ";

	public String keyword() {
		return JQ;
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		// https://github.com/eiiches/jackson-jq
		JsonQuery q = JsonQueryParser.compile(expr);

		Scope scope = new MapScope(ctx);
		
		List<JsonNode> result = q.apply(scope, NullNode.getInstance());
		
		if(result != null && result.size() == 1)
			return result.get(0);
		
		return result;
	}
	
	public static class MapScope extends Scope {
		private Map<String, Object> ctx;

		@SuppressWarnings("deprecation")
		public MapScope(Map<String, Object> ctx) {
			this.ctx = ctx;
			
			setup();
		}
		
		@Deprecated
		public void setup(){
			this.addFunction("set", new Function() {
				public List<JsonNode> apply(Scope scope, List<JsonQuery> args, JsonNode in) throws JsonQueryException {
					scope.setValue(args.get(0).toString(), in);
					return Lists.newArrayList(in);
				}
			});

			this.addFunction("base64", 0, new Function() {
				public List<JsonNode> apply(Scope scope, List<JsonQuery> args, JsonNode in) throws JsonQueryException {
					String source = in.textValue();
					byte[] encoded = Base64.encodeBase64(source.getBytes());
					TextNode node = new TextNode(new String(encoded));

//					return Lists.newArrayList(node);
					return Collections.<JsonNode> singletonList(node);
				}
			});
		}
		
		public void setValue(String name, JsonNode value) {
			ctx.put(name, value);
		};

		public JsonNode getValue(String name) {
			Object val = ctx.get(name);

			if(val == null)
				return super.getValue(name);
			
			if(val instanceof JsonNode)
				return (JsonNode) val;
			
			if(val instanceof String){
				try {
					val = getObjectMapper().readTree(val.toString());
					ctx.put(name, val);
					return (JsonNode) val;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return getObjectMapper().convertValue(val, JsonNode.class);
		}
	}
}
