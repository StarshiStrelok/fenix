package fenix.report;

import fenix.Fenix;
import fenix.db.interactor.DBInteractorFactory;
import fenix.db.interactor.IDBInteractor;
import fenix.invoice.array.InvoiceList;
import fenix.vo.Entry;
import fenix.widget.dialog.CreateReportDialog;
import fenix.widget.listview.ListViewInvoice;
import fenix.widget.table.TableOverview;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Создатель отчетов по выбранным данным.
 *
 * @author Катапусик
 *
 */
public class ReportCreator {

    private static final Logger logger = Logger.getLogger(ReportCreator.class.getSimpleName());

    /**
     * Создать и записать отчет в файл.
     *
     * @param list
     */
    public static void createReport(List<Entry> list) {
        logger.info("createReport# Create report started...");
        if(InvoiceList.getInstance().isEmpty()) {
            logger.info("createReport# Exit with empty invoice list.");
            return;
        }
        // создаем атрибуты файла.
        File file = new File("reports");
        if (!file.exists()) {
            file.mkdirs();
            logger.info("createReport# Folder /reports was created.");
        }
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH-mm");
        String fileName = sdf.format(currentDate) + ".xls";
        // формируем документ
        HSSFWorkbook workbook = new HSSFWorkbook();
        // лист
        Sheet sheet = workbook.createSheet("Накладная");
        Row row = sheet.createRow(0);
        Cell cell_1 = row.createCell(0);
        Cell cell_2 = row.createCell(1);
        Cell cell_3 = row.createCell(2);
        Cell cell_4 = row.createCell(3);
        cell_1.setCellStyle(getStyle(workbook));
        cell_2.setCellStyle(getStyle(workbook));
        cell_3.setCellStyle(getStyle(workbook));
        cell_4.setCellStyle(getStyle(workbook, HSSFCellStyle.ALIGN_RIGHT));
        cell_1.setCellValue("№");
        cell_2.setCellValue("Наименование");
        cell_3.setCellValue("Кол-во");
        cell_4.setCellValue("Цена");
        // наполняем таблицу информацией
        for (int i = 0; i < list.size(); i++) {
            Entry entry = list.get(i);
            if (InvoiceList.TOTAL.equals(entry.getName())) {
                Row rowTotal = sheet.createRow(i + 1);
                Cell cellTotal_2 = rowTotal.createCell(1);
                cellTotal_2.setCellValue(entry.getName());
                cellTotal_2.setCellStyle(getStyle(workbook));
                
                Cell cellTotal_4 = rowTotal.createCell(3);
                cellTotal_4.setCellValue(formatPrice(entry.getPrice()));
                cellTotal_4.setCellStyle(getStyle(workbook, HSSFCellStyle.ALIGN_RIGHT));
            } else {
                Row rowEntry = sheet.createRow(i + 1);

                Cell cellEntry_1 = rowEntry.createCell(0);
                cellEntry_1.setCellValue(i + 1);
                cellEntry_1.setCellStyle(getStyle(workbook));
                
                Cell cellEntry_2 = rowEntry.createCell(1);
                cellEntry_2.setCellValue(entry.getName() == null ? "" : entry.getName());
                cellEntry_2.setCellStyle(getStyle(workbook));
                
                Cell cellEntry_3 = rowEntry.createCell(2);
                cellEntry_3.setCellValue(entry.getQuantity() == null ? 0 : entry.getQuantity());
                cellEntry_3.setCellStyle(getStyle(workbook));
                
                Cell cellEntry_4 = rowEntry.createCell(3);
                cellEntry_4.setCellValue(formatPrice(entry.getPrice() == null ? 0 : entry.getPrice()));
                cellEntry_4.setCellStyle(getStyle(workbook, HSSFCellStyle.ALIGN_RIGHT));
            }
        }
        // устанавливаем ширины столбцов
        sheet.setColumnWidth(0, 940);
        sheet.setColumnWidth(1, 14500);
        sheet.setColumnWidth(2, 2250);
        sheet.setColumnWidth(3, 3300);
        
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);
        // запись в файл
        File reportFile = new File("reports/" + fileName);
        try {
            logger.info("createReport# Output Stream open...");
            FileOutputStream fos = new FileOutputStream(reportFile);
            workbook.write(fos);
            fos.flush();
            fos.close();
            logger.info("createReport# Output Stream close...");
        } catch (IOException e) {
            logger.error("createReport# Write report in file - fail.", e);
        }
        logger.info("createReport# Create report finished...");
        logger.info("createReport# DataBase update started...");
        updateDataBase(list);
        CreateReportDialog dialog = new CreateReportDialog(reportFile.getAbsolutePath());
        logger.info("createReport# Report File absPath is " + reportFile.getAbsolutePath());
        
        ListViewInvoice.getInstance().refresh();
        TableOverview.getInstance().changeTableView();
        InvoiceList.getInstance().clear();
        Fenix.getFenix().showModalMessage(dialog);
        
        logger.info("createReport# DataBase update finished...");
    }

    /**
     * Обновить базу данных с учетом накладной.
     *
     * @return - результат операции.
     */
    private static boolean updateDataBase(List<Entry> list) {
        int lastEntryIndex = list.size() - 1;
        Entry last = list.get(lastEntryIndex);
        if (InvoiceList.TOTAL.equals(last.getName())) {
            list.remove(lastEntryIndex);
            logger.info("updateDataBas# DataBase update - remove 'total' Entry : success!");
        } else {
            logger.error("updateDataBas# DataBase update - remove 'total' Entry : fail!");
            return false;
        }

        IDBInteractor interactor = DBInteractorFactory.getInstance().getIDBInteractor();
        boolean result = interactor.updateDataBase(list);
        return result;
    }
    private static String formatPrice(double price) {
        DecimalFormat df=new DecimalFormat("0.00");
        String result = df.format(price).replace(",", ".");
        if (result.contains(".")) {
            result = result.replace(".", " р. ");
            if (!result.endsWith("00")) {
                result += " коп.";
            } else {
                result = result.substring(0, result.lastIndexOf("00"));
            }
        } else {
            result += " р.";
        }
        if (result.startsWith("0 р.")) {
            result = result.replace("0 р.", "");
        }
        return result;
    }
    private static HSSFCellStyle getStyle(final HSSFWorkbook workbook, final short align) {
        HSSFCellStyle style2 = getStyle(workbook);
        style2.setAlignment(align);
        return style2;
    }
    private static HSSFCellStyle getStyle(final HSSFWorkbook workbook) {
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setBorderBottom(CellStyle.BORDER_DOTTED);
        style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderTop(CellStyle.BORDER_DOTTED);
        style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderRight(CellStyle.BORDER_DOTTED);
        style2.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style2.setBorderLeft(CellStyle.BORDER_DOTTED);
        style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style2.setWrapText(true);
        return style2;
    }
}
