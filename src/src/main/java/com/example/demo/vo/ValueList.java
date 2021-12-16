package com.example.demo.vo;

import java.util.Iterator;
import java.util.Map.Entry;

import com.example.demo.util.SequencedHashMap;


/**
 * 键/值列表类：键是唯一的
 * 
 * @author cy
 *
 */
public class ValueList implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected SequencedHashMap keyValues = new SequencedHashMap();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void add(String key, Object value) {
		int index = this.keyValues.indexOf(key);
		if (index >= 0) {
			Iterator<?> item = keyValues.entrySet().iterator();
			Entry en = null;
			for (int i = 0; i <= index; i++) {
				en = (Entry) item.next();
			}
			en.setValue(value);
		} else {
			keyValues.put(key, value);
		}
	}

	public Object get(String key) {
		return this.keyValues.get(key);
	}

	public Object get(int nIndex) {
		if (nIndex >= this.keyValues.size() || nIndex < 0) {
			return null;
		}
		return this.keyValues.getValue(nIndex);
	}

	public String getString(int nIndex) {
		Object obj = this.get(nIndex);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	public String getString(String key) {
		Object obj = this.get(key);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}
	}

	public int size() {
		return this.keyValues.size();
	}

	public void clear() {
		if (this.keyValues != null) {
			this.keyValues.clear();
		}
	}

	public void free() {
		this.clear();
	}

	public int indexOf(String key) {
		return this.keyValues.indexOf(key);
	}

	public String getKey(int nIndex) {
		return (String) this.keyValues.sequence().get(nIndex);
	}

	public boolean containsKey(String key) {
		return this.keyValues.containsKey(key);
	}

	public Object remove(int nIndex) {
		return this.keyValues.remove(nIndex);
	}

	public Object remove(String key) {
		return this.keyValues.remove(key);
	}

	public String[] getKeys() {
		String[] keys = new String[this.keyValues.size()];
		for (int i = 0; i < this.keyValues.size(); i++) {
			String key = (String) this.keyValues.get(i);
			keys[i] = key;
		}
		return keys;
	}

	public Object[] getValues() {
		Object[] values = new Object[this.keyValues.size()];
		for (int i = 0; i < this.keyValues.size(); i++) {
			Object value = this.keyValues.getValue(i);
			values[i] = value;
		}
		return values;
	}
}
