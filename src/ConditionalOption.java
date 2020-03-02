import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

public class ConditionalOption extends JComponent {
	public static final String[] CONDITIONS = new String[] {"<", "=", ">", "<>"};
	
	private JComboBox<String> attributes;
	private JComboBox<String> condition;
	private JTextField input;
	
	public ConditionalOption() {
		
		GridBagLayout group = new GridBagLayout();
		setLayout(group);

		String[] attrList = new String[] {"a", "b"};
		attributes = new JComboBox<String>(attrList);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		add(attributes, c);

		c = new GridBagConstraints();
		condition = new JComboBox<String>(CONDITIONS);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(0, DBWindow.PADDING, 0, 0);
		add(condition, c);

		c = new GridBagConstraints();
		input = new JTextField();
		input.setPreferredSize(new Dimension(100, 25));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		c.insets = new Insets(0, DBWindow.PADDING, 0, 0);
		add(input, c);
	}

	public boolean isEmpty(){
		return attributes.getSelectedItem().equals(null);
	}

	public String toSQL(){
		return attributes.getSelectedItem().toString() + condition.getSelectedItem().toString() + input.getText();
	}
}
