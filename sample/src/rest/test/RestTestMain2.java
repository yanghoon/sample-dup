package rest.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;

import rest.cmd.Operations;

public class RestTestMain2 {

	static final Operations OPS = new Operations("rest.v2.cmd");

	public static void main(String[] args) {
		Map<String, Object> ctx = new LinkedHashMap<>();

		List<String> lines = null;
		for(File file : sample()){
			try {
				System.out.println(">> " + file.getPath());

				lines = IOUtils.readLines(new FileInputStream(file), "UTF-8");
				ctx.clear();

				ctx.put("res_before", null);
				ctx.put("res", null);

				OPS.eval(lines, ctx);

				MapUtils.verbosePrint(System.out, "context", ctx);
			} catch (Exception e) {
				MapUtils.verbosePrint(System.err, "context", ctx);
				e.printStackTrace();
			}
		}
	}

	private static File[] sample(){
		return new File("case/v2/real").listFiles(p -> p.isFile() && !p.getName().startsWith("#"));
	}
}
