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

package fenix.widget.listview;

import fenix.Fenix;
import fenix.util.convert.InvoiceConverter;
import fenix.widget.table.TableInvoice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;

/**
 * ListView для отображения ранее созданных накладных.
 * @author Катапусик
 */
public class ListViewInvoice extends ListView<String> {
    private static final Logger logger = Logger.getLogger(ListViewInvoice.class.getSimpleName());
    
    private static final String CURRENT_INVOICE = "Текущая накладная";
    private static ListViewInvoice instance;
    private final ArrayList<String> displayedInvoices = new ArrayList<>();
    private final Map<String, File> filesMap = new HashMap<>();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH-mm");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMMM yyyy HH:mm");
    
    /**
     * Конструктор.
     */
    private ListViewInvoice() {
        super();
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        refresh();  
        this.getSelectionModel().selectedItemProperty().addListener(listViewListener);
        this.setOnMouseClicked(doubleClickListener);
    }
    
    /**
     * Синглтон.
     * @return - статический экземпляр класса.
     */
    public static ListViewInvoice getInstance() {
        if(instance == null) {
            instance = new ListViewInvoice();
        }
        return instance;
    }
    
    /**
     * Обработчик события. Срабатывает при выборе любого объекта в ListView.
     */
    private final ChangeListener<String> listViewListener = (ObservableValue<? extends String> arg0, String oldValue, String newValue) -> {
        if (newValue != null) {
            Button createReportButton = (Button)Fenix.getFenix().getInnerNode("createReportButton");
            if(CURRENT_INVOICE.equals(newValue)) {
                createReportButton.setDisable(false);
                TableInvoice.getInstance().refresh();
            } else {
                createReportButton.setDisable(true);
                File choosenFile = filesMap.get(newValue);
                if(choosenFile != null) {
                    try {
                        TableInvoice.getInstance().refresh(InvoiceConverter.extract(choosenFile));
                    } catch(FileNotFoundException e) {
                        logger.error(".xlsx-file not found!", e);
                    } catch(IOException ex) {
                        logger.error("Input-Output exception when try .xlsx-file opened!", ex);
                    }
                }
            }
        }
    };
    
    /**
     * Обработчик двойного клика.
     */
    private final EventHandler<MouseEvent> doubleClickListener = (MouseEvent event) -> {
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                String selected = this.getSelectionModel().getSelectedItem();
                if(CURRENT_INVOICE.equals(selected)) { 
                    return; 
                }
                File selectedFile = filesMap.get(selected);
                if(selectedFile != null) {
                    try {
                        Runtime.getRuntime().exec("explorer " + selectedFile.getAbsolutePath());
                    } catch (IOException ex) {
                        logger.error("Error when try open .xlsx-file with double-click", ex);
                    }
                }
            }
        }
    };
    
    /**
     * Обновить список файлов.
     */
    public final void refresh() {
        displayedInvoices.clear();
        displayedInvoices.add(CURRENT_INVOICE);
        File reportFolder = new File("reports");
        if(reportFolder.exists()) {
            for(File invoice : reportFolder.listFiles()) {
                try {
                    String filename = sdf2.format(sdf.parse(invoice.getName()));
                    displayedInvoices.add(filename);
                    filesMap.put(filename, invoice);
                } catch (ParseException ex) {}
            }  
        }
        this.setItems(FXCollections.observableArrayList(displayedInvoices));
    }
}
