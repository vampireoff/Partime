package com.jianzhiniu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * ƴ��������
 * @author Administrator
 *
 */
public class PingYinUtil {
	/**
	 * ��ȡ���ֵ�ƴ����ĸ
	 *
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new
				HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		
		char[] input = inputString.trim().toCharArray();
		String output = "";
		
		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).
						matches("[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.
							toHanyuPinyinStringArray(input[i],
									format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(
							input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}
	
	/**
	 * ���ת��Ϊȫ��
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	
	/**
	 * ȥ�������ַ����������ı���滻ΪӢ�ı��
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("��", "[").replaceAll("��", "]").replaceAll("��", ",")
				.replaceAll("��", "!").replaceAll("��", ":");// �滻���ı��
		String regEx = "[����]"; // ����������ַ�
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}
