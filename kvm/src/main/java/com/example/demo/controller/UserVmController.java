package com.example.demo.controller;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.websocket.GuacamoleWebSocketTunnelEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.empty.UserVm;
import com.example.demo.http.WebRequest;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserVmService;
import com.example.demo.util.SpringUtils;
import com.example.demo.vo.CommonVO;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@Controller
@RequestMapping("/userVm")
@ServerEndpoint(value = "/webSocket", subprotocols = "guacamole")
public class UserVmController extends GuacamoleWebSocketTunnelEndpoint {
	
	@Autowired
	private UserVmService userVmService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LogService logService;
	
	/**
	 * 查询桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doPageSelect.action",method = RequestMethod.GET)
	public JSONObject doPageSelect(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		JSONObject json = new JSONObject();
		json.put("data", userVmService.allSelectLists(userVm));
		json.put("code", 0);
		json.put("success", true);
		return json;
	}
	
	
	/**
	 * 查询未绑定的用户桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doPageSelectUnbind.action",method = RequestMethod.GET)
	public JSONObject doPageSelectUnbind(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		JSONObject json = new JSONObject();
		json.put("data", userVmService.doPageSelectUnbind(userVm));
		json.put("code", 0);
		json.put("success", true);
		return json;
	}
	
	/**
	 * 添加桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doInsert.action",method = RequestMethod.POST)
	public JSONObject doInsert(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		List<UserVm> userList = userVmService.allSelectLists(userVm);
		for(UserVm vm : userList) {
			if(vm.getVmName().equals(userVm.getVmName())) {
				json.put("success", false);
				json.put("message", "该桌面名已存在，请重试！");
				return json;
			}
		}
		 userVmService.insert(userVm);
		json.put("success", true);
		logService.insert(request, "添加桌面("+userVm.getVmName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	
	/**
	 * 拷贝桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/copyUserVm.action",method = RequestMethod.POST)
	public JSONObject copyUserVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		String[] userVmIds = condition.getString("userVmIds").split(",");
		Integer userId = condition.getInteger("userId");
		String userVmNames = "";
		for(String userVmId : userVmIds) {
			UserVm userVm = userVmService.load(Integer.valueOf(userVmId));
			userVmNames += ","+userVm.getVmName();
			userVm.setUserId(userId);
			userVmService.insert(userVm);
		}
		JSONObject json = new JSONObject();
		json.put("success", true);
		logService.insert(request, "共享桌面("+userVmNames.substring(1)+")用户("+userService.getUserByUserId(userId).getUserName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	/**
	 * 修改桌面配置
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doUpdate.action",method = RequestMethod.POST)
	public JSONObject doUpdate(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		JSONObject json = new JSONObject();
		 userVmService.update(userVm);
		json.put("success", true);
		logService.insert(request, "修改桌面("+userVm.getVmName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 删除桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/doDelete.action",method = RequestMethod.DELETE)
	public JSONObject doDelete(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		UserVm userVm = (UserVm)condition.toBeanResolver(UserVm.class);
		String vmName = userVmService.load(userVm.getUserVmId()).getVmName();
		JSONObject json = new JSONObject();
		userVmService.delete(userVm);
		json.put("success", true);
		logService.insert(request, "删除桌面("+vmName+")", json.getBoolean("success")?1:0);
		return json;
	}
	
	/**
	 * 本地接入
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/openLocalVm.action",method = RequestMethod.POST)
	public JSONObject openLocalVm(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		UserVm userVm = userVmService.load(condition.getInteger("userVmId"));
		String ip = WebRequest.getIpAddress(request);
		String url = "http://"+ip+":8080/desktop/connect.action";
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		FormBody.Builder builder = new FormBody.Builder(Charset.forName("UTF8"));
		builder.add("hostName", userVm.getHostName());
		builder.add("username", userVm.getUsername());
		builder.add("password", userVm.getPassword());
		if(userVm.getParams() != null) {
			if(userVm.getParams().contains("remote-app")) {
				builder.add("remote-app", JSON.parseObject(userVm.getParams()).getString("remote-app"));
			}
			if(userVm.getParams().contains("enable-drive") && userVm.getParams().contains("drive-path")) {
				builder.add("drive-path", JSON.parseObject(userVm.getParams()).getString("drive-path"));
			}
		}
		FormBody body = builder.build();
		Request req = new Request.Builder()
		  .url(url)
		  .post(body)
		  .build();
		client.newCall(req).execute();
		JSONObject json = new JSONObject();
		json.put("success", true);
		logService.insert(request, "本地接入桌面("+userVm.getVmName()+")", json.getBoolean("success")?1:0);
		return json;
	}
	

	/**
	 * 远程桌面
	 */
	@Override
	protected GuacamoleTunnel createTunnel(Session session, EndpointConfig c) throws GuacamoleException {
		UserVmService userVmService = SpringUtils.getObject(UserVmService.class);
		String width = session.getRequestParameterMap().get("width").get(0);  
		String height = session.getRequestParameterMap().get("height").get(0);
		Integer userVmId = Integer.valueOf(session.getRequestParameterMap().get("userVmId").get(0));
		UserVm userVm = userVmService.load(userVmId);
		GuacamoleConfiguration config = new GuacamoleConfiguration();
		Integer agreement = userVm.getAgreement();
	      config.setParameter("height", height);
	      config.setParameter("width", width);
	      //服务器IP
	      config.setParameter("hostname", userVm.getHostName());
		    //端口
	      config.setParameter("port", userVm.getPort().toString());
		if(agreement == 1) {  
		      //远程桌面对象使用的远程协议
		      config.setProtocol("rdp");
		      //忽略服务器证书
		      config.setParameter("ignore-cert", "true");
		      //用户名
		      config.setParameter("username", userVm.getUsername());
		      //密码
		      config.setParameter("password", userVm.getPassword());
		      //个性化
		      config.setParameter("disable-bitmap-caching", "true");
		      config.setParameter("disable-glyph-caching", "true");
		      config.setParameter("disable-offscreen-caching", "true");
		      config.setParameter("enable-desktop-composition", "true");
		      config.setParameter("enable-font-smoothing", "true");
		      config.setParameter("enable-full-window-drag", "true");
		      config.setParameter("enable-menu-animations", "true");
		      config.setParameter("enable-theming", "true");
		      config.setParameter("enable-wallpaper", "true");
		      config.setParameter("resize-method", "reconnect");
		}else if(agreement == 2) {
			config.setProtocol("ssh");
		      //用户名
		      config.setParameter("username", userVm.getUsername());
		      //密码
		      config.setParameter("password", userVm.getPassword());
		}else if(agreement == 3) {
			config.setProtocol("vnc");
			config.setParameter("password", userVm.getPassword());
		}else if(agreement == 4) {
			config.setProtocol("telnet");
	      //用户名
	      config.setParameter("username", userVm.getUsername());
	      //密码
	      config.setParameter("password", userVm.getPassword());
		}
		if(!"0".equals(userVm.getParams())) {
			 Map<String,String> paramMap =  JSONObject.parseObject(userVm.getParams(), new TypeReference<Map<String, String>>(){});
			 if(paramMap != null) {
					for (Map.Entry<String, String> entry : paramMap.entrySet()) {
						config.setParameter(entry.getKey(), entry.getValue());
					}
			 }
		}
	      GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
	              new InetGuacamoleSocket("localhost", 4822),
	              config
	      );
	      return new SimpleGuacamoleTunnel(socket);
	}
	
	
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		// TODO Auto-generated method stub
		super.onClose(session, closeReason);
	}

	/**
	 * 查询单个桌面
	 * @param condition
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(path = "/load.action", method = RequestMethod.GET)
	public JSONObject load(@RequestAttribute("condition") CommonVO condition, HttpServletRequest request) throws Exception {
		JSONObject json = new JSONObject();
		UserVm userVm = userVmService.load(condition.getInteger("userVmId"));
		json.put("success", true);
		json.put("data", userVm);
		return json;
	}
	
	
}
