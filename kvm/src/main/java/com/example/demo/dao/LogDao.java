package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.Log;

public interface LogDao {

	public void insert(Log log);
	
	public void delete(Log log);
	
	public List<Log> allSelectLists(Log log);
	
}
