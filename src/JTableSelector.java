import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class JTableSelector extends JComponent {
	public int max_size;
	public int current_size = 0;
	private String[] TableChoices;
	private ArrayList<JComboBox<String>> TableBoxes;
	private ActionListener e;
	private JButton addTableBox;
	private JButton removeTableBox;
	
	public JTableSelector(String[] TableChoices, int max_size) {
		assert(max_size>0);
		this.max_size = max_size;
		this.TableChoices = TableChoices;
		
		TableBoxes = new ArrayList<JComboBox<String>>();
		GridBagLayout group = new GridBagLayout();
		setLayout(group);
		
		addTableBox = new JButton("+");
		addTableBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRow();
				
			}});
		
		removeTableBox = new JButton("-");
		removeTableBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeRow();
			}});
	
		TableBoxes.add(new JComboBox<String>(getIncrementedValues(TableChoices)));
		
		//Add First Box
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = current_size;
		c.gridwidth = 2;
		c.weightx = 0.5;
		add(TableBoxes.get(current_size), c);
		
		//Add Buttons
		c.gridwidth = 1;
		c.gridy++;
		add(addTableBox, c);
		c.gridx = 1;
		
		add(removeTableBox, c);
		current_size++;
		
		
	}
	
	private void addRow() {
		if(current_size < max_size) {
			setVisible(false);
			
			TableBoxes.add(new JComboBox<String>(getIncrementedValues(TableChoices)));
			//Add ActionListener if we have one
			if(e != null)
				TableBoxes.get(current_size).addActionListener(e);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = current_size;
			c.gridwidth = 2;
			c.weightx = 0.5;
			
			//Remove old buttons
			remove(addTableBox);
			remove(removeTableBox);
			
			//Replace with new box
			add(TableBoxes.get(current_size), c);
			
			//Readd buttons
			c.gridwidth = 1;
			c.gridy++;
			add(addTableBox, c);
			c.gridx = 1;
			add(removeTableBox, c);
			current_size++;
			
			setVisible(true);
		}
		
	}
	
	private void removeRow() {
		if(current_size > 1) {
			setVisible(false);
			remove(TableBoxes.get(TableBoxes.size()-1));
			TableBoxes.remove(TableBoxes.size()-1);
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = current_size;
			c.weightx = 0.5;
			add(addTableBox, c);
			c.gridx = 1;
			add(removeTableBox, c);
			
			current_size--;
			setVisible(true);
		}
	}
	
	//Adds current_size to the end of each string
	//So tables have different names
	private String[] getIncrementedValues(String[] tables) {
		if(current_size == 0)
			return tables;
		String[] temp = new String[tables.length];
		for(int i=0;i<tables.length;i++) {
			
			temp[i] = tables[i] + current_size;
		}
		return temp;
	}
	
	
	public void addActionListener(ActionListener e) {
		this.e = e;
		for(int i=0;i<TableBoxes.size();i++) {
			TableBoxes.get(i).addActionListener(e);
		}
	}
	public String[] getSelectItems() {
		String[] items = new String[current_size];
		for (int i = 0; i < TableBoxes.size(); i++) {
			items[i] = (String) TableBoxes.get(i).getSelectedItem();
		}
		return items;
		
	}
	
	
}
