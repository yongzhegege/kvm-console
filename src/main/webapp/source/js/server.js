var $ = layui.jquery;


$("#addKvmServer").click(function (){
    layui.layer.open({
        type: 2,
        closeBtn:2,
        resize:false,
        title: '添加计算节点',
        area: ['450px', '440px'],
        content: ['win/addKvmServerWin.html', 'no'],
        success: function(layero, index){
             var iframeWin = window[layero.find('iframe')[0]['name']];
			 //得到iframe页的窗口对象，执行iframe页的方法：iframeWin.method();
//             iframeWin.getMunIdAndFuncId($(".menu_second_active",parent.document).attr("id"),funcId);
        }
    });
})