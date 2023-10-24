var $ = layui.jquery;
var table = layui.table;
var dropdown = layui.dropdown;

$(".layui-card-body").css("height",$(window).height()-100+"px");
var comData =[
    {"vmId":378,"vmName":"PC303-01","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:2e:20:bc","vmState":1,"kvmServerId":39,"vncPort":5980,"vmIso":"ubuntu-20.04.5-live-server-amd64.iso","vmDriver":"0","vmMemory":2,"vmDisk":50,"vmCpu":2,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9f9f98c5-95ef-4b91-8fed-b91e5ab87bc0","vmIp":""},
    {"vmId":379,"vmName":"PC303-02","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:68:90:d6","vmState":0,"kvmServerId":39,"vncPort":5978,"vmIso":"CentOS-7-x86_64-Minimal-2009.iso","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"683974d8-e3f0-44f7-9343-d5c7fb7960df","vmIp":""},
    {"vmId":380,"vmName":"PC303-03","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:16:44:1b","vmState":0,"kvmServerId":39,"vncPort":5976,"vmIso":"0","vmDriver":"/home/drivers/virtio-win-0.1.221.iso","vmMemory":8,"vmDisk":250,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9bb5fede-8b0c-499e-934c-119b42f703fe","vmIp":""},
    {"vmId":381,"vmName":"PC303-04","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:ba:e0:6d","vmState":1,"kvmServerId":39,"vncPort":5955,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"57afa604-cb76-450d-8699-5c3af76513bd","vmIp":""},
    {"vmId":382,"vmName":"PC303-05","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:6c:14:67","vmState":1,"kvmServerId":39,"vncPort":5966,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":500,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9431f5d8-6c66-43ed-86ab-f0192c0b72a6","vmIp":""},
    {"vmId":383,"vmName":"PC303-06","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:cc:c4:a1","vmState":0,"kvmServerId":39,"vncPort":5000,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":79,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"419e68d2-3636-4cf3-9097-63653c66d494","vmIp":""},
    {"vmId":384,"vmName":"PC303-07","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:a7:78:66","vmState":0,"kvmServerId":39,"vncPort":5966,"vmIso":"ntu-20.04.4-live-server-amd64.iso","vmDriver":"0","vmMemory":4,"vmDisk":200,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"b2e1928f-f1ad-4573-b9ec-189f8f8f1198","vmIp":""},
    {"vmId":385,"vmName":"PC303-08","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:13:59:4e","vmState":0,"kvmServerId":39,"vncPort":5970,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":2,"vmNetwork":"br_eno1(Bridge)","vmUuid":"67277aa1-d571-4863-8c2d-c1d251fb7eec","vmIp":""},
    {"vmId":386,"vmName":"PC303-09","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:7f:bc:c7","vmState":0,"kvmServerId":39,"vncPort":5971,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"2bd41bb1-dd0c-4d3a-97af-53f7f63a42c7","vmIp":""}
]

var deptData = [
    {"deptId":1,"deptName":"303教室","deptParentId":0}
    ,{"deptId":2,"deptName":"304教室","deptParentId":0}
    ,{"deptId":3,"deptName":"305教室","deptParentId":0}
]

layui.extend({
    dtree : '{/}../../source/layui_exts/dtree' // {/}的意思即代表采用自有路径，即不跟随 base 路径
}).use([ 'jquery', 'layer', 'dtree', 'element' ], function() {
    var dTree = layui.dtree;
    dTree.render({
        elem : '#deptTree',
        width : '200',
        line : true,
        method : 'GET',
        data:deptData,
        load : true,
        initLevel : 7,
        skin : "laySimple",
        dataFormat : "list",
        done: function(res){
            dTree.dataInit("deptTree",res[0].deptId);
        },
        response : {
            parentId : "deptParentId",
            treeId : "deptId",
            title : "deptName"
        }
    });

    dTree.on("node('deptTree')", function(obj){
        var checkData = [];
        if(obj.param.nodeId == 1){
            checkData = comData;
        }else if(obj.param.nodeId == 2){
            checkData = [
                {"vmId":378,"vmName":"PC304-01","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:2e:20:bc","vmState":1,"kvmServerId":39,"vncPort":5980,"vmIso":"ubuntu-20.04.5-live-server-amd64.iso","vmDriver":"0","vmMemory":2,"vmDisk":50,"vmCpu":2,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9f9f98c5-95ef-4b91-8fed-b91e5ab87bc0","vmIp":""},
                {"vmId":379,"vmName":"PC304-02","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:68:90:d6","vmState":1,"kvmServerId":39,"vncPort":5978,"vmIso":"CentOS-7-x86_64-Minimal-2009.iso","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"683974d8-e3f0-44f7-9343-d5c7fb7960df","vmIp":""},
                {"vmId":380,"vmName":"PC304-03","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:16:44:1b","vmState":1,"kvmServerId":39,"vncPort":5976,"vmIso":"0","vmDriver":"/home/drivers/virtio-win-0.1.221.iso","vmMemory":8,"vmDisk":250,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9bb5fede-8b0c-499e-934c-119b42f703fe","vmIp":""},
                {"vmId":381,"vmName":"PC304-04","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:ba:e0:6d","vmState":1,"kvmServerId":39,"vncPort":5955,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"57afa604-cb76-450d-8699-5c3af76513bd","vmIp":""},
                {"vmId":382,"vmName":"PC304-05","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:6c:14:67","vmState":1,"kvmServerId":39,"vncPort":5966,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":500,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"9431f5d8-6c66-43ed-86ab-f0192c0b72a6","vmIp":""},
                {"vmId":383,"vmName":"PC304-06","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:cc:c4:a1","vmState":1,"kvmServerId":39,"vncPort":5000,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":79,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"419e68d2-3636-4cf3-9097-63653c66d494","vmIp":""},
                {"vmId":384,"vmName":"PC304-07","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:a7:78:66","vmState":1,"kvmServerId":39,"vncPort":5966,"vmIso":"ntu-20.04.4-live-server-amd64.iso","vmDriver":"0","vmMemory":4,"vmDisk":200,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"b2e1928f-f1ad-4573-b9ec-189f8f8f1198","vmIp":""},
                {"vmId":385,"vmName":"PC304-08","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:13:59:4e","vmState":1,"kvmServerId":39,"vncPort":5970,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":2,"vmNetwork":"br_eno1(Bridge)","vmUuid":"67277aa1-d571-4863-8c2d-c1d251fb7eec","vmIp":""},
                {"vmId":386,"vmName":"PC304-09","vmDiskMode":0,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"network","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:7f:bc:c7","vmState":1,"kvmServerId":39,"vncPort":5971,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"br_eno1(Bridge)","vmUuid":"2bd41bb1-dd0c-4d3a-97af-53f7f63a42c7","vmIp":""}
            ]
        }else if(obj.param.nodeId == 3){
            checkData = [
                {"vmId":378,"vmName":"PC305-01","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:2e:20:bc","vmState":0,"kvmServerId":39,"vncPort":5980,"vmIso":"ubuntu-20.04.5-live-server-amd64.iso","vmDriver":"0","vmMemory":2,"vmDisk":50,"vmCpu":2,"vmNetwork":"virbr0(NAT)","vmUuid":"9f9f98c5-95ef-4b91-8fed-b91e5ab87bc0","vmIp":""},
                {"vmId":379,"vmName":"PC305-02","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:68:90:d6","vmState":0,"kvmServerId":39,"vncPort":5978,"vmIso":"CentOS-7-x86_64-Minimal-2009.iso","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"683974d8-e3f0-44f7-9343-d5c7fb7960df","vmIp":""},
                {"vmId":380,"vmName":"PC305-03","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:16:44:1b","vmState":0,"kvmServerId":39,"vncPort":5976,"vmIso":"0","vmDriver":"/home/drivers/virtio-win-0.1.221.iso","vmMemory":8,"vmDisk":250,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"9bb5fede-8b0c-499e-934c-119b42f703fe","vmIp":""},
                {"vmId":381,"vmName":"PC305-04","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:ba:e0:6d","vmState":0,"kvmServerId":39,"vncPort":5955,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"57afa604-cb76-450d-8699-5c3af76513bd","vmIp":""},
                {"vmId":382,"vmName":"PC305-05","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:6c:14:67","vmState":0,"kvmServerId":39,"vncPort":5966,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":500,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"9431f5d8-6c66-43ed-86ab-f0192c0b72a6","vmIp":""},
                {"vmId":383,"vmName":"PC305-06","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:cc:c4:a1","vmState":0,"kvmServerId":39,"vncPort":5000,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":79,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"419e68d2-3636-4cf3-9097-63653c66d494","vmIp":""},
                {"vmId":384,"vmName":"PC305-07","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:a7:78:66","vmState":0,"kvmServerId":39,"vncPort":5966,"vmIso":"ntu-20.04.4-live-server-amd64.iso","vmDriver":"0","vmMemory":4,"vmDisk":200,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"b2e1928f-f1ad-4573-b9ec-189f8f8f1198","vmIp":""},
                {"vmId":385,"vmName":"PC305-08","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"UEFI","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:13:59:4e","vmState":0,"kvmServerId":39,"vncPort":5970,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":100,"vmCpu":2,"vmNetwork":"virbr0(NAT)","vmUuid":"67277aa1-d571-4863-8c2d-c1d251fb7eec","vmIp":""},
                {"vmId":386,"vmName":"PC305-09","vmDiskMode":1,"vmShowName":null,"vmDesc":null,"vmDefaultStart":"hd","vmUp":0,"vmDown":0,"vmRecycle":0,"vmType":"Legacy","accountType":null,"computerAccount":null,"computerPassword":null,"computerDomainName":null,"computerPort":null,"vmMac":"32:54:03:7f:bc:c7","vmState":0,"kvmServerId":39,"vncPort":5971,"vmIso":"0","vmDriver":"0","vmMemory":4,"vmDisk":50,"vmCpu":4,"vmNetwork":"virbr0(NAT)","vmUuid":"2bd41bb1-dd0c-4d3a-97af-53f7f63a42c7","vmIp":""}
            ]
        }
        table.reload("vmGrid",{
            data:checkData
        })
    });
})


//初始化虚拟机表格
table.render({
    elem: '#vmGrid'
    ,height:$(window).height()-110
    ,cellMinWidth: 80
    ,data:comData
    ,id:"vmGrid"
    ,cols: [[{
        type: 'checkbox'
    },{
        field: 'vmName',
        title: 'Name',
        align: 'center',
        width: '13%',
        templet: function(d) {
            return "<span title='双击进入虚拟机'>"+d.vmName+"</span>"
        }
    },{
        field: 'vmState',
        title: 'State',
        align: 'center',
        width: '8%',
        templet: function(d) {
            if (d.vmState == 0) {
                return '<span style="color:red">offline</span>'
            } else {
                return '<span style="color:lightgreen">online</span>'
            }
        }
    },{
        field: 'vmMac',
        title: 'CHM',
        align: 'center',
        width: '12%',
        templet: function(d) {
            return d.vmCpu + "C/" + d.vmDisk + "G/" + d.vmMemory + "G"
        }
    },
        {
            field: 'vmIp',
            title: 'IP',
            align: 'center',
            width: '15%'
        },{field:'vmNetwork',title: 'Network',align:'center',width:'17%'},{
            field: 'vmDefaultStart',
            title: 'BootFirst',
            align: 'center',
            templet: function(d) {
                if (d.vmDefaultStart == "cdrom") {
                    return "CDROM"
                } else if (d.vmDefaultStart == "hd") {
                    return "HD"
                } else if (d.vmDefaultStart == "network") {
                    return "NETWORK"
                }
            }
        },{
            field: 'vmType',
            title: 'BootMode',
            align: 'center'
        }]]
    ,parseData: function(res) {
        if (res.data.length > 0) {
            res.data[0].LAY_CHECKED = true;
        }
    }
});

//表格行单击选中事件
$(document).on("click", ".layui-table-body table.layui-table tbody tr",function() {
    var index = $(this).attr('data-index');
    var tableBox = $(this).parents('.layui-table-box');
    if (tableBox.find(".layui-table-fixed.layui-table-fixed-l").length > 0) {
        tableDiv = tableBox.find(".layui-table-fixed.layui-table-fixed-l")
    } else {
        tableDiv = tableBox.find(".layui-table-body.layui-table-main")
    }
    var trs = tableDiv.find(".layui-unselect.layui-form-checkbox.layui-form-checked").parent().parent().parent();
    for (var i = 0; i < trs.length; i++) {
        var ind = $(trs[i]).attr("data-index");
        var checkCell = tableDiv.find("tr[data-index=" + ind + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox I");
        if (checkCell.length > 0) {
            checkCell.click()
        }
    }
    var checkCell = tableDiv.find("tr[data-index=" + index + "]").find("td div.laytable-cell-checkbox div.layui-form-checkbox I");
    if (checkCell.length > 0) {
        checkCell.click()
    }
});
//表格行单击防冒泡事件
$(document).on("click", "td div.laytable-cell-checkbox",function(e) {
    e.stopPropagation()
});
