package com.example.demo.empty;

public class User {
	
	private Integer userId;
	
	private String userName;
	
	private String realName;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getSambaSwitch() {
		return sambaSwitch;
	}

	public void setSambaSwitch(Integer sambaSwitch) {
		this.sambaSwitch = sambaSwitch;
	}

	private String userPassword;
	
	private Integer level;
	
	private Integer sambaSwitch;

}
