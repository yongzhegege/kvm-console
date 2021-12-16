package com.example.demo.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.VmDao;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.Vm;
import com.example.demo.service.KvmServerService;
import com.example.demo.service.VmService;
import com.example.demo.util.LibvirtUtil;
import com.example.demo.util.MacUtil;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.SshUtil;
import com.example.demo.vo.CommonVO;
import com.jcraft.jsch.JSchException;

@Service
public class VmServiceImpl implements VmService {
	
	private String tokenPath = PropertiesUtil.get("tokenPath");
	
	@Autowired
	private VmDao vmDao;
	
	@Autowired
	private KvmServerService kvmServerService;

	@Override
	public void insert(Vm vm) {
		vmDao.insert(vm);
	}

	@Override
	public void insertMoreVm(List<Vm> vmList) {
		for(Vm vm : vmList) {
			vmDao.insert(vm);
		}
	}

	@Override
	public void deleteVmByKvmServerId(Integer kvmServerId) {
		vmDao.deleteByKvmServerId(kvmServerId);
	}

	@Override
	public List<Vm> getAllVm(Integer recycle) {
		Vm vm = new Vm();
		vm.setVmRecycle(recycle);
		return vmDao.allSelectLists(vm);
	}

	@Override
	public void update(Vm vm) {
		vmDao.update(vm);
	}

	@Override
	public JSONObject createVm(CommonVO condition) {
		JSONObject returnMsg = new JSONObject();
		//获取相关参数
		Integer kvmServerId = condition.getInteger("kvmServerId");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		String vmUuid = UUID.randomUUID().toString();
		String vmName = condition.getString("vmName");
		String vmIso = condition.getString("vmIso");
		String vmDriver = condition.getString("vmDriver");
		String vmNetwork = condition.getString("vmNetwork");
		Integer vmMemory = condition.getInteger("vmMemory");
		Integer vmDisk = condition.getInteger("vmDisk");
		Integer vmCpu = condition.getInteger("vmCpu");
		String vmDefaultStart = condition.getString("vmDefaultStart"); 
		String vmType = condition.getString("vmType");
		String xmlPath = PropertiesUtil.get("vmXmlPath");
		if(vmType.equals("legacy_deb")) {
			xmlPath += "x86_64-legacy-deb.xml";
			vmType = "Legacy";
		}else if(vmType.equals("uefi_deb")) {
			xmlPath += "x86_64-uefi-deb.xml";
			vmType = "UEFI";
		}else if(vmType.equals("legacy_rpm")) {
			xmlPath += "x86_64-legacy-rpm.xml";
			vmType = "Legacy";
		}else if(vmType.equals("uefi_rpm")) {
			xmlPath += "x86_64-uefi-rpm.xml";
			vmType = "UEFI";
		}
		String mac = MacUtil.getMacAddr();
		Integer vncPort = getVncPort(kvmServerId);
		//创建qcow2文件
		String command = "cd /var/lib/libvirt/images/;sudo mkdir -p "+vmName+"/base "+vmName+"/data;sudo qemu-img create -f qcow2 "+vmName+"/base/"+vmName+".qcow2 "+vmDisk+"G";
		String vmImage = "/var/lib/libvirt/images/"+vmName+"/base/"+vmName+".qcow2"; 
		try {
			SshUtil.execute(kvmServer, command);
		} catch (JSchException e) {
			returnMsg.put("success", false);
			returnMsg.put("success", "ssh连接异常，请重试！");
			return returnMsg;
		}
		//修改配置文件
		SAXReader reader = new SAXReader();
		Writer out;
		List<Element> elements ;
		Document doc;
		try {
			doc = reader.read(xmlPath);
			Element root = doc.getRootElement(); 
			elements= root.elements();
			out = new PrintWriter(xmlPath, "UTF-8");
			OutputFormat format = new OutputFormat("\t", true);  
	        format.setTrimText(true);
	        XMLWriter writer = new XMLWriter(out, format);
			for(Element e:elements){
				if(e.getName().equals("name")){
					e.setText(vmName);
				}else if(e.getName().equals("uuid")){
					e.setText(vmUuid);
				}else if(e.getName().equals("description")){
					e.setText(vmName);
				}else if(e.getName().equals("memory")){
					e.setText(String.valueOf((vmMemory*1048576)));
				}else if(e.getName().equals("currentMemory")){
					e.setText(String.valueOf((vmMemory*1048576)));
				}else if(e.getName().equals("vcpu")){
					e.setText(String.valueOf(vmCpu));
				}else if(e.getName().equals("os")){
					List<Element> elements2=e.elements("boot");
					for(Element E:elements2){
						E.attribute("dev").setValue(vmDefaultStart);
					 }
				}
				else if(e.getName().equals("devices")){
					 List<Element> elements2 = e.elements("disk");
					 for(Element E:elements2){
						 if(E.element("driver").attribute("type").getValue().equals("qcow2")){
							 E.element("source").attribute("file").setValue(vmImage);
						 }
					 }
					 e.element("interface").element("mac").attribute("address").setValue(mac);
					 e.element("interface").element("source").attribute("bridge").setValue(vmNetwork.substring(0,vmNetwork.indexOf("(")));
					 e.element("graphics").attribute("port").setValue(String.valueOf(vncPort));
				}
			}
			writer.write(doc);
			 out.close();  
			 writer.close(); 
			//读取配置文件
			 String filepath = xmlPath;
			 SAXReader read=new SAXReader();
			String xmlDesc = null;
			Document docu = read.read(new File(filepath));
			xmlDesc=docu.asXML();
				Domain domain=null;
				Connect conn = LibvirtUtil.getConnect(kvmServer);
				domain=conn.domainDefineXML(xmlDesc);
				if(domain!=null){
					//判断是否安装镜像
					if(!vmIso.equals("0")){
						SshUtil.execute(kvmServer, "sudo virsh attach-disk "+domain.getName()+" /home/isos/"+vmIso+" sda --targetbus sata --driver qemu --type cdrom --mode readonly --config");
					}
					//判断是否安装驱动
					if(!vmDriver.equals("0")){
						SshUtil.execute(kvmServer, "sudo virsh attach-disk "+domain.getName()+" /home/drivers/"+vmDriver+" sdb --targetbus sata --driver qemu --type cdrom --mode readonly --config");
					}
					//写入token
					String writeCom = "sudo echo '"+vmName+": "+kvmServer.getKvmServerIp()+":"+vncPort+"' >>   "+ tokenPath;
					SshUtil.executeLocal(writeCom);
					//写入VM表
					Vm vm = new Vm(vmName, vmName, "", "", mac, 0, kvmServerId, vncPort, vmIso, vmDriver, vmMemory, vmDisk, vmCpu, vmNetwork, vmUuid,vmDefaultStart,vmType);
					vmDao.insert(vm);
					//更改kvmServer表vmNum
					kvmServer.setVmNum(kvmServer.getVmNum()+1);
					kvmServerService.update(kvmServer);
					returnMsg.put("success", true);
					return returnMsg;
				}else{
					returnMsg.put("success", false);
					returnMsg.put("success", "创建失败，请联系管理员！");
					return returnMsg;
				}
		} catch (Exception e) {
			returnMsg.put("success", false);
			returnMsg.put("message", "读取虚拟机配置文件异常，请联系管理员！");
			return returnMsg;
		}
	}

	@Override
	public Integer getVncPort(Integer kvmServerId) {
		Integer maxVncPort = vmDao.getMaxVncPort();
		if(maxVncPort == null) {
			maxVncPort = 5900;
		}
		return maxVncPort == 0?5900:maxVncPort + 1;
	}

	@Override
	public JSONObject editVm(CommonVO condition) {
		JSONObject returnMsg = new JSONObject();
		Integer vmId = condition.getInteger("vmId");
		int vmMemory = condition.getInteger("vmMemory");
		int vmCpu = condition.getInteger("vmCpu");
		String vmNetWork = condition.getString("vmNetwork");
		String vmDefaultStart = condition.getString("vmDefaultStart");
		String vmIso = condition.getString("vmIso");
		String vmDriver = condition.getString("vmDriver");
		Vm oldVM = load(vmId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(oldVM.getKvmServerId());
		String command = "";
		String command1 = "";
		//修改内存
		if(oldVM.getVmMemory() != vmMemory){
			command += "sudo sed -i 's/" + (oldVM.getVmMemory()* 1048576) + "/" + (vmMemory* 1048576) + "/' /etc/libvirt/qemu/" + oldVM.getVmName() + ".xml;";
			oldVM.setVmMemory(vmMemory);
		}
		//修改CPU
		if(oldVM.getVmCpu() != vmCpu){
			command += "sudo sed -i '13d' /etc/libvirt/qemu/" + oldVM.getVmName() + ".xml;";
		    command += "sudo sed '12 a<vcpu placement=\"static\">" + vmCpu + "</vcpu>' -i /etc/libvirt/qemu/" + oldVM.getVmName() + ".xml;";
		    oldVM.setVmCpu(vmCpu);
		}
		//修改网卡
		command += "sudo sed -i 's/" + oldVM.getVmNetwork().substring(0, oldVM.getVmNetwork().indexOf("(")) + "/" + vmNetWork.substring(0, vmNetWork.indexOf("(")) + "/' /etc/libvirt/qemu/" + oldVM.getVmName() + ".xml;";
		oldVM.setVmNetwork(vmNetWork);
		//修改启动方式
		if(!oldVM.getVmDefaultStart().equals(vmDefaultStart)){
			command += "sed -i '/<boot dev=/d' /etc/libvirt/qemu/"+oldVM.getVmName() + ".xml;";
			command += "sed -i \"/<bootmenu/i <boot dev='"+vmDefaultStart+"'/>\" /etc/libvirt/qemu/"+oldVM.getVmName() + ".xml;";
			oldVM.setVmDefaultStart(vmDefaultStart);
		}
		try {
			//修改ISO
			if(!oldVM.getVmIso().equals(vmIso)){
				if(oldVM.getVmIso().equals("0")){
					command1 += "sudo virsh attach-disk "+oldVM.getVmName()+" /home/isos/"+vmIso+" sda --targetbus sata --driver qemu --type cdrom --mode readonly --config;";
				}
				if(vmIso.equals("0")){
					command1 += "sudo virsh  detach-disk "+oldVM.getVmName()+" sda --config;";
				}
				if(!oldVM.getVmIso().equals("0") && !vmIso.equals("0")){
					command1 += "sudo virsh  detach-disk "+oldVM.getVmName()+" sda --config;sudo virsh attach-disk "+oldVM.getVmName()+" /home/isos/"+vmIso+" sda --targetbus sata --driver qemu --type cdrom --mode readonly --config;";
				}
				oldVM.setVmIso(vmIso);
			} 
			//修改驱动
			if(!oldVM.getVmDriver().equals(vmDriver)){
				if(oldVM.getVmDriver().equals("0")){
					command1 += "sudo virsh attach-disk " + oldVM.getVmName() + " /home/drivers/" + vmDriver + " sdb --targetbus sata --driver qemu --type cdrom --mode readonly --config;";
				}
				if(vmDriver.equals("0")){
					command1 += "sudo virsh  detach-disk "+oldVM.getVmName()+" sdb --config;";
				}
				if(!oldVM.getVmDriver().equals("0") && !vmDriver.equals("0")){
					command1 += "sudo virsh  detach-disk "+oldVM.getVmName()+" sdb --config;sudo virsh attach-disk " + oldVM.getVmName() + " /home/drivers/" + vmDriver + " sdb --targetbus sata --driver qemu --type cdrom --mode readonly --config;";
				}
				oldVM.setVmDriver(vmDriver);
			}
			//重新定义虚拟机
			command += "sudo virsh define /etc/libvirt/qemu/"+oldVM.getVmName()+".xml";
			SshUtil.execute(kvmServer,command1 + command);
			this.update(oldVM);
			returnMsg.put("success", true);
		} catch (JSchException e) {
			returnMsg.put("success", false);
			returnMsg.put("message", "ssh连接异常，请重试！");
		}
		return returnMsg;
	}

	@Override
	public Vm load(Integer vmId) {
		return vmDao.load(vmId);
	}

	@Override
	public JSONObject opVm(String[] vmId, Integer option) {
		JSONObject returnMsg = new JSONObject();
		KvmServer kvmServer = null;
		Connect connect = null;
		for(String id : vmId) {
			try {
				Vm vm = load(Integer.valueOf(id));
				if(kvmServer == null) {
					kvmServer = kvmServerService.queryKvmServerById(vm.getKvmServerId());
				}
				if(connect == null) {
					connect =  LibvirtUtil.getConnect(kvmServer);
				}
				Domain domain = connect.domainLookupByName(vm.getVmName());
				if(option == 0){ //开机
					domain.create();
					if(domain.isActive() == 1){
						vm.setVmState(1);
						update(vm);
					}
				}else if(option == 1){ //关机
					domain.shutdown();
					vm.setVmState(0);
					update(vm);
				}else if(option == 2) { //强制关机
					domain.destroy();
					vm.setVmState(0);
					update(vm);
				}else if(option == 3) { //重启
					if(domain.isActive()  == 1) {
						domain.reboot(0);
					}else {
						domain.create();
					}
					vm.setVmState(1);
					update(vm);
				}else if(option == 4) { //强制重启
					if(domain.isActive()  == 1) {
						domain.destroy();
						domain.create();
					}else {
						domain.create();
					}
					vm.setVmState(1);
					update(vm);
				}
			} catch (LibvirtException e) {
				LibvirtUtil.removeConnect(kvmServer);
				returnMsg.put("success", false);
				returnMsg.put("message", "libvirt连接异常，请重试！");
				return returnMsg;
			}
		}
		returnMsg.put("success", true);
		return returnMsg;
	}

	@Override
	public JSONObject delete(String[] vmIds, Integer isDelDisk) {
		JSONObject returnMsg = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(load(Integer.valueOf(vmIds[0])).getKvmServerId());
		try {
			Connect connect = LibvirtUtil.getConnect(kvmServer);
			String sshCom = "";
			String localCom = "";
			for(String vmId : vmIds) {
				Vm vm = load(Integer.valueOf(vmId));
				Domain domain = connect.domainLookupByName(vm.getVmName());
				if(domain.isActive() == 1){
					domain.destroy();
				}else{
					sshCom += "sudo virsh undefine "+vm.getVmName() + " --nvram;"; //UNDEFINE虚拟机
					if(isDelDisk == 1){
						sshCom +=  "sudo rm -rf /var/lib/libvirt/images/"+vm.getVmName()+";"; //删除磁盘文件
					}
					localCom += "sudo sed -i '/"+domain.getName()+":/d' "+PropertiesUtil.get("tokenPath")+";"; // 删除VNC TOKEN
				}
				vmDao.delete(vm.getVmId());
			}
			SshUtil.execute(kvmServer, sshCom);
			SshUtil.executeLocal(localCom);
			//更新KVM服务器虚拟机数量
			kvmServer.setVmNum(kvmServer.getVmNum()-vmIds.length);
			kvmServerService.update(kvmServer);
			returnMsg.put("success", true);
			return returnMsg;
		} catch (Exception e) {
			LibvirtUtil.removeConnect(kvmServer);
			returnMsg.put("success", false);
			returnMsg.put("message", "libvirt连接异常，请重试！");
			return returnMsg;
		}
	}

	@Override
	public JSONObject openVm(Integer vmId,String localName) {
		JSONObject returnMsg = new JSONObject();
		Vm vm = load(vmId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(vm.getKvmServerId());
		String tokenStr = vm.getVmUuid().replace("-", "")+": "+kvmServer.getKvmServerIp()+":"+vm.getVncPort();
		String writeTokenCom = "sudo echo '"+tokenStr+"' >>   "+tokenPath;
		try {
			//判断TOKEN是否存在 如不存在则写入
			String token = SshUtil.executeLocalRerurn("sudo cat "+tokenPath);
			if(!token.contains(vm.getVmUuid().replace("-", ""))) {
				SshUtil.executeLocal(writeTokenCom);
			}
		} catch (IOException e) {
			returnMsg.put("success", false);
			returnMsg.put("message", "执行本地命令失败，请重试！");
			return returnMsg;
		}
		returnMsg.put("success", true);
		returnMsg.put("url", localName+":5080/vnc.html?path=websockify/?token="+vm.getVmUuid().replace("-", ""));
		return returnMsg;
	}

	@Override
	public List<Vm> getVmByKvmServerId(Integer kvmServerId) {
		Vm vm = new Vm();
		vm.setKvmServerId(kvmServerId);
		return vmDao.allSelectLists(vm);
	}

	@Override
	public void deleteRecord(Integer vmId) {
		vmDao.delete(vmId);
	}
}
