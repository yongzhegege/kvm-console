package com.example.demo.empty;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class User {
	
	@Excel(name = "ID",orderNum = "1")
	private Integer userId;
	
	@Excel(name = "登录名",orderNum = "2")
	private String userName;
	
	@Excel(name = "姓名",orderNum = "3")
	private String realName;
	
	public Integer getUserParentId() {
		return userParentId;
	}

	public void setUserParentId(Integer userParentId) {
		this.userParentId = userParentId;
	}

	private Integer userParentId = 0;
	
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
