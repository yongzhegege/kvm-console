var $ = layui.jquery;
var table = layui.table;

//初始化日志表格
table.render({
  elem: '#logGrid'
  ,height:$(window).height()-110
  ,cellMinWidth: 80
  ,url:"/log/doPageSelect.action"
   ,id:"logGrid"
  ,cols: [[{
		field: 'userName',
		title: '用户',
		align: 'center',
		width: '20%'
	},{
		field: 'content',
		title: '操作',
		align: 'center',
		width: '20%',
	},{
		field: 'hostIp',
		title: '请求地址',
		align: 'center',
		width: '20%',
	},{
		field: 'result',
		title: '状态',
		align: 'center',
		width: '20%',
		templet: function(d) {
			if(d.result == 1){
				return "<span style='color:green'>成功</span>"
			}else{
				return "<span style='color:red'>失败</span>"
			}
		}
	},
	{
		field: 'createTime',
		title: '时间',
		align: 'center'
	}]]
});

//搜索
function searchInfo(){
	var info = $("#searchInfo").val();
	table.reload("logGrid",{
		url:"/log/doPageSelect.action?searchInfo="+info
	})
}