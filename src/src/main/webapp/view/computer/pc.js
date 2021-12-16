var $ = layui.jquery;
var table = layui.table;
var dropdown = layui.dropdown;
var diskManaIndex = 0;

$(".layui-card-body").css("height",$(window).height()-100+"px");

//初始化虚拟机表格
table.render({
  elem: '#vmGrid'
  ,height:$(window).height()-110
  ,cellMinWidth: 80
  ,url:"/vm/doPageSelect.action"
   ,id:"vmGrid"
  ,cols: [[{
		type: 'checkbox'
	},{
		field: 'vmName',
		title: 'Name',
		align: 'center',
		width: '13%',
		templet: function(d) {
			return "<span title='双击进入虚拟机'>"+d.vmName+"</span>"
		}
	},{
		field: 'vmState',
		title: 'State',
		align: 'center',
		width: '8%',
		templet: function(d) {
			if (d.vmState == 0) {
				return '<span style="color:red">offline</span>'
			} else {
				return '<span style="color:lightgreen">online</span>'
			}
		}
	},{
		field: 'vmMac',
		title: 'CHM',
		align: 'center',
		width: '12%',
		templet: function(d) {
			return d.vmCpu + "C/" + d.vmDisk + "G/" + d.vmMemory + "G"
		}
	},
	{
		field: 'vmIp',
		title: 'IP',
		align: 'center',
		width: '15%'
	},{field:'vmNetwork',title: 'Network',align:'center',width:'17%'},{
		field: 'vmDefaultStart',
		title: 'BootFirst',
		align: 'center',
		templet: function(d) {
			if (d.vmDefaultStart == "cdrom") {
				return "CDROM"
			} else if (d.vmDefaultStart == "hd") {
				return "HD"
			} else if (d.vmDefaultStart == "network") {
				return "NETWORK"
			}
		}
	},{
		field: 'vmType',
		title: 'BootMode',
		align: 'center'
	}]]
	 ,parseData: function(res) {
		if (res.data.length > 0) {
			res.data[0].LAY_CHECKED = true;
		}
	}
});

//弹出创建虚拟机窗口
$("#mkdirVm").click(function() {
	var checkServer = table.checkStatus('kvmServerGrid').data;
	layui.layer.open({
		type: 2,
		closeBtn: 2,
		resize: false,
		title: '创建虚拟机',
		area: ['800px', '500px'],
		content: ['win/mkdirVmWin.html', 'no'],
		success: function(layero, index) {
			var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init("");
		}
	})
});

//弹出修改虚拟机窗口
function editVm() {
	var selectVm = table.checkStatus('vmGrid');
	if (selectVm.data.length == 1) {
		layui.layer.open({
			type: 2,
			closeBtn: 2,
			resize: false,
			title: '编辑虚拟机',
			area: ['800px', '450px'],
			content: ['win/mkdirVmWin.html', 'no'],
			success: function(layero, index) {
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.initEdit(selectVm.data[0])
			}
		})
	} else {
		Dialog.alert("请有且仅勾选一台虚拟机！")
	}
}

//虚拟机开关及重启
function opVm(num) {
	if (table.checkStatus('vmGrid').data.length >= 1) {
		var selectVm = table.checkStatus('vmGrid').data;
		var vmIds = "";
		var onlineIds = "";
		var offlineIds = "";
		for(var i = 0; i < selectVm.length; i++){
			if(selectVm[i].vmState == 0){
				offlineIds += ","+selectVm[i].vmId;
			}else{
				onlineIds += ","+selectVm[i].vmId;
			}
		}
		var ans = "";
		if (num == 0) {
			ans = "开机";
			if(offlineIds != ""){
				vmIds = offlineIds;
			}else{
				Dialog.alert("所选机器都处于在线状态!");
				return false;
			}
		} else if (num == 1) {
			ans = "关机";
			if(onlineIds != ""){
				vmIds = onlineIds;
			}else{
				Dialog.alert("所选机器都处于离线状态!");
				return false;
			}
		} else if (num == 2) {
			ans = "强制关机";
			if(onlineIds != ""){
				vmIds = onlineIds;
			}else{
				Dialog.alert("所选机器都处于离线状态!");
				return false;
			}
		} else if (num == 3) {
			ans = "重启"
			if(onlineIds != ""){
				vmIds = onlineIds;
			}else{
				Dialog.alert("所选机器都处于离线状态!");
				return false;
			}
		} else if(num == 4){
			ans = "强制重启"
			if(onlineIds != ""){
				vmIds = onlineIds;
			}else{
				Dialog.alert("所选机器都处于离线状态!");
				return false;
			}
		}
		loading = layer.load(2, {
	        shade: 0.1,
	        time: 2*30000
	    });
		HyAjax.Get("/vm/opVm.action?vmId=" + vmIds.substring(1)+ "&option=" + num,function(result) {
			if (result.success) {
				Dialog.successMsg(ans + '成功！ ',
				function() {
					queryVm(selectVm[0].vmId);
				})
			} else {
				Dialog.errorMsg(ans + '失败, ' + result.message);
			}
			layer.close(loading);
		})
	} else {
		Dialog.alert("请至少勾选一台虚拟机！")
	}
}

function refush(){
	table.reload("vmGrid",{
		url:"/vm/doPageSelect.action?refush=1"
	})
}

//删除虚拟机
function delVm() {
	if (table.checkStatus('vmGrid').data.length > 0) {
		var selectVm = table.checkStatus('vmGrid').data;
		var hasStart = false;
		var vmIds = "";
		for(var i = 0; i < selectVm.length; i++){
			if(selectVm[i].vmState != 0){
				hasStart = true;
			}
			vmIds += ","+selectVm[i].vmId;
		}
		if (hasStart) {
			Dialog.alert('请先将所选虚拟机关机！');
		} else {
				layer.confirm('是否确定删除选中虚拟机？', {
					btn: ['确定', '取消'],
					closeBtn: 0,
					icon: 3,
					btnAlign: 'c',
					title: '操作确认'
				},function() {
					layer.confirm('是否删除磁盘文件？', {
						btn: ['取消', '确认'],
						closeBtn: 0,
						icon: 3,
						btnAlign: 'c',
						title: '操作确认'
					},function() {
						HyAjax.Delete("/vm/doDelete.action?vmIds=" + vmIds.substring(1) + "&disk=0",function(result) {
							if (result.success) {
								Dialog.successMsg('删除成功！ ',function() {
									queryVm();
								})
							} else {
								Dialog.errorMsg('删除失败, ' + result.message)
							}
						})
					},function() {
						HyAjax.Delete("/vm/doDelete.action?vmIds=" + vmIds.substring(1) + "&disk=1",function(result) {
							if (result.success) {
								Dialog.successMsg('删除成功！ ',function() {
									queryVm();
								})
							} else {
								Dialog.errorMsg('删除失败, ' + result.message)
							}
						})
					})
				})
			}
		}else{
				Dialog.alert("请至少勾选一台虚拟机！");
		}
}

//虚拟机USB管理
function usbMana() {
	var checkVm = table.checkStatus('vmGrid').data;
	if (checkVm.length == 1) {
		HyAjax.Post("/kvmServer/showUsb.action", {
			kvmServerId: checkVm[0].kvmServerId
		},function(result) {
			if (result.success) {
				layui.layer.open({
					type: 2,
					closeBtn: 2,
					resize: false,
					title: 'USB列表',
					area: ['900px', '360px'],
					content: ['win/usbManaWin.html', 'no'],
					success: function(layero, index) {
						var iframeWin = window[layero.find('iframe')[0]['name']];
						iframeWin.init(result.data)
					}
				})
			} else {
				Dialog.errorMsg(result.message)
			}
		})
	} else {
		Dialog.alert('请有且仅选择一台虚拟机！ ')
	}
}

//显示虚拟机磁盘及快照
function showVmDisk() {
	layer.closeAll();
	var data = table.checkStatus('vmGrid').data;
	if (data.length == 1) {
		var loadSnap = false;
		var ifHeight = '350px';
		if (data[0].vmState == 0) {
			loadSnap = true;
			ifHeight = '650px'
		}
		layui.layer.open({
			type: 2,
			closeBtn: 2,
			resize: false,
			title: '磁盘管理<span style="color:red">(请在虚拟机关机状态查看快照信息)</span>',
			area: ['1000px', ifHeight],
			content: ['win/vmDiskMana.html', 'no'],
			success: function(layero, index) {
				diskManaIndex = index;
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.init(data[0].vmId, data[0].kvmServerId, loadSnap)
			}
		})
	} else {
		Dialog.alert("请有且仅选择一台虚拟机！")
	}
}

//虚拟机磁盘扩容
function expansionDisk(kvmServerId, diskPath) {
	layer.close(diskManaIndex);
	layui.layer.open({
		type: 2,
		closeBtn: 2,
		resize: false,
		title: '磁盘扩容',
		area: ['500px', '200px'],
		content: ['win/expansionDisk.html', 'no'],
		success: function(layero, index) {
			var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init(kvmServerId, diskPath)
		},
		cancel: function() {
			showVmDisk()
		}
	})
}

//添加虚拟机磁盘
function addVmDataDisk(kvmServerId, vmId, diskList) {
	layer.close(diskManaIndex);
	layui.layer.open({
		type: 2,
		closeBtn: 2,
		resize: false,
		title: '添加数据盘',
		area: ['500px', '300px'],
		content: ['win/addVmDataDisk.html', 'no'],
		success: function(layero, index) {
			var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init(kvmServerId, vmId, diskList)
		},
		cancel: function() {
			showVmDisk()
		}
	})
}

//显示虚拟机XML文件
function showXml() {
	var data = table.checkStatus('vmGrid').data;
	if (data.length == 1) {
		var param = {
			vmId: data[0].vmId,
			kvmServerId: data[0].kvmServerId
		};
		HyAjax.Post("/vm/showXml.action", param,function(result) {
			if (result.success) {
				layer.open({
					type: 1,
					title: false,
					closeBtn: 1,
					area: '600px;',
					shade: 0.8,
					id: 'LAY_layuipro',
					btnAlign: 'c',
					moveType: 1,
					content: '<textarea style="width:600px;height:600px" placeholder="请输入内容" class="layui-textarea">' + result.xml + '</textarea>'
				})
			} else {
				Dialog.errorMsg(result.message)
			}
		})
	} else {
		Dialog.alert("请有且仅勾选一台虚拟机！")
	}
}


//设置虚拟机是否未开机自启
function autoStart(option) {
	var data = table.checkStatus('vmGrid').data;
	if (data.length > 0) {
		var vmIds = "";
		for (var i = 0; i < data.length; i++) {
			vmIds += "," + data[i].vmId
		}
		var param = {
			kvmServerId: data[0].kvmServerId,
			vmIds: vmIds.substring(1),
			option: option
		}
		HyAjax.Post("/vm/autoStart.action", param,
		function(result) {
			if (result.success) {
				if (option == 0) {
					Dialog.successMsg("取消成功！")
				} else {
					Dialog.successMsg("设置成功！")
				}
			} else {
				Dialog.errorMsg(result.message)
			}
		})
	} else {
		Dialog.alert("请至少选择一台虚拟机！")
	}
}

//显示网卡配置XML文件
function showNetworkXml(){
	var content = "#编辑虚拟机配置文件：sudo vi /etc/libvirt/qemu/XXX.xml（比如：vroute.xml或iKuai8.xml）\n" + 
			"\n" + 
			"     #第一张网卡配置桥接(br_eXXX)\n" + 
			"    <interface type='bridge'>\n" + 
			"      <mac address='32:54:03:02:7c:ce'></mac>\n" + 
			"      <source bridge='br_eno1'></source>\n" + 
			"      <model type='virtio'></model>\n" + 
			"    </interface>\n" + 
			"    \n" + 
			"   #第二张网卡配置NAT(virbr0)\n" + 
			"    <interface type='bridge'>\n" + 
			"      <mac address='32:54:03:02:2c:cf'></mac>\n" + 
			"      <source bridge='virbr0'></source>\n" + 
			"      <model type='virtio'></model>\n" + 
			"    </interface>\n" + 
			"\n"+
			"#使新配置文件生效：sudo virsh define /etc/libvirt/qemu/XXX.xml"
			layer.open({
				type: 1,
				title: false,
				closeBtn: 1,
				area: '650px;',
				shade: 0.8,
				id: 'LAY_layuipro',
				btnAlign: 'c',
				moveType: 1,
				content: '<textarea style="width:650px;height:450px"  class="layui-textarea">' + content+ '</textarea>'
			})
}

//设置虚拟机带宽
function vmUpDown(){
	var data = table.checkStatus('vmGrid').data;
	if (data.length > 0) {
		layui.layer.open({
			type: 2,
			closeBtn: 2,
			resize: false,
			title: '带宽限速',
			area: ['550px', '180px'],
			content: ['win/vmUpDown.html', 'no'],
			success: function(layero, index) {
				var iframeWin = window[layero.find('iframe')[0]['name']];
				iframeWin.init(data[0]);
			}
		})
	}else{
		Dialog.alert("请有且仅选择一台虚拟机！")
	}
}

//设置虚拟机未模板
function addTemplate() {
	var selectVm = table.checkStatus('vmGrid').data;
	if (selectVm.length != 1) {
		Dialog.alert("请有且仅勾选一台虚拟机！")
	} else {
		if (selectVm[0].vmState != 0) {
			Dialog.alert("请先将该虚拟机关机！")
		} else {
			layer.confirm('确认将该虚拟机设为模板？', {
				btn: ['确认', '取消'],
				closeBtn: 0,
				icon: 3,
				btnAlign: 'c',
				title: '操作确认'
			},function() {
				HyAjax.Post("/kvmTemplate/doInsert.action", {
					kvmServerId: selectVm[0].kvmServerId,
					vmId: selectVm[0].vmId
				},
				function(result) {
					if (result.success) {
						Dialog.successMsg('设置成功！ ',
						function() {
							queryVm(selectVm[0].vmId);
						})
					} else {
						Dialog.errorMsg('设置失败, ' + result.message)
					}
				})
			})
		}
	}
}

//刷新虚拟机表格
function queryVm(vmId){
	layer.closeAll();
	table.reload("vmGrid",{
		 url:"/vm/doPageSelect.action"
		 ,parseData: function(res) {
			if (res.data.length > 0) {
				if (typeof(vmId) != "undefined") {
					for (var i = 0; i < res.data.length; i++) {
						if (res.data[i].vmId == vmId) {
							res.data[i].LAY_CHECKED = true;
						}
					}
				} else {
					res.data[0].LAY_CHECKED = true;
				}
			}
		}
	});
}

//加载虚拟机VNC画面
function showVnc() {
	var selectVm = table.checkStatus('vmGrid').data;
	if (selectVm.length != 1) {
		Dialog.alert("请有且仅勾选一台虚拟机！")
	}else{
		var vm = selectVm[0];
		var params = {"vmId": vm.vmId};
		HyAjax.Post("/vm/openVm.action", params,function(result) {
			if (result.success) {
				var url = "http://" + result.url;
			} else {
				var url = "http://" + hostname + ":5080/vnc.html";
			}
			var num = parent.layer.open({
				type: 2,
				content: [url, 'no'],
				title: vm.vmName,
				maxmin: true,
				shade: 0,
				resize: true,
				area: ['700px', '500px']
			});
			parent.layer.full(num);
		})
	}
}

table.on('rowDouble(vmGrid)', function(obj){
	var vm = obj.data;
	var params = {"vmId": vm.vmId};
	HyAjax.Post("/vm/openVm.action", params,function(result) {
		if (result.success) {
			var url = "http://" + result.url;
		} else {
			var url = "http://" + hostname + ":5080/vnc.html";
		}
		window.open(url);
	})
});


//表格行单击选中事件
$(document).on("click", ".layui-table-body table.layui-table tbody tr",function() {
	var index = $(this).attr('data-index');
	var tableBox = $(this).parents('.layui-table-box');
	if (tableBox.find(".layui-table-fixed.layui-table-fixed-l").length > 0) {
		tableDiv = tableBox.find(".layui-table-fixed.layui-table-fixed-l")
	} else {
		tableDiv = tableBox.find(".layui-table-body.layui-table-main")
	}
	var trs = tableDiv.find(".layui-unselect.layui-form-checkbox.layui-form-checked").parent().parent().parent();
	for (var i = 0; i < trs.length; i++) {
		var ind = $(trs[i]).attr("data-index");
		var checkCell = tableDiv.find("tr[data-index=" + ind + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox I");
		if (checkCell.length > 0) {
			checkCell.click()
		}
	}
	var checkCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox I");
	if (checkCell.length > 0) {
		checkCell.click()
	}
});
//表格行单击防冒泡事件
$(document).on("click", "td div.laytable-cell-checkbox",function(e) {
	e.stopPropagation()
});

$(".layui-layer .layui-layer-iframe .layui-layer-border").click(function (){
	alert(1);
})

