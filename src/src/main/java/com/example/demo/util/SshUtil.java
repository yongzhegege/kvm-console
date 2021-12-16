package com.example.demo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.empty.KvmServer;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * SSH连接工具
 * @author root
 *
 */
public class SshUtil {
	
	//SESSION集合
	private static List<Session> sessionList = new ArrayList<Session>();
	
	/**
	 * 获取SSH session会话
	 * @param kvmServer
	 * @return
	 * @throws JSchException
	 */
	private static Session getSession(KvmServer kvmServer) throws JSchException{
		//遍历SESSION集合 是否存在该SESSION
		for(Session session : sessionList) {
			if(session.getHost().equals(kvmServer.getKvmServerIp())){
				return session;
			}
		}
		//如不存在则新创建
		JSch jsch = new JSch();
		Session session = jsch.getSession(kvmServer.getKvmServerUser(),kvmServer.getKvmServerIp(),22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(kvmServer.getKvmServerPassword());
		session.connect();
		sessionList.add(session);
		return session;
	}
	
	/**
	 * 移除SESSION
	 * @param kvmServer
	 */
	private static  void removeSession(KvmServer kvmServer) {
		Session removeSession = null;
		for(Session  session : sessionList) {
			if(session.getHost().equals(kvmServer.getKvmServerIp())){
				removeSession = session;
				break;
			}
		}
		if(removeSession != null) {
			sessionList.remove(removeSession);
		}
	}
	
	/**
	 * 校验服务用户名密码，及生成秘钥
	 * @param kvmServer
	 * @return
	 */
	public static JSONObject checkServer(KvmServer kvmServer){
		JSONObject json = new JSONObject();
		String copykeyCom = "sudo expect /opt/kvm/sh/dcs-copy-id "+kvmServer.getKvmServerIp()+" "+kvmServer.getKvmServerUser()+" "+kvmServer.getKvmServerPassword();
		json.put("copykeyCom", copykeyCom);
		try {
			getSession(kvmServer);
			executeLocal(copykeyCom);
			//生成isos文件夹 drivers 文件夹
			execute(kvmServer, "sudo mkdir /home/isos;sudo mkdir /home/drivers");
			//生成usb模板文件
		    execute(kvmServer, "sudo rm -rf /opt/usb.xml");
		    execute(kvmServer, "sudo touch /opt/usb.xml;sudo chmod 777 /opt/usb.xml");
		    String[] body = { "<hostdev mode='subsystem' type='usb' managed='no'>", "<source>", "<vendor id='vendor1'/>", "<product id='product1'/>",  "</source>", "</hostdev>" };
		    String command = "";
		    byte b2;
		    int j;
		    String[] arrayOfString2;
		    for (j = (arrayOfString2 = body).length, b2 = 0; b2 < j; ) {
		      String b = arrayOfString2[b2];
		      command = String.valueOf(command) + "sudo echo \"" + b + "\" >> /opt/usb.xml;";
		      b2++;
		    } 
		    execute(kvmServer, command);
			json.put("success",true);
		} catch (JSchException e) {
			removeSession(kvmServer);
			try {
				execute(kvmServer, "sudo mkdir /home/isos;sudo mkdir /home/drivers");
				getSession(kvmServer);
				json.put("success",true);
			} catch (JSchException e1) {
				json.put("success",false);
				json.put("code",619L);
				json.put("message","ssh连接异常，请检查用户名和密码！");
			}
		} catch (IOException e){
			json.put("code",619L);
			json.put("message","ssh命令发送异常，请重试！");
		}
		return json;
	}
	
	/**
	 * kvmServer执行命令并返回字符串
	 * @param kvmServer
	 * @param command
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public static String executeReturn(KvmServer kvmServer,String command) throws JSchException, IOException{
		Session session = getSession(kvmServer);
		ChannelExec channelExec = null;
		try {
			channelExec = (ChannelExec) session.openChannel("exec");
		} catch (Exception e) {
			session.setHost("");
			session = getSession(kvmServer);
			channelExec = (ChannelExec) session.openChannel("exec");
		}
		channelExec.setErrStream(System.err);
		InputStream in = channelExec.getInputStream();
		channelExec.setCommand(command);
		channelExec.connect();
		String out = IOUtils.toString(in,"UTF-8");
		channelExec.disconnect();
		return out;
	}
	
	public static void execute(KvmServer kvmServer,String command) throws JSchException{
		Session session = getSession(kvmServer);  
		ChannelExec channelExec = null;
		try {
			channelExec = (ChannelExec) session.openChannel("exec");
		} catch (Exception e) {
			session.setHost("");
			session = getSession(kvmServer);
			channelExec = (ChannelExec) session.openChannel("exec");
		}
        channelExec.setErrStream(System.err);  
        channelExec.setCommand(command); 
        channelExec.connect();
	}
	
	/**
	 * 执行本地命令并返回结果
	 * @param command
	 * @return
	 * @throws IOException
	 */
	public static String executeLocalRerurn(String command) throws IOException{
        Runtime run = Runtime.getRuntime();
        Process process = run.exec(new String[] {"/bin/sh", "-c", command});
        InputStream in = process.getInputStream();
        BufferedReader bs = new BufferedReader(new InputStreamReader(in));
        String result = null;
        String results = "";
        while ((result = bs.readLine()) != null) {
        	results += ","+result;
        }
        in.close();
        process.destroy();
        if(!"".equals(results)){
        	results = results.substring(1);
        }
        return results;
    } 
	
	/**
	 * 上传文件
	 * @param directory
	 * @param file
	 * @return
	 * @throws JSchException 
	 * @throws SftpException 
	 * @throws IOException 
	 */
	public static boolean uploadFile(KvmServer kvmServer,String directory,String uploadFile) throws JSchException, SftpException, IOException {
		Session session = getSession(kvmServer);  
		Channel  channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		try {
			sftp.cd(directory); // 进入目录
			File file = new File(uploadFile);
			InputStream in = new FileInputStream(file);
			sftp.put(in, file.getName());
			in.close();
		} catch (SftpException sException) {
			if (ChannelSftp.SSH_FX_NO_SUCH_FILE == sException.id) { // 指定上传路径不存在
				sftp.mkdir(directory);// 创建目录
				sftp.cd(directory); // 进入目录
			}
		}finally {
			sftp.disconnect();
			channel.disconnect();
		}
		return true;
	}
	
	
	/**
	 * 下载文件至本地服务器
	 * @param kvmServer
	 * @param filePath
	 * @param localFilePath
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public static boolean downloadFile(KvmServer kvmServer,String filePath,String fileName,String localFilePath) throws JSchException, SftpException, IOException {
		boolean ans = false;
		Session session = getSession(kvmServer);  
		Channel  channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		try {
			sftp.cd(filePath);
			File file = new File(localFilePath);
			sftp.get(fileName, new FileOutputStream(file));
			 ans = true;
		} catch (SftpException sException) {
			ans =  false;
		}finally {
			sftp.disconnect();
			channel.disconnect();
		}
		return ans;
	}

	
	
	/**
	 * 执行本地命令不返回结果
	 * @param command
	 * @throws IOException
	 */
	public static void executeLocal(String command) throws IOException{
        Runtime run = Runtime.getRuntime();
        run.exec(new String[] {"/bin/sh", "-c", command});
	}
}
