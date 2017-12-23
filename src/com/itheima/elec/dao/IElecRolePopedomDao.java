package com.itheima.elec.dao;

import java.util.List;

import com.itheima.elec.domain.ElecRolePopedom;

public interface IElecRolePopedomDao extends ICommonDao<ElecRolePopedom> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecRolePopedomDaoImpl";

	List<Object> findPopedomByRoleIDs(String condition);

	
	
}
