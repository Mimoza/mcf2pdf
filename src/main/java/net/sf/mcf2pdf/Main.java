/*******************************************************************************
 * ${licenseText}
 *******************************************************************************/
package net.sf.mcf2pdf;

import net.sf.mcf2pdf.mcfelements.util.PdfUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Main entry point of the mcf2pdf application. Creates an
 * <code>Mcf2FoConverter</code> object with the settings passed on command line,
 * and renders the given input file to XSL-FO. If PDF output is requested
 * (the default), the XSL-FO is converted to PDF using Apache FOP. The result
 * (PDF or XSL-FO) is then written to given output file, which can be STDOUT
 * when a dash (-) is passed.
 */
public class Main {

	private static CommandLine commandLine = initCommandLine();
	private  static final Options OPTIONS = initOptions();

	private static Options initOptions(){
		Options options = new Options();

		Option o = OptionBuilder.hasArg().isRequired().withDescription("Installation location of My CEWE Photobook. REQUIRED.").create('i');
		options.addOption(o);
		options.addOption("h", false, "Prints this help and exits.");
		options.addOption("t", true, "Location of MCF temporary files.");
		options.addOption("w", true, "Location for temporary images generated during conversion.");
		options.addOption("r", true, "Sets the resolution to use for page rendering, in DPI. Default is 150.");
		options.addOption("n", true, "Sets the page number to render up to. Default renders all pages.");
		options.addOption("x", false, "Generates only XSL-FO content instead of PDF content.");
		options.addOption("q", false, "Quiet mode - only errors are logged.");
		options.addOption("d", false, "Enables debugging logging output.");

		return options;
	}

	private static CommandLine initCommandLine(String... args){
		try {
			CommandLineParser parser = new PosixParser();
			return parser.parse(OPTIONS, args);
		}
		catch (ParseException pe) {
			printUsage(pe);
			System.exit(3);
			return null;
		}
	}

	@SuppressWarnings("static-access")
	public static void main(String... args) {

		commandLine = initCommandLine(args);

		if (commandLine.hasOption("h")) {
			printUsage(null);
			return;
		}

		if (commandLine.getArgs().length != 2) {
			printUsage(new ParseException("INFILE and OUTFILE must be specified. Arguments were: " + commandLine.getArgList()));
			System.exit(3);
			return;
		}

		File installDir = new File(commandLine.getOptionValue("i"));
		if (!installDir.isDirectory()) {
			printUsage(new ParseException("Specified installation directory does not exist."));
			System.exit(3);
			return;
		}

		File tempDir;
		String sTempDir = commandLine.getOptionValue("t");
		if (sTempDir == null) {
			tempDir = new File(new File(System.getProperty("user.home")), ".mcf");
			if (!tempDir.isDirectory()) {
				printUsage(new ParseException("MCF temporary location not specified and default location " + tempDir + " does not exist."));
				System.exit(3);
				return;
			}
		}
		else {
			tempDir = new File(sTempDir);
			if (!tempDir.isDirectory()) {
				printUsage(new ParseException("Specified temporary location does not exist."));
				System.exit(3);
				return;
			}
		}

		File mcfFile = new File(commandLine.getArgs()[0]);
		if (!mcfFile.isFile()) {
			printUsage(new ParseException("MCF input file does not exist."));
			System.exit(3);
			return;
		}
		mcfFile = mcfFile.getAbsoluteFile();

		File tempImages = new File(new File(System.getProperty("user.home")), ".mcf2pdf");
		if (commandLine.hasOption("w")) {
			tempImages = new File(commandLine.getOptionValue("w"));
			if (!tempImages.mkdirs() && !tempImages.isDirectory()) {
				printUsage(new ParseException("Specified working dir does not exist and could not be created."));
				System.exit(3);
				return;
			}
		}

		int dpi = checkArgument("r",600,30, 150, "Parameter for option -r must be an integer between 30 and 600.");

		int maxPageNo = checkArgument("n",Integer.MAX_VALUE, 0, -1,"Parameter for option -n must be an integer >= 0.");

		OutputStream finalOut;
		if ("-".equals(commandLine.getArgs()[1]))
			finalOut = System.out;
		else {
			try {
				finalOut = new FileOutputStream(commandLine.getArgs()[1]);
			}
			catch (IOException e) {
				printUsage(new ParseException("Output file could not be created."));
				System.exit(3);
				return;
			}
		}

		// configure logging, if no system property is present
		if (System.getProperty("log4j.configuration") == null) {
			PropertyConfigurator.configure(Main.class.getClassLoader().getResource("log4j.properties"));

			Logger.getRootLogger().setLevel(Level.INFO);
			if (commandLine.hasOption("q"))
				Logger.getRootLogger().setLevel(Level.ERROR);
			if (commandLine.hasOption("d"))
				Logger.getRootLogger().setLevel(Level.DEBUG);
		}

		// start conversion to XSL-FO
		// if -x is specified, this is the only thing we do
		OutputStream xslFoOut;
		if (commandLine.hasOption("x")) {
			xslFoOut = finalOut;
		}else {
			xslFoOut = new ByteArrayOutputStream();
		}

		Log log = LogFactory.getLog(Main.class);

		try {
			new Mcf2FoConverter(installDir, tempDir, tempImages).convert(mcfFile, xslFoOut, dpi, maxPageNo);
			xslFoOut.flush();

			if (!commandLine.hasOption("x")) {
				// convert to PDF
				log.debug("Converting XSL-FO data to PDF");
				byte[] data = ((ByteArrayOutputStream)xslFoOut).toByteArray();
				PdfUtil.convertFO2PDF(new ByteArrayInputStream(data), finalOut, dpi);
				finalOut.flush();
			}
		}
		catch (Exception e) {
			log.error("An exception has occured", e);
			System.exit(1);
			return;
		}
		finally {
			if (finalOut instanceof FileOutputStream) {
				try { finalOut.close(); } catch (Exception e) { }
			}
		}
	}

	private static int checkArgument(String optionName, int max, int min, int defaultValue, String errorMessage){
		int argumentValue = defaultValue;
		if (commandLine.hasOption(optionName)) {
			try {
				argumentValue = Integer.parseInt(commandLine.getOptionValue(optionName));
				if (argumentValue < min || argumentValue > max){
					throw new IllegalArgumentException(errorMessage);
				}
			} catch (IllegalArgumentException e) {
				printUsage(new ParseException(errorMessage));
			}
		}
		return argumentValue;
	}

	private static void printUsage(ParseException pe) {
		if (pe != null){
			System.err.println("ERROR: " + pe.getMessage());
		}
		System.out.println();
		System.out.println("mcf2pdf My CEWE Photobook to PDF converter");
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("mcf2pdf <OPTIONS> INFILE OUTFILE", "Options are:", OPTIONS,
				"If -t is not specified, <USER_HOME>/.mcf is used.\n" +
				"If -w is not specified, <USER_HOME>/.mcf2pdf is created and used.\n" +
				"If you specify a dash (-) as OUTFILE, resulting content will be written to STDOUT. Notice that, in that case, temporary image files will be kept in specified image working directory. Notice also that you should add option -q in this case to avoid logging output.");
	}
}
