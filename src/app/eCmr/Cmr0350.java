/*****************************************************************************************
	1. program ID	: Cmr0350.java
	2. create date	: 2008.09. 17
	3. auth		    : nobody
	4. update date	:
	5. auth		    :
	6. description	: Cmr0350 [문서관리] -> [신청상세]
*****************************************************************************************/

package app.eCmr;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
//import app.common.LoggableStatement;
import app.common.SystemPath;
//import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr0350 {

    /**
     * Logger Class Instance Creation
     * logger
     */
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/**
	 * [신청상세] : 신청건 정보 조회
	 * @param  String strAcptno,String UserId
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getAcptinfo(String strAcptno,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsconf = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>	rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			    rst	   = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String 			  ReqSayu = "";
			String            LblConmsg = "";

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_editor,a.cr_docno,a.cr_prjno,          \n");
			strQuery.append("       to_char(a.cr_acptdate,'yyyy/mm/dd hh:mm:ss') as stdate, \n");
			strQuery.append("       to_char(a.cr_eddate,'yyyy/mm/dd hh:mm:ss') as eddate,   \n");
			strQuery.append("       to_char(a.cr_prcdate,'yyyy/mm/dd hh:mm:ss') as prcdate, \n");
			strQuery.append("       to_char(a.cr_cncllstd,'yyyymmddhh24miss') cncldat,      \n");
			strQuery.append("       a.cr_cnclstep,a.cr_cncllstd,a.cr_retryyn,               \n");
			strQuery.append("       a.cr_prjname,b.cm_username,a.cr_emgcd,a.cr_status,      \n");
			strQuery.append("       c.cm_deptname,d.cm_codename as code3,                   \n");
			strQuery.append("       a.cr_qrycd                                              \n");
			strQuery.append("  from cmm0020 d,cmm0040 b,cmm0100 c,cmr1000 a                 \n");
		    strQuery.append(" where a.cr_acptno = ?                                         \n");
			strQuery.append("   and a.cr_editor = B.cm_userid                               \n");
			strQuery.append("   and a.cr_teamcd = c.cm_deptcd                               \n");
			strQuery.append("   and d.cm_macode = 'REQUEST' and d.cm_micode = a.cr_qrycd    \n");

            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, strAcptno);

            rs = pstmt.executeQuery();
            rtList.clear();

			while(rs.next()){
				if(rs.getString("cr_emgcd")!= null){
					strQuery.setLength(0);
					strQuery.append(" select cm_codename from cmm0020  \n");
					strQuery.append(" where cm_macode='REQGBN' and cm_micode= ?  \n");

		            pstmt2 = conn.prepareStatement(strQuery.toString());
				    //pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, rs.getString("cr_emgcd"));

		            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());

		            rs2 = pstmt2.executeQuery();

		            if (rs2.next()) {
		            	ReqSayu = rs2.getString("cm_codename");

						if(rs.getString("cr_docno") != null){
							ReqSayu = ReqSayu +"["+ rs.getString("cr_docno")+"]";
						}
					}
					rs2.close();
					pstmt2.close();
				}

				if(rs.getString("cr_status").equals("3")){
					strQuery.setLength(0);
					strQuery.append(" select cr_conmsg from cmr9900  \n");
					strQuery.append(" where cr_acptno = ? \n");
					strQuery.append(" and cr_status='3' and cr_locat<>'00' \n");
					strQuery.append(" and cr_conmsg is not null and cr_confdate is not null \n");

		            pstmt2 = conn.prepareStatement(strQuery.toString());
				    //pstmt2 = new LoggableStatement(conn,strQuery.toString());
		            pstmt2.setString(1, strAcptno);

		           // //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());

		            rs2 = pstmt2.executeQuery();

		            if (rs2.next()) {
		            	LblConmsg = rs2.getString("cr_conmsg");
					}
					rs2.close();
					pstmt2.close();
				}

				//rst.put("ID", Integer.toString(rs.getRow()));
				rst = new HashMap<String,String>();
        	    rst.put("cr_prjnoname", rs.getString("cr_prjno")+"   "+ rs.getString("cr_prjname"));//프로젝트
				rst.put("cr_acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
				rst.put("acptno", rs.getString("cr_acptno"));
				rst.put("cr_sayu",rs.getString("code3"));
				rst.put("eddate",rs.getString("eddate"));
				rst.put("acptdate",rs.getString("stdate"));
				rst.put("cr_prjno",rs.getString("cr_prjno"));
				if(rs.getString("prcdate") != null){
					rst.put("prcdate",rs.getString("prcdate"));
				}else{
					rst.put("prcdate","");
				}
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cr_editor",rs.getString("cr_editor"));
				rst.put("cm_deptname",rs.getString("cm_deptname"));
				rst.put("cr_emgcd",rs.getString("cr_emgcd"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_docno",rs.getString("cr_docno"));
				rst.put("cr_qrycd",rs.getString("cr_qrycd"));

				if(ReqSayu == null){
					rst.put("ReqSayu", "");
				}
				else{
					rst.put("ReqSayu",ReqSayu);
				}
				if(LblConmsg == null){
					rst.put("cr_conmsg","");
				}
				else{
					rst.put("cr_conmsg",LblConmsg);
				}

				if (rs.getString("cr_status").equals("9")) {
					rst.put("cr_confname","처리완료");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
				} else if (rs.getString("cr_status").equals("3")) {
					rst.put("cr_confname","반려");
					rst.put("prcsw", "1");
					rst.put("endsw", "1");
					rst.put("updtsw1", "0");
					rst.put("updtsw2", "0");
					rst.put("updtsw3", "0");
					rst.put("log", "1");
				} else {
					String strPrcSw = "0";
					if (rs.getString("prcdate") != null) {
						rst.put("prcsw", "1");
						strPrcSw = "1";
					} else {
						rst.put("prcsw", "0");
					}
					rst.put("endsw", "0");

					Cmr0150 cmr0150 = new Cmr0150();
					rsconf = cmr0150.confLocat_conn(strAcptno,strPrcSw,rs.getString("cr_cnclstep"),rs.getString("cncldat"),rs.getString("cr_retryyn"),conn);

					rst.put("signteam", rsconf.get(0).get("signteam"));
					rst.put("signteamcd", rsconf.get(0).get("signteamcd"));
					rst.put("confusr", rsconf.get(0).get("confusr"));
					rst.put("cr_prcsw", rsconf.get(0).get("prcsw"));
					rst.put("confname", rsconf.get(0).get("confname"));
					rst.put("updtsw1", rsconf.get(0).get("updtsw1"));
					rst.put("updtsw2", rsconf.get(0).get("updtsw2"));
					rst.put("updtsw3", rsconf.get(0).get("updtsw3"));
					rst.put("errtry", rsconf.get(0).get("errtry"));
					rst.put("log", rsconf.get(0).get("log"));

					rsconf = null;
					cmr0150 = null;
				}

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1001               \n");
				strQuery.append(" where cr_acptno=?                                \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, strAcptno);
		        ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("file","1");
					}
					else{
						rst.put("file","0");
					}
				}
				rs2.close();
				pstmt2.close();

				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr9900               \n");
				strQuery.append(" where cr_acptno=? and cr_status='9'              \n");
				strQuery.append("   and cr_confusr=?                               \n");
		        pstmt2 = conn.prepareStatement(strQuery.toString());
				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2.setString(1, strAcptno);
				pstmt2.setString(2, UserId);
		        ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0){
						rst.put("secusw","Y");
					}
					else{
						rst.put("secusw","N");
					}
				}
				rs2.close();
				pstmt2.close();

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
			ecamsLogger.error("## Cmr0350.getAcptinfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.getAcptinfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getAcptinfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.getAcptinfo() Exception END ##");
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
					ecamsLogger.error("## Cmr0350.getAcptinfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getAcptinfo() method statement


	public Object[] getRstList(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] 		  returnObject = null;

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			int			parmCnt	 = 0;
//			UserInfo	userinfo = new UserInfo();
//			boolean 	adminYn  = userinfo.isAdmin_conn(UserId, conn);
//			userinfo = null;

			strQuery.setLength(0);
			strQuery.append("select a.cr_serno,c.cr_docfile,b.cr_ipaddr,b.cr_seqno,                  \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,b.cr_svrseq,  \n");
			strQuery.append("       b.cr_prcrst,b.cr_prcdate,b.cr_prcsys,b.cr_putcode,b.cr_rstfile,  \n");
			strQuery.append("       b.cr_svrcd,b.cr_svrname,b.cr_portno,                             \n");
			strQuery.append("       b.cr_wait,a.cr_pcdir                                             \n");
			strQuery.append("  from cmr1011 b,cmr1100 a,cmr0030 c                                    \n");
			strQuery.append(" where a.cr_acptno=? and a.cr_docid=c.cr_docid                          \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno and a.cr_serno=b.cr_serno                \n");
			strQuery.append("   and (nvl(b.cr_putcode,'NONE')<>'0000' or b.cr_rstfile is not null)   \n");
			strQuery.append("union                                                                   \n");
			strQuery.append("select b.cr_serno,'파일전송결과' cr_docfile,b.cr_ipaddr,b.cr_seqno,       \n");
			strQuery.append("       to_char(b.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,b.cr_svrseq,  \n");
			strQuery.append("       b.cr_prcrst,b.cr_prcdate,b.cr_prcsys,b.cr_putcode,               \n");
			strQuery.append("       b.cr_rstfile,b.cr_svrcd,b.cr_svrname,b.cr_portno,                \n");
			strQuery.append("       b.cr_wait,'' cr_pcdir                                            \n");
			strQuery.append("  from cmr1011 b,cmr1000 a                                              \n");
			strQuery.append(" where a.cr_acptno=?                                                    \n");
			strQuery.append("   and a.cr_acptno=b.cr_acptno and b.cr_serno=0                         \n");
			strQuery.append("   and (nvl(b.cr_putcode,'NONE')<>'0000' or b.cr_rstfile is not null)   \n");
			strQuery.append(" order by cr_prcdate,cr_prcsys,cr_seqno,cr_ipaddr                       \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, AcptNo);
			pstmt.setString(++parmCnt, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				rst = new HashMap<String, String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_docfile",rs.getString("cr_docfile"));
				rst.put("cr_seqno",rs.getString("cr_seqno"));
				rst.put("cr_acptno", AcptNo);
				rst.put("cr_prcsys", rs.getString("cr_prcsys"));
				rst.put("dirpath", rs.getString("cr_pcdir"));
				rst.put("cr_svrname",rs.getString("cr_svrname"));
				if (rs.getString("cr_putcode") != null) {
					PreparedStatement pstmt2       = null;
					ResultSet         rs2         = null;

					strQuery.setLength(0);
					strQuery.append("select cm_codename from cmm0020             \n");
					strQuery.append(" where cm_macode='ERRACCT' and cm_micode=?  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1,rs.getString("cr_putcode"));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("prcrst", rs2.getString("cm_codename"));
					}
					else{
						rst.put("prcrst", rs.getString("cr_putcode"));
					}
					rs2.close();
					pstmt2.close();
					rs2 = null;
					pstmt2 = null;
				}

				if (rs.getString("cr_svrname") != null){
					rst.put("cr_svrname", rs.getString("cr_svrname"));
				}
				if (rs.getString("cr_prcdate") != null){
					rst.put("cr_prcdate", rs.getString("cr_prcdate"));
				}
				if (rs.getString("prcdate") != null){
					rst.put("prcdate", rs.getString("prcdate"));
				}
				rst.put("cr_serno",Integer.toString(rs.getInt("cr_serno")));
				if (rs.getString("cr_putcode") != null){
					rst.put("cr_putcode",rs.getString("cr_putcode"));
				}
				if (rs.getString("cr_prcdate") != null) {
					if (rs.getString("cr_prcrst") != null && !rs.getString("cr_prcrst").equals("0000")){
						rst.put("ColorSw","5");
					} else rst.put("ColorSw","9");
				}
				else{
					if (rs.getString("cr_prcrst") != null && !rs.getString("cr_prcrst").equals("0000")){
						rst.put("ColorSw","5");
					} else rst.put("ColorSw","0");
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

			returnObject = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObject;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getRstList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.getRstList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getRstList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.getRstList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (returnObject !=  null)	returnObject = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();conn=null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.getRstList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement
	public String delReq(String UserId,String AcptNo,String serno,String DocId,
			String QryCd,String ReqCd,String ConfUsr) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";
		Cmr3200           gyulProc    = new Cmr3200();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			//취소건에 반출번호 set
			strQuery.setLength(0);
			strQuery.append("update cmr1100 set cr_confno='' \n");
			strQuery.append(" where cr_acptno in (select cr_baseno from cmr1100 \n");
			strQuery.append("                      where cr_acptno=? and cr_docid=?) \n");
			strQuery.append("   and cr_docid= ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2,DocId);
			pstmt.setString(3,DocId);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
            strQuery.append("delete cmr1100 where cr_acptno=?                     \n");
            strQuery.append("   and cr_serno=?                                    \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            pstmt.setString(2,serno);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            if(ReqCd.equals("31")){
            	strQuery.append("update cmr0030 set cr_status='0'                 \n");
            }else{
            	if(QryCd.equals("03")){
            		strQuery.append("update cmr0030 set cr_status='3'             \n");
            	}
            	else{
            		strQuery.append("update cmr0030 set cr_status='5'             \n");
            	}
            }
            strQuery.append(" where CR_DOCID=?                                    \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,DocId);
            pstmt.executeUpdate();
            pstmt.close();

            strErMsg = "0";
            strQuery.setLength(0);
            strQuery.append("select count(*) as cnt from cmr1100                 \n");
            strQuery.append(" where cr_acptno=?                                  \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,AcptNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	if (rs.getInt("cnt") == 0) {
            		strQuery.setLength(0);
                    strQuery.append("delete cmr9920 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr9910 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr9900 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            		strQuery.setLength(0);
                    strQuery.append("delete cmr1001 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();


            		strQuery.setLength(0);
                    strQuery.append("delete cmr1000 where cr_acptno=?               \n");
                    pstmt2 = conn.prepareStatement(strQuery.toString());
                    pstmt2.setString(1,AcptNo);
                    pstmt2.executeUpdate();
                    pstmt2.close();

            	}
            } else {
	            strQuery.setLength(0);
	            strQuery.append("select count(*) as cnt from cmr1100                 \n");
	            strQuery.append(" where cr_acptno=?                                  \n");
	            strQuery.append("   and (cr_putcode is null or                       \n");
	            strQuery.append("   and  nvl(cr_errcd,'ERR') != '0000')              \n");
	            pstmt2 = conn.prepareStatement(strQuery.toString());
	            pstmt2.setString(1,AcptNo);
	            rs2 = pstmt2.executeQuery();
	            if (rs2.next()) {
	            	if (rs2.getInt("cnt") == 0) {
	            		strErMsg = gyulProc.reqCncl(AcptNo,UserId,"전체 건 삭제로 인한 자동반려",ConfUsr);
	            	}
	            }
	            rs2.close();
	            pstmt.close();
            }
            rs.close();
            pstmt.close();

            gyulProc = null;

            if (strErMsg.equals("0")){
            	conn.commit();
            }
            else{
            	conn.rollback();
            }
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			pstmt2 = null;
			rs2 = null;
    		return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.delReq() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.delReq() SQLException END ##");
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
					ecamsLogger.error("## Cmr0350.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.delReq() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.delReq() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.delReq() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of delReq() method statement

	public Object[] getGridLst1(String strAcptno) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>> rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			   rst	  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

	        strQuery.setLength(0);
	        strQuery.append("select a.cr_acptno,a.cr_serno,a.cr_status,a.cr_qrycd,a.cr_docid,");
	        strQuery.append("a.cr_prjno,a.cr_version,a.cr_confno,a.cr_putcode,a.cr_pcdir, ");
	        strQuery.append("a.cr_unittit,a.cr_editor,a.cr_baseno,a.cr_errcd,a.cr_ccbyn,e.cr_devstep, ");
	        strQuery.append("to_char(a.cr_prcdate,'yyyy/mm/dd hh:mm:ss') as prcdate,e.cr_docfile ");
	        strQuery.append(" FROM CMR1100 a,CMR0030 e ");
	        strQuery.append("WHERE a.cr_acptno = ? "); //acptno
	        strQuery.append("  AND A.CR_DOCID=E.CR_DOCID  ");
	        strQuery.append("ORDER BY A.CR_SERNO ");

            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, strAcptno);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("Rsrcname", rs.getString("cr_docfile"));
				rst.put("cr_pcdir",rs.getString("cr_pcdir"));
				rst.put("cr_unittit",rs.getString("cr_unittit"));
				rst.put("cr_docid",rs.getString("cr_docid"));
				rst.put("cr_version",rs.getString("cr_version"));
				rst.put("cr_prjno",rs.getString("cr_prjno"));
				rst.put("cr_devstep",rs.getString("cr_devstep"));

				if (rs.getString("cr_ccbyn")!= null){
					rst.put("cr_ccbyn",rs.getString("cr_ccbyn"));
				}else{
					rst.put("cr_ccbyn","N");
				}
				rst.put("cr_baseno", rs.getString("cr_baseno").substring(0,4)+"-"+
						rs.getString("cr_baseno").substring(4,6)+"-"+
						rs.getString("cr_baseno").substring(6));

				rst.put("cr_qrycd",rs.getString("cr_qrycd"));

				strQuery.setLength(0);
            	strQuery.append("select cm_codename from cmm0020 ");
                if (rs.getString("cr_qrycd").equals("31") || rs.getString("cr_qrycd").equals("39")){
                	strQuery.append("where cm_macode='REQUEST' and cm_micode=? ");
                }else{
                	strQuery.append("where cm_macode='CHECKIN' and cm_micode=? ");
                }
                pstmt2 = conn.prepareStatement(strQuery.toString());
                pstmt2.setString(1, rs.getString("cr_qrycd"));
                rs2 = pstmt2.executeQuery();
    			if (rs2.next()){
    					rst.put("code1",rs2.getString("cm_codename"));
    			}else{
    					rst.put("code1","");
    			}
    			rs2.close();
    			pstmt2.close();


    			rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cr_errcd",rs.getString("cr_errcd"));
				rst.put("cr_serno",rs.getString("cr_serno"));


				if((rs.getString("cr_errcd")!= null && !strAcptno.substring(4,6).equals("46")) ||
				   (rs.getString("cr_putcode")!= null && strAcptno.substring(4,6).equals("46"))){
					strQuery.setLength(0);
	            	strQuery.append("select cm_codename from cmm0020 ");
                	strQuery.append("where cm_macode='ERRACCT' and cm_micode=? ");
	                pstmt2 = conn.prepareStatement(strQuery.toString());
	                if(rs.getString("cr_errcd")!= null && !strAcptno.substring(4,6).equals("46"))
	                	pstmt2.setString(1, rs.getString("cr_errcd"));
	                else pstmt2.setString(1, rs.getString("cr_putcode"));
	                rs2 = pstmt2.executeQuery();
	    			if (rs2.next()){
	        			rst.put("code2",rs2.getString("cm_codename"));

	    			}else{
	    				rst.put("code2","");

	    			}
	    			rs2.close();
	    			pstmt2.close();
				} else {
					rst.put("code2","");
				}
				if (rs.getString("cr_status").equals("3")) rst.put("ColorSw","3");
				else if (rs.getString("cr_status").equals("9")) rst.put("ColorSw","9");
				else if (rs.getString("prcdate") != null) rst.put("ColorSw","9");
				else {
					if (strAcptno.substring(4,6).equals("46")) {
						if (rs.getString("cr_putcode") != null) {
							rst.put("ColorSw", "5");
						} else rst.put("ColorSw", "0");
					} else if (rs.getString("cr_errcd") == null && !strAcptno.substring(4,6).equals("46")){
						strQuery.setLength(0);
						strQuery.append("select count(*) as cnt from cmr1100 a,cmr9900 b \n");
						strQuery.append("where a.cr_acptno =? \n");
						strQuery.append("and a.cr_acptno=b.cr_acptno \n");
						strQuery.append("and (a.cr_errcd is null or a.cr_errcd <>'0000') \n");
						strQuery.append("and (b.cr_confdate is not null or b.cr_locat ='00') \n");
						if (rs.getString("cr_qrycd").equals("31")){
							strQuery.append("and b.cr_team='SYSDDN' \n");
						}else if (rs.getString("cr_qrycd").equals("39")){
							strQuery.append("and b.cr_team='SYSDNC' \n");
						}else{
							strQuery.append("and b.cr_team='SYSDUP' \n");
						}
						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, strAcptno);
						rs2 = pstmt2.executeQuery();
						if (rs2.next()){
							if (rs2.getInt("cnt") > 0)
								rst.put("ColorSw", "5");
							else
								rst.put("ColorSw", "0");
						}
						rs2.close();
						pstmt2.close();
					} else if (rs.getString("cr_errcd") == null) {
						rst.put("ColorSw", "0");
					} else if (!rs.getString("cr_errcd").equals("0000")) {
						rst.put("ColorSw", "5");
					} else rst.put("ColorSw", "0");

				}
				rtList.add(rst);
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
			ecamsLogger.error("## Cmr0350.getGridLst1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.getGridLst1() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getGridLst1() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.getGridLst1() Exception END ##");
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
					ecamsLogger.error("## Cmr0350.getGridLst1() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getGridLst1() method statement


	public Object[] get_CCBSet(String strAcptno) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		Clob			  clobData	 = null;
		Reader			  clobReader = null;
		char[] 			  clobBuffer = null;

		try {
			conn = connectionContext.getConnection();

	        strQuery.setLength(0);
	        strQuery.append(" select a.cr_number,a.cr_story,a.cr_title,a.cr_md,b.cm_codename as CCBGB \n");
	        strQuery.append(" from cmr1002 a,cmm0020 b  \n");
	        strQuery.append(" where a.cr_acptno = ?  \n");
	        strQuery.append(" and b.cm_macode='CCBGB' and b.cm_micode=a.cr_qrycd  \n");

            pstmt = conn.prepareStatement(strQuery.toString());
		    //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, strAcptno);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cr_number", rs.getString("cr_number"));

				clobData = rs.getClob("cr_story");
				clobBuffer = new char[(int)clobData.length()];
				clobReader = clobData.getCharacterStream();
				clobReader.read(clobBuffer);


				rst.put("cr_story", new String(clobBuffer));
				//rst.put("cr_story",rs.getCharacterStream("cr_story").toString());
				rst.put("cr_title",rs.getString("cr_title"));
				rst.put("cr_md", rs.getString("cr_md"));
				rst.put("CCBGB",rs.getString("CCBGB"));

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
			ecamsLogger.error("## Cmr0350get_CCBSet.getGridLst1() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.get_CCBSet() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.get_CCBSet() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.get_CCBSet() Exception END ##");
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
					ecamsLogger.error("## Cmr0350.get_CCBSet() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_CCBSet() method statement


	public String get_CmdCncl(String UserId,String strAcptno,ArrayList<HashMap<String,String>> fileList,
			String ConfUsr) throws SQLException, Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               rtn_cnt     = 0;
		Cmr3200           cmr3200     = new Cmr3200();
		Cmr0300           cmr0300     = new Cmr0300();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String retMsg = "반려처리에 실패하였습니다.";

			for (int i=0;fileList.size()>i;i++) {
				strQuery.setLength(0);
				strQuery.append("update cmr1100 set cr_status='3',   \n");
				strQuery.append("   cr_prcdate=SYSDATE			 	 \n");
				strQuery.append(" where cr_acptno= ? and cr_serno= ? \n");//StrAcptno,cr_serno
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strAcptno);
				pstmt.setString(2, fileList.get(i).get("cr_serno"));
		        pstmt.executeUpdate();
		        pstmt.close();

				strQuery.setLength(0);
				strQuery.append("update cmr1100  set cr_confno=''        \n");
				strQuery.append(" where cr_confno= ? and cr_docid= ?     \n"); //StrAcptno,cr_docid
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strAcptno);
				pstmt.setString(2, fileList.get(i).get("cr_docid"));
		        pstmt.executeUpdate();
		        pstmt.close();
			}
			conn.commit();

			strQuery.setLength(0);
			strQuery.append("select count(*) as cnt from cmr1100 \n");
			strQuery.append(" where cr_acptno=?                  \n");
			strQuery.append("   and cr_status<>'3'               \n");

			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, strAcptno);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();

			if (rs.next()){
				rtn_cnt = rs.getInt("cnt");
			}
			rs.close();
			pstmt.close();


			if (rtn_cnt == 0) {
				retMsg = cmr3200.reqCncl(strAcptno,UserId,"전체 건 반려 자동반려",ConfUsr);
				retMsg = "정상건이 없어 신청건을 반려 처리하였습니다.";
			}
			else{
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr1100 \n");
				strQuery.append(" where cr_acptno=?                  \n");
				strQuery.append("   and cr_status <> '3'             \n");
				strQuery.append("   and cr_qrycd <> '05'			 \n");
				strQuery.append("   and cr_prcdate is null 			 \n");
				//strQuery.append("   and nvl(cr_errcd,'ERR1')<>'0000' \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, strAcptno);
				rs = pstmt.executeQuery();

				//retMsg = "9";
				retMsg = "개별반려처리를 완료하였습니다.";
				if (rs.next()){
					if (rs.getInt("cnt") == 0) {
						//retMsg = "2";
						retMsg = "정상처리 도중 에러가 발생 되었습니다.";
						if (cmr0300.callCmr9900_Str(strAcptno)){
							//retMsg = "1";
							retMsg = "정상처리 되었습니다.";
						}
					}
				}
				rs.close();
				pstmt.close();
			}

			conn.commit();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return retMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.get_CmdCncl() rollback exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.get_CmdCncl() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.get_CmdCncl() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (conn != null){
				try{
					conn.rollback();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.get_CmdCncl() rollback exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.get_CmdCncl() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.get_CmdCncl) Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					connectionContext.release();
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.get_CmdCncl() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_CmdCncl() method statement


	public Object[] getFileList(String AcptNo,String GbnCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select CR_ACPTNO,CR_SEQNO,CR_FILENAME,CR_RELDOC \n");
			strQuery.append("  from cmr1001 			                     \n");
			strQuery.append(" where cr_acptno=?  	                         \n");	//AcptNo
			strQuery.append("   and cr_gubun=?   		                     \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, GbnCd);
            rs = pstmt.executeQuery();
            rtList.clear();

            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cr_acptno", rs.getString("CR_ACPTNO")); 		//신청번호
				rst.put("cr_seqno", rs.getString("CR_SEQNO")); 			//일련번호
				rst.put("orgname", rs.getString("CR_FILENAME"));		//파일멸
				rst.put("savename", rs.getString("CR_RELDOC"));	        //저장파일
				rtList.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
    		return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement



	public String sysGyulCheck(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  szTeam  = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select cr_team									 \n");
			strQuery.append("  from cmr9900 			                     \n");
			strQuery.append(" where cr_acptno=?  	                         \n");	//AcptNo
			strQuery.append("   and cr_locat='00' 		                     \n");
			strQuery.append("   and cr_status='0' 		                     \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.setString(1, AcptNo);

			rs = pstmt.executeQuery();

			szTeam = "";

            while (rs.next()){
            	szTeam = rs.getString("cr_team");
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
            if (szTeam.length() < 1){
            	return "ecams:null";
            }

            return szTeam;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0350.sysGyulCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.sysGyulCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0350.sysGyulCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.sysGyulCheck() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.sysGyulCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getFileList() method statement

	public boolean clearErrFlag(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("update cmr1100 set cr_errcd = '' \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("and cr_prcdate is null \n");
			strQuery.append("and cr_status <> '3' \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            
            return true;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.clearErrFlag() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.clearErrFlag() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.clearErrFlag() SQLException END ##");
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
					ecamsLogger.error("## Cmr0350.clearErrFlag() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.clearErrFlag() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.clearErrFlag() Exception END ##");
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
					ecamsLogger.error("## Cmr0350.clearErrFlag() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of Cmr0350() method statement

	public Object[] updtYn(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray	= null;
		ConnectionContext connectionContext = new ConnectionResource();

		String            strUpdtSw   = "";
		rsval.clear();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt from cmr1100               \n");
			strQuery.append(" where cr_acptno=? and cr_confno is null          \n");
			strQuery.append("   and cr_prcdate is not null and cr_status<>'3'  \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getInt("cnt") > 0) strUpdtSw = "1";
				else strUpdtSw = "0";
			}
			rs.close();
			pstmt.close();

			if (strUpdtSw.equals("1")) {
				strQuery.setLength(0);

				strQuery.append("select a.cm_userid,a.cm_username                       \n");
				strQuery.append("  from cmm0040 a,cmd0304 b,cmr1100 c                   \n");
				strQuery.append(" where c.cr_acptno=? and c.cr_confno is null           \n");
				strQuery.append("   and c.cr_status<>'3' and c.cr_prcdate is not null   \n");
				strQuery.append("   and c.cr_prjno=b.cd_prjno and b.cd_docyn='Y'        \n");
				strQuery.append("   and b.cd_closedt is null                            \n");
				strQuery.append("   and b.cd_prjuser=a.cm_userid                        \n");
				strQuery.append("   and a.cm_active='1' and a.cm_userid<>c.cr_editor    \n");
				strQuery.append("group by a.cm_userid,a.cm_username                     \n");
				strQuery.append("order by a.cm_username                                 \n");
		        pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, AcptNo);
		        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		        rs = pstmt.executeQuery();
				while (rs.next()){
					if (rs.getRow() == 1 ) {
						rst = new HashMap<String,String>();
						rst.put("cm_username", "선택하세요");
						rst.put("cm_userid", "00000000");
						rsval.add(rst);
						rst = null;
					}
					rst = new HashMap<String, String>();
					rst.put("cm_username", rs.getString("cm_username"));
					rst.put("cm_userid", rs.getString("cm_userid"));
					rsval.add(rst);
					rst = null;
				}
				rs.close();
				pstmt.close();
			}
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
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.updtYn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.updtYn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.updtYn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.updtYn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null) returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.updtYn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of updtYn() method statement


	public String updtEditor(String AcptNo,String editUser,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
            strQuery.setLength(0);
            strQuery.append("update cmr1000 set cr_editor=?                         \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr1100 set cr_editor=?                         \n");
            strQuery.append(" where cr_acptno=?                                     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            pstmt.executeUpdate();
            pstmt.close();

            strQuery.setLength(0);
            strQuery.append("update cmr0030 set cr_editor=?                         \n");
            strQuery.append(" where cr_docid in (select cr_docid from cmr1100       \n");
            strQuery.append("                     where cr_acptno=?)               \n");
            strQuery.append("   and cr_status='5'                                   \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1,editUser);
            pstmt.setString(2,AcptNo);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            pstmt.executeUpdate();
            pstmt.close();

            strErMsg = "0";
            conn.commit();
            conn.close();
            pstmt = null;
            conn = null;

            return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.updtEditor() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0350.updtEditor() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0200.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0350.updtEditor() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0350.updtEditor() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0350.updtEditor() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of updtEditor() method statement

	public String svrProc(String AcptNo,String prcSys,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Runtime  		  run = null;
		Process 		  p = null;
		SystemPath		  systemPath  = new SystemPath();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			String  binpath;
			String[] chkAry;
			int	cmdrtn;

			binpath = systemPath.getTmpDir("15");

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
				return "2";
			}
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("delete cmr1011                                          \n");
			strQuery.append(" where cr_acptno=?                                      \n");
			strQuery.append("   and (cr_serno in (select cr_serno from cmr1100       \n");
			strQuery.append("                     where cr_acptno=?                  \n");
			strQuery.append("                       and cr_prcdate is null           \n");
			strQuery.append("                       and cr_status='0')               \n");
			strQuery.append("                 or cr_serno=0)                         \n");
			strQuery.append("   and cr_prcsys=?                                      \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2,AcptNo);
			pstmt.setString(3,prcSys);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
	        pstmt.close();

			strQuery.setLength(0);
			strQuery.append("update cmr1100                                              \n");
			strQuery.append("   set cr_putcode='',cr_pid='',cr_sysstep=0                 \n");
			strQuery.append(" where cr_acptno=? and cr_prcdate is null                   \n");
			strQuery.append("   and cr_status='0'                                        \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
	        pstmt.close();

			strQuery.setLength(0);
			strQuery.append("update cmr1000 set cr_notify='0',cr_retryyn='Y'             \n");
			strQuery.append(" where cr_acptno=?                                          \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        pstmt.executeUpdate();
	        pstmt.close();

	        conn.close();

			rs = null;
			pstmt = null;
			conn = null;

	        return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## svrProc.svrProc() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## svrProc.svrProc() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## svrProc.svrProc() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## svrProc.putDeploy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## svrProc.svrProc() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of putDeploy() method statement
}//end of Cmr0350() class statement