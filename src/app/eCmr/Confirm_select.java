package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
//import org.w3c.dom.Document;

//import app.common.AutoSeq;
//import app.common.CodeInfo;
//import app.common.DeepCopy;
import app.common.LoggableStatement;
//import app.common.UserInfo;
import app.common.TimeSch;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Confirm_select {

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

	public String confSelect_doc(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_gubun,a.cm_rsrccd,a.cm_position        \n");
			strQuery.append("  from cmm0060 a,cmm0040 b                         \n");
			strQuery.append(" where a.cm_syscd=? 							    \n");
			strQuery.append("   and a.cm_reqcd=?                    			\n");
			strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid   \n");
			strQuery.append("   and b.cm_userid=?                               \n");
			if (dataObj.get("CCB_YN").equals("Y")){
	        	strQuery.append(" and (a.cm_rsrccd is null or (a.cm_rsrccd is not null and a.cm_rsrccd='Y')) \n");
			}
	        else{
	        	strQuery.append(" and a.cm_rsrccd is null \n");
	        }
			strQuery.append(" and a.cm_gubun not in ('1','2','P') \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, dataObj.get("SysCd"));
            pstmt.setString(2, dataObj.get("SinCd"));
            pstmt.setString(3, dataObj.get("UserId"));
            rs = pstmt.executeQuery();

            while (rs.next()) {
            	++rsCnt;
            }
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;
            //ecamsLogger.error("++++++++confirm_doc end+++++++");
            return Integer.toString(rsCnt);

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Confirm_select.confSelect_doc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Confirm_select.confSelect_doc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Confirm_select.confSelect_doc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Confirm_select.confSelect_doc() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Confirm_select.confSelect_doc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confSelect() method statement


	public String confirmYN(String ReqCd,String SysCd,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select count(*) cnt        \n");
			strQuery.append("  from cmm0060 a,cmm0040 b \n");
			strQuery.append(" where a.cm_syscd=? 	    \n");
			strQuery.append("   and a.cm_reqcd=?      	\n");
			strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid   \n");
			strQuery.append("   and b.cm_userid=?       \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, ReqCd);
            pstmt.setString(3, UserId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
            	rsCnt = rs.getInt("cnt");
            }
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;
            //ecamsLogger.error("++++++++confirm_doc end+++++++");
            return Integer.toString(rsCnt);

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Confirm_select.confirmYN() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Confirm_select.confirmYN() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Confirm_select.confirmYN() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Confirm_select.confirmYN() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Confirm_select.confirmYN() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of confSelect() method statement
	
	
	/** 결재정보 조회 쿼리
	 * @param etcData
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] Confirm_Info(HashMap<String, String> etcData) throws SQLException, Exception {
    //public Object[] Confirm_Info(String UserId,String SysCd,String ReqCd,ArrayList<String> RsrcCd,ArrayList<String> PgmType,String EmgSw,String PrjNo,String JobCd,ArrayList<String> QryCd) throws SQLException, Exception {
	//public Object[] Confirm_Info(String UserId,String SysCd,String ReqCd,ArrayList<String> RsrcCd,ArrayList<String> JobCd,ArrayList<String> Pos,String EmgSw,String PrjNo,String webYn,ArrayList<String> QryCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		PreparedStatement pstmt3       = null;
		PreparedStatement pstmt4       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		ResultSet         rs3         = null;
		ResultSet         rs4         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<Object, Object>> rsval = new ArrayList<HashMap<Object, Object>>();
		HashMap<Object, Object>			   rst   = null;
		ArrayList<HashMap<String, String>> ArySv = null;
		HashMap<String, String>			   HashSv= null;
		
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			int				  pstmtcount  = 1;
			String            SvLine      = "";
			String            SvTag       = "";
			String            SvUser      = "";
			String            SvSgnName   = "";
			String            SvBaseUser  = "";
			String            svDeptCd    = "";
//			String 			  Strgubun	  = "";
			String            strPos      = "";
			boolean           FindSw      = true;
			boolean           QrySw       = true;
//			boolean           sameSw      = false;
			boolean           sysSw       = false;
			String[]          svRgtCd     = null;
			String[]          svJobCd     = null;
			
			int i = 0;
			int j = 0;
			//parentvar.UserID,parentvar.SysCd,parentvar.ReqCD,parentvar.Rsrccd,parentvar.Type,parentvar.EmgSw,parentvar.PrjNo,parentvar.JobCd,parentvar.QryCd
			String UserId = etcData.get("UserID");
			String SysCd  = etcData.get("SysCd");
			String ReqCd  = etcData.get("ReqCD");
			String RsrcCd = etcData.get("Rsrccd");
			String PgmType = etcData.get("Type");
			String EmgSw  = etcData.get("EmgSw");
			String PrjNo  = etcData.get("PrjNo");
			String JobCd  = etcData.get("JobCd");
			String QryCd  = etcData.get("QryCd");
			String SvrYN = "Y";
			if (etcData.get("svryn") != null) SvrYN  = etcData.get("svryn");
			
			String OutPos  = "S";
			if (etcData.get("OutPos") != null && etcData.get("OutPos") != "") {
				OutPos = etcData.get("OutPos");
			}
			
			if (RsrcCd != null && RsrcCd.length() > 0) {
				strQuery.setLength(0);
				strQuery.append("select distinct cm_samersrc from cmm0037     \n");
				strQuery.append(" where cm_syscd=?                            \n");
				strQuery.append("   and instr(?,cm_rsrccd)>0                  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, SysCd);
				pstmt.setString(2, RsrcCd);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					if (RsrcCd.indexOf(rs.getString("cm_samersrc"))<0) {
						RsrcCd = RsrcCd + "," + rs.getString("cm_samersrc");
					}
				}
				rs.close();
				pstmt.close();
			} else {
				RsrcCd = "";
			}
			
			String[] aRsrcCd = RsrcCd.split(",");
			FindSw = true;
			TimeSch           timesch    = new TimeSch();
			ecamsLogger.error("#####   EmgSw   #####:"+EmgSw);
			String strHoli = timesch.reqTimeGb(conn,EmgSw);
			ecamsLogger.error("#####   strHoli   #####:"+strHoli);
			
			pstmtcount = 1;
			strQuery.setLength(0);
			strQuery.append("select b.cm_username,b.cm_project,a.cm_seqno,a.cm_name,\n");
			strQuery.append("      a.cm_gubun,a.cm_common,a.cm_blank,a.cm_holiday,  \n");
			strQuery.append("      a.cm_emg,a.cm_emg2,a.cm_manid,                   \n");
			strQuery.append("      a.cm_position,a.cm_jobcd,a.cm_rsrccd,a.cm_prcsw, \n");
			strQuery.append("      b.cm_project,nvl(a.cm_orgstep,'N') cm_orgstep    \n");
			strQuery.append(" from cmm0060 a,cmm0040 b                              \n");
			strQuery.append("where a.cm_reqcd=?                                     \n");
			strQuery.append("  and a.cm_syscd=?                                     \n");
			strQuery.append("  and a.cm_manid=decode(b.cm_manid,'Y','1','2')        \n");
			strQuery.append("  and b.cm_userid=?                                    \n");
            strQuery.append("order by a.cm_seqno, a.cm_position                     \n");

            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(pstmtcount++, ReqCd);
            pstmt.setString(pstmtcount++, SysCd);
            pstmt.setString(pstmtcount++, UserId);

            //ecamsLogger.error("==1"+((LoggableStatement)pstmt).getQueryString());
            //ecamsLogger.error("==1" + getQueryString());
            //String app.common.LoggableStatement.getQueryString();
            //flex.messaging.log.Logger.debug(((LoggableStatement)pstmt).getQueryString());
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				svDeptCd = rs.getString("cm_project");
				FindSw = true;
				QrySw = true;
				strPos = rs.getString("cm_position");
				//svUserName = rs.getString("cm_username");

				if (QryCd != null && QryCd.length()>0) {
		            if (QryCd.equals("05")) {
	            		if (rs.getString("cm_gubun").equals("1")) {
	            			if (!rs.getString("cm_jobcd").equals("SYSCL")) QrySw = false;
		            	}
		            } else {
	            		if (rs.getString("cm_gubun").equals("1")) {
	            			if (rs.getString("cm_jobcd").equals("SYSCL")) QrySw = false;
		            	}
		            } 
				}

	            if (aRsrcCd.length > 0 && rs.getString("cm_rsrccd") != null && QrySw == true) {
	            	FindSw = false;
	            	for (i=0;i<aRsrcCd.length;i++){
	            		if (rs.getString("cm_rsrccd").indexOf(aRsrcCd[i]) > -1) {
	            			FindSw = true;
	            			break;
	            		}
	    			}
	            }
	            if (PgmType != null && PgmType != "") {
		           	String[] aPgmType = PgmType.split(",");
		            if (FindSw  == false && QrySw == true && aPgmType.length > 0 && rs.getString("cm_pgmtype") != null) {
		            	FindSw = false;
		            	for (i=0;i<aPgmType.length;i++){
		            		if (rs.getString("cm_pgmtype").indexOf(aPgmType[i]) > -1) {
		            			FindSw = true;
		            			break;
		            		}
		    			}
		            }
	            }
	            //ecamsLogger.error("++++ReqCd["+ReqCd+"] aPgmType["+aPgmType+"] cm_position["+rs.getString("cm_position")+"] FindSw["+FindSw+"]");
	            if (FindSw == true && QrySw == true) {
					SvLine = "";
					SvTag = "";
					SvUser = "";
					SvSgnName = "";
					SvBaseUser = "";

					ArySv = new ArrayList<HashMap<String, String>>();
					FindSw = false;
					if (strHoli.equals("0")) SvLine = rs.getString("cm_common");//정상 업무중
					else if (strHoli.equals("1")) SvLine = rs.getString("cm_emg");//긴급 업무중
					else if (strHoli.equals("3")) SvLine = rs.getString("cm_emg2");//긴급 업무후
					else {//정상 업무후
						SvLine = rs.getString("cm_holiday");
					}
					ecamsLogger.error("#####   SvLine   #####:" + SvLine);

					FindSw = true;
//					sameSw = false;
					if (SvLine != "") {
						//ecamsLogger.error("++++ cm_gubun,strHoli, SvLine++++"+rs.getString("cm_gubun")+", "+strHoli+", "+SvLine);
						if (rs.getString("cm_gubun").equals("3") || rs.getString("cm_gubun").equals("6")) {
							svRgtCd = rs.getString("cm_position").split(",");
							strQuery.setLength(0);
							pstmtcount = 1;
							strQuery.append("select count(*) cnt                                 \n");
							strQuery.append("  from cmm0040 a,cmm0043 b                          \n");
							strQuery.append(" where a.cm_userid=?                                \n");
							strQuery.append("   and a.cm_userid=b.cm_userid                      \n");				
							strQuery.append("   and b.cm_rgtcd in (                        	     \n");
							for (j=0;svRgtCd.length>j;j++) {
								if (j>0) strQuery.append(",? ");
								else strQuery.append("? ");
							}
							strQuery.append(")	   \n");
							//pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt2.setString(pstmtcount++, UserId);
							for (j=0;svRgtCd.length>j;j++) {
				            	pstmt2.setString(pstmtcount++, svRgtCd[j]);
							}
				            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	if (rs2.getInt("cnt") > 0) {
				            		SvLine = "";
				            	}
				            }
				            pstmt2.close();
				            rs2.close();
						} else if (rs.getString("cm_gubun").equals("P")) {
							if (PrjNo == "" || PrjNo == null){
								SvLine = "";
							}
						} else if (rs.getString("cm_gubun").equals("6") || rs.getString("cm_gubun").equals("7")) {
							strQuery.setLength(0);
							pstmtcount = 1;
							strQuery.append("select count(*) as cnt from cmm0043          \n");
							strQuery.append(" where instr(?,cm_rgtcd)>0                   \n");
							//pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(pstmtcount++, strPos);
				            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	if (rs2.getInt("cnt") == 0){
				            		SvLine = "";
				            	}
				            }
				            pstmt2.close();
				            rs2.close();
						} else if (rs.getString("cm_gubun").equals("1")) {
							ecamsLogger.error("SvrYN:"+SvrYN+",sysSw:"+sysSw+",cm_jobcd:"+rs.getString("cm_jobcd"));
							if (SvrYN.equals("N") && sysSw) {
								SvLine = "";
							} else if (rs.getString("cm_jobcd").equals("SYSUP") || rs.getString("cm_jobcd").equals("SYSEUP") || rs.getString("cm_jobcd").equals("SYSPUP")) {
								/* 20140717 neo. 로컬에서 체크인 신청시 SYSUP 결재정보가 셋팅이 안되서 주석처리함.
								//sysSw = true;
								*/
							}
							if (SvLine.length()>0) {
								if (rs.getString("cm_jobcd").equals("SYSEDN") || rs.getString("cm_jobcd").equals("SYSEUP")) {
									SvLine = "";
								} else if (OutPos.equals("R")) {
									if (rs.getString("cm_jobcd").equals("SYSPDN") || rs.getString("cm_jobcd").equals("SYSFMK") ||
									    rs.getString("cm_jobcd").equals("SYSPUP")) {
									    SvLine = "";
									}
								} else if (OutPos.equals("L")) {
									if (ReqCd.equals("01") && !rs.getString("cm_jobcd").equals("SYSPDN") && !rs.getString("cm_jobcd").equals("SYSFMK")) {
										SvLine = "";
									}
								}
							} else {
								continue;
							}
							if (rs.getString("cm_jobcd").equals("SYSPF")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								strQuery.append("   and (substr(cm_info,27,1)='1' or                          \n");
								strQuery.append("        substr(cm_info,4,1)='1' or                           \n");
								strQuery.append("        substr(cm_info,47,1)='1' or                          \n");
								strQuery.append("        substr(cm_info,9,1)='1')                             \n");
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSCB") || rs.getString("cm_jobcd").equals("SYSTCB")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								//컴파일,빌드서버파일Copy
								if (ReqCd.equals("04")) {
									strQuery.append("   and (substr(cm_info,1,1)='1' or substr(cm_info,13,1)='1') \n");
								} else if (ReqCd.equals("03")) {
									strQuery.append("   and (substr(cm_info,61,1)='1' or substr(cm_info,62,1)='1') \n");
								} else {
									strQuery.append("   and (substr(cm_info,39,1)='1' or substr(cm_info,42,1)='1') \n");
								}
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSGB")|| rs.getString("cm_jobcd").equals("SYSTGB")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								//빌드서버에서 체크인
								strQuery.append("   and substr(cm_info,25,1)='1'                              \n");
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSPC")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								//컴파일,빌드서버파일Copy
								strQuery.append("   and substr(cm_info,50,1)='1'                               \n");
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							}  else if (rs.getString("cm_jobcd").equals("SYSUA")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								//컴파일,빌드서버파일Copy
								strQuery.append("   and (substr(cm_info,51,1)='1' or substr(cm_info,49,1)='1')\n");
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSED") || rs.getString("cm_jobcd").equals("SYSTED")) {
//								int k = 0;
								strQuery.setLength(0);
								pstmtcount = 1;
								strQuery.append("select count(*) as cnt from cmm0036       \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null  \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0               \n");
								if (ReqCd.equals("04")) {
									strQuery.append("   and (substr(cm_info,11,1)='1' or   \n");
									strQuery.append("        substr(cm_info,21,1)='1')     \n");
								} else if (ReqCd.equals("03")) {
									strQuery.append("   and (substr(cm_info,63,1)='1' or   \n");
									strQuery.append("        substr(cm_info,64,1)='1')     \n");
								} else if (ReqCd.equals("06")) {//롤백신청일때-운영서버에만 배포하므로. 배포서버 File Copy(운영)[11] 릴리즈스크립트실행(운영)[21]셋팅
									strQuery.append("   and (substr(cm_info,11,1)='1' or   \n");
									strQuery.append("        substr(cm_info,21,1)='1')     \n");
								} else {
									strQuery.append("   and (substr(cm_info,49,1)='1' or   \n");
									strQuery.append("        substr(cm_info,51,1)='1')     \n");
								}
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2.setString(pstmtcount++, SysCd);
								pstmt2.setString(pstmtcount++,RsrcCd);
					            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							}  else if (rs.getString("cm_jobcd").equals("SYSFT")) {
								strQuery.setLength(0);
								pstmtcount = 1;
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								strQuery.append("   and substr(cm_info,28,1)='1'                              \n");
								//pstmt = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(pstmtcount++, SysCd);
								pstmt2.setString(pstmtcount++,RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0) SvLine = "";
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSRK")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								//컴파일,빌드서버파일Copy,빌드서버에서 체크인
								strQuery.append("   and substr(cm_info,6,1)='1'                               \n");
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, SysCd);
								pstmt2.setString(2, RsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							}
						}
				    }
					ecamsLogger.error("++++++++++++gubun,SvLine+++++++++"+rs.getString("cm_gubun")+","+SvLine);
					if (!SvLine.equals("")) {
						if (rs.getString("cm_gubun").equals("1")) {//자동처리
							SvUser = rs.getString("cm_jobcd");
							SvBaseUser =  rs.getString("cm_jobcd");
							SvSgnName = "자동처리";
							if (rs.getString("cm_jobcd").equals("SYSDDN") ||
								rs.getString("cm_jobcd").equals("SYSDUP") ||
							    rs.getString("cm_jobcd").equals("SYSDNC")) {
								if (rs.getString("cm_manid").equals("N")){
									SvTag = "파트너사 " + rs.getString("cm_username");
								}
							    else{
							    	SvTag = "NICE" + rs.getString("cm_username");
							    }
							}
							else {
								strQuery.setLength(0);
								pstmtcount = 1;
								strQuery.append("select cm_codename from cmm0020                         \n");
								strQuery.append(" where cm_macode='SYSGBN' and cm_micode=?               \n");
								//pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2.setString(pstmtcount++, rs.getString("cm_jobcd"));
								ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	SvTag = rs2.getString("cm_codename");
					            }
					            pstmt2.close();
					            rs2.close();
							}
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							FindSw = true;
						} else if (rs.getString("cm_gubun").equals("2")) {//본인확인
							SvTag = rs.getString("cm_username");
							SvUser = UserId;
							SvBaseUser = UserId;
							SvSgnName = "확인";
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							FindSw = true;
						} else if (rs.getString("cm_gubun").equals("C")) {//결재추가
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							SvSgnName = "결재(순차)";
							FindSw = true;
						} else if (rs.getString("cm_gubun").equals("4") || rs.getString("cm_gubun").equals("5")) {//특정팀,처리팀
							SvUser = rs.getString("cm_position");
							SvBaseUser =  rs.getString("cm_position");
							SvTag = rs.getString("cm_name");
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							SvSgnName = "결재(순차)";
							FindSw = true;
						} else if (rs.getString("cm_gubun").equals("8")) {//협조
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							SvSgnName = "참조";
							FindSw = true;
						}
						/*
						else if (rs.getString("cm_gubun").equals("9")){
							SvUser = rs.getString("cm_jobcd");
							SvBaseUser = rs.getString("cm_jobcd");
							strQuery.setLength(0);
							pstmtcount = 1;
							strQuery.append("select cm_deptname from cmm0100              \n");
							strQuery.append(" where cm_deptcd=?                           \n");
							//pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(pstmtcount++, rs.getString("cm_jobcd"));
							////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	SvTag = rs2.getString("cm_deptname");
				            }
				            rs2.close();
				            pstmt2.close();

							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
				            FindSw = true;

						}*/
						else if (rs.getString("cm_gubun").equals("3")){//팀내책임자
							SvUser = "";
							SvBaseUser = "";
							SvTag = "";
							SvSgnName = "결재(순차)";


							FindSw = false;
							svRgtCd = rs.getString("cm_position").split(",");
							strQuery.setLength(0);
							strQuery.append("select distinct a.cm_userid,a.cm_username,a.cm_position,          \n");
							strQuery.append("       a.cm_status,a.cm_duty,a.cm_deptseq, d.cm_codename           \n");
							strQuery.append("  from cmm0040 a,cmm0043 b, cmm0020 d              \n");
							strQuery.append(" where a.cm_active='1'                             \n");
							strQuery.append("   and a.cm_manid='Y'                              \n");
							strQuery.append("   and a.cm_userid<>?                              \n");
							strQuery.append("   and (a.cm_project in (select cm_deptcd          \n");
							strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
							strQuery.append("                         start with cm_deptcd=?     \n");
							strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd) \n");
							strQuery.append("   or a.cm_project2 in (select cm_deptcd            \n");
							strQuery.append("                          from (select * from cmm0100 where cm_useyn='Y') \n");
							strQuery.append("                         start with cm_deptcd=?     \n");
							strQuery.append("                         connect by prior cm_updeptcd=cm_deptcd)) \n");
							strQuery.append("   and a.cm_userid=b.cm_userid                      \n");
							strQuery.append("   and b.cm_rgtcd in (                              \n");
							for (j=0;svRgtCd.length>j;j++) {
								if (j>0) strQuery.append(",? ");
								else strQuery.append("? ");
							}
							strQuery.append(")	   \n");
							strQuery.append("   and d.cm_macode='POSITION'		                 \n");
							strQuery.append("   and d.cm_micode=a.cm_position		             \n");
							strQuery.append("order by a.cm_userid                                \n");
							//pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmtcount = 1;
							pstmt2.setString(pstmtcount++, UserId);
							pstmt2.setString(pstmtcount++, svDeptCd);
							pstmt2.setString(pstmtcount++, svDeptCd);
							for (j=0;svRgtCd.length>j;j++) {
				            	pstmt2.setString(pstmtcount++, svRgtCd[j]);
							}
				            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	if(!UserId.equals(rs2.getString("cm_userid"))){
					            	SvUser = rs2.getString("cm_userid");
					            	SvTag = rs2.getString("cm_username");
					            	SvBaseUser = rs2.getString("cm_userid");
					            	FindSw = true;
				            	}else{
				            		FindSw = false;
				            	}
				            }
				            rs2.close();
				            pstmt2.close();
				            if (FindSw == true) {
					            strQuery.setLength(0);
				            	strQuery.append("select b.cm_username,b.cm_userid,b.cm_duty       \n");
				            	strQuery.append("  from cmm0040 a,cmm0040 b                       \n");
				            	strQuery.append(" where a.cm_userid=?                             \n");
				            	strQuery.append("   and a.cm_blankdts<=to_char(SYSDATE,'yyyymmdd')\n");
				            	strQuery.append("   and a.cm_blankdte>=to_char(SYSDATE,'yyyymmdd')\n");
				            	strQuery.append("   and nvl(a.cm_daegyul,'0000')=b.cm_userid      \n");
				            	//pstmt3 = conn.prepareStatement(strQuery.toString());
				            	pstmt3 = new LoggableStatement(conn,strQuery.toString());
				            	pstmt3.setString(1, SvUser);
				            	ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
				            	rs3 = pstmt3.executeQuery();
				            	if (rs3.next()) {
				            		SvTag = rs3.getString("cm_username");
					            	SvUser = rs3.getString("cm_userid");
				            	}
				            	rs3.close();
				            	pstmt3.close();
				            }				            

				            strQuery.setLength(0);
				            strQuery.append("select cm_codename from cmm0020                      \n");
				            strQuery.append(" where cm_macode='SGNCD' and cm_micode=?             \n");
				            //pstmt2 = conn.prepareStatement(strQuery.toString());
				            pstmt2 = new LoggableStatement(conn,strQuery.toString());
				            pstmt2.setString(1, SvLine);
				            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	SvSgnName = rs2.getString("cm_codename");
				            }
				            rs2.close();
				            pstmt2.close();

				            HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;

						} 
						else if (rs.getString("cm_gubun").equals("6")){//업무책임자
							SvUser = "";
							SvBaseUser = "";
							SvTag = "";
							SvSgnName = "결재(순차)";


							FindSw = false;

							svRgtCd = rs.getString("cm_position").split(",");
							if (JobCd != null && JobCd != "") svJobCd = JobCd.split(",");
							
							strQuery.setLength(0);
							strQuery.append("select distinct a.cm_userid,a.cm_username,          \n");
							strQuery.append("       a.cm_status,a.cm_duty,a.cm_deptseq           \n");
							strQuery.append("  from cmm0040 a,cmm0043 b                          \n");
							strQuery.append(" where a.cm_userid<>?                               \n");
							strQuery.append("   and a.cm_active='1'                              \n");
							strQuery.append("   and a.cm_userid=b.cm_userid                      \n");
							strQuery.append("   and b.cm_rgtcd in (                              \n");
							for (j=0;svRgtCd.length>j;j++) {
								if (j>0) strQuery.append(",? ");
								else strQuery.append("? ");
							}
							strQuery.append(")	   \n");
							if (svJobCd != null && svJobCd.length>0) {
								strQuery.append("and exists (select 1 from cmm0044             \n");
								strQuery.append("             where cm_syscd=?                 \n");
								strQuery.append("               and cm_userid=a.cm_userid      \n");
								strQuery.append("               and cm_closedt is null         \n");
								strQuery.append("               and cm_jobcd in (              \n");
								for (j=0;svJobCd.length>j;j++) {
									if (j>0) strQuery.append(",? ");
									else strQuery.append("? ");
								}
								strQuery.append("))	   \n");
							} else {
								strQuery.append("and exists (select 1 from cmm0044 y,cmm0044 x \n");
								strQuery.append("             where x.cm_userid=?              \n");
								if (SysCd != null && SysCd != "" && !SysCd.equals("99999")) {
									strQuery.append("               and x.cm_syscd=?           \n");
								}
								strQuery.append("               and x.cm_closedt is null       \n");
								strQuery.append("               and x.cm_syscd=y.cm_syscd      \n");
								strQuery.append("               and x.cm_jobcd=y.cm_jobcd      \n");
								strQuery.append("               and y.cm_userid=a.cm_userid    \n");
								strQuery.append("               and y.cm_closedt is null)      \n");
							}
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmtcount = 1;
							pstmt2.setString(pstmtcount++, UserId);
							for (j=0;svRgtCd.length>j;j++) {
				            	pstmt2.setString(pstmtcount++, svRgtCd[j]);
							}
							if (svJobCd != null && svJobCd.length>0) {
				            	pstmt2.setString(pstmtcount++, SysCd);
				            	for (i=0;svJobCd.length>i;i++) {
					            	pstmt2.setString(pstmtcount++, svJobCd[i]);
								}
				            } else {
				            	pstmt2.setString(pstmtcount++, UserId);
				            	if (SysCd != null && SysCd != "" && !SysCd.equals("99999")) 
				            		pstmt2.setString(pstmtcount++, SysCd);
				            }
				            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	SvUser = rs2.getString("cm_userid");
				            	SvTag = rs2.getString("cm_username");
				            	SvBaseUser = rs2.getString("cm_userid");
				            	FindSw = true;
				            }
				            rs2.close();
				            pstmt2.close();
				            if (FindSw == true) {
					            strQuery.setLength(0);
				            	strQuery.append("select b.cm_username,b.cm_userid,b.cm_duty       \n");
				            	strQuery.append("  from cmm0040 a,cmm0040 b                       \n");
				            	strQuery.append(" where a.cm_userid=?                             \n");
				            	strQuery.append("   and a.cm_blankdts<=to_char(SYSDATE,'yyyymmdd')\n");
				            	strQuery.append("   and a.cm_blankdte>=to_char(SYSDATE,'yyyymmdd')\n");
				            	strQuery.append("   and nvl(a.cm_daegyul,'0000')=b.cm_userid      \n");
				            	pstmt3 = conn.prepareStatement(strQuery.toString());
				            	//pstmt3 = new LoggableStatement(conn,strQuery.toString());
				            	pstmt3.setString(1, SvUser);
				            	//ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
				            	rs3 = pstmt3.executeQuery();
				            	if (rs3.next()) {
				            		SvTag = rs3.getString("cm_username");
					            	SvUser = rs3.getString("cm_userid");
				            	}
				            	rs3.close();
				            	pstmt3.close();
				            }				            

				            strQuery.setLength(0);
				            strQuery.append("select cm_codename from cmm0020                      \n");
				            strQuery.append(" where cm_macode='SGNCD' and cm_micode=?             \n");

				            pstmt2 = conn.prepareStatement(strQuery.toString());
				            pstmt2 = new LoggableStatement(conn,strQuery.toString());
				            pstmt2.setString(1, SvLine);
				            ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	SvSgnName = rs2.getString("cm_codename");
				            }
				            rs2.close();
				            pstmt2.close();

				            HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;

						} else if (rs.getString("cm_gubun").equals("P")) {//지정승인(ISR관련)

						} else if (rs.getString("cm_gubun").equals("7")) {//관리자

			            	strQuery.setLength(0);
							strQuery.append("select a.cm_userid,a.cm_username,a.cm_status, \n");
							strQuery.append("       a.cm_position,b.cm_codename,a.cm_duty  \n");
							strQuery.append("  from cmm0043 c, cmm0020 b, cmm0040 a where  \n");
							if (!rs.getString("cm_gubun").equals("5")){
								strQuery.append("a.cm_project=? and                        \n");
							}
							strQuery.append("    a.cm_active='1'                                        \n");
							strQuery.append("and b.cm_macode='POSITION' and a.cm_position=b.cm_micode   \n");
							strQuery.append("and a.cm_userid = c.cm_userid                              \n");
							strQuery.append("and instr(?,c.cm_rgtcd)>0                                  \n");
							strQuery.append("order by a.cm_position desc,a.cm_userid desc               \n");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmtcount = 1;
				            if (!rs.getString("cm_gubun").equals("5")){
				            	pstmt2.setString(pstmtcount++, rs.getString("cm_project"));
				            }
				            pstmt2.setString(pstmtcount++, strPos);
							ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
							rs2 = pstmt2.executeQuery();
			                while (rs2.next()){
			                	SvBaseUser = rs2.getString("cm_userid");
			                	strQuery.setLength(0);
			                	pstmtcount = 1;
								strQuery.append("select cm_daegyul from cmm0040                             \n");
								strQuery.append(" where cm_userid=?                                         \n");
								strQuery.append("   and cm_blankdts is not null                             \n");
								strQuery.append("   and cm_blankdte is not null                             \n");
								strQuery.append("   and cm_blankdts<=to_char(sysdate,'yyyymmdd')            \n");
								strQuery.append("   and cm_blankdte>=to_char(sysdate,'yyyymmdd')            \n");

								pstmt3 = conn.prepareStatement(strQuery.toString());
								pstmt3 = new LoggableStatement(conn,strQuery.toString());
								pstmt3.setString(pstmtcount++, rs2.getString("cm_userid"));
					            ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
					            rs3 = pstmt3.executeQuery();
					            if (rs3.next()) {
					            	if (!rs2.getString("cm_userid").equals(rs3.getString("cm_daegyul"))) {
					            		strQuery.setLength(0);
										strQuery.append("select a.cm_username,a.cm_duty,b.cm_codename               \n");
										strQuery.append("  from cmm0040 a,cmm0020 b                                 \n");
										strQuery.append(" where a.cm_userid=?                                       \n");
										strQuery.append("   and b.cm_macode='POSITION'                              \n");
										strQuery.append("   and b.cm_micode=a.cm_position                           \n");

							            pstmt4 = conn.prepareStatement(strQuery.toString());
							            pstmt4 = new LoggableStatement(conn,strQuery.toString());
							            pstmt4.setString(1, rs3.getString("cm_daegyul"));
							            ecamsLogger.error(((LoggableStatement)pstmt4).getQueryString());
							            rs4 = pstmt4.executeQuery();
							            if (rs4.next()) {
							            	SvTag = rs4.getString("cm_username");
							            	SvUser = rs4.getString("cm_daegyul");
							            }

							            rs4.close();
							            pstmt4.close();

					            	}
					            } else {
					            	SvTag = rs2.getString("cm_username");
					            	SvUser = rs2.getString("cm_userid");
					            }
					            rs3.close();
					            pstmt3.close();

					            strQuery.setLength(0);
					            strQuery.append("select cm_codename from cmm0020                      \n");
					            strQuery.append(" where cm_macode='SGNCD' and cm_micode=?             \n");

					            pstmt3 = conn.prepareStatement(strQuery.toString());
					            pstmt3 = new LoggableStatement(conn,strQuery.toString());
					            pstmt3.setString(1, SvLine);
					            ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
					            rs3 = pstmt3.executeQuery();
					            if (rs3.next()) {
					            	SvSgnName = rs3.getString("cm_codename");
					            }
					            rs3.close();
					            pstmt3.close();

								HashSv = new HashMap<String, String>();
								HashSv.put("SvUser", SvUser);
								HashSv.put("SvTag", SvTag);
								ArySv.add(HashSv);
								HashSv = null;
					            FindSw = true;

				            }

			                rs2.close();
			                pstmt2.close();

			                if (ArySv.size() == 0){
			                	throw new Exception("결재가능한 사용자가 없습니다. ["+ rs.getString("cm_name") + "]");
				            }
						}
					}
//					Strgubun ="";
					ecamsLogger.error("FindSw:"+FindSw+",SvLine:"+SvLine);
					if (FindSw == true && SvLine != "" && SvLine != null) {
						if (rs.getString("cm_gubun").equals("C") ||
							rs.getString("cm_gubun").equals("3") ||
							rs.getString("cm_gubun").equals("6")) {
							strQuery.setLength(0);
				            strQuery.append("select cm_codename from cmm0020                      \n");
				            strQuery.append(" where cm_macode='SGNCD' and cm_micode=?             \n");

				            pstmt3 = conn.prepareStatement(strQuery.toString());
				            //pstmt3 = new LoggableStatement(conn,strQuery.toString());
				            pstmt3.setString(1, SvLine);
				            ////ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
				            rs3 = pstmt3.executeQuery();
				            if (rs3.next()) {
				            	SvSgnName = rs3.getString("cm_codename");
				            }
				            rs3.close();
				            pstmt3.close();
						}
						rst = new HashMap<Object, Object>();
						rst.put("cm_name", rs.getString("cm_name"));
						rst.put("arysv", ArySv);
						rst.put("cm_sgnname", SvSgnName);
						rst.put("cm_baseuser", SvBaseUser);
						rst.put("cm_congbn", SvLine);
						rst.put("cm_emg2", rs.getString("cm_emg2"));
						rst.put("cm_orgstep", rs.getString("cm_orgstep"));
						rst.put("cm_prcsw", rs.getString("cm_prcsw"));
						rst.put("cm_position", strPos);;
						rst.put("samesw", "0");
						rst.put("cm_gubun", rs.getString("cm_gubun"));
//						Strgubun =  rs.getString("cm_gubun");
						rst.put("cm_common", rs.getString("cm_common"));
						rst.put("cm_blank", rs.getString("cm_blank"));
						rst.put("cm_holi", rs.getString("cm_holiday"));
						rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
						rst.put("cm_emg", rs.getString("cm_emg"));
						rst.put("delyn", "N");
						if (rs.getString("cm_gubun").equals("3") || rs.getString("cm_gubun").equals("C") || rs.getString("cm_gubun").equals("4")
						    || rs.getString("cm_gubun").equals("5") || rs.getString("cm_gubun").equals("6")
						    || rs.getString("cm_gubun").equals("7") || rs.getString("cm_gubun").equals("8")) {
							rst.put("cm_duty", strPos);
						} else rst.put("cm_duty", rs.getString("cm_jobcd"));
						rst.put("cm_seqno", "0");
						if (rs.getString("cm_gubun").equals("3") || rs.getString("cm_gubun").equals("C")){
							rst.put("userSetable", true);
						}
						else{
							rst.put("userSetable", false);
						}
						if (rs.getString("cm_gubun").equals("8")) rst.put("visible", "1");
						else if ((rs.getString("cm_gubun").equals("3") || rs.getString("cm_gubun").equals("6")) && 
								  rs.getString("cm_orgstep").equals("Y")) {
							rst.put("visible", "1");
						} else {
							rst.put("visible", "0");
						}
						rsval.add(rst);
						rst = null;
					}
	            }
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			rs3 = null;
			pstmt3 = null;
			conn = null;
			
//			ecamsLogger.error("Confirm_Info:"+rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Confirm_select.Confirm_Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Confirm_select.Confirm_Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Confirm_select.Confirm_Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Confirm_select.Confirm_Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex4){ex4.printStackTrace();}
			if (rs3 != null)     try{rs3.close();}catch (Exception ex5){ex5.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex6){ex6.printStackTrace();}
			if (rs4 != null)     try{rs4.close();}catch (Exception ex7){ex7.printStackTrace();}
			if (pstmt4 != null)  try{pstmt4.close();}catch (Exception ex8){ex8.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex9){
					ecamsLogger.error("## Confirm_select.Confirm_Info() connection release exception ##");
					ex9.printStackTrace();
				}
			}
		}
	}//end of Confirm_Info() method statement

}//end of Confirm_select class statement
