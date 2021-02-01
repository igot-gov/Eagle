package org.sunbird.portal.department.dto;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class GenericArrayUserType<T extends Serializable> implements UserType {
	protected static final int[] SQL_TYPES = { Types.ARRAY };
	private Class<T> typeParameterClass;

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (T) this.deepCopy(value);
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<T> returnedClass() {
		return typeParameterClass;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.ARRAY };
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		if (rs.wasNull()) {
			return null;
		}
		if (rs.getArray(names[0]) == null) {
			return new Integer[0];
		}

		Array array = rs.getArray(names[0]);
		@SuppressWarnings("unchecked")
		T javaArray = (T) array.getArray();
		return javaArray;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		Connection connection = st.getConnection();
		if (value == null) {
			st.setNull(index, SQL_TYPES[0]);
		} else {
			@SuppressWarnings("unchecked")
			T castObject = (T) value;
			Array array = connection.createArrayOf("integer", (Object[]) castObject);
			st.setArray(index, array);
		}
	}

}
