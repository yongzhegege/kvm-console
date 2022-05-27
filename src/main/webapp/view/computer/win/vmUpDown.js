var $ = layui.jquery;
var form = layui.form;
var selectVm;
//初始化
function init(vm){
	selectVm = vm;
	$("#vmId").val(vm.vmId);
	$("#vmUp").val(vm.vmUp);
	$("#vmDown").val(vm.vmDown);
	$("#vmState").val(vm.vmState);
}

//表单提交
form.on('submit(formfilter)', function(data){
	HyAjax.Post("/vm/upDown.action",data.field,function (result){
		if(result.success){
			Dialog.successMsg("保存成功！",function (){
				parent.queryVm(selectVm.vmId)
				parent.layer.closeAll();
			})
		}else{
			Dialog.errorMsg(result.message);
		}
	})
})