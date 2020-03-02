import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DBWindow extends JFrame{
	public static final int MAX_ATTR = 7;
	public static final int PADDING = 4;
	public static final String[] TABLES = {"Player", "Team", "Game"};
	
	private JPanel ui;
	private JTextArea output;
	private JScrollPane scroll;
	private JLabel retrieveAll;
	private JLabel whoHave;
	private JComboBox<String> tables;
	private ArrayList<ConditionalOption> conditions;
	private JButton go;
	private JButton add;
	private JButton save;
	private JLabel saveToFile;
	private JTextField filename;

	static Connection conn;
	
	DBWindow(){
		dbSetupExample my = new dbSetupExample();
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team7_905_cfb", my.user, my.pswd);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
			System.exit(0);
		}
		
		setSize(800, 533);
		setTitle("Database GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//output on top
		//"retrieve all" [drop down] "who have" [drop down] [drop down] [text box] [GO button]
		//[+ button]
		output = new JTextArea();
		output.setLineWrap(true);
		output.setEditable(false);
		output.setVisible(true);
		scroll = new JScrollPane(output);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		ui = new JPanel();
		
		conditions = new ArrayList<ConditionalOption>();

		ConditionalOption op = new ConditionalOption();
		conditions.add(op);
		update();
		
		setVisible(true);
	}
	
	public void update() {
		ui.setVisible(false);
		ui.removeAll();
		
		GridBagLayout group = new GridBagLayout();
		ui.setLayout(group);
		
		int ylvl = 0;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.gridwidth = 5;
		c.weightx = 0.5;
		c.ipady = 250;
		ui.add(scroll, c);
		
		ylvl++;
		retrieveAll = new JLabel("Retrieve all");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.03;
		ui.add(retrieveAll, c);

		tables = new JComboBox<String>(TABLES);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = ylvl;
		c.weightx = 0.4;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(tables, c);
		
		whoHave = new JLabel("who have");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = ylvl;
		c.weightx = 0.03;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(whoHave, c);
		
		for(int i = 0; i < conditions.size(); i++) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = ylvl;
			c.weightx = 0.5;
			c.insets = new Insets(0, PADDING, 0, 0);
			ui.add(conditions.get(i), c);
			ylvl++;
		}
		
		go = new JButton("Go");
		go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					//Create SQL Statement
					//Vulnerable to SQL injection
					String query = new String("SELECT * FROM  player");
					if(!conditions.get(0).isEmpty()) {
						query += " WHERE ";
						for (int i = 0; i < conditions.size(); i++) {
							if(i != 0)
								query += " AND ";
							query+= conditions.get(i).toSQL();


						}
					}
					System.out.println(query);
					PreparedStatement pstmt = conn.prepareStatement(query);
					if(pstmt.execute()) { //There were results

						//Return String
						String return_string = new String();

						//Result Data
						ResultSet rs = pstmt.getResultSet();
						ResultSetMetaData md = rs.getMetaData();

						//Add Table Column names
						for(int i=1;i<md.getColumnCount()+1;i++) {
							return_string += md.getColumnName(i) + " ";
						}
						return_string += "\n";

						//Add Table Data
						while(rs.next()) {
							for(int i=1;i<md.getColumnCount()+1;i++) {
								return_string += rs.getString(i) + " ";
							}
							return_string += "\n";
						}

						output.setText(return_string);
					}
				} catch (SQLException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
			}
		});

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = ylvl-1;
		c.weightx = 0;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(go, c);
		
		if(conditions.size() < MAX_ATTR) {
			add = new JButton("+");
			add.addActionListener(new ActionListener() {
	
				@Override
				public void actionPerformed(ActionEvent e) {
						conditions.add(new ConditionalOption());
						update();
				}
				
			});
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = ylvl;
			c.weightx = 0.5;
			ui.add(add, c);
		}
		
		save = new JButton("Save");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = ylvl+1;
		c.weightx = 0.1;
		ui.add(save, c);

		filename = new JTextField();
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = ylvl+1;
		c.weightx = 0.1;
		ui.add(filename, c);

		add(ui);
		
		ui.setVisible(true);
	}
}
