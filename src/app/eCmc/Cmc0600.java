/*****************************************************************************************
	1. program ID	: Cmc0300.java
	2. create date	: 2014.07.25
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [적용] -> [모니터링체크]
*****************************************************************************************/
package app.eCmc;

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

public class Cmc0600 {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	/** 모니터링 체크 화면값 조회
	 * @param cc_srid
	 * @param reqCD
	 * @param userID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */	
	
	public Object[] getSecCheckList(String SysCd,String SrId,String strReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strGubun    = null;
		String            strGubun1   = null;
		String            strDetGubun    = null;
		String            strDetGubun1   = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select cm_gbncd , cm_gbnname, cm_reqyn, LEVEL AS lv	\n");
			strQuery.append("  from cmm0110					             			\n");
			strQuery.append(" where cm_clsdate is null		             			\n");
			strQuery.append("start with cm_uppgbncd is null and cm_clsdate is null	\n");
			strQuery.append("connect by prior  cm_gbncd = cm_uppgbncd				\n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			//ecamsLogger.error(strTypeCd);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				rst = new HashMap<String, String>();
				if ( rs.getInt("lv") == 1) {
					if ( strGubun != rs.getString("lv"))
						if ( strGubun1 != rs.getString("lv"))
							strGubun = rs.getString("cm_gbnname");
					
				} else if ( rs.getInt("lv") == 2) {
					if ( strDetGubun != rs.getString("lv"))
						if (strDetGubun1 != rs.getString("cm_gbnname"))
							strDetGubun = rs.getString("cm_gbnname");
					
					strDetGubun = rs.getString("cm_gbnname");
				} else if ( rs.getInt("lv") == 3) {
					strQuery.setLength(0);
					strQuery.append("select cm_reqyn \n");
					strQuery.append("  from cmm0130	 \n");
					strQuery.append(" where cm_syscd=? \n");
					if( strReqCd.equals("63") ){
						strQuery.append("   and cm_gbncd=? \n");
						strQuery.append("   and cm_closedt is null  \n");
					} else {
						strQuery.append("   and cm_gbncd in (select cc_gbncd from cmc0202 where cc_srid=? and cc_syscd=? and cc_gbncd=?) \n");
					}
					
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, SysCd);
					if( strReqCd.equals("63") ){
						pstmt2.setString(2, rs.getString("cm_gbncd"));
					} else {
						pstmt2.setString(2, SrId);
						pstmt2.setString(3, SysCd);
						pstmt2.setString(4, rs.getString("cm_gbncd"));
					}
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if( rs2.next() ) {
						rst.put("cc_gubun", strGubun);
						rst.put("cc_detgubun", strDetGubun);
						//rst.put("sel", "P");
						rst.put("cc_item", rs.getString("cm_gbnname"));
						if ( rs2.getString("cm_reqyn") != null ) {
							rst.put("cc_reqyn", rs2.getString("cm_reqyn"));
						} else {
							rst.put("cc_reqyn", "N");
						}
						rst.put("cc_gbncd", rs.getString("cm_gbncd"));
						rst.put("enabled", "1");
						strGubun1 = strGubun;
						strDetGubun1 = strDetGubun;
						strGubun = null;
						strDetGubun = null;
					}
					rs2.close();
					pstmt2.close();
				}
				
				if ( rs.getInt("lv") == 3 && rst.size()>0)
				{
					rsval.add(rst);
					rst = null;
				}
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs2=null;
			pstmt2=null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getSecCheckList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.getSecCheckList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getSecCheckList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.getSecCheckList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.getSecCheckList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
	}//end of getSecurityCheckList() method statement	
	
	public Object[] getApplyInfo(String srID, String sysCd, String editorID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strGubun    = null;
		String            strDetGubun = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT a.cm_gbnname as gbnname, b.cm_gbnname as detgubun ,              \n");
			strQuery.append("       c.cm_gbnname as itemgubun, d.cc_reqyn, d.cc_eval ,d.cc_gbncd     \n");
			strQuery.append("  FROM cmm0110 a, cmm0110 b, cmm0110 c, cmc0202 d                		 \n");
			strQuery.append(" WHERE d.cc_srid=? and a.cm_gbncd = b.cm_uppgbncd                       \n");
			strQuery.append("  	AND b.cm_gbncd = c.cm_uppgbncd    					                 \n");	
			strQuery.append("  	AND c.cm_gbncd = d.cc_gbncd      					                 \n");
			strQuery.append("  	AND d.cc_syscd=?			      					             	 \n");	
			strQuery.append("  	AND d.cc_editor=?			      					             	 \n");	
			strQuery.append(" order by CC_GBNCD													     \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        pstmt.setString(2, sysCd);
	        pstmt.setString(3, editorID);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while (rs.next()) {
				rst = new HashMap<String, String>();
				if ( rs.getString("gbnname").equals(strGubun)) {
					rst.put("cc_gubun", null);
				}
				else {
					rst.put("cc_gubun", rs.getString("gbnname"));
					strGubun = rs.getString("gbnname");
				}
				
				if ( rs.getString("detgubun").equals(strDetGubun)){
					rst.put("cc_detgubun", null);
					
				} else {
					rst.put("cc_detgubun", rs.getString("detgubun"));
					strDetGubun  = rs.getString("detgubun");	
					
				}
				rst.put("cc_item", rs.getString("itemgubun"));
				rst.put("cc_reqyn", rs.getString("cc_reqyn"));
				rst.put("sel", rs.getString("cc_eval"));
				rst.put("cc_gbncd", rs.getString("cc_gbncd"));
				rst.put("enabled", "1");					
				rsval.add(rst);
				rst = null;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getApplyInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.getApplyInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getApplyInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.getApplyInfo() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.getApplyInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getApplyInfo() method statement

	public String setSecChkInsertUpdate(ArrayList<HashMap<String,String>> tmp_obj /*, ArrayList<HashMap<String,String>> devuser_ary*/) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet		  rs		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		int 			  param_cnt	  = 1;
		int               i           = 0;
		String            gubun       = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			for ( i = 0 ; i < tmp_obj.size(); i ++)
			{
				strQuery.setLength(0);
				//SR_ID count  select
				strQuery.append("SELECT count(*) as cnt  \n");
				strQuery.append("  FROM CMC0202		     \n");
				strQuery.append(" WHERE CC_SRID   = ?	 \n");
				strQuery.append("   AND CC_SYSCD  = ?	 \n");
				strQuery.append("   AND CC_GBNCD  = ?	 \n");
				strQuery.append("   AND CC_SCMUSER  = ?	 \n");

				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				param_cnt = 1;
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_srid"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_syscd"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_gbncd"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_scmuser"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				strQuery.setLength(0);
				param_cnt = 1;
	        	if ( rs.next() && rs.getInt("cnt") == 0 ){
					strQuery.append("INSERT INTO CMC0202(CC_EDITOR,CC_REQYN,CC_EVAL,       \n");
					strQuery.append("		CC_SRID,CC_SYSCD,CC_GBNCD,CC_SCMUSER,CC_REGDT) \n");
					strQuery.append("VALUES (?,?,?,  ?,?,?,?,SYSDATE)                      \n");		
					
					gubun ="insert";
				} else {
		            strQuery.append("UPDATE CMC0202 SET CC_EDTDT = SYSDATE ,  \n");
					strQuery.append("       CC_EDITOR=?,CC_REQYN=?,CC_EVAL =? \n");
		            strQuery.append(" WHERE CC_SRID = ?                       \n");
		            strQuery.append("   AND CC_SYSCD  = ?				 	  \n");
					strQuery.append("   AND CC_GBNCD  = ?					  \n");
					strQuery.append("   AND CC_SCMUSER  = ?               	  \n");
					
		        	gubun = "update";
				}
				pstmt.close();
	        	//pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_editor"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_reqyn"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_eval"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_srid"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_syscd"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_gbncd"));
				pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_scmuser"));
	        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
	        	rs.close();
			}
			
			strQuery.setLength(0);
			strQuery.append("update cmc0110 set cc_status='D'    \n");
			strQuery.append(" where cc_srid=? and cc_userid=?    \n");
			strQuery.append("   and cc_status not in ('D','9')   \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
			pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from (                  \n");
			strQuery.append("     select distinct cr_syscd from cmr1000  \n");
			strQuery.append("      where cr_itsmid=? and cr_editor=?     \n");
			strQuery.append("        and cr_qrycd = '04'                 \n");
			strQuery.append("        and cr_status in ('8','9')          \n");
			strQuery.append("      minus                                 \n");
			strQuery.append("     select distinct cc_syscd from cmc0202  \n");
			strQuery.append("      where cc_srid=? and cc_scmuser=?)     \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
			pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
			pstmt.setString(3, tmp_obj.get(0).get("cc_srid"));
			pstmt.setString(4, tmp_obj.get(0).get("cc_scmuser"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if ( rs.next() && rs.getInt("cnt") == 0 ) {
				pstmt.close();
				strQuery.setLength(0);
				strQuery.append("update cmc0110 set cc_status='9'    \n");
				strQuery.append(" where cc_srid=? and cc_userid=?    \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
				pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmc0110      \n");
			strQuery.append(" where cc_srid=?                      \n");
			strQuery.append("   and cc_status not in ('3','8','9') \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt("cnt") == 0 ) {
				pstmt.close();
				strQuery.setLength(0);
				strQuery.append("update cmc0100 set cc_status='6'    \n");
				strQuery.append(" where cc_srid=? and cc_status<>'6' \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			rs.close();
			pstmt.close();
			
			int cnt1 = 0;//등록된 체크리스트 카운트(개발자별)
			boolean chkB = false;
			
			//모니터링체크리스트에 등록된  카운트
			strQuery.setLength(0);
			strQuery.append("SELECT A.CC_REQYN,A.CC_EVAL   \n");
			strQuery.append("  FROM CMC0202 A 	           \n");
			strQuery.append(" WHERE A.CC_SRID=?    		   \n");
			strQuery.append(" 	AND A.CC_EDITOR=?    	   \n");
			strQuery.append("   AND A.CC_EVAL IS NOT NULL  \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
	        pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while ( rs.next() ) {
	        	//필수 일때. eval값이 무조건 P
	        	if ( rs.getString("CC_REQYN").equals("Y") && rs.getString("CC_EVAL").equals("P") ) {
	        		++cnt1;
	        	} else if ( (rs.getString("CC_REQYN").equals("N") && rs.getString("CC_EVAL").equals("P")) 
	        		|| (rs.getString("CC_REQYN").equals("N") && rs.getString("CC_EVAL").equals("N")) ){//필수아닐때 eval값이 P 또는 N
	        		++cnt1;
	        	}
			}
	        rs.close();
	        pstmt.close();
	        
	        strQuery.setLength(0);
			strQuery.append("SELECT COUNT(*) CNT2              \n");
			strQuery.append("  FROM CMM0130 A,	    		   \n");
			strQuery.append("      (select distinct CR_SYSCD   \n");
			strQuery.append("         from CMR1000             \n");
			strQuery.append("        where CR_ITSMID=?         \n");
			strQuery.append("          and CR_EDITOR=?         \n");
			strQuery.append("          and cr_qrycd='04'       \n");
			strQuery.append("          and CR_STATUS in ('8', '9')) B \n");
			strQuery.append(" WHERE A.CM_SYSCD=B.CR_SYSCD      \n");
			strQuery.append("   AND A.CM_CLOSEDT is null   	   \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
	        pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        if ( rs.next() && cnt1 == rs.getInt("CNT2") ) {
	        	chkB = true;
			}
			rs.close();
			pstmt.close();
			
			if( chkB ){
				strQuery.setLength(0);
				strQuery.append("update cmc0110 set cc_status='9',cc_enddate=SYSDATE  \n");
				strQuery.append(" where cc_srid=? and cc_userid=?                 \n");
				strQuery.append("   and SYSTESTENDYN(cc_srid,cc_userid,'63',0,'0')='OK' \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, tmp_obj.get(0).get("cc_srid"));
				pstmt.setString(2, tmp_obj.get(0).get("cc_scmuser"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			pstmt = null;
				
			conn.commit();
			conn.close();
			conn = null;

			rs = null;
			pstmt = null;
			conn = null;
			
			return gubun;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() SQLException END ##");
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() Exception END ##");
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setSecChkInsertUpdate() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setSecChkInsertUpdate() method statement			
   
    /** 14.07.23 모니터링 체크리스트 조회를 위한 syscd값 얻어오는 함수 추가
     * @param srID : SRID
     * @return
     * @throws SQLException
     * @throws Exception
     * @author siruen
     */
    public Object[] getSyscd(String srID,String scmUser) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT DISTINCT A.CR_SYSCD,B.CM_SYSMSG   \n");
			strQuery.append("  FROM CMR1000 A, CMM0030 B         	  \n");
			strQuery.append(" WHERE A.CR_ITSMID=?		              \n");
			strQuery.append("   AND A.CR_EDITOR=?   		          \n");
			strQuery.append("   AND A.CR_SYSCD = B.CM_SYSCD		      \n");
			strQuery.append("   AND A.CR_QRYCD = '04'    		      \n");
			strQuery.append("   AND A.CR_STATUS in ('8', '9')	      \n");
			strQuery.append("   AND A.CR_QRYCD in ('03','04','05')	  \n");
			strQuery.append(" order by b.cm_sysmsg              	  \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        pstmt.setString(2, scmUser);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rst = new HashMap<String, String>();
            rst.put("syscd", "00");
			rst.put("sysmsg", "선택하세요");
			rsval.add(rst);
			rst = null;
	        while ( rs.next() ) {
	        	rst = new HashMap<String, String>();
				rst.put("syscd", rs.getString("CR_SYSCD"));
				rst.put("sysmsg", rs.getString("CM_SYSMSG"));
				rsval.add(rst);
				rst = null;
			}
	        
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getSyscd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.getSyscd() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getSyscd() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.getSyscd() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.getSyscd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSyscd() method statement
    
    /** 14.07.23~24 siruen 두개의 시스템일때, 두 개의 시스템 전부 모니터링 체크가 되었는지 조사.
     * @param srID
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public String getMonitorChkOk(String srID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retStr      = "FAIL";
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT COUNT(*) CNT \n");
			strQuery.append("  FROM CMC0100 \n");
			strQuery.append(" WHERE CC_SRID=? AND CC_STATUS='5' \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        if ( rs.next() && rs.getInt("CNT")==0 ) {
	        	rs.close();
		        pstmt.close();
		        conn.close();
		        rs = null;
		        pstmt = null;
		        conn = null;
		        
	        	return retStr;
			}
	        rs.close();
	        pstmt.close();
	        
			int cnt1 = 0;//등록된 체크리스트 카운트 
//			int cnt2 = 0;//전체 체크리스트 카운트
			
			//모니터링체크리스트에 등록된  카운트
			strQuery.setLength(0);
			strQuery.append("SELECT A.CC_REQYN,A.CC_EVAL   \n");
			strQuery.append("  FROM CMC0202 A 	           \n");
			strQuery.append(" WHERE A.CC_SRID=?    		   \n");
			strQuery.append("   AND A.CC_EVAL IS NOT NULL  \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while ( rs.next() ) {
	        	//필수 일때. eval값이 무조건 P
	        	if ( rs.getString("CC_REQYN").equals("Y") && rs.getString("CC_EVAL").equals("P") ) {
	        		++cnt1;
	        	} else if ( (rs.getString("CC_REQYN").equals("N") && rs.getString("CC_EVAL").equals("P")) 
	        		|| (rs.getString("CC_REQYN").equals("N") && rs.getString("CC_EVAL").equals("N")) ){//필수아닐때 eval값이 P 또는 N
	        		++cnt1;
	        	}
			}
	        rs.close();
	        pstmt.close();
	        
	        strQuery.setLength(0);
			strQuery.append("SELECT COUNT(*) CNT2              \n");
			strQuery.append("  FROM CMM0130 A,	    		   \n");
			strQuery.append("      (select distinct CR_SYSCD   \n");
			strQuery.append("         from CMR1000             \n");
			strQuery.append("        where CR_ITSMID=?         \n");
			strQuery.append("          and cr_qrycd='04'       \n");
			strQuery.append("          and CR_STATUS in ('8', '9')) B \n");
			strQuery.append(" WHERE A.CM_SYSCD=B.CR_SYSCD      \n");
			strQuery.append("   AND A.CM_CLOSEDT is null   	   \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        if ( rs.next() && cnt1 == rs.getInt("CNT2") ) {
	        	retStr = "OK";
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return retStr;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getMonitorChkOk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.getMonitorChkOk() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.getMonitorChkOk() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.getMonitorChkOk() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.getMonitorChkOk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getMonitorChkOk() method statement

	//14.07.23 siruen 모니터링 체크가 완료가 되면, 모니터링확인완료 상태로 변경하는 쿼리 추가
	public int setStaCmc0100(String srid, String userid, String status) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			int retInt = 0;
			
			strQuery.setLength(0);
			strQuery.append("update cmc0100 set cc_status='6'  \n");
			strQuery.append(" where cc_srid=?                  \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, srid);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			retInt = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			return retInt;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0600.setStaCmc0100() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0600.setStaCmc0100() SQLException END ##");
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setStaCmc0100() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0600.setStaCmc0100() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0600.setStaCmc0100() Exception END ##");
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setStaCmc0100() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0600.setStaCmc0100() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
