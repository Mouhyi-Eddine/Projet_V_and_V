package results;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Result {

	private static final Logger logger = LoggerFactory.getLogger(Result.class);

	private List<Report> reportsAlive;

	private List<Report> reportsDead;

	private List<Report> reports;

	private String projectName;

	private long startTesting;

	private long stopTesting;

	private int aliveNumber;
	private int totalNumber;

	public Result() {
		reportsAlive = new ArrayList<>();
		reportsDead = new ArrayList<>();
		reports = new ArrayList<>();
	}

	public void addReport(Report report) {
		// If the report is null warn add return
		if (report == null || report.getMutantContainer() == null) {
			logger.warn("Adding a NULL report could be an error.");
			return;
		}

		// Debug logging
		logger.debug("======================================");
		logger.debug("Mutation information :");
		logger.debug("Mutated class {}", report.getMutantContainer().getMutatedClass());
		logger.debug("Mutation method {}", report.getMutantContainer().getMutationMethod());
		logger.debug("Mutation type {}", report.getMutantContainer().getMutantType());
		logger.debug("Mutation testing result : {}", report.isMutantAlive());
		logger.debug("======================================");

		totalNumber++;
		if (report.isMutantAlive()) {
			aliveNumber++;
			String percentageDisplayed = percentage(1 - (double) aliveNumber / totalNumber);
			logger.info("\t\tMutant is still alive. (Total : {}%)", percentageDisplayed);
			reportsAlive.add(report);
		} else {
			String percentageDisplayed = percentage(1 - (double) aliveNumber / totalNumber);
			logger.info("\t\tMutant killed.(Total : {}%)", percentageDisplayed);
			reportsDead.add(report);
		}
		reports.add(report);

		logger.debug("Adding report, total size = {}", totalNumber);

	}

	public void generateCSV() {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date(startTesting);

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter("./results/report-" + df.format(date) + ".csv"))) {
			writer.write(toCSV());
		} catch (IOException e) {
			logger.error("Reporting error during file writing", e);
		}
	}

	public String toCSV() {
		String separator = ";";
		String separatorLine = "\n";
		StringBuilder stringBuilder = new StringBuilder();

		// Adding CSV Headers
		stringBuilder.append("Mutated class");
		stringBuilder.append(separator);
		stringBuilder.append("Mutation type");
		stringBuilder.append(separator);
		stringBuilder.append("Mutated method");
		stringBuilder.append(separator);
		stringBuilder.append("Is mutant still alive ?");
		stringBuilder.append(separatorLine);

		for (Report report : reports) {
			if (report.getMutantContainer() != null) {
				stringBuilder.append(report.getMutantContainer().getMutatedClass());
				stringBuilder.append(separator);
				stringBuilder.append(report.getMutantContainer().getMutantType());
				stringBuilder.append(separator);
				stringBuilder.append(report.getMutantContainer().getMutationMethod());
				stringBuilder.append(separator);
				if (report.isMutantAlive()) {
					stringBuilder.append("TRUE");
				} else {
					stringBuilder.append("FALSE");
				}
			}
			stringBuilder.append(separatorLine);
		}

		return stringBuilder.toString();
	}

	public void startMutationTesting() {
		startTesting = System.currentTimeMillis();
		logger.debug("Set startTesting to {}", startTesting);
	}

	public void stopMutationTesting() {
		stopTesting = System.currentTimeMillis();
		logger.debug("Set stopTesting to {}", stopTesting);

	}

	private String getInterval() {
		DateFormat df = new SimpleDateFormat("[dd/MM/yyyy] HH:mm:ss");

		Date startDate = new Date(startTesting);
		Date endDate = new Date(stopTesting);

		String suffix = " (" + df.format(startDate) + " to " + df.format(endDate) + ")";
		String label = "<b>";
		if (stopTesting >= startTesting) {
			// Second conversion
			long start = startTesting / 1000;
			long stop = stopTesting / 1000;

			int hours = (int) (stop - start) / 3600;
			int minutes = (int) (stop - start) / 60 - hours * 60;
			int seconds = (int) (stop - start) - minutes * 60 - hours * 3600;

			if (hours > 0) {
				label += hours + "h";
			}
			if (minutes > 0) {
				label += minutes + "m";
			}
			if (seconds > 0) {
				label += seconds + "s";
			}
			return label + "</b>" + suffix;
		} else {
			return "ERROR";
		}
	}

	public static String percentage(Double number) {

		// Check value
		if (number < 0 || number > 1) {
			logger.warn("The value {} cannot be converted to a percentage.", number);
			return Double.toString(number * 100);
		} else {
			// Maximal value
			if (number == 1D) {
				return "100.0";
			}

			int integerPart = (int) (number * 100);
			int decimalPart = (int) (number * 10000) - (int) (number * 100) * 100;

			return String.format("%2d.%-2d", integerPart, decimalPart);
		}
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<Report> getReports() {
		return reports;
	}

	public int getAliveNumber() {
		return aliveNumber;
	}

	public int getTotalNumber() {
		return totalNumber;
	}

	public void setStartTesting(long startTesting) {
		this.startTesting = startTesting;
	}

	public void setStopTesting(long stopTesting) {
		this.stopTesting = stopTesting;
	}
}