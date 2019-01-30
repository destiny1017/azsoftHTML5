
/*****************************************************************************************
	1. program ID	: Cmr3200.java
	2. create date	: 2008.04.08
	3. auth		    : m.s.kang
	4. update date	: 
	5. auth		    : 
	6. description	: Request List
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.common.LoggableStatement;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
import app.eCmr.Cmr0250;

/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd1400{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 신청한 프로그램정보 조회
	 * @param  String ItemId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getProgInfo(String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			
			rtList.clear();
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT d.cm_sysmsg,b.cm_dirpath,a.cr_rsrcname,a.cr_story, \n");
			strQuery.append("       a.cr_syscd,a.cr_rsrccd,a.cr_jobcd,c.cm_info        \n");
			strQuery.append("  from cmr0020 a,cmm0070 b,cmm0036 c,cmm0030 d            \n");
			strQuery.append(" where a.cr_itemid=?                                      \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
			strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_rsrccd=c.cm_rsrccd  \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd                              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, ItemId);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()){			
	            rst = new HashMap<String, String>();
        		rst.put("cm_dirpath", rs.getString("cm_dirpath"));  //경로
        		rst.put("cr_rsrcname",rs.getString("cr_rsrcname")); //프로그램명
        		if (rs.getString("cr_story") != null) rst.put("cr_story",rs.getString("cr_story")); //프로그램설명
        		rst.put("cm_info",rs.getString("cm_info"));         //팩터
        		rst.put("cr_syscd",rs.getString("cr_syscd"));       //시스템
        		rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));     //프로그램종류
        		rst.put("cr_jobcd",rs.getString("cr_jobcd"));       //업무
        		rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));     //시스템명
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
			rtList.clear();
			rtList = null;
			
			return rtObj;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getProgInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1400.getProgInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getProgInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1400.getProgInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;				
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1400.getProgInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgInfo() method statement
    
    
	/**
	 * 처리구분 조회
	 * @param  String AcptNo
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getPrcSysInfo(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			rtList.clear();
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select b.cm_codename,d.cm_syscd,d.cm_rsrccd,d.cm_jobcd,d.cm_prcsys,d.cm_bldgbn,d.cm_bldcd  \n");
			strQuery.append("  from cmr9900 a, cmm0020 b, cmm0033 d, cmr1000 e,        \n");
			strQuery.append("       (select distinct cr_acptno, cr_syscd, cr_jobcd,cr_rsrccd \n");
			strQuery.append("          from cmr1010 where cr_acptno=?) c \n");
			strQuery.append(" where a.cr_acptno=?                                      \n");
			strQuery.append("   and a.cr_teamcd='1' and b.cm_macode='SYSGBN' and a.cr_team=d.cm_prcsys \n");
			strQuery.append("   and a.cr_team=b.cm_micode and b.cm_micode=d.cm_prcsys \n");
			strQuery.append("   and a.cr_acptno=c.cr_acptno and c.cr_syscd=d.cm_syscd and c.cr_jobcd=d.cm_jobcd \n");
			strQuery.append("   and c.cr_rsrccd=d.cm_rsrccd and a.cr_acptno=e.cr_acptno and d.cm_qrycd=e.cr_qrycd \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, AcptNo);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()){			
	            rst = new HashMap<String, String>();
        		rst.put("cm_codename", rs.getString("cm_codename"));
        		rst.put("cm_syscd",rs.getString("cm_syscd"));
         		rst.put("cm_rsrccd",rs.getString("cm_rsrccd"));
        		rst.put("cm_jobcd",rs.getString("cm_jobcd"));
        		rst.put("cm_prcsys",rs.getString("cm_prcsys"));
        		rst.put("cm_bldgbn",rs.getString("cm_bldgbn"));
        		rst.put("cm_bldcd",rs.getString("cm_bldcd"));
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
			ecamsLogger.error("## Cmd1400.getPrcSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1400.getPrcSysInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getPrcSysInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1400.getPrcSysInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;				
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1400.getPrcSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrcSysInfo() method statement
    
    
	/**
	 * 처리스크립트 조회
	 * @param  String SysCd,String RsrcCd,String JobCd
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getBldList(String SysCd,String RsrcCd,String JobCd,String QryCd,String ItemId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();	
			strQuery.setLength(0);
			strQuery.append("SELECT a.CM_QRYCD,a.CM_PRCSYS,a.CM_BLDGBN,a.CM_BLDCD, \n");
			strQuery.append("       d.cm_codename,e.cm_codename sysgbn,a.cm_rungbn,\n");
			strQuery.append("       a.cm_runpos,a.cm_totyn,a.cm_seqyn,cm_useryn,a.cm_noexecyn \n");
			strQuery.append("  from cmm0033 a,cmm0030 b,cmm0036 c,cmm0020 d,cmm0020 e \n");
			strQuery.append(" where b.cm_syscd=?                                   \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and a.cm_rsrccd=?        \n");
			strQuery.append("   and a.cm_qrycd=?                                   \n");
			strQuery.append("   and  a.cm_jobcd=decode(substr(b.cm_sysinfo,8,1),'1',?,a.cm_jobcd) \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd                          \n");
			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd                        \n");
			strQuery.append("   and a.cm_itemid=decode(substr(c.cm_info,29,1),'1',?,a.cm_itemid)  \n");
			strQuery.append("   and d.cm_macode='BLDGBN' and d.cm_micode=a.cm_bldgbn \n");
			strQuery.append("   and e.cm_macode='SYSGBN' and e.cm_micode=a.cm_prcsys \n");
			strQuery.append("   and a.cm_noexecyn='N'                               \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			pstmt.setString(3, QryCd);
			pstmt.setString(4, JobCd);
			pstmt.setString(5, ItemId);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			while (rs.next()){			
	            rst = new HashMap<String, String>();
        		rst.put("cm_micode", rs.getString("CM_BLDGBN"));   
        		if (rs.getString("cm_rungbn").equals("A")) {
        			rst.put("cm_codename",rs.getString("sysgbn")+" [파일송신후 실행]");
        		} else {
        			rst.put("cm_codename",rs.getString("sysgbn")+" [파일송신전 실행]");
        		}
        		rst.put("cm_bldcd",rs.getString("cm_bldcd"));
        		rst.put("cm_prcsys",rs.getString("CM_PRCSYS"));
        		rst.put("cm_runpos",rs.getString("cm_runpos"));
        		rst.put("cm_runpos",rs.getString("cm_runpos"));
        		rst.put("cm_seqyn",rs.getString("cm_seqyn"));
        		rst.put("cm_totyn",rs.getString("cm_totyn"));
        		rst.put("cm_useryn",rs.getString("cm_useryn"));
        		rst.put("cm_execyn", "Y");
        		
        		if (rs.getString("cm_useryn").equals("Y")) {
        			strQuery.setLength(0);
        			strQuery.append("select count(*) cnt from cmr1060 \n");        		     
					strQuery.append(" WHERE CR_ACPTNO=?            \n");
					strQuery.append("   AND CR_SERNO=(select cr_serno from cmr1010  \n");
					strQuery.append("                  where cr_acptno=?            \n");
					strQuery.append("                    and cr_itemid=?)           \n");
					strQuery.append("   AND CR_PRCSYS=?                             \n");
					strQuery.append("   AND CR_BLDCD=?                              \n");
					strQuery.append("   AND CR_BLDGBN=?                             \n");
					strQuery.append("   AND CR_runyn='N'                            \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				    pstmt2.setString(1, AcptNo);
				    pstmt2.setString(2, AcptNo);
				    pstmt2.setString(3, ItemId);
				    pstmt2.setString(4, rs.getString("CM_PRCSYS"));
				    pstmt2.setString(5, rs.getString("cm_bldcd"));
				    pstmt2.setString(6, rs.getString("CM_BLDGBN"));
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				    rs2 = pstmt2.executeQuery();
				    if (rs2.next()) {
				    	if (rs2.getInt("cnt")>0) rst.put("cm_execyn", "N");
				    	else rst.put("cm_execyn", "Y");
				    }
				    rs2.close();
				    pstmt2.close();
        		
        		}
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
			ecamsLogger.error("## Cmd1400.getBldList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1400.getBldList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getBldList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1400.getBldList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1400.getBldList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getBldList() method statement
    
    
	/**
	 * 처리스크립트 작성
	 * @param  String SysCd,String RsrcCd,String JobCd,String RsrcName,String DirPath,
    		   String BldGbn,String BldCd
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getBldScript(String AcptNo,String SysCd,String RsrcCd,String JobCd,String RsrcName,String DirPath,
    	String BldGbn,String BldCd,String ItemId,String PrcSys) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        String           strScript    = "";
        String           strRstCmd    = "";
        String           strWork1     = "";
        String           strWork2     = "";
        String           strWork3     = "";
        String           strHome      = "";
        int              wkPos        = 0;
        int              svCnt        = 0;
        int              rstCnt       = 0;
        int              wrtCnt       = 0;
        String           strSvrCD     = "";
        int              svrSeq       = 0;
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			
			rtList.clear();
			
			conn = connectionContext.getConnection();
			
			Cmr0250 cmr0250 = new Cmr0250();
			strQuery.setLength(0);
			strQuery.append("select b.cm_svrcd,b.cm_seqno            \n");
			strQuery.append("  from cmm0038 c,cmm0031 b,cmr1010 a    \n");
			strQuery.append(" where a.cr_acptno=? and a.cr_itemid=?  \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd            \n");
			strQuery.append("   and b.cm_svrcd=?                     \n");
			strQuery.append("   and b.cm_closedt is null             \n");
			strQuery.append("   and b.cm_syscd=c.cm_syscd            \n");
			strQuery.append("   and b.cm_svrcd=c.cm_svrcd            \n");
			strQuery.append("   and b.cm_seqno=c.cm_seqno            \n");
			strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd          \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, ItemId);
			if (AcptNo.substring(4,6).equals("07")) {
				if (PrcSys.equals("SYSUP")) strSvrCD = "01";
				else if (PrcSys.equals("SYSCB")) strSvrCD = "23";
				else if (PrcSys.equals("SYSED")) strSvrCD = "25";
				else strSvrCD = "01";
			} else if (AcptNo.substring(4,6).equals("04")) {
				if (PrcSys.equals("SYSUP")) strSvrCD = "01";
				else if (PrcSys.equals("SYSCB")) strSvrCD = "03";
				else if (PrcSys.equals("SYSED")) strSvrCD = "05";
				else strSvrCD = "01";
			} else if (AcptNo.substring(4,6).equals("03")) {
				if (PrcSys.equals("SYSUP")) strSvrCD = "01";
				else if (PrcSys.equals("SYSCB")) strSvrCD = "13";
				else if (PrcSys.equals("SYSED")) strSvrCD = "15";
				else strSvrCD = "01";
			} else strSvrCD = "01";
			pstmt.setString(3, strSvrCD);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				svrSeq = rs.getInt("cm_seqno");
			}
			rs.close();
			pstmt.close();
			
			
			String chgPath = cmr0250.chgVolPath(SysCd,DirPath,strSvrCD,RsrcCd,svrSeq,JobCd,conn);
			
			if (chgPath.length() == 0) chgPath = DirPath;
			
			strQuery.setLength(0);
			strQuery.append("SELECT CM_SEQ,CM_CMDNAME,CM_GBNCD                \n");
			strQuery.append("  from cmm0022                                   \n");
			strQuery.append(" where cm_gbncd=? and cm_bldcd=?                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, BldGbn);
			pstmt.setString(2, BldCd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();            
			while (rs.next()){
				strRstCmd = "";
				strScript = rs.getString("cm_cmdname");
				strRstCmd = strScript;
				strWork1 = strScript;
				if (strScript.indexOf("?#") >= 0) {
					while(strWork1 != null && strWork1 != "") {
						wkPos = strWork1.indexOf("?#");
						if (wkPos >= 0) {
							strWork1 = strWork1.substring(wkPos);
							strWork2 = strWork1.substring(0,2);
							strWork1 = strWork1.substring(2);
							wkPos = strWork1.indexOf("#");
							if (wkPos >= 0) {
								strWork2 = strWork2 + strWork1.substring(0, wkPos+1);
								strWork1 = strWork1.substring(wkPos+1);
								if (strWork2.equals("?#SRCDIR#") || strWork2.equals("?#BASEDIR#")) {
									strRstCmd = strRstCmd.replace(strWork2, DirPath);
								} else if (strWork2.equals("?#SRCHOME#")) {
									strQuery.setLength(0);
									strQuery.append("select c.cm_volpath                     \n");
									strQuery.append("  from cmm0038 c,cmm0031 b,cmr1010 a    \n");
									strQuery.append(" where a.cr_acptno=? and a.cr_itemid=?  \n");
									strQuery.append("   and a.cr_syscd=b.cm_syscd            \n");
									strQuery.append("   and b.cm_svrcd=?                     \n");
									strQuery.append("   and b.cm_closedt is null             \n");
									strQuery.append("   and b.cm_syscd=c.cm_syscd            \n");
									strQuery.append("   and b.cm_svrcd=c.cm_svrcd            \n");
									strQuery.append("   and b.cm_seqno=c.cm_seqno            \n");
									strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd          \n");
									pstmt2 = conn.prepareStatement(strQuery.toString());
									pstmt2.setString(1, AcptNo);
									pstmt2.setString(2, ItemId);
									if (AcptNo.substring(4,6).equals("07")) {
										if (PrcSys.equals("SYSUP")) strSvrCD = "01";
										else if (PrcSys.equals("SYSCB")) strSvrCD = "23";
										else if (PrcSys.equals("SYSED")) strSvrCD = "25";
										else strSvrCD = "01";
									} else if (AcptNo.substring(4,6).equals("04")) {
										if (PrcSys.equals("SYSUP")) strSvrCD = "01";
										else if (PrcSys.equals("SYSCB")) strSvrCD = "03";
										else if (PrcSys.equals("SYSED")) strSvrCD = "05";
										else strSvrCD = "01";
									} else if (AcptNo.substring(4,6).equals("03")) {
										if (PrcSys.equals("SYSUP")) strSvrCD = "01";
										else if (PrcSys.equals("SYSCB")) strSvrCD = "13";
										else if (PrcSys.equals("SYSED")) strSvrCD = "15";
										else strSvrCD = "01";
									} else strSvrCD = "01";
									pstmt2.setString(3, strSvrCD);
									rs2 = pstmt2.executeQuery();
									if (rs2.next()) {
										strRstCmd = strRstCmd.replace(strWork2, rs2.getString("cm_volpath"));
									}
									rs2.close();
									pstmt2.close();
								} else if (strWork2.equals("?#SRCDIRA1#") || strWork2.equals("?#SRCDIRA2") || strWork2.equals("?#SRCDIRA3") 
										  || strWork2.equals("?#SRCDIRA4#") || strWork2.equals("?#SRCDIRA5#")) {
									DirPath = DirPath.replace("//","/");
									if (DirPath.substring(DirPath.length() - 1).equals("/")) DirPath = DirPath.substring(0,DirPath.length() - 1);
									strWork3 = DirPath;
									
									svCnt = Integer.parseInt(strWork2.substring(strWork2.length() - 2, strWork2.length() - 1));
									int j = 0;
									int x = 0;
									while (svCnt > j) {
										x = strWork3.lastIndexOf("/");
										if (x >= 0) {
											strWork3 = strWork3.substring(0,x);
											j = j + 1;
											if (j >= svCnt) break;										
										} else break;
									}
									strRstCmd = strRstCmd.replace(strWork2, strWork3);
								} else if (strWork2.equals("?#SRCDIRB1S#") || strWork2.equals("?#SRCDIRB2S") || strWork2.equals("?#SRCDIRB3S") 
										  || strWork2.equals("?#SRCDIRB4S#") || strWork2.equals("?#SRCDIRB5S#")) {
									DirPath = DirPath.replace("//","/");
									String strWork4 = "";
									if (DirPath.substring(0,1).equals("/")) {
										DirPath = DirPath.substring(1);
									}
									strWork3 = DirPath;
									
									svCnt = Integer.parseInt(strWork2.substring(strWork2.length() - 3, strWork2.length() - 2));
									int j = 0;
									int x = 0;
									while (svCnt > j) {
										x = strWork3.indexOf("/");
										if (x >= 0) {
											if (++j >= svCnt) strWork4 = strWork3.substring(0, x);
											strWork3 = strWork3.substring(x);
											if (j >= svCnt) break;										
										} {
											if (++j >= svCnt) strWork4 = strWork3;
											else break;
										}
									}
									strRstCmd = strRstCmd.replace(strWork2, strWork4);
								} else if (strWork2.equals("?#SRCDIRA1S#") || strWork2.equals("?#SRCDIRA2S") || strWork2.equals("?#SRCDIRA3") 
										  || strWork2.equals("?#SRCDIRA4S#") || strWork2.equals("?#SRCDIRA5S#")) {
									DirPath = DirPath.replace("//","/");
									if (DirPath.substring(DirPath.length() - 1).equals("/")) DirPath = DirPath.substring(0,DirPath.length() - 1);
									strWork3 = DirPath;
									String strWork4 = "";
									svCnt = Integer.parseInt(strWork2.substring(strWork2.length() - 3, strWork2.length() - 2));
									int j = 0;
									int x = 0;
									while (svCnt > j) {
										x = strWork3.lastIndexOf("/");
										if (x >= 0) {
											if (++j >= svCnt) strWork4 = strWork3.substring(x+1);
											strWork3 = strWork3.substring(0,x);
											if (j >= svCnt) break;										
										} else {
											if (++j >= svCnt) strWork4 = strWork3;
											break;
										}
									}
									strRstCmd = strRstCmd.replace(strWork2, strWork4);
								} else if (strWork2.equals("?#BASERSRC#")) {
									strWork3 = "";
									wkPos = RsrcName.indexOf(".");
									if (wkPos > 0) strWork3 = RsrcName.substring(0, wkPos);
									else strWork3 = RsrcName;
									wkPos = strWork3.lastIndexOf("_");
									if (wkPos > 0) strWork3 = strWork3.substring(wkPos+1);
									strRstCmd = strRstCmd.replace(strWork2, strWork3);
								} else if (strWork2.equals("?#BASENAME#")) {
									strWork3 = "";
									wkPos = RsrcName.indexOf(".");
									if (wkPos > 0) strWork3 = RsrcName.substring(0, wkPos);
									else strWork3 = RsrcName;
									wkPos = strWork3.lastIndexOf("_");
									if (wkPos > 0) strWork3 = strWork3.substring(0,wkPos);
									strRstCmd = strRstCmd.replace(strWork2, strWork3);
								} else if (strWork2.equals("?#SRCHOME#")) {
									if (strHome == null || strHome == "") strHome = getHomeDir(SysCd,RsrcCd,BldGbn); 
									
									strRstCmd = strRstCmd.replace(strWork2, strHome);
								} else if (strWork2.equals("?#EXENAME#") || strWork2.equals("?#UPEXENAME#")) {
									wkPos = RsrcName.indexOf(".");
									if (wkPos > 0) strWork3 = RsrcName.substring(0, wkPos);
									else strWork3 = RsrcName;
									strRstCmd = strRstCmd.replace(strWork2, strWork3);
									if (strWork2.equals("?#UPEXENAME#")) strRstCmd = strRstCmd.toUpperCase();
								} else if (strWork2.equals("?#SRCFILE#")) {									
									strRstCmd = strRstCmd.replace(strWork2, RsrcName);
								} else if (strWork2.equals("?#RESULTF#") || strWork2.equals("?#RESULTF1#")) {	
									++rstCnt;
									strRstCmd = strRstCmd.replace(strWork2, "result." + Integer.toString(rstCnt));
								} else if (strWork2.equals("?#EXENAME")) {
									wkPos = RsrcName.indexOf(".");
									if (wkPos > 0) strWork3 = RsrcName.substring(0, wkPos);
									else strWork3 = RsrcName;
									String WkA = strWork2.substring(10);
									wkPos = WkA.indexOf(",");
									String WkB = "";
									if (wkPos >= 0) {
										WkB = WkA.substring(0, wkPos);
										WkA = WkA.substring(wkPos+1);
										wkPos = WkA.indexOf("#");
										if (wkPos >= 0) {
											WkA = WkA.substring(0,wkPos);
											strWork3 = strWork3.replace(WkB, WkA);
											strRstCmd = strRstCmd.replace(strWork2, strWork3);
										}
									}
								} else if (strWork2.equals("?#PARM#")) {
									strWork3 = "";
									strQuery.setLength(0);
									strQuery.append("select CR_PARM FROM CMR1060   \n");      
									strQuery.append(" WHERE CR_ACPTNO=?            \n");
									strQuery.append("   AND CR_SERNO=(select cr_serno from cmr1010  \n");
									strQuery.append("                  where cr_acptno=?            \n");
									strQuery.append("                    and cr_itemid=?)           \n");
									strQuery.append("   AND CR_PRCSYS=?                             \n");
									strQuery.append("   AND CR_BLDCD=?                              \n");
									strQuery.append("   AND CR_BLDGBN=?                             \n");
									strQuery.append("   AND CR_PARM IS NOT NULL                     \n");
									pstmt2 = conn.prepareStatement(strQuery.toString());
									//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								    pstmt2.setString(1, AcptNo);
								    pstmt2.setString(2, AcptNo);
								    pstmt2.setString(3, ItemId);
								    pstmt2.setString(4, PrcSys);
								    pstmt2.setString(5, BldCd);
								    pstmt2.setString(6, BldGbn);
									//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
								    rs2 = pstmt2.executeQuery();
								    if (rs2.next()) {
								    	strWork3 = rs2.getString("CR_PARM");
								    }
								    rs2.close();
								    pstmt2.close();
								    strRstCmd = strRstCmd.replace(strWork2, strWork3);
								}
							} else {
								strWork1 = strWork1.substring(2);
							}
						} else strWork1 = "";
					}
				}
				++wrtCnt;
				rst = new HashMap<String, String>();
        		rst.put("ID", Integer.toString(wrtCnt));  
        		rst.put("cm_cmdname", strRstCmd);  
        		
        		rtList.add(rst);
        		rst = null;
				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
			rtObj =  rtList.toArray();
			rtList = null;
			//ecamsLogger.debug("+++++ Build Script ++++++"+rtList.toString());
			return rtObj;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getBldList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1400.getBldList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getBldList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1400.getBldList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtObj != null)	rtObj = null;			
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}			
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1400.getBldList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		
	}//end of getBldList() method statement
    public String getHomeDir(String SysCd,String RsrcCd,String BldGbn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

        String           strMsg       = "";
		
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT a.cm_volpath from cmm0038 a,cmm0031 b           \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_rsrccd=?                  \n");
			strQuery.append("   and a.cm_svrcd=?                                    \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd and a.cm_svrcd=b.cm_svrcd \n");
			strQuery.append("   and a.cm_seqno=b.cm_seqno and b.cm_closedt is null  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, RsrcCd);
			if (BldGbn.equals("1") || BldGbn.equals("5")) pstmt.setString(3, "01");
			else if (BldGbn.equals("2")) pstmt.setString(3, "03");
			else pstmt.setString(3, "05");
			rs = pstmt.executeQuery();            
			if (rs.next()){
				strMsg = rs.getString("cm_volpath");				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return strMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getHomeDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd1400.getHomeDir() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1400.getHomeDir() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd1400.getHomeDir() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1400.getHomeDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of getHomeDir() method statement
}//end of Cmd1400 class statement
