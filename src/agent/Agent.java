package agent;

import java.io.Serializable;
import java.util.LinkedList;

import exception.MigrationException;

import mail.AccountInfo;

public interface Agent extends Serializable {

	public void execute();
	public void migrate();
	public void trace();
	public void sendReport();
	public void sendStmpReport(String mail, Log report);
	public void incrementMigrate();
	public void addToReport(Log nodeReport);
	public LinkedList<Log> getReport();
	public boolean finishedExecution();
	public void setMailAccount(AccountInfo mailAccount);
	public void migrateToRoot(int numTries) throws MigrationException;
	public boolean isFinalReport();
}
