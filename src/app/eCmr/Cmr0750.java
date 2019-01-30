/*****************************************************************************************
	1. program ID	: Cmr0250.java
	2. create date	: 2008.08.10
	3. auth		    : is.choi
	4. update date	: 2009.07.10
	5. auth		    : no name
	6. description	: 서비스변경요청상세화면
*****************************************************************************************/

package app.eCmr;

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
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0750{    
	
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
	public Object[] getReqList(String UserId,String AcptNo) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		String           strPrcSw       = "N";
		Object[] returnObjectArray		 = null;
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,        \n");			
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,          \n");				
			strQuery.append("       a.cr_acptno,a.cr_syscd,a.cr_editor,a.cr_itsmid,a.cr_status,  \n");		
			strQuery.append("       a.cr_qrycd,a.cr_passok,a.cr_itsmtitle,b.cm_sysmsg,b.cm_sysinfo, \n");		
			strQuery.append("       d.cm_username,c.cr_jobmsg,c.cr_runmsg,a.cr_prcreq            \n");			
			strQuery.append("  from cmr1300 c,cmm0040 d,cmm0030 b,cmr1000 a                      \n"); 
			strQuery.append(" where a.cr_acptno=?                                                \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_editor=d.cm_userid            \n");
			strQuery.append("   and a.cr_acptno=c.cr_acptno                                      \n");
			pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	       //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        
	        rsval.clear();
	        
			if (rs.next()){			
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("acptno",rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptdate",rs.getString("acptdate"));
				if (rs.getString("prcdate") != null) rst.put("prcdate",rs.getString("prcdate"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_passok",rs.getString("cr_passok"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("cr_itsmid",rs.getString("cr_itsmid"));
				rst.put("cr_itsmtitle",rs.getString("cr_itsmtitle"));
				rst.put("cr_jobmsg",rs.getString("cr_jobmsg"));
				rst.put("cr_runmsg",rs.getString("cr_runmsg"));
				
				if (rs.getString("cr_prcreq") != null) {
					rst.put("cr_prcreq",	rs.getString("cr_prcreq").substring(0, 4) + "/" +
										rs.getString("cr_prcreq").substring(4, 6) + "/" +
										rs.getString("cr_prcreq").substring(6, 8) + " " +
										rs.getString("cr_prcreq").substring(8, 10) + ":" +
										rs.getString("cr_prcreq").substring(10, 12));
				}else {
					rst.put("cr_prcreq", "");
				}
				
				if( rs.getString("cr_passok").equals("0") )			rst.put("cr_passokgbn", "일반");
				else if( rs.getString("cr_passok").equals("2") )	rst.put("cr_passokgbn", "긴급");
				else												rst.put("cr_passokgbn", "");
				
				
				if (rs.getString("cr_status").equals("9") || rs.getString("cr_status").equals("8")) {
					rst.put("confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw3", "0");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("confname","반려");	
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw3", "0");
				} else {
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
						strPrcSw = "0";
					}
					rst.put("endsw", "0");
					
					//rsconf = confLocat(AcptNo,strPrcSw);
					rsconf = confLocat_conn(AcptNo,strPrcSw,conn);
					
					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					rst.put("confname", rsconf.get(0).get("confname"));
					rst.put("updtsw3", rsconf.get(0).get("updtsw3"));
					rst.put("errtry", rsconf.get(0).get("errtry"));
					rst.put("log", rsconf.get(0).get("log"));
					rsconf = null;
				}				
				
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("select cr_gubun,count(*) cnt from cmr1001  \n");
			strQuery.append(" where cr_acptno=?                         \n");
			strQuery.append(" group by cr_gubun                         \n");
			pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt.setString(1,AcptNo);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("cr_gubun").equals("1")) rst.put("attfile1", "Y");
				else if (rs.getString("cr_gubun").equals("2")) rst.put("attfile2", "Y");
			}
			rs.close();
			pstmt.close();
			
			if (rst.get("cr_acptno") != null) {
				rsval.add(rst);
				rst = null;
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			returnObjectArray = rsval.toArray();
			//ecamsLogger.error(rsval.toString());
			rsval.clear();
			rsval = null;		
			
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0750.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0750.getReqList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0750.getReqList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0750.getReqList() Exception END ##");				
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
					ecamsLogger.error("## Cmr0750.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement
	
	
	public ArrayList<HashMap<String, String>> confLocat_conn(String AcptNo,String PrcSw,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		rsval.clear();
		
		try {
			strQuery.setLength(0);
			strQuery.append("select cr_team,cr_teamcd,cr_confname,cr_prcsw,cr_confusr            \n");
			strQuery.append("  from cmr9900                                                      \n"); 
			strQuery.append(" where cr_acptno=? and cr_locat='00'                                \n");
	        pstmt = conn.prepareStatement(strQuery.toString());	
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("signteam", rs.getString("cr_team"));
				rst.put("signteamcd", rs.getString("cr_teamcd"));
				rst.put("confusr", rs.getString("cr_confusr"));
				rst.put("prcsw", rs.getString("cr_prcsw"));
				if (rs.getString("cr_teamcd").equals("1")) {
					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020             \n");
					strQuery.append(" where cm_macode='SYSGBN' and cm_micode=?   \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
					pstmt2.setString(1,rs.getString("cr_team"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
					
				}else if (rs.getString("cr_teamcd").equals("2") || rs.getString("cr_teamcd").equals("3") ||
						rs.getString("cr_teamcd").equals("6") || rs.getString("cr_teamcd").equals("7") ||
						rs.getString("cr_teamcd").equals("8")) {
					strQuery.setLength(0);
					strQuery.append("select cm_username from cmm0040             \n");
					strQuery.append(" where cm_userid=?                          \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
					pstmt2.setString(1,rs.getString("cr_team"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("confname", rs2.getString("cm_username") + "결재대기 중");
					}
					rs2.close();
					pstmt2.close();
				}else {
					rst.put("confname", rs.getString("cr_confname"));
				}
			}
			rs.close();
			pstmt.close();
			
			rs = null;
			pstmt = null;
			pstmt = null;
			
            rsval.add(rst);
            rst = null;
            
            return rsval;
            
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0750.confLocat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0750.confLocat() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			ecamsLogger.error("## Cmr0750.confLocat() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0750.confLocat() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of confLocat() method statement
	public String updtData(String AcptNo,String UserId,String runMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
            strQuery.append("update cmr1300 set cr_runmsg=?                         \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,runMsg);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();
            
            strQuery.setLength(0);
            strQuery.append("delete cmr1001 where cr_acptno=? and cr_gubun='2' \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();
            
            /*
			for (int i=0;i<fileList.size();i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmr1001 (cr_acptno,cr_gubun,cr_seqno,cr_filename,cr_reldoc)\n");
				strQuery.append(" values (? , ? , ? , ? , ? ) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());				
				
				String seq = (i+1)+"";
				String fullName = AcptNo.substring(0, 4) + "/" + AcptNo.substring(4, 6) + "/" + fileGb + "_" + AcptNo.substring(6, AcptNo.length())+"_"+seq;
				
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, fileGb);
				pstmt.setString(3, seq);				
				pstmt.setString(4, fileList.get(i).get("name"));
				pstmt.setString(5, fullName);
				
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());    
	            pstmt.executeUpdate();
	            pstmt.close();
			}
			*/
            strErMsg = "OK";
            conn.commit();
            conn.close();
            pstmt = null;
            conn = null;
            return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0750.updtData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0750.updtData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0750.updtData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0150.updtData() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0750.updtData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtData() method statement
	
}//end of Cmr0750 class statement
