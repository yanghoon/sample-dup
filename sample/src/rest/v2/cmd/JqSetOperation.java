package rest.v2.cmd;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public class JqSetOperation extends JqOperation {
	public String keyword() {
		return "SET";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		Object res = super.run(expr, ctx);
		
		if(res instanceof JsonNode){
			JsonNode in = (JsonNode) res;
			
			if(in.isObject()){
//				v1
//				Iterator<String> iter = in.fieldNames();
//				Stream.generate(iter::next).forEach(k -> ctx.put(k, in.get(k)));

//				v2
//				for(int name : in.fieldNames()){
//					ctx.put(name, in.get(name));
//				}

				Iterator<String> iter = in.fieldNames();
				while(iter.hasNext()){
					String name = iter.next();
					ctx.put(name, in.get(name));
				}
				
				return in;
			}
		}
		
		throw new RuntimeException("Can not support type. " + res.getClass());
	}
}