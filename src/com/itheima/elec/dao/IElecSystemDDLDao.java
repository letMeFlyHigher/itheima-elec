package com.itheima.elec.dao;

import java.util.List;

import com.itheima.elec.domain.ElecSystemDDL;

public interface IElecSystemDDLDao extends ICommonDao<ElecSystemDDL> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecSystemDDLDaoImpl";

	List<ElecSystemDDL> findSystemDDLListByDistinct();

	String findDdlNameByKeywordAndDdlCode(String keywrod, String ddlCode);

	
	
}
