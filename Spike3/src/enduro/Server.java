package enduro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Server extends JFrame {


	public Server() {
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
				new RegistrationHandler(listener.accept(), clientNumber++)
						.start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * A private thread to handle capitalization requests on a particular
	 * socket. The client terminates the dialogue by sending a single line
	 * containing only a period.
	 */
	private static class RegistrationHandler extends Thread {
		private Socket socket;
		private int clientNumber;

		public RegistrationHandler(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}

		/**
		 * Services this thread's client by first sending the client a welcome
		 * message then repeatedly reading strings and sending back the
		 * capitalized version of the string.
		 */
		public void run() {
			try {

				// Decorate the streams so we can send characters
				// and not just bytes. Ensure output is flushed
				// after every newline.
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);

				// Get messages from the client, line by line; return them
				// capitalized
				String input = in.readLine();
				long time = Long.parseLong(in.readLine());
				JOptionPane.showMessageDialog(null, "Start number: " + input
						+ "\n Time: " + time);
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

		/**
		 * Logs a simple message. In this case we just write the message to the
		 * server applications standard output.
		 */
		private void log(String message) {
			// System.out.println(message);
		}
	}
}
