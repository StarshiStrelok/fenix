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

import fenix.vo.Entry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Извлекает информацию о накладной из ее .xslx-файла.
 * @author Катапусик
 */
public class InvoiceConverter {
    
    /**
     * Производит разбор .xslx-файла.
     * @param file - файл.
     * @return - список извлеченных записей.
     * @throws java.io.FileNotFoundException
     */
    public static List<Entry> extract(File file) throws FileNotFoundException, IOException {
        List<Entry> list = new ArrayList<>();
        InputStream is = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheet("Накладная");
        Iterator<Row> itr = sheet.iterator();
        itr.next();
        while(itr.hasNext()) {
            Row row = itr.next();
            Entry entry = new Entry();
            entry.setName(validateString(row.getCell(1)));
            Cell cell = row.getCell(2);
            if(cell != null) {
                entry.setQuantity(validateInt(cell));
            }
            entry.setPrice(validateDouble(row.getCell(3)));
            
            list.add(entry);
        }
        
        return list;
    }
    
    /**
     * Метод валидации строки.
     * @param cell - ячейка, содержащая строковое значение.
     * @return - возвращает строковое значение.
     * @throws UnsupportedEncodingException 
     */
    private static String validateString(Cell cell) throws UnsupportedEncodingException {
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
    private static Integer validateInt(Cell cell) {
        if(cell == null) {
            return 0;
        }
        Double d = cell.getNumericCellValue();
        Integer result = d.intValue();
        return result;
    }
    private static Double validateDouble(Cell cell) {
        if(cell == null) {
            return 0d;
        }
        Double d = cell.getNumericCellValue();
        return d;
    }
}
