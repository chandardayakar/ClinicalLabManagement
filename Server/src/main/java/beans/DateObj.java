package beans;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateObj {

    private int day;

    private int month;

    private int year;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public static DateObj fromString(String dateStr) throws Exception{
        String[] s = dateStr.split(" ");
        try{
            DateObj res = new DateObj();
            res.setDay(Integer.valueOf(s[0]));
            res.setMonth(Integer.valueOf(s[1]));
            res.setYear(Integer.valueOf(s[2]));
            return res;
        }catch(Exception e){
            System.out.println("Unable to parse string to date, due to - " + e.getMessage());
            throw new Exception("Invalid date");
        }

    }

    public Calendar convertToCalender() throws Exception{

        Calendar res = new GregorianCalendar();
        res.set(year, month, day);

        return res;
    }

    @Override
    public String toString() {
        return day + " " + month + " " + year;
    }
}
