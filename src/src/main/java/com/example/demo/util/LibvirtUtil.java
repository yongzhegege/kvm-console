package com.example.demo.util;
import java.util.ArrayList;
import java.util.List;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;

import com.example.demo.empty.KvmServer;

/**
 *	LIBVIRT连接工具
 * @author root
 *
 */
public class LibvirtUtil {
	
	private static List<Connect> ConnectList = new ArrayList<Connect>();
	
	public static void removeConnect(KvmServer kvmServer) {
		String url = "qemu+ssh://"+kvmServer.getKvmServerUser()+"@"+kvmServer.getKvmServerIp()+"/system";
		Connect c = null;
		for(Connect connect : ConnectList) {
			try {
				if(connect.getURI().equals(url)) {
					c = connect;
					break;
				}
			} catch (LibvirtException e) {
				e.printStackTrace();
			}
		}
		ConnectList.remove(c);
	}
	
	public static Connect getConnect(KvmServer kvmServer) throws LibvirtException{
		String url = "qemu+ssh://"+kvmServer.getKvmServerUser()+"@"+kvmServer.getKvmServerIp()+"/system";
		for(Connect connect : ConnectList) {
			if(connect.getURI().equals(url)) {
				return connect;
			}
		}
		Connect newCon = new Connect(url);
		ConnectList.add(newCon);
		return newCon;
	}
	
}
