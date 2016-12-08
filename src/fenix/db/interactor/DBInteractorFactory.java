package fenix.db.interactor;

public class DBInteractorFactory {
	private static IDBInteractor iDBInteractor= null;
	private static DBInteractorFactory instance = null;
	
	public static synchronized DBInteractorFactory getInstance() {
		if(instance == null) {
			instance = new DBInteractorFactory();
		}
		return instance;
	}
	
	public IDBInteractor getIDBInteractor() {
		if(iDBInteractor == null) {
			iDBInteractor = new DBInteractorImpl();
		}
		return iDBInteractor;
	}
}
