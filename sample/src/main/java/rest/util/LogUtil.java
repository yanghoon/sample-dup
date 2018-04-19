package rest.util;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import rest.v2.cmd.Const;

public class LogUtil {
	public static class Log extends LogUtil {};
	
	public static void println(Map<String, Object> ctx, Object... msg){
		StringBuilder sb = create(ctx);
		for(Object m : msg)
			sb.append(String.valueOf(m));
		
		System.out.println(sb.toString());
	}

	public static void formatln(Map<String, Object> ctx, String fmt, Object... msg){
		format(ctx, fmt + "\n", msg);
	}

	public static void format(Map<String, Object> ctx, String fmt, Object... msg){
		StringBuilder sb = create(ctx);
		sb.append(fmt);
		
		System.out.format(sb.toString(), msg);
	}

	public static void println(Object... msg) {
		StringBuilder sb = new StringBuilder();
		for(Object m : msg)
			sb.append(String.valueOf(m));
		
		System.out.println(sb.toString());
	}
	
	private static StringBuilder create(Map<String, Object> ctx){
		Object len = ctx.get(Const.INDENT_LENGTH);
		StringBuilder sb = new StringBuilder();

		char[] indent = new char[toInt(len)];
		Arrays.fill(indent, ' ');

		sb.append(indent);
		sb.append(Const.LOG_LPAD_DEFAULT);
		
		return sb;
	}
	
	private static int toInt(Object i){
		return NumberUtils.toInt(String.valueOf(i));
	}
}
