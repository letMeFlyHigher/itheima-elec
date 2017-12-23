package com.itheima.elec.service;

import java.util.List;

import com.itheima.elec.domain.ElecSystemDDL;



public interface IElecSystemDDLService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecSystemDDLServiceImpl";

	List<ElecSystemDDL> findSystemDDLListByDistinct();

	List<ElecSystemDDL> findSystemDDLListByKeyword(String keyword);

	void saveSystemDDL(ElecSystemDDL elecSystemDDL);

	String findDdlNameByKeywordAndDdlCode(String keyword, String ddlCode);


}
