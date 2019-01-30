/*****************************************************************************************
	1. program ID	: eCmr3100.java
	2. create date	:
	3. auth		    : [결재확인] -> [결재현황]
	4. update date	: 090825
	5. auth		    : no name
	6. description	: eCmr3100.java 결재현황
*****************************************************************************************/

package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmp0600{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 결재리스트 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] get_SelectList(String StDate, String EdDate, String Txt_PrgName, String Cbo_SysCd,
    		String Cbo_JobCd, String Cbo_Steam, String Txt_UserId, String Cbo_Sin, String gbn, String proc, String spms, String dategbn)throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        int              Cnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);

			strQuery.append("select a.cr_syscd,a.cr_acptno,c.cm_sysmsg, d.cm_jobname,       \n");
			strQuery.append("       e.cm_deptname, f.cm_username, a.cr_passcd,              \n");
			strQuery.append("       a.cr_acptdate, a.cr_prcdate, a.cr_qrycd, a.cr_prctime,  \n");
			strQuery.append("       g.cm_codename as requestgb ,h.cm_dirpath, b.cr_rsrcname,\n");
			strQuery.append("       i.cm_codename gbn, a.cr_itsmid, a.cr_acptno,            \n");
			strQuery.append("       '['||a.cr_itsmid||']'||a.cr_itsmtitle spms,c.cm_sysinfo,\n");
			strQuery.append("       a.cr_status, a.cr_passok, nvl(a.cr_sayucd, '9') sayucd  \n");//,j.cm_codename sayucd2
			strQuery.append("from cmm0070 h,cmm0020 g,cmm0040 f,cmm0100 e,                  \n");
			strQuery.append("     cmm0102 d, cmm0030 c,cmr1010 b, cmr1000 a,cmm0020 i       \n");//,cmm0020 j
			strQuery.append("where a.cr_acptno = b.cr_acptno                                \n");
			strQuery.append("and a.cr_status <> '3'                                         \n");
			strQuery.append("and a.cr_prcdate is not null                                   \n");
			strQuery.append("and a.cr_syscd = c.cm_syscd                                    \n");
			strQuery.append("and b.cr_jobcd = d.cm_jobcd                                    \n");
			strQuery.append("and f.cm_project = e.CM_DEPTCD                                 \n");
			strQuery.append("and a.cr_editor = f.cm_userid                                  \n");
			strQuery.append("and g.cm_macode = 'CHECKIN'                                    \n");
			strQuery.append("and g.cm_micode = b.cr_qrycd                                   \n");
			strQuery.append("and g.cm_closedt is null                                       \n");
			strQuery.append("and i.cm_macode='REQPASS'                                      \n");
			strQuery.append("and i.cm_closedt is null                                       \n");
			strQuery.append("and a.cr_passok=i.cm_micode                                    \n");
			strQuery.append("and a.cr_syscd = h.cm_syscd                                    \n");
			strQuery.append("and b.cr_dsncd = h.cm_dsncd                                    \n");
			if(dategbn.equals("0")){
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')>=?                   \n");
				strQuery.append("and to_char(a.cr_acptdate,'yyyymmdd')<=?                   \n");
			}else{
				strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')>=?                    \n");
				strQuery.append("and to_char(a.cr_prcdate,'yyyymmdd')<=?                    \n");
			}
			if (Txt_PrgName != null && Txt_PrgName != "") {
				strQuery.append("and ( upper(b.cr_rsrcname) like ?                          \n");
				strQuery.append("or upper(b.cr_story) like ? )                         		\n");
			}
			if (Cbo_SysCd != null && Cbo_SysCd != "") {
				strQuery.append("and c.cm_syscd=?                                           \n");
			}
			if (Cbo_JobCd != null && Cbo_JobCd != "") {
				strQuery.append("and d.cm_jobcd=?                         					\n");
			}
			if (Cbo_Steam != null && Cbo_Steam != "") {
				//strQuery.append("and f.cm_project=?                       				\n");
				strQuery.append("and f.cm_project in (select cm_deptcd         		        \n");
				strQuery.append("                       from (select * from cmm0100		    \n");
				strQuery.append("                              where cm_useyn='Y') 		    \n");
				strQuery.append("                      start with cm_deptcd=? 		        \n");
				strQuery.append("                    connect by prior cm_deptcd=cm_updeptcd)\n");
			}
			if (Txt_UserId != null && Txt_UserId != "") {
				strQuery.append("and f.cm_username=?                                        \n");
			}
			if (Cbo_Sin != null && Cbo_Sin != "") {
				if(Cbo_Sin.equals("99")){
					strQuery.append("and g.cm_micode in ('03','04')        					\n");
				}else{
					strQuery.append("and g.cm_micode=?                      				\n");
				}
			}
			if (gbn != null && gbn != "") {
				strQuery.append("and a.cr_passok = ?                                        \n");
			}

			if (proc != null && proc != "") {
				if(proc.equals("1")){
					strQuery.append("and a.cr_prcdate is null                               \n");
				}else{
					strQuery.append("and a.cr_prcdate is not null                           \n");
				}
			}
			if (spms != null && spms != "") {
				strQuery.append("and (a.cr_itsmid like ? or upper(a.cr_itsmtitle) like upper(?)) \n");
			}
			//strQuery.append("and a.cr_sayucd = j.cm_micode and j.cm_macode='REQSAYU' \n");
			strQuery.append("order by a.cr_syscd                                            \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

			pstmt.setString(++Cnt, StDate);
			pstmt.setString(++Cnt, EdDate);
			if (Txt_PrgName != null && Txt_PrgName != "") {
				pstmt.setString(++Cnt, ("%"+Txt_PrgName+"%").toUpperCase());
				pstmt.setString(++Cnt, ("%"+Txt_PrgName+"%").toUpperCase());
			}
			if (Cbo_SysCd != null && Cbo_SysCd != "") {
				pstmt.setString(++Cnt, Cbo_SysCd);
			}
			if (Cbo_JobCd != null && Cbo_JobCd != "") {
				pstmt.setString(++Cnt, Cbo_JobCd);
			}
			if (Cbo_Steam != null && Cbo_Steam != "") {
				pstmt.setString(++Cnt, Cbo_Steam);
			}
			if (Txt_UserId != null && Txt_UserId != "") {
				pstmt.setString(++Cnt, Txt_UserId);
			}
			if (Cbo_Sin != null && Cbo_Sin != "") {
				if(!Cbo_Sin.equals("99")) pstmt.setString(++Cnt, Cbo_Sin);
			}
			if (gbn != null && gbn != "") {
				pstmt.setString(++Cnt, gbn);
			}
			if (spms != null && spms != "") {
				pstmt.setString(++Cnt, "%"+spms+"%");
				pstmt.setString(++Cnt, "%"+spms+"%");
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rtList.clear();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("acptno",rs.getString("cr_acptno").subSequence(0,4)+"-"+rs.getString("cr_acptno").subSequence(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_passcd",rs.getString("cr_passcd"));
				rst.put("cr_acptdate",rs.getString("cr_acptdate"));
				rst.put("cr_prcdate",rs.getString("cr_prcdate"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));
				rst.put("requestgb",rs.getString("requestgb"));//신청구분
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));//프로그램경로
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));//프로그램명

//				rst.put("cr_sayu", rs.getString("sayucd2")); //신청근거
//				if(rs.getString("sayucd").equals("1")){
//					rst.put("cr_sayu", "SPMS연동"); //신청근거
//				}else{
//					rst.put("cr_sayu", "기타"); //신청근거
//				}

				rst.put("gbn", rs.getString("gbn")); //처리구분
				if(rs.getString("cr_itsmid")!=null) rst.put("spms", rs.getString("spms"));
				else rst.put("spms","");

				String ConfName = "";
				String strSysGbn = "";
				String wkTeamCd = "";
				if (rs.getString("cr_status").equals("3")) {
	            	ConfName = "반려";
	            } else if (rs.getString("cr_status").equals("9")) {	//2009_0706 책임자 ->결재완료
	            	ConfName = "처리완료";
	            } else {
	            	strQuery.setLength(0);
					strQuery.append("select cr_teamcd,cr_team,cr_confname,cr_congbn from cmr9900   \n");
				    strQuery.append(" where cr_acptno=? and cr_locat='00'        \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            pstmt2.setString(1, rs.getString("cr_acptno"));
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()){
		            	strSysGbn = rs2.getString("cr_team");
		            	ConfName = rs2.getString("cr_confname");
		            	if (rs2.getString("cr_teamcd").equals("8")){
			            	ConfName = "처리완료";
		            	}
		            	if (rs2.getString("cr_teamcd").equals("1")){
		            		ConfName = rs2.getString("cr_confname") + "중";
		            	}
		            	if (rs2.getString("cr_congbn").equals("4")){
		            		ConfName = ConfName + "(후결)";
		            	}
		            	wkTeamCd = 	rs2.getString("cr_teamcd");
		            	if (rs.getString("cm_sysinfo").substring(5,6).equals("1") && rs.getString("cr_prcdate") == null) {
			            	if (rs.getString("cr_passok").equals("0") && rs.getString("cr_prctime") != null) {
			            		ConfName = "[정기적용]"+ConfName;
			            	}
		            	}
		            }
		            rs2.close();
		            pstmt2.close();
	            }
				if (wkTeamCd.equals("3")){
	            	strQuery.setLength(0);
					strQuery.append("select cm_username from cmm0040   \n");
				    strQuery.append(" where cm_userid = ?              \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
		            pstmt2.setString(1, strSysGbn);
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()){
		            	ConfName = ConfName + " [" + rs2.getString("cm_username") + "] 결재 대기중";
		            }
		            rs2.close();
		            pstmt2.close();

	            }

	            if (rs.getString("cr_prcdate") != null) {
	            	ConfName = ConfName + "["+rs.getString("cr_prcdate")+"]";
	            }
        		rst.put("sta", ConfName); //상태

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

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp0600.SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp0600.SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp0600.SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp0600.SelectList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmp0600.SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectList() method statement
}//end of Cmp0600 class statement