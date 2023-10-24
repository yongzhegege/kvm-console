var $ = layui.jquery;

layui.extend({
	dtree : '{/}../../../source/layui_exts/dtree' // {/}的意思即代表采用自有路径，即不跟随 base 路径
}).use([ 'jquery', 'layer', 'dtree', 'element' ], function() {
	var dTree = layui.dtree;
	var HEIGHT = $(window).height() - 140;
	$("#treeDiv").height(HEIGHT + "px");
	$(document).on("click", ".layui-table-body table.layui-table tbody tr", function (e) {
	    if ($(e.target).hasClass("layui-table-col-special") || $(e.target).parent().hasClass("layui-table-col-special")) {
	        return false;
	    }
	    var index = $(this).attr('data-index'), tableBox = $(this).closest('.layui-table-box'),
	        tableFixed = tableBox.find(".layui-table-fixed.layui-table-fixed-l"),
	        tableBody = tableBox.find(".layui-table-body.layui-table-main"),
	        tableDiv = tableFixed.length ? tableFixed : tableBody,
	        checkCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox i"),
	        radioCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-radio div.layui-form-radio i");
	    if (checkCell.length) {
	        checkCell.click();
	    }
	    if (radioCell.length) {
	        radioCell.click();
	    }
	});
$(document).on("click", "td div.laytable-cell-checkbox div.layui-form-checkbox,td div.laytable-cell-radio div.layui-form-radio", function (e) {
    e.stopPropagation();
});
	
 dTree.render({
		elem : '#userTree',
		width : '200',
		line : true,
		method : 'GET',
		url : '/user/doPageSelect.action?level=3',
		load : true,
		initLevel : 7,
		skin : "laySimple",
		dataFormat : "list",
		done: function(res){
			 if(res.data.length > 0){
        		 //默认选中第一条树节点
        		 dTree.dataInit("userTree",res.data[0].userId);
					grid.init({
						url : '/userVm/doPageSelect.action?userId='+res.data[0].userId,
						cols : [ [  {
							field : 'vmName',
							width : '50%',
							align : 'center',
							title : '桌面名'
						}, {
							field : 'hostName',
							align : 'center',
							title : '主机IP'
						} ] ],
						'page' : false
					}, {
						url : '/userVm/doPageSelectUnbind.action?userId='+res.data[0].userId,
						cols : [ [ {
							type : 'checkbox',
							width : '10%'
						}, {
							field : 'vmName',
							width : '40%',
							align : 'center',
							title : '桌面名'
						}, {
							field : 'hostName',
							width : '50%',
							align : 'center',
							title : '主机IP'
						} ] ],
						 page: false
					}, function () {},function(data) {
							var userVmIds = "";
							for ( var i = data.length-1; i > -1; i--) {
								userVmIds += "," + data[i].userVmId;
							}
							HyAjax.Post("/userVm/copyUserVm.action",{userVmIds:userVmIds.substring(1),userId:dTree.getNowParam("userTree").nodeId}, function(result) {
								if(result.success){
									Dialog.successMsg("分配成功！",function (){
										grid.getLeftGrid().reload();
										grid.getRightGrid().reload();
									});
								}else{
									Dialog.errorMsg(result.message);
								}
							});
					});
        	 }
		},response : {
			parentId : "userParentId",
			treeId : "userId",
			title : "userName"
		}
	});
	
	var grid = new transferGrid({
		id : 'userVmGrid',
		height : HEIGHT
	});
	
	
	dTree.on("node('userTree')", function(obj){
		grid.getLeftGrid().reload({
			url : '/userVm/doPageSelect.action?userId='+obj.param.nodeId
		});
		grid.getRightGrid().reload({
			url : '/userVm/doPageSelectUnbind.action?userId='+obj.param.nodeId
		});
	});

	
});

