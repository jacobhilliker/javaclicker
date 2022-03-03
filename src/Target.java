import javafx.scene.shape.Circle;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Target extends Circle {
	
	Target target;
	double centerX;
	double centerY;
	double radius;
	
		public Target(double x, double y, double rad) {
			
			super(x, y, rad);
			
			this.setFill(Color.RED);
			
		}
		
	}