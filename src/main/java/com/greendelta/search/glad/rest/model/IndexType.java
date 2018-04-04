package com.greendelta.search.glad.rest.model;

public enum IndexType {

	TEXT,
	KEYWORD,
	DATE,
	INTEGER,
	DOUBLE,
	BOOLEAN;

	public boolean isNumeric() {
		return this == INTEGER || this == DOUBLE || this == DATE;
	}

	@SuppressWarnings("unchecked")
	public <T> T parse(String value) {
		if (value == null)
			return null;
		try {
			switch (this) {
			case BOOLEAN:
				if (value.toLowerCase().equals("true"))
					return (T) new Boolean(true);
				if (value.toLowerCase().equals("false"))
					return (T) new Boolean(false);
				return null;
			case DATE:
				return (T) new Long(Long.parseLong(value));
			case DOUBLE:
				return (T) new Double(Double.parseDouble(value));
			case INTEGER:
				return (T) new Integer(Integer.parseInt(value));
			default:
				return (T) value;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

}