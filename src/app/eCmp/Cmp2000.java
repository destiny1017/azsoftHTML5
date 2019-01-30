/*****************************************************************************************
	1. program ID	: Cmp1900.java
	2. create date	: 2012. 01. 10
	3. auth		    : no name
	4. update date	:
	5. auth		    :
	6. description	: 보고서1->개인별처리현황
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
//import app.common.UserInfo;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author UBIQUITOUS
 *
 */
public class Cmp2000 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/** 보고서-> 이관신청현황 조회 쿼리
	 * @param etcInfo : Sdate 시작일자'YYYYMMDD'
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getList(HashMap<String,String> etcInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

//			UserInfo userinfo = new UserInfo();
//			boolean adminSw = userinfo.isAdmin_conn(etcInfo.get("userid"), conn);
//			int SEQ = 0;
			int xgmCnt = 0;
			int mrdCnt = 0;
			int prgCnt = 0;
			int DBIOCnt = 0;
			int totalxgmCnt = 0;
			int totalmrdCnt = 0;
			int totalprgCnt = 0;
			int totalDBIOCnt = 0;

//			String itemid = "";

//			String strName = "";
//			if (adminSw == false) {
//				strName = userinfo.getUserName(conn, etcInfo.get("userid"));
//			}

			strQuery.setLength(0);
	        strQuery.append("select distinct c.cm_jobcd, c.cm_jobname \n");
	        strQuery.append("  from cmm0030 a, cmm0034 b, cmm0102 c \n");
	        strQuery.append(" where a.cm_syscd = b.cm_syscd \n");
	        strQuery.append("   and a.cm_syscd in ('00001', '00002','00201') \n");
	        strQuery.append("   and a.cm_closedt is null \n");
	        strQuery.append("   and b.cm_closedt is null \n");
	        strQuery.append("   and b.cm_jobcd = c.cm_jobcd \n");
	        strQuery.append(" order by c.cm_jobcd \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();

	        while (rs.next()){

				rst = new HashMap<String, String>();
        		rst.put("cm_jobcd", rs.getString("cm_jobcd"));
        		rst.put("cm_jobname","["+rs.getString("cm_jobcd")+"] "+rs.getString("cm_jobname"));

        		strQuery.setLength(0);
    			strQuery.append("select b.cr_itemid,b.cr_rsrcname,b.cr_rsrccd, \n");
    			strQuery.append("       to_char(a.cr_acptdate,'yyyymmdd') as REQ_DT, \n");
    			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate, \n");
    			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate, \n");
    			strQuery.append("       decode(a.cr_syscd,'00001','B','00002','O','X') as BUSI_GB, \n");
    			strQuery.append("       d.cm_username || '(' || b.cr_editor || ')' as username \n");
    			strQuery.append("  from cmr1000 a, cmr1010 b, cmr0020 c,cmm0040 d \n");
    			strQuery.append(" where a.cr_syscd in ('00001', '00002','00201') \n");
    			strQuery.append("   and b.cr_jobcd = ? \n");//cm_jobcd
    			strQuery.append("   and c.cr_rsrccd in ('01','02','03','04','06','07','09','17','38','39','40','83','42','34','48','49','96') \n");
    			strQuery.append("   and b.cr_acptno = a.cr_acptno \n");
    			strQuery.append("   and b.cr_qrycd in ('03','04') \n");
    			strQuery.append("   and a.cr_qrycd ='04' \n");
    			strQuery.append("   and b.cr_status = '9' \n");
    			strQuery.append("   and b.cr_baseitem = b.cr_itemid \n");
    			strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')=? \n");//조회시작날짜(일)
    			strQuery.append("   and b.cr_itemid = c.cr_itemid \n");
    			strQuery.append("   and b.cr_syscd = c.cr_syscd \n");
    			strQuery.append("   and b.cr_editor = d.cm_userid \n");
    			strQuery.append(" order by b.cr_itemid \n");

    	        pstmt2 = conn.prepareStatement(strQuery.toString());
    			//pstmt2 = new LoggableStatement(conn,strQuery.toString());
    	        pstmt2.setString(1, rs.getString("cm_jobcd"));
    	        pstmt2.setString(2, etcInfo.get("Sdate"));
    			//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
    			rs2 = pstmt2.executeQuery();

    			while (rs2.next()){

//    				if (!itemid.equals(rs.getString("cr_itemid"))){
//    					itemid = rs.getString("cr_itemid");
//    					SEQ = 0;
//    				}
//    				rst.put("cr_itemid", itemid);
//    				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
//    				rst.put("PGM_ID", rs.getString("cr_rsrcname").substring(0, rs.getString("cr_rsrcname").indexOf(".")));
//    				rst.put("REQ_DT", rs.getString("REQ_DT"));
//    				rst.put("SEQ", Integer.toString(++SEQ));
//    				rst.put("PGM_FILE_ID", rs.getString("cr_rsrcname"));
////    				rst.put("HEADER_YN", rs.getString("HEADER_YN"));
//    				rst.put("BUSI_GB", rs.getString("BUSI_GB"));
////    				rst.put("TRANS_YN", rs.getString("TRANS_YN"));
////    				rst.put("DEPLOY_YN", rs.getString("DEPLOY_YN"));
//    				rst.put("username", rs.getString("username"));
//    				rst.put("acptdate", rs.getString("acptdate"));
//    				rst.put("prcdate", rs.getString("prcdate"));

    				switch (Integer.parseInt(rs2.getString("cr_rsrccd"))) {
    				//'83','42','34','48','49','96'
    				case 83://HTSonly_xgm
    	                ++xgmCnt;
    	                break;
    				case 42://전체_xgm
    	                ++xgmCnt;
    	                break;
    				case 34://통단only_xgm
    	                ++xgmCnt;
    	                break;
    				case 48://HTSonly_보고서
    	                ++xgmCnt;
    	                break;
    	            case 49://전체_보고서
    	                ++xgmCnt;
    	                break;
    	            case 96://통단only_보고서
    	                ++mrdCnt;
    	                break;
    	            case 38://DAM(MR,MU)
    	            	++DBIOCnt;
    	                break;
    	            case 39://DAM(RS,RM,RU)
    	            	++DBIOCnt;
    	                break;
    	            case 40://DAM(CM)
    	            	++DBIOCnt;
    	                break;
    	            default ://prg
    	                ++prgCnt;
    	                break;
    				}
    			}
    			rs2.close();
    			pstmt2.close();

    			totalxgmCnt = totalxgmCnt + xgmCnt;
    			totalmrdCnt = totalmrdCnt + mrdCnt;
    			totalprgCnt = totalprgCnt + prgCnt;
    			totalDBIOCnt = totalDBIOCnt + DBIOCnt;

    			if(xgmCnt != 0){
    				//System.out.println("xgmCnt : "+xgmCnt);
	    			rst.put("xgmCnt",Integer.toString(xgmCnt));
	    			rst.put("colorsw", "3");
    			}else{
    				rst.put("xgmCnt", " ");
    			}
    			if(mrdCnt != 0){
    				//System.out.println("mrdCnt : "+mrdCnt);
	    			rst.put("mrdCnt",Integer.toString(mrdCnt));
	    			rst.put("colorsw", "3");
    			}else{
    				rst.put("mrdCnt", " ");
    			}
    			if(prgCnt != 0){
    				//System.out.println("prgCnt : "+prgCnt);
	    			rst.put("prgCnt",Integer.toString(prgCnt));
	    			rst.put("colorsw", "3");
    			}else{
            		rst.put("prgCnt", " ");
    			}
    			if(DBIOCnt != 0){
    				//System.out.println("DBIOCnt : "+DBIOCnt);
	    			rst.put("DBIOCnt",Integer.toString(DBIOCnt));
	    			rst.put("colorsw", "3");
    			}else{
    				rst.put("DBIOCnt", " ");
    			}
    			xgmCnt = 0;
    			mrdCnt = 0;
    			prgCnt = 0;
    			DBIOCnt = 0;
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			//합계 라인 작성
			rst = new HashMap<String, String>();
    		rst.put("cm_jobcd", "TOTAL");
    		rst.put("cm_jobname","합계");
    		if(totalxgmCnt != 0){
				rst.put("xgmCnt",Integer.toString(totalxgmCnt));
				rst.put("colorsw", "3");
    		}else{
    			rst.put("xgmCnt", " ");
    		}
    		if(totalmrdCnt != 0){
				rst.put("mrdCnt",Integer.toString(totalmrdCnt));
				rst.put("colorsw", "3");
    		}else{
    			rst.put("mrdCnt", " ");
    		}
    		if(totalprgCnt != 0){
				rst.put("prgCnt",Integer.toString(totalprgCnt));
				rst.put("colorsw", "3");
    		}else{
    			rst.put("prgCnt", " ");
    		}
    		if(totalDBIOCnt != 0){
				rst.put("DBIOCnt",Integer.toString(totalDBIOCnt));
				rst.put("colorsw", "3");
    		}else{
    			rst.put("DBIOCnt", " ");
    		}
			rsval.add(rst);
			rst = null;
			//합계 라인 작성

			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2000.getList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp2000.getList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2000.getList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp2000.getList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2000.getList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getList() method statement

	public Object[] getList1(HashMap<String,String> etcInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			int SEQ = 0;
			String itemid = "";

    		strQuery.setLength(0);
			strQuery.append("select b.cr_itemid,b.cr_rsrcname,b.cr_rsrccd,                                \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyymmdd') as REQ_DT,                          \n");
			strQuery.append("       c.cr_story as PGM_NM, a.cr_acptno,                                    \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') as acptdate,              \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') as prcdate,                \n");
			strQuery.append("       decode(a.cr_syscd, '00001', 'B', '00002' , 'O' , 'X') as BUSI_GB,     \n");
			strQuery.append("       d.cm_username || '(' || b.cr_editor || ')' as username                \n");
			strQuery.append("  from cmr1000 a, cmr1010 b, cmr0020 c,cmm0040 d                             \n");
			strQuery.append(" where b.cr_jobcd = ?                                                        \n");//cm_jobcd
			if (etcInfo.get("rsrccd").equals("X")){//화면파일
				strQuery.append("   and a.cr_syscd = '00201'                                              \n");
				strQuery.append("   and c.cr_rsrccd in ('83','42','34')                                   \n");
			} else if (etcInfo.get("rsrccd").equals("M")){//보고서
				strQuery.append("   and a.cr_syscd = '00201'                                              \n");
				strQuery.append("   and c.cr_rsrccd in ('48','49','96')                                   \n");
			} else if (etcInfo.get("rsrccd").equals("D")){//DBIO
				strQuery.append("   and a.cr_syscd in ('00001', '00002')                                  \n");
				strQuery.append("   and c.cr_rsrccd in ('38','39','40')                                   \n");
			} else {
				strQuery.append("   and a.cr_syscd in ('00001', '00002')                                  \n");
				strQuery.append("   and c.cr_rsrccd in ('01','02','03','04','05','06','07','10','17','30')\n");
			}
			strQuery.append("   and b.cr_acptno = a.cr_acptno                                             \n");
			strQuery.append("   and a.cr_qrycd ='04'                                                      \n");
			strQuery.append("   and b.cr_qrycd in ('03','04')                                             \n");
			strQuery.append("   and b.cr_status = '9'                                                     \n");
			strQuery.append("   and b.cr_baseitem = b.cr_itemid                                           \n");
			strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')= ?                                  \n");//조회시작날짜(일)
			strQuery.append("   and b.cr_itemid = c.cr_itemid                                             \n");
			strQuery.append("   and b.cr_syscd = c.cr_syscd                                               \n");
			strQuery.append("   and b.cr_editor = d.cm_userid                                             \n");
			strQuery.append(" order by b.cr_itemid                                                        \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, etcInfo.get("job_cd"));
	        pstmt.setString(2, etcInfo.get("Sdate"));
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			while (rs.next()){
    			rst = new HashMap<String, String>();
				if (!itemid.equals(rs.getString("cr_itemid"))){
					itemid = rs.getString("cr_itemid");
					SEQ = 0;
				}
				rst.put("PGM_ID", rs.getString("cr_rsrcname").substring(0, rs.getString("cr_rsrcname").indexOf(".")));
				rst.put("REQ_DT", rs.getString("REQ_DT"));
				rst.put("SEQ", Integer.toString(++SEQ));
				rst.put("PGM_NM", rs.getString("PGM_NM"));
				rst.put("PGM_FILE_ID", rs.getString("cr_rsrcname"));
				rst.put("BUSI_GB", rs.getString("BUSI_GB"));
				rst.put("username", rs.getString("username"));
				rst.put("acptdate", rs.getString("acptdate"));
				rst.put("prcdate", rs.getString("prcdate"));

				rst.put("HEADER_YN", "N");

				strQuery.setLength(0);
				strQuery.append("select count(*) cnt       \n");
				strQuery.append("  from cmr1010         \n");
				strQuery.append(" where cr_baseitem = ? \n");
				strQuery.append("   and cr_acptno = ?   \n");
				strQuery.append("   and cr_rsrccd = '21'\n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
		        pstmt2.setString(1, itemid);
		        pstmt2.setString(2, etcInfo.get("cr_acptno"));
				//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				rs2 = pstmt2.executeQuery();

				if (rs2.next() && rs2.getInt("cnt")>0){
					rst.put("HEADER_YN", "Y");
				}
				rs2 = null;
				pstmt2 = null;

				rst.put("TRANS_YN", ""); //컴파일여부??
				rst.put("DEPLOY_YN", ""); //배포여부??

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
			ecamsLogger.error("## Cmp2100.getList1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp2100.getList1() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2100.getList1() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp2100.getList1() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2100.getList1() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getList1() method statement

	public String updtDoc(HashMap<String,String> etcInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
	        strQuery.append("update  KPCOMPRCREQDOC    \n");
	        strQuery.append("   set  ImpvDt=?,         \n");
	        strQuery.append("        ImpvDocNo=?       \n");
	        strQuery.append(" WHERE  DOCNO=?           \n");
	        strQuery.append("   AND  REQDT=?           \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, etcInfo.get("impvdt").replaceAll("-", ""));
	        pstmt.setString(2, etcInfo.get("impvdocno"));
	        pstmt.setString(3, etcInfo.get("docno"));
	        pstmt.setString(4, etcInfo.get("reqdt").replaceAll("-", ""));
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			pstmt = null;
			conn = null;

			return "Y";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp2000.updtDoc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp2000.updtDoc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp2000.updtDoc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp2000.updtDoc() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp2000.updtDoc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtDoc() method statement
}//end of Cmp1900 class statement