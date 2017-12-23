package com.itheima.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;


public class FilterDemo implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	//作用
	//做用户名，密码回显的操作
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		String servletPath = req.getServletPath();
		
		if(servletPath.equals("/index.jsp")){
			String name = "";
			String password="";
			Cookie[] cookies = req.getCookies();
			if(cookies!=null && cookies.length>0){
				for(Cookie cookie : cookies){
					if(cookie.getName()!=null && cookie.getName().equals("name")
							&& StringUtils.isNotBlank(cookie.getValue())){
						name = cookie.getValue();
						/**
						 * 如果name出现中文，对中文进行解码
						 */
						try {
							name = URLDecoder.decode(name, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

					}
					if(cookie.getName()!=null && cookie.getName().equals("password")
							&& StringUtils.isNotBlank(cookie.getValue())){
						password=cookie.getValue();
					}
				
				}
			}
		
			request.setAttribute("name", name);
			request.setAttribute("password", password);
			request.setAttribute("checked", "checked");
		}
		chain.doFilter(request, response);
	
//		HttpSession session = req.getSession();
//		if(session!=null){
//			Set<String> urlSet = (Set<String>) session.getAttribute("urlSet");
//			if(urlSet!=null){
//				if(urlSet.contains(".."+servletPath)){
//					chain.doFilter(request, response);
//				}else{
//					request.getRequestDispatcher("/error.jsp").forward(request, response);
//				}
//			}else{
//				chain.doFilter(request, response);
//			}
//		}else{
//			chain.doFilter(request, response);
//		}
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
