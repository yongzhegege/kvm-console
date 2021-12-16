package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.Storage;

public interface StorageDao {
	
	public void insert(Storage storage);
	
	public void delete(Integer storageId);

	public void update(Storage storage);
	
	public List<Storage> allSelectLists(Storage storage);
	
	public Storage load(Integer storageId);
	
}
