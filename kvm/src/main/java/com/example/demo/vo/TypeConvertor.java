package com.example.demo.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 基本类型转换存取器
 * 
 * @author cy
 */
public abstract class TypeConvertor implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     * @param key
     * @return
     */
    public abstract Object getObject(String key);


    /**
     * 
     * @param key
     * @param value
     */
    public abstract void setObject(String key, Object value);

    private Object getObject(Object obj, String key) {
        //看看key是否包含splitChar
        if (obj == null) {
            return null;
        }

        return this.getObjectVal(obj,key);
    }

    private Object getObjectVal(Object obj, String key) {
        if (obj instanceof CommonVO) {
            return ((CommonVO)obj).getObject(key);
        } else {
            //把第一个字母小写，后面不动
            String _key1 = key.substring(0, 1).toLowerCase();
            String _keyn = key.substring(1);
            key = _key1 + _keyn;
            try {
                return ReflectionUtils.getField(FieldUtils.getField(obj.getClass(), key), obj);
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * String
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        //1 判断key是否存在
        //2 分割key，做递归
        //3 如果是commonvo，那么直接get
        //4 如果是object，看看是否能够找到getXXXX方法，如果能找到就取，否则就null

        //Object ret = (Object) this.getObject(key);
        Object ret = (Object) this.getObject(this,key);


        if (ret == null) {
            return "";
        } else if (ret instanceof Date) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str = df.format((Date) ret);
            if (str.endsWith("00:00:00.0")) {
                str = str.substring(0, str.length() - 10);
            } else if (str.endsWith("00:00:00")) {
                str = str.substring(0, str.length() - 8);
            }
            return str = str.trim();
        } else {
            return ret.toString();
        }
    }

    /**
     * String
     * 
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        this.setObject(key, value);
    }

    /**
     * Integer
     * 
     * @param key
     * @return
     */
    public int getInteger(String key) {
        String value = this.getString(key);
        return Integer.valueOf(value).intValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     *            如果结果为空或null时的缺省值
     * @return
     */
    public int getInteger(String key, int defaultValue) {
        String value = this.getString(key);
        if ("".equals(value) || value == null) {
            return defaultValue;
        } else {
            return Integer.valueOf(value).intValue();
        }
    }

    /**
     * Long
     * 
     * @param key
     * @return
     */
    public long getLong(String key) {
        String value = this.getString(key);
        return Long.valueOf(value).longValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     *            如果结果为空或null时的缺省值
     * @return
     */
    public long getInteger(String key, long defaultValue) {
        String value = this.getString(key);
        if ("".equals(value) || value == null) {
            return defaultValue;
        } else {
            return Long.valueOf(value).longValue();
        }
    }

    /**
     * Float
     * 
     * @param key
     * @return
     */
    public float getFloat(String key) {
        String value = this.getString(key);
        return Float.valueOf(value).floatValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     *            如果结果为空或null时的缺省值
     * @return
     */
    public float getFloat(String key, float defaultValue) {
        String value = this.getString(key);
        if ("".equals(value) || value == null) {
            return defaultValue;
        } else {
            return Float.valueOf(value).floatValue();
        }
    }

    /**
     * Double
     * 
     * @param key
     * @return
     */
    public double getDouble(String key) {
        String value = this.getString(key);
        return Double.valueOf(value).doubleValue();
    }

    /**
     * 
     * @param key
     * @param defaultValue
     *            如果结果为空或null时的缺省值
     * @return
     */
    public double getDouble(String key, double defaultValue) {
        String value = this.getString(key);
        if ("".equals(value) || value == null) {
            return defaultValue;
        } else {
            return Double.valueOf(value).doubleValue();
        }
    }

    /**
     * Integer
     * 
     * @param key
     * @return
     */
    public void setInteger(String key, int value) {
        this.setString(key, "" + value);
    }

    /**
     * Long
     * 
     * @param key
     * @return
     */
    public void setLong(String key, long value) {
        this.setString(key, "" + value);
    }

    /**
     * Float
     * 
     * @param key
     * @return
     */
    public void setFloat(String key, float value) {
        this.setString(key, "" + value);
    }

    /**
     * double
     * 
     * @param key
     * @return
     */
    public void setDouble(String key, double value) {
        this.setString(key, "" + value);
    }
}