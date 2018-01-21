package org.beuk.idro.calendar.entity;

import java.util.*;

public class Month {

	public String name;
	public int number;
	public int year;
	public Map<Integer, Week> weeks;
	public List<Casa> casas;
	public boolean active;
}
