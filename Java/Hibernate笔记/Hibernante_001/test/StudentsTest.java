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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// test class
public class StudentsTest {
	
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;
	
	@Before
	public void init() {

		Configuration config = new Configuration().configure();

		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).buildServiceRegistry();

		sessionFactory = config.buildSessionFactory(serviceRegistry);

		session = sessionFactory.openSession();

		transaction = session.beginTransaction();
	}
	
	@Test
	public void testSaveStudents() {
		Students student = new Students(1, "mengyuan", "man", new Date(), "nanning");
		
		// 如果注释掉开启事务和事务提交的代码，需要使用dowork来提交
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				// TODO Auto-generated method stub
				connection.setAutoCommit(true);
			}
		});
		
		session.save(student);
	}
	
	@After
	public void destory() {
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
}
