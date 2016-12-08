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
package fenix.util.convert;

import fenix.db.interactor.DBInteractorFactory;
import fenix.vo.Entry;
import fenix.vo.Type;
import fenix.vo.TypeEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Конвертирует .xlsx файл в базу данных.
 * @author Катапусик
 */
public class POIConverter {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private HSSFWorkbook workBook;

    /**
     * Конструктор
     * @param sourceFile - xsl-файл
     */
    public POIConverter(File sourceFile) {
        try {
            InputStream is = new FileInputStream(sourceFile);
            workBook = new HSSFWorkbook(is);
        } catch (IOException e) {
            logger.error("Error initialization of HSSFWorkbook", e);
        }
    }

    /**
     * Извлечь данные из файла и импортировать в базу данных.
     * @throws UnsupportedEncodingException 
     */
    public void extractData() throws UnsupportedEncodingException {
        int sheets = workBook.getNumberOfSheets();
        for (int i = 0; i < sheets - 1; i++) {
            Type type = new Type();
            TypeEntry tEntry = null;
            for (TypeEntry te : TypeEntry.values()) {
                if (i == te.getNumber()) {
                    tEntry = te;
                }
            }
            if (tEntry != null) {
                type.setDescription(tEntry.getDescription());
            } else {
                type.setDescription("Unknown group");
            }
            DBInteractorFactory.getInstance().getIDBInteractor().addType(type);

            Sheet sheet = workBook.getSheetAt(i);
            Iterator<Row> itr = sheet.iterator();
            itr.next();
            while (itr.hasNext()) {
                Row row = itr.next();
                Entry entry = new Entry();
                entry.setName(validateString(row.getCell(0)));
                entry.setQuantity(validateInt(row.getCell(1)));
                entry.setPrice(validateDouble(row.getCell(4)));
                entry.setF(validateString(row.getCell(5)));
                entry.setG(validateString(row.getCell(6)));
                entry.setH(validateString(row.getCell(7)));
                entry.setI(validateString(row.getCell(8)));
                entry.setJ(validateString(row.getCell(9)));
                entry.setK(validateString(row.getCell(10)));
                entry.setType(type);
                DBInteractorFactory.getInstance().getIDBInteractor().addEntry(entry);
            }
        }
    }

    /**
     * Метод валидации строки.
     * @param cell - ячейка, содержащая строковое значение.
     * @return - возвращает строковое значение.
     * @throws UnsupportedEncodingException 
     */
    private String validateString(Cell cell) throws UnsupportedEncodingException {
        if (cell == null) {
            return "";
        } else {
            int cellType = cell.getCellType();
            String result;
            switch (cellType) {
                case Cell.CELL_TYPE_STRING:
                    result = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    result = ("" + cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    result = ("" + cell.getNumericCellValue());
                    break;
                default:
                    result = "";
                    break;
            }
            return result;
        }
    }

    /**
     * Метод валидации целочисленного значения.
     * @param cell - ячейка, содержащая целочисленное значение
     * @return - целочисленное значение.
     */
    private Integer validateInt(Cell cell) {
        assert cell != null;
        Double d = cell.getNumericCellValue();
        Integer result = d.intValue();
        return result;
    }
    private Double validateDouble(Cell cell) {
        assert cell != null;
        Double d = cell.getNumericCellValue();
        return d;
    }
}
