package com.patrickzhong.blockpoints;

public class CalendarUtil {
	
	private static long millisInDay = 24 * 60 * 60 * 1000;
	
	public static long getDay(){
		return System.currentTimeMillis() / millisInDay;
	}
	
	public static boolean sameWeek(long one, long two){
		return two - one < 7;
	}

}
