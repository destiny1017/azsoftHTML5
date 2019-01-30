/*****************************************************************************************
	1. program ID	: Cmp4100.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. User Deploy
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.io.BufferedReader;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;
import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp4100{


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

	public Object[] getUser(String clsCd, String userN) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_userid,cm_username          \n");
			strQuery.append("  from cmm0040                        \n");
			if((!userN.equals("") && userN != "" && userN != null) || !clsCd.equals("Y")){
				strQuery.append(" 	where              \n");
				if(!userN.equals("") && userN != "" && userN != null){
					strQuery.append(" 	 cm_username=? and             \n");
				}
				if (!clsCd.equals("Y")) {
					strQuery.append(" 	 cm_active='1' and             \n");
					strQuery.append("    cm_clsdate is null         \n");
				}
			}

			strQuery.append(" order by cm_username                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			if((!userN.equals("") && userN != "" && userN != null) || !clsCd.equals("Y")){
				if(!userN.equals("") && userN != "" && userN != null){
					pstmt.setString(++parmCnt, userN);
				}
			}
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String,String>();
					rst.put("cm_userid", "000000");
					rst.put("cm_username", "전체");
					rst.put("username", "전체");
					rsval.add(rst);
					rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cm_userid",rs.getString("cm_userid"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("username",rs.getString("cm_username")+"("+ rs.getString("cm_userid"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getUser() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp4100.getUser() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getUser() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp4100.getUser() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp4100.getUser() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getUser() method statement

	public Object[] getTitle_sub(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		int reqCd = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			boolean errSw = false;

			rsval.clear();
			strQuery.setLength(0);
			if(etcData.get("qrygbn").equals("0")){
				if (etcData.get("term").equals("D")) {
					strQuery.append("select TO_CHAR(ADD_MONTHs(TO_DATE(?,'YYYYMMDD'),1),'YYYYMMDD') aftday from dual \n");
				} else if (etcData.get("term").equals("W")) {
					strQuery.append("select TO_CHAR(TO_DATE(?,'YYYYMMDD')+56,'YYYYMMDD') aftday from dual            \n");
				} else {
					strQuery.append("select TO_CHAR(ADD_MONTHs(TO_DATE(?,'YYYYMMDD'),12),'YYYYMMDD') aftday from dual \n");
				}
			}else{
				if (etcData.get("term").equals("D")) {
					strQuery.append("select TO_CHAR(TO_DATE(?,'YYYYMMDD')+7,'YYYYMMDD') aftday from dual            \n");
				} else if (etcData.get("term").equals("W")) {
					strQuery.append("select TO_CHAR(TO_DATE(?,'YYYYMMDD')+28,'YYYYMMDD') aftday from dual            \n");
				} else {
					strQuery.append("select TO_CHAR(ADD_MONTHs(TO_DATE(?,'YYYYMMDD'),6),'YYYYMMDD') aftday from dual \n");
				}
			}
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, etcData.get("stday"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if(etcData.get("qrygbn").equals("0")){
					if (rs.getLong("aftday") < Long.parseLong(etcData.get("edday"))) {
						rst = new HashMap<String,String>();
						if (etcData.get("term").equals("D")) {
							rst.put("errmsg", "일별 조회는 한달을 초과할 수 없습니다.");
						} else if (etcData.get("term").equals("W")) {
							rst.put("errmsg", "주간별 조회는 8주이상을 초과할 수 없습니다.");
						} else {
							rst.put("errmsg", "월별 조회는 12개월이상을 초과할 수 없습니다.");
						}
						rsval.add(rst);
						errSw = true;
						rst = null;
					}
				}else{
					if (rs.getLong("aftday") < Long.parseLong(etcData.get("edday"))) {
						rst = new HashMap<String,String>();
						if (etcData.get("term").equals("D")) {
							rst.put("errmsg", "전체조회가 아닐 시 일별 조회는 1주일을 초과할 수 없습니다.");
						} else if (etcData.get("term").equals("W")) {
							rst.put("errmsg", "전체조회가 아닐 시 주간별 조회는 4주이상을 초과할 수 없습니다.");
						} else {
							rst.put("errmsg", "전체조회가 아닐 시 월별 조회는 6개월이상을 초과할 수 없습니다.");
						}
						rsval.add(rst);
						errSw = true;
						rst = null;
					}
				}
			}
			rs.close();
			pstmt.close();

			if (errSw == false) {
				strQuery.setLength(0);
				if (etcData.get("qrygbn").equals("0")) {
					rst = new HashMap<String,String>();
					rst.put("errmsg", "");
					rsval.add(rst);
					rst = null;
				} else {
					if (etcData.get("qrygbn").equals("4")){
						if (etcData.get("qrycd") != "" && etcData.get("qrycd") != null) {
						   if (etcData.get("qrycd").substring(0,1).equals("C")) {
							   reqCd = 4;
						   }
						} else reqCd = 1;
					}
					if (etcData.get("qrygbn").equals("1")) {
						strQuery.append("select a.cm_syscd cm_micode,a.cm_sysmsg cm_codename \n");
						strQuery.append("  from cmm0030 a,cmr1000 b    \n");
					} else if (etcData.get("qrygbn").equals("2")) {
						strQuery.append("select a.cm_deptcd cm_micode,a.cm_deptname cm_codename \n");
						strQuery.append("  from cmm0100 a,cmr1000 b    \n");
					} else if (etcData.get("qrygbn").equals("3") || (etcData.get("qrygbn").equals("4") && reqCd != 0)){
						strQuery.append("select a.cm_micode,a.cm_codename        \n");
						strQuery.append("  from cmm0020 a,cmr1010 c,cmr1000 b    \n");
					} else {
						strQuery.append("select a.cm_micode,a.cm_codename        \n");
						strQuery.append("  from cmm0020 a,cmr1000 b    \n");
					}
					strQuery.append(" where b.cr_status<>'3'                     \n");
					strQuery.append("   and b.cr_qrycd<'30'                      \n");
					strQuery.append("   and b.cr_prcdate is not null             \n");
					strQuery.append("   and to_char(b.cr_acptdate,'yyyymmdd')>=?  \n");
					strQuery.append("   and to_char(b.cr_acptdate,'yyyymmdd')<=?  \n");
					if (etcData.get("qrygbn").equals("3") || (etcData.get("qrygbn").equals("4") && reqCd != 0)){
						strQuery.append("   and b.cr_acptno=c.cr_acptno  \n");
						strQuery.append("   and c.cr_status<>'3'         \n");
					}

					if (etcData.get("qrygbn").equals("1")) {
						if (etcData.get("syscd") != null && etcData.get("syscd") != "") {
							strQuery.append("and b.cr_syscd=?                   \n");
						}
						strQuery.append("   and b.cr_syscd=a.cm_syscd       \n");
					} else if (etcData.get("qrygbn").equals("2")) {
						if (etcData.get("deptcd") != null && etcData.get("deptcd") != "") {
							strQuery.append("and b.cr_teamcd=?                  \n");
						}
						strQuery.append("   and b.cr_teamcd=a.cm_deptcd     \n");
					} else if (etcData.get("qrygbn").equals("3")) {
						if (etcData.get("rsrccd") != null && etcData.get("rsrccd") != "") {
							strQuery.append("and c.cr_rsrccd=?                  \n");
						}
						strQuery.append("   and a.cm_macode='JAWON'         \n");
						strQuery.append("   and a.cm_micode=c.cr_rsrccd     \n");
					} else if (etcData.get("qrygbn").equals("4") && reqCd == 4) {
					    strQuery.append("   and b.cr_qrycd in ('04','16')   \n");
						strQuery.append("   and a.cm_macode='CHECKIN'       \n");
					    strQuery.append("   and a.cm_micode=c.cr_qrycd      \n");
					    strQuery.append("   and c.cr_qrycd=?                \n");
					} else if (etcData.get("qrygbn").equals("4")) {
						if (reqCd == 0) {
							strQuery.append("   and a.cm_macode='REQUEST'   \n");
						    strQuery.append("   and a.cm_micode=b.cr_qrycd  \n");
						    strQuery.append("   and b.cr_qrycd=?            \n");
						} else {
							strQuery.append("   and a.cm_macode=decode(b.cr_qrycd,'04','CHECKIN','16','CHECKIN','REQUEST')   \n");
						    strQuery.append("   and a.cm_micode=decode(b.cr_qrycd,'04',c.cr_qrycd,'16',c.cr_qrycd,b.cr_qrycd) \n");
						}
					} else if (etcData.get("qrygbn").equals("5")) {
						if (etcData.get("reqcd") != null && etcData.get("reqcd") != "") {
							strQuery.append("and b.cr_emgcd=?                  \n");
						}
						strQuery.append("   and a.cm_macode='REQCD'         \n");
						strQuery.append("   and a.cm_micode=b.cr_emgcd      \n");
					}
					if (etcData.get("qrygbn").equals("1")) {
						strQuery.append("group by a.cm_syscd,a.cm_sysmsg    \n");
						strQuery.append("order by a.cm_syscd,a.cm_sysmsg    \n");
					} else if (etcData.get("qrygbn").equals("2")) {
						strQuery.append("group by a.cm_deptcd,a.cm_deptname \n");
						strQuery.append("order by a.cm_deptcd,a.cm_deptname \n");
					} else {
						strQuery.append("group by a.cm_micode,a.cm_codename \n");
						strQuery.append("order by a.cm_micode,a.cm_codename \n");
					}
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn,strQuery.toString());

					pstmt.setString(++parmCnt, etcData.get("stday"));
					pstmt.setString(++parmCnt, etcData.get("edday"));
					if (etcData.get("qrygbn").equals("1")) {
						if (etcData.get("syscd") != null && etcData.get("syscd") != ""){
							pstmt.setString(++parmCnt, etcData.get("syscd"));
						}
					}else if (etcData.get("qrygbn").equals("2")) {
						if (etcData.get("deptcd") != null && etcData.get("deptcd") != "") {
							pstmt.setString(++parmCnt, etcData.get("deptcd"));
						}
					}else if (etcData.get("qrygbn").equals("3")) {
						if (etcData.get("rsrccd") != null && etcData.get("rsrccd") != "") {
							pstmt.setString(++parmCnt, etcData.get("rsrccd"));
						}
					}else if (etcData.get("qrygbn").equals("4") && reqCd == 4) {
						pstmt.setString(++parmCnt, etcData.get("reqcd"));
					}else if (etcData.get("qrygbn").equals("4")) {
						if (reqCd == 0) {
							pstmt.setString(++parmCnt, etcData.get("reqcd"));
						}
					}else if (etcData.get("qrygbn").equals("5")) {
						if (etcData.get("reqcd") != null && etcData.get("reqcd") != "") {
							pstmt.setString(++parmCnt, etcData.get("reqcd"));
						}
					}
			        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

			        rs = pstmt.executeQuery();
					while (rs.next()){
						rst = new HashMap<String, String>();
						rst.put("cm_micode",rs.getString("cm_micode"));
						rst.put("cm_codename",rs.getString("cm_codename"));
						rsval.add(rst);
						rst = null;
					}//end of while-loop statement
					rs.close();
					pstmt.close();

				}

			}
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getTitle_sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp4100.getTitle_sub() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getTitle_sub() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp4100.getTitle_sub() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp4100.getTitle_sub() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getTitle_sub() method statement

	public Object[] getSelect_List(HashMap<String, String> etcData,ArrayList<HashMap<String, String>> titList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			boolean findSw = false;
			int i = 0;
			int j  = 0;
			rsval.clear();
			strQuery.setLength(0);
			if (etcData.get("term").equals("Y")) {
				findSw = true;
				i = 1;
				rst = new HashMap<String, String>();
				rst.put("daygbn",etcData.get("stday").substring(0,4)+"-"+etcData.get("stday").substring(4,6));
				rst.put("nday",etcData.get("stday").substring(0,6));
				rst.put("prccnt","0");
				rsval.add(rst);
				rst = null;
				do {
					strQuery.setLength(0);
					strQuery.append("select to_char(add_months(to_date(?,'yyyymmdd'),?),'yyyymm') nDay,    \n");
					strQuery.append("       to_char(add_months(to_date(?,'yyyymmdd'),?),'yyyy-mm') titDay  \n");
					strQuery.append("  from dual \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, etcData.get("stday"));
					pstmt.setInt(2, i);
					pstmt.setString(3, etcData.get("stday"));
					pstmt.setInt(4, i++);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rst = new HashMap<String, String>();
						rst.put("daygbn",rs.getString("titDay"));
						rst.put("nday",rs.getString("nDay"));
						rst.put("qday",rs.getString("nDay").substring(4));
						rst.put("prccnt","0");
						rsval.add(rst);
						rst = null;

						if (rs.getString("nDay").equals(etcData.get("edday").substring(0,6))) {
							findSw = false;
							break;
						}
					}
					rs.close();
					pstmt.close();
				} while (findSw == true);
			} else if (etcData.get("term").equals("D")) {
				findSw = true;
				i = 0;
				do {
					strQuery.setLength(0);
					strQuery.append("select to_char(to_date(?,'yyyymmdd')+?,'yyyymmdd') nDay,     \n");
					strQuery.append("       to_char(to_date(?,'yyyymmdd')+?,'yyyy-mm-dd') titDay  \n");
					strQuery.append("  from dual \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, etcData.get("stday"));
					pstmt.setInt(2, i);
					pstmt.setString(3, etcData.get("stday"));
					pstmt.setInt(4, i++);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rst = new HashMap<String, String>();
						rst.put("daygbn",rs.getString("titDay"));
						rst.put("nday",rs.getString("nDay"));
						rst.put("qday",rs.getString("nDay").substring(4));
						rst.put("prccnt","0");
						rsval.add(rst);
						rst = null;

						if (rs.getString("nDay").equals(etcData.get("edday"))) {
							findSw = false;
							break;
						}
					}
					rs.close();
					pstmt.close();
				} while (findSw == true);
			} else {
				findSw = true;
				i = 0;
				String stDay = "";
				strQuery.setLength(0);
				strQuery.append("select to_char(to_date(?,'yyyymmdd') -  \n");
				strQuery.append("  (to_number(to_char(to_date(?,'yyyymmdd'),'d')) - 1),'yyyymmdd') nday from dual  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, etcData.get("stday"));
				pstmt.setString(2, etcData.get("stday"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					stDay = rs.getString("nday");
				}
				rs.close();
				pstmt.close();
				do {
					strQuery.setLength(0);
					strQuery.append("select to_char(to_date(?,'yyyymmdd')+7,'yyyymmdd') nDay,     \n");
					strQuery.append("       to_char(to_date(?,'yyyymmdd')+7,'yyyy-mm-dd') titDay  \n");
					strQuery.append("  from dual \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, stDay);
					pstmt.setString(2, stDay);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rst = new HashMap<String, String>();
						rst.put("daygbn",rs.getString("titDay"));
						rst.put("nday",rs.getString("nDay"));
						rst.put("qday",rs.getString("nDay").substring(4));
						rst.put("prccnt","0");
						rsval.add(rst);
						rst = null;
						stDay = rs.getString("nDay");
						if (rs.getLong("nDay") >= Long.parseLong(etcData.get("edday"))) {
							findSw = false;
							break;
						}
					}
				} while (findSw == true);
			}

			for (i=0;rsval.size()>i;i++) {
				rst = new HashMap<String, String>();
				rst = rsval.get(i);
				for (j=0;titList.size()>j;j++) {
					rst.put("gbn"+titList.get(j).get("cm_micode"), "0");
				}
				rsval.set(i, rst);
			}
			strQuery.setLength(0);
			if (etcData.get("term").equals("Y")) {
				strQuery.append("select to_char(b.cr_acptdate,'yyyymm') pdate,  \n");
			} else {
				strQuery.append("select to_char(b.cr_acptdate,'yyyymmdd') pdate,\n");
			}
			if (etcData.get("qrygbn").equals("0")) {
				strQuery.append("count(*) cnt                          \n");
			} else if (etcData.get("qrygbn").equals("1")) {
				strQuery.append("b.cr_syscd cm_micode,count(*) cnt     \n");
			} else if (etcData.get("qrygbn").equals("2")) {
				strQuery.append("b.cr_teamcd cm_micode,count(*) cnt  \n");
			} else if (etcData.get("qrygbn").equals("3")) {
				strQuery.append("c.cr_rsrccd cm_micode,count(*) cnt  \n");
			} else if (etcData.get("qrygbn").equals("4")) {
				if (etcData.get("qrycd").substring(0,1).equals("C"))
					strQuery.append("c.cr_qrycd cm_micode,count(*) cnt  \n");
				else
					strQuery.append("b.cr_qrycd cm_micode,count(*) cnt  \n");
			} else if (etcData.get("qrygbn").equals("5")) {
				strQuery.append("b.cr_emgcd cm_micode,count(*) cnt  \n");
			}
			strQuery.append("  from cmr1010 c,cmr1000 b             \n");
			strQuery.append(" where b.cr_status<>'3'                \n");
			strQuery.append("   and b.cr_qrycd<'31'                 \n");
			strQuery.append("   and b.cr_prcdate is not null        \n");
			if (etcData.get("userid") != null && etcData.get("userid") != "") {
				strQuery.append("and b.cr_editor=?        \n");
			}
			if (etcData.get("syscd") != null && etcData.get("syscd") != "") {
				strQuery.append("and b.cr_syscd=?                   \n");
			}
			if (etcData.get("deptcd") != null && etcData.get("deptcd") != "") {
				strQuery.append("and b.cr_teamcd=?                  \n");
			}
			if (etcData.get("reqcd") != null && etcData.get("reqcd") != "") {
				strQuery.append("and b.cr_emgcd=?                  \n");
			}
			if (etcData.get("rsrccd") != null && etcData.get("rsrccd") != "") {
				strQuery.append("and c.cr_rsrccd=?                  \n");
			}
			if (etcData.get("qrycd") != null && etcData.get("qrycd") != "") {
				if (etcData.get("qrycd").substring(0,1).equals("C")) {
					strQuery.append("and b.cr_qrycd='04'            \n");
					if(!etcData.get("qrycd").substring(1).equals("00")){
						strQuery.append("and c.cr_qrycd=?               \n");
					}else{
						strQuery.append("and c.cr_qrycd in ('03','04','05') \n");
					}
				} else {
					strQuery.append("and b.cr_qrycd=?               \n");
				}
			}
			strQuery.append("  and to_char(b.cr_acptdate,'yyyymmdd')>=? \n");
			strQuery.append("  and to_char(b.cr_acptdate,'yyyymmdd')<=? \n");
			strQuery.append("  and b.cr_acptno=c.cr_acptno          \n");
			strQuery.append("  and c.cr_status<>'3'                 \n");
			if (etcData.get("term").equals("Y")) {
				strQuery.append("group by to_char(b.cr_acptdate,'yyyymm') ");
			} else {
				strQuery.append("group by to_char(b.cr_acptdate,'yyyymmdd') ");
			}
			if (etcData.get("qrygbn").equals("1")) {
				strQuery.append(",b.cr_syscd          \n");
			} else if (etcData.get("qrygbn").equals("2")) {
				strQuery.append(",b.cr_teamcd         \n");
			} else if (etcData.get("qrygbn").equals("3")) {
				strQuery.append(",c.cr_rsrccd         \n");
			} else if (etcData.get("qrygbn").equals("4")) {
				if (etcData.get("qrycd") == null || etcData.get("qrycd") == "") {
					strQuery.append(",c.cr_qrycd      \n");
				} else {
					if (etcData.get("qrycd").substring(0,1).equals("C"))
						strQuery.append(",c.cr_qrycd      \n");
					else
						strQuery.append(",b.cr_qrycd      \n");
				}
			} else if (etcData.get("qrygbn").equals("5")) {
				strQuery.append(",b.cr_emgcd          \n");
			}

			if (etcData.get("term").equals("Y")) {
				strQuery.append("order by to_char(b.cr_acptdate,'yyyymm') ");
			} else {
				strQuery.append("order by to_char(b.cr_acptdate,'yyyymmdd') ");
			}
			if (etcData.get("qrygbn").equals("1")) {
				strQuery.append(",b.cr_syscd          \n");
			} else if (etcData.get("qrygbn").equals("2")) {
				strQuery.append(",b.cr_teamcd         \n");
			} else if (etcData.get("qrygbn").equals("3")) {
				strQuery.append(",c.cr_rsrccd         \n");
			} else if (etcData.get("qrygbn").equals("4")) {
				if (etcData.get("qrycd").substring(0,1).equals("C"))
					strQuery.append(",c.cr_qrycd      \n");
				else
					strQuery.append(",b.cr_qrycd      \n");
			} else if (etcData.get("qrygbn").equals("5")) {
				strQuery.append(",b.cr_emgcd          \n");
			}
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			if (etcData.get("userid") != null && etcData.get("userid") != "") {
				pstmt.setString(++parmCnt, etcData.get("userid"));
			}
			if (etcData.get("syscd") != null && etcData.get("syscd") != "") {
				pstmt.setString(++parmCnt, etcData.get("syscd"));
			}
			if (etcData.get("deptcd") != null && etcData.get("deptcd") != "") {
				pstmt.setString(++parmCnt, etcData.get("deptcd"));
			}
			if (etcData.get("reqcd") != null && etcData.get("reqcd") != "") {
				pstmt.setString(++parmCnt, etcData.get("reqcd"));
			}
			if (etcData.get("rsrccd") != null && etcData.get("rsrccd") != "") {
				pstmt.setString(++parmCnt, etcData.get("rsrccd"));
			}
			if (etcData.get("qrycd") != null && etcData.get("qrycd") != "") {
				if (etcData.get("qrycd").substring(0,1).equals("C")) {
					if(!etcData.get("qrycd").substring(1).equals("00")){
						pstmt.setString(++parmCnt, etcData.get("qrycd").substring(1));
					}
				} else {
					pstmt.setString(++parmCnt, etcData.get("qrycd"));
				}
			}
			pstmt.setString(++parmCnt, etcData.get("stday"));
			pstmt.setString(++parmCnt, etcData.get("edday"));
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

	        rs = pstmt.executeQuery();
	        int totCnt = 0;
			while (rs.next()){
				for (i=0;rsval.size()>i;i++) {
					if (etcData.get("term").equals("D") || etcData.get("term").equals("Y")) {
						if (rsval.get(i).get("nday").equals(rs.getString("pdate"))) {
							rst = new HashMap<String, String>();
							rst = rsval.get(i);
							totCnt = Integer.parseInt(rst.get("prccnt"));
							totCnt = totCnt + rs.getInt("cnt");
							rst.put("prccnt", Integer.toString(totCnt));
							if (etcData.get("qrygbn").equals("0")) {
							} else {
								if (rst.get("gbn"+rs.getString("cm_micode")) != null && rst.get("gbn"+rs.getString("cm_micode")) != null)
									totCnt = Integer.parseInt(rst.get("gbn"+rs.getString("cm_micode")));
								else totCnt = 0;
								totCnt = totCnt + rs.getInt("cnt");
								rst.put("gbn"+rs.getString("cm_micode"),Integer.toString(totCnt));
							}
							rsval.set(i, rst);
							break;
						}
					} else {
						if (Long.parseLong(rsval.get(i).get("nday")) >= Long.parseLong(rs.getString("pdate"))) {
							rst = new HashMap<String, String>();
							rst = rsval.get(i);
							totCnt = Integer.parseInt(rst.get("prccnt"));
							totCnt = totCnt + rs.getInt("cnt");
							rst.put("prccnt", Integer.toString(totCnt));
							if (etcData.get("qrygbn").equals("0")) {
							} else {
								if (rst.get("gbn"+rs.getString("cm_micode")) != null && rst.get("gbn"+rs.getString("cm_micode")) != null)
									totCnt = Integer.parseInt(rst.get("gbn"+rs.getString("cm_micode")));
								else totCnt = 0;
								totCnt = totCnt + rs.getInt("cnt");
								rst.put("gbn"+rs.getString("cm_micode"),Integer.toString(totCnt));
							}
							rsval.set(i, rst);
							break;
						}
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getSelect_List() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp4100.getSelect_List() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp4100.getSelect_List() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp4100.getSelect_List() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp4100.getSelect_List() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSelect_List() method statement
}//end of Cmp4100 class statement
