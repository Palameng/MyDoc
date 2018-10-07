import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
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
		// Students student = new Students(1, "mengyuan", "man", new Date(), "nanning");
		
		Students student = new Students();
		// student.setSid(1);
		student.setSname("ivy");
		student.setGender("women");
		student.setBirthday(new Date());
		student.setAddress("guilin");
		
		session.save(student);
	}
	
	@After
	public void destory() {
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
}
