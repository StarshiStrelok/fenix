package fenix.db.interactor;

import java.util.List;

import fenix.vo.Entry;
import fenix.vo.Type;

/**
 * Интерфейс - методы доступа к базе данных.
 * @author Катапусик
 *
 */
public interface IDBInteractor {
	/**
	 * Добавить новую запись в базу.
	 * @param entry - запись.
	 */
	public void addEntry(Entry entry);
	
	/**
	 * Обновить запись в базе.
	 * @param entry - запись.
	 */
	public void updateEntry(Entry entry);
	
	/**
	 * Получить запись по ID
	 * @param ID - первичный ключ.
	 * @return - обновленную запись.
	 */
	public Entry getEntryByID(Integer ID);
	
	/**
	 * Получить все записи из базы.
	 * @return - список записей.
	 */
	public List<Entry> getAllEntries();
	
	/**
	 * Удалить запись из базы.
	 * @param entry
	 */
	public void deleteEntry(Entry entry);
	
	/**
	 * Добавить новый тип продукции в базу.
	 * @param type
	 */
	public void addType(Type type);
	
	/**
	 * Получить все типы продукции из базы.
	 * @return - список типов продукции.
	 */
	public List<Type> getAllTypes();
	
	/**
	 * Найти тип продукции по его описанию.
	 * @param description - описание типа продукции.
	 * @return - тип продукции.
	 */
	public Type getTypeByDescription(String description);
	
	/**
	 * Разрушить сессию.
	 */
	public void destroyConnect();
	
	/**
	 * Обновить базу данных после генерации накладной.
	 * @param list - список записей для накладной.
	 * @return - результат операции.
	 */
	public boolean updateDataBase(List<Entry> list);
}
