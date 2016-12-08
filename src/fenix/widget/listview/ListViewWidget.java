package fenix.widget.listview;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import fenix.db.interactor.DBInteractorFactory;
import fenix.db.interactor.IDBInteractor;
import fenix.vo.Type;
import fenix.widget.table.TableOverview;

/**
 * ListView для отображения категорий товаров.
 * @author Катапусик
 *
 */
public class ListViewWidget extends ListView<String> {
    private final IDBInteractor dbInteractor = DBInteractorFactory.getInstance().getIDBInteractor();
    private ObservableList<String> displayedTypeEntry;
    private TableOverview tableView;
    private static ListViewWidget instance;

    public static ListViewWidget getInstance() {
        if (instance == null) {
            instance = new ListViewWidget();
        }
        return instance;
    }

    private final ChangeListener<String> listViewListener = (ObservableValue<? extends String> arg0, String oldValue, String newValue) -> {
        if (newValue != null) {
            tableView.changeTableView(newValue);
        }
    };

    private ListViewWidget() {
        super();
        tableView = TableOverview.getInstance();
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        List<Type> listing = dbInteractor.getAllTypes();
        if (listing != null && !listing.isEmpty()) {
            String[] arrayArticles = new String[listing.size()];
            for (int i = 0; i < listing.size(); i++) {
                arrayArticles[i] = listing.get(i).getDescription();
            }
            displayedTypeEntry = FXCollections.observableArrayList(arrayArticles);
            this.setItems(displayedTypeEntry);
        }
        this.getSelectionModel().selectedItemProperty().addListener(listViewListener);
    }
}
