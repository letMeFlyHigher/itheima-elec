package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecRoleDao;
import com.itheima.elec.domain.ElecRole;


@Repository(IElecRoleDao.SERVICE_NAME)
public class ElecRoleDaoImpl  extends CommonDaoImpl<ElecRole> implements IElecRoleDao {

}
