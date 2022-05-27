package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.KvmTemplateDao;
import com.example.demo.empty.KvmTemplate;
import com.example.demo.service.KvmTemplateService;

@Service
public  class KvmTemplateServiceImpl implements KvmTemplateService {
	
	@Autowired
	private KvmTemplateDao kvmTemplateDao;

	@Override
	public List<KvmTemplate> getAllKvmTemplate() {
		return kvmTemplateDao.allSelectLists(null);
	}
	
	@Override
	public KvmTemplate load(Integer Id) {
		return kvmTemplateDao.load(Id);
	}

	@Override
	public void delete(Integer templateId) {
		kvmTemplateDao.delete(templateId);
	}

	@Override
	public void update(KvmTemplate kvmTemplate) {
		kvmTemplateDao.update(kvmTemplate);
	}

	@Override
	public void insert(KvmTemplate kvmTemplate) {
		kvmTemplateDao.insert(kvmTemplate);
	}

}
