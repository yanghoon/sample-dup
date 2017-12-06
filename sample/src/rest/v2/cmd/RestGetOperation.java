package rest.v2.cmd;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import rest.cmd.Operation;
import rest.util.JsonUtils;
import rest.v2.cmd.JqOperation.MapScope;

public class RestGetOperation implements Operation {
	private Pattern p = Pattern.compile("(\\{([^\\}]*)\\})");

	private HttpClient client = HttpClientBuilder.create().build();

	public String keyword() {
		return "GET";
	}

	public Object run(String expr, Map<String, Object> ctx) throws Exception {
		expr = buildUrl(expr, ctx);
		
		Matcher m = p.matcher(expr);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
			m.appendReplacement(sb, String.valueOf(ctx.get(m.group(2))) );
		}
		m.appendTail(sb);
		
		URIBuilder builder = new URIBuilder(sb.toString());
		setQuerys(builder, ctx.get(Const.HTTP_QUERYS));

		HttpGet get = new HttpGet(builder.build());
		setHeaders(get, ctx.get(Const.HTTP_HEADERS));
		
		System.out.println(Const.LOG_LPAD + get);

		try {
			Object res = client.execute(get, new BasicResponseHandler());
			
			if( get.getURI().getFragment() != null ){
				ctx.put(get.getURI().getFragment(), res);
			}
			
			return res;
		} catch(HttpResponseException e){
			for(Header h : get.getAllHeaders()){
				System.out.println(Const.LOG_LPAD + h.toString());
			}
			
			throw e;
		}
	}
	
	private String buildUrl(String expr, Map<String, Object> ctx) throws JsonQueryException{
		if(StringUtils.startsWith(expr, "{")){
			Matcher m = p.matcher(expr);
			StringBuffer sb = new StringBuffer();
			if(m.find()){
				JsonQuery jq = JsonQuery.compile(m.group(2));
				List<JsonNode> res = jq.apply(new MapScope(ctx), NullNode.getInstance());
				m.appendReplacement(sb, res.get(0).textValue());
			}
			m.appendTail(sb);
			
			expr = sb.toString();
		}	

		if(!StringUtils.startsWith(expr, "http")){
			String base = JsonUtils.str(ctx.get(Const.HTTP_SITE), "http://localhost");
			expr = base + expr;
		}	
		
		return expr;
	}

	private void setHeaders(HttpUriRequest req, Object obj) {
		if(obj instanceof String){
			try {
				obj = new ObjectMapper().readValue(obj.toString(), HashMap.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(obj instanceof ObjectNode){
			obj = new ObjectMapper().convertValue(obj, HashMap.class);
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

		if(obj instanceof ObjectNode){
			obj = new ObjectMapper().convertValue(obj, HashMap.class);
		}

		if(obj instanceof Map){
			Map<Object, Object> querys = (Map<Object, Object>) obj;
			
			for(Object key : querys.keySet())
				builder.addParameter(key.toString(), String.valueOf(querys.get(key)) );
		}
	}
}