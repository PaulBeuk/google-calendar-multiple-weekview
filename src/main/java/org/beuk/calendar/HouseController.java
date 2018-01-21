package org.beuk.calendar;

import java.io.*;
import java.util.*;

import org.beuk.idro.calendar.entity.*;

import com.google.common.collect.*;

public class HouseController {

	ExcelParser excelParser;

	final String huisjes = "huisjes.xls";

	public HouseController() {
		excelParser = new ExcelParser();
	}

	public List<Casa> getCasas() throws IOException {

		final FileInputStream file = new FileInputStream(new File(huisjes));
		final TreeBasedTable<Integer, String, String> table = excelParser.parseFile(file);
		final List<Casa> casaList = new ArrayList<>();
		for (final int rowKey : table.rowKeySet()) {
			final SortedMap<String, String> row = table.row(rowKey);
			final Casa casa = new Casa();
			casa.name = row.get("B");
			casa.houseNumber = Integer.parseInt(row.get("A"));
			casaList.add(casa);
		}
		return casaList;
	}
}
