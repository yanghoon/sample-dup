package rest.v2.cmd;

import java.util.Map;

import rest.cmd.LoopOperation;

public class JqLoopEndOperation implements LoopOperation{
	public String keyword() {
		return "END";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		return null;
	}

	public boolean isFrom() {
		return false;
	}

	public boolean isTo() {
		return true;
	}
}