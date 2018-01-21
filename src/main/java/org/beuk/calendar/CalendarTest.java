package org.beuk.calendar;

import java.io.*;
import java.text.*;
import java.util.*;

import org.beuk.idro.calendar.dto.*;

public class CalendarTest {

	public static class Week {

		public int weekNumber;
		public int month;
		public String monthName;
		public String fromTo;
		final public Character newline = '\n';

		@Override
		public String toString() {

			final StringBuilder sb = new StringBuilder();
			sb.append("weekNumber: ").append(weekNumber).append(newline);
			sb.append("monthName: ").append(monthName).append(newline);
			sb.append("month: ").append(month).append(newline);
			return sb.toString();
		}
	}

	public static void main(String[] args) throws IOException {

		final Locale locale = new Locale("NL", "NL");
		final Calendar cal = Calendar.getInstance(locale);
		for (int i = 2015; i < 2025; i++) {
			cal.set(i, Calendar.DECEMBER, 31, 0, 0, 0);
			System.out.println(i + " last week of year: " + cal.get(Calendar.WEEK_OF_YEAR));
		}
		for (int i = 2015; i < 2025; i++) {
			cal.set(i, Calendar.JANUARY, 1, 0, 0, 0);
			System.out.println(i + " first week of year: " + cal.get(Calendar.WEEK_OF_YEAR));
		}
		cal.set(2015, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.WEEK_OF_YEAR, 1);
		final SimpleDateFormat sd = new SimpleDateFormat("EE, d MMM yyyy HH:mm:ss", locale);
		final SimpleDateFormat sMonth = new SimpleDateFormat("MMMM", locale);
		final Week week = new Week();
		week.month = cal.get(Calendar.MONTH) + 1;
		week.monthName = sMonth.format(cal.getTime());
		week.weekNumber = cal.get(Calendar.WEEK_OF_YEAR);
		System.out.println(week);
		System.out.println("cal date: " + sd.format(cal.getTime()));
		System.out.println("cal week: " + cal.getWeeksInWeekYear());
		System.out.println("cal week: " + cal.get(Calendar.WEEK_OF_YEAR));

		final LinkedHashMap<Integer, LinkedHashMap<Integer, List<Integer>>> calendar = new LinkedHashMap<>();
		for (int kwartaal = 0; kwartaal < 4; kwartaal++) {
			calendar.put(kwartaal + 1, getMaand(kwartaal));
		}
		final CalendarDTO dto = CalendarDTO.fromMap(calendar);
		System.out.println(dto);
	}

	private static LinkedHashMap<Integer, List<Integer>> getMaand(int kwartaal) {

		final LinkedHashMap<Integer, List<Integer>> month = new LinkedHashMap<>();
		for (int maand = 0; maand < 3; maand++) {
			final int maandNumber = maand + (kwartaal * 3);
			month.put(maandNumber + 1, getWeek(maandNumber));
		}
		return month;
	}

	private static List<Integer> getWeek(int maand) {

		final List<Integer> weeks = new ArrayList<>();
		for (int week = 1; week < 5; week++) {
			weeks.add(maand * 4 + week);
		}
		return weeks;
	}
}
