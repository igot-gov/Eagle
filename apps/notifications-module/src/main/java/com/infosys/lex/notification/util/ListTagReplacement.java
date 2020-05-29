package com.infosys.lex.notification.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ListTagReplacement {

	private static String htmlString = "<p>Hi #learnerName, <br /> <br /> You are registered for an instructor led training as per details below:</p>\r\n"
			+ "<p>Name&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #contentTitle</p>\r\n"
			+ "<p>Location&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #location (City, Country)</p>\r\n"
			+ "<p>&nbsp;</p>\r\n" + "<ul style=\"list-style-type:disc;\">\r\n" + "  <li>#item</li>\r\n" + "</ul>\r\n"
			+ "\r\n" + "<p>&nbsp;</p>\r\n" + "<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>\r\n"
			+ "<p>Are you receiving too many notifications? Click <a href=\"\">here</a> to unsubscribe.</p>";

	private static List<String> hashTagListData = Arrays.asList("2019-10-21", "2019-10-22", "2019-10-23");

	Map<String, Object> data = new HashMap<String, Object>();

	public static void main(String args[]) {

		String finalHtmlString = "";

		int startIdx = htmlString.lastIndexOf("<ul");

		int endIdx = htmlString.lastIndexOf("</ul>");

		String tableTagDataExtracted = htmlString.substring(startIdx, endIdx + 5);

		List<String> totalDataToBeReplced = new ArrayList<String>();

		for (int i = 0; i < hashTagListData.size(); i++) {
			totalDataToBeReplced.add(tableTagDataExtracted);
		}

		for (int i = 0; i < totalDataToBeReplced.size(); i++) {

			String data = totalDataToBeReplced.get(i);

			if (data.contains("#item")) {
				data = data.replace("#item", hashTagListData.get(i));
			}

			finalHtmlString += data;
		}

		htmlString = htmlString.replace(tableTagDataExtracted, finalHtmlString);
		System.out.println(htmlString);

	}
}
