package rest.cmd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import rest.v2.cmd.Const;
import rest.v2.cmd.JqOperation;

public class Operations {
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
		
		Log log = LogFactory.getLog("org.apache.http.wire");
		System.out.println(log.getClass());
		System.setProperty(".level", "ALL");
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

		for(String ln : lines){
			if(ln.isEmpty() || ln.charAt(0) == '#')
				continue;
			
			System.out.println(" + " + ln);
		
			// find op
			String[] token = ln.split(" ", 2);
			Operation op = this.get(token[0], JqOperation.JQ);
			String expr = JqOperation.JQ.equals(op.keyword()) ? ln : token[1];

			// execute
			Object result = op.run(expr, ctx);

			ctx.put(Const.RESULT, result);
		}
	}
}
