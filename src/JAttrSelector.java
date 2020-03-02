import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.w3c.dom.Attr;

public class JAttrSelector extends JComponent {
	public int max_size;
	public int current_size = 0;
	private String[] AttrChoices;
	private ArrayList<ConditionalOption> AttrBoxes;
	private JButton addAttrBox;
	private JButton removeAttrBox;
	private DBWindow parent;
	
	public JAttrSelector(DBWindow parent, int max_size) {
		assert(max_size>0);
		this.max_size = max_size;
		this.AttrChoices = new String[1];
		
		AttrBoxes = new ArrayList<ConditionalOption>();
		GridBagLayout group = new GridBagLayout();
		setLayout(group);
		
		addAttrBox = new JButton("+");
		addAttrBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRow();				
				parent.update();
			}
		});
		
		removeAttrBox = new JButton("-");
		removeAttrBox.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeRow();
				parent.update();
			}
		});
		
		AttrBoxes.add(new ConditionalOption());
		
		//Add First Box
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = current_size;
		c.gridwidth = 2;
		c.weightx = 0.5;
		add(AttrBoxes.get(current_size), c);
		
		//Add buttons
		c.gridwidth = 1;
		c.gridy++;
		add(addAttrBox, c);
		c.gridx = 1;
		add(removeAttrBox, c);
		
		current_size++;
	}
	
	private void addRow() {
		if(current_size < max_size) {
			setVisible(false);
			
			AttrBoxes.add(new ConditionalOption());
			AttrBoxes.get(current_size).setAttrList(AttrChoices);
			
			//set grid constraints on conditional options list
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = current_size;
			c.gridwidth = 2;
			c.weightx = 0.5;
			
			//remove old buttons
			remove(addAttrBox);
			remove(removeAttrBox);
			
			//add in new replacing conditional option
			add(AttrBoxes.get(current_size), c);
			
			//readd buttons
			c.gridwidth = 1;
			c.gridy++;
			add(addAttrBox, c);
			c.gridx = 1;
			add(removeAttrBox, c);
			
			current_size++;
			
			setVisible(true);
		}
	}
	
	private void removeRow() {
		if(current_size > 1) {
			setVisible(false);
			
			//remove last conditional option
			remove(AttrBoxes.get(AttrBoxes.size()-1));
			AttrBoxes.remove(AttrBoxes.size()-1);
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = current_size;
			c.weightx = 0.5;
			add(addAttrBox, c);
			c.gridx = 1;
			add(removeAttrBox, c);
			
			current_size--;
			
			setVisible(true);
		}
	}
	
	public void setAttributes(String[] attrs) {
		AttrChoices = new String[attrs.length];
		for(int i = 0; i < AttrChoices.length; i++) {
			AttrChoices[i] = attrs[i];			
		}
		for(int i = 0; i < AttrBoxes.size(); i++) {
			AttrBoxes.get(i).setAttrList(attrs);
		}
	}
	
	public ConditionalOption get(int i) {
		return AttrBoxes.get(i);
	}
}
