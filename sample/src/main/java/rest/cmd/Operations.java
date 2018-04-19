package rest.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.reflections.Reflections;

import rest.util.LogUtil.Log;
import rest.v2.cmd.Const;
import rest.v2.cmd.JqOperation;

public class Operations {
//	private int loopMaxCount = Integer.MAX_VALUE;
//	private int loopMaxCount = 5;

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
		for(int pc=0; pc<lines.size(); pc++){
			ln = lines.get(pc);

			if(ln.isEmpty() || ln.charAt(0) == '#')
				continue;
			
			Log.println(" + ", ln);
		
			// find op
			String[] token = ln.trim().split(" ", 2);
			String expr = token.length == 1 ? "" : token[1]; // support op with no args

			Operation op = this.get(token[0], JqOperation.JQ);
			expr = JqOperation.JQ.equals(op.keyword()) && !JqOperation.JQ.equals(token[0]) ? ln : expr; // run jq-operation as default
			
			// detect & merge multi line
			expr = expr.trim();
			if(expr.startsWith(MULTI_LINE)){
				StringBuffer sb = new StringBuffer(expr.substring(MULTI_LINE_LEN));
				while(pc < lines.size()){
					ln = lines.get(++pc);
					Log.println(" + ", ln);

					sb.append(" ").append(ln.trim());
					
					if(ln.endsWith(MULTI_LINE)){
						sb.setLength(sb.length() - MULTI_LINE_LEN);
						expr = sb.toString();
						break;
					}
				}
				
				//ERR:: no more input!!
				if(lines.size() <= pc){
					ctx.put("err_data", sb.toString());
					throw new IllegalArgumentException("There is no end of multi line command.");
				}
			}
			
			// execute loop
			if(op instanceof LoopOperation){
				LoopOperation opCond = LoopOperation.class.cast(op);
				
				if(!opCond.isFrom())
					throw new RuntimeException("Invalid Loop Operation.");
				
				int from = pc;
				int to = pc + 1;
				for(; to<lines.size(); to++){
					String opStr = lines.get(to).trim().split(" ", 2)[0];

					if(this.get(opStr) instanceof LoopOperation){
						LoopOperation opEnd = LoopOperation.class.cast(this.get(opStr));
						if(opEnd.isTo()){
							break;
						} else {
							lines.subList(from + 1, to + 1).forEach(l -> Log.println(" + ", l));
							throw new RuntimeException("Can not support nested loop.");
						}
					}
				}
				
				if(to == lines.size())
					throw new RuntimeException("There is no end of loop.");
				
				// loop
				final int MAX = NumberUtils.toInt(String.valueOf(ctx.get(Const.LOOP_MAX_COUNT)), Integer.MAX_VALUE);
				List<String> loopLines = lines.subList(from + 1, to);

				ctx.put(Const.INDENT_LENGTH, ln.indexOf(token[0]));
				Object rt = op.run(expr, ctx);

				int k = 0;
				boolean test = Const.LOOP_TEST_TRUE.equals(rt);
				for(; test && k<MAX; k++){
					this.eval(loopLines, ctx);
					
					ctx.put(Const.INDENT_LENGTH, ln.indexOf(token[0]));
					Log.println(" + ", ln);
					rt = op.run(expr, ctx);
					test = Const.LOOP_TEST_TRUE.equals(rt);
				}
				
				if(test && k == MAX)
					Log.formatln(ctx, "STOP. The loop count is MAX(%s=%d)", Const.LOOP_MAX_COUNT, MAX);
				Log.println(" + ", lines.get(to));
				pc = to;
				continue;
			}

			// execute
			ctx.put(Const.INDENT_LENGTH, ln.indexOf(token[0]));

			Object result = op.run(expr, ctx);

			ctx.put("res_before", ctx.get(Const.RESULT));

			if(result != null)
				ctx.put(Const.RESULT, result);
		}
	}
}
