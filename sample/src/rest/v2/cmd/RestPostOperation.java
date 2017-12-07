package rest.v2.cmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;
import rest.cmd.Operation;
import rest.util.JsonUtils;
import rest.v2.cmd.JqOperation.MapScope;

public class RestPostOperation implements Operation {
	private Pattern p = Pattern.compile("(\\{([^\\}]*)\\})");

	private HttpClient client = HttpClientBuilder.create().build();

	public String keyword() {
		return "POST";
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
//		setQuerys(builder, ctx.get(Const.HTTP_QUERYS));

		HttpPost post = new HttpPost(builder.build());
		String body = makeBody(ctx.get(Const.HTTP_QUERYS));

		setHeaders(post, ctx.get(Const.HTTP_HEADERS));
		post.setEntity(new StringEntity(body));
		
		System.out.println(Const.LOG_LPAD + post);

		HttpResponse res = null;
		try {
			res = client.execute(post);
			
			if(res.getStatusLine().getStatusCode() > 300){
				throw new RuntimeException("Fail to POST.");
			}
			
			if( post.getURI().getFragment() != null ){
				ctx.put(post.getURI().getFragment(), res);
			}
			
			return EntityUtils.toString(res.getEntity());
		} catch(HttpResponseException e){
			for(Header h : post.getAllHeaders()){
				System.out.println(Const.LOG_LPAD + h.toString());
			}

			if(res != null){
				System.out.println();
				System.out.println(Const.LOG_LPAD + res.getStatusLine());
				System.out.println(Const.LOG_LPAD + Arrays.toString(res.getAllHeaders()));
				System.out.println(Const.LOG_LPAD + EntityUtils.toString(res.getEntity()));
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

	private String makeBody(Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}