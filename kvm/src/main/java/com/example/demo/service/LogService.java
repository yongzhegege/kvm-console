package com.example.demo.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.empty.Log;

public interface LogService {
	
	public void insert(HttpServletRequest request,String content,Integer result);
	
	public void delete(Log log);
	
	public List<Log> allSelectLists(Log log);

}
