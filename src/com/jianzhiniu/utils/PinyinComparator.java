package com.jianzhiniu.utils;

import java.util.Comparator;
/**
 * Æ´Òô±È½ÏÆ÷
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class PinyinComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		 String str1 = PingYinUtil.getPingYin((String) o1);
	     String str2 = PingYinUtil.getPingYin((String) o2);
	     return str1.compareTo(str2);
	}

}
