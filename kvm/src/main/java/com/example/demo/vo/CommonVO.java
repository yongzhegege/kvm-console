package com.example.demo.vo;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.DateTimeUtils;
import com.example.demo.util.StringUtils;


/**
 * 通用VO对象，通过指定属性名称来获得属性值
 * 
 * @author cy
 */
public class CommonVO extends TypeConvertoForValueList implements
		java.io.Serializable, DataVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7862171697231118373L;

	/**
	 * 
	 */
	protected ValueList lstMetaData = null;

	/**
	 * 
	 */
	protected VOList subData;

	/**
	 * 
	 */
	protected Object parent;

	/**
	 * 
	 */
	private boolean transientVO = false;

	/**
	 * 
	 * 
	 */
	void setTransientVO() {
		this.transientVO = true;
	}

	/**
	 * 
	 * 
	 */
	public void tryFree() {
		if (this.transientVO) {
			lstMetaData = null;
			subData = null;
			parent = null;
			this.hsOwner.free();
		}
	}

	/**
	 * 添加元数据来描述键值
	 * 
	 * @param name
	 *            键名称
	 * @param type
	 *            类型 分为string、number、date三种
	 */
	public void addMetaData(String name, String type) {
		this.addMetaData(name, null, type);
	}

	/**
	 * 添加元数据来描述键值
	 * 
	 * @param name
	 *            键名称
	 * @param label
	 *            对应中文名称
	 * @param type
	 *            类型 分为string、number、date三种
	 */
	public void addMetaData(String name, String label, String type) {
		if (lstMetaData == null) {
			lstMetaData = new ValueList();
		}
		MetaData meta = (MetaData) this.lstMetaData.get(name.toLowerCase());
		if (meta == null) {
			meta = new MetaData();
		}
		if (null != label && !"".equals(label)) {
			meta.setLabel(label);
		}
		if (type != null) {
			meta.setType(type);
		}
		lstMetaData.add(name.toLowerCase(), meta);
		if ("".equals(this.getString(name))) {
			this.setString(name, "");
		}
	}

	/**
	 * 得到该键值对应的中文名称
	 * 
	 * @param name
	 *            键
	 * @return 如果中文名称未设置，则返回键值名称
	 */
	public String getLabel(String name) {
		if (this.lstMetaData != null) {
			MetaData meta = (MetaData) this.lstMetaData.get(name.toLowerCase());
			if (meta != null) {
				String label = meta.getLabel();
				if (label == null || "".equals(label)) {
					return name;
				} else {
					return label;
				}
			}
		}
		return name;
	}

	/**
	 * 得到指定索引的键类型
	 * 
	 * @param index
	 *            从0开始
	 * @return 如果未设置，默认为string类型
	 */
	public String getType(int index) {
		if (this.lstMetaData != null) {
			if (index >= 0 && index < lstMetaData.size()) {
				MetaData meta = (MetaData) lstMetaData.get(index);
				if (meta != null) {
					return meta.getType();
				}
			}
		}
		return "string";
	}

	/**
	 * 得到指定索引的键类型
	 * 
	 * @param key
	 *            键
	 * @return 如果未设置，默认为string类型
	 */
	public String getType(String key) {
		if (this.lstMetaData != null) {
			MetaData type = (MetaData) this.lstMetaData.get(key);
			if (type != null) {
				return type.getType();
			}
		}
		return "string";

	}

	public String getData(String key) {
		return this.getString(key);
	}

	public void setData(String key, Object value) {
		this.setObject(key, value);
	}

	/**
	 * 根据名称设置某一个属性值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            属性值
	 */
	public void setObject(String key, Object value) {
		super.setObject(key, value);
	}

	/**
	 * 判断是否包含某个字段
	 * 
	 * @param key
	 *            字段名称
	 * @return
	 */
	public boolean containKey(String key) {
		return this.hsOwner.containsKey(key);
	}

	/**
	 * 根据属性名称获得属性值
	 * 
	 * @param key
	 *            属性名称
	 * @return 属性字符串类型值
	 */
	public Object getObject(String key) {
		return super.getObject(key);
	}

	public void setSubData(VOList lst) {
		this.subData = lst;
		lst.setParent(this);
	}

	public VOList getSubData() {
		return this.subData;
	}

	public Object getParent() {
		return this.parent;
	}

	public void setParent(Object obj) {
		this.parent = obj;
	}

	 /**
     * 将本身转换成JSONObject对象
     * @return
     * @throws JSONException
     */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		String[] keys = this.listKeys();
		int len = keys.length;
		for (int i = 0; i < len; i++) {
			Object value = this.getObject(keys[i]);

			if (value instanceof Collection) {
				jsonObject.put(keys[i], (Collection<?>)value);
			} else if (value instanceof Object[]) {
				jsonObject.put(keys[i], (Object[])value);
			} else if (value instanceof java.util.Date) {
                 String tmp = this.getString(keys[i]);
                jsonObject.put(keys[i], tmp);
            } else if (value == null) {
            	jsonObject.put(keys[i], "");
            } else {
				jsonObject.put(keys[i], value);
			}
		}
		return jsonObject;
	}

	/**
     * 将本身转换成形如下的json字符串:<br/>
		<pre>
		{"userid":"111","username":"admin"}
		</pre>
     * @return
     * @throws JSONException
     */
	public String toJSONString() throws JSONException {
		return this.toJSONObject().toString();
	}
	
	/**
	 * 将Vo转换成实体Bean
	 * @param clazz
	 * @return
	 */
	public Object toBeanResolver(Class<?> clazz) {
		Object target = null;
		try {
			Field[] fields = clazz.getDeclaredFields();
			target = ConstructorUtils.invokeConstructor(clazz);
			for (int i = 0; i < fields.length; i++) {
				String value = getString(fields[i].getName());
	        	if (!StringUtils.isEmpty(value)) {
	        		fields[i].setAccessible(true);
	        		if (((Class<?>)fields[i].getGenericType()) == Long.class) {
	        			ReflectionUtils.setField(fields[i], target, Long.parseLong(value));
	        		} else if (((Class<?>)fields[i].getGenericType()) == Integer.class) {
	        			ReflectionUtils.setField(fields[i], target, Integer.parseInt(value));
	        		} else if (((Class<?>)fields[i].getGenericType()) == Date.class) {
	        			ReflectionUtils.setField(fields[i], target, DateTimeUtils.getStringToDate(value));
	        		} else {
	        			ReflectionUtils.setField(fields[i], target, value);
	        		}
	        	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

	public String toPostData() {
		String[] fields = this.listKeys();
		int len = fields.length;
		StringBuffer tag2 = new StringBuffer();
		for (int i = 0; i < len; i++) {
			String field = fields[i];
			String value = this.getData(field);
			if (tag2.length() > 0) {
				tag2.append("&");
			}
			tag2.append(field);
			tag2.append("=");
			tag2.append(value);
		}
		return tag2.toString();
	}

	public void parse(String postData) {
		if (postData != null) {
			postData = postData.substring(1,postData.length()-1);
			String[] pv = postData.split(",");
			int len = pv.length;
			for (int i = 0; i < len; i++) {
				String temp = pv[i];
				int equalSignIndex = temp.lastIndexOf("=");
				if (equalSignIndex > 0) {
					String key = temp.substring(0, equalSignIndex).trim().toLowerCase();
					String value = temp.substring(equalSignIndex + 1);
					this.setString(key, value);
				}
			}
		}
	}

	public String getData(int index) {
		return this.getString(index);
	}
}

class MetaData {
	private String label;

	private String type;

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

}