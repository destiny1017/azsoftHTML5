
/*****************************************************************************************
	1. program ID	: PassWdDAO.java
	2. create date	: 2007. 8. 6
	3. auth		    : C.I.S
	4. update date	:
	5. auth		    :
	6. description	: 1. 비밀번호 암호화/복호화
*****************************************************************************************/

package com.ecams.service.passwd.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.lang.*;


import org.apache.log4j.Logger;

import app.common.LoggableStatement;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import com.ecams.common.base.Encryptor;


/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PassWdDAO{

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
	public String selectPassWd(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String usr_cpasswd = null;
		String strEnPassWd = null;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_CPASSWD,CM_DUMYPW FROM CMM0040  ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
			if (rs.next()){
				usr_cpasswd = rs.getString("CM_CPASSWD");
				if (usr_cpasswd == null){
					strEnPassWd = rs.getString("CM_DUMYPW");
				}
				else{
					strEnPassWd = usr_cpasswd;
				}
				//else strEnPassWd = oEncryptor.strGetDecrypt(usr_cpasswd);
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.selectPassWd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PassWdDAO.selectPassWd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.selectPassWd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PassWdDAO.selectPassWd() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PassWdDAO.selectPassWd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return strEnPassWd;

	}//end of selectUserName() method statement
	public String BefPassWd(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String usr_cpasswd = null;
		String strEnPassWd = null;
		String RetPass = null;
		Encryptor oEncryptor = Encryptor.instance();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_PASSWD FROM CMM0041  ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
			while (rs.next()){
				usr_cpasswd = rs.getString("CM_PASSWD");

				if (usr_cpasswd != null) {
					strEnPassWd = oEncryptor.strGetDecrypt(usr_cpasswd);
					if (RetPass != null) RetPass = RetPass + "," + strEnPassWd;
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.BefPassWd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PassWdDAO.BefPassWd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.BefPassWd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PassWdDAO.BefPassWd() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PassWdDAO.BefPassWd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return RetPass;

	}//end of selectUserName() method statement//return RetPass;
	public int PassWdChk(String user_id,String usr_passwd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String usr_cpasswd = null;
		String strEnPassWd = null;
		Encryptor oEncryptor = Encryptor.instance();
		int Cnt = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_PASSWD FROM CMM0041  ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
			while (rs.next()){
				usr_cpasswd = rs.getString("CM_PASSWD");

				if (usr_cpasswd != null) {
					strEnPassWd = oEncryptor.strGetDecrypt(usr_cpasswd);
					if (!strEnPassWd.equals(usr_passwd)) return 1;
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			Cnt = 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.PassWdChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PassWdDAO.PassWdChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.PassWdChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PassWdDAO.PassWdChk() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PassWdDAO.PassWdChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return Cnt;

	}//end of selectUserName() method statement
	public int updtPassWd(String user_id,String usr_passwd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtn_cnt     = 0;
		Encryptor oEncryptor = Encryptor.instance();
		String strEnPassWd = oEncryptor.SHA256(usr_passwd);

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			
			System.out.println("strEnPassWd + user_id" + user_id +":" +strEnPassWd );
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("UPDATE CMM0040 ");
			strQuery.append("   SET CM_CPASSWD = ?, ");
			strQuery.append("       CM_CHANGEDT = SYSDATE, ");
			strQuery.append("       CM_ERCOUNT = 0 ");
			strQuery.append(" WHERE CM_USERID = ? ");
   		    pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, strEnPassWd);
            pstmt.setString(2, user_id);
            rtn_cnt = pstmt.executeUpdate();
            pstmt.close();
            
            int cm_pwdnum=0;
            strQuery.setLength(0);
			strQuery.append("SELECT CM_PWDNUM FROM CMM0010 ");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()){
				cm_pwdnum = rs.getInt("cm_pwdnum");
			}
			rs.close();
            pstmt.close();
            
            int row_num=0;
            strQuery.setLength(0);
			strQuery.append("SELECT COUNT(*) as ROW_NUM FROM CMM0041 ");
			strQuery.append("WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, user_id);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()){
				row_num = rs.getInt("ROW_NUM");
			}
			rs.close();
            pstmt.close();
            
            if( row_num == cm_pwdnum ){
            	strQuery.setLength(0);
            	strQuery.append("UPDATE CMM0041 ");
    			strQuery.append("   SET CM_PASSWD = ?, ");
    			strQuery.append("       CM_CHANGDT = SYSDATE ");
    			strQuery.append(" WHERE CM_USERID = ? ");
    			strQuery.append("AND CM_CHANGDT in(SELECT MIN(CM_CHANGDT) FROM CMM0041)");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			pstmt = new LoggableStatement(conn,strQuery.toString());
                pstmt.setString(1, strEnPassWd);
                pstmt.setString(2, user_id);
    			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rtn_cnt = pstmt.executeUpdate();
                pstmt.close();
            } else {
            	strQuery.setLength(0);
    			strQuery.append("INSERT INTO  CMM0041 ");
    			strQuery.append("(CM_USERID, CM_CHANGDT, CM_PASSWD) VALUES \n");
    			strQuery.append("(?,SYSDATE, ? )");
       		    pstmt = conn.prepareStatement(strQuery.toString());
                pstmt = new LoggableStatement(conn,strQuery.toString());
       		    pstmt.setString(1, user_id);
                pstmt.setString(2, strEnPassWd);
    			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rtn_cnt = pstmt.executeUpdate();
                pstmt.close();
            }

            conn.close();
            pstmt = null;
            conn = null;

            
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.updtPassWd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PassWdDAO.updtPassWd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.updtPassWd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PassWdDAO.updtPassWd() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PassWdDAO.updtPassWd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		return rtn_cnt;

	}//end of selectUserId() method statement
	public int initPassWd(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtn_cnt     = 0;
        String            usr_passwd  = null;

		Encryptor oEncryptor = Encryptor.instance();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_JUMINNUM,CM_DUMYPW FROM CMM0040  ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            rs = pstmt.executeQuery();
			if (rs.next()){
				usr_passwd = rs.getString("CM_JUMINNUM");
				if (usr_passwd == null) usr_passwd = rs.getString("CM_DUMYPW");
				String strEnPassWd = oEncryptor.strGetEncrypt(usr_passwd);

				strQuery.setLength(0);
				strQuery.append("UPDATE CMM0040 ");
				strQuery.append("   SET CM_CPASSWD = ?, ");
				strQuery.append("       CM_CHANGEDT = SYSDATE, ");
				strQuery.append("       CM_ERCOUNT = 0 ");
				strQuery.append(" WHERE CM_USERID = ? ");
	   		    pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, strEnPassWd);
	            pstmt.setString(2, user_id);
	            rtn_cnt = pstmt.executeUpdate();
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.initPassWd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PassWdDAO.initPassWd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PassWdDAO.initPassWd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PassWdDAO.initPassWd() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## PassWdDAO.initPassWd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		return rtn_cnt;

	}//end of initPassWd() method statement

	public String encryptPassWd(String usr_passwd) throws SQLException, Exception {
		String strEnPassWd = null;
		Encryptor oEncryptor = Encryptor.instance();

		strEnPassWd = oEncryptor.SHA256(usr_passwd);

		return strEnPassWd;
	}//end of encryptPassWd() method statement

	
	public Object selectLastPassWord(String user_id) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            lstPasswd   = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			int cm_pwdnum=0;
			conn = connectionContext.getConnection();
			strQuery.append("SELECT CM_PWDNUM FROM CMM0010 ");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()){
				cm_pwdnum = rs.getInt("cm_pwdnum");
			}
			rs.close();
            pstmt.close();
			
            strQuery.setLength(0);
			strQuery.append("SELECT A.CM_PASSWD, CM_PASSWD FROM CMM0041 A  ");
			strQuery.append(" WHERE A.CM_USERID = ? ");
			strQuery.append(" AND  ROWNUM < ? ");
			strQuery.append(" ORDER BY A.CM_CHANGDT DESC");
			

            pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());

            pstmt.setString(1, user_id);
            pstmt.setInt(2, cm_pwdnum);
            
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();
			while (rs.next())
			{
				rst = new HashMap<String, String>();
				lstPasswd = rs.getString("CM_PASSWD");
				rst.put("lst_passwd", lstPasswd);
	    		rsval.add(rst);
	    		rst = null;

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
		ecamsLogger.error("## MemberDAO.selectLastPassWord() SQLException START ##");
		ecamsLogger.error("## Error DESC : ", sqlexception);
		ecamsLogger.error("## MemberDAO.selectLastPassWord() SQLException END ##");
		throw sqlexception;
		} catch (Exception exception) {
		exception.printStackTrace();
		ecamsLogger.error("## MemberDAO.selectLastPassWord() Exception START ##");
		ecamsLogger.error("## Error DESC : ", exception);
		ecamsLogger.error("## MemberDAO.selectLastPassWord() Exception END ##");
		throw exception;
		}finally{
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
	}//end of selectLastPassWord() method statement			
}//end of PassWdDAO class statement
