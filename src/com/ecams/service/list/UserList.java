package com.ecams.service.list;

import java.util.*;

public class UserList{
	
private static Vector userVector = new Vector();      //User_id 저장
private static Vector idVector = new Vector();        //Session id 저장

//새로 접속한 User와 Session id를 저장한다.
public static void addUser(String userName, String id){   
	String temp;
             //전에 접속 되어있는지 확인
	for(int i=0;i<userVector.size();i++){
		temp = (String)userVector.elementAt(i);
		if(userName.equals(temp))
                {      //전에 접속되어있으면 end
				return;
		}
	}
	userVector.addElement(userName);// User id를 저장한다.
	idVector.addElement(id);       // Session id를 저장한다.
}
//User id와 Session id를 삭제 시킨다.
public static void removeUser(String user){ 
	String temp;
	for(int i=0;i<userVector.size();i++){
		temp = (String)userVector.elementAt(i);
		if(user.equals(temp)){       //User id로 값을 비교
			userVector.removeElementAt(i); //User id 삭제
			idVector.removeElementAt(i); //Session id 삭제
			return;
		}
	}
}

//접속되있는(Vector)에 접속되있는 모든 User id를 return 한다.
public Vector getUser(){            
	return userVector;
}
	
//User id로 Session id값을 얻는다.
public static String getid(String name){          
	String temp;
	for(int i=0;i<userVector.size();i++) //User id로 값을 비교. 
	{
		temp = (String)userVector.elementAt(i);
		if(name.equals(temp))//같은 값이 있으면 Session id return
		{
			return (String)idVector.get(i);
		}
	}
	return ""; //같은 값이 없으면 " " return
}
//Session id 값을 교체한다.
public static void setid(String name,String id)   
{
	String temp;
	for(int i=0;i<userVector.size();i++) //User id값을 비교한다.
	{
		temp = (String)userVector.elementAt(i);
		if(name.equals(temp))    //User id 값이 같으면
		{
			idVector.set(i, id);  //Session 값을 교체한다.
		}
	}
}

public static int logck(String name)  //로그인 되있는 지를 check 한다.
{
	String temp;
	for(int i=0;i<userVector.size();i++) //User id 값을 비교한다.
	{
		temp = (String)userVector.elementAt(i);
		if(name.equals(temp))//User id 값이 같으면(로그인 되있으면) 
		{
			return 3;    // 3을 return
		}
	}
	return 1;      //User id 값이 같은게 없으면(로그인 안되있으면)
	}
}
