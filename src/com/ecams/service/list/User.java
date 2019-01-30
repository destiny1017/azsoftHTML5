package com.ecams.service.list;

import javax.servlet.http.*;

public class User implements HttpSessionBindingListener{
	private String userName;
	private String userId;

//User id 와 Session id 값을 Vector에 추가 시킨다.
public void setAddUser(String name, String id)    
{
	userName=name;
	userId=id;
	UserList.addUser(userName,userId);  // 추가
}

//User id 값을 받아서 해당 User id와 Session id값을 삭제
public void setDelUser(String userName)         
{
	this.userName = userName;                 
	UserList.removeUser(userName);       //삭제
}

//User id값으로 해당 Session id 값을 return
public String getid(String name)             
{
	return UserList.getid(name);                    
}

//User id값으로 해당 Session id 값을 교체
public void setid(String name, String id)   
{
	UserList.setid(name,id);
}
public int logck(String name)      //User id값으로 login check
{
	return UserList.logck(name);
}

//객체가 생성됬을때 자동적으로 실행
public void valueBound(HttpSessionBindingEvent event)        
{
	//UserList.addUser(userName);
}

//객체가 사라졌을때 실행.
public void valueUnbound(HttpSessionBindingEvent event)     
{
	UserList.removeUser(userName);
}

}
