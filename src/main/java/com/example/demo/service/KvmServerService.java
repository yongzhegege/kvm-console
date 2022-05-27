package com.example.demo.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.KvmServer;

public interface KvmServerService{
	
	public JSONObject insertKvmServer(KvmServer kvmServer);
	
	public KvmServer queryKvmServerByIp(String serverIp);
	
	public KvmServer queryKvmServerById(Integer kvmServerId);
	
	public List<KvmServer> getAllKvmServer();
	
	public void update(KvmServer kvmServer);
	
	public JSONObject delete(Integer kvmServerId);
	
	public JSONObject getIsoAndDirverAndNetwork(Integer kvmServerId);
	
	public JSONObject uploadFile(Integer type,String url);

}
