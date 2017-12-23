package com.itheima.elec.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogonUtils {
	
	//验证码验证
	public static boolean checkedNumberKey(HttpServletRequest request){
		 String CHECK_NUMBER_KEY = (String) request.getSession().getValue("CHECK_NUMBER_KEY");
		 String checkNumber = (String) request.getAttribute("checkNumber");
		 if(CHECK_NUMBER_KEY!=null && CHECK_NUMBER_KEY.equals(checkNumber)){
			 return true;
		 }else{
			 return false;
		 }
		
		
	}
	
	//记住我功能
	public static void remeberme(String name, String password,
			HttpServletRequest request, HttpServletResponse response){
			String remeberMe = (String) request.getAttribute("remeberMe");
			//如果勾选记住用户名
			if(remeberMe!=null && remeberMe.equals("yes")){
				//保存用户名
				Cookie nameCookie = new Cookie("name",name);
				//保存密码
				Cookie pwdCookie = new Cookie("password",password);
				
				 
				nameCookie.setPath(request.getContextPath()+"/");
				pwdCookie.setPath(request.getContextPath()+"/");
			
				
				nameCookie.setMaxAge(7*24*60*60);
				pwdCookie.setMaxAge(7*24*60*60);
			
				
				response.addCookie(nameCookie);
				response.addCookie(pwdCookie);
			
			}
			//如果没有勾选记住用户名
			else{
				Cookie[] cookies = request.getCookies();
				for(Cookie cookie:cookies){
					if(cookie.getName()!=null && cookie.getName().equals("name")
							&& cookie.getValue()!=null &&cookie.getValue().equals(name)){
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
					if(cookie.getName()!=null && cookie.getName().equals("password")
							&& cookie.getValue()!=null &&cookie.getValue().equals(password)){
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
				
				}
			}
			
			
			
		
	}
}
