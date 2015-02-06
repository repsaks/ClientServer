package enduro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;

public class Server extends JFrame {
	public static TreeMap<String, Contestant> contestants = new TreeMap<String, Contestant>();
	public boolean test = false;

	public Server() {
		setSize(100, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public static void main(String[] args) throws Exception {
		new Server();
		System.out.println("Enduro Result Server Running");
		ServerSocket listener = new ServerSocket(9898);
		int clientNumber = 0;
		try {
			while (true) {
				new RegistrationHandler(listener.accept(), clientNumber++,
						contestants).start();
			}
		} finally {
			listener.close();
		}
	}

	public static synchronized void writeToFile() {
		File f = new File("/home/kasper/results.html");
		try {
			PrintWriter pw = new PrintWriter(f);
			fixed1(pw);
			dynamic(pw);
			fixed2(pw);
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String[] cmd = {"/bin/zsh", "-c", "scp ~/results.html fte10kso@login.student.lth.se:~/public_html/results.html"};
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void fixed2(PrintWriter pw) {
		pw.println("</table>");
		pw.println("</body>");
		pw.println("</html>");

	}

	private static void dynamic(PrintWriter pw) {
		for (Map.Entry<String, Contestant> entry : contestants.entrySet()) {
			Contestant c = entry.getValue();
			pw.println("\t<tr>");
			pw.println("\t\t<td>" + entry.getKey() + "</td>");
			pw.println("\t\t<td>" + c.getStart() + "</td>");
			pw.println("\t\t<td>" + c.getFinish() + "</td>");
			pw.println("\t\t<td>" + c.getResult() + "</td>");
			pw.println("\t</tr>");
		}

	}

	private static void fixed1(PrintWriter pw) {
		pw.println("<!DOCTYPE html>");
		pw.println("<html>");
		pw.println("<head>");
		pw.println("\t<meta charset=\"utf-8\">");
		pw.println("\t<title>Enduro - Current Results</title>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("\t<table border=\"1\">");
		pw.println("\t<tr>");
		pw.println("\t\t<th>Start Number</th>");
		pw.println("\t\t<th>Start Time</th>");
		pw.println("\t\t<th>Finish Time</th>");
		pw.println("\t\t<th>Total Time</th>");
		pw.println("\t</tr>");
	}

	/**
	 * A private thread to handle capitalization requests on a particular
	 * socket. The client terminates the dialogue by sending a single line
	 * containing only a period.
	 */
	private static class RegistrationHandler extends Thread {
		private Socket socket;
		private int clientNumber;
		private TreeMap<String, Contestant> contestants;

		public RegistrationHandler(Socket socket, int clientNumber,
				TreeMap<String, Contestant> contestants) throws Exception {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
			this.contestants = contestants;
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);

				String startNumber = in.readLine();
				long time = (Long.parseLong(in.readLine()) /1000) * 1000;

				register(startNumber, time);
				writeToFile();

				out.println("Request recieved");

			} catch (IOException e) {
				log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}

		}

		private void register(String startNumber, long time) {
			Contestant c = contestants.get(startNumber);
			if (c == null) {
				c = new Contestant();
				contestants.put(startNumber, c);
			}
			c.addTime(time);
		}

		private void log(String message) {
			System.out.println(message);
		}
	}
}
