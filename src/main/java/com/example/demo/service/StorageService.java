package com.example.demo.service;

import java.util.List;

import com.example.demo.empty.Storage;

public interface StorageService {
	
	public void insert(Storage storage);
	
	public void delete(Integer storageId);

	public void update(Storage storage);
	
	public List<Storage> allSelectLists(Storage storage);
	
	public Storage load(Integer storageId);
	
}
