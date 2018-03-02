package rest.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.reflections.Reflections;

import rest.v2.cmd.Const;
import rest.v2.cmd.JqOperation;

public class Operations {
	private final String MULTI_LINE = "\"\"";
	private final int MULTI_LINE_LEN = MULTI_LINE.length();

	private Map<String, Operation> inst = new HashMap<>();

	public Operations(String packaze) {
		Reflections reflections = new Reflections(packaze);    
		for(Class<? extends Operation> clazz : reflections.getSubTypesOf(Operation.class)){
			try {
				Constructor<? extends Operation> cons = ConstructorUtils.getAccessibleConstructor(clazz);

				if(cons == null){
					System.err.format("Can not create instance for '%s'.\n", clazz.getName());
					continue;
				}

				Operation op = ConstructorUtils.invokeConstructor(clazz);
				inst.put(op.keyword(), op);

				System.out.format("Load operation :: %s / %s\n", op.keyword(), clazz.getName());
			} catch (InstantiationException
					| IllegalAccessException
					| NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Operation get(String... keys){
		for(String k : keys){
			Operation op = inst.get(k);
			if(op != null)
				return op;
		}

		return Operation.UNKNOWN;
	}
	
	public void eval(List<String> lines, Map<String, Object> ctx) throws Exception{
		if(ctx == null)
			ctx = new HashMap<>();

		String ln = "";
		for(int i=0; i<lines.size(); i++){
			ln = lines.get(i);

			if(ln.isEmpty() || ln.charAt(0) == '#')
				continue;
			
			System.out.println(" + " + ln);
		
			// find op
			String[] token = ln.split(" ", 2);
			String expr = token.length == 1 ? "" : token[1]; // support op with no args

			Operation op = this.get(token[0], JqOperation.JQ);
			expr = JqOperation.JQ.equals(op.keyword()) && !JqOperation.JQ.equals(token[0]) ? ln : expr; // run jq-operation as default
			
			// detect & merge multi line
			expr = expr.trim();
			if(expr.startsWith(MULTI_LINE)){
				StringBuffer sb = new StringBuffer(expr.substring(MULTI_LINE_LEN));
				while(i < lines.size()){
					ln = lines.get(++i);
					System.out.println(" + " + ln);

					sb.append(" ").append(ln.trim());
					
					if(ln.endsWith(MULTI_LINE)){
						sb.setLength(sb.length() - MULTI_LINE_LEN);
						expr = sb.toString();
						break;
					}
				}
				
				//ERR:: no more input!!
				if(lines.size() <= i){
					ctx.put("err_data", sb.toString());
					throw new IllegalArgumentException("There is no end of multi line command.");
				}
			}

			// execute
			Object result = op.run(expr, ctx);

			ctx.put("res_before", ctx.get(Const.RESULT));

			if(result != null)
				ctx.put(Const.RESULT, result);
		}
	}
}
