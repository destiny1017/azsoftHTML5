/*****************************************************************************************
	1. program ID	: Cmp3600.java
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
public class Cmp3600{


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
	public Object[] getReqList(String UserId,String SinCd,String ReqCd,String FindCd,String FindWord,String StDate,String EdDate,String ccbYn,String PrjNo,String PrjName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		UserInfo         secuinfo     = new UserInfo();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String strSecu = secuinfo.getPMOInfo(UserId);
			strQuery.setLength(0);
			strQuery.append("select c.cd_prjno,c.cd_prjname,a.cr_docno,a.cr_qrycd,a.cr_acptno,              \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,                   \n");
			strQuery.append("       replace(cr_sayu,chr(13) || chr(10),'') cr_sayu,                         \n");
			strQuery.append("       replace(b.cr_unittit,chr(13) || chr(10),'') cr_unittit,                 \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,                     \n");
			strQuery.append("       e.cm_deptname,f.cm_username,d.cm_codename reqcd,h.cd_dirpath,           \n");
			strQuery.append("       j.cr_docfile,b.cr_ccbyn,g.cm_codename as requestgb                      \n");
			strQuery.append("  from cmr0030 j,cmm0020 d,cmd0305 h,cmm0020 g,cmm0040 f,cmm0100 e,cmd0300 c,  \n");
			strQuery.append("       cmr1100 b,cmr1000 a                                                               \n");
			strQuery.append(" where a.cr_qrycd='34' and a.cr_status not in ('0','3')                        \n");
			strQuery.append("   and a.cr_prcdate is not null                                                \n");
			strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')>?                                     \n");
			strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')<?                                     \n");
			if (strSecu.equals("0")) {
				strQuery.append("and c.cd_prjno in (select cd_prjno from cmd0304                           \n");
				strQuery.append("                    where cd_prjuser=? and cd_closedt is null)            \n");
			}
			if (ReqCd != "" && ReqCd != null) strQuery.append(" and a.cr_emgcd is not null and a.cr_emgcd=? \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno                                                 \n");
			if (FindCd != "" && FindCd != null) {
				if (FindCd.equals("1") && PrjNo != null && PrjNo != "") {
					strQuery.append("and c.cd_prjno like '%' || ? || '%'                                       \n");
				} else if (FindCd.equals("2") && PrjName != null && PrjName != "") {
					strQuery.append("and c.cd_prjname like '%' || ? || '%'                                     \n");
				} else if (FindCd.equals("3")) {
					strQuery.append(" and (f.cm_userid like '%' || ? || '%' or                         \n");
					strQuery.append("      f.cm_username like '%' || ? || '%')                         \n");
				}
				else if (FindCd.equals("4")) strQuery.append(" and j.cr_docfile like '%' || ? || '%'        \n");
			}
			if (SinCd != "" && SinCd != null) strQuery.append(" and b.cr_qrycd=?                            \n");
			if (ccbYn.equals("Y")) strQuery.append(" and b.cr_ccbyn='Y'                                     \n");
			strQuery.append("   and a.cr_editor=f.cm_userid and a.cr_teamcd = e.cm_deptcd                   \n");
			strQuery.append("   and a.cr_prjno = c.cd_prjno and b.cr_docid = j.cr_docid                     \n");
			strQuery.append("   and b.cr_prjno = h.CD_PRJNO and b.cr_docseq = h.cd_dsncd                    \n");
			strQuery.append("   and d.cm_macode = 'REQGBN' and d.cm_micode = a.cr_emgcd                     \n");
			strQuery.append("   and g.cm_macode = 'CHECKIN' and g.cm_micode = b.cr_qrycd                    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			if (strSecu.equals("0")) pstmt.setString(++parmCnt, UserId);
			if (ReqCd != "" && ReqCd != null) pstmt.setString(++parmCnt, ReqCd);
			if (FindCd != "" && FindCd != null) {
				if (FindCd.equals("1") && PrjNo != null && PrjNo != "") pstmt.setString(++parmCnt, PrjNo);
				else if (FindCd.equals("2") && PrjName != null && PrjName != "") pstmt.setString(++parmCnt, PrjName);
				else if (FindCd.equals("3")) {
					pstmt.setString(++parmCnt, FindWord);
					pstmt.setString(++parmCnt, FindWord);
				}
				else if (FindCd.equals("4")) pstmt.setString(++parmCnt, FindWord);
			}
			if (SinCd != "" && SinCd != null) pstmt.setString(++parmCnt, SinCd);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cd_prjno",rs.getString("cd_prjno"));
				rst.put("cd_prjname",rs.getString("cd_prjname"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("prcdate",rs.getString("prcdate"));
				rst.put("requestgb",rs.getString("requestgb"));
				if (rs.getString("cd_dirpath") != null) rst.put("cd_dirpath",rs.getString("cd_dirpath"));
				rst.put("cr_docfile",rs.getString("cr_docfile"));
				if (rs.getString("cr_ccbyn") == null || rs.getString("cr_ccbyn") == "") rst.put("cr_ccbyn","N");
				else rst.put("cr_ccbyn",rs.getString("cr_ccbyn"));
				if (rs.getString("cr_docno") != null && rs.getString("cr_docno") != null) {
					rst.put("reqcd",rs.getString("reqcd") + "["+rs.getString("cr_docno")+"]");
				} else	rst.put("reqcd",rs.getString("reqcd"));
				if (rs.getString("cr_unittit") != null && rs.getString("cr_unittit") != "")
					rst.put("cr_unittit",rs.getString("cr_unittit"));
				if (rs.getString("cr_sayu") != null && rs.getString("cr_sayu") != "")
					rst.put("cr_sayu",rs.getString("cr_sayu"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObjectArray = rsval.toArray();

			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3600.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3600.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3600.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3600.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp3600.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement
}//end of Cmp3600 class statement
