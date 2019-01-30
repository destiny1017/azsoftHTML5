/*****************************************************************************************
	1. program ID	: Cmr0200_BefJob.java
	2. create date	: 2008.08.08
	3. auth		    : is.choi
	4. update date	: 2009.02.21
	5. auth		    : no name
	6. description	: [체크인신청상세]->[선행작업]
*****************************************************************************************/

package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0200_BefJob {

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 선행작업 리스트 조회
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */    
	public ArrayList<HashMap<String, String>> reqList_Select(String AcptNo,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>         rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			int parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("select substr(a.cr_acptno,0,4) || '-' || substr(a.cr_acptno,5,2) \n");	
			strQuery.append("      || '-' ||  substr(a.cr_acptno,7,6) acptno,            \n");
			strQuery.append("      to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate, \n");		
			strQuery.append("      a.cr_acptno,a.cr_prcdate,a.cr_status,a.cr_sayu,       \n");		
			strQuery.append("      b.cm_sysmsg,c.cm_username,d.cm_codename              \n");
		    strQuery.append(" from cmm0020 d,cmm0040 c,cmm0030 b,cmr1000 a            \n");
		    strQuery.append("where a.cr_qrycd=? and a.cr_prcdate is null              \n"); 
		    if (AcptNo != null && AcptNo != "") {
			   strQuery.append("and a.cr_acptno not in (select cr_befact from cmr1030 \n");
			   strQuery.append("                         where cr_acptno=?)           \n");
			   strQuery.append("and a.cr_acptno not in (select cr_acptno from cmr1030 \n");
			   strQuery.append("                         where cr_befact=?)           \n");
		       strQuery.append("and a.cr_acptno <> ?								  \n");
		    }    
			strQuery.append("  and a.cr_syscd=b.cm_syscd                                 \n");                
			strQuery.append("  and a.cr_editor=c.cm_userid                               \n");                
			strQuery.append("  and d.cm_macode='CMR1000' and d.cm_micode=a.cr_status     \n");
            strQuery.append("order by a.cr_acptdate desc                                 \n");
            
            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            
            parmCnt = 0;
            pstmt.setString(++parmCnt, ReqCd);
            if (AcptNo != null && AcptNo != "") {
            	pstmt.setString(++parmCnt, AcptNo);
            	pstmt.setString(++parmCnt, AcptNo);
            	pstmt.setString(++parmCnt, AcptNo);
            }
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            String ConfName = "";
    		String ColorSw  = "";
            rs = pstmt.executeQuery(); 
			while (rs.next()){
				ConfName = "";
				ColorSw = "0";
				
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno", rs.getString("acptno"));
				rst.put("acptdate", rs.getString("acptdate"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_username", rs.getString("cm_username"));
				
				/*if (rs.getString("cr_isrid") != null) {
					rst.put("isrtitle", "["+rs.getString("cr_isrid")+"-"+rs.getString("cr_isrsub")+"] " + rs.getString("cc_isrtitle"));
				}*/
				
				ConfName = rs.getString("cm_codename");
				if (rs.getString("cr_status").equals("3")) {
	            	ColorSw = "3";
	            } else if (rs.getString("cr_status").equals("9") || rs.getString("cr_status").equals("8")) {
	            	ColorSw = "9";
	            } else {
	            	strQuery.setLength(0);
					strQuery.append("select cr_teamcd,cr_confname from cmr9900   \n");
				    strQuery.append(" where cr_acptno=? and cr_locat='00'        \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
		            pstmt2.setString(1, rs.getString("cr_acptno"));		            
		            rs2 = pstmt2.executeQuery();		            
		            if (rs2.next()){
		            	ConfName = rs2.getString("cr_confname");	
		            }
		            rs2.close();
		            pstmt2.close();
	            }
	            if (rs.getString("cr_prcdate") == null) {
            		strQuery.setLength(0);
					strQuery.append("select count(*) as cnt from cmr1010   \n");
				    strQuery.append(" where cr_acptno=?                    \n");
				    strQuery.append("   and cr_putcode is not null         \n");
				    strQuery.append("   and nvl(cr_putcode,'RTRY')!='RTRY' \n");
				    strQuery.append("   and nvl(cr_putcode,'0000')!='0000' \n");
				    pstmt2 = conn.prepareStatement(strQuery.toString());	
				    pstmt2.setString(1, rs.getString("cr_acptno"));		            
		            rs2 = pstmt2.executeQuery();		            
		            if (rs2.next()){
		            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
		            }
		            rs2.close();
		            pstmt2.close();

		            if (!ColorSw.equals("5")) {
		            	strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1011   \n");
					    strQuery.append(" where cr_acptno=?                    \n");
					    strQuery.append("   and (nvl(cr_prcrst,'0000')!='0000' \n");
					    strQuery.append("    or nvl(cr_wait,'0')='W')          \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());	
			            pstmt2.setString(1, rs.getString("cr_acptno"));		            
			            rs2 = pstmt2.executeQuery();		            
			            if (rs2.next()){
			            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
			            }
			            rs2.close();
			            pstmt2.close();
	            	}
	            } else if (ColorSw.equals("0")){
	            	ColorSw = "9";
	            }
	            rst.put("colorsw",  ColorSw);
				rst.put("cm_codename", ConfName);
				rst.put("cr_sayu", rs.getString("cr_sayu"));				
				rst.put("cr_befact", rs.getString("cr_acptno"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			conn = null;			
			
			return rsval;

			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Select() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Select() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Select() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Select() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.reqList_Select() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of reqList_Select() method statement
	public ArrayList<HashMap<String, String>> befJob_List(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>         rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			int parmCnt = 0;
			strQuery.setLength(0);
			strQuery.append("select 'BEF' cd,substr(a.cr_acptno,0,4) || '-' || substr(a.cr_acptno,5,2) \n");	
			strQuery.append("      || '-' ||  substr(a.cr_acptno,7,6) acptno,a.cr_editor,\n");
			strQuery.append("      to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate, \n");		
			strQuery.append("      a.cr_acptno,a.cr_prcdate,a.cr_status,a.cr_sayu,       \n");		
			strQuery.append("      b.cm_sysmsg,c.cm_username,d.cm_codename,e.cr_autolink \n");
			strQuery.append("  from cmr1030 e,cmm0020 d,cmm0040 c,cmm0030 b,cmr1000 a    \n");
			strQuery.append(" where e.cr_acptno=?                                        \n");
			strQuery.append("   and e.cr_befact=a.cr_acptno                              \n");   
			strQuery.append("   and a.cr_syscd=b.cm_syscd                                \n");                
			strQuery.append("   and a.cr_editor=c.cm_userid                              \n");                
			strQuery.append("   and d.cm_macode='CMR1000' and d.cm_micode=a.cr_status    \n");
			strQuery.append("union \n");	
			strQuery.append("select 'AFT' cd,substr(a.cr_acptno,0,4) || '-' || substr(a.cr_acptno,5,2) \n");	
			strQuery.append("      || '-' ||  substr(a.cr_acptno,7,6) acptno,a.cr_editor,\n");
			strQuery.append("      to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate, \n");		
			strQuery.append("      a.cr_acptno,a.cr_prcdate,a.cr_status,a.cr_sayu,       \n");		
			strQuery.append("      b.cm_sysmsg,c.cm_username,d.cm_codename,e.cr_autolink \n");
			strQuery.append("  from cmr1030 e,cmm0020 d,cmm0040 c,cmm0030 b,cmr1000 a    \n");
			strQuery.append(" where e.cr_befact=?                                        \n");
			strQuery.append("   and e.cr_acptno=a.cr_acptno                              \n");   
			strQuery.append("   and a.cr_syscd=b.cm_syscd                                \n");                
			strQuery.append("   and a.cr_editor=c.cm_userid                              \n");                
			strQuery.append("   and d.cm_macode='CMR1000' and d.cm_micode=a.cr_status    \n");
            
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            
            parmCnt = 0;
        	pstmt.setString(++parmCnt, AcptNo);
        	pstmt.setString(++parmCnt, AcptNo);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            String ConfName = "";
    		String ColorSw  = "";
            rs = pstmt.executeQuery(); 
			while (rs.next()){
				ConfName = "";
				ColorSw = "0";
				
				rst = new HashMap<String, String>();
				if (rs.getString("cd").equals("BEF")) {
					rst.put("befgbn", "선행");
				} else {
					rst.put("befgbn", "후행");
				}
				if (rs.getString("cr_autolink").equals("Y")) {
					rst.put("linkgbn", "영향분석");
				} else {
					rst.put("linkgbn", "요청자");
				}
				rst.put("cr_acptno", rs.getString("acptno"));
				rst.put("acptdate", rs.getString("acptdate"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cr_editor", rs.getString("cr_editor"));
				
				/*if (rs.getString("cr_isrid") != null) {
					rst.put("isrtitle", "["+rs.getString("cr_isrid")+"-"+rs.getString("cr_isrsub")+"] " + rs.getString("cc_isrtitle"));
				}*/
				
				ConfName = rs.getString("cm_codename");
				if (rs.getString("cr_status").equals("3")) {
	            	ColorSw = "3";
	            } else if (rs.getString("cr_status").equals("9") || rs.getString("cr_status").equals("8")) {
	            	ColorSw = "9";
	            } else {
	            	strQuery.setLength(0);
					strQuery.append("select cr_teamcd,cr_confname from cmr9900   \n");
				    strQuery.append(" where cr_acptno=? and cr_locat='00'        \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());	
		            pstmt2.setString(1, rs.getString("cr_acptno"));		            
		            rs2 = pstmt2.executeQuery();		            
		            if (rs2.next()){
		            	ConfName = rs2.getString("cr_confname");	
		            }
		            rs2.close();
		            pstmt2.close();
	            }
	            if (rs.getString("cr_prcdate") == null) {
            		strQuery.setLength(0);
					strQuery.append("select count(*) as cnt from cmr1010   \n");
				    strQuery.append(" where cr_acptno=?                    \n");
				    strQuery.append("   and cr_putcode is not null         \n");
				    strQuery.append("   and nvl(cr_putcode,'RTRY')!='RTRY' \n");
				    strQuery.append("   and nvl(cr_putcode,'0000')!='0000' \n");
				    pstmt2 = conn.prepareStatement(strQuery.toString());	
				    pstmt2.setString(1, rs.getString("cr_acptno"));		            
		            rs2 = pstmt2.executeQuery();		            
		            if (rs2.next()){
		            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
		            }
		            rs2.close();
		            pstmt2.close();

		            if (!ColorSw.equals("5")) {
		            	strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1011   \n");
					    strQuery.append(" where cr_acptno=?                    \n");
					    strQuery.append("   and (nvl(cr_prcrst,'0000')!='0000' \n");
					    strQuery.append("    or nvl(cr_wait,'0')='W')          \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());	
			            pstmt2.setString(1, rs.getString("cr_acptno"));		            
			            rs2 = pstmt2.executeQuery();		            
			            if (rs2.next()){
			            	if (rs2.getInt("cnt") > 0) ColorSw = "5";
			            }
			            rs2.close();
			            pstmt2.close();
	            	}
	            } else if (ColorSw.equals("0")){
	            	ColorSw = "9";
	            }
	            rst.put("colorsw",  ColorSw);
				rst.put("cm_codename", ConfName);
				rst.put("cr_sayu", rs.getString("cr_sayu"));				
				rst.put("cr_befact", rs.getString("cr_acptno"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			conn = null;			
			
			return rsval;

			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.befJob_List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0200_BefJob.befJob_List() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.befJob_List() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200_BefJob.befJob_List() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.befJob_List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of reqList_Select() method statement
	
	
	
	/** 신청서에 신청된 프로그램 리스트 조회
	 * @param AcptNo
	 * @return {"cm_dirpath", "cm_dirpath"},{"cr_rsrcname", "cr_rsrcname"}
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> reqList_Prog(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>         rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select b.cm_dirpath,a.cr_rsrcname                       \n");
			strQuery.append(" from cmm0070 b,cmr1010 a                               \n");
			strQuery.append("where a.cr_acptno=?                                     \n");  
			strQuery.append("  and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");                
			strQuery.append("  and a.cr_baseitem=a.cr_itemid                         \n"); 
            strQuery.append("order by b.cm_dirpath,a.cr_rsrcname                     \n");
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, AcptNo);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            
            rs = pstmt.executeQuery(); 
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rsval.add(rst);	
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rsval;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Prog() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Prog() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Prog() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200_BefJob.reqList_Prog() Exception END ##");				
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
					ecamsLogger.error("## Cmr0200_BefJob.reqList_Prog() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of reqList_Prog() method statement
	
	
	public String updtBefJob(String AcptNo,ArrayList<HashMap<String, String>> befList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			for (int i=0;befList.size()>i;i++) {
	            strQuery.setLength(0);
	        	strQuery.append("insert into cmr1030             \n");
	            strQuery.append(" (cr_acptno,cr_befact)          \n");
	            strQuery.append(" values (?,?)                   \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1,AcptNo);
	            pstmt.setString(2,befList.get(i).get("cr_befact"));
	            pstmt.executeUpdate();
	            pstmt.close();
			}
			
            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtBefJob() method statement
	
	
	public String delBefJob(String AcptNo,String befAcpt) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("delete cmr1030      \n");
			strQuery.append(" where cr_acptno=?  \n");
			strQuery.append("   and cr_befact=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,befAcpt);
            pstmt.executeUpdate();
            pstmt.close();
            
            conn.commit();
            conn.close();
			pstmt = null;
			conn = null;

            return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200_BefJob.updtBefJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtBefJob() method statement
}//end of Cmr0101 class statement
