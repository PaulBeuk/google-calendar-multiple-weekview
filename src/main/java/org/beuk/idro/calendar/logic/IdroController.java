package org.beuk.idro.calendar.logic;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.Configuration;
import org.apache.xmlrpc.*;
import org.beuk.idro.calendar.*;
import org.beuk.idro.calendar.dto.*;
import org.beuk.idro.calendar.entity.*;
import org.beuk.service.calendar.*;
import org.beuk.service.template.*;
import org.beuk.wordpress.api.client.*;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.*;

import com.google.api.services.calendar.Calendar;

import freemarker.template.*;

public class IdroController {

	public class CalDoc {

		public int year;
		public List<Month> months;
	}

	// google-calendar-multiple-weekview
	public static final char newline = '\n';

	public static void main(String[] args) throws Exception {

		final IdroController idro = new IdroController();
		idro.readCalendar();
		idro.fillCalendar();
		idro.matchBookingsWithHouses();
		final String html = idro.toTemplate();
		idro.toWordpress("kalender 2017", html);
		System.out.println("ready");
	}

	final SimpleDateFormat sd = new SimpleDateFormat("EE, d MMM yyyy HH:mm", FillCalendar.locale);

	XMLRPCController xmlrpcController;

	TemplateController templateController;
	CalendarController calendarController;
	IdroCalendarController idroCalendarController;
	final String JSONOutput = "calendar.json";
	private final int year;
	final ObjectMapper objectMapper;

	List<Casa> casas;

	public List<Month> months;

	public Configuration configuration;

	FillCalendar fillCalendar;

	public IdroController() throws IOException, XmlRpcException, ConfigurationException {
		year = 2017;
		fillCalendar = new FillCalendar();
		objectMapper = new ObjectMapper();
		final String propertyFile = System.getProperty("configPropertyFile");
		if (propertyFile == null) {
			System.err.println("configPropertyFile not set use -DconfigPropertyFile=<file>");
			System.exit(1);
		}
		configuration = new PropertiesConfiguration(propertyFile);
		xmlrpcController = new XMLRPCController(configuration);
		calendarController = new CalendarController(configuration);
		idroCalendarController = new IdroCalendarController();
	}

	public void fillCalendar() {

		months = fillCalendar.fillMonths(year);
		for (final Month month : months) {
			if (month.number == 6) {
				month.active = true;
			}
		}
	}

	protected void toJSONFile(CalDTO dto) throws IOException {

		final String parsed = objectMapper.writeValueAsString(dto);
		System.out.println("writing to: " + JSONOutput);
		Files.write(Paths.get(JSONOutput), parsed.getBytes());
	}

	BookingState getRandomState(Random rand) {

		BookingState state = BookingState.FREE;
		final int i = rand.nextInt(15);
		switch (i) {
			case 0:
			case 1:
			case 2:
			case 3:
				state = BookingState.BOOKED;
				break;
			case 4:
			case 5:
			case 6:
				state = BookingState.FREE;
				break;
			case 7:
				state = BookingState.FREE;
				break;
		}
		return state;
	}

	void makeTestSet() {

		final Random rand = new Random();

		for (final Month month : months) {
			if (month.number == 6) {
				month.active = true;
			}
			month.casas = new ArrayList<>();
			for (final Casa casa : casas) {
				final Casa newCasa = new Casa();
				newCasa.name = casa.name;
				newCasa.houseNumber = casa.houseNumber;
				newCasa.periodBookings = new ArrayList<>();
				for (final Integer weekNumber : month.weeks.keySet()) {
					final Week week = month.weeks.get(weekNumber);
					final PeriodBooking booking = new PeriodBooking();
					booking.weekNumber = week.weekNumber;
					booking.casaNumber = casa.houseNumber;
					booking.state = getRandomState(rand);
					newCasa.periodBookings.add(booking);
				}
				month.casas.add(newCasa);
			}
		}
	}

	void matchBookingsWithHouses() {

		/* loop over the months */
		for (final Month month : months) {
			month.casas = new ArrayList<>();
			/* loop over the casas */
			for (final Casa casa : casas) {
				final Casa newCasa = new Casa();
				newCasa.name = casa.name;
				newCasa.link = casa.link;
				newCasa.houseNumber = casa.houseNumber;
				newCasa.periodBookings = new ArrayList<>();
				for (final Integer weekNumber : month.weeks.keySet()) {
					final Week week = month.weeks.get(weekNumber);
					final PeriodBooking booking = new PeriodBooking();
					booking.weekNumber = week.weekNumber;
					booking.casaNumber = casa.houseNumber;
					booking.state = matchWeek(week, casa.houseBookings);
					// if (casa.houseBookings.size() > 0)
					// System.out.println(casa.houseNumber + " w: " + weekNumber + " hb: " + casa.houseBookings.size() +
					// " st " + booking.state);
					newCasa.periodBookings.add(booking);
				}
				// System.out.println("newCasa: " + newCasa.periodBookings.size());
				month.casas.add(newCasa);
			}
		}
	}

	/* is there a match for this week */
	BookingState matchWeek(Week week, List<HouseBooking> houseBookings) {

		/* no bookings, always free */
		if (houseBookings.isEmpty())
			return BookingState.FREE;

		/* loop over the bookings */
		for (final HouseBooking houseBooking : houseBookings) {
			boolean booked = false;

			/* begint in deze week een vakantie */
			if (week.startTime <= houseBooking.start && week.endTime >= houseBooking.start)
				booked = true;

			/* valt deze week in een vakantie */
			if (week.startTime >= houseBooking.start && week.endTime <= houseBooking.end)
				booked = true;

			/* eindigt in deze week een vakantie */
			if (week.startTime <= houseBooking.end && week.endTime >= houseBooking.end)
				booked = true;

			if (booked)
				return BookingState.BOOKED;
		}
		return BookingState.FREE;
	}

	void readCalendar() throws Exception {

		final Calendar calendarService = calendarController.getCalendarService();
		final List<Casa> calendarList = idroCalendarController.getCalendarList(calendarService);
		casas = new ArrayList<>();
		System.out.println("valid casas: " + calendarList.size());
		for (final Casa casa : calendarList) {
			System.out.println(casa.name + " state: " + casa.state);
			if (casa.state.equals("rent")) {
				casas.add(casa);
				for (final HouseBooking b : casa.houseBookings) {
					System.out.println(casa.houseNumber + ": " + sd.format(new Date(b.start)) + " to " + sd.format(new Date(b.end)));
				}
			}
		}

	}

	CalDTO toDTO() throws JsonGenerationException, JsonMappingException, IOException {

		final CalDoc calDoc = new CalDoc();
		calDoc.year = year;
		calDoc.months = months;
		final CalDTO dto = CalDTO.fromCalDoc(calDoc);
		return dto;
	}

	void toWordpress(String title, String html) throws IOException, XmlRpcException {

		final int userid = xmlrpcController.getUserByName("beuk");
		System.out.println("beuk id: " + userid);
		final String[] cats = {};
		xmlrpcController.savePage(title, title.replace(' ', '-'), html, userid, -1, cats);
	}

	private String toTemplate() throws JsonGenerationException, JsonMappingException, IOException, TemplateException {

		final CalDTO dto = toDTO();
		templateController = new TemplateController();
		return templateController.processTemplate(dto, "idro-calendar-weekview");
	}

}
