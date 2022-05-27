package com.example.demo.vo;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.example.demo.util.StringUtils;

/**
 * 通用VO列表
 * 
 * @author cy
 */
public class VOList implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6420555819208031601L;

	private static final String PROPERTYKEY_FROM_CACHE = "fromCache";

	/**
	 * 保存VOList附加属性
	 */
	protected CommonVO properties = new CommonVO();

	/*
	 * 
	 */
	private boolean transientList = false;

	/*
	 * 
	 */
	protected List<Object> voList = new ArrayList<Object>();

	/*
	 * 
	 */
	protected Object parent;

	/**
	 * 
	 * 
	 */
	public void setTransientList() {
		this.transientList = true;
	}

	/**
	 * 
	 * @param obj
	 */
	void setParent(Object obj) {
		this.parent = obj;
		int size = voList.size();
		for (int i = 0; i < size; i++) {
			DataVO vo = (DataVO) voList.get(i);
			vo.setParent(obj);
		}
	}

	/**
	 * 得到行数
	 * 
	 * @return
	 */
	public int count() {
		return this.voList.size();
	}

	/**
	 * 查询一行数据
	 * 
	 * @param index
	 *            位置
	 * @param commonVO
	 *            待插入行对象
	 */
	public void insert(int index, CommonVO commonVO) {
		if (index < 0) {
			voList.add(0, commonVO);
		} else if (index >= this.count()) {
			voList.add(commonVO);
		} else {
			voList.add(index, commonVO);
		}
	}

	/**
	 * 添加一行数据
	 * 
	 * @param commonVO
	 */
	public void add(CommonVO commonVO) {
		this.voList.add(commonVO);
		commonVO.setParent(this.parent);
	}

	/**
	 * 得到某行数据
	 * 
	 * @param index
	 *            行索引
	 * @return 如果不存在，则返回null
	 */
	public CommonVO get(int index) {
		if (index < 0 || index >= this.count()) {
			return null;
		} else {
			/*
			 * DataVO vo = (DataVO) this.voList.get(index); if (vo instanceof
			 * CommonVO) { return (CommonVO) vo; } else { return new
			 * CommonVOWrapper(vo); }
			 */
			CommonVO vo = (CommonVO) this.voList.get(index);
			if (this.transientList) {
				// this.voList.set(index, null);
				vo.setTransientVO();
			}
			return vo;
		}
	}

	/**
	 * 数据清空
	 * 
	 */
	public void tryFree() {
		if (this.transientList) {
			this.voList.clear();
			this.voList = null;
		}
	}

	/**
	 * 从结果集中删除指定行数据
	 * 
	 * @param index
	 */
	public void delete(int index) {
		if (index >= 0 && index < this.count()) {
			this.voList.remove(index);
		}
	}

	/**
	 * 将给定VOList内容添加到当前VOList
	 * 
	 * @param lst
	 */
	public void add(VOList lst) {
		if (lst != null) {
			int count = lst.count();
			for (int i = 0; i < count; i++) {
				this.add(lst.get(i));
			}
		}
	}

	/**
	 * 清空所有数据
	 * 
	 */
	public void clear() {
		this.properties.clear();
		this.voList.clear();
	}

	/**
	 * 设置VOList附加属性
	 * 
	 * @param key
	 *            属性名称
	 * @param value
	 *            属性值
	 */
	public void setProperty(String key, String value) {
		this.properties.setString(key, value);
	}

	/**
	 * 得到附加属性值
	 * 
	 * @param key
	 *            属性名称
	 * @return 属性值，一般返回空字符串或有值字符串
	 */
	public String getProperty(String key) {
		return this.properties.getString(key);
	}

	/**
	 * 设置来源于缓存标识
	 * 
	 */
	public void setFromCache() {
		this.setProperty(PROPERTYKEY_FROM_CACHE, "true");
	}

	/**
	 * 判断该VOList是否来源于缓存
	 * 
	 * @return
	 */
	public boolean isFromCache() {
		return StringUtils.isTrue(this.getProperty(PROPERTYKEY_FROM_CACHE));
	}

	/**
	 * 按照给定的列值distinct数据集
	 * 
	 * @param fields
	 *            字段间以逗号隔开
	 * @return
	 */
	public VOList distinct(String fields) {
		String[] fieldNames = fields.split(",");
		VOList newList = new VOList();
		int count = this.count();
		for (int i = 0; i < count; i++) {
			CommonVO vo = this.get(i);
			if (!inList(fieldNames, vo, newList)) {
				newList.add(vo);
			}
		}
		return newList;
	}

	protected boolean inList(String[] keyFields, CommonVO vo, VOList lst) {
		int count = lst.count();
		for (int i = 0; i < count; i++) {
			if (this.isEquals(keyFields, vo, lst.get(i))) {
				return true;
			}
		}
		return false;
	}

	protected boolean isEquals(String[] fields, CommonVO source, CommonVO target) {
		int len = fields.length;
		for (int i = 0; i < len; i++) {
			String fieldName = fields[i];
			if (!source.getString(fieldName).equals(target.getString(fieldName))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将本身转换成形如下的json字符串:<br/>
		<pre>
		[
			{"userid":"1","username":"1"},
			{"userid":"8","username":"3"},
			{"userid":"10","username":"10"},
			{"username2":["dddd","55555555555"], "userid":"8", "username":["6","555","2222"]}
		]
		</pre>
	 * @return
	 * @throws JSONException
	 */
	public String toJSONString() throws JSONException {
		JSONArray ary = toJSONObject();
		return ary.toJSONString();
	}

	/**
	* 将本身转换成JSONObject对象
	* @return
	* @throws JSONException
	*/
	public JSONArray toJSONObject() throws JSONException {
		JSONArray ary = new JSONArray();
		int count = this.count();
		for (int i = 0; i < count; i++) {
			CommonVO vo = this.get(i);
			ary.add(vo.toJSONObject());
		}
		return ary;
	}

}
