/**
 * 
 */


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * @author m2330
 * 
 */

public class RTKChecker {
	protected static String wrkTo[] = {"michael.zzz@xxx.com", "David.zzz@xxx.com", "Application.Developer@xxx.com"};
	protected static String workFile = "D:\\dolphinMonitor\\dolphinCount.txt";
//	protected static String workFile = "J:\\DolphinRTKMonitor\\dolphinCount.txt";

	public static void main(String[] args) throws MessagingException {

		
		if (args.length < 1) {
			System.err.println("Please supply a directory name.");
			System.exit(1);
		}
		File dir = new File(args[0]);
		if (!dir.isDirectory()) {
			System.err.println("File " + args[0] + " is not a directory.");
		}
		int ccount = Count(dir);
		// int wfRet = WriteFile(ccount);
		int rcount = ReadFile();
//		semail();

		
		if (rcount != ccount) {
			System.out.println("Counts don't match" + ccount + " - " + rcount);
			System.out.println(WriteFile(ccount));
			String wrkMessage = "The Dolphin MSDS system has a different number of MSDS from last time this monitor was ran! \n" + "Old Count: " + rcount + " New Count: " + ccount;
			postMail(wrkTo,"Dolphin MSDS Count does not match", wrkMessage, "Dolphin_MSDS_Monitor@xxx.com" );

		} else {

			System.out.println("total number of normal files: " + ccount);
			System.out.println(WriteFile(ccount));
			System.out.println(ReadFile());
		}
	}

	// return the number of "normal" (non-directory) files in the given
	// directory and in all subdirectories
	static int Count(File dir) {
		if (!dir.isDirectory()) {
			System.err
					.println("File " + dir.getName() + " is not a directory.");
			System.exit(1);
		}

		int total = 0;
		String[] files = dir.list();
		String thisDir = dir.getPath();

		// count non-directory files in this directory and recurse for each
		// that IS a directory
		for (int k = 0; k < files.length; k++) {
			File f = new File(thisDir + "/" + files[k]);
			if (!f.isDirectory())
				total++;
			else
				total += Count(f);
		}
		return total;
	}

	static int WriteFile(int ccount) throws MessagingException {

		FileOutputStream out; // declare a file output object
		PrintStream p; // declare a print stream object

		try {
			// Create a new file output stream
			// connected to "myfile.txt"
			out = new FileOutputStream(workFile);

			// Connect print stream to the output stream
			p = new PrintStream(out);

			p.println(ccount);

			p.close();
			return 1;
		} catch (Exception e) {
			System.err.println("Error writing to file");
			String wrkMessage = "Error writing to file";
			postMail(wrkTo,wrkMessage, wrkMessage, "Dolphin_MSDS_Monitor@xxx.com" );
			return 0;
		}

	}

	@SuppressWarnings("deprecation")
	static int ReadFile() throws MessagingException {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(workFile);

			// Convert our input stream to a
			// DataInputStream
			DataInputStream in = new DataInputStream(fstream);

			// Continue to read lines while
			// there are still some left to read
			while (in.available() != 0) {
				// Print file line to screen
				String wrkrcount = in.readLine();
				int rcount = Integer.parseInt(wrkrcount);
				System.out.println(rcount);
				System.out.println("howdy");
				return (rcount);
			}

			in.close();

		} catch (Exception e) {
			System.err.println("File input error");
			String wrkMessage = "File input error";
			postMail(wrkTo,wrkMessage, wrkMessage, "Dolphin_MSDS_Monitor@xxx.com" );
			return 0;
		}
		return 0;
	}

/*	static void semail() {
		Runtime runtime = Runtime.getRuntime();
		String path = "C:\\batch.bat";
		String paraStr = "-parameter";
		try {
			runtime.exec(path + " " + paraStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
*/

	public static void postMail(String recipients[], String subject, String message,
			String from) throws MessagingException {
		boolean debug = false;

		// Set the host SMTP address
		Properties props = new Properties();
//		props.put("mail.smtp.host", "10.88.88.98");
		props.put("mail.smtp.host", "10.72.14.15");

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Optional : You can also set your custom headers in the Email if you
		// Want
		msg.addHeader("MyHeaderName", "myHeaderValue");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		Transport.send(msg);
	}
}
