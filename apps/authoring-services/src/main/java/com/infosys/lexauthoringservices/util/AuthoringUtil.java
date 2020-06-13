package com.infosys.lexauthoringservices.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

public class AuthoringUtil {

	public static BigInteger md5HashGenerator(String identifierString) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(identifierString.getBytes());
			byte[] digest = md.digest();
			String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
			BigInteger identifierStringToBigInteger = new BigInteger(myHash, 16);
			return identifierStringToBigInteger;
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean haveSameAuthors(Map<String, Object> parentContent, Map<String, Object> childContent) {

		Set<String> parentContentAuthors = ((List<Map<String, Object>>) parentContent
				.get(LexConstants.CREATOR_CONTACTS)).stream()
						.map(creatorContact -> creatorContact.get(LexConstants.ID).toString())
						.collect(Collectors.toSet());

		Set<String> childContentAuthors = ((List<Map<String, Object>>) childContent.get(LexConstants.CREATOR_CONTACTS))
				.stream().map(creatorContact -> creatorContact.get(LexConstants.ID).toString())
				.collect(Collectors.toSet());

		for (String parentContentAuthor : parentContentAuthors) {
			if (childContentAuthors.contains(parentContentAuthor))
				return true;
		}

		return false;
	}
}
