
/******************************************************************************
   프로젝트명    : 신한 형상관리 시스템
   서브시스템명  : properties files 관련 파일위치를 지정하여 읽어옴
   파일명       : ConfigFactory.java      
   수정내역
   수정일         담당자       수정내용
-------------------------------------------------------------------------------
  2006. 08. 08.  TEOK.KANG     최초생성
******************************************************************************/

package com.ecams.common.base;

/** 
* Created on 2006. 01. 20. 
* 
* To change the template for this generated file go to 
* Window - Preferences - Java - Code Generation - Code and Comments 
*/ 

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties; 

import app.common.SystemPath;

/**  
* @author kangteok 
* 
* To change the template for this generated type comment go to 
* Window - Preferences - Java - Code Generation - Code and Comments 
*/ 
public class ConfigFactory {  

        public static String getProperties(String prop_key) { 
        	String rtn_prop = null;
	        Properties props = new Properties(); 
	        InputStream fip = null;
	        ClassLoader cl;
            
            try{
            	cl = Thread.currentThread().getContextClassLoader();
                if( cl == null ){
                    cl = ClassLoader.getSystemClassLoader();
                }
                
                fip = cl.getResourceAsStream("DBInfo.properties");
	        	
                props.load(fip);
	        	fip.close();
	        	cl = null;
	        	fip = null;
		        
	        	rtn_prop =  props.getProperty(prop_key);
	        	props = null;
	        	
	        }catch(IOException e){
	        	e.printStackTrace();
	        	return null;
	        }catch(Exception e){
	        	e.printStackTrace();
	        	return null;
	        }        
	        
	        return rtn_prop;
        
        }//end of getProperties method()
        
        
        public static String getPluginProperties(String prop_key) { 
        	String rtn_prop = null;
	        Properties props = new Properties();
            
            try{
            	SystemPath systemPath = new SystemPath();
            	String basepath = systemPath.getTmpDir("P1");
            	String filepath = basepath+"/conf/jdbc.properties";
//            	String filepath = "C:\\jdbc.properties";
                
            	
                FileInputStream fip = new FileInputStream(filepath);
                props.load(new BufferedInputStream(fip));
                
                fip.close();
                fip = null;
                
                rtn_prop = props.getProperty(prop_key);
                props = null;
	        	
	        }catch(IOException e){
	        	e.printStackTrace();
	        	return null;
	        }catch(Exception e){
	        	e.printStackTrace();
	        	return null;
	        }        
	        
	        return rtn_prop;
        
        }
} //end of ConfigFactory class
