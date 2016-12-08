package fenix;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.apache.log4j.Logger;

import fenix.invoice.array.InvoiceList;
import fenix.report.ReportCreator;
import fenix.util.convert.POIConverter;
import fenix.widget.breadcrumbar.BreadcrumbBar;
import fenix.widget.button.WindowButtons;
import fenix.widget.button.WindowResizeButton;
import fenix.widget.dialog.SettingsDialog;
import fenix.widget.listview.ListViewInvoice;
import fenix.widget.listview.ListViewWidget;
import fenix.widget.searchbox.SearchBox;
import fenix.widget.table.TableInvoice;
import fenix.widget.table.TableOverview;
import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Точка входа в программу.
 * @author Катапусик
 *
 */
public class Fenix extends Application {
    public static boolean SHOW_ALL_ENTRIES = true;

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private static Fenix fenix;
    private WindowResizeButton windowResizeButton;
    private BorderPane root;
    private Scene scene;
    private StackPane modalDimmer;
    private ToolBar toolBar;
    private double mouseDragOffsetX = 0;
    private double mouseDragOffsetY = 0;
    private ToolBar pageToolBar;
    private ListViewWidget listView;
    private ListViewInvoice listInvoices;
    private SplitPane splitPane;
    private BreadcrumbBar breadcrumbBar;
    private TableOverview tableOverview;
    private TableInvoice tableInvoice;
    private BorderPane rightSplitPane;
    private BorderPane leftSplitPane;
    private Button addNewEntryButton;
    private Button deleteEntryButton;
    private Button deleteInvoiceEntry;
    private Region spacer3;
    private Button settingsButton;
    private Button createReportButton;

    @Override
    public void start(final Stage stage) {
        logger.info("Start Application.........................");
        
//        try {
//            fillDataBase();
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }

        listView = ListViewWidget.getInstance();
        listInvoices = ListViewInvoice.getInstance();
        tableOverview = TableOverview.getInstance();
        tableInvoice = TableInvoice.getInstance();
        rightSplitPane = new BorderPane();
        leftSplitPane = new BorderPane();

        fenix = this;
        stage.setTitle("Fenix");
        StackPane layerPane = new StackPane();
        stage.initStyle(StageStyle.UNDECORATED);
        windowResizeButton = new WindowResizeButton(stage, 1020, 700);
        root = new BorderPane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                windowResizeButton.autosize();
                windowResizeButton.setLayoutX(getWidth() - windowResizeButton.getLayoutBounds().getWidth());
                windowResizeButton.setLayoutY(getHeight() - windowResizeButton.getLayoutBounds().getHeight());
            }
        };
        root.getStyleClass().add("application");
        root.setId("root");
        layerPane.setDepthTest(DepthTest.DISABLE);
        layerPane.getChildren().add(root);
        boolean is3dSupported = Platform.isSupported(ConditionalFeature.SCENE3D);
        scene = new Scene(layerPane, 1020, 700, is3dSupported);
        if (is3dSupported) {
            scene.setCamera(new PerspectiveCamera());
        }
        scene.getStylesheets().add(Fenix.class.getResource("fenix.css").toExternalForm());

        modalDimmer = new StackPane();
        modalDimmer.setId("ModalDimmer");
        modalDimmer.setOnMouseClicked((MouseEvent event) -> {
            event.consume();
            hideModalMessage();
        });
        modalDimmer.setVisible(false);
        layerPane.getChildren().add(modalDimmer);
        //----------------ToolBar--------------------------------------------------------------
        toolBar = new ToolBar();
        toolBar.setId("mainToolBar");
        // логотип
        Image logoImage = new Image(Fenix.class.getResourceAsStream("images/logo_2.png"));
        ImageView logo = new ImageView(logoImage);
        HBox.setMargin(logo, new Insets(0, 0, 0, 5));
        logo.setSmooth(true);
        toolBar.getItems().add(logo);
        // пустое место
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        toolBar.getItems().add(spacer);
        // кнопка
        Button highlightsButton = new Button();
        highlightsButton.setId("highlightsButton");
        highlightsButton.setMinSize(120, 66);
        highlightsButton.setPrefSize(120, 66);
        highlightsButton.setOnAction((ActionEvent event) -> {
            SHOW_ALL_ENTRIES = false;
            tableOverview.refreshContent();
        });
        toolBar.getItems().add(highlightsButton);
        // вторая кнопка
        Button newButton = new Button();
        newButton.setId("newButton");
        newButton.setMinSize(120, 66);
        newButton.setPrefSize(120, 66);
        newButton.setOnAction((ActionEvent event) -> {
            SHOW_ALL_ENTRIES = true;
            tableOverview.refreshContent();
        });
        toolBar.getItems().add(newButton);
        // пустое место
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        toolBar.getItems().add(spacer2);
        // картинка поиска
        ImageView searchImage = new ImageView(new Image(Fenix.class.getResourceAsStream("images/search-text.png")));
        toolBar.getItems().add(searchImage);
        // поисковое поле
        SearchBox searchBox = new SearchBox();
        HBox.setMargin(searchBox, new Insets(0, 5, 0, 0));
        toolBar.getItems().add(searchBox);
        toolBar.setPrefHeight(66);
        toolBar.setMinHeight(66);
        toolBar.setMaxHeight(66);
        GridPane.setConstraints(toolBar, 0, 0);
        // кнопки-утилиты
        final WindowButtons windowButtons = new WindowButtons(stage);
        toolBar.getItems().add(windowButtons);
        // максимальный размер окна по дабл-клику
        toolBar.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                windowButtons.toogleMaximized();
            }
        });
        // тащить окно мышью - при нажатии фиксирует координаты
        toolBar.setOnMousePressed((MouseEvent event) -> {
            mouseDragOffsetX = event.getSceneX();
            mouseDragOffsetY = event.getSceneY();
        });
        toolBar.setOnMouseDragged((MouseEvent event) -> {
            if (!windowButtons.isMaximized()) {
                stage.setX(event.getScreenX() - mouseDragOffsetX);
                stage.setY(event.getScreenY() - mouseDragOffsetY);
            }
        });
        this.root.setTop(toolBar);
        //-----------------------------------------------------------------------------------------

        //------------------ListView---------------------------------------------------------------
        ToolBar pageTreeToolBar = new ToolBar() {
            @Override
            public void requestLayout() {
                super.requestLayout();
                if (pageToolBar != null && getHeight() != pageToolBar.prefHeight(-1)) {	// выровнять высоту тулбаров
                    pageToolBar.setPrefHeight(getHeight());
                }
            }
        };
        pageTreeToolBar.setId("page-tree-toolbar");
        pageTreeToolBar.setMinHeight(35);
        pageTreeToolBar.setPrefHeight(35);
        pageTreeToolBar.setMaxWidth(Double.MAX_VALUE);
        ToggleGroup pageButtonGroup = new ToggleGroup();
        final ToggleButton overwiewButton = new ToggleButton("Обзор");
        final ToggleButton invoiceButton = new ToggleButton("Накладная");
        overwiewButton.setSelected(true);
        overwiewButton.setToggleGroup(pageButtonGroup);
        invoiceButton.setToggleGroup(pageButtonGroup);
        overwiewButton.setOnAction((ActionEvent event) -> {
            pageToolBar.getItems().clear();
            pageToolBar.getItems().addAll(addNewEntryButton, deleteEntryButton, breadcrumbBar, spacer3, settingsButton);
            rightSplitPane.setCenter(tableOverview);
            leftSplitPane.setCenter(listView);
        });
        invoiceButton.setOnAction((ActionEvent event) -> {
            pageToolBar.getItems().clear();
            pageToolBar.getItems().addAll(deleteInvoiceEntry, createReportButton, breadcrumbBar, spacer3, settingsButton);
            tableInvoice.refresh();
            rightSplitPane.setCenter(tableInvoice);
            leftSplitPane.setCenter(listInvoices);
        });
        pageTreeToolBar.getItems().addAll(overwiewButton, invoiceButton);

        leftSplitPane.setTop(pageTreeToolBar);
        leftSplitPane.setCenter(listView);
        //-----------------------------------------------------------------------------------------

        //----------Table--------------------------------------------------------------------------
        pageToolBar = new ToolBar();
        pageToolBar.setId("page-toolbar");
        pageToolBar.setMinHeight(35);
        pageToolBar.setMaxSize(Double.MAX_VALUE, Control.USE_PREF_SIZE);

        addNewEntryButton = new Button("Добавить запись");
        addNewEntryButton.setMaxHeight(Double.MAX_VALUE);
        addNewEntryButton.setOnAction((ActionEvent event) -> {
            tableOverview.addNewEntry();
        });
        deleteEntryButton = new Button("Удалить запись");
        deleteEntryButton.setMaxHeight(Double.MAX_VALUE);
        deleteEntryButton.setOnAction((ActionEvent event) -> {
            tableOverview.deleteEntry();
        });
        addNewEntryButton.setDisable(!tableOverview.isSelected());
        deleteEntryButton.setDisable(!tableOverview.isSelected());

        deleteInvoiceEntry = new Button("Удалить запись");
        deleteInvoiceEntry.setMaxHeight(Double.MAX_VALUE);
        deleteInvoiceEntry.setOnAction((ActionEvent event) -> {
            tableInvoice.deleteInvoiceEntry();
        });
        deleteInvoiceEntry.setDisable(!tableInvoice.isSelected());

        createReportButton = new Button("Создать отчет");
        createReportButton.setMaxHeight(Double.MAX_VALUE);
        createReportButton.setOnAction((ActionEvent event) -> {
            ReportCreator.createReport(InvoiceList.getInstance());
        });

        pageToolBar.getItems().addAll(addNewEntryButton, deleteEntryButton);

        breadcrumbBar = new BreadcrumbBar();
        pageToolBar.getItems().add(breadcrumbBar);

        spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        final SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.loadSettings();
        
        settingsButton = new Button();
        settingsButton.setId("SettingsButton");
        settingsButton.setGraphic(new ImageView(new Image(Fenix.class.getResourceAsStream("images/settings.png"))));
        settingsButton.setMaxHeight(Double.MAX_VALUE);
        settingsButton.setOnAction((ActionEvent event) -> {
            showModalMessage(settingsDialog);
        });
        pageToolBar.getItems().addAll(spacer3, settingsButton);

        tableOverview.setId("table-view");
        tableInvoice.setId("table-invoice");

        rightSplitPane.setTop(pageToolBar);
        rightSplitPane.setCenter(tableOverview);
        //-----------------------------------------------------------------------------------------

        splitPane = new SplitPane();
        splitPane.setId("page-splitpane");
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(splitPane, 0, 1);
        splitPane.getItems().add(leftSplitPane);
        splitPane.getItems().add(rightSplitPane);
        splitPane.setDividerPosition(0, 0.25);
        this.root.setTop(toolBar);
        this.root.setCenter(splitPane);
        //-----------------------------------------------------------------------------------------

        stage.setScene(scene);
        stage.show();
        logger.info("GUI init successfull.");
    }

    /**
     * Запуск без JavaFX Launcher
     * @param args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Анимация. Скрывает модальное окно когда оно видимое.
     */
    public void hideModalMessage() {
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(
                new KeyFrame(Duration.seconds(0.25), (ActionEvent t) -> {
                    modalDimmer.setCache(false);
                    modalDimmer.setVisible(false);
                    modalDimmer.getChildren().clear();
        }, new KeyValue(modalDimmer.opacityProperty(), 0, Interpolator.EASE_BOTH)))
        .build().play();
    }

    /**
     * Анимация. Показывает модальное окно, когда оно невидимое.
     * @param message - визуальное представление.
     */
    public void showModalMessage(Node message) {
        modalDimmer.getChildren().add(message);
        modalDimmer.setOpacity(0);
        modalDimmer.setVisible(true);
        modalDimmer.setCache(true);
        TimelineBuilder.create().keyFrames(new KeyFrame(Duration.seconds(0.25), (ActionEvent arg0) -> {
            modalDimmer.setCache(false);
        }, new KeyValue(modalDimmer.opacityProperty(), 1, Interpolator.EASE_BOTH)))
        .build().play();
    }

    /**
     * Синглтон
     *
     * @return
     */
    public static Fenix getFenix() {
        return fenix;
    }
    
    /**
     * Получить узел по его названию.
     * @param name - название узла.
     * @return - узел или null.
     */
    public Node getInnerNode(String name) {
        if (null != name) {
            switch (name) {
                case "addNewEntryButton":
                    return addNewEntryButton;
                case "deleteEntryButton":
                    return deleteEntryButton;
                case "deleteInvoiceEntry":
                    return deleteInvoiceEntry;
                case "createReportButton":
                    return createReportButton;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Наполнить базу данных из xls-файла.
     * @throws UnsupportedEncodingException 
     */
    public void fillDataBase() throws UnsupportedEncodingException {
        final String PATH = "db.xls";
        File xls = new File(PATH);
        assert xls != null;
        POIConverter converter = new POIConverter(xls);
        converter.extractData();
    }
}
