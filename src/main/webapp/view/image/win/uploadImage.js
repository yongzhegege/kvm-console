var $ = layui.jquery;
var upload = layui.upload;
var element = layui.element;

function init(type){
	var url = "";
	if(type == 1){
		url = '/kvmServer/uploadImage.action';
	}else{
		url = '/kvmServer/uploadDriver.action';
	}
	upload.render({
        elem: '#choseFile'
            ,url: url
            ,accept: 'file' //普通文件
            ,acceptMime:'.iso'
            ,exts: 'iso' 
            ,auto: false
            ,bindAction: '#uploadFile'
			,before:function(obj){
				$("#progressDiv").css("display","block");
			},done:function(res, index, upload){
				if(res.success){
					Dialog.successMsg("上传成功！");
				}
			}
			,progress: function(n, elem, res, index){
		    var percent = n + '%';
		    element.progress('uploadProgress', percent); 
		  }
	});
}




