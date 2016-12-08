package fenix.widget.searchbox;

import fenix.vo.Entry;
import fenix.widget.table.TableOverview;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.apache.log4j.Logger;

/**
 * Реализация поискового текстового поля. Использует
 * полнотекстный поиск на базе Lucene.
 * @author Катапусик 
 */
public class SearchBox extends Region {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    private TextField textField;
    private Button clearText;
    private ContextMenu contextMenu = new ContextMenu();
    private Tooltip searchErrorTooltip = new Tooltip();
    private Timeline searchErrorTooltipHidder = null;
    private Map<String, Entry> map;

    /**
     * Конструктор.
     */
    public SearchBox() {
        setId("SearchBox");
        setMinHeight(24);
        setPrefSize(150, 24);
        setMaxHeight(24);
        textField = new TextField();
        textField.setPromptText("Поиск");
        clearText = new Button();
        clearText.setVisible(false);
        getChildren().addAll(textField, clearText);
        clearText.setOnAction((ActionEvent event) -> {
            textField.setText("");
            textField.requestFocus();
        });

        textField.setOnKeyReleased((KeyEvent event) -> {
            if (event.getCode() == KeyCode.DOWN) {
                logger.debug("SearchBox, key DOWN released");
                contextMenu.setFocused(true);
            }
        });

        textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            clearText.setVisible(textField.getText().length() != 0);
            if (textField.getText().length() == 0) {
                if (contextMenu != null) {
                    contextMenu.hide();
                    showError(null);
                }
            } else {
                boolean haveResult = false;
                List<String> result = new LinkedList<>();
                // ищем, только если введено более 2 букв.
                if(newValue.length() > 1) {
                    map = TableOverview.getInstance().fillMap();
                    Set<String> set = map.keySet();
                    for(String key : set) {
                        if(key.toLowerCase().contains(newValue.toLowerCase())) {
                            result.add(key);
                        }
                    }
                } else {
                    return;
                }
                
                if (result.size() > 0) {
                    haveResult = true;
                }
                
                if (haveResult) {
                    showError(null);
                    popMenu(result);
                    if (!contextMenu.isShowing()) {
                        contextMenu.show(SearchBox.this, Side.BOTTOM, 10, -5);
                    }
                } else {
                    if (searchErrorTooltip.getText() == null) {
                        showError("Совпадений не найдено");
                    }
                    contextMenu.hide();
                }
                contextMenu.setFocused(true);
            }
        });
    }

    /**
     * Создание и наполнение контекстного меню.
     * @param list - список результатов.
     */
    private void popMenu(List<String> list) {
        contextMenu.getItems().clear();
        for (String result : list) {
            final HBox hBox = new HBox();
            hBox.setFillHeight(true);
            Label itemLabel = new Label(result);
            itemLabel.getStyleClass().add("item-label");
            hBox.getChildren().add(itemLabel);

            final CustomMenuItem menuItem = new CustomMenuItem(hBox, true);
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction((ActionEvent event) -> {
                TableOverview.getInstance().focus(map.get(result));
            });
        }
    }

    @Override
    protected void layoutChildren() {
        textField.resize(getWidth(), getHeight());
        clearText.resizeRelocate(getWidth() - 18, 6, 12, 13);
    }

    /**
     * Показывает ошибку в виде тултипа - листок с сообщением.
     * @param message - сообщение.
     */
    private void showError(String message) {
        searchErrorTooltip.setText(message);	// устанавливаем сообщение в тултип
        if (searchErrorTooltipHidder != null) {	// если таймер еще тикает
            searchErrorTooltipHidder.stop();	// останавливам таймер
        }
        if (message != null) {
            Point2D toolTipPos = textField.localToScene(0, textField.getLayoutBounds().getHeight());
            double x = toolTipPos.getX() + textField.getScene().getX() + textField.getScene().getWindow().getX();
            double y = toolTipPos.getY() + textField.getScene().getY() + textField.getScene().getWindow().getY();
            searchErrorTooltip.show(textField.getScene().getWindow(), x, y);
            searchErrorTooltipHidder = new Timeline();
            searchErrorTooltipHidder.getKeyFrames().add(new KeyFrame(Duration.seconds(3), (ActionEvent event) -> {
                searchErrorTooltip.hide();
                searchErrorTooltip.setText(null);
            })
            );
            searchErrorTooltipHidder.play();
        } else {
            searchErrorTooltip.hide();
        }
    }
}
