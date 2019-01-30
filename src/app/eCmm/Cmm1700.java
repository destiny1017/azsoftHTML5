
/*****************************************************************************************
	1. program ID	: eCmm1700.java
	2. create date	:
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: eCmm1700
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
//import com.ecams.common.base.Encryptor;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;




/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm1700{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public int PassWd_reset(String user_id, String JuMinNUM) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        int               rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

            strQuery.setLength(0);
			strQuery.append("SELECT CM_JUMINNUM FROM CMM0040  ");
			strQuery.append(" WHERE CM_USERID = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);

            rs = pstmt.executeQuery();
            strQuery.setLength(0);
            conn.setAutoCommit(false);
			if (rs.next()){
				if(rs.getString("CM_JUMINNUM")!= null && rs.getString("CM_JUMINNUM")!=""){

					if (JuMinNUM.equals(rs.getString("CM_JUMINNUM"))) {
			            strQuery.setLength(0);
						strQuery.append("UPDATE CMM0040 ");
						strQuery.append("   SET CM_CHANGEDT = SYSDATE,		\n");
						strQuery.append("       CM_CPASSWD = '',			\n");
						strQuery.append("       CM_ERCOUNT = 0				\n");
						strQuery.append(" WHERE CM_USERID = ? 				\n");
		                pstmt2 = conn.prepareStatement(strQuery.toString());
		                pstmt2.setString(1,user_id);
		                //ecamsLogger.error(user_id + " ::: "+ strQuery);
		                rtn_cnt = pstmt2.executeUpdate();
		                //ecamsLogger.error(" rtn_cnt===>  "+ rtn_cnt);
		                pstmt2.close();

					}else{
						rtn_cnt = 0;
					}
				}else{
		            strQuery.setLength(0);
					strQuery.append("UPDATE CMM0040 ");
					strQuery.append("   SET CM_JUMINNUM = ?, 			\n");
					strQuery.append("       CM_DUMYPW = ?, 				\n");
					strQuery.append("       CM_CHANGEDT = SYSDATE,		\n");
					strQuery.append("       CM_CPASSWD = '',			\n");
					strQuery.append("       CM_ERCOUNT = 0				\n");
					strQuery.append(" WHERE CM_USERID = ? 				\n");
		   		    pstmt2 = conn.prepareStatement(strQuery.toString());

		            pstmt2.setString(1, JuMinNUM);
		            pstmt2.setString(2, JuMinNUM);
		            pstmt2.setString(3, user_id);

		            rtn_cnt = pstmt2.executeUpdate();
		            pstmt2.close();
				}
			} else {
				//ecamsLogger.error("## 들어왔음 ##");
				rtn_cnt = 9;
			}

			rs.close();
			pstmt.close();

			conn.commit();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rtn_cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of PassWd_reset() method statement


	/**
	 * 사용자 비밀번호 초기화
	 * @param  String user_id, String initPWD
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

	public String PassWd_reset_initPWD(String user_id, String initPWD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
        String            rtnStr      = "";
        String            UserId      = "";
        int cnt = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

            strQuery.setLength(0);
			strQuery.append("SELECT CM_USERID FROM CMM0040  \n");
			strQuery.append(" WHERE (CM_USERID = ?          \n");
			strQuery.append("    or substr(CM_USERID,2) = ?)\n");
			strQuery.append("   AND cm_active='1'           \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, user_id);
            pstmt.setString(2, user_id);

            rs = pstmt.executeQuery();
            strQuery.setLength(0);
			while (rs.next()){
				++cnt;
				UserId = rs.getString("cm_userid");
				if (rs.getString("cm_userid").equals(user_id)) {
					cnt = 1;
					UserId = user_id;
					break;
				}
			}
			rs.close();
			pstmt.close();
			conn.setAutoCommit(false);
			if (cnt == 0) { //미등록된 사용자 일 경우
				rtnStr = "미등록된 사용자 입니다.";
			} else if (cnt > 1) {
				rtnStr = "중복된 사용자ID입니다. \r\n 사원번호 앞부분의 직원여부구분을 정확하게 입력하여 주시기 바랍니다.";
			}
			if (rtnStr==""){
	            strQuery.setLength(0);
				strQuery.append("SELECT cm_initpwd FROM cmm0010  ");
				strQuery.append(" WHERE cm_stno = 'ECAMS' ");

				pstmt = conn.prepareStatement(strQuery.toString());
	            rs = pstmt.executeQuery();

				if (rs.next()){
					if(rs.getString("cm_initpwd").equals(initPWD)){
			            strQuery.setLength(0);
						strQuery.append("UPDATE CMM0040 \n");
						strQuery.append("   SET CM_JUMINNUM = ?, 			\n");
						strQuery.append("       CM_DUMYPW = ?, 				\n");
						strQuery.append("       CM_CHANGEDT = SYSDATE,		\n");
						strQuery.append("       CM_CPASSWD = '',			\n");
						strQuery.append("       CM_ERCOUNT = 0				\n");
						strQuery.append(" WHERE CM_USERID = ? 				\n");
			   		    pstmt2 = conn.prepareStatement(strQuery.toString());

			            pstmt2.setString(1, initPWD);
			            pstmt2.setString(2, initPWD);
			            pstmt2.setString(3, UserId);

			            pstmt2.executeUpdate();
			            pstmt2.close();

			            rtnStr = "";
					}else{
						rtnStr = "입력하신 초기화비밀번호가 틀렸습니다.";
					}
				}else {
					rtnStr = "초기화비밀번호가 셋팅되어 있지 않습니다.\r\n 관리자에게 문의해주세요.";
				}
				rs.close();
				pstmt.close();
			}
			conn.commit();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtnStr;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}//end of PassWd_reset_initPWD() method statement
}//end of Cmm1700 class statement
