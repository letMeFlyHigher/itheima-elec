package junit;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import com.itheima.elec.domain.ElecText;

public class TestHibernate {

	/**测试保存*/
	@Test
	public void save(){
		Configuration configuration = new Configuration();
		configuration.configure();//加载classpath下的hibernate.cfg.xml的文件
		SessionFactory sf = configuration.buildSessionFactory();
		Session s = sf.openSession();
		Transaction tr = s.beginTransaction();
		
		ElecText elecText = new ElecText();
		elecText.setTextName("测试hibernate名称");
		elecText.setTextDate(new Date());
		elecText.setTextRemark("测试hibernate备注");
		s.save(elecText);
		
		tr.commit();
		s.close();
	}
}
