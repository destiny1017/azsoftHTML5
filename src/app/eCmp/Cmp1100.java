/*****************************************************************************************
	1. program ID	: Cmp1100.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 2009. 03. 09
	5. auth		    : No Name
	6. description	: [보고서] -> [변경신청현황]
*****************************************************************************************/

package app.eCmp;

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

//import app.common.LoggableStatement;
import app.common.UserInfo;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp1100{


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
    public Object[] getReqList(String UserId,String SysCd,String DeptCd,String QryCd1,String QryCd2,String FindCd,
    		String FindWord,String StDate,String EdDate,String strDayCd,String approval,boolean subItem) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strReqGbn   = "";
		int               parmCnt     = 0;
		UserInfo         secuinfo     = new UserInfo();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String strSecu = secuinfo.getSecuInfo(UserId);
			secuinfo = null;
			strQuery.setLength(0);
			strQuery.append("select b.cm_codename request,c.cm_sysmsg,d.cm_deptname,l.cm_jobname,\n");   //cr_docno,  \n");
			strQuery.append("       e.cm_codename language,f.cm_codename jawon,h.cr_emgcd,h.cr_sayucd,      \n");
			strQuery.append("       a.cr_rsrcname,decode(h.cr_passsub,'1','긴급','일반') passsub,               \n");
			strQuery.append("       to_char(h.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,                   \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,                     \n");
			strQuery.append("       a.cr_itemid,a.cr_baseitem, a.cr_acptno ,                                             \n");
			strQuery.append("       h.cr_acptdate,a.cr_prcdate,i.cm_username,                               \n");
			strQuery.append("       nvl(a.cr_editcon,h.cr_sayu) cr_sayu,nvl(h.cr_passsub,'0') cr_passsub,   \n");
			strQuery.append("       g.cm_codename reqgbn,a.cr_acptno,j.cnt,									\n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) || '-' || substr(a.cr_acptno,7,6) acptno \n");
			strQuery.append("  from cmr1010 a,cmm0020 b,cmm0030 c,cmm0100 d,cmm0020 e,cmm0020 f,cmr1000 h,  \n");
			if (strSecu.equals("0")) strQuery.append("       cmm0044 k,                                     \n");
			strQuery.append("       cmm0040 i,cmm0020 g,cmm0102 l,			                                \n");
			strQuery.append("       (select y.cr_acptno,count(x.cr_filename) cnt from cmr1001 x,cmr1000 y               \n");
			strQuery.append("         where y.cr_acptno=x.cr_acptno(+) 							            \n");
			strQuery.append("         group by y.cr_acptno) j                                               \n");
			strQuery.append(" where h.cr_status not in ('0','3')                                            \n");
			if (approval.equals("approval3")){
				strQuery.append("   and h.cr_passsub='1'					                                \n");
			}else if(approval.equals("approval2")){
				strQuery.append("   and h.cr_passsub<>'1'					                                \n");
			}
			if (strDayCd.equals("1")) {
				strQuery.append("   and h.cr_acptdate>=to_date(?,'yyyymmddhh24miss')                        \n");
				strQuery.append("   and h.cr_acptdate<=to_date(?,'yyyymmddhh24miss')                        \n");
			} else {
				strQuery.append("   and a.cr_prcdate>=to_date(?,'yyyymmddhh24miss')                         \n");
				strQuery.append("   and a.cr_prcdate<=to_date(?,'yyyymmddhh24miss')                         \n");
			}
			if (strSecu.equals("0")) {
				strQuery.append("   and a.cr_syscd=k.cm_syscd and a.cr_jobcd=k.cm_jobcd                     \n");
				strQuery.append("   and k.cm_userid=?                                                       \n");
			}
			if (SysCd != "" && SysCd != null) strQuery.append(" and h.cr_syscd=?                            \n");
			if (QryCd1 != "" && QryCd1 != null) strQuery.append(" and h.cr_qrycd=?                          \n");
			if (DeptCd != "" && DeptCd != null) strQuery.append(" and h.cr_teamcd=?                         \n");
			strQuery.append("   and h.cr_acptno=a.cr_acptno                                                 \n");
			if (QryCd2 != "" && QryCd2 != null) strQuery.append(" and a.cr_qrycd=?                          \n");
			if (FindCd != "" && FindCd != null) {
				if (FindCd.equals("U")) strQuery.append(" and (i.cm_userid=? or i.cm_username=?)            \n");
				else if (FindCd.equals("P")) strQuery.append(" and a.cr_rsrcname like '%' || ? || '%'       \n");
				else if (FindCd.equals("A")) strQuery.append(" and a.cr_acptno=?                            \n");
			}
			strQuery.append("   and a.cr_status<>'3'                                                        \n");
			if (subItem == false) strQuery.append("and a.cr_itemid=a.cr_baseitem                            \n");
			strQuery.append("   and b.cm_macode=decode(h.cr_qrycd,'04','CHECKIN','REQUEST')                 \n");
			strQuery.append("   and b.cm_micode=decode(h.cr_qrycd,'04',a.cr_qrycd,h.cr_qrycd)               \n");
			strQuery.append("   and h.cr_syscd=c.cm_syscd                                                   \n");
			strQuery.append("   and h.cr_teamcd=d.cm_deptcd                                                 \n");
			strQuery.append("   and a.cr_jobcd=l.cm_jobcd 		                                            \n");
			strQuery.append("   and e.cm_macode='LANGUAGE' and e.cm_micode=a.cr_langcd                      \n");
			strQuery.append("   and f.cm_macode='JAWON' and f.cm_micode=a.cr_rsrccd                         \n");
			strQuery.append("   and h.cr_editor=i.cm_userid                                                 \n");
			strQuery.append("   and g.cm_macode='REQGBN' and g.cm_micode=h.cr_emgcd                         \n");
			//strQuery.append("   and m.cm_macode='PASSCD' and m.cm_micode=h.cr_sayucd                        \n");
			strQuery.append("   and h.cr_acptno=j.cr_acptno                                                 \n");
			//strQuery.append(" order by h.cr_syscd,h.cr_qrycd,h.cr_prcdate desc                              \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

			pstmt.setString(++parmCnt, StDate+"000000");
			pstmt.setString(++parmCnt, EdDate+"235959");
			if (strSecu.equals("0")) pstmt.setString(++parmCnt, UserId);
			if (SysCd != "" && SysCd != null) pstmt.setString(++parmCnt, SysCd);
			if (QryCd1 != "" && QryCd1 != null) pstmt.setString(++parmCnt, QryCd1);
			if (DeptCd != "" && DeptCd != null) pstmt.setString(++parmCnt,DeptCd);
			if (QryCd2 != "" && QryCd2 != null) pstmt.setString(++parmCnt, QryCd2);
			if (FindCd != "" && FindCd != null) {
				if (FindCd.equals("U")) {
					pstmt.setString(++parmCnt, FindWord);
					pstmt.setString(++parmCnt, FindWord);
				}
				else if (FindCd.equals("P")) pstmt.setString(++parmCnt, FindWord);
				else if (FindCd.equals("A")) pstmt.setString(++parmCnt, FindWord);
			}

	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("request",rs.getString("request"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("language",rs.getString("language"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("passsub",rs.getString("passsub"));
				rst.put("acptdate",rs.getString("acptdate"));
				rst.put("prcdate",rs.getString("prcdate"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_sayu",rs.getString("cr_sayu"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_baseitem",rs.getString("cr_baseitem"));

				if (rs.getString("passsub").equals("긴급")){
					strQuery.setLength(0);
					strQuery.append("select a.cm_codename from cmm0020 a,cmr1000 b   			 \n");
					strQuery.append("where a.cm_macode='PASSCD' and a.cm_micode=b.cr_sayucd	 	 \n");
					strQuery.append("and b.cr_acptno=?											 \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_acptno"));
					rs2 = pstmt2.executeQuery();
					//System.out.println("acptno--------"+rs.getString("cr_acptno"));
					//System.out.println(strQuery.toString());
					//System.out.println(rs.getFetchSize());

					if (rs2.next()) {
						rst.put("reqpass", rs2.getString("cm_codename"));
					}
					rs2.close();
					pstmt2.close();
				}
				if (rs.getString("request").equals("수정") || rs.getString("request").equals("신규")){
					if (rs.getInt("cnt") > 0) rst.put("docyn","유");
				}
				else rst.put("docyn","무");
				strReqGbn = rs.getString("reqgbn");
				//if (rs.getString("cr_emgcd").equals("13")) strReqGbn = strReqGbn + rs.getString("cr_docno");
				rst.put("reqgbn",strReqGbn);
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("acptno"));
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
			ecamsLogger.error("## Cmp1100.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp1100.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp1100.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp1100.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmp1100.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

}//end of Cmp1100 class statement
