var $ = layui.jquery;
var form = layui.form;

//编辑初始化
function editInit(data){
	$("#storageTypeDiv").css("display","none");
	$("#storageId").val(data.storageId);
	$("#storageType").val(data.storageType);
	if(data.hasOwnProperty("localPath")){
		$("#localPath").val(data.localPath);
	}
	if(data.storageType == 1){
		$("#userDiv").css("display","none");
		$("#storageUsername").removeAttr("lay-verify");
		$("#storagePassword").removeAttr("lay-verify");
	}else if(data.storageType == 2){
		$("#userDiv").css("display","block");
		$("#storageUsername").attr("lay-verify","required");
		$("#storagePassword").attr("lay-verify","required");
		if(data.hasOwnProperty("storageUsername")){
			$("#storageUsername").val(data.storageUsername);
		}
		if(data.hasOwnProperty("storagePassword")){
			$("#storagePassword").val(data.storagePassword);
		}
	}else if(data.storageType == 3){
		data.storagePath = data.storagePath.split(",")[0];
		$("#storageLabel").html("<span style='color: red;'>*</span>存储IP：");
		$("#userDiv").css("display","none");
		$("#storageUsername").removeAttr("lay-verify");
		$("#storagePassword").removeAttr("lay-verify");
	}
	$("#storagePath").val(data.storagePath);
	form.render();
}

//监听存储类型切换
form.on('select(storageType)',function (data){
	if(data.value == 2){  //smb
		$("#storageLabel").html("<span style='color: red;'>*</span>存储路径：");
		$("#userDiv").css("display","block");
		$("#storageUsername").attr("lay-verify","required");
		$("#storagePassword").attr("lay-verify","required");
		parent.layer.style(parent.addStorageIndx, {
			width: '420px',
			height: '450px'
		})
	}else if(data.value  == 1){ //nfs
		$("#storageLabel").html("<span style='color: red;'>*</span>存储路径：");
		$("#userDiv").css("display","none");
		$("#storageUsername").removeAttr("lay-verify");
		$("#storagePassword").removeAttr("lay-verify");
		parent.layer.style(parent.addStorageIndx, {
			width: '420px',
			height: '350px'
		})
	}else if(data.value == 3){ //iscsi
		$("#storageLabel").html("<span style='color: red;'>*</span>存储IP：");
		$("#userDiv").css("display","none");
		$("#storageUsername").removeAttr("lay-verify");
		$("#storagePassword").removeAttr("lay-verify");
		parent.layer.style(parent.addStorageIndx, {
			width: '420px',
			height: '350px'
		})
	}
});

//获取iqn
$("#getIqn").click(function (){
	var ip = $("#storagePath").val();
	if(ip == ""){
		Dialog.alert("请输入存储IP！");
	}else{
		HyAjax.Post("/Storage/getIqn.action",{ip:ip},function (result){
		if(result.success){
			$("#storageIqn").val(result.iqn);
		}else{
			Dialog.successMsg(result.message);
		}
	})
	}
})

//表单提交
form.on('submit(formfilter)',function (data){
	if(data.field.localPath != ""){
		if (!(/^\/(\w+\/?)+$/.test(data.field.localPath))){
			$("#localPath").val("");
			Dialog.alert("请输入正确的挂载路径");
			return false;
		};	 
	}
	var url = "";
	var msg = "";
	if($("#storageId").val() != ""){
		url = "/storage/doUpdate.action";
		msg = "修改成功！";
	}else{
		url = "/storage/doInsert.action";
		msg = "添加成功！";
	}
	HyAjax.Post(url,data.field,function (result){
		if(result.success){
			Dialog.successMsg(msg,function (){
				parent.table.reload("storageGrid");
				parent.layer.closeAll();
			});
		}else{
			Dialog.errorMsg(result.message);
		}
	})
})

