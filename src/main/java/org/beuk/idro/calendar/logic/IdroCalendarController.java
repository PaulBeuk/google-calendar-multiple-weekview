package org.beuk.idro.calendar.logic;

import java.io.*;
import java.util.*;

import org.beuk.idro.calendar.*;
import org.beuk.idro.calendar.entity.*;

import com.google.api.client.util.*;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

public class IdroCalendarController {

	public static String OFFICE_TIMEZONE = "Europe/Amsterdam";
	final static TimeZone timeZone = TimeZone.getTimeZone(OFFICE_TIMEZONE);

	public List<Casa> getCalendarList(Calendar service) throws IOException {

		String pageToken = null;
		final List<Casa> casas = new ArrayList<>();
		do {
			final CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
			final List<CalendarListEntry> listItems = calendarList.getItems();

			for (final CalendarListEntry calendarListEntry : listItems) {
				final Casa casa = listEvents(service, calendarListEntry);
				if (casa != null)
					casas.add(casa);
			}
			pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
		return casas;
	}

	public void listCalendars(Calendar service) throws IOException {

		String pageToken = null;
		do {
			final CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
			final List<CalendarListEntry> listItems = calendarList.getItems();

			for (final CalendarListEntry calendarListEntry : listItems) {
				listEvents(service, calendarListEntry);
			}
			pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);

	}

	private Casa listEvents(Calendar service, CalendarListEntry calendarListEntry) throws IOException {

		final Casa casa;
		final Properties prop = new Properties();
		final String calendarId = calendarListEntry.getId();

		try (final InputStream stream = new ByteArrayInputStream(calendarListEntry.getDescription().getBytes())) {

			prop.load(stream);
			casa = new Casa();
			casa.houseNumber = Integer.parseInt(prop.getProperty("huisnummer"));
			casa.name = prop.getProperty("naam");
			casa.link = prop.getProperty("link");
			casa.state = prop.getProperty("status");
		} catch (final NumberFormatException | NullPointerException e) {
			System.out.println(calendarListEntry.getDescription() + " unable to parse description: " + e.getMessage());
			return null;
		}

		final DateTime now = new DateTime(System.currentTimeMillis());
		final Events events = service.events().list(calendarId).setMaxResults(300).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute();
		final List<Event> items = events.getItems();
		if (items.size() == 0) {
			System.out.println("No upcoming events found.");
		} else {
			for (final Event event : items) {
				DateTime start = event.getStart().getDateTime();
				if (start == null) {
					start = event.getStart().getDate();
				}
				DateTime end = event.getEnd().getDateTime();
				if (end == null) {
					end = event.getEnd().getDate();
				}

				System.out.printf("%d: %s (%s) tot (%s)\n", casa.houseNumber, event.getSummary(), start, end);
				final HouseBooking booking = new HouseBooking();
				booking.casaNumber = casa.houseNumber;
				booking.description = event.getDescription();
				booking.start = start.getValue();
				booking.end = end.getValue() - (1000 * 60 * 60 * 3);
				booking.state = BookingState.BOOKED;
				casa.houseBookings.add(booking);
				// System.out.println(objectMapper.writeValueAsString(event));
			}
		}
		return casa;
	}

}
