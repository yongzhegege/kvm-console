package com.example.demo.empty;

import java.util.Date;


public class KvmServer{
	
	public Integer getKvmServerId() {
		return kvmServerId;
	}

	public void setKvmServerId(Integer kvmServerId) {
		this.kvmServerId = kvmServerId;
	}

	public String getKvmServerName() {
		return kvmServerName;
	}

	public void setKvmServerName(String kvmServerName) {
		this.kvmServerName = kvmServerName;
	}

	public String getKvmServerIp() {
		return kvmServerIp;
	}

	public void setKvmServerIp(String kvmServerIp) {
		this.kvmServerIp = kvmServerIp;
	}

	public String getKvmServerPassword() {
		return kvmServerPassword;
	}

	public void setKvmServerPassword(String kvmServerPassword) {
		this.kvmServerPassword = kvmServerPassword;
	}

	public Integer getVmNum() {
		return vmNum;
	}

	public void setVmNum(Integer vmNum) {
		this.vmNum = vmNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getKvmServerUser() {
		return kvmServerUser;
	}

	public void setKvmServerUser(String kvmServerUser) {
		this.kvmServerUser = kvmServerUser;
	}

	public String getKvmServerDesc() {
		return kvmServerDesc;
	}

	public void setKvmServerDesc(String kvmServerDesc) {
		this.kvmServerDesc = kvmServerDesc;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	private Integer kvmServerId;
	
	private String kvmServerName;
	
	private String kvmServerIp; 
	
	private String kvmServerPassword;
	
	private Integer vmNum;
	
	private Date createTime;
	
	private String kvmServerUser;
	
	private String kvmServerDesc;
	
	private Integer state;
}
