package com.example.demo.empty;

public class Vm{
	
	public Vm() {
		super();
	}
	private Integer vmId;
	private String vmName;
	private Integer vmDiskMode;
	public Integer getVmId() {
		return vmId;
	}
	public void setVmId(Integer vmId) {
		this.vmId = vmId;
	}
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public String getVmShowName() {
		return vmShowName;
	}
	public void setVmShowName(String vmShowName) {
		this.vmShowName = vmShowName;
	}
	public String getVmDesc() {
		return vmDesc;
	}
	public void setVmDesc(String vmDesc) {
		this.vmDesc = vmDesc;
	}
	public String getVmDefaultStart() {
		return vmDefaultStart;
	}
	public void setVmDefaultStart(String vmDefaultStart) {
		this.vmDefaultStart = vmDefaultStart;
	}
	public Integer getVmUp() {
		return vmUp;
	}
	public void setVmUp(Integer vmUp) {
		this.vmUp = vmUp;
	}
	public Integer getVmDown() {
		return vmDown;
	}
	public void setVmDown(Integer vmDown) {
		this.vmDown = vmDown;
	}
	public Integer getVmRecycle() {
		return vmRecycle;
	}
	public void setVmRecycle(Integer vmRecycle) {
		this.vmRecycle = vmRecycle;
	}
	public String getVmType() {
		return vmType;
	}
	public void setVmType(String vmType) {
		this.vmType = vmType;
	}
	public Integer getAccountType() {
		return accountType;
	}
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
	public String getComputerAccount() {
		return computerAccount;
	}
	public void setComputerAccount(String computerAccount) {
		this.computerAccount = computerAccount;
	}
	public String getComputerPassword() {
		return computerPassword;
	}
	public void setComputerPassword(String computerPassword) {
		this.computerPassword = computerPassword;
	}
	public String getComputerDomainName() {
		return computerDomainName;
	}
	public void setComputerDomainName(String computerDomainName) {
		this.computerDomainName = computerDomainName;
	}
	public String getComputerPort() {
		return computerPort;
	}
	public void setComputerPort(String computerPort) {
		this.computerPort = computerPort;
	}
	public String getVmMac() {
		return vmMac;
	}
	public void setVmMac(String vmMac) {
		this.vmMac = vmMac;
	}
	public Integer getVmState() {
		return vmState;
	}
	public void setVmState(Integer vmState) {
		this.vmState = vmState;
	}
	public Integer getKvmServerId() {
		return kvmServerId;
	}
	public void setKvmServerId(Integer kvmServerId) {
		this.kvmServerId = kvmServerId;
	}
	public Integer getVncPort() {
		return vncPort;
	}
	public void setVncPort(Integer vncPort) {
		this.vncPort = vncPort;
	}
	public String getVmIso() {
		return vmIso;
	}
	public void setVmIso(String vmIso) {
		this.vmIso = vmIso;
	}
	public String getVmDriver() {
		return vmDriver;
	}
	public void setVmDriver(String vmDriver) {
		this.vmDriver = vmDriver;
	}
	public Integer getVmMemory() {
		return vmMemory;
	}
	public void setVmMemory(Integer vmMemory) {
		this.vmMemory = vmMemory;
	}
	public Integer getVmDisk() {
		return vmDisk;
	}
	public void setVmDisk(Integer vmDisk) {
		this.vmDisk = vmDisk;
	}
	public Integer getVmCpu() {
		return vmCpu;
	}
	public void setVmCpu(Integer vmCpu) {
		this.vmCpu = vmCpu;
	}
	public String getVmNetwork() {
		return vmNetwork;
	}
	public void setVmNetwork(String vmNetwork) {
		this.vmNetwork = vmNetwork;
	}
	public String getVmUuid() {
		return vmUuid;
	}
	public void setVmUuid(String vmUuid) {
		this.vmUuid = vmUuid;
	}
	public String getVmIp() {
		return vmIp;
	}
	public void setVmIp(String vmIp) {
		this.vmIp = vmIp;
	}
	private String vmShowName;
	private String vmDesc;
	private String vmDefaultStart;
	private Integer vmUp;
	private Integer vmDown;
	private Integer vmRecycle;
	private String vmType;
	private Integer accountType;
	private String computerAccount;
	private String computerPassword;
	private String computerDomainName;
	private String computerPort;
	private String vmMac;
	private Integer vmState;
	private Integer kvmServerId;
	private Integer vncPort;
	private String vmIso;
	private String vmDriver;
	private Integer vmMemory;
	private Integer vmDisk;
	private Integer vmCpu;
	private String vmNetwork;
	private String vmUuid;
	private String vmIp;
	public Vm(String vmName, String vmShowName, String vmDesc, String vmIp, String vmMac, int vmState, Integer kvmServerId,
			int vncPort, String vmIso, String vmDriver, int vmMemory, int vmDisk, int vmCpu, String vmNetwork,
			String vmUuid,String vmDefaultStart,String vmType,int vmDiskMode) {
		super();
		this.vmType = vmType;
		this.vmName = vmName;
		this.vmShowName = vmShowName;
		this.vmDesc = vmDesc;
		this.vmIp = vmIp;
		this.vmMac = vmMac;
		this.vmState = vmState;
		this.kvmServerId = kvmServerId;
		this.vncPort = vncPort;
		this.vmIso = vmIso;
		this.vmDriver = vmDriver;
		this.vmMemory = vmMemory;
		this.vmDisk = vmDisk;
		this.vmCpu = vmCpu;
		this.vmNetwork = vmNetwork;
		this.vmUuid = vmUuid;
		this.vmDefaultStart = vmDefaultStart;
		this.vmDiskMode = vmDiskMode;
	}
	public Integer getVmDiskMode() {
		return vmDiskMode;
	}
	public void setVmDiskMode(Integer vmDiskMode) {
		this.vmDiskMode = vmDiskMode;
	}
}
