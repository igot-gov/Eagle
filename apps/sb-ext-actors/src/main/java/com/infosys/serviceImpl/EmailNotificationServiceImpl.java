/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectLogger;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.models.util.PropertiesCache;
import org.sunbird.helper.ServiceFactory;

import com.infosys.elastic.helper.ConnectionManager;
import com.infosys.service.EmailNotificationService;
import com.infosys.service.UserUtilityService;
import com.infosys.util.LexJsonKey;
import com.infosys.util.Templates;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

	@Autowired
	UserUtilityService userUtilService;

	SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private PropertiesCache properties = PropertiesCache.getInstance();
	private String SMTPHOST;
	private String SMTPPORT;
	private String bodhiKeyspace = LexJsonKey.BODHI_DB_KEYSPACE;
	private String shareTable = properties.getProperty(LexJsonKey.SHARED_GOALS_TRACKER);
	private CassandraOperation cassandraOperation = ServiceFactory.getInstance();
	private String sunbirdKeyspace = "sunbird";
	private String userTable = "user";

	@Async
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public Map<String, Object> Notify(Map<String, Object> data) {
		SMTPHOST = System.getenv(LexJsonKey.SMTP_HOST);
		SMTPPORT = System.getenv(LexJsonKey.SMTP_PORT);

		if (ProjectUtil.isStringNullOREmpty(SMTPHOST) || ProjectUtil.isStringNullOREmpty(SMTPPORT)) {
			ProjectLogger.log("SMTP config is not coming form System variable.");
			SMTPHOST = properties.getProperty(LexJsonKey.SMTP_HOST);
			SMTPPORT = properties.getProperty(LexJsonKey.SMTP_PORT);
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		String msg = "Success";
		String sharedByEmail = "";
		String sharedByName = "";

		String toList = data.get("to_list").toString();
		String ccList = data.get("cc_list").toString();
		String bccList = data.get("bcc_list").toString();
		String sender = data.get("sender").toString();
		String senderApplicationName = data.get("sender_application_name").toString();
		String bodyText = "";

		if (data.containsKey("body")) {
			Map<String, Object> body = (Map<String, Object>) data.get("body");
			if (body.containsKey("text")) {
				bodyText = body.get("text").toString();
			}
		}

		for (Map<String, Object> tempSharedBy : (List<Map<String, Object>>) data.get("sharedBy")) {
			if (tempSharedBy.containsKey("email")) {
				sharedByEmail = tempSharedBy.get("email").toString();
			}
			if (tempSharedBy.containsKey("name")) {
				sharedByName = tempSharedBy.get("name").toString();
			}
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHOST);
		props.put("mail.smtp.port", SMTPPORT);

		Session session = Session.getDefaultInstance(props, null);

		try {
			Multipart multipart = new MimeMultipart();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender, senderApplicationName + " Platform"));
			if (toList.length() > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(toList.replaceAll("ad.infosys", "infosys")));
			else
				throw new Exception("No valid Ids");

			if (ccList.length() > 0)
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(ccList.replaceAll("ad.infosys", "infosys")));

			if (bccList.length() > 0)
				message.setRecipients(Message.RecipientType.BCC,
						InternetAddress.parse(bccList.replaceAll("ad.infosys", "infosys")));

			if (data.containsKey("emailType") && data.get("emailType").toString().toLowerCase().equals("query")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject("Need help");

				messageBodyPart.setContent(
						Templates.QueryTemplate(bodyText.replaceAll("\n", "</br>"),
								((List<Map<String, Object>>) data.get("artifacts")).get(0), senderApplicationName),
						"text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else if (data.containsKey("emailType")
					&& data.get("emailType").toString().toLowerCase().equals("share")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject(
						"Check out " + ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title") + " on "
								+ senderApplicationName);

				messageBodyPart
						.setContent(Templates.ShareTemplate(((List<Map<String, Object>>) data.get("artifacts")).get(0),
								sharedByName, bodyText), "text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else if (data.containsKey("emailType")
					&& data.get("emailType").toString().toLowerCase().equals("attachment")) {
				message.setSubject(((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString()
						+ " from " + senderApplicationName);

				if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl").toString()
						.toLowerCase().matches("^http(s){0,1}://(www\\.){0,1}youtu(\\.be|be.com).*")) {
					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setContent(
							Templates.DownloadTemplate("Please view the requested content at <a href='"
									+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("url")
									+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
									+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title")
									+ "</span></b></a>.<br/><br/>Click <a href='"
									+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl")
									+ "'><b style='mso-bidi-font-weight:normal'><spanstyle='font-size:16px;color:#3F51B5'>here</span></b></a> to view this on youtube.",
									((List<Map<String, Object>>) data.get("artifacts")).get(0), "0"),
							"text/html; charset=utf-8");
					multipart.addBodyPart(messageBodyPart);

				} else if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("downloadUrl").toString()
						.toLowerCase().contains(".pdf")
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("downloadUrl").toString()
								.toLowerCase().contains(".mp4")
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("downloadUrl").toString()
								.toLowerCase().contains(".mp3")) {

					HttpResponse file = userUtilService.getFileForEmail(
							((List<Map<String, Object>>) data.get("artifacts")).get(0).get("downloadUrl").toString());

					if (Integer.parseInt(((List<Map<String, Object>>) data.get("artifacts")).get(0).get("size")
							.toString()) >= 10000000) {
						BodyPart messageBodyPart = new MimeBodyPart();
						messageBodyPart.setContent(Templates.DownloadTemplate(
								"The file can't be sent over email as it is bigger than 10MB. To view the content please go to <a href='"
										+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("url")
										+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
										+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title")
										+ "</span></b></a>.<br/> <br/> P.S. For getting the content regardless the size restriction please drop a mail to "
										+ sender,
								((List<Map<String, Object>>) data.get("artifacts")).get(0), "0"),
								"text/html; charset=utf-8");
						multipart.addBodyPart(messageBodyPart);

					} else if (file == null || file.getStatusLine().getStatusCode() == 404) {
						BodyPart messageBodyPart = new MimeBodyPart();
						messageBodyPart.setContent(Templates.DownloadTemplate(
								"The content requested is not downloadable. To view the content please go to <a href='"
										+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("url")
										+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
										+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title")
										+ "</span></b></a>",
								(((List<Map<String, Object>>) data.get("artifacts")).get(0)), "0"),
								"text/html; charset=utf-8");
						multipart.addBodyPart(messageBodyPart);

					} else {
						BodyPart messageBodyPart = new MimeBodyPart();
						messageBodyPart.setContent(
								Templates.DownloadTemplate(bodyText,
										((List<Map<String, Object>>) data.get("artifacts")).get(0), "1"),
								"text/html; charset=utf-8");
						try {

							MimeBodyPart attachmentPart = new MimeBodyPart();
							DataSource ds = new ByteArrayDataSource(
									IOUtils.toByteArray(new BufferedInputStream(file.getEntity().getContent())),
									"application/octet-stream");
							attachmentPart.setDataHandler(new DataHandler(ds));
							attachmentPart.setFileName(((List<Map<String, Object>>) data.get("artifacts")).get(0)
									.get("downloadUrl").toString().split("/")[6].split("\\?")[0]);
							multipart.addBodyPart(attachmentPart);

						} catch (Exception e) {
							msg = "failure in adding the attachment";
							messageBodyPart.setContent(Templates.DownloadTemplate(
									"The content requested is not downloadable. To view the content please go to <a href='"
											+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("url")
											+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
											+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title")
											+ "</span></b></a>",
									((List<Map<String, Object>>) data.get("artifacts")).get(0), "0"),
									"text/html; charset=utf-8");
						}
						multipart.addBodyPart(messageBodyPart);
					}
				} else {
					BodyPart messageBodyPart = new MimeBodyPart();
					message.setSubject(
							((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString()
									+ " from " + senderApplicationName);

					messageBodyPart.setContent(Templates.DownloadTemplate(
							"The content requested is not downloadable. To view the content please go to <a href='"
									+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("url")
									+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:16px;color:#3F51B5'>"
									+ ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title")
									+ "</span></b></a>",
							((List<Map<String, Object>>) data.get("artifacts")).get(0), "0"),
							"text/html; charset=utf-8");
					multipart.addBodyPart(messageBodyPart);

				}
			} else if (data.containsKey("emailType")
					&& data.get("emailType").toString().toLowerCase().equals("internal-certification")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject("Internal certification registration process on " + senderApplicationName);
				String title = "";
				String url = "";

				if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString() == ""
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl")
								.toString() == "") {
					if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("identifier") != null) {
						Map<String, Object> edata = this
								.getMetaFromElasticSearch(((List<Map<String, Object>>) data.get("artifacts")).get(0)
										.get("identifier").toString());

						if (edata == null) {
							throw new Exception("Invalid Certification ID");
						}
						title = edata.get("name").toString();
						url = edata.get("artifactUrl").toString();
					} else {
						throw new Exception("Missing Certification ID");
					}
				} else {
					title = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString();
					url = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl").toString();
				}
				// System.out.println(Templates.CertificateTemplate(title,url));
				messageBodyPart.setContent(Templates.CertificateTemplate(title, url), "text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else if (data.containsKey("emailType")
					&& data.get("emailType").toString().toLowerCase().equals("external-certification")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject("External certification registration process on " + senderApplicationName);
				String title = "";
				String url = "";

				if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString() == ""
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl")
								.toString() == "") {
					if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("identifier") != null) {
						Map<String, Object> edata = this
								.getMetaFromElasticSearch(((List<Map<String, Object>>) data.get("artifacts")).get(0)
										.get("identifier").toString());

						if (edata == null) {
							throw new Exception("Invalid Certification ID");
						}
						title = edata.get("name").toString();
						url = edata.get("artifactUrl").toString();
					} else {
						throw new Exception("Missing Certification ID");
					}
				} else {
					title = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString();
					url = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl").toString();
				}
				// System.out.println(Templates.ExternalCertificateTemplate(title,certUrl));

				messageBodyPart.setContent(Templates.ExternalCertificateTemplate(title, url),
						"text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else if (data.containsKey("emailType")
					&& data.get("emailType").toString().toLowerCase().equals("internal-classroom-training")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject("Internal classroom course registration process on " + senderApplicationName);
				String title = "";
				String url = "";

				if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString() == ""
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl") == null
						|| ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl")
								.toString() == "") {
					if (((List<Map<String, Object>>) data.get("artifacts")).get(0).get("identifier") != null) {
						Map<String, Object> edata = this
								.getMetaFromElasticSearch(((List<Map<String, Object>>) data.get("artifacts")).get(0)
										.get("identifier").toString());

						if (edata == null) {
							throw new Exception("Invalid Training ID");
						}
						title = edata.get("name").toString();
						url = edata.get("artifactUrl").toString();
					} else {
						throw new Exception("Missing Training ID");
					}
				} else {
					title = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("title").toString();
					url = ((List<Map<String, Object>>) data.get("artifacts")).get(0).get("artifactUrl").toString();
				}
				// System.out.println(Templates.InternalTrainingTemplate(url));
				messageBodyPart.setContent(Templates.InternalTrainingTemplate(url), "text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else {
				throw new Exception("Invalid Email Type");
			}
			message.setContent(multipart);

			MimeBodyPart imageBodyPart = new MimeBodyPart();
			DataSource fds = new FileDataSource(userUtilService.getImageFromContentStore());
			// DataSource fds = new FileDataSource(
			// this.streamToFile(resourceLoader.getResource("classpath:LexHeader.png").getInputStream()));
			imageBodyPart.setDataHandler(new DataHandler(fds));
			imageBodyPart.setContentID("<Image>");
			imageBodyPart.setFileName("LEXLOGO.png");
			// imageBodyPart.setHeader("Content-ID", "<Image>");
			imageBodyPart.setDisposition(MimeBodyPart.INLINE);
			multipart.addBodyPart(imageBodyPart);

			Transport.send(message);

		} catch (Exception e) {
			msg = e.getMessage();
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}

		ret.put("message", msg);
		return ret;
	}

	private Map<String, Object> getMetaFromElasticSearch(String id) {
		Map<String, Object> source = null;
		try {
			SearchResponse response = ConnectionManager
					.getClient().search(
							new SearchRequest().indices("lexcontentindex").types("resource")
									.searchType(
											SearchType.QUERY_THEN_FETCH)
									.source(new SearchSourceBuilder()
											.query(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_id", id)))
											.fetchSource(new String[] { "identifier", "name", "certificationUrl",
													"artifactUrl" }, new String[0])
											.size(1)),
							RequestOptions.DEFAULT);

			for (SearchHit hit : response.getHits()) {
				source = hit.getSourceAsMap();
			}
		} catch (Exception e) {
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return source;
	}

	@Async
	@SuppressWarnings({ "unchecked" })
	@Override
	public Map<String, Object> NotifyGroup(Map<String, Object> data) {
		ProjectLogger.log("Info : " + "Group Email Started");
		SMTPHOST = System.getenv(LexJsonKey.SMTP_HOST);
		SMTPPORT = System.getenv(LexJsonKey.SMTP_PORT);

		if (ProjectUtil.isStringNullOREmpty(SMTPHOST) || ProjectUtil.isStringNullOREmpty(SMTPPORT)) {
			ProjectLogger.log("SMTP config is not coming form System variable.");
			SMTPHOST = properties.getProperty(LexJsonKey.SMTP_HOST);
			SMTPPORT = properties.getProperty(LexJsonKey.SMTP_PORT);
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		String msg = "Success";
		String sharedByEmail = "";
		String sharedByName = "";

		String toList = data.get("to_list").toString();
		String ccList = data.get("cc_list").toString();
		String bccList = data.get("bcc_list").toString();
		String sender = data.get("sender").toString();
		String senderApplicationName = data.get("sender_application_name").toString();
		String bodyText = "";
		for (Map<String, Object> tempSharedBy : (List<Map<String, Object>>) data.get("sharedBy")) {
			if (tempSharedBy.containsKey("email")) {
				sharedByEmail = tempSharedBy.get("email").toString();
			}
			if (tempSharedBy.containsKey("name")) {
				sharedByName = tempSharedBy.get("name").toString();
			}
		}

		if (data.containsKey("body")) {
			Map<String, Object> body = (Map<String, Object>) data.get("body");
			if (body.containsKey("text")) {
				bodyText = body.get("text").toString();
			}
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHOST);
		props.put("mail.smtp.port", SMTPPORT);

		Session session = Session.getDefaultInstance(props, null);

		// get data from elastic
		String tableContent = getContentFromElasticSearch(
				(ArrayList<String>) ((List<Map<String, Object>>) data.get("artifact")).get(0).get("content"));
		// get title text
		String title = this.getTitleText(((List<Map<String, Object>>) data.get("artifact")).get(0));

		for (String to : toList.split(",")) {

			try {
				Multipart multipart = new MimeMultipart();
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(sender, senderApplicationName + " Platform"));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(to.replaceAll("ad.infosys", "infosys")));

				if (ccList.length() > 0)
					message.setRecipients(Message.RecipientType.CC,
							InternetAddress.parse(ccList.replaceAll("ad.infosys", "infosys")));

				if (bccList.length() > 0)
					message.setRecipients(Message.RecipientType.BCC,
							InternetAddress.parse(bccList.replaceAll("ad.infosys", "infosys")));

				if (data.containsKey("emailType")
						&& data.get("emailType").toString().toLowerCase().equals("remindgoal")) {
					BodyPart messageBodyPart = new MimeBodyPart();
					message.setSubject("Reminder for this goal on " + senderApplicationName);
					List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
					try {
						Map<String, Object> propertyMap = new HashMap<String, Object>();
						propertyMap.put("shared_by", sharedByEmail);
						propertyMap.put("goal_id", UUID.fromString(((List<Map<String, Object>>) data.get("artifact"))
								.get(0).get("identifier").toString()));
						propertyMap.put("shared_with", to);
						result = (List<Map<String, Object>>) userUtilService
								.getRecordsByProperties(bodhiKeyspace, shareTable, propertyMap).getResult()
								.get("response");
					} catch (Exception e) {
						result = new ArrayList<Map<String, Object>>();
						ProjectLogger.log("Error : " + e.getMessage(), e);
					}

					if (result.size() > 0) {
						if (result.get(0).get("goal_end_date") != null) {
							title += "<font size=2>To be completed by "
									+ formatter.format(result.get(0).get("goal_end_date")) + "</font></br>";
						}

						int diffInDays = (int) ((new Date().getTime()
								- ((Date) result.get(0).get("last_updated_on")).getTime()) / (1000 * 60 * 60 * 24));
						String temp = "";
						if (diffInDays == 1) {
							temp = "has sent you a reminder to accept this goal shared on " + senderApplicationName
									+ " 1 day ago.";
						} else if (diffInDays > 1) {
							temp = "has sent you a reminder to accept this goal shared on " + senderApplicationName
									+ " " + diffInDays + " days ago.";
						} else {
							temp = "has sent you a reminder to accept this goal on " + senderApplicationName + ".";
						}

						messageBodyPart
								.setContent(
										Templates.ShareTemplateGP(to.split("@")[0], title, sharedByName, bodyText,
												tableContent, temp, this.getURL(data.get("appURL").toString())),
										"text/html; charset=utf-8");
					} else {
						messageBodyPart.setContent(Templates.ShareTemplateGP(to.split("@")[0], title, sharedByName,
								bodyText, tableContent, "has sent you a reminder to accept this goal",
								this.getURL(data.get("appURL").toString())), "text/html; charset=utf-8");
					}
					multipart.addBodyPart(messageBodyPart);

				} else if (data.containsKey("emailType")
						&& data.get("emailType").toString().toLowerCase().equals("sharegoal")) {
					BodyPart messageBodyPart = new MimeBodyPart();
					message.setSubject("Check out this goal on " + senderApplicationName);
					messageBodyPart.setContent(
							Templates.ShareTemplateGP(to.split("@")[0], title, sharedByName, bodyText, tableContent,
									"has shared a goal with you!", this.getURL(data.get("appURL").toString())),
							"text/html; charset=utf-8");
					multipart.addBodyPart(messageBodyPart);

				} else if (data.containsKey("emailType")
						&& data.get("emailType").toString().toLowerCase().equals("shareplaylist")) {
					BodyPart messageBodyPart = new MimeBodyPart();
					message.setSubject("Check out this playlist on " + senderApplicationName);

					messageBodyPart.setContent(
							Templates.ShareTemplateGP(to.split("@")[0], title, sharedByName, bodyText, tableContent,
									"has shared a playlist with you!", this.getURL(data.get("appURL").toString())),
							"text/html; charset=utf-8");
					multipart.addBodyPart(messageBodyPart);

				} else {
					msg = "Invalid Request Data";
					ProjectLogger.log("EmailError : " + msg);
					break;
				}

				message.setContent(multipart);

				MimeBodyPart imageBodyPart = new MimeBodyPart();
				DataSource fds = new FileDataSource(userUtilService.getImageFromContentStore());
				imageBodyPart.setDataHandler(new DataHandler(fds));
				imageBodyPart.setContentID("<Image>");
				imageBodyPart.setFileName("LEXLOGO.png");
				// imageBodyPart.setHeader("Content-ID", "<Image>");
				imageBodyPart.setDisposition(MimeBodyPart.INLINE);
				multipart.addBodyPart(imageBodyPart);

				Transport.send(message);

			} catch (Exception e) {
				msg = e.getMessage();
				ProjectLogger.log("EmailError : " + e.getMessage(), e);
			}
		}
		ret.put("message", msg);
		return ret;
	}

	private String getContentFromElasticSearch(ArrayList<String> ids) {
		String contentTable = "";
		try {

			SearchResponse response = ConnectionManager
					.getClient().search(
							new SearchRequest().indices("lexcontentindex").types("resource")
									.searchType(SearchType.QUERY_THEN_FETCH)
									.source(new SearchSourceBuilder()
											.query(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("_id", ids)))
											.fetchSource(new String[] { "identifier", "name", "contentType",
													"creatorDetails" }, new String[0])
											.size(ids.size())),
							RequestOptions.DEFAULT);

			contentTable = "<table style='width:100%; text-align:left; border-collapse: collapse;'><tr style='border-bottom: 1px silver solid;'><td><font size=4>Content Type</font><td/><td><font size=4>Title</font><td/></tr>";
			String program = "";
			String course = "";
			String lModule = "";
			String resource = "";
			for (SearchHit hit : response.getHits()) {
				Map<String, Object> source = hit.getSourceAsMap();
				switch (source.get("contentType").toString().toLowerCase()) {
				case "learning path":
					program += "<tr style='text-align:left;'><td>Program<td/><td>" + source.get("name").toString()
							+ "<td/></tr>";
					break;
				case "course":
					course += "<tr style='text-align:left;'><td>Course<td/><td>" + source.get("name").toString()
							+ "<td/></tr>";
					break;
				case "collection":
					lModule += "<tr style='text-align:left;'><td>Learning Module<td/><td>"
							+ source.get("name").toString() + "<td/></tr>";
					break;
				case "resource":
					resource += "<tr style='text-align:left;'><td>Resource<td/><td>" + source.get("name").toString()
							+ "<td/></tr>";
					break;
				default:
					resource += "<tr style='text-align:left;'><td>Resource<td/><td>" + source.get("name").toString()
							+ "<td/></tr>";
					break;
				}
			}
			contentTable += program + course + lModule + resource + "</table>";
		} catch (Exception e) {
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return contentTable;
	}

	private String getTitleText(Map<String, Object> artifact) {
		String title = "";
		try {
			title += artifact.get("title").toString() + "</br>";

			if (artifact.get("description") != null) {
				title += "<font size=3>" + artifact.get("description").toString() + "</font></br>";
			}
		} catch (Exception e) {
			ProjectLogger.log("Error : " + e.getMessage(), e);
		}
		return title;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> PlainMail(Map<String, Object> data) {
		SMTPHOST = System.getenv(LexJsonKey.SMTP_HOST);
		SMTPPORT = System.getenv(LexJsonKey.SMTP_PORT);

		if (ProjectUtil.isStringNullOREmpty(SMTPHOST) || ProjectUtil.isStringNullOREmpty(SMTPPORT)) {
			ProjectLogger.log("SMTP config is not coming form System variable.");
			SMTPHOST = properties.getProperty(LexJsonKey.SMTP_HOST);
			SMTPPORT = properties.getProperty(LexJsonKey.SMTP_PORT);
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		String msg = "Success";
		List<String> invalidIds = new ArrayList<String>();
		String valid = this.validatePlainMail(data);
		if (valid.equals("valid")) {
			String toList = "";
			String ccList = "";
			String bccList = "";
			String bodyText = "";

			Map<String, Object> mailData = userUtilService.getMailData();
			List<String> domains = new ArrayList<>(Arrays.asList(mailData.get("domains").toString().split(",")));
			String sender = mailData.get("senderId").toString();
			String senderApplicationName = mailData.get("senderName").toString();

			for (Map<String, Object> tempTo : (List<Map<String, Object>>) data.get("emailTo")) {
				String toEmailId = tempTo.get("email").toString().contains("@") ? tempTo.get("email").toString()
						: tempTo.get("email").toString() + "@ad.infosys.com";
				if (!domains.contains("@" + toEmailId.split("@")[1])) {
					invalidIds.add(toEmailId);
					continue;
				}
				toList += toEmailId;
				toList += ",";
			}
			if (!toList.isEmpty()) {
				toList = toList.substring(0, toList.length() - 1);
			}

			toList = toList.replaceAll("", "");
			if (!toList.equals("")) {
				Map<String, Object> toIds = userUtilService.verifyUsers(Arrays.asList(toList.split(",")));
				toList = String.join(",", ((List<String>) toIds.get("valid_users")));
				invalidIds.addAll((List<String>) toIds.get("invalid_users"));
			}

			if (data.containsKey("ccTo"))
				for (Map<String, Object> ccTo : (List<Map<String, Object>>) data.get("ccTo")) {
					String ccEmailId = ccTo.get("email").toString().contains("@") ? ccTo.get("email").toString()
							: ccTo.get("email").toString() + "@ad.infosys.com";
					if (!domains.contains("@" + ccEmailId.split("@")[1])) {
						invalidIds.add(ccEmailId);
						continue;
					}
					ccList += ccEmailId;
					ccList += ",";
				}
			if (!ccList.isEmpty()) {
				ccList = ccList.substring(0, ccList.length() - 1);
			}

			ccList = ccList.replaceAll("@infosys", "");

			if (!ccList.equals("")) {
				Map<String, Object> toIds = userUtilService.verifyUsers(Arrays.asList(ccList.split(",")));
				ccList = String.join(",", ((List<String>) toIds.get("valid_users")));
				invalidIds.addAll((List<String>) toIds.get("invalid_users"));
			}

			if (data.containsKey("bccTo"))
				for (Map<String, Object> bccTo : (List<Map<String, Object>>) data.get("bccTo")) {
					String bccEmailId = bccTo.get("email").toString();
					bccList += bccEmailId;
					bccList += ",";
				}
			if (!bccList.isEmpty()) {
				bccList = bccList.substring(0, bccList.length() - 1);
			}

			if (data.containsKey("body")) {
				Map<String, Object> body = (Map<String, Object>) data.get("body");
				if (body.containsKey("text")) {
					bodyText = body.get("text").toString();
				}
			}

			Properties props = new Properties();
			props.put("mail.smtp.host", SMTPHOST);
			props.put("mail.smtp.port", SMTPPORT);

			Session session = Session.getDefaultInstance(props, null);

			try {
				Multipart multipart = new MimeMultipart();
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(sender, senderApplicationName + " Platform"));
				if (toList.length() > 0)
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(toList.replaceAll("ad.infosys", "infosys")));
				else
					throw new Exception("No valid Ids");

				if (ccList.length() > 0)
					message.setRecipients(Message.RecipientType.CC,
							InternetAddress.parse(ccList.replaceAll("ad.infosys", "infosys")));

				if (bccList.length() > 0)
					message.setRecipients(Message.RecipientType.BCC,
							InternetAddress.parse(bccList.replaceAll("ad.infosys", "infosys")));

				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject(data.get("subject").toString());

				messageBodyPart.setContent(bodyText.replaceAll("\n", "</br>"), "text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

				message.setContent(multipart);
				Transport.send(message);

			} catch (Exception e) {
				msg = e.getMessage();
				ProjectLogger.log("EmailError : " + e.getMessage(), e);
				System.out.println(e.getMessage());
			}
		} else {
			msg = valid;
		}
		ret.put("message", msg);
		ret.put("invalidIds", invalidIds);
		return ret;
	}

	@Async
	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public Map<String, Object> NotifyReview(Map<String, Object> data) {
		SMTPHOST = System.getenv(LexJsonKey.SMTP_HOST);
		SMTPPORT = System.getenv(LexJsonKey.SMTP_PORT);

		if (ProjectUtil.isStringNullOREmpty(SMTPHOST) || ProjectUtil.isStringNullOREmpty(SMTPPORT)) {
			ProjectLogger.log("SMTP config is not coming form System variable.");
			SMTPHOST = properties.getProperty(LexJsonKey.SMTP_HOST);
			SMTPPORT = properties.getProperty(LexJsonKey.SMTP_PORT);
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		String msg = "Success";
		String sharedByEmail = "";
		String sharedByName = "";

		String toList = data.get("to_list").toString();
		String ccList = data.get("cc_list").toString();
		String bccList = data.get("bcc_list").toString();
		String sender = data.get("sender").toString();
		String senderApplicationName = data.get("sender_application_name").toString();
		String bodyText = "";
		String lastLine = "";

		if (data.containsKey("body")) {
			Map<String, Object> body = (Map<String, Object>) data.get("body");
			if (body.containsKey("text")) {
				bodyText = body.get("text").toString();
			}
			if (body.containsKey("lastLine")) {
				lastLine = body.get("lastLine").toString();
			}
		}
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHOST);
		props.put("mail.smtp.port", SMTPPORT);

		Session session = Session.getDefaultInstance(props, null);

		try {
			Multipart multipart = new MimeMultipart();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender, senderApplicationName + " Platform"));
			if (toList.length() > 0)
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(toList.replaceAll("ad.infosys", "infosys")));
			else
				throw new Exception("No valid Ids");

			if (ccList.length() > 0)
				message.setRecipients(Message.RecipientType.CC,
						InternetAddress.parse(ccList.replaceAll("ad.infosys", "infosys")));

			if (data.containsKey("emailType")) {
				BodyPart messageBodyPart = new MimeBodyPart();
				message.setSubject(data.get("subject").toString());

				messageBodyPart.setContent(
						Templates.SendForReview(((Map<String, Object>) data.get("artifact")),
								bodyText.replaceAll("\n", "</br>"), lastLine.replaceAll("\n", "</br>"),
								data.get("emailType").toString().toLowerCase(), senderApplicationName),
						"text/html; charset=utf-8");
				multipart.addBodyPart(messageBodyPart);

			} else {
				throw new Exception("Invalid Email Type");
			}
			message.setContent(multipart);

			MimeBodyPart imageBodyPart = new MimeBodyPart();

			DataSource fds = new FileDataSource(userUtilService.getImageFromContentStore());

			imageBodyPart.setDataHandler(new DataHandler(fds));
			imageBodyPart.setContentID("<Image>");
			imageBodyPart.setFileName("LEXLOGO.png");
			// imageBodyPart.setHeader("Content-ID", "<Image>");
			imageBodyPart.setDisposition(MimeBodyPart.INLINE);
			multipart.addBodyPart(imageBodyPart);

			Transport.send(message);

		} catch (Exception e) {
			msg = e.getMessage();
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}
		return ret;
	}

	private String getURL(String url) {
		return "<a href='" + url
				+ "'><b style='mso-bidi-font-weight:normal'><span style='font-size:20px;color:#3F51B5'>CLICK HERE TO ACCEPT</span></b></a>";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void VerifyForGroup(Map<String, Object> data) {
		try {
			if (this.validateContent(data).equals("valid")) {
				String toList = "";
				String ccList = "";
				String bccList = "";
				Set<String> invalidIds = new HashSet<String>();
				Map<String, Object> mailData = userUtilService.getMailData();
				List<String> domains = new ArrayList<>(Arrays.asList(mailData.get("domains").toString().split(",")));
				for (Map<String, Object> tempTo : (List<Map<String, Object>>) data.get("emailTo")) {
					String toEmailId = tempTo.get("email").toString().contains("@") ? tempTo.get("email").toString()
							: tempTo.get("email").toString() + "";
					if (!domains.contains("@" + toEmailId.split("@")[1])) {
						invalidIds.add(toEmailId);
						continue;
					}
					toList += toEmailId;
					toList += ",";
				}
				if (!toList.isEmpty()) {
					toList = toList.substring(0, toList.length() - 1);
				}

				if (data.containsKey("ccTo"))
					for (Map<String, Object> ccTo : (List<Map<String, Object>>) data.get("ccTo")) {
						String ccEmailId = ccTo.get("email").toString();
						ccList += ccEmailId;
						ccList += ",";
					}
				if (!ccList.isEmpty()) {
					ccList = ccList.substring(0, ccList.length() - 1);
				}

				if (data.containsKey("bccTo"))
					for (Map<String, Object> bccTo : (List<Map<String, Object>>) data.get("bccTo")) {
						String bccEmailId = bccTo.get("email").toString();
						bccList += bccEmailId;
						bccList += ",";
					}
				if (!bccList.isEmpty()) {
					bccList = bccList.substring(0, bccList.length() - 1);
				}

				List<String> temp = new ArrayList<>(Arrays.asList(toList.split(",")));
				temp.removeAll(invalidIds);
				toList = String.join(",", temp);

				temp = new ArrayList<>(Arrays.asList(ccList.split(",")));
				temp.removeAll(invalidIds);
				ccList = String.join(",", temp);

				temp = new ArrayList<>(Arrays.asList(bccList.split(",")));
				temp.removeAll(invalidIds);
				bccList = String.join(",", temp);

				data.put("to_list", toList);
				data.put("cc_list", ccList);
				data.put("bcc_list", bccList);
				data.put("invalid_ids", invalidIds);
				data.put("sender", mailData.get("senderId").toString());
				data.put("sender_application_name", mailData.get("senderName").toString());
				data.put("message", "Request Accepted!");
				data.remove("emailTo");
				data.remove("ccTo");
				data.remove("bccTo");
			} else {
				data.put("message", "Invalid Request Data!");
				data.put("invalid_ids", new HashSet<String>());
			}
		} catch (Exception e) {
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}
	}

	@Override
	public void VerifyForReview(Map<String, Object> data) {
		try {
			if (this.ValidateReview(data).equals("valid")) {
				this.validateUserIds(data);
			} else {
				data.put("message", "Invalid Request Data!");
				data.put("invalid_ids", new HashSet<String>());
			}
		} catch (Exception e) {
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}
	}

	@Override
	public void VerifyForOneMail(Map<String, Object> data) {
		try {
			if (this.Validate(data).equals("valid")) {
				this.validateUserIds(data);
			} else {
				data.put("message", "Invalid Request Data!");
				data.put("invalid_ids", new HashSet<String>());
			}
		} catch (Exception e) {
			ProjectLogger.log("EmailError : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateUserIds(Map<String, Object> data) {
		String toList = "";
		String ccList = "";
		String bccList = "";
		Set<String> invalidIds = new HashSet<String>();
		Map<String, Object> mailData = userUtilService.getMailData();
		List<String> domains = new ArrayList<>(Arrays.asList(mailData.get("domains").toString().split(",")));
		for (Map<String, Object> tempTo : (List<Map<String, Object>>) data.get("emailTo")) {
			String toEmailId = tempTo.get("email").toString().contains("@") ? tempTo.get("email").toString()
					: tempTo.get("email").toString() + "";
			if (!domains.contains("@" + toEmailId.split("@")[1])) {
				invalidIds.add(toEmailId);
				continue;
			}
			toList += toEmailId;
			toList += ",";
		}
		if (!toList.isEmpty()) {
			toList = toList.substring(0, toList.length() - 1);
		}

		if (data.containsKey("ccTo"))
			for (Map<String, Object> ccTo : (List<Map<String, Object>>) data.get("ccTo")) {
				String ccEmailId = ccTo.get("email").toString();
				ccList += ccEmailId;
				ccList += ",";
			}
		if (!ccList.isEmpty()) {
			ccList = ccList.substring(0, ccList.length() - 1);
		}

		if (data.containsKey("bccTo"))
			for (Map<String, Object> bccTo : (List<Map<String, Object>>) data.get("bccTo")) {
				String bccEmailId = bccTo.get("email").toString();
				bccList += bccEmailId;
				bccList += ",";
			}
		if (!bccList.isEmpty()) {
			bccList = bccList.substring(0, bccList.length() - 1);
		}

		String verifyIds = (toList.equals("") ? "" : toList.replaceAll("@infosys", "@ad.infosys"))
				+ (ccList.equals("") ? ""
						: (toList.equals("") ? ccList.replaceAll("@infosys", "@ad.infosys")
								: "," + ccList.replaceAll("@infosys", "@ad.infosys")))
				+ (bccList.equals("") ? "" : "," + bccList.replaceAll("@infosys", "@ad.infosys"));

		if (!verifyIds.equals("")) {
			List<String> invalids = new ArrayList<>();
			Map<String, Object> ids = userUtilService.verifyUsers(Arrays.asList(verifyIds.split(",")));
			invalidIds.addAll(((List<String>) ids.get("invalid_users")));
			for (String id : invalidIds)
				invalids.add(id.replace("@infosys", "@ad.infosys"));

			List<String> temp = new ArrayList<>(Arrays.asList(toList.split(",")));
			temp.removeAll(invalids);
			toList = String.join(",", temp);

			temp = new ArrayList<>(Arrays.asList(ccList.split(",")));
			temp.removeAll(invalids);
			ccList = String.join(",", temp);

			temp = new ArrayList<>(Arrays.asList(bccList.split(",")));
			temp.removeAll(invalids);
			bccList = String.join(",", temp);
		}

		data.put("to_list", toList);
		data.put("cc_list", ccList);
		data.put("bcc_list", bccList);
		data.put("invalid_ids", invalidIds);
		data.put("sender", mailData.get("senderId").toString());
		data.put("sender_application_name", mailData.get("senderName").toString());
		data.put("message", "Request Accepted!");
		data.remove("emailTo");
		data.remove("ccTo");
		data.remove("bccTo");
	}

	@SuppressWarnings("unchecked")
	private String Validate(Map<String, Object> data) {
		int keys = 0;
		String validationMessage = "valid";
		try {
			for (String key : data.keySet()) {
				switch (key) {
				case "emailTo":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in email to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "ccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in cc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "bccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in bcc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "sharedBy":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in shared by");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "artifacts":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("identifier"))
								throw new Exception("No identifier for artifact");
							if (!map.containsKey("title"))
								throw new Exception("No title for artifacts");
							if (!map.containsKey("description"))
								throw new Exception("No description for artifacts");
							if (!map.containsKey("duration"))
								throw new Exception("No duration for artifact");
							if (!map.containsKey("track"))
								throw new Exception("No track for artifact");
							if (!map.containsKey("url"))
								throw new Exception("No url for artifact");
							if (!map.containsKey("downloadUrl"))
								throw new Exception("No downloadurl for artifact");
							if (!map.containsKey("size"))
								throw new Exception("No size for artifact");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "emailType":
					keys += 1;
					break;
				case "body":
					break;
				case "appURL":
					keys += 1;
					break;
				case "timestamp":
					keys += 1;
					break;
				default:
					break;
				}
			}
			if (keys < 6)
				throw new Exception("keys missing");
		} catch (Exception e) {
			validationMessage = e.getMessage();
		}
		return validationMessage;
	}

	@SuppressWarnings("unchecked")
	private String validatePlainMail(Map<String, Object> data) {
		int keys = 0;
		String validationMessage = "valid";
		try {
			for (String key : data.keySet()) {
				switch (key) {
				case "emailTo":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in email to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "ccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in cc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "bccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in bcc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "body":
					keys += 1;
					break;
				case "appURL":
					keys += 1;
					break;
				case "timestamp":
					keys += 1;
					break;
				case "subject":
					keys += 1;
					break;
				default:
					break;
				}
			}
			if (keys < 5)
				throw new Exception("keys missing");
		} catch (Exception e) {
			validationMessage = e.getMessage();
		}
		return validationMessage;
	}

	@SuppressWarnings("unchecked")
	private String ValidateReview(Map<String, Object> data) {
		int keys = 0;
		String validationMessage = "valid";
		try {
			for (String key : data.keySet()) {
				switch (key) {
				case "emailTo":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in email to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "ccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in cc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "actionBy":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in shared by");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "artifact":
					keys += 1;
					if (data.get(key) instanceof Map<?, ?>) {
						Map<String, Object> map = (Map<String, Object>) data.get(key);
						if (!map.containsKey("identifier"))
							throw new Exception("No identifier for artifact");
						if (!map.containsKey("name"))
							throw new Exception("No title for artifacts");
						if (!map.containsKey("contentType"))
							throw new Exception("No description for artifacts");
						if (!map.containsKey("trackContacts"))
							throw new Exception("No track contacts for artifacts");
						else {
							if (map.get("trackContacts") instanceof List<?>) {
								for (Map<String, Object> authMap : (List<Map<String, Object>>) map
										.get("trackContacts")) {
									if (!authMap.containsKey("name"))
										throw new Exception("No name for track contacts");
//									if (!authMap.containsKey("email"))
//										throw new Exception("No email for track contacts");
								}
							} else
								throw new Exception("track Contacts is not a list");
						}
					} else
						throw new Exception(key + " is not a dictionary");
					break;
				case "emailType":
					keys += 1;
					break;
				case "body":
					break;
				case "appURL":
					keys += 1;
					break;
				case "subject":
					keys += 1;
					break;
				default:
					break;
				}
			}
			if (keys < 6)
				throw new Exception("keys missing");
		} catch (Exception e) {
			validationMessage = e.getMessage();
		}
		return validationMessage;
	}

	@SuppressWarnings("unchecked")
	private String validateContent(Map<String, Object> data) {
		int keys = 0;
		String validationMessage = "valid";
		try {
			for (String key : data.keySet()) {
				switch (key) {
				case "emailTo":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in email to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "ccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in cc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "bccTo":
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in bcc to");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "sharedBy":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("email"))
								throw new Exception("Email not found in shared by");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "artifact":
					keys += 1;
					if (data.get(key) instanceof List<?>) {
						for (Map<String, Object> map : (List<Map<String, Object>>) data.get(key)) {
							if (!map.containsKey("identifier"))
								throw new Exception("No identifier for artifact");
							if (!map.containsKey("title"))
								throw new Exception("No title for artifacts");
							if (!map.containsKey("description"))
								throw new Exception("No description for artifacts");
							if (!map.containsKey("content"))
								throw new Exception("No content for artifact");
						}
					} else
						throw new Exception(key + " is not a list");
					break;
				case "emailType":
					keys += 1;
					break;
				case "body":
					break;
				case "appURL":
					keys += 1;
					break;
				case "timestamp":
					keys += 1;
					break;
				default:
					break;
				}
			}
			if (keys < 6)
				throw new Exception("keys missing");
		} catch (Exception e) {
			validationMessage = e.getMessage();
		}
		return validationMessage;
	}

	@Override
	public void PDFMail() throws MessagingException, IOException {
		SMTPHOST = System.getenv(LexJsonKey.SMTP_HOST);
		SMTPPORT = System.getenv(LexJsonKey.SMTP_PORT);

		if (ProjectUtil.isStringNullOREmpty(SMTPHOST) || ProjectUtil.isStringNullOREmpty(SMTPPORT)) {
			ProjectLogger.log("SMTP config is not coming form System variable.");
			SMTPHOST = properties.getProperty(LexJsonKey.SMTP_HOST);
			SMTPPORT = properties.getProperty(LexJsonKey.SMTP_PORT);
		}
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTPHOST);
		props.put("mail.smtp.port", SMTPPORT);

		Session session = Session.getDefaultInstance(props, null);

		Multipart multipart = new MimeMultipart();

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("ME", "ME " + " Platform"));
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("akshay.narula".replaceAll("ad.infosys", "infosys")));
		message.setSubject("sub");

		MimeBodyPart attachmentPart = new MimeBodyPart();

		File tempFile = File.createTempFile("temp", ".pdf");
		tempFile.deleteOnExit();
		PDDocument pdf = this.getPDF();
		pdf.save(tempFile);
		pdf.close();

		DataSource ds = new FileDataSource(tempFile);
		attachmentPart.setDataHandler(new DataHandler(ds));
		attachmentPart.setFileName("temp.pdf");
		multipart.addBodyPart(attachmentPart);

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent("some text", "text/html; charset=utf-8");
		multipart.addBodyPart(messageBodyPart);

		message.setContent(multipart);
		Transport.send(message);

	}

	private PDDocument getPDF() throws IOException {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage(new PDRectangle(948, 1188));
		doc.addPage(page);

		AccessPermission accessPermission = new AccessPermission();
		accessPermission.setCanModify(false);
		accessPermission.setCanExtractContent(false);
		accessPermission.setCanPrint(true);
		accessPermission.setCanPrintDegraded(false);
		accessPermission.setReadOnly();

		StandardProtectionPolicy standardProtectionPolicy = new StandardProtectionPolicy(null, null, accessPermission);
		doc.protect(standardProtectionPolicy);

		PDPageContentStream contents = new PDPageContentStream(doc, page);

		ClassPathResource newfont = new ClassPathResource("font/arial.ttf");
		PDType0Font arial = PDType0Font.load(doc, newfont.getInputStream());

		contents.beginText();
		contents.setFont(arial, 20);
		contents.setNonStrokingColor(Color.BLACK);
		contents.newLineAtOffset(110, 930);
		contents.showText("some");
		contents.endText();

		contents.beginText();
		contents.setFont(arial, 16);
		contents.setNonStrokingColor(Color.black);
		contents.newLineAtOffset(125, 890);
		contents.showText("some more");
		contents.endText();

//     contents.drawImage(image_header, -5, 1020, 960, 181);
//     
		contents.close();

		Overlay overlay = new Overlay();
		overlay.setInputPDF(doc);
		overlay.setOverlayPosition(Overlay.Position.BACKGROUND);

		return doc;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void convertUUIDtoEmail(Map<String, Object> requestBody) {
		List<String> allUuids = new ArrayList<>();
		// Extract all uuids from body
		List<String> allKeys = Arrays.asList("emailTo", "ccTo", "actionBy");
		for (String key : allKeys) {
			List<Map<String, Object>> maps = (List<Map<String, Object>>) requestBody.get(key);
			for (Map<String, Object> data : maps) {
				allUuids.add(data.get("id").toString());
			}
		}

		List<String> properties = new ArrayList<>();
		properties.add("email");
		properties.add("id");
		Response res = cassandraOperation.getRecordsByIdsWithSpecifiedColumns(sunbirdKeyspace, userTable, properties, allUuids);
		List<Map<String, Object>> uuidToEmailresponse = (List<Map<String, Object>>) res.getResult().get("response");
		Map<String, Object> uuidToEmailMap = new HashMap<>();
		for (Map<String, Object> row : uuidToEmailresponse) {
			uuidToEmailMap.put(row.get("id").toString(), row.get("email"));
		}

		for (String key : allKeys) {
			List<Map<String, Object>> maps = (List<Map<String, Object>>) requestBody.get(key);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> data : maps) {
				data.put("email",uuidToEmailMap.get(data.get("id").toString()));
				list.add(data);
			}
			requestBody.put(key,list);
			
		}
	}

}
