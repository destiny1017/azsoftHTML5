/*****************************************************************************************
	1. program ID	: UserInfo.java
	2. create date	: 2008.04. 08
	3. auth		    : teok.kang
	4. update date	: 2009.02.20
	5. auth		    : no name
	6. description	: UserInfo 
*****************************************************************************************/
package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UserInfo{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 
	 * @param  UserID
	 * @return ArrayList<HashMap<String,String>>
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getUserInfo(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.append("select a.cm_username,a.cm_manid,a.cm_position,a.cm_project,a.cm_duty, \n");
			strQuery.append("		a.cm_admin,c.cm_deptname teamname,a.cm_posname caption  \n");
			strQuery.append("  from cmm0100 c,cmm0040 a 				\n");
			strQuery.append(" where a.cm_userid= ? 				 				\n");  //Sv_UserID
			strQuery.append("   and a.cm_project=c.cm_deptcd 					\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserID);
            rs = pstmt.executeQuery();
            rtList.clear();
			if (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cm_admin",rs.getString("cm_admin"));
				rst.put("cm_manid",rs.getString("cm_manid"));
				rst.put("cm_project",rs.getString("cm_project"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("teamcd",rs.getString("cm_project"));
				//rst.put("sv_pmo",getPMOInfo(UserID));
				rst.put("caption",rs.getString("caption"));
				rst.put("teamname",rs.getString("teamname"));
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
			ecamsLogger.error("## UserInfo.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getUserInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getUserInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getUserInfo() method statement	


	public String getUserInfo_sub(Connection conn,String UserID,String colName) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "";
		
		try {
			strQuery.setLength(0);
			strQuery.append("select cm_project from cmm0040     \n");
			strQuery.append(" where cm_userid= ?  				\n");  //Sv_UserID
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, UserID);
            rs = pstmt.executeQuery();
                        
			if (rs.next()){
				if (colName.equals("cm_project")) retMsg = rs.getString("cm_project");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserInfo_sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getUserInfo_sub() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserInfo_sub() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getUserInfo_sub() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getUserInfo_sub() method statement
	
	
	/**
	 * 
	 * <PRE>
	 * 1. MethodName	: getPMOInfo
	 * 2. ClassName		: UserInfo
	 * 3. Commnet		: 
	 * 4. editor		: Administrator
	 * 5. date			: 2010. 11. 30 7:43:48
	 * </PRE>
	 * 		@return String
	 * 		@param Sv_UserID
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public String getPMOInfo(String Sv_UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  ret         = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			if (isAdmin_conn(Sv_UserID, conn)) ret = "A";
			else {
				strQuery.setLength(0);
				strQuery.append("SELECT min(a.CM_RGTCD) rgtcd,b.cm_project  \n");
				strQuery.append("  FROM CMM0040 b,CMM0043 a                 \n");
				strQuery.append(" WHERE b.CM_USERID= ? 				    	\n");
				strQuery.append("   AND b.CM_USERID=a.CM_USERID         	\n");
				strQuery.append("GROUP BY b.CM_PROJECT \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, Sv_UserID);
				rs = pstmt.executeQuery();
				
				if (rs.next()){
					if (rs.getString("rgtcd") == null) ret = "X";
					else if (rs.getString("rgtcd").equals("31")) ret = "C";
					else if (rs.getString("rgtcd").equals("41")) ret = "D";
					else if (rs.getString("rgtcd").equals("51")) ret = "D";
					else if (rs.getString("rgtcd").equals("61")) ret = "P";
					else ret = "S";
					ret = ret + rs.getString("cm_project");
				}//end of while-loop statement
				
				rs.close();
				pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return ret;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getPMOInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getPMOInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getPMOInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getPMOInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getPMOInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPMOInfo() method statement
	
	public String getPMOInfo_conn(String Sv_UserID,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  req         = null;
		int			      CNT         = 0;
				
		try {
			strQuery.append("SELECT count(*) CNT FROM CMM0043	    	\n");
			strQuery.append("where cm_userid= ? 				    	\n");  //Sv_UserID
			strQuery.append("  AND cm_rgtcd in ('15','74','78','82')	\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
		//	pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, Sv_UserID);
		//	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			if (rs.next()){
				CNT = rs.getInt("CNT");
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(CNT == 0)		
				req = "0";
			else
				req = "1";
			
			return req;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getPMOInfo_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getPMOInfo_conn() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getPMOInfo_conn() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getPMOInfo_conn() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getPMOInfo_conn() method statement

	/**
	 * 
	 * <PRE>
	 * 1. MethodName	: getManagerInfo
	 * 2. ClassName		: UserInfo
	 * 3. Commnet		: 
	 * 4. editor		: Administrator
	 * 5. date			: 2010. 11. 30. 7:43:48
	 * </PRE>
	 * 		@return String
	 * 		@param Sv_UserID
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public String getRGTCDInfo(String Sv_UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  ret         = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			if (isAdmin_conn(Sv_UserID, conn)) ret = "A";
			else {
				strQuery.setLength(0);
				strQuery.append("SELECT CM_RGTCD \n");
				strQuery.append("  FROM CMM0043 \n");
				strQuery.append(" WHERE CM_USERID= ? \n");
				strQuery.append("   AND CM_RGTCD in ('31','41','51','61','81') \n");
				strQuery.append(" ORDER BY CM_RGTCD \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, Sv_UserID);
				rs = pstmt.executeQuery();
				
				while (rs.next()){
					if (rs.getString("CM_RGTCD").equals("31")) ret = ret + "C";
					if (rs.getString("CM_RGTCD").equals("41")) ret = ret + "D";
					if (rs.getString("CM_RGTCD").equals("51")) ret = ret + "D";
					if (rs.getString("CM_RGTCD").equals("61")) ret = ret + "P";
					if (rs.getString("CM_RGTCD").equals("81")) ret = ret + "M";
				}//end of while-loop statement
				
				rs.close();
				pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return ret;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getRGTCDInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getRGTCDInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getRGTCDInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getRGTCDInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getRGTCDInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRGTCDInfo() method statement
	
	public String getSecuInfo(String Sv_UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  req         = null;
		int			      CNT         = 0;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT count(*) CNT FROM CMM0043	\n");
			strQuery.append("where cm_userid= ? 				\n");
			strQuery.append("  AND cm_rgtcd in ('A1','74')  	\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, Sv_UserID);
			rs = pstmt.executeQuery();
			
			if (rs.next()){
				CNT = rs.getInt("CNT");
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			if(CNT == 0)		
				req = "0";
			else
				req = "1";
			
			return req;		
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getSecuInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getSecuInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getSecuInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSecuInfo() method statement
	public boolean getSecuInfo_conn(String Sv_UserID,String RgtCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int			      CNT         = 0;
		
		
		try {
			strQuery.append("SELECT count(*) CNT FROM CMM0043	\n");
			strQuery.append("where cm_userid= ? 				\n");
			strQuery.append("  AND instr(?,cm_rgtcd)>0       	\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, Sv_UserID);
			pstmt.setString(2, RgtCd);
			rs = pstmt.executeQuery();
			
			if (rs.next()){				
				CNT = rs.getInt("CNT");
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(CNT == 0)		
				return false;
			else
				return true;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuInfo_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getSecuInfo_conn() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getSecuInfo_conn() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getSecuInfo_conn() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getSecuInfo_conn() method statement
	
	
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
			rtList.clear();
			strQuery.append("SELECT c.cm_filename   	          \n");
			strQuery.append("  FROM cmm0080 c,cmm0090 b,CMM0043 a \n");
			strQuery.append(" where a.cm_userid= ? 		          \n");  //Sv_UserID
			strQuery.append("   and a.cm_rgtcd=b.cm_rgtcd         \n");  	
			strQuery.append("   and b.cm_menucd=c.cm_menucd       \n"); 
			strQuery.append(" union     \n");
			strQuery.append("SELECT c.cm_filename   	          \n");
			strQuery.append("  FROM cmm0080 c,cmm0090 b,CMM0043 a,cmm0040 d \n");
			strQuery.append(" where d.cm_blankdts is not null     \n");
			strQuery.append("   and d.cm_blankdte is not null     \n");
			strQuery.append("   and d.cm_blankdts<=to_char(SYSDATE,'yyyymmdd') \n");
			strQuery.append("   and d.cm_blankdte>=to_char(SYSDATE,'yyyymmdd') \n");
			strQuery.append("   and d.cm_daegyul=?                \n");
			strQuery.append("   and d.cm_userid=a.cm_userid      \n");  //Sv_UserID
			strQuery.append("   and a.cm_rgtcd=b.cm_rgtcd         \n");  	
			strQuery.append("   and b.cm_menucd=c.cm_menucd       \n");  
			strQuery.append(" group by cm_filename                \n");  		
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, Sv_UserID);
			pstmt.setString(2, Sv_UserID);
			rs = pstmt.executeQuery();			
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_filename", rs.getString("cm_filename"));
				rtList.add(rst);
				
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
	
	/**
	 * <PRE>
	 * 1. MethodName	: isAdmin
	 * 2. ClassName		: UserInfo
	 * 3. Commnet		:  CM_ADMIN = '1' ==> true
	 * 4. editor		: no name
	 * 5. date			: 2011. 1. 12.  1:11:56
	 * </PRE>
	 * 		@return Boolean
	 * 		@param UserID
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Boolean isAdmin(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  countPermit;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			if (UserID.equals("") || UserID == null || UserID == ""){
				return false;
			}
			conn = connectionContext.getConnection();
			
			strQuery.append("SELECT distinct count(*) as countPermit \n");
			strQuery.append("  FROM CMM0040 \n");
			strQuery.append(" WHERE cm_userid = ? \n");
			strQuery.append("   AND CM_ADMIN = '1' \n");
			strQuery.append("   AND CM_ACTIVE = '1' \n");
			strQuery.append("   AND CM_CLSDATE is null \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            pstmt.setString(1, UserID);	
            
            
            
            rs = pstmt.executeQuery();
            
            countPermit = 0;
			if (rs.next()){
				countPermit = rs.getInt("countPermit");				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			if (countPermit > 0)
				return true;
			else
				return false;			
	
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.isAdmin() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.isAdmin() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.isAdmin() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of isAdmin() method statement

	/**
	 * <PRE>
	 * 1. MethodName	: isAdmin_conn
	 * 2. ClassName		: UserInfo
	 * 3. Commnet		:  CM_ADMIN = '1' ==> true
	 * 4. editor		: Administrator
	 * 5. date			: 2011. 1. 12. 1:12:57
	 * </PRE>
	 * 		@return Boolean
	 * 		@param UserID
	 * 		@param conn
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Boolean isAdmin_conn(String UserID,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  countPermit;
		
		
		try {
			
			if (UserID.equals("") || UserID == null || UserID == ""){
				return false;
			}
			
			strQuery.append("SELECT distinct count(*) as countPermit \n");
			strQuery.append("FROM CMM0040 \n");
			strQuery.append("WHERE cm_userid = ? \n");
			strQuery.append("AND CM_ADMIN = '1' \n");
			strQuery.append("AND CM_ACTIVE = '1' \n");
			strQuery.append("AND CM_CLSDATE is null \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, UserID);	
            
            
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());  
            rs = pstmt.executeQuery();
            
            countPermit = 0;
			while (rs.next()){
				countPermit = rs.getInt("countPermit");				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if (countPermit > 0)
				return true;
			else
				return false;			
	
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.isAdmin_conn() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin_conn() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.isAdmin_conn() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of isAdmin_conn() method statement
	
	
	
	/**
	 * <PRE>
	 * 1. MethodName	: getUserRGTCD
	 * 2. ClassName		: UserInfo
	 * 3. Commnet		: 
	 * 4. editor		: no name
	 * 5. date			: 2011. 4. 1.  1:47:05
	 * </PRE>
	 * 		@return Boolean
	 * 		@param UserID : 
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public String getUserRGTCD(String UserID,String RGTCD,String closeYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            returnStr = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_rgtcd,c.cm_codename \n");
			strQuery.append("  from cmm0043 b,cmm0020 c \n");
			strQuery.append(" where b.cm_userid= ? \n");
			strQuery.append("   and b.cm_rgtcd=c.cm_micode \n");
			strQuery.append("   and c.cm_macode='RGTCD' \n");
			String[] rgtcd = RGTCD.split(",");
			if (rgtcd.length > 0){
				strQuery.append(" and b.cm_rgtcd in ( \n");
				if (rgtcd.length == 1)
					strQuery.append(" ? ");
				else{
					for (int i=0;i<rgtcd.length;i++){
						if (i == rgtcd.length-1)
							strQuery.append(" ? ");
						else
							strQuery.append(" ? ,");
					}
				}
				strQuery.append(" ) \n");
			}
	        if (closeYn.toUpperCase().equals("N") || closeYn.toUpperCase().equals("0")){
	        	strQuery.append("   and c.cm_closedt is null \n");
	        }
	        
            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        int paramInd = 0;
	        pstmt.setString(++paramInd, UserID);
			for (int i=0;i<rgtcd.length;i++){
				pstmt.setString(++paramInd, rgtcd[i]);
			}
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            returnStr = "";
			while (rs.next()){
				returnStr  = returnStr + rs.getString("cm_rgtcd") + ",";
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return returnStr;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserRGTCD() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getUserRGTCD() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserRGTCD() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getUserRGTCD() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.getUserRGTCD() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getUserRGTCD() method statement
	public String getUserName(Connection conn,String UserID) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "";
		
		try {
			strQuery.setLength(0);
			strQuery.append("select cm_username from cmm0040     \n");
			strQuery.append(" where cm_userid= ?  				\n");  //Sv_UserID
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1, UserID);
            rs = pstmt.executeQuery();
                        
			if (rs.next()){
				retMsg = rs.getString("cm_username");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserName() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.getUserName() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getUserName() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.getUserName() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getUserInfo_sub() method statement
	
	public Boolean isAdmin2(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  countPermit;
		boolean           adminYn     = false;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.append("SELECT distinct count(*) as countPermit \n");
			strQuery.append("  FROM CMM0043       \n");
			strQuery.append(" WHERE cm_userid = ? \n");
			strQuery.append("  AND cm_rgtcd in ('58','DB3')	\n");			
            pstmt = conn.prepareStatement(strQuery.toString());	
            pstmt.setString(1, UserID);            
            rs = pstmt.executeQuery();
            
            countPermit = 0;
			if (rs.next()){
				countPermit = rs.getInt("countPermit");				
			}//end of while-loop statement

			if (countPermit > 0){
				adminYn = true;
			}else{
				 adminYn = isAdmin_conn(UserID, conn);				
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return adminYn;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## UserInfo.isAdmin2() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.isAdmin2() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## UserInfo.isAdmin2() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## UserInfo.isAdmin2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of isAdmin2() method statement
	
}//end of UserInfo class statement
