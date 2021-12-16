var $ = layui.jquery;
var form = layui.form;

form.on('submit(formfilter)', function(data) {
	HyAjax.Post("/vm/expansionDisk.action",data.field,function(result){
		if(result.success){
			Dialog.successMsg("扩容成功！",function(){
				parent.showVmDisk();
			});
		}else{
			Dialog.errorMsg(result.message);
		}
	});
	return false;
})

function init(kvmServerId,diskPath){
	$("#kvmServerId").val(kvmServerId);
	$("#diskPath").val(diskPath);
}
