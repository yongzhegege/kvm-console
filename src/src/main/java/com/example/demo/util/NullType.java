package com.example.demo.util;

public class NullType {
	
	protected NullType() {

	}

	public static final NullType Null = new NullType();

	public boolean equals(Object arg0) {
		return arg0 == null || Null == arg0;
	}

	public static boolean isNull(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj == Null) {
			return true;
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode();
	}

	public String toString() {
		return null;
	}
}
