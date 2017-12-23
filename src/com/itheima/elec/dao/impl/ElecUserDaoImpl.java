package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecUserDao;
import com.itheima.elec.domain.ElecUser;


@Repository(IElecUserDao.SERVICE_NAME)
public class ElecUserDaoImpl  extends CommonDaoImpl<ElecUser> implements IElecUserDao {

}
