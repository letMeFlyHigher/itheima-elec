package com.itheima.elec.web.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.itheima.elec.domain.ElecPopedom;
import com.itheima.elec.domain.ElecRole;
import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.service.IElecRoleService;
import com.itheima.elec.service.IElecUserService;

@SuppressWarnings("serial")
@Controller("elecRoleAction")
@Scope(value="prototype")
public class ElecRoleAction extends BaseAction<ElecPopedom> {

	ElecPopedom elecPopedom = this.getModel();
	/**注入Service*/
	@Resource(name=IElecRoleService.SERVICE_NAME)
	IElecRoleService elecRoleService;
	@Resource(name=IElecUserService.SERVICE_NAME)
	IElecUserService elecUserService;
	
	/**
	 *	转发至主页
	 * @return
	 */
	public String home(){
		List<ElecPopedom> elecPopedomList = elecRoleService.findAllPopedomList();
		List<ElecRole> elecRoleList = elecRoleService.findAllRoleList();
		request.setAttribute("elecPopedomList", elecPopedomList);
		request.setAttribute("elecRoleList", elecRoleList);
		return "home";
	}
	/**
	 * 查询权限信息，查询用户信息转发至编辑页面
	 * @return 
	 */
	public String edit(){
		String roleID = elecPopedom.getRoleID(); 
		List<ElecPopedom> elecPopedomList = elecRoleService.findAllPopedomListByRoleID(roleID);
		request.setAttribute("elecPopedomList", elecPopedomList);
		
		//查询用户信息
		List<ElecUser> elecUserList = elecRoleService.findAllUserListByRoleID(roleID);
		request.setAttribute("elecUserList", elecUserList);
		return "edit";
	}
	
	/**@name saveRole
	 * @description 保存角色权限信息,保存角色用户信息
	 * 
	 */
	
	public String saveRole(){
		//1.保存角色权限信息
		elecRoleService.saveRole(elecPopedom);
		return "saveRole";
	}
	
}
