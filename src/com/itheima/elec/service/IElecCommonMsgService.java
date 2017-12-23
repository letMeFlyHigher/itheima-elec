package com.itheima.elec.service;

import com.itheima.elec.domain.ElecCommonMsg;


public interface IElecCommonMsgService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecCommonMsgServiceImpl";

	ElecCommonMsg findCommonMsg();

	void saveCommonMsg(ElecCommonMsg elecCommonMsg);

}
