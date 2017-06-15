package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * <p>
 * Used to import a text file. Provides functions to get access to line based operations.
 * </p>
 */
public class FileParser {

	/**
	 * Is public for quick access. Contains the whole file as a list of strings. line numbers are equal the list id's.
	 */
	public List<String> _fileAsArrayList;

	public static File getRootFolder() {
		return new File(new Object().getClass().getResource("/").getFile()).getParentFile();
	}

	public FileParser(File file) {
		Preconditions.checkNotNull(file);
		_fileAsArrayList = getFileAsList(file);
	}

	/**
	 * <p>
	 * Used to get repeating segments of lines. The startingLineNumber will be the first line of the first segment,
	 * while lineCountPerSegment takes the number of lines a segment has. With repetitions you defined how often the
	 * segment takes place.
	 * </p>
	 * 
	 * <p>
	 * e.g. the text looks like this from line 1 to 7: <br>
	 * 1: segment 1 line 1 <br>
	 * 2: segment 1 line 2 <br>
	 * 4: segment 2 line 1 <br>
	 * 5: segment 2 line 2 <br>
	 * 6: segment 3 line 1 <br>
	 * 7: segment 3 line 2 <br>
	 * </p>
	 * <p>
	 * Every segment contains 2 lines and there are 3 segments starting at line 1, you can call getSegments(1, 2, 3) and
	 * get a List in the same order as the text: <br>
	 * [["segment 1 line 1","segment 1 line 2"],<br>
	 * ["segment 2 line 1","segment 2 line 2"],<br>
	 * ["segment 3 line 1", "segment 3 line 2"]]
	 * </p>
	 * 
	 * <p>
	 * The last line number of the last Segment will be (startingLineNumber + lineCountPerSegment * repetitions) -1
	 * </p>
	 * 
	 * @param startingLineNumber
	 *            start from this line number, including this line
	 * @param lineCountPerSegment
	 *            how many rows the segment has
	 * @param repetitions
	 *            how often the segment is present
	 * @return
	 */
	public FileSegment getSegment(int startingLineNumber, int lineCountPerSegment, int repetitions) {
		Preconditions.checkArgument(startingLineNumber >= 0, "Can not start at a negative line.");
		Preconditions.checkArgument(lineCountPerSegment > 0, "A segment must have at least one line.");
		Preconditions.checkArgument(repetitions > 0, "At least one repetition is needed.");

		List<List<String>> allSegments = new ArrayList<List<String>>();
		int tillLine = startingLineNumber + lineCountPerSegment * repetitions - 1;
		for (int i = startingLineNumber; i <= tillLine; i += lineCountPerSegment) {
			List<String> oneSegment = new ArrayList<String>();
			allSegments.add(oneSegment);
			for (int j = 0; j < lineCountPerSegment; j++) {
				oneSegment.add(_fileAsArrayList.get(i + j));
			}
		}
		return new FileSegment(allSegments, startingLineNumber, tillLine);
	}

	/**
	 * get the String at the line number
	 */
	public String getLine(int lineNumber) {
		Preconditions.checkArgument(lineNumber >= 0 && lineNumber < _fileAsArrayList.size(),
				"The linenumber must be in the range of the given file.");
		return _fileAsArrayList.get(lineNumber);
	}

	/**
	 * collect all lines of the given file and make a list out of it. to get a better access to the lines there will be
	 * one empty line at id=0. So the line number is equal to list id.
	 */
	private List<String> getFileAsList(File file) {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}