var $ =layui.jquery;
var form = layui.form;

$("#kvmServerName").focus();
form.on('submit(formSub)', function(data) {
	var  loading = layer.load(2, {
	        shade: 0.1,
	        time: 2*30000
	    });
    HyAjax.Post("/kvmServer/doInsert.action", data.field, function(result) {
		layer.close(loading);
        if (result.success) {
            Dialog.successMsg('添加成功！ ', function() {
				parent.layer.closeAll();
            	parent.window.location.reload();
            });
        } else {
            Dialog.alert(result.message, {icon : 2});
        }
    });
return false;
});