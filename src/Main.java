import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

  Stage window;

  static Connection conn;
  
  private static HashMap<String, String[]> table_attributes;
  public static void main(String[] args) {
	  
	  //The hash table should have every table and table attribute that a user would want to query.
	  table_attributes = new HashMap<String, String[]>();
	  table_attributes.put("player", new String[]{"Player Code", "Last Name"});
	  table_attributes.put("team", new String[]{"Team Code", "Name"});
	  table_attributes.put("game", new String[]{"Game Code", "Date"});
	  table_attributes.put("stadium", new String[]{"Name", "City"});
	  
	  
    
    
    dbSetupExample my = new dbSetupExample();
    try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team7_905_cfb",
           my.user, my.pswd);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }
    
    launch(args);
  }
  
  
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    window = primaryStage;
    window.setTitle("JavaFX");

    GridPane grid = new GridPane();
    
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(10);
    grid.setHgap(10);
    
    String first_drop[] = {"player", "team", "game", "stadium"};
    String second_drop[] = {"Name", "location", "members", "points", "weight", "height", "Player Code"};
    String third_drop[] = {"<", "=", ">"};

    ArrayList<ComboBox<String>> attributes = new  ArrayList<ComboBox<String>>();
    ArrayList<ComboBox<String>> comparisons = new ArrayList<ComboBox<String>>();
    ArrayList<TextField> values = new ArrayList<TextField>();
    
    
    ComboBox<String> table = new ComboBox<String>(FXCollections.observableArrayList(first_drop));
    
    attributes.add(new ComboBox<String>(FXCollections.observableArrayList(second_drop)));
    comparisons.add(new ComboBox<String>(FXCollections.observableArrayList(third_drop)));
    values.add(new TextField());
    
    
    Button goButton = new Button("Go");
    
    Button moreFiltersButton = new Button("+");

    Text output = new Text();
    output.setText("No output");

    ScrollPane outputScrollPane = new ScrollPane();

    outputScrollPane.setContent(output);
    outputScrollPane.setStyle("-fx-background-color:transparent;");
    outputScrollPane.fitToHeightProperty().set(true);  
    

    //Show gridlines
    grid.setGridLinesVisible(true);
    RowConstraints r1 = new RowConstraints();
    r1.setMaxHeight(200);
    grid.getRowConstraints().add(0, r1);
    
    Text retrieve = new Text();
    retrieve.setText("Retrieve all");
    Text who = new Text();
    who.setText("who have");
    Text write = new Text();
    write.setText("Write to file: ");
    
    
    GridPane.setConstraints(retrieve, 0, 2);
    GridPane.setConstraints(who, 2, 2);
    GridPane.setConstraints(write, 0, 2);

    GridPane.setConstraints(table, 1, 2);
    
    

	GridPane.setConstraints(attributes.get(0), 3, 2);
    GridPane.setConstraints(comparisons.get(0), 4, 2);
    GridPane.setConstraints(values.get(0), 5, 2);
  
    GridPane.setConstraints(moreFiltersButton, 5, 3);
    
    
    
    
    GridPane.setConstraints(outputScrollPane, 0, 0);
    GridPane.setColumnSpan(outputScrollPane, 10);
    GridPane.setConstraints(goButton, 7, 3, 1, 7);

    //Table ComboBox action
    //Attribute box values depends on the table selected
    //as the attributes are the columns of the table
    table.setOnAction(new EventHandler<ActionEvent>(){
    	@Override
        public void handle(ActionEvent event) {
    		for(int i=0; i<attributes.size();i++)
    			attributes.get(i).setItems(FXCollections.observableArrayList(table_attributes.get(table.getValue())));
    	}
    });
    
    //GO BUTTON ACTION
    //Calls query and sets the output text.
    goButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        output
            .setText(makeQuery(table.getValue(),attributes,comparisons, values));
      }
    });
    
    // + BUTTON ACTION
    // Creates more fields
    moreFiltersButton.setOnAction(new EventHandler<ActionEvent>() {
    	@Override
        public void handle(ActionEvent event) {
    		//Create new dropdowns/textfields
    	    attributes.add(new ComboBox<String>(FXCollections.observableArrayList(table_attributes.get(table.getValue()))));
    	    comparisons.add(new ComboBox<String>(FXCollections.observableArrayList(third_drop)));
    	    values.add(new TextField());
    	    
    	    //Index used to put in right position in the grid
    	    int filter_index = attributes.size()-1;
    	    
    	    //Placing in grid
    		GridPane.setConstraints(attributes.get(filter_index), 3, 2 + filter_index);
    	    GridPane.setConstraints(comparisons.get(filter_index), 4, 2 + filter_index);
    	    GridPane.setConstraints(values.get(filter_index), 5, 2 + filter_index);

    	    //Adding elements to grid
    	    grid.getChildren().addAll(attributes.get(filter_index), comparisons.get(filter_index), values.get(filter_index));
        
    	    //Moving + Button
    	    GridPane.setConstraints(moreFiltersButton, 5, 3 + filter_index);
    	}
    });

    // Add everything to grid
    grid.getChildren().addAll(retrieve, who, table, outputScrollPane, goButton, moreFiltersButton);
    grid.getChildren().addAll(attributes);
    grid.getChildren().addAll(comparisons);
    grid.getChildren().addAll(values);
    Scene scene = new Scene(grid, 900, 400);
    window.setScene(scene);
    window.show();
  }

  //Called from Go button
  //Converts Text to SQL statement. Executes Query
  //Returns result of query.
  private String makeQuery(String table, ArrayList<ComboBox<String>> attr, ArrayList<ComboBox<String>> comparison, ArrayList<TextField> value) {

	  try {
		  
		  //Create SQL Statement
		  //Vulnerable to SQL injection
		 String query = new String("SELECT * FROM " + table + " ");
		 if(attr.get(0).getValue() != null && comparison.get(0).getValue() != null && value.get(0).getText() != null) {
			 query += " WHERE ";
			 for(int i=0;i<attr.size();i++) {
				 //One of the fields is empty, ignore
				 if(attr.get(i).getValue() == null && comparison.get(i).getValue() == null && value.get(i).getText() == null)
					 continue;
				 
				 if(i != 0)
					 query += " AND ";
				 
				 query += "\"" + attr.get(i).getValue() + "\"" + comparison.get(i).getValue() + "'" +value.get(i).getText() + "'";
				 
				 

				
	
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
			
			return return_string;
		}
		
		
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  
	  return "SELECT ";
  }

}
