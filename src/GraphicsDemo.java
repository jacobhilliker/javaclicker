import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.control.Slider;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/*
 * THINGS TO ADD
 * 
 * 1. Fix strike system maybe?
 * 
 * 2. btnAfterOptions: fix formatting
 * 
 * 3. Difficulty presets: how to differentiate slider changing by mouse vs. button
 * 
 * 4. Add support to fullscreen? Adaptive to size of window?
 * 
 * */

public class GraphicsDemo extends Application {

	// Setup
	Stage window;
	Scene mainMenu;
	Scene optionsMenu;
	Scene app;
	
	// Main Menu
	Label lbTitle;
	Button btnStart;
	Button btnOptions;
	Button btnQuit;
	
	// Options
	final double DEFAULT_SHRINK = 5;
	final double DEFAULT_SPAWN = 1;
	final double DEFAULT_RADIUS = 15;
	
	Label lbShrinkSlider;
	Slider sldSpeed;
	double shrinkSpeed = DEFAULT_SHRINK;
	
	Label lbSpawnSlider;
	Slider sldSpawn;
	double spawnRate = DEFAULT_SPAWN;
	
	Label lbRadSlider;
	Slider sldRadius;
	double radius = DEFAULT_RADIUS;
	
	HBox difficulties;
	RadioButton rbEasy;
	RadioButton rbMedium;
	RadioButton rbHard;
	ToggleGroup diffToggle;
	
	Button btnOptBack;
	
	// Game
	HBox score;
	Label lbScore;
	Label lbPoints;
	int points;
	Label lbStrikes;
	int strikes;
	Rectangle strikeZone;
	static Boolean targetIsClicked;
	Pane game;
	
	Target target;
	
	int randomX;
	int randomY;
	final int RANDOM_X_LIMIT = 380;
	final int RANDOM_Y_LIMIT = 330;
	
	ArrayList<Target> targetList;
	
	Timeline gameClock;
	
	// Post-game message
	Label lbGameOver;
	Button btnAfterStart;
	Button btnAfterQuit;
	Button btnAfterOptions;
	VBox postGameButtons;
	VBox postGameMessage;
	final int GAME_OVER_X = 100;
	final int GAME_OVER_Y = 100;
	
	public void start(Stage myStage) {
		
		window = myStage;
		
		/* ----------------
			Main menu
		---------------- */

		lbTitle = new Label("TargetClicker");
				
		btnStart = new Button("Start");
		btnOptions = new Button("Options");
		btnQuit = new Button("Quit");
				
		// Layout
		lbTitle.setStyle("-fx-font: 24 helvetica");
		
		btnStart.setPrefWidth(80);
		btnOptions.setPrefWidth(80);
		btnQuit.setPrefWidth(80);
		
		VBox menu = new VBox(lbTitle, btnStart, btnOptions, btnQuit);
		menu.setAlignment(Pos.CENTER);
		menu.setSpacing(75);
				
		// Registers buttons
		btnStart.setDefaultButton(true);
		btnStart.setOnAction(new ButtonHandler());
		
		btnOptions.setOnAction(new ButtonHandler());
		
		btnQuit.setCancelButton(true);
		btnQuit.setOnAction(e -> window.close());
		
		/* ---------------------
			Setting the stage
		--------------------- */
		
		mainMenu = new Scene(menu, 400, 400);
		
		window.setScene(mainMenu);
		window.setTitle("TargetClicker - Main Menu");
		
		window.show();
		
		/* ----------------
			Options menu
		---------------- */
		
		lbShrinkSlider = new Label("Target Shrink Rate per Cycle");
		sldSpeed = new Slider(1, 10, DEFAULT_SHRINK);
		sldSpeed.setMaxWidth(300);
		sldSpeed.setShowTickLabels(true);
		sldSpeed.setSnapToTicks(true);
		sldSpeed.setMajorTickUnit(1);
		sldSpeed.setMinorTickCount(0);
		sldSpeed.setBlockIncrement(1);
		sldSpeed.setPadding(new Insets(0, 0, 16, 0));
		sldSpeed.valueProperty().addListener(new ShrinkHandler());
		
		lbSpawnSlider = new Label("Circles per Second");
		sldSpawn = new Slider(0.5, 2.5, DEFAULT_SPAWN);
		sldSpawn.setMaxWidth(300);
		sldSpawn.setShowTickLabels(true);
		sldSpawn.setBlockIncrement(1);
		sldSpawn.setPadding(new Insets(0, 0, 16, 0));
		sldSpawn.valueProperty().addListener(new SpawnHandler());
		
		lbRadSlider = new Label("Initial Target Radius");
		sldRadius = new Slider(5, 20, DEFAULT_RADIUS);
		sldRadius.setMaxWidth(300);
		sldRadius.setShowTickLabels(true);
		sldRadius.setSnapToTicks(true);
		sldRadius.setMajorTickUnit(1);
		sldRadius.setMinorTickCount(0);
		sldRadius.setBlockIncrement(1);
		sldRadius.setPadding(new Insets(0, 0, 24, 0));
		sldRadius.valueProperty().addListener(new RadiusHandler());
		
		diffToggle = new ToggleGroup();
		
		rbEasy = new RadioButton("Easy");
		rbEasy.setOnAction(new DifficultyHandler());
		rbEasy.setToggleGroup(diffToggle);
		rbMedium = new RadioButton("Medium");
		rbMedium.setOnAction(new DifficultyHandler());
		rbMedium.setToggleGroup(diffToggle);
		rbHard = new RadioButton("Hard");
		rbHard.setOnAction(new DifficultyHandler());
		rbHard.setToggleGroup(diffToggle);
		
		difficulties = new HBox(rbEasy, rbMedium, rbHard);
		difficulties.setAlignment(Pos.CENTER);
		difficulties.setPadding(new Insets(0, 0, 24, 0));
		difficulties.setSpacing(8);
		
		btnOptBack = new Button("Back");
		
		VBox options = new VBox(lbShrinkSlider, sldSpeed, lbSpawnSlider, sldSpawn, lbRadSlider, sldRadius, difficulties, btnOptBack);
		options.setAlignment(Pos.CENTER);
		optionsMenu = new Scene(options, 400, 400);
		
		btnOptBack.setOnAction(new ButtonHandler());
		
		/* ---------------------- 
			Actual program here
		---------------------- */
		
		targetList = new ArrayList<Target>();
		
		points = 0;
		lbScore = new Label("Score: ");
		lbPoints = new Label("" + points);
		
		strikes = 0;
		lbStrikes = new Label("    ");
		lbStrikes.setTextFill(Color.RED);
		
		score = new HBox(lbScore, lbPoints, lbStrikes);
		score.setMaxHeight(30);
		score.setPadding(new Insets(8, 8, 8, 8));
		
		game = new Pane();
		game.setPrefHeight(370);
		game.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(3), new Insets(0))));
		
		// Strike System
		strikeZone = new Rectangle(game.getWidth(), game.getHeight());
		strikeZone.setOnMousePressed(new StrikeHandler());
		strikeZone.setTranslateX(-game.getWidth());
		strikeZone.setTranslateY(-game.getHeight());
		strikeZone.setFill(Color.PURPLE);
		game.getChildren().add(strikeZone);
		
		VBox appLayout = new VBox(score, game);
		app = new Scene(appLayout, 400, 400);
		app.getStylesheets().add("http://fonts.googleapis.com/css?family=Roboto");
		
		gameClock = new Timeline(new KeyFrame(Duration.seconds(1 / spawnRate), e -> spawnAndShrink()));
		gameClock.setCycleCount(Timeline.INDEFINITE);
		
	}
	
	/* --------------------
		Handler Classes
	-------------------- */
	
	public class ButtonHandler implements EventHandler<ActionEvent> {
		
		public void handle (ActionEvent e) {
			
			if (e.getSource() == btnOptBack) {
				
				window.setScene(mainMenu);
				window.setTitle("TargetClicker - Main Menu");
				
			}
			
			if (e.getSource() == btnStart) {
				
				window.setScene(app);
				window.setTitle("TargetClicker");
				runGame();
				
			}
			
			if (e.getSource() == btnAfterStart) {
				
				game.getChildren().remove(postGameMessage);
				points = 0;
				lbPoints.setText(points + "");
				strikes = 0;
				lbStrikes.setText("    ");
				
				runGame();
				
			}
			
			if (e.getSource() == btnOptions) {
				
				window.setScene(optionsMenu);
				window.setTitle("TargetClicker - Options");
				
			}
			
			if (e.getSource() == btnAfterOptions) {
				
				game.getChildren().remove(postGameMessage);
				points = 0;
				lbPoints.setText(points + "");
				strikes = 0;
				lbStrikes.setText("    ");
				
				window.setScene(optionsMenu);
				window.setTitle("TargetClicker - Options");
				
			}
		}
		
	} // end of ButtonHandler
	
	public class MouseHandler implements EventHandler<MouseEvent> {
		
		public void handle(MouseEvent e) {
				
			// Removes the clicked target
			game.getChildren().remove(e.getSource());
			targetList.remove(e.getSource());
					
			// Updates score
			points++;
			lbPoints.setText("" + points);
			
		}
		
	}
	
	public class DifficultyHandler implements EventHandler<ActionEvent> {
		
		public void handle(ActionEvent e) {
			
			if (e.getSource() == rbEasy) {
				
				sldSpawn.setValue(1);
				sldRadius.setValue(20);
				sldSpeed.setValue(2);
				
			}
			
			if (e.getSource() == rbMedium) {
				
				sldSpawn.setValue(1.75);
				sldRadius.setValue(15);
				sldSpeed.setValue(6);
				
			}
			
			if (e.getSource() == rbHard) {
				
				sldSpawn.setValue(2.5);
				sldSpeed.setValue(10);
				sldRadius.setValue(10);
				
			}
			
		}
		
	}
	
	public class RadiusHandler implements ChangeListener<Number> {
		
		public void changed (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
			
			radius = newVal.intValue();
			
			if (!isDifficultyPreset()) {
				
				rbEasy.setSelected(false);
				rbMedium.setSelected(false);
				rbHard.setSelected(false);
				
			}
			
		}
		
	}
	
	public class ShrinkHandler implements ChangeListener<Number> {
		
		public void changed (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
			
			shrinkSpeed = newVal.intValue();
			
			if (!isDifficultyPreset()) {
				
				rbEasy.setSelected(false);
				rbMedium.setSelected(false);
				rbHard.setSelected(false);
				
			}
			
		}
		
	}
	
	public class SpawnHandler implements ChangeListener<Number> {
		
		public void changed (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
			
			spawnRate = newVal.doubleValue();
			
			if (!isDifficultyPreset()) {
				
				rbEasy.setSelected(false);
				rbMedium.setSelected(false);
				rbHard.setSelected(false);
				
			}
			
		}
		
	}
	
	public class StrikeHandler implements EventHandler<MouseEvent> {
		
		public void handle(MouseEvent e) {
			
			if (e.getSource() != target) {
				
				strikes++;
				lbStrikes.setText(lbStrikes.getText() + "X");
					
				if (strikes == 3) {
						
					gameOver();
						
				}
				
			}
			
		}
		
	}
	
	public boolean isDifficultyPreset() {
		
		boolean isEasy = (sldSpawn.getValue() == 1 && sldRadius.getValue() == 20 && sldSpeed.getValue() == 2);
		boolean isMedium = (sldSpawn.getValue() == 1.75 && sldRadius.getValue() == 15 && sldSpeed.getValue() == 6);
		boolean isHard = (sldSpawn.getValue() == 2.5 && sldRadius.getValue() == 10 && sldSpeed.getValue() == 10);
		
		return (isEasy || isMedium || isHard);
		
	}
	
	/* ------------------------
		TIME-BASED METHODS
	------------------------ */
	
	public void runGame() {
		
		// Timer for program				
		gameClock.play();
		
	}
	
	public void spawn() {
				
		randomX = (int) Math.round(Math.random() * (RANDOM_X_LIMIT - (radius + 5)) + (radius + 5));
			
		randomY = (int) Math.round(Math.random() * (RANDOM_Y_LIMIT - (radius + 5)) + (radius + 5));
			
		target = new Target(randomX, randomY, radius + shrinkSpeed); // shrinkSpeed is added because the Target is automatically shrunk on spawn
		target.setOnMousePressed(new MouseHandler());
		targetList.add(target);
		game.getChildren().add(target);
		
	}
	
	// Calls target.shrink() for each Target in targetList
	public void shrinkAll() {
		
		for (int i = 0; i < targetList.size(); i++) {
					
			Target currentTarget = targetList.get(i);
			
			currentTarget.setRadius(currentTarget.getRadius() - shrinkSpeed);
			
			if (currentTarget.getRadius() <= 0) {
				
				gameOver();
				
			}
				
		}
		
	}
	
	public void spawnAndShrink() {
		
		spawn();
		shrinkAll();
		
	}
	
	public void gameOver() {
		
		// Stops spawnAndShrink()
		gameClock.stop();
		
		// Removes all targets
		game.getChildren().removeAll(targetList);
		targetList.removeAll(targetList);
		
		// Game Over message
		lbGameOver = new Label("GAME OVER");
		lbGameOver.setStyle("-fx-font-family: Roboto; -fx-font-size: 24; -fx-text-fill: red; -fx-font-weight: 900");
		
		btnAfterStart = new Button("Play Again");
		btnAfterStart.setOnAction(new ButtonHandler());
		
		btnAfterOptions = new Button("Options");
		btnAfterOptions.setOnAction(new ButtonHandler());
		
		btnAfterQuit = new Button("Quit");
		btnAfterQuit.setCancelButton(true);
		btnAfterQuit.setOnAction(e -> window.close());
		
		postGameButtons = new VBox(btnAfterStart, btnAfterOptions, btnAfterQuit);
		postGameButtons.setSpacing(32);
		postGameButtons.setAlignment(Pos.CENTER);
		
		postGameMessage = new VBox(lbGameOver, postGameButtons);
		postGameMessage.setAlignment(Pos.CENTER);
		postGameMessage.setSpacing(12);
		postGameMessage.setMinSize(200, 132);
		postGameMessage.setLayoutX(GAME_OVER_X);
		postGameMessage.setLayoutY(GAME_OVER_Y);
		postGameMessage.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
		postGameMessage.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(3), new Insets(0))));
		
		game.getChildren().add(postGameMessage);
		
		System.out.println(postGameMessage.getHeight()); // Why does this return 0?
		
	}
	
}
