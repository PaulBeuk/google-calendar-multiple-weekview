package org.beuk.idro.calendar.dto;

import java.text.*;
import java.util.*;

import org.beuk.idro.calendar.*;
import org.beuk.idro.calendar.entity.*;
import org.beuk.idro.calendar.logic.IdroController.*;

public class CalDTO {

	public static class MonthDTO {

		public static class CasaDTO {

			public static class PeriodBookingDTO {

				public static PeriodBookingDTO fromBooking(PeriodBooking periodBooking) {

					final PeriodBookingDTO dto = new PeriodBookingDTO();
					dto.state = periodBooking.state;
					return dto;
				}

				public BookingState state;

				public BookingState getState() {

					return state;
				}
			}

			public static CasaDTO fromCasa(Casa casa) {

				final CasaDTO dto = new CasaDTO();
				dto.houseNumber = casa.houseNumber;
				dto.name = casa.name;
				dto.link = casa.link;
				dto.periodBookings = new ArrayList<>();

				for (final PeriodBooking booking : casa.periodBookings) {
					dto.periodBookings.add(PeriodBookingDTO.fromBooking(booking));
				}

				return dto;
			}

			public String name;
			public int houseNumber;
			public String link;
			public List<PeriodBookingDTO> periodBookings;

			public int getHouseNumber() {

				return houseNumber;
			}

			public String getLink() {

				return link;
			}

			public String getName() {

				return name;
			}

			public List<PeriodBookingDTO> getPeriodBookings() {

				return periodBookings;
			}
		}

		public static class WeekDTO {

			public static WeekDTO fromWeek(Week week) {

				final WeekDTO dto = new WeekDTO();
				dto.weekNumber = week.weekNumber;
				dto.start = CalDTO.formatDate(week.start);
				dto.end = CalDTO.formatDate(week.end);
				return dto;
			}

			public String start;

			public String end;

			public int weekNumber;

			public String getEnd() {

				return end;
			}

			public String getStart() {

				return start;
			}

			public int getWeekNumber() {

				return weekNumber;
			}

		}

		public static MonthDTO fromMonth(Month month) {

			final MonthDTO dto = new MonthDTO();
			dto.weeks = new ArrayList<>();
			dto.casas = new ArrayList<>();
			dto.name = month.name;
			dto.state = month.active ? "open" : "closed";
			dto.active = month.active;
			for (final Integer weekNumber : month.weeks.keySet()) {
				final Week week = month.weeks.get(weekNumber);
				dto.weeks.add(WeekDTO.fromWeek(week));
			}
			for (final Casa casa : month.casas) {
				dto.casas.add(CasaDTO.fromCasa(casa));
			}
			return dto;
		}

		private String state;

		public boolean active;

		public String name;

		public List<WeekDTO> weeks;

		public List<CasaDTO> casas;

		public List<CasaDTO> getCasas() {

			return casas;
		}

		public String getName() {

			return name;
		}

		public String getState() {

			return state;
		}

		public List<WeekDTO> getWeeks() {

			return weeks;
		}

		public boolean isActive() {

			return active;
		}
	}

	final static Locale locale = new Locale("NL", "NL");

	final static SimpleDateFormat sd = new SimpleDateFormat("d/M", locale);

	public static String formatDate(Date date) {

		return sd.format(date);
	}

	public static CalDTO fromCalDoc(CalDoc calDoc) {

		final CalDTO dto = new CalDTO();
		dto.months = new ArrayList<>();
		dto.year = "" + calDoc.year;
		for (final Month month : calDoc.months) {
			dto.months.add(MonthDTO.fromMonth(month));
		}
		return dto;
	}

	// final static SimpleDateFormat sMonth = new SimpleDateFormat("MMMM", locale);

	public static CalDTO fromMonthList(List<Month> list) {

		final CalDTO dto = new CalDTO();
		dto.months = new ArrayList<>();
		for (final Month month : list) {
			dto.months.add(MonthDTO.fromMonth(month));
		}
		return dto;
	}

	public List<MonthDTO> months;

	public String year;

	public List<MonthDTO> getMonths() {

		return months;
	}

	public String getYear() {

		return year;
	}
}
