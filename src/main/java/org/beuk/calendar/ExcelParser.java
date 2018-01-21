package org.beuk.calendar;

import java.io.*;
import java.util.*;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.*;

import com.google.common.collect.*;

public class ExcelParser {

	static String cellColumnToExcelLetter(int column) {

		final int base = 'A';
		final int baseWithOffset = base + column;

		final String columnLetter = String.valueOf((char) baseWithOffset);

		return columnLetter;
	}

	static int cellRowToExcelRow(int row) {

		return row + 1;
	}

	public TreeBasedTable<Integer, String, String> parseFile(FileInputStream file) {

		try {

			final TreeBasedTable<Integer, String, String> result = TreeBasedTable.create();

			new WorkbookFactory();
			// final Workbook workbook = WorkbookFactory.create(in);
			// final Workbook workbook = new XSSFWorkbook(file);
			final Workbook workbook = WorkbookFactory.create(file);

			final Sheet sheet = workbook.getSheetAt(0);

			final Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()) {
				final Row row = rowIterator.next();

				final Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					final Cell cell = cellIterator.next();

					result.put(cellRowToExcelRow(cell.getRowIndex()), cellColumnToExcelLetter(cell.getColumnIndex()), getCellValueAsString(cell));
				}
			}

			return result;
		} catch (final IOException | InvalidFormatException e) {

			throw new RuntimeException("Unexpected Exportion while parsing excel data", e);
		}
	}

	/*
	 * NB: This method is package protected because I need to test it with BOOLEAN/ERROR and unmapped cell types, and i
	 * can not get them into an excel.
	 */
	String getCellValueAsString(Cell cell) {

		switch (cell.getCellType()) {

			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					final Date localDate = cell.getDateCellValue();

					if (new DateTime(localDate.getTime()).getMillisOfSecond() != 0)
						throw new IllegalArgumentException(cell.getColumnIndex() + " " + cell.getRowIndex() + " ERROR Cannot parse milliseconds/frames of a date field. Please change it to a text field.");

					@SuppressWarnings("deprecation")
					final DateTime dt = new DateTime(Math.max(1970, localDate.getYear() + 1900), localDate.getMonth() + 1, localDate.getDate(), localDate.getHours(), localDate.getMinutes(), localDate.getSeconds(), 0, DateTimeZone.UTC);
					return String.valueOf(dt.getMillis());
				} else {
					final Double numericValue = (cell.getNumericCellValue());
					final long longPart = numericValue.longValue();
					// Make sure numeric values without decimals are returned
					// without the decimal point
					if (numericValue - longPart > 0)
						return String.valueOf(numericValue);
					else
						return String.valueOf(longPart);
				}
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_FORMULA:
				throw new IllegalArgumentException(cell.getColumnIndex() + " " + cell.getRowIndex() + " FORMULA, Cannot parse cell of type FORMULA");
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_ERROR:
				throw new IllegalArgumentException(cell.getColumnIndex() + " " + cell.getRowIndex() + " ERROR, Cannot parse cell of type ERROR");
			default:
				throw new IllegalArgumentException(cell.getColumnIndex() + " " + cell.getRowIndex() + " UNKNOWN, Cannot parse cell of type UNKNOWN");
		}
	}
}