package com.example.demo.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public class WebResponse {

	public static void printWriter(HttpServletRequest request,
			HttpServletResponse response, String writeContext)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");;
		PrintWriter writer = response.getWriter();
		writer.print(writeContext);
		writer.flush();
		writer.close();
	}

	/**
	 * 普通Json格式输出
	 * @param request
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	public static void printWriterByJson(HttpServletRequest request,
			HttpServletResponse response, String json) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.print(json);
		writer.flush();
		writer.close();
	}
	
	/**
	 * 业务逻辑处理失败，打印输出
	 * @param request
	 * @param response
	 * @param errorMessage
	 * @throws IOException
	 */
	public static void printWriterFail(HttpServletRequest request,
			HttpServletResponse response, String errorMessage) throws IOException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", false);
		jsonObject.put("message", errorMessage);
		printWriterByJson(request, response, jsonObject.toJSONString());
	}
	
	/**
	 * 业务逻辑处理成功，打印输出
	 * @param request
	 * @param response
	 * @param errorMessage
	 * @throws IOException
	 */
	public static void printWriterSuccess(HttpServletRequest request, HttpServletResponse response,JSONObject jsonObject) throws IOException {
		printWriterByJson(request, response, jsonObject.toJSONString());
	}
}
