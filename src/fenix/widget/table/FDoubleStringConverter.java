/*
 * Copyright (C) 2016 ss
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
package fenix.widget.table;

import java.text.DecimalFormat;
import javafx.util.StringConverter;

/**
 *
 * @author ss
 */
public class FDoubleStringConverter extends StringConverter<Double> {
    private static final DecimalFormat DF =new DecimalFormat("0.00");
    @Override
    public String toString(Double value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        return DF.format(value.doubleValue());
    }

    @Override
    public Double fromString(String value) {
        // If the specified value is null or zero-length, return null
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.length() < 1) {
            return null;
        }

        return Double.valueOf(value);
    }
    
}
