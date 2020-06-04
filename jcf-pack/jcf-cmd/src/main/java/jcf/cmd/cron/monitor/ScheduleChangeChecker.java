package jcf.cmd.cron.monitor;



public interface ScheduleChangeChecker {

	boolean isModified();
	
	void load(ScheduleUpdater updater);

}
