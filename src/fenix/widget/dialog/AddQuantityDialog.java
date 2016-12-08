/*
 * Copyright (C) 2014 Катапусик
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fenix.widget.dialog;

import fenix.Fenix;
import fenix.vo.Entry;
import fenix.widget.table.TableOverview;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Диалоговое окно для добавления количества товара.
 * @author Катапусик
 */
public class AddQuantityDialog extends VBox {
    private final Label title;
    private final String TITLE = "Прибавить количество товара.";
    private final String DEFAULT = "Ничего не выбрано.";
    private Entry currentEntry;
    private final Text text;
    private TextField textField;
    
    /**
     * Конструктор.
     */
    public AddQuantityDialog() {
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(430, USE_PREF_SIZE);
        setOnMouseClicked((MouseEvent event) -> {
            event.consume();
        });
        
        title = new Label(TITLE);
        title.setId("title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);
        
        text = new Text(DEFAULT);
        text.setWrappingWidth(400);
        BorderPane pane = new BorderPane();
        VBox.setMargin(pane, new Insets(5, 5, 5, 5));
        pane.setCenter(text);
        BorderPane.setMargin(text, new Insets(5, 5, 5, 5));
        
        final Button cancelButton = new Button("Отмена");
        cancelButton.setId("cancelButton");
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            Fenix.getFenix().hideModalMessage();
        });
        cancelButton.setMinWidth(74);
        cancelButton.setPrefWidth(74);
        HBox.setMargin(cancelButton, new Insets(0, 8, 0, 0));
        
        final Button okButton = new Button("Добавить");
        okButton.setId("saveButton");
        okButton.setDefaultButton(true);
        okButton.setDisable(true);
        okButton.setOnAction((ActionEvent actionEvent) -> {
            handlerOkButton();
        });
        okButton.setMinWidth(74);
        okButton.setPrefWidth(74);
        
        textField = new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceText(start, end, text);
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (text.matches("[0-9]*")) {
                    super.replaceSelection(text);
                }
            }
        };
        textField.setPromptText("количество");
        textField.setPrefColumnCount(10);
        ChangeListener<String> changeListener = (ObservableValue<? extends String> arg0, String arg1, String arg2) -> {
            String value = textField.getText();
            if(value != null && value.length() != 0) {
                okButton.setDisable(false);
            } else {
                okButton.setDisable(true);
            }
        };
        textField.textProperty().addListener(changeListener);
        textField.requestFocus();
        
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(cancelButton, okButton);
        
        getChildren().addAll(pane, textField, bottomBar);
        
        this.setOnKeyPressed((KeyEvent e) -> {
            if(e.getCode() == KeyCode.ENTER) {
                handlerOkButton();
            } else if(e.getCode() == KeyCode.ALT_GRAPH) {
                Fenix.getFenix().hideModalMessage();
                setFocusOverviewTable();
            }
        });
    }
    
    /**
     * Обработка события при нажатии на кнопку Добавить.
     */
    private void handlerOkButton() {
        Integer quantity;
        try {
            quantity = Integer.parseInt(textField.getText());
        } catch(NumberFormatException e) {
            quantity = 0;
        }
        if(quantity > 0) {
            int total = currentEntry.getQuantity() + quantity;
            currentEntry.setQuantity(total);
            TableOverview.getInstance().updateEntry(currentEntry);
            Fenix.getFenix().hideModalMessage();
            setFocusOverviewTable();
        }
    }
    
    /**
     * Установить фокус на таблицу.
     */
    private void setFocusOverviewTable() {
        Platform.runLater(() -> {
            TableOverview.getInstance().requestFocus();
        });
    }
    
    /**
     * Сброс текстового поля.
     */
    public void refresh() {
        textField.setText(null);
    }
    
    /**
     * Вставить информацию из выбранной записи в диалоговое окно.
     * @param entry - выбранная запись.
     */
    public void insertInfo(Entry entry) {
        if(entry == null) {
            return;
        }
        currentEntry = entry;
        text.setText(entry.getName());
        title.setText(TITLE + "\nТекущее количество " + entry.getQuantity() + " ед.");
        
        Platform.runLater(() -> {
            textField.requestFocus();
        });
    }
}
