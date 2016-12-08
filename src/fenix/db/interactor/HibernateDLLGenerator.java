package fenix.db.interactor;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

@SuppressWarnings("deprecation")
public class HibernateDLLGenerator {
	enum Dialect {
		MYSQL("org.hibernate.dialect.MySQLInnoDBDialect"),
		ORACLE("org.unhcr.omss.db.oracle.OracleDialectDeferredFK"),
		SYBASE("org.hibernate.dialect.SybaseAnywhereDialect");

		private String className;

		private Dialect(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}

	}

	public void execute(Class<?>... classes) {
		Dialect dialect = Dialect.MYSQL;
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.setProperty(Environment.DIALECT, dialect.getClassName());
		for (Class<?> entityClass : classes) {
			configuration.addAnnotatedClass(entityClass);
		}

		SchemaExport schemaExport = new SchemaExport(configuration);
		schemaExport.setDelimiter(";");
		schemaExport.setOutputFile(String.format("%s_%s.%s ", new Object[] {"ddl", dialect.name().toLowerCase(), "sql" }));
		boolean consolePrint = true;
		boolean exportInDatabase = false;
		schemaExport.create(consolePrint, exportInDatabase);
	}

}
