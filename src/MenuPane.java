/*import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MenuPane extends VBox{

	// Declares instance variables
	Label lbTitle;
	
	Button btnStart;
	Button btnOptions;
	Button btnQuit;
	
	public MenuPane() {
		
		// Instantiates controls
		lbTitle = new Label("Main Menu");
		
		btnStart = new Button("Start");
		btnOptions = new Button("Options");
		btnQuit = new Button("Quit");
		
		// Sets up layout
		VBox menu = new VBox(lbTitle, btnStart, btnOptions, btnQuit);
		menu.setAlignment(Pos.CENTER);
		menu.setPadding(new Insets(100));
		menu.setSpacing(100);
		
		// Registers buttons
		btnOptions.setOnAction(new OptionsHandler());
		
		getChildren().add(menu);
		
	}
	
	private class OptionsHandler implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent e) {
			
			GraphicsDemo.setOptionsScene();
			
		}
		
	}
	
} */
