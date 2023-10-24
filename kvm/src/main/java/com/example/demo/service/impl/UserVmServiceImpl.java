package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserVmDao;
import com.example.demo.empty.UserVm;
import com.example.demo.service.UserVmService;

@Service
public class UserVmServiceImpl implements UserVmService {

	@Autowired
	private UserVmDao userVmDao;
	
	@Override
	public void insert(UserVm userVm) {
		// TODO Auto-generated method stub
		userVmDao.insert(userVm);
	}

	@Override
	public void delete(UserVm userVm) {
		// TODO Auto-generated method stub
		userVmDao.delete(userVm);
	}

	@Override
	public void update(UserVm userVm) {
		// TODO Auto-generated method stub
		userVmDao.update(userVm);
	}

	@Override
	public List<UserVm> allSelectLists(UserVm userVm) {
		// TODO Auto-generated method stub
		return userVmDao.allSelectLists(userVm);
	}

	@Override
	public UserVm load(Integer userVmId) {
		// TODO Auto-generated method stub
		return userVmDao.load(userVmId);
	}

	@Override
	public List<UserVm> doPageSelectUnbind(UserVm userVm) {
		// TODO Auto-generated method stub
		return userVmDao.doPageSelectUnbind(userVm);
	}

}
