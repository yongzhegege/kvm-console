package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.CheckAttrDto;
import com.example.demo.empty.KvmImage;
import com.example.demo.empty.KvmImageDto;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.ServerInfoDto;
import com.example.demo.empty.UsbDto;
import com.example.demo.empty.User;
import com.example.demo.service.KvmServerService;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.SshUtil;
import com.example.demo.vo.CommonVO;
import com.jcraft.jsch.JSchException;

@RequestMapping("/kvmServer")
@Controller
public class KvmServerController {
	
	@Autowired
	private KvmServerService kvmServerService;
	
	
	/**
	 * 添加KVM服务器
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doInsert.action",method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception{
		KvmServer kvmServer = (KvmServer)condition.toBeanResolver(KvmServer.class);
		JSONObject json = kvmServerService.insertKvmServer(kvmServer);
		return json;
	}
	
	/**
	 * 获取所有KVM服务器
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
		json.put("data", kvmServerService.getAllKvmServer());
		json.put("success", true);
		User user = (User) request.getSession().getAttribute("user");
		json.put("userId", user.getUserId());
		json.put("userName", user.getUserName());
		return json;
	}
	
	/**
	 * 服务器关机
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/powerOff.action",method=RequestMethod.GET)
	public JSONObject powerOff(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		try {
			SshUtil.execute(kvmServer, "sudo poweroff");
		} catch (JSchException e) {
			json.put("success", false);
			json.put("message", "ssh连接异常，请重试！");
		}
		kvmServer.setState(0);
		kvmServerService.update(kvmServer);
		json.put("success", true);
		return json;
	}
	
	/**
	 * 服务器重启
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/reboot.action",method=RequestMethod.GET)
	public JSONObject reboot(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		try {
			SshUtil.execute(kvmServer, "sudo reboot");
		} catch (JSchException e) {
			json.put("success", false);
			json.put("message", "ssh连接异常，请重试！");
		}
		json.put("success", true);
		return json;
	}
	
	/**
	 * 服务器刷新状态
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/refresh.action",method=RequestMethod.GET)
	public JSONObject refresh(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		Integer kvmServerId = condition.getInteger("kvmServerId");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		kvmServerService.delete(kvmServerId);
		KvmServer newKvmServer = new KvmServer();
		newKvmServer.setKvmServerIp(kvmServer.getKvmServerIp());
		newKvmServer.setKvmServerName(kvmServer.getKvmServerName());
		newKvmServer.setKvmServerUser(kvmServer.getKvmServerUser());
		newKvmServer.setKvmServerPassword(kvmServer.getKvmServerPassword());
		newKvmServer.setKvmServerId(kvmServerId);
		JSONObject json = kvmServerService.insertKvmServer(newKvmServer);
		return json;
	}
	
	/**
	 * 删除服务器
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doDelete.action",method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		Integer kvmServerId = condition.getInteger("kvmServerId");
		return kvmServerService.delete(kvmServerId);
	}
	
	/**
	 * 禁用/本地虚拟机dhcp
	 * @param condition
	 * @param request
	 * @throws IOException 
	 * @throws JSchException 
	 */
	@ResponseBody
	@RequestMapping(path="/manaDhcp.action",method=RequestMethod.POST)
	public JSONObject manaDhcp(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws JSchException, IOException{
		JSONObject json = new JSONObject();
		KvmServer kvmServer = kvmServerService.queryKvmServerById(condition.getInteger("kvmServerId"));
		int statu = condition.getInteger("statu");
		String command = "";
		//判断状态是否为开启
		String conf = SshUtil.executeReturn(kvmServer, "sudo sed -n '/ip>/{x;p};h' /etc/libvirt/qemu/networks/default.xml");
		if(statu == 0) { //关闭DHCP
			if(conf.contains("dhcp")) {
				command = "sudo sed -i '/<dhcp/i\\<!--' /etc/libvirt/qemu/networks/default.xml;" + "sudo sed -i '/ip>/i\\-->' /etc/libvirt/qemu/networks/default.xml;";
			}
		}else {  //开启DHCP
			if(!conf.contains("dhcp")) {
				command = "sudo sed -i '/ip>/i\\<dhcp><range start=\"192.168.122.2\" end=\"192.168.122.254\" /></dhcp>' /etc/libvirt/qemu/networks/default.xml;";
			}
		}
		command += "sudo virsh net-define /etc/libvirt/qemu/networks/default.xml;";
		SshUtil.execute(kvmServer, command);
		json.put("success", true);
		return json;
	}
	
	/**
	 * 获取服务器驱动镜像及网卡
	 * @param condition
	 * @param request
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(path="/getIsoAndDirverAndNetwork.action",method=RequestMethod.GET)
	public JSONObject getIsoAndDirverAndNetwork(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws JSchException, IOException{
		JSONObject resJson = new JSONObject();
		Integer kvmServerId = condition.getInteger("kvmServerId");
		JSONObject json = kvmServerService.getIsoAndDirverAndNetwork(kvmServerId);
		//获取服务器类型
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		String serverType = "".equals(SshUtil.executeReturn(kvmServer, "ls /usr/libexec/qemu-kvm"))?"ubuntu":"centos";
		//获取CPU和内存总数
		String memory = SshUtil.executeReturn(kvmServer, "sudo free -h | awk '{if(NR==2){print $2}}' | sed 's/i//g'");
		int cpu = Integer.valueOf(SshUtil.executeReturn(kvmServer, "cat /proc/cpuinfo | grep processor | wc -l").replace("\n",""))*4;
		if(json.getBoolean("success")){
			resJson.put("success", true);
			resJson.put("isos", json.get("isos"));
			resJson.put("drivers", json.get("drivers"));
			resJson.put("networks", json.get("networks"));
			resJson.put("serverType", serverType);
			resJson.put("memory", memory);
			resJson.put("cpu", cpu);
		}else{
			resJson.put("success", false);
			resJson.put("success", json.get("message"));
		}
		return resJson;
	}
	
	/**
	 * 获取USB列表
	 * @param condition
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(path="/showUsb.action",method=RequestMethod.POST)
	public JSONObject showUsb(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request){
		JSONObject resJson = new JSONObject();
		Integer kvmServerId = condition.getInteger("kvmServerId");
		KvmServer kvmServer = kvmServerService.queryKvmServerById(kvmServerId);
		try {
			String execute = SshUtil.executeReturn(kvmServer, "sudo lsusb");
		    List<UsbDto> usblist = new ArrayList<UsbDto>();
		    if(!"".equals(execute)) {
			    byte b;
			    int i;
			    String[] arrayOfString;
			    for (i = (arrayOfString = execute.split("\n")).length, b = 0; b < i; ) {
			      String s = arrayOfString[b];
			      UsbDto u = new UsbDto();
			      u.setBus(s.split("Bus ")[1].substring(0, 3));
			      u.setDevice(s.split("Device ")[1].substring(0, 3));
			      u.setuId(s.split("ID ")[1].substring(0, 9));
			      u.setuName(s.split("ID ")[1].substring(9));
			      b++;
			      if(u.getuName().contains("Linux Foundation")) {
			    	  continue;
			      }else {
			    	  usblist.add(u);
			      }
			    } 
		    }
		    resJson.put("data", usblist);
		    resJson.put("success", true);
		} catch (Exception e) {
			resJson.put("message", "SSH连接异常，请重试！");
			resJson.put("success", false);
		}
		return resJson;
	}
	
	/**
	 * 加载镜像和驱动文件
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/loadKvmImageTree.action",method=RequestMethod.GET)
	public JSONObject loadKvmImageTree(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		KvmServer kvmServer = kvmServerService.getAllKvmServer().get(0);
		List<KvmImageDto> KvmImageDtos = new ArrayList<KvmImageDto>();
		String isos = SshUtil.executeReturn(kvmServer, "cd /home/isos;ls *.iso");
		int i = 1 ;
		if(isos!=null && !isos.equals("")){
			String[] isoNames = isos.split("\n");
			for(String isoName : isoNames){
				CheckAttrDto checkArr=new CheckAttrDto();
				checkArr.setType("0");
				checkArr.setChecked("0");
				KvmImageDto kvmImageDto = new KvmImageDto();
				KvmImage kvmImage = new KvmImage();
				kvmImage.setImageAddr("/home/isos/"+isoName);
				kvmImage.setImageSize(SshUtil.executeReturn(kvmServer, "sudo du -sh "+kvmImage.getImageAddr()).split("\t")[0]);
				kvmImage.setImageId(Long.valueOf(i));
				kvmImage.setImageName(isoName);
				kvmImageDto.setBasicData(kvmImage);
				kvmImageDto.setImageId(Long.valueOf(i));
				kvmImageDto.setImageName(isoName);
				kvmImageDto.setImageParentId(Long.valueOf(0));
				kvmImageDto.setCheckArr(checkArr);
				KvmImageDtos.add(kvmImageDto);
				i++;
			}
		}
		String drivers = SshUtil.executeReturn(kvmServer, "cd /home/drivers;ls *.iso");
		if(drivers!=null && !drivers.equals("")){
			String[] driverNames = drivers.split("\n");
			for(String driverName : driverNames){
				CheckAttrDto checkArr=new CheckAttrDto();
				checkArr.setType("0");
				checkArr.setChecked("0");
				KvmImageDto kvmImageDto = new KvmImageDto();
				KvmImage kvmImage = new KvmImage();
				kvmImage.setImageAddr("/home/drivers/"+driverName);
				kvmImage.setImageSize(SshUtil.executeReturn(kvmServer, "sudo du -sh "+kvmImage.getImageAddr()).split("\t")[0]);
				kvmImage.setImageId(Long.valueOf(i));
				kvmImage.setImageName(driverName);
				kvmImageDto.setBasicData(kvmImage);
				kvmImageDto.setImageId(Long.valueOf(i));
				kvmImageDto.setImageName(driverName);
				kvmImageDto.setImageParentId(Long.valueOf(0));
				kvmImageDto.setCheckArr(checkArr);
				KvmImageDtos.add(kvmImageDto);
				i++;
			}
		}
		json.put("data",KvmImageDtos);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		json.put("status", jsonObject);
		return json;
	}
	
	/**
	 * 删除KVM镜像
	 * @param condition
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(path="/deleteKvmImage.action",method=RequestMethod.DELETE)
	public JSONObject deleteKvmImage(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) {
		JSONObject json = new JSONObject();
		json.put("success", true);
		KvmServer kvmServer = kvmServerService.getAllKvmServer().get(0);
		String imagePath = condition.getString("imagePath");
		try {
			SshUtil.execute(kvmServer, "sudo rm -rf "+imagePath);
		} catch (JSchException e) {
			json.put("success", false);
			json.put("message", "ssh连接异常，请重试！");
		}
		return json;
	}
	
	/**
	 * 上传镜像
	 * @param file
	 * @param condition
	 * @param request
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(path="/uploadImage.action",method=RequestMethod.POST)
	public JSONObject importLicense(@RequestParam(value = "file", required = false) MultipartFile file,@RequestAttribute("condition") CommonVO condition,HttpServletRequest request) throws IllegalStateException, IOException{
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("success", true);
		if (!file.isEmpty()) {
			String path = "/home/isos/";
			String name = file.getOriginalFilename();
			File destFile = new File(path, name);
			if(!destFile.exists()){
				destFile.mkdirs();
			}
			file.transferTo(destFile);  
		}
		return json;
}
	
	/**
	 * 上传镜像
	 * @param file
	 * @param condition
	 * @param request
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(path="/uploadDriver.action",method=RequestMethod.POST)
	public JSONObject uploadDriver(@RequestParam(value = "file", required = false) MultipartFile file,@RequestAttribute("condition") CommonVO condition,HttpServletRequest request) throws IllegalStateException, IOException{
		JSONObject json = new JSONObject();
		json.put("code", 0);
		json.put("success", true);
		if (!file.isEmpty()) {
			String path = "/home/drivers/";
			String name = file.getOriginalFilename();
			File destFile = new File(path, name);
			if(!destFile.exists()){
				destFile.mkdirs();
			}
			file.transferTo(destFile);  
		}
		return json;
	}
	
	/**
	 * 获取本地服务器CPU/硬盘/内存使用情况
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping(path = "/getServerInfo.action", method = RequestMethod.GET)
	public JSONObject getServerInfo(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		String shPath = PropertiesUtil.get("diskTestPath")+"serverInfo.sh";
		ServerInfoDto serverInfo = new ServerInfoDto();
		try {
			String[] infos = SshUtil.executeLocalRerurn(shPath).split(",");
			String serverVision = SshUtil.executeLocalRerurn("sudo lsb_release -a");
			serverInfo.setMemory(infos[0].split("[(]")[0].trim());
			serverInfo.setPersentMemory(infos[0].split("[(]")[1].split("%")[0]);
			serverInfo.setDisk(infos[1].split("[(]")[0].trim());
			serverInfo.setPersentDisk(infos[1].split("[(]")[1].split("%")[0]);
			serverInfo.setCpu(infos[2]);
			String[] versionArray = serverVision.split(",");
			List<String> versionList = new ArrayList<String>();
			for(String version : versionArray) {
				versionList.add(version.split(":")[1]);
			}
			serverInfo.setServerVersion(versionList);
			serverInfo.setKernelVersion(SshUtil.executeLocalRerurn("sudo uname -r"));
			String res = SshUtil.executeLocalRerurn("sudo ufw status");
			serverInfo.setFirewallOpen(!res.contains("inactive") && !res.contains("不活动"));
			//获取服务器存在天数
			String createTime = SshUtil.executeLocalRerurn("ls -ld /var/log/installer | awk '{print $7\"/\"$6\"/\"$8}'");
			Date now = new Date();
			if(createTime.contains(":")) {
				createTime = SshUtil.executeLocalRerurn("ls -ld /var/log/installer | awk '{print $7\"/\"$6}'")+"/"+(now.getYear()+1900) ;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy",Locale.US);
			Date date = sdf.parse(createTime);
			serverInfo.setActiveTime((now.getTime()-date.getTime())/1000/60/60/24);
		} catch (Exception e) {}
		json.put("data", serverInfo);
		return json;
	}
}
