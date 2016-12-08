package fenix.widget.table;

import fenix.Fenix;
import fenix.db.interactor.DBInteractorFactory;
import fenix.db.interactor.IDBInteractor;
import fenix.vo.Entry;
import fenix.vo.Type;
import fenix.widget.dialog.AddQuantityDialog;
import fenix.widget.dialog.AddToInvoiceDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.IntegerStringConverter;
import org.apache.log4j.Logger;

/**
 * Класс для отображения информации полученной из БД в виде таблицы.
 * @author Катапусик
 *
 */
public final class TableOverview extends TableView<Entry> {
    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    private static TableOverview instance;
    private static Type currentType;
    private IDBInteractor dbInteractor = DBInteractorFactory.getInstance().getIDBInteractor();
    private TableColumn<Entry, String> name, f, g, h, i, j, k;
    private TableColumn<Entry, Integer> quantity;
    private TableColumn<Entry, Double> price;
    private ObservableList<Entry> data;
    private AddToInvoiceDialog alertDialog;
    private AddQuantityDialog quantityDialog;
    private Entry choosenEntry;

    /**
     * Конструктор.
     */
    private TableOverview() {
        super();
        alertDialog = new AddToInvoiceDialog();
        quantityDialog = new AddQuantityDialog();
        name = new TableColumn<>("Наименование");
        f = new TableColumn<>("F");
        g = new TableColumn<>("G");
        h = new TableColumn<>("H");
        i = new TableColumn<>("I");
        j = new TableColumn<>("J");
        k = new TableColumn<>("K");
        quantity = new TableColumn<>("Количество");
        price = new TableColumn<>("Цена");

        name.setPrefWidth(400);
        quantity.setPrefWidth(75);
        price.setPrefWidth(75);
        f.setPrefWidth(75);
        g.setPrefWidth(75);
        h.setPrefWidth(75);
        i.setPrefWidth(75);
        j.setPrefWidth(75);
        k.setPrefWidth(75);

        this.getColumns().addAll(name, quantity, price, f, g, h, i, j, k);
        configTableColumn();
        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.getSelectionModel().getSelectedIndices().addListener((javafx.collections.ListChangeListener.Change<? extends Integer> change) -> {
            if (change.getList().size() > 0) {
                ((Button) Fenix.getFenix().getInnerNode("addNewEntryButton")).setDisable(false);
                ((Button) Fenix.getFenix().getInnerNode("deleteEntryButton")).setDisable(false);
            } else {
                ((Button) Fenix.getFenix().getInnerNode("addNewEntryButton")).setDisable(true);
                ((Button) Fenix.getFenix().getInnerNode("deleteEntryButton")).setDisable(true);
            }
        });

        this.setOnKeyPressed((KeyEvent event) -> {
            ObservableList<Entry> list = instance.getSelectionModel().getSelectedItems();
            if (list == null || list.size() == 0) {
                return;
            }
            choosenEntry = list.get(0);
            logger.debug("Choosen entry: " + choosenEntry.toString());
            if(event.getCode() == KeyCode.ALT) {
                alertDialog.insertInfo(choosenEntry);
                alertDialog.refresh();
                Fenix.getFenix().showModalMessage(alertDialog);
            } else if(event.getCode() == KeyCode.ALT_GRAPH) {
                quantityDialog.insertInfo(choosenEntry);
                quantityDialog.refresh();
                Fenix.getFenix().showModalMessage(quantityDialog);
            }
        });
        changeTableView("Зубная паста");
        logger.info("TableOwerview initialezed successfull.");
    }
    
    /**
     * Просто обновить таблицу.
     */
    public void changeTableView() {
        changeTableView(currentType.getDescription());
    }

    /**
     * Метод для изменения содержимого таблицы.
     * @param newValue - новый тип продукции.
     */
    public void changeTableView(String newValue) {
        currentType = dbInteractor.getTypeByDescription(newValue);
        logger.info("Current type: " + currentType.getDescription());
        List<Entry> entries = currentType.getEntries();

        if (entries != null) {
            if(!Fenix.SHOW_ALL_ENTRIES) {
                List<Entry> emptyEntries = new ArrayList<>();
                for(Entry e : entries) {
                    if(e.getQuantity() == 0) {
                        emptyEntries.add(e);
                    }
                }
                entries.removeAll(emptyEntries);
            }
            ObservableList<Entry> currentList = this.getItems();
            if (currentList != null) {
                currentList.clear();
            }
            data = FXCollections.observableList(entries);

            this.setItems(data);
        }
    }
    
    /**
     * Обновить таблицу с учетом флага SHOW_ALL_ENTRIES.
     */
    public void refreshContent() {
        if(!Fenix.SHOW_ALL_ENTRIES) {
            ObservableList<Entry> currentList = this.getItems();
            List<Entry> empty = new LinkedList<>();
            for(Entry e : currentList) {
                if(e.getQuantity() == 0) {
                    empty.add(e);
                }
            }
            currentList.removeAll(empty);
        } else {
            changeTableView(currentType.getDescription());
        }
    }

    /**
     * Синглтон.
     * @return - экземпляр объекта.
     */
    public static TableOverview getInstance() {
        if (instance == null) {
            instance = new TableOverview();
        }
        return instance;
    }

    /**
     * Добавить новую строку в таблицу.
     */
    public void addNewEntry() {
        Entry entry = new Entry();
        entry.setType(currentType);
        dbInteractor.addEntry(entry);
        changeTableView(currentType.getDescription());
    }

    /**
     * Удалить строку из таблицы.
     */
    public void deleteEntry() {
        ObservableList<Entry> selected = this.getSelectionModel().getSelectedItems();
        if (selected != null && selected.size() > 0) {
            selected.stream().forEach((entry) -> {
                dbInteractor.deleteEntry(entry);
            });
            changeTableView(currentType.getDescription());
        }
    }
    
    /**
     * Обновить запись в базе данных.
     * @param entry - обновляемая запись.
     */
    public void updateEntry(Entry entry) {
        dbInteractor.updateEntry(entry);
        changeTableView(currentType.getDescription());
    }

    /**
     * Метод для определения, выбрана ли хоть одна строка в таблице?
     * @return - true - строка выбрана, false - ничего не выбрано.
     */
    public boolean isSelected() {
        ObservableList<Entry> selected = this.getSelectionModel().getSelectedItems();
        return selected != null && selected.size() > 0;
    }

    /**
     * Конфигуратор столбцов таблицы. Отслеживает изменения в таблице и
     * записывает их в БД.
     */
    private void configTableColumn() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        f.setCellValueFactory(new PropertyValueFactory<>("f"));
        g.setCellValueFactory(new PropertyValueFactory<>("g"));
        h.setCellValueFactory(new PropertyValueFactory<>("h"));
        i.setCellValueFactory(new PropertyValueFactory<>("i"));
        j.setCellValueFactory(new PropertyValueFactory<>("j"));
        k.setCellValueFactory(new PropertyValueFactory<>("k"));

        name.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        name.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setName(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        quantity.setCellFactory(TextFieldTableCell.<Entry, Integer>forTableColumn(new IntegerStringConverter()));
        quantity.setOnEditCommit((CellEditEvent<Entry, Integer> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setQuantity(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        price.setCellFactory(TextFieldTableCell.<Entry, Double>forTableColumn(new FDoubleStringConverter()));
        price.setOnEditCommit((CellEditEvent<Entry, Double> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setPrice(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        f.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        f.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setF(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        g.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        g.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setG(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        h.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        h.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setH(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        i.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        i.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setI(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        j.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        j.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setJ(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });

        k.setCellFactory(TextFieldTableCell.<Entry>forTableColumn());
        k.setOnEditCommit((CellEditEvent<Entry, String> event) -> {
            Entry entry = (Entry) event.getTableView().getItems().get(event.getTablePosition().getRow());
            entry.setK(event.getNewValue());
            dbInteractor.updateEntry(entry);
        });
    }
    
    /**
     * Наполнить карту для поиска.
     * @return - поисковую карту.
     */
    public Map<String, Entry> fillMap() {
        ObservableList<Entry> list = this.getItems();
        Map<String, Entry> searchMap = new HashMap<>();
        for(Entry entry : list) {
            searchMap.put(entry.getName(), entry);
        }
        return searchMap;
    }
    
    /**
     * Установить фокус на выбранное при поиске значение.
     * @param entry - выбранная в поиске запись.
     */
    public void focus(Entry entry) {
        ObservableList<Entry> list = this.getItems();
        if(list.contains(entry)) {
            instance.getSelectionModel().clearSelection();
            Platform.runLater(() -> {
                instance.requestFocus();
                instance.getSelectionModel().select(entry);
                instance.getFocusModel().focus(new TablePosition(instance, list.indexOf(entry), name));
                instance.scrollTo(entry);
            });
        }
    }
}
