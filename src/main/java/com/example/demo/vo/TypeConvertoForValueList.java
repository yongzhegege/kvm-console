package com.example.demo.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.demo.util.NullType;


/**
 * 基于ValueList的类型转换器实现
 * 
 * @author cy
 *
 */
public class TypeConvertoForValueList extends TypeConvertor implements
		java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	protected ValueList hsOwner = new ValueList();

	/**
	 * 
	 * 
	 */
	public void clear() {
		this.hsOwner.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exp.fcl.util.TypeConvertor#setObject(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setObject(String key, Object value) {
		if (value != null) {
			this.hsOwner.add(key, value);
		} else {
			this.hsOwner.add(key, NullType.Null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exp.fcl.util.TypeConvertor#getObject(java.lang.String)
	 */
	public Object getObject(String key) {
		Object obj = this.hsOwner.get(key);
		if (NullType.isNull(obj)) {
			return null;
		} else {
			return obj;
		}
	}

	/**
	 * getObject
	 * 
	 * @param index
	 * @return
	 */
	public Object getObject(int index) {
		Object obj = this.hsOwner.get(index);
		if (NullType.isNull(obj)) {
			return null;
		} else {
			return obj;
		}
	}

	/**
	 * getString
	 * 
	 * @param index
	 * @return
	 */
	public String getString(int index) {
		Object ret = (Object) this.getObject(index);
		if (ret == null) {
			return "";
		} else if(ret instanceof Date){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str = df.format((Date)ret);
			if (str.endsWith("00:00:00.0")) {
				str = str.substring(0,	str.length() - 10);
			} else if (str.endsWith("00:00:00")) {
				str = str.substring(0,	str.length() - 8);
			}
			return str = str.trim();			
		} else {
			return ret.toString();
		}
	}

	/**
	 * getInteger
	 * 
	 * @param index
	 * @return
	 */
	public int getInteger(int index) {
		String value = this.getString(index);
		return Integer.valueOf(value).intValue();
	}

	/**
	 * 
	 * @param index
	 * @param defaultValue
	 *            如果结果为空或null时的缺省值
	 * @return
	 */
	public int getInteger(int index, int defaultValue) {
		String value = this.getString(index);
		if ("".equals(value) || value == null) {
			return defaultValue;
		} else {
			return Integer.valueOf(value).intValue();
		}
	}

	/**
	 * getLong
	 * 
	 * @param index
	 * @return
	 */
	public long getLong(int index) {
		String value = this.getString(index);
		return Long.valueOf(value).longValue();
	}

	/**
	 * 
	 * @param index
	 * @param defaultValue
	 *            如果结果为空或null时的缺省值
	 * @return
	 */
	public long getLong(int index, long defaultValue) {
		String value = this.getString(index);
		if ("".equals(value) || value == null) {
			return defaultValue;
		} else {
			return Long.valueOf(value).longValue();
		}
	}

	/**
	 * getFloat
	 * 
	 * @param index
	 * @return
	 */
	public float getFloat(int index) {
		String value = this.getString(index);
		return Float.valueOf(value).floatValue();
	}

	/**
	 * 
	 * @param index
	 * @param defaultValue
	 *            如果结果为空或null时的缺省值
	 * @return
	 */
	public float getFloat(int index, float defaultValue) {
		String value = this.getString(index);
		if ("".equals(value) || value == null) {
			return defaultValue;
		} else {
			return Float.valueOf(value).floatValue();
		}
	}

	/**
	 * getDouble
	 * 
	 * @param index
	 * @return
	 */
	public double getDouble(int index) {
		String value = this.getString(index);
		return Double.valueOf(value).doubleValue();
	}

	/**
	 * 
	 * @param index
	 * @param defaultValue
	 *            如果结果为空或null时的缺省值
	 * @return
	 */
	public double getDouble(int index, double defaultValue) {
		String value = this.getString(index);
		if ("".equals(value) || value == null) {
			return defaultValue;
		} else {
			return Double.valueOf(value).doubleValue();
		}
	}

	/**
	 * 键值列表
	 * 
	 * @return
	 */
	public String[] listKeys() {
		return this.hsOwner.getKeys();
	}
}
