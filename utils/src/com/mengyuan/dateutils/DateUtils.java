package com.mengyuan.dateutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
    /*********************************************
     * 输入起始日期和结束日期，得到所有中间的日期，包括首尾日期
     *********************************************/
    public static List<Date> dateSplit(Date startDate, Date endDate) throws Exception {
        if (!startDate.before(endDate))
            throw new Exception("开始时间应该在结束时间之后");
        Long spi = endDate.getTime() - startDate.getTime();
        Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数

        List<Date> dateList = new ArrayList<Date>();
        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime() - (24 * 60 * 60 * 1000)));// 比上一天减一
        }
        return dateList;
    }

    /*********************************************
     * 测试
     *********************************************/
    public static void main(String[] args) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse("2015-4-20");
            Date end = sdf.parse("2015-5-2");
            List<Date> lists = dateSplit(start, end);
            if (!lists.isEmpty()) {
                for (Date date : lists) {
                    System.out.println(sdf.format(date));
                }
            }
        } catch (Exception e) {
        }
    }

}
