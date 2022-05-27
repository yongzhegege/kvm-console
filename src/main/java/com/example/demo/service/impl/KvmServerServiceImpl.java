package com.example.demo.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.KvmServerDao;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.Vm;
import com.example.demo.service.KvmServerService;
import com.example.demo.service.VmService;
import com.example.demo.util.DateTimeUtils;
import com.example.demo.util.LibvirtUtil;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.SshUtil;
import com.jcraft.jsch.JSchException;

@Service
public class KvmServerServiceImpl implements KvmServerService {
	
	@Autowired
	private KvmServerDao kvmServerDao;
	
	@Autowired
	private VmService vmService;

	@Override
	public JSONObject insertKvmServer(KvmServer kvmServer) {
		JSONObject returnMsg = new JSONObject();
		if(queryKvmServerByIp(kvmServer.getKvmServerIp()) != null) {
			returnMsg.put("success", false);
			returnMsg.put("message", "该服务器IP已存在，请重试！");
			return returnMsg;
		}
		JSONObject json = SshUtil.checkServer(kvmServer);
		if(json.getBoolean("success")){
			kvmServer.setKvmServerType(json.getInteger("serverType"));
			kvmServer.setCreateTime(DateTimeUtils.getSysNowDate());
			List<Vm> vmList = new ArrayList<Vm>();
				try {
					Connect connect =  LibvirtUtil.getConnect(kvmServer);
					String[] offline = connect.listDefinedDomains();
					int[] online = connect.listDomains();
					if(offline !=null &&  online !=null){
						kvmServer.setVmNum(offline.length+online.length);
					}
					//插入KVM服务器数据
					kvmServerDao.insert(kvmServer);
					//获取在线虚拟机
					for(int id : online){
						Vm vm = new Vm();
						Domain domain = connect.domainLookupByID(id);
						vm.setVmUuid(domain.getUUIDString());
						vm.setVmName(domain.getName());
						vm.setKvmServerId(kvmServer.getKvmServerId());
						String port = "5000";
						if(domain.getXMLDesc(0).contains("<graphics type='vnc' port='")) {
							port = domain.getXMLDesc(0).split("<graphics type='vnc' port='")[1].substring(0,4);
						}
						if(domain.getXMLDesc(0).contains("loader")) {
							vm.setVmType("UEFI");
						}else {
							vm.setVmType("Legacy");
						}
						vm.setVncPort(Integer.valueOf(port));
						vm.setVmCpu(Integer.valueOf(domain.getXMLDesc(0).split("<vcpu placement='static'>")[1].split("</vcpu>")[0]));
						vm.setVmDefaultStart(domain.getXMLDesc(0).split("<boot dev='")[1].split("'")[0]);
						vm.setVmMemory((int)domain.getMaxMemory()/1048576);
						vm.setVmState(domain.isActive());
						String[] networks=SshUtil.executeReturn(kvmServer,"sudo virsh domiflist "+domain.getName()).split("\n")[2].split(" ");
						List<String > nlist =new ArrayList<String>();
						for(String s:networks){
							if(!s.equals("")){
								nlist.add(s);
							}
						}
						if(nlist.get(2).equals("virbr0")){
							vm.setVmNetwork("virbr0(NAT)");
						}else{
							vm.setVmNetwork(nlist.get(2)+"(Bridge)");
						}
						//获取硬盘类型 VIRTIO SATA
						String isos=SshUtil.executeReturn(kvmServer,"sudo virsh domblklist "+domain.getName());
						String diskinfo = "";
						if(isos.contains("sda")) {
							vm.setVmDiskMode(0);
							diskinfo = SshUtil.executeReturn(kvmServer, "sudo virsh domblkinfo "+domain.getName()+" sda");
						}else if(isos.contains("vda")) {
							vm.setVmDiskMode(1);
							diskinfo = SshUtil.executeReturn(kvmServer, "sudo virsh domblkinfo "+domain.getName()+" vda");
						}
						String sde="";
						String sdf="";
						for(String i:isos.split("\n")){
							if(i.contains("sde")){
								sde=i.replace(" ", "").replace("sde", "");
							}
							if(i.contains("sdf")){
								sdf=i.replace(" ", "").replace("sdf", "");
							}
						}
						vm.setVmIso(sde.equals("")?"0":sde.substring(11));
						vm.setVmDriver(sdf.equals("")?"0":sdf);
						if(!diskinfo.equals("\n")) {
							try {
								vm.setVmDisk((int) (Long.valueOf(diskinfo.split("\n")[0].split("Capacity:")[1].trim())/1073741824));
							} catch (Exception e) {
								vm.setVmDisk((int) (Long.valueOf(diskinfo.split("\n")[0].split("容量：")[1].trim())/1073741824));
							}
						}else {
							vm.setVmDisk(0);
						}
						String domainxml = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml");
						vm.setVmMac(domainxml.split("<mac address='")[1].substring(0, 17));
						String vmDownInfo = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml | grep inbound | awk -F \"' peak\" '{print $1}' | awk -F \"average='\" '{print $2}'");
						String vmUpInfo = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml | grep outbound | awk -F \"' peak\" '{print $1}' | awk -F \"average='\" '{print $2}'");
						if(!"".equals(vmDownInfo.replaceAll("\r|\n", ""))) {
							vm.setVmDown(Integer.valueOf(vmDownInfo.trim()));
						}
						if(!"".equals(vmUpInfo.replaceAll("\r|\n", ""))) {
							vm.setVmUp(Integer.valueOf(vmUpInfo.trim()));
						}
						vmList.add(vm);
					}
					//获取离线虚拟机
					for(String name : offline){
						Vm vm = new Vm();
						Domain domain = connect.domainLookupByName(name);
						vm.setVmUuid(domain.getUUIDString());
						vm.setVmName(domain.getName());
						vm.setKvmServerId(kvmServer.getKvmServerId());
						String port = "5000";
						if(domain.getXMLDesc(0).contains("<graphics type='vnc' port='")) {
							port = domain.getXMLDesc(0).split("<graphics type='vnc' port='")[1].substring(0,4);
						}
						if(domain.getXMLDesc(0).contains("loader")) {
							vm.setVmType("UEFI");
						}else {
							vm.setVmType("Legacy");
						}
						vm.setVncPort(Integer.valueOf(port));
						vm.setVmCpu(Integer.valueOf(domain.getXMLDesc(0).split("<vcpu placement='static'>")[1].split("</vcpu>")[0]));
						vm.setVmDefaultStart(domain.getXMLDesc(0).split("dev='")[1].split("'")[0]);
						vm.setVmMemory((int)domain.getMaxMemory()/1048576);
						vm.setVmState(0);
						String[] networks=SshUtil.executeReturn(kvmServer,"sudo virsh domiflist "+domain.getName()).split("\n")[2].split(" ");
						List<String > nlist =new ArrayList<String>();
						for(String s:networks){
							if(!s.equals("")){
								nlist.add(s);
							}
						}
						if(nlist.get(2).equals("virbr0")){
							vm.setVmNetwork("virbr0(NAT)");
						}else{
							vm.setVmNetwork(nlist.get(2)+"(Bridge)");
						}
						//获取硬盘类型 VIRTIO SATA
						String isos = SshUtil.executeReturn(kvmServer,"sudo virsh domblklist "+domain.getName());
						String diskinfo = "";
						if(isos.contains("sda")) {
							vm.setVmDiskMode(0);
							diskinfo = SshUtil.executeReturn(kvmServer, "sudo virsh domblkinfo "+domain.getName()+" sda");
						}else if(isos.contains("vda")) {
							vm.setVmDiskMode(1);
							diskinfo = SshUtil.executeReturn(kvmServer, "sudo virsh domblkinfo "+domain.getName()+" vda");
						}
						String sde="";
						String sdf="";
						for(String i:isos.split("\n")){
							if(i.contains("sde")){
								sde=i.replace(" ", "").replace("sde", "");
							}
							if(i.contains("sdf")){
								sdf=i.replace(" ", "").replace("sdf", "");
							}
						}
						vm.setVmIso(sde.equals("")?"0":sde.substring(14));
						vm.setVmDriver(sdf.equals("")?"0":sdf);
						try {
							vm.setVmDisk((int) (Long.valueOf(diskinfo.split("\n")[0].split("Capacity:")[1].trim())/1073741824));
						} catch (Exception e) {
							vm.setVmDisk((int) (Long.valueOf(diskinfo.split("\n")[0].split("容量：")[1].trim())/1073741824));
						}
						String domainxml = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml");
						vm.setVmMac(domainxml.split("<mac address='")[1].substring(0, 17));
						String vmDownInfo = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml | grep inbound | awk -F \"' peak\" '{print $1}' | awk -F \"average='\" '{print $2}'");
						String vmUpInfo = SshUtil.executeReturn(kvmServer, "sudo cat /etc/libvirt/qemu/"+domain.getName()+".xml | grep outbound | awk -F \"' peak\" '{print $1}' | awk -F \"average='\" '{print $2}'");
						if(!"".equals(vmDownInfo.replaceAll("\r|\n", ""))) {
							vm.setVmDown(Integer.valueOf(vmDownInfo.trim()));
						}
						if(!"".equals(vmUpInfo.replaceAll("\r|\n", ""))) {
							vm.setVmUp(Integer.valueOf(vmUpInfo.trim()));
						}
						vmList.add(vm);
					}
				} catch (LibvirtException e) {
					LibvirtUtil.removeConnect(kvmServer);
					returnMsg.put("success", false);
					returnMsg.put("message", "libvirt连接异常，请重试！");
					return returnMsg;
				} catch (JSchException e) {
					returnMsg.put("success", false);
					returnMsg.put("message", "ssh指令发送异常，请重试！");
					return returnMsg;
				} catch (IOException e) {
					returnMsg.put("success", false);
					returnMsg.put("message", "ssh指令发送异常，请重试！");
					return returnMsg;
				}
				//将虚拟机写入至数据库
				vmService.insertMoreVm(vmList);
			returnMsg.put("success", true);
		}else{
			returnMsg.put("success", false);
	    	returnMsg.put("erorCode", json.getLong("erorCode"));
	    	returnMsg.put("message", json.getString("message"));
		}
		return returnMsg;
	}

	@Override
	public KvmServer queryKvmServerByIp(String serverIp) {
		KvmServer kvmServer = new KvmServer();
		kvmServer.setKvmServerIp(serverIp);
		List<KvmServer> serverList = kvmServerDao.allSelectLists(kvmServer);
		if(serverList.size() > 0) {
			return serverList.get(0);
		}else {
			return null;
		}
	}

	@Override
	public List<KvmServer> getAllKvmServer() {
		return kvmServerDao.allSelectLists(null);
	}

	@Override
	public KvmServer queryKvmServerById(Integer kvmServerId) {
		return kvmServerDao.load(kvmServerId);
	}

	@Override
	public void update(KvmServer kvmServer) {
		kvmServerDao.update(kvmServer);
	}

	@Override
	public JSONObject delete(Integer kvmServerId) {
		JSONObject returnMsg = new JSONObject();
		KvmServer kvmServer = queryKvmServerById(kvmServerId);
		//删除TOKEN
		String tokenPath = PropertiesUtil.get("tokenPath");
		String command = "sed -i '/"+kvmServer.getKvmServerIp()+"/d' "+tokenPath;
		try {
			SshUtil.executeLocal(command);
		} catch (IOException e1) {
			returnMsg.put("success", false);
			returnMsg.put("message", "本地指令发送异常，请重试！");
		}
		//删除服务器及虚拟机数据
		kvmServerDao.delete(kvmServer);
		vmService.deleteVmByKvmServerId(kvmServerId);
		returnMsg.put("success", true);
		return returnMsg;
	}

	@Override
	public JSONObject getIsoAndDirverAndNetwork(Integer kvmServerId) {
		JSONObject returnMsg = new JSONObject();
		KvmServer kvmServer = queryKvmServerById(kvmServerId);
		List<String> networks = new ArrayList<String>();
		try {
			List<String> isos = Arrays.asList(SshUtil.executeReturn(kvmServer, "sudo ls /home/isos").split("\n"));
			List<String> drivers = Arrays.asList(SshUtil.executeReturn(kvmServer, "sudo ls /home/drivers").split("\n"));
			String[] networkStr = SshUtil.executeReturn(kvmServer, "ip link show type bridge | grep -v link/ether | awk '{print $2}' | cut -d ':' -f 1").split("\n");
			String[] onlineNetworkStr = SshUtil.executeReturn(kvmServer, "ip link | grep LOWER_UP | awk -F ':' '{print $2}' | awk '{print $1}'").split("\n");
			for(String  net: networkStr) {
				for(String online :onlineNetworkStr) {
					if(net.equals(online)) {
						if(net.substring(0,net.length()-1).equals("virbr")){
							if(!net.equals("virbr0"))
							networks.add(net+"(NAT)");
						}
						if(!net.substring(0,net.length()-1).equals("virbr")){
							networks.add(net+"(Bridge)");
						}
						break;
					}
				}
			}
			networks.add("virbr0(NAT)");
			returnMsg.put("success", true);
			returnMsg.put("isos",isos);
			returnMsg.put("drivers", drivers);
			returnMsg.put("networks", networks);
			return returnMsg;
		} catch (Exception e) {
			returnMsg.put("success", false);
			returnMsg.put("message", "ssh指令发送异常，请重试！");
			return returnMsg;
		}
	}

	@Override
	public JSONObject uploadFile(Integer type, String url) {
		JSONObject returnMsg = new JSONObject();
		returnMsg.put("success", true);
		KvmServer kvmServer = getAllKvmServer().get(0);
		String mkdirCommad = "";
		String uploadCommand = "";
		if(type == 0){
			mkdirCommad = "sudo mkdir /home/isos/;";
			uploadCommand = "cd /home/isos/;sudo rm -rf wget* ;sudo wget -b "+url;
		}else{
			mkdirCommad = "sudo mkdir /home/drivers/;";
			uploadCommand = "cd /home/drivers/;sudo rm -rf wget*;sudo wget -b "+url;
		}
		try {
			SshUtil.execute(kvmServer, mkdirCommad+uploadCommand);
		} catch (Exception e) {
			returnMsg.put("success", false);
			returnMsg.put("message", "ssh指令发送异常，请重试！");
		}
		return returnMsg;
	}
}
