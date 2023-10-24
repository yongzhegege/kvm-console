package com.example.demo.vo;

/**
 * 数据获取接口
 * 
 * @author cy
 */
public interface DataVO {

	/**
	 * 根据键值获取数据
	 * 
	 * @param key
	 *            数据键值
	 * @return
	 */
	String getData(String key);

	/**
	 * 设置数据
	 * 
	 * @param key
	 * @param value
	 */
	void setData(String key, Object value);

	/**
	 * 获取指定索引的值
	 * 
	 * @param index
	 * @return
	 */
	String getData(int index);

	/**
	 * 设置父级关联数据
	 * 
	 * @param obj
	 */
	void setParent(Object obj);

	/**
	 * 得到父级关联数据
	 * 
	 * @return
	 */
	Object getParent();

	/**
	 * 得到关联数据集
	 * 
	 * @return
	 */
	VOList getSubData();

	/**
	 * 设置关联数据集
	 * 
	 * @param lst
	 */
	void setSubData(VOList lst);
	
	/**
	 * 判断是否包含某个字段
	 * @param key
	 * @return
	 */
	public boolean containKey(String key);
}
