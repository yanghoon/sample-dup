package rest.cmd;

import java.util.Map;

public interface Operation {
	public static final String RESULT = "result";
	public static final String HTTP_HEADERS = "headers";
	public static final String HTTP_QUERYS = "querys";

	public static final Operation UNKNOWN = new Operation() {
		public Object run(String expr, Map<String, Object> ctx) throws Exception {
			System.out.println("Unknown Command");
			return null;
		}
		
		public String keyword() {
			return "UNKNOWN";
		}
	};


	public String keyword();
	public Object run(String expr, Map<String, Object> ctx) throws Exception;
}
