package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecCommonMsgContentDao;
import com.itheima.elec.domain.ElecCommonMsgContent;


@Repository(IElecCommonMsgContentDao.SERVICE_NAME)
public class ElecCommonMsgContentDaoImpl  extends CommonDaoImpl<ElecCommonMsgContent> implements IElecCommonMsgContentDao {

}
