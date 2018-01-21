package org.beuk.idro.calendar;

import java.text.*;
import java.util.*;

import org.beuk.idro.calendar.entity.*;

public class FillCalendar {

	public final static Locale locale = new Locale("NL", "NL");

	public static void main(String[] args) {

		final int year = 2017;
		final FillCalendar filler = new FillCalendar();
		final List<Month> months = filler.fillMonths(year);
		filler.printMonths(months);
	}

	final public long TWO_DAYS = 1000 * 60 * 60 * 24;

	public int START_OF_WEEK = Calendar.MONDAY;

	final SimpleDateFormat sd = new SimpleDateFormat("EE, d MMM yyyy HH:mm", locale);

	final SimpleDateFormat sMonth = new SimpleDateFormat("MMMM", locale);

	public List<Month> fillMonths(int year) {

		final Calendar cal = Calendar.getInstance(locale);
		cal.setFirstDayOfWeek(START_OF_WEEK);
		// cal.set(year - 1, Calendar.DECEMBER, 1, 0, 0, 0);
		cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
		final List<Month> months = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			final Month month = new Month();
			month.number = cal.get(Calendar.MONTH);
			month.name = sMonth.format(cal.getTime());
			month.weeks = fillWeeks(cal, month.number);
			month.year = cal.get(Calendar.YEAR);
			months.add(month);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
		}
		return months;
	}

	Map<Integer, Week> fillWeeks(Calendar cal, int monthNumber) {

		final Map<Integer, Week> map = new LinkedHashMap<>();
		final int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		final Calendar calCheck = Calendar.getInstance(locale);

		for (int i = 0; i < daysInMonth; i++) {
			cal.set(Calendar.DAY_OF_MONTH, i + 1);
			final int weekNr = new Integer(cal.get(Calendar.WEEK_OF_YEAR));
			Week week = map.get(weekNr);
			if (week == null) {
				week = new Week();
				week.weekNumber = weekNr;
				week.start = getStartOfWeek(weekNr, cal);
				week.startTime = week.start.getTime();
				week.end = getEndOfWeek(weekNr, cal);
				week.endTime = week.end.getTime();
				calCheck.setTimeInMillis(week.endTime);
				if (calCheck.get(Calendar.MONTH) >= monthNumber)
					map.put(weekNr, week);
			}
		}
		return map;
	}

	Date getStartOfWeek(int weekNr, Calendar cal) {

		final Calendar tCal = Calendar.getInstance(locale);
		tCal.setFirstDayOfWeek(START_OF_WEEK);
		tCal.setTime(cal.getTime());
		int t = tCal.get(Calendar.WEEK_OF_YEAR);
		int tel = 0;
		while (tel++ < 10 && weekNr == t) {
			tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) - 1);
			t = tCal.get(Calendar.WEEK_OF_YEAR);
		}
		// tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) + 1);
		tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) - 1);
		return tCal.getTime();
	}

	private Date getEndOfWeek(int weekNr, Calendar cal) {

		final Calendar tCal = Calendar.getInstance(locale);
		tCal.setFirstDayOfWeek(START_OF_WEEK);
		tCal.setTime(cal.getTime());
		int t = tCal.get(Calendar.WEEK_OF_YEAR);
		int tel = 0;
		while (tel++ < 10 && weekNr == t) {
			tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) + 1);
			t = tCal.get(Calendar.WEEK_OF_YEAR);
		}
		// tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) - 1);
		tCal.set(Calendar.DAY_OF_MONTH, tCal.get(Calendar.DAY_OF_MONTH) - 2);
		tCal.setTime(new Date(tCal.getTimeInMillis() - 1000));
		return tCal.getTime();
	}

	private void printMonths(List<Month> months) {

		for (final Month month : months) {
			for (final Integer weekNr : month.weeks.keySet()) {
				final Week week = month.weeks.get(weekNr);
				System.out.println(month.year + " " + month.name + " week: " + week.weekNumber + " start: " + sd.format(week.start) + " end " + sd.format(week.end));
			}
		}
	}
}
