/*****************************************************************************************
	1. program ID	: Cmr0250.java
	2. create date	: 2008. 08. 10
	3. auth		    : is.choi
	4. update date	: 2009. 05. 20
	5. auth		    : no name
	6. description	: [체크인신청상세]
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

import app.common.LoggableStatement;
import app.common.UserInfo;
//import app.common.SystemPath;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr2850{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * getReqList
	 * @param user_id, AcptNo
	 * @return ArrayList<HashMap<String, String>>
	 * @throws SQLException
	 * @throws Exception
	 */


	public ArrayList<HashMap<String, String>> getReqList(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt3       = null;
		PreparedStatement pstmt4       = null;
		PreparedStatement pstmt5       = null;
		ResultSet         rs          = null;
		ResultSet         rs3         = null;
		ResultSet         rs4         = null;
		ResultSet         rs5         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append(" select a.cr_passok,cr_editor,cr_acptno,cr_passcd,a.cr_status,\n");
			strQuery.append("        a.cr_qrycd,a.cr_syscd,a.cr_sysgb,a.cr_prcdate,       \n");
			strQuery.append("        to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,\n");
			strQuery.append("        to_char(a.cr_prcdate,'yyyy/mm/dd hh24:mi') prcdate,  \n");
			strQuery.append("        a.cr_acptdate,b.cm_sysmsg,d.cm_username  			  \n");
			strQuery.append("   from cmm0040 d,cmm0030 b, cmr1000 a   					  \n");
			strQuery.append("  where a.cr_acptno = ?                                      \n");
			strQuery.append("    and a.cr_editor = d.cm_userid                            \n");
			strQuery.append("    and a.cr_syscd=b.cm_syscd                                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        //String DeptCd = "";
			if (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cr_passok", rs.getString("cr_passok"));
				rst.put("cr_editor", rs.getString("cr_editor"));
				rst.put("cr_acptno", rs.getString("cr_acptno"));
				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,12));
				if(rs.getString("cr_passcd") != null) rst.put("cr_passcd", rs.getString("cr_passcd"));
	            else rst.put("cr_passcd", "");
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("cr_acptdate", rs.getString("cr_acptdate"));
				rst.put("acptdate", rs.getString("acptdate"));
				rst.put("prcdate", rs.getString("prcdate"));


	            rst.put("cr_qrycd", rs.getString("cr_qrycd"));
	            rst.put("cr_syscd", rs.getString("cr_syscd"));
	            rst.put("cr_sysgb", rs.getString("cr_sysgb"));
	            if (rs.getString("cr_editor").equals(UserId)) rst.put("SecuSw", "Y");

	            if(rs.getString("cr_prcdate") == null){
	            	rst.put("prcdate", "");

	            	strQuery.setLength(0);
	                strQuery.append(" select cr_team,cr_teamcd,cr_confname \n");
	                strQuery.append("   from cmr9900                       \n");
	                strQuery.append("  where cr_acptno=?                   \n");
	                strQuery.append("    and cr_locat='00'                 \n");
	                pstmt3 = conn.prepareStatement(strQuery.toString());
					//pstmt3 = new LoggableStatement(conn,strQuery.toString());
					pstmt3.setString(1,AcptNo);
			        //ecamsLogger.error(((LoggableStatement)pstmt3).getQueryString());
			        rs3 = pstmt3.executeQuery();
			        if(rs3.next()){
			        	if(rs3.getString("cr_teamcd") != null){
			        		if(rs3.getString("cr_teamcd").equals("1")){
					        	strQuery.setLength(0);
					        	strQuery.append("select cm_codename from cmm0020  		\n");
					        	strQuery.append("where cm_macode = 'SYSGBN' 			\n");
					        	strQuery.append("and cm_micode = ? 			 			\n");
					        	pstmt5 = conn.prepareStatement(strQuery.toString());
					        	//pstmt5 = new LoggableStatement(conn,strQuery.toString());
					        	pstmt5.setString(1,rs3.getString("cr_team"));
						        //ecamsLogger.error(((LoggableStatement)pstmt5).getQueryString());
						        rs5 = pstmt5.executeQuery();
						        if(rs5.next()){
						        	rst.put("confname", rs5.getString("cm_codename"));
						        }
						        rs5.close();
						        pstmt5.close();
	                       }else if(rs3.getString("cr_teamcd").equals("2") || rs3.getString("cr_teamcd").equals("3")
	                    		   || rs3.getString("cr_teamcd").equals("6")){
	                    	   strQuery.setLength(0);
	                    	   strQuery.append(" select cm_username  		\n");
	                           strQuery.append("   from cmm0040  			\n");
	                    	   strQuery.append("  where cm_userid = ?  		\n");
	                    	   pstmt4 = conn.prepareStatement(strQuery.toString());
			        		   //pstmt4 = new LoggableStatement(conn,strQuery.toString());
			        		   pstmt4.setString(1,rs3.getString("cr_team"));
						       //ecamsLogger.error(((LoggableStatement)pstmt4).getQueryString());
						       rs4 = pstmt4.executeQuery();
						       if(rs4.next()){
						    	   rst.put("confname", rs4.getString("cm_username") + "님 결재대기 중!");
						       }else{
						    	   rst.put("confname", rs3.getString("cr_confname") + " 결재대기 중!");
						       }
						       rs4.close();
						       pstmt4.close();
	                        }else{
	                        	rst.put("confname", rs3.getString("cr_confname") + " 결재대기 중!");
	                        }
			        	}
			        }
			        rs3.close();
			        pstmt3.close();

	            }else{
	            	rst.put("confname", "처리완료");
	            }
	            rsval.add(rst);
	            rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs3 = null;
			pstmt3 = null;
			rs4 = null;
			pstmt4 = null;
			rs5 = null;
			pstmt5 = null;
			conn = null;

			return rsval;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2850.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2850.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2850.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2850.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2850.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getReqList() method statement

	/**
	 * getProgList
	 * @param user_id, AcptNo, SecuSw
	 * @return ArrayList<HashMap<String, String>>
	 * @throws SQLException
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> getProgList(String UserId,String AcptNo,String SecuSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           RefChkFg    = false;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		try {

			conn = connectionContext.getConnection();
			UserInfo userinfo = new UserInfo();
			boolean adminSw = userinfo.isAdmin_conn(UserId, conn);
			/*
			strQuery.setLength(0);
        	strQuery.append("select cm_username from cmm0040 where cm_userid=?	\n");
        	pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, UserId);
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        if(rs.next()){
	        	strUserName =  rs.getString("cm_username");
	        }
	        rs.close();
	        pstmt.close();SECUCHK_ITEM(B.CR_REFITEMID,?)
	        */
			strQuery.setLength(0);
			strQuery.append("select a.cr_filename,cr_reffile,cr_reffilename,           \n");
			strQuery.append("       a.cr_refuser,a.cr_ciacptno,a.cr_acptno,   	       \n");
			strQuery.append("       a.cr_chkflag,a.cr_chkdate,a.cr_chkuser,   	       \n");
			strQuery.append("       to_char(a.cr_chkdate,'yyyy/mm/dd hh24:mi') chkdate,\n");
			strQuery.append("       b.cm_codename calljawon,c.cm_codename jawon,       \n");
			strQuery.append("       d.cr_rsrccd,e.cm_username chkuser,                 \n");
			strQuery.append("       f.cm_username refuser,a.cr_itemid,a.cr_refitemid,  \n");
			strQuery.append("       a.cr_ciacptno,a.cr_refciacpt,a.cr_reftstacpt,      \n");
			strQuery.append("       SECUCHK_ITEM(a.CR_REFITEMID,?) secucd,             \n");
			strQuery.append("       REFITEMUSER(A.CR_REFITEMID) refusernm              \n");
			strQuery.append("from cmm0040 f,cmm0040 e,cmr0020 d,cmm0020 c,cmm0020 b,cmr1060 a \n");
			if (AcptNo.substring(4,6).equals("03")) {
				strQuery.append("where (a.cr_acptno=? or a.cr_reftstacpt=?)		\n");
			} else {
				strQuery.append("where (a.cr_ciacptno=? or a.cr_refciacpt=?)    \n");
			}
			strQuery.append("and a.cr_itemid=d.cr_itemid                      	\n");
			strQuery.append("and b.cm_macode='JAWON'                        	\n");
			strQuery.append("and a.CR_REFRSRCCD = b.cm_micode                   \n");
			strQuery.append("and c.cm_macode='JAWON'                            \n");
			strQuery.append("and c.cm_micode=d.cr_rsrccd                        \n");
			strQuery.append("and a.cr_chkuser=e.cm_userid(+)                    \n");
			strQuery.append("and a.cr_refuser=f.cm_userid(+)                    \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, AcptNo);
			pstmt.setString(3, AcptNo);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
		        rst.put("cr_acptno", rs.getString("cr_acptno"));
		        rst.put("cr_filename", rs.getString("cr_filename"));
		        rst.put("cr_reffile", rs.getString("cr_reffile"));
		        rst.put("cr_reffilename", rs.getString("cr_reffilename"));
		        rst.put("cm_codename", rs.getString("calljawon"));
		        rst.put("refusernm", rs.getString("refusernm"));
		        rst.put("visible", "0");
		        rst.put("checkbox", "0");
		        rst.put("check", "0");

		        if (rs.getString("cr_ciacptno") != null && rs.getString("cr_ciacptno") != "") RefChkFg = false;
		        else {
		        	RefChkFg = true;
			        if (adminSw == false) {
			        	if (rs.getString("secucd").equals("OK")) RefChkFg = true;
			        } else {
			        	RefChkFg = true;
			        }
		        }

		        if (RefChkFg == true) {
		        	rst.put("visible", "1");
		        	rst.put("check", "1");
		        }

		        if(rs.getString("cr_chkflag") != null && rs.getString("cr_chkflag") != null){
		            if(rs.getString("cr_chkflag").equals("1")) rst.put("chkyn", "수정대상");
		            else rst.put("chkyn", "확인");
		        } else {
		        	rst.put("chkyn", "미확인");
		        }

		        if(rs.getString("CR_CHKDATE") != null){
		        	rst.put("chkdate", rs.getString("chkdate"));
		        	rst.put("cr_chkdate", rs.getString("cr_chkdate"));
		        }else{
		        	rst.put("chkdate", "");
		        	rst.put("cr_chkdate", "");
		        }

		        if(rs.getString("cr_chkuser") != null){ //확인자
		        	if (rs.getString("chkuser") != null) rst.put("chkuser", rs.getString("chkuser"));
		        	else rst.put("chkuser", rs.getString("cr_chkuser"));
		        }

		        if(rs.getString("refuser") != null){ //확인담당자
		            rst.put("dsuser", rs.getString("refuser"));
		        }else{
		            rst.put("dsuser",rs.getString("cr_refuser"));
		        }
		        if (rs.getString("cr_ciacptno") != null) {
        			strQuery.setLength(0);
        			strQuery.append("select to_char(cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate from cmr1000 \n");
        			strQuery.append(" where cr_acptno=?     \n");
        			pstmt2 = conn.prepareStatement(strQuery.toString());
        			pstmt2.setString(1, rs.getString("cr_ciacptno"));
        			rs2 = pstmt2.executeQuery();
        			if (rs2.next()) {
        				rst.put("ciacptdt", rs2.getString("acptdate"));
        			}
        			rs2.close();
        			pstmt2.close();
        			rst.put("ciacptno", rs.getString("cr_ciacptno").substring(0,4)+"-"+
        					            rs.getString("cr_ciacptno").substring(4,6)+"-"+
        					            rs.getString("cr_ciacptno").substring(6));
        		}
		        if (rs.getString("cr_reftstacpt") != null) {
        			strQuery.setLength(0);
        			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,b.cm_username \n");
        			strQuery.append("  from cmm0040 b,cmr1000 a     \n");
        			strQuery.append(" where a.cr_acptno=?           \n");
        			strQuery.append("   and a.cr_editor=b.cm_userid \n");
        			pstmt2 = conn.prepareStatement(strQuery.toString());
        			pstmt2.setString(1, rs.getString("cr_reftstacpt"));
        			rs2 = pstmt2.executeQuery();
        			if (rs2.next()) {
        				rst.put("reftstacptdt", rs2.getString("acptdate"));
        				rst.put("refeditor", rs2.getString("cm_username"));
        			}
        			rs2.close();
        			pstmt2.close();
        			rst.put("reftstacptno", rs.getString("cr_reftstacpt").substring(0,4)+"-"+
        					            rs.getString("cr_reftstacpt").substring(4,6)+"-"+
        					            rs.getString("cr_reftstacpt").substring(6));

        		}
        		if (rs.getString("cr_refciacpt") != null) {
        			strQuery.setLength(0);
        			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate \n");
        			strQuery.append("  from cmr1000 a     \n");
        			strQuery.append(" where a.cr_acptno=? \n");
        			pstmt2 = conn.prepareStatement(strQuery.toString());
        			pstmt2.setString(1, rs.getString("cr_refciacpt"));
        			rs2 = pstmt2.executeQuery();
        			if (rs2.next()) {
        				rst.put("refacptdt", rs2.getString("acptdate"));
        			}
        			rs2.close();
        			pstmt2.close();
        			rst.put("refacptno", rs.getString("cr_refciacpt").substring(0,4)+"-"+
        					            rs.getString("cr_refciacpt").substring(4,6)+"-"+
        					            rs.getString("cr_refciacpt").substring(6));

        		}

		        if(rs.getString("cr_acptno") != null){
		        	rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,8));
		        	rst.put("cr_acptno", rs.getString("cr_acptno"));
		        }else{
		        	rst.put("acptno", "");
		        	rst.put("cr_acptno", "");
		        }
		        rst.put("cr_itemid", rs.getString("cr_itemid"));
		        rst.put("cr_refitemid", rs.getString("cr_refitemid"));

		        rsval.add(rst);
		        rst = null;

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			//ecamsLogger.error("******** rsval ********"+rsval.toString());
			return rsval;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2850.getProgList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2850.getProgList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2850.getProgList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2850.getProgList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rsval != null) rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.close();conn = null;
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2850.getProgList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgList() method statement


	/**
	 * tbl_update
	 * @param cbocd, userid, acptno, grdList
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String tbl_update(String cbocd,String userid,ArrayList<HashMap<String,String>> grdList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  i=0;

		try {
			conn = connectionContext.getConnection();
			for(i=0; i<grdList.size(); i++){
				strQuery.setLength(0);
				strQuery.append(" update CMR1060					 \n");
				strQuery.append(" set CR_CHKDATE=SYSDATE,			 \n");
				strQuery.append("     CR_chkUSER=?,					 \n");
				strQuery.append("     CR_ChkFLAG=?					 \n");
				strQuery.append(" where cr_acptno=? and cr_itemid=?	 \n");
				strQuery.append("   and cr_refitemid=?	             \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, userid);
				pstmt.setString(2, cbocd);
				pstmt.setString(3, grdList.get(i).get("cr_acptno"));
				pstmt.setString(4, grdList.get(i).get("cr_itemid"));
				pstmt.setString(5, grdList.get(i).get("cr_refitemid"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				pstmt.executeUpdate();
				pstmt.close();
			}
			conn.close();
			conn = null;
			
			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2850.tbl_update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2850.tbl_update() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2850.tbl_update() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2850.tbl_update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr2850.tbl_update() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2850.tbl_update() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr2850.tbl_update() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of tbl_update() method statement

}//end of Cmr2850 class statement
