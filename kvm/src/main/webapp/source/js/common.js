if (typeof (layui.layer) == "undefined") {
	layui.use('layer');
}
if (typeof (layui.jquery) == "undefined") {
	layui.use('jquery');
}
/**
 * 提供全局消息提示框
 */
var Dialog = {
	/**
	 * 消息弹出框
	 * 
	 * @param msg
	 *            消息体
	 * @param callbackFunc
	 *            回调函数
	 */
	alert : function(msg, callbackFunc) {
		var layer = layui.layer;
		layer.alert(msg, {
			icon : 7,
			title : '提示',
			btnAlign : 'c'
		}, function(index) {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc(index);
			} else {
				layer.close(index);
			}
		});
	},
	/**
	 * 消息对话框
	 * 
	 * @param msg
	 *            消息体
	 * @param callbackFuncY
	 *            成功回调函数
	 * @param callbackFuncN
	 *            失败回调函数
	 */
	confirm : function(msg, callbackFuncY, callbackFuncN) {
		var layer = layui.layer;
		layer.confirm(msg, {
			icon : 3,
			title : '确认',
			btnAlign : 'c'
		}, function(index) {
			if (callbackFuncY != undefined
					&& (typeof (callbackFuncY) == 'function')) {
				callbackFuncY(index);
				// layer.close(index);
			}
		}, function(index) {
			if (callbackFuncN != undefined
					&& (typeof (callbackFuncN) == 'function')) {
				callbackFuncN(index);
			} else {
				layer.close(index);
			}
		});
	},
	/**
	 * 文本输入消息提示框
	 * 
	 * @param title
	 *            消息体
	 * @param callbackFunc
	 *            回调函数
	 */
	textPrompt : function(title, callbackFunc) {
		var layer = layui.layer;
		layer.prompt({
			formType : 0,
			title : title,
			btnAlign : 'c'
		}, function(value, index, elem) {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc(value, index, elem);
			} else {
				layer.msg("请添加回调函数！", {
					icon : 2,
					time : 1000
				});
			}
		});
	},
	/**
	 * 密码输入消息提示框
	 * 
	 * @param title
	 *            消息体
	 * @param callbackFunc
	 *            回调函数
	 */
	pwdPrompt : function(title, callbackFunc) {
		var layer = layui.layer;
		layer.prompt({
			formType : 1,
			title : title,
			btnAlign : 'c'
		}, function(value, index, elem) {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc(value, index, elem);
			} else {
				layer.msg("请添加回调函数！", {
					icon : 2,
					time : 1000
				});
			}
		});
	},
	/**
	 * 文本域输入消息提示框
	 * 
	 * @param title
	 *            消息体
	 * @param callbackFunc
	 *            回调函数
	 */
	areaPrompt : function(title, callbackFunc) {
		var layer = layui.layer;
		layer.prompt({
			formType : 1,
			title : title,
			btnAlign : 'c',
			area : [ '800px', '350px' ]
		}, function(value, index, elem) {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc(value, index, elem);
			} else {
				layer.msg("请添加回调函数！", {
					icon : 2
				});
			}
		});
	},
	/**
	 * 失败提示消息
	 * 
	 * @param msg
	 *          消息体
	 * @param callbackFunc
	 *          回调函数
	 */
	errorMsg : function(msg, callbackFunc,time) {
		var layer = layui.layer;
		var _time = 2000;
		if (time != undefined
				&& (typeof (time) == 'number')) {
			_time = time;
		}
		layer.msg(msg, {
			icon : 5,
			time : _time
		}, function() {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc();
			}
		});
	},
	/**
	 * 成功提示消息
	 * 
	 * @param msg
	 *          消息体
	 * @param callbackFunc
	 *          回调函数
	 */
	successMsg : function(msg, callbackFunc,time) {
		var layer = layui.layer;
		var _time = 500;
		if (time != undefined
				&& (typeof (time) == 'number')) {
			_time = time;
		}
		layer.msg(msg, {
			icon : 6,
			time : _time
		}, function() {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc();
			}
		});
	},
	
	successHyMsg : function(msg, callbackFunc) {
		var layer = layui.layer;
		layer.alert(msg, {
			icon : 7,
			title : '成功',
			btnAlign : 'c'
		}, function(index) {
			if (callbackFunc != undefined
					&& (typeof (callbackFunc) == 'function')) {
				callbackFunc(index);
				layer.close(index);
			} else {
				layer.close(index);
			}
		});
	}
};
/**
 * 提供全局异步请求函数GET、POST、PUT、DELETE四种模式
 */
var HyAjax = {
	Post : function(url, params, successCallback, errorCallback) {
/*		var loading = layer.load(2, {
	        shade: false,
	        time: 2*30000 //设置默认20s关闭
	    });*/
		layui.jquery.ajax({
			type : "POST",
			timeout : 60000, // 超时时间设置，单位毫秒
			url : url,
			dataType : "json",
			data : params,
			success : function(result) {
/*				layer.close(loading);*/
				if (successCallback != undefined
						&& (typeof (successCallback) == 'function')) {
					successCallback(result);
				}else{
					Dialog.successMsg(result.message);
				}
			},
			error : function(result) {
//				layer.close(loading);
				if (errorCallback != undefined
						&& (typeof (errorCallback) == 'function')) {
					errorCallback(result);
				} else {
					Dialog.errorMsg(result.message);
				}
			},
            complete: function (xhr,status) {
                if(status == 'timeout') {
                	xhr.abort();    // 超时后中断请求
                	Dialog.errorMsg("网络超时，请刷新");
                }
//                layer.close(loading);
            }
		});
	},
	Get : function(url,successCallback, errorCallback) {
/*		var loading = layer.load(2, {
	        shade: false,
	        time: 2*20000 //设置默认20s关闭
	    });*/
		layui.jquery.ajax({
			type : "GET",
			timeout : 20000, // 超时时间设置，单位毫秒
			url : url,
			dataType : "json",
			success : function(result) {
/*				layer.close(loading);*/
				if (successCallback != undefined
						&& (typeof (successCallback) == 'function')) {
					successCallback(result);
				}
			},
			error : function(result) {
/*				layer.close(loading);*/
				if (errorCallback != undefined
						&& (typeof (errorCallback) == 'function')) {
					errorCallback(result);
				} else {
					Dialog.alert(result.message);
				}
			},
            complete: function (xhr,status) {
                if(status == 'timeout') {
                	xhr.abort();    // 超时后中断请求
                	Dialog.errorMsg("网络超时，请刷新");
                }
/*                layer.close(loading);*/
            }
			
		});
	},
	Put : function(url, successCallback, errorCallback) {
		var loading = layer.load(2, {
	        shade: false,
	        time: 2*20000 //设置默认20s关闭
	    });
		layui.jquery.ajax({
			type : "PUT",
			timeout : 12000, // 超时时间设置，单位毫秒
			url : url,
			dataType : "json",
			success : function(result) {
				layer.close(loading);
				if (successCallback != undefined
						&& (typeof (successCallback) == 'function')) {
					successCallback(result);
				}
			},
			error : function(result) {
				layer.close(loading);
				if (errorCallback != undefined
						&& (typeof (errorCallback) == 'function')) {
					errorCallback(result);
				} else {
					Dialog.alert(result.message);
				}
			},
            complete: function (xhr,status) {
                if(status == 'timeout') {
                	xhr.abort();    // 超时后中断请求
                	Dialog.errorMsg("网络超时，请刷新");
                }
                layer.close(loading);
            }
		});
	},
	Delete : function(url, successCallback, errorCallback) {
		var loading = layer.load(2, {
	        shade: false,
	        time: 2*20000 //设置默认20s关闭
	    });
		layui.jquery.ajax({
			type : "DELETE",
			timeout : 40000, // 超时时间设置，单位毫秒
			url : url,
			dataType : "json",
			success : function(result) {
				layer.close(loading);
				if (successCallback != undefined
						&& (typeof (successCallback) == 'function')) {
					successCallback(result);
				}
			},
			error : function(result) {
				layer.close(loading);
				if (errorCallback != undefined
						&& (typeof (errorCallback) == 'function')) {
					errorCallback(result);
				} else {
					Dialog.alert(result.message);
				}
			},
            complete: function (xhr,status) {
                if(status == 'timeout') {
                	xhr.abort();    // 超时后中断请求
                	Dialog.errorMsg("网络超时，请刷新");
                }
                layer.close(loading);
            }
		});
	},
};

/**
 * 提供全局异步请求函数GET、POST、PUT、DELETE四种模式
 */
var HyAjaxNL = {
		Post : function(url, params, successCallback, errorCallback) {
			layui.jquery.ajax({
				type : "POST",
				//timeout : 30000, // 超时时间设置，单位毫秒
				url : url,
				dataType : "json",
				data : params,
				success : function(result) {
					if (successCallback != undefined
							&& (typeof (successCallback) == 'function')) {
						successCallback(result);
					}else{
						Dialog.successMsg(result.message);
					}
				},
				error : function(result) {
					if (errorCallback != undefined
							&& (typeof (errorCallback) == 'function')) {
						errorCallback(result);
					} else {
						Dialog.errorMsg(result.message);
					}
				},
				complete: function (xhr,status) {
					var sessionStatus = xhr.getResponseHeader('sessionstatus');
					if(status == 'timeout') {
						xhr.abort();    // 超时后中断请求
						Dialog.errorMsg("网络超时，请刷新");
					} else if(sessionStatus == 'timeout') {
						var yes = confirm('由于您长时间没有操作, session已过期, 请重新登录.');
						if (yes) {
							top.location.href = '/login.html';
						} else {
							top.location.href = '/login.html';
						}
					}
				}
			});
		},
		Get : function(url,successCallback, errorCallback) {
			layui.jquery.ajax({
				type : "GET",
				//timeout : 30000, // 超时时间设置，单位毫秒
				url : url,
				dataType : "json",
				success : function(result) {
					if (successCallback != undefined
							&& (typeof (successCallback) == 'function')) {
						successCallback(result);
					}
				},
				error : function(result) {
					if (errorCallback != undefined
							&& (typeof (errorCallback) == 'function')) {
						errorCallback(result);
					} else {
						if (result.message!= undefined) {
							Dialog.errorMsg(result.message);
						}
					}
				},
				complete: function (xhr,status) {
					var sessionStatus = xhr.getResponseHeader('sessionstatus');
					if(status == 'timeout') {
						xhr.abort();    // 超时后中断请求
						Dialog.errorMsg("网络超时，请刷新");
					} else if(sessionStatus == 'timeout') {
						var yes = confirm('由于您长时间没有操作, session已过期, 请重新登录.');
						if (yes) {
							top.location.href = '/login.html';
						} else {
							top.location.href = '/login.html';
						}
					}
				}
				
			});
		}
};

//function HyLoader(needProgress) {
//	var _iframeWin = null;
//	var _index = null;
//	var loadUrl = document.baseURI;
//	loadUrl = loadUrl.substring(0, loadUrl.indexOf("view"));
//	loadUrl = loadUrl + "source/loader/loader.html";
//	this.start = function() {
//		if(_index == null){
//			var layer = layui.layer;
//			var jq = layui.jquery;
//			layer.open({
//				type : 2,
//				closeBtn : 0,
//				resize : false,
//				shade : 0.5,
//				id : "loader",
//				anim : 5,
//				title : false,
//				area : [ '250px', '250px' ],
//				content : [ loadUrl, 'no' ],
//				success : function(layero, index) {
//					jq(".layui-layer").css("background-color", "transparent")
//							.css("box-shadow", "none");
//					_iframeWin = window[layero.find('iframe')[0]['name']];
//					if(needProgress){
//						_iframeWin.init();
//					}
//					_index = index;
//				}
//			})
//		}
//	};
//	this.change = function(num) {
//		setTimeout(()=>{
//			_iframeWin.loading(num);
//		},300);
//	};
//	this.close = function() {
//		var layer = layui.layer;
//		setTimeout(()=>{
//			layui.jquery(".layui-layer").css("background-color", "#fff").css("box-shadow", "1px 1px 50px rgba(0,0,0,.3)");
//			layer.close(_index);
//		},300);
//	}
//}