package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParamSetupDao;
import com.example.demo.empty.ParamSetup;
import com.example.demo.service.ParamSetupService;

@Service
public class ParamSetupServiceImpl implements ParamSetupService {
	
	@Autowired
	private ParamSetupDao paramSetupDao;

	@Override
	public List<ParamSetup> allSelectLists(ParamSetup param) {
		// TODO Auto-generated method stub
		return paramSetupDao.allSelectLists(param);
	}

}
