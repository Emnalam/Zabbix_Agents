import java.io.File;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import config.Day;
import config.Days;
import config.Periodic;
import config.Settings;
import config.WorkerInfos;
import data.common.AgentVersion;
import utils.DateUtils;
import utils.OS;
import utils.TripleDes;
import workers.AgentLogger;
import workers.AgentLogger.MyLogManager;
import workers.morning.ClientFile;
import workers.morning.MasterFile;
import workers.morning.MorningReport;
import workers.Worker;

public class Starter {

	static {
		System.setProperty("java.util.logging.manager", MyLogManager.class.getName());
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
	}

	private static ScheduledExecutorService scheduler;

	private static List<DailyRunnerDaemon> dailyTasks = new ArrayList<DailyRunnerDaemon>();

	
  /** 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {

		try {

			if (args.length == 0) {
				String message = "Usage: please check the startAgents script to launch the agents.";
				System.out.print(message);
				AgentLogger.logInfos(message);
			} else {
				// Initiate startup time
				DateUtils.getStartupTime();

				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						AgentLogger.logInfos("Begin shutdown....");
						Worker.destroyRunningProcesses();
						Path currentRelativePath = Paths.get("");
						Path currentDirectory = currentRelativePath.toAbsolutePath();
						File running = new File(currentDirectory.toString(), "running");
						if (running.exists() && running.isFile()) {
							running.delete();
							AgentLogger.logInfos("Deleted 'running' file...");
						}
						AgentLogger.logInfos("End shutdown....");
					}
				});

				URL jarURL = Starter.class.getResource("Starter.class");
				URLConnection urlConnection = jarURL.openConnection();

				if (JarURLConnection.class.isInstance(urlConnection)) {
					JarURLConnection jurlConn = (JarURLConnection) jarURL.openConnection();
					Manifest mf = jurlConn.getManifest();
					Attributes atts = mf.getMainAttributes();
					AgentVersion.buildVersion = atts.getValue("Build-Jdk");
					AgentVersion.implementationVersion = atts.getValue("Implementation-Version");
				}

				if (args[0].trim().toLowerCase().equals("version")) {
					System.out.println("Implementation-Version: " + AgentVersion.implementationVersion);
					System.out.println("Build-Jdk: " + AgentVersion.buildVersion);

					return;
        }
        
        if (args[0].trim().toLowerCase().equals("os_uptime")) {
					System.out.println(OS.getSystemUptime());
					return;
				}

				if (args[0].trim().toLowerCase().equals("merge_morning_files")) {
					if (args.length != 5) {
						throw new Exception(
								"To merge the morning files, the syntax is: java -jar agents.jar merge_morning_files <path_to_master_config_file> <path_to_client_config_file> <path_to_agents_fragment_files> <path_to_output_file>");
					}
					MasterFile mf = MasterFile.getInstance(args[1]);
					ClientFile cf = mf.inferClientFile(args[2]);
					cf.mergeOutputs(args[3]);
					cf.save(args[4]);
					AgentLogger.logInfos("Morning files merged. Output file saved here: " + args[4]);
					return;
        }
        
        if (args[0].trim().toLowerCase().equals("create_client_morning_file")) {
					if (args.length != 4) {
						throw new Exception(
								"To create a client morning file, the syntax is: java -jar agents.jar create_client_morning_file <path_to_master_config_file> <client_name> <output_file>");
					}
					MasterFile mf = MasterFile.getInstance(args[1]);
					ClientFile cf = mf.createClientFile(args[2]);
					cf.save(args[3]);
					AgentLogger.logInfos("Client Morning file created. Output file saved here: " + args[3]);
					return;
        }

        if (args[0].trim().toLowerCase().equals("generate_morning_report")) {
					if (args.length < 3) {
						throw new Exception(
								"To generate the morning report, the syntax is: java -jar agents.jar generate_morning_report <path_to_merged_morning_file> <output_file> (OPTIONAL)<path_to_xsl_transform_file>");
          }
          String xslFile = (args.length == 4 ? args[3] : null);

					String html = MorningReport.getHtml(args[1], xslFile);
          Files.write(Paths.get(args[2]), html.getBytes());
					AgentLogger.logInfos("Morning report generated. Output file saved here: " + args[2]);
					return;
        }
        


				if (args[0].trim().toLowerCase().equals("encrypt")) {
					if (args.length != 2) {
						throw new Exception(
								"to encrypt a value, the syntax is: java -jar agents.jar encrypt your_value");
					}

					String value = args[1];

					TripleDes tDes = new TripleDes();
					System.out.println("Encrypted value is: " + tDes.encrypt(value));

					return;
				}

				Settings settings = Settings.loadFromXml(args[0]);

				String checkConfigOnly = null;

				if (args.length >= 2) {
					checkConfigOnly = args[1];
				}

				AgentLogger.init(settings.getLogLevel(), settings.getLogFile(), settings.getLogFileSizeInBytes(),
						settings.getLogFileCount(), settings.isLogToConsole(), settings.isLogToFile(),
						settings.isTestMode());

				AgentLogger.logInfos("Starting nwamon version " + AgentVersion.implementationVersion + " with "
						+ settings.getWorkers().getWorkersInfos().size() + " workers...");

				if (settings.getMorning() != null) {
					AgentLogger.logInfos("Morning configuration detected....");
				}

				List<Worker> workers = new ArrayList<Worker>();
				List<WorkerInfos> workerInfos = new ArrayList<WorkerInfos>();

				List<WorkerInfos> allWorkerInfos = new ArrayList<WorkerInfos>();

				allWorkerInfos.addAll(settings.getWorkers().getWorkersInfos());

				for (WorkerInfos workerInfo : settings.getMorning().getWorkersInfos()) {
					workerInfo.setScheduling(settings.getMorning().getScheduling());
					allWorkerInfos.add(workerInfo);
				}

				for (WorkerInfos workerInfo : allWorkerInfos) {
					ensureProductName(workerInfo);
					ensureSubProductName(workerInfo);

					AgentLogger
							.logInfos("Initiating worker " + workerInfo.getClassname() + ", " + workerInfo.getName());

					String classname = workerInfo.getClassname();

					Class<?> clazz = Class.forName(classname);
					Constructor<?> ctor = clazz.getConstructor();
					Worker worker = (Worker) ctor.newInstance();

					worker.initOnly(workerInfo);
					workers.add(worker);
					workerInfos.add(workerInfo);
					AgentLogger.logInfos(
							"Successfully initiated worker " + workerInfo.getClassname() + ", " + workerInfo.getName());

				}

				if (checkConfigOnly == null) {
					for (int i = 0; i < workers.size(); i++) {
						workers.get(i).start();
					}

					if (settings.getUseScheduler()) {
						for (WorkerInfos workerInfo : workerInfos) {
							if (workerInfo.getScheduling() != null && workerInfo.getScheduling().isEnabled()) {
								Periodic periodic = workerInfo.getScheduling().getPeriodic();
								Days days = workerInfo.getScheduling().getDays();

								long frequence = settings.getDefaultFrequenceMilliseconds();

								if (periodic != null) {
									if (periodic.getFrequence() > 0) {
										frequence = workerInfo.getScheduling().getPeriodic().getFrequence();
									}
								}

								if (frequence > 0 && (periodic != null || (periodic == null && days == null)
										|| (days != null && (days.getDays() == null || days.getDays().size() == 0)))) {
									RunnableTask task = new RunnableTask(workerInfo);

									try {
										scheduler = Executors.newScheduledThreadPool(1);
										scheduler.scheduleAtFixedRate(task, frequence, frequence,
												TimeUnit.MILLISECONDS);
									} catch (Exception exc) {
										String message = "Failed to schedule agent " + workerInfo.getClassname() + ", "
												+ workerInfo.getName() + ". Frequence was: " + frequence;

										message += System.lineSeparator() + exc.toString();

										throw new Exception(message);
									}
									AgentLogger.logInfos("Scheduling worker " + workerInfo.getClassname() + ", "
											+ workerInfo.getName() + ", with periodic frequence " + frequence
											+ " Milliseconds");
								}

								if (days != null && days.getDays() != null && days.getDays().size() > 0) {
									SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

									for (Day day : days.getDays()) {
										String desc = day.getName() + " " + day.getTime();

										Calendar calTime = Calendar.getInstance();

										calTime.setTime(sdf.parse(day.getTime()));

										DailyRunnerDaemon dailyTask = new DailyRunnerDaemon(day.getName(), calTime,
												new RunnableTask(workerInfo), desc);
										dailyTasks.add(dailyTask);
										dailyTask.start();

										AgentLogger.logInfos("Scheduling worker " + workerInfo.getClassname() + ", "
												+ workerInfo.getName() + ", with daily execution defined as: " + desc);
									}
								}
							} else {
								AgentLogger.logInfos("Worker " + workerInfo.getClassname() + ", " + workerInfo.getName()
										+ ", removed from scheduler");
							}
						}
					}
				}

				ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
				exec.scheduleAtFixedRate(new UpdateChecker(), 0, 1, TimeUnit.MINUTES);
			}

		} catch (Exception e) {
			AgentLogger.logStdErr(e, null);
		}

	}

	
  /** 
   * @param workerInfo
   * @throws Exception
   */
  private static void ensureSubProductName(WorkerInfos workerInfo) throws Exception {
		String subProductWorker = workerInfo.getSubproduct();

		if (subProductWorker == null || subProductWorker.length() == 0) {
			String classname = workerInfo.getClassname();

			if (classname.equals("workers.common.FileProgressMonitorWorker")) {
				workerInfo.setSubproduct("file_progress");
			}
		}
	}

	
  /** 
   * @param workerInfo
   * @throws Exception
   */
  private static void ensureProductName(WorkerInfos workerInfo) throws Exception {
		String productWorker = workerInfo.getProduct();

		if (productWorker == null || productWorker.length() == 0) {
			String classname = workerInfo.getClassname();

			if (classname.contains(".apsys")) {
				workerInfo.setProduct("apsys");
			} else if (classname.contains(".b250")) {
				workerInfo.setProduct("bf");
			} else if (classname.contains(".ebanking")) {
				workerInfo.setProduct("ebk");
			} else if (classname.contains(".cim")) {
				workerInfo.setProduct("cim");
			} else if (classname.contains(".common")) {
				workerInfo.setProduct("common");
			} else if (classname.contains(".equalizer")) {
				workerInfo.setProduct("eqz");
			} else if (classname.contains(".integrator")) {
				workerInfo.setProduct("integrator");
			} else if (classname.contains(".las")) {
				workerInfo.setProduct("las");
			} else if (classname.contains(".pao")) {
				workerInfo.setProduct("pao");
			}
		}
	}
}
