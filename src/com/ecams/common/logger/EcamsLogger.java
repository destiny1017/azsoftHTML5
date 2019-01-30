/******************************************************************************
   프로젝트명    : 형상관리 시스템
   서브시스템명  : Log4j를 변형
   파일명       : EcamsLogger.java
   수정내역
   수정일         담당자       수정내용
-------------------------------------------------------------------------------
  2006. 08. 08.  TEOK.KANG     최초생성
******************************************************************************/
package com.ecams.common.logger;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EcamsLogger {

	//static EcamsLogger kl_instance = null;
	static Logger           l_instance  = null;

	private EcamsLogger (){
	}//end of EcamsLogger() construct statement

	/** 
	 * Logger 객체를 생성 return
	 * @return Logger
	 */
	public static Logger getLoggerInstance(){
		ClassLoader cl;

		if(l_instance == null){
			synchronized(Logger.class) {
				if(l_instance == null){
	            	cl = Thread.currentThread().getContextClassLoader();
	                if( cl == null ){
	                    cl = ClassLoader.getSystemClassLoader();
	                }
	                PropertyConfigurator.configure(cl.getResource("Log4J.properties"));

					l_instance = Logger.getRootLogger();
				}
			}
		}
		return l_instance;
	}//end of static getLoggerInstance() method statement

	/**
	 * Logger 객체를 재생성 properties 변경을 할때 server restart를 안하고 사용할수 있게 하기위한 메쏘드
	 * @return Logger
	 */
	public Logger resetLoggerInstance(){
		ClassLoader cl;
        URL prop_url;

		synchronized(Logger.class) {
        	cl = Thread.currentThread().getContextClassLoader();
            if( cl == null ){
                cl = ClassLoader.getSystemClassLoader();
            }
            PropertyConfigurator.configure(cl.getResource("Log4J.properties"));

			l_instance = Logger.getRootLogger();
		}
		return l_instance;
	}//end of static resetLoggerInstance() method statement

}//end of class EcamsLogger statement
