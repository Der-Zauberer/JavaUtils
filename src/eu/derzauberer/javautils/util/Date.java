package eu.derzauberer.javautils.util;

import java.time.LocalDateTime;

public class Date implements Comparable<Date> {
	
	private int year;
	private int month;
	private int day;
	private Time time;
	
	public Date() {
		this(2000, 1, 1, new Time());
	}
	
	public Date(int year, int month, int day) {
		this(year, month, day, new Time());
	}
	
	public Date(int year, int month, int day, Time time) {
		if (1 > month  || month > 12) {
			throw new IllegalArgumentException("The value of month can only be between 1 and 12 (it is " + month + ")");
		} else if (month != 2 && (1 > day || day > 31 || (month < 8 && month % 2 == 0 && day > 30) || (month > 7 && month % 2 == 1 && day > 30))) {
			throw new IllegalArgumentException("The value of day can only be between 1 and 30 or 31 depending on the month (it is " + day + ")");
		} else if (month == 2 && (year % 4 == 0 && day > 29 || year % 4 != 0 && day > 28)) {
			throw new IllegalArgumentException("The value of day in february can only be between 1 and 28 or 29 depending on the year (it is " + day + ")");
		}
		this.year = year;
		this.month = month;
		this.day = day;
		this.time = time;
	}
	
	public Date(String string, String pattern) {
		year = 0;
		month = 1;
		day = 1;
		time = new Time(string, pattern);
		for (int i = 0; i < pattern.length() - 1; i++) {
			if (pattern.charAt(i) == 'Y' && pattern.charAt(i + 1) == 'Y' && pattern.charAt(i + 2) == 'Y' && pattern.charAt(i + 3) == 'Y' && DataUtil.isIntegerString(string.substring(i, i + 4))) {
				year = Integer.parseInt(string.substring(i, i + 4));
			} else if (year == 0 && pattern.charAt(i) == 'Y' && pattern.charAt(i + 1) == 'Y' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				year = Integer.parseInt(string.substring(i, i + 2)) + 2000;
			} else if (pattern.charAt(i) == 'M' && pattern.charAt(i + 1) == 'M' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				month = Integer.parseInt(string.substring(i, i + 2));
			} else if (pattern.charAt(i) == 'D' && pattern.charAt(i + 1) == 'D' && DataUtil.isIntegerString(string.substring(i, i + 2))) {
				day = Integer.parseInt(string.substring(i, i + 2));
			}
		}
		if (1 > month  || month > 12) {
			throw new IllegalArgumentException("The value of month can only be between 1 and 12 (it is " + month + ")");
		} else if (month != 2 && (1 > day || day > 31 || (month < 8 && month % 2 == 0 && day > 30) || (month > 7 && month % 2 == 1 && day > 30))) {
			throw new IllegalArgumentException("The value of day can only be between 1 and 30 or 31 depending on the month (it is " + day + ")");
		} else if (month == 2 && (year % 4 == 0 && day > 29 || year % 4 != 0 && day > 28)) {
			throw new IllegalArgumentException("The value of day in february can only be between 1 and 28 or 29 depending on the year (it is " + day + ")");
		}
	}
	
	public int getYear() {
		return year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getDay() {
		return day;
	}
	
	public Time getTime() {
		return time;
	}
	
	@Override
	public String toString() {
		return integerToString(year, 4) + "-" + integerToString(month, 2) + "-" + integerToString(day, 2);
	}
	
	public String toString(String pattern) {
		String output = pattern;
		output = output.replace("YYYY", integerToString(year, 4));
		output = output.replace("YY", integerToString(year - ((year / 100) * 100), 2));
		output = output.replace("MM", integerToString(month, 2));
		output = output.replace("DD", integerToString(day, 2));
		output = time.toString(output);
		return output;
	}

	@Override
	public int compareTo(Date date) {
		if (year > date.year) return 1; else if (date.year > year) return - 1;
		else if (month > date.month) return 1; else if (date.month > month) return - 1;
		else if (day > date.day) return 1; else if (date.day > day) return - 1;
		return time.compareTo(date.getTime());
	}
	
	public static Date now() {
		LocalDateTime date = LocalDateTime.now();
		return new Date(date.getYear(), date.getMonth().getValue(), date.getDayOfMonth(), Time.now());
	}
	
	private static String integerToString(int number, int lenght) {
		String string = Integer.toString(number);
		while (string.length() < lenght) string = "0" + string;
		return string;
	}

}
