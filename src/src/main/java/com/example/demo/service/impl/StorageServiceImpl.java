package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.StorageDao;
import com.example.demo.empty.Storage;
import com.example.demo.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {
	
	@Autowired
	private StorageDao storageDao;
	
	public void insert(Storage storage) {
		storageDao.insert(storage);
	}
	
	public void delete(Integer storageId) {
		storageDao.delete(storageId);
	}

	public void update(Storage storage) {
		storageDao.update(storage);
	}
	
	public List<Storage> allSelectLists(Storage storage){
		return storageDao.allSelectLists(storage);
	}
	
	public Storage load(Integer storageId) {
		return storageDao.load(storageId);
	}

}
