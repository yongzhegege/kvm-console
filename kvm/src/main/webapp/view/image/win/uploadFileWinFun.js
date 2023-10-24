var form = layui.form;
var $ = layui.jquery;
var TIMES = null;
var element = layui.element;


form.on('submit(formfilter)', function(data){
	if(data.field.fileUrl.indexOf(".iso") == -1){
		Dialog.alert("请上传iso格式文件！");
		return false;
	}
	$("#percent").css("display","block");
	var kvmServerId = $("#kvmServerId",parent.document).val();
	data.field.kvmServerId = kvmServerId;
	$.get('/kvmServer/uploadFile.action', data.field, function (res) {
		element.progress('progress', "0%");
		if(res.success){
			var filePath = "";
			if(data.field.fileType == 0){
				filePath = "/home/isos";
			}else{
				filePath = "/home/drivers";
			}
			Dialog.successMsg("正在上传！",function(){
				TIMES = setInterval(function(){
					$.post('/kvmServer/getUploadProgress.action',{filePath:filePath,kvmServerId:kvmServerId},function (result){
						if(result.success){
							element.progress('progress', result.progress);
							if(result.progress == "100%"){
								Dialog.successMsg("上传完成！",function (){
									clearInterval(TIMES);
									parent.dtree.reload("kvmImageTree");
									parent.layer.closeAll();
								});
							}
						}else{
							Dialog.errorMsg("上传失败，请检查上传链接！",function (){
								clearInterval(TIMES);
								$("#percent").css("display","none");
							});
						}
					})
				},2000)
			});
		}
	})
});
