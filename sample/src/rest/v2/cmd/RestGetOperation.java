package rest.v2.cmd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import rest.cmd.Operation;

public class RestGetOperation implements Operation {
	private Pattern p = Pattern.compile("(\\{([^\\}]*)\\})");

	private HttpClient client = HttpClientBuilder.create().build();

	public String keyword() {
		return "GET";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		if(true)
			throw new RuntimeException("Unsupported Operation.");
		
		Matcher m = p.matcher(expr);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, String.valueOf(ctx.get(m.group(2))) );
		}
		m.appendTail(sb);
		
		URIBuilder builder = new URIBuilder(sb.toString());
		setQuerys(builder, ctx.get(Operation.HTTP_QUERYS));

		HttpGet get = new HttpGet(builder.build());
		setHeaders(get, ctx.get(Operation.HTTP_HEADERS));
		
		System.out.println(get);

		return client.execute(get, new BasicResponseHandler());
	}

	private void setHeaders(HttpUriRequest req, Object obj) {
		if(obj instanceof String){
			try {
				obj = new ObjectMapper().readValue(obj.toString(), HashMap.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(obj instanceof Map){
			Map<Object, Object> headers = (Map<Object, Object>) obj;
			
			for(Object key : headers.keySet())
				req.setHeader(key.toString(), String.valueOf(headers.get(key)) );
		}
	}

	private void setQuerys(URIBuilder builder, Object obj) {
		if(obj instanceof String){
			try {
				obj = new ObjectMapper().readValue(obj.toString(), HashMap.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(obj instanceof Map){
			Map<Object, Object> querys = (Map<Object, Object>) obj;
			
			for(Object key : querys.keySet())
				builder.addParameter(key.toString(), String.valueOf(querys.get(key)) );
		}
	}
}