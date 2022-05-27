package com.example.demo.empty;

public class FileSys {
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getCanUse() {
		return canUse;
	}

	public void setCanUse(String canUse) {
		this.canUse = canUse;
	}

	public String getUsePersent() {
		return usePersent;
	}

	public void setUsePersent(String usePersent) {
		this.usePersent = usePersent;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	private String cap;
	
	private String used;
	
	private String canUse;
	
	private String usePersent;
	
	private String mountPoint;

}
