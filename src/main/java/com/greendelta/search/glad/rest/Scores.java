package com.greendelta.search.glad.rest;

import static com.greendelta.search.wrapper.score.Comparator.EQUALS;
import static com.greendelta.search.wrapper.score.Comparator.IS_GREATER_OR_EQUAL_THAN;
import static com.greendelta.search.wrapper.score.Comparator.IS_GREATER_THAN;
import static com.greendelta.search.wrapper.score.Comparator.IS_LESS_OR_EQUAL_THAN;
import static com.greendelta.search.wrapper.score.Comparator.IS_LESS_THAN;

import com.greendelta.search.wrapper.score.Condition;
import com.greendelta.search.wrapper.score.Field;
import com.greendelta.search.wrapper.score.Score;

class Scores {

	static Score completeness() {
		return new Score("completeness", null, 0d, 100d)
				.addCase(1.0, IS_GREATER_THAN, 98)
				.addCase(0.8, IS_LESS_OR_EQUAL_THAN, 98, IS_GREATER_THAN, 90)
				.addCase(0.6, IS_LESS_OR_EQUAL_THAN, 90, IS_GREATER_THAN, 75)
				.addCase(0.4, IS_LESS_OR_EQUAL_THAN, 75, IS_GREATER_THAN, 50)
				.addElse(0.2);
	}

	static Score amountDeviation() {
		return new Score("amountDeviation", null, 0d, 100d)
				.addCase(1.0, IS_LESS_THAN, 2)
				.addCase(0.8, IS_GREATER_OR_EQUAL_THAN, 2, IS_LESS_THAN, 5)
				.addCase(0.6, IS_GREATER_OR_EQUAL_THAN, 5, IS_LESS_THAN, 10)
				.addCase(0.4, IS_GREATER_OR_EQUAL_THAN, 10, IS_LESS_THAN, 25)
				.addElse(0.2);
	}

	static Score representativeness() {
		return new Score("representativenessValue", null, 0d, null)
				.addCase(1.0, IS_LESS_OR_EQUAL_THAN, 5)
				.addCase(0.8, IS_GREATER_THAN, 5, IS_LESS_OR_EQUAL_THAN, 25)
				.addCase(0.6, IS_GREATER_THAN, 25, IS_LESS_OR_EQUAL_THAN, 50)
				.addCase(0.4, IS_GREATER_THAN, 50, IS_LESS_OR_EQUAL_THAN, 100)
				.addElse(0.2);
	}

	static Score time(long date) {
		String validFrom = "fieldValues[0]";
		String validUntil = "fieldValues[1]";
		String input = "values[0]";
		String dateDiff = "min(abs(fieldValues[0] - values[0]), abs(fieldValues[1] - values[0])) / 31536000000L";
		return new Score(new Field("validFrom", date), new Field("validUntil", date))
				.addCase(1.0, new Condition(validFrom, IS_LESS_OR_EQUAL_THAN, input),
						new Condition(validUntil, IS_GREATER_OR_EQUAL_THAN, input))
				.addCase(0.8, dateDiff, IS_LESS_THAN, 3)
				.addCase(0.6, dateDiff, IS_GREATER_OR_EQUAL_THAN, 3, IS_LESS_THAN, 6)
				.addCase(0.4, dateDiff, IS_GREATER_OR_EQUAL_THAN, 6, IS_LESS_THAN, 10)
				.addElse(0.2);
	}

	static Score geography(double latitude, double longitude) {
		String distance = "abs(getDistance(fieldValues[0], fieldValues[1], values[0], values[1]))";
		String latDiff = "abs(fieldValues[0] - values[0])";
		return new Score(new Field("latitude", latitude, -90d, 90d), new Field("longitude", longitude, -180d, 180d))
				.addCase(1.0, new Condition(distance, IS_LESS_OR_EQUAL_THAN, 100))
				.addCase(0.8, new Condition(distance, IS_GREATER_THAN, 100),
						new Condition(distance, IS_LESS_OR_EQUAL_THAN, 500))
				.addCase(0.6, new Condition(distance, IS_GREATER_THAN, 500),
						new Condition(latDiff, IS_LESS_OR_EQUAL_THAN, 10))
				.addCase(0.4, new Condition(distance, IS_GREATER_THAN, 500),
						new Condition(latDiff, IS_GREATER_THAN, 10),
						new Condition(latDiff, IS_LESS_OR_EQUAL_THAN, 15)).
				addElse(0.2);
	}

	static Score technology(String unspscCode, String co2peCode) {
		String dataUnspsc8 = "fieldValues[0]";
		String dataUnspsc6 = "substring(fieldValues[0], 0, 6)";
		String dataUnspsc4 = "substring(fieldValues[0], 0, 4)";
		String dataCo2peLevel3 = "fieldValues[1]";
		String dataCo2peLevel2 = "substring(fieldValues[1], 0, lastIndexOf(fieldValues[1], '.'))";
		String dataCo2peLevel1 = "substring(fieldValues[1], 0, indexOf(fieldValues[1], '.'))";
		String inputUnspsc8 = "values[0]";
		String inputUnspsc6 = "substring(values[0], 0, 6)";
		String inputUnspsc4 = "substring(values[0], 0, 4)";
		String inputCo2peLevel3 = "values[1]";
		String inputCo2peLevel2 = "substring(values[1], 0, lastIndexOf(values[1], '.'))";
		String inputCo2peLevel1 = "substring(values[1], 0, indexOf(values[1], '.'))";
		return new Score(new Field("unspscCode", unspscCode), new Field("co2peCode", co2peCode))
				.addCase(1.0, new Condition(dataUnspsc8, EQUALS, inputUnspsc8),
						new Condition(dataCo2peLevel3, EQUALS, inputCo2peLevel3))
				.addCase(0.8, new Condition(dataUnspsc6, EQUALS, inputUnspsc6),
						new Condition(dataCo2peLevel3, EQUALS, inputCo2peLevel3))
				.addCase(0.8, new Condition(dataUnspsc8, EQUALS, inputUnspsc8),
						new Condition(dataCo2peLevel2, EQUALS, inputCo2peLevel2))
				.addCase(0.6, new Condition(dataUnspsc4, EQUALS, inputUnspsc4),
						new Condition(dataCo2peLevel2, EQUALS, inputCo2peLevel2))
				.addCase(0.6, new Condition(dataUnspsc6, EQUALS, inputUnspsc6),
						new Condition(dataCo2peLevel1, EQUALS, inputCo2peLevel1))
				.addCase(0.4, new Condition(dataUnspsc4, EQUALS, inputUnspsc4),
						new Condition(dataCo2peLevel1, EQUALS, inputCo2peLevel1))
				.addElse(0.2);
	}

}
