package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Some useful operations to quickly convert from or to Strings.
 */
public class Util {

	/**
	 * Used to convert a List to a String
	 * 
	 * @param list
	 *            can take any List<?> and uses the elements toString method
	 * @param splitter
	 *            how to Split the String
	 * @return a splitter separated String e.g.: "1,2,3" or "1 2 3"
	 */
	/**
	 * @param list
	 * 
	 * @return
	 */
	public static String convertListToString(List<?> list, String splitter) {
		return list.stream().map(Object::toString).collect(Collectors.joining(splitter));
	}

	/**
	 * Used to quickly convert a String containing Integer values into a List<Integer>
	 * 
	 * @param line
	 *            splitter separated line e.g.: "1 2 3 19 2920" or "1,2,3,19,2920"
	 * @param splitter
	 *            how is the line separated e.g.: " " or ","
	 * @return a List of Integer in the same order as the line
	 */
	public static List<Integer> convertStringToList(String line, String splitter) {
		List<Integer> elements = new ArrayList<Integer>();
		String[] splitedString = line.split(splitter);
		for (int i = 0; i < splitedString.length; i++) {
			elements.add(Integer.parseInt(splitedString[i]));
		}
		return elements;
	}

	public static int getIdOfLowestValue(List<Integer> list) {
		int lowestValue = Integer.MAX_VALUE;
		int id = -1;
		int i = 0;

		for (Integer value : list) {
			if (value < lowestValue) {
				lowestValue = value;
				id = i;
			}
			i++;
		}
		return id;
	}

	public static int getIdOfHighestValue(List<Integer> list) {
		int highestValue = 0;
		int id = -1;
		int i = 0;

		for (Integer value : list) {
			if (value > highestValue) {
				highestValue = value;
				id = i;
			}
			i++;
		}
		return id;
	}
}