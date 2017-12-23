package com.itheima.elec.web.action;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.itheima.elec.domain.ElecCommonMsg;
import com.itheima.elec.domain.ElecPopedom;
import com.itheima.elec.domain.ElecRole;
import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.service.IElecCommonMsgService;
import com.itheima.elec.service.IElecRoleService;
import com.itheima.elec.service.IElecUserService;
import com.itheima.elec.utils.ElecPopedomTree;
import com.itheima.elec.utils.LogonUtils;
import com.itheima.elec.utils.MD5keyBean;
import com.itheima.elec.utils.ValueUtils;
import com.itheima.elec.web.form.MenuForm;


@SuppressWarnings("serial")
@Controller("elecMenuAction")
@Scope(value="prototype")
public class ElecMenuAction extends BaseAction<MenuForm> {
	
	MenuForm menuForm = this.getModel();
	
	/**注入运行监控Service*/
	@Resource(name=IElecCommonMsgService.SERVICE_NAME)
	IElecCommonMsgService elecCommonMsgService;
	@Resource(name=IElecUserService.SERVICE_NAME)
	IElecUserService elecUserService;
	@Resource(name=IElecRoleService.SERVICE_NAME)
	IElecRoleService elecRoleService;
	
	/**  
	* @Name: menuHome
	* @Description: 跳转到系统登录的首页
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-29 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到menu/home.jsp
	*/
	public String menuHome(){
		String name = menuForm.getName();
		String password = menuForm.getPassword();
		MD5keyBean md5 = new MD5keyBean();
		String md5Password = md5.getkeyBeanofStr(password);
		ElecUser elecUser = elecUserService.findUserByNameAndPassword(name,md5Password);
		
		if(elecUser==null){
			this.addFieldError("logon error", "用户名或密码错误");
			return "logonError";
		}else{
			//判断填写的验证码和后台生成的验证码是否相同
			if(!LogonUtils.checkedNumberKey(request)){
				this.addFieldError("yanzhengma error", "验证码输入有误");
				return "logonError";
			}
			
			LogonUtils.remeberme(name, password, request, response);
			
			/**二：判断用户是否分配了角色，如果分配了角色，将角色的信息存放起来*/
			/**
			 * 存放角色信息
			 * key：角色ID
			 * value：角色名称
			 */
			Hashtable<String, String> ht = new Hashtable<String, String>();
			Set<ElecRole> elecRoles = elecUser.getElecRoles();
			//当前用户没有分配角色
			if(elecRoles==null || 0==elecRoles.size()){
				this.addActionError("当前用户没有分配角色，请与管理员联系！");
				return "logonError";//跳转到登录页面
			}
			//如果分配了角色，将角色的信息存放起来
			else{
				for(ElecRole elecRole:elecRoles){
					//一个用户可以对应多个角色
					ht.put(elecRole.getRoleID(), elecRole.getRoleName());
				}
			}
			/**三：判断用户对应的角色是否分配了权限，如果分配了权限，将权限的信息存放起来（aa）*/
			//将权限的细心你存放到字符串中，存放的权限的mid（字符串的格式：aa@ab@ac@ad@ae） -----jquery的ztree动态数据加载
			String popedom = elecRoleService.findPopedomByRoleIDs(ht);
			if(StringUtils.isBlank(popedom)){
				this.addActionError("当前用户具有的角色没有分配权限，请与管理员联系！");
				return "logonError";//跳转到登录页面
			}
			
			//将ElecUser对象放置到Session中
			request.getSession().setAttribute("globle_user", elecUser);
			//将Hashtable中存放的角色信息，放置到Session中
			request.getSession().setAttribute("globle_role", ht);
			//将权限的字符串（格式：aa@ab@ac@ad@ae）存放到Session中
			request.getSession().setAttribute("globle_popedom", popedom);
			
//			this.SavePopedomToSession(elecUser,request);
			
			return "menuHome";
		}
	
	}
	
	public void SavePopedomToSession(ElecUser elecUser,HttpServletRequest request){
		String name=(String) request.getAttribute("name");
		//保存权限信息
		HttpSession session = request.getSession();
		if(session.getAttribute("urlSet")==null){
			Set<String> urlSet = new HashSet<String>();
			Set<ElecPopedom> popedomSet = new HashSet<ElecPopedom>();
			//准备工作：根据用户名，用户密码查询用户，得到elecUser
				//通过elecUser得到elecRoles
				Set<ElecRole> elecRoles = elecUser.getElecRoles();
				//遍历roleIDs,查询权限信息，放入到pepedomSet中
				for(ElecRole elecRole:elecRoles){
					String roleID = elecRole.getRoleID();
					
					List<ElecPopedom> parentPopedomSet = elecRoleService.findAllPopedomListByRoleID(roleID);
					for(ElecPopedom pElecPopedom:parentPopedomSet){
						popedomSet.addAll(pElecPopedom.getList());
					}
				
				}
				for(ElecPopedom elecPopedom:popedomSet){
					urlSet.add(elecPopedom.getUrl());
				}
				urlSet.add("/index.jsp");
			session.setAttribute("urlSet", urlSet);
			
		}
	}
	
	/**标题*/
	public String title(){
		return "title";
	}
	
	/**菜单*/
	public String left(){
		return "left";
	}
	
	/**框架大小改变*/
	public String change(){
		return "change";
	}
	
	/**ZTree数据加载*/
	public String showMenu(){
		//获取Session中存放的权限字符串（格式：aa@ab@ac）
		String popedom = (String) request.getSession().getAttribute("globle_popedom");
		//1：查询当前用户所具有的功能权限，使用权限，查询权限表，返回List<ElecPopedom>
		//List<ElecPopedom> list = elecRoleService.findPopedomListByUser(popedom);
		List<ElecPopedomTree> list = elecRoleService.findPopedomListByUser(popedom);
		//2：将list放置到栈顶，栈顶的对象转换成json数组的形式
		ValueUtils.putValueStack(list);

		return "showMenu";
		
	}
	
	/**  
	* @Name: loading
	* @Description: 功能页面的显示
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-29 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到menu/loading.jsp
	*/
	public String loading(){
		//查询设备运行情况，放置到浮动框中
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = elecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "loading";
	}
	
	/**  
	* @Name: logout
	* @Description: 重新登录
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-29 （创建日期）
	* @Parameters: 无
	* @Return: String：重定向到index.jsp
	*/
	public String logout(){
		/**清空Session*/
		//指定Session的名称清空
//		request.getSession().removeAttribute(arg0);
		//清空所有Session
		request.getSession().invalidate();
		return "logout";
	}
	
	/**  
	* @Name: alermStation
	* @Description: 显示站点运行情况
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-29 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到menu/alermStation.jsp
	*/
	public String alermStation(){
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = elecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "alermStation";
	}

	/**  
	* @Name: alermDevice
	* @Description: 显示设备运行情况
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-29 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到menu/alermDevice.jsp
	*/
	public String alermDevice(){
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = elecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "alermDevice";
	}
	
}
