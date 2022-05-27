package com.example.demo.service.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.LogDao;
import com.example.demo.empty.Log;
import com.example.demo.empty.User;
import com.example.demo.http.WebRequest;
import com.example.demo.service.LogService;
import com.example.demo.util.DateTimeUtils;

@Service
public class LogServiceImpl implements LogService {
	
	@Autowired
	private LogDao logDao;


	@Override
	public void delete(Log log) {
		logDao.delete(log);
	}

	@Override
	public List<Log> allSelectLists(Log log) {
		return logDao.allSelectLists(log);
	}
	
	@Override
	public void insert(HttpServletRequest request,String content,Integer result) {
		String createTime = DateTimeUtils.getCurrentTime();
		String userName = ((User)(request.getSession().getAttribute("user"))).getUserName();
		String ip = "";
		try {
			ip = WebRequest.getIpAddress(request);
		} catch (IOException e) {}
		Log log = new Log(userName, content, result, createTime,ip);
		logDao.insert(log);
	}
	
}
