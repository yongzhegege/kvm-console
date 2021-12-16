var $ = layui.jquery;
var table = layui.table;
var loading = null;

//初始化存储表格
table.render({
	elem : '#storageGrid',
	height : 200,
	url : "/storage/doPageSelect.action",
	cols : [[{
		type : 'radio'},
			{
				field : 'storageType',
				title : '存储类型',
				sort : true,
				align : 'center',
				templet: function(d){
					if(d.storageType == 1){
						return "NFS";
					}else if(d.storageType == 2){
						return "CIFS/SMB";
					}else if(d.storageType == 3){
						return "iSCSI";
					}
				}
			},{
				field : 'storagePath',
				title : '存储路径',
				align : 'center'
			},{
				field : 'localPath',
				title : '挂载路径',
				align : 'center',
			},{
				field : 'storageStatu',
				title : '存储状态',
				align : 'center',
				templet: function(d){
					if(d.storageStatu == 0 || d.storageStatu==null){
						return "<span style='color:red'>未挂载</span>";
					}else if(d.storageStatu == 1){
						return "<span style='color:green'>已挂载</span>";
					}
				}
			} ]]
		,parseData : function(res) { // res 即为原始返回的数据
        if(res.success){
            if (res.data.length>0) {
				_radio_check_obj = res.data[0];
				res.data[0].LAY_CHECKED = true;
            }
        }
    },
	id : 'storageGrid',
	page : false,
	limit:1000
});

//初始化文件系统表格
table.render({
	elem : '#fileSysGrid',
	height : $(window).height()-380,
	url : "/storage/getFileSys.action",
	cols : [[
		{type : 'radio'},
			{
				field : 'name',
				title : '文件系统',
				sort : true,
				align : 'center',
			},{
				field : 'cap',
				title : '容量',
				align : 'center'
			},{
				field : 'used',
				title : '已用',
				align : 'center',
			},{
				field : 'canUse',
				title : '可用',
				align : 'center',
			},{
				field : 'usePersent',
				title : '已用(%)',
				align : 'center',
			},{
				field : 'mountPoint',
				title : '挂载点',
				align : 'center',
			}]]
		,parseData : function(res) { // res 即为原始返回的数据
        if(res.success){
            if (res.data.length>0) {
				_radio_check_obj = res.data[0];
				res.data[0].LAY_CHECKED = true;
            }
        }
    },
	id : 'fileSysGrid',
	page : false,
	limit:1000
});

//添加存储
$("#addStorage").click(function (){
		addStorageIndx =layui.layer.open({
		type : 2,
		closeBtn : 2,
		resize : false,
		title : '添加存储',
		area : [ '420px', '350px' ],
		content : [ 'win/addStorage.html', 'no' ],
		success: function(layero, index){}
	});
})



//编辑存储
$("#editStorage").click(function (){
	var checkStorage = table.checkStatus('storageGrid').data;
	if(checkStorage.length > 0){
		if(checkStorage[0].storageStatu == 1){
			Dialog.alert("该存储处于挂载状态，禁止编辑！");
		}else{
			var height = "";
			if(checkStorage[0].storageType == 1){
				height = "250px";
			}else if(checkStorage[0].storageType == 2){
				height = "350px";
			}else if(checkStorage[0].storageType == 3){
				height = "250px";
			}
			layui.layer.open({
				type : 2,
				closeBtn : 2,
				resize : false,
				title : '编辑存储',
				area : [ '420px', height ],
				content : [ 'win/addStorage.html', 'no' ],
				success: function(layero, index){
					var iframeWin = window[layero.find('iframe')[0]['name']];
					iframeWin.editInit(checkStorage[0]);
				}
			});
		}
	}else{
		Dialog.alert("请选择一条记录！");
	}
})

//删除存储
$("#delStorage").click(function (){
	var checkStorage = table.checkStatus('storageGrid').data;
	if(checkStorage.length > 0){
		if(checkStorage[0].storageStatu == 1){
			Dialog.alert("该存储处于挂载状态，请先卸载！")
		}else{
			layer.confirm('确认删除该存储？', {
				btn: ['确定', '取消'],
				closeBtn: 0,
				icon: 3,
				btnAlign: 'c',
				title: '操作确认'
			},function() {
				HyAjax.Delete("/storage/doDelete.action?pk="+checkStorage[0].storageId,function (result){
					if(result.success){
						Dialog.successMsg("删除成功！",function (){
							table.reload("storageGrid");
						});
					}else{
						Dialog.errorMsg(result.message);
					}
				});
			});
		}
	}else{
		Dialog.alert("请选择一条记录！");
	}
})

//挂载存储
$("#mountStorage").click(function (){
	if(loading != null){
		Dialog.alert("请等待其他进程结束！");
		return;
	}
		var checkStorage = table.checkStatus('storageGrid').data;
		if(checkStorage.length > 0){
		if(checkStorage[0].storageStatu == 1){
			Dialog.alert("该存储处于挂载状态，请先卸载！")
		}else{
			if(!checkStorage[0].hasOwnProperty("localPath") || checkStorage[0].localPath == ""){
				Dialog.alert("请输入挂载路径！");
			}else{
				if(checkStorage[0].storageType )
				layer.confirm('确认挂载该存储？', {
					btn: ['确定', '取消'],
					closeBtn: 0,
					icon: 3,
					btnAlign: 'c',
					title: '操作确认'
				},function() {
					loading = layer.load(2, {
				        shade: false,
				        time: 2*30000
				    });
					layui.jquery.ajax({
						type : "POST",
						timeout : 30000, 
						url : "/storage/mount.action",
						dataType : "json",
						data : {storageId : checkStorage[0].storageId},
						success : function(result) {
							layer.close(loading);
							if(result.success){
								Dialog.successMsg("挂载成功！",function (){
									table.reload("storageGrid");
									table.reload("fileSysGrid");
								});
							}else{
								Dialog.errorMsg(result.message);
							}
						},complete: function (xhr,status) {
			                if(status == 'timeout') {
			                	xhr.abort();
			                	Dialog.errorMsg("网络超时，请检查存储路径或账号密码！");
			                }
			                layer.close(loading);
							loading = null;
			            }
					});
				});
			}
		}
	}else{
		Dialog.alert("请选择一条记录！");
	}
})

//卸载存储
$("#umountStorage").click(function (){
	var checkStorage = table.checkStatus('storageGrid').data;
	if(checkStorage.length > 0){
		if(checkStorage[0].storageStatu == 0){
			Dialog.alert("该存储未挂载，请先挂载！");
		}else{
			layer.confirm('确认卸载该存储？', {
					btn: ['确定', '取消'],
					closeBtn: 0,
					icon: 3,
					btnAlign: 'c',
					title: '操作确认'
				},function() {
					HyAjax.Post("/storage/umount.action",{storageId : checkStorage[0].storageId},function (result){
						if(result.success){
							Dialog.successMsg("卸载成功！",function (){
								table.reload("storageGrid");
								table.reload("fileSysGrid");
							});
						}else{
							Dialog.errorMsg(result.message);
						}
					});
			});
		}
	}else{
		Dialog.alert("请选择一条记录！");
	}
})

//性能测试
$("#diskTest").click(function (){
	if(loading != null){
		Dialog.alert("请等待其他进程结束！");
		return;
	}
	var checkFileSys = table.checkStatus('fileSysGrid').data;
	if(checkFileSys.length > 0){
		loading = layer.load(2, {
	        shade: false,
	        time: 120000
	    });
		layui.jquery.ajax({
			type : "POST",
			timeout : 120000, 
			url : "/storage/diskTest.action",
			dataType : "json",
			data : {localPath : checkFileSys[0].mountPoint},
			success : function(result) {
				layer.close(loading);
				if(result.success){
					if (result.success) {
						layer.open({
							type: 1,
							title: false,
							closeBtn: 1,
							area: '600px;',
							id: 'LAY_layuipro',
							btnAlign: 'c',
							moveType: 1,
							content: '<textarea style="width:600px;height:600px" class="layui-textarea">' + result.test + '</textarea>'
					})
				}else{
					Dialog.errorMsg(result.message);
				}
			}
		},complete: function (xhr,status) {
	            if(status == 'timeout') {
	            	xhr.abort();
	            	Dialog.errorMsg("网络超时，请重试！");
	            }
	            layer.close(loading);
				loading = null;
	        }
		});
	}else{
		Dialog.alert("请至少选择一条记录！");
	}
})


//单击行选中
$(document).on("click",".layui-table-body table.layui-table tbody tr", function () {
	$(this).find('i[class="layui-anim layui-icon"]').trigger("click");
 });