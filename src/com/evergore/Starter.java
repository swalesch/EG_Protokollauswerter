package com.evergore;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.util.FileParser;

public class Starter {
	private static String _workingDir;
	private static String _inputFileName = "protokollLagerEgMaerz";

	public static void main(String[] args) {
		File projectRootFolder = FileParser.getRootFolder();
		_workingDir = projectRootFolder.toPath().toString() + "/resources/eg/";
		FileParser fileToParse = new FileParser(new File(_workingDir + _inputFileName + ".txt"));
		List<List<String>> segmentFileByDate = segmentFileByDate(fileToParse);
		readBaumeister(segmentFileByDate);
		System.out.println("");
		readGoldTransactionProSpieler(segmentFileByDate);
		System.out.println("");
		readGoldTransactionBilanz(segmentFileByDate);
		System.out.println("");
		readLagerTransactionProSpieler(segmentFileByDate);
		System.out.println("");
		readLagerTransactionBilanzProSpieler(segmentFileByDate);
		System.out.println("");
		readLagerTransactionBilanzLager(segmentFileByDate);
		System.out.println("");
		readLagerTransaction(segmentFileByDate);
		System.out.println("");
		readMunitionsVerbrauch(segmentFileByDate);
		System.out.println("");
		readMunitionsEinlagerung(segmentFileByDate);
	}

	private static void readMunitionsEinlagerung(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Munitionseinlagerung");
		System.out.println("---------------------------------");
		Date newestDate = null;
		Date oldesDate = null;
		int essenzenEinlagerung = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Einlagerung")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				for (String line : block) {
					if (line.contains("essenz")) {
						String[] splitedLine = line.split(" ");
						essenzenEinlagerung += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}
		int pfeileEinlagerung = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Einlagerung")) {
				for (String line : block) {
					if (isPfeil(line)) {
						String[] splitedLine = line.split(" ");
						pfeileEinlagerung += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}

		int bolzenEinlagerung = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Einlagerung")) {
				for (String line : block) {
					if (isBolzen(line)) {
						String[] splitedLine = line.split(" ");
						bolzenEinlagerung += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}
		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Essenzen eingelagert: " + essenzenEinlagerung);
		System.out.println("Pfeile eingelagert: " + pfeileEinlagerung);
		System.out.println("Bolzen eingelagert: " + bolzenEinlagerung);
	}

	private static void readMunitionsVerbrauch(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Munitionsverbrauch");
		System.out.println("---------------------------------");
		Date newestDate = null;
		Date oldesDate = null;
		int essenzenEntnommen = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				for (String line : block) {
					if (line.contains("essenz")) {
						String[] splitedLine = line.split(" ");
						essenzenEntnommen += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}
		int pfeileEntnommen = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme")) {
				for (String line : block) {
					if (isPfeil(line)) {
						String[] splitedLine = line.split(" ");
						pfeileEntnommen += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}

		int bolzenEntnommen = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme")) {
				for (String line : block) {
					if (isBolzen(line)) {
						String[] splitedLine = line.split(" ");
						bolzenEntnommen += Integer.parseInt(splitedLine[0]);
					}
				}
			}
		}
		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Essenzen entnommen: " + essenzenEntnommen);
		System.out.println("Pfeile entnommen: " + pfeileEntnommen);
		System.out.println("Bolzen entnommen: " + bolzenEntnommen);
	}

	private static boolean isPfeil(String line) {
		return line.contains("pfeile") || line.contains("Pfeile");
	}

	private static boolean isBolzen(String line) {
		return line.contains("bolzen") || line.contains("Bolzen");
	}

	private static void readLagerTransaction(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Lager Transaktionen");
		System.out.println("---------------------------------");

		Map<String, Integer> einlagerung = Maps.newHashMap();
		Map<String, Integer> entnahme = Maps.newHashMap();
		Date newestDate = null;
		Date oldesDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme") && !block.get(1).contains("Gold")) {
				String name = getUserName(block.get(0), "Entnahme.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEntnommen = splitedLine[0];
						if (entnahme.containsKey(gegenstandsName)) {
							Integer value = entnahme.get(gegenstandsName);
							entnahme.put(gegenstandsName, value + Integer.parseInt(anzahlEntnommen));
						} else {
							entnahme.put(gegenstandsName, Integer.parseInt(anzahlEntnommen));
						}
					}
					i++;
				}

			}
			if (block.get(0).contains("Einlagerung")) {
				String name = getUserName(block.get(0), "Einlagerung.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEingelagert = splitedLine[0];
						if (einlagerung.containsKey(gegenstandsName)) {
							Integer value = einlagerung.get(gegenstandsName);
							einlagerung.put(gegenstandsName, value + Integer.parseInt(anzahlEingelagert));
						} else {
							einlagerung.put(gegenstandsName, Integer.parseInt(anzahlEingelagert));
						}
					}
					i++;
				}
			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Eingelagert:");
		einlagerung.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> System.out.println(entrySet.getKey() + " " + entrySet.getValue()));
		System.out.println("");
		System.out.println("Entnahme:");
		entnahme.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> System.out.println(entrySet.getKey() + " " + entrySet.getValue()));

	}

	private static void readLagerTransactionBilanzLager(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Lager Transaktionen Bilanz");
		System.out.println("---------------------------------");

		Map<String, Integer> transaktion = Maps.newHashMap();
		Date newestDate = null;
		Date oldesDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme") && !block.get(1).contains("Gold")) {
				String name = getUserName(block.get(0), "Entnahme.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEntnommen = splitedLine[0];
						if (transaktion.containsKey(gegenstandsName)) {
							Integer value = transaktion.get(gegenstandsName);
							transaktion.put(gegenstandsName, value - Integer.parseInt(anzahlEntnommen));
						} else {
							transaktion.put(gegenstandsName, -Integer.parseInt(anzahlEntnommen));
						}
					}
					i++;
				}

			}
			if (block.get(0).contains("Einlagerung")) {
				String name = getUserName(block.get(0), "Einlagerung.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEingelagert = splitedLine[0];
						if (transaktion.containsKey(gegenstandsName)) {
							Integer value = transaktion.get(gegenstandsName);
							transaktion.put(gegenstandsName, value + Integer.parseInt(anzahlEingelagert));
						} else {
							transaktion.put(gegenstandsName, Integer.parseInt(anzahlEingelagert));
						}
					}
					i++;
				}
			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Transaktionsbilanz:");
		transaktion.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> {
					String gegenstandsname = entrySet.getKey();
					int anzahl = entrySet.getValue();
					if (anzahl != 0) {
						System.out.println(gegenstandsname + " " + anzahl);
					}
				});
	}

	private static void readLagerTransactionBilanzProSpieler(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Lager Transaktionen Bilanz Pro Spieler");
		System.out.println("---------------------------------");

		Map<String, Map<String, Integer>> transaktion = Maps.newHashMap();
		Date newestDate = null;
		Date oldesDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme") && !block.get(1).contains("Gold")) {
				String name = getUserName(block.get(0), "Entnahme.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				Map<String, Integer> entnahme;
				if (!transaktion.containsKey(name)) {
					entnahme = Maps.newHashMap();
					transaktion.put(name, entnahme);
				} else {
					entnahme = transaktion.get(name);
				}
				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEntnommen = splitedLine[0];
						if (entnahme.containsKey(gegenstandsName)) {
							Integer value = entnahme.get(gegenstandsName);
							entnahme.put(gegenstandsName, value - Integer.parseInt(anzahlEntnommen));
						} else {
							entnahme.put(gegenstandsName, -Integer.parseInt(anzahlEntnommen));
						}
					}
					i++;
				}

			}
			if (block.get(0).contains("Einlagerung")) {
				String name = getUserName(block.get(0), "Einlagerung.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				Map<String, Integer> einlagerung;
				if (!transaktion.containsKey(name)) {
					einlagerung = Maps.newHashMap();
					transaktion.put(name, einlagerung);
				} else {
					einlagerung = transaktion.get(name);
				}

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEingelagert = splitedLine[0];
						if (einlagerung.containsKey(gegenstandsName)) {
							Integer value = einlagerung.get(gegenstandsName);
							einlagerung.put(gegenstandsName, value + Integer.parseInt(anzahlEingelagert));
						} else {
							einlagerung.put(gegenstandsName, Integer.parseInt(anzahlEingelagert));
						}
					}
					i++;
				}
			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Transaktionsbilanz:");
		transaktion.entrySet().stream().sorted(Map.Entry.<String, Map<String, Integer>> comparingByKey())
				.forEach(entrySet -> {
					System.out.println("\n" + entrySet.getKey() + " Bilanz:");
					entrySet.getValue().entrySet().stream()
							.sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
							.forEachOrdered(entrySet2 -> {
						String gegenstandsname = entrySet2.getKey();
						int anzahl = entrySet2.getValue();
						if (anzahl != 0) {
							System.out.println(gegenstandsname + " " + anzahl);
						}
					});
				});
	}

	private static void readLagerTransactionProSpieler(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Lager Transaktionen pro Spieler");
		System.out.println("---------------------------------");

		Map<String, Map<String, Integer>> einlagerungUsers = Maps.newHashMap();
		Map<String, Map<String, Integer>> entnahmeUsers = Maps.newHashMap();
		Date newestDate = null;
		Date oldesDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme") && !block.get(1).contains("Gold")) {
				String name = getUserName(block.get(0), "Entnahme.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				Map<String, Integer> entnahme;
				if (!entnahmeUsers.containsKey(name)) {
					entnahme = Maps.newHashMap();
					entnahmeUsers.put(name, entnahme);
				} else {
					entnahme = entnahmeUsers.get(name);
				}
				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEntnommen = splitedLine[0];
						if (entnahme.containsKey(gegenstandsName)) {
							Integer value = entnahme.get(gegenstandsName);
							entnahme.put(gegenstandsName, value + Integer.parseInt(anzahlEntnommen));
						} else {
							entnahme.put(gegenstandsName, Integer.parseInt(anzahlEntnommen));
						}
					}
					i++;
				}

			}
			if (block.get(0).contains("Einlagerung")) {
				String name = getUserName(block.get(0), "Einlagerung.*");

				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldesDate = getOldesDate(oldesDate, date);

				Map<String, Integer> einlagerung;
				if (!einlagerungUsers.containsKey(name)) {
					einlagerung = Maps.newHashMap();
					einlagerungUsers.put(name, einlagerung);
				} else {
					einlagerung = einlagerungUsers.get(name);
				}

				int i = 0;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEingelagert = splitedLine[0];
						if (einlagerung.containsKey(gegenstandsName)) {
							Integer value = einlagerung.get(gegenstandsName);
							einlagerung.put(gegenstandsName, value + Integer.parseInt(anzahlEingelagert));
						} else {
							einlagerung.put(gegenstandsName, Integer.parseInt(anzahlEingelagert));
						}
					}
					i++;
				}
			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldesDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Eingelagert:");
		einlagerungUsers.entrySet().stream().sorted(Map.Entry.<String, Map<String, Integer>> comparingByKey())
				.forEach(entrySet -> {
					System.out.println("\n" + entrySet.getKey() + " lagerte ein:");
					entrySet.getValue().entrySet().stream()
							.sorted(Map.Entry.<String, Integer> comparingByValue().reversed()).forEachOrdered(
									entrySet2 -> System.out.println(entrySet2.getKey() + " " + entrySet2.getValue()));
				});
		System.out.println("");
		System.out.println("Entnahme:");
		entnahmeUsers.entrySet().stream().sorted(Map.Entry.<String, Map<String, Integer>> comparingByKey())
				.forEach(entrySet -> {
					System.out.println("\n" + entrySet.getKey() + " entnahm:");
					entrySet.getValue().entrySet().stream()
							.sorted(Map.Entry.<String, Integer> comparingByValue().reversed()).forEachOrdered(
									entrySet2 -> System.out.println(entrySet2.getKey() + " " + entrySet2.getValue()));
				});

	}

	private static Date getOldesDate(Date oldesDate, Date date) {
		if (oldesDate == null) {
			return date;
		}
		if (oldesDate.getTime() <= date.getTime()) {
			return oldesDate;
		}
		return date;
	}

	private static Date getNewestDate(Date newestDate, Date date) {
		if (newestDate == null) {
			return date;
		}
		if (newestDate.getTime() >= date.getTime()) {
			return newestDate;
		}
		return date;
	}

	private static void readGoldTransactionBilanz(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Gold Transaktionen Bilanz");
		System.out.println("---------------------------------");
		int summe = 0;
		Date newestDate = null;
		Date oldestDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme")) {
				for (String line : block) {
					if (line.contains("Gold")) {
						Date date = getDate(block.get(0));
						newestDate = getNewestDate(newestDate, date);
						oldestDate = getOldesDate(oldestDate, date);

						int goldCount = Integer.parseInt(line.split(" ")[0]);
						summe -= goldCount;
					}
				}
			}

			if (block.get(0).contains("Einzahlung")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldestDate = getOldesDate(oldestDate, date);

				int goldCount = 0;
				if (block.get(1).contains("Gold")) {
					goldCount = Integer.parseInt(block.get(1).split(" ")[0]);
					summe += goldCount;
				}
			}

			if (block.get(0).contains("Umbuchung")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldestDate = getOldesDate(oldestDate, date);
				int goldCount = 0;
				if (block.get(1).contains("Gold")) {
					goldCount = Integer.parseInt(block.get(1).split(" ")[1]) / 2;
					summe += goldCount;
				}
			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldestDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Bilanz: ");
		System.out.println(summe + " Gold");
	}

	private static void readGoldTransactionProSpieler(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Gold Transaktionen pro Spieler");
		System.out.println("---------------------------------");

		Map<String, Integer> einzahlung = Maps.newHashMap();
		Map<String, Integer> entnahme = Maps.newHashMap();
		Map<String, Integer> umbuchung = Maps.newHashMap();
		Date newestDate = null;
		Date oldestDate = null;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Entnahme")) {
				for (String line : block) {
					if (line.contains("Gold")) {
						Date date = getDate(block.get(0));
						newestDate = getNewestDate(newestDate, date);
						oldestDate = getOldesDate(oldestDate, date);

						String name = getUserName(block.get(0), "Entnahme");
						int goldCount = Integer.parseInt(line.split(" ")[0]);

						if (name != null) {
							if (entnahme.containsKey(name)) {
								Integer value = entnahme.get(name);
								entnahme.put(name, value + goldCount);
							} else {
								entnahme.put(name, goldCount);
							}
						}
					}
				}
			}

			if (block.get(0).contains("Einzahlung")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldestDate = getOldesDate(oldestDate, date);

				String name = getUserName(block.get(0), "Einzahlung");
				int goldCount = 0;
				if (block.get(1).contains("Gold")) {
					goldCount = Integer.parseInt(block.get(1).split(" ")[0]);

				}

				if (name != null) {
					if (einzahlung.containsKey(name)) {
						Integer value = einzahlung.get(name);
						einzahlung.put(name, value + goldCount);
					} else {
						einzahlung.put(name, goldCount);
					}
				}

			}

			if (block.get(0).contains("Umbuchung")) {
				Date date = getDate(block.get(0));
				newestDate = getNewestDate(newestDate, date);
				oldestDate = getOldesDate(oldestDate, date);

				String name = getUserName(block.get(0), "Umbuchung");
				int goldCount = 0;
				if (block.get(1).contains("Gold")) {
					goldCount = Integer.parseInt(block.get(1).split(" ")[1]) / 2;

				}

				if (name != null) {
					if (umbuchung.containsKey(name)) {
						Integer value = umbuchung.get(name);
						umbuchung.put(name, value + goldCount);
					} else {
						umbuchung.put(name, goldCount);
					}
				}

			}
		}

		System.out.println("Erste Transaktion: " + convertDateToString(oldestDate));
		System.out.println("Letzte Transaktion: " + convertDateToString(newestDate));
		System.out.println("");
		System.out.println("Einzahlung: ");
		einzahlung.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> System.out.println(entrySet.getKey() + " " + entrySet.getValue()));
		System.out.println("");
		System.out.println("Entnahme: ");
		entnahme.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> System.out.println(entrySet.getKey() + " " + entrySet.getValue()));
		System.out.println("");
		System.out.println("Umbuchung vom Gildenlager zur Stadt: ");
		umbuchung.entrySet().stream().sorted(Map.Entry.<String, Integer> comparingByValue().reversed())
				.forEachOrdered(entrySet -> System.out.println(entrySet.getKey() + " " + entrySet.getValue()));

	}

	private static String convertDateToString(Date time) {
		return new SimpleDateFormat("dd.MM.yyyy").format(time);
	}

	private static Date getDate(String line) {
		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = format.parse(line.split(" ")[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;

	}

	private static String getUserName(String line, String lineEnding) {
		String name = null;
		String nameRegex = "^\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d \\d\\d:\\d\\d (.*) " + lineEnding;
		Pattern pattern = Pattern.compile(nameRegex);
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			name = matcher.group(1);
		}
		return name;
	}

	private static List<List<String>> segmentFileByDate(FileParser fileToParse) {
		List<List<String>> segmentedFile = Lists.newArrayList();
		List<String> actualblock = null;
		for (String line : fileToParse._fileAsArrayList) {

			if (line.matches("^\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d.*")) {
				actualblock = Lists.newArrayList();
				segmentedFile.add(actualblock);
			}
			if (actualblock != null) {
				actualblock.add(line);
			}
		}
		return segmentedFile;
	}

	private static void readBaumeister(List<List<String>> segmentFileByDate) {
		System.out.println("---------------------------------");
		System.out.println("Baumeister");
		System.out.println("---------------------------------");
		int strukturpunkte = 0;
		Map<String, Integer> entnommen = Maps.newHashMap();
		int days = 0;
		for (List<String> block : segmentFileByDate) {
			if (block.get(0).contains("Baumeister")) {
				int i = 0;
				days++;
				for (String line : block) {
					String[] splitedLine = line.split(" ");
					if (i > 0) {
						String gegenstandsName = splitedLine[1];
						String anzahlEntnommen = splitedLine[0];
						if (entnommen.containsKey(gegenstandsName)) {
							Integer value = entnommen.get(gegenstandsName);
							entnommen.put(gegenstandsName, value + Integer.parseInt(anzahlEntnommen));
						} else {
							entnommen.put(gegenstandsName, Integer.parseInt(anzahlEntnommen));
						}
					} else {
						strukturpunkte += Integer.parseInt(splitedLine[3]);
					}
					i++;
				}
			}
		}
		System.out.println("Strukturpunkte hergestellt insgesammt: " + strukturpunkte);
		System.out.println("Beobachtete Tage: " + days);
		entnommen.forEach((k, v) -> System.out.println(v + " " + k));
	}
}
