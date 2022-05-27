package com.example.demo.empty;

public class Log {
	
	private Integer logId;
	
	private String userName;
	
	private String content;
	
	private  Integer result;
	
	private String createTime;
	
	private String hostIp;

	public Integer getLogId() {
		return logId;
	}

	public Log() {
		super();
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getUserName() {
		return userName;
	}
	
	private String searchInfo;

	public Log(String userName, String content, Integer result, String createTime,String hostIp) {
		super();
		this.userName = userName;
		this.content = content;
		this.result = result;
		this.createTime = createTime;
		this.hostIp = hostIp;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSearchInfo() {
		return searchInfo;
	}

	public void setSearchInfo(String searchInfo) {
		this.searchInfo = searchInfo;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	
	
}
