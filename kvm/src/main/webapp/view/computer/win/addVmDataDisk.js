var $ = layui.jquery;
var form = layui.form;

form.on('submit(formfilter)', function(data) {
	HyAjax.Post("/vm/addDataDisk.action",data.field,function(result){
		if(result.success){
			Dialog.successMsg("添加成功！",function(){
				parent.showVmDisk();
			});
		}else{
			Dialog.errorMsg(result.message);
		}
	});
	return false;
})

function init(kvmServerId,vmId,diskList){
	var hasType = [];
	for(var i=0; i<diskList.length; i++){
		hasType[i] = diskList[i].diskType;
	}
	var body = "";
	if(hasType.indexOf("vdb") == -1){
		body += "<option value='vdb'>vdb</option>";
	}
	if(hasType.indexOf("vdc") == -1){
		body += "<option value='vdc'>vdc</option>";
	}
	if(hasType.indexOf("vdd") == -1){
		body += "<option value='vdd'>vdd</option>";
	}
	$("#diskType").html(body);
	$("#kvmServerId").val(kvmServerId);
	$("#vmId").val(vmId);
	form.render();
}
