package jcf.extproc.process.policy;

import java.text.DateFormat;
import java.util.Calendar;

public class DailyLogFileKeepingPolicy extends AbstractCalendarBasedLogFileKeepingPolicy {

	private static final long serialVersionUID = 1L;
	
	public DailyLogFileKeepingPolicy(int amount) {
		super(amount);
	}

	@Override
	protected Calendar subtract(Calendar calendar, int amount) {
		calendar.add(Calendar.DAY_OF_MONTH, -amount);
		
		return calendar; 
	}
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		/*
		 * Calendar의 월은 0-11 범위임에 주의.
		 */
		calendar.set(2010, 12 - 1, 10, 11, 10, 30);
		
		calendar.add(Calendar.DAY_OF_MONTH, -15);
		
		System.out.println(DateFormat.getDateTimeInstance().format(calendar.getTime()));
		
	}

}
