package com.example.demo.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.Vm;
import com.example.demo.vo.CommonVO;

public interface VmService {
	
	public void insert(Vm vm);
	
	public void insertMoreVm(List<Vm> vmList);
	
	public void deleteVmByKvmServerId(Integer kvmServerId);
	
	public List<Vm> getAllVm(Integer recycle);
	
	public List<Vm> getVmByKvmServerId(Integer kvmServerId);
	
	public void update(Vm vm);
	
	public JSONObject createVm(CommonVO vo);
	
	public Integer getVncPort(Integer kvmServerId);
	
	public JSONObject editVm(CommonVO vo);
	
	public Vm load(Integer vmId);

	public JSONObject opVm(String[] vmId, Integer option);

	public JSONObject delete(String[] vmIds, Integer isDelDisk);
	
	public void deleteRecord(Integer vmId);

	public JSONObject openVm(Integer vmId, String localName);
	
}
