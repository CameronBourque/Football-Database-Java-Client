import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class DBWindow extends JFrame implements ActionListener{
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
	
	DBWindow(){
		setSize(600, 400);
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
		
		conditions = new ArrayList<ConditionalOption>();

		ConditionalOption op = new ConditionalOption();
		conditions.add(op);
		update();
		
		setVisible(true);
	}
	
	public void update() {
		if(ui != null) {
			remove(ui);
		}
		
		ui = new JPanel();
		GridBagLayout group = new GridBagLayout();
		ui.setLayout(group);
		
		int ylvl = 0;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.gridwidth = 5;
		c.weightx = 0.5;
		c.ipady = 200;
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
		go.addActionListener(this);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = ylvl-1;
		c.weightx = 0.5;
		c.insets = new Insets(0, PADDING, 0, 0);
		ui.add(go, c);
		
		add = new JButton("+");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 3;
		c.gridy = ylvl;
		c.weightx = 0.5;
		ui.add(add, c);
		
		add(ui);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Action occured");
		
	}
}
