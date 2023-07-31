package workers.apsys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import data.common.CmdExc;
import workers.AgentLogger;
import workers.Worker;

public class WEBAPIWorker extends Worker {

	private String additionalParameters;
	private String command;
	private String fileName = "C:\\NWAMON\\BIN\\dateComptable.txt";

	/**
	 * @throws Exception
	 */
	@Override
	protected void init() throws Exception {
		this.additionalParameters = this.workerInfos.getAdditionalParameters();
		this.command = this.workerInfos.getCommand();

		if (this.command == null || this.command.trim().length() == 0) {
			throw new Exception("Command parameter was not set for " + this.getClass().getName());
		}
	}

	protected String getDateAccount() throws Exception {
		String execCommand = this.command;

		if (this.additionalParameters != null && this.additionalParameters.trim().length() > 0) {
			execCommand = execCommand + " \"" + this.additionalParameters + "\"";
		}

		AgentLogger.logTrace("Command to execute is: " + execCommand);

		String commandOutput = this.executeCommand(execCommand, this.workerInfos.getSuccessExitCode(), null, null);
		commandOutput = commandOutput.trim();

		AgentLogger.logTrace("Command output is: " + commandOutput);
		String date_comptable = commandOutput.substring(31, 39);

		return date_comptable;
	}

	protected String getDateComptableFromFile() throws IOException {

		// We need to provide file path as the parameter:
		// double backquote is to avoid compiler interpret words
		// like \test as \t (ie. as a escape sequence)
		File file = new File(fileName);

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		if (file.exists()) {
			AgentLogger.logInfos("File name: " + file.getName());
			AgentLogger.logInfos("Absolute path: " + file.getAbsolutePath());

		} else {
			System.out.println("The file does not exist.");
		}

		st = br.readLine();
		AgentLogger.logInfos("date comptable from file = " + st);

		return st;
	}

	protected void writeDateComptableInFile(String d) throws IOException {
		PrintWriter writer = new PrintWriter(fileName);
		writer.print("");
		writer.close();

		BufferedWriter w = new BufferedWriter(new FileWriter(fileName));
		w.write(d);

		w.close();
	}

	/**
	 * @throws Exception
	 */
	@Override
	protected void doWork() throws Exception {

		String execCommand = this.command;

		if (this.additionalParameters != null && this.additionalParameters.trim().length() > 0) {
			execCommand = execCommand + " \"" + this.additionalParameters + "\"";
		}

		AgentLogger.logTrace("Command to execute is: " + execCommand);

		String commandOutput = this.executeCommand(execCommand, this.workerInfos.getSuccessExitCode(), null, null);
		commandOutput = commandOutput.trim();

		AgentLogger.logTrace("Command output is: " + commandOutput);
		AgentLogger.logInfos("char = " + commandOutput.substring(31, 39));

		CmdExc result = new CmdExc();
		result.setHttpCode(commandOutput);

		if (commandOutput.isEmpty()) {
			result.setStatus("NOK");
		} else {
			result.setStatus("OK");
			String date_comptable = commandOutput.substring(31, 39);
			if (!date_comptable.equals(getDateComptableFromFile())) {
			
			String commandeRestart = "powershell -Command "+"\""+"&{ start-process powershell -ArgumentList 'C:\\NWAMON\\BIN\\restartIISApsysWebApi.ps1' -Verb runAs}"+"\"";
				 String resultatRestart = this.executeCommand(commandeRestart,
						 this.workerInfos.getSuccessExitCode(), null, null);
						 AgentLogger.logInfos("la requette = "+commandeRestart);


				writeDateComptableInFile(date_comptable);

			}
		}

		this.save(result);
	}

	/**
	 * @throws Exception
	 */
	@Override
	protected void cleanUp() throws Exception {
	}

}