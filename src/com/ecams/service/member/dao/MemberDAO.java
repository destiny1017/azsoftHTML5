
/*****************************************************************************************
	1. program ID	: MemberDAO.java
	2. create date	: 2006.08. 08
	3. auth		    : teok.kang
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package com.ecams.service.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.lang.*;


import org.apache.log4j.Logger;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.common.base.Encryptor;
import app.common.LoggableStatement;
/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MemberDAO{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

	public Object selectUserName(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String user_name              = null;
		String phone_number           = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
			try {
				conn = connectionContext.getConnection();
				strQuery.append("SELECT A.CM_USERNAME CM_USERNAME, CM_TELNO2 CM_TELNO2 FROM CMM0040 A  ");
				strQuery.append(" WHERE A.CM_USERID = ? ");
	            //pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, user_id);
		        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	
	            rs = pstmt.executeQuery();
	            
	 			if (rs.next()){
					user_name = rs.getString("CM_USERNAME");
					phone_number = rs.getString("CM_TELNO2");
	
					rst = new HashMap<String, String>();
		    		rst.put("user_name", user_name);
		    		rst.put("phone_number", phone_number);
		    		rsval.add(rst);
		    		
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				conn.close();
				rs = null;
				pstmt = null;
				conn = null;
			    return rsval.toArray();
	    		
			} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MemberDAO.selectUserName() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MemberDAO.selectUserName() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MemberDAO.selectUserName() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MemberDAO.selectUserName() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null) rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MemberDAO.selectUserName() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of selectUserName() method statement	

	
	public String selectUserId(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String ret_user = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_USERID USERID FROM CMM0040 ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);

            rs = pstmt.executeQuery();

			if (rs.next()){
				ret_user = rs.getString("USERID");
			}else {
				ret_user = "ERROR";
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MemberDAO.selectUserId() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MemberDAO.selectUserId() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MemberDAO.selectUserId() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MemberDAO.selectUserId() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MemberDAO.selectUserId() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return ret_user;

	}//end of selectUserId() method statement
	/**
	 * LOGIN 유효성검사
	 * @param  user_id
	 * @param  usr_passwd
	 * @throws SQLException
	 * @throws Exception
	 */
	public int validationLogin(String user_id, String usr_passwd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               CNT2        = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
   		    strQuery.append("SELECT count(*)  CNT2 FROM CMM0040 A ");
   		    strQuery.append("WHERE cm_userid = ? ");
   		    strQuery.append("  and CM_DUMYPW = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            pstmt.setString(2, usr_passwd);

            rs = pstmt.executeQuery();

			if (rs.next()) CNT2 = rs.getInt("CNT2");
			if(CNT2 > 0) CNT2 = 2;
			else if (CNT2 < 1) CNT2 = 3;
			else CNT2 = 3;

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return CNT2;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MemberDAO.validationLogin() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MemberDAO.validationLogin() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MemberDAO.validationLogin() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MemberDAO.validationLogin() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MemberDAO.validationLogin() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of validationLogin() method statement
	public int LoginCheck(String user_id, String usr_passwd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               CNT2        = 0;
		int               PwdCd       = 0;
		int               PwdCnt      = 0;
		int               PwdTerm     = 0;
		int               PwdSave     = 0;
//		int               rtn_cnt     = 0;
		String            eCAMSPwd     = "ecamshnb";
		String            testPwd     = "";
		int               retCnt      = 9;

		ConnectionContext connectionContext = new ConnectionResource();

		Encryptor oEncryptor = Encryptor.instance();
		String strEnPassWd = oEncryptor.strGetEncrypt(usr_passwd);
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
   		    strQuery.append("SELECT CM_PWDCD,CM_PWDCNT,CM_PWDTERM,CM_PASSWD,CM_TSTPWD  \n");
   		    strQuery.append("  FROM CMM0010                                            \n");
   		    strQuery.append(" WHERE CM_STNO='ECAMS'                                    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
			if (rs.next()){
			   PwdCd = Integer.parseInt(rs.getString("CM_PWDCD"));
			   PwdCnt = rs.getInt("CM_PWDCNT");
			   PwdTerm = rs.getInt("CM_PWDTERM");
			   eCAMSPwd = rs.getString("CM_PASSWD");
			   testPwd = rs.getString("CM_TSTPWD");
			   if (PwdTerm == 1) PwdSave = PwdCd;
			   else if (PwdTerm == 2) PwdSave = PwdCd * 7;
			   else if (PwdTerm == 3) PwdSave = PwdCd * 30;
			   else if (PwdTerm == 4) PwdSave = PwdCd * 120;
			   else if (PwdTerm == 5) PwdSave = PwdCd * 182;
			   else if (PwdTerm == 6) PwdSave = PwdCd * 365;
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			if (usr_passwd.equals(eCAMSPwd)) retCnt = 0;
			if (usr_passwd.equals(testPwd)) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmm0044 a,cmm0030 b    \n");
				strQuery.append(" where a.cm_userid=?                            \n");
				strQuery.append("   and a.cm_syscd=b.cm_syscd                    \n");
				strQuery.append("   and substr(b.cm_sysinfo,4,1)='1'             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, user_id);
	            rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getInt("cnt") > 0) retCnt = 0;
				}
				rs.close();
				pstmt.close();
				rs = null;
				pstmt = null;
			}
			if (retCnt != 0) {
				strQuery.setLength(0);
	   		    strQuery.append("SELECT CM_DUMYPW,CM_ACTIVE,CM_ERCOUNT,CM_CPASSWD,trunc(SYSDATE - CM_CHANGEDT,0) DayCnt ");
	   		    strQuery.append("FROM CMM0040 ");
	   		    strQuery.append("WHERE cm_userid = ? ");
				pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, user_id);
	            rs = pstmt.executeQuery();
				if (rs.next()){
					++CNT2;
					if (!rs.getString("CM_ACTIVE").equals("1")) retCnt = 1;
					else if (usr_passwd.equals(eCAMSPwd)) retCnt = 0;
					else if (rs.getInt("CM_ERCOUNT") >= PwdCnt) retCnt = 2;
					else if (rs.getInt("DayCnt") > PwdSave)		retCnt = 3;
					else if (rs.getString("CM_DUMYPW") == null && rs.getString("CM_CPASSWD") == null) retCnt = 3;
					else if (rs.getString("CM_CPASSWD") == null) {
						if (rs.getString("CM_DUMYPW").equals(usr_passwd)) {
							retCnt = 3;
						}else {
							strQuery.setLength(0);
				   		    strQuery.append("UPDATE CMM0040 SET CM_ERCOUNT=CM_ERCOUNT+1 ");
				   		    strQuery.append("WHERE cm_userid = ? ");
				   		    pstmt = conn.prepareStatement(strQuery.toString());
				            pstmt.setString(1, user_id);
				            pstmt.executeUpdate();
				            retCnt = 4;
						}
					}
					else if (!rs.getString("CM_CPASSWD").equals(strEnPassWd)) {
						strQuery.setLength(0);
			   		    strQuery.append("UPDATE CMM0040 SET CM_ERCOUNT=CM_ERCOUNT+1 ");
			   		    strQuery.append("WHERE cm_userid = ? ");
			   		    pstmt = conn.prepareStatement(strQuery.toString());
			            pstmt.setString(1, user_id);
			            pstmt.executeUpdate();
			            retCnt = 4;
					} else  {
						strQuery.setLength(0);
			   		    strQuery.append("UPDATE CMM0040 SET CM_ERCOUNT=0 ");
			   		    strQuery.append("WHERE cm_userid = ? ");
			   		    pstmt = conn.prepareStatement(strQuery.toString());
			            pstmt.setString(1, user_id);
			            pstmt.executeUpdate();;
			            retCnt = 0;
					}
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				rs = null;
				pstmt = null;
			}
			return retCnt;
			//if(CNT2 < 1) return 9;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## MemberDAO.LoginCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## MemberDAO.LoginCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## MemberDAO.LoginCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## MemberDAO.LoginCheck() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## MemberDAO.LoginCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of validationLogin() method statement

}//end of MemberDAO class statement
