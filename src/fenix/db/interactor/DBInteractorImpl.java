package fenix.db.interactor;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import fenix.vo.Entry;
import fenix.vo.Type;
import org.hibernate.HibernateException;



public class DBInteractorImpl implements IDBInteractor {
	private final Logger logger = Logger.getLogger("DBInteractorImpl"); 

	@Override
	public void addEntry(Entry entry) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			session.save(entry);
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Add new Entry to DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@Override
	public void updateEntry(Entry entry) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			session.update(entry);
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Update Entry to DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@Override
	public Entry getEntryByID(Integer ID) {
		Entry entry = null;
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			entry = (Entry)session.load(Entry.class, ID);
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Get Entry by ID from DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		return entry;
	}

	@Override
	public List<Entry> getAllEntries() {
		List<Entry> list = null;
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			list = session.createCriteria(Entry.class).list();
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Get all Entries from DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		return list;
	}

	@Override
	public void deleteEntry(Entry entry) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			session.delete(entry);
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Delete Entry from DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		
	}

	@Override
	public void addType(Type type) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			session.save(type);
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Add new Type to DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@Override
	public List<Type> getAllTypes() {
		List<Type> list = null;
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			Query query = session.createSQLQuery("select * from type_of_goods").addEntity(Type.class);
			list = query.list();
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Get all Types from DB - failed.", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		return list;
	}

	@Override
	public Type getTypeByDescription(String description) {
		List<Type> list = null;
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			list = session.createCriteria(Type.class)
					.add(Restrictions.eq("description", description))
					.setFetchMode("entries", FetchMode.JOIN).list();
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Get Type By Description - failed", e);
			txn.rollback();
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		return list.get(0);
	}

	@Override
	public void destroyConnect() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		if(!sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}

	@Override
	public boolean updateDataBase(List<Entry> list) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			txn = session.beginTransaction();
			for(Entry entry : list) {
				Entry entryDB = (Entry)session.load(Entry.class, entry.getId());
				int quantityDB = entryDB.getQuantity();
				int delta = quantityDB - entry.getQuantity();
				entryDB.setQuantity(delta);
				session.update(entryDB);
			}
			txn.commit();
		} catch(HibernateException e) {
			logger.error("DBInteractorImpl# Update DB after generate report - failed", e);
			txn.rollback();
			return false;
		} finally {
			if(session != null && session.isOpen()) {
				session.close();
			}
		}
		return true;
	}

}
