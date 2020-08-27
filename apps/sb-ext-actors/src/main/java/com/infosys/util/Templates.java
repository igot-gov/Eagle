/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.util;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Templates {

	@SuppressWarnings("unchecked")
	public static String ShareTemplate(Map<String, Object> artifact, String SharedByName, String bodyMessage) {
		StringBuilder sb = new StringBuilder();
		String authors = "";
		for (Map<String, Object> author : (List<Map<String, Object>>) artifact.get("authors")) {
			if (author.containsKey("name")) {
				authors += author.get("name").toString();
				authors += " ";
			}
		}
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'>	<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]-->");
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'>");
		sb.append("<div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br><br>" + SharedByName
				+ " has recommended this for you!</p>" + (bodyMessage.equals("") ? "" : bodyMessage + "<br>")
				+ "</div><br><table class='tabtd tab' cellpadding='0' cellspacing='0' width='100%' align='center' style='table-layout: auto !important;font-family: calibri;'>");
		sb.append(
				"<tr><td width='600' align=center valign='top' style='width:371.45pt;border:none;border-bottom:solid #BFBFBF 1.5pt;background:white;padding:5.75pt 5.75pt 5.75pt 5.75pt;height:44.6pt'><span style='font-size:25px;color:#404040;text-decoration:none;text-underline:none'><span style='color:windowtext'>"
						+ artifact.get("title") + "</span>");
		sb.append("</span><br>" + (authors.equals("")?"":authors)
				+ "</td></tr><tr style='border:1px solid black;border-collapse: collapse;'><td width='600' valign='top' style='width:371.45pt;border:none;border-bottom:solid #BFBFBF 1.5pt;background:white;padding:5.75pt 5.75pt 5.75pt 5.75pt;height:44.6pt'>"
				+ artifact.get("description") + "</td>");
		sb.append("</tr><tr><td><br><a href='" + artifact.get("url")
				+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:20px;color:#3F51B5'>LEARN MORE</span></b></a></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");
		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String QueryTemplate(String bodyMessage, Map<String, Object> artifact, String appName) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br><br>I found your reference on "+appName+" while going through <a href='"
						+ artifact.get("url") + "'>");
		sb.append("<b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
				+ artifact.get("title") + "</span></b></a></p>" + (bodyMessage.equals("") ? "" : bodyMessage + "<br>")
				+ "</div><br><p style='text-align:center;'></p><font size='3'>P.S. keep "+appName+" in Cc</font></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");

		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String InvalidTemplate(String bodyMessage) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'>"
						+ (bodyMessage.equals("") ? "" : "<p>" + bodyMessage + "</p>") + "<br></div><br>");
		sb.append(
				"<p style='text-align:center;'></p></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");

		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String DownloadTemplate(String bodyMessage, Map<String, Object> artifact, String fileStatus) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'>"
						+ bodyMessage + "<br>");
		sb.append((fileStatus.equals("0") ? ""
				: "<p><br>PFA content for <a href='" + artifact.get("url")
						+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
						+ artifact.get("title") + "</span></b></a></p>")
				+ "</div><br><p style='text-align:center;'></p></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");
		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String ShareTemplateGP(String email, String title, String SharedByName, String bodyMessage,
			String table, String msg, String link) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'>	<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]-->");
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'>");
		sb.append("<div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi " + email + ",<br><br>"
				+ SharedByName + " " + msg + "</p>"
				+ (bodyMessage.equals("") ? "" : bodyMessage.replaceAll("\n", "</br>") + "<br>")
				+ "</div><br><table class='tabtd tab' cellpadding='0' cellspacing='0' width='100%' align='center' style='table-layout: auto !important;font-family: calibri;'>");
		sb.append(
				"<tr><td width='600' align=center valign='top' style='width:371.45pt;border:none;border-bottom:solid #BFBFBF 1.5pt;background:white;padding:5.75pt 5.75pt 5.75pt 5.75pt;height:44.6pt'><span style='font-size:25px;color:#404040;text-decoration:none;text-underline:none'><span style='color:windowtext'>"
						+ title + "</span>");
		sb.append(
				"</span></td></tr><tr style='border:1px solid black;border-collapse: collapse;'><td width='600' valign='top' style='width:371.45pt;border:none;border-bottom:solid #BFBFBF 1.5pt;background:white;padding:5.75pt 5.75pt 5.75pt 5.75pt;height:44.6pt'>"
						+ table + "</td>");
		sb.append("</tr><tr><td><br>" + link
				+ "</td></tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");
		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String CertificateTemplate(String title, String link) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br><br>Thanks for your interest in taking this internal certification.<br/><br/> Click <a href='"
						+ link
						+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>here</span></b></a> to book an assessment slot in your nearest certification center.<br/><br/> You may contact ACC  for further clarifications / issues.<br/><br/> Regards,<br/> Team ETA ");
		sb.append("</br></p></div></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");

		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String ExternalCertificateTemplate(String title, String link) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br/><br/>Thanks for your interest in taking this certification.<br/><br/> This is an external certification and needs approval from a manager in JL6 or above, because of the budget requirements. Click <a href='"
						+ link
						+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>here</span></b></a> to register for "
						+ title
						+ " certification.  Once approved, you may complete the certification, by paying the required fees to certification vendor, and taking assessment. <br/><br/>Up on successful completion you are required to upload proof of completion of certification on Learning Hub.<br/><br/> Regards,<br/> Team ETA ");
		sb.append("</br></p></div></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");

		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	public static String InternalTrainingTemplate(String link) {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]--><table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'><table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'><div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br><br>Thanks for your interest in taking this internal classroom course.<br/><br/> Click <a href='"
						+ link
						+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>here</span></b></a> to register.<br/><br/> You may contact Queries_ETA for further clarifications / issues.<br/><br/> Regards,<br/> Team ETA ");
		sb.append("</br></p></div></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");

		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	@SuppressWarnings("unchecked")
	public static String SendForReview(Map<String, Object> artifact, String bodyMessage, String lastLine, String emailType, String appName) throws Exception {
		StringBuilder sb = new StringBuilder();
		String reviewers = "";
		String details="";
		for (Map<String, Object> reviewer : (List<Map<String, Object>>) artifact.get("trackContacts")) {
			if (reviewer.containsKey("name")) {
				reviewers += reviewer.get("name").toString()+", ";
			}
		}
		if (!reviewers.isEmpty()) {
			reviewers = reviewers.substring(0, reviewers.length() - 2);
		}
		
		String authors = "";
		for (Map<String, Object> author : (List<Map<String, Object>>) artifact.get("creatorContacts")) {
			if (author.containsKey("name")) {
				authors += author.get("name").toString()+", ";
			}
		}
		if (!authors.isEmpty()) {
			authors = authors.substring(0, authors.length() - 2);
		}
		
		if(emailType.equals("sendforreview")||emailType.equals("sendforpublish")) {	
			//author-reviewer-(no comments)
			details="Details:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Name : "+artifact.get("name")+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+appName+" id: "+artifact.get("identifier")+ (authors.equals("")?"":"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Authored By: "+authors)+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Reviewers: "+reviewers;			
		}else if(emailType.equals("reviewerreviewed")||emailType.equals("reviewerrejected")||emailType.equals("publisherpublished")||emailType.equals("publisherrejected")) {
			//author-reviewer-comments
			if(artifact.containsKey("comments"))
				details="Details:<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Name : "+artifact.get("name")+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+appName+" id: "+artifact.get("identifier")+(authors.equals("")?"":"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Authored By: "+authors)+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Reviewers: "+reviewers+"<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comments Given: "+artifact.get("comments");
			else
				throw new Exception("User Comment Missing");
		}
		else {
			throw new Exception("Invalid Email Type");
		}
		
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='width: 800px !Important ;height:7%;'>");
		sb.append(
				"<tr><td class='tabtd' style='1px solid #00a2e8'>	<!--[if mso]><table bgcolor='#00a2e8' class='tabtd tab' role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' align='center'><tr><td class='tabtd'><![endif]-->");
		sb.append(
				"<table role='presentation' class='tabtd tab' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;'><tr><td class='tabtd' style='1px solid #00a2e8'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' width='100%' style='max-width: 100%;height:85%;'><tr><td bgcolor='#ffffff' style='vertical-align:top'>");
		sb.append(
				"<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='tabtd' style='padding:30px 10px; font-family: calibri; font-size: 16px;line-height: 20px; color: #555555;'>");
		sb.append("<div style='color:#333;padding: 2px 0px 2px 0px;text-align:left;'><p>Hi,<br><br>"
				+ (bodyMessage.equals("") ? "" : bodyMessage + "<br><br>"+details+"<br><br>"+lastLine+"<br>")
				+ "</p></div></td></tr></table></td></tr></table></td></tr></table></td></tr></table>");
		return getParentTemplate().replace("BodyTemplate", sb.toString()).replace("footerYear",
				Calendar.getInstance().get(Calendar.YEAR) + "");
	}

	private static String getParentTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(
				"<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\r\n xmlns:o=\"urn:schemas-microsoft-com:office:office\"\r\n     xmlns:w=\"urn:schemas-microsoft-com:office:word\"\r\n      xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\r\n");
		sb.append(
				"      xmlns=\"http://www.w3.org/TR/REC-html40\">\r\n\r\n<head>\r\n    <meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\r\n    <meta name=ProgId content=Word.Document>\r\n    <meta name=Generator content=\"Microsoft Word 15\">\r\n");
		sb.append(
				"    <meta name=Originator content=\"Microsoft Word 15\">\r\n    <!--[if !mso]>\r\n    <style>\r\n    v\\:* {behavior:url(#default#VML);}\r\n    o\\:* {behavior:url(#default#VML);}\r\n    w\\:* {behavior:url(#default#VML);}\r\n");
		sb.append(
				"    .shape {behavior:url(#default#VML);}\r\n    </style>\r\n    <![endif]-->\r\n    <!--[if gte mso 9]><xml>\r\n     <o:OfficeDocumentSettings>\r\n      <o:AllowPNG/>\r\n     </o:OfficeDocumentSettings>\r\n    </xml><![endif]-->\r\n    <!--[if gte mso 9]><xml>\r\n");
		sb.append(
				"     <w:WordDocument>\r\n      <w:Zoom>110</w:Zoom>\r\n      <w:TrackMoves>false</w:TrackMoves>\r\n      <w:TrackFormatting/>\r\n      <w:ValidateAgainstSchemas/>\r\n      <w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid>\r\n");
		sb.append(
				"      <w:IgnoreMixedContent>false</w:IgnoreMixedContent>\r\n      <w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText>\r\n      <w:DoNotPromoteQF/>\r\n      <w:LidThemeOther>EN-US</w:LidThemeOther>\r\n");
		sb.append(
				"      <w:LidThemeAsian>X-NONE</w:LidThemeAsian>\r\n      <w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript>\r\n      <w:Compatibility>\r\n       <w:DoNotExpandShiftReturn/>\r\n       <w:BreakWrappedTables/>\r\n");
		sb.append(
				"       <w:SplitPgBreakAndParaMark/>\r\n       <w:EnableOpenTypeKerning/>\r\n      </w:Compatibility>\r\n      <w:BrowserLevel>MicrosoftInternetExplorer4</w:BrowserLevel>\r\n      <m:mathPr>\r\n       <m:mathFont m:val=\"Cambria Math\"/>\r\n       <m:brkBin m:val=\"before\"/>\r\n       <m:brkBinSub m:val=\"&#45;-\"/>\r\n");
		sb.append(
				"       <m:smallFrac m:val=\"off\"/>\r\n       <m:dispDef/>\r\n       <m:lMargin m:val=\"0\"/>\r\n       <m:rMargin m:val=\"0\"/>\r\n       <m:defJc m:val=\"centerGroup\"/>\r\n       <m:wrapIndent m:val=\"1440\"/>\r\n       <m:intLim m:val=\"subSup\"/>\r\n       <m:naryLim m:val=\"undOvr\"/>\r\n");
		sb.append(
				"      </m:mathPr></w:WordDocument>\r\n    </xml><![endif]-->\r\n    <!--[if gte mso 9]><xml>\r\n     <w:LatentStyles DefLockedState=\"false\" DefUnhideWhenUsed=\"false\"\r\n      DefSemiHidden=\"false\" DefQFormat=\"false\" DefPriority=\"99\"\r\n      LatentStyleCount=\"371\">\r\n      <w:LsdException Locked=\"false\" Priority=\"0\" QFormat=\"true\" Name=\"Normal\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"9\" QFormat=\"true\" Name=\"heading 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 7\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 8\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"9\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"heading 9\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"index 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 6\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"index 7\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index 8\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"index 9\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n");
		sb.append(
				"       UnhideWhenUsed=\"true\" Name=\"toc 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 7\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 8\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"toc 9\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Normal Indent\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"footnote text\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"annotation text\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"header\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"footer\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"index heading\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"35\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"caption\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"table of figures\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"envelope address\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"envelope return\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"footnote reference\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"annotation reference\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"line number\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"page number\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"endnote reference\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"endnote text\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"table of authorities\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"macro\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"toa heading\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Bullet\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"List Number\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"List Bullet 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Bullet 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Bullet 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Bullet 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Number 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"List Number 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Number 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Number 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"10\" QFormat=\"true\" Name=\"Title\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Closing\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Signature\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"1\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"Default Paragraph Font\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Body Text Indent\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Continue\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"List Continue 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Continue 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"List Continue 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"List Continue 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Message Header\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"11\" QFormat=\"true\" Name=\"Subtitle\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Salutation\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Date\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text First Indent\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text First Indent 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Note Heading\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text Indent 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Body Text Indent 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Block Text\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Hyperlink\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"FollowedHyperlink\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"22\" QFormat=\"true\" Name=\"Strong\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"20\" QFormat=\"true\" Name=\"Emphasis\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Document Map\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Plain Text\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"E-mail Signature\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"HTML Top of Form\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Bottom of Form\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Normal (Web)\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Acronym\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Address\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"HTML Cite\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Code\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Definition\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Keyboard\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Preformatted\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"HTML Sample\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Typewriter\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"HTML Variable\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Normal Table\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"annotation subject\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"No List\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Outline List 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Outline List 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Outline List 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Simple 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Simple 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Simple 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Classic 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Classic 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Classic 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Classic 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Colorful 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Colorful 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Colorful 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Columns 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Columns 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Columns 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Columns 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Columns 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Grid 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 7\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Grid 8\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 4\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 5\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 7\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table List 8\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table 3D effects 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table 3D effects 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table 3D effects 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n");
		sb.append(
				"       Name=\"Table Contemporary\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Elegant\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Professional\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Subtle 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Subtle 2\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Web 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Web 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Web 3\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Balloon Text\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" Name=\"Table Grid\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" SemiHidden=\"true\" UnhideWhenUsed=\"true\"\r\n       Name=\"Table Theme\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" Name=\"Placeholder Text\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"1\" QFormat=\"true\" Name=\"No Spacing\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" SemiHidden=\"true\" Name=\"Revision\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"34\" QFormat=\"true\"\r\n       Name=\"List Paragraph\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"29\" QFormat=\"true\" Name=\"Quote\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"30\" QFormat=\"true\"\r\n       Name=\"Intense Quote\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"60\" Name=\"Light Shading Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"61\" Name=\"Light List Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"62\" Name=\"Light Grid Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"63\" Name=\"Medium Shading 1 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"64\" Name=\"Medium Shading 2 Accent 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"65\" Name=\"Medium List 1 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"66\" Name=\"Medium List 2 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"67\" Name=\"Medium Grid 1 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"68\" Name=\"Medium Grid 2 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"69\" Name=\"Medium Grid 3 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"70\" Name=\"Dark List Accent 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"71\" Name=\"Colorful Shading Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"72\" Name=\"Colorful List Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"73\" Name=\"Colorful Grid Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"19\" QFormat=\"true\"\r\n       Name=\"Subtle Emphasis\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"21\" QFormat=\"true\"\r\n");
		sb.append(
				"       Name=\"Intense Emphasis\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"31\" QFormat=\"true\"\r\n       Name=\"Subtle Reference\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"32\" QFormat=\"true\"\r\n       Name=\"Intense Reference\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"33\" QFormat=\"true\" Name=\"Book Title\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"37\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" Name=\"Bibliography\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"39\" SemiHidden=\"true\"\r\n       UnhideWhenUsed=\"true\" QFormat=\"true\" Name=\"TOC Heading\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"41\" Name=\"Plain Table 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"42\" Name=\"Plain Table 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"43\" Name=\"Plain Table 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"44\" Name=\"Plain Table 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"45\" Name=\"Plain Table 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"40\" Name=\"Grid Table Light\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\" Name=\"Grid Table 1 Light\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\" Name=\"Grid Table 6 Colorful\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\" Name=\"Grid Table 7 Colorful\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n");
		sb.append(
				"       Name=\"Grid Table 7 Colorful Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 2\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"Grid Table 7 Colorful Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"Grid Table 7 Colorful Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"Grid Table 7 Colorful Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"Grid Table 7 Colorful Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"Grid Table 1 Light Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"Grid Table 2 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"Grid Table 3 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"Grid Table 4 Accent 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"Grid Table 5 Dark Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"Grid Table 6 Colorful Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"Grid Table 7 Colorful Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\" Name=\"List Table 1 Light\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\" Name=\"List Table 6 Colorful\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\" Name=\"List Table 7 Colorful\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"List Table 1 Light Accent 1\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"List Table 6 Colorful Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n");
		sb.append(
				"       Name=\"List Table 7 Colorful Accent 1\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"List Table 1 Light Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n");
		sb.append(
				"       Name=\"List Table 6 Colorful Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"List Table 7 Colorful Accent 2\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"List Table 1 Light Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 3\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"List Table 6 Colorful Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"List Table 7 Colorful Accent 3\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n");
		sb.append(
				"       Name=\"List Table 1 Light Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"List Table 6 Colorful Accent 4\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"List Table 7 Colorful Accent 4\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"List Table 1 Light Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 5\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"List Table 6 Colorful Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n       Name=\"List Table 7 Colorful Accent 5\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"46\"\r\n       Name=\"List Table 1 Light Accent 6\"/>\r\n");
		sb.append(
				"      <w:LsdException Locked=\"false\" Priority=\"47\" Name=\"List Table 2 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"48\" Name=\"List Table 3 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"49\" Name=\"List Table 4 Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"50\" Name=\"List Table 5 Dark Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"51\"\r\n       Name=\"List Table 6 Colorful Accent 6\"/>\r\n      <w:LsdException Locked=\"false\" Priority=\"52\"\r\n");
		sb.append(
				"       Name=\"List Table 7 Colorful Accent 6\"/>\r\n     </w:LatentStyles>\r\n    </xml><![endif]-->\r\n    <style>\r\n        <!--\r\n        /* Font Definitions */\r\n        @font-face {\r\n            font-family: \"Cambria Math\";\r\n            panose-1: 2 4 5 3 5 4 6 3 2 4;\r\n            mso-font-charset: 1;\r\n            mso-generic-font-family: roman;\r\n            mso-font-pitch: variable;\r\n            mso-font-signature: 0 0 0 0 0 0;\r\n        }\r\n\r\n        @font-face {\r\n            font-family: Calibri;\r\n");
		sb.append(
				"            panose-1: 2 15 5 2 2 2 4 3 2 4;\r\n            mso-font-charset: 0;\r\n            mso-generic-font-family: swiss;\r\n            mso-font-pitch: variable;\r\n            mso-font-signature: -536870145 1073786111 1 0 415 0;\r\n        }\r\n        /* Style Definitions */\r\n        p.MsoNormal, li.MsoNormal, div.MsoNormal {\r\n            mso-style-unhide: no;\r\n            mso-style-qformat: yes;\r\n            mso-style-parent: \"\";\r\n            margin: 0in;\r\n            margin-bottom: .0001pt;\r\n            mso-pagination: widow-orphan;\r\n            font-size: 11.0pt;\r\n            font-family: \"Calibri\",sans-serif;\r\n");
		sb.append(
				"            mso-fareast-font-family: Calibri;\r\n            mso-fareast-theme-font: minor-latin;\r\n            mso-bidi-font-family: \"Times New Roman\";\r\n        }\r\n\r\n        a:link, span.MsoHyperlink {\r\n            mso-style-noshow: yes;\r\n            mso-style-priority: 99;\r\n            color: #0563C1;\r\n            text-decoration: underline;\r\n            text-underline: single;\r\n        }\r\n\r\n        a:visited, span.MsoHyperlinkFollowed {\r\n");
		sb.append(
				"            mso-style-noshow: yes;\r\n            mso-style-priority: 99;\r\n            color: #954F72;\r\n            text-decoration: underline;\r\n            text-underline: single;\r\n        }\r\n\r\n        p.msonormal0, li.msonormal0, div.msonormal0 {\r\n            mso-style-name: msonormal;\r\n            mso-style-unhide: no;\r\n            mso-margin-top-alt: auto;\r\n            margin-right: 0in;\r\n            mso-margin-bottom-alt: auto;\r\n            margin-left: 0in;\r\n            mso-pagination: widow-orphan;\r\n            font-size: 12.0pt;\r\n            font-family: \"Times New Roman\",serif;\r\n");
		sb.append(
				"            mso-fareast-font-family: Calibri;\r\n            mso-fareast-theme-font: minor-latin;\r\n        }\r\n\r\n        span.EmailStyle18 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: windowtext;\r\n        }\r\n\r\n        span.EmailStyle19 {\r\n            mso-style-type: personal;\r\n");
		sb.append(
				"            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: #1F497D;\r\n        }\r\n\r\n        span.EmailStyle20 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: windowtext;\r\n        }\r\n");
		sb.append(
				"\r\n        span.EmailStyle21 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: #1F497D;\r\n        }\r\n\r\n        span.EmailStyle22 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n");
		sb.append(
				"            mso-hansi-font-family: Calibri;\r\n            color: windowtext;\r\n        }\r\n\r\n        span.EmailStyle23 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: #1F497D;\r\n        }\r\n\r\n        span.EmailStyle24 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n");
		sb.append(
				"            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-hansi-font-family: Calibri;\r\n            color: windowtext;\r\n        }\r\n\r\n        span.EmailStyle25 {\r\n            mso-style-type: personal;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            mso-ansi-font-size: 11.0pt;\r\n            mso-bidi-font-size: 11.0pt;\r\n            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-ascii-theme-font: minor-latin;\r\n            mso-fareast-font-family: Calibri;\r\n");
		sb.append(
				"            mso-fareast-theme-font: minor-latin;\r\n            mso-hansi-font-family: Calibri;\r\n            mso-hansi-theme-font: minor-latin;\r\n            mso-bidi-font-family: \"Times New Roman\";\r\n            mso-bidi-theme-font: minor-bidi;\r\n            color: #1F497D;\r\n        }\r\n\r\n        span.EmailStyle26 {\r\n            mso-style-type: personal-compose;\r\n            mso-style-noshow: yes;\r\n            mso-style-unhide: no;\r\n            mso-ansi-font-size: 11.0pt;\r\n            mso-bidi-font-size: 11.0pt;\r\n");
		sb.append(
				"            font-family: \"Calibri\",sans-serif;\r\n            mso-ascii-font-family: Calibri;\r\n            mso-ascii-theme-font: minor-latin;\r\n            mso-fareast-font-family: Calibri;\r\n            mso-fareast-theme-font: minor-latin;\r\n            mso-hansi-font-family: Calibri;\r\n            mso-hansi-theme-font: minor-latin;\r\n            mso-bidi-font-family: \"Times New Roman\";\r\n            mso-bidi-theme-font: minor-bidi;\r\n            color: windowtext;\r\n        }\r\n\r\n        .MsoChpDefault {\r\n            mso-style-type: export-only;\r\n");
		sb.append(
				"            mso-default-props: yes;\r\n            font-size: 10.0pt;\r\n            mso-ansi-font-size: 10.0pt;\r\n            mso-bidi-font-size: 10.0pt;\r\n        }\r\n\r\n        @page WordSection1 {\r\n            size: 8.5in 11.0in;\r\n            margin: 1.0in 1.0in 1.0in 1.0in;\r\n            mso-header-margin: .5in;\r\n            mso-footer-margin: .5in;\r\n            mso-paper-source: 0;\r\n        }\r\n\r\n        div.WordSection1 {\r\n            page: WordSection1;\r\n        }\r\n        -->\r\n    </style>\r\n    <!--[if gte mso 10]>\r\n    <style>\r\n");
		sb.append(
				"     /* Style Definitions */\r\n     table.MsoNormalTable\r\n        {mso-style-name:\"Table Normal\";\r\n        mso-tstyle-rowband-size:0;\r\n        mso-tstyle-colband-size:0;\r\n        mso-style-noshow:yes;\r\n        mso-style-priority:99;\r\n        mso-style-parent:\"\";\r\n        mso-padding-alt:0in 5.4pt 0in 5.4pt;\r\n        mso-para-margin:0in;\r\n");
		sb.append(
				"        mso-para-margin-bottom:.0001pt;\r\n        mso-pagination:widow-orphan;\r\n        font-size:10.0pt;\r\n        font-family:\"Times New Roman\",serif;}\r\n    table.MsoTableGrid\r\n        {mso-style-name:\"Table Grid\";\r\n        mso-tstyle-rowband-size:0;\r\n        mso-tstyle-colband-size:0;\r\n        mso-style-priority:39;\r\n        mso-style-unhide:no;\r\n        border:solid windowtext 1.0pt;\r\n        mso-border-alt:solid windowtext .5pt;\r\n");
		sb.append(
				"        mso-padding-alt:0in 5.4pt 0in 5.4pt;\r\n        mso-border-insideh:.5pt solid windowtext;\r\n        mso-border-insidev:.5pt solid windowtext;\r\n        mso-para-margin:0in;\r\n        mso-para-margin-bottom:.0001pt;\r\n        mso-pagination:widow-orphan;\r\n        font-size:10.0pt;\r\n        font-family:\"Times New Roman\",serif;}\r\n    </style>\r\n    <![endif]-->\r\n    <!--[if gte mso 9]><xml>\r\n     <o:shapedefaults v:ext=\"edit\" spidmax=\"1026\"/>\r\n    </xml><![endif]-->\r\n    <!--[if gte mso 9]><xml>\r\n    <o:shapelayout v:ext=\"edit\">\r\n");
		sb.append(
				"     <o:idmap v:ext=\"edit\" data=\"1\"/>\r\n    </o:shapelayout></xml><![endif]-->\r\n</head>\r\n\r\n<body bgcolor=\"#F2F2F2\" lang=EN-US link=\"#0563C1\" vlink=\"#954F72\"\r\n      style='tab-interval:.5in'>\r\n\r\n    <div class=WordSection1>\r\n\r\n        <div align=center>\r\n\r\n            <table class=MsoNormalTable border=0 cellspacing=0 cellpadding=0\r\n                   style='border-collapse:collapse;mso-yfti-tbllook:1184;mso-padding-alt:0in 0in 0in 0in'>\r\n");
		sb.append(
				"                <tr style='mso-yfti-irow:0;mso-yfti-firstrow:yes'>\r\n                    <td width=800 valign=top style='width:579.7pt;padding:0in 0in 0in 0in'>\r\n                        <p class=MsoNormal>\r\n                            <a name=\"_MailOriginal\">\r\n                                <span style='mso-no-proof:yes'>\r\n                                    <![if !vml]><img width=800 height=40\r\n");
		sb.append(
				"                                                     src=\"cid:Image\"><![endif]>\r\n                                </span><o:p></o:p>\r\n                            </a>\r\n                        </p>\r\n                    </td>\r\n                    <span style='mso-bookmark:_MailOriginal'></span>\r\n                </tr>\r\n                BodyTemplate\r\n                <span style='mso-bookmark:_MailOriginal'></span></td>\r\n");
		sb.append(
				"                <span style='mso-bookmark:_MailOriginal'></span>\r\n                </tr>\r\n                <tr style='mso-yfti-irow:2;mso-yfti-lastrow:yes'>\r\n                    <td width=800 valign=top style='width:604.5pt;background:#D9D9D9;mso-background-themecolor:\r\n  background1;mso-background-themeshade:217;padding:0in 0in 0in 0in'>\r\n                        <p class=MsoNormal align=center style='text-align:center'>\r\n                            <span style='mso-bookmark:_MailOriginal'>\r\n");
		sb.append(
				"                                <span style='font-size:10.0pt'>\r\n                                    &copy\r\n                                    footerYear, Education Training and Assessment/*, Infosys Ltd*/.\r\n                                </span><o:p></o:p>\r\n                            </span>\r\n                        </p>\r\n                    </td>\r\n                    <span style='mso-bookmark:_MailOriginal'></span>\r\n                </tr>\r\n");
		sb.append(
				"            </table>\r\n\r\n        </div>\r\n\r\n        <span style='mso-bookmark:_MailOriginal'></span>\r\n\r\n        <p class=MsoNormal align=center style='text-align:center'><o:p>&nbsp;</o:p></p>\r\n\r\n    </div>\r\n\r\n</body>\r\n\r\n</html>\r\n");
		return sb.toString();
	}
}
