package com.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {
	@Test
	public void findsSmallestIntegerPosition() {
		List<Integer> input = asList(5, 4, 2, 42, 12);
		int position = Util.getIdOfLowestValue(input);
		assertEquals(2, position);
	}
	
	@Test
	public void findsHighestIntegerPosition() {
		List<Integer> input = asList(5, 4, 2, 42, 12);
		int position = Util.getIdOfHighestValue(input);
		assertEquals(3, position);
	}
	
	@Test
	public void splittsNumberSringToList(){
		String input = "0:8:15";
		List<Integer> result = Util.convertStringToList(input, ":");
		assertThat(result, is(asList(0,8,15)));
	}
}
