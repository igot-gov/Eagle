package com.infosys.lex.notification.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.infosys.lex.notification.exception.ApplicationLogicException;
import com.infosys.lex.notification.service.EncryptionService;

@Service
public class EncyptionServiceImpl implements EncryptionService {

	@Override
	public String decrypt(String data, String key) {
		try {
			SecretKeySpec secretKey;
			secretKey = this.setKey(key);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
		} catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
				| UnsupportedEncodingException | NoSuchAlgorithmException e) {
			throw new ApplicationLogicException("Decryption failure -> " + e.getMessage(), e.getCause());
		}
	}

	@Override
	public String encrypt(String value, String key) {
		try {
			SecretKeySpec secretKey = this.setKey(key);
			Cipher cipher;

			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException
				| IllegalBlockSizeException | BadPaddingException e) {
			throw new ApplicationLogicException("Encryption failure -> " + e.getMessage(), e.getCause());
		}
	}

	private SecretKeySpec setKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest sha = null;
		byte[] setKey = null;
		SecretKeySpec secretKey = null;
		setKey = key.getBytes("UTF-8");
		sha = MessageDigest.getInstance("SHA-1");
		setKey = sha.digest(setKey);
		setKey = Arrays.copyOf(setKey, 16);
		secretKey = new SecretKeySpec(setKey, "AES");

		return secretKey;
	}
}
