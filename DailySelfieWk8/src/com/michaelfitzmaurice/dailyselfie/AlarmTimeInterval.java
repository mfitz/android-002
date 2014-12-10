package com.michaelfitzmaurice.dailyselfie;


public class AlarmTimeInterval {
	
	private static final String FIELD_SEPARATOR = ",";
	
	private int days;
	private int hours;
	private int minutes;
	
	public AlarmTimeInterval() {
	    super();
    }
	
	public AlarmTimeInterval(String interval) {
		
		String[] fields = interval.split(FIELD_SEPARATOR);
		if (fields == null || fields.length != 3) {
			throw new IllegalArgumentException(
				"'" + interval + 
				"' is not a valid AlarmTimeInterval representation");
		}
		
		days = Integer.parseInt(fields[0]);
		hours = Integer.parseInt(fields[1]);
		minutes = Integer.parseInt(fields[2]);
	}

	public AlarmTimeInterval(int days, int hours, int minutes) {
	    super();
	    this.days = days;
	    this.hours = hours;
	    this.minutes = minutes;
    }

	public int getDays() {
		return days;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}
	
	public void setDays(int days) {
		this.days = days;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	public boolean isZero() {
		return (days == 0) && (hours == 0) && (minutes == 0);
	}

	@Override
    public String toString() {
	    return "AlarmTimeInterval [days=" + days + ", hours=" + hours
	            + ", minutes=" + minutes + "]";
    }
	
	public String serialiseToString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(days);
		sb.append(FIELD_SEPARATOR);
		sb.append(hours);
		sb.append(FIELD_SEPARATOR);
		sb.append(minutes);
		
		return sb.toString();
	}
	
}
