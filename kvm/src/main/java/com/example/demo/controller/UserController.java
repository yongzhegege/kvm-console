package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

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
import com.example.demo.empty.UserVm;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserVmService;
import com.example.demo.util.MD5;
import com.example.demo.vo.CommonVO;

@RequestMapping("/user")
@Controller
public class UserController{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LogService logService;
	
	@Autowired
	private UserVmService userVmService;
	
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
				logService.insert(request, "用户登录", 1);
			}else {
				json.put("success", false);
				json.put("message", "用户名或密码错误！");
				logService.insert(request, "用户登录", 0);
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
		logService.insert(request, "用户退出登录", 1);
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
			logService.insert(request,"用户修改密码", 1);
			request.getSession().removeAttribute("user");
		}
		return json;
	}
	
	/**
	 * 用户列表
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doPageSelect.action",method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		User user = (User)condition.toBeanResolver(User.class);
		JSONObject json = new JSONObject();
		json.put("data", userService.allSelectLists(user));
		json.put("success", true);
		json.put("code", 0);
		JSONObject status = new JSONObject();
		status.put("code", 200);
		json.put("status", status);
		return json;
	}
	
	
	/**
	 * 添加用户
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doInsert.action",method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		User adduser = (User)condition.toBeanResolver(User.class);
		JSONObject json = new JSONObject();
		json.put("success", true);
		List<User> userList = userService.allSelectLists(null);
		if(userList != null  && userList.size() > 0) {
			for(User user : userList) {
				if(user.getUserName().equals(adduser.getUserName())) {
					json.put("success", false);
					json.put("message", "用户名已存在，请重新添加！");
					return json;
				}
			}
		}
		adduser.setLevel(3);
		adduser.setSambaSwitch(0);
		adduser.setUserPassword(MD5.GetMD5Code(MD5.GetMD5Code("123456")+MD5.GetMD5Code(adduser.getUserName())));
		userService.addUser(adduser);
		logService.insert(request, "添加用户("+adduser.getUserName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 删除用户
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doDelete.action",method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		User user = (User)condition.toBeanResolver(User.class);
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		userService.deleteUser(user.getUserId());
		userVmService.delete(userVm);
		logService.insert(request, "删除用户("+userService.getUserByUserId(user.getUserId()).getUserName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	
	/**
	 * 重置密码
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/resetPassword.action",method = RequestMethod.GET)
	public JSONObject resetPassword(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		User user = userService.getUserByUserId(condition.getInteger("userId"));
		user.setUserPassword(MD5.GetMD5Code(MD5.GetMD5Code("123456")+MD5.GetMD5Code(user.getUserName())));
		userService.updateUser(user);
		json.put("success", true);
		logService.insert(request, "用户("+userService.getUserByUserId(user.getUserId()).getUserName()+")重置密码", json.getBoolean("success")?1:0);
		return json;
	}
	
}
	
