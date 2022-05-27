package com.example.demo;

import java.util.List;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.KvmServer;
import com.example.demo.empty.Vm;
import com.example.demo.service.KvmServerService;
import com.example.demo.service.VmService;
import com.example.demo.util.LibvirtUtil;
import com.example.demo.util.SshUtil;


@Component
public class InitCommand implements ApplicationRunner {
	
	@Autowired
	private KvmServerService kvmService;
	
	@Autowired
	private VmService vmService;
	

	@Override
	public void run(ApplicationArguments args) throws Exception {
		//检查NOVNC服务是否开启
		String result = SshUtil.executeLocalRerurn("sudo lsof -i:5080");
		if(!result.contains("5080")) {
			SshUtil.executeLocal("systemctl start novnc");
		}
		//开启刷新虚拟机状态及IP
		class LibvirtThread extends Thread{
			public void run(){
				while(true) {
					List<KvmServer> allKvmServer = kvmService.getAllKvmServer();
					if(allKvmServer != null && allKvmServer.size() > 0) {
						KvmServer kvmServer = allKvmServer.get(0);
					try {
						Connect connect  = LibvirtUtil.getConnect(kvmServer);
						List<Vm> vmList = vmService.getVmByKvmServerId(kvmServer.getKvmServerId());
						for(Vm vm : vmList) {
							Domain domain = connect.domainLookupByName(vm.getVmName());
							String vmIp = "";
							if(domain.isActive() == 1) {
								String command = "virsh qemu-agent-command "+vm.getVmName()+" '{\"execute\":\"guest-network-get-interfaces\"}'";
								String results = SshUtil.executeReturn(kvmServer, command);
								if(!results.contains("error")) {
									JSONObject json = JSONObject.parseObject(results);
									try {
										JSONArray networks = json.getJSONArray("return");
										for(int k=0; k<networks.size(); k++) {
											JSONArray ipas = networks.getJSONObject(k).getJSONArray("ip-addresses");
											if(ipas != null) {
												for(int j=0; j<ipas.size(); j++) {
													if(ipas.getJSONObject(j).getString("ip-address-type").equals("ipv4")  && !ipas.getJSONObject(j).getString("ip-address").equals("127.0.0.1")) {
														vmIp = ipas.getJSONObject(j).getString("ip-address");
													}
												}
											}
										}
									} catch (Exception e) {}
								}
							}
							if(domain.isActive() != vm.getVmState() || !vmIp.equals(vm.getVmIp())) {
								vm.setVmState(domain.isActive());
								vm.setVmIp(vmIp);
								vmService.update(vm);
							}
						}
					} catch (Exception e) {}
				}
					try {
						sleep(3000);
					} catch (InterruptedException e) {}
				}
			}
		}
		LibvirtThread thread = new LibvirtThread();
		thread.start();
	}

}
