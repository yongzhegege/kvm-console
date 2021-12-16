var dtree
var $ = layui.jquery;
$("#templateBody").css("height",$(window).height()-100+"px");
$("#imageBody").css("height",$(window).height()-315+"px");
layui.extend({
    dtree: '{/}../../source/layui_exts/dtree'   // {/}的意思即代表采用自有路径，即不跟随 base 路径
}).use('dtree', function(){
	dtree = layui.dtree;
	//初始化镜像树
	var	 kvmImageTree = dtree.render({
		width : 500,
        elem : '#kvmImageTree',// 选择器
        line : true,// 数据
        method : "GET",
        url : '/kvmServer/loadKvmImageTree.action',
        load : true,
        intLevel : 10,
        check: 'checkbox', //勾选风格
        skin : "laySimple",
        dataFormat : "list",
        id : "kvmImage",
        checkbar: false,  
        checkbarType: "only",
        response : {
            treeId: "imageId",
            parentId: "imageParentId",
            title: "imageName",
            checkArr: "checkArr",
            checked: "checked",
            type: "type", 
            basicData: "basicData" 
        },
         done: function(res){
        	 if(res.data.length > 0){
        		 $("#delKvmImage").removeClass("layui-btn-disabled");
        		 //默认选中第一条树节点
        		 dtree.dataInit("kvmImageTree",res.data[0].imageId);
        		 //默认选中第一条的复选框
        		 dtree.chooseDataInit("kvmImageTree",res.data[0].imageId);
        		 showKvmImageInfo(res.data[0].basicData);
        	 }
        }
    });
	
	//显示镜像信息
	function showKvmImageInfo(data){
		$("#kvmImageName").html(data.imageName);
		$("#kvmImageSize").html(data.imageSize);
		$("#kvmImageAddr").html(data.imageAddr);
	}
	
	//树节点点击显示镜像信息
	dtree.on("node('kvmImageTree')",function (obj){
		showKvmImageInfo(obj.param.basicData);
	});
	
	//删除kvm镜像
$("#delKvmImage").click(function(){
	var param = dtree.getNowParam("kvmImageTree").basicData;
    layer.confirm('确定将该镜像删除吗？', {
        btn : [ '确定', '取消' ], // 按钮
        closeBtn : 0,
        icon : 3,
        btnAlign : 'c',
        title : '操作确认'
    }, function(){
    	var param = dtree.getNowParam("kvmImageTree").basicData;
        HyAjax.Delete("/kvmServer/deleteKvmImage.action?imagePath="+param.imageAddr, function(result) {
            if (result.success) {
                Dialog.successMsg('删除成功！ ', function(){
                	dtree.reload("kvmImageTree");
                });
            } else {
                Dialog.errorMsg('删除失败, '+result.message+'！', function(){
                	dtree.reload("kvmImageTree");
                });
            }
        });
   });
})

//上传镜像
$("#uploadImage").click(function (){
	layer.open({
		type:2,
		resize:false,
		shade: 0,
		content: ['win/uploadImage.html', 'no'],
		title:'上传镜像',
		maxmin:true,
		area : [ '500px', '350px' ]
		 ,success: function(layero, index){
           var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init(1);
        }
	});
})

//上传驱动
$("#uploadDriver").click(function (){
	layer.open({
		type:2,
		resize:false,
		shade: 0,
		content: ['win/uploadImage.html', 'no'],
		title:'上传驱动',
		maxmin:true,
		area : [ '500px', '350px' ]
		 ,success: function(layero, index){
           var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init(2);
        }
	});
})

});
var $ =layui.jquery;
loadKvmTemplate();
//获取所有模板
function loadKvmTemplate(){
	HyAjax.Get('/kvmTemplate/doPageSelect.action',function(result){
		var temps = result.data;
		var body = "";
		for(var i=0; i<temps.length; i++){
			body +=   ' <li class="layui-col-xs6">'+
                        '<span  class="layadmin-backlog-body">'+
                         '<h3>'+temps[i].templateNetwork+
                              '<div style="float:right;">'+
	                          '<span class="layui-badge layui-bg-blue layuiadmin-badge">'+temps[i].templateMemory+'G</span>'+
	                          '<span class="layui-badge layui-bg-blue layuiadmin-badge">'+temps[i].templateCpu+'核</span>'+
	                          '<span class="layui-badge layui-bg-blue layuiadmin-badge">'+temps[i].templateDisk+'G</span>'+
                          '</div>'+
                          '</h3>'+
							'<button type="button" onclick="delKvmTemplate('+temps[i].templateId+')"   class="layui-btn layui-btn-sm layui-btn-danger imageButton" style="float:right;margin-top:5px;margin-left:10px">删除</button>'+
                          	'<button type="button" onclick="mkdirVm('+temps[i].templateId+')"   class="layui-btn layui-btn-sm layui-btn-checked imageButton" style="float:right;margin-top:5px;margin-left:0px">创建</button>'+
                          '<p><cite>'+temps[i].templateName+'</cite></p>'+
                        '</span>'+
                      '</li>';
		}
		$("#kvmTemplateBody").empty();
		$("#kvmTemplateBody").html(body);
	});
}

//弹出创建虚拟机窗口
function mkdirVm(templateId){
	    layui.layer.open({
        type: 2,
        closeBtn:2,
        resize:false,
        title: '创建虚拟机',
        area: ['500px', '330px'],
        content: ['win/mkdirVmWin.html', 'no'],
        success: function(layero, index){
           var iframeWin = window[layero.find('iframe')[0]['name']];
			iframeWin.init(templateId);
        }
  });
}

//删除模板
function delKvmTemplate(templateId){
	    layer.confirm('是否删除该模板？', {
        btn : [ '确定', '取消' ], // 按钮
        closeBtn : 0,
        icon : 3,
        btnAlign : 'c',
        title : '操作确认'
    }, function(){
    	var url = '/kvmTemplate/doDelete.action?pk='+templateId;
		HyAjax.Delete(url, function(result) {
			if (result.success) {
	        	Dialog.successMsg('删除模板成功！ ', function() {
					loadKvmTemplate();
	        		layer.closeAll();
	            });
	        } else {
	        	Dialog.alert(result.message, {icon : 2});
	        }
		});
   });
}