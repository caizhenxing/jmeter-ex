package org.apache.jmeter.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Dater {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static synchronized String getCNTime(long time){
		return format.format(new Date(time));
	}
	public static void main(String[] args) {
		format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		String f = format.format(new Date(System.currentTimeMillis()));
		System.out.println(f);
	}
	public static long getCNTime(String time) throws ParseException {
		return format.parse(time).getTime();
	}
	public static String getSampleCNTime(long time) {
		// TODO Auto-generated method stub
		return simpleFormat.format(new Date(time));
	}
}
