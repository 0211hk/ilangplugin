package org.hk.io.language.plugin.util;

public final class SystemPropertyUtil {
	
	private SystemPropertyUtil(){
		throw new UnsupportedOperationException();
	}
	
	public static final String getSystemProperty(final String property){
		return System.getProperty(property);
	}
	
	public static final String getLineSeparator(){
		return getSystemProperty("line.separator");
	}
}
