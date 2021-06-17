package edu.collaboration.tamaa;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class Information {

	private JFrame frmMiddlewareOfMalta;
	UPlanner planner = new UPlanner();
	private String title = "Middleware of MALTA";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Information window = new Information();
					window.frmMiddlewareOfMalta.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Information() {
		initialize();
		planner.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMiddlewareOfMalta = new JFrame();
		frmMiddlewareOfMalta.setResizable(false);
		frmMiddlewareOfMalta.setTitle("Middleware of MALTA");
		frmMiddlewareOfMalta.setBounds(100, 100, 343, 267);
		frmMiddlewareOfMalta.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMiddlewareOfMalta.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("The middleware of MALTA has ...");
		lblNewLabel.setBackground(new Color(240, 240, 240));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(10, 10, 350, 50);
		frmMiddlewareOfMalta.getContentPane().add(lblNewLabel);
		
		JLabel lblStatus = new JLabel("started! ");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblStatus.setBounds(10, 60, 205, 50);
		frmMiddlewareOfMalta.getContentPane().add(lblStatus);
		
		JButton btnStart = new JButton("Start");
		JButton btnStop = new JButton("Stop");
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				planner.exit();
				System.exit(0);
			}
		});
		btnStart.setEnabled(false);
		btnStart.setBounds(130, 182, 85, 21);
		btnStop.setBounds(35, 182, 85, 21);
		frmMiddlewareOfMalta.getContentPane().add(btnStop);
		btnExit.setBounds(220, 182, 85, 21);
		frmMiddlewareOfMalta.getContentPane().add(btnExit);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				planner = new UPlanner();
				planner.start();
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				frmMiddlewareOfMalta.setTitle(title + " - started....");
				lblStatus.setText("started!");
				lblStatus.setBackground(Color.green);
			}
		});
		frmMiddlewareOfMalta.getContentPane().add(btnStart);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				planner.exit();
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				frmMiddlewareOfMalta.setTitle(title + " - stopped....");
				lblStatus.setText("stopped!");
				lblStatus.setBackground(Color.red);
			}
		});
	}
}
