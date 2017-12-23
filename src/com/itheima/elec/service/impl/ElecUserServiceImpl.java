package com.itheima.elec.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.itheima.elec.dao.IElecSystemDDLDao;
import com.itheima.elec.dao.IElecUserDao;
import com.itheima.elec.dao.IElecUserFileDao;
import com.itheima.elec.domain.ElecRole;
import com.itheima.elec.domain.ElecUser;
import com.itheima.elec.domain.ElecUserFile;
import com.itheima.elec.service.IElecUserService;
import com.itheima.elec.utils.FileUtils;
import com.itheima.elec.utils.MD5keyBean;


@Service(IElecUserService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecUserServiceImpl implements IElecUserService {

	/**用户表Dao*/
	@Resource(name=IElecUserDao.SERVICE_NAME)
	IElecUserDao elecUserDao;
	
	/**用户附件表Dao*/
	@Resource(name=IElecUserFileDao.SERVICE_NAME)
	IElecUserFileDao elecUserFileDao;
	
	/**数据字典表Dao*/
	@Resource(name=IElecSystemDDLDao.SERVICE_NAME)
	IElecSystemDDLDao elecSystemDDLDao;
	
	/**  
	* @Name: findUserListByCondition
	* @Description: 组织查询条件，查询用户列表
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-1（创建日期）
	* @Parameters: ElecUser:VO对象
	* @Return: List<ElecUser>：用户集合
	*/
	public List<ElecUser> findUserListByCondition(ElecUser elecUser) {
		//组织查询条件
		String condition = "";
		List<Object> paramsList = new ArrayList<Object>();
		//用户名称
		String userName = elecUser.getUserName();
		if(StringUtils.isNotBlank(userName)){
			condition += " and o.userName like ?";
			paramsList.add("%"+userName+"%");
		}
		//所属单位
		String jctID = elecUser.getJctID();
		if(StringUtils.isNotBlank(jctID)){
			condition += " and o.jctID = ?";
			paramsList.add(jctID);
		}
		//入职开始时间
		Date onDutyDateBegin = elecUser.getOnDutyDateBegin();
		if(onDutyDateBegin!=null){
			condition += " and o.onDutyDate >= ?";
			paramsList.add(onDutyDateBegin);
		}
		//入职结束时间
		Date onDutyDateEnd = elecUser.getOnDutyDateEnd();
		if(onDutyDateEnd!=null){
			condition += " and o.onDutyDate <= ?";
			paramsList.add(onDutyDateEnd);
		}
		Object [] params = paramsList.toArray();
		//排序（按照入职时间的升序排列）
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.onDutyDate", "asc");
		List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, orderby);
		/**
		 * 3：数据字典的转换
		 	* 使用数据类型和数据项的编号，查询数据字典，获取数据项的值
		 */
		this.convertSystemDDL(list);
		return list;
	}

	/**使用数据类型和数据项的编号，查询数据字典，获取数据项的值*/
	private void convertSystemDDL(List<ElecUser> list) {
		if(list!=null && list.size()>0){
			for(ElecUser user:list){
				//性别
				String sexID = elecSystemDDLDao.findDdlNameByKeywordAndDdlCode("性别",user.getSexID());
				user.setSexID(sexID);
				//职位
				String postID = elecSystemDDLDao.findDdlNameByKeywordAndDdlCode("职位",user.getPostID());
				user.setPostID(postID);
			}
		}
	}
	
	/**  
	* @Name: checkUser
	* @Description: 验证登录名是否存在
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-2（创建日期）
	* @Parameters: String：登录名
	* @Return: String
	* 判断登录名是否出现重复，返回一个标识message属性
		  * message=1：表示登录名为空，不可以保存
		  * message=2：表示登录名在数据库中已经存在，不可以保存
		  * message=3：表示登录名在数据库中不存在，可以保存
	*/
	public String checkUser(String logonName) {
		String message = "";
		if(StringUtils.isNotBlank(logonName)){
			//以登录名作为查询条件，查询数据库
			String condition = " and o.logonName = ?";
			Object [] params = {logonName};
			List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage(condition, params, null);
			//表示数据库存在登录名的记录
			if(list!=null && list.size()>0){
				message = "2";
			}
			//表示数据库不存在登录名的记录，可以保存
			else{
				message = "3";
			}
		}
		//为空
		else{
			message = "1";
		}
		return message;
	}

	/**  
	* @Name: saveUser
	* @Description: 保存用户的信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-2（创建日期）
	* @Parameters: ElecUser：VO对象
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveUser(ElecUser elecUser) {
		// 1：遍历多个附件，组织附件的PO对象，完成文件上传，保存用户的附件（多条数据），建立附件表和用户表的关联关系
		this.saveUserFiles(elecUser);
		// 添加md5的密码加密
		this.md5password(elecUser);
		//获取页面传递的userID
		String userID = elecUser.getUserID();
		//更新（update）
		if(StringUtils.isNotBlank(userID)){
			//组织PO对象，执行更新（1条）
			elecUserDao.update(elecUser);
		}
		//新增（save）
		else{
			// 2：组织PO对象，保存用户（1条数据）
			elecUserDao.save(elecUser);
		}
		
	}

	/**添加md5的密码加密*/
	private void md5password(ElecUser elecUser) {
		//获取加密前的密码
		String logonPwd = elecUser.getLogonPwd();
		//加密后的密码
		String md5password = "";
		//如果没有填写密码，设置初始密码为123
		if(StringUtils.isBlank(logonPwd)){
			logonPwd = "123";
		}
		//获取修改用户之前的密码
		String password = elecUser.getPassword();
		//编辑的时候，没有修改密码的时候，是不需要加密的
		if(password!=null && password.equals(logonPwd)){
			md5password = logonPwd;
		}
		//新增的时候，或者是编辑的时候修改密码的时候，需要加密的
		else{
			//使用md5加密的时候
			MD5keyBean md5keyBean = new MD5keyBean();
			md5password = md5keyBean.getkeyBeanofStr(logonPwd);
		}
		//放置到ElecUser对象中
		elecUser.setLogonPwd(md5password);
	}

	//遍历多个附件，组织附件的PO对象，完成文件上传，保存用户的附件（多条数据），建立附件表和用户表的关联关系
	private void saveUserFiles(ElecUser elecUser) {
		//上传时间
		Date progressTime = new Date();
		//获取上传的文件
		File [] uploads = elecUser.getUploads();
		//获取上传的文件名
		String [] fileNames = elecUser.getUploadsFileName();
		//获取上传的文件类型
		String [] contentTypes = elecUser.getUploadsContentType();
		//遍历
		if(uploads!=null && uploads.length>0){
			for(int i=0;i<uploads.length;i++){
				//组织附件的PO对象
				ElecUserFile elecUserFile = new ElecUserFile();
				elecUserFile.setFileName(fileNames[i]);//文件件名
				elecUserFile.setProgressTime(progressTime);//上传时间
				/**将文件上传，同时返回路径path*/
				String fileURL = FileUtils.fileUploadReturnPath(uploads[i],fileNames[i],"用户管理");
				elecUserFile.setFileURL(fileURL);//上传路径（保存，应用与下载）
				elecUserFile.setElecUser(elecUser);//重要：建立关联关系，与用户建立关系，如果不建立，否则外键为null
				elecUserFileDao.save(elecUserFile);
			}
		}
		
	}
	
	/**  
	* @Name: findUserByID
	* @Description: 使用用户ID，查询用户对象
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-2（创建日期）
	* @Parameters: String：用户ID
	* @Return: ElecUser：用户信息
	*/
	public ElecUser findUserByID(String userID) {
		return elecUserDao.findObjectByID(userID);
	}

	/**  
	* @Name: findUserFileByID
	* @Description: 使用用户附件ID，查询用户附件对象
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-2（创建日期）
	* @Parameters: String：用户附件ID
	* @Return: ElecUserFile：用户附件信息
	*/
	public ElecUserFile findUserFileByID(String fileID) {
		return elecUserFileDao.findObjectByID(fileID);
	}
	
	/**  
	* @Name: deleteUserByID
	* @Description: 删除用户信息
	*  	 1：删除该用户对应的文件
		 2：删除该用户对应的用户附件表数据
		 3：删除用户表的信息
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-2（创建日期）
	* @Parameters: ElecUser:vo对象
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void deleteUserByID(ElecUser elecUser) {
		//获取获取ID，String userID，如果是多个值，struts2默认采用", "的形式
		String userID = elecUser.getUserID();
		String [] userIDs = userID.split(", ");
		if(userIDs!=null && userIDs.length>0){
			//获取每个用户ID
			for(String uid:userIDs){
				//使用用户ID，查询用户对象，获取当前用户具有的附件
				ElecUser user = elecUserDao.findObjectByID(uid);
				Set<ElecUserFile> elecUserFiles = user.getElecUserFiles();
				//遍历用户的附件
				if(elecUserFiles!=null && elecUserFiles.size()>0){
					for(ElecUserFile elecUserFile:elecUserFiles){
						//1：删除该用户对应的文件
						//获取路径
						String path = ServletActionContext.getServletContext().getRealPath("")+elecUserFile.getFileURL();
						File file = new File(path);
						if(file.exists()){
							//删除文件
							file.delete();
						}
						//删除每个用户的附件
						//2：删除该用户对应的用户附件表数据(级联删除)
						//elecUserFileDao.deleteObjectByIds(elecUserFile.getFileID());
						//<set name="elecUserFiles" table="Elec_User_File" inverse="true" order-by="progressTime desc" cascade="delete">
					}
				}
			}
		}
		//删除用户角色信息
		for(String id:userIDs){
			ElecUser elecUserT = elecUserDao.findObjectByID(id);
			Set<ElecRole> elecRoleSet = elecUserT.getElecRoles();
			//得到该用户的所有角色信息
			//因为角色维护角色用户表，所以通过角色来删除该用户，来达到删除用户角色信息的作用
			for(ElecRole elecRole:elecRoleSet){
				elecRole.getElecUsers().remove(elecUserT);
			}
		}
		//3：删除用户表的信息
		elecUserDao.deleteObjectByIds(userIDs);
	}
	/**
	 * select * from ElecUser o where 1=1
	 *  and o.logonName=?
	 *  and o.logonPwd=?
	 */
	public ElecUser findUserByNameAndPassword(String name, String password) {
		
		String condition = " and o.logonName=?"
				+ " and o.logonPwd=?";
		Object[] params = {name,password};
		
		List<ElecUser> elecUserList = elecUserDao.findCollectionByConditionNoPage(condition, params, null);
		if(elecUserList!=null && elecUserList.size()>0){
			return elecUserList.get(0);
		}else{
			return null;
		}
	}

}
