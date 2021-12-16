var $ = layui.jquery;
var form = layui.form;
var $ = layui.jquery;
var form = layui.form;
//初始化表单
function init(vm){
	if(vm != ""){
		$("#vmName").attr("readonly","readonly");
	}
	//获取服务器列表
	HyAjax.Get("/kvmServer/doPageSelect.action",function (res){
		var data = res.data;
		if(data.length > 0){
			var _html = "";
			for(var i = 0; i < data.length; i++){
				_html += "<option value='"+data[i].kvmServerId+"'>"+data[i].kvmServerName+"</option>"
			}
			$("#kvmServer").html(_html);
			//获取镜像列表和驱动列表 kvm服务器 idv的BOOT服务器 
		    HyAjax.Get("/kvmServer/getIsoAndDirverAndNetwork.action?kvmServerId=" + data[0].kvmServerId , function(result) {
		        if (result.success) {
		        	var isos = result.isos;
		        	var drivers = result.drivers;
		        	var networks = result.networks;
					var serverType = result.serverType;
		        	var isoHtml = "";
		        	var driverHtml = "";
		        	if(vm !="" && vm.vmIso == "0"){
		        		isoHtml = "<option value='0' selected= 'selected' >不安装镜像</option>";
		        	}else{
		        		isoHtml = "<option value='0'>不安装镜像</option>";
		        	}
		        	if(vm !="" && vm.vmDriver == "0"){
		        		driverHtml = "<option value='0' selected= 'selected'>不安装驱动</option>";
		        	}else{
		        		driverHtml = "<option value='0'>不安装驱动</option>";
		        	}
					if(serverType == "ubuntu"){
						$("#vmType").html('<option value="legacy_deb">Legacy</option><option value="uefi_deb">UEFI</option>');
					}else if(serverType == "centos"){
						$("#vmType").html('<option value="legacy_rpm">Legacy</option><option value="uefi_rpm">UEFI</option>');
					}
		        	var networkHtml = "";
		        	if(isos != ""){
		        		for(var i=0; i<isos.length; i++){
		        			if(vm !="" && vm.vmIso == isos[i]){
		        				isoHtml = isoHtml + "<option value='"+isos[i]+"' selected= 'selected' >"+isos[i]+"</option>"
		        			}else{
		        				isoHtml = isoHtml + "<option value='"+isos[i]+"'>"+isos[i]+"</option>"
		        			}
		            	}
		        	}
		        	if(drivers != ""){
		        		for(var i=0; i<drivers.length; i++){
		        			if(vm !="" && vm.vmDriver == drivers[i]){
		        				driverHtml = driverHtml + "<option selected= 'selected'  value='"+drivers[i]+"'>"+drivers[i]+"</option>" 
		        			}else{
		        				driverHtml = driverHtml + "<option value='"+drivers[i]+"'>"+drivers[i]+"</option>" 
		        			}
		            	}
		        	}
		        	if(networks != ""){
		        		for(var i=0; i<networks.length; i++){
		        			if(vm !="" && vm.vmNetwork == networks[i]){
		        				networkHtml = networkHtml + "<option selected= 'selected' value='"+networks[i]+"' >"+networks[i]+"</option>" 
		        			}else{
		        				networkHtml = networkHtml + "<option value='"+networks[i]+"' >"+networks[i]+"</option>" 
		        			}
		            	}
		        	}
		        	if(isoHtml != ""){
		        		$("#vmIso").html(isoHtml);
		        	}
		        	if(driverHtml !=""){
		        		$("#vmDriver").html(driverHtml);
		        	}
		        	if(networkHtml !=""){
		        		$("#vmNetwork").html(networkHtml);
		        	}
					$("#cpuMax").html("Max:"+result.cpu);
		        	$("#memoryMax").html("Max:"+result.memory);
					form.render();
		        } else {
		            Dialog.errorMsg('镜像文件获取失败, 原因：'+result.message);
		        }
		    });
			form.render();
		}
	});
}

//初始化编辑表单
function initEdit(vm){
	$("#vmTypeDiv").css("display","none");
	init(vm);
	form.val("vmfilter", {
		  "vmName": vm.vmName
		  ,"vmId": vm.vmId
		  ,"vmMemory": vm.vmMemory
		  ,"vmDisk": vm.vmDisk
		  ,"vmCpu":vm.vmCpu
		  ,"vmDefaultStart":vm.vmDefaultStart
		});
	$("#vmDisk").attr("readonly","readonly");
	$("#registDiv").css("display","none");
}

//创建虚拟机表单提交
form.on('submit(formfilter)', function(data){
	if($("#vmId").val()==""){
		var loading = layer.load(2, {
	        shade: false,
	        time: 2*30000 //设置默认20s关闭
	    });
		HyAjax.Post('/vm/doInsert.action', data.field, function (res) {
			if(res.success){
				layer.close(loading);
				Dialog.successMsg("创建成功！",function(){
					parent.queryVm();
				});
			}else{
				Dialog.errorMsg("创建失败，"+res.message,function(){
					layer.close(loading);
					parent.queryVm();
				});
			}
		})
	}else{
		if(parent.table.checkStatus("vmGrid").data[0].vmState == 0){
				HyAjax.Post('/vm/editVm.action', data.field, function (res) {
				if(res.success){
					Dialog.successMsg("修改成功！",function(){
						parent.queryVm(data.field.vmId);
					});
				}else{
					Dialog.errorMsg("修改失败，"+res.message,function(){
						parent.queryVm(data.field.vmId);
					});
				}
			})
		}else{
			Dialog.alert("虚拟机处于开机状态，不允许编辑！")
		}
	}
});

//虚拟机开关及重启
function opVm(num) {
	if (table.checkStatus('vmGrid').data.length >= 1) {
		var selectVm = computerGrid.checkStatus('vmGrid').data;
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
					loadVmGrid(selectVm[0].kvmServerId,selectVm[0].vmId);
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

//显示上传文件表单
$("#uploadFIle").click(function (){
	parent.showUploadFIlePage();
})