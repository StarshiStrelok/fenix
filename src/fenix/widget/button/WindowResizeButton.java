package fenix.widget.button;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * 
 * @author Катапусик
 * Кнопка для изменения размеров окна
 */
public class WindowResizeButton extends Region {
	private double dragOffsetX, dragOffsetY;
	
	public WindowResizeButton(final Stage stage, final double stageMinimumWidth, final double stageMinimumHeight) {
		setId("window-resize-button");
		setPrefSize(11, 11);
		setOnMousePressed(new EventHandler<MouseEvent>() {	// обработчик нажатия кнопки мыши

			@Override
			public void handle(MouseEvent event) {
				dragOffsetX = (stage.getX() + stage.getWidth()) - event.getScreenX();
				dragOffsetY = (stage.getY() + stage.getHeight()) - event.getScreenY();
				event.consume();
			}
			
		});
		
		setOnMouseDragged(new EventHandler<MouseEvent>() {	// обработчик перетаскивания мышью

			@Override
			public void handle(MouseEvent event) {
				ObservableList<Screen> screens = Screen.getScreensForRectangle(stage.getX(), stage.getY(), 1, 1);
				final Screen screen;
				if(screens.size() > 0) {
					screen = screens.get(0);
				} else {
					screen = Screen.getScreensForRectangle(0, 0, 1, 1).get(0);
				}
				Rectangle2D visualBounds = screen.getVisualBounds();
				double maxX = Math.min(visualBounds.getMaxX(), event.getScreenX() + dragOffsetX);
				double maxY = Math.min(visualBounds.getMaxY(), event.getScreenY() - dragOffsetY);
				stage.setWidth(Math.max(stageMinimumWidth, maxX - stage.getX()));
				stage.setHeight(Math.max(stageMinimumHeight, maxY - stage.getY()));
				event.consume();
			}
			
		});
	}
}
