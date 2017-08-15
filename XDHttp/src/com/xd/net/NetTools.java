package com.xd.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetTools {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String s;
		XDHttp xdhttp;
		xdhttp=new XDHttp();
		s= NetTools.GetIP(xdhttp) + "\r\n";
		System.out.println(s);
		s=getIPAddress();
		System.out.println(s);
	}
	
	public static String GetIP(XDHttp xdhttp) {
		if (xdhttp == null)
			xdhttp = new XDHttp();
		try {
			String s = xdhttp.GetData("http://www.baidu.com/s?wd=ip", "GBK");
			Pattern pattern=Pattern.compile("本机IP[^\\d]*((\\d{1,3}\\.){3}\\d{1,3})</span");
			Matcher matcher=pattern.matcher(s);
			if(matcher.find()){
				s=matcher.group(1);
			}
			return s;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return "";
		}
		
	}
	
	/*
	 * get local ip
	 */
	public static String getIPAddress(){
		try {
			String s;
			s= InetAddress.getLocalHost().getHostAddress();
			System.out.println(Calendar.getInstance().getTime() + " " + s);
			return s;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("get ip address error + " + e.getMessage());
			return "";
		}
	}
}
