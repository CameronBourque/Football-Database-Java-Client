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

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    window = primaryStage;
    window.setTitle("thenewboston - JavaFX");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(10);
    grid.setHgap(10);

    String first_drop[] = {"players", "teams", "games", "stadiums"};
    String second_drop[] = {"name", "location", "members", "points", "weight", "height"};
    String third_drop[] = {"<", "=", ">"};
    String fourth_drop[] = {"before", "in", "after"};
    ComboBox combo_box = new ComboBox(FXCollections.observableArrayList(first_drop));
    ComboBox combo_box2 = new ComboBox(FXCollections.observableArrayList(second_drop));
    ComboBox combo_box3 = new ComboBox(FXCollections.observableArrayList(third_drop));
    ComboBox combo_box4 = new ComboBox(FXCollections.observableArrayList(fourth_drop));
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

    GridPane.setConstraints(combo_box, 1, 2);
    GridPane.setConstraints(combo_box2, 3, 2);
    GridPane.setConstraints(combo_box3, 4, 2);
    GridPane.setConstraints(combo_box4, 6, 2);
    GridPane.setConstraints(textField, 5, 2);
    GridPane.setConstraints(textField2, 7, 2);
    GridPane.setConstraints(output, 0, 0, 7, 1);
    GridPane.setConstraints(goButton, 7, 3, 1, 7);

    goButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        output
            .setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry.\n "
                + "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, \n"
                + "when an unknown printer took a galley of type and scrambled it to make a type specimen book. \n"
                + "It has survived not only five centuries, but also the leap into electronic typesetting, \n"
                + "remaining essentially unchanged. It was popularised in the 1960s with the\n "
                + "release of Letraset sheets containing Lorem Ipsum passages,\n "
                + "and more recently with desktop publishing software like Aldus Page\n"
                + "Maker including versions of Lorem Ipsum.");
      }
    });

    // Add everything to grid
    grid.getChildren().addAll(retrieve, who, combo_box, combo_box2, combo_box3, combo_box4,
        textField, textField2, output, goButton);

    Scene scene = new Scene(grid, 900, 400);
    window.setScene(scene);
    window.show();
  }


}
