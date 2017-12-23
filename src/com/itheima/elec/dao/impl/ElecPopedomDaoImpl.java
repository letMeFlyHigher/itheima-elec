package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecPopedomDao;
import com.itheima.elec.domain.ElecPopedom;


@Repository(IElecPopedomDao.SERVICE_NAME)
public class ElecPopedomDaoImpl  extends CommonDaoImpl<ElecPopedom> implements IElecPopedomDao {

}
