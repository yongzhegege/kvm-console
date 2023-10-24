package com.example.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.Log;
import com.example.demo.service.LogService;
import com.example.demo.vo.CommonVO;

@RequestMapping("/log")
@Controller
public class LogController {
	
	@Autowired
	private LogService logService;
	
	@ResponseBody
	@RequestMapping(path = "/doPageSelect.action", method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		Log log = (Log)condition.toBeanResolver(Log.class);
		JSONObject resJson = new JSONObject();
		List<Log> logList = logService.allSelectLists(log);
		resJson.put("code", 0);
		resJson.put("data", logList);
		return resJson;
	}
	
}
