package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.KvmTemplate;

public interface KvmTemplateDao {
	
	public List<KvmTemplate> allSelectLists(KvmTemplate template);

	public KvmTemplate load(Integer templateId);
	
	public void delete(Integer templateId);
	
	public void update(KvmTemplate template);
	
	public void insert(KvmTemplate template);
}
