package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0202 {

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */

	public Object[] Confirm_Info(String UserId,String SysCd,String ReqCd,ArrayList<String> RsrcCd,ArrayList<String> JobCd,ArrayList<String> Pos,String EmgSw,String PrjNo) throws SQLException, Exception {
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
		ArrayList<HashMap<Object, Object>>         rsval = new ArrayList<HashMap<Object, Object>>();
		HashMap<Object, Object>			  rst		  = null;
		int				  pstmtcount  = 1;
		String            SvLine      = "";
		String            SvTag       = "";
		ArrayList<HashMap<String, String>>		  ArySv	  = null;
		HashMap<String, String>		HashSv  = null;
		String            SvUser      = "";
		String            SvSgnName   = "";
		String            SvBaseUser  = "";
		String            aRsrcCd     = "";
		boolean           FindSw      = true;
		Object[] returnObjectArray    = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			for (int i=0;i<RsrcCd.size();i++){
				if (i == 0){
					aRsrcCd = RsrcCd.get(i);
				}
				else{
					aRsrcCd = aRsrcCd + "," + RsrcCd.get(i);
				}
			}
			rsval.clear();
			pstmtcount = 1;
			strQuery.append("select b.cm_username,b.cm_project,a.cm_seqno,a.cm_name,\n");
			strQuery.append("      a.cm_gubun,a.cm_common,a.cm_blank,a.cm_holiday,  \n");
			strQuery.append("      a.cm_emg,a.cm_emg2,a.cm_orgstep,                 \n");
			strQuery.append("      a.cm_position,a.cm_jobcd,a.cm_rsrccd,            \n");
			strQuery.append("      a.cm_pgmtype,a.cm_prcsw                          \n");
			strQuery.append(" from cmm0060 a,cmm0040 b                              \n");
			strQuery.append("where a.cm_reqcd=?                                     \n");
			strQuery.append("  and a.cm_syscd=?                                     \n");
			strQuery.append("  and a.cm_manid=decode(b.cm_manid,'Y','1','2')        \n");
			strQuery.append("  and b.cm_userid=?                                    \n");
            strQuery.append("order by a.cm_seqno, a.cm_position                     \n");

            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(pstmtcount++, ReqCd);
            pstmt.setString(pstmtcount++, SysCd);
            pstmt.setString(pstmtcount++, UserId);

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

            rs = pstmt.executeQuery();
			while (rs.next()){
				FindSw = true;
	            if ( Pos.size() > 0 && rs.getString("cm_position") != null) {
	            	FindSw = false;
	            	for (int i=0;i<Pos.size();i++){
	            		if (rs.getString("cm_position").indexOf(Pos.get(i)) > -1) {
	            			FindSw = true;
	            			break;
	            		}
	    			}
	            }
	            if ( JobCd.size() > 0 && rs.getString("cm_jobcd") != null) {
	            	FindSw = false;
	            	for (int i=0;i<JobCd.size();i++){
	            		if (rs.getString("cm_jobcd").indexOf(JobCd.get(i)) > -1) {
	            			FindSw = true;
	            			break;
	            		}
	    			}

	            	if (FindSw == false){
	            		if (rs.getString("cm_jobcd").indexOf("SYS") > -1){
	            			FindSw = true;
	            		}
	            	}
	            }

	            if ( RsrcCd.size() > 0 && rs.getString("cm_rsrccd") != null) {
	            	FindSw = false;
	            	for (int i=0;i<RsrcCd.size();i++){
	            		if (rs.getString("cm_rsrccd").indexOf(RsrcCd.get(i)) > -1) {
	            			FindSw = true;
	            			break;
	            		}
	    			}
	            }

	            if (FindSw == true) {
					SvLine = "";
					SvTag = "";
					SvUser = "";
					SvSgnName = "";
					ArySv = new ArrayList<HashMap<String, String>>();
					FindSw = false;
					if (EmgSw.equals("Y")){
						SvLine = rs.getString("cm_emg");
					}
					else{
						SvLine = rs.getString("cm_common");
					}

					FindSw = true;
					if (!SvLine.equals("")) {
						if (rs.getString("cm_gubun").equals("3") || rs.getString("cm_gubun").equals("5") ||
						    rs.getString("cm_gubun").equals("6") || rs.getString("cm_gubun").equals("7")) {
							strQuery.setLength(0);
							pstmtcount = 1;
							strQuery.append("select count(*) as cnt from cmm0043                          \n");
							strQuery.append(" where cm_userid=? and instr(?,cm_rgtcd)>0                   \n");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt2.setString(pstmtcount++, UserId);
				            pstmt2.setString(pstmtcount++, rs.getString("cm_position"));
				            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());

				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	if (rs2.getInt("cnt") > 0){
				            		SvLine = "";
				            	}
				            }
				            pstmt2.close();
				            rs2.close();
						}
						else if (rs.getString("cm_gubun").equals("1")) {
							if (rs.getString("cm_jobcd").equals("SYSCB")) {
								strQuery.setLength(0);
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								strQuery.append("   and (substr(cm_info,1,1)='1' or substr(cm_info,13,1)='1') \n");

								pstmt2 = conn.prepareStatement(strQuery.toString());
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2.setString(1, SysCd);
					            pstmt2.setString(2, aRsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0){
					            		SvLine = "";
					            	}
					            }
					            pstmt2.close();
					            rs2.close();
							} else if (rs.getString("cm_jobcd").equals("SYSED")) {
								strQuery.setLength(0);
								pstmtcount = 1;
								strQuery.append("select count(*) as cnt from cmm0036                          \n");
								strQuery.append(" where cm_syscd=? and cm_closedt is null                     \n");
								strQuery.append("   and instr(?,cm_rsrccd)>0                                  \n");
								strQuery.append("   and (substr(cm_info,11,1)='1' or substr(cm_info,21,1)='1') \n");

								pstmt2 = conn.prepareStatement(strQuery.toString());
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2.setString(pstmtcount++, SysCd);
					            pstmt2.setString(pstmtcount++,aRsrcCd);
					            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					            rs2 = pstmt2.executeQuery();
					            if (rs2.next()) {
					            	if (rs2.getInt("cnt") == 0) SvLine = "";
					            }
					            pstmt2.close();
					            rs2.close();
							}
						}
				    }
					if (!SvLine.equals("")) {
						if (rs.getString("cm_gubun").equals("1")) {
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
							    	SvTag = "수협" + rs.getString("cm_username");
							    }
							}
							else {
								strQuery.setLength(0);
								pstmtcount = 1;
								strQuery.append("select cm_codename from cmm0020                         \n");
								strQuery.append(" where cm_macode='SYSGBN' and cm_micode=?               \n");

								pstmt2 = conn.prepareStatement(strQuery.toString());
								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
								pstmt2.setString(pstmtcount++, rs.getString("cm_jobcd"));
								////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
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
						}
						else if (rs.getString("cm_gubun").equals("2")) {
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
						}
						else if (rs.getString("cm_gubun").equals("C")) {
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							SvSgnName = "결재(순차)";
							FindSw = true;
						}
						else if (rs.getString("cm_gubun").equals("8")) {
							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;
							SvSgnName = "참조";
							FindSw = true;
						}
						else if (rs.getString("cm_gubun").equals("9")){
							SvUser = rs.getString("cm_jobcd");
							SvBaseUser = rs.getString("cm_jobcd");
							strQuery.setLength(0);
							pstmtcount = 1;
							strQuery.append("select cm_deptname from cmm0100              \n");
							strQuery.append(" where cm_deptcd=?                           \n");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn,strQuery.toString());
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

						}
						else if (rs.getString("cm_gubun").equals("3")){
							SvUser = "";
							SvBaseUser = "";
							SvTag = "";
							SvSgnName = "결재(순차)";

							HashSv = new HashMap<String, String>();
							HashSv.put("SvUser", SvUser);
							HashSv.put("SvTag", SvTag);
							ArySv.add(HashSv);
							HashSv = null;

						}
						else if (rs.getString("cm_gubun").equals("5") ||
								 rs.getString("cm_gubun").equals("6") || rs.getString("cm_gubun").equals("7")) {
							if (ReqCd.substring(0,1).equals("3") && (rs.getString("cm_position").indexOf("78") > -1)) {
				            	strQuery.setLength(0);
				            	pstmtcount = 1;
								strQuery.append("select a.cm_userid,a.cm_username,a.cm_status,a.cm_position,b.cm_codename   \n");
								strQuery.append("  from cmd0304 c, cmm0020 b, cmm0040 a where               \n");
								if (!rs.getString("cm_gubun").equals("5")){
									strQuery.append("a.cm_project=? and                                     \n");
								}
								strQuery.append("    a.cm_active='1'                                        \n");
								strQuery.append("and b.cm_macode='POSITION' and a.cm_position=b.cm_micode   \n");
								strQuery.append("and a.cm_userid = c.cd_prjuser                             \n");
								strQuery.append("and c.cd_prjno=?                                           \n");
								strQuery.append("and c.cd_prjjik='SM' and c.cd_closedt is null              \n");
								strQuery.append("order by a.cm_position desc,a.cm_userid desc               \n");


								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					            pstmt2 = conn.prepareStatement(strQuery.toString());

								if (!rs.getString("cm_gubun").equals("5")){
									pstmt2.setString(pstmtcount++, rs.getString("cm_project"));
								}
					            pstmt2.setString(pstmtcount++, PrjNo);
				            }
							else {
				            	strQuery.setLength(0);
				            	pstmtcount = 1;
								strQuery.append("select a.cm_userid,a.cm_username,a.cm_status,a.cm_position,b.cm_codename   \n");
								strQuery.append("  from cmm0043 c, cmm0020 b, cmm0040 a where               \n");
								if (!rs.getString("cm_gubun").equals("5")){
									strQuery.append("a.cm_project=? and                                     \n");
								}
								strQuery.append("    a.cm_active='1'                                        \n");
								strQuery.append("and b.cm_macode='POSITION' and a.cm_position=b.cm_micode   \n");
								strQuery.append("and a.cm_userid = c.cm_userid                              \n");
								strQuery.append("and instr(?,c.cm_rgtcd)>0                                  \n");
								strQuery.append("order by a.cm_position desc,a.cm_userid desc               \n");


								//pstmt2 = new LoggableStatement(conn,strQuery.toString());
					            pstmt2 = conn.prepareStatement(strQuery.toString());
					            if (!rs.getString("cm_gubun").equals("5")){
					            	pstmt2.setString(pstmtcount++, rs.getString("cm_project"));
					            }
					            pstmt2.setString(pstmtcount++, rs.getString("cm_position"));
				            }

							////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
							rs2 = pstmt2.executeQuery();

			                while (rs2.next()){
			                	SvBaseUser = rs2.getString("cm_userid");
			                	strQuery.setLength(0);
			                	pstmtcount = 1;
								strQuery.append("select cm_daegyul from cmm0040                             \n");
								strQuery.append(" where cm_userid=?                                         \n");
								strQuery.append("   and cm_blankdts is not null                             \n");
								strQuery.append("   and cm_blankdte is not null                             \n");
								strQuery.append("   and cm_blankdts>=to_char(sysdate,'yyyymmdd')            \n");
								strQuery.append("   and cm_blankdte<=to_char(sysdate,'yyyymmdd')            \n");
								//pstmt3 = new LoggableStatement(conn,strQuery.toString());
					            pstmt3 = conn.prepareStatement(strQuery.toString());
					            pstmt3.setString(pstmtcount++, rs2.getString("cm_userid"));
					            ////ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
					            rs3 = pstmt3.executeQuery();
					            if (rs3.next()) {
					            	if (!rs2.getString("cm_userid").equals(rs3.getString("cm_daegyul"))) {
					            		strQuery.setLength(0);
										strQuery.append("select a.cm_username,a.cm_duty,b.cm_codename               \n");
										strQuery.append("  from cmm0040 a,cmm0020 b                                 \n");
										strQuery.append(" where a.cm_userid=?                                       \n");
										strQuery.append("   and b.cm_macode='POSITION'                              \n");
										strQuery.append("   and b.cm_micode=a.cm_position                           \n");
										//pstmt4 = new LoggableStatement(conn,strQuery.toString());
							            pstmt4 = conn.prepareStatement(strQuery.toString());
							            pstmt4.setString(1, rs3.getString("cm_daegyul"));

							            rs4 = pstmt4.executeQuery();
							            if (rs4.next()) {
							            	SvTag = rs4.getString("cm_codename") + " " + rs4.getString("cm_username");
							            	SvUser = rs4.getString("cm_daegyul");
							            }

							            rs4.close();
							            pstmt.close();
					            	}
					            }
					            else {
					            	SvTag = rs2.getString("cm_codename") + " " + rs2.getString("cm_username");
					            	SvUser = rs2.getString("cm_userid");
					            }
					            rs3.close();
					            pstmt3.close();


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
						else {
							strQuery.setLength(0);
							SvUser = rs.getString("cm_position");
							SvBaseUser = rs.getString("cm_position");
							strQuery.append("select cm_codename from cmm0020                    \n");
							strQuery.append(" where cm_macode = 'RGTCD' and instr(?,cm_micode)>0   \n");

							pstmt2 = conn.prepareStatement(strQuery.toString());
							//pstmt2 = new LoggableStatement(conn,strQuery.toString());
							pstmt2.setString(1, rs.getString("cm_position"));
							////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();
				            if (rs2.next()) {
				            	SvTag = rs2.getString("cm_codename");
				            }
				            rs2.close();
				            pstmt2.close();


				            strQuery.setLength(0);
				            strQuery.append("select cm_codename from cmm0020                      \n");
				            strQuery.append(" where cm_macode='SGNCD' and cm_micode=?             \n");

				            pstmt2 = conn.prepareStatement(strQuery.toString());
				            //pstmt2 = new LoggableStatement(conn,strQuery.toString());
				            pstmt2.setString(1, SvLine);
				            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());

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

				            FindSw = true;
						}
					}

					if (FindSw == true && SvLine != "") {
						rst = new HashMap<Object, Object>();
						rst.put("cm_name", rs.getString("cm_name"));
						rst.put("arysv", ArySv);
						rst.put("cm_sgnname", SvSgnName);
						rst.put("cm_baseuser", SvBaseUser);
						rst.put("cm_congbn", SvLine);
						rst.put("cm_emg2", rs.getString("cm_emg2"));
						rst.put("cm_orgstep", rs.getString("cm_orgstep"));
						rst.put("cm_prcsw", rs.getString("cm_prcsw"));
						rst.put("cm_position", rs.getString("cm_position"));
						rst.put("cm_gubun", rs.getString("cm_gubun"));
						rst.put("cm_common", rs.getString("cm_common"));
						rst.put("cm_blank", rs.getString("cm_blank"));
						rst.put("cm_holi", rs.getString("cm_holiday"));
						rst.put("cm_emg", rs.getString("cm_emg"));
						rst.put("cm_duty", rs.getString("cm_jobcd"));
						rst.put("cm_seqno", "0");
						if (rs.getString("cm_gubun").equals("3")){
							rst.put("userSetable", true);
						}
						else{
							rst.put("userSetable", false);
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
			rs2 = null;
			pstmt = null;
			pstmt2 = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval = null;

			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0202.Confirm_Info() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0202.Confirm_Info() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0202.Confirm_Info() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0202.Confirm_Info() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)  returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0202.Confirm_Info() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of Confirm_Info() method statement
	public String Cmr9900Tmp_Ins(ArrayList<HashMap<String,Object>> ConfList,HashMap<String,String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 0;
		int               SeqNo       = 0;
		String			  AcptNo	  = null;
		int				  i;
		ArrayList<HashMap<String,Object>>	rData = null;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
        	strQuery.append("select max(cr_acptno)+ 1 max,                               \n");
        	strQuery.append("       to_char(SYSDATE,'yymmdd') || '000001' acpt      \n");
        	strQuery.append("  from cmr9900_tmp                                       \n");
        	strQuery.append(" where substr(cr_acptno,1,6)=to_char(SYSDATE,'yyyymmdd') \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	rs = pstmt.executeQuery();

        	if (rs.next()) {
        		if (rs.getString("max") != null) {
        			AcptNo = rs.getString("max");
        		} else {
        			AcptNo = rs.getString("acpt");
        		}
        	}

        	rs.close();
        	pstmt.close();


	        for (i=0;i<ConfList.size();i++){
	        	if (ConfList.get(i).get("cm_congbn").equals("1") || ConfList.get(i).get("cm_congbn").equals("2") ||
	        		ConfList.get(i).get("cm_congbn").equals("3") ||	ConfList.get(i).get("cm_congbn").equals("4") ||
	        		ConfList.get(i).get("cm_congbn").equals("5") || ConfList.get(i).get("cm_congbn").equals("6")) {
		        	strQuery.setLength(0);

		        	strQuery.append("insert into cmr9900_tmp                                           \n");
		        	strQuery.append("      (CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_CONFNAME,CR_TEAM,CR_TEAMCD, \n");
		        	strQuery.append("       CR_STATUS,CR_CONGBN,CR_COMMON, CR_BLANK,CR_EMGER,CR_HOLI,  \n");
		        	strQuery.append("       CR_SGNGBN,CR_ORGSTEP,CR_BASEUSR,CR_PRCSW)                  \n");
		        	strQuery.append("values (                                                          \n");
		        	strQuery.append("?, 1, lpad(?,2,'0'), ?, ?, ?, '0', ?, ?, ?, ?, ?, ?, ?, ?, ? )             \n");

		        	pstmt = conn.prepareStatement(strQuery.toString());
		        	//pstmt = new LoggableStatement(conn,strQuery.toString());

		        	pstmtcount = 0;

	        	    pstmt.setString(++pstmtcount, AcptNo);
	        	    pstmt.setInt(++pstmtcount, ++SeqNo);
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_name"));
	        	    rData = (ArrayList<HashMap<String, Object>>) ConfList.get(i).get("arysv");
					pstmt.setString(++pstmtcount, (String) rData.get(0).get("SvUser"));

	        	    if (ConfList.get(i).get("cm_gubun").equals("C")){
	        	    	pstmt.setString(++pstmtcount,"3");
	        	    }
	        	    else if (ConfList.get(i).get("cm_gubun").equals("R")){
	        	    	pstmt.setString(++pstmtcount,"8");
	        	    }
	        	    else{
	        	    	pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_gubun"));
	        	    }

	        	    rData = null;

	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_congbn"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_common"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_blank"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_emg"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_holi"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_duty"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_orgstep"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_baseuser"));
	        	    pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_prcsw"));


	        	    ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        	pstmt.executeUpdate();
		        	pstmt.close();

		        	if (SeqNo == 1) {
			        	strQuery.setLength(0);

			        	strQuery.append("insert into cmr9900_tmp ");
			        	strQuery.append("(CR_ACPTNO,CR_SEQNO,CR_LOCAT,CR_STATUS,CR_CONFUSR) ");
			        	strQuery.append("values ( ");
			        	strQuery.append("?, '1', '00', '0', '9999' ) ");

			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	pstmtcount = 0;
			        	pstmt.setString(++pstmtcount, AcptNo);
			        	pstmt.executeUpdate();
			        	pstmt.close();
		        	}

		        }
        	}
	        conn.commit();
	        conn.close();

	        rs = null;
	        pstmt = null;
	        conn = null;
	        return AcptNo;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() SQLException END ##");
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
					ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0202.Cmr9900Tmp_Ins() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}
}//end of Cmr0202 class statement
