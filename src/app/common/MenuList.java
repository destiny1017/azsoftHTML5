
/*****************************************************************************************
	1. program ID	: MenuList.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: BBS DAO
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import org.w3c.dom.Document;
import app.common.CreateXml;




/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class MenuList{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	
    public Object[] SelectMenuList(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml      ecmmtb      = new CreateXml();
		Object[] returnObjectArray = null;
		ArrayList<Document> list = new ArrayList<Document>();
		ConnectionContext connectionContext = new ConnectionResource();	
		
		
		try {			
			conn = connectionContext.getConnection();

            strQuery.append("SELECT DISTINCT B.CM_MENUCD   CM_MENUCD");
            strQuery.append(" , B.CM_MANAME   CM_MANAME");
            strQuery.append(" , B.CM_BEFMENU  CM_BEFMENU");
            strQuery.append(" , B.CM_FILENAME CM_FILENAME");
            strQuery.append(" , B.CM_ORDER    CM_ORDER");
            strQuery.append(" FROM CMM0080 B , CMM0090 A"); 
            strQuery.append(" WHERE A.CM_RGTCD IN(SELECT CM_RGTCD"); 
            strQuery.append(" FROM CMM0043 ");
            strQuery.append(" WHERE CM_USERID= ?)");   
            strQuery.append(" AND A.CM_MENUCD  = B.CM_MENUCD");
            strQuery.append(" AND B.CM_BEFMENU <> 999");
            strQuery.append(" ORDER BY  B.CM_BEFMENU, B.CM_ORDER, B.CM_MENUCD, B.CM_MANAME, B.CM_FILENAME");   	            
            
            
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);		
			
            rs = pstmt.executeQuery();	            
            
            ecmmtb.init_Xml("ID","cm_menucd","cm_maname","cm_befmenu","cm_filename","cm_order");
            
			while (rs.next()){
				ecmmtb.addXML(rs.getString("cm_menucd"),rs.getString("cm_menucd"),
						rs.getString("cm_maname"),rs.getString("cm_befmenu"),
						rs.getString("cm_filename"),rs.getString("cm_order"),
						rs.getString("cm_befmenu"));
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			list.add(ecmmtb.getDocument());
			
			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());
			
			return returnObjectArray;			
						
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuList.SelectMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MenuList.SelectMenuList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuList.SelectMenuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MenuList.SelectMenuList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuList.SelectMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of SelectMenuList() method statement	
/*
    public Object[] secuMenuList(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		HashMap<String, String>			    rst	   = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		
		// 메뉴 순번 및 메뉴 이름 추가 변수 선언
		String tmpMenu = "";
		
		try {			
			conn = connectionContext.getConnection();

            strQuery.append("SELECT C.CM_ORDER    cm_order      \n");
            strQuery.append("      ,C.CM_MENUCD   cm_menucd     \n");
            strQuery.append("      ,C.CM_MANAME   cm_maname     \n");
            strQuery.append("      ,B.CM_ORDER    cm_order_sub  \n");
            strQuery.append("      ,B.CM_MENUCD   cm_menucd_sub \n");
            strQuery.append("      ,B.CM_MANAME   cm_maname_sub \n");
            strQuery.append("      ,B.CM_FILENAME cm_filename   \n");
            strQuery.append("      ,B.CM_REQCD    cm_reqcd      \n");
            strQuery.append("  FROM CMM0080 C,CMM0080 B ,CMM0090 A\n");
            strQuery.append(" WHERE A.CM_RGTCD IN(SELECT CM_RGTCD FROM CMM0043 \n");
            strQuery.append("                      WHERE CM_USERID= ?)         \n"); 
            strQuery.append("   AND A.CM_MENUCD=B.CM_MENUCD     \n");
            strQuery.append("   AND B.CM_BEFMENU<>0             \n");
            strQuery.append("   AND B.CM_MENUCD<>999            \n");
            strQuery.append("   AND B.CM_BEFMENU=C.CM_MENUCD    \n");
            strQuery.append(" GROUP BY C.CM_ORDER,C.CM_MENUCD,C.CM_MANAME,B.CM_ORDER,B.CM_MENUCD,B.CM_MANAME,B.CM_FILENAME,B.CM_REQCD  \n"); 	
            strQuery.append(" ORDER BY C.CM_ORDER,B.CM_ORDER    \n");            
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);			
            rs = pstmt.executeQuery();    
			while (rs.next()){
				if(!rs.getString("cm_maname").equals(tmpMenu)) {
					rst = new HashMap<String, String>();
					rst.put("id", rs.getString("cm_order"));
					rst.put("pid", "0");
					rst.put("order", rs.getString("cm_order"));
					rst.put("text", rs.getString("cm_maname"));	
					tmpMenu = rs.getString("cm_maname");
					rtList.add(rst);
					rst = null;
				}
				
				rst = new HashMap<String, String>();
				rst.put("id", rs.getString("cm_order") + "_" + rs.getString("cm_order_sub"));
				rst.put("pid", rs.getString("cm_order"));
				rst.put("order", rs.getString("cm_order_sub"));
				rst.put("text", rs.getString("cm_maname_sub"));
				rst.put("link", rs.getString("cm_filename"));	
				rtList.add(rst);
				rst = null;				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

        	rs = null;
            pstmt = null;
            conn = null;
            
            rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuList.secuMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MenuList.secuMenuList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuList.secuMenuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MenuList.secuMenuList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuList.secuMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of secuMenuList() method statement
	*/ 
	
	
    public Object[] secuMenuList(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		HashMap<String, String>			    rst	   = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		
		// 메뉴 순번 및 메뉴 이름 추가 변수 선언
		String tmpMenu = "";
		
		try {			
			conn = connectionContext.getConnection();

			strQuery.append("SELECT            																																										\n");
			strQuery.append("	A.ID,                                                                                               \n");
			strQuery.append("	A.PID,                                                                                              \n");
			strQuery.append("	A.MENUORDER,                                                                                        \n");
			strQuery.append("	A.TEXT,                                                                                             \n");
			strQuery.append("	DECODE(A.PID,0,'',A.LINK) AS LINK,                                                                	\n");
			strQuery.append("	A.CM_MENUCD,                                                                                        \n");
			strQuery.append("	A.CM_REQCD,                                                                                         \n");
			strQuery.append("	DECODE(A.PID,0,'','ecamsSubframe') AS TARGETNAME                                                    \n");
			strQuery.append("FROM (SELECT CASE WHEN A.CM_BEFMENU = 0 THEN TO_CHAR(A.CM_MENUCD)                                    	\n");
			strQuery.append("			  ELSE A.CM_MENUCD || '_' || A.CM_BEFMENU                                                   \n");
			strQuery.append("			  END  ID,                                                                                  \n");
			strQuery.append("		   A.CM_BEFMENU AS PID,                                                                         \n");
			strQuery.append("		   A.CM_ORDER AS MENUORDER,                                                                     \n");
			strQuery.append("		   A.CM_MANAME AS TEXT,                                                                         \n");
			strQuery.append("		   A.CM_FILENAME AS LINK,                                                                       \n");
			strQuery.append("		   A.CM_MENUCD,                                                                                 \n");
			strQuery.append("		   A.CM_REQCD                                                                                   \n");
			strQuery.append("	  FROM CMM0081 A, CMM0090 B                                                                         \n");
			strQuery.append("	 WHERE B.CM_RGTCD IN(SELECT CM_RGTCD FROM CMM0043                                                   \n");
			strQuery.append("	                      WHERE CM_USERID= ?)                                                    		\n");
			strQuery.append("	  AND A.CM_MENUCD = B.CM_MENUCD                                                                     \n");
			strQuery.append("	  GROUP BY A.CM_MENUCD,A.CM_BEFMENU,A.CM_ORDER,A.CM_MANAME,A.CM_FILENAME,A.CM_REQCD) A              \n");
			strQuery.append("  START WITH A.PID = 0                                                                               	\n");
			strQuery.append("CONNECT BY PRIOR A.CM_MENUCD = A.PID                                                                	\n");
			strQuery.append("ORDER SIBLINGS BY A.MENUORDER                                                                        	\n");  
			
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);			
            rs = pstmt.executeQuery();    
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("id", 		rs.getString("ID"));
				rst.put("pid", 		rs.getString("PID"));
				rst.put("order", 	rs.getString("MENUORDER"));
				rst.put("text", 	rs.getString("TEXT"));
				rst.put("link", 	rs.getString("LINK"));	
				rst.put("reqcd", 	rs.getString("CM_REQCD"));	
				rst.put("targetname",rs.getString("TARGETNAME"));	
				rtList.add(rst);
				rst = null;				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

        	rs = null;
            pstmt = null;
            conn = null;
            
            rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuList.secuMenuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MenuList.secuMenuList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuList.secuMenuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MenuList.secuMenuList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuList.secuMenuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of secuMenuList() method statement
    
   public Object[] getLoginInfo(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml      ecmmtb      = new CreateXml();
		ArrayList<Document> list = new ArrayList<Document>();
		ConnectionContext connectionContext = new ConnectionResource();	
		int menuCnt = 0;
		
		try {			
			conn = connectionContext.getConnection();

            strQuery.append("SELECT CM_USERNAME                 \n");
            strQuery.append("      ,TO_CHAR(SYSDATE,'YYYY/MM/DD HH24:MI') NOWDATE \n");
            strQuery.append("  FROM CMM0040                     \n");
            strQuery.append(" WHERE CM_USERID=?                 \n");  
            strQuery.append("   AND CM_ACTIVE='1'               \n");          
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);			
            rs = pstmt.executeQuery();    
            ecmmtb.init_Xml("ID","cm_userid","label","gbncd","linkyn");
			if (rs.next()){
				ecmmtb.addXML(Integer.toString(++menuCnt),
						user_id,
				//		rs.getString("NOWDATE")+" "+rs.getString("CM_USERNAME")+"님 로그인",
						rs.getString("CM_USERNAME")+"님 로그인",
						"INFO",
						"N",
						user_id);
			}//end of while-loop statement
			rs.close();
			pstmt.close();			
			conn.close();

        	rs = null;
            pstmt = null;
            conn = null;
            
			ecmmtb.addXML(Integer.toString(++menuCnt),
					user_id,
					"로그아웃",
					"OFF",
					"Y",
					user_id);
			list.add(ecmmtb.getDocument());
			

			ecmmtb.addXML(Integer.toString(++menuCnt),
					user_id,
					"HOME",
					"uModules/eCAMSProc.swf",
					"L",
					user_id);
			list.add(ecmmtb.getDocument());
			ecmmtb = null;
			return list.toArray();		
						
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MenuList.getLoginInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## MenuList.getLoginInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MenuList.getLoginInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## MenuList.getLoginInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (list != null)	list = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MenuList.getLoginInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of getLoginInfo() method statement
    public Object[] getSecuList(String Sv_UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();

			strQuery.append("SELECT c.cm_maname,c.cm_filename,c.cm_reqcd \n");
			strQuery.append("  FROM cmm0080 c,cmm0090 b,CMM0043 a \n");
			strQuery.append(" where a.cm_userid= ? 		          \n");  
			strQuery.append("   and a.cm_rgtcd=b.cm_rgtcd         \n");  	
			strQuery.append("   and b.cm_menucd=c.cm_menucd       \n");  
			strQuery.append(" group by c.cm_maname,c.cm_filename,c.cm_reqcd \n");  		
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, Sv_UserID);
			rs = pstmt.executeQuery();			
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_maname", rs.getString("cm_maname"));
				rst.put("cm_filename", rs.getString("cm_filename"));
				rst.put("cm_reqcd", rs.getString("cm_reqcd"));
				rtList.add(rst);
				rst = null;
				
			}//end of while-loop statement			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtList.toArray();		
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getSecuList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getSecuList() Exception END ##");				
			throw exception;
		}finally{
			if (rtList != null) rtList = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getSecuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSecuList() method statement
}//end of MenuList class statement
