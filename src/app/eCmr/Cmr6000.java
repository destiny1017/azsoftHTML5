/*****************************************************************************************
	1. program ID	: Cmr6000.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	:
	5. auth		    :
	6. description	: 1. USER INFO.
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
import app.common.*;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr6000{

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
	public Object[] selectLocat(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			Holiday_Check     holichk     = new Holiday_Check();
			UserInfo          userinfo    = new UserInfo();
			String            strSta      = null;
			String            svEditor    = null;
			String            svNxtSign   = null;
			String            svHoli      = null;
			String            svPassOk    = null;
			String            rtJson      = "";
			String            rtMsg       = "";
			boolean           cnclSw      = false;

			boolean adminSw = userinfo.isAdmin_conn(UserId,conn);
			if(adminSw==false) adminSw = userinfo.getSecuInfo_conn(UserId,"50",conn);

			rst = new HashMap<String, String>();
						
			strQuery.setLength(0);
			strQuery.append("SELECT a.CR_STATUS,NVL(a.CR_CNCL,'0') CNCL,a.CR_EDITOR, \n");
			strQuery.append("       a.CR_PASSOK,c.cm_codename 						 \n");
			strQuery.append("  FROM CMR1000 a,cmm0020 c 							 \n");
			strQuery.append(" WHERE a.Cr_ACPTNO = ?         						 \n");
			strQuery.append("   and c.cm_macode='REQUEST'   						 \n");
			strQuery.append("   and c.cm_micode=a.cr_qrycd  						 \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, AcptNo);
            rs = pstmt.executeQuery();
            rsval.clear();

			if (rs.next()){
				if (rs.getString("cncl").equals("1")) {
					rtJson = "취소완료";
					cnclSw = true;
				}
				else if (rs.getString("cr_status").equals("3")) rtJson = "반려완료";
				else if (rs.getString("cr_status").equals("9")) rtJson = "처리완료";

				strSta = rs.getString("cr_status");
				svEditor = rs.getString("cr_editor");
				svPassOk = rs.getString("cr_passok");
				rst.put("qrycd", rs.getString("cm_codename"));
			} else {
				rs.close();
				pstmt.close();
				
				strQuery.setLength(0);
				strQuery.append("SELECT a.cc_status,a.cc_editor,c.cm_codename 	   	     \n");
				strQuery.append("  FROM CMC0300 a,cmm0020 c 							 \n");
				strQuery.append(" WHERE a.cc_acptno = ?         						 \n");
				strQuery.append("   and c.cm_macode='REQUEST'   						 \n");
				strQuery.append("   and c.cm_micode=a.cc_qrycd  						 \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, AcptNo);
	            rs = pstmt.executeQuery();
				if (rs.next()){
					if (rs.getString("cc_status").equals("3")) rtJson = "반려완료";
					else if (rs.getString("cc_status").equals("9")) rtJson = "결재완료";

					strSta = rs.getString("cc_status");
					svEditor = rs.getString("cc_editor");
					svPassOk = "0";
					rst.put("qrycd", rs.getString("cm_codename"));
				} 
			}
			svHoli = holichk.SelectHoli();
			holichk = null;

			rs.close();
			pstmt.close();

			if (rtJson == null || rtJson == "" || cnclSw == true) {
				strQuery.setLength(0);
				strQuery.append("SELECT A.CR_LOCAT,A.CR_TEAMCD,A.CR_TEAM,B.CM_CODENAME USER1,  \n");
				strQuery.append("       a.cr_conmsg,a.cr_baseusr,                              \n");
				strQuery.append("       to_char(a.cr_confdate,'yyyy/mm/dd hh24:mi:ss') confdate\n");
				strQuery.append("  FROM CMR9900 A,CMM0020 B                                    \n");
				strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD='1'                    \n");
				strQuery.append("   AND A.CR_LOCAT='00'                                        \n");
				strQuery.append("   AND B.CM_MACODE='SYSGBN' AND B.CM_MICODE=A.CR_TEAM         \n");
				strQuery.append(" UNION ALL                                                    \n");
				strQuery.append("SELECT A.CR_LOCAT,A.CR_TEAMCD,A.CR_TEAM,B.CM_USERNAME USER1,  \n");
				strQuery.append("       a.cr_conmsg,a.cr_baseusr,                              \n");
				strQuery.append("       to_char(a.cr_confdate,'yyyy/mm/dd hh24:mi:ss') confdate\n");
				strQuery.append("  FROM CMR9900 A,CMM0040 B                                    \n");
				strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD IN ('2','3','6','7','8','C') \n");
				strQuery.append("   AND A.CR_LOCAT='00'                                        \n");
				strQuery.append("   AND A.CR_TEAM=B.CM_USERID                                  \n");
				strQuery.append(" UNION ALL                                                    \n");
				strQuery.append("SELECT A.CR_LOCAT,A.CR_TEAMCD,A.CR_TEAM,A.CR_CONFNAME USER1,  \n");
				strQuery.append("       a.cr_conmsg,a.cr_baseusr,                              \n");
				strQuery.append("       to_char(a.cr_confdate,'yyyy/mm/dd hh24:mi:ss') confdate\n");
				strQuery.append("  FROM CMR9900 A                                              \n");
				strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD IN ('4','5','9','P')   \n");
				strQuery.append("   AND A.CR_LOCAT='00'                                        \n");
			    strQuery.append(" ORDER BY CR_LOCAT                                            \n");
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, AcptNo);
	            pstmt.setString(2, AcptNo);
	            pstmt.setString(3, AcptNo);

	            rs = pstmt.executeQuery();

	            int a = 0;

	            if (rs.next()){
	            	if (cnclSw == true && rs.getString("cr_conmsg") != null && rs.getString("cr_conmsg") != "") {
	            		rtMsg = "[취소사유 : "+ rs.getString("cr_conmsg") + "]";
	            		if (rs.getString("confdate") != null && rs.getString("confdate")!= "")
	            			rtMsg = rtMsg + " - " + rs.getString("confdate") + " ";
	            	    if (rs.getString("cr_baseusr") != null) {
	            	    	strQuery.setLength(0);
	            	    	strQuery.append("select cm_username from cmm0040 where cm_userid=?  ");
	            	    	pstmt = conn.prepareStatement(strQuery.toString());
	        	            pstmt.setString(1, rs.getString("cr_baseusr"));
	        	            rs2 = pstmt.executeQuery();
	        	            if (rs2.next()) {
	        	            	rtMsg = rtMsg + rs2.getString("cm_username") + "님 취소";
	        	            	a = 1;
	        	            }
	        	            rs2.close();
	            	    }
	            	}
	            	if (a == 1){
	            		rtJson = "반려완료";
	            	}else{
						if (rs.getString("user1") != null) {
							if (rs.getString("cr_teamcd").equals("1"))
								rtJson = "[" + rs.getString("user1") + "] 중 입니다";
					    	else if (rs.getString("cr_teamcd").equals("4") || rs.getString("cr_teamcd").equals("9"))
					    		rtJson = "[" + rs.getString("user1") + "] 결재대기 중 입니다";
					    	else if (rs.getString("cr_teamcd").equals("8"))
					    		rtJson = "처리완료";
					    		//rtJson = "참조 결재대기 중 입니다";
					    	else {
					    		rtJson = "[" + rs.getString("user1") + "]님 결재대기 중 입니다";
					    		svNxtSign = rs.getString("cr_team");
					    	}
						}
	            	}
	            }
	            rs.close();
	            pstmt.close();
			}

			//ecmmtb.init_Xml("rows","confname","sta","editor","team","holigbn","passok","parentNode");

			rst.put("rows",    "1");
			rst.put("confname",rtJson);                                 //1현재상황
			rst.put("msg",     rtMsg);                                  //2상태
			rst.put("sta",     strSta);                                 //2상태
			rst.put("editor",  svEditor);                               //3요청자사번
			rst.put("team",    svNxtSign);                              //4현재결재자
			rst.put("holigbn", svHoli);                                 //5휴일구분
			rst.put("passok",  svPassOk);                               //6처리구분

			if (adminSw == true) rst.put("admin", "Y");
			else rst.put("admin", "N");

			rsval.add(rst);
			rst = null;

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;


			return rsval.toArray();


	    } catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectLocat() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr6000.selectLocat() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectLocat() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr6000.selectLocat() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr6000.selectLocat() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectLocat() method statement


	public Object[] selectConfirm(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String            strUser     = "";
			String            strCncl     = "";
			String            strGbn      = "";
			String            strBaseName = "";
			String            strConfName = "";
			String            strAttYn    = "N";
			String            strAttFile  = "";
			String            strConfDate = "";
	        String            strConMsg = "";
	        String            strSgnGbn = "";
	        String            strConfUsr = "";
	        String            orgstep = "";
	        String            strAcptDate = "";
	        String            strLocat = "";
	        boolean           findSw = false;

	        strQuery.setLength(0);
	        if (Integer.parseInt(AcptNo.substring(4,6)) > 30) {
				strQuery.append("SELECT to_char(A.CC_ACPTDATE,'yyyy/mm/dd hh24:mi:ss') as acptdate,\n");
				strQuery.append("       '0' as cncl,C.CM_USERNAME  \n");
				strQuery.append("  FROM CMM0040 C,CMC0300 A     \n");
				strQuery.append(" WHERE A.CC_ACPTNO = ?         \n");
				strQuery.append("   AND A.CC_EDITOR=C.CM_USERID \n");
	        } else {	
				strQuery.append("SELECT to_char(A.CR_ACPTDATE,'yyyy/mm/dd hh24:mi:ss') as acptdate,\n");
				strQuery.append("       nvl(A.CR_CNCL,'0') as cncl,C.CM_USERNAME \n");
				strQuery.append("  FROM CMM0040 C,CMR1000 A     \n");
				strQuery.append(" WHERE A.CR_ACPTNO = ?         \n");
				strQuery.append("   AND A.CR_EDITOR=C.CM_USERID \n");
	        }

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, AcptNo);
            rs = pstmt.executeQuery();
            rsval.clear();

			if (rs.next()){
				strUser = rs.getString("cm_username");
				strCncl = rs.getString("cncl");
				strAcptDate = rs.getString("acptdate");
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("SELECT A.CR_LOCAT,A.CR_CONFNAME,A.CR_TEAM,A.CR_TEAMCD,A.CR_STATUS,   \n");
			strQuery.append("       A.CR_CONGBN,A.CR_COMMON,A.CR_BLANK,A.CR_EMGER,A.CR_HOLI,      \n");
			strQuery.append("       A.CR_SGNGBN,A.CR_BLANKCD,A.CR_ORGSTEP,A.CR_BASEUSR,           \n");
			strQuery.append("       DECODE(A.CR_CONFDATE,NULL,'',TO_CHAR(A.CR_CONFDATE,'YYYY/MM/DD HH24:Mi:ss')) CONFDATE,  \n");
			strQuery.append("  		A.CR_CONFUSR,A.CR_CONMSG,A.CR_EMGAFT,B.CM_CODENAME USER1      \n");
			strQuery.append("  FROM CMR9900 A,CMM0020 B                                           \n");
			strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD='1'                           \n");
			strQuery.append("   AND B.CM_MACODE='SYSGBN' AND B.CM_MICODE=A.CR_TEAM                \n");
			strQuery.append("   AND B.CM_CLOSEDT is null  AND B.CM_MICODE <> '****'              \n");
			strQuery.append(" UNION ALL                                                           \n");
			strQuery.append("SELECT A.CR_LOCAT,A.CR_CONFNAME,A.CR_TEAM,A.CR_TEAMCD,A.CR_STATUS,   \n");
			strQuery.append("       A.CR_CONGBN,A.CR_COMMON,A.CR_BLANK,A.CR_EMGER,A.CR_HOLI,      \n");
			strQuery.append("       A.CR_SGNGBN,A.CR_BLANKCD,A.CR_ORGSTEP,A.CR_BASEUSR,           \n");
			strQuery.append("       DECODE(A.CR_CONFDATE,NULL,'',TO_CHAR(A.CR_CONFDATE,'YYYY/MM/DD HH24:Mi:ss')) CONFDATE, \n");
			strQuery.append("  		A.CR_CONFUSR,A.CR_CONMSG,A.CR_EMGAFT,'본인확인' USER1          \n");
			strQuery.append("  FROM CMR9900 A                                                    \n");
			strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD='2'                          \n");
			strQuery.append(" UNION ALL                                                          \n");
			strQuery.append("SELECT A.CR_LOCAT,A.CR_CONFNAME,A.CR_TEAM,A.CR_TEAMCD,A.CR_STATUS,  \n");
			strQuery.append("       A.CR_CONGBN,A.CR_COMMON,A.CR_BLANK,A.CR_EMGER,A.CR_HOLI,     \n");
			strQuery.append("       A.CR_SGNGBN,A.CR_BLANKCD,A.CR_ORGSTEP,A.CR_BASEUSR,          \n");
			strQuery.append("       DECODE(A.CR_CONFDATE,NULL,'',TO_CHAR(A.CR_CONFDATE,'YYYY/MM/DD HH24:Mi:ss')) CONFDATE,   \n");
			strQuery.append("  		A.CR_CONFUSR,A.CR_CONMSG,A.CR_EMGAFT,B.CM_USERNAME USER1     \n");
			strQuery.append("  FROM CMR9900 A,CMM0040 B                                          \n");
			strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD IN ('3','6','7','8','C','P') \n");
			strQuery.append("   AND A.CR_TEAM=B.CM_USERID                                        \n");
			strQuery.append(" UNION ALL                                                          \n");
			strQuery.append("SELECT A.CR_LOCAT,A.CR_CONFNAME,A.CR_TEAM,A.CR_TEAMCD,A.CR_STATUS,  \n");
			strQuery.append("       A.CR_CONGBN,A.CR_COMMON,A.CR_BLANK,A.CR_EMGER,A.CR_HOLI,     \n");
			strQuery.append("       A.CR_SGNGBN,A.CR_BLANKCD,A.CR_ORGSTEP,A.CR_BASEUSR,          \n");
			strQuery.append("       DECODE(A.CR_CONFDATE,NULL,'',TO_CHAR(A.CR_CONFDATE,'YYYY/MM/DD HH24:Mi:ss')) CONFDATE, \n");
			strQuery.append("  		A.CR_CONFUSR,A.CR_CONMSG,A.CR_EMGAFT,                        \n");
			strQuery.append("  		'CONFMAN' USER1                                              \n");
			strQuery.append("  FROM CMR9900 A                                                    \n");
			strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD IN ('4','5','9')             \n");
			strQuery.append(" UNION ALL                                                          \n");
			strQuery.append("SELECT A.CR_LOCAT,A.CR_CONFNAME,A.CR_TEAM,A.CR_TEAMCD,A.CR_STATUS,  \n");
			strQuery.append("       A.CR_CONGBN,A.CR_COMMON,A.CR_BLANK,A.CR_EMGER,A.CR_HOLI,     \n");
			strQuery.append("       A.CR_SGNGBN,A.CR_BLANKCD,A.CR_ORGSTEP,A.CR_BASEUSR,          \n");
			strQuery.append("       DECODE(A.CR_CONFDATE,NULL,'',TO_CHAR(A.CR_CONFDATE,'YYYY/MM/DD HH24:Mi:ss')) CONFDATE, \n");
			strQuery.append("  		A.CR_CONFUSR,A.CR_CONMSG,A.CR_EMGAFT,B.CM_CODENAME USER1     \n");
			strQuery.append("  FROM CMR9900 A,CMM0020 B                                          \n");
			strQuery.append(" WHERE A.CR_ACPTNO = ? AND A.CR_TEAMCD='P'                          \n");
			strQuery.append("   AND B.CM_MACODE='PRJJIK' AND INSTR(A.CR_TEAM,B.CM_MICODE)>0      \n");
			strQuery.append("   AND B.CM_CLOSEDT is null  AND B.CM_MICODE <> '****'              \n");
			strQuery.append(" ORDER BY CR_LOCAT ");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			int paramIdx = 1;
            pstmt.setString(paramIdx++, AcptNo);
            pstmt.setString(paramIdx++, AcptNo);
            pstmt.setString(paramIdx++, AcptNo);
            pstmt.setString(paramIdx++, AcptNo);
            pstmt.setString(paramIdx++, AcptNo);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            while (rs.next()){
            	findSw = true;
            	if (strLocat != null && strLocat != "") {
            		if (strLocat.equals(rs.getString("cr_locat"))) findSw = false;
            	}

            	if (findSw == true) {
            		strLocat = rs.getString("cr_locat");
					strGbn = "";
					strBaseName = "";
					strConfName = "";
					if (rs.getString("user1") != null) {
						if (rs.getString("user1").equals("CONFMAN") && !rs.getString("cr_locat").equals("00")) {
							strQuery.setLength(0);
							strQuery.append("select cm_codename from cmm0020                   \n");
							strQuery.append(" where cm_macode='RGTCD' and instr(?,cm_micode)>0 \n");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt2.setString(1, rs.getString("cr_team"));
							//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            while (rs2.next()) {
				            	if (strConfName != null && strConfName != "") strConfName = strConfName + ",";
				            	strConfName = strConfName + rs2.getString("cm_codename");
				            }
				            rs2.close();
				            pstmt2.close();
						} else strConfName =rs.getString("user1");
					} else
					   strConfName = "";

					strAttFile = "";
					strAttYn = "N";
					strConfDate = "";
					strConMsg = "";
					strSgnGbn = "";
			        strConfUsr = "";
			        orgstep = "";
					if (rs.getString("cr_locat").equals("00")){

						strConfName = "";
					    if ((rs.getString("cr_conmsg") != null) || strCncl.equals("1")) {
					    	strBaseName = rs.getString("cr_conmsg");
					    }
					    if (rs.getString("cr_status").equals("0")) {
					    	if (rs.getString("cr_teamcd").equals("1"))
										strConfName = "[" + strConfName + "] 중 입니다";
					    	else if (rs.getString("cr_teamcd").equals("2")) strConfName = "[" + strUser + "]님 결재대기 중 입니다";
					    	else if (rs.getString("cr_teamcd").equals("4") || rs.getString("cr_teamcd").equals("5") || rs.getString("cr_teamcd").equals("9"))
					    		 strConfName = "[" + rs.getString("cr_confname") + "] 결재대기 중 입니다";
					    	else strConfName = "[" + strConfName + "]님 결재대기 중 입니다";
					    } else {
					    	if (rs.getString("cr_status").equals("3")) strConfName = "반려";
					    	else strConfName = "처리완료";
					    }

						if (rs.getString("cr_sgngbn") != null) strSgnGbn = rs.getString("cr_sgngbn");
						if (rs.getString("cr_confusr") != null) strConfUsr = rs.getString("cr_confusr");
						if (rs.getString("cr_orgstep") != null) orgstep = rs.getString("cr_orgstep");
						//ecmmtb.init_Xml("rows","confname","team","teamcd","confdate","baseuser",
			            // 		"conmsg","attfile","team2","rgtcd","baseuser2","confusr","teamcd2","locat",
			            //		"attfile2","congbn","common","blank","emger","holi","emgaft","rowgbn","parentNode");
						rst = new HashMap<String, String>();
						rst.put("rows", Integer.toString(rs.getRow()));
						rst.put("confname",	"요청자");                              //1결재단계명
						rst.put("team",	strUser);                                   //2결재자
						rst.put("teamcd",	"신청");                                //3구분
						rst.put("confdate",	strAcptDate);                           //4결재일시
						rst.put("baseuser",	"");                                    //5원결재자
						rst.put("conmsg",	"");                                    //6결재의견
						rst.put("attfile",	"");                                    //7첨부파일유무
						rst.put("team2",	rs.getString("cr_team"));               //8결재가능팀
						rst.put("rgtcd",	strSgnGbn);                             //9결재가능직무
						rst.put("baseuser2",	rs.getString("cr_baseusr"));        //10원결재자
						rst.put("confusr",	strConfUsr);                            //11대결재자
						rst.put("teamcd2",	rs.getString("cr_teamcd"));             //12결재구분
						rst.put("locat",	rs.getString("cr_locat"));              //13결재단계
						rst.put("attfile2",	strAttFile);                            //14첨부파일명
						rst.put("congbn",	rs.getString("cr_congbn"));             //15cr_congbn
						rst.put("common",	rs.getString("cr_common"));             //16cr_common
						rst.put("blank",	rs.getString("cr_blank"));              //17cr_blank
						rst.put("emger",	rs.getString("cr_emger"));              //18cr_emger
						rst.put("holi",	rs.getString("cr_holi"));                   //19cr_holi
						rst.put("emgaft",	orgstep);                               //20cr_orgstep
						rst.put("rowgbn",	"step");                                //21rowgbn
						rst.put("cr_acptno", AcptNo);
						rsval.add(rst);
						rst = null;
					} else{
						if (rs.getString("cr_status").equals("0")){
							if (rs.getString("cr_team").equals(rs.getString("cr_baseusr")) ||
								(rs.getString("cr_teamcd").equals("3") ||
								 rs.getString("cr_teamcd").equals("6") || rs.getString("cr_teamcd").equals("7") ||
								 rs.getString("cr_teamcd").equals("8") || rs.getString("cr_teamcd").equals("C"))) {

								strGbn = "미결";
						        if (rs.getString("cr_congbn").equals("4") || rs.getString("cr_congbn").equals("5")){
						        	strGbn = "미결(후결)";
						        } else if (rs.getString("cr_teamcd").equals("1")){
					        		strGbn = "미처리";
					        	} else if (rs.getString("cr_teamcd").equals("8")){
					        		strGbn = "참조";
					        	}
							}
						} else if (rs.getString("cr_status").equals("9")){
							if (rs.getString("cr_confusr").equals(rs.getString("cr_baseusr")) ||
								rs.getString("cr_team").equals(rs.getString("cr_baseusr"))	){
								if (rs.getString("cr_teamcd").equals("1")) strGbn = "처리";
					        	else
					        		if (rs.getString("cr_congbn").equals("4") || rs.getString("cr_congbn").equals("5") ||rs.getString("cr_congbn").equals("7"))
					        				strGbn = "결재(후결)";
					        		else if (rs.getString("cr_congbn").equals("0")) strGbn = "생략";
					        		else strGbn = "결재";
							} else {
								if (rs.getString("cr_congbn").equals("4") || rs.getString("cr_congbn").equals("5") || rs.getString("cr_congbn").equals("7"))
			        				strGbn = "결재(후결)";
				        		else if (rs.getString("cr_congbn").equals("0")) strGbn = "생략";
				        		else if (rs.getString("cr_teamcd").equals("4") || rs.getString("cr_teamcd").equals("5") || rs.getString("cr_teamcd").equals("9")) {
				        			strGbn = "결재";
				        		} else strGbn = "결재(대결)";
							}
						} else if (rs.getString("cr_status").equals("3") && rs.getString("confdate") != null){
							if (strCncl.equals("1")) strGbn = "취소";
							else strGbn = "반려";
						} else if (rs.getString("cr_status").equals("3")){
							strGbn = "미결";
						}
						if (rs.getString("confdate") != null) strConfDate = rs.getString("confdate");

						if ((rs.getString("cr_blankcd") != null) && (!rs.getString("cr_status").equals("3"))) {

							strQuery.setLength(0);
							strQuery.append("SELECT CM_CODENAME FROM CMM0020 ");
							strQuery.append(" WHERE CM_MACODE='DAEGYUL' AND CM_MICODE=? ");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(1, rs.getString("cr_blankcd"));
				            rs2 = pstmt2.executeQuery();
				            //ecamsLogger.error("## Query 5 END : ");
							if (rs2.next()){
								strGbn = strGbn + "["+rs2.getString("cm_codename")+"]";
							}
							rs2.close();
							pstmt2.close();

						}
						if (rs.getString("cr_confusr") != null) {
							if  (rs.getString("cr_teamcd").equals("4") || rs.getString("cr_teamcd").equals("5") || rs.getString("cr_teamcd").equals("9")
								 || rs.getString("cr_teamcd").equals("P")) {
								if (rs.getString("cr_congbn").equals("5")) strGbn = "자동결재";

								strQuery.setLength(0);
								strQuery.append("SELECT CM_USERNAME FROM CMM0040 ");
								strQuery.append(" WHERE CM_USERID=? ");

								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, rs.getString("cr_confusr"));
					            rs2 = pstmt2.executeQuery();
					            //ecamsLogger.error("## Query 6 END : ");
								if (rs2.next()){
									strConfName = rs2.getString("cm_username");
								}
								rs2.close();
								pstmt2.close();
							}
							if  (!rs.getString("cr_confusr").equals(rs.getString("cr_baseusr"))) {
								strQuery.setLength(0);
								strQuery.append("SELECT CM_USERNAME FROM CMM0040 ");
								strQuery.append(" WHERE CM_USERID=? ");

								pstmt2 = conn.prepareStatement(strQuery.toString());
								pstmt2.setString(1, rs.getString("cr_baseusr"));
					            rs2 = pstmt2.executeQuery();
					            //ecamsLogger.error("## Query 7 END : ");
								if (rs2.next()){
									strBaseName = rs2.getString("cm_username");
								}
								rs2.close();
								pstmt2.close();
							}
						}
						else {
							if (rs.getString("cr_teamcd").equals("3") || rs.getString("cr_teamcd").equals("6") ||
								rs.getString("cr_teamcd").equals("7") || rs.getString("cr_teamcd").equals("8") ||
								rs.getString("cr_teamcd").equals("C"))
								if (!rs.getString("cr_team").equals(rs.getString("cr_baseusr"))) {
									strQuery.setLength(0);
									strQuery.append("SELECT CM_USERNAME FROM CMM0040 ");
									strQuery.append(" WHERE CM_USERID=? ");

									pstmt2 = conn.prepareStatement(strQuery.toString());
									pstmt2.setString(1, rs.getString("cr_baseusr"));
						            rs2 = pstmt2.executeQuery();
						            //ecamsLogger.error("## Query 8 END : ");
									if (rs2.next()){
										strBaseName = rs2.getString("cm_username");
										if (strGbn.equals("미결(후결)")) strBaseName =strBaseName + "(대결재자)";
									}
									rs2.close();
									pstmt2.close();
								}
						}
						/*
						strQuery.setLength(0);
						strQuery.append("SELECT CR_FILENAME,CR_RELDOC FROM CMR1001 ");
						strQuery.append(" WHERE CR_ACPTNO=? ");
						strQuery.append("   AND CR_GUBUN='C' || ? ");
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, AcptNo);
						pstmt2.setString(2,rs.getString("cr_locat"));

			            rs2 = pstmt2.executeQuery();
						if (rs2.next()){
							strAttYn = "Y";
							strAttFile = rs2.getString("cr_filename") + "," + rs2.getString("cr_reldoc");
						}

						rs2.close();
						pstmt2.close();
						*/
						if (rs.getString("cr_conmsg") != null) strConMsg = rs.getString("cr_conmsg");
						if (rs.getString("cr_sgngbn") != null) strSgnGbn = rs.getString("cr_sgngbn");
						if (rs.getString("cr_confusr") != null) strConfUsr = rs.getString("cr_confusr");
						if (rs.getString("cr_orgstep") != null) orgstep = rs.getString("cr_orgstep");

						rst = new HashMap<String, String>();
						rst.put("rows",     Integer.toString(rs.getRow()));
						rst.put("confname",	rs.getString("cr_confname"));               //1결재단계명
						rst.put("team",	    strConfName);                               //2결재자
						rst.put("teamcd",	strGbn);                                    //3구분
						rst.put("confdate",	strConfDate);                               //4결재일시
						rst.put("baseuser",	strBaseName);                               //5원결재자
						rst.put("conmsg",	strConMsg);                                 //6결재의견
						rst.put("attfile",	strAttYn);                                  //7첨부파일유무
						rst.put("team2",	rs.getString("cr_team"));                   //8결재가능팀
						rst.put("rgtcd",	strSgnGbn);                                 //9결재가능직무
						rst.put("baseuser2",rs.getString("cr_baseusr"));                //10원결재자
						rst.put("confusr",	strConfUsr);                                //11대결재자
						rst.put("teamcd2",	rs.getString("cr_teamcd"));                 //12결재구분
						rst.put("locat",	rs.getString("cr_locat"));                  //13결재단계
						rst.put("attfile2",	strAttFile);                                //14첨부파일명
						rst.put("congbn",	rs.getString("cr_congbn"));                 //15cr_congbn
						rst.put("common",	rs.getString("cr_common"));                 //16cr_common
						rst.put("blank",	rs.getString("cr_blank"));                  //17cr_blank
						rst.put("emger",	rs.getString("cr_emger"));                  //18cr_emger
						rst.put("holi",	    rs.getString("cr_holi"));                   //19cr_holi
						rst.put("emgaft",	orgstep);                                   //20cr_orgstep
						rst.put("rowgbn",	"step");                                    //21rowgbn
						rst.put("cr_acptno", AcptNo);
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
            conn = null;


    		return rsval.toArray();


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectConfirm()sub SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr6000.selectConfirm() sub SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectConfirm() sub Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr6000.selectConfirm() sub Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr6000.selectConfirm() sub connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of selectConfirm() method statement


	public Object[] selectDaegyul(String UserId,String BaseUser) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		StringBuffer      strQuery    = new StringBuffer();
		Integer           parmCnt     = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			parmCnt = 0;
			strQuery.append("select distinct a.cm_userid,a.cm_username,a.cm_status,a.cm_daegyul, \n");
			strQuery.append("       a.cm_blankdts,a.cm_blankdte,b.cm_codename                    \n");
			strQuery.append("  from cmm0020 b,cmm0040 a            \n");
			strQuery.append(" where a.cm_userid<>?                 \n");
			strQuery.append("   and a.cm_active='1'                \n");
			strQuery.append("   and b.cm_macode='POSITION'         \n");
			strQuery.append("   and b.cm_micode=a.cm_position      \n");
			strQuery.append(" order by a.cm_username               \n");
			//strQuery.append("   and a.cm_userid=c.cm_userid        \n");
			//strQuery.append("   and instr(?,c.cm_rgtcd)>0          \n");
			pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(++parmCnt, UserId);
            //pstmt.setString(++parmCnt, BaseUser);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            //ecmmtb.init_Xml("rows","username","userid","parentNode");
            while (rs.next()){
            	if (rs.getRow() == 1) {
            		//ecmmtb.init_Xml("rows","username","userid","parentNode");
            		rst = new HashMap<String, String>();
            		rst.put("rows", "0");
    				rst.put("username", "선택하세요");
    				rst.put("userid", "00");
    				rsval.add(rst);
            	}
            	parmCnt = 0;
                if (rs.getString("cm_blankdts") != null && rs.getString("cm_blankdte") != null) {
                	strQuery.setLength(0);
       			    strQuery.append("select count(*) cnt from dual          \n");
       			    strQuery.append(" where to_char(SYSDATE,'yyyymmdd')>=?  \n");
       			    strQuery.append("   and to_char(SYSDATE,'yyyymmdd')<=?  \n");
       			    pstmt = conn.prepareStatement(strQuery.toString());
       			    //pstmt =  new LoggableStatement(conn, strQuery.toString());
       			    pstmt.setString(1,rs.getString("cm_blankdts"));
       			    pstmt.setString(2,rs.getString("cm_blankdte"));
       			    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString()); 
       			    rs = pstmt.executeQuery();
       			    if (rs.next()) {
       				   parmCnt = rs.getInt("cnt");
       			    }
       			    rs.close();
       			    pstmt.close();
       			    
                    if (parmCnt > 0 &&rs.getString("cm_daegyul") != null) {
                	   strQuery.setLength(0);
                	   strQuery.append("select a.cm_username,b.cm_codename,a.cm_userid                    \n");
                	   strQuery.append("  from cmm0040 a,cmm0020 b                                        \n");
                	   strQuery.append(" where a.cm_userid=?                                              \n");
                	   strQuery.append("  and b.cm_macode='POSITION' and a.cm_position=b.cm_micode        \n");
                	   pstmt2 = conn.prepareStatement(strQuery.toString());
                	   //pstmt2 = new LoggableStatement(conn,strQuery.toString());
                	   pstmt2.setString(1, rs.getString("cm_daegyul"));
                	   ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
                	   rs2 = pstmt2.executeQuery();
                	   if (rs2.next()){
                		  // ecmmtb.init_Xml("rows","position","username","userid","parentNode");
                		   rst = new HashMap<String, String>();
	   	            	   rst.put("rows", Integer.toString(rs2.getRow()));
	   	    			   rst.put("username", rs2.getString("cm_codename") + " " +
	   	    					rs2.getString("cm_username"));
	   	    			   rst.put("userid", rs2.getString("cm_userid"));
	   	    			   rsval.add(rst);
	   	    			   rst = null;
                	   }
                	   rs2.close();
                	   pstmt2.close();
                   }
                }
                if (parmCnt==0) {
        		   rst = new HashMap<String, String>();
            	   rst.put("rows", Integer.toString(rs.getRow()));
    			   rst.put("username", rs.getString("cm_codename") + " " +
    					rs.getString("cm_username"));
    			   rst.put("userid", rs.getString("cm_userid"));
    			   rsval.add(rst);
    			   rst = null;
                }
            }
            rs.close();
            pstmt.close();
			conn.close();

			conn = null;
			pstmt = null;
			rs = null;

			return rsval.toArray();

	    } catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectDaegyul() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr6000.selectDaegyul() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr6000.selectDaegyul() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr6000.selectDaegyul() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr6000.selectDaegyul() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of selectDaegyul() method statement
	public String updtConfirm(String AcptNo,String Locat,String BlankCd,String SayuCd,String UserId,String DaeUser) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String			  rtJson      = null;
			String            ConfUsr     = null;
			String            FindSw      = null;
			String            strNxtPos   = null;
			//String            strNxtSign  = null;
			Integer           parmCnt     = 0;
			Integer           rsCnt       = 0;
			Boolean           nxtSw       = false;


			//System.out.println("1111111122222222");
			strQuery.setLength(0);
			strQuery.append("SELECT cr_confusr from cmr9900                            \n");
			strQuery.append(" WHERE cr_acptno=? and cr_locat='00'                      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			rs = pstmt.executeQuery();
			if (rs.next()) {
			   ConfUsr = rs.getString("cr_confusr");
			}
			rs.close();
			pstmt.close();

			//ecamsLogger.error("+++++++++Locat=="+Locat + " " + BlankCd + " " + SayuCd + " " + DaeUser);

            if (Integer.parseInt(ConfUsr.substring(0,2)) >= Integer.parseInt(Locat)){
            	if (Integer.parseInt(ConfUsr.substring(2,4)) <= Integer.parseInt(Locat)) {
            		if (BlankCd.equals("3")) {
            			strQuery.setLength(0);
            			strQuery.append("UPDATE cmr9900 set cr_team=?                    \n");
            			strQuery.append(" WHERE cr_acptno=? and cr_locat='00'            \n");
            			pstmt = conn.prepareStatement(strQuery.toString());
            			//pstmt = new LoggableStatement(conn,strQuery.toString());
            			parmCnt = 0;
            			pstmt.setString(++parmCnt, DaeUser);
            			pstmt.setString(++parmCnt, AcptNo);
            			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            			rsCnt = pstmt.executeUpdate();
            			pstmt.close();
            	   }
            	   else {
            		   strQuery.setLength(0);
            		   strQuery.append("SELECT cr_team,cr_locat,cr_teamcd,decode(cr_congbn,'6','3',cr_congbn) congbn \n");
            		   strQuery.append("  FROM cmr9900                                     \n");
            		   strQuery.append(" WHERE cr_acptno=? and to_number(cr_locat)>0       \n");
            		   strQuery.append("   and cr_locat != ? and cr_status='0'             \n");
            		   strQuery.append("   and cr_congbn in ('2','3','4','6')              \n");
            		   strQuery.append(" ORDER BY cr_congbn,cr_locat                       \n");
            		   pstmt = conn.prepareStatement(strQuery.toString());
            		   parmCnt = 0;
            		   pstmt.setString(++parmCnt, AcptNo);
            		   pstmt.setString(++parmCnt, Locat);
            		   rs = pstmt.executeQuery();

            		   FindSw = "0";

            		   while (rs.next()) {
            			   if (FindSw.equals("1")){
            				   if (!rs.getString("cr_teamcd").equals("8")){
            					   FindSw = "9";
            				   }
            				   else{
            					   strNxtPos = strNxtPos.substring(0,2) + rs.getString("cr_loat");
            				   }
            			   } else {
            				   strNxtPos = rs.getString("cr_locat") + rs.getString("cr_locat");
            				   FindSw = "1";
            			   }

            			   if ((FindSw.equals("1") && !rs.getString("cr_teamcd").equals("8")) || FindSw.equals("9")){
            				   break;
            			   }
            		   }

            		   rs.close();
            		   pstmt.close();

            		   //ecamsLogger.debug("+++++++++strNxtPos=="+strNxtPos+"++++++++strNxtSign=="+strNxtSign);

            		   if (strNxtPos == null) {
            			   rtJson = "다음 결재자가 없어 결재정보를 수정할 수 없습니다.";
            			   conn.rollback();
            			   conn.close();
            			   conn = null;
            			   rs = null;
            			   pstmt = null;
            			   return rtJson;
            		   }

            		   strQuery.setLength(0);

            		   strQuery.append("update cmr9900 set                                             \n");
            		   strQuery.append("(cr_confname,cr_team,cr_teamcd,cr_congbn,cr_common,cr_blank,   \n");
            		   strQuery.append(" cr_emger,cr_holi,cr_confusr,cr_sgngbn,cr_baseusr) =           \n");
            		   strQuery.append("(select cr_confname,cr_team,cr_teamcd,cr_congbn,cr_common,     \n");
            		   strQuery.append("        cr_blank,cr_emger,cr_holi,?, cr_sgngbn, cr_baseusr     \n");
            		   strQuery.append("   from cmr9900                                                \n");
            		   strQuery.append("  where cr_acptno=? and cr_locat=?)                            \n");
            		   strQuery.append(" where cr_acptno=? and cr_locat='00'                           \n");
            		   pstmt = conn.prepareStatement(strQuery.toString());
           			   //pstmt = new LoggableStatement(conn,strQuery.toString());
            		   parmCnt = 0;
            		   pstmt.setString(++parmCnt, strNxtPos.substring(0,2) + strNxtPos.substring(0,2));
            		   pstmt.setString(++parmCnt, AcptNo);
            		   pstmt.setString(++parmCnt, strNxtPos.substring(0,2));
            		   pstmt.setString(++parmCnt, AcptNo);
           			   //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            		   rsCnt = pstmt.executeUpdate();

            		   if (rsCnt != 1) {
            			   rtJson = "결재정보 수정이 실패하였습니다. [수정대상이 1개이상]";
            			   pstmt.close();
            			   conn.rollback();
            			   conn.close();
            			   conn = null;
            			   rs = null;
            			   pstmt = null;
            			   return rtJson;
            		   }

            		   pstmt.close();
            	   }
            	}
            }

            strQuery.setLength(0);
			strQuery.append("UPDATE CMR9900 SET                                        \n");
			if (!BlankCd.equals("4")){
				if (DaeUser != "") {
					strQuery.append("  cr_team=?                                           \n");
					nxtSw = true;
				}
			}
			if (SayuCd != "" && SayuCd != null)
				if (nxtSw == true) strQuery.append(",cr_blankcd=?                      \n");
				else {
					strQuery.append("cr_blankcd=?                                      \n");
				    nxtSw = true;
				}
			if (BlankCd.equals("0"))
			   if (nxtSw == true){
				   strQuery.append(",cr_congbn='0',cr_confdate=SYSDATE,                \n");
				   strQuery.append(" cr_cr_status='9',cr_confusr=?                     \n");
			   } else {
				   strQuery.append(" cr_congbn='0',cr_confdate=SYSDATE,                \n");
				   strQuery.append(" cr_cr_status='9',cr_confusr=?                     \n");
				   nxtSw = true;
			   }
			else if (BlankCd.equals("4"))
				if (nxtSw == true) strQuery.append(",cr_congbn=4                       \n");
				else strQuery.append("               cr_congbn=4                       \n");
			strQuery.append(" WHERE cr_acptno=?                                        \n");
			strQuery.append("   AND cr_locat=?                                         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

            parmCnt = 0;
			if (!BlankCd.equals("4")){
				if (DaeUser != "")
					pstmt.setString(++parmCnt, DaeUser);
			}

			if (SayuCd != "") pstmt.setString(++parmCnt, SayuCd);
			if (BlankCd.equals("0")) pstmt.setString(++parmCnt, UserId);

			pstmt.setString(++parmCnt, AcptNo);
			pstmt.setString(++parmCnt, Locat);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rsCnt = pstmt.executeUpdate();
			if (rsCnt == 1) {
				rtJson = "OK";
				conn.commit();
			}
			else {
				rtJson = "결재정보 수정이 실패하였습니다.";
				conn.rollback();
			}

			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rtJson;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr6000.updtConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr6000.updtConfirm() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr6000.updtConfirm() SQLException END ##");
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
					ecamsLogger.error("## Cmr0200.updtConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr6000.updtConfirm() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr6000.putDeploy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr6000.updtConfirm() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtConfirm() method statement

}//end of Cmr6000 class statement
