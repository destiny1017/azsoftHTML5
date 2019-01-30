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
//import app.common.SystemPath;
import app.common.UserInfo;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmr2800{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * get_SelectList
	 * @param  pSysCd, pStatus, pReqUser, pStartDt, pEndDt, pUserId, gbn, txtProg
	 * @return Object[]
	 * @throws SQLException
	 * @throws Exception
	 */
    public ArrayList<HashMap<String, String>> get_SelectList(String pSysCd,String pStatus,String pReqUser,
    		String pStartDt,String pEndDt,String pUserId,String gbn,String txtProg) throws SQLException, Exception {
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
			UserInfo userinfo = new UserInfo();

	        Integer           Cnt         = 0;
			conn = connectionContext.getConnection();
			boolean adminSw = userinfo.isAdmin_conn(pUserId, conn);

			strQuery.setLength(0);
			strQuery.append(" select a.cr_acptno, a.cr_passcd, a.CR_EDITOR, a.CR_ACPTDATE, a.CR_PRCDATE,\n");
			strQuery.append(" 		 D.CM_SYSMSG, C.CM_JOBNAME,                                         \n");
			strQuery.append(" 		 TO_CHAR(a.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as acptdate,		\n");
			strQuery.append(" 		 TO_CHAR(a.CR_PRCDATE,'yyyy/mm/dd hh24:mi:ss') as PRCDATE,          \n");
			strQuery.append(" 		 h.cm_username username                                             \n");
			if(gbn.equals("1")){
				strQuery.append(" ,b.CR_REFUSER,b.cr_chkuser,B.CR_CHKDATE                               \n");
				strQuery.append(" ,TO_CHAR(b.CR_CHKDATE,'yyyy/mm/dd hh24:mi:ss') as CHKDATE             \n");
				strQuery.append(" ,g.cm_username refuser,f.cm_username chkuser,b.cr_itemid              \n");
				strQuery.append(" ,b.CR_FILENAME, b.cr_reffile,b.CR_CHKFLAG,b.CR_REFFILENAME,e.cm_codename \n");
				strQuery.append(" ,b.cr_ciacptno,b.cr_refciacpt,b.cr_refitemid,SECUCHK_ITEM(B.CR_REFITEMID,?) secucd \n");
			}
			strQuery.append("   from CMR1000 A,CMR1060 B,CMM0102 c,CMM0030 D, CMM0040 H \n");
			if(gbn.equals("1")){
				strQuery.append("    , CMM0040 F, CMM0040 G, CMM0020 E                                  \n");
			}
			strQuery.append("  where A.CR_ACPTNO= B.CR_ACPTNO	 										\n");
			if(!adminSw){
				strQuery.append("and SECUCHK_ITEM(B.CR_REFITEMID,?)='OK'                  			    \n");
			}
			if(gbn.equals("1")){
				if(!txtProg.equals("")){
					strQuery.append(" and b.CR_FILENAME like ?											\n");
				}
				strQuery.append("and e.cm_macode='JAWON'                        						\n");
				strQuery.append("and b.CR_REFRSRCCD = e.cm_micode                   					\n");
			}
			if(!pReqUser.equals("")){
				strQuery.append("and A.CR_EDITOR in (select cm_userid 									\n");
				strQuery.append("                       from cmm0040 									\n");
				strQuery.append("                      where cm_active = '1'  							\n");
				strQuery.append("                        and cm_username = ?)	 						\n");
			}
			if(!pSysCd.equals("")){
				strQuery.append("and a.CR_SYSCD= ?				 										\n");
			}
			if(pStatus.equals("")){
				strQuery.append("and (b.CR_CHKDATE IS NULL AND A.CR_QRYCD='03'  						\n");
				strQuery.append("	  or (B.CR_CHKDATE IS NOT NULL  									\n");
				strQuery.append("		  and to_char(B.CR_CHKDATE,'yyyymmdd')>=?  						\n");
				strQuery.append("		  and to_char(B.CR_CHKDATE,'yyyymmdd')<=?)) 					\n");
			}else if(pStatus.equals("1")){
				strQuery.append("and b.CR_CHKDATE IS NULL						 						\n");
			}else if(pStatus.equals("9")){
				strQuery.append("and B.CR_CHKDATE IS NOT NULL  											\n");
				strQuery.append("and to_char(B.CR_CHKDATE,'yyyymmdd')>=?  								\n");
				strQuery.append("and to_char(B.CR_CHKDATE,'yyyymmdd')<=? 								\n");
			}

			strQuery.append("  and A.CR_SYSCD = D.CM_SYSCD												\n");
			strQuery.append("  and A.CR_JOBCD = C.CM_JOBCD	 											\n");
			if(gbn.equals("1")){
				strQuery.append("  and b.cr_chkuser=f.cm_userid(+)                    					\n");
				strQuery.append("  and b.cr_refuser=g.cm_userid(+)                    					\n");
			}
			strQuery.append("  and a.cr_editor=h.cm_userid(+)                    						\n");
			if(gbn.equals("2")){
				strQuery.append("  group by a.cr_acptno,a.cr_passcd,a.CR_EDITOR,a.CR_ACPTDATE,          \n");
				strQuery.append("  	     a.CR_PRCDATE,D.CM_SYSMSG,C.CM_JOBNAME,h.cm_username            \n");
			}
			strQuery.append("  order by a.CR_ACPTDATE, a.cr_acptno										\n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            Cnt = 0;
            if(gbn.equals("1")) pstmt.setString(++Cnt, pUserId);

            if(!adminSw){
            	pstmt.setString(++Cnt, pUserId);
            }
            if(gbn.equals("1")){
				if(!txtProg.equals("")){
            	pstmt.setString(++Cnt, "%"+txtProg+"%");
				}
            }
            if(!pReqUser.equals("")){
            	pstmt.setString(++Cnt, pReqUser);
            }
            if(!pSysCd.equals("")){
            	pstmt.setString(++Cnt, pSysCd);
			}

			if(pStatus.equals("")){
				pstmt.setString(++Cnt, pStartDt);
				pstmt.setString(++Cnt, pEndDt);
			}else if(pStatus.equals("9")){
				pstmt.setString(++Cnt, pStartDt);
				pstmt.setString(++Cnt, pEndDt);
			}

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
	            rst = new HashMap<String, String>();
        		rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
        		rst.put("cm_jobname", rs.getString("cm_jobname"));
        		rst.put("cr_acptno", rs.getString("cr_acptno"));
        		rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6));
        		rst.put("cr_passcd", rs.getString("cr_passcd"));
        		rst.put("cr_editor", rs.getString("CR_EDITOR"));
        		rst.put("username", rs.getString("username"));
        		rst.put("cr_acptdate", rs.getString("CR_ACPTDATE"));
        		rst.put("acptdate", rs.getString("acptdate"));

        		if(gbn.equals("1")){
            		rst.put("cr_itemid", rs.getString("cr_itemid"));
            		rst.put("cr_refitemid", rs.getString("cr_refitemid"));
            		rst.put("cr_chkdate", rs.getString("CR_CHKDATE"));

            		if(rs.getString("chkuser") != null) rst.put("chkuser", rs.getString("chkuser"));
            		else rst.put("chkuser", rs.getString("cr_chkuser"));

            		if(rs.getString("refuser") != null) rst.put("dduser", rs.getString("refuser"));
            		else rst.put("dduser", rs.getString("CR_REFUSER"));
    				rst.put("cr_filename", rs.getString("CR_FILENAME"));
    				rst.put("cr_reffile", rs.getString("cr_reffile"));
    				rst.put("cr_refrsrccd", rs.getString("cm_codename"));
    				if(rs.getString("CR_CHKDATE") != null){
	    				if(rs.getString("CR_CHKFLAG").equals("1"))	rst.put("cr_chkflag", "수정대상");
	    				else rst.put("cr_chkflag", "확인");
    				}else{
    					rst.put("cr_chkflag", "미완료");
    				}
            		rst.put("cr_reffilename", rs.getString("CR_REFFILENAME"));
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

            		if (rs.getString("cr_refciacpt") != null) {
            			strQuery.setLength(0);
            			strQuery.append("select to_char(a.cr_acptdate,'yyyy/mm/dd hh24:mi') acptdate,b.cm_username \n");
            			strQuery.append("  from cmm0040 b,cmr1000 a     \n");
            			strQuery.append(" where a.cr_acptno=?           \n");
            			strQuery.append("   and a.cr_editor=b.cm_userid \n");
            			pstmt2 = conn.prepareStatement(strQuery.toString());
            			pstmt2.setString(1, rs.getString("cr_refciacpt"));
            			rs2 = pstmt2.executeQuery();
            			if (rs2.next()) {
            				rst.put("refacptdt", rs2.getString("acptdate"));
            				rst.put("refeditor", rs2.getString("cm_username"));
            			}
            			rs2.close();
            			pstmt2.close();
            			rst.put("refacptno", rs.getString("cr_refciacpt").substring(0,4)+"-"+
            					            rs.getString("cr_refciacpt").substring(4,6)+"-"+
            					            rs.getString("cr_refciacpt").substring(6));

            		}
            		if(rs.getString("CR_CHKDATE") != null){
            			rst.put("chkdate", rs.getString("CHKDATE"));
            		}else{
            			rst.put("chkdate", "");
            		}
            		if((adminSw == true || rs.getString("secucd").equals("OK"))
            				&& rs.getString("CR_CIACPTNO") == null){
            			rst.put("visible", "1");
            			rst.put("check", "1");
            			rst.put("checkbox", "1");
            		}else{
            			rst.put("visible", "0");
            			rst.put("check", "0");
            			rst.put("checkbox", "0");
            		}
    			}else{
    				rst.put("cr_filename", "");
    				rst.put("cr_reffile", "");
    				rst.put("cr_refrsrccd", "");
            		rst.put("cr_chkflag", "");
            		rst.put("cr_reffilename", "");
            		rst.put("chkdate", "");
            		rst.put("visible", "0");
        			rst.put("check", "0");
        			rst.put("checkbox", "0");
    			}

        		if(rs.getString("CR_PRCDATE") != null){
        			rst.put("prcdate", rs.getString("PRCDATE"));
        		}else{
        			rst.put("prcdate", "");
        		}


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


			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr2800.get_SelectList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr2800.get_SelectList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr2800.get_SelectList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr2800.get_SelectList() Exception END ##");
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
					ecamsLogger.error("## Cmr2800.get_SelectList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of get_SelectList() method statement

}//end of Cmr2800 class statement
