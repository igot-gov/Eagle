package com.infosys.lex.ext.util;

import java.lang.reflect.Field;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import com.infosys.lex.ext.Models.Cassandra.UserTermsAndCondition;

public class Check {
	public static void main(String args[]) {
		print(UserTermsAndCondition.class);
	}

	public static void print(Class className) {
		try {
			Field fields[] = className.getDeclaredFields();
			for (Field field : fields) {
				System.out.println(field.getAnnotationsByType(Column.class));
				PrimaryKey primaryKeyFields[] = field.getAnnotationsByType(PrimaryKey.class);
				if (primaryKeyFields != null && primaryKeyFields.length > 0) {
					System.out.println(field.getName());
					Class primaryKeyClass = Class.forName(field.getGenericType().toString().substring(6));
					Field primaryKeyFieldsName[] = primaryKeyClass.getDeclaredFields();
					for (Field primaryKeyFieldName : primaryKeyFieldsName) {
						if (primaryKeyFieldName.getAnnotation(Column.class) != null) {
							System.out.println(primaryKeyFieldName.getAnnotation(Column.class).value());
						}

					}

				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
