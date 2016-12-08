package fenix.widget.dialog;

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
import fenix.Fenix;
import fenix.invoice.array.InvoiceList;
import fenix.vo.Entry;
import fenix.widget.table.TableOverview;

/**
 * Диалоговое окно для передачи записи в накладную.
 *
 * @author Катапусик
 *
 */
public class AddToInvoiceDialog extends VBox {

    private final Text text;
    private TextField textField;
    private final String DEFAULT = "Ничего не выбрано.";
    private final String TITLE = "Добавить в накладную";
    private final Label title;
    private Entry currentEntry;								// выбранная запись

    /**
     * Конструктор.
     */
    public AddToInvoiceDialog() {
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(430, USE_PREF_SIZE);
        setOnMouseClicked((MouseEvent t) -> {
            t.consume();
        });
        // Заголовок
        title = new Label(TITLE);
        title.setId("title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);
        // Текстовое поле
        text = new Text(DEFAULT);
        text.setWrappingWidth(400);
        BorderPane pane = new BorderPane();
        VBox.setMargin(pane, new Insets(5, 5, 5, 5));
        pane.setCenter(text);
        BorderPane.setMargin(text, new Insets(5, 5, 5, 5));
        // Кнопка отмены
        Button cancelButton = new Button("Отмена");
        cancelButton.setId("cancelButton");
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            Fenix.getFenix().hideModalMessage();
        });
        cancelButton.setMinWidth(74);
        cancelButton.setPrefWidth(74);
        HBox.setMargin(cancelButton, new Insets(0, 8, 0, 0));
        // Кнопка ОК
        final Button okButton = new Button("Добавить");
        okButton.setId("saveButton");
        okButton.setDefaultButton(true);
        okButton.setDisable(true);
        okButton.setOnAction((ActionEvent arg0) -> {
            handlerOkButton();
        });
        okButton.setMinWidth(74);
        okButton.setPrefWidth(74);
        // Текстовое поле для ввода
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
            if (value != null && value.length() != 0) {
                okButton.setDisable(false);
            } else {
                okButton.setDisable(true);
            }
        };
        textField.textProperty().addListener(changeListener);
        textField.requestFocus();

        HBox bottomBar = new HBox(0);
        bottomBar.setAlignment(Pos.BASELINE_RIGHT);
        bottomBar.getChildren().addAll(cancelButton, okButton);

        getChildren().addAll(pane, textField, bottomBar);

        this.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!okButton.isDisable()) {
                    handlerOkButton();
                }
            } else if(e.getCode() == KeyCode.ALT) {
                Fenix.getFenix().hideModalMessage();
                setFocusOverviewTable();
            }
        });
    }

    /**
     * Вставить информацию из выбранной записи в диалог.
     *
     * @param entry - выбранная запись.
     */
    public void insertInfo(Entry entry) {
        if (entry == null) {
            return;
        }
        currentEntry = entry;
        text.setText(entry.getName());
        title.setText(TITLE + " (не более " + entry.getQuantity() + " ед.)");
        // установка фокуса
        Platform.runLater(() -> {
            textField.requestFocus();
        });
    }

    /**
     * Сброс текстового поля.
     */
    public void refresh() {
        textField.setText(null);
    }

    /**
     * Обработка события нажатия на кнопку "Добавить".
     */
    private void handlerOkButton() {
        Integer quantity;
        try {
            quantity = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            quantity = 0;
        }
        if (quantity > 0) {
            if (quantity > currentEntry.getQuantity()) {
                textField.setText("");
                return;
            }
            Entry newEntry = new Entry();
            newEntry.setId(currentEntry.getId());
            newEntry.setName(currentEntry.getName());
            newEntry.setQuantity(quantity);
            newEntry.setPrice(currentEntry.getPrice());
            InvoiceList.getInstance().addEntry(newEntry);

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
}
