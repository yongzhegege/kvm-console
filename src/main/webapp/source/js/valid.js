layui.use(['jquery','form'],function(){
	var $ = layui.jquery;
	var form = layui.form;
	form.verify({
		//手机号码效验
		mobile : function(value, item) {
			if (!new RegExp("^((0\d{2,3})(-)?)(\d{7,8})(-(\d{3,}))?$").test(value)
					|| !new RegExp("/^1[3458]\d{9}$").test(value)
					|| !new RegExp("/^((-)?\d){0,10}$").test(value)) {
				return '请输入正确的手机电话，集团号长度为10位，固定电话须加区号！';
			}
		},

		//IP地址校验
		ip : function(value, item) {
			if(!(/^(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)\.(25[0-5]|2[0-4]\d|[0-1]\d{2}|[1-9]?\d)$/.test(value)) || value == "127.0.0.1"){
				return '请输入正确的IP地址！';
			}				
		},

		// MAC地址输入校验
		mac : function(val, field) {
				if (!(/^([0-9A-F]{2})((:[0-9A-F]{2}){5})$/gi
						.test(val))){
					return '请输入正确的MAC地址！';
				}
		},

		// 子网掩码输入校验
		subnetmask : function(val, field) {
				if (!(/^(254|252|248|240|224|192|128|0)\.0\.0\.0$|^(255\.(254|252|248|240|224|192|128|0)\.0\.0)$|^(255\.255\.(254|252|248|240|224|192|128|0)\.0)$|^(255\.255\.255\.(254|252|248|240|224|192|128|0))$/
						.test(val))){
					return '请输入正确的子网掩码！';
				}
		},

		// 网关输入校验
		gateway : function(val, field) {
				if (!(/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
						.test(val))){
					return '请输入正确的网关！';
				}
		},

	    // DNS1输入校验
	    dns : function(value, item) {
	    	if (value !="") {
	    		if (!new RegExp("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").test(value))
		            return '请输入正确的DNS地址！';
	    	}
	    },
	    
	    //正整数
	    positiveNum : function(value, item) {
	    	if (!(/^[1-9]\d*$/.test(value)))
				return '请输入一个正整数！';
	    },

		//不为纯数字
		noNum : function(value, item) {
	    	if ((/^[1-9]\d*$/.test(value)))
				return '群组名不能为纯数字！';
	    },

		//纯数字
		num  : function(value, item) {
	    	if (!(/^[0-9]\d*$/.test(value)))
				return '请输入一个数字！';
	    },
	    
	    //文件路径
	    fileAddr:function(value,item) {
	    	if (!/^[a-zA-Z]:(\\\\([A-Za-z0-9\u4e00-\u9fa5]+))+/.test(value)){
	    		return '请输入正确的文件路径！';
	    	}
	    },
	    
	    //长度
	    maxLength : function(value, item) {
	    	if (value !="") {
	    		if (value.length>50)
					return '请输入50以内的长度！';
	    	}
	    	
	    },
	    
	    //输入框check
	    inputCheck:function(value,item) {
	    	if (value !="") {
	    		if (!/^[_A-Za-z0-9\u4e00-\u9fa5]+$/.test(value)){
		    		return '只能输入英文字母、数字、下划线或中文';
		    	}
	    	}
	    	
	    },
	    
	    //输入框长度check
	    lengthCheck25:function(value,item) {
	    	if (value.length>=25)
				return '请输入25以内的长度！';
	    },
	    
	    //输入框长度check
	    lengthCheck150:function(value,item) {
	    	if (value !="") {
	    		if (value.length>=150)
					return '请输入150以内的长度！';
	    	}
	    },
	    
	    //汉字check
	    checkChinese:function(value,item) {
	    	if (/^[\u4e00-\u9fa5]+$/.test(value)){
	    		return '只能输入英文字母、数字、下划线';
	    	}
	    },
	    //域名称check
	    domainChinese:function(value,item) {
	    	if (!/^[A-Za-z0-9-.]+$/.test(value)){
	    		return '只能输入英文字母、数字、连字符';
	    	} 	
	    },

		//日期时间
		dayTime:function(value,item){
			if(!/^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\s+(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/.test(value)){
				return '请输入正确的日期时间';
			}
		},
		
		time:function(value,item){
			if(!/^(20|21|22|23|[0-1]\d):[0-5]\d:[0-5]\d$/.test(value)){
				return '请输入正确的时间';
			}
		}
		
	});
});

