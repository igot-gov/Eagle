package com.tarento.walletService.util;

import java.util.List;

public class Utils {
	public static boolean isListEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}
}
