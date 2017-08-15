package com.xd.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class XDHttp {
	private BasicCookieStore cookieStore;
	private CloseableHttpClient httpclient;
	private final int timeOut = 15000;
	RequestConfig config;
	// private HttpUriRequest httppost;
	private HttpPost httppost;
	private HttpGet httpget;
	private CloseableHttpResponse response;
	private HttpEntity entity;
	private List<NameValuePair> nvps;
	String returndata = "";
	
	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public CloseableHttpClient getHttpclient() {
		return httpclient;
	}

	public void setHttpclient(CloseableHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	

	public XDHttp() {

		config = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
				.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
		cookieStore = new BasicCookieStore();

		httpclient = HttpClients.custom().setDefaultRequestConfig(config)
				.setDefaultCookieStore(cookieStore).build();

		nvps = new ArrayList<NameValuePair>();
		returndata = "";
	}

	// get xdhttp with a proxy
	public XDHttp(int id) {
		config = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
				.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
		if (id == 1) {
			cookieStore = new BasicCookieStore();
			nvps = new ArrayList<NameValuePair>();
			returndata = "";

			// proxy configure
			String proxyHost = "58.96.185.105";
			int proxyPort = 8445;
			String userName = "";
			String password = "";

			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(new AuthScope(proxyHost,
					proxyPort), new UsernamePasswordCredentials(userName,
					password));

			httpclient = HttpClients.custom().setDefaultRequestConfig(config)
					.setDefaultCredentialsProvider(credentialsProvider)
					.setProxy(proxy).build();
		} else {
			cookieStore = new BasicCookieStore();
			httpclient = HttpClients.custom()
					.setDefaultCookieStore(cookieStore).build();

			nvps = new ArrayList<NameValuePair>();
			returndata = "";
		}
	}

	public HttpPost getHttppost() {
		return httppost;
	}

	public void setHttppost(HttpPost httppost) {
		this.httppost = httppost;
	}

	public HttpGet getHttpget() {
		return httpget;
	}

	public void setHttpget(HttpGet httpget) {
		this.httpget = httpget;
	}

	public byte[] GetDataImage(String url) {
		httpget = new HttpGet(url);
		try {
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			// System.out.println(url + " get result: " +
			// response.getStatusLine());
			// System.out.println("contentype:" +
			// response.getFirstHeader("Content-Type"));
			return EntityUtils.toByteArray(entity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			PrintError(e);
			return GetDataImage(url);

		}

	}

	public String GetData(String url, String charset) {
		if (charset == null)
			charset = "UTF-8";
		httpget = new HttpGet(url);
		try {
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			returndata = EntityUtils.toString(entity, charset);
			if (response.getStatusLine().getStatusCode() > 300
					&& response.getStatusLine().getStatusCode() < 400) {

				String redirection = (response.getFirstHeader("location"))
						.getValue();
				String host = httppost.getURI().getHost();
				if (!host.startsWith("http://")) {
					host = "http://" + host;
				}

				if (redirection.startsWith("/")) {
					redirection = host + redirection;
				}

				System.out.println("get重定向" + redirection);
				returndata = GetData(redirection, charset);
			}

			EntityUtils.consume(entity);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			PrintError(e);
			return GetData(url, charset);

		}

		return returndata;
	}

	public String PostData(String url, String data, String charset) {

		if (charset == null)
			charset = "UTF-8";
		httppost = new HttpPost(url);

		if (data == null || data.split("=").length == 1
				|| data.indexOf('=') < 1) {
			nvps.clear();
			System.out.println("notice: Post data invalid.");
		} else {
			String namepass[] = data.split("&");
			for (int i = 0; i < namepass.length; i++) {
				if (namepass[i].split("=").length == 1) {
					nvps.add(new BasicNameValuePair(namepass[i].split("=")[0],
							""));
				} else {
					nvps.add(new BasicNameValuePair(namepass[i].split("=")[0],
							namepass[i].split("=")[1]));
				}
			}
		}
		try {
			httppost.setEntity((HttpEntity) new UrlEncodedFormEntity(nvps,
					HTTP.UTF_8));
			try {
				response = httpclient.execute(httppost);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("xdhttp execute post error. " + e.getMessage());
				//return "";
			}
			entity = response.getEntity();
			// System.out.println(url + " post result: " +
			// response.getStatusLine());
			returndata = EntityUtils.toString(entity, charset);

			if (response.getStatusLine().getStatusCode() > 300
					&& response.getStatusLine().getStatusCode() < 400) {
				// System.out.println(response.getFirstHeader("location").toString());
				// System.out.println((response.getFirstHeader("location")).getValue());
				// System.out.println((response.getFirstHeader("location")).getName());
				// System.out.println("getHost : " +
				// httppost.getURI().getHost());
				String redirection = (response.getFirstHeader("location"))
						.getValue();
				String host = httppost.getURI().getHost();
				if (!host.startsWith("http://")) {
					host = "http://" + host;
				}

				if (redirection.startsWith("/")) {
					redirection = host + redirection;
				}
				url = url.trim();
				redirection = redirection.trim();
				if (url.endsWith("/")) {
					if (!redirection.endsWith("/"))
						redirection = redirection + "/";
				} else {
					if (redirection.endsWith("/"))
						redirection = redirection.substring(0,
								redirection.length() - 2);
				}
				if (redirection.endsWith("/"))
					redirection.substring(0, redirection.length() - 2);
				if (redirection.equals(url)) {
					System.out.println("post重定向到相同的地址：" + redirection);
					returndata = GetData(redirection, charset);
				} else {
					System.out.println("post重定向：" + redirection);
					returndata = PostData(redirection, data, charset);
				}
			}

			EntityUtils.consume(entity);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			PrintError(e);
			return PostData(url, data, charset);

		} finally {
			nvps.clear();
		}

		return returndata;
	}

	public void PrintError(Exception e) {
		System.out.println("Error: " + e.getMessage()
				+ " And Retry Again after 1s.");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (!IsNetAvailable()) {
			int second = 10;
			System.out.println("wait " + second + " seconds for retest...");
			try {
				Thread.sleep(second * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public static boolean IsNetAvailable() {
		URL url = null;
		try {
			url = new URL("http://baidu.com");
			java.io.InputStream in = url.openStream();
			byte[] b = new byte[4096];
			int length = in.read(b);
			// String srt2=new String(b,"UTF-8");
			in.close();
			if (length == 81) {
				System.out.println("网络连接正常！");
				return true;
			} else {
				System.out.println("网络连接失败2！");
				return false;
			}

		} catch (IOException e) {
			System.out.println("网络连接失败！");
			return false;
		}
	}

	public RequestConfig getConfig() {
		return config;
	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public String getReturndata() {
		return returndata;
	}
	
	
}
