package com.util;

import java.util.List;

public class FileSegment {
	public FileSegment(List<List<String>> allSegments, int startingLineNumber, int tillLine) {
		_segment = allSegments;
		_firstLine = startingLineNumber;
		_lastLine = tillLine;
	}

	public List<List<String>> _segment;
	public int _firstLine;
	public int _lastLine;

}