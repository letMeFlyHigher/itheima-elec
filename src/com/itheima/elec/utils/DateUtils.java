package com.itheima.elec.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**将日期类型转换成String类型，file的格式*/
	public static String dateToStringByFile(Date date) {
		String sDate = new SimpleDateFormat("/yyyy/MM/dd/").format(date);
		return sDate;
	}

	
}
