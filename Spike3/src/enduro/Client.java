package enduro;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.Document;

public class Client extends JFrame {
	private BufferedReader in;
	private PrintWriter out;

	private JLabel startNumberLabel;
	private JTextField startNumberTF;
	private JEditorPane results;
	private JButton register;
	private JButton getResults;

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
		getResults = new JButton("Get Results");
	}

	private void setUp() {
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		setLayout(new BorderLayout());
		JPanel topRow = new JPanel();
		topRow.add(startNumberLabel);
		topRow.add(startNumberTF);
		topRow.add(register);
		topRow.add(getResults);
		add(topRow, BorderLayout.NORTH);
		JScrollPane sp = new JScrollPane(results);
		add(sp, BorderLayout.CENTER);

		startNumberTF.setColumns(20);
		
		ActionListener al = new RegisterListener();
		register.addActionListener(al);
		startNumberTF.addActionListener(al);
		
		getResults.addActionListener(new GetResultsListener());
		
		results.setMargin(new Insets(10,dim.width/8,10,dim.width/8)); 
		
		setSize(dim.width/2,dim.height/2);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private boolean isNumeric(String s) {
		return s.matches("[0-9]+");

	}

	public String sendToServer(String startNumber, long time) throws IOException {
		Socket socket = new Socket("localhost", 4242);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		out.println(startNumber);
		out.println(time);

		String msg = in.readLine();
		socket.close();
		return msg;
	}
	
	private void register() {
		String input = startNumberTF.getText();
		if (isNumeric(input)) {
			try {
				sendToServer(input, System.currentTimeMillis());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null,
						"Could not connect to server");
			}
			startNumberTF.setText("");
		} else {
			JOptionPane.showMessageDialog(null, "Must be number");
		}
	}
	

	private class RegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			register();
		}
	}
	
	private class GetResultsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Document doc = results.getDocument();
			doc.putProperty(Document.StreamDescriptionProperty, null);
			try {
				results.setPage("http://users.student.lth.se/fte10kso/results.html");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new Client();
	}

}
