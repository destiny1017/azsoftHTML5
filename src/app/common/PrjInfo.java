/*****************************************************************************************
	1. program ID	: PrjInfo.java
	2. create date	: 2008.08. 29
	3. auth		    : NO Name
	4. update date	:
	5. auth		    :
	6. description	: PrjInfo 프로젝트정보
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

import app.eCmr.Cmr0200;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PrjInfo{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 프로젝트정보를 조회합니다.
	 * @param  String UserId,String SysCd, String ReqCd
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getPrjList(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strProc   = "";
		ArrayList<HashMap<String, String>> rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			   rst	  = null;
		String            svSysCd    = "";
		

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select distinct a.cc_srid,a.cc_reqtitle,                            \n");
			strQuery.append("       to_char(a.cc_createdate,'yyyy/mm/dd') reqday,       \n");
			//strQuery.append("       a.cc_reqcompdate,b.cm_username requser,             \n");
			strQuery.append("       a.cc_reqcompdate,             						\n");
			strQuery.append("       c.cm_deptname reqdept,                              \n");
			strQuery.append("       d.cm_codename cattype,                              \n");
			strQuery.append("       e.cm_codename chgtype,                              \n");
			strQuery.append("       f.cm_codename isrsta,                               \n");
			strQuery.append("       g.cm_codename workrank,                             \n");
			strQuery.append("       a.cc_excdate,a.cc_workrank,                         \n");
			//strQuery.append("       a.cc_reqdept,a.cc_requser,a.cc_cattype,             \n");
			strQuery.append("       a.cc_reqdept,a.cc_cattype,             			\n");
			strQuery.append("       a.cc_chgtype,a.cc_exptime,                          \n");
			strQuery.append("       a.cc_status,a.cc_lastupuser,           	    	    \n"); 
			strQuery.append("       a.cc_workrank workrank,a.cc_createuser,            	\n"); 
			strQuery.append("       a.cc_devdept,a.cc_realdatause,         		        \n"); 
			strQuery.append("       PROGEDIT_YN(a.cc_srid) progyn         		        \n");
			if (etcData.get("reqcd").equals("99")) {
				strQuery.append(",SRSTEPINFO(a.cc_srid) srstep                          \n");
			}
			strQuery.append("  from (select cm_micode,cm_codename from cmm0020 where cm_macode='CATTYPE') d,  \n");
			strQuery.append("       (select cm_micode,cm_codename from cmm0020 where cm_macode='CHGTYPE') e,  \n");
			strQuery.append("       (select cm_micode,cm_codename from cmm0020 where cm_macode='ISRSTA') f,   \n");
			strQuery.append("       (select cm_micode,cm_codename from cmm0020 where cm_macode='WORKRANK') g, \n");
			//strQuery.append("       cmm0100 c,cmm0040 b,cmc0100 a                       \n");
			strQuery.append("       cmm0100 c,cmc0100 a        			                \n");
			if(etcData.get("reqcd").equals("CP54")){
				strQuery.append("   ,cmc0212 b        			            			\n");
			}
			if (etcData.get("reqcd").equals("CP43") || etcData.get("reqcd").equals("CP44") || etcData.get("reqcd").equals("CP54") ) {
				if (etcData.get("qrygbn").equals("00")) {
					strQuery.append(" where to_char(a.cc_createdate,'yyyymmdd')>=?      \n");
					strQuery.append("   and to_char(a.cc_createdate,'yyyymmdd')<=?      \n");
					strQuery.append("   and a.cc_status not in ('0','1','2','3')        \n");
				} else {
					strQuery.append(" where a.cc_status in ('4','5','6','7')            \n");
				}
				if (etcData.get("reqcd").equals("CP43")) {
					strQuery.append("   and exists (select 1 from cmc0200               \n");
					strQuery.append("               where cc_srid=a.cc_srid             \n");
					strQuery.append("                 and cc_status<>'3')               \n");
				} else {
					strQuery.append("   and exists (select 1 from cmc0210               \n");
					strQuery.append("               where cc_srid=a.cc_srid             \n");
					strQuery.append("                 and cc_status<>'3')               \n");
				}
				if(etcData.get("reqcd").equals("CP54")){
					strQuery.append("   and a.cc_srid = b.cc_srid        					\n");
				}
			} else if (etcData.get("reqcd").equals("LINK")) {
				strQuery.append(" where a.cc_status not in ('3','8')                    \n");
				strQuery.append("   and to_char(a.cc_createdate,'yyyymmdd')>=?          \n");
				strQuery.append("   and to_char(a.cc_createdate,'yyyymmdd')<=?          \n");
			} else if (etcData.get("reqcd").equals("99") && etcData.get("qrygbn").equals("99")) {
				strQuery.append(" where a.cc_srid=?                                     \n");
			} else if (etcData.get("qrygbn").equals("00")) {
				strQuery.append(" where ((to_char(a.cc_createdate,'yyyymmdd')>=?        \n");
				strQuery.append("   and to_char(a.cc_createdate,'yyyymmdd')<=?) )        \n");
				//strQuery.append("   or a.cc_status not in ('3','6','7','8','9'))        \n");
			}
			if (etcData.get("reqcd").equals("LINK") || etcData.get("reqcd").equals("CP43") || etcData.get("reqcd").equals("CP44") || etcData.get("reqcd").equals("CP54")) {
			} else if (etcData.get("reqcd").equals("99") && etcData.get("qrygbn").equals("99")) {
				
			} else if (etcData.get("reqcd").equals("41") && !etcData.get("qrygbn").equals("00")) {   //SR등록
				strQuery.append(" where a.cc_status in ('2','4','C')                   \n");
			} else if (etcData.get("reqcd").equals("42") ||   //개발계획
					   etcData.get("reqcd").equals("43") ||   //단위테스트
					   etcData.get("reqcd").equals("54") ||   //개발검수					   
					   etcData.get("reqcd").equals("01") ||   //체크아웃
					   etcData.get("reqcd").equals("02") ||   //이전버전체크아웃
					   etcData.get("reqcd").equals("11") ||   //체크아웃취소
					   etcData.get("reqcd").equals("07") ||   //개발체크인
				       etcData.get("reqcd").equals("03") ||   //테스트적용요청
				       etcData.get("reqcd").equals("08") ||   //개발적용요청	
				       etcData.get("reqcd").equals("04")) {    //운영적용요청
				if (etcData.get("qrygbn").equals("00")) {
					strQuery.append("   and a.cc_status not in ('0','1') \n");
				} else {
					strQuery.append(" where a.cc_status in ('2','4') \n");
				}
			} else if (etcData.get("reqcd").equals("63")) {   //모니터링	
				if (etcData.get("qrygbn").equals("00")) {
					strQuery.append("   and a.cc_status in ('4','5','6','7','9','A','D') \n");
				} else if (etcData.get("qrygbn").equals("01")) {
					strQuery.append(" where a.cc_status in ('4','5') \n");
				} else{
					strQuery.append(" where a.cc_status in ('4','5') \n");
				}
				
			} else if (etcData.get("reqcd").equals("69")) {   //SR종료	
				if (etcData.get("qrygbn").equals("00")) {
					strQuery.append("   and a.cc_status not in ('0','1') \n");
				} else if (etcData.get("qrygbn").equals("01")) {
					strQuery.append(" where a.cc_status in ('6','D') \n");
					strQuery.append("   and a.cc_lastupuser=? \n");
				} else{
					strQuery.append(" where a.cc_status in ('2','4','D') \n");
					strQuery.append("   and SRSTOP_YN(a.cc_srid)='OK' \n");
					strQuery.append("   and a.cc_lastupuser=? \n");
				}
			}	
			if (etcData.get("secuyn").equals("Y")) {
				if( !etcData.get("reqcd").equals("CP54") ){
					strQuery.append("   and SRSTEP_SECUCHK(?,a.cc_srid,?,?)='OK' \n");
				}
				
			}
			/*
			if (!etcData.get("reqcd").equals("99") && !etcData.get("reqcd").equals("XX") &&
				!etcData.get("reqcd").equals("LINK") && !etcData.get("reqcd").equals("CP43") &&
				!etcData.get("reqcd").equals("CP44") && !etcData.get("reqcd").equals("CP54")) {
				strQuery.append("   and exists (select 1 from cmm0120                   \n");
				strQuery.append("                where cm_qrycd=? and cm_cattype=a.cc_cattype \n");
				strQuery.append("                  and cm_useyn='Y')                    \n");
			}*/
			if (etcData.get("qrycd") != null && etcData.get("qrycd") != "") {
				if (etcData.get("qrycd").equals("01")) {
					strQuery.append("and exists (select x.cc_srid                       \n");
					strQuery.append("              from cmm0040 y,cmc0110 x             \n");
					strQuery.append("             where x.cc_srid=a.cc_srid             \n");
					strQuery.append("               and x.cc_status<>'3'                \n");
					strQuery.append("               and x.cc_userid=y.cm_userid         \n");
					strQuery.append("               and y.cm_username=?)                \n");
				} else if (etcData.get("qrycd").equals("02")) {
					strQuery.append("and a.cc_reqtitle like ?                           \n");
				}
			}
			if (etcData.get("cattype") != null && etcData.get("cattype") != "") {
				strQuery.append("and a.cc_cattype=?                                     \n");
			}
			if (etcData.get("reqdept") != null && etcData.get("reqdept") != "") {
				strQuery.append("and a.cc_reqdept=?                                     \n");
			}
			//strQuery.append("  and a.cc_requser=b.cm_userid                             \n");
			strQuery.append("  and a.cc_reqdept=c.cm_deptcd                             \n");
			strQuery.append("  and a.cc_cattype=d.cm_micode(+)                          \n");
			strQuery.append("  and a.cc_chgtype=e.cm_micode(+)                          \n");
			strQuery.append("  and a.cc_workrank=g.cm_micode(+)                         \n");
			strQuery.append("  and a.cc_status=f.cm_micode                              \n");
			strQuery.append("  order by a.cc_srid desc                                  \n");
	        //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        int paramIndex=0;
	        if (etcData.get("reqcd").equals("CP43") || etcData.get("reqcd").equals("CP44") || etcData.get("reqcd").equals("CP54") ) {
	        	if (etcData.get("qrygbn").equals("00")) {
		        	pstmt.setString(++paramIndex, etcData.get("stday"));
		        	pstmt.setString(++paramIndex, etcData.get("edday"));
	        	}
			} else if (etcData.get("reqcd").equals("LINK")) {
	        	pstmt.setString(++paramIndex, etcData.get("stday"));
	        	pstmt.setString(++paramIndex, etcData.get("edday"));
			} else if (etcData.get("reqcd").equals("99") && etcData.get("qrygbn").equals("99")) {
	        	pstmt.setString(++paramIndex, etcData.get("srid"));
	        } else {
		        if (etcData.get("qrygbn").equals("00")) {
		        	pstmt.setString(++paramIndex, etcData.get("stday"));
		        	pstmt.setString(++paramIndex, etcData.get("edday"));
				}
		        if (etcData.get("reqcd").equals("69")) {
		        	if (etcData.get("qrygbn").equals("01") || etcData.get("qrygbn").equals("02")) {
		        		pstmt.setString(++paramIndex, etcData.get("userid"));
		        	}
		        }
				if (etcData.get("secuyn").equals("Y")) {
					pstmt.setString(++paramIndex, etcData.get("userid"));
					pstmt.setString(++paramIndex, etcData.get("reqcd"));
					pstmt.setString(++paramIndex, etcData.get("qrygbn"));
				}
	        }
	        /*
	        if (!etcData.get("reqcd").equals("99") && !etcData.get("reqcd").equals("XX") &&
				!etcData.get("reqcd").equals("LINK") && !etcData.get("reqcd").equals("CP43") &&
				!etcData.get("reqcd").equals("CP44") && !etcData.get("reqcd").equals("CP54")) {
	        	pstmt.setString(++paramIndex, etcData.get("reqcd"));
	        }*/
	        if (etcData.get("qrycd") != null && etcData.get("qrycd") != "") {
	        	if (etcData.get("qrycd").equals("01")) pstmt.setString(++paramIndex, etcData.get("qrytxt"));
	        	else if (etcData.get("qrycd").equals("02")) pstmt.setString(++paramIndex, "%"+etcData.get("qrytxt")+"%");
	        }
			if (etcData.get("cattype") != null && etcData.get("cattype") != "") {
				pstmt.setString(++paramIndex, etcData.get("cattype"));
			}
			if (etcData.get("reqdept") != null && etcData.get("reqdept") != "") {
				pstmt.setString(++paramIndex, etcData.get("reqdept"));
			}
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while(rs.next()){
				if (etcData.get("reqcd").equals("55") && rs.getString("progyn").equals("N")) {
					continue;
				}
				rst = new HashMap<String, String>();
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("cc_reqtitle", rs.getString("cc_reqtitle"));
				
				if (etcData.get("reqcd").equals("01") ||
					etcData.get("reqcd").equals("02") ||
					etcData.get("reqcd").equals("03") ||//20140718 neo. 테스트적용요청구분 추가
					etcData.get("reqcd").equals("04") ||
					etcData.get("reqcd").equals("07") ||
					etcData.get("reqcd").equals("11")) {
					
					rst.put("syscd", "");
					if (etcData.get("reqcd").equals("03") ||//20140718 neo. 테스트적용요청구분 추가
						etcData.get("reqcd").equals("04") ||
						etcData.get("reqcd").equals("07") ||
						etcData.get("reqcd").equals("11")) {
						svSysCd = "";
						strQuery.setLength(0);
						strQuery.append("select distinct cr_syscd from cmr0020 \n");
						strQuery.append(" where cr_isrid=? \n");
						if (etcData.get("reqcd").equals("04")) {
							strQuery.append("   and cr_lstusr=? \n");
							//20140721 neo. 운영배포 일때 프로그램의 상태값 변경. 체크인완료[B] 또는 테스트적용완료[G] 상태만 조회 되도록 수정. CMM0020 테이블 cm_macode='CMR0020' 확인
							//테스트가 있는 시스템 [G] , 테스트가 없는 시스템은 [B] 상태가 조회 됨.
							//변경전:strQuery.append("   and cr_status='D' \n");
							strQuery.append("   and cr_status in ('B','G') \n");
						} else if (etcData.get("reqcd").equals("03")) {
							strQuery.append("   and cr_lstusr=? \n");
							strQuery.append("   and cr_status in ('B','G') \n");
						} else {
							//strQuery.append("   and decode(cr_status,'3',cr_editor,cr_lstusr)=?   \n");
							strQuery.append("   and decode(cr_status,'3',cr_creator,'5',cr_ckoutuser,cr_editor)=?  \n");
							strQuery.append("   and cr_status in ('3','5','B','D','G') \n");
						}
						pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn, strQuery.toString());
						pstmt2.setString(1, rs.getString("cc_srid"));
						pstmt2.setString(2, etcData.get("userid"));
						//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
						rs2 = pstmt2.executeQuery();
						while ( rs2.next() ) {
							svSysCd = svSysCd + ","+rs2.getString("cr_syscd");
						}
						rs2.close();
						pstmt2.close();
						rst.put("syscd", svSysCd);
					}
					rst.put("chgtype", rs.getString("chgtype"));
					rst.put("cc_chgtype", rs.getString("cc_chgtype"));
					rst.put("srid", "["+rs.getString("cc_srid")+"] "+ rs.getString("cc_reqtitle"));
				} else {
					rst.put("createdate", rs.getString("reqday"));
					if (rs.getString("cc_reqcompdate") != null) {
						rst.put("reqcompdat", rs.getString("cc_reqcompdate").substring(0,4)+"/"+
							              rs.getString("cc_reqcompdate").substring(4,6)+"/"+
							              rs.getString("cc_reqcompdate").substring(6));
					}
					//rst.put("requser", rs.getString("requser"));
					rst.put("requser", null);
					rst.put("reqdept", rs.getString("reqdept"));
					rst.put("cattype", rs.getString("cattype"));
					rst.put("chgtype", rs.getString("chgtype"));
					rst.put("status", rs.getString("isrsta"));
					rst.put("cc_status", rs.getString("cc_status"));
					if ( (rs.getString("cc_excdate") != null) && !rs.getString("cc_excdate").equals("")) {
						rst.put("excdate", rs.getString("cc_excdate").substring(0,4)+"/"+
							              rs.getString("cc_excdate").substring(4,6)+"/"+
							              rs.getString("cc_excdate").substring(6));
					}
					rst.put("exptime", rs.getString("cc_exptime"));
					rst.put("workrank", rs.getString("workrank"));
	
					rst.put("cc_excdate", rs.getString("cc_excdate"));
					rst.put("cc_workrank", rs.getString("cc_workrank"));
					rst.put("cc_reqdept", rs.getString("cc_reqdept"));
					//rst.put("cc_requser", rs.getString("cc_requser"));
					rst.put("cc_requser", "");
					rst.put("cc_cattype", rs.getString("cc_cattype"));
					rst.put("cc_chgtype", rs.getString("cc_chgtype"));
					rst.put("cc_devdept", rs.getString("cc_devdept"));
					rst.put("cc_exptime", rs.getString("cc_exptime"));
					rst.put("cc_realdatause", rs.getString("cc_realdatause"));
					rst.put("cc_createuser", rs.getString("cc_createuser"));
					rst.put("cc_lastupuser", rs.getString("cc_lastupuser"));
					rst.put("progyn", rs.getString("progyn"));
					if (etcData.get("reqcd").equals("99")) strProc = rs.getString("srstep");
					else {
						if (rs.getString("cc_status").equals("0") || rs.getString("cc_status").equals("1") || 
							rs.getString("cc_status").equals("2") || rs.getString("cc_status").equals("C")) {
							if (etcData.get("reqcd").equals("42") && rs.getString("cc_status").equals("2")) {
								strProc = "41,42";
							} else strProc = "41,";
						} else if (rs.getString("cc_status").equals("4")) {
							strProc = "41,42,43,44,54,92,93";
						} else if (rs.getString("cc_status").equals("5")) {
							strProc = "41,42,43,44,54,63,92,93";
						} else if (rs.getString("cc_status").equals("6") || rs.getString("cc_status").equals("7") || 
								   rs.getString("cc_status").equals("9") || rs.getString("cc_status").equals("A") ||
								   rs.getString("cc_status").equals("D")) {
							strProc = "41,42,43,44,54,63,69,92,93";
						}
						if (rs.getString("progyn").equals("N")) {
							strProc = strProc.replace(",92,93", "");
						}
					}
					rst.put("isrproc", strProc);
				}
				
				rtList.add(rst);
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
			
			//ecamsLogger.error("++++ rtList ++++"+rtList.toString());
			return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPrjInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getPrjInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPrjInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getPrjInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)		rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrjInfo() method statement
    /**
     * <PRE>
     * 1. MethodName	: get_SelectList
     * 2. ClassName		: Cmc1100
     * 3. Commnet			: SR진행현황을 조회합니다.
     * 4. 작성자				: no name
     * 5. 작성일				: 2010. 12. 15. 오후 2:13:17
     * </PRE>
     * 		@return Object[]
     * 		@param etcData
     * 		@return
     * 		@throws SQLException
     * 		@throws Exception
     */
    public Object[] get_SelectList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;	
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			
			Integer           cnt         = 0;
			String            svPer       = "";
			
			conn = connectionContext.getConnection();
						
			strQuery.append("SELECT a.cc_srid,	a.cc_docid,												  \n");
			strQuery.append("		SUBSTR(a.cc_reqdate,0,4)||'/'||SUBSTR(a.cc_reqdate,5,2)||'/'||SUBSTR(a.cc_reqdate,7,2) reqdate,         \n");
//			strQuery.append("       C.CM_DEPTNAME reqdept,D.CM_USERNAME requser,a.cc_reqtitle,    \n");
			strQuery.append("       C.CM_DEPTNAME reqdept,a.cc_reqtitle,						  			\n");
			strQuery.append("       a.cc_reqcompdate,j.cm_deptname comdept,                       			\n");
			strQuery.append("       TO_CHAR(a.cc_createdate,'YYYY/MM/DD') createdate,             			\n");
			strQuery.append("       f.cm_username createuser,e.cm_deptname devdept,g.cm_username devuser,   \n");
			strQuery.append("       h.cm_codename devsta,i.cm_codename srsta,                     			\n");
			strQuery.append("       a.cc_excdate,to_char(b.cc_enddate,'yyyy/mm/dd') devenddt,     			\n");
			strQuery.append("       to_char(a.cc_compdate,'yyyy/mm/dd') srenddt,                  			\n");
			strQuery.append("       a.cc_status mainsta,b.cc_status substa,                       			\n");
			strQuery.append("       b.cc_devtime,b.cc_userid,b.cc_devstday,b.cc_devedday          			\n");
			strQuery.append("  from (select cm_micode,cm_codename from cmm0020 where cm_macode='ISRSTA') i,    \n");
			strQuery.append("       (select cm_micode,cm_codename from cmm0020 where cm_macode='ISRSTAUSR') h, \n");
//			strQuery.append("       cmm0040 g,cmm0040 f,cmm0100 e,cmm0040 d,cmm0100 c,                \n");
			strQuery.append("       cmm0040 g,cmm0040 f,cmm0100 e,cmm0100 c,cmm0100 j,                \n");
			strQuery.append("       cmc0110 b,cmc0100 a                                               \n");			
			strQuery.append(" where a.cc_status<>'0'                            \n");
			if (etcData.get("reqsta1") != null && etcData.get("reqsta1") != "") {
				if (etcData.get("reqsta1").equals("XX")) {
					strQuery.append("  and a.cc_status not in ('0','3','8','9') \n");
				} else {
					strQuery.append("  and a.cc_status=?                        \n");
				}
			}
			if (etcData.get("reqsta2") != null && etcData.get("reqsta2") != "") {
				if (etcData.get("reqsta2").equals("XX")) {
					strQuery.append("  and b.cc_status not in ('3','8','9') \n");
				} else {
					strQuery.append("  and b.cc_status=?                        \n");
				}
			}
//			else {
//				strQuery.append("  and b.cc_status<>'3'                         \n");
//			}
			if (etcData.get("stday") != null && etcData.get("stday") != "" ) {
				if ((etcData.get("reqsta1") == null || etcData.get("reqsta1") == "") &&
					(etcData.get("reqsta2") == null || etcData.get("reqsta2") == "")) {
					strQuery.append(" and (a.cc_status not in ('0','3','8','9') or (\n");
				} else {
					strQuery.append(" and  \n");
				}
				strQuery.append("     to_char(a.cc_createdate,'yyyymmdd')>=?    \n");
				strQuery.append(" and to_char(a.cc_createdate,'yyyymmdd')<=?    \n");
				if ((etcData.get("reqsta1") == null || etcData.get("reqsta1") == "") &&
					(etcData.get("reqsta2") == null || etcData.get("reqsta2") == "")) {
					strQuery.append(")) \n");
				}
			}
			if (etcData.get("reqdept") != null && etcData.get("reqdept") != "") {
				strQuery.append(" and a.cc_reqdept=?                            \n");
			} 
			if (etcData.get("recvdept") != null && etcData.get("recvdept") != "") {
				strQuery.append(" and f.cm_project=?                            \n");
			} 
			if (etcData.get("reqtit") != null && etcData.get("reqtit") != "") {
				strQuery.append("and (a.cc_reqtitle like ?                       \n");
				strQuery.append("		or a.cc_docid like ?                     \n");
				strQuery.append("		or a.cc_srid like ? )                    \n");
		
			}
			if (etcData.get("selfsw").equals("M")) {
				strQuery.append("and b.cc_userid=?                              \n");
			} else if (etcData.get("selfsw").equals("T")) {
				strQuery.append("and b.cc_deptcd=(select cm_project from cmm0040\n");
				strQuery.append("                  where cm_userid=?)           \n");
			} 
			strQuery.append("   and a.cc_srid=b.cc_srid                         \n");
			strQuery.append("   and a.cc_reqdept=c.cm_deptcd                    \n");
			//strQuery.append("   and a.cc_requser=d.cm_userid                    \n");
			strQuery.append("   and a.cc_devdept=e.cm_deptcd                    \n");
			strQuery.append("   and f.cm_project=j.cm_deptcd                    \n");
			strQuery.append("   and a.cc_createuser=f.cm_userid                 \n");
			strQuery.append("   and b.cc_userid=g.cm_userid                     \n");
			strQuery.append("   and b.cc_status=h.cm_micode                     \n");
			strQuery.append("   and a.cc_status=i.cm_micode                     \n");
			
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			cnt = 0;
			if (etcData.get("reqsta1") != null && etcData.get("reqsta1") != "") {
				if (!etcData.get("reqsta1").equals("XX")) {
					pstmt.setString(++cnt, etcData.get("reqsta1"));
				}
			}
			if (etcData.get("reqsta2") != null && etcData.get("reqsta2") != "") {
				if (!etcData.get("reqsta2").equals("XX")) {
					pstmt.setString(++cnt, etcData.get("reqsta2"));
				}
			}
			if (etcData.get("stday") != null && etcData.get("stday") != "" ) {
				pstmt.setString(++cnt, etcData.get("stday"));
				pstmt.setString(++cnt, etcData.get("edday"));
			}
			if (etcData.get("reqdept") != null && etcData.get("reqdept") != "") {
				pstmt.setString(++cnt, etcData.get("reqdept"));
			} 
			if (etcData.get("recvdept") != null && etcData.get("recvdept") != "") {
				pstmt.setString(++cnt, etcData.get("recvdept"));
			} 
			if (etcData.get("reqtit") != null && etcData.get("reqtit") != "") {
				pstmt.setString(++cnt, "%"+etcData.get("reqtit")+"%");
				pstmt.setString(++cnt, "%"+etcData.get("reqtit")+"%");
				pstmt.setString(++cnt, "%"+etcData.get("reqtit")+"%");
			}
			if (etcData.get("selfsw").equals("M") || etcData.get("selfsw").equals("T")) {
				pstmt.setString(++cnt, etcData.get("userid"));
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());        
			rs = pstmt.executeQuery();
			
			while (rs.next()){
				rst = new HashMap<String, String>();
	        	rst.put("isrid",   rs.getString("cc_srid"));
	        	rst.put("genieid", rs.getString("cc_docid"));
	        	rst.put("reqdate", rs.getString("reqdate"));
	        	rst.put("reqdept", rs.getString("reqdept"));
	        	rst.put("comdept", rs.getString("comdept"));
//	        	rst.put("requser", rs.getString("requser")); 
	        	rst.put("reqtitle",rs.getString("cc_reqtitle"));
	        	rst.put("reqedday",   rs.getString("cc_reqcompdate").substring(0,4)+"/"+
	        			              rs.getString("cc_reqcompdate").substring(4,6)+"/"+
	        			              rs.getString("cc_reqcompdate").substring(6));      
	        	rst.put("recvdept",   rs.getString("devdept"));      
	        	rst.put("recvdate",   rs.getString("createdate"));    
	        	rst.put("recvuser",   rs.getString("createuser"));      
	        	rst.put("devuser",   rs.getString("devuser"));       
	        	rst.put("reqsta1",   rs.getString("srsta"));      
	        	rst.put("reqsta2",   rs.getString("devsta"));
	        	
	        	if (rs.getString("cc_excdate")!=null) {
	        		rst.put("chgedday", rs.getString("cc_excdate").substring(0,4)+"/"+
	  			             rs.getString("cc_excdate").substring(4,6)+"/"+
				             rs.getString("cc_excdate").substring(6)); 
	        	}
	        	if (rs.getString("mainsta").equals("3") || rs.getString("mainsta").equals("7") 
	        			|| rs.getString("mainsta").equals("8") || rs.getString("mainsta").equals("9")) {
	        		if (rs.getString("srenddt") != null) {
	        			rst.put("isreddate", rs.getString("srenddt"));
	        		}
	        		if(rs.getString("mainsta").equals("7")) rst.put("isredgbn", "완료진행중");
	        		else if (rs.getString("mainsta").equals("8")) rst.put("isredgbn", "중단");
		        	else if (rs.getString("mainsta").equals("3")) rst.put("isredgbn", "반려");
		        	else if (rs.getString("mainsta").equals("9")) rst.put("isredgbn", "정상종료");
	        	}
	        	
	        	if (rs.getString("substa").equals("3") || rs.getString("substa").equals("8") || rs.getString("substa").equals("9")) {
	        		if (rs.getString("devenddt") != null) {
		        		rst.put("chgeddate", rs.getString("devenddt")); 
	        		}
	        		if (rs.getString("substa").equals("8")) rst.put("chgedgbn", "진행중단");
		        	else if (rs.getString("substa").equals("3")) rst.put("chgedgbn", "제외");
		        	else if (rs.getString("substa").equals("9")) rst.put("chgedgbn", "정상종료");
	        	}
	        	if (rs.getString("substa").equals("9")) {   //완료
	        		rst.put("color", "9");
	        	} else if (rs.getString("substa").equals("3") ||   //제외
	        		rs.getString("substa").equals("3") ||
	        		rs.getString("substa").equals("A") ) {          //진행중단
	        		rst.put("color", "3");
	        	} else {
	        		if (rs.getString("substa").substring(0,1).equals("0") || rs.getString("substa").substring(0,1).equals("1")) {
	        			rst.put("color", "R");
	        		} else if (rs.getString("substa").substring(0,1).equals("2")) {
	        			rst.put("color", "C");	        			
	        		} else if (rs.getString("substa").substring(0,1).equals("4")) {
	        			rst.put("color", "T");	        			
	        		} else rst.put("color", "P");
	        	}
        		rst.put("chgpercent", "0%");
        		if (rs.getInt("cc_devtime") > 0) {
        			rst.put("chgdevtime", rs.getString("cc_devtime"));
        			rst.put("chgdevterm", rs.getString("cc_devstday").substring(0,4)+"/"+
	  			             rs.getString("cc_devstday").substring(4,6)+"/"+
				             rs.getString("cc_devstday").substring(6)+ "~"+
				             rs.getString("cc_devedday").substring(0,4)+"/"+
	  			             rs.getString("cc_devedday").substring(4,6)+"/"+
				             rs.getString("cc_devedday").substring(6));
	        		strQuery.setLength(0);
	        		strQuery.append("select sum(cc_worktime) sumtime from cmc0111  \n");
	        		strQuery.append(" where cc_srid=? and cc_userid=?             \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		//pstmt2 = new LoggableStatement(conn,strQuery.toString());
	        		pstmt2.setString(1, rs.getString("cc_srid"));
	        		pstmt2.setString(2, rs.getString("cc_userid"));
	        		//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());        
	        		rs2 = pstmt2.executeQuery();
	        		if (rs2.next()) {
	        			if (rs2.getInt("sumtime") > 0) {
	        				//ratio=ratio.format("%.3f%%", (double)totalCnt / (double)totalMemberCnt * 100);
	        				svPer = svPer.format("%.2f%%", rs2.getDouble("sumtime") / rs.getDouble("cc_devtime") * 100);
	        				rst.put("chgpercent", svPer);
	        			}
	        		}
	        		rs2.close();
	        		pstmt2.close();	        		
	        	}

        		rtList.add(rst);
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
			
			return rtList.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.get_SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## PrjInfo.get_SelectList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.get_SelectList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## PrjInfo.get_SelectList() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.get_SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_SelectList() method statement
    
    public String getHomeDir(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    strQuery.append("SELECT replace(cd_devhome,'\','\\') as cd_devhome FROM CMD0300 ");
		    strQuery.append(" WHERE cd_syscd = '99999' and cd_userid=? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, UserId);
            rs = pstmt.executeQuery();
            if (rs.next()){
            	if (rs.getString("cd_devhome") != null && rs.getString("cd_devhome") != "") {
            		return rs.getString("cd_devhome").replace("\\","/");
            	}
			}//end of while-loop statement
            rs.close();
            pstmt.close();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;

            return "";

	    } catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getHomeDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getHomeDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getHomeDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getHomeDir() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getHomeDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getHomeDir() method statement


    public String chkDocseq(String PrjNo,String devHome,String dirPath,boolean insSw,Connection conn)	throws SQLException, Exception {
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		PreparedStatement 	pstmt2       = null;
		ResultSet         	rs2          = null;
		StringBuffer      	strQuery    = new StringBuffer();

		try {
			int                 i = 0;
			String              strPath1 = "";
			String              strSeq   = "";
			String              strUpSeq = "";
			boolean             findSw   = false;
			String              wkDepth[] = null;
			String              fullPath = "";
			String              prjName = "";

			devHome = devHome.replace("\\", "/");
			prjName = devHome.substring(devHome.lastIndexOf("/")+1);
			if (prjName.indexOf(".")>0) prjName = prjName.substring(prjName.indexOf(".")+1);
			dirPath = dirPath.replace("\\", "/");

			if (dirPath.indexOf("/")>0) strPath1 = dirPath.substring(0,dirPath.indexOf("/"));
			else strPath1 = dirPath;

			if (strPath1.indexOf(".")>0) strPath1 = strPath1.substring(strPath1.indexOf(".")+1);
			if (dirPath.indexOf("/")>0) dirPath = strPath1 + dirPath.substring(dirPath.indexOf("/"));
			else dirPath = strPath1;

			strPath1 = prjName + "/"+dirPath;
			wkDepth = strPath1.split("/");
			//ecamsLogger.error("+++++strPath1+++++"+new String(strPath1.getBytes("MS949")));
			for (i=0;wkDepth.length>i;i++) {
				if (i==0) fullPath = wkDepth[i];
				else fullPath = fullPath + "\\" + wkDepth[i];

				findSw = false;
				strQuery.setLength(0);
				strQuery.append("select cd_docseq from cmd0303      \n");
				strQuery.append(" where cd_prjno=? and cd_dirpath=? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, PrjNo);
				pstmt.setString(2, fullPath);
				//new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")
				//ecamsLogger.error(new String((((LoggableStatement)pstmt).getQueryString()).getBytes("MS949")));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					strSeq = rs.getString("cd_docseq");
					strUpSeq = rs.getString("cd_docseq");
					//ecamsLogger.error("++++dircode 1++++"+PrjNo + " " + strSeq+" "+strUpSeq);
					findSw = true;
				} else {
					if (insSw == true) {
						strQuery.setLength(0);
		            	strQuery.append("select lpad(to_number(nvl(max(cd_docseq),0))+1,5,'0') seq \n");
		            	strQuery.append("  from cmd0303 where cd_prjno=?         \n");
		            	pstmt2 = conn.prepareStatement(strQuery.toString());
			            pstmt2.setString(1, PrjNo);
			            rs2 = pstmt2.executeQuery();
			            if (rs2.next()) strSeq = rs2.getString("seq");
			            pstmt2.close();
			            rs2.close();

			           //ecamsLogger.error("++++dircode 2++++"+PrjNo + " " + strSeq+" "+strUpSeq);
		            	strQuery.setLength(0);
		            	strQuery.append("insert into cmd0303 (CD_PRJNO,CD_DOCSEQ, \n");
		            	strQuery.append("   CD_DIRNAME,CD_UPDOCSEQ,CD_DIRPATH) values        \n");
		            	strQuery.append("   (?, ?, ?, ?, ?)         \n");
		            	pstmt2 = conn.prepareStatement(strQuery.toString());
		            	pstmt2.setString(1, PrjNo);
		            	pstmt2.setString(2, strSeq);
		            	pstmt2.setString(3, wkDepth[i]);
		            	pstmt2.setString(4, strUpSeq);
		            	pstmt2.setString(5, fullPath);
		            	pstmt2.executeUpdate();
		            	pstmt2.close();
		            	pstmt2 = null;

		            	strUpSeq = strSeq;
		            	findSw = true;
					} else 	{
						findSw = false;
						break;
					}
				}
				pstmt.close();
				rs.close();
				pstmt = null;
				rs = null;
				rs2 = null;
			}
			if (findSw == false) strSeq = "";
			return strSeq;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1600.dirOpenChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.dirOpenChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.dirOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.dirOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of dirOpenChk() method statement

    public ArrayList<HashMap<String, String>> getPrccnt(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		int               cnt = 0;

		try {
			conn = connectionContext.getConnection();
			//요구사항등록
			strQuery.append("select 'P41' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0100 a                     \n");
			strQuery.append(" where a.cc_status in ('2','4','C')  \n");
			strQuery.append("   and a.cc_createuser=?             \n");
			strQuery.append("   and SRSTEP_SECUCHK(a.cc_createuser,a.cc_srid,'41','01')='OK' \n");
			++cnt;
			//요구사항승인
			strQuery.append(" union                               \n");
			strQuery.append("select 'P41C' cd,count(*) cnt        \n");
			strQuery.append("  from cmc0300 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='1'               \n");
			strQuery.append("   and a.cc_createuser=?             \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status='0'               \n");
			strQuery.append("   and b.cc_qrycd='41'               \n");
			++cnt;
			//개발계획대상
			strQuery.append(" union                               \n");
			strQuery.append("select 'P42' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status in ('2','4')      \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			strQuery.append("   and b.cc_status='1'               \n");
			++cnt;
			//개발실적대상
			strQuery.append(" union                               \n");
			strQuery.append("select 'P48' cd,count(*) cnt        \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='4'               \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			strQuery.append("   and b.cc_status not in ('0','1','3','8','9') \n");
			++cnt;
			//프로그램등록건
			strQuery.append(" union                               \n");
			strQuery.append("select 'P01' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 c                     \n");
			strQuery.append(" where c.cc_status in ('2','A','C')  \n");
			strQuery.append("   and c.cc_userid=?                 \n");
			++cnt;
			//체크아웃건
			strQuery.append(" union                               \n");
			strQuery.append("select 'P02' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 c                     \n");
			strQuery.append(" where c.cc_status in ('2','A','C')  \n");
			strQuery.append("   and c.cc_userid=?                 \n");
			++cnt;
			//체크인
			strQuery.append(" union                               \n");
			strQuery.append("select 'P07' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 c                     \n");
			strQuery.append(" where c.cc_status in ('2','A','C')  \n");
			strQuery.append("   and c.cc_userid=?                 \n");
			strQuery.append("   and exists (select 1 from cmr0020 a    \n");
			strQuery.append("                where cr_isrid=c.cc_srid  \n");
			strQuery.append("                  and decode(cr_status,'3',cr_editor,'4',cr_editor,'5',cr_ckoutuser,cr_lstusr)=?  \n");
			strQuery.append("                  and cr_status in ('3','4','5','A','B','G','F'))  \n");
			++cnt;
			++cnt;
			//수정 및 테스트
			strQuery.append(" union                               \n");
			strQuery.append("select 'P03' cd,count(*) cnt         \n");
			strQuery.append("  from cmr0020 c,cmm0036 b           \n");
			strQuery.append(" where c.cr_status in ('B','D','E','F','G') \n");
			strQuery.append("   and c.cr_lstusr=?                 \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd         \n");
			strQuery.append("   and c.cr_rsrccd=b.cm_rsrccd       \n");
			strQuery.append("   and substr(b.cm_info,26,1)<>'1'   \n");
			strQuery.append("   and b.cm_rsrccd not in            \n");
			strQuery.append("       (select cm_samersrc from cmm0037 \n");
			strQuery.append("         where cm_syscd=b.cm_syscd   \n");
			strQuery.append("           and cm_rsrccd<>cm_samersrc) \n");
			++cnt;
			//단위테스트대상
			strQuery.append(" union                               \n");
			strQuery.append("select 'P43' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='4'               \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status in ('2','A','C')  \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			strQuery.append("   and CHECKINYN(b.cc_userid,b.cc_srid,'43',?,b.cc_status)='OK' \n");
			++cnt;
			++cnt;
			//개발검수요청
			strQuery.append(" union                               \n");
			strQuery.append("select 'P44' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='4'               \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status in ('2','A','C')  \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			strQuery.append("   and SRSTEP_SECUCHK(b.cc_userid,a.cc_srid,'43','02')='OK' \n");
			++cnt;
			//테스트적용
			strQuery.append(" union                               \n");
			strQuery.append("select 'R03' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 c                     \n");
			strQuery.append(" where c.cc_status in ('2','A','C')  \n");
			strQuery.append("   and c.cc_userid=?                 \n");
			strQuery.append("   and exists (select 1 from cmr0020 a  \n");
			strQuery.append("                where cr_isrid=c.cc_srid\n");
			strQuery.append("                  and cr_lstusr=?       \n");
			strQuery.append("                  and cr_status in ('A','B','G','F')    \n");
			strQuery.append("                  and TESTSVR_YN(a.cr_syscd,'03')='OK') \n");
			++cnt;
			++cnt;
			//통합테스트
			strQuery.append(" union                               \n");
			strQuery.append("select 'P54' cd,count(distinct a.cc_srid) cnt \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='4'               \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status in ('7','4')      \n");
			strQuery.append("   and SRSTEP_SECUCHK(?,A.CC_SRID,'54','01')='OK' \n");
			++cnt;
			//운영배포
			strQuery.append(" union                               \n");
			strQuery.append("select 'R04' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 c                     \n");
			strQuery.append(" where c.cc_status in ('2','B','5','C') \n");
			strQuery.append("   and c.cc_userid=?                 \n");
			strQuery.append("   and SRSTEP_SECUCHK(?,c.cc_srid,'04','01')='OK'  \n");
			++cnt;
			++cnt;
			//적용승인
			strQuery.append(" union                               \n");
			strQuery.append("select 'C04' cd,count(*) cnt         \n");
			strQuery.append("  from cmr1000 a                     \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_editor=?                 \n");
			strQuery.append("   and a.cr_qrycd='04'               \n");
			++cnt;
			//모니터링
			strQuery.append(" union                               \n");
			strQuery.append("select 'P63' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0100 a,cmc0110 b           \n");
			strQuery.append(" where a.cc_status='4'               \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			strQuery.append("   and b.cc_status in ('6','D')      \n");
			++cnt;
			//SR완료
			strQuery.append(" union                               \n");
			strQuery.append("select 'P69' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0100 a                     \n");
			strQuery.append(" where a.cc_status in ('6','D')  \n");
			strQuery.append("   and a.cc_createuser=?             \n");
			strQuery.append("   and SRSTEP_SECUCHK(?,a.cc_srid,'69','01')='OK' \n");
			++cnt;
			++cnt;
			//SR완료승인
			strQuery.append(" union                               \n");
			strQuery.append("select 'C69' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0300 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status='A'               \n");
			strQuery.append("   and a.cc_createuser=?             \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status='0'               \n");
			strQuery.append("   and b.cc_qrycd='69'               \n");
			++cnt;
			//요청건수
			strQuery.append(" union                               \n");
			strQuery.append("select b.cr_qrycd cd,count(*) cnt    \n");
			strQuery.append("  from cmr1000 b                     \n");
			strQuery.append(" where b.cr_status ='0'              \n");
			strQuery.append("   and b.CR_EDITOR = ?               \n");
			strQuery.append("  group by b.cr_qrycd                \n");
			++cnt;
			//체크아웃건 중 오류대기 중 건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'E' || substr(a.cr_acptno,5,2) cd,count(*) cnt \n");
			strQuery.append("  from (select cr_acptno from cmr9900\n");
			strQuery.append("         where cr_locat='00'         \n");
			strQuery.append("           and cr_teamcd='1'         \n");
			strQuery.append("           and cr_status='0') a,     \n");
			strQuery.append("      (select y.cr_acptno            \n");
			strQuery.append("         from cmr1000 y,cmr1010 x    \n");
			strQuery.append("        where y.cr_qrycd in('01','02','11','07','04','06','08') \n");
			strQuery.append("          and y.cr_status ='0'       \n");
			strQuery.append("          and y.CR_EDITOR = ?        \n");
			strQuery.append("          and y.cr_acptno=x.cr_acptno\n");
			strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000'\n");
			strQuery.append("        group by y.cr_acptno) c      \n");
			strQuery.append(" where c.cr_acptno=a.cr_acptno       \n");
			strQuery.append(" group by substr(a.cr_acptno,5,2)    \n");
			++cnt;
			//결재대상건1
			strQuery.append(" union                               \n");
			strQuery.append("select 'CF1' cd,count(*) cnt          \n");
			strQuery.append("  from cmr1000 b,cmr9900 a           \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd in ('2','3','6','7','8','P') \n");
			strQuery.append("   and a.cr_team=?                   \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno 	  \n");
			strQuery.append("   and b.cr_status not in('3','9')   \n");
			++cnt;
			//결재대상건2
			strQuery.append(" union                               \n");
			strQuery.append("select 'CF2' cd,count(*) cnt          \n");
			strQuery.append("  from (                             \n");
			strQuery.append("        select distinct b.cr_acptno  \n");
			strQuery.append("          from cmm0043 c,cmr1000 b,cmr9900 a \n");
			strQuery.append("         where a.cr_status='0'               \n");
			strQuery.append("           and a.cr_locat='00'               \n");
			strQuery.append("           and a.cr_teamcd not in ('2','3','6','7','8','P') \n");
			strQuery.append("           and instr(a.cr_team,c.cm_rgtcd)>0 \n");
			strQuery.append("           and c.cm_userid=?           	  \n");
			strQuery.append("           and a.cr_acptno=b.cr_acptno 	  \n");
			strQuery.append("           and b.cr_status not in('3','9'))  \n");
			++cnt;
			//결재대상건3
			strQuery.append(" union                               \n");
			strQuery.append("select 'CF3' cd,count(*) cnt          \n");
			strQuery.append("  from cmc0300 b,cmr9900 a           \n");
			strQuery.append(" where a.cr_status='0'               \n");
			strQuery.append("   and a.cr_locat='00'               \n");
			strQuery.append("   and a.cr_teamcd in ('2','3','6','7','8','P') \n");
			strQuery.append("   and a.cr_team=?                   \n");
			strQuery.append("   and a.cr_acptno=b.cc_acptno 	  \n");
			++cnt;
			//결재대상건4
			strQuery.append(" union                               \n");
			strQuery.append("select 'CF4' cd,count(*) cnt          \n");
			strQuery.append("  from (                             \n");
			strQuery.append("        select distinct a.cr_acptno  \n");
			strQuery.append("          from cmm0043 c,cmc0300 b,cmr9900 a \n");
			strQuery.append("         where a.cr_status='0'               \n");
			strQuery.append("           and a.cr_locat='00'               \n");
			strQuery.append("           and a.cr_teamcd not in ('2','3','6','7','8','P') \n");
			strQuery.append("           and instr(a.cr_team,c.cm_rgtcd)>0 \n");
			strQuery.append("           and c.cm_userid=?           	  \n");
			strQuery.append("           and a.cr_acptno=b.cc_acptno) 	  \n");
			++cnt;
			//SR진행건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'SR1' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 b,cmc0100 a           \n");
			strQuery.append(" where a.cc_status in ('2','4')      \n");
			strQuery.append("   and a.cc_srid=b.cc_srid           \n");
			strQuery.append("   and b.cc_status not in ('3','8','9') \n");
			strQuery.append("   and b.cc_userid=?                 \n");
			++cnt;
			//SR진행건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'RB1' cd,count(*) cnt         \n");
			strQuery.append("  from cmr1000 a                     \n");
			strQuery.append(" where a.cr_qrycd='06'               \n");
			strQuery.append("   and a.cr_status<>'3'              \n");
			strQuery.append("   and a.cr_prcdate is not null      \n");
			strQuery.append("   and a.cr_relatno is null          \n");
			strQuery.append("   and a.cr_editor=?                 \n");
			++cnt;
			//반려건수
			strQuery.append(" union                               \n");
			strQuery.append("select 'CN1' cd,count(*) cnt         \n");
			strQuery.append("  from cmc0110 a,cmr1000 b           \n");
			strQuery.append(" where a.cc_userid=?                 \n");
			strQuery.append("   and a.cc_status not in ('0','1','3','8','9') \n");
			strQuery.append("   and a.cc_srid=b.cr_itsmid         \n");
			strQuery.append("   and a.cc_userid=b.cr_editor       \n");
			strQuery.append("   and b.cr_qrycd='04'               \n");
			strQuery.append("   and b.cr_status='3'               \n");
			strQuery.append("   and to_char(b.cr_prcdate,'yyyymmdd')=to_char(SYSDATE,'yyyymmdd')  \n");  //당일반려건만
			//strQuery.append("   and nvl(b.cr_cncl,'0')<>'1'       \n");   회수건은 제외
			++cnt;

            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            for (int i=0;cnt>i;i++) {
            	pstmt.setString(pstmtcount++, UserID);
            }
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("CD", rs.getString("cd"));
				rst.put("cnt", rs.getString("cnt"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.error("+++rtList+++"+rtList.toString());
			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPrccnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getPrccnt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPrccnt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getPrccnt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getPrccnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    /*
     * getDevcnt 개발중 상태 건수
     */
    public ArrayList<HashMap<String, String>> getDevcnt(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int rstcnt = 0;
		try {
			conn = connectionContext.getConnection();

            rtList.clear();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt						\n");
			strQuery.append("  from cmr0020 c,cmm0036 b, cmm0044 a      \n");
			strQuery.append(" where c.cr_status='3'               	    \n");
			strQuery.append("   and c.cr_lstver=0                       \n");
			strQuery.append("   and c.CR_EDITOR = ?                     \n");
			strQuery.append("   and substr(b.cm_info, 2, 1)='1'         \n");
			strQuery.append("   and substr(b.cm_info, 26, 1)='0'        \n");
			strQuery.append("   and b.cm_closedt is null                \n");
			strQuery.append("   and c.cr_syscd=b.cm_syscd               \n");
			strQuery.append("   and c.cr_rsrccd=b.cm_rsrccd             \n");
			strQuery.append("   and b.cm_rsrccd not in                  \n");
			strQuery.append("       (select cm_samersrc from cmm0037    \n");
			strQuery.append("         where cm_syscd=b.cm_syscd         \n");
			strQuery.append("           and cm_rsrccd<>cm_samersrc)     \n");
			strQuery.append("   and c.cr_editor = a.cm_userid           \n");
			strQuery.append("   and c.cr_jobcd = a.cm_jobcd             \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, UserID);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst = new HashMap<String,String>();
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "신규상태"); //chart 항목명

					rstcnt = Integer.parseInt(rs.getString("cnt"));

					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt						\n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c       \n");
			strQuery.append(" where b.cr_qrycd = '01'                   \n");
			strQuery.append("   and b.cr_status ='9'                    \n");
			strQuery.append("   and b.CR_EDITOR = ?                     \n");
			strQuery.append("   and a.CR_CONFNO is null                 \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	        \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID           \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID             \n");
			strQuery.append("   and c.cr_status='5'                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst = new HashMap<String,String>();
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "체크아웃상태"); //chart 항목명

					rstcnt = rstcnt + Integer.parseInt(rs.getString("cnt"));

					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt					\n");
			strQuery.append("  from cmr1010 a,cmr1000 b,cmr0020 c   \n");
			strQuery.append(" where b.cr_qrycd = '03'               \n");
			strQuery.append("   and b.cr_status ='9'                \n");
			strQuery.append("   and b.CR_EDITOR = ?                 \n");
			strQuery.append("   and a.CR_CONFNO is null             \n");
			strQuery.append("   and b.cr_acptno=a.cr_acptno 	    \n");
			strQuery.append("   and a.CR_BASEITEM=a.CR_ITEMID       \n");
			strQuery.append("   and a.CR_itemid=c.CR_ITEMID         \n");
			strQuery.append("   and c.cr_status='B'                 \n");;
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst = new HashMap<String,String>();
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "테스트적용상태"); //chart 항목명

					rstcnt = rstcnt + Integer.parseInt(rs.getString("cnt"));

					rtList.add(rst);
					rst = null;
				}
			}//end of while-loop statement

			if(rstcnt == 0){
				rst = new HashMap<String,String>();
				rst.put("amount", ""); //chart 값
				rst.put("expense", "O"); //chart 항목명
				rtList.add(rst);
				rst = null;
			}

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;


			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getDevcnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getDevcnt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getDevcnt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getDevcnt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getDevcnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    /*
     * getProcnt 진행중 상태 건수 (오류건수)
     */
    public ArrayList<HashMap<String, String>> getProcnt(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt1       = null;
		ResultSet         rs          = null;
		ResultSet         rs1          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int rstcnt = 0;
		try {
			conn = connectionContext.getConnection();

            rtList.clear();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt 					\n");
			strQuery.append("from cmr1000 b                         \n");
			strQuery.append("where b.cr_qrycd<'20'                  \n");
			strQuery.append("and b.cr_status ='0'                   \n");
			strQuery.append("and b.CR_EDITOR = ?                    \n");
			strQuery.append("and b.cr_qrycd in ('01','02')          \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				rst = new HashMap<String,String>();
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "체크아웃 중"); //chart 항목명

					rstcnt = Integer.parseInt(rs.getString("cnt"));

					strQuery.setLength(0);
					strQuery.append("select count(*) cnt							\n");
					strQuery.append("  from cmr9900 a,                              \n");
					strQuery.append("      (select y.cr_acptno                      \n");
					strQuery.append("         from cmr1000 y,cmr1010 x              \n");
					strQuery.append("        where y.cr_qrycd in ('01','02')        \n");
					strQuery.append("          and y.cr_status ='0'                 \n");
					strQuery.append("          and y.CR_EDITOR = ?                  \n");
					strQuery.append("          and y.cr_acptno=x.cr_acptno          \n");
					strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000' \n");
					strQuery.append("        group by y.cr_acptno) c                \n");
					strQuery.append(" where c.cr_acptno=a.cr_acptno                 \n");
					strQuery.append("   and a.CR_locat='00'                         \n");
					strQuery.append("   and a.cr_teamcd='1'                         \n");
					pstmt1 = conn.prepareStatement(strQuery.toString());
					//pstmt1 = new LoggableStatement(conn, strQuery.toString());
					pstmt1.setString(1, UserID);
					//ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
		            rs1 = pstmt1.executeQuery();
		            if(rs1.next()){
		            	rst.put("err", rs1.getString("cnt")); //chart 오류건수
		            }
		            rs1.close();
		            pstmt1.close();

					rtList.add(rst);
					rst = null;
				}
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt   			\n");
			strQuery.append("  from cmr1000 b               \n");
			strQuery.append(" where b.cr_qrycd<'20'         \n");
			strQuery.append("   and b.cr_status ='0'        \n");
			strQuery.append("   and b.CR_EDITOR = ?         \n");
			strQuery.append("   and b.cr_qrycd = '03'       \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				rst = new HashMap<String,String>();
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "테스트체크인 중"); //chart 항목명

					rstcnt = rstcnt + Integer.parseInt(rs.getString("cnt"));

					strQuery.setLength(0);
					strQuery.append("select count(*) cnt							\n");
					strQuery.append("  from cmr9900 a,                              \n");
					strQuery.append("      (select y.cr_acptno                      \n");
					strQuery.append("         from cmr1000 y,cmr1010 x              \n");
					strQuery.append("        where y.cr_qrycd = '03'                \n");
					strQuery.append("          and y.cr_status ='0'                 \n");
					strQuery.append("          and y.CR_EDITOR = ?                  \n");
					strQuery.append("          and y.cr_acptno=x.cr_acptno          \n");
					strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000' \n");
					strQuery.append("        group by y.cr_acptno) c                \n");
					strQuery.append(" where c.cr_acptno=a.cr_acptno                 \n");
					strQuery.append("   and a.CR_locat='00'                         \n");
					strQuery.append("   and a.cr_teamcd='1'                         \n");
					pstmt1 = conn.prepareStatement(strQuery.toString());
					//pstmt1 = new LoggableStatement(conn, strQuery.toString());
					pstmt1.setString(1, UserID);
					//ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
		            rs1 = pstmt1.executeQuery();
		            if(rs1.next()){
		            	rst.put("err", rs1.getString("cnt")); //chart 오류건수
		            }
		            rs1.close();
		            pstmt1.close();

					rtList.add(rst);
					rst = null;
				}
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt   			\n");
			strQuery.append("  from cmr1000 b               \n");
			strQuery.append(" where b.cr_qrycd<'20'         \n");
			strQuery.append("   and b.cr_status ='0'        \n");
			strQuery.append("   and b.CR_EDITOR = ?         \n");
			strQuery.append("   and b.cr_qrycd = '04'       \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				rst = new HashMap<String,String>();
				if(Integer.parseInt(rs.getString("cnt"))>0){
					rst.put("amount", rs.getString("cnt")); //chart 값
					rst.put("expense", "운영체크인 중"); //chart 항목명

					rstcnt = rstcnt + Integer.parseInt(rs.getString("cnt"));

					strQuery.setLength(0);
					strQuery.append("select count(*) cnt							\n");
					strQuery.append("  from cmr9900 a,                              \n");
					strQuery.append("      (select y.cr_acptno                      \n");
					strQuery.append("         from cmr1000 y,cmr1010 x              \n");
					strQuery.append("        where y.cr_qrycd = '04'                \n");
					strQuery.append("          and y.cr_status ='0'                 \n");
					strQuery.append("          and y.CR_EDITOR = ?                  \n");
					strQuery.append("          and y.cr_acptno=x.cr_acptno          \n");
					strQuery.append("          and nvl(x.cr_putcode,'0000')<>'0000' \n");
					strQuery.append("        group by y.cr_acptno) c                \n");
					strQuery.append(" where c.cr_acptno=a.cr_acptno                 \n");
					strQuery.append("   and a.CR_locat='00'                         \n");
					strQuery.append("   and a.cr_teamcd='1'                         \n");
					pstmt1 = conn.prepareStatement(strQuery.toString());
					//pstmt1 = new LoggableStatement(conn, strQuery.toString());
					pstmt1.setString(1, UserID);
					//ecamsLogger.error(((LoggableStatement)pstmt1).getQueryString());
		            rs1 = pstmt1.executeQuery();
		            if(rs1.next()){
		            	rst.put("err", rs1.getString("cnt")); //chart 오류건수
		            }
		            rs1.close();
		            pstmt1.close();

					rtList.add(rst);
					rst = null;
				}
			}

			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select count(*) cnt                                        \n");
			strQuery.append("  from cmr1000 b,cmr9900 a                                 \n");
			strQuery.append(" where a.cr_status='0'                                     \n");
			strQuery.append("   and a.cr_locat='00'                                     \n");
			strQuery.append("   and a.cr_teamcd in ('2','3','6','7','8','P')            \n");
			strQuery.append("   and a.cr_team=?                                  		\n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno 	                        \n");
			strQuery.append("   and b.cr_status not in('3','9')                         \n");
			strQuery.append("union                                                      \n");
			strQuery.append("select count(*) cnt                                        \n");
			strQuery.append("  from (                                                   \n");
			strQuery.append("    select distinct b.cr_acptno                            \n");
			strQuery.append("      from cmm0043 c,cmr1000 b,cmr9900 a                   \n");
			strQuery.append("     where a.cr_status='0'                                 \n");
			strQuery.append("       and a.cr_locat='00'                                 \n");
			strQuery.append("       and a.cr_teamcd not in ('2','3','6','7','8','P')    \n");
			strQuery.append("       and instr(a.cr_team,c.cm_rgtcd)>0                   \n");
			strQuery.append("       and c.cm_userid=?              	                	\n");
			strQuery.append("       and a.cr_acptno=b.cr_acptno 	                    \n");
			strQuery.append("       and b.cr_status not in('3','9'))                    \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserID);
			pstmt.setString(2, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            int amount = 0;
			while (rs.next()){
				if(amount==0){
					amount = Integer.parseInt(rs.getString("cnt")); //chart 값
				}else{
					amount = amount + Integer.parseInt(rs.getString("cnt")); //chart 값
				}
			}//end of while-loop statement

			if(amount>0){
				rst = new HashMap<String,String>();
				rst.put("amount", Integer.toString(amount)); //chart 값
				rst.put("expense", "결재대기상태"); //chart 항목명
				rst.put("err", "X");

				rstcnt = rstcnt + amount;

				rtList.add(rst);
				rst = null;
			}
			if(rstcnt == 0){
				rst = new HashMap<String,String>();
				rst.put("amount", ""); //chart 값
				rst.put("expense", ""); //chart 항목명
				rst.put("err", "O");
				rtList.add(rst);
				rst = null;
			}

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			rs1 = null;
			pstmt1 = null;
			conn = null;


			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getProcnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getProcnt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getProcnt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getProcnt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs1 != null)     try{rs1.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt1 != null)  try{pstmt1.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getProcnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    public Object[] getSREnd(String srID) throws SQLException, Exception {
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
			strQuery.append("SELECT a.cc_confmsg,b.cm_username,a.cc_editor,a.cc_endgbn,\n");
			strQuery.append("       to_char(a.cc_lastdt,'yyyy/mm/dd hh24:mi') lastdt,  \n");
			strQuery.append("       (select max(cc_acptno) from cmc0300                \n");
			strQuery.append("         where cc_srid=? and cc_qrycd='69') acptno        \n");
			strQuery.append("  FROM cmm0040 b, cmc0290 a				 			   \n");
			strQuery.append(" WHERE a.cc_srid=? and a.cc_gbncd='E'                     \n");
			strQuery.append("  	AND a.cc_editor=b.cm_userid							   \n");	
			pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        pstmt.setString(2, srID);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	rst = new HashMap<String, String>();
				rst.put("cc_confmsg", rs.getString("cc_confmsg"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cc_editor", rs.getString("cc_editor"));
				rst.put("lastdt", rs.getString("lastdt"));		
				rst.put("cc_endgbn", rs.getString("cc_endgbn"));	
				rst.put("acptno", rs.getString("acptno"));				
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
			ecamsLogger.error("## PrjInfo.getSREnd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## PrjInfo.getSREnd() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getSREnd() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## PrjInfo.getSREnd() Exception END ##");				
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
					ecamsLogger.error("## PrjInfo.getSREnd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSREnd() method statement
	
	public String insertSREnd(HashMap<String,String> tmp_obj,ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet		  rs		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		int 			  param_cnt	  = 1;
		int               i           = 0;
		String            inUpGubun   = "";
		boolean           endSw       = false;
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			if (ConfList != null && ConfList.size()>0) endSw = true;
			strQuery.setLength(0);
			strQuery.append("SELECT count(*) as cnt                     \n");
			strQuery.append("  FROM cmc0290		                        \n");
			strQuery.append(" WHERE cc_srid = ?							\n");
			strQuery.append("   AND cc_gbncd='E'                        \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
		 	pstmt.setString(1, tmp_obj.get("cc_srid"));
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();			
        	if (rs.next()){
        		i = rs.getInt("cnt");
        	}			
			rs.close();
			pstmt.close();
			
			conn.setAutoCommit(false);
			if ( i == 0 ) {
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMC0290                           \n");
				strQuery.append("    (cc_srid,cc_gbncd,cc_seqno,cc_lastdt,     \n");
				strQuery.append("	  cc_editor,cc_confmsg,cc_status,cc_endgbn)\n");
				strQuery.append("(SELECT ?,'E',nvl(max(cc_seqno),0)+1,SYSDATE, \n");
				strQuery.append("		 ?,?,?,?             		           \n");
				strQuery.append("	FROM CMC0290              		           \n");
				strQuery.append("  WHERE CC_SRID=?)            		           \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				param_cnt = 1;				
				pstmt.setString(param_cnt++, tmp_obj.get("cc_srid"));
				pstmt.setString(param_cnt++, tmp_obj.get("cc_editor"));
				pstmt.setString(param_cnt++, tmp_obj.get("cc_confmsg"));
				if (endSw) pstmt.setString(param_cnt++,"0");
				else pstmt.setString(param_cnt++,"9");
				if (tmp_obj.get("endgbn").equals("01")) pstmt.setString(param_cnt++, "9");
				else pstmt.setString(param_cnt++, "3");
				pstmt.setString(param_cnt++, tmp_obj.get("cc_srid"));
				pstmt.executeUpdate();
				inUpGubun = "INSERT";
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.close();
			} else {
	            strQuery.setLength(0);
	            strQuery.append("UPDATE CMC0290 SET                      \n");
	            strQuery.append("       CC_LASTDT =SYSDATE,CC_CONFMSG=?, \n");
	            strQuery.append("       CC_STATUS=?,cc_endgbn=?          \n");
	            strQuery.append(" WHERE CC_SRID=?                        \n");//
	            strQuery.append("   AND CC_GBNCD='E'                     \n");//
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, tmp_obj.get("cc_confmsg"));
				if (endSw) pstmt.setString(2,"0");
				else pstmt.setString(2,"9");
				if (tmp_obj.get("endgbn").equals("01")) pstmt.setString(3, "9");
				else pstmt.setString(3, "3");
			    pstmt.setString(4, tmp_obj.get("cc_srid"));
	        	pstmt.executeUpdate();
	        	inUpGubun = "UPDATE";
	        	pstmt.close();
        	}
			if (endSw) {
				AutoSeq autoseq = new AutoSeq();
				String AcptNo;
	        	do {
			        AcptNo = autoseq.getSeqNo(conn,tmp_obj.get("reqcd"));

			        i = 0;
			        strQuery.setLength(0);		        
			        strQuery.append("select count(*) as cnt from cmc0300 \n");
		        	strQuery.append(" where cc_acptno= ?                 \n");		        
		        	
		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt.setString(1, AcptNo);		        	
		        	rs = pstmt.executeQuery();
		        	
		        	if (rs.next()){
		        		i = rs.getInt("cnt");
		        	}	        	
		        	rs.close();
		        	pstmt.close();
		        } while(i>0);
	        	
	    		String retMsg = base_Confirm(AcptNo,tmp_obj.get("cc_srid"),tmp_obj.get("cc_editor"),tmp_obj.get("reqcd"),conn);
	    		if (!retMsg.equals("0")) {
					conn.rollback();
					conn.close();
					throw new Exception("SR완료승인요청기본 정보 등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
	    		}
	    		
	    		Cmr0200 cmr0200 = new Cmr0200();
				retMsg = cmr0200.request_Confirm(AcptNo,"99999",tmp_obj.get("reqcd"),tmp_obj.get("cc_editor"),true,ConfList,conn);
				if (!retMsg.equals("OK")) {
					conn.rollback();
					conn.close();
					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
				}
			} 
			//SR완료 Update
			param_cnt = 0;
			strQuery.setLength(0);
			strQuery.append("update cmc0100                    \n");
			strQuery.append("   set cc_status=?,               \n");
			strQuery.append("       cc_compdate=sysdate        \n");
			strQuery.append(" where cc_srid=?                  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			if (endSw) pstmt.setString(++param_cnt,"A");
			else {
				if (tmp_obj.get("endgbn").equals("01")) pstmt.setString(++param_cnt,"9");
				else pstmt.setString(++param_cnt,"8");
			}
			pstmt.setString(++param_cnt, tmp_obj.get("cc_srid"));
			pstmt.executeUpdate();
			pstmt.close();
			
			conn.commit();
			
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
//			return SR_ID;
			return inUpGubun;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0100.insertPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## PrjInfo.insertSREnd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## PrjInfo.insertSREnd() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmc0100.insertPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## PrjInfo.insertSREnd() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## PrjInfo.insertSREnd() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.insertSREnd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of insertSREnd() method statement

    public Object[] getPMSInfo_new(String UserID) throws SQLException, Exception
    {
    	Connection          connPMS     = null;
		PreparedStatement 	pstmt       = null;
		ResultSet         	rs          = null;
		HashMap<String, String>	rst		= null;
		ArrayList<HashMap<String, String>>	 rtList	 = new ArrayList<HashMap<String, String>>();
		StringBuffer      	strQuery    = new StringBuffer();
		List<String>        userList    = new ArrayList<String>();


		ConnectionContext connectionContextPMS = new ConnectionResource(false,"PMS");//PMS MSSQL DB연결

		try {

			connPMS = connectionContextPMS.getConnection();//BPI DB연결

        	rst = new HashMap<String,String>();
			rst.put("prj_cd", "");
			rst.put("prj_nm", "");
			rst.put("pmsname", "선택하세요");
			rtList.add(rst);
			rst = null;

//			PRJ_CD	char	no	13
//			PRJ_NM	varchar	no	200
//			EMP_NO	varchar	no	20
//			EMP_PNM	varchar	no	20
//			CORP_NM 그룹사
//			DEPT_NM 부서
//			ASK_EMP_NM 담당자
//			ASK_REG_DATE 요청일
//			PRJ_TYPE 업무구분

	    	strQuery.setLength(0);
	    	strQuery.append("select distinct prj_cd, prj_nm,isnull(corp_nm,'') as corp_nm, \n");
	    	strQuery.append("       isnull(dept_nm,'') as dept_nm,ask_emp_nm,ask_reg_date,prj_type \n");
	    	strQuery.append("  from VI_ECAMS_PROJECT_QUERY  \n");
	    	strQuery.append(" where emp_no = ? ");
	    	pstmt = connPMS.prepareStatement(strQuery.toString());
	    	//pstmt = new LoggableStatement(connPMS, strQuery.toString());
			pstmt.setString(1, UserID);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	rs = pstmt.executeQuery();
	    	while(rs.next()){
	    		rst = new HashMap<String,String>();
	    		rst.put("prj_cd", rs.getString("prj_cd"));
	    		rst.put("prj_nm", rs.getString("prj_nm"));
	    		rst.put("pmsname", "["+rs.getString("prj_cd")+"] "+rs.getString("prj_nm"));
	    		rst.put("corp_nm", rs.getString("corp_nm"));
	    		rst.put("dept_nm", rs.getString("dept_nm"));
	    		rst.put("ask_emp_nm", rs.getString("ask_emp_nm"));
	    		rst.put("ask_reg_date", rs.getString("ask_reg_date"));
	    		rst.put("prj_type",rs.getString("prj_type"));
				rtList.add(rst);
				rst = null;
	    	}
        	rs.close();
        	pstmt.close();
        	connPMS.close();

        	if (rtList.size() == 1) rtList.clear();
        	else ecamsLogger.error("getPMSInfo_new():"+rtList.toString());

        	rs = null;
        	pstmt = null;
        	connPMS = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPMSInfo_new() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getPMSInfo_new() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getPMSInfo_new() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getPMSInfo_new() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rtList != null)     rtList = null;
			if (userList != null)   userList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (connPMS != null){
				try{
					ConnectionResource.release(connPMS);
				}catch(Exception ex4){
					ecamsLogger.error("## PrjInfo.getPMSInfo_new() connection release exception ##");
					ex4.printStackTrace();
				}
			}
		}
	}//end of getPMSInfo_new() method statement

    public Object[] getAcptHist(String IsrId, String UserId,String qryGbn) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		int				  pstmtcount  = 1;
		Object[] returnObjectArray		 = null;
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			if (qryGbn.equals("A")) {
				strQuery.append("select a.cr_acptno,b.cm_sysmsg,a.cr_editor,c.cm_username,           \n");
				strQuery.append(" 		a.cr_syscd,a.cr_status,d.cm_codename qrycd,a.cr_passok,      \n"); 
				strQuery.append("    	TO_CHAR(a.cr_acptdate,'yyyy/mm/dd') as acptdate,a.cr_qrycd,  \n");
				strQuery.append("    	TO_CHAR(a.cr_prcdate,'yyyy/mm/dd') as prcdate,a.cr_prcreq,   \n");	
				strQuery.append("    	e.cm_codename status,a.cr_prcreq,f.cm_codename passok        \n");	
				strQuery.append("  from cmr1000 a,cmm0030 b,cmm0040 c, cmm0020 d,cmm0020 e,cmm0020 f \n"); 
				strQuery.append(" where a.cr_itsmid=? and a.cr_editor=?                              \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd                                        \n");
				strQuery.append("   and a.cr_editor=c.cm_userid                                      \n");
				strQuery.append("   and d.cm_macode='REQUEST' and d.cm_micode=a.cr_qrycd             \n");
				strQuery.append("   and e.cm_macode='CMR1000' and e.cm_micode=a.cr_status            \n");
				strQuery.append("   and f.cm_macode='REQPASS' and f.cm_micode=a.cr_passok            \n");
				strQuery.append(" order by a.cr_acptdate                                             \n");
			} else {
				strQuery.append("select a.cr_acptno,b.cm_sysmsg,a.cr_editor,c.cm_username,           \n");
				strQuery.append(" 		a.cr_syscd,a.cr_status,d.cm_codename qrycd,a.cr_passok,      \n"); 
				strQuery.append("    	TO_CHAR(a.cr_acptdate,'yyyy/mm/dd') as acptdate,a.cr_qrycd,  \n");
				strQuery.append("    	TO_CHAR(a.cr_prcdate,'yyyy/mm/dd') as prcdate,a.cr_prcreq,   \n");	
				strQuery.append("    	e.cm_codename status,a.cr_prcreq,f.cm_codename passok,       \n");	
				strQuery.append("    	g.cr_rsrcname,h.cm_dirpath,g.cr_itemid                       \n");	
				strQuery.append("  from cmm0070 h,cmr1010 g,cmr1000 a,cmm0030 b,cmm0040 c, cmm0020 d,cmm0020 e,cmm0020 f \n"); 
				strQuery.append(" where a.cr_itsmid=? and a.cr_editor=?                              \n");
				strQuery.append("   and a.cr_syscd=b.cm_syscd                                        \n");
				strQuery.append("   and a.cr_editor=c.cm_userid                                      \n");
				strQuery.append("   and a.cr_acptno=g.cr_acptno                                      \n");
				strQuery.append("   and g.cr_itemid=g.cr_baseitem                                    \n");
				strQuery.append("   and g.cr_syscd=h.cm_syscd and g.cr_dsncd=h.cm_dsncd              \n");
				strQuery.append("   and d.cm_macode='REQUEST' and d.cm_micode=a.cr_qrycd             \n");
				strQuery.append("   and e.cm_macode='CMR1000' and e.cm_micode=g.cr_status            \n");
				strQuery.append("   and f.cm_macode='REQPASS' and f.cm_micode=a.cr_passok            \n");
				strQuery.append(" order by a.cr_acptdate,g.cr_rsrcname                               \n");
			}
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++,IsrId);
			pstmt.setString(pstmtcount++,UserId);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        rsval.clear();
			while (rs.next()){			
				rst = new HashMap<String, String>();
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("prcdate", rs.getString("prcdate"));
				rst.put("passok", rs.getString("passok"));
				rst.put("qrycd", rs.getString("qrycd"));
				rst.put("status", rs.getString("status"));
				rst.put("qrycd", rs.getString("qrycd"));
				if (!rs.getString("cr_qrycd").equals("03") && !rs.getString("cr_qrycd").equals("04")) {
					rst.put("passok", "일반");
				}
				if (rs.getString("cr_passok").equals("4")) {
					rst.put("prcreq", rs.getString("cr_prcreq").substring(0,4)+"/"+rs.getString("cr_prcreq").substring(4,6)+"/"+
							          rs.getString("cr_prcreq").substring(6,8)+" "+
							          rs.getString("cr_prcreq").substring(8,10)+":"+rs.getString("cr_prcreq").substring(10));
				}
				if (!qryGbn.equals("A")) {
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_itemid", rs.getString("cr_itemid"));
				}
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			rs = null;
			pstmt = null;
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0030.getAcptHist() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0030.getAcptHist() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0030.getAcptHist() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0030.getAcptHist() Exception END ##");				
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
					ecamsLogger.error("## Cmr0030.getAcptHist() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getAcptHist() method statement
    public Object[] getProgHist(String IsrId, String UserId,String qryGbn) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();	
		int				  pstmtcount  = 1;
		Object[] returnObjectArray		 = null;
		
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,d.cm_sysmsg,a.cr_editor,                         \n");
			strQuery.append(" 		 a.cr_syscd,a.cr_dsncd,a.cr_status,a.cr_rsrcname,            \n"); 
			strQuery.append("    	 TO_CHAR(b.cr_acptdate,'yyyy/mm/dd') as lastdate,            \n");	
			strQuery.append(" 		 c.cm_dirpath,f.cr_story,f.cr_itemid,g.cm_codename,          \n");
			strQuery.append(" 		 i.cm_codename qrycd,f.cr_story,j.cm_codename rsrccd         \n");	
			strQuery.append("  from cmm0020 i,cmr1010 a,cmr1000 b,cmm0070 c,cmm0030 d,cmr0020 f, \n");	
			strQuery.append("       cmm0020 g,cmm0020 j,                                         \n");	
			strQuery.append("       (select a.cr_itemid,max(b.cr_acptdate) cr_acptdate           \n"); 
			strQuery.append("          from cmr1010 a,cmr1000 b                                  \n"); 
			strQuery.append("         where b.cr_itsmid=? and b.cr_editor=?                      \n"); 
			strQuery.append("           and b.cr_status <> '3'                                   \n"); 
			strQuery.append("           and b.cr_qrycd<'30'                                      \n"); 
			strQuery.append("           and b.cr_acptno = a.cr_acptno                            \n"); 
			strQuery.append("           and a.cr_status <> '3'                                   \n"); 
			strQuery.append("           and a.cr_itemid = a.cr_baseitem                          \n"); 
			strQuery.append("         group by a.cr_itemid) h                                    \n"); 
			strQuery.append(" where b.cr_itsmid=? and b.cr_acptdate=h.cr_acptdate                \n");
			strQuery.append("   and b.cr_acptno = a.cr_acptno	                                 \n");
			strQuery.append("   and a.cr_itemid=h.cr_itemid                                      \n");
			strQuery.append("   and a.cr_syscd = c. cm_syscd                                     \n");
			strQuery.append("   and a.cr_dsncd = c. cm_dsncd                                     \n");
			strQuery.append("   and a.cr_syscd = d. cm_syscd                                     \n");
			strQuery.append("   and a.cr_itemid = f.cr_itemid                                    \n");
			strQuery.append("   and g.cm_macode = 'CMR0020'                                      \n");
			strQuery.append("   and g.cm_micode = f.cr_status                                    \n");
			strQuery.append("   and i.cm_macode = 'REQUEST'                                      \n");
			strQuery.append("   and i.cm_micode = b.cr_qrycd                                     \n");
			strQuery.append("   and j.cm_macode = 'JAWON'                                        \n");
			strQuery.append("   and j.cm_micode = f.cr_rsrccd                                    \n");
			strQuery.append("union                                                               \n");
			strQuery.append("select '' cr_acptno,d.cm_sysmsg,f.cr_editor,                        \n");
			strQuery.append(" 		 f.cr_syscd,f.cr_dsncd,f.cr_status,f.cr_rsrcname, 	         \n");
			strQuery.append("    	 TO_CHAR(f.cr_lastdate,'yyyy/mm/dd') as lastdate,	         \n");
			strQuery.append(" 		 c.cm_dirpath,f.cr_story,f.cr_itemid,g.cm_codename,          \n");
			strQuery.append(" 		 '신규등록' qrycd,f.cr_story,i.cm_codename rsrccd              \n");
			strQuery.append("  from cmm0020 i,cmm0070 c,cmm0030 d,cmr0020 f,cmm0020 g            \n");
			strQuery.append(" where f.cr_isrid is not null and f.cr_isrid=?                      \n");
			strQuery.append("   and f.cr_editor=?                                                \n");
			strQuery.append("   and f.cr_lstver=0 and f.cr_status='3'                            \n");
			strQuery.append("   and f.cr_syscd=c. cm_syscd and f.cr_dsncd=c.cm_dsncd             \n");
			strQuery.append("   and f.cr_syscd=d.cm_syscd                                        \n");
			strQuery.append("   and f.cr_rsrccd not in (select cm_samersrc from cmm0037          \n");
			strQuery.append("                            where cm_syscd=f.cr_syscd               \n");
			strQuery.append("                              and cm_samersrc is not null)	         \n");
			strQuery.append("   and g.cm_macode='CMR0020'                                        \n");
			strQuery.append("   and g.cm_micode=f.cr_status                                      \n");
			strQuery.append("   and i.cm_macode = 'JAWON'                                        \n");
			strQuery.append("   and i.cm_micode = f.cr_rsrccd                                    \n");
			//pstmt = conn.prepareStatement(strQuery.toString());	
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmtcount = 1;
			pstmt.setString(pstmtcount++,IsrId);
			pstmt.setString(pstmtcount++,UserId);
			pstmt.setString(pstmtcount++,IsrId);
			pstmt.setString(pstmtcount++,IsrId);
			pstmt.setString(pstmtcount++,UserId);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());           
	        rs = pstmt.executeQuery();
	        rsval.clear();
			while (rs.next()){			
				rst = new HashMap<String, String>();
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				if (rs.getString("cr_acptno") != null) {
					rst.put("acptno",rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				}
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("lastdate",rs.getString("lastdate"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_story", rs.getString("cr_story"));
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("status", rs.getString("cm_codename"));
				rst.put("qrycd", rs.getString("qrycd"));
				rst.put("cr_story", rs.getString("cr_story"));
				rst.put("rsrccd", rs.getString("rsrccd"));
				rst.put("selfsw","Y");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			rs = null;
			pstmt = null;
			
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			return returnObjectArray;			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0030.getProgHist() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0030.getProgHist() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0030.getProgHist() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0030.getProgHist() Exception END ##");				
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
					ecamsLogger.error("## Cmr0030.getProgHist() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgHist() method statement
	
    public Object[] getRealTime(String srID) throws SQLException, Exception {
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
			strQuery.append("select c.cc_srid, '개발담당자' gbn, c.cc_userid, b.cm_username, sum(a.cc_worktime) cc_worktime	 			\n");
			strQuery.append("  from cmc0111 a, cmm0040 b, cmc0110 c 													 				\n");
			strQuery.append(" where c.cc_srid = ?															 							\n");
			strQuery.append("  and c.cc_userid = b.cm_userid 																			\n");
			strQuery.append("  and c.cc_srid = a.cc_srid(+) 																			\n");
			strQuery.append("   and c.cc_userid = a.cc_userid(+) 																		\n");
			strQuery.append("  and c.cc_status not in ('3','8') 	 																	\n");
			strQuery.append("group by  c.cc_srid, c.cc_userid, b.cm_username 															\n");
			strQuery.append(" union all 															 									\n");					
			strQuery.append("select a.cc_srid, '검수담당자' gbn, a.cc_testuser cc_userid, b.cm_username, sum(a.cc_worktime) cc_worktime 	\n");
			strQuery.append("  from cmc0211 a, cmm0040 b 																 				\n");
			strQuery.append(" where a.cc_srid = ?																	 					\n");
			strQuery.append("   and a.cc_testuser = b.cm_userid 														 				\n");
			strQuery.append("   and a.cc_status <> '3' 																					\n");
			strQuery.append(" group by a.cc_srid, a.cc_testuser, b.cm_username 															\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt = new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, srID);
	        pstmt.setString(2, srID);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	rst = new HashMap<String, String>();
				rst.put("cc_srid", rs.getString("cc_srid"));
				rst.put("gbn", rs.getString("gbn"));
				rst.put("cc_userid", rs.getString("cc_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				if(rs.getString("cc_worktime")!=null && !"".equals(rs.getString("cc_worktime"))){
					rst.put("cc_worktime", rs.getString("cc_worktime")+"시간");
				}else{
					rst.put("cc_worktime", "");
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
			ecamsLogger.error("## PrjInfo.getRealTime() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## PrjInfo.getRealTime() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getRealTime() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## PrjInfo.getRealTime() Exception END ##");				
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
					ecamsLogger.error("## PrjInfo.getRealTime() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRealTime() method statement
    
    public String base_Confirm(String AcptNo,String IsrId,String UserId,String ReqCd,Connection conn) throws SQLException, Exception {
		
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               pstmtcount = 0;
		try {
    		strQuery.setLength(0);        	
        	strQuery.append("insert into cmc0300 \n");
        	strQuery.append("(CC_ACPTNO,CC_SRID,CC_EDITOR,CC_TEAMCD,CC_QRYCD,CC_ACPTDATE,CC_STATUS) \n");
        	strQuery.append("(select ?, ?, cm_userid,cm_project,?,SYSDATE,'0'   \n");
        	strQuery.append("   from cmm0040 where cm_userid=?)   \n");
        	
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, IsrId);
        	pstmt.setString(pstmtcount++, ReqCd);
        	pstmt.setString(pstmtcount++, UserId);        	
        	pstmt.executeUpdate();
        	pstmt.close();
        	pstmt = null;
        	
        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return "0";
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.base_Confirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## PrjInfo.base_Confirm() SQLException END ##");			
			return "1";
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.base_Confirm() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## PrjInfo.base_Confirm() Exception END ##");				
			return "2";
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}		
	}
    
    public Object[] getChartData1(String date1, String date2) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);        	
        	strQuery.append("select b.cm_codename, count(*) as cnt						    \n");
        	strQuery.append("  from cmr1000 a, cmm0020 b    								\n");
        	strQuery.append(" where b.cm_macode = 'REQUEST' and b.cm_micode = a.cr_qrycd	\n");
        	strQuery.append("   and to_char(a.cr_acptdate,'yyyy/mm/dd') between ? and ?    	\n");
        	strQuery.append(" group by b.cm_codename    									\n");
        	strQuery.append(" order by cnt desc         									\n");
        	
        	pstmt = conn.prepareStatement(strQuery.toString());
        	
        	int pstmtcount = 1;
        	pstmt.setString(pstmtcount++, date1);
        	pstmt.setString(pstmtcount++, date2);        	
        	rs = pstmt.executeQuery();
        	while(rs.next()){
        		rst = new HashMap<String,String>();
    			rst.put("request", rs.getString("cm_codename"));
    			rst.put("cnt",  rs.getString("cnt"));
    			rtList.add(rst);
    			rst = null;
        	}
        	rs.close();
        	pstmt.close();
        	
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getChartData1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getChartData1() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getChartData1() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getChartData1() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getChartData1() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
    public Object[] getChartData2() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);        	
        	strQuery.append("select a.cm_sysmsg, count(*) as cnt		\n");
        	strQuery.append("  from cmm0030 a, cmr0020 b				\n");
        	strQuery.append(" where a.cm_syscd = b.cr_syscd				\n");
        	strQuery.append("   and a.cm_closedt is null				\n");
        	strQuery.append("   and b.cr_status <> '9'					\n");
        	strQuery.append(" group by a.cm_sysmsg						\n");
        	strQuery.append(" order by cnt desc         				\n");
        	
        	pstmt = conn.prepareStatement(strQuery.toString());
        	
        	rs = pstmt.executeQuery();
        	while(rs.next()){
        		rst = new HashMap<String,String>();
    			rst.put("sysmsg", rs.getString("cm_sysmsg"));
    			rst.put("cnt",  rs.getString("cnt"));
    			rtList.add(rst);
    			rst = null;
        	}
        	rs.close();
        	pstmt.close();
        	
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getChartData2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## PrjInfo.getChartData2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## PrjInfo.getChartData2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## PrjInfo.getChartData2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## PrjInfo.getChartData2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
    
}//end of PrjInfo class statement
