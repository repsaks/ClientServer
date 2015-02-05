package enduro;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame {
	private BufferedReader in;
	private PrintWriter out;

	private JLabel startNumberLabel;
	private JTextField startNumberTF;
	private JEditorPane results;
	private JButton register;

	public Client() {
		initiate();
		setUp();
	}

	private void initiate() {
		startNumberLabel = new JLabel("Start Number: ");
		startNumberTF = new JTextField();
		try {
			results = new JEditorPane(
					"http://users.student.lth.se/fte10kso/results.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		register = new JButton("Register");

	}

	private void setUp() {
		setLayout(new BorderLayout());
		JPanel topRow = new JPanel();
		topRow.add(startNumberLabel);
		topRow.add(startNumberTF);
		topRow.add(register);
		add(topRow, BorderLayout.NORTH);
		add(results, BorderLayout.CENTER);

		startNumberTF.setColumns(20);

		register.addActionListener(new RegisterListener());

		results.setEditable(false);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setResizable(false);
		setVisible(true);
	}

	private boolean isNumeric(String s) {
		return s.matches("[0-9]+");

	}

	public String sendToServer(String startNumber, long time) throws IOException {
		// Make connection and initialize streams
		Socket socket = new Socket("localhost", 9898);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		out.println(startNumber);
		out.println(time);

		// Read Server msg
		String msg = in.readLine();
		socket.close();
		return msg;
	}

	private class RegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String input = startNumberTF.getText();
			if (isNumeric(input)) {
				try {
					String response = sendToServer(input, System.currentTimeMillis());
					JOptionPane.showMessageDialog(null, response);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							"Could not connect to server");
				}
				startNumberTF.setText("");
			} else {
				JOptionPane.showMessageDialog(null, "Must be number");
			}
		}
	}

	public static void main(String[] args) {
		new Client();
	}

}
