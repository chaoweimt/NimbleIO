package com.gifisan.nio.component.protocol.http11.future;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.protocol.future.AbstractIOReadFuture;

public class HttpRequestFutureImpl extends AbstractIOReadFuture implements HttpRequestFuture {

	private String				url;

	private String				requestURI;

	private String				method;

	private Map<String, String>	cookies;

	private Map<String, String>	headers;

	private Map<String, String>	params;

	public HttpRequestFutureImpl(Session session, String url, String method) {
		super(session);
		this.url = url;
		this.method = method;
	}

	public String getServiceName() {
		return requestURI;
	}

	public String getUrl() {
		return url;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}
	
	public void setCookie(String name,String value){
		if(cookies == null){
			cookies = new HashMap<String, String>();
		}
		cookies.put(name, value);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void setHeader(String name,String value){
		if(headers == null){
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void setParam(String name,String value){
		if(params == null){
			params = new HashMap<String, String>();
		}
		params.put(name, value);
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public boolean read() throws IOException {
		return true;
	}
}
