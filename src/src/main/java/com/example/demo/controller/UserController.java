package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.User;
import com.example.demo.service.UserService;
import com.example.demo.util.MD5;
import com.example.demo.vo.CommonVO;

@RequestMapping("/user")
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 登录
	 * @param request
	 * @param userName
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/login",method = RequestMethod.POST)
	public JSONObject login(HttpServletRequest request,@RequestParam(value = "username")String userName,
			@RequestParam(value = "password")String password) {
		JSONObject json = new JSONObject();
		User user = userService.getUserByUserName(userName);
		if(user != null) {
			if(MD5.GetMD5Code(password + MD5.GetMD5Code(userName)).equals(user.getUserPassword())) {
				json.put("success", true);
				request.getSession().setAttribute("user", user);
			}else {
				json.put("success", false);
				json.put("message", "用户名或密码错误！");
			}
		}else {
			json.put("success", false);
			json.put("message", "用户名不存在！");
		}
		return json;
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/logout",method = RequestMethod.GET)
	public void logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.getSession().removeAttribute("user");
		response.sendRedirect("/login.html");
	}
	
	/**
	 * 修改密码
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doModifyUserPwd.action", method = RequestMethod.POST)
	public JSONObject doModifyUserPwd(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		User oldUser = userService.getUserByUserId(condition.getInteger("userId"));
		String userName = condition.getString("userName");
		String oldPwd = condition.getString("oldPwd").trim();
		String newPwd = condition.getString("newPwd").trim();
		if (!MD5.GetMD5Code(oldPwd + MD5.GetMD5Code(userName)).equals(oldUser.getUserPassword())) {
			json.put("success", false);
			json.put("message", "您输入的原密码不正确！");
		}else{
			User newUser = new User();
			newUser.setUserId(condition.getInteger("userId"));
			newUser.setUserPassword(MD5.GetMD5Code(newPwd + MD5.GetMD5Code(userName)));
			userService.updateUser(newUser);
			request.getSession().removeAttribute("user");
		}
		return json;
	}
}
	
