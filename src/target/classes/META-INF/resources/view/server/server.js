var $ = layui.jquery;
var table = layui.table;
$("#serverDiv").css("height",$(window).height()-430+"px");
loadChart();
//获取CPU磁盘内存等信息
HyAjax.Get('/kvmServer/getServerInfo.action',function (res){
	if(res.success){
		var serverInfo = res.data;
		$("#cpuPersent").html(serverInfo.cpu);
		$("#cpu").attr("data-percent",serverInfo.cpu);
		$("#diskPersent").html(serverInfo.disk);
		$("#disk").attr("data-percent",serverInfo.persentDisk);
		$("#memoryPersent").html(serverInfo.memory);
		$("#memory").attr("data-percent",serverInfo.persentMemory);
		var version = serverInfo.serverVersion;
		var _html = "操作系统:	"+version[0]+"<p/>"+"系统版本:	"+version[1]+"<p/>"+"内核版本:	"+serverInfo.kernelVersion+"<p/>";
		if(serverInfo.firewallOpen){
			_html += "系统防火墙:已开启<p/>";
		}else{
			_html += "系统防火墙:未开启<p/>";
		}
		if(serverInfo.hasOwnProperty("activeTime")){
			_html += "累计运行:"+serverInfo.activeTime+"天";
		}
		$("#serverVersion").html(_html);
		loadChart();
	}else{
		Dialog.errorMsg(res.message);
	}
});

//加载饼状图
function loadChart(){
	$('.percentage').easyPieChart({
		animate: 1000
	});
	$('.percentage-light').easyPieChart({
		barColor: function(percent) {
			percent /= 100;
			return "rgb(" + Math.round(255 * (1-percent)) + ", " + Math.round(255 * percent) + ", 0)";
		},
		trackColor: '#666',
		scaleColor: false,
		lineCap: 'butt',
		lineWidth: 15,
		animate: 1000
	});
}

//初始化服务器表格
table.render({
  elem: '#kvmServerGrid'
  ,cellMinWidth: 80
  ,url:"/kvmServer/doPageSelect.action"
    ,cols: [[
        {type:'radio'}
        ,{field:'kvmServerName', title: 'ServerName', align:'center',width:'20%'}
        ,{field:'kvmServerIp',title: 'IP', align:'center',width:'30%'}
        ,{field:'vmNum',title: 'VmNum', align:'center',width:'25%'}
        ,{field:'state',title: 'State',align:'center',templet: function(d){
            if(d.state == 0){
                return '<span style="color:red">离线</span>';
            }else{
                return '<span style="color:lightgreen">在线</span>';
            }
       }}
    ]]
	,id:"kvmServerGrid"
   ,parseData : function(res) {
	    if(res.success){
	        if (res.data.length>0) {
	            res.data[0].LAY_CHECKED = true;
	        }
	    }
	}
});

//单击行选中
$(document).on("click",".layui-table-body table.layui-table tbody tr", function () {
	$(this).find('i[class="layui-anim layui-icon"]').trigger("click");
 });


//刷新服务器表格
function queryKvmServer(){
	layui.layer.closeAll();
	table.reload("kvmServerGrid");
}

//弹出添加服务器窗口
$("#addKvmServer").click(function (){
    layer.open({
        type: 2,
        closeBtn:2,
        resize:false,
        title: '添加计算节点',
        area: ['450px', '440px'],
        content: ['win/addServer.html', 'no']
    });
})

//服务器开机重启
function kvmServerOption(type){
	var checkServer = table.checkStatus('kvmServerGrid').data;
	if(checkServer.length == 1){
		var state = checkServer[0].state;
		var kvmServerId = checkServer[0].kvmServerId;
		if(type != 2 && state != 1){
			Dialog.alert('服务器正处于关机状态！ ');
		}else{
			var msg = "";
			var url = "";
			if(type == 0){
				msg = "是否确认将该服务器关机?";
				url = "/kvmServer/powerOff.action?kvmServerId="+kvmServerId;
			}else if(type ==1){
				msg = "是否确认将该服务器重启?";
				url = "/kvmServer/reboot.action?kvmServerId="+kvmServerId;
			}else if(type ==2){
				msg = "是否确认刷新该服务器状态?";
				url = "/kvmServer/refresh.action?kvmServerId="+kvmServerId;
			}
		    layer.confirm(msg, {
		        btn : [ '确定', '取消' ], // 按钮3
		        closeBtn : 0,
		        icon : 3,
		        btnAlign : 'c',
		        title : '操作确认'
		    }, function(){
		        HyAjax.Get(url , function(result) {
		            if (result.success) {
		                Dialog.successMsg('操作成功！ ', function(){
		                	queryKvmServer();
		                });
		            } else {
		                Dialog.errorMsg('操作失败, '+result.message);
		            }
		        });
		   });
		}
	}else{
		Dialog.alert('请选择一台服务器！ ');
	}
}

//删除kvm服务器
function deleteKvmServer(kvmServerId){
	var checkServer = table.checkStatus('kvmServerGrid').data;
	if(checkServer.length == 1){
		var kvmServerId = checkServer[0].kvmServerId;
	    layer.confirm('是否确定将该服务器删除？', {
	        btn : [ '确定', '取消' ], // 按钮3
	        closeBtn : 0,
	        icon : 3,
	        btnAlign : 'c',
	        title : '操作确认'
	    }, function(){
	        HyAjax.Delete("/kvmServer/doDelete.action?kvmServerId=" + kvmServerId , function(result) {
	            if (result.success) {
	                Dialog.successMsg('删除成功！ ', function(){
	                	queryKvmServer();
	                });
	            } else {
	                Dialog.errorMsg('删除失败, '+result.message);
	            }
	        });
	   });
	}else{
		Dialog.alert('请选择一台服务器！ ');
	}
}

//打开终端
function openSsh(){
	var checkServer = table.checkStatus('kvmServerGrid').data;
	if(checkServer.length == 1){
		var url = "http://"+checkServer[0].kvmServerIp+":14377";
		var num = parent.layer.open({
							type:2,
							shade: 0,
							content:[url,'no'],
							title:'云终端',
							maxmin:true,
							area:['700px','500px']
						});
	}else{
		Dialog.alert('请选择一台服务器！ ');
	}
}

//开关DHCP
function manaDhcp(statu){
	var checkServer = table.checkStatus('kvmServerGrid').data;
	if(checkServer.length == 1){
		var param = {
			kvmServerId : checkServer[0].kvmServerId,
			statu : statu
		}
		HyAjax.Post("/kvmServer/manaDhcp.action",param,function(result) {
			if(result.success){
				Dialog.successMsg("操作成功！");
			}else{
				Dialog.errorMsg("操作失败！");
			}
		});
	}else{
		Dialog.alert('请选择一台服务器！ ');
	}
}
