package com.example.demo.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * 时间工具类
 * 
 */
public class DateTimeUtils {

	private static String FORMATTIME_DATE = "yyyy-MM-dd";
	private static String FORMATTIME_DATETIME = "yyyy-MM-dd HH:mm:ss";
	private static String FORMATTIME_DATE_ZH = "yyyy年MM月dd日";
	private static String FORMATTIME = "yyyyMMdd";
	private static String FORMATTIME_DATETIME_SEC = "yyyyMMddHHmmss";
	private static String FORMATTIME_DATE_MIN = "yyyyMMddHHmm";
	private static String FORMATTIME_DATE_HOR = "yyyy-MM-dd HH";
	
	private static int OPTION_TIME = 1325347200;
	
	/**
	 * 锁对象
	 */
	private static final Object lockObj = new Object(); 
	
	/**
	 * 存放不同的日期模板格式的sdf的Map
	 */
	private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
	
	
	/**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> local = sdfMap.get(pattern);
 
        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (local == null) {
            synchronized (lockObj) {
            	local = sdfMap.get(pattern);
                if (null == local) {
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                	local = new ThreadLocal<SimpleDateFormat>() {
 
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, local);
                }
            }
        }
 
        return local.get();
    }

	public static String getDateTime(Timestamp sourceDate) {
		if (sourceDate == null) {
			return "";
		}
		return getSdf(FORMATTIME_DATETIME).format(sourceDate);
	}

	public static String getDateTime(Date sourceDate) {
		if (sourceDate == null) {
			return "";
		}
		return getSdf(FORMATTIME_DATETIME).format(sourceDate);
	}

	public static String getCurrDate(Date sourceDate) {
		if (sourceDate == null) {
			return "";
		}
		return getSdf(FORMATTIME_DATE).format(sourceDate);
	}
	
	public static Date getSysNowDate() {
		return new Date();
	}

	/**
	 * 取得当前时间
	 * 
	 * @return Timestamp类型
	 */
	public static Timestamp getTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 取得当前时间
	 * 
	 * @return yyyy-MM-dd 格式
	 */
	public static String getCurrentDate() {
		return getSdf(FORMATTIME_DATE).format(new java.util.Date());
	}

	/**
	 * 取得当前时间
	 * 
	 * @return yyMMdd格式
	 */
	public static String getCurrentSequnceDate() {
		String today = null;
		today = getSdf(FORMATTIME_DATE).format(new java.util.Date());
		today = today.replace("-", "");
		today = today.substring(2, today.length());
		return today;
	}

	/**
	 * 取得当前时间
	 * 
	 * @return yyyy-MM-dd HH:mm:ss 格式
	 */
	public static String getCurrentTime() {
		return getSdf(FORMATTIME_DATETIME).format(new java.util.Date());
	}

	/**
	 * 取得前一天日期
	 * 
	 * @return yyyy-MM-dd 格式
	 */
	public static String getYesterDay() {
		return getSdf(FORMATTIME_DATE).format(new java.util.Date(System
				.currentTimeMillis() - 1000 * 3600 * 24));
	}

	/**
	 * 取得当前时间
	 * 
	 * @return yyyyMMddHHmmss 格式
	 */
	public static String getCompactTime() {
		return getSdf(FORMATTIME_DATETIME_SEC).format(new java.util.Date());
	}
	
	/**
	 * 取得当前时间
	 * 
	 * @return yyyyMMddHHmmss 格式
	 */
	public static String getCompactTime(Date date) {
		return getSdf(FORMATTIME_DATETIME_SEC).format(date);
	}

	/**
	 * 取得当前时间
	 * 
	 * @return yyyyMMddHHmm 格式
	 */
	public static String getTimeByMinute() {
		return getSdf(FORMATTIME_DATE_MIN).format(new java.util.Date());
	}
	
	/**
	 * 取得当前时间
	 * 
	 * @return yyyy-MM-dd HH 格式
	 */
	public static String getTimeByHour() {
		return getSdf(FORMATTIME_DATE_HOR).format(new java.util.Date());
	}
	
	/**
	 * 取得当前日期
	 * 
	 * @return yyyyMMdd 格式
	 */
	public static String getCompactDate() {
		return getSdf(FORMATTIME).format(new java.util.Date());
	}
	
	/**
	 * 取得当前日期
	 * 
	 * @return yyyyMMdd 格式
	 * @throws ParseException 
	 */
	public static Date getCompactDate(String dateString) throws ParseException {
		return getSdf(FORMATTIME).parse(dateString);
	}
	
	/**
	 * 取得当前日期
	 * 
	 * @return yyyyMMdd 格式
	 */
	public static String getCompactDate(Date date) {
		return getSdf(FORMATTIME).format(date);
	}
	
	public static String getTimeInMillis() {
		Calendar cal = Calendar.getInstance();
		return Long.toString(cal.getTimeInMillis());
	}
	
	/**
	 * String转换成Date类型日期
	 * yyyy-MM-dd
	 * @param strDate
	 * @return
	 */
	public static Date getStringToDate(String strDate) {
		if (strDate == null) {
			return null;
		}
		ParsePosition pos = new ParsePosition(0);
		if (strDate.length() == 10) {
			return getSdf(FORMATTIME_DATE).parse(strDate, pos);
		}
		return getSdf(FORMATTIME_DATETIME).parse(strDate, pos);
	}

	/**
	 * 获取时间跟“2012-1-1 0：0：0”之间的差
	 * 
	 * @param date
	 * @return
	 */
	public static int getTimeToSecondFrom2012(Date date) {
		if (date == null) {
			return 0;
		}
		return (int) (date.getTime() / 1000 - OPTION_TIME);

	}

	public static long getStringDateToTimeLong(String strDate) {
		if (strDate == null) {
			return 0;
		}

		if (strDate.length() >= 10) {
			// String year = "20"+strDate.substring(0,2);
			// String month = strDate.substring(3,5);
			// String day = strDate.substring(6,8);
			// strDate = year+"-"+month+"-"+day;
			return new Timestamp(getStringToDate(strDate).getTime()).getTime();
		}
		return 0;

	}

	public static Timestamp getStringToTimestamp(String strDate) {
		if (strDate == null) {
			return null;
		}

		if (strDate.length() >= 10) {
			// String year = "20"+strDate.substring(0,2);
			// String month = strDate.substring(3,5);
			// String day = strDate.substring(6,8);
			// strDate = year+"-"+month+"-"+day;
			return new Timestamp(getStringToDate(strDate).getTime());
		}
		return null;

	}

	/**
	 * 返回当前时间是一周中的第几天
	 * 
	 * @return 从周日到周六分别是1-7
	 */
	public static String getDayOfWeek() {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		String dayofweek = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
		return dayofweek;
	}

	public static int getWeekNumber() {
		int w = -1;
		String week = getDayOfWeek();
		try {
			w = Integer.parseInt(week);
		} catch (Exception e) {
			return -1;
		}
		switch (w) {
		case 1:
			return 7;
		case 2:
			return 1;
		case 3:
			return 2;
		case 4:
			return 3;
		case 5:
			return 4;
		case 6:
			return 5;
		case 7:
			return 6;
		default:
			return -1;
		}
	}

	public static String getDayOfWeek(int week) {
		int w = -1;
		try {
			w = week;
		} catch (Exception e) {
			return "";
		}
		switch (w) {
		case 0:
			return "星期日";
		case 1:
			return "星期一";
		case 2:
			return "星期二";
		case 3:
			return "星期三";
		case 4:
			return "星期四";
		case 5:
			return "星期五";
		case 6:
			return "星期六";
		default:
			return "";
		}
	}

	/**
	 * 数字转换成时间类型
	 * 
	 * @param time
	 * @return
	 */
	public static Date long2Date(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c.getTime();
	}

	public static String getDayOfWeek(String week) {
		int w = -1;
		try {
			w = Integer.parseInt(week);
		} catch (Exception e) {
			return "";
		}
		switch (w) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return "";
		}
	}

	public static String getCurrWeek() {
		int w = -1;
		String week = getDayOfWeek();
		try {
			w = Integer.parseInt(week);
		} catch (Exception e) {
			return "";
		}
		switch (w) {
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return "";
		}
	}

	public static String backDayandWeek(Date date) {
		int n = 0;
		String[] weekDays = { "sunday", "monday", "tuesday", "wednesday",
				"thursday", "friday", "saturday" };
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			n = c.get(Calendar.DAY_OF_WEEK) - 1;
			if (n < 0)
				n = 0;
		} catch (Exception e) {
			n = 0;
		}
		return weekDays[n];
	}

	/***
	 * 根据日期返回当前是周几
	 * 
	 * @param date
	 * @return
	 */
	public static int getWeek(Date date) {
		int n = 0;
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			n = c.get(Calendar.DAY_OF_WEEK) - 1;
			if (n < 0)
				n = 0;
		} catch (Exception e) {
			n = 0;
		}
		return n;
	}

	public static String getWeekMonth(int week) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, week);
		cal.add(Calendar.DATE, 1);// 推迟一天
		int month = cal.get(Calendar.MONTH) + 1;
		switch (month) {
		case 1:
			return "一月";
		case 2:
			return "二月";
		case 3:
			return "三月";
		case 4:
			return "四月";
		case 5:
			return "五月";
		case 6:
			return "六月";
		case 7:
			return "七月";
		case 8:
			return "八月";
		case 9:
			return "九月";
		case 10:
			return "十月";
		case 11:
			return "十一月";
		case 12:
			return "十二月";
		default:
			return "";
		}
	}

	public static String getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);// 推迟一天
		int month = cal.get(Calendar.MONTH) + 1;
		switch (month) {
		case 1:
			return "一月";
		case 2:
			return "二月";
		case 3:
			return "三月";
		case 4:
			return "四月";
		case 5:
			return "五月";
		case 6:
			return "六月";
		case 7:
			return "七月";
		case 8:
			return "八月";
		case 9:
			return "九月";
		case 10:
			return "十月";
		case 11:
			return "十一月";
		case 12:
			return "十二月";
		default:
			return "";
		}
	}

	public static int getWeekDay(int week) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, week);
		cal.add(Calendar.DATE, 1);// 推迟一天
		int currDay = cal.get(Calendar.DATE);
		return currDay;
	}

	/***
	 * 前台播放使用日期
	 * 
	 * @param week
	 * @return
	 */
	public static String getWeekDate(int week) {
		String strDate = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, week);
		cal.add(Calendar.DATE, 1);// 推迟一天
		strDate = getSdf(FORMATTIME_DATE).format(cal.getTime());
		return strDate;
	}

	public static String getWeekDateTime(int week) {
		String strDate = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, week);
		cal.add(Calendar.DATE, 1);// 推迟一天
		strDate = getSdf(FORMATTIME_DATE_ZH).format(cal.getTime());
		switch (week) {
		case 1:
			return strDate + "星期一";
		case 2:
			return strDate + "星期二";
		case 3:
			return strDate + "星期三";
		case 4:
			return strDate + "星期四";
		case 5:
			return strDate + "星期五";
		case 6:
			return strDate + "星期六";
		case 7:
			return strDate + "星期日";
		default:
			return "";
		}
	}

	public static String getYear(String datestr) {
		return datestr.substring(0, 4);
	}

	/**
	 * 取得当前时间的年份
	 * 
	 * @return yyyy
	 */
	public static String getYear() {
		return getYear(getCurrentDate());
	}

	public static String getMonth(String datestr) {
		return datestr.substring(5, 7);
	}

	/**
	 * 取得当前时间的月份
	 * 
	 * @return
	 */
	public static String getMonth() {
		return getMonth(getCurrentDate());
	}

	public static String getDay(String datestr) {
		return datestr.substring(8, 10);
	}

	/**
	 * 取得当前时间的日
	 * 
	 * @return
	 */
	public static String getDay() {
		return getDay(getCurrentDate());
	}

	public static String getHour(String date) {
		return date.substring(11, 13);
	}

	public static String getHour() {
		return getHour(getCurrentTime());
	}

	/**
	 * 计算两个Date类型的时间差，单位转换成秒
	 * 
	 * @param starttime
	 * @param endtime
	 * @return 返回结果是endtime-starttime的时间差，单位是秒
	 */
	public static int getSecondDifference(Date starttime, Date endtime) {
		int diff = new Long((endtime.getTime() - starttime.getTime()) / 1000)
				.intValue();
		return diff;
	}

	/**  
	 * 计算两个日期之间相差的天数  
	 * @param smdate 较小的时间 
	 * @param bdate  较大的时间 
	 * @return 相差天数 
	 * @throws ParseException  
	 */    
	public static int daysBetween(Date smdate,Date bdate) throws ParseException {    
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		smdate=sdf.parse(sdf.format(smdate));  
		bdate=sdf.parse(sdf.format(bdate));  
		Calendar cal = Calendar.getInstance();    
		cal.setTime(smdate);    
		long time1 = cal.getTimeInMillis();                 
		cal.setTime(bdate);    
		long time2 = cal.getTimeInMillis();         
		long between_days=(time2-time1)/(1000*3600*24);  

		return Integer.parseInt(String.valueOf(between_days));           
	}
	
	/**
	 * 判断当前时间是否在这两个时间之间
	 * 
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public static boolean betweenCurrentTime(Date starttime, Date endtime) {
		Date cd = new Date();
		return cd.before(endtime) && cd.after(starttime);
	}

	public static int getSecondDifference(Timestamp starttime, Timestamp endtime) {
		int diff = new Long((endtime.getTime() - starttime.getTime()) / 1000)
				.intValue();
		return diff;
	}

	public static int getDaysInterval(String starttime, String endtime) {
		return getDaysInterval(getStringToDate(starttime),
				getStringToDate(endtime));
	}

	public static int getDaysInterval(Date starttime, Date endtime) {
		return new Long((endtime.getTime() - starttime.getTime()) / 86400000)
				.intValue();
	}

	public static int getMonths(Date date1, Date date2) {
		int iMonth = 0;
		int flag = 0;
		Calendar objCalendarDate1 = Calendar.getInstance();
		objCalendarDate1.setTime(date1);

		Calendar objCalendarDate2 = Calendar.getInstance();
		objCalendarDate2.setTime(date2);

		if (objCalendarDate2.equals(objCalendarDate1)) {
			return 0;
		}
		if (objCalendarDate1.after(objCalendarDate2)) {
			Calendar temp = objCalendarDate1;
			objCalendarDate1 = objCalendarDate2;
			objCalendarDate2 = temp;
		}
		// if (objCalendarDate2.get(Calendar.DAY_OF_MONTH) < objCalendarDate1
		// .get(Calendar.DAY_OF_MONTH)) {
		// flag = 1;
		// }
		if (objCalendarDate2.get(Calendar.YEAR) > objCalendarDate1
				.get(Calendar.YEAR)) {
			iMonth = ((objCalendarDate2.get(Calendar.YEAR) - objCalendarDate1
					.get(Calendar.YEAR))
					* 12
					+ objCalendarDate2.get(Calendar.MONTH) - flag)
					- objCalendarDate1.get(Calendar.MONTH);
		} else {
			iMonth = objCalendarDate2.get(Calendar.MONTH)
					- objCalendarDate1.get(Calendar.MONTH) - flag;
		}
		return iMonth;
	}

	public static boolean isDateTimeBefore(String date1, String date2) {
		try {
			return getSdf(FORMATTIME_DATETIME).parse(date1).before(
					getSdf(FORMATTIME_DATETIME).parse(date2));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isDateBefore(String date1, String date2) {
		try {
			return getSdf(FORMATTIME_DATE).parse(date1).before(getSdf(FORMATTIME_DATE).parse(date2));
		} catch (Exception e) {
			return false;
		}
	}

	public static Timestamp getNotNullTimestampValue(String str) {
		Timestamp value;
		try {
			if (str == null || str.equals("")) {
				value = new Timestamp(System.currentTimeMillis());
			} else {
				value = new Timestamp(getSdf(FORMATTIME_DATE).parse(str).getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = new Timestamp(System.currentTimeMillis());
		}
		return value;
	}

	public static Timestamp getNotNullTimestampValue(String str, String format) {
		Timestamp value;
		try {
			if (str == null || str.equals("")) {
				value = new Timestamp(System.currentTimeMillis());
			} else {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
				value = new Timestamp(simpleDateFormat.parse(str).getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = new Timestamp(System.currentTimeMillis());
		}
		return value;
	}

	public static boolean compareTotime(String time) {
		boolean bFlag = false;
		long l = 0;
		String currentTime = getCurrentTime();
		try {
			Date currentDate = getSdf(FORMATTIME_DATE).parse(currentTime);
			Date date = getSdf(FORMATTIME_DATE).parse(time);
			l = currentDate.getTime() - date.getTime();
		} catch (Exception e) {
			l = 0;
			e.printStackTrace();
		}
		if (l > 0)
			bFlag = true;
		return bFlag;
	}

	public static int compareTimstampStr(String sTime, String tTime,
			String format) {
		int nRslt = 10;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		long l = 10;
		try {
			Date sDate = simpleDateFormat.parse(sTime);
			Date tDate = simpleDateFormat.parse(tTime);
			l = sDate.getTime() - tDate.getTime();
		} catch (Exception e) {
			nRslt = 2147483647;
			e.printStackTrace();
		}
		if (l > 0)
			nRslt = 1;
		else if (l == 0)
			nRslt = 0;
		else if (l < 0)
			nRslt = -1;
		return nRslt;
	}

	public static String getCurrentDay(String s) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(s);
		return simpleDateFormat.format(new Date());
	}

	public static String getTimetoString(String seconds) {
		if (StringUtils.isEmpty(seconds)) {
			return "";
		}
		long l = 0;
		try {
			l = Long.parseLong(seconds);
		} catch (Exception e) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date(l * 1000 - sdf.getTimeZone().getRawOffset());
		String str = sdf.format(d);
		return str;
	}

	public static Date getDateBeforeNum(int n) {
		return new Date(System.currentTimeMillis() - 1000 * 3600 * 24 * (n - 1));
	}

	public static Date getDateBeforeNum(Date date, int n) {
		return new Date(date.getTime() - 1000 * 3600 * 24 * (n - 1));
	}

	public static Date getSomeDateToggle(Date date, int n) {
		return new Date(date.getTime() + 1000 * 3600 * 24 * n);

	}

	public static int daysOfMonth(int year, int month) {
		int days = 0; // 该月的天数
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, 1); // java中Calendar类中的月从0-11，所以要month-1

		// 日历每天向后滚一天，如果月份相等，天数+1，月份不等，说明已经到下月第一天了，跳出循环
		while (true) {
			if (c.get(Calendar.MONTH) == (month - 1)) {
				days++;
				c.add(Calendar.DAY_OF_YEAR, 1);
			} else {
				break;
			}
		}
		return days;
	}

	/**
	 * 获取前一天时间
	 * 
	 * @return
	 */
	public static String getPreDate() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -1);
		return DateTimeUtils.getCurrDate(c.getTime());
	}

	public static int getCurrWeekOfMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 获取当天的最晚时间
	 * 
	 * @return
	 */
	public static Date getCurrentDateLastedTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	/**
	 * 获取当天零点时间
	 * 
	 * @return
	 */
	public static Date getCurrentDateZeroTime() {
		Calendar c = Calendar.getInstance();
		// c.setLenient(true);
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取本周第一天零点时间
	 * 
	 * @return
	 */
	public static Date getTheFirstOfThisWeek() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取本周最后一天最晚时间
	 * 
	 * @return
	 */
	public static Date getTheLastOfThisWeek() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_WEEK, c.getMaximum(Calendar.DAY_OF_WEEK));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		return c.getTime();
	}

	/**
	 * 获取本月第一天的零点时间
	 * 
	 * @return
	 */
	public static Date getTheFirstOfMoth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_MONTH, c.getMinimum(Calendar.DATE));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取当月最后一天的最后时间
	 * 
	 * @return
	 */
	public static Date getTheLastOfMoth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DATE));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		return c.getTime();
	}

	public static Date getTheFirstOfYear(int year) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 获取本年最后一天的最后时间
	 * 
	 * @return
	 */
	public static Date getTheLastOfYear(int year) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		return c.getTime();
	}

	public static int getCurrMonth() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.MONTH) + 1;
	}

	public static String Seconds2Time(long sec) {
		int h = 0, m = 0;
		h = (int) (sec == 0 ? 0 : sec / 3600);
		sec = (sec == 0 ? 0 : sec % 3600);
		m = (int) (sec == 0 ? 0 : sec / 60);
		sec = (sec == 0 ? 0 : sec % 60);
		return (h == 0 ? "00" : (h / 10) + "" + h % 10) + ":"
				+ (m == 0 ? "00" : (m / 10) + "" + m % 10) + ":"
				+ (sec == 0 ? "00" : (sec / 10) + "" + sec % 10);
	}

	public static boolean isToday(Date date) {
		return getCurrDate(new Date()).equals(getCurrDate(date));
	}
	
	
	/**
	 * 取得当前日期过去N天的日期
	 * @param day
	 * @return yyyy-MM-dd 格式
	 */
	public static String getStorgeTime(int day) {
		Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, - day);
        Date d = c.getTime();
		return getSdf(FORMATTIME_DATE).format(d);
	}

}
