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
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Диалоговое окно, появляющееся после создания накладной.
 * @author Катапусик
 */
public class CreateReportDialog extends VBox {
    private final Label title;
    private final String TITLE = "Успешное создание отчета.";
    private final Text text;
    private final String absPath;
    
    /**
     * Конструктор.
     * @param absPath - путь к только что созданному отчету.
     */
    public CreateReportDialog(String absPath) {
        this.absPath = absPath;
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(430, USE_PREF_SIZE);
        
        title = new Label(TITLE);
        title.setId("title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);
        
        text = new Text("Чтобы открыть папку с отчетом, нажмите кнопку \"Открыть\"");
        text.setWrappingWidth(400);
        BorderPane pane = new BorderPane();
        VBox.setMargin(pane, new Insets(5, 5, 5, 5));
        pane.setCenter(text);
        BorderPane.setMargin(pane, new Insets(5, 5, 5, 5));
        
        final Button cancelButton = new Button("Отмена");
        cancelButton.setId("cancelButton");
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            Fenix.getFenix().hideModalMessage();
        });
        cancelButton.setMinWidth(74);
        cancelButton.setPrefWidth(74);
        HBox.setMargin(cancelButton, new Insets(0, 8, 0, 0));
        
        final Button okButton = new Button("Открыть");
        okButton.setId("saveButton");
        okButton.setDefaultButton(true);
        okButton.setOnAction((ActionEvent actionEvent) -> {
            handlerOkButton();
        });
        okButton.setMinWidth(74);
        okButton.setPrefWidth(74);
        
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(cancelButton, okButton);
        
        getChildren().addAll(pane, bottomBar);
        
        this.setOnKeyPressed((KeyEvent e) -> {
            if(e.getCode() == KeyCode.ENTER) {
                handlerOkButton();
            } else if(e.getCode() == KeyCode.ESCAPE) {
                Fenix.getFenix().hideModalMessage();
            }
        });
    }
    
    /**
     * Обработка нажатия на кнопку "Открыть".
     */
    private void handlerOkButton() {
        try {
            Runtime.getRuntime().exec("explorer " + absPath);
        } catch (IOException ex) {}
        Fenix.getFenix().hideModalMessage();
    }
}
