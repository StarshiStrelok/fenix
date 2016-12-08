package fenix.widget.table;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import fenix.Fenix;
import fenix.invoice.array.InvoiceList;
import fenix.vo.Entry;
import java.util.List;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.log4j.Logger;

/**
 * Класс-таблица для отображения информации о товарах, выбранных для накладной.
 * @author Катапусик
 *
 */
public class TableInvoice extends TableView<Entry> {
    private final Logger logger = Logger.getLogger("TableInvoice");

    private static TableInvoice instance;
    private final TableColumn<Entry, String> name;
    private final TableColumn<Entry, Integer> quantity;
    private final TableColumn<Entry, Double> price;
    private ObservableList<Entry> data;

    /**
     * Синглтон
     *
     * @return - экземпляр объекта.
     */
    public static TableInvoice getInstance() {
        if (instance == null) {
            instance = new TableInvoice();
        }
        return instance;
    }

    /**
     * Конструктор
     */
    private TableInvoice() {
        super();
        name = new TableColumn<>("Наименование");
        quantity = new TableColumn<>("Количество");
        price = new TableColumn<>("Цена");
        price.setCellFactory(TextFieldTableCell.<Entry, Double>forTableColumn(new FDoubleStringConverter()));

        name.setPrefWidth(400);
        quantity.setPrefWidth(75);
        price.setPrefWidth(75);

        this.getColumns().addAll(name, quantity, price);
        this.setEditable(false);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        configTableColumn();

        this.getSelectionModel().getSelectedIndices().addListener((javafx.collections.ListChangeListener.Change<? extends Integer> change) -> {
            if (change.getList().size() > 0) {
                ((Button) Fenix.getFenix().getInnerNode("deleteInvoiceEntry")).setDisable(false);
            } else {
                ((Button) Fenix.getFenix().getInnerNode("deleteInvoiceEntry")).setDisable(true);
            }
        });
        logger.info("TableInvoice initialized successfull.");
    }

    /**
     * Обновить содержимое накладной.
     */
    public void refresh() {
        data = FXCollections.observableArrayList(InvoiceList.getInstance());
        this.setItems(data);
    }
    
    /**
     * Обновить содержимое таблицы.
     * @param list - список с информацией для обновления.
     */
    public void refresh(List<Entry> list) {
        data = FXCollections.observableArrayList(list);
        this.setItems(data);
    }

    /**
     * Конфигурирование столбцов таблицы.
     */
    private void configTableColumn() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
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
     * Удалить выделенные записи из накладной.
     */
    public void deleteInvoiceEntry() {
        ObservableList<Entry> selected = this.getSelectionModel().getSelectedItems();
        if (selected != null && selected.size() > 0) {
            selected.stream().forEach((e) -> {
                InvoiceList.getInstance().remove(e);
            });
            this.refresh();
        }
    }
}
