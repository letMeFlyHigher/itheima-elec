package com.itheima.elec.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.elec.dao.IElecPopedomDao;
import com.itheima.elec.dao.IElecRoleDao;
import com.itheima.elec.dao.IElecRolePopedomDao;
import com.itheima.elec.dao.IElecUserDao;
import com.itheima.elec.domain.ElecPopedom;
import com.itheima.elec.domain.ElecRole;
import com.itheima.elec.domain.ElecRolePopedom;
import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.service.IElecRoleService;
import com.itheima.elec.utils.ElecPopedomTree;


@Service(IElecRoleService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecRoleServiceImpl implements IElecRoleService {

	/**用户表Dao*/
	@Resource(name=IElecUserDao.SERVICE_NAME)
	IElecUserDao elecUserDao;
	
	/**角色表Dao*/
	@Resource(name=IElecRoleDao.SERVICE_NAME)
	IElecRoleDao elecRoleDao;
	
	/**权限表Dao*/
	@Resource(name=IElecPopedomDao.SERVICE_NAME)
	IElecPopedomDao elecPopedomDao;
	
	
	/**角色权限表Dao*/
	@Resource(name=IElecRolePopedomDao.SERVICE_NAME)
	IElecRolePopedomDao elecRolePopedomDao;

	/**
	 * 查询所有的Role表信息
	 * @return 返回表Role
	 */
	public List<ElecRole> findAllRoleList() {
		return elecRoleDao.findCollectionByConditionNoPage("",null, null);
		
	}

	/**
	 * 查询所有的Popedom表信息
	 * @return 返回表Popedom
	 */
	public List<ElecPopedom> findAllPopedomList() {
		return elecPopedomDao.findCollectionByConditionNoPage("", null, null);
	}

	/**
	 * @author 99624
	 * @description 通过角色ID，查找对应的所有的权限。
	 * @date 2017-12-19
	 * @return 角色的权限
	 */
	public List<ElecPopedom> findAllPopedomListByRoleID(String roleID) {
		
		List<ElecRolePopedom> elecRolePopedomList=findAllRolePopedomListByRoleID(roleID);
	
		String midStr = "";//拼接mid字符串：ab@ac@ad
		
		//如果该角色没有权限咋整？
		if(elecRolePopedomList!=null && elecRolePopedomList.size()>0){
			Iterator<ElecRolePopedom> iter = elecRolePopedomList.iterator();
			
			StringBuffer  sb = new StringBuffer();
			
			while(iter.hasNext()){
				ElecRolePopedom elecRolePopedom = iter.next();
				String mid = elecRolePopedom.getMid();
				sb.append(mid).append("@");
			}
			sb.deleteCharAt(sb.length()-1);
			midStr = sb.toString();//拼接完成
		}
		//利用isparent 变量，查询elecPopedom表，得到所有的父节点
		/**
		 * select * from ElecPopedom o 
		 *  and o.isParent=?
		 */
		String condition = " and o.isParent=?";
		Boolean tempParam = true;
		Object[] param1={tempParam};
		List<ElecPopedom> parentPopedomList = elecPopedomDao.findCollectionByConditionNoPage(condition, param1, null);
		//遍历所有的权限
		Iterator<ElecPopedom> popedomIter= parentPopedomList.iterator();
		while(popedomIter.hasNext()){
			ElecPopedom elecPopedom = popedomIter.next();
			String popMid = elecPopedom.getMid();
			condition = " and o.pid=?";
			Object[] param2 = {popMid};
			List<ElecPopedom> childPopedomList = elecPopedomDao.findCollectionByConditionNoPage(condition, param2, null);
			
			for(int i = 0; i < childPopedomList.size();i++){
				ElecPopedom childPopedom = childPopedomList.get(i);
				if(midStr.contains(childPopedom.getMid())){
					//最新的改进，子节点选中的时候，父节点也要选中，所以
					childPopedom.setFlag("1");
					elecPopedom.setFlag("1");
				}else{
					childPopedom.setFlag("0");
				}
			}
			elecPopedom.setList(childPopedomList);
		}
		
		return parentPopedomList;
	}

	/**
	 * select * from elecRole o 
	 * 	and o.roleID=?
	 */
	private List<ElecRolePopedom> findAllRolePopedomListByRoleID(String roleID) {
	
		String condition = " and o.roleID=?";
		Object[] params = {roleID};
		return elecRolePopedomDao.findCollectionByConditionNoPage(condition, params, null);
		
	}
	/**
	 * 根据roleID，查询表elecRole到所有对应的elecUsers
	 * 如果userID不为空
		 * 查询所有的用户
		 * 如果用户不为空
			 * 遍历所有的用户
			 * 	如果userIDList中包含该ID---》将其checkbox标志，flag设置为1
			 * 	如果userIDList中不包含该ID---->>将其checkbox标志，flag设置为0
	 */
	public List<ElecUser> findAllUserListByRoleID(String roleID) {
		ElecRole elecRole = elecRoleDao.findObjectByID(roleID);
		Set<ElecUser> elecUserChecked = elecRole.getElecUsers();
		
		List<ElecUser> elecUserList = elecUserDao.findCollectionByConditionNoPage("", null, null);
		if(elecUserList!=null && elecUserList.size()>0){
			for(int i = 0 ; i < elecUserList.size(); i ++){
				if(elecUserChecked.contains(elecUserList.get(i))){
					elecUserList.get(i).setChecked("1");
				}else{
					elecUserList.get(i).setChecked("0");
				}
			}
		}
		return elecUserList;
	}

	/**
	 * @desecription 保存用户角色信息 
	 */
	//加上事务
	@Transactional(readOnly=false)
	public void saveRole(ElecPopedom elecPopedom) {
		String roleID = elecPopedom.getRoleID();
		String[] selectopers = elecPopedom.getSelectoper();
		//保存权限角色信息
		saveRolePopedom(selectopers,roleID);
		
		//保存用户角色信息
		String[] userIDs = elecPopedom.getSelectuser();
		saveRoleUser(userIDs,roleID);
		
	}
	
	private void saveRoleUser(String[] userIDs, String roleID) {
		ElecRole elecRole = elecRoleDao.findObjectByID(roleID);
		Set<ElecUser> elecUsers = elecRole.getElecUsers();
		if(elecUsers!=null&&elecUsers.size()>0){
			elecUsers.clear();
		}
		if(userIDs!=null&&userIDs.length>0){
			Set<ElecUser> elecUserSet = new HashSet<ElecUser>();
			for(int i = 0; i < userIDs.length;i++){
				ElecUser elecUser = elecUserDao.findObjectByID(userIDs[i]);
				elecUserSet.add(elecUser);
			}
			elecRole.setElecUsers(elecUserSet);
		}
	}

	public void saveRolePopedom(String[] selectopers,String roleID){
		//根据roleID查出elecRolePopedom
				List<ElecRolePopedom> elecRolePopedomList = findAllRolePopedomListByRoleID(roleID);
				//删除查出来的所有elecRolePopedom
				elecRolePopedomDao.deleteObjectByCollection(elecRolePopedomList);
				
				//如果权限被选中，查出相应的权限角色信息做保存，否则什么也不做
				if(selectopers!=null&&selectopers.length>0){
							
					for(String mid : selectopers){
						/**
						 * select * from ElecPopedom o where 1=1
						 *  and o.mid=?
						 *  and o.isParent=?
						 *  mid,0
						 *  
						 */
						String condition = " and o.mid=?"
								+ " and o.isParent=?";
						Object[] params = {mid,false};
						List<ElecPopedom> elecPopedomList = elecPopedomDao.findCollectionByConditionNoPage(condition, params, null);
						//如果存在
						if(elecPopedomList!=null && elecPopedomList.size()>0){
							String pid = elecPopedomList.get(0).getPid();
			
							//组织VO对象
							ElecRolePopedom elecRolePopedom = new ElecRolePopedom();
							elecRolePopedom.setMid(mid);
							elecRolePopedom.setPid(pid);
							elecRolePopedom.setRoleID(roleID);
					
							elecRolePopedomDao.save(elecRolePopedom);
						}
					}
				}
	}

	public String findPopedomByRoleIDs(Hashtable<String, String> ht) {
		//组织查询条件
				StringBuffer buffercondition = new StringBuffer("");
				//遍历Hashtable
				if(ht!=null && ht.size()>0){
					for(Iterator<Entry<String, String>> ite = ht.entrySet().iterator();ite.hasNext();){
						Entry<String, String> entry = ite.next();
						buffercondition.append("'").append(entry.getKey()).append("'").append(",");
					}
					//删除最后一个逗号
					buffercondition.deleteCharAt(buffercondition.length()-1);
				}
				//查询条件
				String condition = buffercondition.toString();
				//组织查询
				List<Object> list = elecRolePopedomDao.findPopedomByRoleIDs(condition);
				//组织权限封装的字符串：（字符串的格式：aa@ab@ac@ad@ae）
				StringBuffer buffer = new StringBuffer("");
				if(list!=null && list.size()>0){
					for(Object o:list){
						buffer.append(o.toString()).append("@");
					}
					//删除最后一个@
					buffer.deleteCharAt(buffer.length()-1);
				}
				return buffer.toString();
	}

	/**
	 * ab@ac@cc --->ab','ac','cc
	 * select * from ElecPopedom where 1=1
	 *  and o.mid in ('ab','ac','cc')
	 *  and isMenu=?
	 */
	/*
	public List<ElecPopedom> findPopedomListByUser(String popedom) {
		//hql语句和sql语句的嵌套查询
		//问题是能动态的加载zTree,不再是树形结构。
		String condition = " and o.mid IN('"+popedom.replace("@", "','")+"') AND isMenu = ?";
			Object [] params = {true};
			Map<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("o.mid", "asc");
			List<ElecPopedom> list = elecPopedomDao.findCollectionByConditionNoPage(condition, params, orderby);
			
			//子节点的样子：mid:'ab',pid:'aa',isParent:0
			//父节点的样子：mid：'aa',pid:'0',isParent:1,list:[]
			//组织树形结构
				//0.初始化一个set：[] pList:()
				//1.得到一个mid，得到一个pid：如果set中不存在pid,用集合记下这个pid，根据pid来查找父节点，把字节点放到父节点下面，把父节点放入到pList下；
					//如果set中存在pid，通过pid在pList中查找父节点，找到后把子节点放到父节点下
				Set<String> pidSet = new HashSet();
				List<ElecPopedom> pList = new ArrayList<ElecPopedom>();
				
				for(int i = 0; i < list.size(); i++){
					ElecPopedom elecPopedom = list.get(i);
					String pid = elecPopedom.getPid();
					
					if(!pidSet.contains(pid)){
						pidSet.add(pid);
						String condition1 = " and o.mid=?";
						Object[] param1={pid};
						List<ElecPopedom> pElecPopedomList = elecPopedomDao.findCollectionByConditionNoPage(condition1, param1, null);
						ElecPopedom pElecPopedom = pElecPopedomList.get(0);
					
						
						//有可能出现bug的地方？因为这里还不是很懂。。这个byID究竟是怎么实现的？？
						if(pElecPopedom!=null){
							List<ElecPopedom> L = new ArrayList<ElecPopedom>();
							L.add(elecPopedom);
							pElecPopedom.setList(L);
							
							pList.add(elecPopedom);
						}
					}else{
						if(pList!=null){
							for(int j = 0; j < pList.size();j++){
								if(pList.get(j).getMid().equals(pid)){
									//pList.get(j).getList().add(elecPopedom);
									pList.get(j).getList().add(elecPopedom);
								}
							}
						}
					}
				}
			return pList;
	}
	*/
	
	//改良版
	public List<ElecPopedomTree> findPopedomListByUser(String popedom) {
		//hql语句和sql语句的嵌套查询
		//问题是能动态的加载zTree,不再是树形结构。
		String condition = " and o.mid IN('"+popedom.replace("@", "','")+"') AND isMenu = ?";
			Object [] params = {true};
			Map<String, String> orderby = new LinkedHashMap<String, String>();
			orderby.put("o.mid", "asc");
			List<ElecPopedom> list = elecPopedomDao.findCollectionByConditionNoPage(condition, params, orderby);
			
			//子节点的样子：mid:'ab',pid:'aa',isParent:0
			//父节点的样子：mid：'aa',pid:'0',isParent:1,list:[]
			//组织树形结构
				//0.初始化一个set：[] pList:()
				//1.得到一个mid，得到一个pid：如果set中不存在pid,用集合记下这个pid，根据pid来查找父节点，把字节点放到父节点下面，把父节点放入到pList下；
					//如果set中存在pid，通过pid在pList中查找父节点，找到后把子节点放到父节点下
				Set<String> pidSet = new HashSet();
				List<ElecPopedomTree> pList = new ArrayList<ElecPopedomTree>();
				
				for(int i = 0; i < list.size(); i++){
					ElecPopedom elecPopedom = list.get(i);
					String pid = elecPopedom.getPid();
					
					if(!pidSet.contains(pid)){
						pidSet.add(pid);
						String condition1 = " and o.mid=?";
						Object[] param1={pid};
						List<ElecPopedom> pElecPopedomList = elecPopedomDao.findCollectionByConditionNoPage(condition1, param1, null);
						ElecPopedom pElecPopedom = pElecPopedomList.get(0);
						ElecPopedomTree elecPopedomTree = new ElecPopedomTree();
						//考虑到把ElecPopedom中的属性改名字，会牵扯到其他的部分，所以新建了一个临时的类，用来存放该信息
						BeanUtils.copyProperties(pElecPopedom, elecPopedomTree);
						
						//有可能出现bug的地方？因为这里还不是很懂。。这个byID究竟是怎么实现的？？
						if(pElecPopedom!=null){
							elecPopedomTree.getNodes().add(elecPopedom);
							pList.add(elecPopedomTree);
						}
					}else{
						if(pList!=null){
							for(int j = 0; j < pList.size();j++){
								if(pList.get(j).getMid().equals(pid)){
									//pList.get(j).getList().add(elecPopedom);
									pList.get(j).getNodes().add(elecPopedom);
								}
							}
						}
					}
				}
			return pList;
	}

	

	
}
