package com.example.demo.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import com.example.demo.util.StringUtils;
import com.example.demo.vo.CommonVO;

public class WebRequest extends WebUtils {
	
	public static CommonVO getCommonVO(HttpServletRequest request) {
		CommonVO commonVO = new CommonVO();
		Map<String, Object> params = getParametersStartingWith(request, null);
		Iterator<String> iterators = params.keySet().iterator();
		String method = request.getMethod().toLowerCase();
		if ("get".equals(method)) {
			while (iterators.hasNext()) {
				String next = iterators.next();
				commonVO.setString(next, StringUtils.utf8Decoder((String) params.get(next)));
			}
		} else {
			while (iterators.hasNext()) {
				String next = iterators.next();
				commonVO.setString(next, (String) params.get(next));
			}
		}
		return commonVO;
	}
	
	/**
	 * 获取request请求里的value值
	 * @param request
	 * @param value
	 * @return
	 */
	public static String get(HttpServletRequest request,String value) {
		return get(request, value, "");
	}
	
	/**
	 * 获取request请求里的value值 如果没有的话返回设置的默认值
	 * @param request
	 * @param value
	 * @return
	 */
	public static String get(HttpServletRequest request,String value,String defaultValue) {
		String reqValue = request.getParameter(value);
		if (StringUtils.isEmpty(reqValue)) {
			reqValue = defaultValue;
		}
		return reqValue;
	}
	
	/**
	 * 获取登录会话
	 * @param request
	 * @return
	 */
	public static boolean isLoginSession(HttpServletRequest request) {
		return null != request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
	}
	
	
	public final static String getIpAddress(HttpServletRequest request) throws IOException {
		// 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
		String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if("0:0:0:0:0:0:0:1".equals(ip)){
        	ip = InetAddress.getLocalHost().getHostAddress();
        }
		return ip;
	}
	
    public static String setDownloadFileHeader(HttpServletRequest request, String fileName) {
        final String userAgent = request.getHeader("USER-AGENT");
        try {
            String finalFileName = null;
            if(org.apache.commons.lang.StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                finalFileName = URLEncoder.encode(fileName,"UTF8");
            }else if(org.apache.commons.lang.StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
                finalFileName = new String(fileName.getBytes(), "ISO8859-1");
            }else{
                finalFileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
            }
            return finalFileName;
        } catch (UnsupportedEncodingException e) {
        	return null;
        }
    }
    
    public static String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }
        return url.toLowerCase();
    }
    
}
