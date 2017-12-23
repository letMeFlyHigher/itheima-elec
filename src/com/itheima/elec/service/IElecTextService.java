package com.itheima.elec.service;

import java.util.List;

import com.itheima.elec.domain.ElecText;

public interface IElecTextService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecTextServiceImpl";
	
	void saveElecText(ElecText elecText);

	List<ElecText> findCollectionByConditionNoPage(ElecText elecText);
}
