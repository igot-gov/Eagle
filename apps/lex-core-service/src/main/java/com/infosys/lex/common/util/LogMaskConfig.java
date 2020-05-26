/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
//package com.infosys.lex.common.util;

//
//import java.lang.reflect.Field;
//
//import org.apache.logging.log4j.ThreadContext;
//import org.apache.logging.log4j.core.LogEvent;
//import org.apache.logging.log4j.core.config.plugins.Plugin;
//import org.apache.logging.log4j.core.pattern.ConverterKeys;
//import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
//import org.springframework.context.ApplicationContext;
//
//import com.infosys.lex.common.util.DatabaseProperties;
//import com.infosys.lex.core.config.SpringContext;
//
//@Plugin(name = "LogMask", category = "Converter")
//@ConverterKeys({ "convMessage", "conMsg" })
//public class LogMaskConfig extends LogEventPatternConverter {
//
//	protected LogMaskConfig(String name, String style) {
//        super(name, style);
//    }
//
//    public static LogMaskConfig newInstance(String[] options) {
//        return new LogMaskConfig("convMessage","m");
//    }
//	
//	@Override
//	public void format(LogEvent logEvent, StringBuilder outputMsg) {
//
//		String message = logEvent.getMessage().getFormat();
//		if (ThreadContext.get("req.url") != null) {
//			ApplicationContext context = SpringContext.getApplicationContextWhereInaccessible();
//			// get instance of MainSpringClass (Spring Managed class)
//			DatabaseProperties databaseProperties = (DatabaseProperties) context.getBean("databaseProperties");
//			for (Field f : databaseProperties.getClass().getDeclaredFields()) {
//				f.setAccessible(true);
//				try {
//					if (!f.get(databaseProperties).toString().isEmpty() && !f.get(databaseProperties).toString().equals("true") && !f.get(databaseProperties).toString().equals("false"))
//						message = message.replaceAll(f.get(databaseProperties).toString(), "XXXXXX");
//				} catch (Exception e) {
//					message = "";
//				}
//			}
//		}
//
//		outputMsg.append(message);
//
//	}
//
//}