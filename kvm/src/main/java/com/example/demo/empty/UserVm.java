package com.example.demo.empty;

public class UserVm {
	
	private Integer userVmId;
	
	private Integer userId;
	
	private String vmName;
	
	private Integer agreement;  //协议 1RDP 2SSH 3VNC 4TELNET
	
	private String hostName; 
	
	private Integer port;
	
	private String username;
	
	private String password;
	
	private String params;
	
	private String desc;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public Integer getAgreement() {
		return agreement;
	}

	public void setAgreement(Integer agreement) {
		this.agreement = agreement;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Integer getUserVmId() {
		return userVmId;
	}

	public void setUserVmId(Integer userVmId) {
		this.userVmId = userVmId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
}
