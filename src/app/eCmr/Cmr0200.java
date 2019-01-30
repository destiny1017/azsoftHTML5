/*****************************************************************************************
	1. program ID	: Cmr0200.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
*****************************************************************************************/
package app.eCmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.Holiday_Check;
import app.common.LoggableStatement;
import app.common.SysInfo;
import app.common.SystemPath;
import app.common.UserInfo;
import app.common.eCAMSInfo;
import app.eCmd.Cmd0100;
import app.thread.ThreadPool;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0200{


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
    public String bldcdChk(String SysCd,String JobCd,String RsrcCd,String rsrcInfo,String QryCd,String ReqCd,
    		String ItemId,ArrayList<HashMap<String,Object>> ConfList,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErrMsg   = null;
		int               cnt         = 0;
		int               i           = 0;

		try {
			cnt = 0;
			//ecamsLogger.error("+++++++ RsrcCd,QryCd,ReqCd ++++++++"+RsrcCd+", "+QryCd+", "+ReqCd);
			String strRsrc = RsrcCd;
			String strCmInfo = rsrcInfo;
			if (QryCd.equals("16")) {
				strQuery.setLength(0);
	    		strQuery.append("select cm_info from cmm0036             \n");
	    		strQuery.append(" where cm_syscd=? and cm_rsrccd=?       \n");
	    		pstmt = conn.prepareStatement(strQuery.toString());
	    		pstmt.setString(1, SysCd);
	    		pstmt.setString(2, RsrcCd);
	    		rs = pstmt.executeQuery();
	    		if (rs.next()) {
	    			strCmInfo = rs.getString("cm_info");
	    		}
	    		rs.close();
	    		pstmt.close();
			}

			strQuery.setLength(0);
    		strQuery.append("select a.cm_info,a.cm_rsrccd            \n");
    		strQuery.append("  from cmm0036 a,cmm0037 b              \n");
    		strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?   \n");
    		strQuery.append("   and b.cm_syscd=a.cm_syscd            \n");
    		strQuery.append("   and b.cm_samersrc=a.cm_rsrccd        \n");
    		strQuery.append("   and b.cm_samersrc<>b.cm_rsrccd        \n");
    		strQuery.append("   and a.cm_closedt is null             \n");
    		pstmt = conn.prepareStatement(strQuery.toString());
    		pstmt.setString(1, SysCd);
    		pstmt.setString(2, RsrcCd);
    		rs = pstmt.executeQuery();
    		while (rs.next()) {
    			++i;
    			strRsrc = strRsrc + ","+ rs.getString("cm_rsrccd");
    			strCmInfo = strCmInfo + "," + rs.getString("cm_info");
    		}
    		rs.close();
    		pstmt.close();

    		String strRsrcCd[] = strRsrc.split(",");
			String strInfo[] = strCmInfo.split(",");
			String strPrcSys[] = strRsrc.split(",");
			int j = 0;
			int k = 0;
			boolean findSw = false;
			String confTeam = "";
			ArrayList<HashMap<String,Object>>	rData2 = null;

    		for (i=0;strRsrcCd.length>i;i++) {
    			if (strInfo[i].substring(7,8).equals("1") ||   //파일삭제(개발서버)
    				strInfo[i].substring(1,3).equals("10") ||   //체크아웃
					strInfo[i].substring(13,14).equals("1") || //체크아웃스크립트실행
					strInfo[i].substring(21,22).equals("1") || //형상관리저장스크립트실행
		        	strInfo[i].substring(23,24).equals("1") || //개발서버에서 체크인
		        	strInfo[i].substring(26,27).equals("1")) { //개발툴연계
    				findSw = false;
    				if (QryCd.equals("01") || QryCd.equals("02")) {
    					if (strInfo[i].substring(1,2).equals("1") || strInfo[i].substring(13,14).equals("1")) {
    						for (k=0;ConfList.size()>k;k++) {
		    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
		    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
		        					confTeam = (String) rData2.get(0).get("SvUser");
		        					rData2 = null;
		        					if (confTeam.equals("SYSDN")) {
		        						findSw = true;
		        						break;
		        					}
		    					}
    						}
    					}
    				} else if (!ReqCd.equals("05")) {
    					if (strInfo[i].substring(21,22).equals("1") || strInfo[i].substring(23,24).equals("1")) {
    						for (k=0;ConfList.size()>k;k++) {
		    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
		    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
		        					confTeam = (String) rData2.get(0).get("SvUser");
		        					rData2 = null;
		        					if (confTeam.equals("SYSUP")) {
		        						findSw = true;
		        						break;
		        					}
		    					}
    						}
    					} else if (strInfo[i].substring(7,8).equals("1")) findSw = true;
    				}

    				if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='01'        \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());

			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);

			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "체크아웃서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
    				}
				}
				if (strErrMsg == null &&
			        QryCd.equals("04") &&			
					(strInfo[i].substring(0,1).equals("1") ||   //컴파일
					strInfo[i].substring(6,7).equals("1") ||   //파일삭제(컴파일)
					strInfo[i].substring(12,13).equals("1") || //빌드서버FileCopy
					strInfo[i].substring(24,25).equals("1"))) { //빌드서버체크인
					findSw  = false;
					if (!ReqCd.equals("05")) {
    					if (strInfo[i].substring(0,1).equals("1") || strInfo[i].substring(12,13).equals("1") ||
    					    strInfo[i].substring(24,25).equals("1")) {
    						for (k=0;ConfList.size()>k;k++) {
		    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
		    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
		        					confTeam = (String) rData2.get(0).get("SvUser");
		        					rData2 = null;
		        					if (confTeam.equals("SYSFT") && strInfo[i].substring(27,28).equals("1")) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		        					if (confTeam.equals("SYSAC") && strInfo[i].substring(43,44).equals("1")) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		        					if (confTeam.equals("SYSCB") &&
		        						(strInfo[i].substring(0,1).equals("1") || strInfo[i].substring(12,13).equals("1") ||
		        						 strInfo[i].substring(24,25).equals("1"))) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		    					}
    						}
    					} else if (strInfo[i].substring(6,7).equals("1")) findSw = true;
    				}
					if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='03'           \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	//pstmt = new LoggableStatement(conn,strQuery.toString());

			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);
			        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "빌드서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
					}
				}
				if (strErrMsg == null &&
					QryCd.equals("04") &&
					(strInfo[i].substring(10,11).equals("1") ||   //운영서버FileCopy
					strInfo[i].substring(20,21).equals("1") ||   //릴리즈스크립트실행
					strInfo[i].substring(34,35).equals("1"))) {   //적용스크립트실행

					findSw  = false;
					if (!ReqCd.equals("05")) {
						for (k=0;ConfList.size()>k;k++) {
	    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
	    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
	        					confTeam = (String) rData2.get(0).get("SvUser");
	        					rData2 = null;
	        					if (confTeam.equals("SYSRC") && strInfo[i].substring(34,35).equals("1")) {
	        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
	        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
	        						} else findSw = true;
	        						break;
	        					}
	        					if (confTeam.equals("SYSED") &&
	        						(strInfo[i].substring(10,11).equals("1") ||   //운영서버FileCopy
	        						 strInfo[i].substring(20,21).equals("1"))) {
	        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
	        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
	        						} else findSw = true;
	        						break;
	        					}
	    					}
						}
    				}
					if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='05'        \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	//pstmt = new LoggableStatement(conn,strQuery.toString());
			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);
			        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "배포서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
					}
				}

				if (strErrMsg == null &&
			        QryCd.equals("07") &&			
					(strInfo[i].substring(38,39).equals("1") || //컴파일(개발)
					strInfo[i].substring(6,7).equals("1") ||   //파일삭제(컴파일)
					strInfo[i].substring(41,42).equals("1") || //빌드서버FileCopy
					strInfo[i].substring(24,25).equals("1"))) { //빌드서버체크인
					findSw  = false;
					if (!ReqCd.equals("05")) {
    					if (strInfo[i].substring(38,39).equals("1") || strInfo[i].substring(41,42).equals("1") ||
    					    strInfo[i].substring(24,25).equals("1")) {
    						for (k=0;ConfList.size()>k;k++) {
		    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
		    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
		        					confTeam = (String) rData2.get(0).get("SvUser");
		        					rData2 = null;
		        					if (confTeam.equals("SYSFT") && strInfo[i].substring(27,28).equals("1")) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		        					if (confTeam.equals("SYSAC") && strInfo[i].substring(43,44).equals("1")) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		        					if (confTeam.equals("SYSCB") &&
		        						(strInfo[i].substring(38,39).equals("1") || strInfo[i].substring(27,28).equals("1") ||
		        						 strInfo[i].substring(24,25).equals("1"))) {
		        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
		        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
		        						} else findSw = true;
		        						break;
		        					}
		    					}
    						}
    					} else if (strInfo[i].substring(6,7).equals("1")) findSw = true;
    				}
					if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='23'           \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	//pstmt = new LoggableStatement(conn,strQuery.toString());

			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);
			        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "개발빌드서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
					}
				}
				if (strErrMsg == null &&
					QryCd.equals("07") &&
					(strInfo[i].substring(48,49).equals("1") ||   //운영서버FileCopy
					strInfo[i].substring(50,51).equals("1") ||   //릴리즈스크립트실행
					strInfo[i].substring(34,35).equals("1"))) {   //적용스크립트실행

					findSw  = false;
					if (!ReqCd.equals("05")) {
						for (k=0;ConfList.size()>k;k++) {
	    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
	    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
	        					confTeam = (String) rData2.get(0).get("SvUser");
	        					rData2 = null;
	        					if (confTeam.equals("SYSRC") && strInfo[i].substring(34,35).equals("1")) {
	        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
	        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
	        						} else findSw = true;
	        						break;
	        					}
	        					if (confTeam.equals("SYSED") &&
	        						(strInfo[i].substring(48,49).equals("1") ||   //운영서버FileCopy
	        						 strInfo[i].substring(50,51).equals("1"))) {
	        						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
	        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
	        						} else findSw = true;
	        						break;
	        					}
	    					}
						}
    				}
					if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='25'        \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	//pstmt = new LoggableStatement(conn,strQuery.toString());
			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);
			        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "개발배포서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
					}
				}
				if (strErrMsg == null &&
					strInfo[i].substring(39,40).equals("1")) {   //사후처리쉘실행
					findSw  = false;
					if (QryCd.equals("01") || QryCd.equals("02") || QryCd.equals("03") || QryCd.equals("07")) findSw = false;
					else if (!ReqCd.equals("05")) {
						for (k=0;ConfList.size()>k;k++) {
	    					if (ConfList.get(k).get("cm_gubun").equals("1")) {
	    						rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
	        					confTeam = (String) rData2.get(0).get("SvUser");
	        					rData2 = null;
	        					if (confTeam.equals("SYSAR")) {
	        						findSw = true;
	        						break;
	        					}
	    					}
						}
    				}

					if ( findSw ) {
			        	strQuery.setLength(0);
			        	strQuery.append("select count(*) cnt from cmm0038 b,cmm0031 a   \n");
			        	strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='09'        \n");
			        	strQuery.append("   and a.cm_closedt is null                    \n");
			        	strQuery.append("   and a.cm_syscd=b.cm_syscd                   \n");
			        	strQuery.append("   and a.cm_svrcd=b.cm_svrcd                   \n");
			        	strQuery.append("   and a.cm_seqno=b.cm_seqno                   \n");
			        	strQuery.append("   and b.cm_rsrccd=?                           \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	//pstmt = new LoggableStatement(conn,strQuery.toString());
			        	pstmt.setString(1, SysCd);
			        	pstmt.setString(2, strRsrcCd[i]);
			        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        	rs = pstmt.executeQuery();
			        	if (rs.next()){
			        		if (rs.getInt("cnt") == 0) {
			        			strErrMsg = "최종소스보관서버가 연결되어 있지 않습니다. [관리자 연락요망]. RsrcCd="+strRsrcCd[i];
			        			break;
			        		}
			        	}
			        	rs.close();
			        	pstmt.close();
					}
				}
    		}

    		if (strErrMsg == null) {
	    		strQuery.setLength(0);
				strQuery.append("select a.cm_rsrccd,a.cm_prcsys          \n");
				strQuery.append("  from cmm0033 a,cmm0030 b              \n");
				strQuery.append(" where b.cm_syscd= ?                    \n");
				strQuery.append("   and b.cm_syscd=a.cm_syscd            \n");
				strQuery.append("   and a.cm_qrycd=?                     \n");
				if (ReqCd.equals("05"))
					strQuery.append("and a.cm_prcsys='SYSDEL'            \n");
				strQuery.append("   and cm_jobcd=decode(substr(b.cm_sysinfo,8,1),'1',?,'****')\n");
				strQuery.append("   and cm_rsrccd in ( 				     \n");
				for (i=0;strRsrcCd.length>i;i++) {
					if (i>0) strQuery.append(", ? ");
					else strQuery.append("? ");
				}
			    strQuery.append(")                                       \n");
			    strQuery.append("order by a.cm_rsrccd                    \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            //pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(++cnt, SysCd);
	            pstmt.setString(++cnt, QryCd);
	            pstmt.setString(++cnt, JobCd);
	            for (i=0;strRsrcCd.length>i;i++) {
					pstmt.setString(++cnt, strRsrcCd[i]);
				}
	            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					for (i=0;strPrcSys.length>i;i++) {
						if (strPrcSys[i].substring(0,2).equals(rs.getString("cm_rsrccd"))) {
							strPrcSys[i] = strPrcSys[i] + "," + rs.getString("cm_prcsys");
						}
					}
				}//end of while-loop statement
				rs.close();
				pstmt.close();

				if (strErrMsg == null) {
					for (i=0;strRsrcCd.length>i;i++) {
						findSw = false;
						if (QryCd.equals("01") || QryCd.equals("02") || QryCd.equals("11") || QryCd.equals("04") || QryCd.equals("07")) {
							if (strInfo[i].substring(5,6).equals("1")) {   //LOCK관리
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSRK")) {
				    						findSw = true;
				    						break;
				    					}
									}
								}
								if (findSw == true) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "LOCK관리를 위한 스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "LOCK관리를 위한 스크립트정보가 등록되지 않았습니다.2";
										break;
									}
								}
							}
						}
						if (QryCd.equals("01") || QryCd.equals("02")) {
							if (strInfo[i].substring(13,14).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSDN")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "체크아웃스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "체크아웃스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
						} else if (!ReqCd.equals("05")) {
							if (strInfo[i].substring(27,28).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSFT")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "웹취약성스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "웹취약성스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (strInfo[i].substring(21,22).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSUP")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "형상관리저장스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "형상관리저장스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (QryCd.equals("04") && strInfo[i].substring(0,1).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSCB")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
											break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									if (strInfo[i].substring(47,48).equals("1")) {
										strQuery.setLength(0);
										strQuery.append("select count(*) cnt from cmr0026          \n");
										strQuery.append(" where cr_itemid=? and cr_prcsys='SYSCB'  \n");
										pstmt = conn.prepareStatement(strQuery.toString());
										pstmt.setString(1, ItemId);
										rs = pstmt.executeQuery();
										if (rs.next()) {
											if (rs.getInt("cnt")>0) findSw = true;
										}
										rs.close();
										pstmt.close();
									} else {
										for (j=0;strPrcSys.length>j;j++) {
											//ecamsLogger.error("++++ prcsys ++++"+strPrcSys[j]);
											//ecamsLogger.error("++++ strRsrcCd ++++"+strRsrcCd[i]);
											if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
												findSw = true;
												if (strPrcSys[j].indexOf(confTeam)<0) {
													strErrMsg = "실행 할 빌드스크립트정보가 등록되지 않았습니다.1";
													break;
												} else break;
											}
										}
									}
									if (findSw == false) {
										strErrMsg = "실행 할 빌드스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (QryCd.equals("04") && strInfo[i].substring(20,21).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSED")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									if (strInfo[i].substring(47,48).equals("1")) {
										strQuery.setLength(0);
										strQuery.append("select count(*) cnt from cmr0026         \n");
										strQuery.append(" where cr_itemid=? and cr_prcsys='SYSED' \n");
										pstmt = conn.prepareStatement(strQuery.toString());
										pstmt.setString(1, ItemId);
										rs = pstmt.executeQuery();
										if (rs.next()) {
											if (rs.getInt("cnt")>0) findSw = true;
										}
										rs.close();
										pstmt.close();
									} else {
										for (j=0;strPrcSys.length>j;j++) {
											if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
												findSw = true;
												if (strPrcSys[j].indexOf(confTeam)<0) {
													strErrMsg = "실행 할 배포스크립트정보가 등록되지 않았습니다.1";
													break;
												} else break;
											}
										}
									}
									if (findSw == false) {
										strErrMsg = "실행 할 배포스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (strInfo[i].substring(34,35).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSRC")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "실행 할 적용스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "실행 할 적용스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (strInfo[i].substring(39,40).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSAR")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "사후처리스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "사후처리스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
							if (strInfo[i].substring(16,17).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSCN")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									for (j=0;strPrcSys.length>j;j++) {
										if (strPrcSys[j].substring(0,2).equals(strRsrcCd[i])) {
											findSw = true;
											if (strPrcSys[j].indexOf(confTeam)<0) {
												strErrMsg = "체크인취소시실행할 스크립트정보가 등록되지 않았습니다.1";
												break;
											} else break;
										}
									}
									if (findSw == false) {
										strErrMsg = "체크인취소시실행할 스크립트정보가 등록되지 않았습니다.2";
									}
								}
							}
						} else if (ReqCd.equals("05")) {             //폐기
							if (strInfo[i].substring(17,18).equals("1")) {
								findSw = false;
								for (k=0;ConfList.size()>k;k++) {
									if (ConfList.get(k).get("cm_gubun").equals("1")) {
										rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(k).get("arysv");
				    					confTeam = (String) rData2.get(0).get("SvUser");
				    					rData2 = null;
				    					if (confTeam.equals("SYSED")) {
				    						if (ConfList.get(k).get("cm_rsrccd") != null && ConfList.get(k).get("cm_rsrccd") != "") {
			        							if (ConfList.get(k).get("cm_rsrccd").toString().indexOf(strRsrcCd[i])>=0) findSw = true;
			        						} else findSw = true;
				    						break;
				    					}
									}
								}
								if ( findSw ) {
									findSw = false;
									if (strPrcSys[i].indexOf("SYSDEL")<0) {
										strErrMsg = "파일삭제용 스크립트정보가 등록되지 않았습니다.1";
									} else findSw = true;
								}
							}
						}
						if (strErrMsg != null) break;
					}
				}
    		}

			return strErrMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.bldcdChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.bldcdChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.bldcdChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.bldcdChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}

	public Object[] getFileList_excel(ArrayList<HashMap<String,String>> fileList,String UserId,String SysCd,String SinCd,String TstSw,String SysInfo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		int				  filecnt = 0;
		int               cnt = 0;
		int               i = 0;
		int               j = 0;
		String            svAcpt = "";
		String			  strRsrcCd = "";
		String            strRsrc[] = null;

		try {

			conn = connectionContext.getConnection();

			rsval.clear();
			int allCd = 0;

			//UserInfo     userinfo = new UserInfo();
			boolean adminYn = false;//userinfo.isAdmin(UserId);
			//userinfo = null;

			if (TstSw.equals("1") && SinCd.equals("04")) allCd = 1;
			else allCd = 9;

			strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			strQuery.append("   and substr(cm_info, 2, 1)='1'                  \n");
			strQuery.append("   and substr(cm_info, 26, 1)='0'                 \n");
			strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?)       \n");
			strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	if (strRsrcCd != "") strRsrcCd = strRsrcCd + ",";
            	strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd");
            }
			rs.close();
			pstmt.close();

			strRsrc = strRsrcCd.split(",");

			for (i=0;i<fileList.size();i++){
				filecnt = 0;
				cnt = 0;
				if (allCd == 1 || allCd == 9) {
					strQuery.setLength(0);
					strQuery.append("select /*+ ALL ROWS */                                              \n");
					strQuery.append("       a.cr_rsrcname,a.cr_lstver,a.cr_langcd,a.cr_syscd,a.cr_dsncd, \n");
				    strQuery.append("       a.cr_itemid,a.cr_rsrccd,b.cr_acptno,a.cr_story,b.cr_jobcd,   \n");
				    strQuery.append("       b.cr_baseno,b.cr_editcon,a.cr_status,c.cm_dirpath,i.cm_info, \n");
				    strQuery.append("       lpad(i.cm_stepsta,4,'0') prcreq,nvl(i.cm_vercnt,9999) vercnt, \n");
				    strQuery.append("       d.cm_codename,e.cm_codename jawon,b.cr_editor,h.cm_username, \n");
				    strQuery.append("       f.cm_jobname,to_char(a.cr_lastdate,'yyyy/mm/dd') lastdate    \n");
					strQuery.append("  from cmm0102 f,cmm0040 h,cmr1000 g,cmm0020 e,cmr0020 a,cmr1010 b, \n");
					strQuery.append("       cmm0070 c,cmm0020 d,cmm0036 i                                \n");
					if (adminYn == false && SysInfo.substring(2,3).equals("1")) {
						strQuery.append(", cmm0044 k                              \n");
					}
					strQuery.append(" where a.cr_syscd=? and a.cr_rsrcname=?                             \n");
					if (adminYn == false) {
						if (SysInfo.substring(2,3).equals("1")) {
							strQuery.append("and a.cr_jobcd=k.cm_jobcd and a.cr_syscd=k.cm_syscd  and a.cr_editor=k.cm_userid \n");
						} else strQuery.append("and a.cr_editor=?                                        \n");
					} else strQuery.append("and a.cr_editor=?                                            \n");
					strQuery.append("and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd               \n");
					if (SinCd.equals("03") || (SinCd.equals("04") && TstSw.equals("0"))) {
						strQuery.append(" and a.cr_status in ('5','B')                                   \n");
					} else {
						strQuery.append(" and a.cr_status='B'                                           \n");
					}
					strQuery.append("and b.cr_confno is null and b.cr_status in ('8','9')                \n");
					strQuery.append("and b.cr_acptno=g.cr_acptno                                         \n");
					if (SinCd.equals("03") || (SinCd.equals("04") && TstSw.equals("0")))
						strQuery.append("and g.cr_qrycd in('01','02')                                    \n");
					else {
						strQuery.append("and b.cr_qrycd in ('03','04')                                   \n");
					}
					strQuery.append("and a.cr_itemid=b.cr_itemid and b.cr_itemid=b.cr_baseitem          \n");
					strQuery.append("and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd                \n");
					strQuery.append("and d.cm_macode='CMR0020' and a.cr_status=d.cm_micode              \n");
					strQuery.append("and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode                \n");
					strQuery.append("and b.cr_editor=h.cm_userid and b.cr_jobcd=f.cm_jobcd              \n");
					strQuery.append("and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd              \n");
					strQuery.append("and i.cm_closedt is null                                           \n");

					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());

				    pstmt.setString(++cnt, SysCd);
			        pstmt.setString(++cnt, fileList.get(i).get("cr_rsrcname"));
		            if (adminYn == false) {
					 	if (SysInfo.substring(2,3).equals("1")) {
							//pstmt.setString(++cnt, UserId);
						} else pstmt.setString(++cnt, UserId);
				    } else pstmt.setString(++cnt, UserId);

					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();
					while (rs.next()){
						++filecnt;
						if (filecnt==2) {
							rst = new HashMap<String, String>();
						    rst = rsval.get(rsval.size()-1);
							rst.put("errmsg", "파일중복");
							rsval.set(rsval.size()-1, rst);
							rst = null;
						}
						rst = new HashMap<String, String>();
						rst.put("ID", Integer.toString(rs.getRow()));
						if (rs.getString("cr_acptno") != null) {
							if (!svAcpt.equals(rs.getString("cr_acptno"))) {
							   rst.put("acptno", rs.getString("cr_acptno").substring(2,4) +
									    "-" + rs.getString("cr_acptno").substring(4,6) +
									    "-" + rs.getString("cr_acptno").substring(6,12));
							   svAcpt = rs.getString("cr_acptno");
							} else {
							   rst.put("acptno", "");
							}
						} else {
							rst.put("acptno", "");
						}
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("jawon", rs.getString("jawon"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
						else  rst.put("cr_story", "");
						rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
						rst.put("cr_editor", rs.getString("cr_editor"));
						rst.put("cm_codename", rs.getString("cm_codename"));
						rst.put("cr_syscd", rs.getString("cr_syscd"));
						rst.put("cr_dsncd", rs.getString("cr_dsncd"));
						rst.put("cr_itemid", rs.getString("cr_itemid"));
						rst.put("baseitem", rs.getString("cr_itemid"));
						rst.put("cr_jobcd", rs.getString("cr_jobcd"));
						rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
						if (rs.getInt("cr_lstver") == 0) rst.put("reqcd", "03");
						else rst.put("reqcd", "04");
						rst.put("cr_acptno", rs.getString("cr_acptno"));
						rst.put("cm_username", rs.getString("cm_username"));
						rst.put("cm_jobname", rs.getString("cm_jobname"));
						rst.put("cr_lastdate", rs.getString("lastdate"));
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("prcseq", rs.getString("prcreq"));
						if (rs.getString("cr_baseno") != null) rst.put("cr_baseno", rs.getString("cr_baseno"));
						else rst.put("cr_baseno", "");

						if (rs.getString("cr_editcon") != null) {
						   rst.put("cr_sayu", rs.getString("cr_editcon"));
						} else rst.put("cr_sayu", "");
						if (rs.getInt("vercnt") == 0) {
							if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
							else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						} else {
							if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
								rst.put("cr_aftver", "1");
							} else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						}
						if (filecnt>1) rst.put("errmsg", "파일중복");
						else rst.put("errmsg", "정상");

						rst.put("selected_flag","0");
						rsval.add(rst);
						rst = null;
					}//end of while-loop statement
					rs.close();
					pstmt.close();
				}

				cnt = 0;
	            if (allCd == 2 || allCd == 9) {
	            	//if (allCd == 9) strQuery.append("union                                               \n");
					strQuery.setLength(0);
					strQuery.append("select /*+ ALL ROWS */                                              \n");
	            	strQuery.append("       a.cr_rsrcname,a.cr_lstver,a.cr_langcd,a.cr_syscd,a.cr_dsncd, \n");
				    strQuery.append("       a.cr_itemid,a.cr_rsrccd,'' cr_acptno,a.cr_story,a.cr_jobcd,  \n");
				    strQuery.append("       a.cr_acptno cr_baseno,                          \n");
				    strQuery.append("       '' cr_editcon,a.cr_status,c.cm_dirpath,i.cm_info,            \n");
				    strQuery.append("       lpad(i.cm_stepsta,4,'0') prcreq,nvl(i.cm_vercnt,50) vercnt,  \n");
				    strQuery.append("       d.cm_codename,e.cm_codename jawon,a.cr_editor,f.cm_username, \n");
				    strQuery.append("       b.cm_jobname,to_char(a.cr_lastdate,'yyyy/mm/dd') lastdate    \n");
					strQuery.append("  from cmm0040 f,cmm0102 b,cmm0020 e,cmr0020 a,cmm0070 c,cmm0020 d,cmm0036 i \n");
					if (adminYn == false) {
						strQuery.append(", cmm0044 h                              \n");
					}
					strQuery.append(" where a.cr_syscd=? and a.cr_rsrcname=?                             \n");
					strQuery.append(" and a.cr_lstver=0 and (a.cr_status='3' or                          \n");
					strQuery.append("     (a.cr_status='B' and a.cr_editor=?))                           \n");
					if (adminYn == false) {
						strQuery.append("and a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd and a.cr_editor=? and a.cr_editor=h.cm_userid \n");
					}
					strQuery.append("and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd                \n");
					strQuery.append("and d.cm_macode='CMR0020' and a.cr_status=d.cm_micode              \n");
					strQuery.append("and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode                \n");
					strQuery.append("and nvl(a.cr_lstusr,a.cr_editor)=f.cm_userid                       \n");
					strQuery.append("and a.cr_jobcd=b.cm_jobcd                                          \n");
					strQuery.append("and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd              \n");
					strQuery.append("and a.cr_rsrccd in (");
					for (j=0;strRsrc.length>j;j++) {
						if (j>0) strQuery.append(", ? ");
						else strQuery.append("? ");
					}
					strQuery.append(")                                \n");

					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());

			        pstmt.setString(++cnt, SysCd);
			        pstmt.setString(++cnt, fileList.get(i).get("cr_rsrcname"));
			        pstmt.setString(++cnt, UserId);
			        if (adminYn == false) pstmt.setString(++cnt, UserId);
					for (j=0;strRsrc.length>j;j++) {
	            		pstmt.setString(++cnt, strRsrc[j]);
	            	}
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();

					while (rs.next()){
						++filecnt;
						if (filecnt==2) {
							rst = new HashMap<String, String>();
						    rst = rsval.get(rsval.size()-1);
							rst.put("errmsg", "파일중복");
							rsval.set(rsval.size()-1, rst);
							rst = null;
						}
						rst = new HashMap<String, String>();
						rst.put("ID", Integer.toString(rs.getRow()));
						if (rs.getString("cr_acptno") != null) {
							if (!svAcpt.equals(rs.getString("cr_acptno"))) {
							   rst.put("acptno", rs.getString("cr_acptno").substring(2,4) +
									    "-" + rs.getString("cr_acptno").substring(4,6) +
									    "-" + rs.getString("cr_acptno").substring(6,12));
							   svAcpt = rs.getString("cr_acptno");
							} else {
							   rst.put("acptno", "");
							}
						} else {
							rst.put("acptno", "");
						}
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("jawon", rs.getString("jawon"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
						else  rst.put("cr_story", "");
						rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
						rst.put("cr_editor", rs.getString("cr_editor"));
						rst.put("cm_codename", rs.getString("cm_codename"));
						rst.put("cr_syscd", rs.getString("cr_syscd"));
						rst.put("cr_dsncd", rs.getString("cr_dsncd"));
						rst.put("cr_itemid", rs.getString("cr_itemid"));
						rst.put("cr_jobcd", rs.getString("cr_jobcd"));
						rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
						rst.put("cr_acptno", rs.getString("cr_acptno"));
						rst.put("cm_username", rs.getString("cm_username"));
						rst.put("cm_jobname", rs.getString("cm_jobname"));
						rst.put("cr_lastdate", rs.getString("lastdate"));
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("prcseq", rs.getString("prcreq"));
						if (rs.getString("cr_baseno") != null) rst.put("cr_baseno", rs.getString("cr_baseno"));
						else rst.put("cr_baseno", "");

						if (rs.getString("cr_editcon") != null) {
						   rst.put("cr_sayu", rs.getString("cr_editcon"));
						} else rst.put("cr_sayu", "");
						if (rs.getInt("vercnt") == 0) {
							if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
							else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						} else {
							if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
								rst.put("cr_aftver", "1");
							} else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						}
						if (rs.getInt("cr_lstver") > 0) rst.put("reqcd", "04");
						else rst.put("reqcd", "03");
						if (filecnt>1) rst.put("errmsg", "파일중복");
						else rst.put("errmsg", "정상");
						rst.put("selected_flag","0");

						rsval.add(rst);
						rst = null;
					}//end of while-loop statement
					rs.close();
					pstmt.close();
	            }
			}

			boolean findSw = false;

			for (i=0;fileList.size()>i;i++) {
				findSw = false;
				for (j=0;rsval.size()>j;j++) {
					if (fileList.get(i).get("cr_rsrcname").equals(rsval.get(j).get("cr_rsrcname"))) {
						findSw = true;
						break;
					}
				}
				if (findSw == false) {
					rst = new HashMap<String,String>();
					rst.put("linenum",Integer.toString(i));
					rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
					strQuery.setLength(0);
					strQuery.append("select a.cr_rsrcname,a.cr_lstver,a.cr_syscd,a.cr_dsncd, \n");
				    strQuery.append("       a.cr_itemid,a.cr_rsrccd,a.cr_story,a.cr_jobcd,               \n");
				    strQuery.append("       a.cr_acptno cr_baseno,a.cr_status,c.cm_dirpath,              \n");
				    strQuery.append("       d.cm_codename,e.cm_codename jawon,a.cr_editor,f.cm_username, \n");
				    strQuery.append("       b.cm_jobname,to_char(a.cr_lastdate,'yyyy/mm/dd') lastdate    \n");
					strQuery.append("  from cmm0040 f,cmm0102 b,cmm0020 e,cmr0020 a,cmm0070 c,cmm0020 d  \n");
					strQuery.append(" where a.cr_syscd=? and a.cr_rsrcname=?                             \n");
					strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd              \n");
					strQuery.append("   and d.cm_macode='CMR0020' and a.cr_status=d.cm_micode            \n");
					strQuery.append("   and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode              \n");
					strQuery.append("   and nvl(a.cr_lstusr,a.cr_editor)=f.cm_userid                     \n");
					strQuery.append("   and a.cr_jobcd=b.cm_jobcd                                        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, SysCd);
					pstmt.setString(2, fileList.get(i).get("cr_rsrcname"));
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rst.put("jawon", rs.getString("jawon"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
						else  rst.put("cr_story", "");
						rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
						rst.put("cm_codename", rs.getString("cm_codename"));
						rst.put("cr_syscd", rs.getString("cr_syscd"));
						rst.put("cr_dsncd", rs.getString("cr_dsncd"));
						rst.put("cr_itemid", rs.getString("cr_itemid"));
						rst.put("cr_jobcd", rs.getString("cr_jobcd"));
						rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
						rst.put("cm_username", rs.getString("cm_username"));
						rst.put("cm_jobname", rs.getString("cm_jobname"));
						rst.put("cr_lastdate", rs.getString("lastdate"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("errmsg", rs.getString("cm_codename"));
						rst.put("selected_flag","0");
					} else {
						rst.put("errmsg", "원장없음");
						rst.put("cr_rsrccd", "");
						rst.put("selected_flag","0");
					}
					rs.close();
					pstmt.close();

					rsval.add(rst);
					rst = null;
				}
			}
			conn.close();
			conn = null;
			/*
			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			*/
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getFileList_excel() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getFileList_excel() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getFileList_excel() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getFileList_excel() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			//if (rsval != null) rsval=null;
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getFileList_excel() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	/**
	 *
	 * <PRE>
	 * 1. MethodName	: getReqList
	 * 2. ClassName		: Cmr0200
	 * 3. Commnet			: 체크인(테스트적용)화면에서 대상리스트 조회
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 1. 오후 3:32:18
	 * </PRE>
	 * 		@return Object[]
	 * 		@param UserId : 로그인사용자사번
	 * 		@param SysCd : 시스템코드
	 * 		@param SinCd
	 * 		@param ReqCd
	 * 		@param TstSw
	 * 		@param RsrcName
	 * 		@param DsnCd
	 * 		@param DirPath
	 * 		@param SysInfo
	 * 		@param RsrcCd
	 * 		@param UpLowSw
	 * 		@param selfSw
	 * 		@param txtISRInfo
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Object[] getReqList(HashMap<String,String> etcData) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               cnt         = 0;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		String			  szDsnCD = "";
		String			  strRsrcCd = "";
//		String            strChgPath = "";
		String            strRsrc[] = null;
		int               i = 0;
		boolean           srSw = false;
		ArrayList<HashMap<String, String>> svrList = new ArrayList<HashMap<String, String>>();

		try {

			conn = connectionContext.getConnection();

			String strVolPath = "";
			String strDirPath = "";
			String strDevHome = "";
			if (etcData.get("srid") != null && etcData.get("srid") != "") srSw = true;
			
			SysInfo sysinfo = new SysInfo();
			if (sysinfo.getLocalYn(etcData.get("SysCd"))) {
				strQuery.setLength(0);
				strQuery.append("select cd_devhome from cmd0300               \n");
				strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("SysCd"));
				pstmt.setString(2, etcData.get("UserId"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					strDevHome = rs.getString("cd_devhome");
				}
				rs.close();
				pstmt.close();

				svrList = sysinfo.getHomePath_conn(etcData.get("SysCd"), conn);
			}
			sysinfo = null;

            if (etcData.get("RsrcCd") == null || etcData.get("RsrcCd").equals("") || etcData.get("RsrcCd").equals("0000")) etcData.put("RsrcCd", "00");
            //ecamsLogger.error("++++++++++RsrcCd+++++++++"+RsrcCd);
			
            strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			if (etcData.get("SinCd").equals("05") && etcData.get("ReqCd").equals("05")) {
				strQuery.append("   and substr(cm_info,18, 1)='1'              \n");
			} else if (etcData.get("ReqCd").equals("09")) {
				strQuery.append("  and  (substr(cm_info, 1, 1)='1'             \n");
				strQuery.append("   or  substr(cm_info, 15, 1)='1')            \n");
			} else {
				strQuery.append("   and substr(cm_info, 2, 1)='1'              \n");
				strQuery.append("   and substr(cm_info, 26, 1)='0'             \n");
			}
			strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?        \n");
			strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, etcData.get("SysCd"));
            pstmt.setString(2, etcData.get("SysCd"));
            if (etcData.get("SinCd").equals("05") && etcData.get("ReqCd").equals("05"))
            	pstmt.setString(3, etcData.get("SysCd"));
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	if (strRsrcCd == ""){
            		strRsrcCd = rs.getString("cm_rsrccd") + ",";
            	} else{
            		strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd") + ",";
            	}
            }
			rs.close();
			pstmt.close();

			
			if (strRsrcCd == ""){
				rs = null;
				pstmt = null;
	        	if (conn != null){
	        		conn.close();
	        		conn = null;
	        	}
				throw new Exception("형상관리 대상 프로그램이 없습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
			//ecamsLogger.error("strRsrcCd 1:"+strRsrcCd);
			if(strRsrcCd.substring(strRsrcCd.length()-1).indexOf(",")>-1){
				strRsrcCd = strRsrcCd.substring(0,strRsrcCd.length()-1);
			}
			strRsrc = strRsrcCd.split(",");
			//ecamsLogger.error("strRsrcCd 2:"+strRsrcCd);

			if (!etcData.get("DsnCd").equals("")){
				if (etcData.get("DsnCd").length() == 7 && etcData.get("DsnCd").indexOf("/") < 0){
					szDsnCD = etcData.get("DsnCd");
				}
			}
			cnt = 0;
            
			strQuery.setLength(0);
			strQuery.append("select /*+ ALL ROWS */                                              \n");
        	strQuery.append("       a.cr_rsrcname,a.cr_lstver,a.cr_syscd,a.cr_dsncd,             \n");
		    strQuery.append("       a.cr_itemid,a.cr_rsrccd,'' cr_acptno,a.cr_story,a.cr_jobcd,  \n");
		    strQuery.append("       to_char(nvl(a.cr_lstdat,a.cr_lastdate),'yyyy/mm/dd hh24:mi') enddate, \n");
		    strQuery.append("       a.cr_status,c.cm_dirpath,i.cm_info,a.cr_ckoutacpt,           \n");
		    strQuery.append("       lpad(i.cm_stepsta,4,'0') prcreq,nvl(i.cm_vercnt,50) vercnt,  \n");
		    strQuery.append("       d.cm_codename,e.cm_codename jawon,a.cr_editor,f.cm_username, \n");
		    strQuery.append("       b.cm_jobname,to_char(a.cr_lastdate,'yyyy/mm/dd') lastdate,   \n");
		    strQuery.append("       g.cm_codename checkin                                        \n");
			strQuery.append("  from cmm0040 f,cmm0102 b,cmm0020 e,cmr0020 a,cmm0070 c,cmm0020 d, \n");
			strQuery.append("       cmm0036 i,cmm0020 g,cmm0044 h \n");
			strQuery.append(" where a.cr_syscd=?                          \n");
			if (srSw) {
				strQuery.append("and a.cr_isrid=?                         \n");
			}
			if (etcData.get("RsrcName") != null && etcData.get("RsrcName") != "") {
				if ( (etcData.get("RsrcCd") != null) && !etcData.get("RsrcCd").equals("00") && !etcData.get("RsrcCd").equals("0000") ) strQuery.append("and a.cr_rsrccd=?      \n");
				strQuery.append("and ( upper(a.cr_rsrcname) like upper(?) or upper(a.cr_story) like upper(?) ) \n");
			} else if ( (etcData.get("RsrcCd") != null) && !etcData.get("RsrcCd").equals("00") && !etcData.get("RsrcCd").equals("0000") && !etcData.get("RsrcCd").equals("ID"))
				    strQuery.append("and a.cr_rsrccd=?                                          \n");

			//05:폐기 또는 09:무수정
			if (etcData.get("ReqCd").equals("05") || etcData.get("ReqCd").equals("09")) {
				strQuery.append(" and a.cr_lstver>0 and a.cr_status='0'                         \n");
				if (etcData.get("ReqCd").equals("09"))
					strQuery.append("and nvl(a.cr_nomodify,'0')='0'                             \n");
			} else {
				//20140718 neo.
				//변경전:strQuery.append(" and decode(a.cr_lstver,0,a.cr_editor,nvl(a.cr_ckoutuser,a.cr_devusr))=? \n");
				strQuery.append(" and a.cr_editor = ? \n");
				 if(etcData.get("ReqCd").equals("03")){//신규
					 strQuery.append("  and a.cr_status = '3'				                    \n"); 
				 }else if(etcData.get("ReqCd").equals("04")){//수정
					 strQuery.append("  and a.cr_status in ('B','D','5','G')                    \n"); 
				 }else{//신규+수정 99
					 strQuery.append("  and a.cr_status in ('3','B','D','5','G')                \n"); 
				 }
			}
			
			strQuery.append("and h.cm_syscd=? and h.cm_userid=? and h.cm_closedt is null    \n");
			strQuery.append("and h.cm_syscd=a.cr_syscd and h.cm_jobcd=a.cr_jobcd            \n");

			if (etcData.get("DsnCd") != "") {
				if (etcData.get("DsnCd").substring(0,1).equals("F")) {
					strQuery.append(" and c.cm_dirpath like ?                                   \n");
				} else strQuery.append(" and a.cr_dsncd=?                                       \n");
			}
			strQuery.append("and g.cm_macode='CHECKIN' and g.cm_micode=decode(?,'05','05',decode(a.cr_lstver,0,'03','04')) \n");
			strQuery.append("and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd                \n");
			strQuery.append("and d.cm_macode='CMR0020' and a.cr_status=d.cm_micode              \n");
			strQuery.append("and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode                \n");
			strQuery.append("and nvl(a.cr_lstusr,a.cr_editor)=f.cm_userid                       \n");
			strQuery.append("and a.cr_jobcd=b.cm_jobcd                                          \n");
			strQuery.append("and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd              \n");
			if (etcData.get("RsrcCd").equals("00") || etcData.get("RsrcCd").equals("0000")) {
				strQuery.append("and a.cr_rsrccd in (");
				for (i=0;strRsrc.length>i;i++) {
					if (i==0) strQuery.append("?");
					else strQuery.append(",?");
				}
				strQuery.append(") \n");
			}
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++cnt, etcData.get("SysCd"));
			if (srSw) pstmt.setString(++cnt, etcData.get("srid")); 
			if (etcData.get("RsrcName") != null && etcData.get("RsrcName") != "") {
	    	   if ( (etcData.get("RsrcCd") != null) && !etcData.get("RsrcCd").equals("00") && !etcData.get("RsrcCd").equals("0000") ) pstmt.setString(++cnt, etcData.get("RsrcCd"));
	    	   pstmt.setString(++cnt, "%" + etcData.get("RsrcName") + "%");
	    	   pstmt.setString(++cnt, "%" + etcData.get("RsrcName") + "%");
			} else if ( (etcData.get("RsrcCd") != null) && !etcData.get("RsrcCd").equals("00") && !etcData.get("RsrcCd").equals("0000") ) pstmt.setString(++cnt, etcData.get("RsrcCd"));
			if (!etcData.get("ReqCd").equals("05") && !etcData.get("ReqCd").equals("09")) pstmt.setString(++cnt, etcData.get("UserId"));
			
			pstmt.setString(++cnt, etcData.get("SysCd"));
			pstmt.setString(++cnt, etcData.get("UserId"));
			
			if (etcData.get("DsnCd") != "") {
				if (etcData.get("DsnCd").substring(0,1).equals("F")) {
					pstmt.setString(++cnt, etcData.get("DsnCd").substring(1) + "%");
				} else pstmt.setString(++cnt, szDsnCD);
			}
			pstmt.setString(++cnt,etcData.get("ReqCd"));
			if ( etcData.get("RsrcCd").equals("00") || etcData.get("RsrcCd").equals("0000") ) {
				for (i=0 ; i<strRsrc.length ; i++) {
            		pstmt.setString(++cnt, strRsrc[i]);
            	}
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("acptno", rs.getString("cr_ckoutacpt"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("jawon", rs.getString("jawon"));

				if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
				else  rst.put("cr_story", "");
				rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
				rst.put("cr_editor", rs.getString("cr_editor"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cr_syscd", rs.getString("cr_syscd"));
				rst.put("cr_dsncd", rs.getString("cr_dsncd"));
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("baseitem", rs.getString("cr_itemid"));
				rst.put("cr_jobcd", rs.getString("cr_jobcd"));
				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
				rst.put("reqcd", etcData.get("ReqCd"));
				rst.put("cr_acptno", "");
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("cr_lastdate", rs.getString("lastdate"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("prcseq", rs.getString("prcreq"));
				rst.put("enddate", rs.getString("enddate"));
				rst.put("sortgbn", "0");

				if (rs.getInt("vercnt") == 0) {
					if (etcData.get("ReqCd").equals("05") || etcData.get("ReqCd").equals("09")) rst.put("cr_aftver", rs.getString("cr_lstver"));
					else if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
				} else {
					if (etcData.get("ReqCd").equals("05") || etcData.get("ReqCd").equals("09")) rst.put("cr_aftver", rs.getString("cr_lstver"));
					else if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
						rst.put("cr_aftver", "1");
					} else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
				}
				if (etcData.get("ReqCd").equals("05")) rst.put("reqcd", etcData.get("ReqCd"));
				else {
					if (rs.getInt("cr_lstver")>0) rst.put("reqcd", "04");
					else rst.put("reqcd", "03");
				}
				rst.put("checkin", rs.getString("checkin"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("selected_flag","0");
				if (rs.getString("cm_info").substring(44,45).equals("1")) {
					if (strDevHome != null && strDevHome != "") {
						for (i=0 ; svrList.size()>i ; i++) {
							if (svrList.get(i).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
								strVolPath = svrList.get(i).get("cm_volpath");
								strDirPath = rs.getString("cm_dirpath");
								if (strVolPath != null && strVolPath != "") {
									if (strDirPath.length()>=strVolPath.length() && 
										strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
										rst.put("pcdir", strDevHome + "\\" + strDirPath.substring(strVolPath.length()).replace("/", "\\"));
									}
								} 
								break;
							}
						}
						if (rst.get("pcdir") != null && rst.get("pcdir") != "") {
							strVolPath = rst.get("pcdir").replaceAll("\\\\\\\\", "\\\\");
							rst.put("pcdir", strVolPath );
							rst.put("localdir", strVolPath.replaceAll("\\\\", "\\\\\\\\"));
						} else {
							rst.put("pcdir",  "[관리자연락]홈디렉토리설정오류");
							rst.put("selected_flag","1");
						}					
					} else {
						rst.put("pcdir", "사용자환경설정에서 홈디렉토리설정요망");
						rst.put("selected_flag","1");
					}
				} else {
					rst.put("pcdir", rs.getString("cm_dirpath"));
				}
				
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;
			//ecamsLogger.error(rsval.toString());

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	public Object[] getDeployList(HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               cnt         = 0;
	
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
	
		ConnectionContext connectionContext = new ConnectionResource();
	
		boolean           errSw     = false;
		String            strRsrcCd = "";
		String            strRsrc[] = null;
		int               i = 0;
		boolean           srSw = false;
		try {
	
			conn = connectionContext.getConnection();
			if (etcData.get("srid") != null && etcData.get("srid") != "") srSw = true;
			if ( etcData.get("RsrcCd") == null || etcData.get("RsrcCd").equals("") || etcData.get("RsrcCd").equals("0000") ) etcData.put("RsrcCd", "00");
			
	        strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			if ( etcData.get("SinCd").equals("05") && etcData.get("ReqCd").equals("05") ) {
				strQuery.append("   and substr(cm_info,18, 1)='1'              \n");
			} else if ( etcData.get("ReqCd").equals("09") ) {
				strQuery.append("  and  (substr(cm_info, 1, 1)='1'             \n");
				strQuery.append("   or  substr(cm_info, 15, 1)='1')            \n");
			} else {
				strQuery.append("   and substr(cm_info, 2, 1)='1'              \n");
				strQuery.append("   and substr(cm_info, 26, 1)='0'             \n");
			}
			strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?        \n");
			strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");
	
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, etcData.get("SysCd"));
	        pstmt.setString(2, etcData.get("SysCd"));
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	if (strRsrcCd == ""){
	        		strRsrcCd = rs.getString("cm_rsrccd") + ",";
	        	} else{
	        		strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd") + ",";
	        	}
	        }
			rs.close();
			pstmt.close();
	
			ecamsLogger.error("strRsrcCd:"+strRsrcCd);
			if (strRsrcCd == ""){
				rs = null;
				pstmt = null;
	        	if (conn != null){
	        		conn.close();
	        		conn = null;
	        	}
				throw new Exception("형상관리 대상 프로그램이 없습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
	
			if(strRsrcCd.substring(strRsrcCd.length()-1).indexOf(",")>-1){
				strRsrcCd = strRsrcCd.substring(0,strRsrcCd.length()-1);
			}
			strRsrc = strRsrcCd.split(",");
			
	        cnt = 0;
			strQuery.setLength(0);
			strQuery.append("select /*+ ALL ROWS */                                             \n");
	    	strQuery.append("       a.cr_rsrcname,a.cr_lstver,a.cr_syscd,a.cr_dsncd,            \n");
		    strQuery.append("       a.cr_itemid,a.cr_rsrccd,a.cr_story,a.cr_jobcd,              \n");
	    	strQuery.append("       nvl(a.cr_realdep,0) cr_realdep,nvl(a.cr_testdep,0) cr_testdep,\n");
	    	strQuery.append("       nvl(a.cr_realver,0) cr_realver,nvl(a.cr_testver,0) cr_testver,\n");
	    	strQuery.append("       a.cr_devdep,a.cr_devver,a.cr_eclrsrc,a.cr_realsta,a.cr_teststa,\n");
		    strQuery.append("       to_char(a.cr_lstdat,'yyyy/mm/dd hh24:mi') enddate,          \n");
		    strQuery.append("       a.cr_status,c.cm_dirpath,i.cm_info,                         \n");
		    strQuery.append("       lpad(i.cm_stepsta,4,'0') prcreq,nvl(i.cm_vercnt,50) vercnt, \n");
		    strQuery.append("       d.cm_codename,e.cm_codename jawon,a.cr_editor,f.cm_username,\n");
		    strQuery.append("       b.cm_jobname,g.cm_sysgb,a.cr_devacpt cr_acptno              \n");
			strQuery.append("  from cmm0040 f,cmm0102 b,cmm0020 e,cmr0020 a,cmm0070 c,cmm0020 d,\n");
			strQuery.append("       cmm0036 i,cmm0030 g,cmm0044 h                               \n");
			strQuery.append(" where a.cr_syscd=?                                                \n");
			if ( srSw ) {//sr있을경우
				strQuery.append("   and a.cr_isrid=?                                            \n");
				strQuery.append("   and a.cr_lstusr=?                                           \n");
				//20140725 neo. 테스트배포와 운영배포에 대한 상태값 조건 추가
				//변경후 시작
//				if ( etcData.get("SinCd").equals("04") && etcData.get("TstSw").equals("1") ){//운영배포이면서 테스트 있음
//					strQuery.append("  and a.cr_status in ('G')                                 \n");
//				} else if ( etcData.get("SinCd").equals("04") && etcData.get("TstSw").equals("1") ){//운영배포이면서 테스트 없음
//					strQuery.append("  and a.cr_status in ('B')                                 \n");
//				} else {//테스트배포이면  상태값은 체크인완료  또는  테스트배포완료  상태이여야 함. 
//					strQuery.append("  and a.cr_status in ('B','G')                             \n");
//				}
				//변경후 끝
			} else {//sr 없을 경우 
				//20140725 neo.
				//strQuery.append("  and a.cr_status='D'                                          \n");
				strQuery.append("  and a.cr_lstver>0                                            \n");
			}
//			if ( etcData.get("SinCd").equals("04") && etcData.get("TstSw").equals("1") ){//운영배포이면서 테스트 있음
//				strQuery.append("  and a.cr_status in ('G')                                 \n");
//			} else if ( etcData.get("SinCd").equals("04") && etcData.get("TstSw").equals("1") ){//운영배포이면서 테스트 없음
//				strQuery.append("  and a.cr_status in ('B')                                 \n");
//			} else {//테스트배포이면  상태값은 체크인완료  또는  테스트배포완료  상태이여야 함. 
//				strQuery.append("  and a.cr_status in ('B','G')                             \n");
//			}
			strQuery.append("  and a.cr_status in ('3','4','5','A','B','F','G') \n");
			
			strQuery.append("   and a.cr_syscd=g.cm_syscd                                       \n");
			strQuery.append("   and h.cm_userid=?                                               \n");
			strQuery.append("   and a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd             \n");
			if ( !etcData.get("RsrcCd").equals("00") ) strQuery.append("and a.cr_rsrccd=?         \n");
			if ( etcData.get("RsrcName") != null && etcData.get("RsrcName") != "" ) {
				strQuery.append("and ( upper(a.cr_rsrcname) like upper(?) or upper(a.cr_story) like upper(?) )\n");
			}
			strQuery.append("   and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd           \n");	
			strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_dsncd=c.cm_dsncd             \n");
			strQuery.append("   and d.cm_macode='CMR0020' and a.cr_status=d.cm_micode           \n");
			strQuery.append("   and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode             \n");
			strQuery.append("   and nvl(a.cr_lstusr,a.cr_editor)=f.cm_userid                    \n");
			strQuery.append("   and a.cr_jobcd=b.cm_jobcd                                       \n");
	
			if ( etcData.get("RsrcCd").equals("00") || etcData.get("RsrcCd").equals("0000") ) {
				strQuery.append("and a.cr_rsrccd in (");
				for (i=0;strRsrc.length>i;i++) {
					if (i==0) strQuery.append("?");
					else strQuery.append(",?");
				}
				strQuery.append(") \n");
			}
			//strQuery.append("order by cr_rsrcname                                               \n");
	
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++cnt, etcData.get("SysCd"));
			if ( srSw ) {
				pstmt.setString(++cnt, etcData.get("srid"));
				pstmt.setString(++cnt, etcData.get("UserId"));
			}
			pstmt.setString(++cnt, etcData.get("UserId"));
			if (!etcData.get("RsrcCd").equals("00")) pstmt.setString(++cnt, etcData.get("RsrcCd"));
			if (etcData.get("RsrcName") != null && etcData.get("RsrcName") != "") {    	   
				pstmt.setString(++cnt, "%" + etcData.get("RsrcName") + "%"); 	   
				pstmt.setString(++cnt, "%" + etcData.get("RsrcName") + "%");
			}
			if ( etcData.get("RsrcCd").equals("00") || etcData.get("RsrcCd").equals("0000") ) {
				for (i=0 ; i<strRsrc.length ; i++) {
	        		pstmt.setString(++cnt, strRsrc[i]);
	        	}
			}
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("jawon", rs.getString("jawon"));
				if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
				else  rst.put("cr_story", "");
				rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
				rst.put("cr_version", rs.getString("cr_lstver"));
				rst.put("cr_editor", rs.getString("cr_editor"));
				rst.put("codename", rs.getString("cm_codename"));
				rst.put("cr_syscd", rs.getString("cr_syscd"));
				rst.put("cr_dsncd", rs.getString("cr_dsncd"));
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("baseitem", rs.getString("cr_itemid"));
				rst.put("cr_jobcd", rs.getString("cr_jobcd"));
				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
				rst.put("cr_eclrsrc", rs.getString("cr_eclrsrc"));
				rst.put("basename", rs.getString("cr_rsrcname"));
				
				if (etcData.get("ReqCd").equals("05") || etcData.get("ReqCd").equals("09")) {
					rst.put("reqcd", etcData.get("ReqCd"));
				} else {
					if (etcData.get("SinCd").equals("03")) {
						if (rs.getInt("cr_testver") == 0) rst.put("reqcd", "03");
						else rst.put("reqcd", "04");
					} else if (etcData.get("SinCd").equals("04")) {
						if (rs.getInt("cr_realver") == 0) rst.put("reqcd", "03");
						else rst.put("reqcd", "04");
					} else {
						if (rs.getInt("cr_lstver") == 0) rst.put("reqcd", "03");
						else rst.put("reqcd", "04");
					}
				}
				if (rs.getInt("vercnt") == 0) {
					if (etcData.get("SinCd").equals("03")) {
						rst.put("cr_befver", rs.getString("cr_testdep"));
						if (rs.getInt("cr_testver") >= 9999) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_testver")+1));
					} else if (etcData.get("SinCd").equals("04")) {
						rst.put("cr_befver", rs.getString("cr_realdep"));
						if (rs.getInt("cr_realver") >= 9999) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_realver")+1));
					} else {
						rst.put("cr_befver", rs.getString("cr_lstver"));
						if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
					}
				} else {
					if (etcData.get("SinCd").equals("03")) {
						rst.put("cr_befver", rs.getString("cr_testdep"));
						if (rs.getInt("cr_testver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_testver")+1));
					} else if (etcData.get("SinCd").equals("04")) {
						rst.put("cr_befver", rs.getString("cr_realdep"));
						if (rs.getInt("cr_realver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_realver")+1));
					} else {
						rst.put("cr_befver", rs.getString("cr_lstver"));
						if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
						else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_devver")+1));
					}
				}
	
				if (rs.getInt("cr_testdep") >0) rst.put("cr_testver", rs.getString("cr_testdep"));
				else rst.put("cr_testver", "0");
				if(rs.getInt("cr_realdep") >0) rst.put("cr_realver", rs.getString("cr_realdep"));
				else rst.put("cr_realver", "0");
				if(rs.getInt("cr_lstver") >0) rst.put("cr_devver", rs.getString("cr_lstver"));
				else rst.put("cr_devver", "0");
				
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("jobname", rs.getString("cm_jobname"));
				rst.put("cr_lastdate", rs.getString("enddate"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("prcseq", rs.getString("prcreq"));
				rst.put("lastdt", rs.getString("enddate"));
	
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("view_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				
				rst.put("selected_flag","0");
				rst.put("selected","0");
				rst.put("enabled","1");			
				
				
				errSw = false;
				ecamsLogger.error("cr_lstver:"+rs.getInt("cr_lstver")+",SinCd:"+etcData.get("SinCd")+",cr_status:"+rs.getString("cr_status"));
				if ( rs.getInt("cr_lstver") == 0 ) errSw = true;
				else if ( etcData.get("SinCd").equals("03") && !rs.getString("cr_status").equals("B") && !rs.getString("cr_status").equals("G") ) {
					errSw = true;//20140718 neo. 테스트적용일때 상태값이 체크인완료[B] 이여야 함.
				} else if ( etcData.get("SinCd").equals("04")) {
					ecamsLogger.error("############ TstSw:"+etcData.get("TstSw")+",cr_status:"+rs.getString("cr_status") );
					if ( etcData.get("TstSw").equals("1") && !rs.getString("cr_status").equals("G") ) errSw = true;
					else if ( etcData.get("TstSw").equals("0") && !rs.getString("cr_status").equals("B") ) errSw = true;
					//else errSw = true;//20140718 neo. 테스트적용 제외 하고 나머지 경우에 적용
				}
				
				//20140718 neo. 테스트적용 요청 일대는 자동 선택 안되도록 하기 위해.
				//변경전:rst.put("enabled", "0");
				if ( !etcData.get("SinCd").equals("03") ) rst.put("enabled", "0");
				//20140718 neo. 테스트적용 요청 일대는 자동 선택 안되도록 하기 위해.
				//변경전 : if (errSw)) {
				if ( errSw ) {
					rst.put("selected", "0");
					rst.put("selected_flag", "1");
				} else {
					rst.put("selected","1");
					rst.put("selected_flag", "0");
				}
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
	        conn.close();
	        rs = null;
	        pstmt = null;
	        conn = null;
			
//	        ecamsLogger.error("############ getDeployList  E N D ########: "+rsval.toString());
	
			return rsval.toArray();
	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDeployList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getDeployList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDeployList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getDeployList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getDeployList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDeployList() method statement
	
	public Boolean chk_Resouce(String syscd,String Rsrccd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  rtnval	  = 0;
		try {

			conn = connectionContext.getConnection();
			strQuery.append("select count(cm_rsrccd) as rowcnt from cmm0036 ");
			strQuery.append("where cm_syscd= ? ");
			strQuery.append("and   cm_rsrccd= ? ");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            pstmt.setString(2, Rsrccd);
            //pstmt.setInt(2, cnt);

            rs = pstmt.executeQuery();

			while (rs.next()){
				rtnval = rs.getInt("rowcnt");
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
            rs = null;
            pstmt = null;
            conn = null;

			if (rtnval > 0){
				return true;
			}
			else{
				return false;
			}


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.chk_Resouce() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.chk_Resouce() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.chk_Resouce() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.chk_Resouce() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## BbsDAO.chk_Resouce() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String sameModule(HashMap<String,String> etcData,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           errSw       = false;
		String            strWork1    = "";
//		String            strWork2    = "";
		String            strWork3    = "";
		String            strDirPath  = "";
		String            rsrcName    = etcData.get("rsrcname");
		String            strRsrcCd   = "";
//		String            strInfo     = "";
		String            strItemId   = "";
//		String            strDsnCd    = "";
    	int               parmCnt = 0;
		HashMap<String,String> tmpObj = new HashMap<String,String>();
		try {

			strQuery.setLength(0);
			strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
			strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
			strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
			strQuery.append("   and b.cm_factcd='04'                                  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
			strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("syscd"));
	        pstmt.setString(2, etcData.get("rsrccd"));

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	errSw = false;
	        	if (rsrcName.indexOf(".") > -1) {
	        		strWork1 = rsrcName.substring(0,rsrcName.indexOf("."));
	        	} else {
	        		strWork1 = rsrcName;
	        	}
	        	//ecamsLogger.error("+++++++++++++++strWork1=========>"+strWork1);
	        	if (rs.getString("cm_samename").indexOf("?#")>=0) {
	        		tmpObj = new HashMap<String,String>();
	        		tmpObj.put("rsrcname",etcData.get("cr_rsrcname"));
	        		tmpObj.put("extname", strWork1);
	        		tmpObj.put("dirpath",etcData.get("dirpath"));
	        	    tmpObj.put("acptno", "");
	        	    tmpObj.put("samename", rs.getString("cm_samename"));
	        	    tmpObj.put("itemid", etcData.get("cr_itemid"));
	        		strWork3 = nameChange(tmpObj,conn);
	        		if (strWork3.equals("ERROR")) {
	        			errSw = true;
	        			break;
	        		}
	        	} else {
	        		strWork3 = rs.getString("cm_samename").replace("*", strWork1);
	        	}
	        	strDirPath = etcData.get("dirpath");
	        	if (rs.getString("cm_basedir") != null) {
	        		if (!rs.getString("cm_basedir").equals("cm_samedir")){
	        			if (rs.getString("cm_basedir").equals("*")) {
	        				if (rs.getString("cm_samedir").indexOf("?#")>=0) {
	        					tmpObj = new HashMap<String,String>();
	        	        		tmpObj.put("rsrcname",etcData.get("cr_rsrcname"));
	        	        		tmpObj.put("extname", strWork1);
	        	        		tmpObj.put("dirpath",etcData.get("dirpath"));
	        	        	    tmpObj.put("acptno", "");
	        	        	    tmpObj.put("samename", rs.getString("cm_samedir"));
	        	        	    tmpObj.put("itemid", etcData.get("cr_itemid"));

	        	        		strDirPath = nameChange(tmpObj,conn);
	        	        		if (strDirPath.equals("ERROR")) {
	        	        			errSw = true;
	        	        			break;
	        	        		}
	        				} else {
	        					strDirPath = rs.getString("cm_samedir");
	        				}
	        			}
	        			else {
	        				strDirPath = strDirPath.replace(rs.getString("cm_basedir"), rs.getString("cm_samedir"));
	        			}
	        		}
	        	}
	        	if (!errSw) {
		        	strRsrcCd = rs.getString("cm_samersrc");
//		        	strInfo = rs.getString("cm_info");
		        	strItemId = "";
//		        	strDsnCd = "";
		        	parmCnt = 0;
		        	strQuery.setLength(0);
					strQuery.append("select a.cr_itemid                     \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a             \n");
				   	strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?  \n");
				   	strQuery.append("   and upper(a.cr_rsrcname)= upper(?)  \n");
				   	strQuery.append("   and upper(b.cm_dirpath)=upper(?)    \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd           \n");
				   	strQuery.append("   and a.cr_dsncd=b.cm_dsncd           \n");
				   pstmt2 = conn.prepareStatement(strQuery.toString());
//					pstmt2 =  new LoggableStatement(conn, strQuery.toString());
		            pstmt2.setString(++parmCnt, etcData.get("syscd"));
		            pstmt2.setString(++parmCnt, strRsrcCd);
				   	pstmt2.setString(++parmCnt,strWork3);
				   	pstmt2.setString(++parmCnt,strDirPath);
//		            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		            rs2 = pstmt2.executeQuery();
		            if (rs2.next()) {
		            	strItemId = rs2.getString("cr_itemid");
		            }
		            rs2.close();
		            pstmt2.close();

		            if (strItemId.length() == 0) {
		            	errSw = true;
		            }
	        	}
	        }
            rs = null;
            pstmt = null;
            
            if (errSw) strItemId = "ERROR";
            
            return strItemId;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.sameModule() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.sameModule() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.sameModule() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.sameModule() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
	public String confSelect(String SysCd,String ReqCd,String RsrcCd,String UserId,String QryCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            retMsg      = "N";
		int               cnt         = 0;

		try {

			conn = connectionContext.getConnection();
			if (ReqCd.equals("41") || ReqCd.equals("69")) {
				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from (       \n");
				strQuery.append("select a.cm_seqno                \n");
				strQuery.append("  from cmm0040 b,cmm0060 a       \n");
				strQuery.append(" where a.cm_syscd=?              \n");
				strQuery.append("   and a.cm_reqcd=?              \n");
				strQuery.append("   and b.cm_userid=?             \n");
				strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid \n");
				strQuery.append(" minus                           \n");
				strQuery.append("select a.cm_seqno                \n");
				strQuery.append("  from cmm0043 c,cmm0040 b,cmm0060 a \n");
				strQuery.append(" where a.cm_syscd=?              \n");
				strQuery.append("   and a.cm_reqcd=?              \n");
				strQuery.append("   and b.cm_userid=?             \n");
				strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid \n");
				strQuery.append("   and a.cm_gubun='3'            \n");
				strQuery.append("   and b.cm_userid=c.cm_userid   \n");
				strQuery.append("   and instr(a.cm_position,c.cm_rgtcd)>0 \n");
				strQuery.append(") \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, SysCd);
	            pstmt.setString(2, ReqCd);
	            pstmt.setString(3, UserId);
	            pstmt.setString(4, SysCd);
	            pstmt.setString(5, ReqCd);
	            pstmt.setString(6, UserId);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
		            if (rs.getInt("cnt")>0) {
		            	retMsg = "Y";
		            }
	            }
	            rs.close();
	            pstmt.close();				
			} else {
				SysInfo sysinfo = new SysInfo();
				cnt = sysinfo.getTstSys_conn(SysCd,conn);
				boolean mkSw = false;
				boolean updownSw  = false;
				if (ReqCd.equals("04") && cnt > 0) {
				// 테스트서버가 있는 체크인요청은 SKIP
				} else {
					cnt = 0;
					if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("05")) {
						strQuery.setLength(0);
						strQuery.append("select distinct a.cm_jobcd                         \n");
						strQuery.append("  from cmm0060 a,cmm0036 c,cmm0040 b               \n");
						strQuery.append(" where a.cm_syscd=? and a.cm_reqcd=?               \n");
						strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid   \n");
						strQuery.append("   and b.cm_userid=? and a.cm_gubun='1'            \n");
						strQuery.append("   and a.cm_syscd=c.cm_syscd                       \n");
						strQuery.append("   and instr(?,c.cm_rsrccd)>0                      \n");
						if (ReqCd.equals("01") || ReqCd.equals("02")) {
							strQuery.append("and (substr(c.cm_info,38,1)='1' or             \n");
							strQuery.append("     substr(c.cm_info,45,1)='1')               \n");
						} else {
							strQuery.append("and substr(c.cm_info,45,1)='1'                 \n");
						}
	
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
			            pstmt.setString(1, SysCd);
			            pstmt.setString(2, ReqCd);
			            pstmt.setString(3, UserId);
			            pstmt.setString(4, RsrcCd);
			            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			            rs = pstmt.executeQuery();
			            while (rs.next()) {
			            	++cnt;
		            		if (rs.getString("cm_jobcd").equals("SYSPDN") || rs.getString("cm_jobcd").equals("SYSPUP")) updownSw = true;
		            		else if (rs.getString("cm_jobcd").equals("SYSFMK")) mkSw = true;
			            }
			            rs.close();
			            pstmt.close();
			            if (cnt > 0) {
				            if (ReqCd.equals("01") || ReqCd.equals("02")) {
				            	if (updownSw == false || mkSw == false) {
				            		retMsg = "X";
				            	}
				            } else {
				            	if (updownSw == false) {
				            		retMsg = "X";
				            	}
				            }
			            }
					}
				}
	
				if (!retMsg.equals("X")) {
					cnt = 0;
	
					strQuery.setLength(0);
					strQuery.append("select a.cm_gubun,a.cm_rsrccd                           \n");
					strQuery.append("  from cmm0060 a,cmm0040 b                              \n");
					strQuery.append(" where a.cm_syscd=? and a.cm_reqcd=?                    \n");
					strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid        \n");
					strQuery.append("   and b.cm_userid=?                                    \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
		            pstmt.setString(1, SysCd);
		            pstmt.setString(2, ReqCd);
		            pstmt.setString(3, UserId);
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
		            while (rs.next()){
		            	++cnt;
		            	if (!rs.getString("cm_gubun").equals("1") && !rs.getString("cm_gubun").equals("2")) {
		            		if (rs.getString("cm_gubun").equals("C")) retMsg = "C";
		            		else {
			            		if (rs.getString("cm_rsrccd") != null) {
			            			String strRsrc[] = RsrcCd.split(",");
	
			            			for (int i = 0;strRsrc.length > i; i++) {
			            				if (rs.getString("cm_rsrccd").indexOf(strRsrc[i]) >= 0) {
			            					retMsg = "Y";
			            					break;
			            				}
			            			}
			            		} else {
			            			retMsg = "Y";
			            			break;
			            		}
	
		            		}
		            	}
		            }
	
			        if (QryCd.equals("09")) {
			        	retMsg = "N";
			        }
			        if (cnt == 0) retMsg = "0";
		            rs.close();
		            pstmt.close();
				}
	
				strQuery.setLength(0);
				strQuery.append("select a.cm_gubun,a.cm_rsrccd                           \n");
				strQuery.append("  from cmm0060 a,cmm0040 b                              \n");
				strQuery.append(" where a.cm_syscd=? and a.cm_reqcd=?                    \n");
				strQuery.append("   and decode(a.cm_manid,'1','Y','N')=b.cm_manid        \n");
				strQuery.append("   and b.cm_userid=?                                    \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, SysCd);
	            pstmt.setString(2, ReqCd);
	            pstmt.setString(3, UserId);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            while (rs.next()){
	            	++cnt;
	            	if (!rs.getString("cm_gubun").equals("1") && !rs.getString("cm_gubun").equals("2")) {
	            		if (rs.getString("cm_gubun").equals("C")) retMsg = "C";
	            		else {
		            		if (rs.getString("cm_rsrccd") != null) {
		            			String strRsrc[] = RsrcCd.split(",");
	
		            			for (int i = 0;strRsrc.length > i; i++) {
		            				if (rs.getString("cm_rsrccd").indexOf(strRsrc[i]) >= 0) {
		            					retMsg = "Y";
		            					break;
		            				}
		            			}
		            		} else {
		            			retMsg = "Y";
		            			break;
		            		}
	            		}
	            	}
	            }
	
		        if (QryCd.equals("09")) {
		        	retMsg = "N";
		        }
		        if (cnt == 0) retMsg = "0";
	            rs.close();
	            pstmt.close();
			}
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

            //ecamsLogger.error("+++++++retMsg+++++"+retMsg);
            return retMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.confSelect() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.confSelect() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.confSelect() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.confSelect() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] dbioCheck(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String            tableName = "";
		int               j = 0;
		try {
			conn = connectionContext.getConnection();

			rtList.clear();

			for (int i=0;i<fileList.size();i++){
				rst = new HashMap<String, String>();
				rst = fileList.get(i);
				rst.put("error", "N");
				rst.put("errmsg", "");
				//동일한 Table을 사용하는 DBIO 체크인신청여부 Check
				if (fileList.get(i).get("cr_rsrccd").equals("12") ||
					fileList.get(i).get("cr_rsrccd").equals("46") ||
					fileList.get(i).get("cr_rsrccd").equals("47") ||
					fileList.get(i).get("cr_rsrccd").equals("48") ||
					fileList.get(i).get("cr_rsrccd").equals("49") ||
					fileList.get(i).get("cr_rsrccd").equals("50")) {
					tableName = fileList.get(i).get("cr_rsrcname");
					//substr('*',1,length('*')-7)
					j = tableName.lastIndexOf("_");
					if (j> 0) tableName = tableName.substring(0,j);
					else tableName = tableName.substring(0,tableName.length()-7);
					strQuery.setLength(0);
					strQuery.append("select c.cm_username,to_char(a.cr_acptdate,'yyyy-mm-dd') acptdate \n");
					strQuery.append("  from cmm0040 c,cmr1010 b,cmr1000 a           \n");
					strQuery.append(" where a.cr_status='0' and a.cr_qrycd='04'     \n");
					strQuery.append("   and a.cr_syscd=?                            \n");
					strQuery.append("   and a.cr_acptno=b.cr_acptno                 \n");
					strQuery.append("   and b.cr_status='0'                         \n");
					strQuery.append("   and b.cr_rsrccd in ('12','46','47','48','49','50') \n");
					strQuery.append("   and length(b.cr_rsrcname)=?                 \n");
					strQuery.append("   and b.cr_rsrcname like ?                    \n");
					strQuery.append("   and a.cr_editor=c.cm_userid                 \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_syscd"));
					pstmt.setInt(2, tableName.length()+7);
					pstmt.setString(3, tableName+"%");
					////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rst.put("error", "Y");
						rst.put("errmsg", "[Table : "+tableName+" 체크인 신청 중] 요청자:"+rs.getString("cm_username")+", 요청일:"+rs.getString("acptdate"));
					}
					rs.close();
					pstmt.close();
				} else if (fileList.get(i).get("cr_rsrccd").equals("20")) {
					//Table LayOut의 경우 DBIO에 대한 테스트적용요청이 없는 경우 오류처리
					String AcptDate = "";
					strQuery.setLength(0);
					if (fileList.get(i).get("reqcd").equals("03")) {
						strQuery.append("select to_char(cr_opendate,'yyyymmdd') acptdate \n");
						strQuery.append("  from cmr0020                                  \n");
						strQuery.append(" where cr_itemid=?                              \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(1, fileList.get(i).get("cr_itemid"));
						////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						rs = pstmt.executeQuery();
						if (rs.next()) {
							AcptDate = rs.getString("acptdate");
						}
						rs.close();
						pstmt.close();
					} else {
						strQuery.append("select to_char(max(a.cr_acptdate),'yyyymmdd') acptdate \n");
						strQuery.append("  from cmr1010 b,cmr1000 a                      \n");
						strQuery.append(" where b.cr_itemid=? and b.cr_status<>'3'       \n");
						strQuery.append("   and b.cr_prcdate is not null                 \n");
						strQuery.append("   and b.cr_acptno=a.cr_acptno                  \n");
						strQuery.append("   and a.cr_qrycd='01'                          \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(1, fileList.get(i).get("cr_itemid"));
						////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						rs = pstmt.executeQuery();
						if (rs.next()) {
							AcptDate = rs.getString("acptdate");
						}
						rs.close();
						pstmt.close();
					}

					tableName = fileList.get(i).get("cr_rsrcname");
					strQuery.setLength(0);
					strQuery.append("select count(*) as cnt                         \n");
					strQuery.append("  from cmr1010 b,cmr1000 a                     \n");
					strQuery.append(" where a.cr_qrycd='03' and a.cr_status<>'3'    \n");
					strQuery.append("   and a.cr_prcdate is not null                \n");
					strQuery.append("   and a.cr_syscd=?                            \n");
					strQuery.append("   and to_char(a.cr_acptdate,'yyyymmdd')>=?    \n");
					strQuery.append("   and a.cr_acptno=b.cr_acptno                 \n");
					strQuery.append("   and b.cr_rsrccd in ('12','46','47','48','49','50') \n");
					strQuery.append("   and length(b.cr_rsrcname)=?                 \n");
					strQuery.append("   and b.cr_rsrcname like ?                    \n");
					strQuery.append("   and b.cr_status<>'3'                        \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_syscd"));
					pstmt.setString(2, AcptDate);
					pstmt.setInt(3, tableName.length()+7);
					pstmt.setString(4, tableName+"%");
					////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getInt("cnt") == 0) {
							rst.put("error", "Y");
							rst.put("errmsg", "[TableLayOut : " + tableName + "]에 대한 DBIO의  테스트적용요청기록이 없습니다.");
						}
					}
					rs.close();
					pstmt.close();
				}
				rtList.add(rst);
			}//end of while-loop statement

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.dbioCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.dbioCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.dbioCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.dbioCheck() Exception END ##");
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
					ecamsLogger.error("## Cmr0200.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String analCheck(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retVal = "OK";
		String            strBaseNo = "";
		try {
			conn = connectionContext.getConnection();


			for (int i=0;i<fileList.size();i++){
				strBaseNo = "";
				strQuery.setLength(0);
				strQuery.append("select max(a.cr_acptno) baseno     \n");
				strQuery.append("  from cmr1010 b,cmr1000 a         \n");
				strQuery.append(" where b.cr_itemid=?               \n");
				strQuery.append("   and b.cr_status<>'3'            \n");
				strQuery.append("   and b.cr_acptno=a.cr_acptno     \n");
				strQuery.append("   and a.cr_qrycd='03'             \n");
				strQuery.append("   and a.cr_status<>'3'            \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, fileList.get(i).get("cr_itemid"));
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					retVal = "OK";
					if (rs.getString("baseno") != null) {
						strBaseNo = rs.getString("baseno");
					} else {
						retVal = "ER";
					}
				}
				rs.close();
				pstmt.close();

				if (retVal.equals("OK")) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmr1060     \n");
					strQuery.append(" where cr_acptno=?                   \n");
					strQuery.append("   and cr_itemid=?                   \n");
					strQuery.append("   and nvl(cr_chkflag,'1')='1'       \n");
					strQuery.append("   and cr_reftstacpt is null         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, strBaseNo);
					pstmt.setString(2, fileList.get(i).get("cr_itemid"));
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getInt("cnt")>0) {
							retVal = "NO";
							break;
						}
					}
					rs.close();
					pstmt.close();
				} else {
					break;
				}
			}//end of while-loop statement

			conn.close();
			conn = null;
			rs = null;
			pstmt = null;
			return retVal;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.analCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.analCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.analCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.analCheck() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.analCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getDownFileList_save(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		String            strBinPath  = "";
		String            strTmpPath  = "";


		try {
			strBinPath = ecamsinfo.getFileInfo("14");
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo("99");
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");


			ecamsinfo = null;
			rtList.clear();

			ThreadPool pool = new ThreadPool(10);
			//ecamsLogger.error("+++++++++CHECK-IN LIST START+++");
			for (int i=0;i<fileList.size();i++){
				pool.assign(new Cmr0200_ThreadWorker(fileList.get(i),etcData,strBinPath,strTmpPath,rtList));
				Thread.sleep(100);
			}//end of while-loop statement

			pool.complete();

			for (int i=0;rtList.size()>i;i++) {
				for (int j=i+1;rtList.size()>j;j++) {
					if (rtList.get(i).get("cr_itemid").equals(rtList.get(j).get("cr_itemid"))){
						rtList.remove(j--);
					}
				}
			}
			rtObj =  rtList.toArray();
			rtList.clear();
			//ecamsLogger.error("+++++++++CHECK-IN LIST E N D+++");
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList_save() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getDownFileList_save() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList_save() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getDownFileList_save() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
		}
	}

	public Object[] getDownFileList(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		eCAMSInfo         ecamsinfo   = new eCAMSInfo();
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		String            strBinPath  = "";
		String            strTmpPath  = "";
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		HashMap<String, String>			  rst		  = null;
		HashMap<String, String>			  tmpObj      = null;
		int               reqCnt      = 0;
		int               addCnt      = 0;
		int               svCnt       = 0;
		boolean            ErrSw      = false;
		String            strErr      = "";
		String            strWork1    = null;
		String            strWork3    = null;
		String            strRsrcCd   = null;
		String            strRsrcName = null;

		ArrayList<HashMap<String, String>> svrList = new ArrayList<HashMap<String, String>>();

		int                 i = 0;                                                                                               
		int                 j = 0;
		int              parmCnt = 0;
		Cmd0100 cmd0100 = new Cmd0100();

		try {
			conn = connectionContext.getConnection();
			strBinPath = ecamsinfo.getFileInfo_conn("14", conn);
			if (strBinPath == "" || strBinPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");

			strTmpPath = ecamsinfo.getFileInfo_conn("99", conn);
			if (strTmpPath == "" || strTmpPath == null)
				throw new Exception("관리자에게 연락하여 주시기 바랍니다. (형상관리환경설정 - 실행디렉토리)");
			
			ecamsinfo = null;
			String strVolPath = "";
			String strDirPath = "";
			String strHomeDirPath = "";
			String strSameDirPath = "";
			String strDevHome = "";
			String strItemId = "";
			String strDsnCd = "";
			String strInfo = "";
			String verYN = etcData.get("veryn");
			SysInfo sysinfo = new SysInfo();
			
			//로컬PC 홈 디렉토리 조회
//			if (etcData.get("localyn").equals("Y")) {
			strQuery.setLength(0);
			strQuery.append("select cd_devhome from cmd0300               \n");
			strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, etcData.get("syscd"));
			pstmt.setString(2, etcData.get("userid"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strDevHome = rs.getString("cd_devhome");
			}
			rs.close();
			pstmt.close();

			svrList = sysinfo.getHomePath_conn(etcData.get("syscd"), conn);
//			}
			sysinfo = null;
			rtList.clear();
			
			for (i=0;fileList.size()>i;i++) {
				rst = new HashMap<String,String>();
				rst.put("cm_dirpath", fileList.get(i).get("cm_dirpath"));
				rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
				rst.put("cr_story",fileList.get(i).get("cr_story"));
				rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
				rst.put("jawon", fileList.get(i).get("jawon"));

				strQuery.setLength(0);
				strQuery.append("select cm_codename from cmm0020 where cm_macode='CHECKIN' and cm_micode=?  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, fileList.get(i).get("reqcd"));
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	rst.put("checkin", rs.getString("cm_codename"));
	            }
	            rs.close();
	            pstmt.close();
          
				//rst.put("checkin", etcData.get("QryName"));
				rst.put("prcseq", fileList.get(i).get("prcseq"));
				rst.put("cr_lstver",fileList.get(i).get("cr_lstver"));
				rst.put("cr_itemid",fileList.get(i).get("cr_itemid"));
				rst.put("sysgb", fileList.get(i).get("sysgb"));
				rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
				rst.put("cr_rsrccd",fileList.get(i).get("cr_rsrccd"));
				rst.put("cr_dsncd",fileList.get(i).get("cr_dsncd"));
				rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
				rst.put("baseitem",fileList.get(i).get("baseitem"));
				rst.put("cm_info",fileList.get(i).get("cm_info"));
				rst.put("cr_acptno",fileList.get(i).get("cr_acptno"));
				rst.put("sortgbn",fileList.get(i).get("sortgbn"));
				if (fileList.get(i).get("analsw") != null) rst.put("analsw",fileList.get(i).get("analsw"));
				else rst.put("analsw", "N");
				if (verYN.equals("Y")) rst.put("cr_aftver",fileList.get(i).get("cr_aftver"));
				else rst.put("cr_aftver",fileList.get(i).get("cr_lstver"));
				rst.put("modsel", "N");
				if (fileList.get(i).get("cr_sayu") != null && fileList.get(i).get("cr_sayu") != ""){
					rst.put("cr_sayu",fileList.get(i).get("cr_sayu"));
				}
				else{
					rst.put("cr_sayu",etcData.get("sayu"));
				}
				rst.put("reqcd",fileList.get(i).get("reqcd"));
				rst.put("cr_baseno",fileList.get(i).get("cr_baseno"));
				rst.put("pcdir",fileList.get(i).get("pcdir"));
				rst.put("localdir", fileList.get(i).get("localdir"));  
				rst.put("enable1","1");
				rst.put("selected","0");
				reqCnt = addCnt + 1;
				rst.put("seq", Integer.toString(reqCnt));
				rtList.add(addCnt++, rst);
				rst = null;
				svCnt = addCnt - 1;
				strRsrcName = fileList.get(i).get("cr_rsrcname");
				
				if (ErrSw == false && fileList.get(i).get("cm_info").substring(3,4).equals("1") && (etcData.get("ReqCD").equals("07"))) {
					strQuery.setLength(0);
					strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
					strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
					strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
					strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
					strQuery.append("   and b.cm_factcd='04'                                  \n");
					strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
					strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_syscd"));
			        pstmt.setString(2, fileList.get(i).get("cr_rsrccd"));
		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();

			        while (rs.next()) {
			            ErrSw = false;
			        	if (fileList.get(i).get("cr_rsrcname").indexOf(".") > -1) {
			        		strWork1 = fileList.get(i).get("cr_rsrcname").substring(0,fileList.get(i).get("cr_rsrcname").indexOf("."));
			        	} else {
			        		strWork1 = fileList.get(i).get("cr_rsrcname");
			        	}
			        	if (rs.getString("cm_samename").indexOf("?#")>=0) {
			        		tmpObj = new HashMap<String,String>();
			        		tmpObj.put("rsrcname",fileList.get(i).get("cr_rsrcname"));
			        		tmpObj.put("dirpath",fileList.get(i).get("cm_dirpath"));
			        		tmpObj.put("samename",rs.getString("cm_samename"));

			        		strWork3 = nameChange(tmpObj,conn);
			        		if (strWork3.equals("ERROR")) {
			        			for (j=rtList.size()-1;j>=svCnt;j--) {
									rtList.remove(j);
								}
				            	strErr = "["+strRsrcName+"]에 대한 동시적용모듈정보가 정확하지 않습니다.";
								rst = new HashMap<String,String>();
								rst.put("cr_itemid","ERROR");
								rst.put("cm_dirpath",strErr);
								rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
								rtList.add(svCnt, rst);
								rst = null;
								ErrSw = true;
			        		}
			        	} else if (rs.getString("cm_samename").indexOf("*")<0) {
			        		strWork3 = rs.getString("cm_samename");
			        	} else if (rs.getString("cm_samename").equals("*.*")) {
			        		strWork3 = fileList.get(i).get("cr_rsrcname");
			        	} else {
			        		strWork3 = rs.getString("cm_samename").replace("*", strWork1);
			        	}
			        	
			        	
			        	strHomeDirPath = rs.getString("cm_basedir");
			        	strSameDirPath = rs.getString("cm_samedir");
			        	
						//로컬에서 올리는 프로그램인 경우 서버 경로로 변경해서 리스트업 되어야 함.
			        	//strDirPath:기준이되는 디렉토리
						strDirPath = fileList.get(i).get("cm_dirpath");
//						if ( fileList.get(i).get("cm_info").substring(44,45).equals("1") ){
//							for (j=0 ; svrList.size()>j ; j++) {
//								if (svrList.get(j).get("cm_rsrccd").equals( fileList.get(i).get("cr_rsrccd") )) {
//									strVolPath = svrList.get(j).get("cm_volpath");
//									if (strVolPath != null && strVolPath != "") {
////										ecamsLogger.error("[0]strDirPath:"+strDirPath+",strDevHome:"+strDevHome +",strVolPath:"+strVolPath );
//										if ( strDirPath.length() >= strDevHome.length() && strDirPath.substring( 0,strDevHome.length() ).equals( strDevHome ) ) {
////											ecamsLogger.error("[1]strDirPath:"+strDirPath+",strDevHome:"+strDevHome +",strVolPath:"+strVolPath );
//											strDirPath = strDirPath.replace(strDevHome, strVolPath).replaceAll("\\\\", "/");
//										}else{
////											ecamsLogger.error("[2]strDirPath:"+strDirPath+",strVolPath:"+strVolPath );
//											strDirPath = strVolPath + "/" + strDirPath.replaceAll("\\\\", "/");
//										}
//									} else {
////										ecamsLogger.error("[3]strDirPath:"+strDirPath  );
//										strDirPath = strDirPath.replaceAll("/", "\\");
//									}
//									strDirPath = strDirPath.replaceAll("//", "/");
//									rst.put("pcdir", strDirPath);
//									break;
//								}
//							}
//						} else {
//							strDirPath = strDirPath.replaceAll("//", "/");
//							rst.put("pcdir", strDirPath);
//						}
//						ecamsLogger.error("[4]strDirPath:"+strDirPath +",cr_rsrcname:"+fileList.get(i).get("cr_rsrcname") );
			        	//ecamsLogger.error("strDirPath:"+strDirPath+",cm_basedir:"+strHomeDirPath +",strSameDirPath:"+strSameDirPath );
			        	if (strHomeDirPath != null) {
			        		if (!strHomeDirPath.equals("cm_samedir")){
			        			if (!strHomeDirPath.equals("*")) {
			        				if (strDirPath.indexOf(strHomeDirPath)<0) {
			        					continue;
			        				}
			        			}
			        			if (strHomeDirPath.equals("*") && strSameDirPath.indexOf("?#")<0) strDirPath = strSameDirPath;
			        			else if (strSameDirPath.indexOf("?#")>=0) {
					        		tmpObj = new HashMap<String,String>();
					        		tmpObj.put("rsrcname",fileList.get(i).get("cr_rsrcname"));
					        		tmpObj.put("dirpath",fileList.get(i).get("cm_dirpath"));
					        		tmpObj.put("samename",strSameDirPath);

					        		strWork3 = nameChange(tmpObj,conn);
					        		if (strWork3.equals("ERROR")) {
					        			for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}
						            	strErr = "["+strRsrcName+"]에 대한 동시적용모듈정보가 정확하지 않습니다.";
										rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										ErrSw = true;
					        		}
			        			} else {
			        				strDirPath = strDirPath.replace(strHomeDirPath, strSameDirPath);
			        			}
			        		} else {
			        			if (rs.getString("cm_basename").equals("*") && rs.getString("cm_samename").equals("*.*")) {
			        				continue;
			        			}
			        					
			        		}
			        	}
			        	//ecamsLogger.error("ErrSw:"+ErrSw+", *** samedir, samename ++++"+strWork3 + ", "+ strDirPath);
			        	if ( !ErrSw ) {
				        	strRsrcCd = rs.getString("cm_samersrc");
				        	strInfo = rs.getString("cm_info");
				        	strItemId = "";
				        	strDsnCd = "";
				        	parmCnt = 0;
				        	strQuery.setLength(0);
							strQuery.append("select a.cr_itemid                     \n");
						   	strQuery.append("  from cmm0070 b,cmr0020 a             \n");
						   	strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?  \n");
						   	strQuery.append("   and upper(a.cr_rsrcname)= upper(?)  \n");
						   	if (strInfo.substring(25,26).equals("0") || !strDirPath.equals("*")) {
						   		strQuery.append("   and upper(b.cm_dirpath)=upper(?)\n");
						   	}
						   	strQuery.append("   and a.cr_syscd=b.cm_syscd           \n");
						   	strQuery.append("   and a.cr_dsncd=b.cm_dsncd           \n");
						    pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn, strQuery.toString());
				            pstmt2.setString(++parmCnt, fileList.get(i).get("cr_syscd"));
				            pstmt2.setString(++parmCnt, strRsrcCd);
						   	pstmt2.setString(++parmCnt,strWork3);
						   	if (strInfo.substring(25,26).equals("0") || !strDirPath.equals("*")) {
						   		pstmt2.setString(++parmCnt,strDirPath);
						   	}
				            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	strItemId = rs2.getString("cr_itemid");
				            }
				            rs2.close();
				            pstmt2.close();
				            if ( strItemId.length() == 0 ) {
				            	if (strInfo.substring(25,26).equals("1")) continue;
				            	
				            	strQuery.setLength(0);
				            	strQuery.append("select cm_dsncd from cmm0070       \n");
				            	strQuery.append(" where cm_syscd=? and cm_dirpath=? \n");
				            	pstmt2 = conn.prepareStatement(strQuery.toString());
				            	//pstmt2 = new LoggableStatement(conn, strQuery.toString());
				            	pstmt2.setString(1, etcData.get("syscd"));
				            	pstmt2.setString(2,strDirPath);
				            	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            	rs2 = pstmt2.executeQuery();
				            	if (rs2.next()) {
				            		strDsnCd  = rs2.getString("cm_dsncd");
				            	}
				            	rs2.close();
				            	pstmt2.close();

				            	if (strDsnCd.length() == 0) {
				            		strDsnCd = cmd0100.cmm0070_Insert(etcData.get("userid"), etcData.get("syscd"),"",strRsrcCd,fileList.get(i).get("cr_jobcd"),strDirPath,true,conn);
				            		if (strDsnCd == null || strDsnCd == "") {
					        			for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}
						            	strErr = "["+strDirPath+"]에 대한 디렉토리등록에 실패하였습니다.";
										rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										ErrSw = true;
					        		}

				            	} 
//				            	else {
				            		//strItemId = cmd0100.cmr0020_Insert(etcData.get("userid"), etcData.get("syscd"), strDsnCd, strWork3, strRsrcCd, fileList.get(i).get("cr_jobcd"), fileList.get(i).get("cr_story"), fileList.get(i).get("cr_itemid"), strInfo, conn);
				    	        	rst = new HashMap<String, String>();
				    	        	rst.put("userid",etcData.get("userid"));
				    	        	rst.put("syscd",etcData.get("syscd"));
				    	        	rst.put("dsncd",strDsnCd);
				    	        	rst.put("rsrcname",strWork3);
				    	        	rst.put("rsrccd",strRsrcCd);
				    	        	rst.put("jobcd",fileList.get(i).get("cr_jobcd"));
				    	        	rst.put("story",fileList.get(i).get("cr_story"));
				    	        	//rst.put("baseitem",fileList.get(i).get("cr_itemid"));
				    	        	rst.put("baseitem",fileList.get(i).get("baseitem"));
				    	        	rst.put("info",strInfo);
				    	        	strItemId = cmd0100.cmr0020_Insert(rst,conn);
				    	        	rst = null;
				            		if (!strItemId.substring(0,1).equals("0")) {
				            			for (j=rtList.size()-1;j>=svCnt;j--) {
											rtList.remove(j);
										}
						            	strErr = "["+strWork3+"]에 대한 프로그램등록에 실패하였습니다.";
										rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",strErr);
										rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
										rtList.add(svCnt, rst);
										rst = null;
										ErrSw = true;
				            		} else {
				            			strItemId = strItemId.substring(1);
				            		}
//				            	}
				            }
				            if ( !ErrSw ) {
					        	parmCnt = 0;
					        	strQuery.setLength(0);
								strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
								strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
								strQuery.append("       d.CM_INFO,lpad(d.cm_stepsta,4,'0') prcseq,         \n");
								strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin   \n");
							   	strQuery.append("  from cmm0070 b,cmr0020 a,cmm0036 d,cmm0020 e,cmm0020 f  \n");
							   	strQuery.append(" where a.cr_itemid=?                                      \n");
							   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
							   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
								strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
								strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
								pstmt2 = conn.prepareStatement(strQuery.toString());
								//pstmt2 = new LoggableStatement(conn, strQuery.toString());
					            pstmt2.setString(++parmCnt, strItemId);
					            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();

					            if (rs2.next()) {
					            	boolean fileSw = false;
					            	for (int k=0;rtList.size()>k;k++) {
					            		if (rtList.get(k).get("cr_itemid").equals(rs2.getString("cr_itemid"))) {
					            			fileSw = true;
					            			/*
					            			if (rtList.get(k).get("baseitem").indexOf(fileList.get(i).get("baseitem"))<0) {
						            			rst = new HashMap<String,String>();
							            		rst = rtList.get(k);
							            		//rst.put("baseitem", rst.get("baseitem")+","+fileList.get(i).get("cr_itemid"));
							            		rst.put("baseitem", rtList.get(k).get("baseitem")+","+fileList.get(i).get("baseitem"));
												rtList.set(k, rst);
					            			}
					            			*/
					            			break;
					            		}
					            	}
					            	if (fileSw == false) {
						            	rst = new HashMap<String,String>();
						    			rst.put("cm_dirpath",rs2.getString("cm_dirpath"));
						    			rst.put("cr_rsrcname",rs2.getString("cr_rsrcname"));
						    			rst.put("cr_story",rs2.getString("cr_story"));
						    			rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
						    			rst.put("jawon", rs2.getString("jawon"));
						    			if (fileList.get(i).get("reqcd").equals("03") || fileList.get(i).get("reqcd").equals("04") || fileList.get(i).get("reqcd").equals("09")){
						    				rst.put("checkin", rs2.getString("checkin"));
						    			}
						    			else{
						    				rst.put("checkin", fileList.get(i).get("checkin"));
						    			}
						    			rst.put("prcseq", rs2.getString("prcseq"));
						    			rst.put("cr_lstver",rs2.getString("cr_lstver"));
						    			rst.put("cr_itemid",rs2.getString("cr_itemid"));
						    			rst.put("sysgb", fileList.get(i).get("sysgb"));
						    			rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
						    			rst.put("cr_rsrccd",rs2.getString("cr_rsrccd"));
						    			rst.put("cr_dsncd",rs2.getString("cr_dsncd"));
						    			rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
						    			//rst.put("baseitem",fileList.get(i).get("cr_itemid"));
						    			rst.put("baseitem",fileList.get(i).get("baseitem"));
						    			rst.put("sortgbn", "1");
						    			rst.put("modsel", "N");
						    			if (fileList.get(i).get("cr_sayu") != null && fileList.get(i).get("cr_sayu") != ""){
						    				rst.put("cr_sayu",fileList.get(i).get("cr_sayu"));
						    			}
										else{
											rst.put("cr_sayu",etcData.get("sayu"));
										}
						    			rst.put("cm_info",rs2.getString("cm_info"));
						    			if (fileList.get(i).get("reqcd").equals("03") || fileList.get(i).get("reqcd").equals("04") || fileList.get(i).get("reqcd").equals("09")) {
						    				if (rs2.getInt("cr_lstver")== 0){
						    					rst.put("reqcd","03");
						    				}
						    				else{
						    					rst.put("reqcd","04");
						    				}
						    			}
						    			else{
						    				rst.put("reqcd",fileList.get(i).get("reqcd"));
						    			}
						    			rst.put("cr_acptno",fileList.get(i).get("cr_acptno"));
						    			if (verYN.equals("Y")) {
							    			if (rs2.getInt("cr_lstver") >= rs2.getInt("vercnt")) {
											   rst.put("cr_aftver", "1");
											}
							    			else{
							    				rst.put("cr_aftver", Integer.toString(rs2.getInt("cr_lstver")+1));
							    			}
						    			} else {
						    				rst.put("cr_aftver", rs2.getString("cr_lstver"));
						    			}
						    			rst.put("cr_baseno",fileList.get(i).get("cr_baseno"));
										rst.put("enable1","1");
										rst.put("selected","0");
										rst.put("pcdir", rs2.getString("cm_dirpath"));
										if (rs2.getString("cm_info").substring(44,45).equals("1") && strDevHome != null) {
											//rst.put("cm_dirpath", strDevHome + rs2.getString("cm_dirpath").replace("/", "\\"));
											for (j=0 ; svrList.size()>j ; j++) {
												if (svrList.get(j).get("cm_rsrccd").equals(rs2.getString("cr_rsrccd"))) {
													strVolPath = svrList.get(j).get("cm_volpath");// /scmtst/hyjungtest/dev
													strDirPath = rs2.getString("cm_dirpath");// /scmtst/hyjungtest/dev/kicc_flex/src

													if (strVolPath != null && strVolPath != "") {
														if (strDirPath.length()>=strVolPath.length() && strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
															rst.put("pcdir", strDevHome + strDirPath.substring(strVolPath.length()).replace("/", "\\"));
														}else{
															rst.put("pcdir", strDevHome + rs2.getString("cm_dirpath").replace("/", "\\"));
														}
													} else {
														rst.put("pcdir", strDevHome + rs2.getString("cm_dirpath").replace("/", "\\"));
													}
													break;
												}
											}
											rst.put("pcdir", rst.get("pcdir").replaceAll("\\\\\\\\", "\\\\"));
											rst.put("localdir", rst.get("pcdir").replaceAll("\\\\", "\\\\\\\\"));
										}
						    			reqCnt = addCnt + 1;
										rst.put("seq", Integer.toString(reqCnt));
						    			rtList.add(addCnt++, rst);
						    			rst = null;
					            	}
					            }
					            else {
									for (j=rtList.size()-1;j>=svCnt;j--) {
										rtList.remove(j);
									}
					            	strErr = "["+strRsrcName+"]에 대한 프로그램정보를 찾을 수가 없습니다.";
									rst = new HashMap<String,String>();
									rst.put("cr_itemid","ERROR");
									rst.put("cm_dirpath",strErr);
									rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
									rtList.add(svCnt, rst);
									rst = null;
									//ecamsLogger.error(strErr);
									ErrSw = true;
					            }
					            pstmt2.close();
					            rs2.close();
				            }
			        	}
			        }
			        rs.close();
			        pstmt.close();
				}
				if (ErrSw == false && fileList.get(i).get("cm_info").substring(8,9).equals("1") && etcData.get("ReqCD").equals("07")) {
					
			        int readCnt = 0;
				   	strQuery.setLength(0);
					strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
					strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
					strQuery.append("       d.CM_INFO,lpad(d.cm_stepsta,4,'0') prcseq,                   \n");
					strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin             \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a,cmd0011 c,cmm0036 d,cmm0020 e,cmm0020 f  \n");
				   	strQuery.append(" where c.cd_itemid=?                                      \n");
				   	strQuery.append("   and c.cd_prcitem=a.cr_itemid                           \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
				   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
				   	//strQuery.append("   and substr(d.cm_info,24,1)='1'                         \n");
					strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
					strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
				   	
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_itemid"));

		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();

		            while (rs.next()) {
		            	boolean fileSw = false;
		            	++readCnt;
		            	for (int k=0;rtList.size()>k;k++) {
		            		if (rtList.get(k).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
		            			fileSw = true;
		            			//if (rtList.get(k).get("baseitem").indexOf(fileList.get(i).get("cr_itemid"))<0) {
		            			/*
		            			if (rtList.get(k).get("baseitem").indexOf(fileList.get(i).get("baseitem"))<0) {
			            			rst = new HashMap<String,String>();
				            		rst = rtList.get(k);
				            		//rst.put("baseitem", rst.get("baseitem")+","+fileList.get(i).get("cr_itemid"));
				            		rst.put("baseitem", rtList.get(k).get("baseitem")+","+fileList.get(i).get("baseitem"));
									rtList.set(k, rst);
		            			}*/
		            			break;
		            		}
		            	}
		            	if (fileSw == false) {
			            	rst = new HashMap<String,String>();
			    			rst.put("basepath",fileList.get(i).get("cm_dirpath"));
			    			rst.put("basename",fileList.get(i).get("cr_rsrcname"));
			    			if (rs.getString("cm_info").substring(32,33).equals("1")) {
			    				rst.put("modsel","Y");
							} else {
								rst.put("modsel", "N");
							}
			    			rst.put("cm_dirpath",rs.getString("cm_dirpath"));
			    			rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
			    			if (rs.getString("cr_story") != null && rs.getString("cr_story") != "")
			    				rst.put("cr_story",rs.getString("cr_story"));
			    			rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
			    			rst.put("jawon", rs.getString("jawon"));
			    			if (fileList.get(i).get("reqcd").equals("03") || fileList.get(i).get("reqcd").equals("04") || fileList.get(i).get("reqcd").equals("09")){
			    				rst.put("checkin", rs.getString("checkin"));
			    			}
			    			else{
			    				rst.put("checkin", fileList.get(i).get("checkin"));
			    			}
			    			rst.put("prcseq", rs.getString("prcseq"));
			    			rst.put("cr_lstver",rs.getString("cr_lstver"));
			    			rst.put("cr_itemid",rs.getString("cr_itemid"));
			    			rst.put("sysgb", fileList.get(i).get("sysgb"));
			    			rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
			    			rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
			    			rst.put("cr_langcd",rs.getString("cr_langcd"));
			    			rst.put("cr_dsncd",rs.getString("cr_dsncd"));
			    			rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
			    			//rst.put("baseitem",fileList.get(i).get("cr_itemid"));
			    			rst.put("baseitem",fileList.get(i).get("baseitem"));
			    			rst.put("cm_info",rs.getString("cm_info"));
			    			rst.put("cr_acptno",fileList.get(i).get("cr_acptno"));
			    			rst.put("sortgbn", "1");
			    			if (fileList.get(i).get("cr_sayu") != null && fileList.get(i).get("cr_sayu") != ""){
			    				rst.put("cr_sayu",fileList.get(i).get("cr_sayu"));
			    			}
							else{
								rst.put("cr_sayu",etcData.get("sayu"));
							}
			    			if (fileList.get(i).get("reqcd").equals("03") || fileList.get(i).get("reqcd").equals("04") || fileList.get(i).get("reqcd").equals("09")) {
			    				if (rs.getInt("cr_lstver")== 0){
			    					rst.put("reqcd","03");
			    				}
			    				else{
			    					rst.put("reqcd","04");
			    				}
			    			}
			    			else{
			    				rst.put("reqcd",fileList.get(i).get("reqcd"));
			    			}
			    			if (verYN.equals("Y")) {
				    			if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
								   rst.put("cr_aftver", "1");
								}
				    			else{
				    				rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
				    			}
			    			} else {
			    				rst.put("cr_aftver", rs.getString("cr_lstver"));
			    			}
			    			rst.put("cr_baseno",fileList.get(i).get("cr_baseno"));
							rst.put("enable1","1");
							rst.put("selected","0");
							rst.put("pcdir", rs.getString("cm_dirpath"));
							if (rs.getString("cm_info").substring(44,45).equals("1") && strDevHome != null) {
								//rst.put("cm_dirpath", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
								for (j=0 ; svrList.size()>j ; j++) {
									if (svrList.get(j).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
										strVolPath = svrList.get(j).get("cm_volpath");// /scmtst/hyjungtest/dev
										strDirPath = rs.getString("cm_dirpath");// /scmtst/hyjungtest/dev/kicc_flex/src

										if (strVolPath != null && strVolPath != "") {
											if (strDirPath.length()>=strVolPath.length() && strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
												rst.put("pcdir", strDevHome + strDirPath.substring(strVolPath.length()).replace("/", "\\"));
											}else{
												rst.put("pcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
											}
										} else {
											rst.put("pcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
										}
										break;
									}
								}
								rst.put("pcdir", rst.get("pcdir").replaceAll("\\\\\\\\", "\\\\"));
								rst.put("localdir", rst.get("pcdir").replaceAll("\\\\", "\\\\\\\\"));
							}
			    			reqCnt = addCnt + 1;
							rst.put("seq", Integer.toString(reqCnt));
			    			rtList.add(addCnt++, rst);
			    			rst = null;
		            	}
		            }
		            pstmt.close();
		            rs.close();

		            if (readCnt == 0) {
						for (j=rtList.size()-1;j>=svCnt;j--) {
							rtList.remove(j);
						}

						strErr = "["+strRsrcName+"]에 대한 실행모듈정보를 찾을 수가 없습니다.";
				    	rst = new HashMap<String,String>();
						rst.put("cr_itemid","ERROR");
						rst.put("cm_dirpath",strErr);
						rst.put("cr_rsrcname",strRsrcName);
						rtList.add(svCnt, rst);
						rst = null;
						//ecamsLogger.error(strErr);
						ErrSw = true;
		            }
				}    // 실행모듈체크 처리 end
				
				//ecamsLogger.error(rtList.get(i).get("cr_itemid")+","+rtList.get(i).get("baseitem"));
			}
			conn.close();
			/*
			for (i=0;rtList.size()>i;i++) {
				for (j=i+1;rtList.size()>j;j++) {
					if (!rtList.get(i).get("cr_itemid").equals("ERROR")) {
						if (rtList.get(i).get("cr_itemid").equals(rtList.get(j).get("cr_itemid"))){
							rtList.remove(j--);
						}
					}
				}
			}*/
			//rtObj =  rtList.toArray();
			//ecamsLogger.error("+++++++++CHECK-IN LIST E N D+++"+rtList.toString());

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getDownFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getDownFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rtList != null) rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getAnalFileList(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		HashMap<String, String>			  rst		  = null;

		ArrayList<HashMap<String, String>> svrList = new ArrayList<HashMap<String, String>>();

		int                 i = 0;                                                                                               
		int                 j = 0;
		int                 k = 0;
		int               ret = 0;
		String       shFileName = "";
		String       strParm = "";
	    int          prcseq = 0;
		try {
			for (i=0;fileList.size()>i;i++) {				
				if (fileList.get(i).get("cm_info").substring(33,34).equals("1")) {   //재컴파일대상					
					shFileName = etcData.get("userid")+ fileList.get(i).get("cr_itemid") +"_analcall.sh";
					strParm = "./ecams_analcall RELAT " + fileList.get(i).get("cr_itemid")+ " "+ etcData.get("userid");
					ret = execShell(shFileName, strParm, false);
					if (ret != 0) {
						throw new Exception("관련모듈추출모듈 Call실패. run=["+strParm +"]" + " return=[" + ret + "]" );
					}
					
				}
			}
			conn = connectionContext.getConnection();
			String strVolPath = "";
			String strDirPath = "";
			String strDevHome = "";

        	boolean fileSw = false;
        	boolean findSw = false;
			SysInfo sysinfo = new SysInfo();
			
			if (etcData.get("localyn").equals("Y")) {
				strQuery.setLength(0);
				strQuery.append("select cd_devhome from cmd0300               \n");
				strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, etcData.get("syscd"));
				pstmt.setString(2, etcData.get("userid"));
				rs = pstmt.executeQuery();
				if (rs.next()) {
					strDevHome = rs.getString("cd_devhome");
				}
				rs.close();
				pstmt.close();

				svrList = sysinfo.getHomePath_conn(etcData.get("syscd"), conn);
			}
			sysinfo = null;
			rtList.clear();
			for (i=0;fileList.size()>i;i++) {
				rst = new HashMap<String,String>();
				rst = fileList.get(i);
				rtList.add(rst);
				rst = null;
				fileSw = false;
				if (fileList.get(i).get("cm_info").substring(33,34).equals("1")) {   //재컴파일대상
					if (prcseq==0) {
						prcseq = Integer.parseInt(fileList.get(i).get("prcseq"));
					}
					strQuery.setLength(0);
					strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
					strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon,a.cr_story, \n");
					strQuery.append("       d.CM_INFO,lpad(d.cm_stepsta,4,'0') prcseq,                   \n");
					strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_codename checkin             \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a,cmd0010 c,cmm0036 d,cmm0020 e,cmm0020 f  \n");
				   	strQuery.append(" where c.cd_itemid=?                                      \n");
				   	strQuery.append("   and c.cd_makeitem=a.cr_itemid                          \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
				   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
					strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
					strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode='09'         \n");
					strQuery.append(" order by c.cd_compseq                                    \n");
				   	
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_itemid"));
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();

		            while (rs.next()) {
		            	findSw = false;
		            	if (fileList.get(i).get("cr_itemid").equals(fileList.get(i).get("baseitem"))) {
	            			if (fileList.get(i).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
	            				for (k=0;rtList.size()>k;k++) {
	            					if (rtList.get(k).get("cr_itemid").equals(rs.getString("cr_itemid")) &&
	            						rtList.get(k).get("cr_itemid").equals(rtList.get(k).get("baseitem"))) {
			            				rst = new HashMap<String,String>();
			            				rst = rtList.get(k);
			            				rst.put("prcseq", Integer.toString(prcseq));
		    			    			rst.put("modsel", "N");
		    			    			rst.put("enable1","0");
			    						rst.put("selected","1");	
			    		    			rst.put("analsw", "Y");  
			    		    			if (!fileSw) {
			    			    			rst.put("basepath",fileList.get(i).get("cm_dirpath"));
			    			    			rst.put("basename",fileList.get(i).get("cr_rsrcname"));
			    			    			fileSw = true;
			    		    			}
			            				rtList.add(rst);
			            				rtList.remove(k);
			            				rst = null;
			            				findSw = true;
			            				break;
	            					}
		            			}
		            		}
		            	}
		            	if (!findSw) {
			            	rst = new HashMap<String,String>();
			            	if (!fileSw) {
    			    			rst.put("basepath",fileList.get(i).get("cm_dirpath"));
    			    			rst.put("basename",fileList.get(i).get("cr_rsrcname"));
    			    			fileSw = true;
    		    			}
							rst.put("modsel", "N");
			    			rst.put("cm_dirpath",rs.getString("cm_dirpath"));
			    			rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
			    			if (rs.getString("cr_story") != null && rs.getString("cr_story") != "") {
			    				rst.put("cr_story",rs.getString("cr_story"));
			    			}
			    			rst.put("cm_jobname", fileList.get(i).get("cm_jobname"));
			    			rst.put("jawon", rs.getString("jawon"));
			    			rst.put("reqcd","09");
			    			rst.put("analsw", "Y");
			    			rst.put("sortgbn", "1");
			    			rst.put("prcseq", Integer.toString(prcseq));
			    			rst.put("cr_lstver",rs.getString("cr_lstver"));
			    			rst.put("cr_itemid",rs.getString("cr_itemid"));
			    			rst.put("sysgb", fileList.get(i).get("sysgb"));
			    			rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
			    			rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
			    			rst.put("cr_langcd",rs.getString("cr_langcd"));
			    			rst.put("cr_dsncd",rs.getString("cr_dsncd"));
			    			rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
			    			rst.put("baseitem",fileList.get(i).get("cr_itemid"));
			    			rst.put("cm_info",rs.getString("cm_info"));
			    			rst.put("checkin", rs.getString("checkin"));
			    			rst.put("cr_acptno",fileList.get(i).get("cr_acptno"));
			    			if (fileList.get(i).get("cr_sayu") != null && fileList.get(i).get("cr_sayu") != ""){
			    				rst.put("cr_sayu",fileList.get(i).get("cr_sayu"));
			    			}
							else{
								rst.put("cr_sayu",etcData.get("sayu"));
							}
			    			rst.put("cr_aftver", rs.getString("cr_lstver"));
			    			rst.put("cr_baseno",fileList.get(i).get("cr_baseno"));
	
							rst.put("enable1","1");
							rst.put("selected","0");
							
							if (rs.getString("cm_info").substring(44,45).equals("1") && strDevHome != null) {
								//rst.put("cm_dirpath", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
								for (j=0 ; svrList.size()>j ; j++) {
									if (svrList.get(j).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
										strVolPath = svrList.get(j).get("cm_volpath");// /scmtst/hyjungtest/dev
										strDirPath = rs.getString("cm_dirpath");// /scmtst/hyjungtest/dev/kicc_flex/src
	
										if (strVolPath != null && strVolPath != "") {
											if (strDirPath.length()>=strVolPath.length() && strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
												rst.put("pcdir", strDevHome + strDirPath.substring(strVolPath.length()).replace("/", "\\"));
											}else{
												rst.put("pcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
											}
										} else {
											rst.put("pcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
										}
										break;
									}
								}
								rst.put("pcdir", rst.get("pcdir").replaceAll("\\\\\\\\", "\\\\"));
								rst.put("localdir", rst.get("pcdir").replaceAll("\\\\", "\\\\\\\\"));
							}
			    			rtList.add(rst);
			    			rst = null;
		            	}
			            ++prcseq;
		            }
		            pstmt.close();
		            rs.close();
				}
			}
			rs = null;
			pstmt = null;
			conn.close();
			//ecamsLogger.error("++++ rtList+++"+rtList.toString());
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getAnalFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getAnalFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getAnalFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getAnalFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rtList != null) rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getRelatFileList(String UserId,String srID,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ConnectionContext connectionContext = new ConnectionResource();
		ResultSet         rs          = null;
		HashMap<String, String>			  rst		  = null;

		int                 i = 0;   
		int                 j = 0;    
		String    strItemId   = "";
		String    strAcptNo   = "";
		boolean   findSw      = false;
		boolean   okSw        = false;
		try {
			conn = connectionContext.getConnection();
			rtList.clear();
			for (i=0;fileList.size()>i;i++) {
				okSw = false;
				strQuery.setLength(0);
				strQuery.append("select j.cr_rsrcname,j.cr_realdep,j.cr_lstver,a.cr_itemid,          \n");
				strQuery.append("       e.cm_dirpath,f.cm_codename status,c.cr_acptno,c.cr_editor,   \n");
				strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') acptdate,         \n");
				strQuery.append("       g.cm_username,b.cr_version,h.cc_srid,h.cc_reqtitle           \n");
				if (!fileList.get(i).get("cr_realver").equals("0")) {
				   	strQuery.append("  from cmr0020 j,cmr1070 i,cmc0100 h,cmm0040 g,cmm0020 f,cmm0070 e,cmr1010 d,cmr0020 a,cmr1010 b,cmr1000 c  \n");
				   	strQuery.append(" where a.cr_itemid=?                                      \n");
				   	strQuery.append("   and a.cr_realacpt=d.cr_acptno                          \n");
				   	strQuery.append("   and a.cr_itemid=d.cr_itemid                            \n");
				   	strQuery.append("   and a.cr_itemid=b.cr_itemid                            \n");
				   	strQuery.append("   and b.cr_status<>'3' and b.cr_prcdate is not null      \n");
				   	strQuery.append("   and b.cr_qrycd not in ('05','09')                      \n");
				   	strQuery.append("   and b.cr_acptno=c.cr_acptno                            \n");
				   	strQuery.append("   and c.cr_qrycd='07'                                    \n");
				   	strQuery.append("   and c.cr_status<>'3' and c.cr_prcdate is not null      \n");
				   	strQuery.append("   and c.cr_acptno>d.cr_veracpt                           \n");
				   	strQuery.append("   and b.cr_acptno=i.cr_acptno                            \n");
				   	strQuery.append("   and b.cr_itemid=i.cr_itemid and i.cr_baseitem=j.cr_itemid  \n");
				   	strQuery.append("   and j.cr_syscd=e.cm_syscd and j.cr_dsncd=e.cm_dsncd    \n");
					strQuery.append("   and f.cm_macode='CMR0020' and f.cm_micode=j.cr_status  \n");
					strQuery.append("   and c.cr_editor=g.cm_userid                            \n");
				} else {
				   	strQuery.append("  from cmr0020 j,cmr1070 i,cmc0100 h,cmm0040 g,cmm0020 f,cmm0070 e,cmr0020 a,cmr1010 b,cmr1000 c  \n");
				   	strQuery.append(" where a.cr_itemid=?                                      \n");
				   	strQuery.append("   and a.cr_itemid=b.cr_itemid                            \n");
				   	strQuery.append("   and b.cr_status<>'3' and b.cr_prcdate is not null      \n");
				   	strQuery.append("   and b.cr_qrycd not in ('05','09')                      \n");
				   	strQuery.append("   and b.cr_acptno=c.cr_acptno                            \n");
				   	strQuery.append("   and c.cr_qrycd='07'                                    \n");
				   	strQuery.append("   and c.cr_status<>'3' and c.cr_prcdate is not null      \n");
				   	strQuery.append("   and b.cr_acptno=i.cr_acptno                            \n");
				   	strQuery.append("   and b.cr_itemid=i.cr_itemid and i.cr_baseitem=j.cr_itemid  \n");
				   	strQuery.append("   and j.cr_syscd=e.cm_syscd and j.cr_dsncd=e.cm_dsncd    \n");
					strQuery.append("   and f.cm_macode='CMR0020' and f.cm_micode=j.cr_status  \n");
					strQuery.append("   and c.cr_editor=g.cm_userid                            \n");
					strQuery.append("   and c.cr_itsmid=h.cc_srid                              \n");
				}
				strQuery.append(" order by c.cr_prcdate,b.cr_itemid                            \n");			   	
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt =  new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, fileList.get(i).get("cr_itemid"));
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	if (strAcptNo.length() ==0) findSw = true;
	            	else if (strItemId.length() == 0) findSw = true;
	            	else if (!strAcptNo.equals(rs.getString("cr_acptno")) || !strItemId.equals(rs.getString("cr_itemid"))) {
	            		findSw = true;
	            	}
	            	rst = new HashMap<String,String>();
	            	if (!srID.equals(rs.getString("cc_srid")) || !UserId.equals(rs.getString("cr_editor"))) {
	            		rst.put("colorsw","3"); 
	            		okSw = true;
	            	} else {
	            		rst.put("colorsw","0"); 
	            	}
	            	if (findSw) {
	            		strAcptNo = rs.getString("cr_acptno");
	            		strItemId = rs.getString("cr_itemid");
	            		
	            		rst.put("moddir", fileList.get(i).get("cm_dirpath"));
	            		rst.put("modname", fileList.get(i).get("cr_rsrcname"));
	            		rst.put("acptdate", rs.getString("acptdate"));
	            		rst.put("editor", rs.getString("cm_username"));
	            		rst.put("srid", rs.getString("cc_srid"));
	            		rst.put("srtitle", rs.getString("cc_reqtitle"));
	            	}
	            	rst.put("srcdir", rs.getString("cm_dirpath"));
	            	rst.put("rsrcname", rs.getString("cr_rsrcname"));
	            	rst.put("status", rs.getString("status"));
            		rst.put("cr_itemid", rs.getString("cr_itemid"));

					rst.put("enable1","1");
					rst.put("selected","1");
	    			rtList.add(rst);
	    			rst = null;
	            }
	            pstmt.close();
	            rs.close();
	            
	            if (!okSw) {
	            	for (j=0;rtList.size()>j;j++) {
	            		if (rtList.get(j).get("cr_itemid").equals(fileList.get(i).get("cr_itemid"))) {
	            			rtList.remove(j--);
	            		}
	            	}
	            }
			}
			rs = null;
			pstmt = null;
			conn.close();

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getRelatFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getRelatFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getRelatFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getRelatFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rtList != null) rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getRelatFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getDownFileList_Deploy(ArrayList<HashMap<String,String>> fileList,HashMap<String,String> etcData) throws SQLException, Exception {
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		Connection        conn        = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt       = null;
		ConnectionContext connectionContext = new ConnectionResource();
		HashMap<String, String>			  rst		  = null;
		int               reqCnt      = 0;
		int               addCnt      = 0;
		int               i = 0;
		int               j = 0;
//		int               svCnt = 0;
		int               parmCnt = 0;
//		boolean           findSw = false;

		try {
			conn = connectionContext.getConnection();

			rtList.clear();
			for (i=0;fileList.size()>i;i++) {
				rst = new HashMap<String,String>();
				rst = fileList.get(i);
				reqCnt = addCnt + 1;
				rst.put("seq", Integer.toString(reqCnt));
				rtList.add(addCnt++, rst);
//				svCnt = addCnt - 1;
				rst = null;
				if (fileList.get(i).get("cm_info").substring(3,4).equals("1") || fileList.get(i).get("cm_info").substring(8,9).equals("1")) {
		        	parmCnt = 0;
//		        	findSw = false;
		        	strQuery.setLength(0);
					strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_lstver,a.cr_itemid, \n");
					strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,e.CM_CODENAME as jawon, \n");
					strQuery.append("       d.CM_INFO,lpad(d.cm_prcstep,4,'0') prcseq,         \n");
					strQuery.append("       nvl(d.cm_vercnt,50) vercnt,f.cm_jobname,           \n");
					strQuery.append("       a.cr_devver,a.cr_devdep,a.cr_syscd,                \n");
					strQuery.append("       nvl(a.cr_testver,0) cr_testver,nvl(a.cr_testdep,0) cr_testdep,\n");
					strQuery.append("       nvl(a.cr_realver,0) cr_realver,nvl(a.cr_realdep,0) cr_realdep,\n");
					strQuery.append("       a.cr_status,a.cr_editor,c.cr_qrycd,c.cr_version,c.cr_story \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a,cmm0036 d,cmm0020 e,cmm0102 f,cmr1010 c  \n");
				   	strQuery.append(" where c.cr_acptno=? and c.cr_baseitem=?                  \n");
				   	strQuery.append("   and c.cr_itemid<>c.cr_baseitem                         \n");
				   	strQuery.append("   and c.cr_qrycd<>'09'                                   \n");
				   	strQuery.append("   and c.cr_itemid=a.cr_itemid                            \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd    \n");
				   	strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd  \n");
					strQuery.append("   and e.cm_macode='JAWON' and e.cm_micode=a.cr_rsrccd    \n");
					strQuery.append("   and a.cr_jobcd=f.cm_jobcd                              \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
		            pstmt.setString(++parmCnt, fileList.get(i).get("cr_acptno"));
		            pstmt.setString(++parmCnt, fileList.get(i).get("cr_itemid"));
				    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
		            while (rs.next()) {
		            	boolean fileSw = false;
		            	for (int k=0;rtList.size()>k;k++) {
		            		if (rtList.get(k).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
		            			/*rst = new HashMap<String,String>();
		            			rst = rtList.get(k);
		            			rst.put("baseitem", rst.get("baseitem")+rs.getString("cr_itemid"));
		            			*/
		            			fileSw = true;
		            		}
		            	}
		            	if (!fileSw) {
			            	rst = new HashMap<String,String>();
			            	rst = new HashMap<String, String>();
			    			rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
			    			rst.put("jawon", rs.getString("jawon"));
			    			if (rs.getString("cr_story") != null) rst.put("cr_story", rs.getString("cr_story"));
			    			else  rst.put("cr_story", "");
			    			rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
			    			rst.put("cr_version", rs.getString("cr_version"));
			    			rst.put("cr_editor", rs.getString("cr_editor"));
			    			rst.put("cr_syscd", rs.getString("cr_syscd"));
			    			rst.put("cr_dsncd", rs.getString("cr_dsncd"));
			    			rst.put("cr_itemid", rs.getString("cr_itemid"));
			    			rst.put("baseitem", fileList.get(i).get("cr_itemid"));
			    			rst.put("cr_jobcd", rs.getString("cr_jobcd"));
			    			rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
			    			rst.put("basename", rs.getString("cr_rsrcname"));
			    			
			    			if (etcData.get("ReqCD").equals("05")) {
			    				rst.put("reqcd", etcData.get("ReqCd"));
			    			} else {
			    				if (rs.getString("cr_qrycd").equals("03") || rs.getString("cr_qrycd").equals("04")) {
				    				if (etcData.get("SinCd").equals("03")) {
				    					if (rs.getInt("cr_testver") == 0) rst.put("reqcd", "03");
				    					else rst.put("reqcd", "04");
				    				} else if (etcData.get("SinCd").equals("04")) {
				    					if (rs.getInt("cr_realver") == 0) rst.put("reqcd", "03");
				    					else rst.put("reqcd", "04");
				    				} else {
				    					if (rs.getInt("cr_lstver") == 0) rst.put("reqcd", "03");
				    					else rst.put("reqcd", "04");
				    				}
			    				} else {
			    					rst.put("reqcd", rs.getString("cr_qrycd"));
			    				}
			    			}
			    			if (rs.getString("cr_qrycd").equals("03") || rs.getString("cr_qrycd").equals("04")) {
				    			if (rs.getInt("vercnt") == 0) {
				    				if (etcData.get("SinCd").equals("03")) {
				    					rst.put("cr_befver", rs.getString("cr_testdep"));
				    					if (rs.getInt("cr_testver") >= 9999) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_testver")+1));
				    				} else if (etcData.get("SinCd").equals("04")) {
				    					rst.put("cr_befver", rs.getString("cr_realdep"));
				    					if (rs.getInt("cr_realver") >= 9999) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_realver")+1));
				    				} else {
				    					rst.put("cr_befver", rs.getString("cr_lstver"));
				    					if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
				    				}
				    			} else {
				    				if (etcData.get("SinCd").equals("03")) {
				    					rst.put("cr_befver", rs.getString("cr_testdep"));
				    					if (rs.getInt("cr_testver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_testver")+1));
				    				} else if (etcData.get("SinCd").equals("04")) {
				    					rst.put("cr_befver", rs.getString("cr_realdep"));
				    					if (rs.getInt("cr_realver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_realver")+1));
				    				} else {
				    					rst.put("cr_befver", rs.getString("cr_lstver"));
				    					if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) rst.put("cr_aftver", "1");
				    					else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_devver")+1));
				    				}
				    			}
			    			} else {
			    				if (etcData.get("SinCd").equals("03")) {
			    					rst.put("cr_befver", rs.getString("cr_testdep"));
			    					rst.put("cr_aftver", rs.getString("cr_testver"));
			    				} else if (etcData.get("SinCd").equals("04")) {
			    					rst.put("cr_befver", rs.getString("cr_realdep"));
			    					rst.put("cr_aftver",rs.getString("cr_realver"));
			    				} else {
			    					rst.put("cr_befver", rs.getString("cr_lstver"));
			    					rst.put("cr_aftver", rs.getString("cr_lstver"));
			    				}				    				
			    			}
			    			if (rs.getInt("cr_testdep") >0) rst.put("cr_testver", rs.getString("cr_testdep"));
			    			else rst.put("cr_testver", "0");
			    			if(rs.getInt("cr_realdep") >0) rst.put("cr_realver", rs.getString("cr_realdep"));
			    			else rst.put("cr_realver", "0");
			    			if(rs.getInt("cr_lstver") >0) rst.put("cr_devver", rs.getString("cr_lstver"));
			    			else rst.put("cr_devver", "0");
			    			
			    			rst.put("jobname", rs.getString("cm_jobname"));
			    			rst.put("cm_info", rs.getString("cm_info"));
			    			rst.put("cr_status", rs.getString("cr_status"));
			    			rst.put("prcseq", rs.getString("prcseq"));
	
			    			rst.put("cm_dirpath", rs.getString("cm_dirpath"));
			    			rst.put("view_dirpath", rs.getString("cm_dirpath"));
			    			rst.put("cr_acptno", fileList.get(i).get("cr_acptno"));
			    						
			    			
							rst.put("enable1","0");
							rst.put("selected","0");
			    			reqCnt = addCnt + 1;
							rst.put("seq", Integer.toString(reqCnt));
			    			rtList.add(addCnt++, rst);
			    			rst = null;
		            	}
		            }
		            pstmt.close();
		            rs.close();
		            /*
		            if (!findSw) {
		            	for (j=rtList.size()-1;j>=svCnt;j--) {
							rtList.remove(j);
						}
						rst = new HashMap<String,String>();
						rst.put("cr_itemid","ERROR");
						rst.put("cm_dirpath","["+fileList.get(i).get("cr_rsrcname")+"]에 대한 동시적용모듈정보가 정확하지 않습니다.");
						rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
						rtList.add(svCnt, rst);
						rst = null;
		            }*/
				}
			}
			rs = null;
			pstmt = null;			
			conn.close();
			for (i=0;rtList.size()>i;i++) {
				for (j=i+1;rtList.size()>j;j++) {
					if (!rtList.get(i).get("cr_itemid").equals("ERROR")) {
						if (rtList.get(i).get("cr_itemid").equals(rtList.get(j).get("cr_itemid"))){
							rtList.remove(j--);
						}
					}
				}
			}
			ecamsLogger.error("+++++++++getDownFileList_Deploy E N D+++"+rtList.toString());
			
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList_Deploy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.getDownFileList_Deploy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.getDownFileList_Deploy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.getDownFileList_Deploy() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rtList != null) rtList = null;
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.getDownFileList_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_Check_Bef(ArrayList<HashMap<String,String>> chkInList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		PreparedStatement pstmt3      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  i=0;
		int               j = 0;
		String            strPath     = "";
		String            strRsrcName = "";
		String            strSysCd    = "";
		String            strErMsg    = "";

		try {
			conn = connectionContext.getConnection();
			ArrayList<HashMap<String, String>> svrList = new ArrayList<HashMap<String, String>>();
			SysInfo sysinfo = new SysInfo();
			svrList = sysinfo.getHomePath_Relat_conn(chkInList.get(i).get("cr_syscd"), conn);
			if (svrList.size() == 0) {
				strErMsg = "ERROR시스템별 홈디렉토리정보를 찾을 수가 없습니다. [관리자연락요망]";
			} else {
		        for (i=0;i<chkInList.size();i++){
		        	if (chkInList.get(i).get("baseitem").equals(chkInList.get(i).get("cr_itemid"))) {
		        		strPath = chkInList.get(i).get("cm_dirpath");
		        		strSysCd = svrList.get(0).get("basesys");
		        		for (j=0;svrList.size()>j;j++) {
		        			if (chkInList.get(i).get("cr_rsrccd").equals(svrList.get(j).get("cm_rsrccd"))) {
				        		if (!svrList.get(j).get("cm_volpath").equals(svrList.get(j).get("basehome"))) {
				        			if (strPath.substring(0,svrList.get(j).get("cm_volpath").length()).equals(svrList.get(j).get("cm_volpath"))) {
				        				strPath = svrList.get(j).get("basehome") + strPath.substring(svrList.get(j).get("cm_volpath").length());
				        			}
				        		} else {
				        			strPath = chkInList.get(i).get("cm_dirpath");
				        		}
				        		break;
		        			}
			        	}
			        	if (strPath.length() == 0) {
			        		strErMsg = "ERROR : 프로그램유형에 대한 홈디렉토리정보가 부정확합니다. [" + chkInList.get(i).get("cr_rsrcname")+"]";
			        		break;
			        	} else {
			        		strRsrcName = chkInList.get(i).get("cr_rsrcname");
			        		strQuery.setLength(0);
				        	strQuery.append("select a.cr_status,a.cr_lstver          \n");
				        	strQuery.append("  from cmr0020 a,cmm0070 b              \n");
				        	strQuery.append(" where a.cr_syscd=? and a.cr_rsrcname=? \n");
				        	strQuery.append("   and a.cr_syscd=b.cm_syscd            \n");
				        	strQuery.append("   and a.cr_dsncd=b.cm_dsncd            \n");
				        	strQuery.append("   and b.cm_dirpath=?                   \n");
				        	pstmt = conn.prepareStatement(strQuery.toString());
				        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
				        	pstmt.setString(1, strSysCd);
				        	pstmt.setString(2, strRsrcName);
				        	pstmt.setString(3, strPath);
				        	//ecamsLogger.error("[Cmr0200.request_Check_Bef] strSysCd : " + strSysCd + ", strRsrcName : " + strRsrcName + ", strPath : " + strPath);
				        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				        	rs = pstmt.executeQuery();
				        	if (rs.next()) {
				        		if (rs.getInt("cr_lstver")>0 && !rs.getString("cr_status").equals("5")) {
				        			if (strErMsg.length() > 0) strErMsg = strErMsg + ",";
				        			else strErMsg = "[" + svrList.get(0).get("cm_sysmsg") + "]에서 체크아웃하지 않은 파일이 존재합니다. \n"
				        			                + "계속 진행할까요? \n"
				        			                + "(";
				        			strErMsg = strErMsg + chkInList.get(i).get("cr_rsrcname");
				        		}
				        	}
				        	rs.close();
				        	pstmt.close();
			        	}

		        	}
		        }
			}
        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;

        	if (strErMsg.length()==0) strErMsg = "OK";
        	else if (!strErMsg.substring(0,5).equals("ERROR")) {
        		strErMsg = strErMsg + ")";
        	}
        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Check_Bef() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Check_Bef() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Check_Bef() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Check_Bef() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_Bef() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String request_Check_In(ArrayList<HashMap<String,String>> chkInList,HashMap<String,String> etcData,
			ArrayList<HashMap<String,Object>> ConfList,String confFg,ArrayList<HashMap<String,String>> scriptList) throws SQLException, Exception {
			
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		String			  AcptNo	  = null;
		int				  i=0;
		int               j = 0;

		try {
			conn = connectionContext.getConnection();

			SysInfo sysinfo = new SysInfo();
			//신청 통제 시간인지 확인 작업
			if (sysinfo.getSysStopChk(etcData.get("UserID"), chkInList.get(0).get("cr_syscd")) == 1){
	        	if (conn != null){
	        		conn.close();
	        		conn = null;
	        	}
	        	return "ERROR[이관통제을 위하여 일시적으로 형상관리 사용을 중지합니다.(체크인신청)]";
			}


	        for (i=0;i<chkInList.size();i++){
	        	AcptNo = bldcdChk(chkInList.get(i).get("cr_syscd"),chkInList.get(i).get("cr_jobcd"),chkInList.get(i).get("cr_rsrccd"),chkInList.get(i).get("cm_info"),etcData.get("ReqCD"),chkInList.get(i).get("reqcd"),chkInList.get(i).get("cr_itemid"),ConfList,conn);
				if (AcptNo != "" && AcptNo != null) AcptNo = "ERROR[" + chkInList.get(i).get("cr_rsrcname")+"]에 대하여 " + AcptNo;
				else AcptNo = null;

	        	if (AcptNo == null) {
		        	strQuery.setLength(0);
		        	strQuery.append("select a.cr_status,a.cr_editor,a.cr_nomodify,b.cm_codename \n");
		        	strQuery.append("  from cmr0020 a,cmm0020 b,cmm0036 c                       \n");
		        	strQuery.append(" where a.cr_itemid = ?                                     \n");
		        	strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_status   \n");
		        	strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_rsrccd=c.cm_rsrccd   \n");

		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt.setString(1, chkInList.get(i).get("cr_itemid"));
		        	rs = pstmt.executeQuery();
		        	if (rs.next()){
		        		if (etcData.get("ReqCD").equals("05")) {//체크인요청
		        			if (chkInList.get(i).get("reqcd").equals("03")) {
		        				if (chkInList.get(i).get("cr_itemid").equals(chkInList.get(i).get("baseitem"))) {
		        				   if (!rs.getString("cr_status").equals("3")) {
		        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[1] [" + rs.getString("cm_codename") +"]";
		        				   }
		        				} else {
		        				   if (!rs.getString("cr_status").equals("0") && !rs.getString("cr_status").equals("3")) {
		        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[2] [" + rs.getString("cm_codename") +"]";
		        				   }
		        				}
		        			} else if (chkInList.get(i).get("reqcd").equals("04")) {
		        				if (chkInList.get(i).get("cr_itemid").equals(chkInList.get(i).get("baseitem"))) {
		        				   if (!rs.getString("cr_status").equals("5")) {
		        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[4] [" + rs.getString("cm_codename") +"]";
		        				   }
		        				} else {
		        				   if (!rs.getString("cr_status").equals("0") && !rs.getString("cr_status").equals("5")) {
		        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[5] [" + rs.getString("cm_codename") +"]";
		        				   }
		        				}
		        			} else if (chkInList.get(i).get("reqcd").equals("09")) {
	        				   if (!rs.getString("cr_status").equals("0") && !rs.getString("cr_status").equals("5")) {
	        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[6] [" + rs.getString("cm_codename") +"]";
	        				   }
	        				   if (rs.getString("cr_status").equals("0") && !rs.getString("cr_nomodify").equals("0")) {
	        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 현재 무수정요청 중인 상태입니다.[8] ";
	        				   }
			        		} else if (chkInList.get(i).get("reqcd").equals("05")) {
		        				   if (!rs.getString("cr_status").equals("0")) {
		        					   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[9] [" + rs.getString("cm_codename") +"]";
		        				   }
				        		}
		        		} else if (etcData.get("ReqCD").equals("06")) {//롤벡신청
		        			if (!rs.getString("cr_status").equals("0")) {
	        				   AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[10] [" + rs.getString("cm_codename") +"]";
	        				}
		        		}
		        	}
		        	rs.close();
		        	pstmt.close();
	        	}
	        }

	        if (AcptNo != null) {
	        	if (conn != null) conn.close();
	        	return AcptNo;
	        }

	        String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
	        Cmr0200 cmr0200 = new Cmr0200();
	        //ArrayList<HashMap<String,Object>> conflist = null;
	        int wkC = chkInList.size()/300;
	        int wkD = chkInList.size()%300;
	        if (wkD>0) wkC = wkC + 1;
            String svAcpt[] = null;
            svAcpt = new String [wkC];
            for (j=0;wkC>j;j++) {
            	do {
    		        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

    		        i = 0;
    		        strQuery.setLength(0);
    		        strQuery.append("select count(*) as cnt from cmr1000 \n");
    	        	strQuery.append(" where cr_acptno= ?                 \n");

    	        	pstmt = conn.prepareStatement(strQuery.toString());
    	        	pstmt.setString(1, AcptNo);

    	        	rs = pstmt.executeQuery();

    	        	if (rs.next()){
    	        		i = rs.getInt("cnt");
    	        	}
    	        	rs.close();
    	        	pstmt.close();
    	        } while(i>0);
            	svAcpt[j] = AcptNo;
            }
        	int    seq = 0;
        	String retMsg = "";
            autoseq = null;
            conn.setAutoCommit(false);
        	boolean insSw = false;
        	for (i=0;i<chkInList.size();i++){
        		insSw = false;
        		if (i == 0) insSw = true;
        		else {
        			wkC = i%300;
        			if (wkC == 0) insSw = true;
        		}
        		if (insSw == true) {
        			if (i>=300) {
        				retMsg = cmr0200.request_Confirm(AcptNo,chkInList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
        				if (!retMsg.equals("OK")) {
        					conn.rollback();
        					conn.close();
        					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
        				}
        			}
        			wkC = i/300;
        			AcptNo = svAcpt[wkC];
        			pstmtcount = 1;
        			strQuery.setLength(0);
                	strQuery.append("insert into cmr1000 \n");
                	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, \n");
                	strQuery.append("CR_PASSOK,CR_PASSCD,CR_BEFJOB,CR_EMGCD,CR_EDITOR,CR_SAYU,CR_PASSSUB,            \n");
                	strQuery.append("CR_ECLIPSE,CR_ITSMID,CR_SVRYN,CR_VERSION,CR_ITSMTITLE) values 		   \n");
                	strQuery.append("(?,?,?,?,sysdate,'0',?,?,  ?,?,'N',?,?,?,?,  ?,?,?,?,?)     \n");

                	pstmt = conn.prepareStatement(strQuery.toString());
                	//pstmt = new LoggableStatement(conn,strQuery.toString());
                	pstmt.setString(pstmtcount++, AcptNo);
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("cr_syscd"));
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("sysgb"));
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("cr_jobcd"));
                	pstmt.setString(pstmtcount++, strTeam);
                	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));

                	pstmt.setString(pstmtcount++, etcData.get("Deploy"));
                	pstmt.setString(pstmtcount++, etcData.get("Sayu"));
                	pstmt.setString(pstmtcount++, etcData.get("EmgCd"));
                	pstmt.setString(pstmtcount++, etcData.get("UserID"));
                	pstmt.setString(pstmtcount++, etcData.get("txtSayu"));
                	pstmt.setString(pstmtcount++, etcData.get("PassCd"));
                	pstmt.setString(pstmtcount++, etcData.get("outpos"));
                	pstmt.setString(pstmtcount++, etcData.get("srid"));
                	if (etcData.get("svryn") != null && etcData.get("svryn") != "") pstmt.setString(pstmtcount++, etcData.get("svryn"));
                	else pstmt.setString(pstmtcount++, "Y");
                	if (etcData.get("veryn") != null && etcData.get("veryn") != "") pstmt.setString(pstmtcount++, etcData.get("veryn"));
                	else pstmt.setString(pstmtcount++, "Y");
                	pstmt.setString(pstmtcount++, etcData.get("cc_reqtitle"));
                	
                	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                	pstmt.executeUpdate();

                	pstmt.close();
                	seq = 0;
        		}


        		strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD,  \n");
            	strQuery.append("CR_RSRCCD,CR_LANGCD,CR_DSNCD,CR_RSRCNAME,CR_RSRCNAM2,CR_SRCCHG,CR_SRCCMP, \n");
            	strQuery.append("CR_PRIORITY,CR_VERSION,CR_BEFVER,CR_CONFNO,CR_EDITOR,CR_DSNCD2,   \n");
            	strQuery.append("CR_BASENO,CR_BASEITEM,CR_ITEMID,CR_COACPT,CR_BASEPGM,CR_SVRYN,CR_VERYN,CR_STORY) values (    \n");
            	strQuery.append("?,?,?,?,?,'0',?,  ?,?,?,?,?,?,'Y',  ?,?,?,?,?,?,  ?,?,?,?,?,?,?,?)      \n");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn,strQuery.toString());

            	//ecamsLogger.error("++++++reqcd,rsrccd++++++"+chkInList.get(i).get("reqcd")+","
            	//		+chkInList.get(i).get("cr_rsrccd"));

            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, ++seq);
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("sysgb"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_jobcd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("reqcd"));
            	
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_langcd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrcname"));
            	if (chkInList.get(i).get("reqcd").equals("04") || chkInList.get(i).get("reqcd").equals("03")) {
            		pstmt.setString(pstmtcount++,"1");
            	} else {
            		pstmt.setString(pstmtcount++,"0");
            	}
            	
            	pstmt.setInt(pstmtcount++, Integer.parseInt(chkInList.get(i).get("prcseq")));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_aftver"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_lstver"));

            	if (etcData.get("ReqCD").equals("05")) {
            		if (chkInList.get(i).get("cr_acptno") != "" && chkInList.get(i).get("cr_acptno") != null) {

            			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_acptno"));

            		} else {

            			pstmt.setString(pstmtcount++,AcptNo);

            		}
            	} else {
            		pstmt.setString(pstmtcount++,chkInList.get(i).get(""));
            	}
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("pcdir"));

            	if (chkInList.get(i).get("reqcd").equals("05") || chkInList.get(i).get("reqcd").equals("09")) {
            		pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_baseno"));
            	} else {
	        		if (chkInList.get(i).get("cr_acptno") != null && chkInList.get(i).get("cr_acptno") != "" &&
	        			chkInList.get(i).get("cr_acptno").substring(4,6).equals("01") &&
	            		chkInList.get(i).get("cr_acptno").substring(4,6).equals("02")) {
	        			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_acptno"));
	        		} else if (chkInList.get(i).get("cr_acptno") != null && chkInList.get(i).get("cr_acptno") != "" &&
	        				!chkInList.get(i).get("cr_aftver").equals("0")) {
		        			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_baseno"));
		        	}else {
	        			pstmt.setString(pstmtcount++,chkInList.get(i).get(""));
	        		}
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("baseitem").substring(0,12));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_itemid"));
            	if (chkInList.get(i).get("reqcd").equals("09")) {
            		pstmt.setString(pstmtcount++, selBaseno(chkInList.get(i).get("cr_itemid"),conn));
            	} else {
            		pstmt.setString(pstmtcount++, AcptNo); 
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("baseitem"));

            	if (etcData.get("svryn") != null && etcData.get("svryn") != "") pstmt.setString(pstmtcount++, etcData.get("svryn"));
            	else pstmt.setString(pstmtcount++, "Y");
            	if (etcData.get("veryn") != null && etcData.get("veryn") != "") pstmt.setString(pstmtcount++, etcData.get("veryn"));
            	else pstmt.setString(pstmtcount++, "Y");

            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_story"));
            	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
        	}

            retMsg = request_Confirm(AcptNo,chkInList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
			//ecamsLogger.error("+++++++++CHECK-IN LIST Update START (cmr0020)+++ 3721");
  	        conn.commit();

  	        conn.setAutoCommit(false);
        	for (j=0;svAcpt.length>j;j++) {
        		strQuery.setLength(0);
            	strQuery.append("select cr_acptno,cr_confno,cr_serno,cr_qrycd,cr_itemid,cr_baseitem,cr_baseno \n");
            	strQuery.append("  from cmr1010 where cr_acptno=?                                     \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn, strQuery.toString());
                //pstmt.setString(1, etcData.get("ReqCD"));
                pstmt.setString(1, svAcpt[j]);
                //pstmt.setString(3, svAcpt[j]);
                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
            	while (rs.next()) {
            		for (i=0;scriptList.size()>i;i++) {
            			if (scriptList.get(i).get("cr_itemid").equals(rs.getString("cr_itemid")) &&
            				scriptList.get(i).get("baseitem").equals(rs.getString("cr_baseitem"))) {
            				if (scriptList.get(i).get("compyn").equals("Y")) {
	            				strQuery.setLength(0);
	            				pstmtcount = 0;
	            				strQuery.append("insert into cmr1060                  \n");
	            				strQuery.append(" (CR_ACPTNO,CR_SERNO,CR_PRCSYS,CR_RUNGBN,CR_BLDGBN,CR_BLDCD,CR_PARM,CR_RUNYN) \n");
	            				strQuery.append("values (?,?,'SYSCB',?,?,?,?,'Y')     \n");
	            				pstmt2 = conn.prepareStatement(strQuery.toString());
	            				pstmt2.setString(++pstmtcount, svAcpt[j]);
	            				pstmt2.setString(++pstmtcount, rs.getString("cr_serno"));            				
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("cm_rungbn"));			
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("compbldgbn"));		
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("compbldcd"));
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chgparm1"));
	            				pstmt2.executeUpdate();
	            				pstmt2.close();
            				}
            				if (scriptList.get(i).get("deployyn").equals("Y")) {
	            				strQuery.setLength(0);
	            				pstmtcount = 0;
	            				strQuery.append("insert into cmr1060                  \n");
	            				strQuery.append("       (CR_ACPTNO,CR_SERNO,CR_PRCSYS,CR_RUNGBN,CR_BLDGBN,CR_BLDCD,CR_PARM,CR_RUNYN) \n");
	            				strQuery.append("values (?,?,'SYSED',?,?,?,?,?)       \n");
	            				pstmt2 = conn.prepareStatement(strQuery.toString());
	            				pstmt2.setString(++pstmtcount, svAcpt[j]);
	            				pstmt2.setString(++pstmtcount, rs.getString("cr_serno"));            				
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("cm_rungbn"));			
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("deploybldgbn"));		
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("deploybldcd"));
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chgparm2"));
	            				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chguseryn"));	            				
	            				pstmt2.executeUpdate();
	            				pstmt2.close();
            				}
            			}
            		}
            		if (!rs.getString("cr_qrycd").equals("09")) {
	                	pstmtcount = 1;
	                	strQuery.setLength(0);
	                	strQuery.append("update cmr0020 set cr_savesta=cr_status,     \n");
	                	if (etcData.get("ReqCD").equals("07")) {
	                		strQuery.append("cr_status='A',                           \n");
	                	} else {
	                		strQuery.append("cr_status='7',                           \n");
	                	}
	                	strQuery.append("cr_editor= ?                                 \n");
	                	strQuery.append("where cr_itemid= ?                           \n");
	
	                	pstmt2 = conn.prepareStatement(strQuery.toString());
	                	pstmt2.setString(pstmtcount++, etcData.get("UserID"));
	                	pstmt2.setString(pstmtcount++, rs.getString("cr_itemid"));
	                	pstmt2.executeUpdate();
	                	pstmt2.close();
            		}
                	if (rs.getString("cr_confno") != "" && rs.getString("cr_confno") != null && etcData.get("ReqCD").equals("07")) {
                		strQuery.setLength(0);
                		strQuery.append("update cmr1010 set cr_confno=?                               \n");
                		strQuery.append(" where cr_acptno=? and cr_itemid=?                           \n");
                		pstmt2 = conn.prepareStatement(strQuery.toString());
                		//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
                		pstmtcount = 1;
                		pstmt2.setString(pstmtcount++, svAcpt[j]);
                		pstmt2.setString(pstmtcount++, rs.getString("cr_confno"));
                		pstmt2.setString(pstmtcount++, rs.getString("cr_itemid"));
                    	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
                    	pstmt2.executeUpdate();
                    	pstmt2.close();
                	}
            	}
            	rs.close();
            	pstmt.close();
        	}
        	conn.commit();

        	//ecamsLogger.error("+++++++++CHECK-IN LIST Update START (cmr0020)+++ 3844");
        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;

        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Check_In() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Check_In() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Check_In() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
//					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_Deploy(ArrayList<HashMap<String,String>> chkInList,HashMap<String,String> etcData,
			ArrayList<HashMap<String,String>> befJob,ArrayList<HashMap<String,Object>> ConfList,String confFg,ArrayList<HashMap<String,String>> scriptList) throws SQLException, Exception {
			
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		PreparedStatement pstmt3      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		String			  AcptNo	  = null;
		int				  i=0;

		try {
			conn = connectionContext.getConnection();

			//20120629 테스트 중
			SysInfo sysinfo = new SysInfo();
			//신청 통제 시간인지 확인 작업
			if (sysinfo.getSysStopChk(etcData.get("UserID"), chkInList.get(0).get("cr_syscd")) == 1){
	        	if (conn != null){
	        		conn.close();
	        		conn = null; 
	        	}
	        	return "ERROR[이관통제을 위하여 일시적으로 형상관리 사용을 중지합니다.(체크인신청)]";
			}
			sysinfo = null;

	        for (i=0;i<chkInList.size();i++){
	        	AcptNo = bldcdChk(chkInList.get(i).get("cr_syscd"),chkInList.get(i).get("cr_jobcd"),chkInList.get(i).get("cr_rsrccd"),chkInList.get(i).get("cm_info"),etcData.get("ReqCD"),chkInList.get(i).get("reqcd"),chkInList.get(i).get("cr_itemid"),ConfList,conn);
				if (AcptNo != "" && AcptNo != null) AcptNo = "ERROR[" + chkInList.get(i).get("cr_rsrcname")+"]에 대하여 " + AcptNo;
				else AcptNo = null;

	        	if ( AcptNo == null && chkInList.get(i).get("cr_itemid").equals( chkInList.get(i).get("baseitem") ) ) {
		        	strQuery.setLength(0);
		        	if ( etcData.get("ReqCD").equals("03") ) {//테스트배포요청
		        		strQuery.append("select a.cr_teststa status,b.cm_codename                   \n");
			        	strQuery.append("  from cmr0020 a,cmm0020 b                                 \n");
			        	strQuery.append(" where a.cr_itemid = ?                                     \n");
			        	strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_teststa  \n");
		        	} else if ( etcData.get("ReqCD").equals("07") ) {//체크인
		        		strQuery.append("select a.cr_devsta status,b.cm_codename                    \n");
			        	strQuery.append("  from cmr0020 a,cmm0020 b                                 \n");
			        	strQuery.append(" where a.cr_itemid = ?                                     \n");
			        	strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_devsta   \n");
		        	} else {
		        		strQuery.append("select a.cr_status status,b.cm_codename                   \n");
			        	strQuery.append("  from cmr0020 a,cmm0020 b                                 \n");
			        	strQuery.append(" where a.cr_itemid = ?                                     \n");
			        	strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_status   \n");
		        	}
		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt.setString(1, chkInList.get(i).get("cr_itemid"));
		        	rs = pstmt.executeQuery();
		        	if ( rs.next() ){
		        		//20140721 neo. 
		        		/* 변경전
		        		 * if ( !rs.getString("status").equals("D") ) {
		        	  	     AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[11] [" + rs.getString("cm_codename") +"]";
		        			}
		        		 */
		        		//변경후 시작
		        		if ( !etcData.get("ReqCD").equals("03") && !rs.getString("status").equals("G") && !rs.getString("status").equals("B") ) {
		        	  	     AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[11] [" + rs.getString("cm_codename") +"]";
		        		} else if ( etcData.get("ReqCD").equals("03") && rs.getString("status").equals("F") ) {
		        			AcptNo = "ERROR["+ chkInList.get(i).get("cr_rsrcname") + "]는 요청가능한 상태가 아닙니다.[12] [" + rs.getString("cm_codename") +"]";
		        		}
		        		//변경후 끝
		        	}
		        	rs.close();
		        	pstmt.close();
	        	}
	        }

	        if (AcptNo != null) {
	        	if (conn != null) conn.close();
	        	return AcptNo;
	        }

	        String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
	        Cmr0200 cmr0200 = new Cmr0200();
	        //ArrayList<HashMap<String,Object>> conflist = null;
	        int wkC = chkInList.size()/300;
	        int wkD = chkInList.size()%300;
	        if (wkD>0) wkC = wkC + 1;
            String svAcpt[] = null;
            svAcpt = new String [wkC];
            for (int j=0;wkC>j;j++) {
            	do {
    		        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

    		        i = 0;
    		        strQuery.setLength(0);
    		        strQuery.append("select count(*) as cnt from cmr1000 \n");
    	        	strQuery.append(" where cr_acptno= ?                 \n");

    	        	pstmt = conn.prepareStatement(strQuery.toString());
    	        	pstmt.setString(1, AcptNo);

    	        	rs = pstmt.executeQuery();

    	        	if (rs.next()){
    	        		i = rs.getInt("cnt");
    	        	}
    	        	rs.close();
    	        	pstmt.close();
    	        } while(i>0);
            	svAcpt[j] = AcptNo;
            }
        	int    seq = 0;
        	int    j   = 0;
        	String retMsg = "";
            autoseq = null;
            conn.setAutoCommit(false);
        	boolean insSw = false;
//        	String strBasePgm = "";
//        	String strBase2[] = null;
        	for (i=0;i<chkInList.size();i++){
        		insSw = false;
        		if (i == 0) insSw = true;
        		else {
        			wkC = i%300;
        			if (wkC == 0) insSw = true;
        		}
        		if (insSw == true) {
        			if (i>=300) {
        				retMsg = cmr0200.request_Confirm(AcptNo,chkInList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
        				if (!retMsg.equals("OK")) {
        					conn.rollback();
        					conn.close();
        					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
        				}
        				conn.commit();
        				
        				conn.setAutoCommit(false);
        			}

        			wkC = i/300;
        			AcptNo = svAcpt[wkC];
        			//ecamsLogger.error("++++ i, wkC ++++++"+ Integer.toString(i)+", "+ Integer.toString(wkC));
        			pstmtcount = 1;
        			strQuery.setLength(0);
                	strQuery.append("insert into cmr1000 \n");
                	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, \n");
                	strQuery.append("CR_PASSOK,CR_PASSCD,CR_BEFJOB,CR_EMGCD,CR_EDITOR,CR_SAYU,CR_PASSSUB,CR_SAYUCD,  \n");
                	strQuery.append("CR_ECLIPSE,CR_PRCREQ,CR_CLOSEYN,CR_ITSMID,CR_ITSMTITLE) values ( \n");
                	strQuery.append("?,?,?,?,sysdate,'0',?,?,  ?,?,?,?,?,?,?,?,  'N',?,?,?,? ) \n");

                	//pstmt = conn.prepareStatement(strQuery.toString());
                	pstmt = new LoggableStatement(conn,strQuery.toString());
                	pstmt.setString(pstmtcount++, AcptNo);
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("cr_syscd"));
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("sysgb"));
                	pstmt.setString(pstmtcount++, chkInList.get(0).get("cr_jobcd"));
                	pstmt.setString(pstmtcount++, strTeam);
                	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));

                	pstmt.setString(pstmtcount++, etcData.get("Deploy"));
                	pstmt.setString(pstmtcount++, etcData.get("Sayu"));
                	//pstmt.setString(pstmtcount++, strRequest);
                	if (befJob.size() > 0) pstmt.setString(pstmtcount++, "Y");
                	else pstmt.setString(pstmtcount++, "N");
                	pstmt.setString(pstmtcount++, etcData.get("ReqSayu"));
                	pstmt.setString(pstmtcount++, etcData.get("UserID"));
                	pstmt.setString(pstmtcount++, etcData.get("txtSayu"));
                	pstmt.setString(pstmtcount++, etcData.get("EmgCd"));
                	pstmt.setString(pstmtcount++, etcData.get("PassCd"));

                	if (etcData.get("Deploy").equals("4")) {
                		pstmt.setString(pstmtcount++,etcData.get("AplyDate"));
                	} else {
                		pstmt.setString(pstmtcount++,"");
                	}
                	pstmt.setString(pstmtcount++, etcData.get("closeyn"));
                	pstmt.setString(pstmtcount++, etcData.get("cc_srid"));
                	pstmt.setString(pstmtcount++, etcData.get("cc_reqtitle"));
                	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                	pstmt.executeUpdate();

                	pstmt.close();
                	seq = 0;
        		}


        		strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD,  \n");
            	strQuery.append("CR_RSRCCD,CR_LANGCD,CR_DSNCD,CR_RSRCNAME,CR_RSRCNAM2,CR_SRCCHG,CR_SRCCMP, \n");
            	strQuery.append("CR_PRIORITY,CR_APLYDATE,CR_VERSION,CR_BEFVER,CR_CONFNO,CR_EDITOR,  \n");
            	strQuery.append("CR_BASENO,CR_BASEITEM,CR_ITEMID,CR_COACPT,CR_BASEPGM,CR_REALBEFVER,CR_STORY) values \n");
            	strQuery.append("(?,?,?,?,?,'0',?,  ?,?,?,?,?,'0','Y',  ?,?,?,?,?,?,  ?,?,?,?,?,?,?)  \n");

            	pstmtcount = 1;
            	//pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt = new LoggableStatement(conn,strQuery.toString());

            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, ++seq);
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("sysgb"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_jobcd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("reqcd"));
            	
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_langcd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_rsrcname"));
            	
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("prcseq"));
            	if (etcData.get("Deploy").equals("4")) {
            		pstmt.setString(pstmtcount++,etcData.get("AplyDate"));
            	} else {
            		pstmt.setString(pstmtcount++,"");
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_aftver"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_version"));
            	if (etcData.get("ReqCD").equals("04")) {
            		if (chkInList.get(i).get("cr_acptno") != "" && chkInList.get(i).get("cr_acptno") != null) {

            			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_acptno"));

            		} else {

            			pstmt.setString(pstmtcount++,AcptNo);

            		}
            	} else {
            		pstmt.setString(pstmtcount++,chkInList.get(i).get(""));
            	}
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));

            	if (chkInList.get(i).get("reqcd").equals("05") || chkInList.get(i).get("reqcd").equals("09")) {
            		pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_baseno"));
            	} else {
	        		if (chkInList.get(i).get("cr_acptno") != null && chkInList.get(i).get("cr_acptno") != "" &&
	        			chkInList.get(i).get("cr_acptno").substring(4,6).equals("01") &&
	            		chkInList.get(i).get("cr_acptno").substring(4,6).equals("02")) {
	        			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_acptno"));
	        		} else if (chkInList.get(i).get("cr_acptno") != null && chkInList.get(i).get("cr_acptno") != "" &&
	        				!chkInList.get(i).get("cr_aftver").equals("0")) {
		        			pstmt.setString(pstmtcount++,chkInList.get(i).get("cr_baseno"));
		        	}else {
	        			pstmt.setString(pstmtcount++,chkInList.get(i).get(""));
	        		}
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("baseitem"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_itemid"));
            	if (chkInList.get(i).get("reqcd").equals("09")) {
            		pstmt.setString(pstmtcount++, selBaseno(chkInList.get(i).get("cr_itemid"),conn));
            	} else {
            		pstmt.setString(pstmtcount++, AcptNo);
            	}
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("baseitem"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_befver"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_story"));

            	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
        	}

            retMsg = request_Confirm(AcptNo,chkInList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
  	        
//  	        String strWork1 = "";
//  	        String strWork2 = "";
//  	        String strWork3 = "";
//  	        int szSerNo = 0;
//  	        boolean findSw = false;
        	for (j=0;svAcpt.length>j;j++) {
        		strQuery.setLength(0);
        		strQuery.append("update cmr1010 set cr_confno=?                     \n");
        		strQuery.append(" where cr_itemid in (select cr_itemid from cmr1010 \n");
        		strQuery.append("                   where cr_acptno=?               \n");
        		strQuery.append("                     and cr_itemid=cr_baseitem)    \n");
        		strQuery.append("   and cr_acptno in (select cr_acptno from cmr1000 \n");
        		strQuery.append("                      where cr_itsmid=?            \n");
        		strQuery.append("                        and cr_prcdate is not null \n");
        		strQuery.append("                        and cr_status<>'3')        \n");
        		strQuery.append("   and cr_confno is null                           \n");
        		//pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt = new LoggableStatement(conn, strQuery.toString());
                pstmt.setString(1, svAcpt[j]);
                pstmt.setString(2, svAcpt[j]);
                pstmt.setString(3, etcData.get("cc_srid"));
                ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                pstmt.executeUpdate();
                pstmt.close();
        		
        		strQuery.setLength(0);
            	strQuery.append("select cr_acptno,cr_serno,cr_qrycd,cr_itemid             \n");
            	strQuery.append("  from cmr1010 where cr_acptno=?                         \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn, strQuery.toString());
                pstmt.setString(1, svAcpt[j]);
                //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
            	while (rs.next()) {
            		for (i=0;scriptList.size()>i;i++) {
            			if (scriptList.get(i).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
            				if (scriptList.get(i).get("compyn").equals("Y")) {
                				strQuery.setLength(0);
                				pstmtcount = 0;
                				strQuery.append("insert into cmr1060                  \n");
                				strQuery.append(" (CR_ACPTNO,CR_SERNO,CR_PRCSYS,CR_RUNGBN,CR_BLDGBN,CR_BLDCD,CR_PARM,CR_RUNYN) \n");
                				strQuery.append("values (?,?,'SYSCB',?,?,?,?,'Y')     \n");
                				pstmt2 = conn.prepareStatement(strQuery.toString());
                				pstmt2.setString(++pstmtcount, svAcpt[j]);
                				pstmt2.setString(++pstmtcount, rs.getString("cr_serno"));            				
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("cm_rungbn"));			
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("compbldgbn"));		
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("compbldcd"));
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chgparm1"));
                				pstmt2.executeUpdate();
                				pstmt2.close();
            				}
            				if (scriptList.get(i).get("deployyn").equals("Y")) {
                				strQuery.setLength(0);
                				pstmtcount = 0;
                				strQuery.append("insert into cmr1060                  \n");
                				strQuery.append("       (CR_ACPTNO,CR_SERNO,CR_PRCSYS,CR_RUNGBN,CR_BLDGBN,CR_BLDCD,CR_PARM,CR_RUNYN) \n");
                				strQuery.append("values (?,?,'SYSED',?,?,?,?,?)       \n");
                				pstmt2 = conn.prepareStatement(strQuery.toString());
                				pstmt2.setString(++pstmtcount, svAcpt[j]);
                				pstmt2.setString(++pstmtcount, rs.getString("cr_serno"));            				
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("cm_rungbn"));			
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("deploybldgbn"));		
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("deploybldcd"));
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chgparm2"));
                				pstmt2.setString(++pstmtcount, scriptList.get(i).get("chguseryn"));	            				
                				pstmt2.executeUpdate();
                				pstmt2.close();
            				}
            			}
            		}
            		//ecamsLogger.error("$$$$$$$$$$$$$$$$$$  cr_qrycd:"+rs.getString("cr_qrycd"));
            		if ( !rs.getString("cr_qrycd").equals("09") ) {
	                	pstmtcount = 1;
	                	strQuery.setLength(0);
	                	strQuery.append("update cmr0020 set cr_savesta=cr_status, \n");
	                	if ( etcData.get("ReqCD").equals("03") ) {
	                		//20140721 neo. 테스트적용요청 상태 값으로 수정
	                		//변경전:strQuery.append("cr_teststa='A' \n");
	                		strQuery.append("cr_teststa='F', \n");
	                		strQuery.append("cr_status='F' \n");
	                	} else {
	                		strQuery.append("cr_status='7' \n");
	                	}
	                	strQuery.append("where cr_itemid= ? \n");
	                	//pstmt2 = conn.prepareStatement(strQuery.toString());
	                	pstmt2 = new LoggableStatement(conn,strQuery.toString());
	                	pstmt2.setString(pstmtcount++, rs.getString("cr_itemid"));
	                	ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
	                	pstmt2.executeUpdate();
	                	pstmt2.close();
            		}
            	}
            	rs.close();
            	pstmt.close();
            	
            	for (i=0;i<befJob.size();i++){
                	strQuery.setLength(0);
                	strQuery.append("insert into cmr1030 ");
                	strQuery.append("(CR_ACPTNO,CR_BEFACT) values (?, ?) \n");

                	pstmtcount = 1;
                	pstmt = conn.prepareStatement(strQuery.toString());
                	//pstmt = new LoggableStatement(conn,strQuery.toString());

                	pstmt.setString(pstmtcount++, svAcpt[j]);
                	pstmt.setString(pstmtcount++, befJob.get(i).get("cr_befact"));
                	pstmt.executeUpdate();
                	pstmt.close();
            	}
        	}
        	if ( etcData.get("cc_srid") != null && etcData.get("ReqCD").equals("04") ) {
            	strQuery.setLength(0);
            	strQuery.append("update cmc0110 set cc_status='5'\n");
            	strQuery.append(" where cc_srid=?                \n");
            	strQuery.append("   and cc_userid=?              \n");
            	strQuery.append("   and cc_status in ('B','C')   \n");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn,strQuery.toString());
            	pstmt.setString(pstmtcount++, etcData.get("cc_srid"));
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.executeUpdate();
            	pstmt.close();
        	}
        	conn.commit();

        	conn.close();
        	rs = null;
        	pstmt = null;
        	rs2 = null;
        	pstmt2 = null;
        	pstmt3 = null;
        	conn = null;

        	//ecamsLogger.error("+++++++++Request E N D+++");
        	return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Deploy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Deploy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.request_Deploy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Deploy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
//					conn.rollback();
					ConnectionResource.release(conn);
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String request_Confirm(String AcptNo,String SysCd,String ReqCd,String UserId,boolean confSw,ArrayList<HashMap<String,Object>> ConfList,Connection conn) throws SQLException, Exception {

		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               i = 0;
		int               pstmtcount = 0;
		int               SeqNo = 0;
		ArrayList<HashMap<String,Object>>	rData2 = null;
		try {
        	if (confSw == true) {
        		for (i=0;i<ConfList.size();i++){
    	        	if (ConfList.get(i).get("cm_congbn").equals("1") || ConfList.get(i).get("cm_congbn").equals("2") ||
    	        		ConfList.get(i).get("cm_congbn").equals("3") ||	ConfList.get(i).get("cm_congbn").equals("4") ||
    	        		ConfList.get(i).get("cm_congbn").equals("5") || ConfList.get(i).get("cm_congbn").equals("6")) {
    	        		if (ConfList.get(i).get("cm_gubun").equals("8") &&
    	        			(ConfList.get(i).get("cm_baseuser") == null || ConfList.get(i).get("cm_baseuser") == "")) {
    	        			strQuery.setLength(0);
        		        	strQuery.append("insert into cmr9900                                               \n");
        		        	strQuery.append("      (CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD, \n");
        		        	strQuery.append("       CR_STATUS,CR_CONGBN,CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,  \n");
        		        	strQuery.append("       CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR,CR_PRCSW,CR_QRYCD)         \n");
        		        	strQuery.append("(select c.cr_acptno, 1, lpad(?+rownum,2,'0'), ?, a.cm_userid, ?,  \n");
        		        	strQuery.append("        '0', ?, ?, ?, ?, ?, ?, ?, a.cm_userid, ?, ?               \n");
        		        	strQuery.append("   from cmm0043 b,cmm0040 a,cmr1000 c                             \n");
        		        	strQuery.append("  where c.cr_acptno=? and c.cr_teamcd<>a.cm_project               \n");
        		        	strQuery.append("    and a.cm_active='1' and a.cm_userid=b.cm_userid               \n");
        		        	strQuery.append("    and b.cm_rgtcd='61')                                          \n");
    	        		} else {
    	        			strQuery.setLength(0);
        		        	strQuery.append("insert into cmr9900                                               \n");
        		        	strQuery.append("      (CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD, \n");
        		        	strQuery.append("       CR_STATUS,CR_CONGBN,CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,  \n");
        		        	strQuery.append("       CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR,CR_PRCSW,CR_QRYCD)         \n");
        		        	strQuery.append("values                                                            \n");
        		        	strQuery.append("(?, 1, lpad(?,2,'0'), ?, ?, ?, '0', ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) \n");
    	        		}
    		        	//pstmt = conn.prepareStatement(strQuery.toString());
    		        	pstmt = new LoggableStatement(conn,strQuery.toString());
    		        	pstmtcount = 0;
    	        	    if (!ConfList.get(i).get("cm_gubun").equals("8") || (ConfList.get(i).get("cm_gubun").equals("8") &&
            	        	ConfList.get(i).get("cm_baseuser") != null && ConfList.get(i).get("cm_baseuser") != "")) {
    		        		pstmt.setString(++pstmtcount, AcptNo);
    		        	}
    	        	    pstmt.setInt(++pstmtcount, ++SeqNo);
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_name"));

    	        	    if (!ConfList.get(i).get("cm_gubun").equals("8") || (ConfList.get(i).get("cm_gubun").equals("8") &&
        	        		ConfList.get(i).get("cm_baseuser") != null && ConfList.get(i).get("cm_baseuser") != "")) {
	    	        	    rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(i).get("arysv");
	    					pstmt.setString(++pstmtcount, (String) rData2.get(0).get("SvUser"));
	    					rData2 = null;
    	        	    }
    	        	    if (ConfList.get(i).get("cm_gubun").equals("C")){
    	        	    	pstmt.setString(++pstmtcount,"3");
    	        	    } else {
    	        	    	pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_gubun"));
    	        	    }
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_congbn"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_common"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_blank"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_emg"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_holi"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_duty"));
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_orgstep"));

    	        	    if (!ConfList.get(i).get("cm_gubun").equals("8") || (ConfList.get(i).get("cm_gubun").equals("8") &&
        	        		ConfList.get(i).get("cm_baseuser") != null && ConfList.get(i).get("cm_baseuser") != "")) {
    	        	    	pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_baseuser"));
    	        	    }
    	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_prcsw"));
    	        	    pstmt.setString(++pstmtcount, ReqCd);
    	        	    if (ConfList.get(i).get("cm_gubun").equals("8") &&
        	        		(ConfList.get(i).get("cm_baseuser") == null || ConfList.get(i).get("cm_baseuser") == "")) {
    	        	       pstmt.setString(++pstmtcount, AcptNo);
    	        	    }
    	        	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
    		        	pstmt.executeUpdate();
    		        	pstmt.close();

    		        	if (ConfList.get(i).get("cm_gubun").equals("8") &&
            	        	(ConfList.get(i).get("cm_baseuser") == null || ConfList.get(i).get("cm_baseuser") == "")) {
    		        		strQuery.setLength(0);
    		        		strQuery.append("select max(cr_locat) max from cmr9900   \n");
    		        		strQuery.append(" where cr_acptno=?                      \n");
    		        		pstmt = conn.prepareStatement(strQuery.toString());
    		        		pstmt.setString(1, AcptNo);
    		        		rs = pstmt.executeQuery();
    		        		if (rs.next()) {
    		        			SeqNo = rs.getInt("max");
    		        		}
    		        		rs.close();
    		        		pstmt.close();

    		        		strQuery.setLength(0);
    		        		strQuery.append("select a.cm_daegyul,b.cr_locat,a.cm_daegmsg \n");
    		        		strQuery.append("  from cmm0040 a,cmr9900 b               \n");
    		        		strQuery.append(" where b.cr_acptno=? and b.cr_teamcd='8' \n");
    		        		strQuery.append("   and b.cr_team=a.cm_userid             \n");
    		        		strQuery.append("   and a.cm_status='9'                   \n");
    		        		strQuery.append("   and a.cm_daegyul is not null          \n");
    		        		strQuery.append("   and a.cm_blankdts is not null         \n");
    		        		strQuery.append("   and a.cm_blankdts<=to_char(SYSDATE,'yyyymmdd') \n");
    		        		strQuery.append("   and a.cm_blankdte>=to_char(SYSDATE,'yyyymmdd') \n");
    		        		//pstmt = conn.prepareStatement(strQuery.toString());
    		        		pstmt = new LoggableStatement(conn,strQuery.toString());
    		        		pstmt.setString(1, AcptNo);
    		        		ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
    		        		rs = pstmt.executeQuery();
    		        		while (rs.next()) {
    		        			strQuery.setLength(0);
    		        			strQuery.append("update cmr9900 set cr_team=?,        \n");
    		        			strQuery.append("       cr_blankcd=?                  \n");
    		        			strQuery.append(" where cr_acptno=? and cr_locat=?    \n");
    		        			pstmt2 = conn.prepareStatement(strQuery.toString());
    		  //      			pstmt = new LoggableStatement(conn,strQuery.toString());
    		        			pstmt2.setString(1, rs.getString("cm_daegyul"));
    		        			pstmt2.setString(2, rs.getString("cm_daegmsg"));
    		        			pstmt2.setString(3, AcptNo);
    		        			pstmt2.setString(4, rs.getString("cr_locat"));
    		//        			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
    		        			pstmt2.executeUpdate();
    		        			pstmt2.close();
    		        		}
    		        		rs.close();
    		        		pstmt.close();
    		        	}
    		        }
            	}
        	} else {
	        	strQuery.setLength(0);
	        	strQuery.append("insert into cmr9900 ");
	        	strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD,CR_STATUS,CR_CONGBN, ");
	        	strQuery.append("CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR, CR_PRCSW,CR_QRYCD) ");
	        	strQuery.append("(SELECT ?,1,lpad(a.CM_seqno,2,'0'),a.CM_NAME,a.CM_JOBCD,a.CM_GUBUN, ");
	        	strQuery.append("'0',a.CM_COMMON,a.CM_COMMON,a.CM_BLANK,a.CM_EMG,a.CM_HOLIDAY,");
	        	strQuery.append("a.CM_POSITION,a.CM_ORGSTEP,a.CM_JOBCD,a.CM_PRCSW,? ");
	        	strQuery.append("FROM CMm0060 a,cmm0040 b ");
	        	strQuery.append("WHERE a.CM_SYSCD= ? ");
	        	strQuery.append("AND a.CM_REQCD= ? and b.cm_userid=?  ");
	        	strQuery.append("AND a.CM_MANID=decode(b.cm_manid,'N','2','1') ");
	        	if (!ReqCd.equals("16"))
	        		strQuery.append("AND CM_GUBUN='1') ");
	        	else strQuery.append(") ");

	        	//pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt = new LoggableStatement(conn, strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.setString(pstmtcount++, ReqCd);
	        	pstmt.setString(pstmtcount++, SysCd);
	        	pstmt.setString(pstmtcount++, ReqCd);
	        	pstmt.setString(pstmtcount++, UserId);
	        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();


	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900 set cr_team=cr_sgngbn             \n");
	        	strQuery.append(" where cr_acptno=? and cr_teamcd='4'              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();


	        	strQuery.setLength(0);
	        	strQuery.append("update cmr9900 set cr_team=?,cr_baseusr=?         \n");
	        	strQuery.append(" where cr_acptno=? and cr_teamcd='2'              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmtcount = 1;
	        	pstmt.setString(pstmtcount++, UserId);
	        	pstmt.setString(pstmtcount++, UserId);
	        	pstmt.setString(pstmtcount++, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();
        	}

        	strQuery.setLength(0);
        	strQuery.append("insert into cmr9900 ");
        	strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_STATUS,CR_CONFUSR) ");
        	strQuery.append("values ( ");
        	strQuery.append("?, '1', '00', '0', '9999' ) ");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);

        	pstmt.executeUpdate();
        	pstmt.close();

        	boolean findSw = false;
        	if (ReqCd.equals("16")) {
            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and (substr(b.cm_info,1,1)='1' or           \n");
        		strQuery.append("       substr(b.cm_info,25,1)='1')             \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and (substr(b.cm_info,1,1)='1' or           \n");
        		strQuery.append("       substr(b.cm_info,25,1)='1'))            \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSCB' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}


            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and (substr(b.cm_info,11,1)='1' or          \n");
        		strQuery.append("       substr(b.cm_info,21,1)='1')             \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and (substr(b.cm_info,11,1)='1' or          \n");
        		strQuery.append("       substr(b.cm_info,21,1)='1'))            \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSED' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}

            	findSw = false;
        		strQuery.setLength(0);
        		strQuery.append("select sum(cnt) cnt from                       \n");
        		strQuery.append("(select count(*) cnt from cmr1010 a,cmm0036 b  \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                 \n");
        		strQuery.append("   and substr(b.cm_info,35,1)='1'              \n");
        		strQuery.append(" union                                         \n");
        		strQuery.append("select count(*) cnt from cmr1010 a,cmm0036 b,cmm0037 c \n");
        		strQuery.append(" where a.cr_acptno=?                           \n");
        		strQuery.append("   and a.cr_syscd=c.cm_syscd                   \n");
        		strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                 \n");
        		strQuery.append("   and c.cm_syscd=b.cm_syscd                   \n");
        		strQuery.append("   and c.cm_samersrc=b.cm_rsrccd               \n");
        		strQuery.append("   and substr(b.cm_info,35,1)='1')             \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, AcptNo);
            	rs = pstmt.executeQuery();
            	if (rs.next()) {
            		if (rs.getString("cnt") == null) findSw = true;
            		else if (rs.getInt("cnt") == 0) findSw = true;
            	}
            	rs.close();
            	pstmt.close();

            	if (findSw == true) {
	            	strQuery.setLength(0);
	            	strQuery.append("delete cmr9900 where cr_acptno=? and cr_teamcd='1' and cr_team='SYSRC' ");
	            	pstmt = conn.prepareStatement(strQuery.toString());
	            	pstmt.setString(1, AcptNo);
	            	pstmt.executeUpdate();
	            	pstmt.close();
            	}

        	}
        	strQuery.setLength(0);
        	strQuery.append("Begin NICE.CMR9900_STR ( ");
        	strQuery.append("?, '000000', '', '9', ?, '1' ); End;");
//        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt = new LoggableStatement(conn, strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, ReqCd);
        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

        	rs = null;
        	pstmt = null;

        	//ecamsLogger.error("+++++++++Request E N D+++CMR0200");

        	return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Confirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.request_Confirm() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.request_Confirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.request_Confirm() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}
	public Object[] pgmCheck(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String LangCd,String ProgTit,String DirPath) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            BaseItem    = "";
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select a.cr_status,a.cr_lstver,a.cr_editor,b.cm_codename \n");
			strQuery.append("  from cmm0020 b,cmr0020 a                               \n");
			strQuery.append(" where a.cr_syscd=? and a.cr_dsncd=?                     \n");
			strQuery.append("   and a.cr_rsrcname=?                                   \n");
			strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_status \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, DsnCd);
	        pstmt.setString(3, RsrcName);
	        rs = pstmt.executeQuery();
	        rsval.clear();
	        if (rs.next() == true) {
	        	if (rs.getString("cr_status").equals("3") && !rs.getString("cr_editor").equals(UserId))
	        		retMsg = "다른 사용자가 신규등록한 프로그램ID입니다.";
	        	else if (rs.getString("cr_status").equals("3")) retMsg = "0";
	        	else retMsg = "기 등록하여 운영 중인 프로그램ID입니다.";
	        }

	        if (retMsg == "0") {
	        	retMsg = null;
	        	retMsg = cmr0020_Insert(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,LangCd,ProgTit,"");
	        	if (retMsg.substring(0,1).equals("0")) {
	        		BaseItem = retMsg.substring(1);
	        		retMsg = "";
	        		retMsg = moduleChk(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,LangCd, ProgTit,DirPath,BaseItem);

	        	}
	        }

	        rs.close();
	        pstmt.close();
	        conn.close();

	        rs = null;
	        pstmt = null;
	        conn = null;

			rst = new HashMap<String, String>();
			rst.put("retMsg", retMsg);
			rst = null;
			rsval.add(rst);

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			//ecamsLogger.error(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.pgmCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.pgmCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.pgmCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.pgmCheck() Exception END ##");
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
					ecamsLogger.error("## Cmr0200.pgmCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of pgmCheck() method statement

	public String selBaseno(String ItemId,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            baseAcpt    = "";

		try {

			strQuery.append("select cr_acptno from cmr0021         \n");
			strQuery.append(" where cr_itemid=?                    \n");
			strQuery.append(" order by cr_acptdate desc            \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, ItemId);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	baseAcpt = rs.getString("cr_acptno");
	        }
	        rs.close();
	        pstmt.close();

	        return baseAcpt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.selBaseno() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.selBaseno() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.selBaseno() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.selBaseno() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of selBaseno() method statement

	public String moduleChk(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String LangCd,String ProgTit,String DirPath,String BaseItem) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            strBefDsn   = null;
		String            strAftDsn   = null;
		String            strWork1    = null;
		String            strWork3    = null;
		String            strDevPath  = null;
		String            strRsrcCd   = null;
		String            strRsrcName = null;
		int               j           = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
			strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
			strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
			strQuery.append("   and b.cm_factcd='04'                                  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
			strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, RsrcCd);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	strBefDsn = "";
	        	strAftDsn = "";

	        	if (rs.getString("cm_basedir") != null) strBefDsn = rs.getString("cm_basedir");
	        	if (RsrcName.indexOf(".") > -1) {
	        		strWork1 = RsrcName.substring(0,RsrcName.indexOf("."));
	        	}
	        	else strWork1 = RsrcName;
	        	if (!rs.getString("cm_basename").equals("*")) {
	        		strWork3 = rs.getString("cm_basename");
	        		while (strWork3 == "") {
	        			j = strWork3.indexOf("*");
	        			if (j > -1) {
	        				strWork3 = strWork3.substring(j + 1);
	        				if (strWork3.equals("*")) strWork3 = "";
	        			} else {
	        				strWork3 = "";
	        			}
	        			if (strWork3 == "") break;
	        		}
	        	}
	        	if (rs.getString("cm_cmdyn").equals("Y")) {
	        		strWork1 = rs.getString("cm_samename").replace("*",strWork1);

	        		strQuery.setLength(0);
	        		strQuery.append("select ? relatId  from dual                           \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, strWork1);
	    	        rs2 = pstmt2.executeQuery();
	    	        if (rs2.next()) {
	    	        	strWork1 = rs2.getString("relatId");
	    	        }

	    	        rs2.close();
	    	        pstmt2.close();

	        	}
	        	else{
	        		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
	        	}

	        	strRsrcName = strWork1;
	        	strRsrcCd = rs.getString("cm_samersrc");
	        	if (rs.getString("cm_samedir") != null){
	        		strAftDsn = rs.getString("cm_samedir");
	        	}

	        	if (rs.getString("cm_samersrc").equals("52")) {
	        		strQuery.setLength(0);
	        		strQuery.append("select a.cm_volpath  from cmm0038 a,cmm0031 b  \n");
	        		strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='01'        \n");
	        		strQuery.append("   and a.cm_rsrccd=? and a.cm_syscd=b.cm_syscd \n");
	        		strQuery.append("   and a.cm_svrcd=b.cm_svrcd and a.cm_seqno=b.cm_seqno  \n");
	        		strQuery.append("   and b.cm_closedt is null                    \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, SysCd);
	        		pstmt2.setString(2, strRsrcCd);
	    	        rs2 = pstmt2.executeQuery();
	    	        if (rs2.next()) {
	    	        	strDevPath = rs2.getString("cm_volpath");
	    	        }
	    	        rs2.close();
	    	        pstmt2.close();


	        	}
	        	else{
	        		strDevPath = DirPath.replace(strBefDsn, strAftDsn);
	        	}

	        	strQuery.setLength(0);
        		strQuery.append("select cm_dsncd from cmm0070            \n");
        		strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, SysCd);
        		pstmt2.setString(2, strDevPath);
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strAftDsn = rs2.getString("cm_dsncd");
    	        }
    	        else {
    	        	retMsg = "[" + strDevPath + "]에 대한 디렉토리 등록을 관리자에게 요청한 후 처리하십시오.";
    	        }

    	        rs2.close();
    	        pstmt2.close();


    	        if (retMsg == "0") {
    	        	//ecamsLogger.error("+++++++++++++++rsrcname=========>"+strRsrcName);
    	        	retMsg = cmr0020_Insert(UserId,SysCd,strAftDsn,strRsrcName,strRsrcCd,JobCd,LangCd,ProgTit,BaseItem) ;
    	        }

	        }

	        rs.close();
	        pstmt.close();
	        conn.close();

	        return retMsg;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.moduleChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.moduleChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.moduleChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.moduleChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.moduleChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of moduleChk() method statement

	public Object[]  moduleChk_new(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
		    rtList.clear();
			for (int i=0;fileList.size()>i;i++) {
				rst = new HashMap<String, String>();
				rst = fileList.get(i);
        		rst.put("moderr", "0");
				if (fileList.get(i).get("cm_info").substring(8,9).equals("1")) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt                                      \n");
					strQuery.append("  from cmm0037 b,cmd0011 c,cmr0020 d                     \n");
					strQuery.append(" where c.cd_itemid=?                                     \n");
					strQuery.append("   and c.cd_prcitem=d.cr_itemid                          \n");
					strQuery.append("   and d.cr_syscd=b.cm_syscd                             \n");
					strQuery.append("   and d.cr_rsrccd=b.cm_samersrc                         \n");
					strQuery.append("   and b.cm_factcd='09'                                  \n");
					strQuery.append("   and d.cr_status<>'9'                                  \n");

			        pstmt = conn.prepareStatement(strQuery.toString());
			        //pstmt = new LoggableStatement(conn,strQuery.toString());
			        pstmt.setString(1, fileList.get(i).get("cr_itemid"));
			        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();
			        if (rs.next()) {
			        	if (rs.getInt("cnt") == 0) {
			        		rst.put("moderr", "1");
			        	}
			        }
			        rs.close();
			        pstmt.close();
				}
				rtList.add(rst);
			}
	        conn.close();

	        return rtList.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.moduleChk_new() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.moduleChk_new() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.moduleChk_new() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.moduleChk_new() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.moduleChk_new() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of moduleChk_new() method statement
	public String nameChange(HashMap<String,String> etcData,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            strWork1    = "";
		String            strWork2    = "";
		String            strWork3    = "";
		String            strWork4    = "";
		String            strWork5    = "";
		String            strExtName  = "";
		int               i           = 0;
		int               j           = 0;

		try {
			
    	    strWork1 = etcData.get("samename");    	   
    	    if (etcData.get("rsrcname").indexOf(".") > -1) {
    	    	strExtName = etcData.get("rsrcname").substring(0,etcData.get("rsrcname").indexOf("."));
        	} else {
        		strExtName = etcData.get("rsrcname");
        	}
    	    do{
    	    	strWork4 = strWork1;
    	    	j = strWork4.indexOf("?#");
    	        if (j>=0) {
    	        	strWork4 = strWork4.substring(j+2);
    	        } else return "ERROR";

    	        j = strWork4.indexOf("#");
    	        if (j>0) {
    	        	strWork2 = "?#" + strWork4.substring(0,j+1);
    	        } else return "ERROR";
    	        
    	        strWork3 = strWork1;
                if (strWork2.equals("?#ACPTNO#")) {
                	strWork1 = strWork3.replace(strWork2, etcData.get("acptno"));
                } else if (strWork2.equals("?#SRCFILE#")) {
                	strWork1 = strWork3.replace(strWork2, etcData.get("rsrcname"));
                } else if (strWork2.equals("?#SRCDIR#")) {
                	if (etcData.get("prcsys") == null) strWork1 = strWork3.replace(strWork2, etcData.get("dirpath"));
                	else {
                		Cmr0250 cmr0250 = new Cmr0250();
                		strWork4 = cmr0250.chgVolPath2(etcData.get("syscd"), etcData.get("dirpath"), etcData.get("reqcd"), etcData.get("rsrccd"), etcData.get("prcsys"), conn);
                		strWork1 = strWork3.replace(strWork2, strWork4);
                	}
                } else if (strWork2.equals("?#PARENTEXENM#")) {
                	strQuery.setLength(0);
                	strQuery.append("select b.cr_rsrcname from cmr1010 a,cmr1010 b  \n");
                	strQuery.append(" where a.cr_itemid = ? \n");
                	strQuery.append("   and a.cr_qrycd in ('07','04','03') \n");
                	if ( etcData.get("acptno") != "" ) {
                		strQuery.append("   and a.cr_acptno = ? \n");
                	}
                	strQuery.append("   and a.cr_acptno=b.cr_acptno   \n");
                	strQuery.append("   and a.cr_baseitem=b.cr_itemid \n");
                	//pstmt = conn.prepareStatement(strQuery.toString());
                	pstmt = new LoggableStatement(conn,strQuery.toString());
                	pstmt.setString(1, etcData.get("itemid"));
                	//신청번호가 없을때 base가 되는ID로 조회.
                	if ( etcData.get("acptno") != "" ) {
                		pstmt.setString(2, etcData.get("acptno"));
                	}
                	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                	rs = pstmt.executeQuery();
                	if (rs.next()) {
                		strWork4 = rs.getString("cr_rsrcname");
                	}
                	rs.close();
                	pstmt.close();
                	
                	//부모프로그램이 확장자가 있는지 부터 체크
                	if ( strWork4.lastIndexOf(".")>0 ) {
	                	ecamsLogger.error("strWork4:"+strWork4 +",strWork4.lastIndexOf:"+strWork4.lastIndexOf(".")+",strWork4.substring:"+strWork4.substring(0,strWork4.lastIndexOf(".")));
	                	strWork1 = strWork3.replace(strWork2, strWork4.substring(0,strWork4.lastIndexOf(".")));
                	} else {
                		strWork1 = strWork3;
                		break;
                	}
                } else if (strWork2.equals("?#SRCDIRA1S#") ||
                		   strWork2.equals("?#SRCDIRA2S#") ||
                		   strWork2.equals("?#SRCDIRA3S#") ||
                		   strWork2.equals("?#SRCDIRA4S#") ||
                		   strWork2.equals("?#SRCDIRA5S#") ||
                		   strWork2.equals("?#SRCDIRA6S#") ||
                		   strWork2.equals("?#SRCDIRA7S#") ||
                		   strWork2.equals("?#SRCDIRA8S#") ||
                		   strWork2.equals("?#SRCDIRA9S#")) {
                    strWork4 = etcData.get("dirpath");
                    if (strWork5.substring(strWork4.length()-1).equals("/")) {
                    	strWork5 = strWork4.substring(0,strWork4.length()-1);
                    }
                    j = Integer.parseInt(strWork2.substring(9,10));
                    
                    do {
                    	++i;
                    	strWork4 = strWork5.substring(strWork5.lastIndexOf("/")+1);
                    	strWork5 = strWork5.substring(0,strWork5.lastIndexOf("/"));
                    	if (i == j) break;
                    } while (j>=i);
                    
                    strWork1 = strWork3.replace(strWork2, strWork4);
                } else if (strWork2.equals("?#SRCDIRB1S#") ||
             		   strWork2.equals("?#SRCDIRB2S#") ||
            		   strWork2.equals("?#SRCDIRB3S#") ||
            		   strWork2.equals("?#SRCDIRB4S#") ||
            		   strWork2.equals("?#SRCDIRB5S#") ||
            		   strWork2.equals("?#SRCDIRB6S#") ||
            		   strWork2.equals("?#SRCDIRB7S#") ||
            		   strWork2.equals("?#SRCDIRB8S#") ||
            		   strWork2.equals("?#SRCDIRB9S#")) {
	                strWork4 = etcData.get("dirpath");
	                if (strWork5.substring(0,1).equals("/")) {
	                	strWork5 = strWork4.substring(1);
	                }
	                j = Integer.parseInt(strWork2.substring(9,10));
	                
	                do {
	                	++i;
	                	strWork4 = strWork5.substring(0,strWork5.indexOf("/"));
	                	strWork5 = strWork5.substring(strWork5.indexOf("/")+1);
	                	if (i == j) break;
	                } while (j>=i);
	                
	                strWork1 = strWork3.replace(strWork2, strWork4);
	            } else if (strWork2.equals("?#EXENAME#") ||
	            		   strWork2.equals("?#LOWEXENAME#") ||
	            		   strWork2.equals("?#UPEXENAME#")) {
    	        	strWork4 =strExtName;
    	        	if (strWork2.equals("?#UPEXENAME#")) {
    	        		strWork4 = strWork4.toUpperCase();
    	        	}else if (strWork2.equals("?#LOWEXENAME#")) {
    	        		strWork4 = strWork4.toLowerCase();
    	        	}
    	        	strWork1 = strWork3.replace(strWork2, strWork4);
    	        } else if (strWork2.equals("?#TABLENM")) {
    	        	strWork4 = etcData.get("rsrcname");
    	        	if (strWork4.lastIndexOf("_")>0) {
    	        		strWork4 = strWork4.substring(0,strWork4.lastIndexOf("_"));
    	        	} else return "ERROR";
    	        	strWork1 = strWork3.replace(strWork2, strWork4);
    	        } else {
    	        	retMsg = "ERROR";
    	        	break;
    	        } 
                ecamsLogger.error("++strWork1="+strWork1);
    	    } while(strWork1.indexOf("?#")>=0);
    	    
	        if (strWork1.indexOf("?#")>=0 ) {
	        	retMsg = "ERROR";
	        } else {
	        	retMsg = strWork1;
	        }
	        return retMsg;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.nameChange() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.nameChange() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}

	}//end of nameChange() method statement
	public String cmr0020_Insert(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String LangCd,String ProgTit,String BaseItem) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		//PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		//ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            strItemId   = null;
		boolean           insFg       = true;
		//int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			strQuery.append("select cr_itemid from cmr0020                  \n");
			strQuery.append(" where cr_syscd=? and cr_dsncd=?               \n");
			strQuery.append("   and cr_rsrcname=?                                   \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, DsnCd);
	        pstmt.setString(3, RsrcName);
	        rs = pstmt.executeQuery();

	        if (rs.next() == true) {
	        	insFg = false;
	        	strItemId = rs.getString("cr_itemid");
	        }
	        else {
	        	insFg = true;
	        	/*
	        	strQuery.setLength(0);
        		strQuery.append("select to_number(max(cr_itemid)) + 1 as max,to_char(SYSDATE,'yyyymm') datYYMM  \n");
        		strQuery.append("  from cmr0020                                                  \n");
        		strQuery.append(" where substr(cr_itemid,1,6)=to_char(SYSDATE,'yyyymm')          \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	if (rs2.getString("max") == null) {
    	        		strItemId = rs2.getString("datYYMM") + "000001";
    	        	}
    	        	else {
    	        		strItemId = rs2.getString("max");
    	        	}
    	        }

    	        rs2.close();
    	        pstmt2.close();
    	        */
	        }

	        rs.close();
	        pstmt.close();


	        if (insFg == true) {
	        	strQuery.setLength(0);
        		strQuery.append("insert into cmr0020 (CR_ITEMID,CR_SYSCD,CR_DSNCD,CR_RSRCNAME,  \n");
        		strQuery.append("   CR_RSRCCD,CR_JOBCD,CR_LANGCD,CR_STATUS,CR_CREATOR,CR_STORY, \n");
        		strQuery.append("   CR_OPENDATE,CR_LASTDATE,CR_LSTVER,CR_EDITOR,CR_NOMODIFY)    \n");
        		strQuery.append(" (select decode(nvl(max(cr_itemid),'0'),'0',to_char(SYSDATE,'yyyymm')||'000001', \n");
        		strQuery.append(" to_number(max(cr_itemid)) +1) as cr_itemid, \n");
        		strQuery.append(" ?, ?, ?, ?, ?, ?, '3', ?, ?, SYSDATE, SYSDATE, 0, ?, '0' \n");
        		strQuery.append(" from cmr0020 \n");
        		strQuery.append(" where substr(cr_itemid,1,6)=to_char(SYSDATE,'yyyymm') ) \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
          	    pstmt.setString(1, SysCd);
          	    pstmt.setString(2, DsnCd);
          	    pstmt.setString(3, RsrcName);
          	    pstmt.setString(4, RsrcCd);
          	    pstmt.setString(5, JobCd);
          	    pstmt.setString(6, LangCd);
          	    pstmt.setString(7, UserId);
          	    pstmt.setString(8, ProgTit);
          	    pstmt.setString(9, UserId);
          		pstmt.executeUpdate();
	        }
	        else {
	        	strQuery.setLength(0);
        		strQuery.append("update cmr0020 set CR_RSRCCD=?,CR_JOBCD=?,          \n");
        		strQuery.append("                   CR_LANGCD=?,CR_STATUS='3',       \n");
        		strQuery.append("                   CR_CREATOR=?,CR_STORY=?,         \n");
        		strQuery.append("                   CR_OPENDATE=SYSDATE,             \n");
        		strQuery.append("                   CR_LASTDATE=SYSDATE,CR_LSTVER=0, \n");
        		strQuery.append("                   CR_EDITOR=?,CR_NOMODIFY=0        \n");
        		strQuery.append("where cr_itemid=?                                   \n");
        		pstmt = conn.prepareStatement(strQuery.toString());
          	    pstmt.setString(1, RsrcCd);
          	    pstmt.setString(2, JobCd);
          	    pstmt.setString(3, LangCd);
          	    pstmt.setString(4, UserId);
          	    pstmt.setString(5, ProgTit);
          	    pstmt.setString(6, UserId);
          	    pstmt.setString(7, strItemId);
          		pstmt.executeUpdate();
	        }
	        pstmt.close();

	        if (insFg == true) {
		        strQuery.setLength(0);
				strQuery.append("select cr_itemid from cmr0020                  \n");
				strQuery.append(" where cr_syscd=? and cr_dsncd=?               \n");
				strQuery.append("   and cr_rsrcname=?                                   \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, SysCd);
		        pstmt.setString(2, DsnCd);
		        pstmt.setString(3, RsrcName);
		        rs = pstmt.executeQuery();

		        strItemId = "";
		        if (rs.next()) {
		        	strItemId = rs.getString("cr_itemid");
		        }

		        rs.close();
		        pstmt.close();

		        if (strItemId.equals("")){
					if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
					if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
					if (conn != null){
						try{
							conn.rollback();
							ConnectionResource.release(conn);
						}catch(Exception ex3){
							ecamsLogger.error("## Cmr0200.cmr0020_Insert() connection release exception ##");
							ex3.printStackTrace();
						}
					}
		        	throw new Exception("SysCD="+SysCd+" DsnCD="+DsnCd+" RsrcName="+RsrcName+" 자원의 ITEMID 조회 실패.");
		        }
	        }

	        if (BaseItem == ""){
	        	retMsg = "0" + strItemId;
	        }
	        else {
	        	strQuery.setLength(0);
	        	strQuery.append("select count(*) as cnt from cmr0022               \n");
				strQuery.append(" where cr_itemid=?                                \n");
				strQuery.append("   and cr_baseitem=?                              \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, strItemId);
		        pstmt.setString(2, BaseItem);
		        rs = pstmt.executeQuery();
		        if (rs.next() == false) {
		        	strQuery.setLength(0);
	        		strQuery.append("insert into cmr0022 (CR_ITEMID,CR_BASEITEM)  \n");
	        		strQuery.append("values (?, ?)                                \n");
	        		pstmt = conn.prepareStatement(strQuery.toString());
	          	    pstmt.setString(1, strItemId);
	          	    pstmt.setString(2, BaseItem);
	          		pstmt.executeUpdate();
		        }

		        rs.close();
		        pstmt.close();


		        retMsg = "0";
	        }

	        conn.commit();
	        conn.close();
	        conn = null;

	        return retMsg;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.cmr0020_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.cmr0020_Insert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.cmr0020_Insert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.cmr0020_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0200.cmr0020_Insert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.cmr0020_Insert() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					conn.close();
					conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.cmr0020_Insert() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Insert() method statement

	public Object[] cmr0020_Delete(String UserId,String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		//int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			strQuery.append("select cr_itemid from cmr0022              \n");
			strQuery.append(" where cr_baseitem=?                       \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, ItemId);
	        rs = pstmt.executeQuery();
	        rsval.clear();

	        while(rs.next()) {
	        	strQuery.setLength(0);
	        	strQuery.append("delete cmr0020 where cr_itemid=?  \n");
	        	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	pstmt2.setString(1, rs.getString("cr_itemid"));
	      		pstmt2.executeUpdate();
	      		pstmt2.close();
	        }
	        rs.close();
	        pstmt.close();


	        strQuery.setLength(0);
	        strQuery.append("delete cmr0022 where cr_baseitem=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	  	    pstmt.setString(1, ItemId);
	  		pstmt.executeUpdate();

	  		pstmt.close();


	        strQuery.setLength(0);
	        strQuery.append("delete cmr0020 where cr_itemid=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	  	    pstmt.setString(1, ItemId);
	  		pstmt.executeUpdate();
	  		pstmt.close();


	  		conn.commit();
	  		conn.close();
	  		
	  		rs = null;
	  		pstmt = null;
	  		conn = null;

	  		rst = new HashMap<String, String>();
			rst.put("retMsg", "0");
			rsval.add(rst);
			rst = null;

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			//ecamsLogger.error(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() Exception END ##");
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
					ecamsLogger.error("## Cmr0200.cmr0020_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Delete() method statement


	public Object[] checkTestCase(String UserId,String ItemId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray    = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		//int               rsCnt       = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			strQuery.append("select cr_itemid from cmr0022              \n");
			strQuery.append(" where cr_baseitem=?                       \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, ItemId);
	        rs = pstmt.executeQuery();
	        rsval.clear();

	        while(rs.next()) {
	        	strQuery.setLength(0);
	        	strQuery.append("delete cmr0020 where cr_itemid=?  \n");
	        	pstmt2 = conn.prepareStatement(strQuery.toString());
	        	pstmt2.setString(1, rs.getString("cr_itemid"));
	      		pstmt2.executeUpdate();
	      		pstmt2.close();
	        }
	        rs.close();
	        pstmt.close();


	        strQuery.setLength(0);
	        strQuery.append("delete cmr0022 where cr_baseitem=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	  	    pstmt.setString(1, ItemId);
	  		pstmt.executeUpdate();

	  		pstmt.close();


	        strQuery.setLength(0);
	        strQuery.append("delete cmr0020 where cr_itemid=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	  	    pstmt.setString(1, ItemId);
	  		pstmt.executeUpdate();
	  		pstmt.close();


	  		conn.commit();
	  		conn.close();
	  		rs = null;
	  		pstmt = null;
	  		conn = null;

	  		rst = new HashMap<String, String>();
			rst.put("retMsg", "0");
			rsval.add(rst);
			rst = null;

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;
			//ecamsLogger.error(rsval.toString());
			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.cmr0020_Delete() Exception END ##");
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
					ecamsLogger.error("## Cmr0200.cmr0020_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Delete() method statement

	/**
	 * <PRE>
	 * 1. MethodName	: checkReqYN
	 * 2. ClassName		: Cmr0200
	 * 3. Commnet			:
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 22. 오후 2:10:28
	 * </PRE>
	 * 		@return boolean
	 * 		@param SysCd
	 * 		@param chkInList
	 * 		@param UserId
	 * 		@param strReqCD
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */


	public Object[] fileOpenChk(String UserId,ArrayList<String> fileList,HashMap<String,String> baseFile)
		throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		PreparedStatement 	pstmt2      = null;
		ResultSet         	rs          = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String,String> rst = new HashMap<String,String>();
		int                 i = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			rsval.clear();
			int parmCnt = 0;
			boolean insSw = true;

			String subItem = "";
			String tmpWork1 = "";
			String tmpExe = "";
			String tmpRsrcCd = "";
			String tmpInfo = "";
			String retMsg = "";
			boolean errSw = false;
			Cmd0100 cmd0100 = new Cmd0100();
			for (i=0;fileList.size()>i;i++){
				errSw = false;
				rst = new HashMap<String,String>(baseFile);
				rst.put("cr_rsrcname",fileList.get(i));
				rst.put("errsw", "1");

				insSw = true;
				parmCnt = 0;
				strQuery.setLength(0);
				strQuery.append("select cr_itemid from cmr0020   	\n");
	            strQuery.append(" where cr_syscd=?           		\n");
	            strQuery.append("   and cr_dsncd=?                  \n");
	        	strQuery.append("   and upper(cr_rsrcname)=upper(?) \n");
				pstmt = conn.prepareStatement(strQuery.toString());
	//			pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(++parmCnt, baseFile.get("cr_syscd"));
	            pstmt.setString(++parmCnt, baseFile.get("cr_dsncd"));
	            pstmt.setString(++parmCnt, fileList.get(i));
//	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if (rs.next()) {
	            	subItem = rs.getString("cr_itemid");
	            	rst.put("cr_itemid", subItem);
	            	rst.put("errsw","0");
	            	insSw = false;
	            }
				rs.close();
				pstmt.close();

				if (insSw == true) {
					insSw = false;
					tmpWork1 = fileList.get(i);
					tmpExe = "";
					if (tmpWork1.indexOf(".")>0) tmpExe = tmpWork1.substring(tmpWork1.indexOf("."));

					if (tmpExe.length() > 0) {
						tmpExe = tmpExe + ",";
						tmpRsrcCd = "";
						tmpInfo = "";
						strQuery.setLength(0);
						strQuery.append("select a.cm_rsrccd,a.cm_info       \n");
						strQuery.append("  from cmm0036 a                   \n");
						strQuery.append(" where cm_syscd=?                  \n");
						strQuery.append("   and cm_closedt is null          \n");
						strQuery.append("   and substr(cm_info,26,1)='1'    \n");
						strQuery.append("   and instr(upper(cm_exename),upper(?))>0 \n");
						pstmt2 = conn.prepareStatement(strQuery.toString());
	//					pstmt2 = new LoggableStatement(conn,strQuery.toString());
						parmCnt = 0;
						pstmt2.setString(++parmCnt, baseFile.get("cr_syscd"));
						pstmt2.setString(++parmCnt, tmpExe);
//						//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
						rs2 = pstmt2.executeQuery();
						if (rs2.next()) {
							tmpRsrcCd = rs2.getString("cm_rsrccd");
							tmpInfo = rs2.getString("cm_info");
						}
						rs2.close();
						pstmt2.close();

						if (tmpRsrcCd.length() > 0) insSw = true;
					}
					if (insSw == true) {
						//(UserId, SysCd, DsnCd, RsrcName, RsrcCd, JobCd, ProgTit, BaseItem, rsrcInfo, conn)
						//retMsg = cmd0100.cmr0020_Insert(UserId, baseFile.get("cr_syscd"), baseFile.get("cr_dsncd"), tmpWork1, tmpRsrcCd, baseFile.get("cr_jobcd"), baseFile.get("cr_story"), baseFile.get("cr_itemid"), tmpInfo, conn);
						rst = new HashMap<String, String>();
	    	        	rst.put("userid",UserId);
	    	        	rst.put("syscd",baseFile.get("cr_syscd"));
	    	        	rst.put("dsncd",baseFile.get("cr_dsncd"));
	    	        	rst.put("rsrcname",tmpWork1);
	    	        	rst.put("rsrccd",tmpRsrcCd);
	    	        	rst.put("jobcd",baseFile.get("cr_jobcd"));
	    	        	rst.put("story",baseFile.get("cr_story"));
	    	        	rst.put("baseitem",baseFile.get("cr_itemid"));
	    	        	rst.put("info",tmpInfo);
	    	        	retMsg = cmd0100.cmr0020_Insert(rst,conn);
	    	        	//rst = null;

						//(String UserId,String SysCd,String IsrId,String IsrSub,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String ProgTit,String BaseItem,String rsrcInfo,Connection conn)
						if (!retMsg.substring(0,1).equals("0")) {
							rst.put("errmsg", retMsg);
							errSw = true;
						} else {
							rst.put("errmsg", "정상");
							rst.put("itemid", retMsg.substring(1));
							subItem = retMsg.substring(1);
							rst.put("errsw","0");
						}
					} else {
						rst.put("errmsg", "제외(관리대상아님)");
						errSw = true;
						rst.put("errsw","1");
					}
				} else {
					if (subItem.equals(baseFile.get("cr_itemid"))) {
						rst.put("errmsg", "제외(기준프로그램)");
						errSw = true;
						rst.put("errsw","1");
					}
				}

				if (errSw == false) {
					strQuery.setLength(0);
					strQuery.append("select a.cr_rsrcname,a.cr_lstver,a.cr_syscd,a.cr_dsncd,             \n");
				    strQuery.append("       a.cr_itemid,a.cr_rsrccd,'' cr_acptno,a.cr_story,a.cr_jobcd,  \n");
				    strQuery.append("       a.cr_status,i.cm_info,                                       \n");
				    strQuery.append("       lpad(i.cm_stepsta,4,'0') prcreq,nvl(i.cm_vercnt,50) vercnt,  \n");
				    strQuery.append("       e.cm_codename jawon,a.cr_editor,c.cm_codename,               \n");
				    strQuery.append("       to_char(a.cr_lastdate,'yyyy/mm/dd') lastdate                 \n");
					strQuery.append("  from cmm0020 e,cmm0020 c,cmr0020 a,cmm0036 i                      \n");
					strQuery.append(" where a.cr_itemid=?                                                \n");
					strQuery.append("   and e.cm_macode='JAWON' and a.cr_rsrccd=e.cm_micode              \n");
					strQuery.append("   and c.cm_macode='CHECKIN' and c.cm_micode=decode(a.cr_lstver,0,'03','04') \n");
					strQuery.append("   and a.cr_syscd=i.cm_syscd and a.cr_rsrccd=i.cm_rsrccd            \n");
					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn,strQuery.toString());

					pstmt.setString(1, subItem);
	//				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();

					if (rs.next()){
						//rst.put("ID", Integer.toString(rs.getRow()));
						rst.put("acptno", baseFile.get("acptno"));
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("jawon", rs.getString("jawon"));
						rst.put("checkin", rs.getString("cm_codename"));
						rst.put("cr_lstver", Integer.toString(rs.getInt("cr_lstver")));
						rst.put("cr_itemid", rs.getString("cr_itemid"));
						rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
						rst.put("cr_lastdate", rs.getString("lastdate"));
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("cr_status", rs.getString("cr_status"));
						rst.put("prcseq", rs.getString("prcreq"));
						if (rs.getInt("vercnt") == 0) {
							if (rs.getInt("cr_lstver") >= 9999) rst.put("cr_aftver", "1");
							else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						} else {
							if (rs.getInt("cr_lstver") >= rs.getInt("vercnt")) {
								rst.put("cr_aftver", "1");
							} else rst.put("cr_aftver", Integer.toString(rs.getInt("cr_lstver")+1));
						}
						if (rs.getInt("cr_lstver") > 0) rst.put("reqcd", "04");
						else rst.put("reqcd", "03");

						rst.put("selected_flag","0");

					} else {
						rst.put("errsw","1");
						rst.put("errmsg","원장없음");
					}
					rs.close();
					pstmt.close();
				}


				rsval.add(rst);
				//ecamsLogger.error("+++++rst+++++++"+fileList.get(i)+", "+rst.toString());
			}

			conn.close();
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.fileOpenChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.fileOpenChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.fileOpenChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.fileOpenChk() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex4){ex4.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.fileOpenChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of fileOpenChk() method statement

	/** 처리구분[0:수시적용 2:긴급 4:일반적용(적용일시)] 에 따라 배포날짜 시간 가능 여부 체크해서 리턴
	 * @param ReleaseType : 처리구분 0:수시적용 2:긴급 4:일반적용(적용일시)
	 * @param ReleaseDay : 배포날짜 ex) 20121125
	 * @param ReleaseTime : 배포시간 ex) 1230
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String chkRelease(String ReleaseType,String ReleaseDay,String ReleaseTime) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            returnStr   = "";
		String            StartTime   = "";
		String            EndTime     = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			//영업시간 쿼리
			strQuery.setLength(0);
			strQuery.append("select cm_sttime,cm_edtime \n");
			strQuery.append("  from cmm0014 \n");
			strQuery.append(" where cm_stno='ECAMS' and cm_timecd='01' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				StartTime = rs.getString("cm_sttime");
				EndTime = rs.getString("cm_edtime");
			}
			if (StartTime == "" || EndTime == ""){
				rs.close();
				pstmt.close();
				conn.close();
				rs = null;
				pstmt = null;
				conn = null;
				return "영업시간 정보가 없습니다. 관리자에게 문의하시기 바랍니다.";
			}

			//현재 시스템(서버) 시간 받아오기
			GregorianCalendar gc = new GregorianCalendar();
	        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
	        Date date = gc.getTime();
	        String nowDateStr = sf.format(date);//현재시간 str = 20121128123000

	        Holiday_Check holiday_Check = new Holiday_Check();

	        if (ReleaseType.equals("4")){//일반적용(적용일시)

	        	ecamsLogger.error("StartTime:"+StartTime+" , EndTime:"+EndTime+" , ReleaseTime:"+ReleaseTime);

	        	if (holiday_Check.chkBusinessDay(ReleaseDay) > 0){
	        	//배포시간이 영업일 아닐때
					returnStr = "일반적용 신청은 주말 또는 휴일에 배포될 수 없습니다. 영업일 " + EndTime.substring(0,2) + ":" + EndTime.substring(2) + "분 이후로 조정 바랍니다.";

	        	} else if (!nowDateStr.substring(0,8).equals(ReleaseDay) && Integer.parseInt(ReleaseTime) < Integer.parseInt(EndTime)) {
	        	//신청일과 배포일이 틀리고 배포시간이 EndTime 이전 일때
					returnStr =  "일반적용 신청은 "+EndTime.substring(0,2) + ":" + EndTime.substring(2)+"분 이전에 배포될 수 없습니다. [수시적용]을 활용하시기 바랍니다.";

	        	} else if (Integer.parseInt(StartTime) <= Integer.parseInt(ReleaseTime) && Integer.parseInt(ReleaseTime) < Integer.parseInt(EndTime)) {
	        	//배포시간이 영업일 StartTime ~ EndTime 일때
					returnStr =  "일반적용 신청은 온라인("+StartTime.substring(0,2) + ":" + StartTime.substring(2)+"~"+EndTime.substring(0,2) + ":" + EndTime.substring(2)+") 시간에 배포될 수 없습니다.[수시적용]을 활용하시기 바랍니다.";

	        	} else if (holiday_Check.chkBusinessDay(nowDateStr.substring(0,8))== 0 &&	Integer.parseInt(nowDateStr.substring(8)) >= Integer.parseInt(EndTime+"00") && nowDateStr.substring(0,8).equals(ReleaseDay)){
				//신청 시점이 영업일 EndTime시 이후이면서, 신청일과 배포일일 동일할때

	    	        java.util.Date tmpdate = new java.text.SimpleDateFormat("yyyyMMddHHmm").parse( ReleaseDay+EndTime );
	    	        Calendar calendar = Calendar.getInstance ( );
	    	        calendar.setTime( tmpdate );
	    	        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 30);
	    	        if (Integer.toString(calendar.get(Calendar.MINUTE)).length() == 1){
	    	        	returnStr =  "금일 일반적용 신청이 마감되었습니다.\n다음 영업일 "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+"0분 이후로 배포시간을 조정하시거나, [긴급적용]을 활용하시기 바랍니다.";
	    	        } else {
	    	        	returnStr =  "금일 일반적용 신청이 마감되었습니다.\n다음 영업일 "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+"분 이후로 배포시간을 조정하시거나, [긴급적용]을 활용하시기 바랍니다.";
	    	        }
				}

			} else if (ReleaseType.equals("0")){//수시적용
				//영업일 체크
				if (holiday_Check.chkBusinessDay(nowDateStr.substring(0,8))>0){//영업일 아닐때
					returnStr =  "영업일이 아닙니다.\n[긴급적용]을 활용하시기 바랍니다.";

				} else if (Integer.parseInt(nowDateStr.substring(8)) > Integer.parseInt(EndTime+"00")){//신청시간이 18시 이후 일때
					returnStr =  "금일 수시적용 신청이 마감되었습니다.\n[긴급적용]을 활용하시기 바랍니다.";
				}
			}

			holiday_Check = null;
			gc = null;
			sf = null;
			date = null;

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return returnStr;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.chkRelease() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.chkRelease() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.chkRelease() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.chkRelease() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.chkRelease() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of chkRelease() method statement

	public int execShell(String shFile,String parmName,boolean viewSw) throws Exception {
		SystemPath		  cTempGet	  = new SystemPath();
		String			  tmpPath = "";
		String			  strBinPath = "";
		File shfile=null;
		String  shFileName = "";
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;

		String outFile = "";
		File outf = null;
		//ByteArrayOutputStream fileStream = null;
		
		try {
			
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");
			
			shFileName = tmpPath + "/" + shFile; 			
			shfile = new File(shFileName);
			
			if( !(shfile.isFile()) )              //File존재여부
			{
				shfile.createNewFile();          //File생성
			}
			
			if (viewSw) {
				outFile = shFileName.replace(".sh", ".out"); 			
				outf = new File(outFile);
				
				if((outf.isFile()))              //File존재여부
				{
					outf.delete();         //File생성
				}
				parmName = parmName + " >"+outFile;
			}
			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write(parmName+"\n");
			writer.write("exit $?\n");
			writer.close();
			
			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;			
			
			run = Runtime.getRuntime();

			ecamsLogger.error("+++ server command execute +++"+parmName);
			p = run.exec(strAry);
			p.waitFor();
			
			run = Runtime.getRuntime();
			
			strAry = new String[2];
			
			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;
			
			p = run.exec(strAry);
			p.waitFor();

			if (p.exitValue() == 0) {
				shfile.delete();
			}								
			return p.exitValue();			
			
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.execShell() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200.execShell() Exception END ##");				
			throw exception;
		}finally{
			//fileStr = fileStream.toString("EUC-KR");	
		}
	}//execShell

	public int execShell_was(String shFile,String parmName,boolean viewSw) throws Exception {
		SystemPath		  cTempGet	  = new SystemPath();
		String			  tmpPath = "";
		String			  strBinPath = "";
		File shfile=null;
		String  shFileName = "";
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;

		String outFile = "";
		File outf = null;
		//ByteArrayOutputStream fileStream = null;
		
		try {
			
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");
			
			shFileName = tmpPath + "/" + shFile; 			
			shfile = new File(shFileName);
			
			if( !(shfile.isFile()) )              //File존재여부
			{
				shfile.createNewFile();          //File생성
			}
			
			if (viewSw) {
				outFile = shFileName.replace(".sh", ".out"); 			
				outf = new File(outFile);
				
				if((outf.isFile()))              //File존재여부
				{
					outf.delete();         //File생성
				}
				parmName = parmName + " >"+outFile;
			}
			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write(parmName+"\n");
			writer.write("exit $?\n");
			writer.close();
			
			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;			
			
			run = Runtime.getRuntime();

			ecamsLogger.error("+++ server command execute +++"+parmName);
			p = run.exec(strAry);
			p.waitFor();
			
			run = Runtime.getRuntime();
			
			strAry = new String[2];
			
			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;
			
			p = run.exec(strAry);
			p.waitFor();

			if (p.exitValue() == 0) {
				shfile.delete();
			}								
			return p.exitValue();			
			
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.execShell_was() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200.execShell_was() Exception END ##");				
			throw exception;
		}finally{
			//fileStr = fileStream.toString("EUC-KR");	
		}
	}//execShell_was
	
	public int execShell_ap(String shFile,String parmName,boolean viewSw) throws Exception {
		SystemPath		  cTempGet	  = new SystemPath();
		String			  tmpPath = "";
		String			  strBinPath = "";
		File shfile=null;
		String  shFileName = "";
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;

		String outFile = "";
		File outf = null;
		//ByteArrayOutputStream fileStream = null;
		
		try {
			
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");
			String ecamsInfo = cTempGet.geteCAMSInfo();
			
			shFileName = tmpPath + "/" + shFile; 			
			shfile = new File(shFileName);
			
			if( !(shfile.isFile()) )              //File존재여부
			{
				shfile.createNewFile();          //File생성
			}
			
			if (viewSw) {
				outFile = shFileName.replace(".sh", ".out"); 			
				outf = new File(outFile);
				
				if((outf.isFile()))              //File존재여부
				{
					outf.delete();         //File생성
				}
				parmName = parmName + " >"+outFile;
			}
			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write("./ecams_batexec "+ecamsInfo+ " \"cd "+strBinPath+ ";"+ parmName + "\" \n");
			writer.write("exit $?\n");
			writer.close();
			
			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;			
			
			run = Runtime.getRuntime();

			
			p = run.exec(strAry);
			p.waitFor();
			
			run = Runtime.getRuntime();
			
			strAry = new String[2];
			
			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;
			
			p = run.exec(strAry);
			p.waitFor();

			if (p.exitValue() == 0) {
				//shfile.delete();
			}								
			return p.exitValue();			
			
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.execShell_ap() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0200.execShell_ap() Exception END ##");				
			throw exception;
		}finally{
			//fileStr = fileStream.toString("EUC-KR");	
		}
	}//execShell_ap
	public Object[] diffList(String UserId,String sysCd,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int i = 0;
		boolean sameSw = false;
		String  shFileName = "";
		String	fileName = "";
		String	outName = "";
		String  strParm = "";
		String  strMd5sum = "";

		File shfile=null;
		BufferedReader in1 = null;
		String	readline1 = "";
		int     ret = 0;
		try {
			conn = connectionContext.getConnection();
			SystemPath cTempGet = new SystemPath();
			String tmpPath = cTempGet.getTmpDir_conn("99",conn);
			rtList.clear();
			for (i=0;fileList.size()>i;i++) {
				rst = new HashMap<String,String>();
				rst = fileList.get(i);
				rst.put("diffrst", "해당사항없음");
				rst.put("diffrstcd", "0");
				
				if (rst.get("cm_info").substring(52,53).equals("1") && !rst.get("cr_lstver").equals("0")) {
					Cmr0200 cmr0200 = new Cmr0200();
					strParm = "./ecams_chkdoc_filediff " + fileList.get(i).get("cr_itemid")+ " "+UserId;
					shFileName = UserId+"_"+ fileList.get(i).get("cr_itemid") + "diff04.sh";
					ret = cmr0200.execShell(shFileName, strParm, false);
					if (ret != 0 && ret != 8 && ret != 9) {
						throw new Exception("파일비교 실패. run=["+strParm +"]" + " return=[" + ret + "]" );
					} else {
						if (ret == 0) {
							rst.put("diffrst", "파일변경안됨");
							rst.put("diffrstcd", "1");
						}
						else if (ret == 9) {
							rst.put("diffrst", "파일없음");
							rst.put("diffrstcd", "1");
						}
						else if (ret == 8) {
							rst.put("diffrst", "파일변경");
							rst.put("diffrstcd", "0");
						}
					}
				}
				rtList.add(rst);
			} 
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			//ecamsLogger.error("+++ rtList +++"+rtList.toString());
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0200.diffList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0200.diffList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.diffList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.diffList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.diffList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}//end of Cmr0200 class statement
