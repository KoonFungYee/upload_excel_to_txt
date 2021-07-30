package com.example.upload_excel_to_txt;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static final String COMMON_PATTERN       = "yyyy-MM-dd HH:mm:ss";
    public static final String COMMON_PATTERN_TYPE2 = "yyyy/MM/dd HH:mm:ss";
    public static final String SHORT_PATTERN        = "yyyy-MM-dd";
    public static final String SHORT_PATTERN_TYPE2  = "yyyy/MM/dd";
    public static final String LONG_PATTERN         = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SUP_SHORT_PATTERN    = "yyyyMMdd";
    public static final String SUP_LONG_PATTERN     = "yyyyMMddHHmmss";
    public static final String YEAR_MONTH           = "yyyyMM";
    public static final String CN_SHORT_PATTERN     = "yyyy年MM月dd日";
    public static final String DDMM_PATTERN         = "ddMM";
    public static final String MAL_SHORT_CST_TIME_PATTERN = "dd-MM-yyyy";
    public static final String MAL_CST_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final String DAY_MONTH_YEAR = "dd/MM/yyyy";

    public static Date trans2Date(String dateString, String pattern) {
        String fRTN = StringUtils.isNotBlank(pattern) ? pattern : COMMON_PATTERN;
        DateTimeFormatter format = DateTimeFormat.forPattern(fRTN);
        DateTime dt = DateTime.parse(dateString, format);
        return dt.toDate();
    }

    public static String formatDate2String(String dateString, String fromPattern, String toPattern) {
        String tRTN = StringUtils.isNotBlank(toPattern) ? toPattern : COMMON_PATTERN;
        DateTimeFormatter format = DateTimeFormat.forPattern(fromPattern);
        DateTime dt = DateTime.parse(dateString, format);
        return dt.toString(tRTN);
    }

    public static String formatDate2String(Date date, String pattern) {
        String fRTN = StringUtils.isNotBlank(pattern) ? pattern : COMMON_PATTERN;
        DateTimeFormatter format = DateTimeFormat.forPattern(fRTN);
        return new DateTime(date).toString(format);
    }

    public static <T> Date add(T date, int daysCount) {
        DateTime dt = new DateTime(date);
        return dt.plusDays(daysCount).toDate();
    }

    public static <T> Date sub(T date, int daysCount) {
        DateTime dt = new DateTime(date);
        return dt.minusDays(daysCount).toDate();
    }

    public static <T> Date addMonths(T date, int months) {
        DateTime dt = new DateTime(date);
        return dt.plusMonths(months).toDate();
    }

    public static <T> Date subMonths(T date, int months) {
        DateTime dt = new DateTime(date);
        return dt.minusMonths(months).toDate();
    }

    public static <T> Date subYears(T date, int years) {
        DateTime dt = new DateTime(date);
        return dt.minusYears(years).toDate();
    }

    public static int getMonthParse(String lastRepaymentDate, String fundLoanDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(sdf.parse(fundLoanDate));
        aft.setTime(sdf.parse(lastRepaymentDate));
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR))*12;
        return month+result;
    }

    public static <T> int betweenDays(T dateMin, T dateMax) {
        LocalDate start = new LocalDate(dateMin);
        LocalDate end = new LocalDate(dateMax);
        return Days.daysBetween(start, end).getDays();
    }

    public static <T> long betweenMs(T dateMin, T dateMax) {
        DateTime start = new DateTime(dateMin);
        DateTime end = new DateTime(dateMax);
        return end.getMillis() - start.getMillis();
    }

    public static <T> boolean isTradeDay(T date) {
        boolean isTD = false;
        DateTime dt = new DateTime(date);
        if (isSpWorkDay(dt) || (!isHoliday(dt) && !isWeekEnd(dt))) {
            isTD = true;
        }
        return isTD;
    }

    private static boolean isWeekEnd(DateTime dt) {
        boolean isWE = false;
        int wd = dt.dayOfWeek().get();
        switch (wd) {
            case 6:
            case 7:
                isWE = true;
                break;
            default:
                break;
        }
        return isWE;
    }

    private static boolean isHoliday(DateTime dt) {
        boolean isHD = false;
        List<String> holidayList = new ArrayList<String>();
        if (holidayList != null) {
            for (String holiday : holidayList) {
                if (dt.isEqual(new DateTime(holiday))) {
                    isHD = true;
                    break;
                }
            }
        }
        return isHD;
    }

    private static boolean isSpWorkDay(DateTime dt) {
        boolean isSpWD = false;
        List<String> spWorkDayList = new ArrayList<String>();
        if (spWorkDayList != null) {
            for (String spWorkDay : spWorkDayList) {
                if (dt.isEqual(new DateTime(spWorkDay))) {
                    isSpWD = true;
                    break;
                }
            }
        }
        return isSpWD;
    }

    public static final Date diffDate(Date aDate, int field, int diff) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        cal.add(field, diff);
        return cal.getTime();
    }

    public static <T> boolean isInRange(T sDate, T eDate, T cDate){
        DateTime dtLow = new DateTime(sDate);
        DateTime dtHigh = new DateTime(eDate);
        DateTime dt = new DateTime(cDate);
        if(dt.getMillis()>=dtLow.getMillis() && dt.getMillis()<=dtHigh.getMillis()){
            return true;
        }
        return false;
    }
    public static <T> boolean isInRange(){
        DateTime now = DateTime.now();
        return isInRange(now,now,now);
    }
    public static <T> boolean isInRange(T sDate, T eDate){
        DateTime now = DateTime.now();
        return isInRange(sDate,eDate,now);
    }

    public static <T> Date getMDate(T date, int i, boolean fl, boolean iHms){
        DateTime t = new DateTime(subMonths(date, i));
        DateTime cd;
        if(iHms && fl){
            cd = new DateTime(t.getYear(),t.getMonthOfYear(),t.getDayOfMonth(),0,0,0);
        } else if (iHms && !fl) {
            cd = new DateTime(t.getYear(),t.getMonthOfYear(),t.getDayOfMonth(),23,59,59);
        } else {
            cd = new DateTime(t);
        }
        return fl?cd.dayOfMonth().withMinimumValue().toDate():cd.dayOfMonth().withMaximumValue().toDate();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
