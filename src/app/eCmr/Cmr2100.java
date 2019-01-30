/*****************************************************************************************
	1. program ID	: Cmr3200.java
	2. create date	: 2008.04.08
	3. auth		    : m.s.kang
	4. update date	: 2009.02.21
	5. auth		    : no name
	6. description	: Request List
*****************************************************************************************/

package app.eCmr;

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
//import app.common.SystemPath;
//import app.common.UserInfo;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr2100{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 요청현황을 조회합니다.
	 * @param  syscd,reqcd,teamcd,sta,requser,startdt,enddt,userid
	 * @return json
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] get_SelectList(String pStateCd,String pStartDt,String pEndDt,String pUserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] 		  returnObject= null;
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
		String            svAcptNo    = "";
		boolean           findSw      = false;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			rtList.clear();
	        Integer           Cnt         = 0;
	        Cmr0250 cmr0250 = new Cmr0250();
//	        UserInfo userinfo = new UserInfo();
			conn = connectionContext.getConnection();
//			boolean adminSw = userinfo.isAdmin_conn(pUserId, conn);
			strQuery.setLength(0);
			if(pStateCd.equals("00") || pStateCd.equals("01") || pStateCd.equals("02")) {
				strQuery.append("select /*+ ALL_ROWS */ d.cm_sysmsg,a.cr_acptno,to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,e.cm_username, \n");
				strQuery.append("       i.cm_svrname,f.cm_codename,b.cr_rsrcname,g.cm_dirpath,'' rst,b.cr_serno,'' prcdate,         \n");
				strQuery.append("       h.cm_codename sysgbn,c.cr_team,c.cr_locat,c.cr_status,b.cr_syscd,                          \n");
			    strQuery.append("      b.cr_rsrccd,i.cm_svrcd,i.cm_seqno,b.cr_jobcd,a.cr_qrycd,'' prcdate2,'' cr_prcrst,'' errmsg,  \n");
			    strQuery.append("      decode(b.cr_qrycd,'03','신규','') NEWPGM,b.cr_jobcd                                           \n");
				strQuery.append("  from cmm0036 k,cmm0020 h,cmm0038 j,cmm0031 i,cmm0070 g,cmm0020 f,cmm0040 e,cmm0030 d,cmr9900 c,  \n");
				strQuery.append("       cmr1010 b,cmr1000 a   \n");
				strQuery.append(" where a.cr_qrycd in ('03','04')                                                                   \n");
				strQuery.append("   and a.cr_status='0'                                                                             \n");
/*
				if (adminSw == false) {
					strQuery.append("and a.cr_teamcd in (select b.cm_deptcd from cmm0100 a,cmm0100 b,cmm0040 c                      \n");
					strQuery.append("                     where c.cm_userid=? and c.cm_project=a.cm_deptcd                          \n");
					strQuery.append("                       and a.cm_updeptcd=b.cm_updeptcd and b.cm_useyn='Y')                     \n");
				}
*/
				strQuery.append("   and a.cr_acptno=b.cr_acptno                                                                     \n");
				strQuery.append("   and b.cr_status<>'3'                                                                            \n");
			    strQuery.append("   and a.cr_acptno=c.cr_acptno                                                                     \n");
				strQuery.append("   and c.cr_teamcd='1' and c.cr_status='0'                                                         \n");
				if (pStateCd.equals("01")) strQuery.append("and c.cr_locat='00'      \n");
				else strQuery.append("and c.cr_locat<>'00'      \n");
				strQuery.append("   and c.cr_team='SYSED'  \n");
				strQuery.append("   and a.cr_syscd=d.cm_syscd                                                                       \n");
				strQuery.append("   and a.cr_editor=e.cm_userid                                                                     \n");
				strQuery.append("   and f.cm_macode='JAWON' and f.cm_micode=b.cr_rsrccd                                             \n");
				strQuery.append("   and b.cr_syscd=g.cm_syscd and b.cr_dsncd=g.cm_dsncd                                             \n");
				strQuery.append("   and d.cm_syscd=i.cm_syscd                                                                       \n");
				strQuery.append("   and i.cm_closedt is null                                                                        \n");
				strQuery.append("   and i.cm_svrcd=decode(a.cr_qrycd,'03','15','05')                                                \n");
				strQuery.append("   and i.cm_syscd=j.cm_syscd and i.cm_svrcd=j.cm_svrcd                                             \n");
				strQuery.append("   and i.cm_seqno=j.cm_seqno                                                                       \n");
				strQuery.append("   and b.cr_rsrccd=j.cm_rsrccd                                                                     \n");
				strQuery.append("   and h.cm_macode='SYSGBN' and h.cm_micode=c.cr_team                                              \n");
				strQuery.append("   and d.cm_syscd=k.cm_syscd                                                                       \n");
				strQuery.append("   and b.cr_rsrccd=k.cm_rsrccd                                                                     \n");
				//strQuery.append("   and (substr(k.cm_info,11,1)='1' or substr(k.cm_info,21,1)='1')                                \n");
				strQuery.append("   and substr(k.cm_info,11,1)='1'                                                                  \n");
			}

			 if(pStateCd.equals("00")){
			    strQuery.append("union \n");
			 }
			 if(pStateCd.equals("00") || pStateCd.equals("04")){
				 strQuery.append("select /*+ ALL_ROWS */ d.cm_sysmsg,a.cr_acptno,to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,e.cm_username,\n");
				 strQuery.append("       i.cm_svrname,f.cm_codename,b.cr_rsrcname,g.cm_dirpath,'' rst,b.cr_serno,                   \n");
				 strQuery.append("       to_char(c.cr_confdate,'yyyy/mm/dd hh24:mi') prcdate,                                       \n");
				 strQuery.append("       h.cm_codename sysgbn,c.cr_team,c.cr_locat,c.cr_status,b.cr_syscd,                          \n");
				 strQuery.append("       b.cr_rsrccd,i.cm_svrcd,i.cm_seqno,b.cr_jobcd,a.cr_qrycd,                                   \n");
				 strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate2,l.cr_prcrst,m.cm_codename errmsg,      \n");
			     strQuery.append("      decode(b.cr_qrycd,'03','신규','') NEWPGM,b.cr_jobcd                                          \n");
				 strQuery.append("  from cmm0036 j,cmm0031 i,cmm0020 h,cmm0070 g,cmm0020 f,cmm0040 e,cmm0030 d,cmr9900 c,                     \n");
				 strQuery.append("       cmr1011 l,(select cm_micode,cm_codename from cmm0020 where cm_macode='ERRACCT') m,         \n");
				 strQuery.append("       cmr1010 b,cmr1000 a                                                                        \n");
				 strQuery.append(" where a.cr_qrycd in ('03','04')                                                                  \n");
 				  /*
				 if (adminSw == false) {
					strQuery.append("and a.cr_teamcd in (select b.cm_deptcd from cmm0100 a,cmm0100 b,cmm0040 c                      \n");
					strQuery.append("                     where c.cm_userid=? and c.cm_project=a.cm_deptcd                          \n");
					strQuery.append("                       and a.cm_updeptcd=b.cm_updeptcd and b.cm_useyn='Y')                     \n");
				}
*/
				 strQuery.append("   and a.cr_acptno=c.cr_acptno                                                                    \n");
				 strQuery.append("   and c.cr_locat<>'00'                                                                           \n");
				 strQuery.append("   and c.cr_teamcd='1' and c.cr_status='9'                                                        \n");
				 strQuery.append("   and c.cr_team='SYSED' \n");
				 strQuery.append("   and to_char(c.cr_confdate,'yyyymmdd')>=?                                                       \n");
				 strQuery.append("   and to_char(c.cr_confdate,'yyyymmdd')<=?                                                       \n");
				 strQuery.append("   and a.cr_acptno=b.cr_acptno and b.cr_status<>'3'                                               \n");
				 strQuery.append("   and a.cr_acptno=b.cr_acptno and b.cr_acptno=l.cr_acptno and b.cr_serno=l.cr_serno              \n");
				 strQuery.append("   and l.cr_prcsys='SYSED' and l.cr_prcrst=m.cm_micode(+)                                         \n");
				 strQuery.append("   and a.cr_syscd=d.cm_syscd                                                                      \n");
				 strQuery.append("   and a.cr_editor=e.cm_userid                                                                    \n");
				 strQuery.append("   and f.cm_macode='JAWON' and f.cm_micode=b.cr_rsrccd                                            \n");
				 strQuery.append("   and b.cr_syscd=g.cm_syscd and b.cr_dsncd=g.cm_dsncd                                            \n");
				 strQuery.append("   and d.cm_syscd=i.cm_syscd                                                                      \n");
				 strQuery.append("   and i.cm_syscd=l.cr_syscd and i.cm_svrcd=l.cr_svrcd and i.cm_seqno=l.cr_svrseq                 \n");
				 strQuery.append("   and h.cm_macode='SYSGBN' and h.cm_micode=c.cr_team                                             \n");
				 strQuery.append("   and d.cm_syscd=j.cm_syscd and b.cr_rsrccd=j.cm_rsrccd and substr(j.cm_info,11,1)='1'           \n");
			 }
			strQuery.append("order by cr_acptno                                                                                    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            Cnt = 0;

			if (pStateCd.equals("00") || pStateCd.equals("04")) {
	            pstmt.setString(++Cnt, pStartDt);
	            pstmt.setString(++Cnt, pEndDt);
            }

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				findSw = false;
				if (svAcptNo.length() == 0) findSw = true;
				else if (!svAcptNo.equals(rs.getString("cr_acptno"))) findSw = true;
				svAcptNo = rs.getString("cr_acptno");
				rst = new HashMap<String, String>();
				rst.put("cr_syscd", rs.getString("cr_syscd"));
				rst.put("cr_jobcd", rs.getString("cr_jobcd"));
				rst.put("cr_team", rs.getString("cr_team"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("cr_locat", rs.getString("cr_locat"));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("NEWPGM", rs.getString("NEWPGM"));
				if (findSw == true) {
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+
							rs.getString("cr_acptno").substring(6));
					rst.put("acptdate", rs.getString("acptdate"));
					rst.put("cm_username", rs.getString("cm_username"));
				}
				rst.put("username", rs.getString("cm_username"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_qrycd", rs.getString("cr_qrycd"));
				rst.put("sysgbn", rs.getString("sysgbn"));
				rst.put("cm_volpath", cmr0250.chgVolPath(rs.getString("cr_syscd"),rs.getString("cm_dirpath"),rs.getString("cm_svrcd"),rs.getString("cr_rsrccd"),rs.getInt("cm_seqno"),rs.getString("cr_jobcd"),conn));
				rst.put("prcdate", rs.getString("prcdate"));

				if (rs.getString("cr_locat").equals("00")) {
					strQuery.setLength(0);
					strQuery.append("select to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,  \n");
					strQuery.append("       b.cm_codename,a.cr_prcrst,a.cr_prcdate               \n");
					strQuery.append("  from (select cm_micode,cm_codename from cmm0020           \n");
					strQuery.append("         where cm_macode='ERRACCT') b,cmr1011 a             \n");
					strQuery.append(" where a.cr_acptno=? and a.cr_serno=?                       \n");
					strQuery.append("   and a.cr_svrcd=? and a.cr_svrseq=?                       \n");
					strQuery.append("   and a.cr_prcsys=?                                        \n");
					strQuery.append("   and a.cr_prcrst=b.cm_micode(+)                           \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_acptno"));
					pstmt2.setString(2, rs.getString("cr_serno"));
					pstmt2.setString(3, rs.getString("cm_svrcd"));
					pstmt2.setString(4, rs.getString("cm_seqno"));
					pstmt2.setString(5, rs.getString("cr_team"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getString("cr_prcdate") != null) rst.put("prcdate", rs2.getString("prcdate"));
						if (rs2.getString("cm_codename") != null) rst.put("rst", rs2.getString("cm_codename"));
						else rst.put("rst", rs2.getString("cr_prcrst"));
					}
					rs2.close();
					pstmt2.close();
				} else {
					rst.put("prcdate", rs.getString("prcdate2"));
					if (rs.getString("errmsg") != null) rst.put("rst", rs.getString("errmsg"));
					else rst.put("rst", rs.getString("cr_prcrst"));

				}
                rtList.add(rst);
        		rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			returnObject = rtList.toArray();
			rtList.clear();
			rtList = null;

			return returnObject;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2100.SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2100.SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2100.SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2100.SelectList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObject != null)	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2100.SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectList() method statement

}//end of Cmr2100 class statement
