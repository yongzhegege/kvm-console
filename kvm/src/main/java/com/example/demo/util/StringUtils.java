package com.example.demo.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;


/**
 * 字符串处理
 * 
 * @author
 * 
 */
public class StringUtils {

	/**
	 * 获取10位随机数，可做ID
	 * @param length
	 * @return
	 */
	public static String getRandom(int length){
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			val += String.valueOf(random.nextInt(10));
		}
		return val;
	}
	
	/**
	 * 默认分隔符
	 */
	private static final String defaultDelimiter = ",";
	
	public static String getByteArrayToStr(byte[] bytes) {
		byte[] bs = new byte[bytes.length / 2];
		int index = 0;
		for (int i = 0; i < bytes.length; i++) {
			if ((i + 1) % 2 != 0) {
				bs[index++] = bytes[i];
			}
		}
		return new String(bs);
	}
	
	public static char[] setStrToChar(String str, int len) {
		char[] items = new char[len];
		if (!StringUtils.isEmpty(str)) {
			char[] conset = str.toCharArray();
			for (int i = 0; i < conset.length; i++) {
				items[i] = conset[i];
			}
		}
		return items;
	}
	
	public static String getCharToStr(char[] items) {
		String rtnStr = "";
		for (int i = 0; i < items.length; i++) {
			if (items[i] != ' ') {
				rtnStr += items[i];
			} else {
				break;
			}
		}
		rtnStr = rtnStr.replaceAll("\u0000", "");
		return rtnStr;
	}

	/**
	 * 分割字符串
	 * 
	 * @param str
	 *            输入字符串
	 * @param delimiters
	 *            分隔符
	 * @return
	 */
	public static String[] split(final String str, final String delimiters) {
		return org.apache.commons.lang.StringUtils.split(str, delimiters);
	}

	/**
	 * 分割字符串(采用默认分隔符 , )
	 * 
	 * @param str
	 *            输入字符串
	 * @return
	 */
	public static String[] split(final String str) {
		return split(str, defaultDelimiter);
	}
	
	/**
	 * 获取符号的个数
	 * @param str
	 * @param delimiters
	 * @return
	 */
	public static Integer countDelimiter(final String str, final char delimiters) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == delimiters) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 正则表达式
	 * @param str
	 * @param regEx
	 * @return
	 */
	public static boolean regEx(String str, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}
	
	/**
	 * 集合类连接
	 * 
	 * @param c
	 *            集合
	 * @param delim
	 *            连接符
	 * @return
	 */
	public static String join(final Collection<?> c, final String delim) {
		return org.apache.commons.lang.StringUtils.join(c.iterator(), delim);
	}

	/**
	 * 集合类连接（采用默认连接符 ,）
	 * 
	 * @param c
	 *            集合
	 * @return
	 */
	public static String join(final Collection<?> c) {
		return join(c, defaultDelimiter);
	}

	/**
	 * 数组连接
	 * 
	 * @param arr
	 *            数组
	 * @param delim
	 *            连接符
	 * @return
	 */
	public static String join(final Object[] arr, final String delim) {
		return org.apache.commons.lang.StringUtils.join(arr, delim);
	}

	/**
	 * 数组连接（采用默认连接符 ,）
	 * 
	 * @param arr
	 *            数组
	 * @return
	 */
	public static String join(final Object[] arr) {
		return join(arr, defaultDelimiter);
	}

	/**
	 * 字符串替换
	 * 
	 * @param inString
	 *            输入字符串
	 * @param oldPattern
	 *            替换字符
	 * @param newPattern
	 *            输出字符
	 * @return
	 */
	public static String replace(final String inString,
			final String oldPattern, final String newPattern) {
		return org.apache.commons.lang.StringUtils.replace(inString,
				oldPattern, newPattern);
	}

	/**
	 * 输入参数是否为空
	 * 
	 * @param string
	 *            输入字符串
	 * @return 输入： null|"" 返回 false 否则 true
	 */
	public static boolean hasText(final String string) {
		return org.apache.commons.lang.StringUtils.isNotBlank(string);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(final String str) {
		// return org.apache.commons.lang.StringUtils.isEmpty(str);
		return !hasText(str);
	}

	/**
	 * 输入参数是否为空
	 * 
	 * @param object
	 *            输入参数
	 * @return
	 */
	public static boolean hasTextObject(final Object object) {
		if (object == null) {
			return false;
		}
		if (object instanceof String) {
			return hasText((String) object);
		}
		return true;
	}

	/**
	 * 返回输入字符中首个不为空的字符
	 * 
	 * @param strings
	 *            字符数组
	 * @return
	 */
	public static String text(final String... strings) {
		if (strings != null) {
			for (final String string : strings) {
				if (hasText(string)) {
					return string;
				}
			}
		}
		return "";
	}

	/**
	 * 判断n是否大于0
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public static int blank(final int n, final int m) {
		return n > 0 ? n : m;
	}

	/**
	 * 获取文件名称
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static String getFilename(final String path) {
		return FilenameUtils.getName(path);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static String getFilenameExtension(final String path) {
		return FilenameUtils.getExtension(path);
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
//	static{DateTimeUtil.getCompactTime();}
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 转换字节数组为16进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字符串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * MD5加密一个字符串
	 * 
	 * @param origin
	 *            原始字符串
	 * @return 加密后字符串
	 */
	public static String MD5Encode(String origin) {
		String resultString = "";
		resultString = new String(origin);
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
		return resultString;
	}
	
	/**
	 * 获取32位字符串
	 * @return
	 */
	public static String getKeyCode() {
		return MD5Encode(String.valueOf(Math.random())).toUpperCase();
	}

	/**
	 * String转化Int
	 * 
	 * @param str
	 * @return str为空或不为数字 返回-1
	 */
	public static int parseInt(String str) {
		if (str == null || str.trim().length() == 0) {
			return -1;
		}
		try {
			Integer.parseInt(str.trim());
		} catch (Exception e) {
			return -1;
		}
		return Integer.parseInt(str.trim());
	}

	public static int parseInt(String num, int def) {
		if (num == null || num.trim().length() == 0) {
			return def;
		}
		try {
			Integer.parseInt(num.trim());
		} catch (Exception e) {
			return def;
		}
		return Integer.parseInt(num.trim());
	}
	
	public static String arrayToString(String[] strArray) {
		StringBuffer buffer = new StringBuffer();
		if (strArray != null && strArray.length > 0) {
			for (String s : strArray) {
				buffer.append(s).append(",");
			}
		}
		return buffer.toString();
	}

	public static String getWSUrl(String Ip, int Port) {
		return "http://" + String.valueOf(Ip) + ":" + String.valueOf(Port);
	}

	// **************Html相关 开始********************//

	/**
	 * 转化Html编码
	 * 
	 * @param strTHML
	 * @return
	 */
	public static String HTMLEscape(String strTHML) {
		return StringEscapeUtils.escapeHtml(strTHML);
	}

	/**
	 * 转化Html解码
	 * 
	 * @param strTHML
	 * @return
	 */
	public static String HTMLUNEscape(String strTHML) {
		return StringEscapeUtils.unescapeHtml(strTHML);
	}

	/**
	 * 转化xml编码
	 * 
	 * @param xml
	 * @return
	 */
	public static String xmlEscape(String xml) {
		return StringEscapeUtils.escapeXml(xml);
	}

	/**
	 * 转化xml解码
	 */
	public static String xmlUNEscape(String xml) {
		return StringEscapeUtils.unescapeXml(xml);
	}

	/**
	 * 删除input中html格式
	 * 
	 * @param input
	 * @return
	 */
	public static String removeHtml(String input) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		// 去掉所有html元素,
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		return str;
	}
	
	/**
	 * 删除特殊字符
	 * @param value
	 * @return
	 */
	public static String removeSign(String value) {
		char[] items = { 	'.', '!', '#', '$', 
							'%', '^', '&', '*', 
							'(', ')', '-', '=', 
							'+', '~', '`', '{', 
							'}', ';', '\'', '；', 
							'’', '“', '？', '<',
							'>', '【', '】', '、'};
		String str = "";
		for (int i = 0; i < value.length(); i++) {
			boolean flag = false;
			for (int j = 0; j < items.length; j++) {
				if (items[j] == value.charAt(i)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				str += value.charAt(i);
			}
		}
		return trimAll(str);
	}

	/**
	 * 删除input中span和div标记
	 * 
	 * @param input
	 * @return
	 */
	public static String removeStyle(String input) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		String str = input
				.replaceAll(
						"<\\s{0,}((span)|(div)|(SPAN)|(DIV)|(/DIV)|(/SPAN)|(/div)|(/span))\\s{0,}[^>]*>",
						"");
		return str;
	}

	// **************Html相关 结束********************//

	/**
	 * javascript 解析处理成字符串
	 * 
	 * @param js
	 * @return
	 */
	public static String javascriptEscape(String js) {
		return org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(js);
	}

	/**
	 * javascript 处理
	 * 
	 * @param js
	 * @return
	 */
	public static String javascriptUNEscape(String js) {
		return org.apache.commons.lang.StringEscapeUtils.unescapeJavaScript(js);
	}

	public static String trimToEmpty(String str) {
		return StringUtils.trimToEmpty(str);
	}

	public static String trim(String str) {
		return org.apache.commons.lang.StringUtils.trim(str);
	}
	
	public static String trimAll(String str) {
		return str.replaceAll(" ", "");
	}
	
	public static String getSerialNo() {
		return DateTimeUtils.getCompactTime();
	}

	public static String enCode(String str) {
		if (str == null) {
			return "";
		}
		String hs = "";
		try {
			byte b[] = str.getBytes("UTF-16");
			for (int n = 0; n < b.length; n++) {
				str = (java.lang.Integer.toHexString(b[n] & 0XFF));
				if (str.length() == 1) {
					hs = hs + "0" + str;
				} else {
					hs = hs + str;
				}
				if (n < b.length - 1) {
					hs = hs + "";
				}
			}
			// 去除第一个标记字符
			str = hs.toUpperCase().substring(4);
			// System.out.println(str);
			char[] chs = str.toCharArray();
			str = "";
			for (int i = 0; i < chs.length; i = i + 4) {
				str += "&#x" + chs[i] + chs[i + 1] + chs[i + 2] + chs[i + 3]
						+ ";";
			}
			return str;
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return str;
	}

	/**
	 * 
	 * 
	 * @param str
	 *            给定的字符
	 * @param searchChar
	 *            搜索字符
	 * @return
	 */
	public static int firstIndexOf(String str, String searchChar) {
		return org.apache.commons.lang.StringUtils.indexOf(str, searchChar);
	}

	/**
	 * 
	 * 
	 * @param str
	 *            给定的字符
	 * @param searchChar
	 *            搜索字符
	 * @return
	 */
	public static int lastIndexOf(String str, String searchChar) {
		return org.apache.commons.lang.StringUtils.lastIndexOf(str, searchChar);
	}

	/**
	 * 截取字符串
	 * 
	 * @param str
	 *            输入字符串
	 * @param separator
	 *            分割符号
	 * @return
	 */
	public static String subStringAfter(String str, String separator) {

		return org.apache.commons.lang.StringUtils.substringAfter(str,
				separator);
	}

	/**
	 * 截取字符串
	 * 
	 * @param str
	 *            输入字符串
	 * @param start
	 *            起始索引
	 * @return
	 */
	public static String subString(String str, int start) {
		return org.apache.commons.lang.StringUtils.substring(str, start);

	}

	/**
	 * 截取字符串
	 * 
	 * @param srcStr
	 *            输入字符串
	 * @param start
	 *            截取起点
	 * @param end
	 *            结束索引
	 * @return
	 */
	public static String subString(String srcStr, int start, int end) {
		return org.apache.commons.lang.StringUtils
				.substring(srcStr, start, end);

	}

	/**
	 * 连接字符串
	 * 
	 * @param str1
	 *            输入串1
	 * @param str2
	 *            输入串2
	 * @param flag
	 *            是否删除公共部分
	 * @return
	 */
	public static String contractString(String str1, String str2, boolean flag,
			String dot) {
		if (isEmpty(str1) || isEmpty(str2)) {
			return isEmpty(str1) ? (isEmpty(str2) ? "" : str2) : str1;
		}
		int i = 0;
		for (i = str2.length(); i > 0; i--) {
			if (str1.length() - str1.indexOf(str2.substring(0, i)) <= str1
					.length())
				break;
		}
		// System.out.println("   ======="+str2.substring(0,i));
		StringBuffer sb = new StringBuffer(str1);
		// sb.delete(i,sb.length());
		if (!flag) {
			sb.append(isEmpty(dot) ? "" : dot).append(str2.substring(i));

		} else {
			sb.append(isEmpty(dot) ? "" : dot).append(str2.substring(i));
		}
		return sb.toString();
	}

	/**
	 * 路径处理方法
	 * 
	 * @param path
	 *            编码任意,格式任意
	 * @param prefixed
	 *            是否前缀目录符号(false 前没有 "/" )
	 * @param suffixed
	 *            是否后缀目录符号 (不是目录最后不存在 "/")
	 * @return 返回 路径格式 [/]XXX/YYY/zz[/] UTF-8编码
	 */
	public static String triggerPath(String path, boolean prefixed,
			boolean suffixed) {
		if (isEmpty(path) || "/".equals(path)) {
			return (prefixed || suffixed) ? "/" : "";
		}
		StringBuffer sb = new StringBuffer(path.replaceAll("\\\\", "/"));
		if(suffixed){
			sb.append("/");
		}
		// /System.out.println("=================> "+sb.toString());
		for (int i = 0; i < sb.length() - 1;) {
			if ((sb.charAt(i) & '/' & sb.charAt(i) ^ sb.charAt(i + 1)) == 0) {
				sb.deleteCharAt(i);
			} else {
				i++;
			}
		}
		if ((sb.charAt(sb.length() - 1) ^ '/') == 0) {
			if (!suffixed) {
				sb.deleteCharAt(sb.length() - 1);
			}
		} else {
			if (suffixed) {
				sb.insert(0, "/");
			}
		}
		if (sb.length() <= 0) {
			return prefixed ? "/" : sb.toString();
		}

		if ((sb.charAt(0) ^ '/') == 0) {
			if (!prefixed) {
				sb.deleteCharAt(0);
			}
		} else {
			if (prefixed) {
				sb.insert(0, "/");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 判断给定的值是否代表真
	 * 
	 * @param value
	 *            true|1|真|是|y
	 * @return
	 */
	public static boolean isTrue(String value) {
		return "true".equalsIgnoreCase(value) || "1".equals(value) || "真".equals(value) || "是".equals(value)
				|| "y".equalsIgnoreCase(value);
	}


	/**
	 * 系统目标编码
	 */
	public static String Encoding = "UTF-8";

	/**
	 * 容器编码
	 */
	public static String ContainerEncoding = "ISO-8859-1";

	/**
	 * 将指定的字符串转换为目标编码的字节数组
	 * 
	 * @param source
	 *            待转换的字符串
	 * @param coding
	 *            目标编码
	 * @return 转换后的字节数组
	 */
	public static byte[] enCodingString(String source, String coding) {
		try {
			return source.getBytes(coding);
		} catch (Exception e) {
		}
		return source.getBytes();
	}

	/**
	 * 从ISO-8859-1到GBK编码
	 * 
	 * @param value
	 * @return
	 */
	public static String chgGBK(String value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			try {
				return new String(value.getBytes("ISO-8859-1"), "GBK");
			} catch (Exception e) {
			}
			return null;
		}
	}

	/**
	 * 从容器编码到系统目标编码
	 * 
	 * @param value
	 *            待编码的值
	 * @return
	 */
	public static String enCoding(String value) {
		try {
			return codeTran(value, ContainerEncoding, Encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 从系统目标编码到容器编码格式
	 * 
	 * @param value
	 *            待解码的值
	 * @return
	 */
	public static String deCoding(String value) {
		try {
			return codeTran(value, Encoding, ContainerEncoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 将字符串从源编码格式转换到目标编码格式
	 * 
	 * @param value
	 *            待转换的字符串
	 * @param sourceCode
	 *            源编码
	 * @param targetCode
	 *            目标编码
	 * @return 转换后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String codeTran(String value, String sourceCode, String targetCode)
			throws UnsupportedEncodingException {
		if (sourceCode.equalsIgnoreCase(targetCode)) {
			return value;
		} else if (value == null || value.equals("")) {
			return value;
		} else {
			return new String(value.getBytes(sourceCode), targetCode);
		}
	}

	public static String URLEncoding(String value) {
		try {
			return URLEncoder.encode(value, Encoding);
		} catch (Exception e) {
		}
		return value;
	}

	public static String URLDecoding(String value) {
		try {
			return URLDecoder.decode(value, Encoding);
		} catch (Exception e) {

		}
		return value;
	}
	/**
	 * 将NULL转化为空字符串
	 * 
	 * @param strTarget
	 *            转化目标
	 * @return 转化结果
	 */
	public static String makeNullToEmptyString(String strTarget) {
		if (strTarget == null) {
			strTarget = "";
		}
		return strTarget;
	}
	
	 /**
     * utf8转码
     * Description :.
     * 
     * @param str the str
     * 
     * @return the string
     */
    public static String utf8Decoder(String str) {
        try {
        	String decoderUrl = URLDecoder.decode(str,"UTF-8");
            return URLDecoder.decode(decoderUrl, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }
    
	// 首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toLowerCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}

	// 首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
	}
	
	public static List<Long> strToLongList(String[] strArr) {
	    Long[] convert = (Long[]) ConvertUtils.convert(strArr, Long.class);
	    List<Long> longArr = Arrays.asList(convert);
	    return longArr;
	}
	
	public static List<String> strToStringList(String[] strArr) {
		String[] convert = (String[]) ConvertUtils.convert(strArr, String.class);
		List<String> longArr = Arrays.asList(convert);
		return longArr;
	}
}
