import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.*;

public class DBWindow extends JFrame {
	public static final String[] TABLES = {"Player", "Team", "Game"};
	
	private JPanel ui;
	private JTextArea output;
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
		conditions = new ArrayList<ConditionalOption>();
		//fix
		ConditionalOption op = new ConditionalOption();
		ConditionalOption op2 = new ConditionalOption();
		conditions.add(op);
		conditions.add(op2);
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
		ui.add(output, c);
		
		ylvl++;
		retrieveAll = new JLabel("Retrieve all");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = ylvl;
		c.weightx = 0.5;
		ui.add(retrieveAll, c);

		tables = new JComboBox<String>(TABLES);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = ylvl;
		c.weightx = 0.5;
		ui.add(tables, c);
		
		whoHave = new JLabel("who have");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = ylvl;
		c.weightx = 0.5;
		ui.add(whoHave, c);
		
		for(int i = 0; i < conditions.size(); i++) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = ylvl;
			c.weightx = 0.5;
			ui.add(conditions.get(i), c);
			ylvl++;
		}
		
		go = new JButton("Go");
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 4;
		c.gridy = ylvl-1;
		c.weightx = 0.5;
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
}
