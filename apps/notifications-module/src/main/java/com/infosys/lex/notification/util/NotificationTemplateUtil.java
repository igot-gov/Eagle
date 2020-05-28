package com.infosys.lex.notification.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.infosys.lex.notification.dto.UserInfo;
import com.infosys.lex.notification.exception.ApplicationLogicException;

public class NotificationTemplateUtil {

	public static String replaceTags(String rootOrg, Map<String, Object> tagValuePairs, String text,
			Map<String, UserInfo> usersInfoMap, Map<String, List<String>> recipients, String recipientRole,

			Map<String, String> targetUrlData, String domainName, String appEmail) {
		// this is for replacement of tabular data.
		// all entries in tagValuePairs that are of list type will be replace as columns
		// in the table row size will depend on no of items in the list.
		text = replaceTabularTags(tagValuePairs, text);

//		text = replaceListTags(tagValuePairs, text, usersInfoMap, recipients);

		// replace targetUrl existing in email's, if not found in request body then
		// constructed url from with domains name and relative url is used.
		text = replaceTargetUrlTags(rootOrg, tagValuePairs, text, recipientRole, targetUrlData, domainName, appEmail);

		// derived tags like userName,userEmail,userFirstName which will be formed by
		// adding Name,Email etc in every entry in tag value pair map and recipient key
		// in request body.
		text = replaceDerivedTags(tagValuePairs, text, usersInfoMap, recipients);

		// blind replacement of notification template tags as received in the request
		// body in tagValuePairs.
		text = replaceFixedTags(tagValuePairs, text);

		return text;
	}

	/*
	 * This method updates the list tags that matcheslist tags to tagvaluepairs or
	 * the recipients and replaces with the list of the users. For this the
	 * tagsvalues should be list
	 */
	@SuppressWarnings("unchecked")
	private static String replaceListTags(Map<String, Object> tagValuePairs, String text,
			Map<String, UserInfo> usersInfo, Map<String, List<String>> recipients) {

		String checkTag;
		StringBuilder replaceText;
		String userName;
		String userFirstName;
		String userEmail;
		if (text.contains("<li>")) {
			// replace list tag-value pairs if exits
			for (String tagKey : tagValuePairs.keySet()) {
				if (tagValuePairs.get(tagKey) instanceof List) {
					List<String> tagValues = (List<String>) tagValuePairs.get(tagKey);
					checkTag = "<li>" + tagKey + "</li>";
					if (text.contains(checkTag)) {
						replaceText = new StringBuilder();
						for (String value : tagValues) {
							replaceText.append("<li>" + value + "</li>");
						}
						text = text.replaceAll(checkTag, replaceText.toString());
					}

					// check if the tag-Name is present then replace with Name
					checkTag = "<li>" + tagKey + "Name" + "</li>";
					if (text.contains(checkTag)) {
						replaceText = new StringBuilder();
						for (String userId : tagValues) {
							if (!usersInfo.containsKey(userId))
								continue;
							userName = usersInfo.get(userId).getName();
							replaceText.append("<li>" + userName + "</li>");
						}
						text = text.replaceAll(checkTag, replaceText.toString());
					}

					// check if the taguser-FirstName is present then replace with Name
					checkTag = "<li>" + tagKey + "FirstName" + "</li>";
					if (text.contains(checkTag)) {
						replaceText = new StringBuilder();
						for (String userId : tagValues) {
							if (!usersInfo.containsKey(userId))
								continue;
							userFirstName = usersInfo.get(userId).getFirstName();
							replaceText.append("<li>" + userFirstName + "</li>");
						}
						text = text.replaceAll(checkTag, replaceText.toString());
					}

					// check if the taguser-email is present then replace with Name
					checkTag = "<li>" + tagKey + "Email" + "</li>";
					if (text.contains(checkTag)) {
						replaceText = new StringBuilder();
						for (String userId : tagValues) {
							if (!usersInfo.containsKey(userId))
								continue;
							userEmail = usersInfo.get(userId).getEmail();
							replaceText.append("<li>" + userEmail + "</li>");
						}
						text = text.replaceAll(checkTag, replaceText.toString());
					}
				}
			}
			// replace derived

			for (Map.Entry<String, List<String>> recipientEntry : recipients.entrySet()) {

				// check if the tag-Name is present then replace with Name
				checkTag = "<li>" + recipientEntry.getKey() + "Name" + "</li>";
				if (text.contains(checkTag)) {
					replaceText = new StringBuilder();
					for (String userId : recipientEntry.getValue()) {
						if (!usersInfo.containsKey(userId))
							continue;
						userName = usersInfo.get(userId).getName();
						replaceText.append("<li>" + userName + "</li>");
					}
					text = text.replaceAll(checkTag, replaceText.toString());
				}

				// check if the taguser-FirstName is present then replace with Name
				checkTag = "<li>" + recipientEntry.getKey() + "FirstName" + "</li>";
				if (text.contains(checkTag)) {
					replaceText = new StringBuilder();
					for (String userId : recipientEntry.getValue()) {
						if (!usersInfo.containsKey(userId))
							continue;
						userFirstName = usersInfo.get(userId).getFirstName();
						replaceText.append("<li>" + userFirstName + "</li>");
					}
					text = text.replaceAll(checkTag, replaceText.toString());
				}

				// check if the taguser-email is present then replace with Name
				checkTag = "<li>" + recipientEntry.getKey() + "Email" + "</li>";
				if (text.contains(checkTag)) {
					replaceText = new StringBuilder();
					for (String userId : recipientEntry.getValue()) {
						if (!usersInfo.containsKey(userId))
							continue;
						userEmail = usersInfo.get(userId).getEmail();
						replaceText.append("<li>" + userEmail + "</li>");
					}
					text = text.replaceAll(checkTag, replaceText.toString());
				}

			}

		}

		return text;

	}

	@SuppressWarnings("unchecked")
	private static String replaceTabularTags(Map<String, Object> tagValuePairs, String text) {

		// check for tabular data
		if (!text.contains("<tr") || tagValuePairs == null)
			return text;

		// find out all list entries to to replace as Columns in the table.
		List<String> tagDataList = new ArrayList<>();
		for (Map.Entry<String, Object> entry : tagValuePairs.entrySet())
			if (entry.getValue() instanceof List)
				tagDataList.add(entry.getKey());

		if (tagDataList.isEmpty())
			return text;

		String generatedHtmlRows = "";

		int startIdx = text.lastIndexOf("<tr");
		int endIdx = text.lastIndexOf("</tr>");

		String tableTagDataExtracted = text.substring(startIdx, endIdx + 5);

		List<String> totalDataToBeReplced = new ArrayList<String>();

		int size = ((List<String>) tagValuePairs.get(tagDataList.get(0))).size();

		for (int i = 0; i < size; i++) {
			totalDataToBeReplced.add(tableTagDataExtracted);
		}

		for (int i = 0; i < totalDataToBeReplced.size(); i++) {

			String data = totalDataToBeReplced.get(i);
			for (String tagData : tagDataList) {
				if (data.contains(tagData)) {
					data = data.replace(tagData, ((List<String>) tagValuePairs.get(tagData)).get(i));
				}
			}
			generatedHtmlRows += data;
		}

		text = text.replace(tableTagDataExtracted, generatedHtmlRows);
		return text;

	}

	private static String replaceFixedTags(Map<String, Object> tagValuePairs, String text) {

		if (tagValuePairs == null)
			return text;

		for (Map.Entry<String, Object> entry : tagValuePairs.entrySet()) {
			if (text.contains(entry.getKey())) {
				if (entry.getKey().endsWith("Title"))
					text = text.replaceAll(entry.getKey(), "\"" + (String) entry.getValue() + "\"");
				else
					text = text.replaceAll(entry.getKey(), (String) entry.getValue());
			}
		}
		return text;
	}

	private static String replaceDerivedTags(Map<String, Object> tagValuePairs, String text,
			Map<String, UserInfo> usersInfoMap, Map<String, List<String>> recipients) {

		if (tagValuePairs == null) {
			return text;
		}
		// if tag found in the template matches with tagValuePairs entry, then replace
		// the as is.
		for (Map.Entry<String, Object> entry : tagValuePairs.entrySet()) {
			if (!usersInfoMap.containsKey(entry.getValue()))
				continue;
			if (text.contains(entry.getKey() + "Name"))
				text = text.replaceAll(entry.getKey() + "Name", usersInfoMap.get(entry.getValue()).getName());

			if (text.contains(entry.getKey() + "Email"))
				text = text.replaceAll(entry.getKey() + "Email", usersInfoMap.get(entry.getValue()).getEmail());

			if (text.contains(entry.getKey() + "FirstName"))
				text = text.replaceAll(entry.getKey() + "FirstName", usersInfoMap.get(entry.getValue()).getFirstName());
		}

		// also searching in recipients of the notification event, if tag name matches
		// then replace as comma seperated values.
		for (Map.Entry<String, List<String>> recipientEntry : recipients.entrySet()) {
			String replaceWith = "";
			String replaceText = "";
			
			//replace count of the given recipient
			if(text.contains("#" + recipientEntry.getKey() + "Count"))
			{
				replaceText = "#" + recipientEntry.getKey() + "Count";
				replaceWith = String.valueOf(recipientEntry.getValue().size());
				text = text.replaceAll(replaceText, replaceWith);
			}
				
			
			if (text.contains("#" + recipientEntry.getKey() + "Name")) {
				replaceText = "#" + recipientEntry.getKey() + "Name";
				for (String userId : recipientEntry.getValue()) {
					if (!usersInfoMap.containsKey(userId))
						continue;
					replaceWith = replaceWith + ", " + usersInfoMap.get(userId).getName();

				}
				if (!replaceWith.isEmpty()) {
					replaceWith = replaceWith.trim();
					replaceWith = replaceWith.substring(2);
					text = text.replaceAll(replaceText, replaceWith);
				}
			}
			if (text.contains("#" + recipientEntry.getKey() + "Email")) {
				replaceText = "#" + recipientEntry.getKey() + "Email";
				replaceWith = "";
				for (String userId : recipientEntry.getValue()) {
					if (!usersInfoMap.containsKey(userId))
						continue;
					replaceWith = replaceWith + ", " + usersInfoMap.get(userId).getEmail();
				}
				if (!replaceWith.isEmpty()) {
					replaceWith = replaceWith.trim();
					//remove initial ", "
					replaceWith = replaceWith.substring(2);
					text = text.replaceAll(replaceText, replaceWith);
				}
			}
			if (text.contains("#" + recipientEntry.getKey() + "FirstName")) {
				replaceText = "#" + recipientEntry.getKey() + "FirstName";
				replaceWith = "";
				for (String userId : recipientEntry.getValue()) {
					if (!usersInfoMap.containsKey(userId))
						continue;
					replaceWith = replaceWith + ", " + usersInfoMap.get(userId).getFirstName();
				}
				if (!replaceWith.isEmpty()) {
					replaceWith = replaceWith.trim();
					replaceWith = replaceWith.substring(2);
					text = text.replaceAll( replaceText, replaceWith);
				}
			}

		}

		return text;
	}

	private static String replaceTargetUrlTags(String rootOrg, Map<String, Object> tagValuePairs, String text,
			String recipientRole, Map<String, String> targetUrlData, String domainName, String appEmail) {

		if (tagValuePairs == null) {
			return text;
		}

		if (text.contains("#targetUrl")) {
			if (tagValuePairs.containsKey("#targetUrl"))
				text = text.replaceAll("#targetUrl", tagValuePairs.get("#targetUrl").toString());
			else {
				if(!targetUrlData.containsKey(recipientRole))
					throw new ApplicationLogicException("target Url not set for recipient role : "+recipientRole);
				
				if (targetUrlData.get(recipientRole).contains("{lexId}")) {
					if (tagValuePairs.containsKey("#lexId") && tagValuePairs.get("#lexId") != null
							|| !tagValuePairs.get("#lexId").toString().isEmpty()) {

						String url = targetUrlData.get(recipientRole);
						url = url.replace("{lexId}", tagValuePairs.get("#lexId").toString());
						targetUrlData.put(recipientRole, url);
					} else {
						throw new ApplicationLogicException("Required lexId for processing the event not present!");
					}
				}

				text = text.replaceAll("#targetUrl", domainName + "/" + targetUrlData.get(recipientRole));
			}
		}

		if (text.contains("#appUrl")) {
			text = text.replaceAll("#appUrl", domainName);
		}

		if (text.contains("#appEmail")) {
			if (appEmail == null)
				throw new ApplicationLogicException("Required App Email not found!");
			text = text.replaceAll("#appEmail", appEmail);
		}

		return text;
	}

	/*
	 * This method gets domain name to be sent to user
	 */
	public static String getDomainForUser(List<String> orgs, Map<String, String> orgDomainMap) {

		String orgKey = orgs.stream().filter(org -> orgDomainMap.containsKey(org)).findFirst().orElse(null);

		if (orgKey == null) {
			orgKey = orgDomainMap.keySet().stream().findFirst().get();
		}

		return orgDomainMap.get(orgKey);

	}

	public static String getAppEmailForUser(List<String> orgs, Map<String, String> orgAppEmailMap) {

		String orgKey = orgs.stream().filter(org -> orgAppEmailMap.containsKey(org)).findFirst().orElse(null);

		if (orgKey == null) {
			orgKey = orgAppEmailMap.keySet().stream().findFirst().get();
		}

		return orgAppEmailMap.get(orgKey);
	}

}
