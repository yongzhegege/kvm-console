var form = layui.form;
var $ = layui.jquery;

function init(templateId){
	$("#templateId").val(templateId);
}

form.on('submit(formfilter)', function(data){
	$.get('/kvmTemplate/mkdirVm.action', data.field, function (res) {
		if(res.success){
			var message = "";
			if(data.field.createType == 1){
				message = "创建成功！";
			}else{
				message = "创建成功，请于10分钟后开机！";
			}
			Dialog.successMsg(message,function(){
				parent.layer.closeAll();
			});
		}else{
			Dialog.errorMsg(res.message);
		}
	})
});