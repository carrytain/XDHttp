package com.xd.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * 利用CMD命令宽带拔号
 */
public class LoalDialTools {
	public static final int delayTime=1500;
	public static void main(String[] args) throws Exception {
		
		cutAdsl("宽带连接");
		Thread.sleep(delayTime);
		// 再连，分配一个新的IP
		connAdsl("宽带连接", "zhanghao", "mima");
	}

	/**
	 * 执行CMD命令,并返回String字符串
	 */
	public static void Dial() throws Exception {
		cutAdsl("宽带连接");
		Thread.sleep(delayTime);
		connAdsl("宽带连接", "17703412506001", "123456");
	}

	public static String executeCmd(String strCmd) throws Exception {
        Process p = Runtime.getRuntime().exec("cmd /c " + strCmd);
        StringBuilder sbCmd = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(p
                .getInputStream(),"GB2312"));
//这里很重要，设置GB2312解决乱码！！！
//如果程序默认编码就是GB2312，可以不写我NetBeans默认用UTF8
        String line;
        while ((line = br.readLine()) != null) {
            sbCmd.append(line + "\n");
        }
        return sbCmd.toString();
/*
//如果整个过程换成这样，就更清楚了。getInputStream是获取最原始的字节流，cmd返回的是以GB2312双字节编码的字节流
	InputStream in = p.getInputStream();
	byte[] b = new byte[1000];
	in.read(b);
	String msg = new String(b,"GB2312");
//用GB2312解释这堆字节，就可以组装成一个正常的String了如果上边不写GB2312，等于这里用UTF8组装，结果一样
 * 
 */
    }

	/**
	 * 连接ADSL
	 */
	public static boolean connAdsl(String adslTitle, String adslName,
			String adslPass) throws Exception {
		System.out.println("正在建立连接.");
		String adslCmd = "rasdial " + adslTitle + " " + adslName + " "
				+ adslPass;
		String tempCmd = executeCmd(adslCmd);
		// 判断是否连接成功
		if (tempCmd.indexOf("已连接") > 0 || tempCmd.indexOf("您已经连接到")>0) {
			System.out.println("已成功建立连接.");
			return true;
		} else {
			System.err.println(tempCmd);
			System.err.println("建立连接失败,重试。");
			cutAdsl(adslTitle);
			Thread.sleep(delayTime);
			return connAdsl(adslTitle,adslName,adslPass);
		}
	}

	/**
	 * 断开ADSL
	 */
	public static boolean cutAdsl(String adslTitle) throws Exception {
		String cutAdsl = "rasdial " + adslTitle + " /disconnect";
		String result = executeCmd(cutAdsl);

		if (result.indexOf("没有连接") != -1) {
			System.err.println(adslTitle + "连接不存在!");
			return false;
		} else {
			System.out.println("连接已断开");
			return true;
		}
	}

	
}