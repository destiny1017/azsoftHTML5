/*****************************************************************************************
	1. program ID	: Cmm0400.java
	2. create date	: 2008.12. 03
	3. auth		    : NO name
	4. update date	:
	5. auth		    :
	6. description	: [관리자] -> 사용자정보
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//import app.common.LoggableStatement;


public class Cmm0400{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * 사용자정보를 조회합니다.
	 * @param  UserId,UserName
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserInfo(String UserId,String UserName) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_userid,cm_username,to_char(cm_logindt,'yyyy-mm-dd hh24:mi') cm_logindt,cm_ercount,");
			strQuery.append("cm_admin,cm_manid,cm_status,cm_project,cm_position,cm_duty,cm_ipaddress,cm_active,cm_telno1,");
			strQuery.append("cm_telno2,cm_project2,cm_handrun,cm_dumypw,cm_juminnum,cm_blankdts,cm_blankdte,cm_daegyul,to_char(sysdate,'yyyymmdd') as sysdt, ");
			strQuery.append("cm_daegmsg,cm_daesayu,cm_email from cmm0040 where ");
			String tmpStr = "";
			if (UserId != "" && UserId != null){
				tmpStr = UserId;
				strQuery.append("cm_userid = ? ");
			}else{
				tmpStr = UserName;
				strQuery.append("cm_username = ? ");
			}
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,tmpStr);
            rs = pstmt.executeQuery();

            boolean userYN = true;
            while(rs.next()){
            	userYN = false;
				rst = new HashMap<String, String>();
				rst.put("ID", "");
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_logindt",rs.getString("cm_logindt"));
				rst.put("cm_ercount",rs.getString("cm_ercount"));
				rst.put("cm_admin",rs.getString("cm_admin"));
				rst.put("cm_manid",rs.getString("cm_manid"));
				rst.put("cm_project",rs.getString("cm_project"));
				if (rs.getString("cm_project") != null && rs.getString("cm_project") != ""){
					strQuery.setLength(0);
					strQuery.append("select cm_deptname from cmm0100 where \n");
					strQuery.append("cm_deptcd=? \n");//cm_project
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cm_project"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("deptname1", rs2.getString("cm_deptname"));
					}else{
						rst.put("deptname1", "");
					}
	                rs2.close();
	                pstmt2.close();
				}

				rst.put("cm_position",rs.getString("cm_position"));
				rst.put("cm_duty",rs.getString("cm_duty"));
				rst.put("cm_ipaddress", rs.getString("cm_ipaddress"));
				rst.put("cm_active", rs.getString("cm_active"));
				rst.put("cm_telno1",rs.getString("cm_telno1"));
				rst.put("cm_telno2",rs.getString("cm_telno2"));
				rst.put("cm_email",rs.getString("cm_email"));
				rst.put("cm_project2", rs.getString("cm_project2"));
				if (rs.getString("cm_project2") != null && rs.getString("cm_project2") != ""){
					strQuery.setLength(0);
					strQuery.append("select cm_deptname from cmm0100 where \n");
					strQuery.append("cm_deptcd=? \n");//cm_project2
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cm_project2"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("deptname2", rs2.getString("cm_deptname"));
					}else{
						rst.put("deptname2", "");
					}
	                rs2.close();
	                pstmt2.close();
				}
				rst.put("cm_handrun",rs.getString("cm_handrun"));
				rst.put("cm_dumypw",rs.getString("cm_dumypw"));
				rst.put("cm_juminnum", rs.getString("cm_juminnum"));

				//if (rs.getString("cm_blankdts") != null && rs.getString("cm_blankdts") != "" && rs.getString("cm_status").equals("9")){
				if (rs.getString("cm_blankdts") != null && rs.getString("cm_blankdts") != "" ){
		            Calendar cDate = Calendar.getInstance(); // Calendar 객체선언
		            String sDate = rs.getString("sysdt");
		            int iDay = 0;
		            int iYyyy = Integer.parseInt(sDate.substring(0, 4)); // 년 YYYY
		            int iMm = Integer.parseInt(sDate.substring(4, 6)) - 1; // 월은 0~11
		            int iDd = Integer.parseInt(sDate.substring(6, 8)); // 일 1 ~ 31
		            cDate.set(iYyyy, iMm, iDd); // 입력된 날짜 set
		            cDate.add(Calendar.DATE, iDay); // 객체 cDate 에 날짜더함 set
		            iYyyy = cDate.get(Calendar.YEAR);
		            iMm = cDate.get(Calendar.MONTH);
		            iDd = cDate.get(Calendar.DATE); // Calendar 객체 cDate 에서 년월일을 차례로 받는다.
		            // 날짜를 String 객체로 변환 "YYYYMMDD"
		            if (iMm<9 && iDd<10) sDate = String.valueOf(iYyyy)+"0"+String.valueOf(iMm+1)+"0"+String.valueOf(iDd);
		            else if (iMm<9 && iDd>=10) sDate = String.valueOf(iYyyy)+"0"+String.valueOf(iMm+1)+String.valueOf(iDd);
		            else if (iMm>=9 && iDd<10) sDate = String.valueOf(iYyyy)+String.valueOf(iMm+1)+"0"+String.valueOf(iDd);
		            else sDate = String.valueOf(iYyyy)+String.valueOf(iMm+1)+String.valueOf(iDd);

					if (Integer.parseInt(rs.getString("cm_blankdte")) >= Integer.parseInt(sDate) && Integer.parseInt(rs.getString("cm_blankdts")) <= Integer.parseInt(sDate)){
						if (rs.getString("cm_daegyul") != "" && rs.getString("cm_daegyul") != null){
							strQuery.setLength(0);
							strQuery.append("select cm_username from cmm0040 where \n");
							strQuery.append("rtrim(cm_userid)=? \n");//DbSet!cm_daegyul
							pstmt2 = conn.prepareStatement(strQuery.toString());
							pstmt2.setString(1, rs.getString("cm_daegyul"));
							rs2 = pstmt2.executeQuery();
							String Txt_DaeGyul = "";
							if (rs2.next()){
								Txt_DaeGyul = rs2.getString("cm_username");
							}
			                rst.put("Txt_DaeGyul", Txt_DaeGyul + " [" + rs.getString("cm_daegyul") + "]");
			                rs2.close();
			                pstmt2.close();
						}
						rst.put("Txt_BlankTerm", rs.getString("cm_blankdts") + " ~ " + rs.getString("cm_blankdte"));

						String Txt_BlankSayu = "";
			            if (rs.getString("cm_daegmsg") != "" && rs.getString("cm_daegmsg") != null){
			            	strQuery.setLength(0);
			            	strQuery.append("select cm_codename from cmm0020 \n");
			            	strQuery.append(" where cm_macode='DAEGYUL'      \n");
			            	strQuery.append("   and cm_micode=?              \n");
			            	pstmt2 = conn.prepareStatement(strQuery.toString());
			            	pstmt2.setString(1, rs.getString("cm_daegmsg"));
			            	rs2 = pstmt2.executeQuery();
			            	if (rs2.next()){
			            		Txt_BlankSayu = rs2.getString("cm_codename");
			            	}
			                rs2.close();
			                pstmt2.close();
			            }
			            if (rs.getString("cm_daesayu") != "" && rs.getString("cm_daesayu") != null) {
		            		if (Txt_BlankSayu.length() > 0) Txt_BlankSayu = Txt_BlankSayu + "\n";
		            		Txt_BlankSayu = Txt_BlankSayu + "내용 : " + rs.getString("cm_daesayu");
			            }
			            rst.put("Txt_BlankSayu", Txt_BlankSayu);
					}
				}
				rsval.add(rst);
				rst = null;
            }
            if (userYN){
				rst = new HashMap<String, String>();
				rst.put("ID", "ERROR");
				rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자의 권한를 조회합니다.
	 * @param  UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserRGTCD(String UserId) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select b.cm_macode,b.cm_micode,b.cm_codename from cmm0043 a,cmm0020 b ");
			strQuery.append("where a.cm_userid =? and a.cm_rgtcd=b.cm_micode ");
			strQuery.append("  and b.cm_macode='RGTCD' and b.cm_closedt is null order by cm_rgtcd ");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,UserId);
            rs = pstmt.executeQuery();

            String RGTCD = "";
            while(rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cm_macode", rs.getString("cm_macode"));
            	rst.put("cm_micode", rs.getString("cm_micode"));
            	rst.put("cm_codename", rs.getString("cm_codename"));
            	rst.put("checkbox", "true");
				rsval.add(rst);
				rst = null;

				if (RGTCD == "")
					RGTCD = rs.getString("cm_micode");
				else RGTCD = RGTCD + "," + rs.getString("cm_micode");
            }
            rs.close();
            pstmt.close();

            strQuery.setLength(0);
			if (RGTCD != ""){
				String[] micode = RGTCD.split(",");
				strQuery.append("select cm_macode,cm_micode,cm_codename from cmm0020 ");
				strQuery.append("where cm_macode='RGTCD' and cm_micode <> '****' ");
				strQuery.append("  and cm_closedt is null ");
				strQuery.append("  and cm_micode not in ( ");
				if (micode.length == 1)
					strQuery.append(" ? ");
				else{
					for (int i=0;i<micode.length;i++){
						if (i == micode.length-1)
							strQuery.append(" ? ");
						else
							strQuery.append(" ? ,");
					}
				}
				strQuery.append(" ) ");
				strQuery.append("order by cm_micode ");
	            pstmt = conn.prepareStatement(strQuery.toString());
				for (int i=0 ; i<micode.length ; i++){
					pstmt.setString(i+1, micode[i]);
				}
			}else{
				strQuery.append("select cm_macode,cm_micode,cm_codename from cmm0020 ");
				strQuery.append("where cm_macode='RGTCD' and cm_micode <> '****' ");
				strQuery.append("  and cm_closedt is null ");
				strQuery.append("order by cm_micode ");
				pstmt = conn.prepareStatement(strQuery.toString());
			}
			rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_macode", rs.getString("cm_macode"));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("checkbox", "");
				rsval.add(rst);
				rst = null;
			}

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRGTCD() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserRGTCD() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRGTCD() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserRGTCD() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserRGTCD() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자의 담당업무를 조회합니다.
	 * @param  UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserJobList(String gbnCd,String UserId) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_jobcd,d.cm_jobname job,  \n");
			strQuery.append("       e.cm_sysmsg jobgrp,e.cm_sysgb,\n");
			strQuery.append("       e.cm_syscd,c.cm_userid,c.cm_username \n");
			strQuery.append("from cmm0102 d,cmm0030 e,cmm0044 a,cmm0040 c ");
			if (gbnCd.equals("USER")) {
				strQuery.append("where a.cm_userid=?           \n");
				strQuery.append("  and a.cm_userid=c.cm_userid \n");
			} else {
				strQuery.append("where c.cm_project=?          \n");
				strQuery.append("  and a.cm_userid=c.cm_userid \n");
			}
			strQuery.append("and a.cm_closedt is null          \n");
			strQuery.append("and a.cm_syscd=e.cm_syscd         \n");;
			strQuery.append("and a.cm_jobcd=d.cm_jobcd         \n");
			strQuery.append("and c.cm_active='1'               \n");
			strQuery.append("order by e.cm_sysmsg,d.cm_jobcd   \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1,UserId);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            while(rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cm_jobcd", rs.getString("cm_jobcd"));
            	rst.put("job", rs.getString("job") + "(" + rs.getString("cm_jobcd") + ")");
            	rst.put("jobgrp", rs.getString("jobgrp"));
            	rst.put("cm_sysgb", rs.getString("cm_sysgb"));
                rst.put("cm_syscd", rs.getString("cm_syscd"));
            	rst.put("cm_userid", rs.getString("cm_userid"));
                rst.put("cm_username", rs.getString("cm_username"));
				rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserJobList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserJobList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserJobList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserJobList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserJobList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    /**
	 * 사용자의 권한를 조회합니다.
	 * @param  UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserRgtDept(String UserId) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmm0047            \n");
			strQuery.append(" where cm_userid=?        \n");
			strQuery.append("   and cm_rgtcd not in    \n");
			strQuery.append("       (select cm_rgtcd from cmm0043 \n");
			strQuery.append("         where cm_userid=?) \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,UserId);
            pstmt.setString(2,UserId);
            pstmt.executeUpdate();
            pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select a.cm_rgtcd,a.cm_deptcd      \n");
			strQuery.append("  from cmm0047 a,cmm0043 b         \n");
			strQuery.append(" where b.cm_userid=?               \n");
			strQuery.append("   and b.cm_userid=a.cm_userid     \n");
			strQuery.append("   and b.cm_rgtcd=a.cm_rgtcd       \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,UserId);
            rs = pstmt.executeQuery();

            String deptCd = "";
            String svRgtCd = "";
            while(rs.next()){
            	if (svRgtCd.length() == 0 || !svRgtCd.equals(rs.getString("cm_rgtcd"))) {
            		if (svRgtCd.length() > 0) {
            			rsval.add(rst);
            		}
            		rst = new HashMap<String, String>();
                	rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
                	svRgtCd = rs.getString("cm_rgtcd");
                	deptCd = "";
            	} else {
            		deptCd = deptCd + ",";
            	}
                deptCd = deptCd + rs.getString("cm_deptcd");
                rst.put("cm_deptcd", deptCd);
            }
            rs.close();
            pstmt.close();

            if (svRgtCd.length()>0) rsval.add(rst);
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRgtDept() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserRgtDept() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRgtDept() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserRgtDept() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserRgtDept() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
    /**
	 * 사용자의 권한를 조회합니다.
	 * @param  UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserRgtDept_All() throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_userid,b.cm_username,a.cm_rgtcd,a.cm_deptcd,c.cm_deptname,d.cm_codename \n");
			strQuery.append("  from cmm0047 a,cmm0040 b,cmm0100 c,cmm0020 d \n");
			strQuery.append(" where b.cm_active='1'             \n");
			strQuery.append("   and b.cm_userid=a.cm_userid     \n");
			strQuery.append("   and a.cm_deptcd=c.cm_deptcd     \n");
			strQuery.append("   and d.cm_macode='RGTCD'         \n");
			strQuery.append("   and d.cm_micode=a.cm_rgtcd      \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

            while(rs.next()){
        		rst = new HashMap<String, String>();
            	rst.put("cm_userid", rs.getString("cm_userid"));
            	rst.put("cm_username", rs.getString("cm_username"));
            	rst.put("cm_rgtcd", rs.getString("cm_rgtcd"));
                rst.put("cm_deptcd", rs.getString("cm_deptcd"));
                rst.put("cm_deptname", rs.getString("cm_deptname"));
                rst.put("cm_codename", rs.getString("cm_codename"));
                rsval.add(rst);
            }
            rs.close();
            pstmt.close();

            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRgtDept_All() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserRgtDept_All() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserRgtDept_All() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserRgtDept_All() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserRgtDept_All() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
	/**
	 * 사용자정보를 저장 및 등록 합니다.
	 * @param  HashMap, ArrayList<HashMap>,ArrayList<HashMap>
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
    public String setUserInfo(HashMap<String,String> dataObj,ArrayList<HashMap<String,String>> DutyList,
    		ArrayList<HashMap<String,String>> JobList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String UserId = dataObj.get("Txt_UserId");
			String UserName = dataObj.get("Txt_UserName");
			int ercount = 0;
			if (dataObj.get("Txt_ercount") != null && dataObj.get("Txt_ercount") != ""){
				ercount = Integer.parseInt(dataObj.get("Txt_ercount"));
			}

			String admin = "0";
		   	if (dataObj.get("Chk_ManId2").equals("true")){
		   		admin = "1";
		   	}
		   	String manid = "N";
		   	if (dataObj.get("Chk_ManId0").equals("true")){
		   		manid = "Y";
		   	}
		   	String active = "0";
		   	if (dataObj.get("active1").equals("true")){
		   		active = "1";
		   	}
		   	String Txt_Ip = "";
        	if (dataObj.get("Txt_Ip") != null && dataObj.get("Txt_Ip") != ""){
        		Txt_Ip = dataObj.get("Txt_Ip");
        	}
        	String Txt_TelNo1 = "";
        	if (dataObj.get("Txt_TelNo1") != null && dataObj.get("Txt_TelNo1") != ""){
        		Txt_TelNo1 = dataObj.get("Txt_TelNo1");
        	}
        	String Txt_TelNo2 = "";
        	if (dataObj.get("Txt_TelNo2") != null && dataObj.get("Txt_TelNo2") != ""){
        		Txt_TelNo2 = dataObj.get("Txt_TelNo2");
        	}
        	String Lbl_Org11 = "";
        	if (dataObj.get("Lbl_Org11") != null && dataObj.get("Lbl_Org11") != ""){
        		Lbl_Org11 = dataObj.get("Lbl_Org11");
        	}
		   	String handrun = "N";
	        if (dataObj.get("Chk_HandYn").equals("true")){
	        	handrun = "Y";
	        }
	        String Txt_email = "";
        	if (dataObj.get("Txt_email") != null && dataObj.get("Txt_email") != ""){
        		Txt_email = dataObj.get("Txt_email");
        	}
			strQuery.setLength(0);
		    strQuery.append("select cm_userid from cmm0040 \n");
		    strQuery.append("where rtrim(cm_userid)=? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, UserId);
            rs = pstmt.executeQuery();

            strQuery.setLength(0);
            int paramIndex = 0;
            if(rs.next()){
            	strQuery.append("update cmm0040 set ");
            	strQuery.append("cm_username=?, ");//UserName
			   	strQuery.append("cm_changedt=SYSDATE, ");
			   	strQuery.append("cm_ercount=?, ");//ercount
			   	strQuery.append("cm_admin=?, ");//admin
			   	strQuery.append("cm_manid=?, ");//manid
			   	strQuery.append("cm_project=?, ");//Lbl_Org00
			   	strQuery.append("cm_position=?, ");//Cbo_Pos
			   	strQuery.append("cm_deptseq='', ");
			   	strQuery.append("cm_duty=?, ");//Cbo_Duty
			   	strQuery.append("cm_ipaddress=?,");//Txt_Ip
			   	strQuery.append("cm_active=?, ");//active
			   	strQuery.append("cm_telno1=?,");//Txt_TelNo1
			   	strQuery.append("cm_telno2=?,");//Txt_TelNo2
    	        strQuery.append("cm_project2=?, ");//Lbl_Org11
    	        strQuery.append("cm_deptseq2='', ");
    	        strQuery.append("cm_handrun=?, ");//handrun
    	        strQuery.append("cm_clsdate='', ");
    	        strQuery.append("cm_email=? ");
    	        strQuery.append("where cm_userid=? ");//UserId
            }else{
 		       	strQuery.append("insert into cmm0040 (cm_username,cm_ercount,cm_changedt,cm_logindt,cm_admin,");
 		       	strQuery.append("cm_manid,cm_status,cm_project,cm_position,cm_duty,cm_ipaddress,cm_active,");
 		        strQuery.append("cm_telno1,cm_telno2,cm_dumypw,cm_juminnum,cm_project2,cm_handrun,cm_email,cm_userid) ");
 		        strQuery.append("values (?,?,SYSDATE,SYSDATE,?,?,'0',?,?,?,?,?,?,?,'1234','1234',?,?,?,?)");
            }
            //pstmt2 =  new LoggableStatement(conn, strQuery.toString());
        	pstmt2 = conn.prepareStatement(strQuery.toString());
        	pstmt2.setString(++paramIndex, UserName);
        	pstmt2.setInt(++paramIndex, ercount);
        	pstmt2.setString(++paramIndex, admin);
        	pstmt2.setString(++paramIndex, manid);
        	pstmt2.setString(++paramIndex, dataObj.get("Lbl_Org00"));
        	pstmt2.setString(++paramIndex, dataObj.get("Cbo_Pos"));
        	pstmt2.setString(++paramIndex, dataObj.get("Cbo_Duty"));
        	pstmt2.setString(++paramIndex, Txt_Ip);
        	pstmt2.setString(++paramIndex, active);
        	pstmt2.setString(++paramIndex, Txt_TelNo1);
        	pstmt2.setString(++paramIndex, Txt_TelNo2);
        	pstmt2.setString(++paramIndex, Lbl_Org11);
        	pstmt2.setString(++paramIndex, handrun);
        	pstmt2.setString(++paramIndex, Txt_email);
        	pstmt2.setString(++paramIndex, UserId);
        	////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
        	pstmt2.executeUpdate();
        	pstmt2.close();
        	rs.close();
        	pstmt.close();

        	//if (dataObj.get("Cbo_Pos").equals("580002302")) partUser = true;

        	strQuery.setLength(0);
        	strQuery.append("delete cmm0043 where cm_userid=? ");//Txt_UserId
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, UserId);
        	pstmt.executeUpdate();
        	pstmt.close();


        	for(int i=0 ; i<DutyList.size() ; i++){
        		//if (DutyList.get(i).get("cm_micode").equals("52")) partUser = true;
        		strQuery.setLength(0);
                strQuery.append("insert into cmm0043 (cm_userid,cm_rgtcd,cm_creatdt,cm_lastdt) values (");
                strQuery.append("?,?,SYSDATE, SYSDATE) ");//Txt_UserId  Lst_Duty
                pstmt = conn.prepareStatement(strQuery.toString());
                pstmt.setString(1, UserId);
                pstmt.setString(2, DutyList.get(i).get("cm_micode"));
                pstmt.executeUpdate();
                pstmt.close();
        	}

        	if (JobList != null){
        		String SysCd = dataObj.get("cm_syscd");
	            for (int i=0 ; i<JobList.size() ; i++){
            		strQuery.setLength(0);
            		strQuery.append("select cm_jobcd from cmm0044 where ");
            		strQuery.append("rtrim(cm_userid)=? and ");//Txt_UserId
            		strQuery.append("cm_syscd=? and ");//Cbo_SysCd
            		strQuery.append("rtrim(cm_jobcd)=? ");//jobcd
            		pstmt = conn.prepareStatement(strQuery.toString());
            		paramIndex = 0;
            		pstmt.setString(++paramIndex, UserId);
            		pstmt.setString(++paramIndex, SysCd);
            		pstmt.setString(++paramIndex, JobList.get(i).get("cm_jobcd"));
            		rs = pstmt.executeQuery();

            		strQuery.setLength(0);
            		if (rs.next()){
            			strQuery.append("update cmm0044 set cm_closedt='' ");
            			strQuery.append("where rtrim(cm_userid)=? and ");//Txt_UserId
            			strQuery.append("cm_syscd=? and ");//
            			strQuery.append("rtrim(cm_jobcd)=? ");//Lst_Job
            		}else{
            			strQuery.append("insert into cmm0044 (cm_userid,cm_syscd,cm_jobcd,cm_creatdt) values (");
            			strQuery.append("?,?,?,SYSDATE) ");//Txt_UserId Cbo_SysCd Lst_Job
            		}
            		pstmt2 = conn.prepareStatement(strQuery.toString());
            		paramIndex = 0;
            		pstmt2.setString(++paramIndex, UserId);
            		pstmt2.setString(++paramIndex, SysCd);
            		pstmt2.setString(++paramIndex, JobList.get(i).get("cm_jobcd"));
            		pstmt2.executeUpdate();

            		pstmt2.close();
            		rs.close();
            		pstmt.close();
	            }
        	}

        	strQuery.setLength(0);
			strQuery.append("delete cmm0047            \n");
			strQuery.append(" where cm_userid=?        \n");
			strQuery.append("   and cm_rgtcd not in    \n");
			strQuery.append("       (select cm_rgtcd from cmm0043 \n");
			strQuery.append("         where cm_userid=?) \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,UserId);
            pstmt.setString(2,UserId);
            pstmt.executeUpdate();

		    conn.commit();
		    conn.setAutoCommit(true);
            conn.close();
            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return "["+UserId+"]"+UserName;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.setUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.setUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.setUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.setUserInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.setUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자정보를 폐기합니다.
	 * @param  String UserId
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
    public String delUserInfo(String UserId) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("update cmm0040 set cm_active='9', cm_clsdate=SYSDATE \n");
			strQuery.append("where cm_userid=? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, UserId);
        	pstmt.executeUpdate();
        	pstmt.close();

            strQuery.setLength(0);
	        strQuery.append("update cmm0044 set cm_closedt=SYSDATE where cm_userid=? \n");
            pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, UserId);
        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("delete cmm0043 where cm_userid=? ");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, UserId);
        	pstmt.executeUpdate();
        	pstmt.close();

		    conn.commit();
		    conn.setAutoCommit(true);
            conn.close();
            strQuery = null;
            pstmt = null;
            conn = null;

			return UserId;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.delUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.delUserInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.delUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }


	/**
	 * 사용자정보의 업무를 폐기합니다.
	 * @param  String, ArrayList<HashMap>
	 * @return INT
	 * @throws SQLException
	 * @throws Exception
	 */
    public int delUserJob(String UserId,ArrayList<HashMap<String,String>> JobList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String _UserId = "";
			_UserId = UserId;

			for (int i=0 ; i<JobList.size() ; i++){
				if (_UserId == ""){
					_UserId = JobList.get(i).get("cm_userid");
				}
	        	strQuery.setLength(0);
	        	strQuery.append("delete cmm0044 ");
	        	strQuery.append("where cm_userid=? ");
	        	strQuery.append("  and cm_syscd=? ");
	        	strQuery.append("  and cm_jobcd=? ");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, _UserId);
	        	pstmt.setString(2, JobList.get(i).get("cm_syscd"));
	        	pstmt.setString(3, JobList.get(i).get("cm_jobcd"));
	        	pstmt.executeUpdate();
	        	pstmt.close();
			}

		    conn.commit();
		    conn.setAutoCommit(true);
            conn.close();
            strQuery = null;
            pstmt = null;
            conn = null;

            return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.delUserJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.delUserJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.delUserJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자의 담당업무를 조회합니다.
	 * @param  UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getTeamList() throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_deptcd,a.cm_deptname from cmm0100 a,cmm0040 b ");
			strQuery.append("where a.cm_useyn='Y' ");
			strQuery.append("  and a.cm_deptcd=b.cm_project ");
			strQuery.append("group by a.cm_deptcd,a.cm_deptname ");
			strQuery.append("order by a.cm_deptname ");

            pstmt = conn.prepareStatement(strQuery.toString());
            rs = pstmt.executeQuery();

			rst = new HashMap<String, String>();
			rst.put("cm_deptcd", "00");
			rst.put("cm_deptname", "전체");
			rsval.add(rst);
			rst = null;

            while(rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cm_deptcd", rs.getString("cm_deptcd"));
                rst.put("cm_deptname", rs.getString("cm_deptname"));
				rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getTeamList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getTeamList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getTeamList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getTeamList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getTeamList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자정보를 전체 또는 팀별 조회합니다.
	 * @param  String ,INT
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getAllUserInfo(String Cbo_Team,int Option) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
    	PreparedStatement	pstmt2		= null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			/*
			strQuery.append("select cm_userid,cm_username,to_char(cm_logindt,'yyyy-mm-dd hh24:mi') ");
			strQuery.append("cm_logindt,cm_project,cm_position,");
			strQuery.append("cm_duty,cm_ipaddress,cm_telno1,cm_telno2 from cmm0040 ");
			*/
			strQuery.append("select a.cm_userid,a.cm_username,to_char(a.cm_logindt,'yyyy-mm-dd hh24:mi') ");
			strQuery.append("      cm_logindt,a.cm_ipaddress,a.cm_telno1,a.cm_telno2, ");
			strQuery.append("      b.cm_codename position,c.cm_codename duty,d.cm_deptname deptname ");
			strQuery.append(" from cmm0040 a, cmm0020 b, cmm0020 c, cmm0100 d  ");
			strQuery.append("where a.cm_position=b.cm_micode and b.cm_macode='POSITION' and b.cm_micode<>'****' and b.cm_closedt is null ");
			strQuery.append("  and a.cm_duty=c.cm_micode and c.cm_macode='DUTY' and c.cm_micode<>'****' and c.cm_closedt is null ");
			strQuery.append("  and a.cm_project=d.cm_deptcd ");

	    	if (Option == 1){//폐기사용자제외 조회
	    		strQuery.append("and a.cm_active='1' ");
	    	}else if (Option == 2) {//폐기사용자만 조회
	        	strQuery.append("and a.cm_active<>'1' ");
		    }else {//전체조회
		    }
		    if (!Cbo_Team.equals("00")){
		    	strQuery.append("and a.cm_project=? ");//Cbo_Team
		    }
		    strQuery.append("order by a.cm_userid ");

            pstmt = conn.prepareStatement(strQuery.toString());
            if (!Cbo_Team.equals("00")){
            	pstmt.setString(1,Cbo_Team);
            }
            rs = pstmt.executeQuery();

            while(rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_logindt",rs.getString("cm_logindt"));
				rst.put("cm_ipaddress", rs.getString("cm_ipaddress"));
				rst.put("cm_telno1",rs.getString("cm_telno1"));
				rst.put("cm_telno2",rs.getString("cm_telno2"));

				rst.put("position", rs.getString("position"));
				rst.put("duty", rs.getString("duty"));
				rst.put("deptname", rs.getString("deptname"));

				strQuery.setLength(0);
				strQuery.append("select a.cm_codename from cmm0020 a,cmm0043 b ");
				strQuery.append("where b.cm_userid=? ");//userid
				strQuery.append("  and a.cm_macode='RGTCD' and a.cm_micode=b.cm_rgtcd ");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("cm_userid"));
				rs2 = pstmt2.executeQuery();
				String rgtname = "";
				while (rs2.next()){
					if (rgtname != ""){
						rgtname = rgtname + ", ";
					}
					rgtname = rgtname + rs2.getString("cm_codename");
				}
				rst.put("rgtname", rgtname);
                rs2.close();
                pstmt2.close();

				rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();
            rs2 = null;
            pstmt2 = null;
            rs = null;
            pstmt = null;
            conn = null;
            strQuery = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getAllUserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getAllUserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getAllUserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getAllUserInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getAllUserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자명 또는 팀명으로 사용자를 조회합니다.
	 * @param  int, String
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getUserList(int selectedIndex,String UserName) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
		    if (selectedIndex == 0){
		    	strQuery.append("select cm_userid code,cm_username name from cmm0040 where ");
		        strQuery.append("cm_username like ? and ");//'" & Txt_UserName & "%'
		        strQuery.append("cm_active='1' and cm_clsdate is null ");
		        strQuery.append("order by cm_userid ");
		    }else{
		    	strQuery.append("select cm_deptcd code,cm_deptname name from cmm0100 where ");
		        strQuery.append("cm_deptname like ? and ");//'%" & Txt_UserName & "%'
		        strQuery.append("cm_useyn='Y' ");
		        strQuery.append("order by cm_deptname ");
		    }

            pstmt = conn.prepareStatement(strQuery.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            pstmt.setString(1, "%"+UserName+"%");
            rs = pstmt.executeQuery();

            if (rs.next()){
	            rs.last();
	            if (rs.getRow()>1) {
					rst = new HashMap<String, String>();
					rst.put("ID", "");
					rst.put("code", "00");
					rst.put("labelField", "선택하세요");
					rsval.add(rst);
					rst = null;
	            }
				rs.first();
	        	rst = new HashMap<String, String>();
	        	rst.put("code", rs.getString("code"));
	            if (selectedIndex == 0){
	            	rst.put("ID", "USER");
	            	rst.put("labelField", "["+rs.getString("code")+"]  "+ rs.getString("name"));
	            }else{
	            	rst.put("ID", "TEAM");
	            	rst.put("labelField", rs.getString("name"));
	            }
				rsval.add(rst);
				rst = null;

	            while(rs.next()){
	            	rst = new HashMap<String, String>();
	            	rst.put("code", rs.getString("code"));
		            if (selectedIndex == 0){
		            	rst.put("ID", "USER");
		            	rst.put("labelField", "["+rs.getString("code")+"]  "+ rs.getString("name"));
		            }else{
		            	rst.put("ID", "TEAM");
		            	rst.put("labelField", rs.getString("name"));
		            }
					rsval.add(rst);
					rst = null;
	            }
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getUserList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getUserList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getUserList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getUserList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 팀에 포함된 사용자를 조회합니다.
	 * @param  String
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public Object[] getTeamUserList(String Cbo_Sign) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cm_userid,cm_username from cmm0040 ");
			strQuery.append("where cm_project=? ");//Cbo_Sign
			strQuery.append("  and cm_active='1'  and cm_clsdate is null ");
			strQuery.append("order by cm_username ");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, Cbo_Sign);
            rs = pstmt.executeQuery();

            while(rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("cm_userid", rs.getString("cm_userid"));
            	rst.put("cm_username", rs.getString("cm_username"));
	            rst.put("labelField", "["+rs.getString("cm_userid")+"]  "+ rs.getString("cm_username"));
				rsval.add(rst);
				rst = null;
            }

		    rs.close();
            pstmt.close();
            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            conn = null;

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getTeamUserList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getTeamUserList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getTeamUserList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getTeamUserList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getTeamUserList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	/**
	 * 사용자에 대한 업무권한을 일괄등록 합니다.
	 * @param  String, ArrayList<HashMap>,ArrayList<HashMap>
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
    public String setAllUserJob(String SysCd,ArrayList<HashMap<String,String>> UserList,
    		ArrayList<HashMap<String,String>> JobList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			int paramIndex = 0;
        	for(int j=0 ; j<UserList.size() ; j++){
	            for (int i=0 ; i<JobList.size() ; i++){
            		strQuery.setLength(0);
            		strQuery.append("delete cmm0044 ");
            		strQuery.append("where cm_syscd=? and cm_jobcd=? and cm_userid=? ");
            		pstmt = conn.prepareStatement(strQuery.toString());
            		paramIndex = 0;
            		pstmt.setString(++paramIndex, SysCd);
            		pstmt.setString(++paramIndex, JobList.get(i).get("cm_jobcd"));
            		pstmt.setString(++paramIndex, UserList.get(j).get("code"));
            		pstmt.executeUpdate();
            		pstmt.close();

            		strQuery.setLength(0);
        			strQuery.append("insert into cmm0044 (cm_userid,cm_syscd,cm_jobcd,cm_creatdt) values (");
        			strQuery.append("?,?,?,SYSDATE) ");//UserId Cbo_SysCd Lst_Job
        			pstmt = conn.prepareStatement(strQuery.toString());
            		paramIndex = 0;
            		pstmt.setString(++paramIndex, UserList.get(j).get("code"));
            		pstmt.setString(++paramIndex, SysCd);
            		pstmt.setString(++paramIndex, JobList.get(i).get("cm_jobcd"));
            		pstmt.executeUpdate();
            		pstmt.close();
	            }
        	}

		    conn.commit();
		    conn.setAutoCommit(true);
            conn.close();

            strQuery = null;
            pstmt = null;
            conn = null;

			return "";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.setAllUserJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.setAllUserJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.setAllUserJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.setAllUserJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.setAllUserJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }

	public Object[] getAllUser() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select cm_userid,cm_username from cmm0040 where  \n");
			strQuery.append(" cm_active='1' \n");
			strQuery.append(" order by cm_username  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            //ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() == 1) {
					rst = new HashMap<String, String>();
					rst.put("cm_userid", "0000");
					rst.put("cm_username", "0000");
					rst.put("cm_userinfo", "선택하세요");
					rsval.add(rst);
					rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("cm_userid", rs.getString("cm_userid"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cm_userinfo", rs.getString("cm_userid") + "[" + rs.getString("cm_username") + "]" );
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			returnObjectArray = rsval.toArray();
			rsval = null;


			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## UserInfo.getAllUser() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## UserInfo.getAllUser() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## UserInfo.getAllUser() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## UserInfo.getAllUser() Exception END ##");
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
					ecamsLogger.error("## UserInfo.getAllUser() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getAllUser() method statement

	public int userCopy(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> JobList,ArrayList<HashMap<String,String>> RgtList) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
    	PreparedStatement	pstmt2		= null;
		ResultSet         	rs          = null;
		StringBuffer      	strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();
			//conn.setAutoCommit(false);

        	for (int i=0 ; i<RgtList.size() ; i++){
        		strQuery.setLength(0);
        		strQuery.append("select count(*) cnt from cmm0043 \n");
    			strQuery.append(" where CM_USERID = ?	          \n");
    			strQuery.append("   and CM_RGTCD = ?		 	  \n");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			//pstmt =  new LoggableStatement(conn, strQuery.toString());
	        	pstmt.setString(1, etcData.get("userid1"));
	        	pstmt.setString(2, RgtList.get(i).get("cm_micode"));
    			//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
    			if (rs.next()){
    				if(rs.getInt("cnt") == 0){
			        	strQuery.setLength(0);
			        	strQuery.append("insert into  cmm0043 ");
			        	strQuery.append("(CM_USERID,CM_RGTCD,CM_CREATDT,CM_LASTDT) values (     \n");
			        	strQuery.append(" ?,?,sysdate,sysdate) \n");
			        	pstmt2 = conn.prepareStatement(strQuery.toString());
			        	pstmt2.setString(1, etcData.get("userid1"));
			        	pstmt2.setString(2, RgtList.get(i).get("cm_micode"));
			        	pstmt2.executeUpdate();
			        	pstmt2.close();
    				}
    			}
			}
			for (int i=0 ; i<JobList.size() ; i++){
				strQuery.setLength(0);
        		strQuery.append("select count(*) cnt from cmm0044 where  \n");
    			strQuery.append(" CM_USERID = ?							 \n");
    			strQuery.append(" and CM_SYSCD = ?		  				 \n");
    			strQuery.append(" and CM_JOBCD = ?		  				 \n");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			//pstmt =  new LoggableStatement(conn, strQuery.toString());
	        	pstmt.setString(1, etcData.get("userid1"));
	        	pstmt.setString(2, JobList.get(i).get("cm_syscd"));
	        	pstmt.setString(3, JobList.get(i).get("cm_jobcd"));
    			//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
                rs = pstmt.executeQuery();
    			if (rs.next()){
    				if(rs.getInt("cnt") == 0){
						strQuery.setLength(0);
			        	strQuery.append("insert into  cmm0044 ");
			        	strQuery.append("(CM_USERID,CM_SYSCD,CM_JOBCD,CM_CREATDT) values (     \n");
			        	strQuery.append(" ?,?,?,sysdate) \n");
			        	pstmt = conn.prepareStatement(strQuery.toString());
			        	pstmt.setString(1, etcData.get("userid1"));
			        	pstmt.setString(2, JobList.get(i).get("cm_syscd"));
			        	pstmt.setString(3, JobList.get(i).get("cm_jobcd"));
			        	pstmt.executeUpdate();
			        	pstmt.close();
    				}
    			}
			}

			//conn.commit();
        	conn.close();
        	pstmt = null;
        	conn = null;

            return 0;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserJob() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.delUserJob() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			conn.rollback();
			ecamsLogger.error("## Cmm0400.delUserJob() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.delUserJob() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.delUserJob() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }


	public String subNewDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String DeptName = dataObj.get("DeptName");
			String DeptUpCd = dataObj.get("DeptUpCd");
			String DeptCd 	=	"";
			strQuery.setLength(0);
            strQuery.append("select 'HAND'||lpad(to_number(nvl(max(Substr(CM_DEPTCD,5,9)),0)) + 1,5,'0') as dptcd 	");
            strQuery.append("from cmm0100 where Substr(CM_DEPTCD,1,4) ='HAND' 	");
            pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	rs = pstmt.executeQuery();
        	if (rs.next()){
        		DeptCd = rs.getString("dptcd");
        	}
        	rs.close();
        	pstmt.close();

        	strQuery.setLength(0);
	        strQuery.append("insert into cmm0100 (CM_DEPTCD,CM_DEPTNAME,CM_UPDEPTCD,CM_USEYN,CM_HANDYN) values ( ");
	        strQuery.append("?,?,?,'Y','Y') ");//PrjNo,TKey,Dirname,Docseq
        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	pstmt.setString(1, DeptCd);
        	pstmt.setString(2, DeptName);
        	pstmt.setString(3, DeptUpCd);
        	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();
			conn = null;
			pstmt = null;
			rs = null;

        	return dataObj.get("gbncd") + DeptCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subNewDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subNewDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subNewDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subNewDir() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2!= null)  try{pstmt2.close();}catch (Exception ex3){ex3.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subNewDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public int subDelDir_Check(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int cnt = 0;
		try {
			conn = connectionContext.getConnection();
			String DeptCd = dataObj.get("DeptUpCd");

			strQuery.setLength(0);
	        strQuery.append("select count(*) cnt from cmm0040       \n");
	        strQuery.append(" where cm_project in (select cm_deptcd \n");
	        strQuery.append("                        from (select * from cmm0100)\n");
	        strQuery.append("                       start with cm_deptcd=?       \n");
	        strQuery.append("                       connect by prior cm_deptcd=cm_updeptcd) \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, DeptCd);
	    	rs = pstmt.executeQuery();
	    	if (rs.next()) {
	    		cnt = rs.getInt("cnt");
	    	}
	    	rs.close();
	    	pstmt.close();

	    	conn.close();
			conn = null;
			pstmt = null;
			rs = null;
	    	return cnt;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd1600.subDelDir_Check() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subDelDir_Check() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1600.subDelDir_Check() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subDelDir_Check() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subDelDir_Check() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String subDelDir(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String DeptCd = dataObj.get("DeptUpCd");

			strQuery.setLength(0);
//	        strQuery.append("delete cmm0100                        							\n");//PrjNo
//	        strQuery.append(" where cm_deptcd in (select cm_deptcd 							\n");
//	        strQuery.append("                       from (select * from cmm0100)			\n");
//	        strQuery.append("                      start with cm_deptcd=?       			\n");
//	        strQuery.append("                      connect by prior cm_deptcd=cm_updeptcd)  \n");
			strQuery.append("UPDATE  CMM0100													\n");
			strQuery.append("SET     cm_useyn = 'N'												\n");
			strQuery.append("WHERE   cm_deptcd IN (SELECT    cm_deptcd 							\n");
			strQuery.append("                      FROM      (SELECT * FROM CMM0100)			\n");
			strQuery.append("                      START WITH        cm_deptcd = ?				\n");
			strQuery.append("                      CONNECT BY PRIOR  cm_deptcd = cm_updeptcd)	\n");
	    	pstmt = conn.prepareStatement(strQuery.toString());
	    	pstmt.setString(1, DeptCd);
	    	pstmt.executeUpdate();
	    	pstmt.close();
	    	conn.close();
			conn = null;
			pstmt = null;

	    	return DeptCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subDelDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subDelDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subDelDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subDelDir() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subDelDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public String subRename(HashMap<String,String> dataObj) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			String DeptName = dataObj.get("dirname");
			String DeptCd = dataObj.get("DeptUpCd");


			strQuery.setLength(0);
	        strQuery.append("update cmm0100 set CM_DEPTNAME=?    \n");
	        strQuery.append(" where CM_DEPTCD=?                  \n");
	    	pstmt = conn.prepareStatement(strQuery.toString());

        	pstmt.setString(1, DeptName);
        	pstmt.setString(2, DeptCd);
        	pstmt.executeUpdate();
	    	pstmt.close();

	    	conn.close();
			conn = null;
			pstmt = null;

	    	return DeptCd;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subRename() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1600.subRename() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd1600.subRename() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1600.subRename() Exception END ##");
			throw exception;
		}finally{
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.commit();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd1600.subRename() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	public Object[] getAllUserRGTCD(String sysCd, String jobCd, String rgtCd, String userId) throws SQLException, Exception{
    	Connection			conn		= null;
    	PreparedStatement	pstmt		= null;
    	PreparedStatement	pstmt2		= null;
    	ResultSet         	rs          = null;
		ResultSet         	rs2         = null;
		StringBuffer      	strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	  = null;
		int					pstmtcnt	= 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("SELECT      a.cm_userid, a.cm_username, b.cm_syscd, d.cm_sysmsg, b.cm_jobcd, e.cm_jobname	\n");
			strQuery.append("FROM        CMM0040 a, CMM0044 b, CMM0043 c, CMM0030 d, CMM0102 e							\n");
			strQuery.append("WHERE       a.cm_userid = b.cm_userid														\n");
			strQuery.append("AND         a.cm_userid = c.cm_userid														\n");
			strQuery.append("AND         b.cm_syscd = d.cm_syscd														\n");
			strQuery.append("AND         b.cm_jobcd = e.cm_jobcd														\n");
			strQuery.append("AND         d.cm_closedt IS NULL															\n");
			if( ((sysCd != null) && !sysCd.equals("")) && !sysCd.equals("0000") ) {
				strQuery.append("AND         b.cm_syscd = ?																	\n");
			}
			if( ((jobCd != null) && !jobCd.equals("")) && !jobCd.equals("0000") ) {
				strQuery.append("AND         b.cm_jobcd = ?																	\n");
			}
			if( ((rgtCd != null) && !rgtCd.equals("")) && !rgtCd.equals("0000") ) {
				strQuery.append("AND         c.cm_rgtcd = ?																	\n");
			}
			if( (userId != null) && !userId.equals("") ) {
				strQuery.append("AND         a.cm_userid = ?																\n");
			}
			strQuery.append("GROUP   BY  a.cm_userid, a.cm_username, b.cm_syscd, d.cm_sysmsg, b.cm_jobcd, e.cm_jobname	\n");
			strQuery.append("ORDER   BY  a.cm_username, a.cm_userid, d.cm_sysmsg, e.cm_jobname							\n");								
			
            pstmt = conn.prepareStatement(strQuery.toString());
            if( ((sysCd != null) && !sysCd.equals("")) && !sysCd.equals("0000") ) {
            	pstmt.setString(++pstmtcnt,sysCd);
            }
            if( ((jobCd != null) && !jobCd.equals("")) && !jobCd.equals("0000") ) {
            	pstmt.setString(++pstmtcnt,jobCd);
            }
            if( ((rgtCd != null) && !rgtCd.equals("")) && !rgtCd.equals("0000") ) {
            	pstmt.setString(++pstmtcnt,rgtCd);
            }
            if( (userId != null) && !userId.equals("") ) {
            	pstmt.setString(++pstmtcnt,userId);
            }
            
            rs = pstmt.executeQuery();

            String tmpUserId = ""; //이전 사용자id
            String tmpSysCd = ""; //이전 시스템
            boolean tmpChgSW = false; //사용자 바뀜여부
            while(rs.next()){
            	rst = new HashMap<String, String>();
            	if( !tmpUserId.equals(rs.getString("cm_userid")) ) {
            		tmpUserId = rs.getString("cm_userid");
            		tmpChgSW = true;
            		
            		rst.put("cm_userid", tmpUserId);
            		rst.put("cm_username", rs.getString("cm_username"));
            		
            		strQuery.setLength(0);
            		strQuery.append("SELECT      cm_codename								\n");
            		strQuery.append("FROM        CMM0020									\n");
            		strQuery.append("WHERE       cm_macode = 'RGTCD'						\n");
            		strQuery.append("AND         cm_micode <> '****'						\n");
            		strQuery.append("AND         cm_micode IN (  SELECT  cm_rgtcd			\n");
            		strQuery.append("                            FROM    CMM0043			\n");
            		strQuery.append("                            WHERE   cm_userid = ? )	\n");
            		strQuery.append("ORDER   BY  cm_micode									\n");
            		
            		pstmt2 = conn.prepareStatement(strQuery.toString());
            		pstmt2.setString(1,tmpUserId);
            		
            		rs2 = pstmt2.executeQuery();
            		
            		String tmpRgtCdName = "";
            		while(rs2.next()) {
        				if (tmpRgtCdName == "")
        					tmpRgtCdName = rs2.getString("cm_codename");
        				else tmpRgtCdName = tmpRgtCdName + ", " + rs2.getString("cm_codename");
            		}
            		rs2.close();
            		pstmt2.close();
            		
            		rst.put("cm_rgtcdname", tmpRgtCdName);
            	}else {
            		tmpChgSW = false;
            		rst.put("cm_userid", "");
            		rst.put("cm_username", "");
            	}
            	
            	if( !tmpSysCd.equals(rs.getString("cm_syscd")) ) {
            		tmpSysCd = rs.getString("cm_syscd");
            		
            		rst.put("cm_syscd", tmpSysCd);
            		rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));         		
            	}else {
            		if( tmpChgSW ) {
                		rst.put("cm_syscd", tmpSysCd);
                		rst.put("cm_sysmsg", rs.getString("cm_sysmsg")); 
            		} else {
	            		rst.put("cm_syscd", "");
	            		rst.put("cm_sysmsg", "");    
            		}
            	}
            	rst.put("cm_jobcd", rs.getString("cm_jobcd"));
            	rst.put("cm_jobname", rs.getString("cm_jobname"));
            	
            	
				rsval.add(rst);
				rst = null;
            }
            rs.close();
            pstmt.close();

            conn.close();

            strQuery = null;
            rs = null;
            pstmt = null;
            rs2 = null;
            pstmt2 = null;
            conn = null;

			return rsval.toArray();
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getAllUserRGTCD() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0400.getAllUserRGTCD() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0400.getAllUserRGTCD() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0400.getAllUserRGTCD() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0400.getAllUserRGTCD() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
    }
}