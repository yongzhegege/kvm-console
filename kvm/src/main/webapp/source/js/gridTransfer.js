/**
 * {
 * id: 容器元素ID
 * height: 表格高度
 * }
 * @param config
 */
function transferGrid(config) {
	var leftGrid = null, rightGrid = null;
	var leftGridId = config.id + '_leftGrid';
	var rightGridId = config.id + '_rightGrid';
	var leftBtnId = config.id + '_leftBtn';
	var rightBtnId = config.id + '_rightBtn';
	var _height = config.height ? config.height : 260;
	var padTop = (_height + 100 - 86) / 2;
	var _html = '<div class="layui-fluid" style="padding-top:0px;">\
                    <div class="layui-row layui-col-space12" style="margin-top: 5px; background: #f2f2f2">\
                        <div class="layui-col-md5">\
                            <div class="layui-card">\
                                <div class="layui-card-header"><i style="margin-right:5px;vertical-align:middle;color:gray;font-size:20px;" class="layui-icon layui-icon-circle"></i>已分配</div>\
                                <div class="layui-card-body">\
                                    <table class="layui-hide" id="'
			+ leftGridId
			+ '" lay-filter="'
			+ leftGridId
			+ '"></table>\
                                </div>\
                            </div>\
                        </div>\
                        <div class="layui-col-md2"  style="text-align: center;padding-top: '
			+ padTop
			+ 'px">\
                            <div>\
                                <button id="'
			+ rightBtnId
			+ '" class="layui-btn layui-btn-normal layui-btn-disabled btn"> \
                                    <i class="layui-icon">&#xe603;</i>\
                                </button>\
                            </div>\
                        </div>\
                        <div class="layui-col-md5">\
                            <div class="layui-card">\
                                <div class="layui-card-header"><i style="margin-right:5px;vertical-align:middle;color:gray;font-size:20px;" class="layui-icon layui-icon-radio"></i>未分配</div>\
                                <div class="layui-card-body">\
                                    <table class="layui-hide" id="'
			+ rightGridId
			+ '" lay-filter="'
			+ rightGridId
			+ '"></table>\
                                </div>\
                            </div>\
                        </div>\
                    </div>\
                </div>';
	function create(config1, config2, callback1, callback2) {
		layui.use([ 'table', 'jquery' ], function() {
			var table = layui.table;
			var $ = layui.jquery;
			config1.elem = '#' + leftGridId;
			config1.height = _height;
			leftGrid = table.render(config1);
			config2.elem = '#' + rightGridId;
			config2.height = _height;
			rightGrid = table.render(config2);

			table.on('checkbox(' + leftGridId + ')', function(obj) {
				// console.log(obj.checked); //当前是否选中状态
				// console.log(obj.data); //选中行的相关数据
				// console.log(obj.type);
				// //如果触发的是全选，则为：all，如果触发的是单选，则为：one
				var checkStatus = table.checkStatus(leftGridId);
				var size = checkStatus.data.length;
				if (size > 0) {
					$('#' + leftBtnId).removeClass('layui-btn-disabled');
				} else {
					$('#' + leftBtnId).addClass('layui-btn-disabled');
				}
			});
			// 左边-->右边
			$('#' + leftBtnId).click(function() {
				if (!$('#' + leftBtnId).hasClass('layui-btn-disabled')) {
					var checkStatus = table.checkStatus(leftGridId);
					var size = checkStatus.data.length;
					if (size == 0) {
						layer.msg("请至少选择一个节点");
					} else {
						if (callback1 != undefined && (typeof (callback1) == 'function')) {
							$('#' + leftBtnId).addClass('layui-btn-disabled');
							callback1(checkStatus.data, leftGridId, rightGridId);
						}
					}
				}
			});
			table.on('checkbox(' + rightGridId + ')', function(obj) {
				var checkStatus = table.checkStatus(rightGridId);
				var size = checkStatus.data.length;
				if (size > 0) {
					$('#' + rightBtnId).removeClass('layui-btn-disabled');
				} else {
					$('#' + rightBtnId).addClass('layui-btn-disabled');
				}
			});
			// 右边-->左边
			$('#' + rightBtnId).click(function() {
				if (!$('#' + rightBtnId).hasClass('layui-btn-disabled')) {
					var checkStatus = table.checkStatus(rightGridId);
					var size = checkStatus.data.length;
					if (size == 0) {
						layer.msg("请至少选择一个节点");
					} else {
						if (callback2 != undefined && (typeof (callback2) == 'function')) {
							$('#' + rightBtnId).addClass('layui-btn-disabled');
							callback2(checkStatus.data, leftGridId, rightGridId);
						}
					}
				}
			});
		});
	};

	/**
	 * config1：左边表格参数(见layui,不要设置width、height)
	 * config2：右边表格参数见layui,不要设置width、height)
	 * func1：> 按钮点击函数
	 * func2：< 按钮点击函数
	 * @param config1
	 * @param config2
	 * @param func1
	 * @param func2
	 */
	this.init = function(config1, config2, func1, func2) {
		document.getElementById(config.id).innerHTML = _html;
		var callback1 = undefined, callback2 = undefined;
		if (func1 != undefined && (typeof (func1) == 'function')) {
			callback1 = func1;
		}
		if (func2 != undefined && (typeof (func2) == 'function')) {
			callback2 = func2;
		}
		create(config1, config2, callback1, callback2);
	};

	this.getLeftGrid = function() {
		return leftGrid;
	};

	this.getRightGrid = function() {
		return rightGrid;
	};
}