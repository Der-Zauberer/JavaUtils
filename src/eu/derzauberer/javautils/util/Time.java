package eu.derzauberer.javautils.util;

import java.time.LocalDateTime;

public class Time implements Comparable<Time> {
	
	private int hour;
	private int minute;
	private int second;
	private int millisecond;
	
	public Time() {
		this(0, 0, 0, 0);
	}
	
	public Time(int hour, int minute) {
		this(hour, minute, 0, 0);
	}
	
	public Time(int hour, int minute, int second) {
		this(hour, minute, second, 0);
	}
	
	public Time(int hour, int minute, int second, int millisecond) {
		if (0 > hour || hour > 23) throw new IllegalArgumentException("The value of hour can only be between 0 and 23 (it is " + hour + ")");
		else if (0 > minute || minute > 59) throw  new IllegalArgumentException("The value of minute can only be between 0 and 59 (it is " + minute + ")");
		else if (0 > second || second > 59) throw  new IllegalArgumentException("The value of second can only be between 0 and 59 (it is " + second + ")");
		else if (0 > millisecond || millisecond > 999) throw new IllegalArgumentException("The value of millisecond can only be between 0 and 999 (it is " + second + ")");
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.millisecond = millisecond;
	}
	
	public Time(String string, String pattern) {
		hour = 0;
		minute = 0;
		second = 0;
		millisecond = 0;
		for (int i = 0; i < pattern.length() - 1; i++) {
			if (pattern.charAt(i) == 'h' && pattern.charAt(i + 1) == 'h' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				hour = Integer.parseInt(string.substring(i, i + 2));
			} else if (pattern.charAt(i) == 'm' && pattern.charAt(i + 1) == 'm' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				minute = Integer.parseInt(string.substring(i, i + 2));
			} else if (pattern.charAt(i) == 's' && pattern.charAt(i + 1) == 's' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				second = Integer.parseInt(string.substring(i, i + 2));
			} else if (pattern.charAt(i) == 'm' && pattern.charAt(i + 1) == 's' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				millisecond = Integer.parseInt(string.substring(i, i + 2));
			}
		}
		if (0 > hour || hour > 23) throw new IllegalArgumentException("The value of hour can only be between 0 and 23 (it is " + hour + ")");
		else if (0 > minute || minute > 59) throw  new IllegalArgumentException("The value of minute can only be between 0 and 59 (it is " + minute + ")");
		else if (0 > second || second > 59) throw  new IllegalArgumentException("The value of second can only be between 0 and 59 (it is " + second + ")");
		else if (0 > millisecond || millisecond > 999) throw new IllegalArgumentException("The value of millisecond can only be between 0 and 999 (it is " + second + ")");
	}
		
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getSecond() {
		return second;
	}
	
	public int getMillisecond() {
		return millisecond;
	}
	
	public Time addTime(int hour, int minute) {
		return addTime(hour, minute, 0, 0);
	}
	
	public Time addTime(int hour, int minute, int second) {
		return addTime(hour, minute, second, 0);
	}
	
	public Time addTime(int hour, int minute, int second, int millisecond) {
		int carryMillisecond = calculateTimeCarry(this.millisecond, millisecond, 0, 1000);
		int carrySecond = calculateTimeCarry(this.second, second, carryMillisecond, 60);
		int carryMinute = calculateTimeCarry(this.minute, minute, carrySecond, 60);
		int newMillisecond = calculateTime(this.millisecond, millisecond, 0, 1000);
		int newSecond = calculateTime(this.second, second, carryMillisecond, 60);
		int newMinute = calculateTime(this.minute, minute, carrySecond, 60);
		int newHour = calculateTime(this.hour, hour, carryMinute, 24);
		return new Time(newHour, newMinute, newSecond, newMillisecond);
	}
	
	public Time addTime(Time time) {
		return addTime(time.getHour(), time.getMinute(), time.getSecond(), time.getMillisecond());
	}
	
	private int calculateTime(int oldValue, int newValue, int carry, int maxValue) {
		int tempValue = oldValue + newValue + carry;
		return (tempValue >= 0) ? tempValue % maxValue : maxValue + (tempValue % maxValue);
	}
	
	private int calculateTimeCarry(int oldValue, int newValue, int carry, int maxValue) {
		int tempValue = oldValue + newValue + carry;
		return (tempValue >= 0) ? tempValue / maxValue : (tempValue / maxValue) - 1;
	}
	
	@Override
	public String toString() {
		return integerToString(hour, 2) + ":" + integerToString(minute, 2) + ":" + integerToString(second, 2);
	}
	
	public String toString(String pattern) {
		String output = pattern;
		output = output.replace("hh", integerToString(hour, 2));
		output = output.replace("mm", integerToString(minute, 2));
		output = output.replace("ss", integerToString(second, 2));
		output = output.replace("ms", integerToString(millisecond, 3));
		return output;
	}
	
	@Override
	public int compareTo(Time time) {
		if (hour > time.hour) return 1; else if (time.hour > hour) return - 1;
		else if (minute > time.minute) return 1; else if (time.minute > minute) return - 1;
		else if (second > time.second) return 1; else if (time.second > second) return - 1;
		else if (millisecond > time.millisecond) return 1; else if (time.millisecond > millisecond) return - 1;
		return 0;
	}
	
	public static Time now() {
		LocalDateTime date = LocalDateTime.now();
		return new Time(date.getHour(), date.getMinute(), date.getSecond(), date.getNano() / 1000000);
	}
	
	private static String integerToString(int number, int lenght) {
		String string = Integer.toString(number);
		while (string.length() < lenght) string = "0" + string;
		return string;
	}

}
