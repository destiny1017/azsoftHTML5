/*****************************************************************************************
	1. program ID	: Cmm2900.java
	2. create date	: 2011.05. 09
	3. auth		    : no name
	4. update date	: 
	5. auth		    : 
	6. description	: 농협중앙회 파일대사
*****************************************************************************************/

package app.eCmm;

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
import app.common.LoggableStatement;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm2900{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 시스템 조회(String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd)
	 * @param  String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	
	/**
	 * @param UserId
	 * @param SecuYn
	 * @param cboJobRuncd
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getFileInf(String UserId,String SecuYn,String cboJobRuncd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_syscd,cm_lastdt \n");
			strQuery.append("  from cmm0026 \n");
			strQuery.append(" where cm_stno = 'ECAMS' \n");
			strQuery.append("   and cm_runcd = ? \n");
			strQuery.append("   and cm_existyn = 'Y' \n");
			strQuery.append("order by cm_syscd \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, cboJobRuncd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();            
            String sysList = "";
			while (rs.next()){
				if (sysList.length() == 0) sysList = rs.getString("cm_syscd");
				else sysList = sysList + "," + rs.getString("cm_syscd");
			}
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("select cm_runterm,cm_runtermcd,cm_runtime,cm_rundate,cm_delterm, \n");
			strQuery.append("       cm_deltermcd,cm_noexename \n");
			strQuery.append("  from cmm0028                   \n");
			strQuery.append(" where cm_stno = 'ECAMS'         \n");
			strQuery.append("   and cm_runcd = ?              \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, cboJobRuncd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
            
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_runterm",rs.getString("cm_runterm"));
				rst.put("cm_runtermcd",rs.getString("cm_runtermcd"));
				rst.put("cm_runtime",rs.getString("cm_runtime"));
				rst.put("cm_rundate",rs.getString("cm_rundate"));
				rst.put("cm_delterm",rs.getString("cm_delterm"));
				rst.put("cm_deltermcd",rs.getString("cm_deltermcd"));
				rst.put("cm_syslist", sysList);
				rtList.add(rst);
				rst = null;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSysInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSysInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSysInfo() method statement
	
	
	/**
	 * @param UserId
	 * @param SecuYn
	 * @param etcData
	 * @return Boolean
	 * @throws SQLException
	 * @throws Exception
	 */
	public Boolean setFileInf(String UserId,String SecuYn,HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			boolean           tmpBoolean  = false;
			int               parmCnt     = 0;
			int               i           = 0;
			boolean           insSw       = false;
			///////////////////////////////////////
			//  cmm0028 테이블에  insert or update //
			///////////////////////////////////////
			strQuery.setLength(0);
			strQuery.append("select cm_runcd        \n");
			strQuery.append("  from cmm0028         \n");
			strQuery.append(" where cm_stno='ECAMS' \n");
			strQuery.append("   and cm_runcd=?      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_runcd"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				tmpBoolean = true;
			}
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			parmCnt = 0;
			if (tmpBoolean){//RUNCD 존재하면 update
				strQuery.append("update cmm0028 set                \n");             
				strQuery.append("   CM_RUNTERM=?,CM_RUNTERMCD=?,   \n");          
				strQuery.append("   CM_RUNTIME=?,CM_DELTERM=?,     \n");          
				strQuery.append("   CM_DELTERMCD=?                 \n");             
				strQuery.append(" where cm_stno='ECAMS' \n");
				strQuery.append("   and cm_runcd=? \n");
			}else{//RUNCD 존재하지 않으면 insert
				strQuery.append("insert into cmm0028                     \n");
				strQuery.append("  (CM_STNO,CM_RUNTERM,CM_RUNTERMCD,CM_RUNTIME, \n");
				strQuery.append("   CM_DELTERM,CM_DELTERMCD,CM_RUNCD) \n");
				strQuery.append(" values ('ECAMS',?,?,?,  ?,?,?) \n");
			}
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, etcData.get("cm_runterm"));
			pstmt.setString(++parmCnt, etcData.get("cm_runtermcd"));
			pstmt.setString(++parmCnt, etcData.get("cm_runtime"));
			pstmt.setString(++parmCnt, etcData.get("cm_delterm"));
			pstmt.setString(++parmCnt, etcData.get("cm_deltermcd"));
			pstmt.setString(++parmCnt, etcData.get("cm_runcd"));
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			
			///////////////////////////////////////
			//  cmm0026 테이블에 예외시스템 insert  //
			///////////////////////////////////////
			strQuery.setLength(0);
			strQuery.append("UPDATE cmm0026         \n");
			strQuery.append("   SET CM_EXISTYN='N'  \n");
			strQuery.append(" where cm_stno='ECAMS' \n");
			strQuery.append("   and cm_runcd=?      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_runcd"));
			pstmt.executeUpdate();
			pstmt.close();

			String tmpSyslist[] = etcData.get("cm_syslist").split(",");
			
			for (i=0 ; i<tmpSyslist.length ; i++){
				if (tmpSyslist[i] != "" && tmpSyslist[i] != null){
					insSw = false;
					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmm0026  \n");
					strQuery.append(" where cm_stno='ECAMS'            \n");
					strQuery.append("   and cm_runcd=?                 \n");
					strQuery.append("   and cm_syscd=?                 \n");
					pstmt = conn.prepareStatement(strQuery.toString());

					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_runcd"));
					pstmt.setString(++parmCnt, tmpSyslist[i]);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getInt("cnt")==0) insSw = true;
					}
					rs.close();
					pstmt.close();
					
					strQuery.setLength(0);
					parmCnt = 0;
					if (insSw == true) {
						strQuery.append("insert into cmm0026                     \n");
						strQuery.append("  (CM_STNO,CM_SYSCD,CM_LASTDT,CM_RUNCD) \n");
						strQuery.append(" values ('ECAMS',?,SYSDATE,?) \n");
					} else {
						strQuery.append("update cmm0026                 \n");
						strQuery.append("   set cm_existyn='Y',         \n");
						strQuery.append("       cm_lastdt=SYSDATE       \n");
						strQuery.append(" where cm_stno='ECAMS'         \n");
						strQuery.append("   and cm_syscd=?              \n");
						strQuery.append("   and cm_runcd=?              \n");
					}
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, tmpSyslist[i]);
					pstmt.setString(++parmCnt, etcData.get("cm_runcd"));
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return true;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setFileInf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.setFileInf() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setFileInf() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.setFileInf() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.setFileInf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getSysBase_diff(String UserId,String SecuYn,String JobRuncd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,b.cm_svrname,b.cm_svrip,  \n");
			strQuery.append("       c.cm_exename,c.cm_basedircd,c.cm_execd \n");
			strQuery.append("  from cmm0027 c,cmm0026 d,cmm0031 b,cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd         \n");
			strQuery.append("   and b.cm_closedt is null          \n");
			strQuery.append("   and b.cm_svrcd='05'               \n");
			strQuery.append("   and c.cm_stno = 'ECAMS'           \n");
			strQuery.append("   and c.cm_runcd=?                  \n");
			strQuery.append("   and b.cm_syscd=c.cm_syscd         \n");
			strQuery.append("   and b.cm_svrip=c.cm_svrip         \n");
			strQuery.append("   and c.cm_stno=d.cm_stno           \n");
			strQuery.append("   and c.cm_runcd=d.cm_runcd         \n");
			strQuery.append("   and d.cm_existyn='N'              \n");
			strQuery.append(" union                               \n");
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,b.cm_svrname,b.cm_svrip, \n");
			strQuery.append("       '' cm_exename,'' cm_basedircd,'' cm_execd \n");
			strQuery.append("  from cmm0031 b,cmm0030 a           \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			strQuery.append("   and a.cm_syscd not in (select cm_syscd from cmm0026 \n");
			strQuery.append("                           where cm_stno='ECAMS'       \n");
			strQuery.append("                             and cm_runcd=?            \n");
			strQuery.append("                             and cm_existyn='Y')       \n");
			strQuery.append("   and b.cm_syscd || b.cm_svrip not in (select cm_syscd || cm_svrip  \n");
			strQuery.append("                                          from cmm0027               \n");
			strQuery.append("                                         where cm_stno='ECAMS'       \n");
			strQuery.append("                                           and cm_runcd=?)           \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd         \n");
			strQuery.append("   and b.cm_closedt is null          \n");
			strQuery.append("   and b.cm_svrcd='05'               \n");
			strQuery.append("order by cm_sysmsg,cm_svrip          \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, JobRuncd);
           	pstmt.setString(2, JobRuncd);
           	pstmt.setString(3, JobRuncd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("svrname",rs.getString("cm_svrname")+"["+rs.getString("cm_svrip")+"]");
				rst.put("cm_execd",rs.getString("cm_execd"));
				rst.put("cm_basedircd",rs.getString("cm_basedircd"));
				if (rs.getString("cm_execd") != null && rs.getString("cm_execd") != "") {
					if (!rs.getString("cm_execd").equals("X")) {
						rst.put("cm_exename",rs.getString("cm_exename"));
					}
					if (rs.getString("cm_execd").equals("X")) rst.put("execdname", "모든확장자");
					else if (rs.getString("cm_execd").equals("C")) rst.put("execdname", "등록관리확장자");
					else if (rs.getString("cm_execd").equals("E")) rst.put("execdname", "대상확장자관리");
					else rst.put("execdname", "제외확장자관리");
				}
				if (rs.getString("cm_basedircd") != null && rs.getString("cm_basedircd") != "") {
					if (rs.getString("cm_basedircd").equals("C")) rst.put("basedirname", "등록관리기준");
					else rst.put("basedirname", "홈디렉토리기준");
				}
				rst.put("checked", "true");
				rst.put("visible", "true");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSysBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSysBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSysBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSysBase_diff() method statement
	public Object[] getDirBase_diff(String UserId,String SecuYn,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,b.cm_svrname,b.cm_svrip,  \n");
			strQuery.append("       c.cm_dirpath                  \n");
			strQuery.append("  from cmm0074 c,cmm0031 b,cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			if (SysCd != null && SysCd != "") {
				strQuery.append("and a.cm_syscd=?                 \n");
			}
			strQuery.append("   and a.cm_syscd=b.cm_syscd         \n");
			strQuery.append("   and b.cm_closedt is null          \n");
			strQuery.append("   and b.cm_svrcd='05'               \n");
			strQuery.append("   and b.cm_syscd=c.cm_syscd         \n");
			strQuery.append("   and b.cm_svrip=c.cm_svrip         \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("svrname",rs.getString("cm_svrname")+"["+rs.getString("cm_svrip")+"]");
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("visible", "true");
				rst.put("check", "true");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSysBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSysBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSysBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSysBase_diff() method statement

	public Object[] getSysDir(String UserId,String dirPath) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,c.cm_dsncd,c.cm_dirpath    \n");
			strQuery.append("  from cmm0070 c,cmm0030 a           \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd         \n");
			strQuery.append("   and c.cm_clsdt is null            \n");
			if (dirPath != null && dirPath != "") {
				strQuery.append("   and c.cm_dirpath like ?       \n");
			}
			strQuery.append(" minus       \n");
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,d.cm_dsncd,d.cm_dirpath  \n");
			strQuery.append("  from cmm0070 d,cmm0029 c,cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			//strQuery.append("   and a.cm_syscd=c.cm_syscd         \n");
			strQuery.append("    and d.cm_syscd = a.cm_syscd         \n");
			strQuery.append("    and d.cm_syscd=c.cm_syscd         \n");
			strQuery.append("    and d.cm_dsncd = c.cm_dsncd         \n");
			strQuery.append("   and c.cm_stno='ECAMS'             \n");
			strQuery.append("   and c.cm_gbncd='01'               \n");
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            if (dirPath != null && dirPath != "") pstmt.setString(1, "%"+dirPath+"%");
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_dsncd",rs.getString("cm_dsncd"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("visible", "true");
				rst.put("check", "true");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSysDir() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysDir() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSysDir() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSysDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSysDir() method statement
	public Object[] getEtcDir(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,d.cm_dsncd,d.cm_dirpath    \n");
			strQuery.append("  from cmm0070 d,cmm0029 c,cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd         \n");
			strQuery.append("   and c.cm_stno='ECAMS'             \n");
			strQuery.append("   and c.cm_gbncd='01'               \n");
			strQuery.append("   and c.cm_syscd=d.cm_syscd         \n");
			strQuery.append("   and c.cm_dsncd=d.cm_dsncd         \n");
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_dsncd",rs.getString("cm_dsncd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("visible", "true");
				rst.put("check", "true");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getEtcDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getEtcDir() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getEtcDir() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getEtcDir() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getEtcDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getEtcDir() method statement
	public String setEtcDir(String UserId,String gbnCd,ArrayList<HashMap<String, String>> dirList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            rtMsg       = "OK";
		try {
			
			conn = connectionContext.getConnection();
			for (int i=0;dirList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0029 where cm_stno='ECAMS' and cm_gbncd='01' \n");
				strQuery.append("   and cm_syscd=? and cm_dsncd=?  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, dirList.get(i).get("cm_syscd"));
				pstmt.setString(2, dirList.get(i).get("cm_dsncd"));
				pstmt.executeUpdate();
				pstmt.close();
				
				if (gbnCd.equals("I")) {
					strQuery.setLength(0);
					strQuery.append("insert into cmm0029   \n");
					strQuery.append(" (cm_stno,cm_gbncd,cm_syscd,cm_dsncd)  \n");
					strQuery.append(" values ('ECAMS', '01', ?, ?) \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, dirList.get(i).get("cm_syscd"));
					pstmt.setString(2, dirList.get(i).get("cm_dsncd"));
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return rtMsg;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSysDir() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSysDir() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSysDir() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSysBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSysDir() method statement

	public Object[] getHandrun_diff(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select to_char(a.cd_lastdt,'yyyy/mm/dd hh24:mi') lastdt,  \n");
			strQuery.append("       a.cd_sysday,a.cd_seqno,a.cd_syscd,a.cd_svrip,      \n");
			strQuery.append("       a.cd_diffday,'전체' cm_sysmsg \n");
			strQuery.append("  from cmd0025 a                     \n");
			strQuery.append(" where a.cd_status<>'9'              \n");
			strQuery.append("   and a.cd_syscd='ALL'              \n");
			strQuery.append(" union  \n");
			strQuery.append("select to_char(a.cd_lastdt,'yyyy/mm/dd hh24:mi') lastdt,  \n");
			strQuery.append("       a.cd_sysday,a.cd_seqno,a.cd_syscd,a.cd_svrip,      \n");
			strQuery.append("       a.cd_diffday,b.cm_sysmsg      \n");
			strQuery.append("  from cmd0025 a,cmm0030 b           \n");
			strQuery.append(" where a.cd_status<>'9'              \n");
			strQuery.append("   and a.cd_syscd<>'ALL'             \n");
			strQuery.append("   and a.cd_syscd=b.cm_syscd         \n");
			strQuery.append(" order by lastdt                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("cd_sysday",rs.getString("cd_sysday"));
				rst.put("cd_seqno",rs.getString("cd_seqno"));
				rst.put("sysmsg",rs.getString("cm_sysmsg"));
				if (rs.getString("cd_diffday") == null) rst.put("diffday", "즉시실행"); 
				else rst.put("diffday",rs.getString("cd_diffday").substring(0,4)+"/"+rs.getString("cd_diffday").substring(4,6)+"/"+rs.getString("cd_diffday").substring(6,8)
						       +" "+rs.getString("cd_diffday").substring(8,10)+":"+rs.getString("cd_diffday").substring(10,12));
				if (rs.getString("cd_svrip").equals("ALL")) rst.put("svrip","전체");
				else rst.put("svrip",rs.getString("cd_svrip"));
				rst.put("visible", "true");
				rst.put("check", "true");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getHandrun_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getHandrun_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getHandrun_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getHandrun_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getHandrun_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getHandrun_diff() method statement
	public String setHandrun_diff(String UserId,String gbnCd,HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            rtMsg       = "OK";
		try {
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("insert into cmd0025     \n");
			strQuery.append("(CD_SYSDAY,CD_SEQNO,CD_SYSCD,CD_SVRIP,CD_DIFFDAY,CD_LASTDT,CD_STATUS) \n");
			strQuery.append("(select to_char(SYSDATE,'yyyymmdd'),nvl(max(cd_seqno),0)+1,?,?,?,SYSDATE,'0' \n");
			strQuery.append("   from cmd0025        \n");
			strQuery.append("  where cd_sysday=to_char(SYSDATE,'yyyymmdd')) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("syscd"));
			pstmt.setString(2, etcData.get("svrip"));
			pstmt.setString(3, etcData.get("diffday"));
			pstmt.executeUpdate();
			pstmt.close();
			
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return rtMsg;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setHandrun_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.setHandrun_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setHandrun_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.setHandrun_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.setHandrun_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of setHandrun_diff() method statement

	public String delHandrun_diff(String UserId,ArrayList<HashMap<String, String>> dirList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            rtMsg       = "OK";
		try {
			
			conn = connectionContext.getConnection();
			for (int i=0;dirList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmd0025 where cd_sysday=? and cd_seqno=? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, dirList.get(i).get("cd_sysday"));
				pstmt.setString(2, dirList.get(i).get("cd_seqno"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return rtMsg;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.delHandrun_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.delHandrun_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.delHandrun_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.delHandrun_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.delHandrun_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of delHandrun_diff() method statement
	public Object[] getSvrBase_diff(String UserId,String SecuYn,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,b.cm_svrname,b.cm_svrip  \n");
			strQuery.append("  from cmm0031 b,cmm0030 a \n");
			strQuery.append(" where a.cm_closedt is null          \n");
			strQuery.append("   and a.cm_syscd=?                 \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd         \n");
			strQuery.append("   and b.cm_closedt is null          \n");
			strQuery.append("   and b.cm_svrcd='05'               \n");
			
            pstmt = conn.prepareStatement(strQuery.toString());	
            //pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();  
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_svrip",rs.getString("cm_svrip"));
				rst.put("svrname",rs.getString("cm_svrname")+"["+rs.getString("cm_svrip")+"]");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSvrBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.getSvrBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.getSvrBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.getSvrBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.getSvrBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}	
	}//end of getSvrBase_diff() method statement


	public Boolean setSysBase_diff(HashMap<String, String> etcData,ArrayList<HashMap<String, String>> dataList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			int               parmCnt     = 0;
			
			for (int i=0;dataList.size()>i;i++) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("delete cmm0027          \n");
				strQuery.append(" where cm_stno='ECAMS'  \n");
				strQuery.append("   and cm_runcd='01'    \n");
				strQuery.append("   and cm_syscd=?       \n");
				strQuery.append("   and cm_svrip=?       \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_syscd"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_svrip"));
				pstmt.executeUpdate();
				pstmt.close();
				
				strQuery.setLength(0);
				parmCnt = 0;
				strQuery.append("insert into cmm0027    \n");
				strQuery.append("  (CM_STNO,CM_RUNCD,CM_SYSCD,CM_SVRIP,CM_LASTDT, \n");
				strQuery.append("   CM_EXENAME,CM_BASEDIRCD,CM_EXECD,CM_EXISTYN)  \n");
				strQuery.append(" values ('ECAMS', '01', ?, ?, SYSDATE, ?, ?, ?,'N') \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_syscd"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_svrip"));
				pstmt.setString(++parmCnt, etcData.get("CM_EXENAME"));
				pstmt.setString(++parmCnt, etcData.get("CM_BASEDIRCD"));
				pstmt.setString(++parmCnt, etcData.get("CM_EXECD"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return true;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setSysBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.setSysBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setSysBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.setSysBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.setSysBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Boolean delSysBase_diff(HashMap<String, String> etcData,ArrayList<HashMap<String, String>> dataList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			int               parmCnt     = 0;
			
			for (int i=0;dataList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0027          \n");
				strQuery.append(" where cm_stno='ECAMS'  \n");
				strQuery.append("   and cm_runcd=?       \n");
				strQuery.append("   and cm_syscd=?       \n");
				strQuery.append("   and cm_svrip=?       \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_micode"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_syscd"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_svrip"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return true;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.delSysBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.delSysBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.delSysBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.delSysBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.delSysBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Boolean setDirBase_diff(HashMap<String, String> etcData,ArrayList<HashMap<String, String>> dataList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			int               parmCnt     = 0;
			boolean           insSw       = false;
			
			for (int i=0;dataList.size()>i;i++) {
				parmCnt = 0;
				insSw = true;
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt     \n");
				strQuery.append("  from cmm0074          \n");
				strQuery.append(" where cm_syscd=?       \n");
				strQuery.append("   and cm_svrip=?       \n");
				strQuery.append("   and cm_dirpath=?     \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_svrip"));
				pstmt.setString(++parmCnt, etcData.get("cm_dirpath"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					if (rs.getInt("cnt")>0) insSw = false;
				}
				pstmt.close();
				rs.close();
				
				strQuery.setLength(0);
				parmCnt = 0;
				strQuery.append("insert into cmm0074      \n");
				strQuery.append("  (CM_SYSCD,CM_DIRPATH,CM_SVRIP,CM_SVRCD) \n");
				strQuery.append(" values (?, ?, ?, '05')  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, dataList.get(i).get("cm_svrip"));
				pstmt.setString(++parmCnt, etcData.get("cm_dirpath"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return true;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setDirBase_diff() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm2900.setDirBase_diff() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2900.setDirBase_diff() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm2900.setDirBase_diff() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2900.setDirBase_diff() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}//end of Cmm2900 class statement

