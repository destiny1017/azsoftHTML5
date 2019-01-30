/*****************************************************************************************
	1. program ID	: Cmd010.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import java.util.ArrayList;
import java.util.HashMap;
//import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmd0101{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


//	public Object[] getGbnCd(String UserID) throws SQLException, Exception {
//		Connection        conn        = null;
//		PreparedStatement pstmt       = null;
//		ResultSet         rs          = null;
//		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
//		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String>			  rst		  = null;
//		int     parmCnt = 0;
//		ConnectionContext connectionContext = new ConnectionResource();
//
//		try {
//			conn = connectionContext.getConnection();
//
//			UserInfo     userinfo = new UserInfo();
//			boolean adminYn = userinfo.isAdmin_conn(UserID, conn);
//
//			strQuery.setLength(0);
//		    strQuery.append("select gbncd,decode(gbncd,'1','기간계[C FrameWork]','기간계[Java FrameWork]') gbnname \n");
//		    strQuery.append("  from ( \n");
//		    strQuery.append("select decode(a.cm_syscd,'00089','1','88888','1','9') gbncd \n");
//		    strQuery.append("  from cmm0030 a,cmm0036 b                \n");
//		    strQuery.append(" where a.cm_closedt is null              \n");
//		    strQuery.append("   and a.cm_syscd=b.cm_syscd             \n");
//		    if (adminYn == false){
//		       strQuery.append("and a.cm_syscd in (select distinct cm_syscd \n");
//		       strQuery.append("                     from cmm0044           \n");
//		       strQuery.append("                    where cm_userid=?       \n");
//		       strQuery.append("                      and cm_closedt is null)\n");
//		    }
//		    strQuery.append("  and b.cm_closedt is null              \n");
//		    strQuery.append("  and substr(b.cm_info,27,1)='1'        \n");
//		    strQuery.append("group by a.cm_syscd)                    \n");
//			strQuery.append("group by gbncd                          \n");
//			strQuery.append("order by gbncd                          \n");
//			//pstmt = new LoggableStatement(conn,strQuery.toString());
//            pstmt = conn.prepareStatement(strQuery.toString());
//		    if (adminYn == false){
//		    	pstmt.setString(++parmCnt, UserID);
//			}
//            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//            rs = pstmt.executeQuery();
//
//            rtList.clear();
//
//			while (rs.next()){
//				rst = new HashMap<String,String>();
//				rst.put("cm_micode", rs.getString("gbncd"));
//				rst.put("cm_codename", rs.getString("gbnname"));
//				rtList.add(rst);
//				rst = null;
//			}
//			rs.close();
//			pstmt.close();
//			conn.close();
//
//			rs = null;
//			pstmt = null;
//			conn = null;
//
//			rtObj =  rtList.toArray();
//			rtList = null;
//
//			return rtObj;
//
//		} catch (SQLException sqlexception) {
//			sqlexception.printStackTrace();
//			ecamsLogger.error("## Cmd0101.getGbnCd() SQLException START ##");
//			ecamsLogger.error("## Error DESC : ", sqlexception);
//			ecamsLogger.error("## Cmd0101.getGbnCd() SQLException END ##");
//			throw sqlexception;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## Cmd0101.getGbnCd Exception START ##");
//			ecamsLogger.error("## Error DESC : ", exception);
//			ecamsLogger.error("## Cmd0101.getGbnCd Exception END ##");
//			throw exception;
//		}finally{
//			if (strQuery != null) 	strQuery = null;
//			if (rtObj != null)  rtObj = null;
//			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (conn != null){
//				try{
//					ConnectionResource.release(conn);
//				}catch(Exception ex3){
//					ecamsLogger.error("## Cmd0101.getGbnCd() connection release exception ##");
//					ex3.printStackTrace();
//				}
//			}
//		}
//	}

	public Object[] getSysCd(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int     parmCnt = 0;
		boolean adminYn = false;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			UserInfo     userinfo = new UserInfo();
			adminYn = userinfo.isAdmin_conn(UserID,conn);

			strQuery.setLength(0);
		    strQuery.append("select a.cm_syscd,a.cm_sysmsg,'1' gbncd  \n");
		    strQuery.append("  from cmm0030 a,cmm0036 b               \n");
		    strQuery.append(" where a.cm_closedt is null              \n");
		    strQuery.append("   and a.cm_syscd=b.cm_syscd             \n");
		    if (adminYn == false){
		       strQuery.append("and a.cm_syscd in (select distinct cm_syscd \n");
		       strQuery.append("                     from cmm0044           \n");
		       strQuery.append("                    where cm_userid=?       \n");
		       strQuery.append("                      and cm_closedt is null)\n");
		    }
		    strQuery.append("  and b.cm_closedt is null              \n");
			strQuery.append("  and (substr(b.cm_info,27,1)='1'    	 \n");
			strQuery.append("   or b.cm_rsrccd in ('09','18', '22')) \n");
		    strQuery.append(" group by a.cm_syscd,a.cm_sysmsg        \n");
			strQuery.append(" order by a.cm_syscd                    \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
		    if (adminYn == false){
		    	pstmt.setString(++parmCnt, UserID);
			}
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", "선택하세요.");
					rst.put("labelField", "선택하세요.");
					rtList.add(rst);
					rst = null;
				}
				rst = new HashMap<String,String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("gbncd", rs.getString("gbncd"));
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
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getSysCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0101.getSysCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getSysCd Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0101.getSysCd Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0101.getSysCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getJobCd(String UserID, String Syscd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();


			UserInfo     userinfo = new UserInfo();
			boolean adminYn = userinfo.isAdmin_conn(UserID,conn);


			strQuery.setLength(0);
		    strQuery.append("select c.cm_jobcd,c.cm_jobname,a.cm_syscd \n");
		    strQuery.append("  from cmm0030 a,cmm0036 b,cmm0102 c,     \n");
		    if (adminYn == false) {
		    	strQuery.append("(select cm_syscd,cm_jobcd,cm_closedt  \n");
		    	strQuery.append("   from cmm0044                       \n");
		    	strQuery.append("  where cm_userid=?) d                \n");
		    } else {
		    	strQuery.append("cmm0034 d                             \n");
		    }
		    strQuery.append(" where a.cm_closedt is null              \n");
		    strQuery.append("   and a.cm_syscd=b.cm_syscd             \n");
		    strQuery.append("   and a.cm_syscd=?             		  \n");
		    strQuery.append("   and b.cm_closedt is null              \n");
			strQuery.append("   and (substr(b.cm_info,27,1)='1'    	  \n");
			strQuery.append("   or b.cm_rsrccd in ('09','18', '22'))  \n");
		    strQuery.append("   and a.cm_syscd=d.cm_syscd             \n");
		    strQuery.append("   and d.cm_closedt is null              \n");
		    strQuery.append("   and d.cm_jobcd=c.cm_jobcd             \n");
		    strQuery.append(" group by a.cm_syscd,c.cm_jobcd,c.cm_jobname \n");
			strQuery.append(" order by a.cm_syscd,c.cm_jobname,c.cm_jobcd \n");

			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            if (adminYn == false){
            	pstmt.setString(1, UserID);
            	pstmt.setString(2, Syscd);
            }else{
            	pstmt.setString(1, Syscd);
            }

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("cm_jobcd", "00000");
					rst.put("cm_syscd", "00000");
					rst.put("cm_jobname", "선택하세요.");
					rtList.add(rst);
					rst = null;
				}
				rst = new HashMap<String,String>();
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
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
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getJobCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0101.getJobCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getJobCd Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0101.getJobCd Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getJobCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getPfmList(String UserId,String fileName,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        connPfm        = null;
		PreparedStatement pstmtPfm       = null;
		ResultSet         rsPfm          = null;
		Connection        conn           = null;
		PreparedStatement pstmt          = null;
		ResultSet         rs             = null;


		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>  rtRsrcCd = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int               i = 0;
		boolean 		  findSw     = false;
		int               parmCnt = 0;

		ConnectionContext connectionContextPfm;
		ConnectionContext connectionContext;

		try {
			String pfmstring = "";
			String exeRsrcCd = "";
			String strRsrcName = "";
			String tmpWork1 = "";
			pfmstring = "PFM"+SysCd.substring(2,5);
			//ecamsLogger.debug("pfmstring "+pfmstring);
			connectionContextPfm = new ConnectionResource(false, pfmstring);
			connPfm = connectionContextPfm.getConnection();

			connectionContext = new ConnectionResource();
			conn = connectionContext.getConnection();

			rtRsrcCd.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cm_rsrccd,a.cm_volpath,b.cm_info,b.cm_exename	\n");
			strQuery.append("  from cmm0038 a,cmm0036 b,cmm0031 c 		\n");
			strQuery.append(" where c.cm_syscd=?                  		\n");
			strQuery.append("   and c.cm_svrcd='01'               		\n");
			strQuery.append("   and c.cm_closedt is null          		\n");
			strQuery.append("   and c.cm_syscd=a.cm_syscd         		\n");
			strQuery.append("   and c.cm_svrcd=a.cm_svrcd         		\n");
			strQuery.append("   and c.cm_seqno=a.cm_seqno         		\n");
			strQuery.append("   and c.cm_syscd=b.cm_syscd         		\n");
			strQuery.append("   and b.cm_closedt is null          		\n");
			strQuery.append("   and (substr(b.cm_info,27,1)='1'    		\n");
			strQuery.append("   or b.cm_rsrccd in ('09','18', '22','48'))    \n");
			strQuery.append("   and a.cm_rsrccd=b.cm_rsrccd       		\n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, SysCd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("cm_exename", rs.getString("cm_exename"));
				if (rs.getString("cm_info").substring(43,44).equals("1")) exeRsrcCd = exeRsrcCd + "," + rs.getString("cm_rsrccd") + rs.getString("cm_exename");
				rtRsrcCd.add(rst);
			}
			rs.close();
			pstmt.close();

			rtList.clear();
			/*
			-----------------------------------------------------------------------
			-- 사용자 CHECK OUT RESOURCE 정보
			-----------------------------------------------------------------------
			-- 항목 설명 (사용자 ID 를 변수로 함
			-----------------------------------------------------------------------
			-- 1. CHECK_USER               : 체크아웃 사용자 ID (D~, S~)
			-- 2. RESOURCE_TYPE            : 리소스 종류
			-- 3. RESOURCE_GROUP           : 리소스 그룹
			-- 4. PHYSICAL_NAME            : 물리명
			-- 5. LOGICAL_NAME             : 논리명
			-- 6. OWNER                    : 소유자
			-- 7. CREATE_TIME              : 최초 생성 시간
			-- 8. UPDATE_TIME              : 최종 수정 시간
			-- 9. PLATFORM                 : 개발 환경 (소문자 c - C FRAMEWORK, 소문자 java - JAVA FRAMEWORK)
			-- 10. DEPLOY_REQUEST_YN       : 일괄 요청 여부
			-- 11. REQUEST_DTM             : 일괄 요청 시간
			-- 12. C_PATH                  : '.c' 위치 (상대 위치를 기준으로 FULL)
			-- 13. H_PATH                  : '.h' 위치 (상대 위치를 기준으로 FULL, MAP 의 경우 공백)
			-- 14. XML_PATH                : '.xml' 위치 (상대 위치를 기준으로 FULL)
			-- 15. PDW_C_PATH              : pdw source 위치 (C 에 한정됨, 공백일 수 있음)
			-- 16. PDW_H_PATH              : pdw header 위치 (C 에 한정됨, 공백일 수 있음)
			-----------------------------------------------------------------------
			*/
			strQuery.setLength(0);
			strQuery.append("SELECT   RESOURCE_TYPE,        	                        										\n");
			strQuery.append("         RESOURCE_GROUP,                               				                   		    \n");
			strQuery.append("		  PHYSICAL_NAME,                                			 		                  		\n");
			strQuery.append("         LOGICAL_NAME,                                 			 		                  		\n");
			//strQuery.append("       OWNER,                                        					                  		\n");
			//strQuery.append("       MODIFIER,                                      			 	                      		\n");
			//strQuery.append("       TO_CHAR(CREATE_TIME,'YY/MM/DD') CREATE_TIME,  					                  		\n");
			//strQuery.append("       TO_CHAR(UPDATE_TIME,'YY/MM/DD') UPDATE_TIME,  					                  		\n");
			strQuery.append("       CASE WHEN RESOURCE_TYPE = 'SERVICE_MODULE' THEN '01'                              			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'BATCH_MODULE' THEN '02'                                			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'BIZ_MODULE' THEN '03'                                  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'PERSIST' THEN '10' 									  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'VIEW' THEN '11' 										  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'EXECSQL' THEN '12' 									  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'DYNAMICSQL' THEN '16' 								  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'STRUCTURE' THEN '13'  								  			\n");
			strQuery.append("            WHEN RESOURCE_TYPE = 'HEADER' THEN '09'                                	  			\n");
			strQuery.append("       END RSRCCD,                                      				                 			\n");
			strQuery.append("		      DECODE( RESOURCE_TYPE, 'SERVICE_MODULE', '/appl/cfw/app/compile/'||RESOURCE_GROUP||'/src/serviceModule',  \n");
			strQuery.append("                      'BATCH_MODULE'  , '/appl/cfw/app/compile/'||RESOURCE_GROUP||'/src/batch' ,     \n");
			strQuery.append("                      'BIZ_MODULE'    , '/appl/cfw/app/compile/'||RESOURCE_GROUP||'/src/module',     \n");
			strQuery.append("                      'PERSIST'       , '/appl/cfw/app/release/dbio/src',                            \n");
		    strQuery.append("                      'VIEW'          , '/appl/cfw/app/release/dbio/src',                                 \n");
			strQuery.append("                      'EXECSQL'       , '/appl/cfw/app/release/dbio/src',                                \n");
		    strQuery.append("                      'DYNAMICSQL'    , '/appl/cfw/app/release/dbio/src',                                \n");
			strQuery.append("                      'STRUCTURE'     , '/appl/cfw/app/release/pmap/src',                                 \n");
		    strQuery.append("                      'HEADER'        , '/appl/cfw/app/release/inc')  AS DIR			                                                           \n");
			strQuery.append("  FROM   DEV_RESOURCE		                               				                  			\n");
			strQuery.append(" WHERE	RESOURCE_TYPE not in ('MAP','MESSAGE','PROPERTY','MODULE','FILEIO','BATCH_SHELL')  \n");
			strQuery.append("   AND  (RESOURCE_DIV is null or RESOURCE_DIV = 'FILEIO')		                          			\n");
			if (fileName != null && fileName != ""){
				strQuery.append("   AND PHYSICAL_NAME like ?                                                    	  			\n");
			}
			if (JobCd != null && JobCd != "") {
				strQuery.append("  AND RESOURCE_GROUP=?                                                                       \n");
			}
			strQuery.append("union																								\n");
			strQuery.append("SELECT '거래파라미터' RESOURCE_TYPE, B.RESOURCE_GROUP,A.TX_CODE PHYSICAL_NAME,   	 			    \n");
			strQuery.append("	     A.TX_NAME LOGICAL_NAME, '18' RSRCCD, '/appl/cfw/publish' AS DIR    														\n");
			strQuery.append("  FROM LCF_SVC A,DEV_RESOURCE B       																\n");
			strQuery.append(" WHERE A.INST_NO = '261'                       				  								    \n");
			strQuery.append("   AND A.CALLEE_NAME=B.PHYSICAL_NAME              				  								    \n");
			if (fileName != null && fileName != ""){
				strQuery.append("   AND TX_CODE LIKE ?                       													\n");
			}
			if (JobCd != null && JobCd != "") {
				strQuery.append("  AND B.RESOURCE_GROUP=?                                                                       \n");
			}
			strQuery.append("union																								\n");
			strQuery.append("SELECT '배치파라미터' RESOURCE_TYPE,B.RESOURCE_GROUP, BAT_CODE PHYSICAL_NAME,		   			    \n");
			strQuery.append("		BAT_LOGICAL_NAME LOGICAL_NAME, '22' RSRCCD, '/appl/cfw/publish' AS DIR  												\n");
			strQuery.append("  FROM LCF_BATCH A,DEV_RESOURCE B WHERE                      			  						    \n");
			if (JobCd != null && JobCd != "") {
				strQuery.append("  B.RESOURCE_GROUP=?                                                                       \n");
			}
			if (fileName != null && fileName != ""){
				strQuery.append("  AND A.BAT_CODE LIKE ?                     													\n");
			}
			strQuery.append("   AND A.CALLEE_NAME=B.PHYSICAL_NAME              				  								    \n");

			//업무계 AB업무 예외 처리
			if(SysCd.equals("00001") && JobCd.equals("AB")){
				strQuery.append("union																								\n");
				strQuery.append("  SELECT  'KDPMAP' RESOURCE_TYPE,                                            \n");
				strQuery.append("      'AB'             AS RESOURCE_GROUP,                                    \n");
				strQuery.append("      'kdpMap'||B.TLMS_CNVR_NAME                  AS PHYSICAL_NAME,          \n");
				strQuery.append("      RTRIM(C.CMN_CODE_NAME)||'_' || A.TLMS_NAME  AS LOGICAL_NAME,           \n");
				strQuery.append("      '48' AS RSRCCD, '/appl/cfw/frm_dev/src/ExtBinary/kdpmap' DIR           \n");
				strQuery.append("  FROM AB6190MS A                                                            \n");
				strQuery.append("  , (SELECT TRNF_SYST_DTCD, SDRC_DTCD, STND_TLMS_CODE, TLMS_CNVR_NAME        \n");
				strQuery.append("  FROM AB6720ST) B                                                           \n");
				strQuery.append("  , ZZ1101CD C                                                               \n");
				strQuery.append("  WHERE A.TRNF_SYST_DTCD = B.TRNF_SYST_DTCD                                  \n");
				strQuery.append("  AND A.SDRC_DTCD = B.SDRC_DTCD                                              \n");
				strQuery.append("  AND A.STND_TLMS_CODE = B.STND_TLMS_CODE                                    \n");
				strQuery.append("  AND C.CMN_CODE_GRP_ID = 'TRNF_SYST_DTCD'                                   \n");
				strQuery.append("  AND C.CMN_CODE = A.TRNF_SYST_DTCD                                          \n");
				strQuery.append("  GROUP BY B.TLMS_CNVR_NAME, RTRIM(C.CMN_CODE_NAME)||'_' || A.TLMS_NAME      \n");
			}
			pstmtPfm = connPfm.prepareStatement(strQuery.toString());
            //pstmtPfm = new LoggableStatement(connPfm,strQuery.toString());
			if (fileName != null && fileName != "") pstmtPfm.setString(++parmCnt, "%"+fileName+"%");
			if (JobCd != null && JobCd != "") pstmtPfm.setString(++parmCnt, JobCd);
			if (fileName != null && fileName != "") pstmtPfm.setString(++parmCnt, "%"+fileName+"%");
			if (JobCd != null && JobCd != "") pstmtPfm.setString(++parmCnt, JobCd);
			if (JobCd != null && JobCd != "") pstmtPfm.setString(++parmCnt, JobCd);
			if (fileName != null && fileName != "") pstmtPfm.setString(++parmCnt, "%"+fileName+"%");
			//ecamsLogger.error(((LoggableStatement)pstmtPfm).getQueryString());
			rsPfm = pstmtPfm.executeQuery();
			//ecamsLogger.error("============query start : ");
			while (rsPfm.next()){
				findSw = false;
				if(rsPfm.getString("RSRCCD") == null){
					findSw = false;
				}else{
					strRsrcName = rsPfm.getString("PHYSICAL_NAME");
					if (exeRsrcCd.indexOf(rsPfm.getString("RSRCCD"))>=0) {
						tmpWork1 = exeRsrcCd.substring(exeRsrcCd.indexOf(rsPfm.getString("RSRCCD"))+2);
						tmpWork1 = tmpWork1.substring(0,tmpWork1.indexOf(","));
						strRsrcName = strRsrcName + tmpWork1;
					}
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmr0020            \n");
					strQuery.append(" where cr_syscd=? and cr_rsrccd=?           \n");
					strQuery.append("   and cr_rsrcname=?                        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, SysCd);
					pstmt.setString(2, rsPfm.getString("RSRCCD"));
					pstmt.setString(3, strRsrcName);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getInt("cnt")==0) {
							findSw = true;
						}
					}
					rs.close();
					pstmt.close();
				}
				//ecamsLogger.error("============PHYSICAL_NAME : "+rsPfm.getString("PHYSICAL_NAME"));
				if (findSw == true) {
					rst = new HashMap<String, String>();
					rst.put("cr_rsrccd",rsPfm.getString("RSRCCD"));
					//ecamsLogger.error("============PHYSICAL_NAME : "+rsPfm.getString("PHYSICAL_NAME"));
					//ecamsLogger.error("============RSRCCD : "+rsPfm.getString("RSRCCD"));
					rst.put("cr_rsrcname", rsPfm.getString("PHYSICAL_NAME"));
					rst.put("cr_story", rsPfm.getString("LOGICAL_NAME"));
					rst.put("rsrctype", rsPfm.getString("RESOURCE_TYPE"));
					rst.put("cr_jobcd", rsPfm.getString("RESOURCE_GROUP"));
					rst.put("cr_status", "Z");
					rst.put("cr_syscd", SysCd);
					rst.put("visible", "1");
					rst.put("checked", "0");
					for (i=0;rtRsrcCd.size()>i;i++) {
						if (rtRsrcCd.get(i).get("cm_rsrccd").equals(rsPfm.getString("RSRCCD"))) {
							rst.put("cm_dirpath",rtRsrcCd.get(i).get("cm_volpath"));
							rst.put("cm_dirpath",rsPfm.getString("DIR"));
							rst.put("cm_info", rtRsrcCd.get(i).get("cm_info"));
							rst.put("cm_exename", rtRsrcCd.get(i).get("cm_exename"));
							break;
						}
					}
					rtList.add(rst);
					rst = null;
				}
			}

			rsPfm.close();
			pstmtPfm.close();
			connPfm.close();
			conn.close();

			rs = null;
			pstmt = null;
			rsPfm = null;
			pstmtPfm = null;
			connPfm = null;
			conn = null;


			rtObj = rtList.toArray();
			//ecamsLogger.debug("++++++pfmList+++++++"+rtList.toString());
			//return rtList;
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getPfmList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0101.getPfmList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0101.getPfmList Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0101.getPfmList Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rsPfm != null)     try{rsPfm.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmtPfm != null)  try{pstmtPfm.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (connPfm != null){
				try{
					ConnectionResource.release(connPfm);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0101.getPfmList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0101.getPfmList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String insCmr0020(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		ArrayList<HashMap<String, String>>  rtList = new ArrayList<HashMap<String, String>>();
		ConnectionContext connectionContext = new ConnectionResource();
		Cmd0100 cmd0100 = new Cmd0100();
		svrOpen svropen = new svrOpen();
		HashMap<String, String> rst = null;
		String          strMsg = "OK";

		try {
			conn = connectionContext.getConnection();
	        String strDsnCd = "";
	        int j = 0;
	        rtList.clear();
			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String, String>();
				rst = fileList.get(i);
				if (fileList.get(i).get("cm_dsncd") == null || fileList.get(i).get("cm_dsncd") == "") {
					strDsnCd = cmd0100.cmm0070_Insert(etcData.get("userid"),etcData.get("cr_syscd"),fileList.get(i).get("cr_rsrcname"),fileList.get(i).get("cr_rsrccd"),etcData.get("cr_jobcd"),fileList.get(i).get("cm_dirpath"),false,conn);
					rst.put("cm_dsncd", strDsnCd);
				}
				rtList.add(rst);
				rst = null;
				if ((i + 2) < fileList.size()) {
					for (j=i+1;fileList.size()>j;j++) {
						if (fileList.get(j).get("cm_dsncd") == null || fileList.get(j).get("cm_dsncd") == "") {
							if (fileList.get(i).get("cm_dirpath").equals(fileList.get(j).get("cm_dirpath"))) {
								rst = new HashMap<String, String>();
								rst = fileList.get(j);
								rst.put("cm_dsncd", strDsnCd);
								fileList.set(j, rst);
								rst = null;
							}
						}
					}
				}
			}//end of while-loop statement

			//ecamsLogger.error("+++++++++fileList+++"+fileList.toString());
			String retMsg = "";
			for (int i=0;i<rtList.size();i++){
				retMsg = svropen.statusCheck(etcData.get("cr_syscd"),rtList.get(i).get("cm_dsncd"),rtList.get(i).get("cr_rsrcname"),etcData.get("userid"),conn);
	        	if (retMsg.equals("0")) {
	        		//cmr0020_Insert(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,LangCd,ProgTit,Service,"",conn);
	        		//retMsg = cmd0100.cmr0020_Insert(etcData.get("userid"),etcData.get("cr_syscd"),rtList.get(i).get("cm_dsncd"),rtList.get(i).get("cr_rsrcname"),fileList.get(i).get("cr_rsrccd"),etcData.get("cr_jobcd"),rtList.get(i).get("cr_story"),"","",conn);
	        		//retMsg = cmd0100.cmr0020_Insert(etcData.get("userid"),etcData.get("cr_syscd"),rtList.get(i).get("cm_dsncd"),rtList.get(i).get("cr_rsrcname"),rtList.get(i).get("cr_rsrccd"),etcData.get("cr_jobcd"),rtList.get(i).get("cr_story"),"",rtList.get(i).get("cm_info"),conn);
	        		//ecamsLogger.debug("++++filename,result++"+file.get("filename")+","+retMsg);
	        		rst = new HashMap<String, String>();
		        	rst.put("userid",etcData.get("userid"));
		        	rst.put("syscd",etcData.get("cr_syscd"));
		        	rst.put("dsncd",rtList.get(i).get("cm_dsncd"));
		        	rst.put("rsrcname",rtList.get(i).get("cr_rsrcname"));
		        	rst.put("rsrccd",rtList.get(i).get("cr_rsrccd"));
		        	rst.put("jobcd",etcData.get("cr_jobcd"));
		        	rst.put("story",rtList.get(i).get("cr_story"));
		        	rst.put("grpcd",etcData.get("cr_jobcd"));
		        	rst.put("baseitem","");
		        	rst.put("info",rtList.get(i).get("cm_info"));
		        	retMsg = cmd0100.cmr0020_Insert(rst,conn);
		        	rst = null;
	        		if (!retMsg.substring(0,1).equals("0")) {
		        		strMsg = "ER"+retMsg.substring(1);
		        		break;
		        	}
	        	} else {
	        		strMsg = "ER"+retMsg.substring(1);
	        		break;
	        	}
			}//end of while-loop statement

			conn.close();
			conn = null;

			return strMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0101.insCmr0020() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0101.insCmr0020() Exception END ##");
			throw exception;
		}finally{
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0101.insCmr0020() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of insCmr0020() method statement
}//end of Cmd0101 class statement
