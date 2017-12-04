package rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import rest.cmd.Operation;

public class RestTestMain {
	
	static Map<String, Operation> inst = new HashMap<>();
	
	static {
		Reflections reflections = new Reflections("rest");    
		for(Class<? extends Operation> clazz : reflections.getSubTypesOf(Operation.class)){
			try {
				Constructor<? extends Operation> cons = ConstructorUtils.getAccessibleConstructor(clazz);

				if(cons == null){
					System.err.format("Can not create instance for '%s'.\n", clazz.getName());
					continue;
				}

				Operation op = ConstructorUtils.invokeConstructor(clazz);
				inst.put(op.keyword(), op);
			} catch (InstantiationException
					| IllegalAccessException
					| NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		Log log = LogFactory.getLog("org.apache.http.wire");
		System.out.println(log.getClass());
//		System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
//		System.setProperty("org.apache.http.wire.level", "FINEST");
		System.setProperty(".level", "ALL");
	}

	public static void main(String[] args) throws Exception {
		File[] files = new File("case").listFiles(p -> !p.getName().startsWith("#"));

		Map<String, Object> ctx = new HashMap<>();

		List<String> lines = null;
		for(File file : files){
			lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");
			get(lines, ctx);
		}
		
		MapUtils.verbosePrint(System.out, "context", ctx);
	}

	public static void get(List<String> lines, Map<String, Object> ctx) throws Exception{
		for(String ln : lines){
			if(ln.isEmpty() || ln.charAt(0) == '#')
				continue;
		
			String[] token = ln.split(" ", 2);
			Optional<Operation> op = Optional.ofNullable(inst.get(token[0]));
			Object result = op.orElse(Operation.UNKNOWN).run(token[1], ctx);

//			if(result != null)
			ctx.put(Operation.RESULT, result);
		}
	}
}
