package junit;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.itheima.elec.domain.ElecText;
import com.itheima.elec.service.IElecTextService;

public class TestService {

	/**保存*/
	@Test
	public void save(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextService elecTextService = (IElecTextService) ac.getBean(IElecTextService.SERVICE_NAME);
		
		ElecText elecText = new ElecText();
		elecText.setTextName("测试Service名称");
		elecText.setTextDate(new Date());
		elecText.setTextRemark("测试Service备注");
		elecTextService.saveElecText(elecText);
		
	}
	
	/**模拟Action层，测试底层方法，指定查询条件查询结果列表*/
	@Test
	public void findCollectionByConditionNoPage(){
		//加载spring容器
		ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
		IElecTextService elecTextService = (IElecTextService) ac.getBean(IElecTextService.SERVICE_NAME);
		
		//模型驱动中封装结果
		ElecText elecText = new ElecText();
//		elecText.setTextName("张");
//		elecText.setTextRemark("张");
		List<ElecText> list = elecTextService.findCollectionByConditionNoPage(elecText);
		if(list!=null && list.size()>0){
			for(ElecText text:list){
				System.out.println(text.getTextName()+"   "+text.getTextRemark());
			}
		}
		
	}
}
