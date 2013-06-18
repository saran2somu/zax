package com.inovex.zabbixmobile.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "triggers")
public class Trigger {

	public static final String COLUMN_TRIGGERID = "triggerid";
	@DatabaseField(id = true, columnName = COLUMN_TRIGGERID)
	long id;
	public static final String COLUMN_DESCRIPTION = "description";
	@DatabaseField(columnName = COLUMN_DESCRIPTION)
	String description;
	public static final String COLUMN_EXPRESSION = "expression";
	@DatabaseField(columnName = COLUMN_EXPRESSION)
	String expression;
	public static final String COLUMN_COMMENTS = "comments";
	@DatabaseField(columnName = COLUMN_COMMENTS)
	String comments;
	public static final String COLUMN_LASTCHANGE = "lastchange";
	@DatabaseField(columnName = COLUMN_LASTCHANGE)
	Date lastChange;
	public static final String COLUMN_PRIORITY = "priority";
	@DatabaseField(columnName = COLUMN_PRIORITY, index = true)
	TriggerSeverity priority;
	public static final String COLUMN_STATUS = "status";
	@DatabaseField(columnName = COLUMN_STATUS)
	int status;
	public static final String COLUMN_VALUE = "value";
	@DatabaseField(columnName = COLUMN_VALUE)
	int value;
	public static final String COLUMN_URL = "url";
	@DatabaseField(columnName = COLUMN_URL)
	String url;

	// @ForeignCollectionField(eager = false)
	ForeignCollection<Host> hosts;
	// @ForeignCollectionField(eager = false)
	ForeignCollection<Item> items;

	public Trigger() {

	}

	public Trigger(long id, String description, String expression,
			String comments, long lastChange, TriggerSeverity priority,
			int status, int value, String url) {
		this.id = id;
		this.description = description;
		this.expression = expression;
		this.comments = comments;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lastChange);
		this.lastChange = cal.getTime();
		this.priority = priority;
		this.status = status;
		this.value = value;
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getExpression() {
		return expression;
	}

	public String getComments() {
		return comments;
	}

	public Date getLastchange() {
		return lastChange;
	}

	public TriggerSeverity getPriority() {
		return priority;
	}

	public int getStatus() {
		return status;
	}

	public int getValue() {
		return value;
	}

	public String getUrl() {
		return url;
	}

	public ForeignCollection<Host> getHosts() {
		return hosts;
	}

	public ForeignCollection<Item> getItems() {
		return items;
	}

	public Date getLastChange() {
		return lastChange;
	}

	public void setLastChange(Date lastChange) {
		this.lastChange = lastChange;
	}

	public void setLastChange(long lastChange) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(lastChange);
		this.lastChange = cal.getTime();
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setPriority(TriggerSeverity priority) {
		this.priority = priority;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("description=").append(description);
		sb.append(", ").append("expression=").append(expression);
		sb.append(", ").append("comments=").append(comments);
		DateFormat dateFormatter = SimpleDateFormat.getDateTimeInstance(
				SimpleDateFormat.DEFAULT, SimpleDateFormat.DEFAULT, Locale.US);
		sb.append(", ").append("lastchange=")
				.append(dateFormatter.format(lastChange));
		sb.append(", ").append("priority=").append(priority);
		sb.append(", ").append("status=").append(status);
		sb.append(", ").append("value=").append(value);
		sb.append(", ").append("url=").append(url);
		return sb.toString();
	}

}
