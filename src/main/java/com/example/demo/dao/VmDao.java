package com.example.demo.dao;

import java.util.List;

import com.example.demo.empty.Vm;

public interface VmDao {
	
	public void insert(Vm vm);
	
	public void deleteByKvmServerId(Integer kvmServerId);
	
	public List<Vm> allSelectLists(Vm vm);
	
	public void update(Vm vm);
	
	public Integer getMaxVncPort();
	
	public Vm load(Integer vmId);
	
	public void delete(Integer vmId);

}
