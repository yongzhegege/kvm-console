package com.example.demo.service;

import java.util.List;

import com.example.demo.empty.KvmTemplate;


public interface KvmTemplateService {
	
	public List<KvmTemplate> getAllKvmTemplate();
	
	public KvmTemplate load(Integer templateId);
	
	public void delete(Integer templateId);
	
	public void update(KvmTemplate kvmTemplate);
	
	public void insert(KvmTemplate kvmTemplate);

}
