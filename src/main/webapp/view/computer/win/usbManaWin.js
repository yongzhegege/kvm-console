var $ = layui.jquery;
var form = layui.form;
var table = layui.table;

function init(data){
	table.reload("usbGrid",{
		data : data
	})
}

	table.render({
	    elem: '#usbGrid'
	    ,height: "full-50"
	    ,autoSort:false
	    ,data: []
	    ,cols : [ [{
			field : 'bus',
			title : 'Bus',
			width:"15%",
			sort : false,
			align : 'center'
		},{
			field : 'device',
			title : 'Device',
			width:"15%",
			sort : false,
			align : 'center'
		},{
			field : 'uId',
			title : 'ID',
			width:"15%",
			sort : false,
			align : 'center'
		},{
			field : 'uName',
			title : 'Name',
			width:"40%",
			sort : false,
			align : 'center'
		},{
			title : '操作',
			align : 'center',
			templet: function(d){
				var _html = "";
				_html = '<div><a style="background-color:#5793cf"class="layui-btn layui-btn-danger layui-btn-xs" onclick="opUsb(1,\''+d.bus+'\',\''+d.device+'\',\''+d.uId+'\')" lay-event="recover">挂载</a>'
			    +'<a style="background-color:#5793cf" class="layui-btn layui-btn-danger layui-btn-xs"onclick="opUsb(0,\''+d.bus+'\',\''+d.device+'\',\''+d.uId+'\')" lay-event="del">卸载</a></div>';
		        return _html;
		    }
		} ] ]
	    ,id: 'usbGrid'
	    ,page: false
	    ,limit:1000
	});
	
	function opUsb(opType,bus,device,id){
		var checkVm = parent.table.checkStatus('vmGrid').data;
		var msg = "";
		if(opType == 1 ){
			msg = "挂载成功！";
		}else{
			msg = "卸载成功！";
		}
		var param = {	
			opType : opType,
			kvmServerId :  checkVm[0].kvmServerId,
			vmId : checkVm[0].vmId,
			bus : bus,
			device : device,
			id : id	
		}
		HyAjax.Post("/vm/opUsb.action",param,function(result) {
			if (result.success) {
				Dialog.successMsg(msg);
			} else {
				Dialog.errorMsg(result.message);
			}
		});	
	}
