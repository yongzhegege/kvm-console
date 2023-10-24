var form = layui.form;
var $ = layui.jquery;

//编辑初始化表单
function init(userVmId){
	$("#agreement").attr("disabled","disabled");
	$("#vmNameDiv").html('<input type="text" lay-verify="required" name="vmName" id="vmName" autocomplete="off" placeholder="连接名" class="layui-input" >')
	HyAjax.Get("/userVm/load.action?userVmId="+userVmId,function(res){
		form.val("loadfilter",res.data);
		if(res.data.agreement == 1){
			$("#rdpParam").css("display","block");
			$("#sshAndTelnetParam").css("display","none");
			$("#vncParam").css("display","none");
			$("#font-size").removeAttr("lay-verify");
			$("#username").attr("lay-verify","required");
			$("#password").attr("lay-verify","required");
		}else if(res.data.agreement == 2 || res.data.agreement == 4){
			if(res.data.agreement == 2){
				$("#username").attr("lay-verify","required");
				$("#password").attr("lay-verify","required");
			}else{
				$("#username").removeAttr("lay-verify");
				$("#password").removeAttr("lay-verify");
			}
			$("#rdpParam").css("display","none");
			$("#sshAndTelnetParam").css("display","block");
			$("#vncParam").css("display","none");
			$("#font-size").attr("lay-verify","num");
		}else if(res.data.agreement == 3){
			$("#rdpParam").css("display","none");
			$("#sshAndTelnetParam").css("display","none");
			$("#vncParam").css("display","block");
			$("#font-size").removeAttr("lay-verify");
			$("#password").removeAttr("lay-verify");
			$("#username").removeAttr("lay-verify");
		}
		if(res.data.hasOwnProperty("params") && res.data.params != "0"){
			var param = JSON.parse(res.data.params);
				for(var key in param){
					if(param[key] == "true"){
						$("#"+key).prop("checked",true);
					}else{
						$("#"+key).val(param[key]);
					}
			　}
			form.render();
		}
	})
}


//监听协议下拉切换
form.on('select(agreement)',function (data){
	if(data.value == 1){
		$("#rdpParam").css("display","block");
		$("#sshAndTelnetParam").css("display","none");
		$("#vncParam").css("display","none");
		$("#font-size").removeAttr("lay-verify");
		$("#username").attr("lay-verify","required");
		$("#password").attr("lay-verify","required");
		$("#port").val("3389");
	}else if(data.value == 2 || data.value == 4){
		if(data.value == 2){
			$("#port").val("22");
			$("#username").attr("lay-verify","required");
			$("#password").attr("lay-verify","required");
		}else{
			$("#port").val("23");
			$("#username").removeAttr("lay-verify");
			$("#password").removeAttr("lay-verify");
		}
		$("#rdpParam").css("display","none");
		$("#sshAndTelnetParam").css("display","block");
		$("#vncParam").css("display","none");
		$("#font-size").attr("lay-verify","num");
	}else if(data.value == 3){
		$("#port").val("5900");
		$("#rdpParam").css("display","none");
		$("#sshAndTelnetParam").css("display","none");
		$("#vncParam").css("display","block");
		$("#font-size").removeAttr("lay-verify");
		$("#password").removeAttr("lay-verify");
		$("#username").removeAttr("lay-verify");
	}
})

//表单提交
form.on('submit(formfilter)', function(data){
	var param = {};
	if($("#userVmId").val() != ""){
		param.userVmId = $("#userVmId").val();
		delete data.field.userVmId;
	}
	param.vmName = data.field.vmName;
	param.agreement = data.field.agreement;
	param.hostName = data.field.hostName;
	param.username = data.field.username;
	param.password = data.field.password;
	param.port = data.field.port;
	param.desc = data.field.desc;
	param.userId = parent.table.checkStatus("userGrid").data[0].userId;
	delete data.field.vmName;
	delete data.field.agreement;
	delete data.field.hostName;
	delete data.field.username;
	delete data.field.password;
	delete data.field.port;
	delete data.field.desc;
	for (var i in data.field) {
		if(data.field[i] == ""){
			delete data.field[i];
		}
	}
	if(param.agreement == 1){
		delete data.field["color-scheme"];
		delete data.field["font-name"];
		delete data.field["color-depth"]
	}else if(param.agreement == 2 || param.agreement == 4){
		delete data.field["color-depth"]
	}else if(param.agreement == 3){
		delete data.field["color-scheme"];
		delete data.field["font-name"];
	}
	if(JSON.stringify(data.field) != "{}"){
		param.params =JSON.stringify(data.field).replace(/\"on\"/g, "\"true\"");
	}else{
		param.params = "0";
	}
	if($("#userVmId").val() == ""){ //添加
		HyAjax.Post("/userVm/doInsert.action",param,function(res){
			if(res.success){
				Dialog.successMsg("添加成功！",function (){
					parent.table.reload("userVmGrid");
					parent.layer.closeAll();
				});
			}else{
				Dialog.alert(res.message);
			}
		})
	}else{
		HyAjax.Post("/userVm/doUpdate.action",param,function(res){
			if(res.success){
				Dialog.successMsg("修改成功！",function (){
					parent.table.reload("userVmGrid");
				});
			}else{
				Dialog.errorMsg(res.message);
			}
		})
	}

	return false;
})