package com.itheima.elec.dao.impl;

import org.springframework.stereotype.Repository;

import com.itheima.elec.dao.IElecUserFileDao;
import com.itheima.elec.domain.ElecUserFile;


@Repository(IElecUserFileDao.SERVICE_NAME)
public class ElecUserFileDaoImpl  extends CommonDaoImpl<ElecUserFile> implements IElecUserFileDao {

}
