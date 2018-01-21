package org.beuk.idro.calendar.entity;

import org.beuk.idro.calendar.*;
import org.beuk.idro.calendar.logic.*;

public class HouseBooking {

	public int weekNumber;
	public int casaNumber;
	public long start;
	public long end;
	public String description;
	public BookingState state;

	@Override
	public String toString() {

		final StringBuilder sb = new StringBuilder();
		sb.append("decription: ").append(description).append(IdroController.newline);
		sb.append("casanumber: ").append(casaNumber).append(IdroController.newline);
		sb.append("state: ").append(state).append(IdroController.newline);
		sb.append("start: ").append(start).append(IdroController.newline);
		sb.append("end: ").append(end).append(IdroController.newline);
		return sb.toString();
	}

}
