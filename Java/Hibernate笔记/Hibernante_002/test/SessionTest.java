import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

public class SessionTest {
	@Test
	public void testOpenSession() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		// Session session = sessionFactory.openSession();
		
		Session session1 = sessionFactory.openSession();
		Session session2 = sessionFactory.openSession();
		
		System.out.println(session1 == session2);
		
		
//		if (session != null) {
//			System.out.println("session create success!");
//		} else {
//			System.out.println("session create fail!");
//		}
	}
	
	@Test
	public void testGetCurrentSession() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		// Session session = sessionFactory.getCurrentSession();
		Session session1 = sessionFactory.getCurrentSession();
		Session session2 = sessionFactory.getCurrentSession();
		
		System.out.println(session1 == session2);
		
//		if (session != null) {
//			System.out.println("session create success!");
//		} else {
//			System.out.println("session create fail!");
//		}
	}
	
	/**
	 * 連接池溢出危險，不手動釋放session，使用opensession每一次都會創建一個新連接。
	 */
	@Test
	public void testSaveStudentsWithOpenSession() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		Session session1 = sessionFactory.openSession();
		Transaction transaction = session1.beginTransaction();
		Students student = new Students(3, "mengyuan", "man", new Date(), "nanning");
		
		session1.doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println("connection hashCode:" + conn.hashCode());
			}
		});
		
		session1.save(student);
		// session1.close();
		transaction.commit();
		
		Session session2 = sessionFactory.openSession();
		transaction = session2.beginTransaction();
		student = new Students(2, "palameng", "man", new Date(), "beijing");
		
		session2.doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println("connection hashCode:" + conn.hashCode());
			}
		});
		
		session2.save(student);
		// session2.close();
		transaction.commit();	
	}
	
	/**
	 * 每一次都用相同的連接，並且提交事務后會自動釋放資源，單例模式。
	 */
	@Test
	public void testSaveStudentsWithGetCurrentSession() {
		Configuration configuration = new Configuration().configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		Session session1 = sessionFactory.getCurrentSession();
		Transaction transaction = session1.beginTransaction();
		Students student = new Students(4, "mengyuan", "man", new Date(), "nanning");
		
		session1.doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println("connection hashCode:" + conn.hashCode());
			}
		});
		
		session1.save(student);
		// session1.close();
		transaction.commit();
		
		Session session2 = sessionFactory.getCurrentSession();
		transaction = session2.beginTransaction();
		student = new Students(5, "palameng", "man", new Date(), "beijing");
		
		session2.doWork(new Work() {
			
			@Override
			public void execute(Connection conn) throws SQLException {
				// TODO Auto-generated method stub
				System.out.println("connection hashCode:" + conn.hashCode());
			}
		});
		
		session2.save(student);
		// session2.close();
		transaction.commit();	
	}
	
}
