package org.beuk.idro.calendar.entity;

import java.util.*;

import org.beuk.idro.calendar.logic.*;

public class Casa {

	public int houseNumber;
	public String name;
	public List<PeriodBooking> periodBookings;
	public List<HouseBooking> houseBookings = new ArrayList<>();
	public String link;
	public String state;

	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		sb.append("name: ").append(name).append(IdroController.newline);
		sb.append("housenumber: ").append(houseNumber).append(IdroController.newline);
		sb.append("link: ").append(link).append(IdroController.newline);
		sb.append("state: ").append(state).append(IdroController.newline);
		for (final HouseBooking houseBooking : houseBookings) {
			sb.append(houseBooking.toString());
		}
		return sb.toString();
	}
}
