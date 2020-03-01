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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

  Stage window;

  static Connection conn;
  public static void main(String[] args) {
	  
    
    
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

    String first_drop[] = {"players", "teams", "games", "stadium"};
    String second_drop[] = {"Name", "location", "members", "points", "weight", "height"};
    String third_drop[] = {"<", "=", ">"};
    String fourth_drop[] = {"before", "in", "after"};
    
    ComboBox<String> table = new ComboBox<String>(FXCollections.observableArrayList(first_drop));
    ComboBox<String> attribute = new ComboBox<String>(FXCollections.observableArrayList(second_drop));
    ComboBox<String> comparison = new ComboBox<String>(FXCollections.observableArrayList(third_drop));
    ComboBox<String> combo_box4 = new ComboBox<String>(FXCollections.observableArrayList(fourth_drop));
    TextField textField = new TextField();
    TextField textField2 = new TextField();
    Button goButton = new Button("Go");

    Text output = new Text();
    output.setText("default output");

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
    GridPane.setConstraints(attribute, 3, 2);
    GridPane.setConstraints(comparison, 4, 2);
    GridPane.setConstraints(combo_box4, 6, 2);
    GridPane.setConstraints(textField, 5, 2);
    GridPane.setConstraints(textField2, 7, 2);
    GridPane.setConstraints(output, 0, 0, 7, 1);
    GridPane.setConstraints(goButton, 7, 3, 1, 7);

    goButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        output
            .setText(makeQuery(table.getValue(),attribute.getValue(),comparison.getValue(),textField.getText()));
      }
    });

    // Add everything to grid
    grid.getChildren().addAll(retrieve, who, table, attribute, comparison, combo_box4,
        textField, textField2, output, goButton);

    Scene scene = new Scene(grid, 900, 400);
    window.setScene(scene);
    window.show();
  }

  //Called from Go button
  //Converts Text to SQL statement. Executes Query
  //Returns result of query.
  private String makeQuery(String table, String attr, String comparison, String value) {

	  try {
		  
		  //Create SQL Statement
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM " +table+ " WHERE " + "\"" + attr + "\"" + comparison + " ?");
		pstmt.setString(1, value);
		

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
				for(int i=1;i<8;i++) {
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
