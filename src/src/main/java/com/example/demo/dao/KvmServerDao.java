package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.KvmServer;

public interface KvmServerDao {
	
	public List<KvmServer> allSelectLists(KvmServer kvmServer);

	public  void insert(KvmServer kvmServer);
	
	public KvmServer load(Integer kvmServerId);
	
	public void update(KvmServer kvmServer);
	
	public void delete(KvmServer kvmServer);
}
