var table = layui.table;
var $ = layui.jquery;
var userVmId = 0;

//初始化用户表格
table.render({
  elem: '#userGrid'
  ,height:$(window).height()-110
  ,cellMinWidth: 80
  ,url:"/user/doPageSelect.action?level=3"
   ,id:"userGrid"
  ,cols: [[{
		type: 'radio'
	},{
		field: 'userName',
		title: '用户名',
		align: 'center',
		width: '50%'
	},{
		field: 'realName',
		title: '姓名',
		align: 'center'
	}]]
	,parseData: function(res) {
		if (res.data.length > 0) {
			res.data[0].LAY_CHECKED = true;
		}
	}
	,done: function(res){
		if(res.data.length > 0){
			table.reload("userVmGrid",{
				url:"/userVm/doPageSelect.action?userId="+res.data[0].userId
			})
		}else{
			table.reload("userVmGrid",{
				url:"/userVm/doPageSelect.action?userId="+0
			})
		}
	}
});

//初始化用户表格
table.render({
  elem: '#userVmGrid'
  ,height:$(window).height()-110
  ,cellMinWidth: 80
  ,data:[]
   ,id:"userVmGrid"
  ,cols: [[{
		type: 'radio'
	},{
		field: 'vmName',
		title: '桌面名',
		align: 'center',
		width: '20%'
	},{
		field: 'hostName',
		title: '主机IP',
		align: 'center',
		width: '20%'
	},{
		field: 'agreement',
		title: '协议',
		align: 'center',
		width: '15%',
		templet: function(d) {
			if(d.params.indexOf("remote-app") != -1){
				return '<span class="ant-tag ant-tag-cyan" >Remote App</span>'
			}else if (d.agreement == 1) {
				return '<span class="ant-tag ant-tag-cyan" >RDP</span>'
			} else if(d.agreement == 2){
				return '<span class="ant-tag ant-tag-blue" >SSH</span>'
			}else if(d.agreement == 3){
				return '<span class="ant-tag ant-tag-purple">VNC</span>'
			}else if(d.agreement == 4){
				return '<span class="ant-tag ant-tag-geekblue">TELNET</span>'
			}
		}
	},{
		field: 'port',
		title: '端口',
		align: 'center',
		width: '10%'
	},{
		field: 'username',
		title: '用户名',
		align: 'center',
		width: '15%'
	},{
		field: 'desc',
		title: '备注',
		align: 'center'
	}]],
	parseData: function(res) {
		if (res.data.length > 0) {
			res.data[0].LAY_CHECKED = true;
		}
	}
});

//选择用户事件
table.on('radio(userGrid)',function (obj){
	table.reload("userVmGrid",{
		url:"/userVm/doPageSelect.action?userId="+obj.data.userId
	})
})

//添加用户
$("#adduser").click(function (){
	layui.layer.open({
		type : 2,
		closeBtn : 2,
		resize : false,
		title : '添加用户',
		area : [ '400px', '320px' ],
		content : ['win/addUser.html', 'no' ]
	});
})

//删除用户
$("#deluser").click(function (){
	var checkUser = table.checkStatus('userGrid').data;
	if(checkUser == 0){
		layer.msg("请选择一个用户！");
	}else{
		layer.confirm('是否确定删除该用户？', {
			btn: ['确定', '取消'],
			closeBtn: 0,
			icon: 3,
			btnAlign: 'c',
			title: '操作确认'
		},function() {
			HyAjax.Delete("/user/doDelete.action?userId="+checkUser[0].userId,function (res){
				if(res.success){
					Dialog.successMsg("删除成功！",function (){
						table.reload("userGrid");
						layer.closeAll();
					});
				}else{
					Dialog.errorMsg(res.message);
				}
			})
		})
	}
})

//重置密码
$("#resetPassword").click(function (){
	var checkUser = table.checkStatus('userGrid').data;
	if(checkUser == 0){
		layer.msg("请选择一个用户！");
	}else{
		layer.confirm('是否确定重置该用户密码？', {
			btn: ['确定', '取消'],
			closeBtn: 0,
			icon: 3,
			btnAlign: 'c',
			title: '操作确认'
		},function() {
			HyAjax.Get("/user/resetPassword.action?userId="+checkUser[0].userId,function (res){
				if(res.success){
					Dialog.successMsg("重置成功！",function (){
						layer.closeAll();
					});
				}else{
					Dialog.errorMsg(res.message);
				}
			})
		})
	}
})

//添加桌面
$("#addDesktop").click(function (){
	var checkUser = table.checkStatus('userGrid').data;
	if (checkUser.length == 0) {
		layer.msg('请先添加普通用户！ ')
	} else {
		layui.layer.open({
			type: 2,
			closeBtn: 2,
			resize: false,
			title: '修改配置',
			area: ['900px', '550px'],
			content: ['win/bindVm.html', 'no']
		})
	}
})

//删除桌面
$("#delDesktop").click(function (){
	var checkUser = table.checkStatus('userVmGrid').data;
	if (checkUser.length == 0) {
		layer.msg('请选择一条桌面记录！ ')
	} else {
		HyAjax.Delete("/userVm/doDelete.action?userVmId="+checkUser[0].userVmId,function (res){
			if(res.success){
				Dialog.successMsg("删除成功！",function (){
					table.reload("userVmGrid");
					layer.closeAll();
				});
			}else{
				Dialog.errorMsg(res.message);
			}
		})
	}
})

//修改配置
$("#editDesktop").click(function (){
	var checkVm =  table.checkStatus("userVmGrid").data;
	if(checkVm.length < 1){
		layer.msg('请选择一条桌面记录！ ')
	}else{
		layer.open({
			type: 2,
			closeBtn: 2,
			resize: false,
			title: '添加桌面',
			area: ['900px', '550px'],
			content: ['win/bindVm.html', 'no'],
			success: function(layero, index) {
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.init(checkVm[0].userVmId);
			}
		})
	}
})

//接入
$("#openVm").click(function (){
	var checkVm = table.checkStatus('userVmGrid').data;
	if (checkVm.length == 0) {
		layer.msg('请选择一条桌面记录！ ')
	} else {
		userVmId = checkVm[0].userVmId;
		window.open("win/desktop.html");
	}
})

//本地接入
$("#openLocalVm").click(function (){
	var checkVm = table.checkStatus('userVmGrid').data;
	if (checkVm.length == 0) {
		layer.msg('请选择一条桌面记录！ ')
	} else {
		if(checkVm[0].agreement != 1){
			layer.msg('暂不支持该桌面协议！');
			return false;
		}
		var	loading = layer.load(2, {
	        shade: 0.1,
	        time: 2*30000
	    });
		HyAjax.Post("/userVm/openLocalVm.action",{userVmId:checkVm[0].userVmId},function (res){
			if(res.success){
				Dialog.successMsg("接入成功！",function (){
					layer.close(loading);
				});
			}else{
				Dialog.errorMsg("接入失败！");
				layer.close(loading);
			}
		},function (){
			Dialog.errorMsg("接入失败！");
			layer.close(loading);
		})
	}
})

//复制桌面
$("#copyVm").click(function (){
	layui.layer.open({
		type: 2,
		closeBtn: 2,
		resize: false,
		title: '桌面共享',
		area: ['1200px', '500px'],
		content: ['win/copyDesktop.html', 'no']
	})
})

//单击行选中
$(document).on("click",".layui-table-body table.layui-table tbody tr", function () {
	$(this).find('i[class="layui-anim layui-icon"]').trigger("click");
 });
