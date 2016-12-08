package fenix.invoice.array;

import java.util.ArrayList;
import java.util.List;

import fenix.vo.Entry;

/**
 * Список записей для накладной.
 *
 * @author Катапусик
 *
 */
public class InvoiceList extends ArrayList<Entry> implements List<Entry> {

    private static final long serialVersionUID = 1L;
    private static InvoiceList instance;
    public static final String TOTAL = "Итого";

    public static InvoiceList getInstance() {
        if (instance == null) {
            instance = new InvoiceList();
        }
        return instance.refresh();
    }

    /**
     * Добавить запись в список. При наличии такой записи, заменить старую новой.
     * @param entry - запись для добавления в список.
     * @return - результат операции.
     */
    public boolean addEntry(Entry entry) {
        if (entry == null) {
            return false;
        } else {
            int index = entry.getId();
            for (int i = 0; i < this.size(); i++) {
                Entry e = this.get(i);
                if (index == e.getId()) {
                    this.remove(i);
                    this.add(entry);
                    return true;
                }
            }
            add(entry);
            return true;
        }
    }

    /**
     * Получить обновленный список для накладной.
     *
     * @return
     */
    private InvoiceList refresh() {
        Entry sum = null;
        for (int i = 0; i < this.size(); i++) {
            Entry e = this.get(i);
            if (TOTAL.equals(e.getName())) {
                sum = e;
                this.remove(i);
                break;
            }
        }
        if (sum == null) {
            sum = new Entry();
            sum.setName(TOTAL);
            sum.setPrice(getTotal());
            this.add(sum);
            return this;
        } else {
            sum.setPrice(getTotal());
            this.add(this.size(), sum);
            return this;
        }
    }

    /**
     * Вернуть итоговую сумму.
     *
     * @return
     */
    private Double getTotal() {
        Double total = 0d;
        for (Entry e : this) {
            if (!TOTAL.equals(e.getName())) {
                total = total + e.getQuantity() * e.getPrice();
            }
        }
        return total;
    }
    
    /**
     * Пустой массив или нет.
     * @return - true - пустой, false - нет.
     */
    @Override
    public boolean isEmpty() {
        if(this.size() == 0) {
            return true;
        }
        if(this.size() == 1) {
            Entry e = this.get(0);
            return TOTAL.equals(e.getName());
        } else {
            return false;
        }
    }
}
