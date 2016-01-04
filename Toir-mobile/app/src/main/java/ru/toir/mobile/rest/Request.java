package ru.toir.mobile.rest;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.toir.mobile.rest.RestClient.Method;

/**
 * @author Dmitriy Logachov
 * 
 */
public class Request {
	private URI mUri;
	private Method mMethod;
	private byte[] mBody;
	private Map<String, List<String>> mHeaders;

	public Request(Method method, URI uri, Map<String, List<String>> headers, byte[] body) {
		mUri = uri;
		mMethod = method;
		mBody = body;
		mHeaders = headers;
	}

	/**
	 * @return the mUri
	 */
	public URI getUri() {
		return mUri;
	}
	
	/**
	 * @return the mMethod
	 */
	public Method getMethod() {
		return mMethod;
	}

	/**
	 * @return the mBody
	 */
	public byte[] getBody() {
		return mBody;
	}

	/**
	 * @return the mHeaders
	 */
	public Map<String, List<String>> getHeaders() {
		return mHeaders;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, List<String> value) {

		if (mHeaders == null) {
			mHeaders = new HashMap<String, List<String>>();
		}
		mHeaders.put(key, value);
	}
}
