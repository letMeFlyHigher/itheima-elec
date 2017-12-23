package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecCommonMsgDao;
import com.itheima.elec.domain.ElecCommonMsg;


@Repository(IElecCommonMsgDao.SERVICE_NAME)
public class ElecCommonMsgDaoImpl  extends CommonDaoImpl<ElecCommonMsg> implements IElecCommonMsgDao {

}
