var $ = layui.jquery;
var form = layui.form;
var table = layui.table;
var diskList = null;
var _loadSnap;

table.on('row(diskGrid)', function (obj) {
	if(_loadSnap){
		loadDiskSnapGrid($("#kvmServerId").val(),obj.data.diskPath)
	}
	//选中行样式
	obj.tr.addClass('layui-table-click').siblings().removeClass('layui-table-click');
	//选中radio样式
	obj.tr.find('i[class="layui-anim layui-icon"]').trigger("click");
});
	
	//初始化快照表格
	table.render({
	   elem: '#diskSnapGrid'
	    ,height: "250"
	    ,autoSort:false
	    ,data: []
	    ,cols : [ [{
			field : 'id',
			title : 'ID',
			width:"10%",
			sort : false,
			align : 'center'
		},{
			field : 'tag',
			title : '快照名称',
			width:"25%",
			sort : false,
			align : 'center'
		},{
			field : 'vmsize',
			title : '大小',
			width:"10%",
			sort : false,
			align : 'center'
		},{
			field : 'date',
			title : '创建时间',
			width:"30%",
			sort : false,
			align : 'center'
		},{
			title : '操作',
			align : 'center',
			templet: function(d){ 
				var _html = '<div><a class="layui-btn layui-btn-danger layui-btn-xs" onclick="opSnap(3,\''+d.tag+'\')" lay-event="recover">删除</a>'+
										'<a class="layui-btn layui-btn-danger layui-btn-xs" onclick="opSnap(2,\''+d.tag+'\')" lay-event="recover">还原</a></div>';
		        return _html;
		    }
		} ] ]
	    ,id: 'diskSnapGrid'
	    ,page: false
	    ,limit:1000
});

//初始化磁盘表格
function init(vmId,kvmServerId,loadSnap){
	_loadSnap = loadSnap;
	if(loadSnap == false){
		$("#snapDiv").css("display","none");
	}
	$("#kvmServerId").val(kvmServerId);
	$("#vmId").val(vmId);
	var url = "/vm/diskList.action?vmId="+vmId+"&kvmServerId="+kvmServerId;
	table.render({
		url : url
	   ,elem: '#diskGrid'
	    ,height: "250"
	    ,autoSort:false
	    ,data: []
	    ,cols : [ [{type:'radio'},{
			field : 'diskType',
			title : '磁盘类型',
			width:"15%",
			sort : false,
			align : 'center'
		},{
			field : 'diskSize',
			title : '磁盘大小(G)',
			width:"15%",
			sort : false,
			align : 'center'
		},{
			field : 'diskPath',
			title : '磁盘路径',
			width:"50%",
			sort : false,
			align : 'center'
		},{
			title : '操作',
			align : 'center',
			templet: function(d){
				var _html = '<div><a class="layui-btn layui-btn-danger layui-btn-xs" onclick="opDisk(1,\''+d.diskType+'\',\''+d.diskPath+'\')" lay-event="recover">扩容</a>';
				if(d.diskType == "vda"){
					_html += '<a id="addDataDiskBtn" class="layui-btn layui-btn-danger  layui-btn-xs"onclick="opDisk(2,\''+d.diskType+'\',\''+d.diskPath+'\')" lay-event="del">添加数据盘</a></div>';
				}else{
					_html += '<a class="layui-btn layui-btn-danger layui-btn-xs"onclick="opDisk(0,\''+d.diskType+'\',\''+d.diskPath+'\')" lay-event="del">卸载</a></div>';
				}
		        return _html;
		    }
		} ] ]
	    ,id: 'diskGrid'
	    ,page: false
	    ,limit:1000
		,parseData : function(res) { // res 即为原始返回的数据
	        if(res.success){
	            if (res.data.length>0) {
					res.data[0].LAY_CHECKED = true;
	            }
	        }
	    },done: function(res, curr, count){
			diskList = res.data;
			if(res.data.length > 3 ){
				$("#addDataDiskBtn").css("display","none");
			}
			if(res.data.length > 0 && loadSnap){
				loadDiskSnapGrid($("#kvmServerId").val(),res.data[0].diskPath)
			}
		 }
	});
}
	//重载快照表格
	function loadDiskSnapGrid(kvmServerId,diskPath){
		table.reload("diskSnapGrid",{
			url : "/vm/diskSnapList.action?kvmServerId="+kvmServerId+"&diskPath="+diskPath
		})
	}
	
	//磁盘操作  卸载0  扩容1 添加数据盘2 
	function opDisk(opType,diskType,diskPath){
		var checkVm = parent.table.checkStatus('vmGrid').data;
		if(opType == 0){
			layer.confirm('是否确定将该磁盘卸载？', {
		      btn : [ '确定', '取消' ], // 按钮
		      closeBtn : 0,
		      icon : 3,
		      btnAlign : 'c',
		      title : '操作确认'
		  }, function(){
				var param = {
					vmId : checkVm[0].vmId,
					kvmServerId : checkVm[0].kvmServerId,
					diskType : diskType,
					diskPath : diskPath
				};
			  	layer.confirm('是否删除磁盘物理文件？', {
			      btn : [ '确定', '取消' ], // 按钮
			      closeBtn : 0,
			      icon : 3,
			      btnAlign : 'c',
			      title : '操作确认'
			  }, function(){
				param.deleteDisk = 1;
				HyAjax.Post("/vm/detachDisk.action",param, function (result){
					if(result.success){
						Dialog.successMsg("卸载成功！",function (){
							table.reload('diskGrid');
						})
					}else{
						Dialog.errorMsg(result.message);
					}
				})
			  },function(){
				param.deleteDisk = 0;
				HyAjax.Post("/vm/detachDisk.action",param, function (result){
					if(result.success){
						Dialog.successMsg("卸载成功！",function (){
							table.reload('diskGrid');
						})
					}else{
						Dialog.errorMsg(result.message);
					}
				})
			});
		  });
		}else if(opType == 1){
			if(checkVm[0].vmState == 0){
				parent.expansionDisk(checkVm[0].kvmServerId,diskPath);
			}else{
				Dialog.alert("请先将虚拟机关机！");
			}
		}else if(opType == 2){
			parent.addVmDataDisk(checkVm[0].kvmServerId,checkVm[0].vmId,diskList);
		}
	}
	
	//磁盘快照创建1  恢复2  删除 3
	function opSnap(option,snapName){
		if(snapName == ""){
			snapName = $("#snapName").val();
			if(snapName == ""){
				Dialog.alert("快照名不能为空！");
				return ;
			}
		}
		var diskPath = table.checkStatus('diskGrid').data[0].diskPath;
		var param = {
				kvmServerId : $("#kvmServerId").val(),
				diskPath : diskPath,
				option : option,
				snapName : snapName
		}
		if(option == 1){
			HyAjax.Post("/vm/optionDiskSnap.action",param, function (result){
				if(result.success){
					Dialog.successMsg("创建成功！",function (){
						table.reload('diskSnapGrid');
					})
				}else{
					Dialog.errorMsg(result.message);
				}
			})
		}else{
			var confirmMsg = "";
			var successMsg = "";
			if(option == 2){
				confirmMsg = "是否确认还原该快照？";
				successMsg = "还原成功！"
			}else if(option == 3){
				confirmMsg = "是否确认删除该快照？";
				successMsg = "删除成功！"
			}
			layer.confirm(confirmMsg, {
		      btn : [ '确定', '取消' ], // 按钮
		      closeBtn : 0,
		      icon : 3,
		      btnAlign : 'c',
		      title : '操作确认'
		  }, function(){
			HyAjax.Post("/vm/optionDiskSnap.action",param, function (result){
				if(result.success){
					Dialog.successMsg(successMsg,function (){
						table.reload('diskSnapGrid');
					})
				}else{
					Dialog.errorMsg(result.message);
				}
			})
		 });
		}
	}
