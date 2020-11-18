import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class tricks {

    @Test
    public void Testp1(){
        long now = System.currentTimeMillis();
        String res = MillisToDateStr(now);
        //System.out.println(res);
        String p1_start = "****11150900";
        String p1_end = "****12162359";
        isIn(res,p1_start,p1_end);
    }


    @Test
    public void test2(){
        System.out.println(DateStrToMillis("20200809125636"));
        System.out.println(MillisToDateStr(1596948996000L));
    }

    @Test
    public void test3(){
        getPattern(YearTime());
        getPattern(MonthTime());
        getPattern(DayTime());
    }


    /**
     * 毫秒转换为日期
     * @param millis
     * @return
     */
    public String MillisToDateStr(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;//获取当前月份需要+1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//24小时
        int minute= calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return String.valueOf(year)
                + String.valueOf(month)
                + String.valueOf(day)
                + String.valueOf(hour)
                + String.valueOf(minute)
                + String.valueOf(second);
    }

    /**
     * 日期字符串转Date对象
     * @param dateStr
     * @return
     */
    public long DateStrToMillis(String dateStr){
        long res = 0L;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try{
            date = format.parse(dateStr);
            res = date.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 判断时间是否在预设时间段内
     * @param actual
     * @param start
     * @param end
     * @return
     */
    public boolean isIn(String actual, String start, String end){
        boolean flag = true;
        int len = start.length();
        //确定从第几位开始比较
        int index = 0;
        for(int i = 0; i<len;i++){
            if(start.charAt(i)!='*'){
                index = i;
                break;
            }
        }

        int startInt = Integer.parseInt(start.substring(index,len));
        int endInt = Integer.parseInt(end.substring(index,len));
        int actualInt = Integer.parseInt(actual.substring(index,len));
        System.out.println(startInt+";"+endInt+";"+actualInt);
        if(actualInt>startInt && actualInt<endInt){
            //flag=true;
        }else {
            flag=false;
        }
        //System.out.println(flag);
        return flag;
    }

    /**
     * 解析时间段表达式
     * @param dateJO
     * @return
     */
    public String getPattern(JSONObject dateJO){
        StringBuilder res = new StringBuilder();
        Set<String> sortSet = new TreeSet<String>(String::compareTo);
        sortSet.addAll(dateJO.keySet());
        for(String key_str:sortSet){
            String value = dateJO.getString(key_str);
            if(key_str.contains("year")){//单独处理年份
                if(StringUtils.isBlank(value)){
                    res.append("****");
                }else{
                    res.append(value);
                }
            }else{
                if(StringUtils.isBlank(value)){
                    res.append("**");
                }else{
                    res.append(value);
                }
            }
        }
        //System.out.println(res);
        return res.toString();
    }









    //每年的08月08日16点56分00秒
    public JSONObject YearTime(){
        JSONObject res = new JSONObject();
        res.put("1year","");
        res.put("2month","08");
        res.put("3day","08");
        res.put("4hour","16");//24小时
        res.put("5minute","56");
        res.put("6second","00");//默认是00
        return res;
    }
    //每月08日16点56分00秒
    public JSONObject MonthTime(){
        JSONObject res = new JSONObject();
        res.put("1year","");
        res.put("2month","");
        res.put("3day","08");
        res.put("4hour","16");//24小时
        res.put("5minute","56");
        res.put("6second","00");//默认是00
        return res;
    }
    //每日16点56分00秒
    public JSONObject DayTime(){
        JSONObject res = new JSONObject();
        res.put("1year","");
        res.put("2month","");
        res.put("3day","");
        res.put("4hour","16");//24小时
        res.put("5minute","56");
        res.put("6second","00");//默认是00
        return res;
    }

}
