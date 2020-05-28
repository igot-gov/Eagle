/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential

*/

package com.infosys.lex.notification.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.notification.bodhi.repository.AppConfigRepository;
import com.infosys.lex.notification.bodhi.repository.NotificationErrorsRepo;
import com.infosys.lex.notification.dto.EmailRequest;
import com.infosys.lex.notification.entity.SMTPConfig;
import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.model.cassandra.AppConfig;
import com.infosys.lex.notification.model.cassandra.AppConfigPrimaryKey;
import com.infosys.lex.notification.model.cassandra.NotificationErrors;
import com.infosys.lex.notification.model.cassandra.NotificationErrorsPrimaryKey;
import com.infosys.lex.notification.properties.ApplicationServerProperties;
import com.infosys.lex.notification.service.EmailService;
import com.infosys.lex.notification.service.SMTPConfigService;
import com.infosys.lex.notification.service.UserInformationService;
import com.infosys.lex.notification.util.LexNotificationLogger;
import com.infosys.lex.notification.util.ProjectCommonUtil;

import android.util.Base64;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	SMTPConfigService smtpConfigService;

	@Autowired
	AppConfigRepository appConfigRepo;

	@Autowired
	NotificationErrorsRepo notificationErrorsRepo;

	@Autowired
	UserInformationService userInfoService;

	@Autowired
	Environment env;
	
	@Autowired
	ApplicationServerProperties appProp;

	private LexNotificationLogger logger = new LexNotificationLogger(getClass().getName());

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void sendEmail(EmailRequest emailEvent) {

		// removes email ids that matches invalid domains stored in app_config table for
		// the given rootOrg.
		removeInvalidMailIds(emailEvent);

		SMTPConfig smtpConfig = smtpConfigService.getSMTPConfig(emailEvent.getRootOrg(),emailEvent.getOrgs());
		int chunkSize = smtpConfig.getChunkSize();
		// smtp server properties.
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpConfig.getHost());
		props.put("mail.smtp.connectiontimeout", appProp.getSmtpConnectionTimeout());

		// by default port is 25
		if (smtpConfig.getPort() != null && !smtpConfig.getPort().isEmpty())
			props.put("mail.smtp.port", smtpConfig.getPort());

		Authenticator auth = null;
		if (smtpConfig.getUserName() != null && !smtpConfig.getUserName().isEmpty() && smtpConfig.getPassword() != null
				&& !smtpConfig.getPassword().isEmpty()) {

			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpConfig.getUserName(), smtpConfig.getPassword());
				}
			};
		}

		try {
			// if sender not specified in request then sender set from smtp config table
			String senderId = null;
			if (emailEvent.getFrom() != null && !emailEvent.getFrom().isEmpty())
				senderId = emailEvent.getFrom();
			else
				senderId = smtpConfig.getSenderId();

			Session session = Session.getInstance(props, auth);

			// divide the to list by chunk size
			List<String> toList = emailEvent.getTo();
			List<String> newToList;
			int tolistSize = toList.size();
			int endIndex = tolistSize< chunkSize?tolistSize:chunkSize;
			int startIndex = 0;
			while (endIndex <= tolistSize && startIndex != endIndex) {
				newToList = toList.subList(startIndex, endIndex);
				startIndex = endIndex;
				endIndex += chunkSize;

				if (endIndex > tolistSize)
					endIndex = tolistSize;

				emailEvent.setTo(newToList);
				MimeMessage message = new MimeMessage(session);

				MimeBodyPart emailBody = createEmailBodyPart(emailEvent.getBody());
				MimeMultipart mixedMultipart = new MimeMultipart("mixed");

				mixedMultipart.addBodyPart(emailBody);
				message.setContent(mixedMultipart);

				// digitally sign email based on rootOrg's configuration
				if (smtpConfig.signEmail()) {
					X509Certificate certificate = loadCertificate(emailEvent.getRootOrg());
					PrivateKey privateKey = getPrivateKey(emailEvent.getRootOrg());
					message = signMessage(certificate, privateKey, message);
				}

				message.setSubject(emailEvent.getSubject());
				senderId = senderId.replaceAll("@ad.", "@");
				message.setFrom(new InternetAddress(senderId));
				setRecipients(emailEvent, message);
//
//				System.out.println(message.toString());
//				System.out.println(emailEvent.getTo());
//				System.out.println(emailEvent.getBody());
				Transport.send(message);

			}

		} catch (Exception e) {
			saveError(emailEvent, e);
			throw new ApplicationLogicException("Email service exception " + e.getMessage(), e);
		}
	}

	private void removeInvalidMailIds(EmailRequest emailEvent) {

		List<String> invalidDomains = getInvalidDomains(emailEvent.getRootOrg());
		// removing email ids with invalid domains
		emailEvent.setTo(ProjectCommonUtil.removeInvalidEmailIds(invalidDomains, emailEvent.getTo()));
		emailEvent.setCc(ProjectCommonUtil.removeInvalidEmailIds(invalidDomains, emailEvent.getCc()));
		emailEvent.setBcc(ProjectCommonUtil.removeInvalidEmailIds(invalidDomains, emailEvent.getBcc()));
	}

	private List<String> getInvalidDomains(String rootOrg) {

		List<String> invalidDomains = new ArrayList<>();

		Optional<AppConfig> appConfig = appConfigRepo.findById(new AppConfigPrimaryKey(rootOrg, "invalid_domains"));
		if (appConfig.isPresent())
			invalidDomains = Arrays.asList(appConfig.get().getValue().split(","));

		return invalidDomains;
	}

	private MimeBodyPart createEmailBodyPart(String emailTemplate) throws MessagingException {

		// Define the text part.
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(emailTemplate, "text/plain; charset=UTF-8");

		// Define the HTML part.
		MimeBodyPart htmlBody = new MimeBodyPart();
		htmlBody.setContent(emailTemplate, "text/html; charset=UTF-8");

		MimeMultipart alternativeMultipart = new MimeMultipart("alternative");

		// Add the text and HTML parts to the child container.
		alternativeMultipart.addBodyPart(textBody);
		alternativeMultipart.addBodyPart(htmlBody);

		MimeBodyPart alternativeBody = new MimeBodyPart();
		alternativeBody.setContent(alternativeMultipart);

		return alternativeBody;
	}

	private PrivateKey getPrivateKey(String rootOrg)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		File keyFile = new File(env.getProperty(rootOrg.toLowerCase() + ".smtp.key"));
		FileInputStream in = new FileInputStream(keyFile);
		byte[] keyBytes = new byte[in.available()];
		in.read(keyBytes);
		in.close();

		String privateKey = new String(keyBytes, "UTF-8");

		StringBuilder pkcs8Lines = new StringBuilder();
		BufferedReader rdr = new BufferedReader(new StringReader(privateKey));
		String line;
		while ((line = rdr.readLine()) != null) {
			pkcs8Lines.append(line);
		}

		String pkcs8Pem = pkcs8Lines.toString();
		pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
		pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
		pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

		java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Base64 decode the result
		byte[] pkcs8EncodedBytes = Base64.decode(pkcs8Pem, Base64.DEFAULT);

		// extract the private key
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey privKey = kf.generatePrivate(keySpec);
		return privKey;
	}

	private X509Certificate loadCertificate(String rootOrg) throws CertificateException, IOException {

		CertificateFactory cf = CertificateFactory.getInstance("X509");

		File certFile = new File(env.getProperty(rootOrg.toLowerCase() + ".smtp.certificate"));

		FileInputStream in = new FileInputStream(certFile);
		try {
			X509Certificate c = (X509Certificate) cf.generateCertificate(in);
			c.checkValidity();
			return c;
		} finally {
			in.close();
		}

	}

	@SuppressWarnings("rawtypes")
	private MimeMessage signMessage(X509Certificate certificate, PrivateKey privateKey, MimeMessage message)
			throws CertificateEncodingException, OperatorCreationException, MessagingException, IOException,
			SMIMEException {

		List<X509Certificate> certList = new ArrayList<>();
		certList.add(certificate);

		Store certs = new JcaCertStore(certList);
		SMIMESignedGenerator gen = new SMIMESignedGenerator();

		gen.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1withRSA",
				privateKey, certificate));
		gen.addCertificates(certs);

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent((MimeMultipart) message.getContent());

		MimeMultipart mm = gen.generate(mimeBodyPart);

		MimeMessage signedMessage = new MimeMessage(message.getSession());
		signedMessage.setContent(mm);
		signedMessage.saveChanges();

		return signedMessage;
	}

	private void setRecipients(EmailRequest emailEvent, MimeMessage message)
			throws MessagingException, AddressException {

		message.setRecipients(Message.RecipientType.TO,
				InternetAddress
						.parse(String.join(",", emailEvent.getTo() != null ? emailEvent.getTo() : new ArrayList<>())
								.replaceAll("@ad.", "@")));

		message.setRecipients(Message.RecipientType.CC,
				InternetAddress
						.parse(String.join(",", emailEvent.getCc() != null ? emailEvent.getCc() : new ArrayList<>())
								.replaceAll("@ad.", "@")));

		message.setRecipients(Message.RecipientType.BCC,
				InternetAddress
						.parse(String.join(",", emailEvent.getBcc() != null ? emailEvent.getBcc() : new ArrayList<>())
								.replaceAll("@ad.", "@")));
	}

	private void saveError(EmailRequest emailEvent, Exception e) {

		try {
			notificationErrorsRepo.save(new NotificationErrors(
					new NotificationErrorsPrimaryKey(emailEvent.getRootOrg(), emailEvent.getEventId()), e.getMessage(),
					mapper.writeValueAsString(emailEvent), e.getStackTrace().toString()));
		} catch (Exception e1) {
			logger.error("could not save error event for rootOrg" + emailEvent.getRootOrg() + " and eventId req body "
					+ emailEvent.toString());
		}
	}
}