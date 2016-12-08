package fenix.db.interactor;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@SuppressWarnings("deprecation")
public class HibernateUtil {
	private static Logger logger = Logger.getLogger("HibernateUtil");
    private static SessionFactory sessionFactory = null;
    private static Configuration cfg;
    
    static {
        try {
        	cfg = new Configuration();
        	cfg.configure("hibernate.cfg.xml");
        	
            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception e) {
            logger.error("Hibernate SessionFactory creating error", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**
     * Генерация DDL-схемы.
     */
    public static void generateSchema() {
        assert cfg != null;
        SchemaExport schemaExport = new SchemaExport(cfg);
	schemaExport.setDelimiter(";");
        schemaExport.setOutputFile(String.format("%s_%s.%s ", new Object[] {"ddl", "org.hibernate.dialect.MySQLInnoDBDialect", "sql" }));
        schemaExport.create(true, false);
    }
}