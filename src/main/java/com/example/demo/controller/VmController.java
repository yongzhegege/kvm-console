package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.Vm;
import com.example.demo.empty.VmDisk;
import com.example.demo.empty.VmDiskSnap;
import com.example.demo.service.KvmServerService;
import com.example.demo.service.LogService;
import com.example.demo.service.VmService;
import com.example.demo.util.SshUtil;
import com.example.demo.vo.CommonVO;


@RequestMapping("/vm")
@Controller
public class VmController {

	@Autowired
	private VmService vmService;
	
	@Autowired
	private KvmServerService kvmServerService;
	
	@Autowired
	private LogService logService;
	
	
	/**
	 * 查询所有虚拟机并刷新IP
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doPageSelect.action", method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject resJson = new JSONObject();
		List<Vm> vmList = vmService.getAllVm(0);
		int online = 0;
		for(Vm vm : vmList) {
			if(vm.getVmState() == 1) {
				online ++;
			}
		}
		resJson.put("code", 0);
		resJson.put("data", vmList);
		resJson.put("count", vmList.size());
		resJson.put("online", online+"/"+vmList.size());
		resJson.put("success", true);
		return resJson;
	}
	
	/**
	 * 创建虚拟机
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doInsert.action",method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception{
		JSONObject json = vmService.createVm(condition);
		logService.insert(request, "创建虚拟机("+condition.getString("vmName")+")",  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 修改虚拟机
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/editVm.action",method= RequestMethod.POST)
	public JSONObject editVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception{
		JSONObject json = vmService.editVm(condition);
		logService.insert(request, "修改虚拟机("+condition.getString("vmName")+")",  json.getBoolean("success")?1:0);
		return  json;
	}
	
	/**
	 * 虚拟机开机/关机/强制关机/重启
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/opVm.action",method = RequestMethod.GET)
	public JSONObject opVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
			String[] vmId = condition.getString("vmId").split(",");
			Integer option = condition.getInteger("option");
			JSONObject json = vmService.opVm(vmId,option);
			String content = "";
			if(option == 0) {
				content = "开机";
			}else if(option == 1) {
				content = "关机";
			}else if(option == 2) {
				content = "强制关机";
			}else if(option == 3) {
				content = "重启";
			}else if(option == 3) {
				content = "强制重启";
			}
			logService.insert(request, "虚拟机("+json.getString("vmNames").substring(1)+")"+content,  json.getBoolean("success")?1:0);
			return json;
	}
	
	/**
	 * 删除虚拟机
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doDelete.action",method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		String[] vmIds = condition.getString("vmIds").split(",");
		Integer isDelDisk = condition.getInteger("disk");
		JSONObject json = vmService.delete(vmIds,isDelDisk);
		logService.insert(request, "删除虚拟机("+json.getString("vmNames").substring(1)+")",  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 虚拟机挂载/卸载USB
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/opUsb.action",method = RequestMethod.POST)
	public JSONObject opUsb(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer opType = condition.getInteger("opType");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		String vmName = vmService.load(condition.getInteger("vmId")).getVmName();
		String bus = condition.getString("bus");
		String device = condition.getString("device");
		String id = condition.getString("id");
		String xmlname = String.valueOf(id.split(":")[0]) + id.split(":")[1];
	    SshUtil.execute(kvmServer, "sudo cp /opt/kvm/vmXml/usb.xml /opt/kvm/vmXml/" + xmlname + ".xml");
	    bus = "0x" + String.format("%02d", new Object[] { Integer.valueOf(bus) });
	    device = "0x" + String.format("%02d", new Object[] { Integer.valueOf(device) });
	    String vendor1 = "0x" + id.split(":")[0];
	    String product1 = "0x" + id.split(":")[1];
	    String command = "sudo sed -i 's/vendor1/" + vendor1 + "/' /opt/kvm/vmXml/" + xmlname + ".xml;sudo sed -i 's/product1/" + product1 + "/' /opt/kvm/vmXml/" + xmlname + ".xml;sudo sed -i 's/bus1/" + bus + "/' /opt/kvm/vmXml/" + xmlname + ".xml;sudo sed -i 's/device1/" + device + "/' /opt/kvm/vmXml/" + xmlname + ".xml;";
	    SshUtil.execute(kvmServer, command);
	    String execute = "";
	    if(opType == 1) {  //挂载
	    	execute = SshUtil.executeReturn(kvmServer, "sudo virsh attach-device " + vmName + " /opt/kvm/vmXml/" + xmlname + ".xml  --persistent");
		    if (!execute.contains("successfully")) {
		    	json.put("success", false);
		    	json.put("message", "挂载失败，请检查该usb是否被其它虚拟机挂载或该虚拟机无端口可使用！");
		    }
			logService.insert(request, "虚拟机("+vmName+")挂载USB("+xmlname+")",  json.getBoolean("success")?1:0);
	    }else {  //卸载
	    	execute = SshUtil.executeReturn(kvmServer, "sudo virsh detach-device " + vmName + " /opt/kvm/vmXml/" + xmlname + ".xml --persistent");
		    if (!execute.contains("successfully")) {
		    	json.put("success", false);
		    	json.put("message", "卸载失败，请检查该usb是否被虚拟机挂载！");
		    }
			logService.insert(request, "虚拟机("+vmName+")卸载USB("+xmlname+")",  json.getBoolean("success")?1:0);
	    }
	    return json;
	}
	
	/**
	 * 获取虚拟机所有磁盘
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/diskList.action",method = RequestMethod.GET)
	public JSONObject getVmDiskList(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		Vm vm = vmService.load(condition.getInteger("vmId"));
		String result = SshUtil.executeReturn(kvmServer, "sudo virsh domblklist "+vm.getVmName());
		String[] lines = result.split("\n");
		List<VmDisk> diskList = new ArrayList<VmDisk>();
		for(int i=2; i<lines.length; i++) {
			if(lines[i].contains("qcow2")) {
				VmDisk disk = new VmDisk();
				disk.setDiskType(lines[i].replaceAll(" ", "").substring(0, 3));
				disk.setDiskPath(lines[i].replaceAll(" ", "").trim().substring(3));
		        disk.setDiskSize((int)(Long.valueOf(SshUtil.executeReturn(kvmServer, "sudo virsh domblkinfo "+vm.getVmName()+" "+disk.getDiskType()).split("\n")[0].split("Capacity:")[1].trim()).longValue() / 1073741824L));
				diskList.add(disk);
			}
		}
		json.put("data", diskList);
		json.put("code", 0);
		json.put("success", true);
		return json;
	}
	
	/**
	 * 获取磁盘所有快照
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/diskSnapList.action",method = RequestMethod.GET)
	public JSONObject diskSnapList(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		String diskPath = condition.getString("diskPath");
		String execute = SshUtil.executeReturn(kvmServer, "qemu-img snapshot -l "+diskPath);
		VmDiskSnap disksnap = null;
		List<VmDiskSnap> disksnaplist = new ArrayList<VmDiskSnap>();
	      for (int i = 2; i < (execute.split("\n")).length; i++) {
	        disksnap = new VmDiskSnap();
	        disksnap.setId(execute.split("\n")[i].substring(0, execute.split("\n")[i].length() - 36).trim().split(" ")[0]);
	        disksnap.setTag(execute.split("\n")[i].substring(0, execute.split("\n")[i].length() - 36).trim().split(" ")[(execute.split("\n")[i].substring(0, execute.split("\n")[i].length() - 40).trim().split(" ")).length - 1]);
	        disksnap.setVmsize(execute.split("\n")[i].substring(execute.split("\n")[i].length() - 38, execute.split("\n")[i].length() - 35));
	        disksnap.setDate(execute.split("\n")[i].substring(execute.split("\n")[i].length() - 34, execute.split("\n")[i].length() - 15));
	        disksnap.setVmclock(execute.split("\n")[i].substring(execute.split("\n")[i].length() - 12));
	        disksnaplist.add(disksnap);
	      } 
	      json.put("data", disksnaplist);
	      json.put("code", 0);
	      json.put("success", true);
	      return json;
	}
	
	/** 
	 * 卸载磁盘
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/detachDisk.action",method = RequestMethod.POST)
	public JSONObject detachDisk(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer deleteDisk = condition.getInteger("deleteDisk"); // 1删除物理文件 0不删除物理文件
		String diskPath = condition.getString("diskPath");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		Vm vm = vmService.load(condition.getInteger("vmId"));
		String diskType = condition.getString("diskType");
		 String message = SshUtil.executeReturn(kvmServer, "sudo virsh detach-disk --domain " + vm.getVmName() + " --target " + diskType + " --persistent");
		 if(message.contains("successfully")) {
			 if(deleteDisk == 1)
			 SshUtil.execute(kvmServer, "sudo rm -rf "+diskPath);
		 }else {
			 json.put("success", false);
			 json.put("message", message);
		 }
		logService.insert(request, "虚拟机("+vm.getVmName()+")卸载磁盘("+diskPath+")",  json.getBoolean("success")?1:0);
		 return json;
	}
	
	/**
	 * 磁盘扩容
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/expansionDisk.action",method = RequestMethod.POST)
	public JSONObject expansionDisk(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		String diskPath = condition.getString("diskPath");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		Integer expansionSize = condition.getInteger("expansionSize");
		String message = SshUtil.executeReturn(kvmServer, "sudo qemu-img resize " + diskPath + " +" + expansionSize + "G");
		if(!message.contains("resized")) {
			json.put("success", false);
			json.put("message", message);
		}
		logService.insert(request, "磁盘("+diskPath+")扩容"+expansionSize+"G",  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 添加数据盘
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/addDataDisk.action",method = RequestMethod.POST)
	public JSONObject addDataDisk(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		Vm vm = vmService.load(condition.getInteger("vmId"));
		String diskType = condition.getString("diskType");
		Integer diskSize = condition.getInteger("diskSize");
		String dname = vm.getVmName()+"_"+diskType+".qcow2";
		SshUtil.execute(kvmServer, "sudo qemu-img create -f qcow2 /var/lib/libvirt/images/" + vm.getVmName() +"/data/" + dname + " " + diskSize + "G");
		String message = SshUtil.executeReturn(kvmServer, "sudo virsh attach-disk --domain " + vm.getVmName() + " --source /var/lib/libvirt/images/" + vm.getVmName() +"/data/" + dname + " --target " + diskType + " --targetbus virtio --driver qemu --subdriver qcow2 --sourcetype file --cache none --persistent");
		if(!message.contains("successfully")) {
			json.put("success", false);
			json.put("message", message);
		}
		logService.insert(request, "虚拟机("+vm.getVmName()+")添加数据盘("+dname+" "+diskSize+"G)",  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 磁盘快照创建1  恢复2  删除 3
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/optionDiskSnap.action",method = RequestMethod.POST)
	public JSONObject optionDiskSnap(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer option = condition.getInteger("option");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		String diskPath = condition.getString("diskPath");
		String snapName = condition.getString("snapName");
		String parameter = "";
		String content = "磁盘("+diskPath+")";
		if(option == 1) {
			parameter = "-c";
			content += "创建快照";
		}else if(option == 2) {
			parameter = "-a";
			content += "恢复快照";
		}else if(option == 3) {
			parameter = "-d";
			content += "删除快照";
		}
		SshUtil.execute(kvmServer, "sudo qemu-img snapshot "+ parameter +" " + snapName + " " + diskPath);
		logService.insert(request, content,  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 显示虚拟机XML
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/showXml.action",method = RequestMethod.POST)
	public JSONObject showXml(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request){
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer kvmServerId = condition.getInteger("kvmServerId");
		Integer vmId = condition.getInteger("vmId");
		Vm vm = vmService.load(vmId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		String xml = "";
		try {
			xml = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+vm.getVmName()+".xml");
		} catch (Exception e) {
			json.put("success", false);
			json.put("message","SSH指令发送异常，请重试！");
		}
		json.put("xml",xml);
		return json;
	}
	
	/**
	 * 设置/取消开机自启
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/autoStart.action",method = RequestMethod.POST)
	public JSONObject autoStart(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		String[] vmIds = condition.getString("vmIds").split(",");
		Integer option  = condition.getInteger("option");
		String command = "";
		String vmNames = "";
		String content = "";
		for(String vmId : vmIds) {
			Vm vm = vmService.load(Integer.valueOf(vmId));
			vmNames += vm.getVmName();
			if(option  == 0) {  //取消开机自启
				command += "sudo virsh autostart --disable "+vm.getVmName()+";";
			}else { //设置开机自启
				command += "sudo virsh autostart "+vm.getVmName()+";";
			}
		}
		if(option  == 0) { 
			content = "虚拟机("+vmNames.substring(1)+")设置开机自启";
		}else {
			content = "虚拟机("+vmNames.substring(1)+")取消开机自启";
		}
		SshUtil.execute(kvmServer, command);
		logService.insert(request, content,  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 修改虚拟机带宽
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/upDown.action", method = RequestMethod.POST)
	public JSONObject upDown(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Vm oldVM = vmService.load(condition.getInteger("vmId"));
		int vmState = condition.getInteger("vmState");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(oldVM.getKvmServerId());
		Integer vmUp = condition.getInteger("vmUp");
		Integer vmDown = condition.getInteger("vmDown");
		//修改上下行带宽
		oldVM.setVmUp(vmUp);
		oldVM.setVmDown(vmDown);
		String command ="";
		if(vmState == 1) {
			command = "sudo virsh domiftune "+oldVM.getVmName()+" "+oldVM.getVmMac()+"  --live  --config --outbound "+vmUp+","+vmUp+","+vmUp+";";
			command += "sudo virsh domiftune "+oldVM.getVmName()+" "+oldVM.getVmMac()+"  --live   --config --inbound "+vmDown+","+vmDown+","+vmDown+";";
		}else {
			command = "sudo virsh domiftune "+oldVM.getVmName()+" "+oldVM.getVmMac()+"   --config --outbound "+vmUp+","+vmUp+","+vmUp+";";
			command += "sudo virsh domiftune "+oldVM.getVmName()+" "+oldVM.getVmMac()+"   --config --inbound "+vmDown+","+vmDown+","+vmDown+";";
		}
		//执行SHELL
		SshUtil.execute(kvmServer, command);
		//入库
		vmService.update(oldVM);
		logService.insert(request, "虚拟机修改上下行带宽("+vmUp+","+vmDown+")",  json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 获取虚拟机VNC链接
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/openVm.action",method= RequestMethod.POST)
	public JSONObject openVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		String localName = request.getServerName();
		Integer vmId = condition.getInteger("vmId");
		JSONObject json = vmService.openVm(vmId,localName);
		return json;
	}
	
}
