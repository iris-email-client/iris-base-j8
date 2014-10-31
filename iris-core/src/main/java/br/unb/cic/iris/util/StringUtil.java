package br.unb.cic.iris.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil {

	public static boolean notEmpty(String s) {
		return s != null && !s.isEmpty();
	}
	
	public static boolean isEmpty(String s){
		return s == null || s.isEmpty();
	}

	public static boolean notEmpty(String... parameters) {
		return notEmpty(Arrays.asList(parameters));
	}

	public static boolean notEmpty(List<String> parameters) {
		boolean retValue = true;
		for (String str : parameters) {
			if (isEmpty(str)) {
				retValue = false;
				break;
			}
		}
		return retValue;
	}

}
