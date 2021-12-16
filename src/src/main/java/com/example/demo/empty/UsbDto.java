package com.example.demo.empty;

public class UsbDto {
	
	 private String bus;
	  
	  private String device;
	  
	  public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
	}

	private String uId;
	  
	  private String uName;
	  
	  public String getBus() {
	    return this.bus;
	  }
	  
	  public void setBus(String bus) {
	    this.bus = bus;
	  }
	  
	  public String getDevice() {
	    return this.device;
	  }
	  
	  public void setDevice(String device) {
	    this.device = device;
	  }

}
