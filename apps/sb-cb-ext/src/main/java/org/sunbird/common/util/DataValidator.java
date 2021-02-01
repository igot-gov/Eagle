package org.sunbird.common.util;

import java.util.List;

public class DataValidator {
	public static boolean isStringEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isCollectionEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	public static boolean isCollectionEmpty(Iterable<?> list) {
		return list == null || !list.iterator().hasNext();
	}

}
