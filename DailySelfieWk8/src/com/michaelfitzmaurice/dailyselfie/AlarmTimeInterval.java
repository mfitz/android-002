package com.michaelfitzmaurice.dailyselfie;


public class AlarmTimeInterval {
	
	private static final long ONE_MINUTE_IN_MS = 1000 * 60;
	private static final long ONE_HOUR_IN_MS = ONE_MINUTE_IN_MS * 60;
	private static final long ONE_DAY_IN_MS = ONE_HOUR_IN_MS * 24;
	
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
		
		this.days = Integer.parseInt(fields[0]);
		this.hours = Integer.parseInt(fields[1]);
		this.minutes = Integer.parseInt(fields[2]);
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
		return (toMilliseconds() == 0);
	}
	
	public long toMilliseconds() {
		
		return ( getDays() * ONE_DAY_IN_MS ) 
				+ ( getHours() * ONE_HOUR_IN_MS ) 
				+ ( getMinutes() * ONE_MINUTE_IN_MS );
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
