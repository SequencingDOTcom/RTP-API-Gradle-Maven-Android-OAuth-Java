package com.sequencing.androidoauth.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for common HTTP request processing routines
 */
public class HttpHelper
{
	private static final Logger log = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * Basic method for executing HTTP request
	 * @param request request object
	 * @param headers additional request headers
	 * @return String server reply
	 * @throws IOException
	 */
	private static int getCode(HttpRequestBase request, Map<String, String> headers) throws IOException
	{
		if (headers != null) {
			for (Map.Entry<String, String> h : headers.entrySet())
				request.addHeader(h.getKey(), h.getValue());
		}
		
		HttpResponse response = getHttpClient().execute(request);
		
		return response.getStatusLine().getStatusCode();
	}
	
	/**
	 * Returns result of POST request
	 * @param uri request URL
	 * @param headers additional request headers
	 * @param params additional request parameters
	 * @return String server reply
	 */
	public static int doPost(String uri, Map<String, String> headers, Map<String, String> params)
	{
		try {
			HttpPost post = new HttpPost(uri);

			if (params != null) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				for (Map.Entry<String, String> p : params.entrySet())
					pairs.add(new BasicNameValuePair(p.getKey(), p.getValue()));

				post.setEntity(new UrlEncodedFormEntity(pairs));
			}

			return getCode(post, headers);
		} 
		catch (IOException e) {
			log.debug("Error executing HTTP POST request to " + uri, e);
		}
		catch (ParseException e) {
			log.debug("Error executing HTTP POST request to " + uri, e);
		}
		return -1;
	}

	private static HttpClient getHttpClient()
	{
		return HttpClientBuilder.create().build();
	}
}