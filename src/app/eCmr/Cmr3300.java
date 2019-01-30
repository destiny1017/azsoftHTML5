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
import app.common.SystemPath;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr3300{
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
    public Object[] get_SelectList(String pSysCd,String pStartDt,String pEndDt,String pDept,String pTeam,String pUserId,String Radio_Result,String ChkRes) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

	        Integer           Cnt         = 0;
	        String			  CmsDate	  = "";

			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select to_char(cm_cmsdate,'yyyymmddhh24miss') as cmsdate from cmm0010 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
    		rs = pstmt.executeQuery();
    		while (rs.next()){
    			CmsDate=rs.getString("cmsdate");
    		}
    		rs.close();
    		pstmt.close();

			strQuery.setLength(0);
			strQuery.append("Select substr(b.cm_deptname,instr(b.cm_deptname,' ') + 1) deptname,		 \n");
			strQuery.append("       c.cr_story,d.cr_prjno,d.cr_devno,e.cm_dirpath,h.cm_sysmsg,			 \n");
			strQuery.append("       a.cr_rsrcname,decode(a.cr_rsrccd,'16','MAP','AP') apcd,			     \n");
			strQuery.append("       d.cr_sayu,f.cm_codename,g.cm_username,d.cr_acptdate,			         \n");
			strQuery.append("       a.cr_syscd,a.cr_dsncd,d.cr_teamcd,decode(a.cr_rsrccd,'16','01','02') rsrccd, \n");
			strQuery.append("       i.cm_codename jawon,j.cm_codename qrycd,a.cr_acptno,d.cr_aftchg,      \n");
			strQuery.append("       substr(a.cr_acptno,1,4) || '-' || substr(a.cr_acptno,5,2) ||    		\n");
			strQuery.append("       '-' || substr(a.cr_acptno,7,6) as acptno					            \n");
			strQuery.append("from cmr1010 a,cmm0100 b,cmr0020 c,cmr1000 d,cmm0070 e,cmm0020 f,cmm0040 g,cmm0030 h,cmm0020 i,cmm0020 j  \n");
			strQuery.append("where 																		 \n");
			strQuery.append("to_char(d.cr_acptdate,'yyyymmdd')>=? and 									 \n");
			strQuery.append("to_char(d.cr_acptdate,'yyyymmdd')<=? and 									 \n");
			strQuery.append("d.cr_qrycd='04' and d.cr_status<>'3' and 									 \n");
			if(pSysCd != "" && pSysCd != null){
				  strQuery.append("a.cr_syscd=? and \n");//ll.L_Syscd
			}
			if(Radio_Result.equals("1")){
				if(Integer.parseInt(CmsDate.substring(0,8)) >= Integer.parseInt(pStartDt)){
					strQuery.append("d.cr_aftchg='N' and												 \n");
				}else if(Integer.parseInt(CmsDate.substring(0,8)) >= Integer.parseInt(pStartDt) &&
						Integer.parseInt(CmsDate.substring(0,8)) < Integer.parseInt(pEndDt)){
					strQuery.append("d.cr_aftchg='N' and												 \n");
				}else{
					strQuery.append("d.cr_aftchg='Y' and												 \n");
				}
			}else if(Radio_Result.equals("2")){
				strQuery.append("d.cr_aftchg='Y' and													 \n");
			}
			strQuery.append("d.cr_acptno=a.cr_acptno and												 \n");
			strQuery.append("a.cr_dsncd=a.cr_basedsn and a.cr_rsrcname=a.cr_basepgm and 				 \n");
			strQuery.append("d.cr_teamcd=b.cm_deptcd and			 									 \n");

			if(pDept != "" && pDept != null){
				strQuery.append("b.cm_deptseq like ? and												 \n");
			}
			if(pTeam != "" && pTeam != null){
				strQuery.append("b.cm_deptseq like ? and												 \n");
			}

			strQuery.append(" a.cr_syscd=c.cr_syscd and a.cr_dsncd=c.cr_dsncd and a.cr_rsrcname=c.cr_rsrcname and \n");
			strQuery.append(" a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd and \n");
			strQuery.append(" f.cm_macode='REQCD' and f.cm_micode=d.cr_emgcd and \n");
			strQuery.append(" d.cr_editor=g.cm_userid and \n");
			strQuery.append(" d.cr_syscd=h.cm_syscd and \n");
			strQuery.append(" i.cm_macode='JAWON' and i.cm_micode=a.cr_rsrccd and \n");
			strQuery.append(" j.cm_macode='CHECKIN' and j.cm_micode=a.cr_qrycd 	  \n");
			strQuery.append("order by decode(a.cr_rsrccd,'16','01','02'),a.cr_syscd,substr(b.cm_deptname,instr(b.cm_deptname,' ') + 1),a.cr_rsrcname \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            Cnt = 0;
            pstmt.setString(++Cnt, pStartDt);
            pstmt.setString(++Cnt, pEndDt);
            if (pSysCd != "" && pSysCd != null) {
            	pstmt.setString(++Cnt, pSysCd);
            }
            if(pDept != "" && pDept != null){
            	pstmt.setString(++Cnt, pDept);
            }
            if(pTeam != "" && pTeam != null){
            	pstmt.setString(++Cnt, pTeam);
            }

			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            String prjid ="";
            
			while (rs.next()){
				prjid ="";
				if(ChkRes.equals("1")){
					strQuery.append("update cmm0010 set cm_cmsdate=SYSDATE \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					rs2 = pstmt2.executeQuery();
					pstmt2.close();
					rs2.close();
					pstmt2=null;
					rs2=null;
				}
				prjid = rs.getString("cr_prjno")+"-"+rs.getString("cr_devno");
				rst = new HashMap<String, String>();
				rst.put("apcd",   rs.getString("apcd"));
				rst.put("cm_sysmsg",   rs.getString("cm_sysmsg"));
				rst.put("deptname",   rs.getString("deptname"));
				rst.put("cr_rsrcname",   rs.getString("cr_rsrcname"));
				rst.put("cm_username",   rs.getString("cm_username"));
				rst.put("QryCd",   rs.getString("QryCd"));
				rst.put("cm_dirpath",   rs.getString("cm_dirpath"));
				rst.put("prjid",   prjid);
				rst.put("cr_sayu",   rs.getString("cr_sayu"));
				rst.put("jawon",   rs.getString("jawon"));

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

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.SelectList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectList() method statement


    public String reqCncl(String AcptNo,String UserId,String conMsg,String ConfUsr) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Runtime  run = null;
		Process p = null;
		SystemPath		  systemPath = new SystemPath();
		String  binpath;
		String[] chkAry;
		int		cmdrtn;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			binpath = systemPath.getTmpDir_conn("14",conn);

			run = Runtime.getRuntime();

			chkAry = new String[4];
			chkAry[0] = "/bin/sh";
			chkAry[1] = binpath+"/procck2";
			chkAry[2] = "ecams_acct";
			chkAry[3] = AcptNo;


			p = run.exec(chkAry);
			p.waitFor();

			cmdrtn = p.exitValue();
			if (cmdrtn > 1) {
	        	pstmt = null;
	        	conn = null;

				return "2";
			}


        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, ?, ?, '9', ?, '9' ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.setString(2,UserId);
        	pstmt.setString(3,conMsg);
        	pstmt.setString(4,ConfUsr);

        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
    		strQuery.append("select a.cr_acptno,a.cr_confusr      \n");
    		strQuery.append("  from (Select cr_acptno             \n");
    		strQuery.append("          from cmr1030               \n");
    		strQuery.append("          start with cr_befact=?     \n");
    		strQuery.append("        connect by prior             \n");
    		strQuery.append("             cr_acptno=cr_befact) b, \n");
    		strQuery.append("  cmr9900 a                          \n");
    		strQuery.append(" where b.cr_acptno=a.cr_acptno       \n");
    		strQuery.append("   and a.cr_locat='00'               \n");
    		strQuery.append("   and a.cr_status='0'               \n");
    		pstmt2 = conn.prepareStatement(strQuery.toString());
    		pstmt2.setString(1, AcptNo);
    		rs = pstmt2.executeQuery();
    		while (rs.next()) {
    			strQuery.setLength(0);
            	strQuery.append("Begin CMR9900_STR ( ");
            	strQuery.append("?, ?, ?, '9', ?, ? ); End;");
            	pstmt2 = conn.prepareStatement(strQuery.toString());
            	pstmt2.setString(1, rs.getString("cr_acptno"));
            	pstmt2.setString(2,UserId);
            	pstmt2.setString(3,"선행작업 회수로 인한 자동반려처리");
            	pstmt2.setString(4,rs.getString("cr_confusr"));
            	pstmt2.setString(5,"9");

            	pstmt2.executeUpdate();
            	pstmt2.close();
    		}
    		rs.close();
    		pstmt2.close();

        	conn.close();
			conn = null;
			pstmt = null;
			rs = null;

        	return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.reqCncl() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.reqCncl() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.reqCncl() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.reqCncl() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)  try{rs.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.reqCncl() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of reqCncl() method statement


    public String cnclYn(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retUser     = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

        	strQuery.setLength(0);
        	strQuery.append("select count(*) as cnt from cmr9900          \n");
        	strQuery.append("where cr_acptno=? and cr_locat<>'00'         \n");
        	strQuery.append("  and cr_status='9' and cr_teamcd<>'2'       \n");
        	strQuery.append("  and cr_team not in ('SYSFT','SYSPF','SYSUP','SYSCB') \n");
        	//pstmt =  new LoggableStatement(conn, strQuery.toString());
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
        	rs = pstmt.executeQuery();

        	if (rs.next()){
        		if (rs.getInt("cnt") == 0) {

        			strQuery.setLength(0);
        			strQuery.append("select cr_confusr from cmr9900           \n");
        			strQuery.append(" where cr_acptno=? and cr_locat='00'     \n");
        			strQuery.append("   and cr_status='0'                     \n");
        			//pstmt =  new LoggableStatement(conn, strQuery.toString());
        			pstmt2 = conn.prepareStatement(strQuery.toString());
                	pstmt2.setString(1, AcptNo);
                	//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
                	rs2 = pstmt2.executeQuery();
                	if (rs2.next()) {
                		retUser = rs2.getString("cr_confusr");
                	}
                	rs2.close();
                	pstmt2.close();
                	rs2 = null;
                	pstmt2 = null;
        		}
        	}
        	rs.close();
        	pstmt.close();
        	conn.close();

        	rs = null;
        	pstmt = null;
        	conn = null;

        	return retUser;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.cnclYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.cnclYn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.cnclYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.cnclYn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.cnclYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cnclYn() method statement

    public String reqDelete(String AcptNo,String UserId,String QryCd,String ConfUsr) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, ?, '삭제처리를 위한 사전작업', '9', ?, '9' ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.setString(2,UserId);
        	pstmt.setString(3,ConfUsr);
        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("delete cmr9920 where cr_acptno=?              \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("delete cmr9910 where cr_acptno=?              \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("delete cmr9900 where cr_acptno=?              \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("delete cmr1001 where cr_acptno=?              \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();

        	if (QryCd.substring(0,1).equals("3")) {
        		strQuery.setLength(0);
            	strQuery.append("delete cmr1100 where cr_acptno=?              \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, AcptNo);
            	pstmt.executeUpdate();
            	pstmt.close();

        		strQuery.setLength(0);
            	strQuery.append("delete cmr1002 where cr_acptno=?              \n");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(1, AcptNo);
            	pstmt.executeUpdate();
            	pstmt.close();
        	} else {
        		strQuery.setLength(0);
	        	strQuery.append("delete cmr1011 where cr_acptno=?              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();

        		strQuery.setLength(0);
	        	strQuery.append("delete cmr1030 where cr_acptno=?              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();

        		strQuery.setLength(0);
	        	strQuery.append("delete cmr1030 where cr_befact=?              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();

        		strQuery.setLength(0);
	        	strQuery.append("delete cmr0027 where cr_acptno=?              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();

        		strQuery.setLength(0);
	        	strQuery.append("delete cmr1010 where cr_acptno=?              \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, AcptNo);
	        	pstmt.executeUpdate();
	        	pstmt.close();
        	}
        	strQuery.setLength(0);
        	strQuery.append("delete cmr1000 where cr_acptno=?              \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.executeUpdate();
        	pstmt.close();
        	conn.commit();
        	conn.close();
			conn = null;
			pstmt = null;

        	return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr3200.reqDelete() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr3200.reqDelete() SQLException END ##");
			if (conn != null) conn.rollback();
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr3200.reqDelete() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr3200.reqDelete() Exception END ##");
			if (conn != null) conn.rollback();
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr3200.reqDelete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of reqDelete() method statement

}//end of Cmr3200 class statement
