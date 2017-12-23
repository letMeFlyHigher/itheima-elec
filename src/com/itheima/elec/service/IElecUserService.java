package com.itheima.elec.service;

import java.util.List;

import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.domain.ElecUserFile;



public interface IElecUserService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecUserServiceImpl";

	List<ElecUser> findUserListByCondition(ElecUser elecUser);

	String checkUser(String logonName);

	void saveUser(ElecUser elecUser);

	ElecUser findUserByID(String userID);

	ElecUserFile findUserFileByID(String fileID);

	void deleteUserByID(ElecUser elecUser);

	ElecUser findUserByNameAndPassword(String name, String password);

}
