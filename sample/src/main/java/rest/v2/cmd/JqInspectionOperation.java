package rest.v2.cmd;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

public class JqInspectionOperation extends JqOperation {
	public String keyword() {
		return "INSPEC";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		Object res =  null;

		if(expr != null && !expr.isEmpty()){
			res = super.run(expr, ctx);
			System.out.format("res = %s\n", res);
		}

		MapUtils.verbosePrint(System.out, "context", ctx);

		return res;
	}
}