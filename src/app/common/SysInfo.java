/*****************************************************************************************
	1. program ID	: SysInfo.java
	2. create date	: 2008.08. 08
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: 시스템정보 조회
*****************************************************************************************/
package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class SysInfo{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 시스템 조회(String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd)
	 * @param  String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getSysInfo(String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int               tstCnt     = 0;
		boolean           strSw      = false;

		try {
			String        strSelMsg   = "";
			String        strSysCd    = "";
			boolean       stopSw      = false;
			boolean       chkSw      = false;
			int           parmCnt = 0;

			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}
			boolean adminYn = false;
			if (UserId != null && UserId != "") {
				UserInfo     userinfo = new UserInfo();
				adminYn = userinfo.isAdmin_conn(UserId,conn);
				if (adminYn) SecuYn = "N";
				userinfo = null;

				if (ReqCd != "" && ReqCd != null) {
					if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("03") || ReqCd.equals("04")  || ReqCd.equals("07") || ReqCd.equals("11") || ReqCd.equals("12")) {
						SecuYn = "Y";
						strQuery.setLength(0);
						strQuery.append("select cr_syscd from cmr1000 a,                     \n");
						strQuery.append("  (select max(cr_acptdate) acptdate from cmr1000    \n");
						strQuery.append("    where cr_editor=?                               \n");
						strQuery.append("      and cr_qrycd in ('01','02','03','04','11')) b \n");
						strQuery.append(" where b.acptdate=a.cr_acptdate                     \n");
						strQuery.append("   and a.cr_editor=?                                \n");
						strQuery.append("   and a.cr_qrycd in ('01','02','03','04','11')     \n");
						pstmt = conn.prepareStatement(strQuery.toString());
			            //pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, UserId);
			            pstmt.setString(2, UserId);

			            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			            rs = pstmt.executeQuery();
			            if (rs.next()) {
			            	strSysCd = rs.getString("cr_syscd");
			            }
			            rs.close();
			            pstmt.close();
					} else if (ReqCd.equals("06") || ReqCd.equals("OPEN")) {
						SecuYn = "Y";
					}
				}

			}

			strQuery.setLength(0);
			strQuery.append("select	a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1, \n");
			strQuery.append("       nvl(a.cm_dirbase,'01') cm_dirbase,             \n");
			strQuery.append("       a.cm_sysinfo,a.cm_systime,                     \n");
			strQuery.append("       LOCALCHK(a.cm_syscd) localyn,                  \n");
			strQuery.append("       sign(nvl(cm_stopst,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff1,\n");
			strQuery.append("       sign(nvl(cm_stoped,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff2 \n");
			strQuery.append("from cmm0030 a				                          \n");

			if (SecuYn.toUpperCase().equals("Y")) {
				strSw = true;
				strQuery.append("where a.cm_syscd in (select cm_syscd from cmm0044 \n");
				strQuery.append("                      where cm_userid=?           \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_closedt is null                      \n");
				}
			} else {
				if (CloseYn.toUpperCase().equals("N")) {
					strSw = true;
					strQuery.append("where a.cm_closedt is null                    \n");
				}
			}
			if (ReqCd != "" && ReqCd != null) {
				if (ReqCd.equals("03")) {
					if (strSw == true) strQuery.append("and ");
					else {
						strSw = true;
						strQuery.append("where ");
					}

					strQuery.append("a.cm_syscd in (select distinct cm_syscd      \n");
					strQuery.append("                 from cmm0031                \n");
					strQuery.append("                where cm_svrcd IN('13','15') \n");
					strQuery.append("                  and cm_closedt is null)    \n");
				} 
			}
			strQuery.append("order by a.cm_sysmsg \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if (SecuYn.toUpperCase().equals("Y")){
            	pstmt.setString(1, UserId);
            }
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
            String tmpSyscd = "";
            String tstInfo = "";
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysfc1","00");
					rst.put("cm_dirbase","00");
					rst.put("cm_sysinfo", "0");
					rst.put("TstSw", "0");
					rst.put("setyn", "N");
					rtList.add(rst);
					rst = null;
				}
				tstCnt = 0;
				tstInfo = rs.getString("cm_sysinfo");
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				tmpSyscd = rs.getString("cm_syscd");
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				rst.put("cm_dirbase",rs.getString("cm_dirbase"));
				rst.put("localyn",rs.getString("localyn"));
				rst.put("websw", "N");
				rst.put("delsw", "Y");
				rst.put("usersw", "Y");

				if(tstInfo.substring(5,6).equals("1")){//SYSINFO[06] 정기배포사용 여부 확인 후 정기적용시간 셋팅
					if(rs.getString("cm_systime") != null && rs.getString("cm_systime") != ""){
						rst.put("cm_systime", rs.getString("cm_systime"));
					} else{
						rst.put("cm_systime", "ERR:설정된 정기적용 시간이 없습니다.[관리자문의]");
					}
				} else{
					rst.put("cm_systime", "");
				}

				stopSw = false;
				if (rs.getString("cm_sysinfo").substring(3,4).equals("1")) {
					if (rs.getInt("diff1")<0 && rs.getInt("diff2")>0 && adminYn == false) {
						rst.put("cm_stopsw", "1");
						tstInfo = tstInfo.substring(0,4) + "1" + tstInfo.substring(5);
						stopSw = true;
					} else {
						rst.put("cm_stopsw", "0");
						tstInfo = tstInfo.substring(0,4) + "0" + tstInfo.substring(5);
					}
				} else{
					rst.put("cm_stopsw", "0");
				}
				if (stopSw == false && adminYn == false) {
					strQuery.setLength(0);
					chkSw = false;
					strQuery.append("select cm_termcd,cm_sttime,cm_edtime,cm_termsub,\n");
					strQuery.append("       to_char(SYSDATE,'d') weekday,            \n");
					strQuery.append("       to_char(SYSDATE,'dd') monday,            \n");
					strQuery.append("       sign(nvl(cm_sttime,to_char(sysdate,'hh24mi')) - to_char(sysdate,'hh24mi')) diff1,\n");
					strQuery.append("       sign(nvl(cm_edtime,to_char(sysdate,'hh24mi')) - to_char(sysdate,'hh24mi')) diff2 \n");
					strQuery.append("  from cmm0330                                 \n");
					strQuery.append(" where cm_syscd=?                              \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, tmpSyscd);
		            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						if (rs2.getString("cm_termcd").equals("1")) {        //매일
							chkSw = true;
						} else if (rs2.getString("cm_termcd").equals("2")) { //매주
							if (rs2.getString("weekday").equals(rs2.getString("cm_termsub"))) {
								chkSw = true;
							}
						} else if (rs2.getString("cm_termcd").equals("3")) { //매월
							if (rs2.getString("monday").equals(rs2.getString("cm_termsub"))) {
								chkSw = true;
							}
						}

						if (chkSw == true) {
							//ecamsLogger.debug("++++check++++++++"+tstInfo);
							if (rs2.getInt("diff1")<0 && rs2.getInt("diff2")>0 && adminYn == false) {
								rst.put("cm_stopsw", "1");
								tstInfo = tstInfo.substring(0,4) + "1" +  tstInfo.substring(5);
							}
							//ecamsLogger.debug("++++check2++++++++"+tstInfo);
						}
					}
					rs2.close();
					pstmt2.close();
				}
				rst.put("cm_sysinfo",tstInfo);
				rst.put("uploadsw", "N");
				if (ReqCd != "" && ReqCd != null && ReqCd.length() > 0) {
					if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("03") || ReqCd.equals("04") || ReqCd.equals("D12")) {
						if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("04") || ReqCd.equals("D12")) {
							tstCnt = 0;
							if (getTstSys_conn(tmpSyscd,conn) > 0) tstCnt = 1;
							rst.put("TstSw", Integer.toString(tstCnt));

							if (tstInfo.substring(3,4).equals("1")) {
								rst.put("uploadsw", "Y");
							}
						} else rst.put("TstSw", "0");
						if (tstCnt == 0) {
							parmCnt = 0;
							strQuery.setLength(0);
							strQuery.append("select '1' as cd,count(*) as cnt from cmm0036  \n");
							strQuery.append(" where cm_syscd=? and cm_closedt is null       \n");
							strQuery.append("   and substr(cm_info,18,1)='1'                \n");
							strQuery.append(" union                                         \n");
							strQuery.append("select '2' as cd,count(*) as cnt from cmm0036  \n");
							strQuery.append(" where cm_syscd=? and cm_closedt is null       \n");
							strQuery.append("   and substr(cm_info,28,1)='1'                \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(++parmCnt, tmpSyscd);
				            pstmt2.setString(++parmCnt, tmpSyscd);
				            rs2 = pstmt2.executeQuery();
							while (rs2.next()){
								if (rs2.getInt("cnt") > 0 && rs2.getString("cd").equals("2"))
									rst.put("websw", "Y");
								else if (rs2.getInt("cnt") == 0 && rs2.getString("cd").equals("1"))
									rst.put("delsw", "N");
							}
							rs2.close();
							pstmt2.close();
						} else {
							parmCnt = 0;
							strQuery.setLength(0);
							strQuery.append("select '1' as cd,count(*) as cnt from cmm0036  \n");
							strQuery.append(" where cm_syscd=? and cm_closedt is null       \n");
							strQuery.append("   and substr(cm_info,18,1)='1'                \n");
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(++parmCnt, tmpSyscd);
				            rs2 = pstmt2.executeQuery();
							if (rs2.next()){
								 if (rs2.getInt("cnt") == 0) rst.put("delsw", "N");
							}
							rs2.close();
							pstmt2.close();
						}
					}else if (ReqCd.equals("OPEN")){
						tstCnt = 0;
						if (getTstSys_conn(rs.getString("cm_syscd"),conn) > 0) tstCnt = 1;
						rst.put("TstSw", Integer.toString(tstCnt));
						//getUserBld
						if (getUserBld(rs.getString("cm_syscd"),conn) > 0) tstCnt = 1;
						else tstCnt = 0;
						rst.put("usersw", Integer.toString(tstCnt));
					}
				} else {
					rst.put("TstSw", "0");
				}
				if (strSysCd != "") {
					if (strSysCd.equals(tmpSyscd)){
						rst.put("setyn","Y");
					} else{
						rst.put("setyn", "N");
					}
				} else{
					rst.put("setyn", "N");
				}
				rtList.add(rst);
				rst = null;
			}

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			pstmt2 = null;
			rs2 = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysInfo() Exception END ##");
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
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement


	/**
	 * @param UserId : 사용자ID
	 * @param SysCd : 시스템코드
	 * @return 0:신청가능 1:신청불가능
	 * @throws SQLException
	 * @throws Exception
	 */
	public int getSysStopChk(String UserId,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int retStr = 0;//0:체크인가능 , 1:체크인통제

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select sign(nvl(cm_stopst,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff1,\n");
			strQuery.append("       sign(nvl(cm_stoped,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff2 \n");
			strQuery.append("  from cmm0030 \n");
			strQuery.append(" where cm_syscd = ? \n");
			strQuery.append("   and substr(cm_sysinfo,5,1) = '1' \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			if (rs.next()){
				UserInfo userinfo = new UserInfo();
				//관리자는 통제 대상 제외
				if (rs.getInt("diff1")<0 && rs.getInt("diff2")>0 && !userinfo.isAdmin_conn(UserId,conn)) {
					retStr = 1;//체크인통제
				} else {
					retStr = 0;//체크인가능
				}
				userinfo = null;
			}

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;
			//ecamsLogger.error("retStr:"+retStr);

			return retStr;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysStopChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysStopChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysStopChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysStopChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysStopChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getSysInfo_bld(String UserId,String SecuYn,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			String        strSelMsg   = "";

			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}
			boolean adminYn = false;
			if (UserId != null && UserId != "") {
				UserInfo     userinfo = new UserInfo();
				adminYn = userinfo.isAdmin(UserId);
				if (adminYn == true) SecuYn = "N";
			}

			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1, \n");
			strQuery.append("       a.cm_dirbase,a.cm_sysinfo,                     \n");
			strQuery.append("       LOCALCHK(a.cm_syscd) localyn,                  \n");
			strQuery.append("       sign(nvl(cm_stopst,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff1,\n");
			strQuery.append("       sign(nvl(cm_stoped,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff2 \n");
			strQuery.append("from cmm0030 a \n");
			strQuery.append("where a.cm_closedt is null                    \n");
			if (SecuYn.toUpperCase().equals("Y")) {
				strQuery.append("and a.cm_syscd in (select cm_syscd from cmm0044 \n");
				strQuery.append("                    where cm_userid=?           \n");
				strQuery.append("                      and cm_closedt is null)   \n");
			}
			strQuery.append("and a.cm_syscd in (select distinct cm_syscd from cmm0036 \n");
			strQuery.append("                    where cm_syscd=a.cm_syscd       \n");
			strQuery.append("                      and cm_closedt is null        \n");
			strQuery.append("                      and substr(cm_info,48,1)='1') \n");
			strQuery.append("order by a.cm_sysmsg \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());

            if (SecuYn.toUpperCase().equals("Y")){
            	pstmt.setString(1, UserId);
            }
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
            //String tmpSyscd = "";
            //String tstInfo = "";
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysfc1","00");
					rst.put("cm_dirbase","00");
					rst.put("cm_sysinfo", "0");
					rst.put("TstSw", "0");
					rst.put("setyn", "N");
					rtList.add(rst);
					rst = null;
				}
				//tstInfo = rs.getString("cm_sysinfo");
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				//tmpSyscd = rs.getString("cm_syscd");
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				rst.put("cm_dirbase",rs.getString("cm_dirbase"));
				rst.put("localyn",rs.getString("localyn"));
				rst.put("websw", "N");
				rst.put("delsw", "Y");
				rst.put("setyn", "N");
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
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo_bld() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysInfo_bld() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo_bld() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysInfo_bld() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysInfo_bld() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo_bld() method statement

	public String getSysOSInfo(String SysCd,String SvrCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retSw       = "";
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select cm_sysos \n");
			strQuery.append("  from cmm0031 \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null  \n");
			strQuery.append("   and cm_svrcd = ? \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, SvrCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if (rs.next()){
				retSw = rs.getString("cm_sysos");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return retSw;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysOSInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysOSInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysOSInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysOSInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysOSInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysOSInfo() method statement

	public boolean getLocalYn(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		boolean           retSw = false;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select count(*) cnt                       \n");
			strQuery.append("  from cmm0036 a                          \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null  \n");
			strQuery.append("   and (substr(cm_info,45,1)='1' or substr(cm_info,38,1)='1') \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getInt("cnt")>0) retSw = true;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return retSw;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getLocalYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getLocalYn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getLocalYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getRsrcInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getLocalYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getLocalYn() method statement

	public ArrayList<HashMap<String,String>> getHomePath_conn(String SysCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery3   = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		try {
			rtList.clear();
			strQuery3.append("select a.cm_volpath,b.cm_rsrccd,b.cm_jobcd       \n");
			strQuery3.append("  from cmm0038 b,cmm0031 a,cmm0030 c             \n");
			strQuery3.append(" where c.cm_syscd=? and c.cm_syscd=a.cm_syscd    \n");
			strQuery3.append("   and nvl(c.cm_dirbase,'01')=a.cm_svrcd         \n");
			strQuery3.append("   and a.cm_closedt is null                      \n");
			strQuery3.append("   and a.cm_syscd=b.cm_syscd                     \n");
			strQuery3.append("   and a.cm_svrcd=b.cm_svrcd                     \n");
			strQuery3.append("   and a.cm_seqno=b.cm_seqno                     \n");

            pstmt3 = conn.prepareStatement(strQuery3.toString());
            pstmt3.setString(1, SysCd);
            rs3 = pstmt3.executeQuery();
			while (rs3.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_rsrccd", rs3.getString("cm_rsrccd"));
				rst.put("cm_volpath", rs3.getString("cm_volpath"));
				rst.put("cm_jobcd", rs3.getString("cm_jobcd"));
				rtList.add(rst);
				rst = null;
			}
			rs3.close();
			pstmt3.close();
			rs3 = null;
			pstmt3 = null;

			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getHomePath_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getHomePath_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getHomePath_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getHomePath_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery3 != null) 	strQuery3 = null;
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}

		}
	}//end of getHomePath_conn() method statement

	public ArrayList<HashMap<String,String>> getSvrHomePath_conn(String UserId,String SysCd,String SvrCd,String SvrSeq,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery3   = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String            sysHome     = "";
		String            userHome    = "";
		String            chgHome     = "";
		int               parmCnt     = 0;
		try {
			rtList.clear();
			UserId = "k"+UserId;
			strQuery3.append("select b.cm_volpath,b.cm_rsrccd,a.cm_svruse,     \n");
			strQuery3.append("       a.cm_volpath volpath,                     \n");
			strQuery3.append("       nvl(a.cm_samedir,a.cm_volpath) cm_samedir \n");
			strQuery3.append("  from cmm0038 b,cmm0031 a                       \n");
			strQuery3.append(" where a.cm_syscd=? and a.cm_svrcd=?             \n");
			if (SvrSeq != null && SvrSeq != "") {
				strQuery3.append("   and a.cm_seqno=?                          \n");
			}
			strQuery3.append("   and a.cm_syscd=b.cm_syscd                     \n");
			strQuery3.append("   and a.cm_svrcd=b.cm_svrcd                     \n");
			strQuery3.append("   and a.cm_seqno=b.cm_seqno                     \n");

            pstmt3 = conn.prepareStatement(strQuery3.toString());
            pstmt3.setString(++parmCnt, SysCd);
            pstmt3.setString(++parmCnt, SvrCd);
            if (SvrSeq != null && SvrSeq != "") {
            	pstmt3.setString(++parmCnt, SvrSeq);
            }
            rs3 = pstmt3.executeQuery();
			while (rs3.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_rsrccd", rs3.getString("cm_rsrccd"));
				rst.put("cm_volpath", rs3.getString("cm_volpath"));
				rst.put("cm_samedir", rs3.getString("cm_samedir"));
				rst.put("pchome", rs3.getString("volpath"));
				if (rs3.getString("cm_svruse").substring(4,5).equals("1")) {
					sysHome = rs3.getString("volpath").replace("/##USER##", "");
					userHome = rs3.getString("volpath").replace("##USER##", UserId);
					rst.put("cm_volpath", rs3.getString("cm_volpath").replace(sysHome, userHome));
					
					chgHome = rs3.getString("cm_volpath").replace(sysHome, rs3.getString("volpath"));
					sysHome = chgHome.replace(rs3.getString("volpath"), rs3.getString("cm_samedir"));
					rst.put("cm_samedir", sysHome.replace("##USER##", UserId));
					rst.put("pchome", sysHome);
				}
				rtList.add(rst);
				rst = null;
			}
			rs3.close();
			pstmt3.close();
			rs3 = null;
			pstmt3 = null;
			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSvrHomePath_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSvrHomePath_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSvrHomePath_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSvrHomePath_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery3 != null) 	strQuery3 = null;
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}

		}
	}//end of getSvrHomePath_conn() method statement
	public ArrayList<HashMap<String,String>> getHomePath_Relat_conn(String SysCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery   = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		try {
			rtList.clear();
			strQuery.setLength(0);
			strQuery.append("select d.cm_syscd,f.cm_sysmsg,b.cm_volpath,e.cm_volpath basehome,b.cm_rsrccd \n");
        	strQuery.append("  from cmm0030 a,cmm0038 b,cmm0031 c,cmm0031 d,cmm0038 e,cmm0030 f \n");
        	strQuery.append(" where a.cm_syscd=?                   \n");
        	strQuery.append("   and a.cm_basesys=f.cm_syscd        \n");
        	strQuery.append("   and a.cm_basesys=c.cm_syscd        \n");
        	strQuery.append("   and c.cm_svrcd='01'                \n");
        	strQuery.append("   and c.cm_closedt is null           \n");
        	strQuery.append("   and c.cm_syscd=b.cm_syscd          \n");
        	strQuery.append("   and c.cm_svrcd=b.cm_svrcd          \n");
        	strQuery.append("   and c.cm_seqno=b.cm_seqno          \n");
        	strQuery.append("   and f.cm_syscd=d.cm_syscd          \n");
        	strQuery.append("   and d.cm_svrcd='01'                \n");
        	strQuery.append("   and d.cm_closedt is null           \n");
        	strQuery.append("   and d.cm_syscd=e.cm_syscd          \n");
        	strQuery.append("   and d.cm_svrcd=e.cm_svrcd          \n");
        	strQuery.append("   and d.cm_seqno=e.cm_seqno          \n");
        	strQuery.append("   and b.cm_rsrccd=e.cm_rsrccd        \n");

            pstmt3 = conn.prepareStatement(strQuery.toString());
            pstmt3.setString(1, SysCd);
            rs3 = pstmt3.executeQuery();
			while (rs3.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_rsrccd", rs3.getString("cm_rsrccd"));
				rst.put("cm_volpath", rs3.getString("cm_volpath"));
				rst.put("basesys", rs3.getString("cm_syscd"));
				rst.put("basehome", rs3.getString("basehome"));
				rst.put("cm_sysmsg", rs3.getString("cm_sysmsg"));
				rtList.add(rst);
				rst = null;
			}
			rs3.close();
			pstmt3.close();
			rs3 = null;
			pstmt3 = null;

			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getHomePath_Relat_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getHomePath_Relat_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getHomePath_Relat_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getHomePath_Relat_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}

		}
	}//end of getHomePath_Relat_conn() method statement
	public Object[] getSysInfo_Rpt(String UserId,String SelMsg,String CloseYn,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		String            strSelMsg   = "";
		boolean           secuYn      = false;
		UserInfo          userinfo    = new UserInfo();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			secuYn = userinfo.isAdmin_conn(UserId, conn);

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1,a.cm_dirbase,a.cm_sysinfo,cm_prjname \n");
			strQuery.append("from cmm0030 a             \n");
			if (SysCd != null && SysCd != "") {
				strQuery.append("where cm_syscd=?       \n");
			} else {
				if (secuYn == false) {
					strQuery.append("where a.cm_syscd in (select cm_syscd from cmm0044 \n");
					strQuery.append("                where cm_userid=?                 \n");
					strQuery.append("                  and cm_closedt is null)         \n");
					if (CloseYn.toUpperCase().equals("N")) {
						strQuery.append("and a.cm_closedt is null                      \n");
					}
				} else {
					if (CloseYn.toUpperCase().equals("N")) {
						strQuery.append("where a.cm_closedt is null                     \n");
					}
				}
			}
			strQuery.append("order by a.cm_sysmsg \n");
            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if (SysCd != null && SysCd != "") pstmt.setString(1, SysCd);
            else
	            if (secuYn == false){
	            	pstmt.setString(1, UserId);
	            }
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysinfo", "0");
					rst.put("cm_prjname", "");
					rtList.add(rst);
					rst = null;
				}
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysinfo",rs.getString("cm_sysinfo"));
				rst.put("cm_prjname", rs.getString("cm_prjname"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo_Rpt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysInfo_Rpt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo_Rpt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysInfo_Rpt() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysInfo_Rpt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo_Rpt() method statement

	public Object[] getSysInfo_Tmax(String UserId,String SecuYn,String SelMsg,String CloseYn,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		String            strSelMsg   = "";
		String            strSysCd    = "";
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int               tstCnt     = 0;
		boolean           strSw      = false;

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			if (UserId != null && UserId != "") {
				UserInfo     userinfo = new UserInfo();
				boolean adminYn = userinfo.isAdmin(UserId);
				if (adminYn == true) SecuYn = "N";

				if (ReqCd != "" && ReqCd != null) {
					if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("03") || ReqCd.equals("04") || ReqCd.equals("11")) {
						strQuery.setLength(0);
						strQuery.append("select cr_syscd from cmr1000 a,                     \n");
						strQuery.append("  (select max(cr_acptdate) acptdate from cmr1000    \n");
						strQuery.append("    where cr_editor=?                               \n");
						strQuery.append("      and cr_qrycd in ('01','02','03','04','11')) b \n");
						strQuery.append(" where b.acptdate=a.cr_acptdate                     \n");
						strQuery.append("   and a.cr_editor=?                                \n");
						strQuery.append("   and a.cr_qrycd in ('01','02','03','04','11')     \n");
						pstmt = conn.prepareStatement(strQuery.toString());
			            //pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, UserId);
			            pstmt.setString(2, UserId);

			            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			            rs = pstmt.executeQuery();
			            if (rs.next()) {
			            	strSysCd = rs.getString("cr_syscd");
			            }
			            rs.close();
			            pstmt.close();
					}
				}

			}

			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysgb,a.cm_sysfc1, \n");
			strQuery.append("       a.cm_dirbase,a.cm_sysinfo,                     \n");
			strQuery.append("       sign(nvl(cm_stopst,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff1,\n");
			strQuery.append("       sign(nvl(cm_stoped,to_char(sysdate,'yyyymmddhh24mi')) - to_char(sysdate,'yyyymmddhh24mi')) diff2 \n");
			strQuery.append("from cmm0030 a \n");
			if (SecuYn.toUpperCase().equals("Y")) {
				strSw = true;
				strQuery.append("where a.cm_syscd in (select cm_syscd from cmm0044 \n");
				strQuery.append("                      where cm_userid=?           \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_closedt is null                      \n");
				}
			} else {
				if (CloseYn.toUpperCase().equals("N")) {
					strSw = true;
					strQuery.append("where a.cm_closedt is null                    \n");
				}
			}
			if (strSw == true) strQuery.append("and ");
			else {
				strSw = true;
				strQuery.append("where ");
			}
			strQuery.append("a.cm_syscd in (select distinct cm_syscd         \n");
			strQuery.append("                 from cmm0031                   \n");
			strQuery.append("                where cm_svrcd in ('T3','T5')   \n");
			strQuery.append("                  and cm_closedt is null)       \n");
			strQuery.append("order by a.cm_sysmsg \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());

            if (SecuYn.toUpperCase().equals("Y")){
            	pstmt.setString(1, UserId);
            }
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg.length() > 0 &&strSelMsg != "" && !strSelMsg.equals("")) {
					rst = new HashMap<String,String>();
					rst.put("ID", "0");
					rst.put("cm_syscd", "00000");
					rst.put("cm_sysmsg", strSelMsg);
					rst.put("cm_sysgb", "0");
					rst.put("cm_sysfc1","00");
					rst.put("cm_dirbase","00");
					rst.put("cm_sysinfo", "0");
					rst.put("TstSw", "0");
					rst.put("setyn", "N");
					rtList.add(rst);
					rst = null;
				}
				tstCnt = 0;
				String tstInfo = rs.getString("cm_sysinfo");
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cm_sysmsg",rs.getString("cm_sysmsg"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cm_sysfc1",rs.getString("cm_sysfc1"));
				rst.put("cm_dirbase",rs.getString("cm_dirbase"));

				if (rs.getString("cm_sysinfo").substring(4,5).equals("1")) {
					if (rs.getInt("diff1")<0 && rs.getInt("diff2")>0) {
						rst.put("cm_stopsw", "1");
						tstInfo = tstInfo.substring(0,4) + "1" + tstInfo.substring(5);
					} else {
						rst.put("cm_stopsw", "0");
						tstInfo = tstInfo.substring(0,4) + "0" + tstInfo.substring(5);
					}
				} else rst.put("cm_stopsw", "0");
				rst.put("cm_sysinfo",tstInfo);
				if (ReqCd != "" && ReqCd != null && ReqCd.length() > 0) {
					if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("04")) {
						//tstCnt = getTstSys(rs.getString("cm_syscd"));
						tstCnt = getTstSys_conn(rs.getString("cm_syscd"),conn);

						if (tstCnt > 0) tstCnt = 1;
						rst.put("TstSw", Integer.toString(tstCnt));
					} else rst.put("TstSw", "0");

					if (tstCnt == 0) {
						strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmm0036            \n");
						strQuery.append(" where cm_syscd=? and cm_closedt is null       \n");
						strQuery.append("   and substr(cm_info,28,1)='1'                \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
			        //    pstmt2 = new LoggableStatement(conn,strQuery.toString());
			            pstmt2.setString(1, rs.getString("cm_syscd"));
			        //    ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			            rs2 = pstmt2.executeQuery();
						if (rs2.next()){
							if (rs2.getInt("cnt") > 0) rst.put("websw", "Y");
							else rst.put("websw", "N");
						} else rst.put("websw", "N");
						rs2.close();
						pstmt2.close();
					} else rst.put("websw", "N");
				} else {
					rst.put("TstSw", "0");
					rst.put("websw", "N");
				}
				if (strSysCd != "") {
					if (strSysCd.equals(rs.getString("cm_syscd"))) rst.put("setyn","Y");
					else rst.put("setyn", "N");
				} else rst.put("setyn", "N");
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

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

	public int getTstSys_conn(String SysCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery3   = new StringBuffer();
		int               Cnt         = 0;

		try {
			strQuery3.append("select count(*) as cnt from cmm0031              \n");
			strQuery3.append(" where cm_syscd=? and cm_closedt is null         \n");
			strQuery3.append("   and cm_svrcd IN('13','15')                    \n");
            pstmt3 = conn.prepareStatement(strQuery3.toString());
            pstmt3.setString(1, SysCd);
            rs3 = pstmt3.executeQuery();
			if (rs3.next()){
				Cnt = rs3.getInt("cnt");
			}
			rs3.close();
			pstmt3.close();
			rs3 = null;
			pstmt3 = null;

			return Cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getTstSys_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getTstSys_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getTstSys_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getTstSys_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery3 != null) 	strQuery3 = null;
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getSysInfo() method statement

	public int getBaseSys_conn(String SysCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt3      = null;
		ResultSet         rs3         = null;
		StringBuffer      strQuery3   = new StringBuffer();
		int               Cnt         = 0;

		try {
			strQuery3.append("select count(*) as cnt from cmm0031              \n");
			strQuery3.append(" where cm_syscd=? and cm_closedt is null         \n");
			strQuery3.append("   and cm_svrcd IN('05')                         \n");
            pstmt3 = conn.prepareStatement(strQuery3.toString());
            pstmt3.setString(1, SysCd);
            rs3 = pstmt3.executeQuery();
			if (rs3.next()){
				Cnt = rs3.getInt("cnt");
			}
			rs3.close();
			pstmt3.close();
			rs3 = null;
			pstmt3 = null;

			return Cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getBaseSys_conn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getBaseSys_conn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getBaseSys_conn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getBaseSys_conn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery3 != null) 	strQuery3 = null;
			if (rs3 != null)     try{rs3.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getBaseSys_conn() method statement
	public int getTstSys(String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               Cnt         = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select count(*) as cnt from cmm0031              \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null         \n");
			strQuery.append("   and cm_svrcd IN('13','15')                    \n");
            pstmt = conn.prepareStatement(strQuery.toString());

           pstmt.setString(1, SysCd);
            rs = pstmt.executeQuery();
			if (rs.next()){
				Cnt = rs.getInt("cnt");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return Cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getSysInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getSysInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getSysInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getSysInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

	public int getUserBld(String SysCd,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               Cnt         = 0;

		try {
			strQuery.append("select count(*) as cnt from cmm0036              \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null         \n");
			strQuery.append("   and substr(cm_info,48,1)='1'                  \n");
            pstmt = conn.prepareStatement(strQuery.toString());

           pstmt.setString(1, SysCd);
            rs = pstmt.executeQuery();
			if (rs.next()){
				Cnt = rs.getInt("cnt");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

			return Cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getUserBld() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getUserBld() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getUserBld() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getUserBld() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of getUserBld() method statement
	public Object[] getJobInfo(String UserID,String SysCd,String SecuYn,String CloseYn,String SelMsg,String sortCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			if (UserID != null && UserID != "") {
				UserInfo     userinfo = new UserInfo();
				boolean adminYn = userinfo.isAdmin(UserID);
				if (adminYn == true) SecuYn = "N";
			}

			strQuery.append("select a.cm_jobcd,a.cm_jobname        \n");
			strQuery.append("from cmm0102 a                        \n");
			if (SecuYn.toUpperCase().equals("Y")) {
				strQuery.append("where a.cm_jobcd in (select cm_jobcd from cmm0044 \n");
				strQuery.append("                      where cm_userid=?           \n");
				strQuery.append("                        and cm_syscd=?            \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_useyn='Y'                            \n");
				}
			} else {
				strQuery.append("where a.cm_jobcd in (select cm_jobcd from cmm0034 \n");
				strQuery.append("                      where cm_syscd=?            \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_useyn='Y'                            \n");
				}
			}
			if (sortCd != null && sortCd != "") {
				if (sortCd.equals("CD")) strQuery.append("order by a.cm_jobcd       \n");
				else strQuery.append("order by a.cm_jobname                         \n");
			} else strQuery.append("order by a.cm_jobname                           \n");


            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if (SecuYn.toUpperCase().equals("Y")) {
            	pstmt.setString(1, UserID);
            	pstmt.setString(2, SysCd);
            } else pstmt.setString(1, SysCd);
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_jobcd", "0000");
				   rst.put("cm_jobname", strSelMsg);
				   rst.put("jobcdname", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("jobcdname", rs.getString("cm_jobname") + "(" + rs.getString("cm_jobcd") + ")");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rtObj =  rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobCd() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getJobCd() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobCd() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getJobCd() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getJobCd() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

	public Object[] getJobInfo_Rpt(String UserID,String SysCd,String CloseYn,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		String            secuYn      = "";
		UserInfo          userinfo    = new UserInfo();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		secuYn = userinfo.getSecuInfo(UserID);
		userinfo = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			strQuery.append("select a.cm_jobcd,a.cm_jobname        \n");
			strQuery.append("from cmm0102 a                        \n");
			if (secuYn.equals("0")) {
				strQuery.append("where a.cm_jobcd in (select cm_jobcd from cmm0044 \n");
				strQuery.append("                      where cm_userid=?           \n");
				strQuery.append("                        and cm_syscd=?            \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_useyn='Y'                      \n");
				}
			} else {
				strQuery.append("where a.cm_jobcd in (select cm_jobcd from cmm0034 \n");
				strQuery.append("                      where cm_syscd=?            \n");
				strQuery.append("                        and cm_closedt is null)   \n");
				if (CloseYn.toUpperCase().equals("N")) {
					strQuery.append("and a.cm_useyn='Y'                    \n");
				}
			}
			strQuery.append("order by a.cm_jobname \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            if (secuYn.equals("0")) {
            	pstmt.setString(1, UserID);
            	pstmt.setString(2, SysCd);
            } else pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_jobcd", "0000");
				   rst.put("cm_jobname", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_jobcd", rs.getString("cm_jobcd"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
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
			rsval.clear();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobInfo_Rpt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getJobInfo_Rpt() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getJobInfo_Rpt() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getJobInfo_Rpt() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getJobInfo_Rpt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

	public Object[] getRsrcInfo(String UserID,String SysCd,String SecuYn,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;


		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}

			strQuery.append("select b.cm_micode,b.cm_codename                            \n");
			strQuery.append("from cmm0036 a,cmm0020 b                                    \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null                \n");
			strQuery.append("   and b.cm_macode='JAWON' and b.cm_micode=a.cm_rsrccd      \n");
			strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037  \n");
			strQuery.append("                            where cm_syscd=?                \n");
			strQuery.append("                              and cm_rsrccd<>cm_samersrc)   \n");
			strQuery.append("order by b.cm_seqno                                         \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, SysCd);

            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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
			rsval.clear();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getRsrcInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getRsrcInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getRsrcInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getRsrcInfo() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getRsrcInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSysInfo() method statement

	public Object[] getsvrInfo(String UserID,String SysCd,String SecuYn,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}
			strQuery.append("select a.cm_svrip,a.cm_portno,a.cm_svrname,a.cm_volpath,\n");
			strQuery.append("       a.cm_svruse,a.cm_svrcd,a.cm_seqno,a.cm_sysos,    \n");
			strQuery.append("       b.cm_sysinfo,a.cm_dir,a.cm_buffsize,             \n");
			strQuery.append("       nvl(a.cm_samedir,a.cm_volpath) cm_samedir        \n");
			strQuery.append("from cmm0031 a,cmm0030 b                                \n");
			strQuery.append("where b.cm_syscd=? and b.cm_syscd=a.cm_syscd            \n");
			strQuery.append("  and a.cm_svrcd in ('01','23')                         \n");
			strQuery.append("  and a.cm_closedt is null                              \n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrname                        \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
           	pstmt.setString(1, SysCd);
           	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("cm_svrip", "");
				   rst.put("cm_portno", "");
				   rst.put("cm_svrname", strSelMsg);
					rst.put("cm_volpath", "");
					rst.put("svrinfo", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_portno", Integer.toString(rs.getInt("cm_portno")));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_seqno", rs.getString("cm_seqno"));

				rst.put("cm_sysos", rs.getString("CM_SYSOS"));
				rst.put("cm_sysinfo", rs.getString("cm_sysinfo"));
				rst.put("cm_dir", rs.getString("cm_dir"));
				rst.put("cm_svruse", rs.getString("cm_svruse"));
				rst.put("cm_buffsize", rs.getString("cm_buffsize"));
				rst.put("cm_samedir", rs.getString("cm_samedir"));
				rst.put("svrinfo", rs.getString("cm_svrname")+"["+rs.getString("cm_svrip")+"]");

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
			rsval.clear();
			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getsvrInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getsvrInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getsvrInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getsvrInfo() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getsvrInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getsvrInfo() method statement
	public Object[] getsvrInfo_doc(String UserID,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.toUpperCase().equals("ALL")){
					strSelMsg = "전체";
				}
				else if (SelMsg.toUpperCase().equals("SEL")){
					strSelMsg = "선택하세요";
				}
				else{
					strSelMsg = "";
				}
			}
			strQuery.append("select a.cm_svrip,a.cm_portno,a.cm_svrname,a.cm_volpath \n");
			strQuery.append("       ,a.cm_svrcd,a.cm_seqno,a.cm_sysos,a.cm_dir,a.cm_buffsize \n");
			strQuery.append("from  cmm0031 a                                         \n");
			strQuery.append("where a.cm_syscd='99999'                                \n");
			strQuery.append("  and a.cm_svrcd='01'                                   \n");
			strQuery.append("  and a.cm_closedt is null                              \n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrname                        \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() ==1 && strSelMsg != "" && !strSelMsg.equals("") && strSelMsg.length() > 0) {
				   rst = new HashMap<String, String>();
				   rst.put("cm_svrip", "");
				   rst.put("cm_portno", "");
				   rst.put("cm_svrname", strSelMsg);
				   rst.put("cm_volpath", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_portno", Integer.toString(rs.getInt("cm_portno")));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_sysos", rs.getString("cm_sysos"));
				rst.put("cm_dir", rs.getString("cm_dir"));
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));
				rst.put("cm_buffsize", rs.getString("cm_buffsize"));
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
			rsval.clear();
			rsval = null;
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SysInfo.getsvrInfo_doc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getsvrInfo_doc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getsvrInfo_doc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getsvrInfo_doc() Exception END ##");
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
					ecamsLogger.error("## SysInfo.getsvrInfo_doc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getsvrInfo_doc() method statement
	
	public Object[] getDownSys(String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval     = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;


		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			UserInfo userInfo = new UserInfo();
			boolean admin = userInfo.isAdmin_conn(UserID, conn);
			
			strQuery.setLength(0);
			strQuery.append("select a.cm_syscd,a.cm_sysmsg,a.cm_sysinfo,b.cm_svrcd,b.cm_seqno,c.cm_ipaddr,c.cm_port, LOCALCHK(a.cm_syscd) localyn  \n");
			strQuery.append("  from cmm0031 b,cmm0030 a, cmm0010 c  							\n");
			strQuery.append(" where a.cm_closedt is null										\n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd										\n");
			strQuery.append("   and b.cm_closedt is null										\n");
			strQuery.append("   and b.cm_svrcd='01'										        \n");
			if(!admin){
				strQuery.append("   and a.cm_syscd in (select distinct cm_syscd from cmm0044	\n");
				strQuery.append("                       where cm_closedt is null	            \n");
				strQuery.append("                         and cm_userid = ?)	                \n");
			}
			strQuery.append(" order by a.cm_sysmsg		                                        \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			if(!admin){
				pstmt.setString(1, UserID);
			}
			
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_syscd", rs.getString("cm_syscd"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_sysinfo", rs.getString("cm_sysinfo"));
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_seqno", rs.getString("cm_seqno"));
				rst.put("cm_svrip", rs.getString("cm_ipaddr"));
				rst.put("cm_portno", rs.getString("cm_port"));
				rst.put("localyn", rs.getString("localyn"));
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
			ecamsLogger.error("## SysInfo.getDownSys() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SysInfo.getDownSys() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SysInfo.getDownSys() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SysInfo.getDownSys() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SysInfo.getDownSys() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDownSys() method statement
	
}//end of SysInfo class statement
