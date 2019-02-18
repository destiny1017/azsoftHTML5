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

public class Cmc0400 { 
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	/** 개발검수 테스트케이스 조회
	 * @param cc_srid
	 * @param reqCD
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getTestCaseList(String cc_srid,String reqCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		String            strItemId   = "";
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			if (reqCD.equals("44")) {//44:통합테스트 준비
				strQuery.append("SELECT a.cc_srid,a.cc_caseseq,a.cc_casename,a.cc_userid,b.cm_username username,   \n");
				strQuery.append("       to_char(a.cc_lastdt,'yyyy/mm/dd') lastdt,a.cc_testgbn,a.cc_scmuser,a.cc_status \n");
				strQuery.append("  FROM cmc0210 a, cmm0040 b                                                       \n");
				strQuery.append(" WHERE a.cc_srid=?  and a.cc_status not in ('3','8')                              \n");
				strQuery.append("   and a.cc_scmuser=b.cm_userid                                                   \n");
				strQuery.append(" ORDER BY a.cc_caseseq                                                            \n");

				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, cc_srid);
			} else {
				strQuery.append("SELECT a.cc_srid, a.cc_caseseq, a.cc_casename, a.cc_userid, b.cm_username username,a.cc_status,   \n");
				strQuery.append("       to_char(a.cc_lastdt,'yyyy/mm/dd') lastdt, d.cc_testgbn, nvl(d.cc_testuser,'') cc_testuser, \n");
				strQuery.append("       e.cm_username testuser,nvl(d.cc_testday,'') cc_testday,d.cc_testrst,a.cc_scmuser,          \n");
				strQuery.append("       SYSTESTENDYN(a.cc_srid,a.cc_scmuser,?,a.cc_caseseq,d.cc_testgbn) caseend,d.cc_worktime     \n");
				strQuery.append("  FROM cmc0210 a, cmm0040 b, cmc0211 d, cmm0040 e                                                 \n");
				strQuery.append(" WHERE a.cc_status not in ('3','8')                                                   			   \n");
				strQuery.append("   AND a.cc_srid = ?                                                                              \n");
				strQuery.append("   AND d.cc_srid = ?                                                                              \n");
				strQuery.append("   AND a.cc_caseseq = d.cc_caseseq(+)                                                             \n");
				strQuery.append("   AND d.cc_status <> '3'			                                                               \n");
				strQuery.append("	AND a.cc_userid = b.cm_userid                                                            	   \n");
				strQuery.append("   AND d.cc_testuser = e.cm_userid(+)                                                             \n");
				strQuery.append(" ORDER BY a.cc_caseseq                                                                            \n");
	
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, reqCD);
				pstmt.setString(2, cc_srid);
				pstmt.setString(3, cc_srid);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				rst = new HashMap<String, String>();				
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
				rst.put("cc_casename", rs.getString("cc_casename"));
				rst.put("cc_userid", rs.getString("cc_userid"));
				rst.put("username", rs.getString("username"));
				rst.put("lastdt", rs.getString("lastdt"));
				rst.put("cc_testgbn", rs.getString("cc_testgbn"));
				rst.put("cc_scmuser", rs.getString("cc_scmuser"));
				if(rs.getString("cc_testgbn").equals("T")){
					rst.put("testgbn", "사전/사후");
				}else if(rs.getString("cc_testgbn").equals("A")){
					rst.put("testgbn", "사후");
				}else{
					rst.put("testgbn", "사전");
				}
				if (!reqCD.equals("44")) {
					rst.put("cc_testuser", rs.getString("cc_testuser"));
					rst.put("testuser", rs.getString("testuser"));
					rst.put("cc_testday", rs.getString("cc_testday"));
					rst.put("caseend", rs.getString("caseend"));
					rst.put("cc_worktime", rs.getString("cc_worktime"));
					if(rs.getString("cc_testday") != null){
						rst.put("testday", rs.getString("cc_testday").substring(0,4)+"/"+rs.getString("cc_testday").substring(4,6)+"/"+rs.getString("cc_testday").substring(6,8));
					}else{
						rst.put("testday", "");
					}
					rst.put("cc_testrst", rs.getString("cc_testrst"));
					
					strItemId = "";
					strQuery.setLength(0);
					strQuery.append("select cc_itemid from cmc0214       \n");
					strQuery.append(" where cc_srid=? and cc_scmuser=?   \n");
					strQuery.append("   and cc_tester=? and cc_caseseq=? \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, cc_srid);
					pstmt2.setString(2, rs.getString("cc_scmuser"));
					pstmt2.setString(3, rs.getString("cc_testuser"));
					pstmt2.setString(4, rs.getString("cc_caseseq"));
					rs2 = pstmt2.executeQuery();
					while (rs2.next()) {
						strItemId = strItemId + ","+rs2.getString("cc_itemid");
					}
					rs2.close();
					pstmt2.close();
					rst.put("cc_itemid", strItemId);
					
					//확인사항 결과 값 조회 시작. cnt==1 오류  cnt==0 정상
					strQuery.setLength(0);
					strQuery.append("SELECT		cc_testrst			\n");
					strQuery.append("FROM		CMC0213				\n");
					strQuery.append("WHERE		cc_srid = ?			\n");
					strQuery.append("AND		cc_caseseq = ?		\n");
					
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, cc_srid);
					pstmt2.setString(2, rs.getString("cc_caseseq"));
					
					rs2 = pstmt2.executeQuery();
					
					boolean errSw = true;
					
					while( rs2.next() ) {
						if( "1".equals(rs2.getString("cc_testrst")) ) {
							errSw = true;
							break;
						} else {
							errSw = false;
						}
					}
					
					if( errSw ) {
						rst.put("testrst", "비정상");
					} else {
						rst.put("testrst", "정상");
					}
					
					rs2.close();
					pstmt2.close();
					//확인사항 결과 값 조회 끝
					
				} else {
					rst.put("caseend", "");
					rst.put("testrst", "");
				}
				rst.put("cc_status", rs.getString("cc_status"));
				rsval.add(rst);
				rst = null;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCaseList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getTestCaseList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCaseList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getTestCaseList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.getTestCaseList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTestCaseList() method statement
	
	public Object[] getTestCase_sub(String cc_srid,String UserId,String reqCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			if (reqCD.equals("44")) {
				strQuery.append("SELECT a.cc_srid, a.cc_caseseq, a.cc_gbncd, a.cc_seqno, a.cc_itemmsg, 		\n");
				strQuery.append("       a.cc_lastdt, a.cc_editor,c.cc_scmuser,c.cc_testgbn                  \n");
				strQuery.append("  FROM cmc0210 c,cmc0212 a                                                 \n");
				strQuery.append(" WHERE c.cc_srid=? and c.cc_status<>'3' and c.cc_srid=a.cc_srid            \n");
				strQuery.append("   AND c.cc_caseseq=a.cc_caseseq and a.cc_status<>'3'                      \n");
				strQuery.append(" ORDER BY a.cc_caseseq,a.cc_seqno                                          \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, cc_srid);
			} else {
				strQuery.append("SELECT a.cc_srid, a.cc_caseseq, a.cc_gbncd, a.cc_seqno, a.cc_itemmsg, 		\n");
				strQuery.append("       a.cc_lastdt, a.cc_editor, c.cc_scmuser,b.cc_testuser,               \n");
				strQuery.append("       d.cm_username,b.cc_testgbn                                          \n");
				strQuery.append("  FROM cmc0210 c,cmc0212 a,cmc0211 b,cmm0040 d                             \n");
				strQuery.append(" WHERE c.cc_srid=? and c.cc_status<>'3'                                    \n");
				strQuery.append("   AND c.cc_srid=b.cc_srid and c.cc_caseseq=b.cc_caseseq                   \n");
				strQuery.append("   and b.cc_status<>'3' and b.cc_testgbn=decode(?,'54','B','55','A',b.cc_testgbn) \n");
				strQuery.append("   and c.cc_srid=a.cc_srid AND c.cc_caseseq=a.cc_caseseq                   \n");
				strQuery.append("   and a.cc_status<>'3'                                                    \n");
				strQuery.append("   and b.cc_testuser=d.cm_userid                                           \n");
				strQuery.append(" ORDER BY a.cc_caseseq,a.cc_seqno,a.cc_gbncd                               \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, cc_srid);
				pstmt.setString(2, reqCD);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while(rs.next()) {
				rst = new HashMap<String, String>();				
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
				rst.put("cc_gbncd", rs.getString("cc_gbncd"));
				rst.put("cc_seqno", rs.getString("cc_seqno"));
				rst.put("cc_itemmsg", rs.getString("cc_itemmsg"));
				rst.put("cc_lastdt", rs.getString("cc_lastdt"));
				rst.put("cc_editor", rs.getString("cc_editor"));
				rst.put("cc_testgbn", rs.getString("cc_testgbn"));
				if (!reqCD.equals("44")) {
					rst.put("selected1", "0");
					rst.put("selected2", "0");
					rst.put("cc_testuser", rs.getString("cc_testuser"));
					if (rs.getString("cc_gbncd").equals("R")) {
						rst.put("testuser", rs.getString("cm_username"));
						strQuery.setLength(0);
						strQuery.append("select cc_testuser,cc_testday,cc_testrst   \n");
						strQuery.append("  from cmc0213               \n");
						strQuery.append(" where cc_srid=?             \n");
						strQuery.append("   and cc_caseseq=?          \n");
						strQuery.append("   and cc_gbncd='R'          \n");
						strQuery.append("   and cc_seqno=?            \n");
						strQuery.append("   and cc_testuser=?         \n");
						strQuery.append("   and cc_testgbn=?          \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, cc_srid);
						pstmt2.setString(2, rs.getString("cc_caseseq"));
						pstmt2.setString(3, rs.getString("cc_seqno"));
						pstmt2.setString(4, rs.getString("cc_testuser"));
						pstmt2.setString(5, rs.getString("cc_testgbn"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							rst.put("cc_testday", rs2.getString("cc_testday"));
							rst.put("cc_testrst", rs2.getString("cc_testrst"));
							if (rs2.getString("cc_testrst") != null) {
								if (rs2.getString("cc_testrst").equals("0")) rst.put("selected1", "1");
								else rst.put("selected2", "1");
							}
						}
						rs2.close();
						pstmt2.close();
					}
				}
				rst.put("cc_scmuser", rs.getString("cc_scmuser"));				
				if(rs.getString("cc_editor").equals(UserId)){
					rst.put("editable", "1");
				}else{
					rst.put("editable", "0");
				}
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCase_sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getTestCase_sub() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCase_sub() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getTestCase_sub() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.getTestCase_sub() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public Object[] getTestCase_sub_tester(String cc_srid) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("SELECT distinct a.cc_srid,a.cc_caseseq,            \n");
			strQuery.append("       a.cc_testuser,b.cm_username cm_username,    \n");
			strQuery.append("       c.cm_deptname,d.cc_scmuser                  \n");
			strQuery.append("  FROM cmc0210 d,cmc0211 a, cmm0040 b, cmm0100 c   \n");
			strQuery.append(" WHERE d.cc_srid=? and d.cc_status<>'3'            \n");
			strQuery.append("   and d.cc_srid=a.cc_srid                         \n");
			strQuery.append("   and d.cc_caseseq=a.cc_caseseq              	 	\n");
			strQuery.append("   AND a.cc_status <> '3'                	  	    \n");
			strQuery.append("   AND a.cc_testuser = b.cm_userid                 \n");
			strQuery.append("   AND b.cm_project = c.cm_deptcd                  \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, cc_srid);
			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				rst = new HashMap<String, String>();
				
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
				rst.put("cc_testuser", rs.getString("cc_testuser"));
				rst.put("cc_scmuser", rs.getString("cc_scmuser"));
				rst.put("cc_username", rs.getString("cm_username"));
				rst.put("cc_deptname", rs.getString("cm_deptname"));
				
				rsval.add(rst);
				rst = null;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			
			return returnObjectArray;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCase_sub_tester() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getTestCase_sub_tester() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTestCase_sub_tester() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getTestCase_sub_tester() Exception END ##");				
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
					ecamsLogger.error("## Cmc0400.getTestCase_sub_tester() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	/**  개발검수 테스트케이스 등록 
	 * @param caseInfo
	 * @param condList
	 * @param checkList
	 * @param testerList
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String set_testCase(HashMap<String,String> caseInfo, ArrayList<HashMap<String,String>> condList, ArrayList<HashMap<String,String>> checkList, 
			ArrayList<HashMap<String,String>> testerList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean			  ins_sw	  = false;//케이스가 신규인지 여부확인
		int 			  max_cnt	  = 0;
		int				  param_cnt   = 1;
		int 		      i 		  = 0;
		boolean           findSw      = false;
		boolean           endSw       = false;
		boolean           editSw      = false;
		int               max_seq     = 0;
		String[]          strItemId   = null;
		String            retMsg      = "OK";
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
			if (caseInfo.get("edityn").equals("Y")) editSw = true;
			else if (caseInfo.get("cc_caseseq").equals("999")) editSw = true;
			if (!caseInfo.get("reqcd").equals("44")) {
				endSw = true;
				for(i=0; checkList.size()>i; i++){
					findSw = false;
					if (caseInfo.get("cc_caseseq").equals(checkList.get(i).get("cc_caseseq")) &&
						checkList.get(i).get("cc_gbncd").equals("R")) findSw = true;
					if (findSw) {
						if (checkList.get(i).get("cc_testuser") == null || checkList.get(i).get("cc_testuser") == null) findSw = true;
						else {
							if (checkList.get(i).get("cc_testuser").equals(caseInfo.get("cc_tester"))) findSw = true;
							else findSw = false;
						}
					}
					if (findSw) {
						if (checkList.get(i).get("selected2").equals("1")) {
							endSw = false;
							break;
						}
					}
				}
			} else if (editSw) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from (              \n");
				strQuery.append("select distinct b.cr_itemid             \n");
				strQuery.append("  from cmr1010 b,cmr1000 a,cmr0020 c    \n");
				strQuery.append(" where c.cr_isrid=? and c.cr_lstusr=?   \n");
				strQuery.append("   and c.cr_status not in ('0','9')     \n");
				strQuery.append("   and a.cr_itsmid=? and a.cr_qrycd='07'\n");
				strQuery.append("   and a.cr_status<>'3'                 \n");
				strQuery.append("   and a.cr_prcdate is not null         \n");
				strQuery.append("   and a.cr_acptno=b.cr_acptno          \n");
				strQuery.append("   and c.cr_itemid=b.cr_baseitem        \n");
				strQuery.append("   and b.cr_qrycd='09'                  \n");
				strQuery.append(" minus                                  \n");
				strQuery.append("select distinct b.cr_itemid             \n");
				strQuery.append("  from cmr1010 b,cmr1000 a,cmr0020 c    \n");
				strQuery.append(" where c.cr_isrid=? and c.cr_lstusr=?   \n");
				strQuery.append("   and c.cr_status not in ('0','9')     \n");
				strQuery.append("   and a.cr_itsmid=? and a.cr_qrycd='07'\n");
				strQuery.append("   and a.cr_status<>'3'                 \n");
				strQuery.append("   and a.cr_prcdate is not null         \n");
				strQuery.append("   and a.cr_acptno=b.cr_acptno          \n");
				strQuery.append("   and c.cr_itemid=b.cr_baseitem        \n");
				strQuery.append("   and b.cr_qrycd<>'09'                 \n");
				strQuery.append(" minus                                  \n");
				strQuery.append("select distinct b.cc_itemid             \n");
				strQuery.append("  from cmc0214 b                        \n");
				strQuery.append(" where b.cc_srid=? and b.cc_scmuser=?   \n");
				strQuery.append(")                                       \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, caseInfo.get("cc_srid"));
				pstmt.setString(2, caseInfo.get("cc_scmuser"));
				pstmt.setString(3, caseInfo.get("cc_srid"));
				pstmt.setString(4, caseInfo.get("cc_srid"));
				pstmt.setString(5, caseInfo.get("cc_scmuser"));
				pstmt.setString(6, caseInfo.get("cc_srid"));
				pstmt.setString(7, caseInfo.get("cc_srid"));
				pstmt.setString(8, caseInfo.get("cc_scmuser"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());          
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					if (rs.getInt("cnt")>0) {
						retMsg = "NO관련모듈에 대해 테스트담당자를 지정하지 않은 건이 있습니다. [관련모듈테스트담당자]버튼을 클릭하여 테스트담당자를 지정한 후 진행하시기 바랍니다.";
					}
				}				
				rs.close();
				pstmt.close();
			}
			if (!retMsg.equals("OK")) {
				rs = null;
				pstmt = null;
				conn.close();
				conn = null;
				return retMsg;
			}
			if (editSw) {
				if(caseInfo.get("cc_caseseq").equals("999")){
					ins_sw = true;
					
					strQuery.setLength(0);
					strQuery.append("SELECT nvl(max(cc_caseseq),'0') maxcnt  \n");
					strQuery.append("  FROM cmc0210                          \n");
					strQuery.append(" WHERE cc_srid = ?                      \n");
					
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, caseInfo.get("cc_srid"));
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					
					if(rs.next()) {
						max_cnt = rs.getInt("maxcnt")+1;
					}
					
					rs.close();
					pstmt.close();
				}else{
					ins_sw = false;
					max_cnt = Integer.parseInt(caseInfo.get("cc_caseseq"));
				}
			} else {
				max_cnt = Integer.parseInt(caseInfo.get("cc_caseseq"));
			}
			
			if( ins_sw ){//개발검수 케이스 신규 등록
				strQuery.setLength(0);
				strQuery.append("INSERT INTO cmc0210(cc_srid, cc_caseseq, cc_casename, cc_userid, cc_testgbn, \n");
				strQuery.append("                    cc_status, cc_creatdt, cc_lastdt,cc_scmuser)		      \n");
				strQuery.append("     VALUES        (      ?,          ?,           ?,         ?,          ?, \n");
				strQuery.append("                        '0',    SYSDATE,     SYSDATE, ?)                     \n");
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);
				pstmt.setString(param_cnt++, caseInfo.get("cc_casename"));
				pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
				pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
				pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			} else if (editSw) {
				strQuery.setLength(0);
				strQuery.append("UPDATE cmc0210 set cc_casename = ?, cc_lastdt = SYSDATE, cc_testgbn = ?  	\n");
				strQuery.append(" WHERE cc_srid = ?                                       					\n");
				strQuery.append("	  AND cc_caseseq = ?                                  					\n");
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_casename"));
				pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}				
			
			if (editSw) {
				strQuery.setLength(0);
				strQuery.append("update cmc0211 set cc_status='8'  \n");	
				strQuery.append(" WHERE cc_srid    = ?             \n");
				strQuery.append("   AND cc_caseseq = ?             \n");	
				strQuery.append("   AND cc_status = '0'            \n");		
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			for(i=0; testerList.size()>i; i++){
				findSw = false;
				if (caseInfo.get("cc_caseseq").equals(testerList.get(i).get("cc_caseseq"))) {
					if ( caseInfo.get("reqcd").equals("44") ) findSw = true;//44:통테 준비
					else if ( testerList.get(i).get("cc_testuser").equals(caseInfo.get("cc_tester")) ) findSw = true;
					ins_sw = false;
					if (findSw) {
						if (editSw) {
							strQuery.setLength(0);
							strQuery.append("select count(*) cnt from cmc0211   \n");	
							strQuery.append(" WHERE cc_srid = ?                 \n");
							strQuery.append("   AND cc_caseseq = ?              \n");
							strQuery.append("   AND cc_testuser=?               \n");		
							strQuery.append("   AND cc_testgbn=?                \n");	
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
							if (caseInfo.get("reqcd").equals("55")) pstmt.setString(param_cnt++, "A");
							else if (caseInfo.get("cc_testgbn").equals("T")) pstmt.setString(param_cnt++, "B");
							else pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							rs = pstmt.executeQuery();
							if (rs.next()) {
								if (rs.getInt("cnt")==0) ins_sw = true;
							}
							rs.close();
							pstmt.close();
						}
						
						//테스트시나리오별담당자 등록 시작
						if ( ins_sw ) {
							strQuery.setLength(0);
							strQuery.append("INSERT INTO cmc0211(cc_srid, cc_caseseq, cc_testuser, cc_testgbn, cc_status,   \n");
							strQuery.append("                    cc_creatdt, cc_lastdt, cc_testday, cc_testrst, cc_worktime)\n");
							strQuery.append("     VALUES        (      ?,          ?,           ?,         ?,        ?,     \n");
							strQuery.append("                    SYSDATE,    SYSDATE, ?, ?, ?)                              \n");
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
							if (caseInfo.get("cc_testgbn").equals("T")) pstmt.setString(param_cnt++, "B");
							else pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
							if (caseInfo.get("reqcd").equals("44")) {
								pstmt.setString(param_cnt++, "0");
								pstmt.setString(param_cnt++, "");
								pstmt.setString(param_cnt++, "");
								pstmt.setString(param_cnt++, "");
							} else {
								if (endSw) pstmt.setString(param_cnt++, "9");
								else pstmt.setString(param_cnt++, "4");
								
								pstmt.setString(param_cnt++, caseInfo.get("cc_testday"));
								pstmt.setString(param_cnt++,caseInfo.get("cc_testrst"));
								pstmt.setString(param_cnt++,caseInfo.get("cc_exptime"));
							}
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							pstmt.executeUpdate();
							pstmt.close();
						} else {
							strQuery.setLength(0);
							strQuery.append("update cmc0211 set cc_status=?     \n");
							if (caseInfo.get("reqcd").equals("44")) {
								strQuery.append(" ,cc_lastdt=SYSDATE            \n");
							} else {
								strQuery.append(" ,cc_testday=?,cc_testrst=?    \n");
								strQuery.append(" ,cc_worktime=?                \n");
								
							}
							strQuery.append(" WHERE cc_srid = ?                 \n");
							strQuery.append("   AND cc_caseseq = ?              \n");
							strQuery.append("   AND cc_testuser=?               \n");		
							strQuery.append("   AND cc_testgbn=?                \n");	
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							if (caseInfo.get("reqcd").equals("44")) pstmt.setString(param_cnt++,"0");
							else {//상태 0:등록,3:삭제,9:테스트완료
								if (endSw) pstmt.setString(param_cnt++, "9");
								else pstmt.setString(param_cnt++, "4");
								
								pstmt.setString(param_cnt++, caseInfo.get("cc_testday"));
								pstmt.setString(param_cnt++,caseInfo.get("cc_testrst"));
								pstmt.setString(param_cnt++,caseInfo.get("cc_exptime"));
							}
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
							if (caseInfo.get("reqcd").equals("55")) pstmt.setString(param_cnt++, "A");
							else if (caseInfo.get("cc_testgbn").equals("T")) pstmt.setString(param_cnt++, "B");
							else pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							pstmt.executeUpdate();
							pstmt.close();
						}
						//테스트시나리오별담당자 등록 끝
						
						
//						if ( !caseInfo.get("reqcd").equals("55") && caseInfo.get("cc_testgbn").equals("T") ) {
//							ins_sw = false;
//							if (editSw) {
//								strQuery.setLength(0);
//								strQuery.append("select count(*) cnt from cmc0211   \n");	
//								strQuery.append(" WHERE cc_srid = ?                 \n");
//								strQuery.append("   AND cc_caseseq = ?              \n");
//								strQuery.append("   AND cc_testuser=?               \n");		
//								strQuery.append("   AND cc_testgbn='A'              \n");	
//								param_cnt = 1;
//								pstmt = conn.prepareStatement(strQuery.toString());
//								//pstmt = new LoggableStatement(conn,strQuery.toString());
//								pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//								pstmt.setInt(param_cnt++, max_cnt);
//								pstmt.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
//								//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//								rs = pstmt.executeQuery();
//								if (rs.next()) {
//									if (rs.getInt("cnt")==0) ins_sw = true;
//								}
//								rs.close();
//								pstmt.close();
//							}
//							
//							if (ins_sw) {		
//								strQuery.setLength(0);
//								strQuery.append("INSERT INTO cmc0211(cc_srid, cc_caseseq, cc_testuser, cc_testgbn, cc_status,   \n");
//								strQuery.append("                    cc_creatdt, cc_lastdt, cc_testday, cc_testrst,cc_worktime) \n");
//								strQuery.append("     VALUES        (      ?,          ?,           ?,         'A',        ?,   \n");
//								strQuery.append("                    SYSDATE,    SYSDATE, ?, ?,?)                               \n");
//								
//								param_cnt = 1;
//								pstmt2 = conn.prepareStatement(strQuery.toString());
//								pstmt2 = new LoggableStatement(conn,strQuery.toString());
//								pstmt2.setString(param_cnt++, caseInfo.get("cc_srid"));
//								pstmt2.setInt(param_cnt++, max_cnt);
//								pstmt2.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
//								if (caseInfo.get("reqcd").equals("44")) {
//									pstmt2.setString(param_cnt++, "0");
//									pstmt2.setString(param_cnt++, "");
//									pstmt2.setString(param_cnt++, "");
//									pstmt2.setString(param_cnt++, "");
//								} else {
//									if (endSw) pstmt2.setString(param_cnt++, "9");
//									else pstmt2.setString(param_cnt++, "4");
//									
//									pstmt2.setString(param_cnt++, caseInfo.get("cc_testday"));
//									pstmt2.setString(param_cnt++,caseInfo.get("cc_testrst"));
//									pstmt2.setString(param_cnt++,caseInfo.get("cc_exptime"));
//								}
//								ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
//								pstmt2.executeUpdate();
//								pstmt2.close();
//							} else {
//								strQuery.setLength(0);
//								strQuery.append("update cmc0211 set cc_status=?     \n");
//								if (caseInfo.get("reqcd").equals("44")) {
//									strQuery.append(" ,cc_lastdt=SYSDATE            \n");
//								} else {
//									strQuery.append(" ,cc_testday=?,cc_testrst=?    \n");
//									strQuery.append(" ,cc_worktime=?                \n");
//								}
//								strQuery.append(" WHERE cc_srid = ?                 \n");
//								strQuery.append("   AND cc_caseseq = ?              \n");
//								strQuery.append("   AND cc_testuser=?               \n");		
//								strQuery.append("   AND cc_testgbn='A'              \n");	
//								param_cnt = 1;
//								pstmt = conn.prepareStatement(strQuery.toString());
//								pstmt = new LoggableStatement(conn,strQuery.toString());
//								if (caseInfo.get("reqcd").equals("44")) pstmt.setString(param_cnt++,"0");
//								else {
//									if (endSw) pstmt.setString(param_cnt++, "9");
//									else pstmt.setString(param_cnt++, "4");
//									
//									pstmt.setString(param_cnt++, caseInfo.get("cc_testday"));
//									pstmt.setString(param_cnt++,caseInfo.get("cc_testrst"));
//									pstmt.setString(param_cnt++,caseInfo.get("cc_exptime"));
//								}
//								pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//								pstmt.setInt(param_cnt++, max_cnt);
//								pstmt.setString(param_cnt++, testerList.get(i).get("cc_testuser"));
//								ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//								pstmt.executeUpdate();
//								pstmt.close();
//							}	
//						}
					}
				}
			}

			//테스트 조건 및 확인사항 등록 시작
			if (editSw) {
				strQuery.setLength(0);
				strQuery.append("update cmc0211 set cc_status='3'  \n");	
				strQuery.append("       ,cc_lastdt=SYSDATE         \n");
				strQuery.append(" WHERE cc_srid = ?       \n");
				strQuery.append("   AND cc_caseseq = ?    \n");	
				strQuery.append("   AND cc_status = '8'   \n");		
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
				
				strQuery.setLength(0);
				strQuery.append("update cmc0212 set cc_status='8'		 \n");
				strQuery.append(" WHERE cc_srid = ?                      \n");
				strQuery.append("   AND cc_caseseq = ?                   \n");
				strQuery.append("   AND cc_status='0'                    \n");	
				strQuery.append("   AND cc_gbncd='C'                     \n");					
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);					
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();				
				
				for(i=0; condList.size()>i; i++){
					if (caseInfo.get("cc_caseseq").equals(condList.get(i).get("cc_caseseq")) &&
						condList.get(i).get("cc_gbncd").equals("C")) {
						if (condList.get(i).get("cc_seqno").equals("999")) {
							ins_sw = true;
						} else ins_sw = false;
						
						strQuery.setLength(0);
						param_cnt = 1;
						if (ins_sw) {
							strQuery.append("INSERT INTO cmc0212(cc_srid, cc_caseseq, cc_gbncd, cc_seqno, cc_itemmsg,  \n");
							strQuery.append("                    cc_lastdt, cc_editor, cc_status)                      \n");
							strQuery.append(" (select ?,?,'C',nvl(max(cc_seqno),0)+1,?,SYSDATE,?,'0'                   \n");
							strQuery.append("    from cmc0212             \n");
							strQuery.append("   where cc_srid=?           \n");
							strQuery.append("     and cc_caseseq=?        \n");
							strQuery.append("     and cc_gbncd='C')       \n");
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, condList.get(i).get("cc_itemmsg"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
						} else {
							strQuery.append("update cmc0212 set cc_itemmsg=?,      \n");
							strQuery.append("                   cc_lastdt=SYSDATE, \n");
							strQuery.append("                   cc_editor=?,       \n");
							strQuery.append("                   cc_status='0'      \n");
							strQuery.append(" where cc_srid=?           \n");
							strQuery.append("   and cc_caseseq=?        \n");
							strQuery.append("   and cc_gbncd='C'        \n");
							strQuery.append("   and cc_seqno=?          \n");
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, condList.get(i).get("cc_itemmsg"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, condList.get(i).get("cc_seqno"));
						}
						ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						pstmt.executeUpdate();
						pstmt.close();
					}
				}

				strQuery.setLength(0);
				strQuery.append("update cmc0212 set cc_status='3'  \n");	
				strQuery.append("       ,cc_lastdt=SYSDATE         \n");
				strQuery.append(" WHERE cc_srid = ?       \n");
				strQuery.append("   AND cc_caseseq = ?    \n");	
				strQuery.append("   and cc_gbncd='C'      \n");
				strQuery.append("   AND cc_status = '8'   \n");		
				param_cnt = 1;
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
				pstmt.setInt(param_cnt++, max_cnt);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			//테스트 조건 및 확인사항 등록 끝
			
			for(i=0; checkList.size()>i; i++){
				findSw = false;
				if (caseInfo.get("cc_caseseq").equals(checkList.get(i).get("cc_caseseq")) &&
					checkList.get(i).get("cc_gbncd").equals("R")) findSw = true;
				if (findSw) {
					if (checkList.get(i).get("cc_testuser") == null || checkList.get(i).get("cc_testuser") == null) findSw = true;
					else {
						if (checkList.get(i).get("cc_testuser").equals(caseInfo.get("cc_tester"))) findSw = true;
						else findSw = false;
					}
				}
				
				if (findSw) {
					if (editSw) {
						if (checkList.get(i).get("cc_seqno").equals("999")) {
							ins_sw = true;
						} else ins_sw = false;
						
						if (ins_sw) {
							strQuery.setLength(0);
							strQuery.append("select (nvl(max(cc_seqno),0)+1) maxseq from cmc0212  \n");
							strQuery.append("  where cc_srid=?           \n");
							strQuery.append("    and cc_caseseq=?        \n");
							strQuery.append("    and cc_gbncd='R'        \n");
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							rs = pstmt.executeQuery();
							if (rs.next()) {
								max_seq = rs.getInt("maxseq");
							}
							rs.close();
							pstmt.close();
							
							strQuery.setLength(0);
							strQuery.append("INSERT INTO cmc0212(cc_srid, cc_caseseq, cc_gbncd, cc_seqno, cc_itemmsg,  \n");
							strQuery.append("                    cc_lastdt, cc_editor, cc_status)                      \n");
							strQuery.append(" values (?, ?,'R',?,?,SYSDATE,?,'0')    \n");
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setInt(param_cnt++, max_seq);
							pstmt.setString(param_cnt++, checkList.get(i).get("cc_itemmsg"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							pstmt.executeUpdate();
							pstmt.close();
						} else {
							strQuery.setLength(0);
							strQuery.append("update cmc0212 set cc_itemmsg=?,      \n");
							strQuery.append("                   cc_lastdt=SYSDATE, \n");
							strQuery.append("                   cc_editor=?        \n");
							strQuery.append(" where cc_srid=?           \n");
							strQuery.append("   and cc_caseseq=?        \n");
							strQuery.append("   and cc_gbncd='R'        \n");
							strQuery.append("   and cc_seqno=?          \n");
							param_cnt = 1;
							//pstmt = conn.prepareStatement(strQuery.toString());
							pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(param_cnt++, checkList.get(i).get("cc_itemmsg"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
							pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
							pstmt.setInt(param_cnt++, max_cnt);
							pstmt.setString(param_cnt++, checkList.get(i).get("cc_seqno"));
							ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							pstmt.executeUpdate();
							pstmt.close();
							max_seq = Integer.parseInt(checkList.get(i).get("cc_seqno"));
						}
					} else max_seq = Integer.parseInt(checkList.get(i).get("cc_seqno"));
					
					//통테준비가 아닐때  확인사항별 테스트결과 등록 시작. 삭제 후 insert
					if (!caseInfo.get("reqcd").equals("44")) {
						strQuery.setLength(0);
						strQuery.append("delete cmc0213             \n");
						strQuery.append(" where cc_srid=?           \n");
						strQuery.append("   and cc_caseseq=?        \n");
						strQuery.append("   and cc_gbncd='R'        \n");
						strQuery.append("   and cc_seqno=?          \n");
						strQuery.append("   and cc_testuser=?       \n");
						strQuery.append("   and cc_scmuser=?       \n");							
						param_cnt = 1;
						//pstmt = conn.prepareStatement(strQuery.toString());
						pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
						pstmt.setInt(param_cnt++, max_cnt);
						pstmt.setInt(param_cnt++, max_seq);
						pstmt.setString(param_cnt++, caseInfo.get("cc_tester"));
						pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
						ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						pstmt.executeUpdate();
						pstmt.close();
						
						strQuery.setLength(0);
						strQuery.append("INSERT INTO cmc0213(cc_srid, cc_caseseq, cc_gbncd, cc_seqno, cc_testgbn,  \n");
						strQuery.append("                    cc_lastdt, cc_testuser,cc_testday,cc_testrst,cc_scmuser) \n");
						strQuery.append("     VALUES        (?,?,'R',?,?,   SYSDATE,?,?,?,?) \n");
						param_cnt = 1;
						//pstmt = conn.prepareStatement(strQuery.toString());
						pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
						pstmt.setInt(param_cnt++, max_cnt);
						pstmt.setInt(param_cnt++, max_seq);
						if (caseInfo.get("reqcd").equals("54")) pstmt.setString(param_cnt++,"B");
						else pstmt.setString(param_cnt++,"A");
						//20140723 neo. 개발검수는 개발자 단위로 체크해야함. cc_testuser 컬럼에 개발자ID가 셋팅 되도록 재사용.
						pstmt.setString(param_cnt++, caseInfo.get("cc_tester"));
						pstmt.setString(param_cnt++, caseInfo.get("cc_testday"));
						if (checkList.get(i).get("selected1").equals("1")) pstmt.setString(param_cnt++,"0");
						else pstmt.setString(param_cnt++,"1");
						pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
						ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						pstmt.executeUpdate();
						pstmt.close();
					}
					//통테준비가 아닐때  확인사항별 테스트결과 등록 끝
				}
			}
			
			if (!caseInfo.get("reqcd").equals("44")) {
				if (caseInfo.get("itemid") != null && caseInfo.get("itemid") != "") {
					strItemId = caseInfo.get("itemid").split(",");

					strQuery.setLength(0);
					strQuery.append("delete cmc0214                       \n");
					strQuery.append(" where cc_srid=? and cc_scmuser=?    \n");
					strQuery.append("   and cc_tester=? and cc_caseseq=?  \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					param_cnt = 1;
					pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
					pstmt.setString(param_cnt++,caseInfo.get("cc_scmuser"));
					pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
					pstmt.setInt(param_cnt++, max_cnt);
					pstmt.executeUpdate();
					pstmt.close();
					
					for (i=0;strItemId.length>i;i++) {
						strQuery.setLength(0);
						strQuery.append("insert into cmc0214                 \n");
						strQuery.append(" (cc_srid,cc_scmuser,cc_itemid,     \n");
						strQuery.append("  cc_tester,cc_lastdt,cc_caseseq)   \n");
						strQuery.append("(select cc_srid,cc_scmuser,cc_itemid,\n");
						strQuery.append("        cc_tester,SYSDATE,?         \n");
						strQuery.append("   from cmc0214                     \n");
						strQuery.append("  where cc_srid=? and cc_scmuser=?  \n");
						strQuery.append("    and cc_itemid=? and cc_tester=? \n");
						strQuery.append("    and cc_caseseq=999)             \n");
						//pstmt = conn.prepareStatement(strQuery.toString());
						pstmt = new LoggableStatement(conn,strQuery.toString());
						param_cnt = 1;
						pstmt.setInt(param_cnt++, max_cnt);
						pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
						pstmt.setString(param_cnt++,caseInfo.get("cc_scmuser"));
						pstmt.setString(param_cnt++, strItemId[i]);
						pstmt.setString(param_cnt++, caseInfo.get("cc_userid"));
						ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						pstmt.executeUpdate();
						pstmt.close();
					}
				}
				if (caseInfo.get("reqcd").equals("54")) {
					strQuery.setLength(0);
					strQuery.append("update cmc0110 set cc_status='4'  \n");
					strQuery.append(" where cc_srid=? and cc_userid=?  \n");
					strQuery.append("   and cc_status in ('2','D')     \n");
					param_cnt = 1;
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
					pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();
					
//					strQuery.setLength(0);
//				    strQuery.append("update cmc0110 set cc_status='9',cc_enddate=SYSDATE  \n");
//				    strQuery.append(" where cc_srid=? and cc_userid=?                 \n");
//				    strQuery.append("   and SYSTESTENDYN(cc_srid,cc_userid,'54',0,'0')='OK' \n");
//				    strQuery.append("   and PROGEDIT_YN(cc_srid)='N'                  \n");
//				    pstmt = conn.prepareStatement(strQuery.toString());
//				    param_cnt = 1;
//					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn,strQuery.toString());
//					pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//					pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
//					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//					pstmt.executeUpdate();
//					pstmt.close();
				}
				//55:사후통테
//				else if (caseInfo.get("reqcd").equals("55")) {
//					strQuery.setLength(0);
//					strQuery.append("update cmc0110 set cc_status='A'  \n");
//					strQuery.append(" where cc_srid=? and cc_userid=?  \n");
//					strQuery.append("   and cc_status not in ('A','9') \n");
//					param_cnt = 1;
//					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn,strQuery.toString());
//					pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//					pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
//					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//					pstmt.executeUpdate();
//					pstmt.close();
//					
//				    strQuery.setLength(0);
//				    strQuery.append("update cmc0110 set cc_status='9',cc_enddate=SYSDATE  \n");
//				    strQuery.append(" where cc_srid=? and cc_userid=?                 \n");
//				    strQuery.append("   and SYSTESTENDYN(cc_srid,cc_userid,'55',0,'0')='OK' \n");
//				    pstmt = conn.prepareStatement(strQuery.toString());
//				    param_cnt = 1;
//					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn,strQuery.toString());
//					pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//					pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
//					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//					pstmt.executeUpdate();
//					pstmt.close();
//				}
			}
			
			//개발자 상태가  3:폐기 8:진행중단 9:모니터링완료 일때  SR상태를 5:적용확인중으로 변경
			strQuery.setLength(0);
			strQuery.append("update cmc0100 set cc_status='5'      \n");
			strQuery.append(" where cc_srid=?                      \n");
			strQuery.append("   and not exists (select 1 from cmc0110  \n");
			strQuery.append("                    where cc_srid=?       \n");
			strQuery.append("                      and cc_status not in ('3','8','9')) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, caseInfo.get("cc_srid"));
			pstmt.setString(2, caseInfo.get("cc_srid"));
			pstmt.executeUpdate();
			pstmt.close();
			conn.commit();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			if (retMsg.equals("OK")) {
				retMsg = retMsg + Integer.toString(max_cnt);
			}
			return retMsg;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.set_testCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmc0400.set_testCase() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.set_testCase() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.set_testCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}			
			ecamsLogger.error("## Cmc0400.set_testCase() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.set_testCase() Exception END ##");				
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
					ecamsLogger.error("## Cmc0400.set_testCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String del_testCase(HashMap<String,String> caseInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  param_cnt   = 1;
		HashMap<String,String> rst = new HashMap<String,String>();
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("DELETE cmc0213				\n");
			strQuery.append(" WHERE cc_srid = ?      	\n");
			strQuery.append("   AND cc_caseseq = ?   	\n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("UPDATE cmc0211 set cc_status = '3', cc_lastdt = SYSDATE				\n");
			strQuery.append(" WHERE cc_srid = ?      	\n");
			strQuery.append("   AND cc_caseseq = ?   	\n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
					
			strQuery.setLength(0);
			strQuery.append("update cmc0210 set cc_status = '3', cc_lastdt = SYSDATE	\n");
			strQuery.append("where cc_srid = ?   	                                    \n");
			strQuery.append("  and cc_caseseq = ?   	                                 \n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();	
		    
	        Cmc0100 cmc0100 = new Cmc0100();
	        rst = new HashMap<String,String>();
			rst.put("cc_srid", caseInfo.get("cc_srid"));
			rst.put("cc_division", "44");
			rst.put("cc_caseseq", caseInfo.get("cc_caseseq"));
			cmc0100.fileDelete(rst,conn);
			conn.close();
			pstmt = null;
			conn = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.del_testCase() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.del_testCase() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.del_testCase() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.del_testCase() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.del_testCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	/**  개발검수 반려
		CMC0110 상태 7 -> 2 변경
		CMC0210 상태 0 -> 3 변경  where cc_srid , cc_status = '0' , cc_scmuser = '개발자ID'
		CMC0211 상태 4 -> 3  또는 9 -> 3 변경  where cc_srid , cc_status in ('4','9') , cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid and cc_scmuser = '개발자ID')
		CMC0213 삭제 where cc_srid , cc_scmuser = '개발자ID' --cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid and cc_scmuser = '개발자ID')
		CMC0101 첨부파일 삭제. 테스트케이스별 삭제진행.
	 * @param caseInfo {cc_srid:SRID} {cc_scmuser:개발자ID} {cc_editor:검수자ID}
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String rejectDevCase(HashMap<String,String> caseInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		//ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  param_cnt   = 1;
		//HashMap<String,String> rst = new HashMap<String,String>();
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			//CMC0110 개발자 상태값 변경
			strQuery.setLength(0);
			strQuery.append("UPDATE cmc0110 set cc_status = 'A', cc_lastdate = SYSDATE, cc_editor = ? \n");
			strQuery.append(" WHERE cc_srid = ? \n");
			strQuery.append("   AND cc_userid = ? \n");//개발자ID
			strQuery.append("   AND cc_status = '7' \n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_editor"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			//CMC0210 개발검수 테스트케이스 상태값 변경
			strQuery.setLength(0);
			strQuery.append("UPDATE cmc0210 set cc_status = '2', cc_lastdt = SYSDATE \n");
			strQuery.append(" WHERE cc_srid = ?     \n");
			strQuery.append("   AND cc_scmuser = ?  \n");
			strQuery.append("   AND cc_status = '0' \n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			//CMC0211 테스트시나리오별담당자 상태값 변경
			strQuery.setLength(0);
			strQuery.append("UPDATE cmc0211 set cc_status = '2', cc_lastdt = SYSDATE \n");
			strQuery.append(" WHERE cc_srid = ?     \n");
			strQuery.append("   AND cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid = ? and cc_scmuser = ?)  \n");
			strQuery.append("   AND cc_status in ('4','9') \n");
			param_cnt = 1;
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
			pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			//CMC0213 테스트확인사항별 테스트결과 삭제 where cc_srid , cc_scmuser = '개발자ID'
//			strQuery.setLength(0);
//			strQuery.append("DELETE cmc0213			\n");
//			strQuery.append(" WHERE cc_srid = ?     \n");
//			strQuery.append("   AND cc_scmuser = ?  \n");
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			pstmt.executeUpdate();
//			pstmt.close();
			
			//CMC0101 첨부파일 삭제. 테스트케이스별 삭제진행.
//			Cmc0100 cmc0100 = new Cmc0100();
//			strQuery.setLength(0);
//			strQuery.append("SELECT a.cc_caseseq  \n");
//			strQuery.append("  FROM cmc0210 a, cmc0101 b \n");
//			strQuery.append(" WHERE a.cc_srid = ?     \n");
//			strQuery.append("   AND a.cc_scmuser = ?  \n");
//			strQuery.append("   AND a.cc_status = '0'  \n");
//			strQuery.append("   AND b.cc_srid = a.cc_srid  \n");
//			strQuery.append("   AND b.cc_caseseq = a.cc_caseseq  \n");
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_scmuser"));
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			rs = pstmt.executeQuery();
//			while ( rs.next() ) {
//		        rst = new HashMap<String,String>();
//				rst.put("cc_srid", caseInfo.get("cc_srid"));
//				rst.put("cc_division", "54");//54:개발검수
//				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
//				cmc0100 = new Cmc0100();
//				cmc0100.fileDelete(rst,conn);
//				rst = null;
//				cmc0100 = null;
//			}
//			cmc0100 = null;
//			rs.close();
//			pstmt.close();
			
			HashMap<String,String> tmp_obj = new HashMap<String, String>();
			tmp_obj.put("SR_ID", caseInfo.get("cc_srid"));
			tmp_obj.put("USER_ID", caseInfo.get("cc_scmuser"));//보내는사람 ID
			tmp_obj.put("QRY_CD", "54");
			tmp_obj.put("CR_CONUSR", caseInfo.get("cc_editor"));//받는사람 ID
			tmp_obj.put("CR_ACPTNO", "999999999999");//임시번호
			tmp_obj.put("CR_NOTIGBN", "CN");//알림구분(NOTIGBN) [CO]결재알림,[ED]완료알림,[CN]반려알림,[ER]오류알림
			Cmc0100 cmc0100 = new Cmc0100();
			String retNOTI = cmc0100.insertCMR9910ForNotice(tmp_obj, conn);
			cmc0100 = null;
			ecamsLogger.error("Cmc0400.java setStaCmc0110: 개발검수 반려 CMR9910 inert 결과값["+retNOTI+"]");
			
			
			conn.commit();
			conn.close();
			//rs = null;
			pstmt = null;
			conn = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.rejectDevCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmc0400.rejectDevCase() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.rejectDevCase() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.rejectDevCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmc0400.rejectDevCase() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.rejectDevCase() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			//if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.rejectDevCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	/**  테스트케이스 반려 이벤트
	 * @param caseInfo
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String reject_testCase(HashMap<String,String> caseInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
//		int				  param_cnt   = 1;
//		HashMap<String,String> rst = new HashMap<String,String>();
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();

//			strQuery.setLength(0);
//			strQuery.append("DELETE cmc0213				\n");
//			strQuery.append(" WHERE cc_srid = ?      	\n");
//			strQuery.append("   AND cc_caseseq = ?   	\n");
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			pstmt.executeUpdate();
//			pstmt.close();
//			
//			strQuery.setLength(0);
//			strQuery.append("UPDATE cmc0211 set cc_status = '3', cc_lastdt = SYSDATE				\n");
//			strQuery.append(" WHERE cc_srid = ?      	\n");
//			strQuery.append("   AND cc_caseseq = ?   	\n");
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));			
//			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			pstmt.executeUpdate();
//			pstmt.close();
//					
//			strQuery.setLength(0);
//			strQuery.append("update cmc0210 set cc_status = '3', cc_lastdt = SYSDATE	\n");
//			strQuery.append("where cc_srid = ?   	                                    \n");
//			strQuery.append("  and cc_caseseq = ?   	                                 \n");
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//			pstmt.executeUpdate();
//			pstmt.close();	
//		    
//	        Cmc0100 cmc0100 = new Cmc0100();
//	        rst = new HashMap<String,String>();
//			rst.put("cc_srid", caseInfo.get("cc_srid"));
//			rst.put("cc_division", "44");
//			rst.put("cc_caseseq", caseInfo.get("cc_caseseq"));
//			boolean okSw = cmc0100.fileDelete(rst,conn);
			
			conn.close();
			pstmt = null;
			conn = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.reject_testCase() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.reject_testCase() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.reject_testCase() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.reject_testCase() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.reject_testCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
//	public String set_testCaseRst(HashMap<String,String> caseInfo) throws SQLException, Exception {
//		Connection        conn        = null;
//		PreparedStatement pstmt       = null;
//		PreparedStatement pstmt2      = null;
//		ResultSet         rs          = null;
//		StringBuffer      strQuery    = new StringBuffer();
//		int				  param_cnt   = 1;
		
//		ConnectionContext connectionContext = new ConnectionResource();		
//		
//		try {
//			conn = connectionContext.getConnection();
//			//미완성
//			strQuery.setLength(0);
//			strQuery.append("UPDATE cmc0211 set cc_status = '9', cc_lastdt = SYSDATE, cc_testday = ?, cc_testrst = ?	\n");
//			strQuery.append("WHERE cc_srid = ?                                                                      	\n");
//			strQuery.append("  AND cc_caseseq = ?                                                                   	\n");
//			strQuery.append("  AND cc_testuser = ?                                                                  	\n");
//			strQuery.append("  AND cc_testgbn = ?                                                                   	\n");
//			
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testday"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testrst"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testuser"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
//			
//			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			pstmt.executeUpdate();
//			pstmt.close();
//			
//
//			strQuery.append("UPDATE cmc0213 set cc_lastdt = SYSDATE, cc_testday = ?, cc_testrst = ?	\n");
//			strQuery.append("WHERE cc_srid = ?                                                     	\n");
//			strQuery.append("  AND cc_caseseq = ?                                                  	\n");
//			strQuery.append("  AND cc_gbncd = 'R'                                                  	\n");
//			strQuery.append("  AND cc_testgbn = ?                                                  	\n");
//			strQuery.append("  AND cc_testuser = ?                                                 	\n");
//			strQuery.append("  AND cc_seqno = ?                                                    	\n");
//
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testuser"));
//			
//			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			pstmt.executeUpdate();
//			pstmt.close();
//			
//			
//			strQuery.setLength(0);
//			strQuery.append("SELECT count(*) cnt   		\n");
//			strQuery.append("  FROM cmc0211          	\n");
//			strQuery.append(" WHERE cc_srid = ?      	\n");
//			strQuery.append("   AND cc_caseseq = ?  	\n");
//			strQuery.append("   AND cc_testgbn = ?  	\n");
//
//			param_cnt = 1;
//			pstmt = conn.prepareStatement(strQuery.toString());
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(param_cnt++, caseInfo.get("cc_srid"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//			pstmt.setString(param_cnt++, caseInfo.get("cc_testgbn"));
//			
//			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//			rs = pstmt.executeQuery();
//			if(rs.next()){
//				if(rs.getInt("cnt") == 0){
//					
//					strQuery.setLength(0);
//					strQuery.append("update cmc0210 set cc_status = '8', cc_lastdt = SYSDATE	\n");
//					strQuery.append("where cc_srid = ?   	                                    \n");
//					strQuery.append("  and cc_caseseq = ?   	                                 \n");
//					
//					param_cnt = 1;
//					pstmt2 = conn.prepareStatement(strQuery.toString());
//					//pstmt = new LoggableStatement(conn,strQuery.toString());
//					pstmt2.setString(param_cnt++, caseInfo.get("cc_srid"));
//					pstmt2.setString(param_cnt++, caseInfo.get("cc_caseseq"));
//					pstmt2.close();
//				}
//			}			
//			rs.close();
//			pstmt.close();
//			
//			strQuery.setLength(0);
//			strQuery.append("update cmc0110 set cc_status=decode(?,'B','4','A')  \n");
//			strQuery.append(" where cc_srid=?                                    \n");
//			strQuery.append("   and cc_userid=?                                  \n");
//			if (caseInfo.get("cc_testgbn").equals("B")) {
//				strQuery.append(" and cc_status in ('2','D')                     \n");
//			} else {
//				strQuery.append(" and cc_status not in ('A','9')                 \n");
//			}
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt.setString(1, caseInfo.get("cc_testgbn"));
//			pstmt.setString(2, caseInfo.get("cc_srid"));
//			pstmt.setString(3, caseInfo.get("cc_scmuser"));
//			pstmt.executeUpdate();
//			pstmt.close();
//			
//			conn.close();
//			
//			rs = null;
//			pstmt = null;
//			conn = null;
//			
//			return "OK";
//			
//		} catch (SQLException sqlexception) {
//			sqlexception.printStackTrace();
//			ecamsLogger.error("## Cmc0100.insert_testCase() SQLException START ##");
//			ecamsLogger.error("## Error DESC : ", sqlexception);	
//			ecamsLogger.error("## Cmc0100.insert_testCase() SQLException END ##");			
//			throw sqlexception;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## Cmc0100.insert_testCase() Exception START ##");				
//			ecamsLogger.error("## Error DESC : ", exception);	
//			ecamsLogger.error("## Cmc0100.insert_testCase() Exception END ##");				
//			throw exception;
//		}finally{
//			if (strQuery != null) 	strQuery = null;
//			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (conn != null){
//				try{
//					ConnectionResource.release(conn);
//				}catch(Exception ex3){
//					ecamsLogger.error("## Cmc0100.insert_testCase() connection release exception ##");
//					ex3.printStackTrace();
//				}
//			}
//		}
//	}
	public Object[] getTesterList(String cc_srid,String reqCD,String scmUser,String status) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT a.cc_testuser,b.cm_username      		    \n");
			strQuery.append("  FROM cmc0210 c,cmc0211 a, cmm0040 b			    \n");
			strQuery.append(" WHERE	c.cc_srid=? and c.cc_scmuser=?				\n");
			if (reqCD.equals("54")) strQuery.append("and c.cc_testgbn in ('B','T')      \n");
			else if (reqCD.equals("55")) strQuery.append("and c.cc_testgbn in ('A','T') \n");
			strQuery.append(" 	AND c.cc_status<>'3'        					\n");
			strQuery.append(" 	AND c.cc_srid=a.cc_srid     					\n");
			strQuery.append(" 	AND c.cc_caseseq=a.cc_caseseq					\n");
			strQuery.append(" 	AND a.cc_testuser = b.cm_userid					\n");
			strQuery.append(" 	AND a.cc_status<>'3'    					    \n");
			strQuery.append(" group by a.cc_testuser,b.cm_username		        \n");
			if (status.equals("2") || status.equals("4")) {
				strQuery.append("union   \n");
				strQuery.append("select b.cm_userid cc_testuser,b.cm_username   \n");
				strQuery.append("  from cmm0040 b,cmc0214 c                     \n");
				strQuery.append(" where c.cc_srid=? and c.cc_scmuser=?          \n");
				strQuery.append("   and c.cc_tester=b.cm_userid                 \n");
				strQuery.append(" group by b.cm_userid,b.cm_username            \n");
			}
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, cc_srid);
			pstmt.setString(2, scmUser);
			if (status.equals("2") || status.equals("4"))  {
				pstmt.setString(3, cc_srid);
				pstmt.setString(4, scmUser);
			}			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			rst = new HashMap<String, String>();
			rst.put("cc_tester", "00");
			rst.put("tester", "전체");
			rst.put("cc_scmuser",scmUser);
			rsval.add(rst);
			rst = null;
			
			while(rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cc_scmuser", scmUser);
				rst.put("cc_tester", rs.getString("cc_testuser"));
				rst.put("tester", rs.getString("cm_username"));
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmc0214    \n");
				strQuery.append(" where cc_srid=? and cc_scmuser=?   \n");
				strQuery.append("  and cc_tester=?                   \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, cc_srid);
				pstmt2.setString(2, scmUser);
				pstmt2.setString(3, rs.getString("cc_testuser"));
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					if (rs2.getInt("cnt")>0) rst.put("relatprog", "Y");
					else rst.put("relatprog", "N");
				}
				rs2.close();
				pstmt2.close();
				rsval.add(rst);
				rst = null;
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTesterList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getTesterList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getTesterList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getTesterList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.getTesterList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of getTesterList() method statement
	public Object[] chkExcelList(ArrayList<HashMap<String,String>> FileList,String ReqCd,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();		
		
		boolean           errSw       = false;
		HashMap<String, String> rst	  = null;
//		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		String[]          strTester   = null;
		int               j = 0;
		
		try {
			conn = connectionContext.getConnection();
			
			for (int i=0 ; i<FileList.size() ; i++)	{
				errSw = false;
				rst = new HashMap<String, String>();
				rst = FileList.get(i);
				if (FileList.get(i).get("casename") == null || FileList.get(i).get("casename") == "") {					
					rst.put("errmsg", "테스트케이스명");
					errSw = true;
				}
				
				if (!ReqCd.equals("43")) {           //통합테스트
//					if (!errSw) {
//						if (FileList.get(i).get("testgbn") == null || FileList.get(i).get("testgbn") == "") {
//							rst.put("errmsg", "테스트구분");
//							errSw = true;
//						}
//					}
//					if (!errSw) {
//						if (!FileList.get(i).get("testgbn").equals("사전") && 
//							!FileList.get(i).get("testgbn").equals("사후") &&
//							!FileList.get(i).get("testgbn").equals("사전/사후")) {
//							rst.put("errmsg", "테스트구분");
//							errSw = true;
//						}
//					}
//					if (!errSw) {
//						if (ReqCd.equals("54") && !FileList.get(i).get("testgbn").equals("사전")) {
//							rst.put("errmsg", "테스트구분[사전만 가능]");
//							errSw = true;
//						} else if (ReqCd.equals("55") && !FileList.get(i).get("testgbn").equals("사후")) {
//							rst.put("errmsg", "테스트구분[사후만 가능]");
//							errSw = true;
//						} 
//					}
					if(!errSw) {						
						if (FileList.get(i).get("tester") == null || FileList.get(i).get("tester") == "") {
							rst.put("errmsg", "테스트담당자");
							errSw = true;
						}
					}
					if (!errSw && !ReqCd.equals("44")) {
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmm0040      \n");
						strQuery.append(" where cm_userid=?                    \n");
						strQuery.append("   and cm_username=?                  \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, UserId);
						pstmt.setString(2, FileList.get(i).get("tester"));
						rs = pstmt.executeQuery();
						if (rs.next()) {
							if (rs.getInt("cnt")==0) {
								rst.put("errmsg", "테스트담당자[본인만 가능]");
								errSw = true;
							} 
						}
						rs.close();
						pstmt.close();
					} else {
						if(!errSw) {
							strTester = FileList.get(i).get("tester").split(",");
							for (j=0;strTester.length>j;j++) {
								strQuery.setLength(0);
								strQuery.append("select count(*) cnt from cmm0040       \n");
								strQuery.append(" where cm_username=? and cm_active='1' \n");
								strQuery.append("   and cm_userid<>?                    \n");
								pstmt = conn.prepareStatement(strQuery.toString());
								pstmt.setString(1, strTester[j].trim());
								pstmt.setString(2, UserId);
								rs = pstmt.executeQuery();
								if (rs.next()) {
									if (rs.getInt("cnt")==0) {
										rst.put("errmsg", "테스트담당자미존재 ["+strTester[j].trim());
										errSw = true;
									} else if (rs.getInt("cnt")>1) {
										rst.put("errmsg", "테스트담당자명중복");
										errSw = true;
									} 
								}
								rs.close();
								pstmt.close();
							}
						}
					}
					if (errSw == false) {
						if (FileList.get(i).get("casecond1") == null || FileList.get(i).get("casecond1") == "") {
							rst.put("errmsg", "테스트조건1");
							errSw = true;
						}
					}
	
					if (errSw == false) {
						if (FileList.get(i).get("casechk1") == null || FileList.get(i).get("casechk1") == "") {
							rst.put("errmsg", "확인사항1");
							errSw = true;
						}
					}

					if (errSw == false) {
						if (FileList.get(i).get("tester") == null || FileList.get(i).get("tester") == "") {
							rst.put("errmsg", "테스트담당자");
							errSw = true;
						}						
					}
				} else {                            //단위테스트
					if (errSw == false) {
						if (FileList.get(i).get("casegbn") == null || FileList.get(i).get("casegbn") == "") {
							rst.put("errmsg", "테스트구분");
							errSw = true;
						}
					}
					if (errSw == false) {
						strQuery.setLength(0);
						strQuery.append("select cm_micode from cmm0020    \n");
						strQuery.append(" where cm_macode='UNITTSTGBN'    \n");
						strQuery.append("   and cm_codename=?             \n");
						strQuery.append("   and cm_closedt is null        \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, FileList.get(i).get("casegbn").trim());
						rs = pstmt.executeQuery();
						if (rs.next()) {
							rst.put("cc_testgbn", rs.getString("cm_micode"));
						} else {
							rst.put("errmsg", "테스트구분");
							errSw = true;
						}
						rs.close();
						pstmt.close();
					}                           //단위테스트
					if (errSw == false) {
						if (FileList.get(i).get("caseetc") == null || FileList.get(i).get("caseetc") == "") {
							rst.put("errmsg", "입력내용");
							errSw = true;
						}
					}
					if (errSw == false) {
						if (FileList.get(i).get("caserst") == null || FileList.get(i).get("caserst") == "") {
							rst.put("errmsg", "예상결과");
							errSw = true;
						}
					}
					
				}
				if (errSw == true) {
					rst.put("errsw", "Y");					
				} else {
					rst.put("errmsg", "정상");
					rst.put("errsw", "N");					
				}
				FileList.set(i, rst);
				rst = null;
			}
			
	        
	        return FileList.toArray();
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.chkExcelList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.chkExcelList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.chkExcelList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.chkExcelList() Exception END ##");				
			throw exception;
		}finally{
		}
	}//end of chkExcelList() method statement
	
	public String setExcelList(ArrayList<HashMap<String,String>> FileList, HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		int				  seq		  = 1;
		int				  i		  	  = 0;
		int				  pstmtcount  = 1;
		String[]          strTester   = null;            
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {			
			conn = connectionContext.getConnection();
			
			conn.setAutoCommit(false);
			
			strQuery.setLength(0);
			strQuery.append("select nvl(max(cc_caseseq),0) maxseq from cmc0210  \n");
			strQuery.append(" where CC_SRID=?                 	                \n");
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++,etcData.get("strSrid"));
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
			if (rs.next()){
				seq = rs.getInt("maxseq");
				++seq;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			
			int k = 0;
			int j = 0;
			String  itemMsg = "";
			String  testGbn = "";
			boolean findSw = false;
			
			for(i=0;i<FileList.size();i++){
	        	strQuery.setLength(0);
	    		strQuery.append("insert into CMC0210(CC_SRID,CC_CASESEQ,	    \n");
	    		strQuery.append("   CC_CASENAME,CC_SCMUSER,CC_USERID,CC_TESTGBN,\n");
	    		strQuery.append("   CC_STATUS,CC_CREATDT,CC_LASTDT)  	        \n");
	    		strQuery.append("	values(?,?, ?,?,?,?, '0',SYSDATE,SYSDATE)   \n");
	    		
	    		//pstmt = conn.prepareStatement(strQuery.toString());
	    		pstmt = new LoggableStatement(conn,strQuery.toString());
	    		pstmtcount = 1;
	          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
	      	    pstmt.setInt(pstmtcount++, seq);
	          	pstmt.setString(pstmtcount++, FileList.get(i).get("casename"));
	          	pstmt.setString(pstmtcount++, etcData.get("scmuser"));
	          	pstmt.setString(pstmtcount++, etcData.get("userid"));
	          	pstmt.setString(pstmtcount++,"B");
	          	testGbn = "B";
//	          	if (FileList.get(i).get("testgbn").equals("사전")) {
//	          		pstmt.setString(pstmtcount++,"B");
//	          		testGbn = "B";
//	          	} else if (FileList.get(i).get("testgbn").equals("사후")) {
//	          		pstmt.setString(pstmtcount++,"A");
//	          		testGbn = "A";
//	          	} else {
//	          		pstmt.setString(pstmtcount++,"T");	 
//	          		testGbn = "T";
//	          	}
	          	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
	      		pstmt.executeUpdate();
	      		pstmt.close();
	      		
	      		k = 0;
	      		for (j=0;5>j;j++) {
	      			itemMsg = "";
	      			findSw = false;
	      			if (j == 0) {
	      				if (!FileList.get(i).get("casecond1").equals("")&& 
			      			FileList.get(i).get("casecond1") != null && FileList.get(i).get("casecond1") != "") {
			      			itemMsg = FileList.get(i).get("casecond1");
			      			findSw = true;
			      		}
	      			} 
//	      			else if (j == 1) {
//			      		if (!FileList.get(i).get("casecond2").equals("")&& 
//			      			FileList.get(i).get("casecond2") != null && FileList.get(i).get("casecond2") != "") {
//			      			itemMsg = FileList.get(i).get("casecond2");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 2) {
//			      		if (!FileList.get(i).get("casecond3").equals("")&& 
//			      			FileList.get(i).get("casecond3") != null && FileList.get(i).get("casecond3") != "") {
//			      			itemMsg = FileList.get(i).get("casecond3");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 3) {
//			      		if (!FileList.get(i).get("casecond4").equals("")&& 
//			      			FileList.get(i).get("casecond4") != null && FileList.get(i).get("casecond4") != "") {
//			      			itemMsg = FileList.get(i).get("casecond4");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 4) {
//			      		if (!FileList.get(i).get("casecond5").equals("")&& 
//			      			FileList.get(i).get("casecond5") != null && FileList.get(i).get("casecond5") != "") {
//			      			itemMsg = FileList.get(i).get("casecond5");
//			      			findSw = true;
//			      		}
//	      			}
	      			
	      			if (findSw == true) {
			      		strQuery.setLength(0);
			    		strQuery.append("insert into CMC0212(CC_SRID, CC_CASESEQ,CC_STATUS,	  \n");
			    		strQuery.append("   CC_GBNCD,CC_SEQNO,CC_ITEMMSG,CC_LASTDT,CC_EDITOR) \n");
			    		strQuery.append("   values(?,?,'0', 'C',?,?,SYSDATE,?)				  \n");
			    		
			    		pstmt = conn.prepareStatement(strQuery.toString());
			    		pstmtcount = 1;
			          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
			      	    pstmt.setInt(pstmtcount++, seq);
			      	    pstmt.setInt(pstmtcount++, ++k);
			          	pstmt.setString(pstmtcount++, itemMsg);
			          	pstmt.setString(pstmtcount++, etcData.get("userid"));
			      		pstmt.executeUpdate();
			      		pstmt.close();
	      			}
	      		}
	      		k = 0;
	      		for (j=0;5>j;j++) {
	      			itemMsg = "";
	      			findSw = false;
	      			if (j == 0) {
			      		if (!FileList.get(i).get("casechk1").equals(" ")&& 
			      			FileList.get(i).get("casechk1") != null && FileList.get(i).get("casechk1") != "") {
			      			itemMsg = FileList.get(i).get("casechk1");
			      			findSw = true;
			      		}
	      			} 
//	      			else if (j == 1) {
//			      		if (!FileList.get(i).get("casechk2").equals(" ")&& 
//			      			FileList.get(i).get("casechk2") != null && FileList.get(i).get("casechk2") != "") {
//			      			itemMsg = FileList.get(i).get("casechk2");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 2) {
//			      		if (!FileList.get(i).get("casechk3").equals(" ")&& 
//			      			FileList.get(i).get("casechk3") != null && FileList.get(i).get("casechk3") != "") {
//			      			itemMsg = FileList.get(i).get("casechk3");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 3) {
//			      		if (!FileList.get(i).get("casechk4").equals(" ")&& 
//			      			FileList.get(i).get("casechk4") != null && FileList.get(i).get("casechk4") != "") {
//			      			itemMsg = FileList.get(i).get("casechk4");
//			      			findSw = true;
//			      		}
//	      			}else if (j == 4) {
//			      		if (!FileList.get(i).get("casechk5").equals(" ")&&
//			      				FileList.get(i).get("casechk5") != null && FileList.get(i).get("casechk5") != "") {
//			      			itemMsg = FileList.get(i).get("casechk5");
//			      			findSw = true;
//			      		}
//	      			}
	      			
	      			if (findSw == true) {
			      		strQuery.setLength(0);
			    		strQuery.append("insert into CMC0212(CC_SRID,CC_CASESEQ,CC_STATUS,  	\n");
			    		strQuery.append("   CC_GBNCD,CC_SEQNO,CC_ITEMMSG,CC_LASTDT,CC_EDITOR)	\n");
			    		strQuery.append("   values(?,?,'0', 'R',?,?,SYSDATE,?)									    		\n");
			    		
			    		pstmt = conn.prepareStatement(strQuery.toString());
			    		pstmtcount = 1;
			          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
			      	    pstmt.setInt(pstmtcount++, seq);
			      	    pstmt.setInt(pstmtcount++, ++k);
			          	pstmt.setString(pstmtcount++, itemMsg);
			          	pstmt.setString(pstmtcount++, etcData.get("userid"));
			      		pstmt.executeUpdate();
			      		pstmt.close();
	      			}
	      		}
	      		strTester = FileList.get(i).get("tester").split(",");
	      		for (j=0;strTester.length>j;j++) {
	      			strQuery.setLength(0);
	      			strQuery.append("insert into cmc0211                  \n");
	      			strQuery.append("  (cc_srid,cc_caseseq,cc_testuser,   \n");
	      			strQuery.append("   cc_testgbn,cc_status,cc_creatdt,  \n");
	      			strQuery.append("   cc_lastdt)                        \n");
	      			strQuery.append("(select ?,?,cm_userid,               \n");
	      			strQuery.append("        ?,'0',SYSDATE,SYSDATE        \n");
	      			strQuery.append("   from cmm0040                      \n");
	      			strQuery.append("  where cm_username=?                \n");
	      			strQuery.append("    and cm_active='1')               \n");
	      			pstmt = conn.prepareStatement(strQuery.toString());
		    		pstmtcount = 1;
		          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
		      	    pstmt.setInt(pstmtcount++, seq);
		      	    if (testGbn.equals("T")) pstmt.setString(pstmtcount++,"B");
		      	    else pstmt.setString(pstmtcount++,testGbn);
		          	pstmt.setString(pstmtcount++, strTester[j].trim());
		      		pstmt.executeUpdate();
		      		pstmt.close();
		      		
		      		if (testGbn.equals("T")) {
		      			strQuery.setLength(0);
		      			strQuery.append("insert into cmc0211                  \n");
		      			strQuery.append("  (cc_srid,cc_caseseq,cc_testuser,   \n");
		      			strQuery.append("   cc_testgbn,cc_status,cc_creatdt,  \n");
		      			strQuery.append("   cc_lastdt)                        \n");
		      			strQuery.append("(select ?,?,cm_userid,               \n");
		      			strQuery.append("        'A','0',SYSDATE,SYSDATE      \n");
		      			strQuery.append("   from cmm0040                      \n");
		      			strQuery.append("  where cm_username=?                \n");
		      			strQuery.append("    and cm_active='1')               \n");
		      			pstmt = conn.prepareStatement(strQuery.toString());
			    		pstmtcount = 1;
			          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
			      	    pstmt.setInt(pstmtcount++, seq);
			          	pstmt.setString(pstmtcount++, strTester[j].trim());
			      		pstmt.executeUpdate();
			      		pstmt.close();
		      		}
	      		}
	      		++seq;
	      		
			}
	        
		    retMsg = "0";
	        conn.commit();
	        conn.close();
	        rs.close();
	        
			conn = null;
			rs = null;
			pstmt = null;
	        
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			if( conn != null ){
				conn.rollback();
				conn.close();
				conn = null;
			}
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.setExcelList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.setExcelList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			if( conn != null ){
				conn.rollback();
				conn.close();
				conn = null;
			}
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.setExcelList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.setExcelList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.setExcelList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setExcelList() method statement

	public Object[] getRelatList(String cc_srid,String scmUser,String reqCD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
//		PreparedStatement pstmt2      = null;
//		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		String            svItemId    = "";
		boolean           findSw      = false;
		String            strTester   = "";
		String            strTesterName = "";
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select e.cm_dirpath,c.cr_rsrcname,d.cc_tester,c.cr_itemid, \n");
			strQuery.append("       (select cm_username from cmm0040 where cm_userid=d.cc_tester) cm_username \n");
			strQuery.append("  from cmm0070 e,cmc0214 d,cmr0020 c,(   \n");
			strQuery.append("select distinct a.cr_itsmid,a.cr_editor,b.cr_itemid \n");
			strQuery.append("  from cmr1010 b,cmr1000 a,cmr0020 c    \n");
			strQuery.append(" where c.cr_isrid=? and c.cr_lstusr=?   \n");
			strQuery.append("   and c.cr_status not in ('0','9')     \n");
			strQuery.append("   and a.cr_itsmid=? and a.cr_qrycd='07'\n");
			strQuery.append("   and a.cr_status<>'3'                 \n");
			strQuery.append("   and a.cr_prcdate is not null         \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno          \n");
			strQuery.append("   and c.cr_itemid=b.cr_baseitem        \n");
			strQuery.append("   and b.cr_qrycd='09'                  \n");
			strQuery.append(" minus                                  \n");
			strQuery.append("select distinct a.cr_itsmid,a.cr_editor,b.cr_itemid \n");
			strQuery.append("  from cmr1010 b,cmr1000 a,cmr0020 c    \n");
			strQuery.append(" where c.cr_isrid=? and c.cr_lstusr=?   \n");
			strQuery.append("   and c.cr_status not in ('0','9')     \n");
			strQuery.append("   and a.cr_itsmid=? and a.cr_qrycd='07'\n");
			strQuery.append("   and a.cr_status<>'3'                 \n");
			strQuery.append("   and a.cr_prcdate is not null         \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno          \n");
			strQuery.append("   and c.cr_itemid=b.cr_baseitem        \n");
			strQuery.append("   and b.cr_qrycd<>'09') a              \n");
			strQuery.append(" where a.cr_itemid=c.cr_itemid          \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd            \n");
			strQuery.append("   and c.cr_dsncd=e.cm_dsncd            \n");
			strQuery.append("   and a.cr_itsmid=d.cc_srid(+)         \n");
			strQuery.append("   and a.cr_editor=d.cc_scmuser(+)      \n");
			strQuery.append("   and a.cr_itemid=d.cc_itemid(+)       \n");
			strQuery.append(" group by e.cm_dirpath,c.cr_rsrcname,d.cc_tester,c.cr_itemid \n");
			strQuery.append(" order by c.cr_itemid                   \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, cc_srid);
			pstmt.setString(2, scmUser);
			pstmt.setString(3, cc_srid);
			pstmt.setString(4, cc_srid);
			pstmt.setString(5, scmUser);
			pstmt.setString(6, cc_srid);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());          
			rs = pstmt.executeQuery();
			while (rs.next()) {
				findSw = false;
				if (svItemId == null || svItemId.length()==0) findSw = true;
				else if (!svItemId.equals(rs.getString("cr_itemid"))) findSw = true;
				if (findSw) {
					if (strTester.length()>0) {
						rst.put("tester", strTester);
						rst.put("testername",strTesterName);
					}
					if (svItemId.length()>0) {
						rsval.add(rst);
						rst = null;
					}
					rst = new HashMap<String, String>();
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("enabled", "1");
					strTester = "";
					strTesterName = "";
				}
				svItemId = rs.getString("cr_itemid");
				if (rs.getString("cc_tester") != null) {
					if (strTester.length()>0) {
						strTester = strTester + ",";
						strTesterName = strTesterName + ",";
					}
					strTester = strTester + rs.getString("cc_tester");
					strTesterName = strTesterName + rs.getString("cm_username");					
				}
			}
			rs.close();
			pstmt.close();
			
			if (strTester.length()>0) {
				rst.put("tester", strTester);
				rst.put("testername",strTesterName);
			}
			if (svItemId.length()>0) {
				rsval.add(rst);
				rst = null;
			}			
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			ecamsLogger.error("++++ rsval++++"+rsval.toString());
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			
			return returnObjectArray;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getRelatList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getRelatList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getRelatList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getRelatList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.getRelatList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of getRelatList() method statement

	public Object[] getProgList(String cc_srid,String scmUser,String UserId,String secuYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
//		PreparedStatement pstmt2      = null;
//		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);

			strQuery.append("select e.cm_dirpath,c.cr_rsrcname,d.cc_tester, \n");
			strQuery.append("       d.cc_itemid,a.cm_sysmsg,d.cc_caseseq    \n");
			strQuery.append("  from cmm0030 a,cmm0070 e,cmc0214 d,cmr0020 c \n");
			strQuery.append(" where d.cc_srid=? and d.cc_scmuser=?   \n");
			strQuery.append("   and d.cc_tester=?                    \n");
			strQuery.append("   and d.cc_itemid=c.cr_itemid          \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd            \n");
			strQuery.append("   and c.cr_dsncd=e.cm_dsncd            \n");
			strQuery.append("   and c.cr_syscd=a.cm_syscd            \n");			
			if (secuYn.equals("Y")) {
				strQuery.append("and d.cc_caseseq=999                \n");
			} else {
				strQuery.append("and d.cc_caseseq<>999                \n");
			}
			strQuery.append(" order by d.cc_itemid                   \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, cc_srid);
			pstmt.setString(2, scmUser);
			pstmt.setString(3, UserId);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cc_itemid", rs.getString("cc_itemid"));
				rst.put("cc_tester", rs.getString("cc_tester"));
				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
				if (secuYn.equals("Y")) {
					rst.put("enabled", "1");
				}else {
					rst.put("enabled", "0");
				}
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			
			return returnObjectArray;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getRelatList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.getRelatList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.getRelatList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.getRelatList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.getRelatList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of getRelatList() method statement
	public String setAnalTester(ArrayList<HashMap<String,String>> TesterList,String cc_srid,String scmUser,String reqCD,String userID ) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String[]          strTester   = null;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			for (int i=0;TesterList.size()>i;i++) {
				strTester = TesterList.get(i).get("cc_tester").split(",");
				//ecamsLogger.error("++++ tester +++"+TesterList.get(i).get("cc_tester"));
				strQuery.setLength(0);
				strQuery.append("delete cmc0214                     \n");
				strQuery.append(" where cc_srid=? and cc_scmuser=?  \n");
				strQuery.append("   and cc_itemid=?                 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, cc_srid);
				pstmt.setString(2, scmUser);
				pstmt.setString(3, TesterList.get(i).get("cc_itemid"));
				pstmt.executeUpdate();
				pstmt.close();	
				
				for (j=0;strTester.length>j;j++) {
					strQuery.setLength(0);
					strQuery.append("insert into cmc0214               \n");
					strQuery.append(" (cc_srid,cc_scmuser,cc_itemid,   \n");
					strQuery.append("  cc_tester,cc_lastdt,cc_caseseq) \n");
					strQuery.append("values                            \n");
					strQuery.append(" (?,?,?,?,SYSDATE,999)            \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, cc_srid);
					pstmt.setString(2, scmUser);
					pstmt.setString(3, TesterList.get(i).get("cc_itemid"));
					pstmt.setString(4, strTester[j]);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();					
				}
			}
			conn.close();
			
			pstmt = null;
			conn = null;
			
			return "OK";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0400.setAnalTester() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0400.setAnalTester() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0400.setAnalTester() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0400.setAnalTester() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0400.setAnalTester() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	//14.07.23 siruen 모니터링 체크가 완료가 되면, 모니터링확인완료 상태로 변경하는 쿼리 추가
	public int setStaCmc0110(String srid, String userid, String status) throws SQLException, Exception {
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
			strQuery.append("update cmc0110 set cc_status = ?  	\n");
			strQuery.append(" where cc_srid=?                	\n");
			if( !status.equals("9") ) {
				strQuery.append("   and cc_userid=?             \n");
			}
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, status);
			pstmt.setString(2, srid);
			if( !status.equals("9") ) {
				pstmt.setString(3, userid);
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			retInt = pstmt.executeUpdate();
			
			rs = null;
			pstmt = null;
			
			if( status.equals("9") ){
				strQuery.setLength(0);
				strQuery.append("update cmc0100 set cc_status='6'  \n");
				strQuery.append(" where cc_srid=?                  \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, srid);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
				pstmt = null;
			}
			
			HashMap<String,String> tmp_obj = new HashMap<String, String>();
			tmp_obj.put("SR_ID", srid);
			tmp_obj.put("USER_ID", userid);//보내는사람 ID
			tmp_obj.put("QRY_CD", "54");
			tmp_obj.put("CR_CONUSR", userid);//받는사람 ID
			tmp_obj.put("CR_ACPTNO", "999999999999");//임시번호
			if ( "7".equals( status ) ) {//개발검수 요청 일때
				tmp_obj.put("CR_NOTIGBN", "CO");//알림구분(NOTIGBN) [CO]결재알림,[ED]완료알림,[CN]반려알림,[ER]오류알림
			} else if ( "B".equals( status ) ) {
				tmp_obj.put("CR_NOTIGBN", "ED");//알림구분(NOTIGBN) [CO]결재알림,[ED]완료알림,[CN]반려알림,[ER]오류알림
			} else {
				tmp_obj.put("CR_NOTIGBN", "CN");//알림구분(NOTIGBN) [CO]결재알림,[ED]완료알림,[CN]반려알림,[ER]오류알림
			}
			Cmc0100 cmc0100 = new Cmc0100();
			String retNOTI = cmc0100.insertCMR9910ForNotice(tmp_obj, conn);
			cmc0100 = null;
			ecamsLogger.error("Cmc0400.java setStaCmc0110: 개발검수 관련 CMR9910 inert 결과값["+retNOTI+"]");
			
			conn.commit();
			conn.close();
			conn = null;
			
			return retInt;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0100.setStaCmc0110() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0100.setStaCmc0110() SQLException END ##");
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0100.setStaCmc0110() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0100.setStaCmc0110() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0100.setStaCmc0110() Exception END ##");
			if (conn != null){
				try{
					conn.rollback();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0100.setStaCmc0110() connection release exception ##");
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
					ecamsLogger.error("## Cmc0100.setStaCmc0110() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
