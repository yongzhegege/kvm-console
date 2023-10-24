package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.KvmTemplate;
import com.example.demo.empty.Vm;
import com.example.demo.service.KvmServerService;
import com.example.demo.service.KvmTemplateService;
import com.example.demo.service.LogService;
import com.example.demo.service.VmService;
import com.example.demo.util.LibvirtUtil;
import com.example.demo.util.MacUtil;
import com.example.demo.util.SshUtil;
import com.example.demo.vo.CommonVO;

@RequestMapping("/kvmTemplate")
@Controller
public class KvmTemplateController {
	
	@Autowired
	private KvmTemplateService kvmTemplateService;
	
	@Autowired
	private KvmServerService kvmServerService;
	
	@Autowired
	private VmService vmService;
	
	@Autowired
	private LogService logService;
	
	/**
	 * 获取所有模板
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doPageSelect.action",method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception{
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("data", kvmTemplateService.getAllKvmTemplate());
		json.put("success", true);
		return json;
	}
	
	/**
	 * 根据模板批量创建虚拟机
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/mkdirVm.action", method = RequestMethod.GET)
	public JSONObject mkdirVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer createType = condition.getInteger("createType");
		Integer templateId = condition.getInteger("templateId");
		String vmName = condition.getString("vmName");
		Integer vmNum = condition.getInteger("vmNum");
		KvmTemplate template = kvmTemplateService.load(templateId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(template.getKvmServerId());
		List<Vm> vmList = vmService.getVmByKvmServerId(template.getKvmServerId());
		List<String> vmNameList = new ArrayList<String>();
		List<Vm> addList = new ArrayList<Vm>();
		boolean ans = true;
		//检查是否含有同名
		if(vmNum == 1) {
			vmNameList.add(vmName);
		}else {
			for(int i=0; i<vmNum; i++) {
				vmNameList.add(vmName+(i+1));
			}
		}
		for(Vm  v: vmList) {
			if(vmNameList.contains(v.getVmName())) {
				ans = false;
				json.put("message", "虚拟机名："+v.getVmName()+"已存在，请重试！");
				break;
			}
		}
		if(ans) {
			String command = "";
			String createCmd = "";
		      for(String name:vmNameList) {
		    	  if(createType == 1) {
		    		  //创建差异qcow2
			    	  createCmd += "sudo mkdir -p /var/lib/libvirt/images/"+name+"/base;"+
								 					"sudo mkdir -p /var/lib/libvirt/images/"+name+"/data;"+
								 					"qemu-img create -f qcow2 -o backing_file=/var/lib/libvirt/images/template/"+template.getTemplateName()+"/images/base/"+template.getTemplateName()+".qcow2 /var/lib/libvirt/images/"+name+"/base/"+name+".qcow2;";
		    	  }else {
			    	  //拷贝qcow2
			    	  createCmd	 += 	"sudo mkdir -p /var/lib/libvirt/images/"+name+"/base;"+
			    			  								 "sudo mkdir -p /var/lib/libvirt/images/"+name+"/data;"+
			    			  								 "cp /var/lib/libvirt/images/template/"+template.getTemplateName()+"/images/base/"+template.getTemplateName()+".qcow2 /var/lib/libvirt/images/"+name+"/base/"+name+".qcow2;"+
			    			  								"cp /var/lib/libvirt/images/template/"+template.getTemplateName()+"/images/data/"+template.getTemplateName()+"_vdb.qcow2 /var/lib/libvirt/images/"+name+"/data/"+name+"_vdb.qcow2;"+
			    			  								"cp /var/lib/libvirt/images/template/"+template.getTemplateName()+"/images/data/"+template.getTemplateName()+"_vdc.qcow2 /var/lib/libvirt/images/"+name+"/data/"+name+"_vdc.qcow2;"+
			    			  								"cp /var/lib/libvirt/images/template/"+template.getTemplateName()+"/images/data/"+template.getTemplateName()+"_vdd.qcow2 /var/lib/libvirt/images/"+name+"/data/"+name+"_vdd.qcow2;";
		    	  }
		    	  String uuid = UUID.randomUUID().toString();
		    	  Integer vncPort = vmService.getVncPort(template.getKvmServerId());
		    	  	  //拷贝XML
			      	  command +="sudo cp /var/lib/libvirt/images/template/" + template.getTemplateName() + "/xml/" + template.getTemplateName() + ".xml /etc/libvirt/qemu/" + name+ ".xml;";
				      String mac = MacUtil.getMacAddr();
				      //替换MAC
				      command += "sudo sed -i 's/" + template.getTemplateMac() + "/" + mac + "/' /etc/libvirt/qemu/" + name + ".xml;";
				      //替换NAME
				      command +="sudo sed -i 's/<name>" + template.getTemplateName() + "/<name>" + name + "/' /etc/libvirt/qemu/" + name + ".xml;";
				      //替换UUID
				      command +="sudo sed -i 's/" + template.getTemplateUuid() + "/" + uuid + "/' /etc/libvirt/qemu/" + name+ ".xml;";
				      //替换VNC端口
				      command += "sudo sed -i 's/" + template.getTemplateVncPort()+ "/" + vncPort + "/' /etc/libvirt/qemu/" + name + ".xml;";
				      //替换磁盘文件
				      command +="sudo sed -i 's#/var/lib/libvirt/images/"+template.getTemplateName()+"/base/" + template.getTemplateName()+ ".qcow2#" + "/var/lib/libvirt/images/"+name+"/base/"+name + ".qcow2#' /etc/libvirt/qemu/" + name+ ".xml;";
				      //定义虚拟机
				      command +="sudo virsh define /etc/libvirt/qemu/" + name + ".xml;";
				      //判断如果是差异创建则写入关联文件
				      if(createType == 1) {
				    	  command += "sudo echo '"+uuid+"' >> /var/lib/libvirt/images/template/"+template.getTemplateName()+"/splitDisk.xml;";
				      }
				      Vm vm = new Vm();
				      vm.setKvmServerId(kvmServer.getKvmServerId());
				      vm.setVmName(name);
				      vm.setVmShowName(name);
				      vm.setVmMac(mac);
				      vm.setVmState(0);
				      vm.setVncPort(vncPort);
				      vm.setVmIso("0");
				      vm.setVmDriver("0");
				      vm.setVmNetwork(template.getTemplateNetwork());
				      vm.setVmMemory(template.getTemplateMemory());
				      vm.setVmCpu(template.getTemplateCpu());
				      vm.setVmDisk(template.getTemplateDisk());
				      vm.setVmUuid(uuid);
				      vm.setVmDefaultStart(template.getTemplateDefaultStart());
				      vm.setVmType(template.getVmType());
				      vmService.insert(vm);;
				      addList.add(vm);
		      }
		      kvmServer.setVmNum(kvmServer.getVmNum()+addList.size());
		      kvmServerService.update(kvmServer);
		      SshUtil.execute(kvmServer, createCmd);
		      SshUtil.execute(kvmServer, command);
		}else {
			json.put("success", false);
		}
		logService.insert(request, "模板("+template.getTemplateName()+")批量创建虚拟机("+vmName+")", json.getBoolean("success")?1:0);
		return json;
	}

	/**
	 * 删除模板信息
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doDelete.action", method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer templateId = condition.getInteger("pk");
		KvmTemplate template = kvmTemplateService.load(templateId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(template.getKvmServerId());
		//判断该模板是否存在差异虚拟机
		String res = SshUtil.executeReturn(kvmServer, "cat /var/lib/libvirt/images/template/"+template.getTemplateName()+"/splitDisk.xml");
		String splitVmNames = "";
		if(!"".equals(res)) {
			List<String> splitUuids = Arrays.asList(res.split("\n"));
			List<Vm> vmList = vmService.getVmByKvmServerId(kvmServer.getKvmServerId());
			for(Vm vm : vmList) {
				if(splitUuids.toString().contains(vm.getVmUuid())) {
					splitVmNames += ","+vm.getVmName();
				}
			}
		}
		if(splitVmNames.equals("")) {
			//删除物理文件
			SshUtil.execute(kvmServer, "sudo rm -rf /var/lib/libvirt/images/template/"+template.getTemplateName());
			//删除数据库信息
			kvmTemplateService.delete(templateId);
		}else {
			json.put("success", false);
			json.put("message", "该模板存在差异虚拟机["+splitVmNames.substring(1)+"]，请先删除！");
		}
		logService.insert(request, "删除模板("+template.getTemplateName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 将虚拟机设为模板
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(path = "/doInsert.action", method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request)
			throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		List<String> list = new ArrayList<String>();
		Integer kvmServerId = condition.getInteger("kvmServerId");
		Integer vmId = condition.getInteger("vmId");
		Vm vm = vmService.load(vmId);
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		Connect con = LibvirtUtil.getConnect(kvmServer);
		Domain domain = con.domainLookupByName(vm.getVmName());
		if (domain.isActive() != 0) {
			json.put("success", false);
			json.put("success", "虚拟机正处于开机状态，请刷新！");
		} else {
			//创建模板文件夹
			  String mkdirCommad = "sudo mkdir /var/lib/libvirt/images/template;"+
			 "sudo mkdir /var/lib/libvirt/images/template/" + vm.getVmName()+";"+
			 "sudo mkdir /var/lib/libvirt/images/template/" + vm.getVmName() + "/xml;"+
			 "sudo mkdir /var/lib/libvirt/images/template/" + vm.getVmName() + "/images;";
			  SshUtil.execute(kvmServer, mkdirCommad);
			  //1.查询该虚拟机挂载的磁盘 并卸载 2.拷贝该虚拟机的该虚拟机的XML文件 3.删除该虚拟机
			  String execute = SshUtil.executeReturn(kvmServer, "sudo virsh domblklist " + vm.getVmName());
			  byte b;
			  int i;
			  String[] arrayOfString;
		      for (i = (arrayOfString = execute.split("\n")).length, b = 0; b < i; ) {
		      String s = arrayOfString[b];
		      if (s.contains("vda") || s.contains("vdb") || s.contains("vdc") || s.contains("vdd"))
		        list.add(s.substring(35).replace(" ", "")); 
		      b++;
		    }
		      String mvCommad = "sudo virsh detach-disk " + vm.getVmName() + " hdc --config;"+
		    		  										"sudo virsh detach-disk " + vm.getVmName()  + " hda --config;"+
		    		  										"sudo cp /etc/libvirt/qemu/" + vm.getVmName() + ".xml  /var/lib/libvirt/images/template/" + vm.getVmName() + "/xml;"+
		    		  										"sudo virsh undefine " + vm.getVmName();
		      SshUtil.execute(kvmServer, mvCommad);
		      //将该虚拟机挂载的盘移至对应文件夹
		      SshUtil.execute(kvmServer, "sudo mv /var/lib/libvirt/images/" + vm.getVmName()+"/*" + "  /var/lib/libvirt/images/template/" + vm.getVmName() + "/images;sudo rm -rf /var/lib/libvirt/images/" + vm.getVmName());
		      //删除数据库VM信息
		      vmService.deleteRecord(vmId);
		      //修改服务器虚拟机数量
		      kvmServer.setVmNum(kvmServer.getVmNum()-1);
		      kvmServerService.update(kvmServer);
		      //添加数据库模板信息
		      KvmTemplate template = new KvmTemplate();
		      template.setKvmServerId(kvmServerId);
		      template.setTemplateName(vm.getVmName());
		      template.setTemplateMac(vm.getVmMac());
		      template.setTemplateUuid(vm.getVmUuid());
		      template.setTemplateVncPort(String.valueOf(vm.getVncPort()));
		      template.setTemplateMemory(vm.getVmMemory());
		      template.setTemplateDisk(vm.getVmDisk());
		      template.setTemplateCpu(vm.getVmCpu());
		      template.setTemplateNetwork(vm.getVmNetwork());
		      template.setTemplateDefaultStart(vm.getVmDefaultStart());
		      template.setVmType(vm.getVmType());
		      kvmTemplateService.insert(template);
		}
		logService.insert(request, "设置虚拟机("+vm.getVmName()+")为模板", json.getBoolean("success")?1:0);
		return json;
	}
}
