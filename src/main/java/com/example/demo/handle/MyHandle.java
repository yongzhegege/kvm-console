package com.example.demo.handle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.http.WebRequest;
import com.example.demo.vo.CommonVO;


public class MyHandle implements HandlerInterceptor {

	  @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	        Object user = request.getSession().getAttribute("user");
	        if (user != null) {
	        	CommonVO condition = WebRequest.getCommonVO(request);
	        	request.setAttribute("condition", condition);
	            return true;
	        } else {
	            response.sendRedirect("/login.html");
	            return false;
	        }
	    }

	    /**
	     * 调用完controller,视图渲染之前
	     *
	     * @param request
	     * @param response
	     * @param handler
	     * @param modelAndView
	     * @throws Exception
	     */
	    @Override
	    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	    }

	    /**
	     * 整个完成后，通常用于资源清理
	     *
	     * @param request
	     * @param response
	     * @param handler
	     * @param ex
	     * @throws Exception
	     */
	    @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	    }
}
