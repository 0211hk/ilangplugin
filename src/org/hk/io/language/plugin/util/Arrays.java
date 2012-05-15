package org.hk.io.language.plugin.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Arrays {
	
	public static <T> ArrayList<T> newArrayList(){
		return new ArrayList<T>();
	}
	
	public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(){
		return new CopyOnWriteArrayList<T>();
	}

    public static <T> boolean isEmpty(T... anArray) {
        return isNotEmpty(anArray) == false;
    }

    public static <T> boolean isNotEmpty(T... anArray) {
        return (anArray != null && anArray.length != 0);
    }

    public static <T> T[] newArray(T... objects) {
        return objects;
    }

    public static <T> T[] clone(Class<T> type, T... original) {
        return clone(type, 0, original);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] clone(Class<T> type, int additionalLength, T... original) {
        if (original == null) {
            return null;
        }
        if (type == null) {
            return original;
        }
        T[] array = (T[]) Array.newInstance(type, original.length + additionalLength);
        System.arraycopy(original, 0, array, 0, original.length);
        return array;
    }

    public static <T> T[] add(Class<T> clazz, T addition, T... original) {
        T[] array = clone(clazz, 1, original);
        if (array != null) {
            array[array.length - 1] = addition;
            return array;
        }
        return null;
    }

}
