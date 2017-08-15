package com.xd.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class Union3GTools {

	public static void main(String args[])
	{
		ChangeIP();
		GetStatus();
	}
	public static void ChangeIP()
	{
		Dial(0);
		try {
			Thread.sleep(2000);
			while(!GetStatus().equals("901"))
			{
				Thread.sleep(2000);
				Dial(1);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void Dial(int action)
	{
		// 1 is dial up , 0 is dial downhttp://192.168.1.1/api/dialup/dial
		HttpClient httpclient=get3GHttpClient();
		HttpPost httppost = new HttpPost("http://192.168.1.1/api/dialup/dial");
		String data= "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><Action>" + action + "</Action></request>";
		HttpEntity entity = new StringEntity(data, "UTF-8"); 
		httppost.setEntity(entity);
		
		HttpResponse response;
			try {
				response = httpclient.execute(httppost);
				entity = response.getEntity();
				//System.out.println(" post result: "	+ response.getStatusLine());
				String returndata = EntityUtils.toString(entity,"UTF-8");
				System.out.println(returndata);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public static String GetStatus()
	{
		XDHttp xdhttp=new XDHttp();
		String s=xdhttp.GetData("http://192.168.1.1/api/monitoring/status",null);
		s=s.substring(s.indexOf("ConnectionStatus>")+"ConnectionStatus>".length());
		s=s.substring(0,3);
		System.out.println(s);
		return s;
	}
	public static HttpClient get3GHttpClient()
	{
		BasicHeader h1=new BasicHeader("Host","192.168.1.1");
		BasicHeader h2=new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		BasicHeader h3=new BasicHeader("Accept","*/*");
		BasicHeader h4=new BasicHeader("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		BasicHeader h5=new BasicHeader("Accept-Encoding","gzip, deflate");
		BasicHeader h6=new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		BasicHeader h7=new BasicHeader("__RequestVerificationToken",GetToken());
		BasicHeader h8=new BasicHeader("X-Requested-With","XMLHttpRequest");
		BasicHeader h9=new BasicHeader("Referer","http://192.168.1.1/html/home.html");
		BasicHeader h10=new BasicHeader("Content-Length","75");
		BasicHeader h11=new BasicHeader("Connection","keep-alive");
		BasicHeader h12=new BasicHeader("Pragma","no-cache");
		BasicHeader h13=new BasicHeader("Cache-Control","no-cache");
		ArrayList<Header> defaultHeaders=new ArrayList();
		
		defaultHeaders.add(h1);
		defaultHeaders.add(h2);
		defaultHeaders.add(h3);
		defaultHeaders.add(h4);
		defaultHeaders.add(h5);
		defaultHeaders.add(h6);
		defaultHeaders.add(h7);
		defaultHeaders.add(h8);
		defaultHeaders.add(h9);
	//	defaultHeaders.add(h10);
		defaultHeaders.add(h11);
		defaultHeaders.add(h12);
		defaultHeaders.add(h13);
		
		
		HttpClient httpclient=HttpClients.custom().setDefaultHeaders(defaultHeaders).build();
		return httpclient;
	}
	public static String GetToken()
	{
		XDHttp xdhttp=new XDHttp();
		String s=xdhttp.GetData("http://192.168.1.1/api/webserver/token",null);
		s=s.substring(s.indexOf("token>")+ 6);
		s=s.substring(0,s.indexOf("<"));
		System.out.println(s);
		return s;
	}
}
