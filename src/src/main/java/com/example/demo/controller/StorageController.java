package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.example.demo.empty.FileSys;
import com.example.demo.empty.Storage;
import com.example.demo.service.StorageService;
import com.example.demo.util.PropertiesUtil;
import com.example.demo.util.SshUtil;
import com.example.demo.vo.CommonVO;

@RequestMapping("/storage")
@Controller
public class StorageController {
	
	@Autowired
	private StorageService storageService;

	
	/**
	 * 查询所有存储
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doPageSelect.action",method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("data", storageService.allSelectLists(null));
		json.put("code", 0);
		return json;
	}
	
	
	/**
	 * 添加存储
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doInsert.action",method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Storage storage = (Storage) condition.toBeanResolver(Storage.class);
		this.storageService.insert(storage);
		return json;
	}
	
	/**
	 * 修改存储
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doUpdate.action",method = RequestMethod.POST)
	public JSONObject doUpdate(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Storage storage = (Storage) condition.toBeanResolver(Storage.class);
		this.storageService.update(storage);
		return json;
	}
	
	/**
	 * 删除存储
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/doDelete.action",method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		this.storageService.delete(condition.getInteger("pk"));
		return json;
	}
	
	/**
	 * 挂载存储
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/mount.action",method = RequestMethod.POST)
	public JSONObject mount(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer storageId = condition.getInteger("storageId");
		Storage storage = storageService.load(storageId);
		if(storage.getStorageType() == 1) { //NFS
			String res = SshUtil.executeLocalRerurn("sudo mkdir -p "+storage.getLocalPath()+";sudo mount -t nfs "+storage.getStoragePath()+" "+storage.getLocalPath());
			if(SshUtil.executeLocalRerurn("sudo df -h").contains(storage.getLocalPath())) {  //判断分区是否挂载成功
				storage.setStorageStatu(1);
				storageService.update(storage);
			}else {
				json.put("success", false);
				if("".equals(res)) {
					json.put("message","挂载失败，请检查存储路径或账号密码！");
				}else {
					json.put("message",res);
				}
			}
		}else if(storage.getStorageType() == 2){ //SMB
			if("".equals(storage.getStorageUsername()) || "".equals(storage.getStoragePassword())) {
				json.put("success", false);
				json.put("message", "smb存储账号或密码不能为空！");
			}else {
				String res = SshUtil.executeLocalRerurn("sudo mkdir -p "+storage.getLocalPath()+";sudo mount -t cifs -o username="+storage.getStorageUsername()+",password="+storage.getStoragePassword()+" "+storage.getStoragePath()+" "+storage.getLocalPath());
				if(SshUtil.executeLocalRerurn("sudo df -h").contains(storage.getLocalPath())) { //判断分区是否挂载成功
					storage.setStorageStatu(1);
					storageService.update(storage);
				}else {
					json.put("success", false);
					if("".equals(res)) {
						json.put("message","挂载失败，请检查存储路径或账号密码！");
					}else {
						json.put("message",res);
					}
				}
			}
		}
		else if(storage.getStorageType() == 3){ //ISCSI
			String ip = storage.getStoragePath();
			//获取IQN
			String iqnStr = SshUtil.executeLocalRerurn("sudo iscsiadm --mode discovery --type sendtargets --portal " + ip + " | awk -F 'iqn' '{print $2}'");
			String iqn = "";
			if(!"".equals(iqnStr)) {
				iqn = "iqn"+iqnStr.split(",")[0];
				//映射该分区
				String res = SshUtil.executeLocalRerurn("sudo iscsiadm  -m node -T "+iqn+"  -p "+ip+"  -l");
				if(res.contains("successful")) {
					String pathStr = SshUtil.executeLocalRerurn("sudo fdisk -l | grep -B1 iSCSI").split(",")[0];
					try {
						String storagePath = "";
						if(pathStr.contains("：")) {
							storagePath = pathStr.split("：")[0].split("Disk")[1].trim()+1;
						}else {
							storagePath = pathStr.split(":")[0].split("Disk")[1].trim()+1;
						}
						//挂载设备
						String mountStr = SshUtil.executeLocalRerurn("sudo mkdir -p "+ storage.getLocalPath() +" ;sudo mount "+storagePath+" "+storage.getLocalPath());
						if(SshUtil.executeLocalRerurn("sudo df -h").contains(storage.getLocalPath())) { //判断分区是否挂载成功
							storage.setStorageStatu(1);
							storageService.update(storage);
						}else {
							json.put("success", false);
							if("".equals(mountStr)) {
								json.put("message","挂载失败，请检查存储路径或账号密码！");
							}else {
								json.put("message",mountStr);
							}
						}
					} catch (Exception e) {
						json.put("success", false);
						json.put("message", "挂载失败，未找到该分区对应的设备！");
					}
				}else {
					json.put("success", false);
					json.put("message", "映射该分区失败："+res);
				}
			}else {
				json.put("success", false);
				json.put("message", "获取IQN失败，请检查IP是否正确！");
			}
		}
		return json;
	}
	
	/**
	 * 卸载存储
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/umount.action",method = RequestMethod.POST)
	public JSONObject umount(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		Integer storageId = condition.getInteger("storageId");
		Storage storage = storageService.load(storageId);
		String res = SshUtil.executeLocalRerurn("sudo umount -lf "+storage.getLocalPath()+";sudo rm -rf "+storage.getLocalPath());
		if(storage.getStorageType() == 3) { //如是ISCSI则要取消映射
			//获取IQN
			String iqnStr = SshUtil.executeLocalRerurn("sudo iscsiadm --mode discovery --type sendtargets --portal " + storage.getStoragePath() + " | awk -F 'iqn' '{print $2}'");
			if(!"".equals(iqnStr)) {
				String iqn = "iqn"+iqnStr.split(",")[0];
				//取消映射
				SshUtil.executeLocalRerurn("sudo iscsiadm  -m node -T "+iqn+"  -p "+ storage.getStoragePath()+"  -u");
			}
		}
		if(!"".equals(res)) {
			json.put("success", false);
			json.put("message",res);
		}else {
			storage.setStorageStatu(0);
			storageService.update(storage);
		}
		return json;
	}
	
	/**
	 * 获取所有文件系统
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/getFileSys.action",method = RequestMethod.GET)
	public JSONObject getFileSys(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("code", 0);
		String[] fileSysArray = SshUtil.executeLocalRerurn("sudo df -h |awk '{if(NR>1){print $1,$2,$3,$4,$5,$6}}'").split(",");
		List<FileSys> fileSysList= new ArrayList<FileSys>();
		for(String fileSysStr : fileSysArray) {
			String[] attr = fileSysStr.split(" ");
			if(attr[0].contains("loop"))continue;
			FileSys fileSys = new FileSys();
			fileSys.setName(attr[0]);
			fileSys.setCap(attr[1]);
			fileSys.setUsed(attr[2]);
			fileSys.setCanUse(attr[3]);
			fileSys.setUsePersent(attr[4]);
			fileSys.setMountPoint(attr[5]);
			fileSysList.add(fileSys);
		}
		json.put("data", fileSysList);
		return json;
	}
	
	/**
	 * 磁盘性能测试
	 * @param condition
	 * @param request
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path="/diskTest.action",method = RequestMethod.POST)
	public JSONObject diskTest(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		json.put("success", true);
		String mountPoint = condition.getString("localPath");
		String shPath = PropertiesUtil.get("diskTestPath")+"diskTest.sh";
		String testCommand = "sudo chmod 777 "+shPath +";sudo sh "+ shPath + " "+mountPoint;
		//执行测试脚本
		SshUtil.executeLocalRerurn(testCommand);
		//打印日志 并删除
        Runtime run = Runtime.getRuntime();
        Process process = run.exec(new String[] {"/bin/sh", "-c", "sudo cat "+mountPoint+"/fileio.log;sudo rm -rf "+mountPoint+"/fileio.log"});
        InputStream in = process.getInputStream();
        BufferedReader bs = new BufferedReader(new InputStreamReader(in));
        String result = null;
        String results = "";
        while ((result = bs.readLine()) != null) {
        	results += result+"\n";
        }
        in.close();
        process.destroy();
        json.put("test", results);
        return json;
	}
}
