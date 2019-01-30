/*****************************************************************************************
	1. program ID	: Cmp1100.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 2009. 03. 09
	5. auth		    : No Name
	6. description	: [보고서] -> [형상관리운영현황]
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
import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmp3500{


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
	public Object[] getReqList(String UserId,String Gubun,String StDate,String EdDate,String PrjNo,String PrjName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            svDate      = null;
		String            svQry       = null;
		String            svPrjNo     = null;
		String            svStep      = null;
		long              docCnt      = 0;
		long              ccbCnt      = 0;
		long              ccbHap      = 0;
		long              docHap      = 0;
		long              ccbTot      = 0;
		long              docTot      = 0;
		//int               stepCnt     = 0;
		boolean           rowSw       = false;
		int               i           = 0;
		UserInfo         secuinfo     = new UserInfo();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ArrayList<String>  rstot1 = new ArrayList<String>();
		ArrayList<Long>  rstot2 = new ArrayList<Long>();
		ArrayList<Long>  rstot3 = new ArrayList<Long>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String strSecu = secuinfo.getPMOInfo(UserId);
			secuinfo = null;

			if (Gubun.equals("01")) {
				strQuery.append("select c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm') as ilja,   \n");
				strQuery.append("       '01' as sin,nvl(b.cr_ccbyn,'N') cr_ccbyn,a.cr_devstep,count(*) as cnt \n");
			} else if (Gubun.equals("02")) {
				strQuery.append("select c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm') as ilja,   \n");
				strQuery.append("       b.cr_qrycd as sin,nvl(b.cr_ccbyn,'N') cr_ccbyn,a.cr_devstep,count(*) as cnt \n");
			} else if (Gubun.equals("03")) {
				strQuery.append("select c.cd_prjno,c.cd_prjname,'월별' as ilja,'01' as sin,                 \n");
				strQuery.append("       nvl(b.cr_ccbyn,'N') cr_ccbyn,a.cr_devstep, count(*) as cnt         \n");
			} else if (Gubun.equals("04")) {
				strQuery.append("select c.cd_prjno,c.cd_prjname,'월별' as ilja,b.cr_qrycd as sin,           \n");
				strQuery.append("       nvl(b.cr_ccbyn,'N') cr_ccbyn,a.cr_devstep,count(*) as cnt          \n");
			}
			strQuery.append("from cmr1000 d,cmd0300 c,cmr1100 b,cmr0030 a                                  \n");
			if (Gubun.equals("01") || Gubun.equals("02")) {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymm') between ? and ?                     \n");
			} else if (Gubun.equals("03") || Gubun.equals("04")) {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymmdd') between ? and ?                   \n");
			}
			strQuery.append("and d.cr_status in ('8','9') and d.cr_qrycd='34'                              \n");
			if (strSecu.equals("0")) {
				strQuery.append("and c.cd_prjno in (select cd_prjno from cmd0304                           \n");
				strQuery.append("                    where cd_prjuser=? and cd_closedt is null)            \n");
			}
			strQuery.append("and d.cr_acptno = b.cr_acptno                                                 \n");
			strQuery.append("and b.cr_docid = a.cr_docid                                                   \n");
			strQuery.append("and d.cr_prjno=c.cd_prjno                                                     \n");
			if (PrjNo != null && PrjNo != "") {
				strQuery.append("and c.cd_prjno like '%' || ? || '%'                                       \n");
			} else if (PrjName != null && PrjName != "") {
				strQuery.append("and c.cd_prjname like '%' || ? || '%'                                     \n");
			}
			if (Gubun.equals("01")) {
				strQuery.append("group by c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm'),a.cr_devstep,nvl(b.cr_ccbyn,'N') \n");
				strQuery.append("order by c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm'),a.cr_devstep   \n");
			} else if (Gubun.equals("02")) {
				strQuery.append("group by c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm'),b.cr_qrycd,a.cr_devstep,nvl(b.cr_ccbyn,'N') \n");
				strQuery.append("order by c.cd_prjno,c.cd_prjname,to_char(d.cr_acptdate,'yyyymm'),b.cr_qrycd,a.cr_devstep  \n");
			} else if (Gubun.equals("03")) {
				strQuery.append("group by c.cd_prjno,c.cd_prjname,a.cr_devstep,nvl(b.cr_ccbyn,'N')         \n");
				strQuery.append("order by c.cd_prjno,c.cd_prjname,a.cr_devstep,a.cr_devstep                \n");
			} else if (Gubun.equals("04")) {
				strQuery.append("group by c.cd_prjno,c.cd_prjname,b.cr_qrycd,a.cr_devstep,nvl(b.cr_ccbyn,'N')  \n");
				strQuery.append("order by c.cd_prjno,c.cd_prjname,b.cr_qrycd,a.cr_devstep                  \n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			if (strSecu.equals("0")) pstmt.setString(++parmCnt, UserId);
			if (PrjNo != null && PrjNo != "") {
				pstmt.setString(++parmCnt, PrjNo);
			} else if (PrjName != null && PrjName != "") {
				pstmt.setString(++parmCnt, PrjName);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        rstot1.clear();
	        rstot2.clear();
	        rstot3.clear();
			while (rs.next()){
				rowSw = false;
				if (svPrjNo == null) rowSw = true;
				else if (!rs.getString("cd_prjno").equals(svPrjNo) || !rs.getString("ilja").equals(svDate) || !rs.getString("sin").equals(svQry)) {
					rowSw = true;
				}
				if (rowSw == true) {
					if (svPrjNo != null) {
						rst.put("col" + svStep, Long.toString(docCnt) + " (" + Long.toString(ccbCnt) + ")");
						docTot = docTot + docCnt;
					    ccbTot = ccbTot + ccbCnt;
						rst.put("rowhap" , Long.toString(docTot) + " (" + Long.toString(ccbTot) + ")");
						rsval.add(rst);

					    rowSw = false;
				 		for (i = 0;rstot1.size() > i;i++) {
							 if (rstot1.get(i).equals("col" + svStep)) {
							 	 rowSw = true;
								 rstot2.set(i, rstot2.get(i) + docCnt);
								 rstot3.set(i, rstot3.get(i) + ccbCnt);
							 }
						}
						if (rowSw == false) {
							rstot1.add("col" + svStep);
							rstot2.add(docCnt);
							rstot3.add(ccbCnt);
						}

					    docHap = docHap + docTot;
					    ccbHap = ccbHap + ccbTot;
					}
					ccbTot = 0;
					docTot = 0;
					ccbCnt = 0;
					docCnt = 0;

					svPrjNo = rs.getString("cd_prjno");
					svDate = rs.getString("ilja");
					svQry = rs.getString("sin");
					svStep = rs.getString("cr_devstep");
					rst = new HashMap<String, String>();
					rst.put("cd_prjno",rs.getString("cd_prjno"));
					rst.put("cd_prjname",rs.getString("cd_prjname"));
					if (Gubun.equals("01") || Gubun.equals("02")) {
						rst.put("ilja",rs.getString("ilja"));
					}
					if (Gubun.equals("02") || Gubun.equals("04")) {
						strQuery.setLength(0);

						strQuery.append("select cm_codename from cmm0020 where cm_macode='CHECKIN' and cm_micode=?  \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
						pstmt2.setString(1, rs.getString("sin"));
				        //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				        rs2 = pstmt2.executeQuery();
				        if (rs2.next()) {
				        	rst.put("cm_codename", rs2.getString("cm_codename"));
				        }

				        rs2.close();
				        pstmt2.close();
					}
					if (rs.getString("cr_ccbyn").equals("Y")) {
						ccbCnt = rs.getLong("cnt");
					}
					docCnt = docCnt + rs.getLong("cnt");
				} else {
					if (!rs.getString("cr_devstep").equals(svStep)) {
					     rst.put("col" + svStep, Long.toString(docCnt) + " (" + Long.toString(ccbCnt) + ")");
					     rowSw = false;
				 		 for (i = 0;rstot1.size() > i;i++) {
						 	 if (rstot1.get(i).equals("col" + svStep)) {
							 	 rowSw = true;
								 rstot2.set(i, rstot2.get(i) + docCnt);
								 rstot3.set(i, rstot3.get(i) + ccbCnt);
							 }
						 }
						 if (rowSw == false) {
							rstot1.add("col" + svStep);
							rstot2.add(docCnt);
							rstot3.add(ccbCnt);
						 }
					     docTot = docTot + docCnt;
					     ccbTot = ccbTot + ccbCnt;
					     docCnt = 0;
					     ccbCnt = 0;
					     svStep = rs.getString("cr_devstep");

				    }
					if (rs.getString("cr_ccbyn").equals("Y")) {
						ccbCnt = rs.getLong("cnt");
					}
					docCnt = docCnt + rs.getLong("cnt");
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			if (svPrjNo != null) {
				rst.put("col" + svStep, Long.toString(docCnt) + " (" + Long.toString(ccbCnt) + ")");
				docTot = docTot + docCnt;
			    ccbTot = ccbTot + ccbCnt;
				rst.put("rowhap" , Long.toString(docTot) + " (" + Long.toString(ccbTot) + ")");
				rsval.add(rst);
			    docHap = docHap + docTot;
			    ccbHap = ccbHap + ccbTot;
			    rowSw = false;
		 		 for (i = 0;rstot1.size() > i;i++) {
				 	 if (rstot1.get(i).equals("col" + svStep)) {
					 	 rowSw = true;
						 rstot2.set(i, rstot2.get(i) + docCnt);
						 rstot3.set(i, rstot3.get(i) + ccbCnt);
					 }
				 }
				 if (rowSw == false) {
					rstot1.add("col" + svStep);
					rstot2.add(docCnt);
					rstot3.add(ccbCnt);
				 }
				rst = new HashMap<String, String>();
				rst.put("cd_prjno","합계");
				for (i = 0;rstot1.size() > i;i++) {
					rst.put(rstot1.get(i), Long.toString(rstot2.get(i)) + "(" + Long.toString(rstot3.get(i)) + ")");
				}
				rst.put("rowhap" , Long.toString(docHap) + " (" + Long.toString(ccbHap) + ")");
				rsval.add(rst);
			}

			returnObjectArray = rsval.toArray();

			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getReqList() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public Object[] getProgList(String UserId,String Gubun,String StDate,String EdDate,String Step1,String Step2,String Step3,String Step4,String SysCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;
		String            svStep1     = null;
		String            svStep2     = null;
		String            svStep3     = null;
		String            svStep4     = null;
		String            strStep1    = null;
		String            strStep2    = null;
		String            strStep3    = null;
		String            strStep4    = null;
		String            strRsrc     = "";
		long               stepCnt     = 0;
		boolean           rowSw       = false;
		int               i           = 0;
		long             rowCnt       = 0;
		long             subCnt       = 0;
		//long             totCnt       = 0;
		boolean          sysSw        = false;
		boolean          jobSw        = false;
		boolean          qrySw        = false;
		boolean          teamSw        = false;
		int              chgSw        = 0;
		int              stepGbn      = 1;
		boolean          firstSw      = true;
		UserInfo         secuinfo     = new UserInfo();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ArrayList<String>  rstot1 = new ArrayList<String>();
		ArrayList<Long>  rstot = new ArrayList<Long>();
		ArrayList<Long>  subtot1 = new ArrayList<Long>();
		ArrayList<Long>  subtot2 = new ArrayList<Long>();
		ArrayList<Long>  subtot3 = new ArrayList<Long>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			//String strSecu = secuinfo.getSecuInfo(UserId);
			boolean Secuflg = secuinfo.isAdmin2(UserId);
			
			secuinfo = null;
			strQuery.setLength(0);
			strQuery.append("select ");

			if (Step1.equals("1")) {
				strQuery.append("c.cm_sysmsg, ");
				sysSw = true;
			}
			else if (Step1.equals("2")) {
				strQuery.append("a.cm_jobname, ");
				jobSw = true;
			}
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
				else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
			}
			else if (Step1.equals("5")) {
				strQuery.append("f.cm_deptname, ");
				teamSw = true;
			}
			else {
				strQuery.append("e.cm_codename, ");
				qrySw = true;
			}

			if (Step2 != null && Step2 != "") {
				stepGbn = 2;
				if (Step2.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step2.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				}
				else if (Step2.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");
					qrySw = true;
				}
			}
			if (Step3 != null && Step3 != "") {
				stepGbn = 3;
				if (Step3.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step3.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				}
				else if (Step3.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");
					qrySw = true;
				}
			}
			if (Step4 != null && Step4 != "") {
				stepGbn = 4;
				if (Step4.equals("1")) {
					strQuery.append("c.cm_sysmsg, ");
					sysSw = true;
				}
				else if (Step4.equals("2")) {
					strQuery.append("a.cm_jobname, ");
					jobSw = true;
				}
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm') as ilja, ");
					else if (Gubun.equals("02")) strQuery.append("'' as ilja, ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd') as ilja, ");
				}
				else if (Step4.equals("5")) {
					strQuery.append("f.cm_deptname, ");
					teamSw = true;
				} else {
					strQuery.append("e.cm_codename, ");
					qrySw = true;
				}
			}
			strQuery.append("  b.cr_rsrccd,count(*) as cnt \n");
			strQuery.append("from ");
			if (sysSw == true) strQuery.append("cmm0030 c, ");
			if (jobSw == true) strQuery.append("cmm0102 a, ");
			if (qrySw == true) strQuery.append("cmm0020 e, ");
			if (teamSw == true) strQuery.append("cmm0100 f, ");
			strQuery.append("cmr1000 d,cmr1010 b                                  \n");
			if (Gubun.equals("01")) {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymm') between ? and ?           \n");
			} else {
				strQuery.append("where to_char(d.cr_acptdate,'yyyymmdd') between ? and ?         \n");
			}
			strQuery.append("and d.cr_status in ('8','9') and d.cr_qrycd='04'                    \n");
			strQuery.append("and d.cr_acptno = b.cr_acptno                                       \n");
			//if (strSecu.equals("0")) {
			if(!Secuflg){
				strQuery.append("and b.cr_syscd in (select distinct cm_syscd from cmm0044        \n");
				strQuery.append("                    where cm_userid=? and cm_closedt is null)   \n");
				strQuery.append("and b.cr_jobcd in (select distinct cm_syscd from cmm0044        \n");
				strQuery.append("                    where cm_userid=? and cm_closedt is null    \n");
				strQuery.append("                      and cm_syscd=b.cr_syscd)                  \n");
			}
			strQuery.append("and b.cr_itemid=b.cr_baseitem                                       \n");
			if (sysSw == true) strQuery.append("and d.cr_syscd=c.cm_syscd                        \n");
			if (jobSw == true) strQuery.append("and b.cr_jobcd=a.cm_jobcd                        \n");
			if (teamSw == true) strQuery.append("and d.cr_teamcd=f.cm_deptcd                     \n");
			if (qrySw == true)
				strQuery.append("and e.cm_macode='CHECKIN' and e.cm_micode=b.cr_qrycd            \n");
			if (JobCd != null && JobCd != "") {
				strQuery.append("and b.cr_syscd=? and b.cr_jobcd=?                               \n");
			} else if (SysCd != null && SysCd != "") {
				strQuery.append("and b.cr_syscd=?                                                \n");
			}

            strQuery.append("group by ");
			if (Step1.equals("1")) strQuery.append("c.cm_sysmsg, ");
			else if (Step1.equals("2")) strQuery.append("a.cm_jobname, ");
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
			} else if (Step1.equals("5")) strQuery.append("f.cm_deptname, ");
			else if (qrySw) strQuery.append("e.cm_codename, ");

			if (Step2 != null && Step2 != "") {
				if (Step2.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step2.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step2.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}

			if (Step3 != null && Step3 != "") {
				if (Step3.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step3.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step3.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}

			if (Step4 != null && Step4 != "") {
				if (Step4.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step4.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step4.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}

			strQuery.append("b.cr_rsrccd \n");

            strQuery.append("order by ");
			if (Step1.equals("1")) strQuery.append("c.cm_sysmsg, ");
			else if (Step1.equals("2")) strQuery.append("a.cm_jobname, ");
			else if (Step1.equals("3")) {
				if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
				else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
			} else if (Step1.equals("5")) strQuery.append("f.cm_deptname, ");
			else if (qrySw) strQuery.append("e.cm_codename, ");

			if (Step2 != null && Step2 != "") {
				if (Step2.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step2.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step2.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step2.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}
			if (Step3 != null && Step3 != "") {
				if (Step3.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step3.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step3.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step3.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}
			if (Step4 != null && Step4 != "") {
				if (Step4.equals("1")) strQuery.append("c.cm_sysmsg, ");
				else if (Step4.equals("2")) strQuery.append("a.cm_jobname, ");
				else if (Step4.equals("3")) {
					if (Gubun.equals("01")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm'), ");
					else if (Gubun.equals("03")) strQuery.append("to_char(d.cr_acptdate,'yyyy-mm-dd'), ");
				} else if (Step4.equals("5")) strQuery.append("f.cm_deptname, ");
				else if (qrySw) strQuery.append("e.cm_codename, ");
			}
			strQuery.append("b.cr_rsrccd \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, StDate);
			pstmt.setString(++parmCnt, EdDate);
			//if (strSecu.equals("0")) {
			if(!Secuflg){
				pstmt.setString(++parmCnt, UserId);
				pstmt.setString(++parmCnt, UserId);
			}
			if (JobCd != null && JobCd != "") {
				pstmt.setString(++parmCnt, SysCd);
				pstmt.setString(++parmCnt, JobCd);
			} else if (SysCd != null && SysCd != "") {
				pstmt.setString(++parmCnt, SysCd);
			}
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();
	        rstot1.clear();
	        rstot.clear();
	        subtot1.clear();
	        subtot2.clear();
	        subtot3.clear();
			while (rs.next()){
				rowSw = false;
				chgSw = 0;
				strStep1 = null;
				strStep2 = null;
				strStep3 = null;
				strStep4 = null;

				if (Step1.equals("1")) strStep1 = rs.getString("cm_sysmsg");
				else if (Step1.equals("2")) strStep1 = rs.getString("cm_jobname");
				else if (Step1.equals("3")) {
					if (Gubun.equals("02")) strStep1 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
					                            +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
					else strStep1 = rs.getString("ilja");
				}
				else if (Step1.equals("5")) strStep1 = rs.getString("cm_deptname");
				else strStep1 = rs.getString("cm_codename");

				if (Step2 != "" && Step2 != null) {
					if (Step2.equals("1")) strStep2 = rs.getString("cm_sysmsg");
					else if (Step2.equals("2")) strStep2 = rs.getString("cm_jobname");
					else if (Step2.equals("3")) {
						if (Gubun.equals("02")) strStep2 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep2 = rs.getString("ilja");
					}
					else if (Step2.equals("5")) strStep2 = rs.getString("cm_deptname");
					else strStep2 = rs.getString("cm_codename");
				}

				if (Step3 != "" && Step3 != null) {
					if (Step3.equals("1")) strStep3 = rs.getString("cm_sysmsg");
					else if (Step3.equals("2")) strStep3 = rs.getString("cm_jobname");
					else if (Step3.equals("3")) {
						if (Gubun.equals("02")) strStep3 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep3 = rs.getString("ilja");
					}
					else if (Step3.equals("5")) strStep3 = rs.getString("cm_deptname");
					else strStep3 = rs.getString("cm_codename");
				}

				if (Step4 != "" && Step4 != null) {
					if (Step4.equals("1")) strStep4 = rs.getString("cm_sysmsg");
					else if (Step4.equals("2")) strStep4 = rs.getString("cm_jobname");
					else if (Step4.equals("3")) {
						if (Gubun.equals("02")) strStep4 = StDate.substring(2,4)+"-"+StDate.substring(4,6)+"-"+StDate.substring(6)+" ~ "
                        +EdDate.substring(2,4)+"-"+EdDate.substring(4,6)+"-"+EdDate.substring(6);
						else strStep4 = rs.getString("ilja");
					}
					else if (Step4.equals("5")) strStep4 = rs.getString("cm_deptname");
					else strStep4 = rs.getString("cm_codename");
				}


				//System.out.println("svStep1 : "+svStep1+ " " + strStep1);
				if (svStep1 == null) rowSw = true;
				else if (!svStep1.equals(strStep1)) rowSw = true;
			    if (rowSw == true) {
					chgSw = 1;
			    }

			    //System.out.println("svStep2 : "+svStep2+ " " + strStep2);
			    if (rowSw == false && Step2 != null && Step2 != "") {
			    	if (svStep2 == null) rowSw = true;
			    	else if (!svStep2.equals(strStep2)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 2;
			    	}
			    }
			    if (rowSw == false && Step3 != null && Step3 != "") {
			    	if (svStep3 == null) rowSw = true;
			    	else if (!svStep3.equals(strStep3)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 3;
			    	}
			    }
			    if (rowSw == false && Step4 != null && Step4 != "") {
			    	if (svStep4 == null) rowSw = true;
			    	else if (!svStep4.equals(strStep4)) rowSw = true;
			    	if (rowSw == true) {
						chgSw = 4;
			    	}
			    }

			    //System.out.println("check : "+rowSw+ " " + chgSw);
				if (rowSw == true && firstSw == false) {
					if (rowCnt == 0) rst.put("rowhap" , "");
					else rst.put("rowhap" , Long.toString(rowCnt));
					rsval.add(rst);
                    rowCnt = 0;

                    //System.out.println("+++++strStep1,strStep2,stepGbn,chgSw+++++"+
                    //		           strStep1+","+strStep2+","+Integer.toString(stepGbn)+","+Integer.toString(chgSw));
					if ((stepGbn > 1) && (stepGbn > chgSw)) {
                        if (stepGbn == 4) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
    							if (subtot3.get(i) == 0) rst.put(rstot1.get(i), "");
    							else rst.put(rstot1.get(i), Long.toString(subtot3.get(i)));
    							subCnt = subCnt + subtot3.get(i);
    							subtot3.set(i, rowCnt);
    						}
                        	rst.put("step4name" , svStep3+" 합계");
                        	if (subCnt == 0) rst.put("rowhap" , "");
                        	else rst.put("rowhap" , Long.toString(subCnt));
                        	rsval.add(rst);



                        	if (chgSw == 2) {
                            	rst = new HashMap<String, String>();
                            	subCnt = 0;
                            	for (i = 0;rstot1.size() > i;i++) {
        							if (subtot2.get(i) == 0) rst.put(rstot1.get(i), "");
        							else rst.put(rstot1.get(i), Long.toString(subtot2.get(i)));
        							subCnt = subCnt + subtot2.get(i);
        							subtot2.set(i, rowCnt);
        						}
                            	rst.put("step4name" , svStep2+" 합계");
                            	if (subCnt == 0) rst.put("rowhap" , "");
                            	else rst.put("rowhap" , Long.toString(subCnt));
                            	rsval.add(rst);

                            	if (chgSw == 1) {
                                	rst = new HashMap<String, String>();
                                	subCnt = 0;
                                	for (i = 0;rstot1.size() > i;i++) {
            							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
            							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
            							subCnt = subCnt + subtot1.get(i);
            							subtot1.set(i, rowCnt);
            						}
                                	rst.put("step4name" , svStep1+" 합계");
                                	if (subCnt == 0) rst.put("rowhap" , "");
                                	else rst.put("rowhap" , Long.toString(subCnt));
                                	rsval.add(rst);
                            	}
                        	}
                        } else if (stepGbn == 3) {
	                    	rst = new HashMap<String, String>();
	                    	subCnt = 0;
	                    	for (i = 0;rstot1.size() > i;i++) {
    							if (subtot2.get(i) == 0) rst.put(rstot1.get(i), "");
    							else rst.put(rstot1.get(i), Long.toString(subtot2.get(i)));
								subCnt = subCnt + subtot2.get(i);
								subtot2.set(i, rowCnt);
							}
                        	rst.put("step3name" , svStep2+" 합계");
                        	if (subCnt == 0) rst.put("rowhap" , "");
                        	else rst.put("rowhap" , Long.toString(subCnt));
	                    	rsval.add(rst);

	                    	if (chgSw == 1) {
	                        	rst = new HashMap<String, String>();
	                        	subCnt = 0;
	                        	for (i = 0;rstot1.size() > i;i++) {
	    							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
	    							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
	    							subCnt = subCnt + subtot1.get(i);
	    							subtot1.set(i, rowCnt);
	    						}
	                        	rst.put("step3name" , svStep1+" 합계");
	                        	if (subCnt == 0) rst.put("rowhap" , "");
                            	else rst.put("rowhap" , Long.toString(subCnt));
	                        	rsval.add(rst);
	                    	}
						} else if (stepGbn == 2) {
	                    	rst = new HashMap<String, String>();
	                    	subCnt = 0;
	                    	for (i = 0;rstot1.size() > i;i++) {
    							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
    							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
								subCnt = subCnt + subtot1.get(i);
								subtot1.set(i, rowCnt);
							}
                        	rst.put("step2name" , svStep1+" 합계");
                        	if (subCnt == 0) rst.put("rowhap" , "");
                        	else rst.put("rowhap" , Long.toString(subCnt));

	                    	//System.out.println("+++++++++++rowhap+++++++++"+Long.toString(subCnt));
	                    	rsval.add(rst);
						}
					}
					svStep1 = strStep1;
					svStep2 = strStep2;
					svStep3 = strStep3;
					svStep4 = strStep4;

					rst = new HashMap<String, String>();
					rst.put("step1name",strStep1);
					rst.put("step2name",strStep2);
					rst.put("step3name",strStep3);
					rst.put("step4name",strStep4);
				}

				//System.out.println("firstSw : "+firstSw);
				if (firstSw == true) {
					svStep1 = strStep1;
					svStep2 = strStep2;
					svStep3 = strStep3;
					svStep4 = strStep4;

					rst = new HashMap<String, String>();
					rst.put("step1name",strStep1);
					rst.put("step2name",strStep2);
					rst.put("step3name",strStep3);
					rst.put("step4name",strStep4);
					firstSw = false;
				}

				strRsrc = rs.getString("cr_rsrccd");
				rst.put("col" + strRsrc, Integer.toString(rs.getInt("cnt")));
				rowCnt = rowCnt + rs.getInt("cnt");
			    rowSw = false;

		 		 for (i = 0;rstot1.size() > i;i++) {
				 	 if (rstot1.get(i).equals("col" + strRsrc)) {
				 		 rowSw = true;
				 		 rstot.set(i, rstot.get(i) + rs.getInt("cnt"));

				 		 if (stepGbn > 1) subtot1.set(i, subtot1.get(i) + rs.getInt("cnt"));
				 		 if (stepGbn > 2) subtot2.set(i, subtot2.get(i) + rs.getInt("cnt"));
				 		 if (stepGbn > 3) subtot3.set(i, subtot3.get(i) + rs.getInt("cnt"));
				 		 break;

					 }
				 }
		 		//System.out.println("rowSw2 : "+rowSw);
				 if (rowSw == false) {
					rstot1.add("col" + strRsrc);
					stepCnt = rs.getInt("cnt");
					rstot.add(stepCnt);

					if (stepGbn>1) subtot1.add(stepCnt);
					if (stepGbn>2) subtot2.add(stepCnt);
					if (stepGbn>3) subtot3.add(stepCnt);
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;
			if (firstSw == false) {
				if (rowCnt == 0) rst.put("rowhap" , "");
				else rst.put("rowhap" , Long.toString(rowCnt));
				rsval.add(rst);

				chgSw = stepGbn - 1;
				if ((stepGbn > 1) && (stepGbn > chgSw)) {
                    if (stepGbn == 4) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							if (subtot3.get(i) == 0) rst.put(rstot1.get(i), ""); 
							else rst.put(rstot1.get(i), Long.toString(subtot3.get(i)));
							subCnt = subCnt + subtot3.get(i);
							subtot3.set(i, rowCnt);
						}
                    	rst.put("step4name" , svStep3+" 합계");
        				if (subCnt == 0) rst.put("rowhap" , "");
        				else rst.put("rowhap" , Long.toString(subCnt));
                    	rsval.add(rst);



                    	if (chgSw == 2) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
                        		if (subtot2.get(i) == 0) rst.put(rstot1.get(i), "");
    							else rst.put(rstot1.get(i), Long.toString(subtot2.get(i)));
    							subCnt = subCnt + subtot2.get(i);
    							subtot2.set(i, rowCnt);
    						}
                        	rst.put("step4name" , svStep2+" 합계");
            				if (subCnt == 0) rst.put("rowhap" , "");
            				else rst.put("rowhap" , Long.toString(subCnt));
                        	rsval.add(rst);

                        	if (chgSw == 1) {
                            	rst = new HashMap<String, String>();
                            	subCnt = 0;
                            	for (i = 0;rstot1.size() > i;i++) {
        							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
        							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
        							subCnt = subCnt + subtot1.get(i);
        							subtot1.set(i, rowCnt);
        						}
                            	rst.put("step4name" , svStep1+" 합계");
                				if (subCnt == 0) rst.put("rowhap" , "");
                				else rst.put("rowhap" , Long.toString(subCnt));
                            	rsval.add(rst);
                        	}
                    	}
                    } else if (stepGbn == 3) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							if (subtot2.get(i) == 0) rst.put(rstot1.get(i), "");
							else rst.put(rstot1.get(i), Long.toString(subtot2.get(i)));
							subCnt = subCnt + subtot2.get(i);
							subtot2.set(i, rowCnt);
						}
                    	rst.put("step3name" , svStep2+" 합계");
        				if (subCnt == 0) rst.put("rowhap" , "");
        				else rst.put("rowhap" , Long.toString(subCnt));
                    	rsval.add(rst);

                    	if (chgSw == 1) {
                        	rst = new HashMap<String, String>();
                        	subCnt = 0;
                        	for (i = 0;rstot1.size() > i;i++) {
    							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
    							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
    							subCnt = subCnt + subtot1.get(i);
    							subtot1.set(i, rowCnt);
    						}
                        	rst.put("step3name" , svStep1+" 합계");
            				if (subCnt == 0) rst.put("rowhap" , "");
            				else rst.put("rowhap" , Long.toString(subCnt));
                        	rsval.add(rst);
                    	}
					} else if (stepGbn == 2) {
                    	rst = new HashMap<String, String>();
                    	subCnt = 0;
                    	for (i = 0;rstot1.size() > i;i++) {
							if (subtot1.get(i) == 0) rst.put(rstot1.get(i), "");
							else rst.put(rstot1.get(i), Long.toString(subtot1.get(i)));
							subCnt = subCnt + subtot1.get(i);
							subtot1.set(i, rowCnt);
						}
                    	rst.put("step2name" , svStep1+" 합계");
        				if (subCnt == 0) rst.put("rowhap" , "");
        				else rst.put("rowhap" , Long.toString(subCnt));

                    	//System.out.println("+++++++++++rowhap+++++++++"+Long.toString(subCnt));
                    	rsval.add(rst);
					}
				}

            	rst = new HashMap<String, String>();
            	subCnt = 0;
            	for (i = 0;rstot1.size() > i;i++) {
					if (rstot.get(i) == 0) rst.put(rstot1.get(i), "");
					else rst.put(rstot1.get(i), Long.toString(rstot.get(i)));
					subCnt = subCnt + rstot.get(i);
				}
            	rst.put("step1name" , "총계");
				if (subCnt == 0) rst.put("rowhap" , "");
				else rst.put("rowhap" , Long.toString(subCnt));
            	rsval.add(rst);
			}

			returnObjectArray = rsval.toArray();
			//ecamsLogger.debug(rsval.toString());
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getProgList() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement

	public Object[] getPrjStep() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select b.cm_micode,b.cm_codename from cmr0030 a,cmm0020 b      \n");
			strQuery.append(" where a.cr_lstver>0                                           \n");
			strQuery.append("   and b.cm_macode='DEVSTEP' and b.cm_micode=a.cr_devstep      \n");
			strQuery.append(" group by b.cm_micode,b.cm_codename                            \n");
			strQuery.append(" order by b.cm_micode,b.cm_codename                            \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			conn.close();
			rs = null;
			conn = null;

			returnObjectArray = rsval.toArray();

			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getPrjStep() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getPrjStep() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getPrjStep() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getPrjStep() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getPrjStep() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrjStep() method statement

	public Object[] getPrjInfo(String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		//String            strReqGbn   = "";
		UserInfo         secuinfo     = new UserInfo();
		Object[] returnObjectArray   = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String strSecu = secuinfo.getPMOInfo(UserId);
			strQuery.setLength(0);
			strQuery.append("select a.cd_prjno,a.cd_prjname,a.cd_setyn            \n");
			strQuery.append("  from cmd0300 a                                     \n");
			strQuery.append(" where A.cd_status <>'9' AND a.cd_closedt is null    \n");
			if (strSecu.equals("0")) {
				strQuery.append("and a.cd_prjno in (select cd_prjno from cmd0304  \n");
				strQuery.append("                    where cd_prjuser=?           \n");
				strQuery.append("                      and cd_closedt is null)    \n");
			}
			strQuery.append(" order by a.cd_prjno,a.cd_prjname                    \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			if (strSecu.equals("0")) pstmt.setString(1,UserId);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			while (rs.next()){
				if (rs.getRow() == 0) {
					rst = new HashMap<String, String>();
					rst.put("ID", Integer.toString(rs.getRow()));
					rst.put("cd_prjname",rs.getString("선택하세요"));

					strQuery.setLength(0);
					strQuery.append("select cr_prjno from cmr1000                  \n");
					strQuery.append(" where cr_editor=? and cr_prjno is not null   \n");
					strQuery.append(" order by cr_acptdate desc                    \n");

					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					pstmt2.setString(1,UserId);
					//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			        rs2 = pstmt2.executeQuery();
			        if (rs2.next()) {
			        	rst.put("cd_prjno", rs2.getString("cr_prjno"));
			        } else rst.put("cd_prjno","");
			        rs2.close();
			        pstmt2.close();
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cd_prjno",rs.getString("cd_prjno"));
				rst.put("cd_prjname",rs.getString("cd_prjname"));
				rst.put("cd_setyn",rs.getString("cd_setyn"));
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
			ecamsLogger.error("## Cmp3500.getPrjInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getPrjInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getPrjInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getPrjInfo() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getPrjInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getPrjInfo() method statement

	public String excelDataMake(ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep,String UserId,String QryCd,String exlName) throws SQLException, Exception {
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ArrayList<String> headerDef = new ArrayList<String>();
		excelUtil excelutil = new excelUtil();
		SystemPath systempath = new SystemPath();
		int j     = 0;
		String retMsg         = "";

		try {
			headerDef.add("cd_prjno");
			headerDef.add("cd_prjname");
			headerDef.add("ilja");

			if (QryCd.equals("02") || QryCd.equals("04")) {
				headerDef.add("cm_codename");
			}
			headerDef.add("rowhap");
			for (j=0;prjStep.size()>j;j++) {
				headerDef.add("col"+prjStep.get(j).get("cm_micode"));
			}

			rtList.clear();
			rst = new HashMap<String,String>();
			rst.put("cd_prjno", "프로젝트번호");
			rst.put("cd_prjname", "프로젝트명");
			rst.put("rowhap", "합계");

			for (j=0;prjStep.size()>j;j++) {
				rst.put("col"+prjStep.get(j).get("cm_micode"), prjStep.get(j).get("cm_codename"));
			}
			rtList.add(rst);

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				rst.put("cd_prjno", fileList.get(i).get("cd_prjno"));
				rst.put("cd_prjname", fileList.get(i).get("cd_prjname"));
				rst.put("ilja", fileList.get(i).get("ilja"));
				if (QryCd.equals("02") || QryCd.equals("04")) {
					rst.put("cm_codename",fileList.get(i).get("cm_codename"));
				}
				rst.put("rowhap", fileList.get(i).get("rowhap"));
				for (j=0;prjStep.size()>j;j++) {
					rst.put("col"+prjStep.get(j).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(j).get("cm_micode")));
				}
				rtList.add(rst);
			}//end of while-loop statement

			String strPath = systempath.getTmpDir("99");
			retMsg = excelutil.setExcel(strPath+"/"+exlName, headerDef, rtList);

			systempath = null;
			excelutil = null;


			return retMsg;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.excelDataMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.excelDataMake() Exception END ##");
			throw exception;
		}finally{
		}
	}

	public String excelDataMake_Prog(ArrayList<HashMap<String,String>> fileList,ArrayList<HashMap<String,String>> prjStep,String UserId,String QryCd,HashMap<String,String> etcData,String exlName) throws SQLException, Exception {

		ArrayList<HashMap<String, String>>	rtList	   = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst		   = null;
		ArrayList<String> 					headerDef  = new ArrayList<String>();
		excelUtil 							excelutil  = new excelUtil();
		SystemPath 							systempath = new SystemPath();
		int 								j     	   = 0;
		String 								retMsg     = "";

		try {
			if (etcData.get("step1name") != null && etcData.get("step1name") != "") {
				headerDef.add("step1name");
			}
			if (etcData.get("step2name") != null && etcData.get("step2name") != "") {
				headerDef.add("step2name");
			}
			if (etcData.get("step3name") != null && etcData.get("step3name") != "") {
				headerDef.add("step3name");
			}
			if (etcData.get("step4name") != null && etcData.get("step4name") != "") {
				headerDef.add("step4name");
			}
			headerDef.add("rowhap");

			headerDef.add("col"+prjStep.get(2).get("cm_micode"));
			headerDef.add("col"+prjStep.get(0).get("cm_micode"));
			headerDef.add("col"+prjStep.get(1).get("cm_micode"));
//			for (j=0;prjStep.size()>j;j++) {
//				headerDef.add("col"+prjStep.get(j).get("cm_micode"));
//			}

			rtList.clear();
			rst = new HashMap<String,String>();
			if (etcData.get("step1name") != null && etcData.get("step1name") != "") {
				rst.put("step1name",etcData.get("step1name"));
			}
			if (etcData.get("step2name") != null && etcData.get("step2name") != "") {
				rst.put("step2name",etcData.get("step2name"));
			}
			if (etcData.get("step3name") != null && etcData.get("step3name") != "") {
				rst.put("step3name",etcData.get("step3name"));
			}
			if (etcData.get("step4name") != null && etcData.get("step4name") != "") {
				rst.put("step4name",etcData.get("step4name"));
			}
			rst.put("rowhap", "합계");

			rst.put("col"+prjStep.get(2).get("cm_micode"), prjStep.get(2).get("cm_codename"));
			rst.put("col"+prjStep.get(0).get("cm_micode"), prjStep.get(0).get("cm_codename"));
			rst.put("col"+prjStep.get(1).get("cm_micode"), prjStep.get(1).get("cm_codename"));
//			for (j=0;prjStep.size()>j;j++) {
//				rst.put("col"+prjStep.get(j).get("cm_micode"), prjStep.get(j).get("cm_codename"));
//			}
			rtList.add(rst);

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String,String>();
				if (etcData.get("step1name") != null && etcData.get("step1name") != "") {
					rst.put("step1name",fileList.get(i).get("step1name"));
				}
				if (etcData.get("step2name") != null && etcData.get("step2name") != "") {
					rst.put("step2name",fileList.get(i).get("step2name"));
				}
				if (etcData.get("step3name") != null && etcData.get("step3name") != "") {
					rst.put("step3name",fileList.get(i).get("step3name"));
				}
				if (etcData.get("step4name") != null && etcData.get("step4name") != "") {
					rst.put("step4name",fileList.get(i).get("step4name"));
				}
				rst.put("rowhap", fileList.get(i).get("rowhap"));
				rst.put("col"+prjStep.get(2).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(2).get("cm_micode")));
				rst.put("col"+prjStep.get(0).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(0).get("cm_micode")));
				rst.put("col"+prjStep.get(1).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(1).get("cm_micode")));
//				for (j=0;prjStep.size()>j;j++) {
//					rst.put("col"+prjStep.get(j).get("cm_micode"), fileList.get(i).get("col"+prjStep.get(j).get("cm_micode")));
//				}
				rtList.add(rst);
			}//end of while-loop statement

			String strPath = systempath.getTmpDir("99");
			retMsg = excelutil.setExcel(strPath+"/"+exlName, headerDef, rtList);

			systempath = null;
			excelutil = null;


			return retMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.excelDataMake_Prog() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.excelDataMake_Prog() Exception END ##");
			throw exception;
		}finally{
		}
	}

	public Object[] getRsrcCd(String UserId,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		//ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		//String            strReqGbn   = "";
		int               parmCnt     = 0;
		String            secuYn      = "";
		UserInfo          userinfo    = new UserInfo();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			secuYn = userinfo.getSecuInfo(UserId);

			strQuery.append("select b.cm_micode,b.cm_codename from cmm0036 a,cmm0020 b    \n");
			strQuery.append(" where a.cm_closedt is null                                  \n");
			if (SysCd != "" && SysCd != null) strQuery.append("and a.cm_syscd=?           \n");
			else if (secuYn.equals("0")) {
			   strQuery.append("and a.cm_syscd in (select distinct cm_syscd from cmm0044  \n");
			   strQuery.append("                    where cm_userid=? and cm_closedt is null) \n");
			}
			strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037   \n");
			strQuery.append("                            where cm_syscd=a.cm_syscd        \n");
			strQuery.append("                              and cm_rsrccd<>cm_samersrc)    \n");
			strQuery.append("   and substr(a.cm_info,26,1)='0'                            \n");
			strQuery.append("   and b.cm_macode='JAWON'                                   \n");
			strQuery.append("   and b.cm_micode=a.cm_rsrccd                               \n");
			strQuery.append(" group by b.cm_micode,b.cm_codename                          \n");
			strQuery.append(" order by b.cm_micode,b.cm_codename                          \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        if (SysCd != "" && SysCd != null) pstmt.setString(++parmCnt, SysCd);
	        else if (secuYn.equals("0")) pstmt.setString(++parmCnt, UserId);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmp3500.getRsrcCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getRsrcCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getRsrcCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getRsrcCd() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getRsrcCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcCd() method statement

	public Object[] getRsrcCd_New(String UserId,String SysCd,String Gubun,String StDate,String EdDate) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		//ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		//String            strReqGbn   = "";
		int               parmCnt     = 0;
		boolean           secuYn      = false;
		UserInfo          userinfo    = new UserInfo();
		Object[] returnObjectArray = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			//secuYn = userinfo.getSecuInfo(UserId);
			secuYn = userinfo.isAdmin2(UserId);
			
			strQuery.append("select b.cm_micode,b.cm_codename from cmm0020 b,   \n");
			strQuery.append("     (select b.cr_rsrccd from cmr1010 b,cmr1000 a  \n");
			strQuery.append("       where a.cr_qrycd='04'                       \n");
			if (SysCd != "" && SysCd != null) strQuery.append("and a.cr_syscd=? \n");
			if (!secuYn) {
			   strQuery.append("     and a.cr_syscd in (select distinct cm_syscd from cmm0044     \n");
			   strQuery.append("                         where cm_userid=? and cm_closedt is null) \n");
			}
			strQuery.append("         and a.cr_status<>'3'                      \n");
			if (Gubun.equals("01")) { 
				strQuery.append("     and to_char(a.cr_acptdate,'yyyymm') between ? and ?  \n");
			} else {
				strQuery.append("     and to_char(a.cr_acptdate,'yyyymmdd') between ? and ? \n");
			}
			strQuery.append("       and a.cr_prcdate is not null                \n");
			strQuery.append("       and a.cr_acptno=b.cr_acptno                 \n");
			strQuery.append("       and b.cr_status<>'3'                        \n");
			strQuery.append("       and b.cr_prcdate is not null                \n");
			strQuery.append("       and b.cr_itemid=b.cr_baseitem) a            \n");
			strQuery.append(" where b.cm_macode='JAWON'                         \n");                                 
			strQuery.append("   and b.cm_micode=a.cr_rsrccd                     \n");                               
			strQuery.append(" group by b.cm_micode,b.cm_codename                \n");                          
			strQuery.append(" order by b.cm_micode,b.cm_codename                \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        if (SysCd != "" && SysCd != null) pstmt.setString(++parmCnt, SysCd);
	        if (!secuYn) pstmt.setString(++parmCnt, UserId);
	        pstmt.setString(++parmCnt, StDate);
	        pstmt.setString(++parmCnt, EdDate);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
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
			ecamsLogger.error("## Cmp3500.getRsrcCd_New() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3500.getRsrcCd_New() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3500.getRsrcCd_New() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3500.getRsrcCd_New() Exception END ##");
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
					ecamsLogger.error("## Cmp3500.getRsrcCd_New() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcCd() method statement
	
}//end of Cmp3500 class statement
