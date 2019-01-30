/*****************************************************************************************
	1. program ID	: eCmm0700.java
	2. create date	:
	3. auth		    : min seuk
	4. update date	:
	5. auth		    : No Name
	6. description	: [관리자] -> [환경설정]
*****************************************************************************************/

package app.eCmm;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
//import com.ecams.common.base.Encryptor;
import com.ecams.common.base.ConfigFactory;
import com.ecams.common.base.Encryptor;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import app.common.LoggableStatement;
import app.common.SystemPath;
//import app.common.LoggableStatement;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm0700{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

    //Encryptor oEncryptor = Encryptor.instance();

	/**
	 * eCAMS Agent정보 조회
	 * @param
	 * @return HashMap
	 * @throws SQLException
	 * @throws Exception
	 */
    public HashMap<String, String> getAgentInfo() throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_ipaddr,cm_port,cm_passwd,cm_policypwd,cm_pwdcnt,     \n");
			strQuery.append("       cm_pwdterm,cm_pwdcd,cm_ipaddr2,cm_initpwd,cm_tstpwd,    \n");
			strQuery.append("       cm_proctot,cm_lockbasedt,cm_loghisp,cm_pwdnum,cm_noname \n");
			strQuery.append("from cmm0010 \n");
			strQuery.append("where cm_stno='ECAMS' \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rst = new HashMap<String, String>();

            if (rs.next())
            {
				rst.put("cm_ipaddr", rs.getString("cm_ipaddr"));
				rst.put("cm_port", rs.getString("cm_port"));
				rst.put("cm_passwd", rs.getString("cm_passwd"));
				rst.put("cm_policypwd", rs.getString("cm_policypwd"));
				rst.put("cm_pwdcnt", rs.getString("cm_pwdcnt"));
				rst.put("cm_pwdterm", rs.getString("cm_pwdterm"));
				rst.put("cm_pwdcd", rs.getString("cm_pwdcd"));
				rst.put("cm_ipaddr2", rs.getString("cm_ipaddr2"));
				rst.put("cm_initpwd", rs.getString("cm_initpwd"));
				rst.put("cm_tstpwd", rs.getString("cm_tstpwd"));
				if (rs.getString("cm_proctot") == null && rs.getString("cm_proctot") == ""){
					rst.put("cm_proctot", "50");
				}else{
					rst.put("cm_proctot", rs.getString("cm_proctot"));
				}
				rst.put("cm_lockbasedt", rs.getString("cm_lockbasedt"));
				rst.put("cm_loghisp", rs.getString("cm_loghisp"));
				rst.put("cm_pwdnum", rs.getString("cm_pwdnum"));
				rst.put("cm_noname", rs.getString("cm_noname"));
			}
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;
            return rst;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getAgentInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getAgentInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getAgentInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getAgentInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getAgentInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getAgentInfo() method statement


    public HashMap<String, String> setAgentInfo(HashMap<String,String> objData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;

		int				  nret;

		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt \n");
			strQuery.append("from cmm0010 \n");
			strQuery.append("where cm_stno='ECAMS' \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            nret = 0;
            if (rs.next()){
            	nret = rs.getInt("cnt");
			}
            rs.close();
            pstmt.close();

            if (nret > 0){
    			strQuery.setLength(0);
    			strQuery.append("update cmm0010            \n");
    			strQuery.append("	set	cm_ipaddr = ? ,    \n");
    			strQuery.append("		cm_port   = ? ,    \n");
    			strQuery.append("		cm_passwd   = ? ,  \n");
    			strQuery.append("		cm_initpwd   = ? , \n");
    			strQuery.append("		cm_pwdcnt   = ? ,  \n");
    			strQuery.append("		cm_pwdterm   = ? , \n");
    			strQuery.append("		cm_pwdcd   = ? ,   \n");
    			strQuery.append("		cm_ipaddr2   = ? , \n");
    			strQuery.append("		cm_tstpwd   = ?,   \n");
    			strQuery.append("		cm_proctot   = ?,  \n");
    			strQuery.append("		cm_lockbasedt = ?, \n");
    			strQuery.append("		cm_loghisp   = ?,  \n");
    			strQuery.append("		cm_pwdnum   = ?,   \n");
    			strQuery.append("		cm_noname   = ?    \n");
    			strQuery.append("where cm_stno='ECAMS'     \n");
            }
            else{
    			strQuery.setLength(0);
    			strQuery.append("insert into cmm0010 (CM_STNO,CM_IPADDR,CM_PORT,CM_PASSWD,CM_INITPWD, \n");
    			strQuery.append("CM_PWDCNT,CM_PWDTERM,CM_PWDCD,CM_IPADDR2,CM_TSTPWD,CM_PROCTOT,       \n");
    			strQuery.append("CM_LOCKBASEDT,CM_LOGHISP,CM_PWDNUM,CM_NONAME)                        \n");
    			strQuery.append("values ('ECAMS', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");
            }

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, objData.get("cm_ipaddr"));
            pstmt.setString(2, objData.get("cm_port"));
            if (objData.get("cm_passwd") != null)
            	pstmt.setString(3, objData.get("cm_passwd"));
            else
            	pstmt.setString(3, "");
            if (objData.get("cm_initpwd") != null)
            	pstmt.setString(4, objData.get("cm_initpwd"));
            else
            	pstmt.setString(4, "");
            pstmt.setString(5, objData.get("cm_pwdcnt"));
            pstmt.setString(6, objData.get("cm_pwdterm"));
            pstmt.setString(7, objData.get("cm_pwdcd"));
            pstmt.setString(8, objData.get("cm_ipaddr2"));
            if (objData.get("cm_tstpwd") != null)
            	pstmt.setString(9, objData.get("cm_tstpwd"));
            else
            	pstmt.setString(9, "");
            if (objData.get("cm_proctot") != null)
            	pstmt.setString(10, objData.get("cm_proctot"));
            else
            	pstmt.setString(10, "");
            
        	pstmt.setString(11, objData.get("cm_lockbasedt"));
        	pstmt.setString(12, objData.get("cm_loghisp"));
        	pstmt.setString(13, objData.get("cm_pwdnum"));
        	pstmt.setString(14, objData.get("cm_noname"));
            
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            nret = pstmt.executeUpdate();

            rst = new HashMap<String, String>();
            if (nret > 0){
            	rst.put("retval", "0");
            	rst.put("retmsg", "환경설정등록 처리 완료하였습니다.");
            	conn.commit();
            	pstmt.close();
            }
            else{
            	rst.put("retval", "1");
            	rst.put("retmsg", "환경설정등록 처리 실패.");
            	conn.rollback();
            	pstmt.close();
            }
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;


            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.setAgentInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.setAgentInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.setAgentInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.setAgentInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.setAgentInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setAgentInfo() method statement


    public Object[] getTab1Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_stno,a.cm_timecd,a.cm_sttime,a.cm_edtime,b.cm_codename \n");
			strQuery.append("from cmm0020 b,cmm0014 a \n");
			strQuery.append("where b.cm_macode='ECAMSTIME' and b.cm_micode=a.cm_timecd \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();
            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_stno", rs.getString("cm_stno"));
				rst.put("cm_timecd", rs.getString("cm_timecd"));
				rst.put("stedtime", rs.getString("cm_sttime")+"~"+rs.getString("cm_edtime"));
				rst.put("cm_sttime", rs.getString("cm_sttime"));
				rst.put("cm_edtime", rs.getString("cm_edtime"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab1Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab1Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab1Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab1Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab1Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement


    public HashMap<String, String> delTab1Info(String timegb) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		int		nret;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("delete cmm0014 where cm_stno='ECAMS' \n");
			strQuery.append("and CM_TIMECD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, timegb);

            nret = pstmt.executeUpdate();

            if (nret > 0){
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "0");
	        	rst.put("retmsg", "삭제처리를 완료하였습니다.");
	        	conn.commit();
            }
            else{
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "1");
	        	rst.put("retmsg", "삭제처리를 실패하였습니다.");
	        	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab1Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab1Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab1Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab1Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab1Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab1Info() method statement


    public HashMap<String, String> addTab1Info(String timegb,String stTime,String edTime) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  nret;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmm0014 \n");
			strQuery.append("where cm_stno='ECAMS' \n");
			strQuery.append("and CM_TIMECD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, timegb);

			rs = pstmt.executeQuery();

			nret = 0;
			if (rs.next()){
				nret = rs.getInt("cnt");
			}

			rs.close();
			pstmt.close();

			if (nret > 0){
				strQuery.setLength(0);
				strQuery.append("update cmm0014 set 				\n");
				strQuery.append("		cm_sttime = ?, 				\n");
				strQuery.append("		cm_edtime = ? 				\n");
				strQuery.append("where cm_stno = 'ECAMS' 			\n");
				strQuery.append("and   cm_timecd = ? 				\n");
			}
			else{
				strQuery.setLength(0);
				strQuery.append("insert into cmm0014 (CM_STNO,CM_STTIME,CM_EDTIME,CM_TIMECD) 	\n");
				strQuery.append(" values ( 'ECAMS', ?, ?, ? ) 									\n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, stTime);
			pstmt.setString(2, edTime);
			pstmt.setString(3, timegb);

			nret = pstmt.executeUpdate();

            if (nret > 0){
            	rst = new HashMap<String, String>();
            	rst.put("retval", "0");
            	rst.put("retmsg", "등록처리를 완료하였습니다.");
            	conn.commit();
            }
            else{
            	rst = new HashMap<String, String>();
            	rst.put("retval", "1");
            	rst.put("retmsg", "등록처리를 실패하였습니다.");
            	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab1Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab1Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab1Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab1Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab1Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab1Info() method statement


    public Object[] getTab2Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_stno,a.cm_delcd,a.cm_delterm,a.cm_termcd, 		\n");
			strQuery.append("b.cm_codename,c.cm_codename termname 						\n");
			strQuery.append("from cmm0020 c,cmm0020 b,cmm0013 a  						\n");
			strQuery.append("where b.cm_macode='ECAMSDIR' and b.cm_micode=a.cm_delcd 	\n");
			strQuery.append("and c.cm_macode='DBTERM' and c.cm_micode=a.cm_termcd 		\n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_stno", rs.getString("cm_stno"));
				rst.put("cm_delcd", rs.getString("cm_delcd"));
				rst.put("delterm", rs.getString("cm_delterm")+" "+rs.getString("termname"));
				rst.put("cm_delterm", rs.getString("cm_delterm"));
				rst.put("cm_termcd", rs.getString("cm_termcd"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab2Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab2Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab2Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab2Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab2Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTab2Info() method statement


    public HashMap<String, String> delTab2Info(String delgb) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		int		nret;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0013 where cm_stno='ECAMS' \n");
			strQuery.append("and CM_DELCD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, delgb);

            nret = pstmt.executeUpdate();

            if (nret > 0){
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "0");
	        	rst.put("retmsg", "삭제처리를 완료하였습니다.");
	        	conn.commit();
            }
            else{
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "1");
	        	rst.put("retmsg", "삭제처리를 실패하였습니다.");
	        	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab2Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab2Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab2Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab2Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab2Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab2Info() method statement


    public HashMap<String, String> addTab2Info(String delgb,String deljugi,String jugigb) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  nret;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmm0013 \n");
			strQuery.append("where cm_stno='ECAMS' \n");
			strQuery.append("and cm_delcd = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, delgb);

			rs = pstmt.executeQuery();

			nret = 0;
			if (rs.next()){
				nret = rs.getInt("cnt");
			}

			rs.close();
			pstmt.close();

			if (nret > 0){
				strQuery.setLength(0);
				strQuery.append("update cmm0013 set \n");
				strQuery.append("		cm_DELTERM = ?, \n");
				strQuery.append("		cm_TERMCD = ? \n");
				strQuery.append("where cm_stno = 'ECAMS' \n");
				strQuery.append("and   cm_delcd = ? \n");

			}
			else{
				strQuery.setLength(0);
				strQuery.append("insert into cmm0013 (CM_STNO,cm_DELTERM,cm_TERMCD,cm_delcd,CM_AGENTDATE) \n");
				strQuery.append(" values ( 'ECAMS', ?, ?, ? ,sysdate) \n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, deljugi);
			pstmt.setString(2, jugigb);
			pstmt.setString(3, delgb);

			nret = pstmt.executeUpdate();

            if (nret > 0){
            	rst = new HashMap<String, String>();
            	rst.put("retval", "0");
            	rst.put("retmsg", "등록처리를 완료하였습니다.");
            	conn.commit();
            }
            else{
            	rst = new HashMap<String, String>();
            	rst.put("retval", "1");
            	rst.put("retmsg", "등록처리를 실패하였습니다.");
            	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;



            return rst;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab2Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab2Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab2Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab2Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab2Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab2Info() method statement


    public Object[] getTab3Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_stno,a.cm_pathcd,a.cm_path,a.cm_downip,a.cm_downport, \n");
			strQuery.append("a.cm_downusr, a.cm_downpass , b.cm_codename  \n");
			strQuery.append("from cmm0020 b,cmm0012 a \n");
			strQuery.append("where b.cm_macode='ECAMSDIR'  \n");
			strQuery.append("and a.cm_pathcd=b.cm_micode  \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_stno", rs.getString("cm_stno"));
				rst.put("cm_pathcd", rs.getString("cm_pathcd"));
				rst.put("cm_path", rs.getString("cm_path"));
				rst.put("cm_downip", rs.getString("cm_downip"));
				rst.put("cm_downport", rs.getString("cm_downport"));
				rst.put("cm_downusr", rs.getString("cm_downusr"));
				rst.put("cm_downpass", rs.getString("cm_downpass"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab3Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab3Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab3Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab3Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab3Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTab3Info() method statement


    public HashMap<String, String> delTab3Info(String pathcd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		int		nret;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("delete cmm0012 where cm_stno='ECAMS' \n");
			strQuery.append("and CM_PATHCD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, pathcd);

            nret = pstmt.executeUpdate();

            if (nret > 0){
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "0");
	        	rst.put("retmsg", "삭제처리를 완료하였습니다.");
	        	conn.commit();
            }
            else{
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "1");
	        	rst.put("retmsg", "삭제처리를 실패하였습니다.");
	        	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;


            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab3Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab3Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab3Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab3Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab3Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab3Info() method statement


    public HashMap<String, String> addTab3Info(String pathcd,String path,String tip,String tport) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  nret;
		int				  pstmtcnt;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmm0012 \n");
			strQuery.append("where cm_stno='ECAMS' \n");
			strQuery.append("and CM_PATHCD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, pathcd);

			rs = pstmt.executeQuery();

			nret = 0;
			if (rs.next()){
				nret = rs.getInt("cnt");
			}

			rs.close();
			pstmt.close();

			if (nret > 0){
				strQuery.setLength(0);
				strQuery.append("update cmm0012 set \n");
				if (tip != null){
					if (!tip.equals("")){
						strQuery.append("		cm_downip = ?, \n");
					}
					else{
						strQuery.append("		cm_downip = '', \n");
					}
				}
				else{
					strQuery.append("		cm_downip = '', \n");
				}
				if (tport != null){
					if (!tport.equals("")){
						strQuery.append("		cm_downport = ?, \n");
					}
					else{
						strQuery.append("		cm_downport = '', \n");
					}
				}
				else{
					strQuery.append("		cm_downport = '', \n");
				}
				strQuery.append("		cm_path = ? \n");
				strQuery.append("where cm_stno = 'ECAMS' \n");
				strQuery.append("and   cm_pathcd = ? \n");

			}
			else{
				strQuery.setLength(0);
				strQuery.append("insert into cmm0012 (CM_STNO,CM_downip,CM_downport,CM_path,CM_pathcd) \n");
				strQuery.append(" values ( 'ECAMS', \n");
				if (tip != null){
					if (!tip.equals("")){
						strQuery.append(" ?, \n");
					}
					else{
						strQuery.append(" '', \n");
					}
				}
				else{
					strQuery.append(" '', \n");
				}
				if (tport != null){
					if (!tport.equals("")){
						strQuery.append(" ?, \n");
					}
					else{
						strQuery.append(" '', \n");
					}
				}
				else{
					strQuery.append(" '', \n");
				}
				strQuery.append(" ?, ? ) \n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmtcnt = 1;

			if (tip != null){
				if (!tip.equals("")){
					pstmt.setString(pstmtcnt++, tip);
				}
			}
			if (tport != null){
				if (!tport.equals("")){
					pstmt.setString(pstmtcnt++, tport);
				}
			}
			pstmt.setString(pstmtcnt++, path);
			pstmt.setString(pstmtcnt++, pathcd);

			nret = pstmt.executeUpdate();

            if (nret > 0){
            	rst = new HashMap<String, String>();
            	rst.put("retval", "0");
            	rst.put("retmsg", "등록처리를 완료하였습니다.");
            	conn.commit();
            }
            else{
            	rst = new HashMap<String, String>();
            	rst.put("retval", "1");
            	rst.put("retmsg", "등록처리를 실패하였습니다.");
            	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab3Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab3Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab3Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab3Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab3Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab3Info() method statement


    public Object[] getTab4Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_stno,a.cm_gbncd,a.cm_svrip, \n");
			strQuery.append("a.cm_portno,a.cm_svrusr,a.cm_svrpass,b.cm_codename \n");
			strQuery.append("from cmm0020 b,cmm0015 a  \n");
			strQuery.append("where b.cm_macode='SVPROC' \n");
			strQuery.append("and b.cm_micode=a.cm_gbncd \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_stno", rs.getString("cm_stno"));
				rst.put("cm_gbncd", rs.getString("cm_gbncd"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_portno", rs.getString("cm_portno"));
				rst.put("cm_svrusr", rs.getString("cm_svrusr"));
				//rst.put("cm_svrpass", rs.getString("cm_svrpass"));
				if (rs.getString("cm_svrpass") != null)
					//rst.put("cm_svrpass", oEncryptor.strGetDecrypt(rs.getString("cm_svrpass")));
					rst.put("cm_svrpass", rs.getString("cm_svrpass"));
				else
					rst.put("cm_svrpass", "");
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab4Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab4Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab4Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab4Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab4Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTab4Info() method statement


    public HashMap<String, String> delTab4Info(String jobgb) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		int		nret;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("delete cmm0015 where cm_stno='ECAMS' \n");
			strQuery.append("and CM_GBNCD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());


			pstmt.setString(1, jobgb);

            nret = pstmt.executeUpdate();

            if (nret > 0){
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "0");
	        	rst.put("retmsg", "삭제처리를 완료하였습니다.");
	        	conn.commit();
            }
            else{
	            rst = new HashMap<String, String>();
	           	rst.put("retval", "1");
	        	rst.put("retmsg", "삭제처리를 실패하였습니다.");
	        	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab4Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab4Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab4Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab4Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab4Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab4Info() method statement


    public HashMap<String, String> addTab4Info(String jobgb,String tip,String tport,String tuserid,String tpwd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  nret;
		int				  pstmtcnt;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmm0015 \n");
			strQuery.append("where cm_stno='ECAMS' \n");
			strQuery.append("and CM_GBNCD = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(1, jobgb);

			rs = pstmt.executeQuery();

			nret = 0;
			if (rs.next()){
				nret = rs.getInt("cnt");
			}

			rs.close();
			pstmt.close();

			if (nret > 0){
				strQuery.setLength(0);
				strQuery.append("update cmm0015 set \n");
				if (tuserid != null){
					if (!tuserid.equals("")){
						strQuery.append("		CM_SVRUSR = ?, \n");
					}
					else{
						strQuery.append("		CM_SVRUSR = '', \n");
					}
				}
				else{
					strQuery.append("		CM_SVRUSR = '', \n");
				}
				if (tpwd != null){
					if (!tpwd.equals("")){
						strQuery.append("		CM_SVRPASS = ?, \n");
					}
					else{
						strQuery.append("		CM_SVRPASS = '', \n");
					}
				}
				else{
					strQuery.append("		CM_SVRPASS = '', \n");
				}
				strQuery.append("		cm_svrip = ?, \n");
				strQuery.append("		cm_portno = ? \n");
				strQuery.append("where cm_stno = 'ECAMS' \n");
				strQuery.append("and   cm_gbncd = ? \n");
			}else{
				strQuery.setLength(0);
				strQuery.append("insert into cmm0015 (CM_STNO,CM_SVRUSR,CM_SVRPASS,CM_SVRIP,CM_PORTNO,CM_GBNCD) \n");
				strQuery.append(" values ( 'ECAMS', \n");
				if (tuserid != null){
					if (!tuserid.equals("")){
						strQuery.append(" ?, \n");
					}
					else{
						strQuery.append(" '', \n");
					}
				}
				else{
					strQuery.append(" '', \n");
				}
				if (tpwd != null){
					if (!tpwd.equals("")){
						strQuery.append(" ?, \n");
					}
					else{
						strQuery.append(" '', \n");
					}
				}
				else{
					strQuery.append(" '', \n");
				}
				strQuery.append(" ?, ?, ? ) \n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());

			pstmtcnt = 1;

			if (tuserid != null){
				if (!tuserid.equals("")){
					pstmt.setString(pstmtcnt++, tuserid);
				}
			}
			if (tpwd != null){
				if (!tpwd.equals("")){
					//pstmt.setString(pstmtcnt++, oEncryptor.strGetEncrypt(tpwd));
					pstmt.setString(pstmtcnt++, tpwd);
				}
			}

			pstmt.setString(pstmtcnt++, tip);
			pstmt.setString(pstmtcnt++, tport);
			pstmt.setString(pstmtcnt++, jobgb);

			nret = pstmt.executeUpdate();

            if (nret > 0){
            	rst = new HashMap<String, String>();
            	rst.put("retval", "0");
            	rst.put("retmsg", "등록처리를 완료하였습니다.");
            	conn.commit();
            }
            else{
            	rst = new HashMap<String, String>();
            	rst.put("retval", "1");
            	rst.put("retmsg", "등록처리를 실패하였습니다.");
            	conn.rollback();
            }

            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rst;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab4Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab4Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab4Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab4Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab4Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab4Info() method statement
    public Object[] getTab5Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_sysgbn,a.cm_rsrccd,b.cm_codename,      \n");
			strQuery.append("       c.cm_codename rsrccd                        \n");
			strQuery.append("  from cmm0020 c,cmm0020 b,cmm0016 a               \n");
			strQuery.append(" where b.cm_macode='SYSPROC'                       \n");
			strQuery.append("   and b.cm_micode=a.cm_sysgbn                     \n");
			strQuery.append("   and c.cm_macode='JAWON'                         \n");
			strQuery.append("   and c.cm_micode=a.cm_rsrccd                     \n");
			strQuery.append(" order by a.cm_sysgbn,a.cm_rsrccd                  \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_sysgbn", rs.getString("cm_sysgbn"));
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("rsrccd", rs.getString("rsrccd"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab5Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab5Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab5Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab5Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab5Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTab5Info() method statement


    public Object[] getJawon() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select c.cm_micode,c.cm_codename                      \n");
			strQuery.append("  from cmm0036 e,cmm0031 d,cmm0020 c,cmm0038 b,cmm0030 a \n");
			strQuery.append(" where substr(a.cm_sysinfo,6,1)='1'                   \n");
			strQuery.append("   and a.cm_closedt is null                           \n");
			strQuery.append("   and a.cm_syscd=d.cm_syscd and d.cm_closedt is null \n");
			strQuery.append("   and d.cm_svrcd='05'                                \n");
			strQuery.append("   and d.cm_syscd=b.cm_syscd and d.cm_svrcd=b.cm_svrcd\n");
			strQuery.append("   and d.cm_seqno=b.cm_seqno                          \n");
			strQuery.append("   and b.cm_syscd=e.cm_syscd and b.cm_rsrccd=e.cm_rsrccd\n");
			strQuery.append("   and substr(e.cm_info,35,1)='1'                     \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=b.cm_rsrccd\n");
			strQuery.append(" group by c.cm_micode,c.cm_codename                   \n");
			strQuery.append(" order by c.cm_codename                               \n");

			pstmt = conn.prepareStatement(strQuery.toString());
		//	pstmt = new LoggableStatement(conn,strQuery.toString());
		//	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getJawon() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getJawon() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getJawon() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getJawon() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getJawon() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getJawon() method statement

    public int delTab5Info(String SysGbn,String Jawon) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int               i = 0;

		try {
			conn = connectionContext.getConnection();
			String strJawon[] = Jawon.split(",");

			for (i=0;strJawon.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0016           \n");
				strQuery.append(" where cm_sysgbn=?       \n");
				strQuery.append("   and cm_rsrccd=?       \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, SysGbn);
				pstmt.setString(2, strJawon[i]);
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;


            return 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab5Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab5Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab5Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab5Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab5Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab5Info() method statement


    public int addTab5Info(String SysGbn,String Jawon) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		StringBuffer      strQuery    = new StringBuffer();
		ResultSet         rs          = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int               i = 0;

		try {
			conn = connectionContext.getConnection();
			String strJawon[] = Jawon.split(",");

			for (i=0;strJawon.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmm0016 \n");
				strQuery.append("where cm_sysgbn=?                \n");
				strQuery.append("  and CM_rsrccd=?                \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, SysGbn);
				pstmt.setString(2, strJawon[i]);
				rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getInt("cnt") == 0) {
						strQuery.setLength(0);
						strQuery.append("insert into cmm0016      \n");
						strQuery.append(" (cm_sysgbn,cm_rsrccd)   \n");
						strQuery.append("values (?, ?)            \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, SysGbn);
						pstmt2.setString(2, strJawon[i]);
						pstmt2.executeUpdate();
						pstmt2.close();
					}
				}
				rs.close();
				pstmt.close();
			}

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;


            return 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab5Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab5Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab5Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab5Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab5Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab5Info() method statement


    public Object[] getTab6Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		String            strTerm    = "";
		String            strTime    = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_termcd,a.cm_sttime,  \n");
			strQuery.append("       a.cm_edtime,a.cm_termsub,            \n");
			strQuery.append("       b.cm_codename,c.cm_sysmsg            \n");
			strQuery.append("  from cmm0030 c,cmm0020 b,cmm0330 a        \n");
			strQuery.append(" where b.cm_macode='STOPTERM'               \n");
			strQuery.append("   and b.cm_micode=a.cm_termcd              \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd                \n");
			strQuery.append(" order by a.cm_syscd                        \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_termcd", rs.getString("cm_termcd"));
				rst.put("cm_sttime", rs.getString("cm_sttime"));
				rst.put("cm_edtime", rs.getString("cm_edtime"));
				strTerm = rs.getString("cm_codename");
				if (rs.getString("cm_termsub") != null) {
					if (rs.getString("cm_termcd").equals("2")) {
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020    \n");
						strQuery.append(" where cm_macode='WEEKDAY'         \n");
						strQuery.append("   and cm_micode=?                 \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cm_termsub"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							strTerm = strTerm + " " + rs2.getString("cm_codename");
						}
						rs2.close();
						pstmt2.close();
					} else {
						strTerm = strTerm + " " + rs.getString("cm_termsub") + "일";
					}

					rst.put("cm_termsub", rs.getString("cm_termsub"));
				}
				rst.put("termmsg", strTerm);
				strTime = rs.getString("cm_sttime").substring(0,2) + ":" + rs.getString("cm_sttime").substring(2)
				    + " - " + rs.getString("cm_edtime").substring(0,2) + ":" + rs.getString("cm_edtime").substring(2);
				rst.put("timemsg", strTime);
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab5Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab5Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab5Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab5Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab5Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTab6Info() method statement
    public int addTab6Info(String TermCd,String StTime,String EdTime,String TermSub,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int               i = 0;

		try {
			conn = connectionContext.getConnection();
			String strSysCd[] = SysCd.split(",");

			for (i=0;strSysCd.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0330                \n");
				strQuery.append(" where cm_syscd=?             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strSysCd[i]);
				pstmt.executeUpdate();
				pstmt.close();

				strQuery.setLength(0);
				strQuery.append("insert into cmm0330                                 \n");
				strQuery.append(" (CM_SYSCD,CM_TERMCD,CM_STTIME,CM_EDTIME,CM_TERMSUB)\n");
				strQuery.append(" values (?, ?, ?, ?, ?)                             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strSysCd[i]);
				pstmt.setString(2, TermCd);
				pstmt.setString(3, StTime);
				pstmt.setString(4, EdTime);
				pstmt.setString(5, TermSub);
				pstmt.executeUpdate();
				pstmt.close();
			}

			conn.close();
			conn = null;
			pstmt = null;

            return 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab6Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab6Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab6Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab6Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab6Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab6Info() method statement
    public int delTab6Info(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int               i = 0;

		try {
			conn = connectionContext.getConnection();
			String strSysCd[] = SysCd.split(",");

			for (i=0;strSysCd.length>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0330                \n");
				strQuery.append(" where cm_syscd=?             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strSysCd[i]);
				pstmt.executeUpdate();
				pstmt.close();
			}

			conn.close();
			conn = null;
			pstmt = null;

            return 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab6Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delTab6Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delTab6Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delTab6Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delTab6Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab6Info() method statement

    public int addTab7Info(String Year,String Day) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0017                \n");
			strQuery.append(" where cm_year=?             \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, Year);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("insert into cmm0017                                 \n");
			strQuery.append(" (cm_year,cm_monthday)								 \n");
			strQuery.append(" values (?, ?)			                             \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, Year);
			pstmt.setString(2, Day);
			pstmt.executeUpdate();
			pstmt.close();

			conn.close();
			conn = null;
			pstmt = null;

            return 0;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab6Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addTab6Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addTab6Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addTab6Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addTab6Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addTab6Info() method statement

    public Object[] getTab7Info() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_year, cm_monthday 					\n");
			strQuery.append("from cmm0017  									\n");

			pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("year", rs.getString("cm_year"));
				rst.put("monthday", rs.getString("cm_monthday"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;


            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab1Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getTab1Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getTab1Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getTab1Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getTab1Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement
    public Object[] getSvrProperties() throws Exception{
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
        
		Properties props = new Properties(); 
        ClassLoader cl;
        
        try{
        	cl = Thread.currentThread().getContextClassLoader();
            if( cl == null ){
                cl = ClassLoader.getSystemClassLoader();
            }

            SystemPath systemPath = new SystemPath();
            String path = systemPath.getTmpDir("14")+"/ecams.conf";
            systemPath = null;
            
            FileInputStream fip = new FileInputStream(path);
            props.load(new BufferedInputStream(fip));
        	
            int arrCnt = 0;
            
        	rst = new HashMap<String, String>();
        	rst.put("gbn", "DBCONN");
        	if(props.getProperty("DBCONN") == null || props.getProperty("DBCONN") == ""){
        		rst.put("value", "");
        	}else{
        		rst.put("value", props.getProperty("DBCONN"));
        	}
        	rtList.add(arrCnt++, rst);
        	rst = null;
        	
        	rst = new HashMap<String, String>();
        	rst.put("gbn", "DBUSER");
        	if(props.getProperty("DBUSER") == null || props.getProperty("DBUSER") == ""){
        		rst.put("value", "");
        	}else{
        		rst.put("value", props.getProperty("DBUSER"));
        	}
        	rtList.add(arrCnt++, rst);
        	rst = null;
        	
        	rst = new HashMap<String, String>();
        	rst.put("gbn", "DBPASS");
        	if(props.getProperty("DBPASS") == null || props.getProperty("DBPASS") == ""){
        		rst.put("value", "");
        	}else{
        		rst.put("value", props.getProperty("DBPASS"));
        	}
        	rtList.add(arrCnt++, rst);
        	rst = null;
        	
        	props = null;
        	fip.close();
        	fip = null;
        	
        	return  rtList.toArray();
			
		} catch(Exception e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.getSvrProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.getSvrProperties() Exception END ##");	
        	return null;
        } finally{
        	if(rst != null) rst = null;
		}
    }

    public String setSvrProperties(String dbconn, String dbuser, String dbpass) throws  Exception,IOException {
        Properties props = new Properties(); 
        File shfile=null;
        
        try{

            SystemPath systemPath = new SystemPath();
            String path = systemPath.getTmpDir("14")+"/ecams.conf";
            systemPath = null;
            
            shfile = new File(path);			
			if( !(shfile.isFile()) )              //File존재여부
			{
				shfile.createNewFile();          //File생성
			} 
			
            FileInputStream fip = new FileInputStream(path);
            props.load(new BufferedInputStream(fip));
            
            Encryptor oEncryptor = Encryptor.instance();
            props.setProperty("DBCONN", oEncryptor.strGetEncrypt(dbconn));
            props.setProperty("DBUSER", oEncryptor.strGetEncrypt(dbuser));
            props.setProperty("DBPASS", oEncryptor.strGetEncrypt(dbpass));
            
            props.store(new FileOutputStream(path), "");
			props = null;
            oEncryptor = null;
			
            fip.close();       	
        	fip = null;
        	
			return  "OK";

		} catch(IOException e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.setSvrProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.setSvrProperties() Exception END ##");	
        	return null;
        } catch(Exception e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.setSvrProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.setSvrProperties() Exception END ##");	
        	return null;
        } finally{
        	
		}
	}
    
    public Object[] getProperties(String strGbn) throws Exception{
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
        
        try{
        	int arrCnt = 0;
        	
        	if("O".equals(strGbn)){
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_jdbcUse");
	        	if(ConfigFactory.getProperties(strGbn+"_jdbcUse") == null || ConfigFactory.getProperties(strGbn+"_jdbcUse") == ""){
	        		rst.put("value", "false");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_jdbcUse"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_poolName");
	        	if(ConfigFactory.getProperties(strGbn+"_poolName") == null || ConfigFactory.getProperties(strGbn+"_poolName") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_poolName"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_driverClassName");
	        	if(ConfigFactory.getProperties(strGbn+"_driverClassName") == null || ConfigFactory.getProperties(strGbn+"_driverClassName") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_driverClassName"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_url");
	        	if(ConfigFactory.getProperties(strGbn+"_url") == null || ConfigFactory.getProperties(strGbn+"_url") == ""){
	        		rst.put("value", "");
	        	}else{
	            	String urlName = ConfigFactory.getProperties(strGbn+"_url").toString();
	        		if(urlName.substring(0, urlName.lastIndexOf(":")+1).length()==0){
	        			Encryptor oEncryptor = Encryptor.instance();
	        			urlName = oEncryptor.strGetDecrypt(ConfigFactory.getProperties(strGbn+"_url")).toString();
	        			oEncryptor = null;
	        		}
	            	rst.put("value", urlName.substring(0, urlName.lastIndexOf(":")+1)+"****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_username");
	        	//rst.put("value", ConfigFactory.getProperties(strGbn+"_username"));
	        	if(ConfigFactory.getProperties(strGbn+"_username") == null || ConfigFactory.getProperties(strGbn+"_username") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", "****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_password");
	        	//rst.put("value", ConfigFactory.getProperties(strGbn+"_password"));
	        	if(ConfigFactory.getProperties(strGbn+"_password") == null || ConfigFactory.getProperties(strGbn+"_password") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", "****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_defaultAutoCommit");
	        	if (ConfigFactory.getProperties(strGbn+"_defaultAutoCommit") == null || ConfigFactory.getProperties(strGbn+"_defaultAutoCommit") == "") {
	        		rst.put("value", "true");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_defaultAutoCommit"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_defaultReadOnly");
	        	if (ConfigFactory.getProperties(strGbn+"_defaultReadOnly") == null || ConfigFactory.getProperties(strGbn+"_defaultReadOnly") == "") {
	        		rst.put("value", "true");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_defaultReadOnly"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_maxActive");
	        	if (ConfigFactory.getProperties(strGbn+"_maxActive") == null || ConfigFactory.getProperties(strGbn+"_maxActive") == "") {
	        		rst.put("value", "100");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_maxActive"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_maxActive");
	        	if (ConfigFactory.getProperties(strGbn+"_maxIdle") == null || ConfigFactory.getProperties(strGbn+"_maxIdle") == "") {
	        		rst.put("value", "30");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_maxIdle"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_maxActive");
	        	if (ConfigFactory.getProperties(strGbn+"_maxWait") == null || ConfigFactory.getProperties(strGbn+"_maxWait") == "") {
	        		rst.put("value", "10000");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_maxWait"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_secu");
	        	if (ConfigFactory.getProperties(strGbn+"_secu") == null || ConfigFactory.getProperties(strGbn+"_secu") == "") {
	        		rst.put("value", "true");
	        	}else{
	        		rst.put("value", ConfigFactory.getProperties(strGbn+"_secu"));
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
        	}else if("P".equals(strGbn)){
        		rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_url");
	        	if(ConfigFactory.getPluginProperties(strGbn+"_url") == null || ConfigFactory.getPluginProperties(strGbn+"_url") == ""){
	        		rst.put("value", "****");
	        	}else{
	            	String urlName = ConfigFactory.getPluginProperties(strGbn+"_url").toString();
	        		if(urlName.substring(0, urlName.lastIndexOf(":")+1).length()==0){
	        			Encryptor oEncryptor = Encryptor.instance();
	        			urlName = oEncryptor.strGetDecrypt(ConfigFactory.getPluginProperties(strGbn+"_url")).toString();
	        			oEncryptor = null;
	        		}
	            	rst.put("value", urlName.substring(0, urlName.lastIndexOf(":")+1)+"****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_username");
	        	if(ConfigFactory.getPluginProperties(strGbn+"_username") == null || ConfigFactory.getPluginProperties(strGbn+"_username") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", "****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
	        	
	        	rst = new HashMap<String, String>();
	        	rst.put("gbn", strGbn+"_password");
	        	if(ConfigFactory.getPluginProperties(strGbn+"_password") == null || ConfigFactory.getPluginProperties(strGbn+"_password") == ""){
	        		rst.put("value", "");
	        	}else{
	        		rst.put("value", "****");
	        	}
	        	rtList.add(arrCnt++, rst);
	        	rst = null;
        	}
			//ecamsLogger.error(rtList.toString());
			
			return  rtList.toArray();
			
		} catch(Exception e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.getProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.getProperties() Exception END ##");	
        	return null;
        } finally{
        	if(rst != null) rst = null;
		}
    }
    
    public String setProperties(ArrayList<HashMap<String, String>> infoList) throws  Exception,IOException {
        Properties props = new Properties();
        
        try{
        	String path = "";
        	
        	if("O".equals(infoList.get(0).get("gbn").substring(0,1))){
        		ClassLoader cl = Thread.currentThread().getContextClassLoader();
	            if( cl == null ){
	                cl = ClassLoader.getSystemClassLoader();
	            }
	            
	            path = cl.getResource("DBInfo.properties").toString().split(":")[1];
	            
	            cl = null;
        	}else if("P".equals(infoList.get(0).get("gbn").substring(0,1))){
            	SystemPath systemPath = new SystemPath();
            	String basepath = systemPath.getTmpDir("P1");
            	path = basepath+"/conf/jdbc.properties";
//            	path = "C:\\jdbc.properties";
            	systemPath = null;
            }
        	

            FileInputStream fip = new FileInputStream(path);
            props.load(new BufferedInputStream(fip));
            
            Encryptor oEncryptor = Encryptor.instance();
            for(int i=0; i<infoList.size(); i++){
            	if(infoList.get(i).get("gbn").indexOf("_url")>0 || infoList.get(i).get("gbn").indexOf("_username")>0 || infoList.get(i).get("gbn").indexOf("_password")>0){
            		props.setProperty(infoList.get(i).get("gbn"), oEncryptor.strGetEncrypt(infoList.get(i).get("value")));
            	}else{
            		props.setProperty(infoList.get(i).get("gbn"), infoList.get(i).get("value"));
            	}
			}
            /*
             * Set<Object> set = props.keySet(); //프로퍼티 keySet
             * Iterator it = set.iterator();
             * while(it.hasNext()){
             * 	String temp = (String) it.next();
             * 	String temp2 = props.getProperty(temp); //프로퍼티 속성
             * }
             */
            
            props.store(new FileOutputStream(path), "");
			props = null;
            oEncryptor = null;
			
            fip.close();       	
        	fip = null;
        	
			return  "OK";

		} catch(IOException e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.setProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.setProperties() Exception END ##");	
        	return null;
        } catch(Exception e){
        	e.printStackTrace();
			ecamsLogger.error("## Cmm0700.setProperties() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", e);	
			ecamsLogger.error("## Cmm0700.setProperties() Exception END ##");	
        	return null;
        } finally{
        	
		}
	}

    public Object[] getNotiInfo1() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_notiuser,a.cm_smssend, \n");
			strQuery.append("       b.cm_userid,b.cm_username   \n");
			strQuery.append("  from cmm0040 b,cmm0010 a         \n");
			strQuery.append(" where a.cm_stno='ECAMS'           \n");
			strQuery.append("   and a.cm_notiuser=b.cm_userid(+)\n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	if (rs.getString("cm_smssend") != null) {
            		rst = new HashMap<String, String>();
    				rst.put("gbncd", "SMS");
    				rst.put("smstelno", rs.getString("cm_smssend"));
    				rsval.add(rst);
    				rst = null;
            	}
            	if (rs.getString("cm_notiuser") != null) {
	            	rst = new HashMap<String, String>();
					rst.put("gbncd", "NOTI");
					rst.put("cm_userid", rs.getString("cm_userid"));
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("cm_idname", rs.getString("cm_username")+"["+rs.getString("cm_userid")+"]");
					rsval.add(rst);
					rst = null;
            	}
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getNotiInfo1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getNotiInfo1() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getNotiInfo1() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getNotiInfo1() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getNotiInfo1() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getNotiInfo1() method statement


    public Object[] getNotiInfo2() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_qrycd,a.cm_notigbn,             \n");
			strQuery.append("       a.cm_holiday,a.cm_common,            \n");
			strQuery.append("       (select cm_codename from cmm0020     \n");
			strQuery.append("         where cm_macode='REQUEST'          \n");
			strQuery.append("           and cm_micode=a.cm_qrycd) qrycd, \n");
			strQuery.append("       (select cm_codename from cmm0020     \n");
			strQuery.append("         where cm_macode='NOTIGBN'          \n");
			strQuery.append("           and cm_micode=a.cm_notigbn) notigbn, \n");
			strQuery.append("       (select cm_codename from cmm0020     \n");
			strQuery.append("         where cm_macode='NOTICD'           \n");
			strQuery.append("           and cm_micode=a.cm_common) common, \n");
			strQuery.append("       (select cm_codename from cmm0020     \n");
			strQuery.append("         where cm_macode='NOTICD'           \n");
			strQuery.append("           and cm_micode=a.cm_holiday) holiday \n");
			strQuery.append("  from cmm0018 a                             \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("qrycd", rs.getString("qrycd"));
				rst.put("notigbn", rs.getString("notigbn"));
				rst.put("cm_qrycd", rs.getString("cm_qrycd"));
				rst.put("cm_notigbn", rs.getString("cm_notigbn"));
				rst.put("cm_holiday", rs.getString("cm_holiday"));
				rst.put("cm_common", rs.getString("cm_common"));
				rst.put("common", rs.getString("common"));
				rst.put("holiday", rs.getString("holiday"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getNotiInfo2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getNotiInfo2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getNotiInfo2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getNotiInfo2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getNotiInfo2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getNotiInfo1() method statement
    public String delNotiInfo(String UserId,ArrayList<HashMap<String, String>> delList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			for (int i=0;delList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0018      \n");
				strQuery.append(" where cm_qrycd=?   \n");
				strQuery.append("   and cm_notigbn=? \n");	
				pstmt = conn.prepareStatement(strQuery.toString());	
				pstmt.setString(1, delList.get(i).get("cm_qrycd"));
				pstmt.setString(2, delList.get(i).get("cm_notigbn"));
	            pstmt.executeUpdate();
	            pstmt.close();
			}
            conn.close();
			conn = null;
			pstmt = null;

            return "0";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delNotiInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.delNotiInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.delNotiInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.delNotiInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.delNotiInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delTab4Info() method statement


    public String addNotiInfo1(String UserId,String smssend,String notiuser) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmm0010         \n");
			strQuery.append("   set cm_smssend=?,   \n");
			strQuery.append("       cm_notiuser=?   \n");
			strQuery.append(" where cm_stno='ECAMS' \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, smssend);
			pstmt.setString(2, notiuser);
			pstmt.executeUpdate();
			pstmt.close();
			
            conn.close();
			conn = null;
			pstmt = null;

            return "0";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addNotiInfo1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addNotiInfo1() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addNotiInfo1() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addNotiInfo1() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addNotiInfo1() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addNotiInfo1() method statement

    public String addNotiInfo2(String UserId,String qrycd,String notigbn,String common,String holiday) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String[]          qrylist     = null;
		String[]          notilist    = null;
		int               i = 0;
		int               j = 0;

		try {
			conn = connectionContext.getConnection();
			
			qrylist = qrycd.split(",");
			notilist = notigbn.split(",");
			
			for (i=0;qrylist.length>i;i++) {
				for (j=0;notilist.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0018        \n");
					strQuery.append(" where cm_qrycd=?     \n");
					strQuery.append("   and cm_notigbn=?   \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, qrylist[i]);
					pstmt.setString(2, notilist[j]);
					pstmt.executeUpdate();
					pstmt.close();
					
					strQuery.setLength(0);
					strQuery.append("insert into cmm0018     \n");
					strQuery.append("  (cm_qrycd,cm_notigbn,cm_common,cm_holiday,cm_editor,cm_lastdt) \n");
					strQuery.append("values                  \n");
					strQuery.append("  (?,?,?,?,?,SYSDATE)   \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, qrylist[i]);
					pstmt.setString(2, notilist[j]);
					pstmt.setString(3, common);
					pstmt.setString(4, holiday);
					pstmt.setString(5, UserId);
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
            conn.close();
			conn = null;
			pstmt = null;

            return "0";
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addNotiInfo2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.addNotiInfo2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.addNotiInfo2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.addNotiInfo2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.addNotiInfo2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of addNotiInfo2() method statement

    public Object[] getSrCattype(String cattype) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_cattype, a.cm_qrycd, a.cm_useyn,   \n");
			strQuery.append("       b.cm_codename name1, c.cm_codename name2\n");
			strQuery.append("  from cmm0120 a, cmm0020 b, cmm0020 c  		\n");
			strQuery.append(" where a.cm_cattype = ?  						\n");
			strQuery.append("   and b.cm_macode = 'CATTYPE'  				\n");
			strQuery.append("   and b.cm_micode = a.cm_cattype  			\n");
			strQuery.append("   and b.cm_closedt is null  					\n");
			strQuery.append("   and c.cm_macode = 'REQUEST'  				\n");
			strQuery.append("   and c.cm_micode = a.cm_qrycd  				\n");
			strQuery.append("   and c.cm_closedt is null                    \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, cattype);
            rs = pstmt.executeQuery();

            rsval.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_cattype", rs.getString("cm_cattype"));
				rst.put("cm_qrycd", rs.getString("cm_qrycd"));
				rst.put("cm_useyn", rs.getString("cm_useyn"));
				rst.put("name1", rs.getString("name1"));
				rst.put("name2", rs.getString("name2"));
				rsval.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getSrCattype() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.getSrCattype() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.getSrCattype() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.getSrCattype() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rst != null)	rst = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.getSrCattype() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSrCattype() method statement
    
    
    public int setSrCattype(ArrayList<HashMap<String, String>> typeList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			int retCnt = 0;
			
			strQuery.setLength(0);
			strQuery.append("delete from cmm0120 where cm_cattype = ? ");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, typeList.get(0).get("cm_cattype"));
            pstmt.executeUpdate();
            pstmt.close();
            
			int i = 0;
			for(i=0; i<typeList.size(); i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmm0120 (cm_cattype, cm_qrycd, cm_useyn) \n");
				strQuery.append("values (?, ?, ?) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, typeList.get(0).get("cm_cattype"));
				pstmt.setString(2, typeList.get(i).get("cm_qrycd"));
				pstmt.setString(3, typeList.get(i).get("cm_useyn"));
				retCnt = retCnt + pstmt.executeUpdate();
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.close();
			}
			
            conn.close();
			conn = null;
			pstmt = null;

            return retCnt;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0700.setSrCattype() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0700.setSrCattype() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0700.setSrCattype() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0700.setSrCattype() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0700.setSrCattype() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setSrCattype() method statement
}//end of Cmm0700 class statement
