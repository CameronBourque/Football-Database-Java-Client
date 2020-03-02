import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ConditionalOption extends JComponent {
	public static final String[] CONDITIONS = new String[] {"<", "=", ">", "<>"};
	
	private JComboBox<String> attributes;
	private JComboBox<String> condition;
	private JComboBox<String> input;
	
	public ConditionalOption() {
		
		GridBagLayout group = new GridBagLayout();
		setLayout(group);

		String[] attrList = new String[] {""};
		attributes = new JComboBox<String>(attrList);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		
		attributes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(attributes.getSelectedIndex() == attributes.getItemCount()-1) {
					attributes.setEditable(true);
				}else {
					attributes.setEditable(false);
				}
				
			}});
		
		
		add(attributes, c);

		c = new GridBagConstraints();
		condition = new JComboBox<String>(CONDITIONS);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(0, DBWindow.PADDING, 0, 0);
		add(condition, c);


		
		input = new JComboBox<String>(attrList);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		
		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(input.getSelectedIndex() == input.getItemCount()-1) {
					input.setEditable(true);
				}else {
					input.setEditable(false);
				}
				
			}});
		
		add(input, c);
	}

	public void setAttrList(String[] list) {
		Object old_attr = attributes.getSelectedItem();
		Object old_input = input.getSelectedItem();
		
		if(attributes.getSelectedIndex() == -1) {
			//selectIndex == -1 occurs when Custom Text is made
			//TODO: Keep custom values through model changes
			
			attributes.setModel(new DefaultComboBoxModel<String>(list));
		}else {
			attributes.setModel(new DefaultComboBoxModel<String>(list));
		}
		
		input.setModel(new DefaultComboBoxModel<String>(list));
		
		input.setSelectedItem(old_input);
		attributes.setSelectedItem(old_attr);
		
		
	}
	
	
	public boolean isEmpty(){
		return attributes.getSelectedItem().equals(null);
	}

	public String toSQL(){
		String lhs = attributes.getSelectedItem().toString();
		String rhs = input.getSelectedItem().toString();
		
		if(attributes.getSelectedIndex() == -1) {
			lhs = "\'" + attributes.getSelectedItem() + "\'";
		}
		
		if(input.getSelectedIndex() == -1) {
			rhs = "\'" + input.getSelectedItem() + "\'";
		}
		
		return lhs + condition.getSelectedItem().toString() +  rhs;
	}
}
