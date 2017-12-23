package com.itheima.elec.service;

import java.util.Hashtable;
import java.util.List;

import com.itheima.elec.domain.ElecPopedom;
import com.itheima.elec.domain.ElecRole;
import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.utils.ElecPopedomTree;



public interface IElecRoleService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecRoleServiceImpl";

	List<ElecRole> findAllRoleList();

	List<ElecPopedom> findAllPopedomList();

	List<ElecPopedom> findAllPopedomListByRoleID(String roleID);

	List<ElecUser> findAllUserListByRoleID(String roleID);

	void saveRole(ElecPopedom elecPopedom);

	String findPopedomByRoleIDs(Hashtable<String, String> ht);
	//原版
	//List<ElecPopedom> findPopedomListByUser(String popedom);
	//改良版
	List<ElecPopedomTree> findPopedomListByUser(String popedom);

}
