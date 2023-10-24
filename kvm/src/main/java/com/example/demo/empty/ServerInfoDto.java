package com.example.demo.empty;

import java.util.List;

public class ServerInfoDto {
	
	private String memory;
	
	private String persentMemory;
	
	private String disk;
	
	private String persentDisk;
	
	private String cpu;
	
	private String kernelVersion;
	
	private List<String> serverVersion;
	
	private boolean firewallOpen;
	
	private long activeTime;

	public String getMemory() {
		return memory;
	}

	public String getPersentMemory() {
		return persentMemory;
	}

	public void setPersentMemory(String persentMemory) {
		this.persentMemory = persentMemory;
	}


	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		Double useCpu = 100 - Double.parseDouble(cpu);
		this.cpu = useCpu.toString().split("[.]")[0];
	}

	public String getPersentDisk() {
		return persentDisk;
	}

	public void setPersentDisk(String persentDisk) {
		this.persentDisk = persentDisk;
	}

	public List<String> getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(List<String> serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getKernelVersion() {
		return kernelVersion;
	}

	public void setKernelVersion(String kernelVersion) {
		this.kernelVersion = kernelVersion;
	}

	public boolean isFirewallOpen() {
		return firewallOpen;
	}

	public void setFirewallOpen(boolean firewallOpen) {
		this.firewallOpen = firewallOpen;
	}

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}





}
