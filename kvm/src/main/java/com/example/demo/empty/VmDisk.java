package com.example.demo.empty;

public class VmDisk {
	
	private Integer diskId;
	
	private String diskPath;
	
	private Integer diskSize;
	
	
	public Integer getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(Integer diskSize) {
		this.diskSize = diskSize;
	}

	public Integer getDiskId() {
		return diskId;
	}

	public void setDiskId(Integer diskId) {
		this.diskId = diskId;
	}

	public String getDiskPath() {
		return diskPath;
	}

	public void setDiskPath(String diskPath) {
		this.diskPath = diskPath;
	}

	public String getDiskType() {
		return diskType;
	}

	public void setDiskType(String diskType) {
		this.diskType = diskType;
	}

	private String diskType;
	
}
