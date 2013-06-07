package com.inovex.zabbixmobile.model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class Event extends BaseDaoEnabled<Event, Integer> {

	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(index = true)
	String string;
	@DatabaseField
	long millis;
	@DatabaseField
	Date date;
	@DatabaseField
	boolean even;
	@DatabaseField
	String name;

	Event() {
		// needed by ormlite
	}

	public Event(long millis) {
		this.date = new Date(millis);
		this.string = (millis % 1000) + "ms";
		this.millis = millis;
		this.even = ((millis % 2) == 0);
		String millisString = String.valueOf(millis);
		this.name = "SimpleData" + millisString.substring(millisString.length() - 3, millisString.length() - 1);
	}
	
	public Event(DatabaseHelper databaseHelper, long millis) throws SQLException {
		this(millis);
		dao = databaseHelper.getDao(Event.class);
		create();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("string=").append(string);
		sb.append(", ").append("millis=").append(millis);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		sb.append(", ").append("date=").append(dateFormatter.format(date));
		sb.append(", ").append("even=").append(even);
		sb.append(", ").append("name=").append(name);
		return sb.toString();
	}
}
