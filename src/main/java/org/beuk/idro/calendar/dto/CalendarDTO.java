package org.beuk.idro.calendar.dto;

import java.util.*;

public class CalendarDTO {

	public static class QuarterDTO {

		public static class MonthDTO {

			public static class WeekDTO {

				public static List<WeekDTO> fromList(List<Integer> weeks) {

					final List<WeekDTO> list = new ArrayList<>();
					for (final Integer week : weeks) {
						final WeekDTO dto = new WeekDTO();
						dto.weekNumber = week;
						list.add(dto);
					}
					return list;
				}

				public int weekNumber;
			}

			public static List<MonthDTO> fromMap(LinkedHashMap<Integer, List<Integer>> quarter) {

				final List<MonthDTO> months = new ArrayList<>();
				for (final Integer month : quarter.keySet()) {
					final MonthDTO dto = new MonthDTO();
					dto.monthNumber = month;
					dto.weeks = WeekDTO.fromList(quarter.get(month));
				}
				return months;
			}

			public int monthNumber;
			public List<WeekDTO> weeks = new ArrayList<>();
		}

		public static List<QuarterDTO> fromMap(LinkedHashMap<Integer, LinkedHashMap<Integer, List<Integer>>> calendar) {

			final List<QuarterDTO> quarters = new ArrayList<>();
			for (final Integer quarter : calendar.keySet()) {
				final QuarterDTO dto = new QuarterDTO();
				dto.name = quarter;
				dto.months = MonthDTO.fromMap(calendar.get(quarter));
				quarters.add(dto);
			}
			return quarters;
		}

		public int monthNumber;
		public int name;
		public List<MonthDTO> months = new ArrayList<>();
	}

	public static CalendarDTO fromMap(LinkedHashMap<Integer, LinkedHashMap<Integer, List<Integer>>> calendar) {

		final CalendarDTO dto = new CalendarDTO();
		dto.quarters = QuarterDTO.fromMap(calendar);
		return dto;
	}

	public int year;

	public List<QuarterDTO> quarters;
}
