package com.infosys.lex.notification.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TableTagReplacement {

	private static String htmlString = "<p>Hi #learnerName, <br /> <br /> You are registered for an instructor led training as per details below:</p>\r\n"
			+ "<p>Name&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #contentTitle</p>\r\n"
			+ "<p>Location&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; #location (City, Country)</p>\r\n"
			+ "<p>&nbsp;</p>\r\n" + "<table>\r\n" + "<tbody>\r\n" + "<tr>\r\n" + "<td width=\"114\">\r\n"
			+ "<p>Date</p>\r\n" + "</td>\r\n" + "<td width=\"150\">\r\n" + "<p>Time</p>\r\n" + "</td>\r\n"
			+ "<td width=\"360\">\r\n" + "<p>Venue</p>\r\n" + "</td>\r\n" + "</tr>\r\n" + "<tr>\r\n"
			+ "<td width=\"114\">\r\n" + "<p>#date</p>\r\n" + "</td>\r\n" + "<td width=\"150\">\r\n"
			+ "<p>#time</p>\r\n" + "</td>\r\n" + "<td width=\"360\">\r\n" + "<p>#venue</p>\r\n" + "</td>\r\n"
			+ "</tr>\r\n" + "</tbody>\r\n" + "</table>\r\n" + "<p>&nbsp;</p>\r\n"
			+ "<p>Have a great day! <br /> <br /> Regards, <br /> Wingspan Team</p>\r\n"
			+ "<p>Are you receiving too many notifications? Click <a href=\"http://lex.infosysapps.com/\">here</a> to unsubscribe.</p>";

	private static List<String> hashTagDateData = Arrays.asList("2019-10-21", "2019-10-22", "2019-10-23");

	private static List<String> hashTagTimeData = Arrays.asList("3 PM - 4 PM", "3 PM - 4 PM", "3 PM - 4 PM");

	private static List<String> hashTagVenueData = Arrays.asList("Building B-12", "Building B-12", "Building B-12");

	public static void main(String args[]) {

		String finalHtmlString = "";

		int startIdx = htmlString.lastIndexOf("<tr");

		int endIdx = htmlString.lastIndexOf("</tr>");

		String tableTagDataExtracted = htmlString.substring(startIdx, endIdx + 5);

		List<String> totalDataToBeReplced = new ArrayList<String>();

		for (int i = 0; i < hashTagDateData.size(); i++) {
			totalDataToBeReplced.add(tableTagDataExtracted);
		}

		for (int i = 0; i < totalDataToBeReplced.size(); i++) {

			String data = totalDataToBeReplced.get(i);

			if (data.contains("#date")) {
				data = data.replace("#date", hashTagDateData.get(i));
			}
			if (data.contains("#time")) {
				data = data.replace("#time", hashTagTimeData.get(i));
			}
			if (data.contains("#venue")) {
				data = data.replace("#venue", hashTagVenueData.get(i));
			}

			finalHtmlString += data;
		}

		htmlString = htmlString.replace(tableTagDataExtracted, finalHtmlString);
		System.out.println(htmlString);

	}
}
