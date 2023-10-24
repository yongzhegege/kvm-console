package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.UserVm;

public interface UserVmDao {
	
	public void insert(UserVm userVm);
	
	public void delete(UserVm userVm);
	
	public void update(UserVm userVm);
	
	public List<UserVm> allSelectLists(UserVm userVm);
	
	public UserVm load(Integer userVmId);

	public List<UserVm> doPageSelectUnbind(UserVm userVm);
}
