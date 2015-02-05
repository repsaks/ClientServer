package enduro;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame {
	private JLabel startNumberLabel;
	private JTextField startNumberTF;
	private JEditorPane results;
	private JButton register;

	public Client() {
		startNumberLabel = new JLabel("Start Number: ");
		startNumberTF = new JTextField();
		results = new JEditorPane();
		register = new JButton("Register");
		setUp();
	}

	private void setUp() {

		setLayout(new GridLayout(2, 1));
		JPanel topRow = new JPanel();
		topRow.add(startNumberLabel);
		topRow.add(startNumberTF);
		topRow.add(register);
		add(topRow);
		add(results);

		startNumberTF.setColumns(20);

		register.addActionListener(new RegisterListener());

		results.setEditable(false);
		results.setText("Test");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}

	private boolean isNumeric(String s) {
		return s.matches("[0-9]+");

	}

	private class RegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String input = startNumberTF.getText();
			if (isNumeric(input)) {
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
