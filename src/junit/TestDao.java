package junit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.itheima.elec.dao.IElecTextDao;
import com.itheima.elec.domain.ElecText;

public class TestDao {

	/**保存*/
	@Test
	public void save(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextDao elecTextDao = (IElecTextDao) ac.getBean(IElecTextDao.SERVICE_NAME);
		
		ElecText elecText = new ElecText();
		elecText.setTextName("测试Dao名称");
		elecText.setTextDate(new Date());
		elecText.setTextRemark("测试Dao备注");
		elecTextDao.save(elecText);
		
	}
	
	/**更新*/
	@Test
	public void update(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextDao elecTextDao = (IElecTextDao) ac.getBean(IElecTextDao.SERVICE_NAME);
		
		ElecText elecText = new ElecText();
		elecText.setTextID("402895ef49f462790149f46280060001");
		elecText.setTextName("王五");
		elecText.setTextDate(new Date());
		elecText.setTextRemark("王小五");
		elecTextDao.update(elecText);
		
	}
	
	/**使用主键ID查询对象的对象*/
	@Test
	public void findObjectById(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextDao elecTextDao = (IElecTextDao) ac.getBean(IElecTextDao.SERVICE_NAME);
		Serializable id = "402895ef49f462790149f46280060001";
		ElecText elecText = elecTextDao.findObjectByID(id);
		System.out.println(elecText.getTextName()+"    "+elecText.getTextRemark());
	}
	
	/**删除（使用主键ID删除）*/
	@Test
	public void deleteObjectByIds(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextDao elecTextDao = (IElecTextDao) ac.getBean(IElecTextDao.SERVICE_NAME);
		//Serializable [] ids = {"402895ef49f42c000149f42c0faf0001","402895ef49f42db20149f42db7d10001"};
		Serializable ids = "402895ef49f46a760149f46a7c560001";
		elecTextDao.deleteObjectByIds(ids);
	}
	
	/**删除（使用集合List进行删除）*/
	@Test
	public void deleteObjectByCollection(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextDao elecTextDao = (IElecTextDao) ac.getBean(IElecTextDao.SERVICE_NAME);
		List<ElecText> list = new ArrayList<ElecText>();
		ElecText elecText1 = new ElecText();
		elecText1.setTextID("402895ef49f46c5f0149f46c64cd0001");
		
		ElecText elecText2 = new ElecText();
		elecText2.setTextID("402895ef49f46cd80149f46d55950001");
		
		ElecText elecText3 = new ElecText();
		elecText3.setTextID("402895ef49f46f060149f46f0cbb0001");
		
		list.add(elecText1);
		list.add(elecText2);
		list.add(elecText3);
		
		elecTextDao.deleteObjectByCollection(list);
	}
}
