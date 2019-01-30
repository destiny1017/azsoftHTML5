
/******************************************************************************
   프로젝트명    : 형상관리 시스템
   서브시스템명  : String 관련 UTIL CLASS
   파일명       : StringHelper.java      
   수정내역
   수정일         담당자       수정내용
-------------------------------------------------------------------------------
  2006. 08. 08.  TEOK.KANG     최초생성
******************************************************************************/


package com.ecams.common.base;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StringHelper{
	
	/**
	 * @param param
	 * @param param2
	 * @return
	 */
	public static String nvl(String param, String param2){
	        return param != null ? param : param2;
	}//end of nvl method() statement
	
	/**
	 * @param param
	 * @param param2
	 * @return
	 */
	public static String evl(String param, String param2){
        return param != null && !param.trim().equals("") ? param : param2;
    }//end of evl method() statement
	
	/**
	 * @param s
	 * @return
	 */
	public static String toAsc(String s)
	{
		if(s == null)
			return null;
		try
		{
			return new String(s.getBytes("KSC5601"), "8859_1");
		}
		catch(Exception exception)
		{
			return s;
		}
	}//end of toAsc method() statement
	
	/**
	 * @param s
	 * @return
	 */
	public static String toKsc(String s)
	{
		if(s == null)
			return null;
		try
		{
			return new String(s.getBytes("8859_1"), "KSC5601");
		}
		catch(Exception exception)
		{
			return s;
		}
	}//end of toKsc method() statement
	
	/**
	 * 문자열의 공백을 제거하고 반환한다.
	 * @param s
	 * @return
	 */
	public static String deleteWhiteSpace(String s){
		if (s == null)
			return null;
		int sz                 = s.length();
		StringBuffer strbuffer = new StringBuffer(sz); 
		
		for (int i = 0; i < sz; i++){
			if (!Character.isWhitespace(s.charAt(i))){
				strbuffer.append(s.charAt(i));
			}
		}//end of for-loop statement
		return strbuffer.toString();
	}//end of deleteWhiteSpace() method statement
	
}//end of StringHelper class statement