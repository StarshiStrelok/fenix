package fenix.widget.dialog;

import fenix.Fenix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.log4j.Logger;

/**
 * Диалоговое окно настроек.
 * @author Катапусик
 */
public class SettingsDialog extends VBox {
    private static final Logger logger = Logger.getLogger(SettingsDialog.class.getSimpleName());
    
    private final Button okButton;
    private final Button cancelButton;
    private final Tab generalTab;
    private final Tab reserveTab;
    private final TabPane options;
    
    /**
     * Конструктор.
     */
    public SettingsDialog() {
        setId("ProxyDialog");
        setSpacing(10);
        setMaxSize(430, USE_PREF_SIZE);
        setOnMouseClicked((MouseEvent event) -> {
            event.consume();
        });
        Text explanation = new Text("Вы находитесь в настройках приложения. "
            + "Изменяйте их по своему усмотрению.");
        explanation.setWrappingWidth(400);
        
        BorderPane borderPane = new BorderPane();
        VBox.setMargin(explanation, new Insets(5, 5, 5, 5));
        borderPane.setCenter(explanation);
        BorderPane.setMargin(explanation, new Insets(5, 5, 5, 5));
        
        Label title = new Label("Настройки");
        title.setId("title");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);
        getChildren().add(title);
        
        generalTab = new Tab("Общие");
        GeneralPanel generalPanel = new GeneralPanel();
        generalTab.setContent(generalPanel);
        
        reserveTab = new Tab("Дополнительно");
        
        cancelButton = new Button("Отмена");
        cancelButton.setId("cancelButton");
        cancelButton.setOnAction((ActionEvent event) -> {
            Fenix.getFenix().hideModalMessage();
        });
        cancelButton.setMinWidth(74);
        cancelButton.setPrefWidth(74);
        HBox.setMargin(cancelButton, new Insets(0, 8, 0, 0));
        
        okButton = new Button("Сохранить");
        okButton.setId("saveButton");
        okButton.setDefaultButton(true);
        okButton.setOnAction((ActionEvent event) -> {
            final Properties settings = new Properties();
            Thread storeTask = new Thread(() -> {
                try {
                    File fenixSettings = new File(System.getProperty("user.home"), ".fenix-settings");
                    FileOutputStream fos = new FileOutputStream(fenixSettings);
                    settings.store(fos, "Fenix - Settings");
                    fos.flush();
                    fos.close();
                } catch(IOException e) {
                    logger.error("Save settings - fail", e);
                }
            });
            storeTask.start();
        });
        okButton.setMinWidth(74);
        okButton.setPrefWidth(74);
        
        HBox buttonBar = new HBox();
        buttonBar.setAlignment(Pos.BASELINE_RIGHT);
        buttonBar.getChildren().addAll(cancelButton, okButton);
        VBox.setMargin(buttonBar, new Insets(20, 5, 5, 5));
        
        options = new TabPane();
        options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        options.getTabs().addAll(generalTab, reserveTab);
        
        getChildren().addAll(borderPane, options, buttonBar);
    }
    
    /**
     * Загрузить настройки.
     */
    public void loadSettings() {
        File ensembleSettings = new File(System.getProperty("user.home"),".ensemble-settings");
        if (ensembleSettings.exists() && ensembleSettings.isFile()) {
            final Properties settings = new Properties();
            try {
                settings.load(new FileInputStream(ensembleSettings));
            } catch (IOException e) {
                logger.error("Load settings - fail", e);
            }
        }
    }
    
    /**
     * Вкладка "Общие".
     */
    private class GeneralPanel extends GridPane {
        TextField hostNameBox;
        TextField portBox;
        
        public GeneralPanel() {
            setPadding(new Insets(8));
            setHgap(5.0f);
            setVgap(5.0f);
            
            int rowIndex = 0;
            
            Label label2 = new Label("Хост");
            label2.setId("proxy-dialog-label");
            GridPane.setConstraints(label2, 0, rowIndex);
            
            Label label3 = new Label("Порт");
            label3.setId("proxy-dialog-label");
            GridPane.setConstraints(label3, 1, rowIndex);
            getChildren().addAll(label2, label3);
            
            rowIndex++;
            hostNameBox = new TextField();
            hostNameBox.setPromptText("proxy.host.com");
            hostNameBox.setPrefColumnCount(20);
            GridPane.setConstraints(hostNameBox, 0, rowIndex);
            
            portBox = new TextField();
            portBox.setPromptText("8080");
            portBox.setPrefColumnCount(10);
            GridPane.setConstraints(portBox, 1, rowIndex);
            
            getChildren().addAll(hostNameBox, portBox);
        }
    }
}






