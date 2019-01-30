/*****************************************************************************************
	1. program ID	: Cmr0200.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	: 
	5. auth		    : 
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmm;

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

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmm0200_Svr{    
	

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
    public Object[] getSvrList(String SysCd,String SvrInfo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int               i = 0;
		String          tmpWork1 = "";
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			String[] strInfo = SvrInfo.split(",");
			strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_svrip,a.cm_svrusr,a.cm_seqno,  \n");
			strQuery.append("       a.cm_portno,a.cm_sysos,a.cm_volpath,a.cm_dir,  \n");
			strQuery.append("       a.cm_svruse,a.cm_svrname,a.cm_ftppass,         \n");
			strQuery.append("       a.cm_agent,a.cm_svrstop,a.cm_buffsize,         \n");
			strQuery.append("       b.cm_codename,c.cm_codename sysos,             \n");
			strQuery.append("       nvl(a.cm_cmpsvr,'N') cm_cmpsvr,                \n");
			strQuery.append("       d.cm_codename ab,a.cm_prcseq,a.cm_samedir      \n");
			strQuery.append(" from cmm0031 a,cmm0020 b,cmm0020 c , cmm0020 d       \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("   and c.cm_macode='SYSOS'                            \n");
			strQuery.append("   and c.cm_micode=a.cm_sysos                         \n");
			strQuery.append("   and d.cm_macode='BUFFSIZE'                         \n");
			strQuery.append("   and d.cm_micode!='****'                             \n");
			strQuery.append("   and d.cm_micode =to_char(LPAD(a.cm_buffsize,8,'0'))\n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrip,a.cm_svrname           \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());	
            pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
                        
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_portno", Integer.toString(rs.getInt("cm_portno")));
				rst.put("sysos", rs.getString("sysos"));
				rst.put("cm_sysos", rs.getString("cm_sysos"));
				rst.put("cm_svrusr", rs.getString("cm_svrusr"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_ftppass", rs.getString("cm_ftppass"));
				rst.put("cm_buffsize", rs.getString("cm_buffsize"));
				rst.put("cm_prcseq", rs.getString("cm_prcseq"));
				rst.put("cm_cmpsvr", rs.getString("cm_cmpsvr"));
				rst.put("ab", rs.getString("ab"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("cm_dir", rs.getString("cm_dir"));
				if (rs.getString("cm_samedir") != null) rst.put("cm_samedir", rs.getString("cm_samedir"));
				if (rs.getString("cm_svruse") != null) rst.put("cm_svruse", rs.getString("cm_svruse"));
				if (rs.getString("cm_agent") != null) {
					if (rs.getString("cm_agent").equals("ER")) rst.put("agent", "장애");
					else rst.put("agent", "정상"); 
				} else rst.put("agent", "정상"); 
				if (rs.getString("cm_svrstop") != null) {
					if (rs.getString("cm_svrstop").equals("Y")) rst.put("svrstop", "YES");
					else rst.put("svrstop", "NO"); 
				} else rst.put("svrstop", "NO");
				tmpWork1 = "";
				if (rs.getString("cm_svruse") != null) {
					for (i=0;rs.getString("cm_svruse").length()>i;i++) {
						if (rs.getString("cm_svruse").substring(i,i+1).equals("1")) {
							if (tmpWork1.length() > 0) tmpWork1 = tmpWork1 + "/";
						    tmpWork1 = tmpWork1 + strInfo[i];
						}
					}
					rst.put("svrinfo", tmpWork1);
				} else rst.put("svrinfo", "NO");
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.getSvrList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
    public Object[] getSecuList(String SysCd,String sysInfo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select distinct a.cm_svrcd,a.cm_svrip,a.cm_seqno,a.cm_portno,  \n");
			strQuery.append("       b.cm_codename,d.cm_owner,d.cm_jobcd,           \n");
			strQuery.append("       d.cm_grpid,d.cm_permission,a.cm_svrname,       \n");
			strQuery.append("       d.cm_dbuser,d.cm_dbpass,d.cm_dbconn		       \n"); //추가
			if (sysInfo.substring(8,9).equals("1")) {
				strQuery.append(",c.cm_jobname                                     \n");
				strQuery.append("from cmm0031 a,cmm0020 b,cmm0102 c,cmm0035 d      \n");
			} else {
				strQuery.append("from cmm0031 a,cmm0020 b,cmm0035 d                \n");
			}
			
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("   and a.cm_syscd=d.cm_syscd and a.cm_svrcd=d.cm_svrcd\n");
			strQuery.append("   and a.cm_seqno=d.cm_seqno                          \n");
			if (sysInfo.substring(8,9).equals("1")) {
				strQuery.append("and d.cm_jobcd=c.cm_jobcd                         \n");
			} else strQuery.append("and d.cm_jobcd='****'                          \n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrip,a.cm_svrname           \n");
            //pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, SysCd);
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_svrusr", rs.getString("cm_owner"));
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));
				rst.put("cm_portno", Integer.toString(rs.getInt("cm_portno")));
				rst.put("cm_codename", rs.getString("cm_codename"));
				if (sysInfo.substring(8,9).equals("1")) rst.put("cm_jobname", rs.getString("cm_jobname"));
				else rst.put("cm_jobname","모든업무");
				rst.put("cm_dbuser", rs.getString("cm_dbuser"));
				rst.put("cm_dbconn", rs.getString("cm_dbconn"));
				rst.put("cm_dbpass", rs.getString("cm_dbpass"));
				if (rs.getString("cm_grpid") != null) 
					rst.put("cm_grpid", rs.getString("cm_grpid"));
				if (rs.getString("cm_permission") != null) 
					rst.put("cm_permission", rs.getString("cm_permission"));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rsval.toArray();
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSecuList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.getSecuList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSecuList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.getSecuList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.getSecuList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
    public String svrInfo_Ins(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           isrtSw      = false;
		int               parmCnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			String returnSTR = "NO";
			int seqNo = 0;
			isrtSw = true;
			if (etcData.get("gbncd").equals("1")) {
				strQuery.setLength(0);
				strQuery.append("select cm_seqno from cmm0031     					\n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  					\n");
				strQuery.append("   and cm_svrip=? and cm_svrusr=? and cm_portno=?	\n");
				strQuery.append("   and cm_volpath=?					 	        \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setString(3, etcData.get("cm_svrip"));
				pstmt.setString(4, etcData.get("cm_svrusr"));
				pstmt.setString(5, etcData.get("cm_portno"));
				pstmt.setString(6, etcData.get("cm_volpath"));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					isrtSw = false;
					seqNo = rs.getInt("cm_seqno");
				}
				rs.close();
				pstmt.close();
				
				if (seqNo == 0) {
					strQuery.setLength(0);
					strQuery.append("select nvl(max(cm_seqno),0)+1 max       \n");
					strQuery.append("  from cmm0031                          \n");
					//strQuery.append(" where cm_syscd=?                       \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, etcData.get("cm_svrcd"));
					rs = pstmt.executeQuery();
					if (rs.next()) {
						seqNo = rs.getInt("max");
					}
					rs.close();
					pstmt.close();
				}
				strQuery.setLength(0);
				parmCnt = 0;
				
				if (isrtSw == true) {
					strQuery.append("insert into cmm0031                     \n");             
					strQuery.append("   (CM_SYSCD,CM_SEQNO,CM_SVRCD,CM_SVRIP,\n");           
					strQuery.append("    CM_SVRNAME,CM_VOLPATH,CM_CREATDT,   \n");          
					strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_PRCSEQ,    \n");          
					strQuery.append("    CM_PORTNO,CM_SYSOS,CM_SVRUSE,       \n");         
					strQuery.append("    CM_DIR,CM_SVRSTOP,CM_FTPPASS,CM_CMPSVR,   \n");  
					strQuery.append("    CM_BUFFSIZE,CM_SVRUSR,cm_samedir) values  \n");             
					strQuery.append("(?, ?, ?, ?, ?, ?, SYSDATE, SYSDATE,          \n");               
					strQuery.append(" ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?)          \n"); 
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setInt(++parmCnt, seqNo);
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrip"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
					pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
					pstmt.setString(++parmCnt, etcData.get("cm_editor"));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_portno")));
					pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
					pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
					pstmt.setString(++parmCnt, etcData.get("cm_dir"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrstop"));
					pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
					pstmt.setString(++parmCnt, etcData.get("dirbase"));
					pstmt.setString(++parmCnt, etcData.get("cm_buffsize"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
					pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
					
					pstmt.executeUpdate();
					pstmt.close();
					
					returnSTR = "OK";
					
				} else {
					strQuery.append("update cmm0031 set cm_closedt='',       \n");             
					strQuery.append("   CM_SVRNAME=?,CM_VOLPATH=?,           \n");          
					strQuery.append("   CM_LASTUPDT=SYSDATE,CM_EDITOR=?,     \n");          
					strQuery.append("   CM_PRCSEQ=?,cm_cmpsvr=?,             \n");           
					strQuery.append("   CM_SYSOS=?,CM_SVRUSE=?,CM_DIR=?,     \n");         
					strQuery.append("   CM_FTPPASS=?,CM_BUFFSIZE=?,cm_samedir=? \n");             
					strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");               
					strQuery.append("   and cm_seqno=?                       \n"); 
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
					pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
					pstmt.setString(++parmCnt, etcData.get("cm_editor"));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
					pstmt.setString(++parmCnt, etcData.get("dirbase"));
					pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
					pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
					pstmt.setString(++parmCnt, etcData.get("cm_dir"));
					pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
					pstmt.setString(++parmCnt, etcData.get("cm_buffsize"));
					pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, seqNo);
					
					pstmt.executeUpdate();
					pstmt.close();
					
					returnSTR = "OK";
				}
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				
				
				if (etcData.get("cm_svruse").substring(0,1).equals("0")) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0035                          \n");      
					strQuery.append("where cm_syscd=? and cm_svrcd=?         \n");     
					strQuery.append("  and cm_seqno=?                        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, etcData.get("cm_svrcd"));
					pstmt.setInt(3, seqNo);
					pstmt.executeUpdate();
					pstmt.close();
				}
			} else {
				seqNo = Integer.parseInt(etcData.get("cm_seqno"));
				parmCnt = 0;
				strQuery.append("update cmm0031 set                      \n");            
				strQuery.append("   CM_SVRIP=?,CM_PORTNO=?,              \n");            
				strQuery.append("   CM_SVRUSR=?,CM_PRCSEQ=?,cm_cmpsvr=?, \n");               
				strQuery.append("   CM_SVRNAME=?,CM_VOLPATH=?,           \n");          
				strQuery.append("   CM_LASTUPDT=SYSDATE,CM_EDITOR=?,     \n");          
				strQuery.append("   CM_SYSOS=?,CM_SVRUSE=?,CM_DIR=?,     \n");         
				strQuery.append("   CM_FTPPASS=?,CM_BUFFSIZE=?,cm_samedir=? \n");             
				strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");               
				strQuery.append("   and cm_seqno=?                       \n"); 
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_svrip"));
				pstmt.setString(++parmCnt, etcData.get("cm_portno"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
				pstmt.setString(++parmCnt, etcData.get("cm_prcseq"));
				pstmt.setString(++parmCnt, etcData.get("dirbase"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
				pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
				pstmt.setString(++parmCnt, etcData.get("cm_editor"));
				pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
				pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
				pstmt.setString(++parmCnt, etcData.get("cm_dir"));
				pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
				pstmt.setString(++parmCnt, etcData.get("cm_buffsize"));
				pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setInt(++parmCnt, seqNo);
				
				pstmt.executeUpdate();
				pstmt.close();
				
				returnSTR = "OK";
			}
			if (etcData.get("basesvr").equals(etcData.get("cm_svrcd"))) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("update cmm0031 set cm_cmpsvr='Y'     \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?     \n");
				strQuery.append("   and cm_closedt is null            \n");
				strQuery.append("   and nvl(cm_cmpsvr,'N')='N'        \n");
				strQuery.append("   and not exists (select 1 from cmm0031 \n");
				strQuery.append("                    where cm_syscd=?     \n");
				strQuery.append("                      and cm_svrcd=?     \n");
				strQuery.append("                      and cm_closedt is null \n");
				strQuery.append("                    having count(*)>1)   \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return returnSTR;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
    //20090702_수정버튼
    public String svrInfo_Ins2(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			String returnSTR = "NO";
			
			int seqNo = 0;
			strQuery.setLength(0);
			strQuery.append("select cm_seqno from cmm0031     					\n");
			strQuery.append(" where cm_syscd=? and cm_svrcd=?  					\n");
			strQuery.append("   and cm_svrip=? and cm_svrusr=? and cm_portno=?	\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_svrcd"));
			pstmt.setString(3, etcData.get("cm_svrip"));
			pstmt.setString(4, etcData.get("cm_svrusr"));
			pstmt.setString(5, etcData.get("cm_portno"));
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				seqNo = rs.getInt("cm_seqno");
			}else{
				seqNo = Integer.parseInt(etcData.get("cm_seqno"));
			}
			rs.close();
			pstmt.close();
			strQuery.setLength(0);
			parmCnt = 0;
			if (seqNo == Integer.parseInt(etcData.get("cm_seqno"))){
				strQuery.append("update cmm0031 set                      \n");         
				strQuery.append("   (CM_SVRNAME,CM_VOLPATH,CM_CLOSEDT,   \n");          
				strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_PRCSEQ,    \n");          
				strQuery.append("    CM_PORTNO,CM_SYSOS,CM_SVRUSE,       \n");         
				strQuery.append("    CM_DIR,CM_SVRSTOP,CM_FTPPASS,       \n");  
				strQuery.append("    CM_SVRUSR,CM_SVRIP) = 		         \n");	//아이피
				strQuery.append("(select ?, ?, '', SYSDATE,              \n");           
				strQuery.append("        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?    \n");      
				strQuery.append("   from cmm0031                         \n");      
				strQuery.append("  where cm_syscd=? and cm_svrcd=?       \n");     
				strQuery.append("    and cm_seqno=?)                     \n");       
				strQuery.append("where cm_syscd=? and cm_svrcd=?         \n");     
				strQuery.append("  and cm_seqno=?                        \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
				pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
				pstmt.setString(++parmCnt, etcData.get("cm_editor"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_portno")));
				pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
				pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
				pstmt.setString(++parmCnt, etcData.get("cm_dir"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrstop"));
				pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrip"));		//아이피
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_seqno")));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_seqno")));
				
				pstmt.executeUpdate();
				pstmt.close();
				
				returnSTR = "OK";
			}
	
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return returnSTR;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.getSvrInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
        public String svrInfo_Close(String SysCd,String UserId,String SvrCd,String SeqNo) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("update cmm0031 set cm_closedt=SYSDATE,          \n");
			strQuery.append("   cm_lastupdt=SYSDATE,cm_editor=?              \n");
			strQuery.append("where cm_syscd=? and cm_svrcd=? and cm_seqno=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, SysCd);
			pstmt.setString(3, SvrCd);
			pstmt.setInt(4, Integer.parseInt(SeqNo));
			pstmt.executeUpdate();
			
			strQuery.setLength(0);
			strQuery.append("delete cmm0035                                  \n");
			strQuery.append("where cm_syscd=? and cm_svrcd=? and cm_seqno=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, SvrCd);
			pstmt.setInt(3, Integer.parseInt(SeqNo));
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmm0038                                  \n");
			strQuery.append("where cm_syscd=? and cm_svrcd=? and cm_seqno=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, SvrCd);
			pstmt.setInt(3, Integer.parseInt(SeqNo));
			pstmt.executeUpdate();
			pstmt.close();	
			conn.close();
			conn = null;
			pstmt = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Close() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Close() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Close() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.svrInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of svrInfo_Close

    public String secuInfo_Ins(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> svrList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               i           = 0;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();		
		try {
			conn = connectionContext.getConnection();
			
			String[] strJob = etcData.get("jobcd").split(",");
			for (i=0;svrList.size()>i;i++) {
				for (j=0;strJob.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0035                    \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
					strQuery.append("   and cm_seqno=? and cm_jobcd=?  \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, svrList.get(i).get("cm_micode"));
					pstmt.setInt(3, Integer.parseInt(svrList.get(i).get("cm_seqno")));
					pstmt.setString(4, strJob[j]);
					pstmt.executeUpdate();					
					pstmt.close();
					
					strQuery.setLength(0);
					strQuery.append("insert into cmm0035               \n");
					strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,     \n");  
					strQuery.append("  CM_JOBCD,CM_OWNER,CM_GRPID,     \n"); 
					strQuery.append("  CM_PERMISSION,CM_DBUSER,        \n");
					strQuery.append("  CM_DBPASS,CM_DBCONN)            \n");
					strQuery.append("   values                         \n");
					strQuery.append("  (?, ?, ?, ?, ?, ?,              \n");
					strQuery.append("   ?, ?, ?, ?)                    \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, svrList.get(i).get("cm_micode"));
					pstmt.setInt(3, Integer.parseInt(svrList.get(i).get("cm_seqno")));
					pstmt.setString(4, strJob[j]);
					pstmt.setString(5, etcData.get("cm_svrusr"));
					pstmt.setString(6, etcData.get("cm_grpid"));
					pstmt.setString(7, etcData.get("cm_permission"));
					pstmt.setString(8, etcData.get("dbuser"));
					pstmt.setString(9, etcData.get("dbpass"));
					pstmt.setString(10, etcData.get("dbconn"));
					pstmt.executeUpdate();
					
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Ins() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Ins() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Ins() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.secuInfo_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}	


    public String secuInfo_Close(String SysCd,String JobCd,ArrayList<HashMap<String,String>> svrList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               i           = 0;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();		
		try {
			conn = connectionContext.getConnection();
			
			String[] strJob = JobCd.split(",");
			for (i=0;svrList.size()>i;i++) {
				for (j=0;strJob.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0035                    \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
					strQuery.append("   and cm_seqno=? and cm_jobcd=?  \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, SysCd);
					pstmt.setString(2, svrList.get(i).get("cm_micode"));
					pstmt.setInt(3, Integer.parseInt(svrList.get(i).get("cm_seqno")));
					pstmt.setString(4, strJob[j]);
					pstmt.executeUpdate();
					
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Close() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Close() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.secuInfo_Close() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.secuInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}
    public String svrInfo_Ins_copy(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           isrtSw      = false;
		int               parmCnt     = 0;
		String            retMsg      = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			int seqNo = 0;
			int aftSeq = 0;
			isrtSw = true;
			strQuery.setLength(0);
			strQuery.append("select cm_closedt,cm_seqno        \n");
			strQuery.append("  from cmm0031                    \n");
			strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
			strQuery.append("   and cm_svrip=? and cm_svrusr=? \n");
			strQuery.append("   and cm_portno=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_svrcd"));
			pstmt.setString(3, etcData.get("aftip"));
			pstmt.setString(4, etcData.get("cm_svrusr"));
			pstmt.setString(5, etcData.get("cm_portno"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString("cm_closedt") == null) {
					retMsg = "기 등록하여 사용 중인 IP입니다.";
				} else {
					isrtSw = false;	
					seqNo = rs.getInt("cm_seqno");
				}
			}
			rs.close();
			pstmt.close();
			
			if (isrtSw == false && retMsg == null) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0038                    \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_seqno=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setInt(3, seqNo);
				pstmt.executeUpdate();
				
				strQuery.setLength(0);
				strQuery.append("delete cmm0031                    \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_seqno=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setInt(3, seqNo);
				pstmt.executeUpdate();				
			}
			
			if (retMsg == null) {
				isrtSw = true;
				strQuery.setLength(0);
				strQuery.append("select cm_seqno from cmm0031      \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_svrip=? and cm_svrusr=? \n");
				strQuery.append("   and cm_portno=?                \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setString(3, etcData.get("cm_svrip"));
				pstmt.setString(4, etcData.get("cm_svrusr"));
				pstmt.setString(5, etcData.get("cm_portno"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					seqNo = rs.getInt("cm_seqno");
				} else {
					retMsg = "복사할 서버IP가 기 등록되지 않은 서버입니다. [" + etcData.get("cm_svrip") + "]";
				}
				rs.close();
				pstmt.close();
			}
			if (retMsg == null) {	
				retMsg = "OK";
				
				strQuery.setLength(0);
				strQuery.append("select nvl(max(cm_seqno),0)+1 max       \n");
				strQuery.append("  from cmm0031                          \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					aftSeq = rs.getInt("max");
				}
				rs.close();
				pstmt.close();
				//CM_SYSCD,CM_SVRCD,CM_SEQNO,CM_SVRIP,CM_SVRUSR,CM_SVRNAME,CM_VOLPATH,
				//CM_CREATDT,CM_LASTUPDT,CM_EDITOR,CM_CLOSEDT,CM_PORTNO,
				//CM_SYSOS,CM_CMPSVR,CM_SVRUSE,CM_FORTIDIR
				strQuery.setLength(0);
				parmCnt = 0;
				strQuery.append("insert into cmm0031                     \n");             
				strQuery.append("   (CM_SYSCD,CM_SEQNO,CM_SVRCD,CM_SVRIP,\n");           
				strQuery.append("    CM_SVRNAME,CM_VOLPATH,CM_CREATDT,   \n");          
				strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_PRCSEQ,    \n");          
				strQuery.append("    CM_PORTNO,CM_SYSOS,CM_SVRUSE,cm_cmpsvr, \n");         
				strQuery.append("    CM_DIR,CM_FTPPASS,CM_SVRUSR,cm_samedir) \n");   
				strQuery.append(" values                                 \n");            
				strQuery.append("(?,?,?,?,?,?,  SYSDATE,SYSDATE,?,?,     \n");               
				strQuery.append(" ?, ?, ?,   ?, ?, ?, ?, ?)              \n"); 
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setInt(++parmCnt, aftSeq);
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setString(++parmCnt, etcData.get("aftip"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
				pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
				pstmt.setString(++parmCnt, etcData.get("cm_editor"));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
				pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_portno")));
				pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
				pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
				pstmt.setString(++parmCnt, etcData.get("dirbase"));
				pstmt.setString(++parmCnt, etcData.get("cm_dir"));
				pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
				pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
				pstmt.executeUpdate();
				pstmt.close();
				//CM_SYSCD,CM_SVRCD,CM_SEQNO,CM_JOBCD,CM_OWNER,CM_GRPID,CM_PERMISSION,CM_DBUSER,CM_DBPASS,CM_DBCONN
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("insert into cmm0035               \n");
				strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,     \n");
				strQuery.append("  CM_JOBCD,CM_OWNER,CM_GRPID,     \n");
				strQuery.append("  CM_PERMISSION,CM_DBUSER,        \n");
				strQuery.append("  CM_DBPASS,CM_DBCONN)            \n");
				strQuery.append("(select CM_SYSCD,CM_SVRCD,?,      \n");
				strQuery.append("  CM_JOBCD,CM_OWNER,CM_GRPID,     \n");
				strQuery.append("  CM_PERMISSION,CM_DBUSER,        \n");
				strQuery.append("  CM_DBPASS,CM_DBCONN             \n");
				strQuery.append("   from cmm0035                   \n");
				strQuery.append("  where cm_syscd=? and cm_svrcd=? \n");
				strQuery.append("   and cm_seqno=?)                \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setInt(++parmCnt, aftSeq);				
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setInt(++parmCnt, seqNo);				
				pstmt.executeUpdate();
				pstmt.close();
				
				//CM_SYSCD,CM_SVRCD,CM_SEQNO,CM_RSRCCD,CM_JOBCD,CM_PORTNO,CM_VOLPATH,CM_TIME
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("insert into cmm0038               \n");
				strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,     \n");
				strQuery.append("  CM_RSRCCD,CM_VOLPATH,CM_TIME,   \n");
				strQuery.append("  CM_JOBCD)                       \n");
				strQuery.append("(select CM_SYSCD,CM_SVRCD,?,      \n");
				strQuery.append("    CM_RSRCCD,CM_VOLPATH,CM_TIME, \n");
				strQuery.append("    CM_JOBCD                      \n");
				strQuery.append("   from cmm0038                   \n");
				strQuery.append("  where cm_syscd=? and cm_svrcd=? \n");
				strQuery.append("   and cm_seqno=?)                \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setInt(++parmCnt, aftSeq);				
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setInt(++parmCnt, seqNo);				
				pstmt.executeUpdate();
				pstmt.close();
			}

			if (etcData.get("basesvr").equals(etcData.get("cm_svrcd"))) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("update cmm0031 set cm_cmpsvr='Y'     \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?     \n");
				strQuery.append("   and cm_closedt is null            \n");
				strQuery.append("   and nvl(cm_cmpsvr,'N')='N'        \n");
				strQuery.append("   and not exists (select 1 from cmm0031 \n");
				strQuery.append("                    where cm_syscd=?     \n");
				strQuery.append("                      and cm_svrcd=?     \n");
				strQuery.append("                      and cm_closedt is null \n");
				strQuery.append("                    having count(*)>1)   \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_copy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_copy() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_copy() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_copy() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_copy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    public String svrInfo_Ins_updt(HashMap<String,String> etcData) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           isrtSw      = false;
		int               parmCnt     = 0;
		String            retMsg      = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			int seqNo = 0;
			int aftSeq = 0;
			isrtSw = true;
			strQuery.setLength(0);
			strQuery.append("select cm_closedt,cm_seqno        \n");
			strQuery.append("  from cmm0031                    \n");
			strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
			strQuery.append("   and cm_svrip=? and cm_svrusr=? \n");
			strQuery.append("   and cm_portno=?                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("cm_syscd"));
			pstmt.setString(2, etcData.get("cm_svrcd"));
			pstmt.setString(3, etcData.get("aftip"));
			pstmt.setString(4, etcData.get("cm_svrusr"));
			pstmt.setString(5, etcData.get("cm_portno"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString("cm_closedt") == null) {
					retMsg = "기 등록하여 사용 중인 IP입니다.";
				} else {
					isrtSw = false;	
					seqNo = rs.getInt("cm_seqno");
				}
			}
			rs.close();
			pstmt.close();
			
			if (isrtSw == false && retMsg == null) {
				strQuery.setLength(0);
				strQuery.append("delete cmm0038                    \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_seqno=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setInt(3, seqNo);
				pstmt.executeUpdate();
				
				strQuery.setLength(0);
				strQuery.append("delete cmm0035                    \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_seqno=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setInt(3, seqNo);
				pstmt.executeUpdate();
				
				strQuery.setLength(0);
				strQuery.append("delete cmm0031                    \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_seqno=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setInt(3, seqNo);
				pstmt.executeUpdate();				
			}
			
			if (retMsg == null) {
				retMsg = "OK";
				isrtSw = true;
				
				strQuery.setLength(0);
				strQuery.append("select cm_seqno from cmm0031      \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?  \n");
				strQuery.append("   and cm_svrip=? and cm_svrusr=? \n");
				strQuery.append("   and cm_portno=?                \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("cm_syscd"));
				pstmt.setString(2, etcData.get("cm_svrcd"));
				pstmt.setString(3, etcData.get("cm_svrip"));
				pstmt.setString(4, etcData.get("cm_svrusr"));
				pstmt.setString(5, etcData.get("cm_portno"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					isrtSw = false;
					seqNo = rs.getInt("cm_seqno");
				} 
				rs.close();
				pstmt.close();
				
				strQuery.setLength(0);
				parmCnt = 0;
				if (isrtSw == true) {
					strQuery.setLength(0);
					strQuery.append("select nvl(max(cm_seqno),0)+1 max       \n");
					strQuery.append("  from cmm0031                          \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, etcData.get("cm_syscd"));
					pstmt.setString(2, etcData.get("cm_svrcd"));
					rs = pstmt.executeQuery();
					if (rs.next()) {
						seqNo = rs.getInt("max");
					}
					rs.close();
					pstmt.close();		
					
					strQuery.setLength(0);
					parmCnt = 0;
					strQuery.append("insert into cmm0031                     \n");             
					strQuery.append("   (CM_SYSCD,CM_SEQNO,CM_SVRCD,CM_SVRIP,\n");           
					strQuery.append("    CM_SVRNAME,CM_VOLPATH,CM_CREATDT,   \n");          
					strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_PRCSEQ,    \n");          
					strQuery.append("    CM_PORTNO,CM_SYSOS,CM_SVRUSE,cm_cmpsvr,  \n");         
					strQuery.append("    CM_DIR,CM_FTPPASS,CM_SVRUSR,cm_samedir)  \n"); 
					strQuery.append(" values                                 \n");              
					strQuery.append("(?,?,?,?,?,?,   SYSDATE, SYSDATE,?,?,   \n");               
					strQuery.append(" ?, ?, ?, ?,   ?, ?, ?, ?)              \n"); 
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setInt(++parmCnt, aftSeq);
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setString(++parmCnt, etcData.get("aftip"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
					pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
					pstmt.setString(++parmCnt, etcData.get("cm_editor"));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_portno")));
					pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
					pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
					pstmt.setString(++parmCnt, etcData.get("dirbase"));
					pstmt.setString(++parmCnt, etcData.get("cm_dir"));
					pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
					pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
					pstmt.executeUpdate();
					pstmt.close();
					
					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmm0035               \n");
					strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,     \n");
					strQuery.append("  CM_JOBCD,CM_OWNER,CM_GRPID,     \n");
					strQuery.append("  CM_PERMISSION,CM_DBUSER,        \n");
					strQuery.append("  CM_DBPASS,CM_DBCONN)            \n");
					strQuery.append("(select CM_SYSCD,CM_SVRCD,?,      \n");
					strQuery.append("  CM_JOBCD,CM_OWNER,CM_GRPID,     \n");
					strQuery.append("  CM_PERMISSION,CM_DBUSER,        \n");
					strQuery.append("  CM_DBPASS,CM_DBCONN             \n");
					strQuery.append("   from cmm0035                   \n");
					strQuery.append("  where cm_syscd=? and cm_svrcd=? \n");
					strQuery.append("   and cm_seqno=?)                \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setInt(++parmCnt, aftSeq);				
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, seqNo);				
					pstmt.executeUpdate();
					pstmt.close();
					
					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmm0038               \n");
					strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,     \n");
					strQuery.append("  CM_RSRCCD,CM_VOLPATH,CM_TIME,   \n");
					strQuery.append("  CM_JOBCD)                       \n");
					strQuery.append("(select CM_SYSCD,CM_SVRCD,?,      \n");
					strQuery.append("    CM_RSRCCD,CM_VOLPATH,CM_TIME, \n");
					strQuery.append("    CM_JOBCD                      \n");
					strQuery.append("   from cmm0038                   \n");
					strQuery.append("  where cm_syscd=? and cm_svrcd=? \n");
					strQuery.append("   and cm_seqno=?)                \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setInt(++parmCnt, aftSeq);				
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, seqNo);				
					pstmt.executeUpdate();
					pstmt.close();	
				} else {
					strQuery.append("update cmm0031 set                           \n");           
					strQuery.append("   (CM_SVRIP,CM_SVRNAME,CM_VOLPATH,CM_CLOSEDT,\n");          
					strQuery.append("    CM_CREATDT,CM_LASTUPDT,CM_EDITOR,CM_PRCSEQ,\n");          
					strQuery.append("    CM_PORTNO,CM_SYSOS,CM_SVRUSE,cm_cmpsvr,CM_DIR,  \n");         
					strQuery.append("    CM_FTPPASS,CM_SVRUSR,cm_samedir) =       \n");
					strQuery.append("(select ?, ?, ?, '', SYSDATE,SYSDATE,?,?,    \n");           
					strQuery.append("        ?, ?, ?, ?, ?,    ?, ?, ?            \n");      
					strQuery.append("   from cmm0031                              \n");      
					strQuery.append("  where cm_syscd=? and cm_svrcd=?            \n");     
					strQuery.append("    and cm_seqno=?)                          \n");       
					strQuery.append("where cm_syscd=? and cm_svrcd=?              \n");     
					strQuery.append("  and cm_seqno=?                             \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(++parmCnt, etcData.get("aftip"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrname"));
					pstmt.setString(++parmCnt, etcData.get("cm_volpath"));
					pstmt.setString(++parmCnt, etcData.get("cm_editor"));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_prcseq")));
					pstmt.setInt(++parmCnt, Integer.parseInt(etcData.get("cm_portno")));
					pstmt.setString(++parmCnt, etcData.get("cm_sysos"));
					pstmt.setString(++parmCnt, etcData.get("cm_svruse"));
					pstmt.setString(++parmCnt, etcData.get("dirbase"));
					pstmt.setString(++parmCnt, etcData.get("cm_dir"));
					pstmt.setString(++parmCnt, etcData.get("cm_ftppass"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrusr"));
					pstmt.setString(++parmCnt, etcData.get("cm_samedir"));
					
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, seqNo);
					
					pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
					pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
					pstmt.setInt(++parmCnt, seqNo);
					pstmt.executeUpdate();
					pstmt.close();
				}
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				
			}
			
			if (etcData.get("basesvr").equals(etcData.get("cm_svrcd"))) {
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("update cmm0031 set cm_cmpsvr='Y'     \n");
				strQuery.append(" where cm_syscd=? and cm_svrcd=?     \n");
				strQuery.append("   and cm_closedt is null            \n");
				strQuery.append("   and nvl(cm_cmpsvr,'N')='N'        \n");
				strQuery.append("   and not exists (select 1 from cmm0031 \n");
				strQuery.append("                    where cm_syscd=?     \n");
				strQuery.append("                      and cm_svrcd=?     \n");
				strQuery.append("                      and cm_closedt is null \n");
				strQuery.append("                    having count(*)>1)   \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				pstmt.setString(++parmCnt, etcData.get("cm_syscd"));
				pstmt.setString(++parmCnt, etcData.get("cm_svrcd"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_updt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_updt() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_updt() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_updt() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0200_Svr.svrInfo_Ins_updt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}//end of Cmm0200_Svr class statement
