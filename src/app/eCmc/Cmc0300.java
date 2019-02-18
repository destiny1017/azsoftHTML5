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

public class Cmc0300 {
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	/** 단위테스트 및 개발검수 화면 개발자 콤보 값 조회
	 * @param cc_srid
	 * @param reqCD
	 * @param userID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getScmuserList(String cc_srid,String reqCD,String userID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		int               paramIndex  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("SELECT a.CC_USERID,b.cm_username scmuser,a.cc_status,\n");
			strQuery.append("       SRSTEP_SECUCHK(?,a.cc_srid,?,'01') secuyn \n");
			strQuery.append("  FROM cmc0110 a, cmm0040 b \n");
			//개발검수[54] 일때는 개발검수대상 시스템이어야 함
			if ( reqCD.equals("54") ) {
				strQuery.append("  ,(SELECT distinct A.CR_EDITOR \n");
				strQuery.append("      FROM CMM0030 B,CMR0020 A  \n");
				strQuery.append("     WHERE A.CR_ISRID=?         \n");
				strQuery.append("       AND A.CR_STATUS NOT IN ('3','9','C') \n");
				strQuery.append("       AND A.CR_SYSCD=B.CM_SYSCD \n");
				strQuery.append("       AND SUBSTR(B.CM_SYSINFO,12,1)='1') c \n");
			}
			strQuery.append(" WHERE	a.cc_srid = ? \n");
			strQuery.append(" 	AND a.cc_userid = b.cm_userid \n");
			if ( reqCD.equals("54") ) {//54:개발검수
				//strQuery.append(" 	AND a.cc_status in ('5','6','7','9','C','D') \n");
				strQuery.append(" 	AND a.cc_status = '7'  \n");
			} else if (reqCD.equals("63")) {//63:모니터링  cc_status 6:운영적용완료 D:모니터링중 9:모니터링완료
				strQuery.append(" 	AND a.cc_status in ('6','D','9') \n");
			} else {
				strQuery.append(" 	AND a.cc_status <> '3' \n");
			}
			if ( reqCD.equals("54") ) {
				strQuery.append(" 	AND a.cc_userid = c.CR_EDITOR \n");
			}
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			paramIndex = 1;
			pstmt.setString(paramIndex++, userID);
			pstmt.setString(paramIndex++, reqCD);
			if ( reqCD.equals("54") ) {
				pstmt.setString(paramIndex++, cc_srid);				
			}
			pstmt.setString(paramIndex++, cc_srid);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			rst = new HashMap<String, String>();
			rst.put("cc_scmuser", "00");
			rst.put("scmuser", "선택하세요");
			rsval.add(rst);
			rst = null;
			
			while(rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cc_scmuser", rs.getString("CC_USERID"));
				rst.put("scmuser", rs.getString("scmuser"));
				rst.put("secuyn", rs.getString("secuyn"));
				rst.put("pgmyn", "NO");
				rst.put("devChkEnabled", "NO");
				rst.put("cc_status", rs.getString("cc_status"));
				rst.put("testreqyn", "N");
				
				//SR번호로 1건이상 프로그램을 체크인 또는 개발적용 또는 테스트적용한건 있는지 조회
				if (reqCD.equals("43") && rs.getString("secuyn").equals("OK") ) {
					//43:단위테스트  44:통합테스트준비
					strQuery.setLength(0);
					strQuery.append("select CHECKINYN(?,?,'43',?,?) testyn,      \n");
					strQuery.append("       CHECKINYN(?,?,'4302',?,?) testrreqyn \n");
					strQuery.append("  from dual                                 \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("CC_USERID"));
					pstmt2.setString(2, cc_srid);
					pstmt2.setString(3, userID);
					pstmt2.setString(4, rs.getString("cc_status"));
					pstmt2.setString(5, rs.getString("CC_USERID"));
					pstmt2.setString(6, cc_srid);
					pstmt2.setString(7, userID);
					pstmt2.setString(8, rs.getString("cc_status"));
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("pgmyn", rs2.getString("testyn"));
						rst.put("devChkEnabled", rs2.getString("testrreqyn"));
					}
					rs2.close();
					pstmt2.close();
				} else if ( reqCD.equals("54") && rs.getString("secuyn").equals("OK") ) {
					//20140722 neo. 개발검수완료 버튼 활성화 여부 체크 및 등록/수정 가능 여부
					//개발검수완료 버튼 활성화 여부
					//케이스별 확인사항이 등록되어 있는지 체크
					strQuery.setLength(0);
					strQuery.append("select cc_caseseq from cmc0210 \n");
					strQuery.append(" where cc_srid=? and cc_status='0' and cc_scmuser=?\n");
					strQuery.append(" minus \n");
					strQuery.append("select cc_caseseq from cmc0212 \n");
					strQuery.append(" where cc_srid =? and cc_gbncd ='R' \n");
					strQuery.append("   and cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid=? and cc_status='0' and cc_scmuser=?) \n");
//					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, cc_srid);
					pstmt2.setString(2, rs.getString("CC_USERID"));
					pstmt2.setString(3, cc_srid);
					pstmt2.setString(4, cc_srid);
					pstmt2.setString(5, rs.getString("CC_USERID"));
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if ( !rs2.next() ) {
						rst.put("pgmyn", "OK");
					}
					rs2.close();
					pstmt2.close();
					
					if ( rst.get("pgmyn").equals("OK") ) {
						//모든 확인사항이 정상완료 되였는지 체크
						strQuery.setLength(0);
						strQuery.append("select a.cnt1 total, a.cnt1 - b.cnt2 as cnt \n");
						strQuery.append(" from (select count(*) cnt1 \n");
						strQuery.append("         from cmc0212 \n");
						strQuery.append("        where cc_srid = ? \n");
						strQuery.append("          and cc_gbncd ='R' \n");
						strQuery.append("          and cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid=? and cc_status='0' and cc_scmuser=?)) a \n");
						strQuery.append("     ,(select count(*) cnt2 \n");
						strQuery.append("         from cmc0213 \n");
						strQuery.append("        where cc_srid = ? \n");
						strQuery.append("          and cc_scmuser=? \n");
						strQuery.append("          and CC_TESTRST = '0' \n");
						strQuery.append("          and cc_caseseq in (select cc_caseseq from cmc0210 where cc_srid=? and cc_status='0' and cc_scmuser=?)) b \n");
//						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2 = new LoggableStatement(conn,strQuery.toString());
						pstmt2.setString(1, cc_srid);
						pstmt2.setString(2, cc_srid);
						pstmt2.setString(3, rs.getString("CC_USERID"));
						pstmt2.setString(4, cc_srid);
						pstmt2.setString(5, rs.getString("CC_USERID"));
						pstmt2.setString(6, cc_srid);
						pstmt2.setString(7, rs.getString("CC_USERID"));
						ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
						rs2 = pstmt2.executeQuery();
						if ( rs2.next() && rs2.getInt("total")>0 && rs2.getInt("cnt")==0 ) {
							rst.put("pgmyn", "OK");
						} else {
							rst.put("pgmyn", "NO");
						}
						rs2.close();
						pstmt2.close();
					}
					
					//등록/수정 가능 여부
					strQuery.setLength(0);
					strQuery.append("select CHECKINYN(?,?,?,?,?) testyn from dual    \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, rs.getString("CC_USERID"));
					pstmt2.setString(2, cc_srid);
					pstmt2.setString(3, "4302");
					pstmt2.setString(4, userID);
					pstmt2.setString(5, rs.getString("cc_status"));
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						rst.put("devChkEnabled", rs2.getString("testyn"));
					}
					rs2.close();
					pstmt2.close();
					
					ecamsLogger.error("##### devChkEnabled:"+rst.get("devChkEnabled")+",pgmyn:"+rst.get("pgmyn"));
				}
				
				if (reqCD.equals("54") || reqCD.equals("55") || reqCD.equals("XX")) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmc0214     \n");
					strQuery.append(" where cc_srid=? and cc_scmuser=?    \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, cc_srid);
					pstmt2.setString(2, rs.getString("CC_USERID"));
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getInt("cnt") == 0) {
							rst.put("relatprog2", "N");
						} else {
							rst.put("relatprog2", "Y");
						}
					}
					rs2.close();
					pstmt2.close();
				} else if (reqCD.equals("44") && rs.getString("secuyn").equals("OK")) {
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
					strQuery.append(")                                       \n");
					//pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1, cc_srid);
					pstmt2.setString(2, rs.getString("CC_USERID"));
					pstmt2.setString(3, cc_srid);
					pstmt2.setString(4, cc_srid);
					pstmt2.setString(5, rs.getString("CC_USERID"));
					pstmt2.setString(6, cc_srid);
					ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());          
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getInt("cnt") == 0) {
							rst.put("relatprog1", "N");
						} else {
							rst.put("relatprog1", "Y");
						}
					}
					rs2.close();
					pstmt2.close();
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
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getScmuserList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.getScmuserList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getScmuserList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.getScmuserList() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.getScmuserList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getScmuserList() method statement
	
	/** 개발자 조회
	 * @param cc_srid
	 * @param scmuser
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getCaseList(String cc_srid, String scmuser) throws SQLException, Exception {
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
			strQuery.append("SELECT a.cc_srid,a.cc_caseseq,a.cc_testgbn,a.cc_casename,  	    \n");
			strQuery.append("       a.cc_etc,a.cc_testday,a.cc_scmuser, b.cm_username scmuser,	\n");
			strQuery.append("       a.cc_testrst,a.cc_createuser,c.cm_username createuser,      \n");
			strQuery.append("       to_char(a.cc_createdate,'yyyy/mm/dd') createdate,           \n");
			strQuery.append("       a.cc_exprst,d.cm_codename      					            \n");
			strQuery.append("  FROM cmm0020 d,cmc0200 a,cmm0040 b,cmm0040 c                     \n");
			strQuery.append(" WHERE a.cc_srid = ?                                               \n");
			if(!scmuser.equals("00")){
				strQuery.append(" AND a.cc_scmuser = ?				              			    \n");
			}
			strQuery.append("   AND a.cc_scmuser = b.cm_userid                                  \n");
			strQuery.append("   AND a.cc_createuser = c.cm_userid                               \n");
			strQuery.append("   AND d.cm_macode='UNITTSTGBN' and d.cm_micode=a.cc_testgbn       \n");
			strQuery.append("ORDER BY a.cc_caseseq                                              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, cc_srid);
			if(!scmuser.equals("00")){
				pstmt.setString(2, scmuser);
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_caseseq", rs.getString("cc_caseseq"));
				rst.put("cc_testgbn", rs.getString("cc_testgbn"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cc_casename", rs.getString("cc_casename"));
				rst.put("cc_exprst", rs.getString("cc_exprst"));
				rst.put("cc_etc", rs.getString("cc_etc"));
				if(rs.getString("cc_testday") != null){
					rst.put("testday", rs.getString("cc_testday").substring(0,4)+"/"+rs.getString("cc_testday").substring(4,6)+"/"+rs.getString("cc_testday").substring(6,8));
				}else{
					rst.put("testday","");
				}
				rst.put("scmuser", rs.getString("scmuser"));
				rst.put("cc_scmuser", rs.getString("cc_scmuser"));
				rst.put("testrst", "FAIL");
				if (rs.getString("cc_testrst") != null && rs.getString("cc_testrst").equals("P")) {
					rst.put("testrst", "PASS");
				}
				rst.put("cc_testrst", rs.getString("cc_testrst"));
				rst.put("cc_createuser", rs.getString("cc_createuser"));
				rst.put("createuser", rs.getString("createuser"));
				rst.put("createdate", rs.getString("createdate"));
				
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
			ecamsLogger.error("## Cmc0100.getCaseList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0100.getCaseList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0100.getCaseList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0100.getCaseList() Exception END ##");				
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
					ecamsLogger.error("## Cmc0100.getCaseList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getCaseList() method statement
	
	
	public String setCaseInfo(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		boolean           insFg       = false;
		
		int 			  seq     = 0;
		int               pstmtcount  = 1;
		String            retMsg      = "OK";
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			//20140714 neo. NICE에서는 단위테스트의 보안체크리스트 사용안함.
//			strQuery.setLength(0);
//			strQuery.append("select count(*) cnt from cmc0201 where cc_srid=? and cc_editor=?    \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt.setString(1, etcData.get("srId"));
//			pstmt.setString(2, etcData.get("UserId"));
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				if (rs.getInt("cnt") == 0) {
//					retMsg = "NO";
//				}
//			}
//			rs.close();
//			pstmt.close();
//			
//			if (!retMsg.equals("OK")) {
//				conn.close();
//				rs = null;
//				pstmt = null;
//				conn = null;
//				return retMsg;
//			}
			if (etcData.get("testid") == null || etcData.get("testid") == "") {
				strQuery.setLength(0);
				strQuery.append("SELECT nvl(max(cc_caseseq),0) max		\n");			
				strQuery.append("  FROM cmc0200  			            \n");
				strQuery.append(" WHERE CC_SRID = ?	    		    \n");
				pstmt = conn.prepareStatement(strQuery.toString());	
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++,etcData.get("srId"));
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
		        rs = pstmt.executeQuery();
				if (rs.next()){
					seq = rs.getInt("max")+1;
					etcData.put("testid", Integer.toString(seq));
					insFg = true;
				}//end of while-loop statement
				rs.close();
				pstmt.close();
			}
			
			if(insFg){
				strQuery.setLength(0);
	    		strQuery.append("insert into cmc0200 (CC_SRID,CC_CASESEQ,CC_SCMUSER,CC_STATUS,CC_CREATEDATE,    \n");
	    		strQuery.append("                     CC_LASTDATE,CC_TESTGBN,CC_CASENAME,CC_EXPRST,CC_ETC,	    \n");
	    		strQuery.append("                     CC_TESTDAY,CC_TESTRST,CC_CREATEUSER)		            	\n");
	    		strQuery.append("	values (?,?,?,?,SYSDATE,													\n");
	    		strQuery.append("			SYSDATE,?,?,?,?,													\n");
	    		strQuery.append("			?,?,?)  															\n");
	    		
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		//pstmt = new LoggableStatement(conn,strQuery.toString());
	    		pstmtcount = 1;
	          	pstmt.setString(pstmtcount++, etcData.get("srId"));
	          	pstmt.setInt(pstmtcount++, seq);
	          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
	          	if (etcData.get("testrst") != null && etcData.get("testrst") != "") {
	          		if (etcData.get("testrst").equals("P")) pstmt.setString(pstmtcount++, "9");
	          		else pstmt.setString(pstmtcount++, "0");
	          	} else pstmt.setString(pstmtcount++, "0");
	          	pstmt.setString(pstmtcount++, etcData.get("testgbn"));
	          	pstmt.setString(pstmtcount++, etcData.get("Tcase"));
	          	pstmt.setString(pstmtcount++, etcData.get("ExpResult"));
	          	pstmt.setString(pstmtcount++, etcData.get("txtRef"));
	          	
	          	pstmt.setString(pstmtcount++, etcData.get("testday"));
	          	pstmt.setString(pstmtcount++, etcData.get("testrst"));
	          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
	          	
	      	    ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	      		pstmt.executeUpdate();           		
		        pstmt.close();
			}else{
				strQuery.setLength(0);
				strQuery.append("UPDATE cmc0200 SET CC_LASTDATE=SYSDATE,CC_TESTGBN=?,CC_CASENAME=?, \n");
				strQuery.append("                   CC_EXPRST=?,CC_ETC=?,                           \n");
				strQuery.append("                   CC_TESTDAY=?,CC_TESTRST=?,                      \n");
	          	if (etcData.get("testrst") != null && etcData.get("testrst") != "") {
	          		if (etcData.get("testrst").equals("P")) strQuery.append(" cc_status='9'         \n");
	          		else strQuery.append(" cc_status='0'                                            \n");
	          	} else {
	          		strQuery.append(" cc_status='0'                                                 \n");
	          	}
				strQuery.append("WHERE CC_SRID = ?                                                  \n");
				strQuery.append("  AND CC_CASESEQ = ?                                               \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());
	    		//pstmt = new LoggableStatement(conn,strQuery.toString());
	    		pstmtcount = 1;
	    		pstmt.setString(pstmtcount++, etcData.get("testgbn"));
	    		pstmt.setString(pstmtcount++, etcData.get("Tcase"));
	    		pstmt.setString(pstmtcount++, etcData.get("ExpResult"));
	    		pstmt.setString(pstmtcount++, etcData.get("txtRef"));
	    		
	    		pstmt.setString(pstmtcount++, etcData.get("testday"));
	    		pstmt.setString(pstmtcount++, etcData.get("testrst"));
	    		
	          	pstmt.setString(pstmtcount++, etcData.get("srId"));
	          	pstmt.setString(pstmtcount++, etcData.get("testid"));
	          	
	          	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	      		pstmt.executeUpdate();           		
		        pstmt.close();
			}
			
			strQuery.setLength(0);
			strQuery.append("update cmc0110 set cc_status='2'  \n");
			strQuery.append(" where cc_srid=? and cc_userid=?  \n");
			strQuery.append("   and cc_status<>'2'             \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("srId"));
			pstmt.setString(2, etcData.get("UserId"));
			pstmt.executeUpdate();
			pstmt.close();
			
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return retMsg+etcData.get("testid");
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0100.getCaseList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0100.getCaseList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0100.getCaseList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0100.getCaseList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0100.getCaseList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public String delCaseInfo(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		int				  pstmtcount  = 1;
		HashMap<String,String> rst = new HashMap<String,String>();
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
        	strQuery.setLength(0);
    		strQuery.append("delete cmc0200 		\n");
    		strQuery.append(" where CC_SRID=?		\n");
    		strQuery.append("   and CC_CASESEQ=?	\n");
    		
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmtcount = 1;
      	    pstmt.setString(pstmtcount++, etcData.get("IsrId"));
      	  	pstmt.setString(pstmtcount++, etcData.get("testid"));
      		pstmt.executeUpdate();           		
	        pstmt.close();
		    
	        Cmc0100 cmc0100 = new Cmc0100();
	        rst = new HashMap<String,String>();
			rst.put("cc_srid", etcData.get("IsrId"));
			rst.put("cc_division", "43");
			rst.put("cc_caseseq", etcData.get("testid"));
			if ( cmc0100.fileDelete(rst,conn) ) {
				//첨부파일 서버에서 File 삭제 정상 완료 시 이력도 삭제 CMC0101 테이블에서 삭제.
				strQuery.setLength(0);
	    		strQuery.append("delete cmc0101          \n");
	    		strQuery.append(" where CC_SRID=?	     \n");
	    		strQuery.append("   and CC_DIVISION='43' \n");
	    		strQuery.append("   and CC_CASESEQ=?     \n");
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		pstmtcount = 1;
	      	    pstmt.setString(pstmtcount++, etcData.get("IsrId"));
	      	  	pstmt.setString(pstmtcount++, etcData.get("testid"));
	      		pstmt.executeUpdate();
		        pstmt.close();
			}
			rst = null;
			cmc0100 = null;
			
		    retMsg = "OK";
	        conn.close();
			conn = null;
			pstmt = null;
			
			
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0030.TcaseDel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0030.TcaseDel() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0030.TcaseDel() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0030.TcaseDel() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0030.TcaseDel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	} //end of TcaseDel() method statement		
	
	public Object[] getSecCheckList(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
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
					rst.put("cc_gubun", strGubun);
					rst.put("cc_detgubun", strDetGubun);
					//rst.put("sel", "P");
					rst.put("cc_item", rs.getString("cm_gbnname"));
					rst.put("cc_reqyn", rs.getString("cm_reqyn"));
					rst.put("cc_gbncd", rs.getString("cm_gbncd"));
					rst.put("enabled", "1");
					strGubun1 = strGubun;
					strDetGubun1 = strDetGubun;
					strGubun = null;
					strDetGubun = null;
					
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
			conn = null;
			
			
			return rsval.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getSecCheckList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.getSecCheckList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getSecCheckList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.getSecCheckList() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.getSecCheckList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
	}//end of getSecurityCheckList() method statement	
	
	public Object[] getApplyInfo(String srID, String strTypeCd, String sysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strGubun    = null;
		String            strDetGubun = null;
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);

			strQuery.append("SELECT a.cm_gbnname as gbnname, b.cm_gbnname as detgubun ,              \n");
			strQuery.append("       c.cm_gbnname as itemgubun, d.cc_reqyn, d.cc_eval ,d.cc_gbncd     \n");
			strQuery.append("  FROM cmm0110 a, cmm0110 b, cmm0110 c, cmc0201 d                   \n");
			strQuery.append(" WHERE d.cc_srid=? and a.cm_gbncd = b.cm_uppgbncd                       \n");
			strQuery.append("  	AND b.cm_gbncd = c.cm_uppgbncd    					                 \n");	
			strQuery.append("  	AND c.cm_gbncd = d.cc_gbncd      					                 \n");
			if(strTypeCd.equals("Monitor")){
				strQuery.append("  	AND d.cc_syscd=?			      					             \n");	
			}
			strQuery.append(" order by CC_GBNCD");
			//pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
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
			
			returnObjectArray = rsval.toArray();
			
			rsval.clear();
			rsval = null;
			
			return returnObjectArray;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getApplyInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.getApplyInfo() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getApplyInfo() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.getApplyInfo() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.getApplyInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getApplyInfo() method statement

	public String setSecChkInsertUpdate(String strTypeCd,ArrayList<HashMap<String,String>> tmp_obj /*, ArrayList<HashMap<String,String>> devuser_ary*/) throws SQLException, Exception {
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
				strQuery.append("  FROM CMC0201		     \n");
				strQuery.append(" WHERE CC_SRID   = ?	 \n");
				strQuery.append("   AND CC_EDITOR = ?	 \n");	
				strQuery.append("   AND CC_GBNCD  = ?	 \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, tmp_obj.get(i).get("cc_srid"));
				pstmt.setString(2, tmp_obj.get(i).get("cc_editor"));
				pstmt.setString(3, tmp_obj.get(i).get("cc_gbncd"));
				rs = pstmt.executeQuery();
				param_cnt = 1;
				strQuery.setLength(0);
	        	if ( rs.next() && rs.getInt("cnt") == 0 ){
	        		pstmt.close();
					if(strTypeCd.equals("Sec")){
						strQuery.append("INSERT INTO CMC0201(		                                 \n");
					} else if (strTypeCd.equals("Monitor")){
						strQuery.append("INSERT INTO CMC0202(		                                 \n");
						strQuery.append("cc_syscd,													 \n");
					}
					strQuery.append("cc_srid,   	cc_editor,	 	cc_gbncd,                        \n");
					strQuery.append("			 	     cc_reqyn,      cc_eval   ,     cc_regdt)    \n");
					strQuery.append("VALUES(		 ?,			  	  ?,			 ?,	    	     \n");
					strQuery.append("			 		 ?,               ?,            SYSDATE)     \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_srid"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_editor"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_gbncd"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_reqyn"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_eval"));
					gubun ="insert";
	        	} else {
	        		pstmt.close();
		            strQuery.append("UPDATE CMC0201 SET                   \n");
		            strQuery.append("       CC_REGDT =SYSDATE ,CC_EVAL =? \n");
		            strQuery.append(" WHERE CC_SRID=?                     \n");
		            strQuery.append("   AND CC_EDITOR = ?				  \n");
					strQuery.append("   AND CC_GBNCD  = ?				  \n");
		        	pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_eval"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_srid"));
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_editor"));	    
					pstmt.setString(param_cnt++, tmp_obj.get(i).get("cc_gbncd"));
		        	gubun = "update";
	        	}
	        	pstmt.executeUpdate();
	        	rs.close();
	        	pstmt.close();
			}
			conn.commit();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return gubun;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() SQLException END ##");
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() Exception END ##");
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() connection release exception ##");
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
					ecamsLogger.error("## Cmc0300.setSecChkInsertUpdate() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setSecChkInsertUpdate() method statement

	public Object[] getTestCase(String IsrId, String scmUser, String ReqCd) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		int				  pstmtcount  = 1;
		boolean           findSw      = false;
		
		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			if ( !ReqCd.equals("43") ) {//개발검수 테스트
				strQuery.append("select a.cc_caseseq,a.cc_casename,a.cc_testgbn,a.cc_status \n");
				strQuery.append("  from cmc0210 a       						    \n");
				strQuery.append(" where a.cc_srid=?                 		        \n");
				strQuery.append("   and a.cc_scmuser=?   				            \n");
				strQuery.append("   and nvl(a.cc_status,'0') not in('3', '2')	    \n");
				//pstmt = conn.prepareStatement(strQuery.toString());	
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++,IsrId);
				pstmt.setString(pstmtcount++,scmUser);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				while(rs.next()){	
					findSw = false;
					if (!ReqCd.equals("44") && !rs.getString("cc_testgbn").equals("T")) {
						if (ReqCd.equals("54") && rs.getString("cc_testgbn").equals("B")) findSw = true;
						else if (ReqCd.equals("55") && rs.getString("cc_testgbn").equals("A")) findSw = true;
					} else findSw = true;
					
					if (findSw) {
						findSw = false;
						strQuery.setLength(0);
						strQuery.append("select count(*) cnt from cmc0211 a,cmm0040 b  \n");
						strQuery.append(" where a.cc_srid=? and a.cc_caseseq=?         \n");
						strQuery.append("   and a.cc_testuser=b.cm_userid              \n");
						strQuery.append("   and b.cm_active='1'                        \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, IsrId);
						pstmt2.setString(2, rs.getString("CC_CASESEQ"));
						rs2 = pstmt2.executeQuery();
						if ( rs2.next() && rs2.getInt("cnt")>0 ) {
							findSw = true;
						}
						rs2.close();
						pstmt2.close();
					}
					if (findSw) {
						rst = new HashMap<String, String>();
						rst.put("cc_caseseq",rs.getString("CC_CASESEQ"));
						rst.put("cc_casename",rs.getString("cc_casename"));
//						if (rs.getString("cc_testgbn").equals("B")) rst.put("testgbn", "사전");
//						else if (rs.getString("cc_testgbn").equals("A")) rst.put("testgbn", "사후");
//						else rst.put("testgbn", "사전/사후");
						rst.put("cc_casename",rs.getString("cc_casename"));
						rst.put("cc_status",rs.getString("cc_status"));
						rsval.add(rst);
						rst = null;
					}
				}//end of while-loop statement
			} else {//단위테스트일때
				strQuery.append("select a.cc_caseseq,a.cc_testgbn,a.cc_status,      \n");			
				strQuery.append("        a.cc_casename,a.cc_exprst,a.cc_etc  		\n");
				strQuery.append("  from cmc0200 a       						    \n");
				strQuery.append(" where a.cc_srid=?                 		        \n");
				strQuery.append("   and a.cc_scmuser=?   				            \n");
				strQuery.append("   and nvl(a.cc_status,'0')<>'3'					\n");
				//pstmt = conn.prepareStatement(strQuery.toString());	
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++,IsrId);
				pstmt.setString(pstmtcount++,scmUser);
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				while(rs.next()){			
					rst = new HashMap<String, String>();
					rst.put("cc_caseseq",rs.getString("CC_CASESEQ"));
					rst.put("cc_casename",rs.getString("cc_casename"));
					//rst.put("testgbn",rs.getString("cc_testgbn"));
					rst.put("cc_exprst",rs.getString("cc_exprst"));
					rst.put("cc_etc",rs.getString("cc_etc"));
					rst.put("cc_status",rs.getString("cc_status"));
					rsval.add(rst);
					rst = null;
				}//end of while-loop statement
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
			ecamsLogger.error("## Cmc0300.getTestCase() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.getTestCase() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.getTestCase() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.getTestCase() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.getTestCase() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTestCase() method statement
    
    public String setCaseCopy(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> CopyList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		int				  seq		  = 1;
		int				  i			  = 0;
		int				  pstmtcount  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			if (!etcData.get("reqcd").equals("43")) {  //통합테스트
				strQuery.setLength(0);
				strQuery.append("select nvl(max(cc_caseseq),0) maxseq \n");
				strQuery.append("  from cmc0210                       \n");	
				strQuery.append(" where cc_srid=?       			   \n");
				//pstmt = conn.prepareStatement(strQuery.toString());	
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++,etcData.get("tosrid"));
		        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
		        rs = pstmt.executeQuery();
				if (rs.next()){
					
					seq = rs.getInt("maxseq");
					++seq;
					
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				
				for(i=0;i<CopyList.size();i++){
		        	strQuery.setLength(0);
		    		strQuery.append("insert into cmc0210 (cc_srid,CC_CASESEQ,CC_SCMUSER,CC_STATUS,  \n");
		    		strQuery.append("   cc_userid,cc_testgbn,CC_CREATDT,CC_LASTDT,CC_CASENAME)	    \n");
		    		strQuery.append(" (select ?,?,?,'0',  	        \n");
		    		strQuery.append("         ?,decode(?,'54','B','55','A',cc_testgbn),SYSDATE,SYSDATE,CC_CASENAME  \n");
		    		strQuery.append("    from cmc0210	    \n");
		    		strQuery.append("   where cc_srid=?	    \n");
		    		strQuery.append("     and cc_caseseq=?)	\n");
		    		
		    		//pstmt = conn.prepareStatement(strQuery.toString());
		    		pstmt = new LoggableStatement(conn,strQuery.toString());
		    		pstmtcount = 1;
		          	pstmt.setString(pstmtcount++, etcData.get("tosrid"));
		      	    pstmt.setInt(pstmtcount++, seq);
		          	pstmt.setString(pstmtcount++, etcData.get("toscmuser"));
		          	
		          	
		          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
		          	pstmt.setString(pstmtcount++, etcData.get("reqcd"));
		          	pstmt.setString(pstmtcount++, etcData.get("fromsrid"));
		      	    pstmt.setString(pstmtcount++, CopyList.get(i).get("cc_caseseq"));
		      	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		      		pstmt.executeUpdate();
		      		pstmt.close();
		      		
		      		if (etcData.get("reqcd").equals("44")) {
			        	strQuery.setLength(0);
			    		strQuery.append("insert into cmc0211 (CC_SRID,CC_CASESEQ,CC_STATUS, 	\n");
			    		strQuery.append("   cc_testuser,cc_testgbn,cc_creatdt,cc_lastdt)    	\n");
			    		strQuery.append(" (select ?,?,'0',cc_testuser,cc_testgbn,SYSDATE,SYSDATE\n");
			    		strQuery.append("    from cmc0211 a,cmm0040 b    \n");
			    		strQuery.append("   where a.cc_srid=?     	     \n");
			    		strQuery.append("     and a.cc_testuser<>?       \n");
			    		strQuery.append("     and a.cc_testuser=b.cm_userid  \n");
			    		strQuery.append("     and b.cm_active='1'        \n");
			    		strQuery.append("     and a.cc_caseseq=?)	     \n");
			    		
			    		//pstmt = conn.prepareStatement(strQuery.toString());
			    		pstmt = new LoggableStatement(conn,strQuery.toString());
			    		pstmtcount = 1;
			          	pstmt.setString(pstmtcount++, etcData.get("tosrid"));
			      	    pstmt.setInt(pstmtcount++, seq);
			          	pstmt.setString(pstmtcount++, etcData.get("fromsrid"));
			          	pstmt.setString(pstmtcount++,etcData.get("UserId"));
			      	    pstmt.setString(pstmtcount++, CopyList.get(i).get("cc_caseseq"));
			      	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			      		pstmt.executeUpdate();
			      		pstmt.close();
		      		} else {
			        	strQuery.setLength(0);
			    		strQuery.append("insert into cmc0211 (CC_SRID,CC_CASESEQ,CC_STATUS, 	\n");
			    		strQuery.append("   cc_testuser,cc_testgbn,cc_creatdt,cc_lastdt)    	\n");
			    		strQuery.append("values (?,?,'0',?,?,SYSDATE,SYSDATE)                   \n");
			    		
			    		//pstmt = conn.prepareStatement(strQuery.toString());
			    		pstmt = new LoggableStatement(conn,strQuery.toString());
			    		pstmtcount = 1;
			          	pstmt.setString(pstmtcount++, etcData.get("tosrid"));
			      	    pstmt.setInt(pstmtcount++, seq);
			          	pstmt.setString(pstmtcount++,etcData.get("tester"));
			          	if (etcData.get("reqcd").equals("54")) pstmt.setString(pstmtcount++,"B");
			          	else  pstmt.setString(pstmtcount++,"A");
			      	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			      		pstmt.executeUpdate();
			      		pstmt.close();
		      		}
		        	strQuery.setLength(0);
		    		strQuery.append("insert into cmc0212 (CC_SRID,CC_CASESEQ,CC_STATUS, 	\n");
		    		strQuery.append("   CC_GBNCD,CC_SEQNO,CC_ITEMMSG,CC_LASTDT,CC_EDITOR)	\n");
		    		strQuery.append(" (select ?,?,'0',CC_GBNCD,CC_SEQNO,CC_ITEMMSG,SYSDATE,?\n");
		    		strQuery.append("    from cmc0212	    \n");
		    		strQuery.append("   where cc_srid=?  	\n");
		    		strQuery.append("     and cc_caseseq=?)	\n");
		    		
		    		//pstmt = conn.prepareStatement(strQuery.toString());
		    		pstmt = new LoggableStatement(conn,strQuery.toString());
		    		pstmtcount = 1;
		          	pstmt.setString(pstmtcount++, etcData.get("tosrid"));
		      	    pstmt.setInt(pstmtcount++, seq);
		          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
		          	pstmt.setString(pstmtcount++, etcData.get("fromsrid"));
		      	    pstmt.setString(pstmtcount++, CopyList.get(i).get("cc_caseseq"));
		      	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		      		pstmt.executeUpdate();
		      		pstmt.close();
		      		seq++;
				}
				
			} else {
				strQuery.setLength(0);
				strQuery.append("select nvl(max(cc_caseseq),0) maxseq from cmc0200 \n");	
				strQuery.append(" where CC_SRID = ?    			                   \n");
				//pstmt = conn.prepareStatement(strQuery.toString());	
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++,etcData.get("tosrid"));
		        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
		        rs = pstmt.executeQuery();
				if (rs.next()){
					
					seq = rs.getInt("maxseq");
					++seq;
					
				}//end of while-loop statement
				rs.close();
				pstmt.close();
				for(i=0;i<CopyList.size();i++){
		        	strQuery.setLength(0);
		    		strQuery.append("insert into cmc0200 (CC_SRID,CC_CASESEQ,cc_createuser,cc_scmuser,\n");
		    		strQuery.append("   cc_createdate,CC_TESTGBN,CC_CASENAME,CC_EXPRST,CC_ETC)	\n");
		    		strQuery.append(" (select ?,?,?,?,  SYSDATE,CC_TESTGBN,	\n");
		    		strQuery.append("         CC_CASENAME,CC_EXPRST,CC_ETC	\n");
		    		strQuery.append("    from cmc0200	    \n");
		    		strQuery.append("   where cc_srid=?  	\n");
		    		strQuery.append("     and cc_caseseq=?)	\n");
		    		
		    		//pstmt = conn.prepareStatement(strQuery.toString());
		    		pstmt = new LoggableStatement(conn,strQuery.toString());
		    		pstmtcount = 1;
		          	pstmt.setString(pstmtcount++, etcData.get("tosrid"));
		      	    pstmt.setInt(pstmtcount++, seq++);
		          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
		          	pstmt.setString(pstmtcount++, etcData.get("UserId"));
		          	
		          	
		          	pstmt.setString(pstmtcount++, etcData.get("fromsrid"));
		      	    pstmt.setString(pstmtcount++, CopyList.get(i).get("cc_caseseq"));
		      	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		      		pstmt.executeUpdate();
		      		pstmt.close();
				}
			}
			conn.close();
			conn = null;
			rs = null;
			pstmt = null;
			
	        return retMsg;
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setCaseCopy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.setCaseCopy() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setCaseCopy() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.setCaseCopy() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.setCaseCopy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setCaseCopy() method statement
    
    public String setExcelList(ArrayList<HashMap<String,String>> FileList, HashMap<String,String> etcData) 
	throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		int				  i		  	  = 0;
		int				  pstmtcount  = 1;
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();	
			
			for (i=0 ; i<FileList.size() ; i++)			{
				strQuery.setLength(0);
	    		strQuery.append("insert into cmc0200 (CC_SRID,CC_CASESEQ,CC_SCMUSER,   \n");
	    		strQuery.append("   CC_CREATEUSER,CC_CREATEDATE,CC_TESTGBN,CC_CASENAME,\n");
	    		strQuery.append("   CC_EXPRST,CC_ETC,CC_STATUS)                        \n");
	    		strQuery.append("(SELECT ?,NVL(MAX(CC_CASESEQ)+1,1),?,  	   	       \n");
	    		strQuery.append("        ?,SYSDATE,?,?,  ?,?,'0'	                   \n");
	    		strQuery.append("   FROM CMC0200         	                           \n");
	    		strQuery.append("  WHERE CC_SRID=?)        	                           \n");
	    		
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		//pstmt = new LoggableStatement(conn,strQuery.toString());
	    		pstmtcount = 1;
	          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
	          	pstmt.setString(pstmtcount++, etcData.get("userid"));
	          	pstmt.setString(pstmtcount++, etcData.get("userid"));
	      	    pstmt.setString(pstmtcount++, FileList.get(i).get("cc_testgbn"));
	      	    pstmt.setString(pstmtcount++, FileList.get(i).get("casename"));
	      	    pstmt.setString(pstmtcount++, FileList.get(i).get("caserst"));
	      	    pstmt.setString(pstmtcount++, FileList.get(i).get("caseetc"));
	          	pstmt.setString(pstmtcount++, etcData.get("strSrid"));
	      	   // ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
	      		pstmt.executeUpdate();
	      		pstmt.close();
			} 
						
		    retMsg = "0";
	        
	        conn.close();
			conn = null;
			pstmt = null;
			rs = null;
	        
	        return retMsg;
	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setExcelList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmc0300.setExcelList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmc0300.setExcelList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmc0300.setExcelList() Exception END ##");				
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
					ecamsLogger.error("## Cmc0300.setExcelList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setExcelList() method statement
}