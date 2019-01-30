/*****************************************************************************************
	1. program ID	: Cmr0700.java
	2. create date	: 2006.08.08
	3. auth		    : is.choi
	4. update date	: 2009.07.10
	5. auth		    : no name
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
public class Cmr0700{    
	
    /**
     * Logger Class Instance Creation
     * logger
     */
	
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * request_Check_In
	 * @param 
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public String request_Check_In(ArrayList<HashMap<String,String>> chkInList,HashMap<String,String> etcData,ArrayList<HashMap<String,Object>> ConfList,String confFg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		String			  AcptNo	  = null;
		int				  pstmtcount  = 1;
		int				  i           = 0;
		int               SeqNo       = 0;
		ArrayList<HashMap<String,Object>>	rData2 = null;

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
	        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));
	        autoseq = null;
	        
	        strQuery.setLength(0);
	        strQuery.append("select count(*) as cnt from cmr1000 \n");
        	strQuery.append("where cr_acptno= ? \n");
	        
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	
        	rs = pstmt.executeQuery();
        	
        	if (rs.next()){
        		i = rs.getInt("cnt");
        	}
        	rs.close();
        	pstmt.close();
        	
        	if (i>0){
        		throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
        	}        	
        	String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
        	String strRequest = "";
	        strQuery.setLength(0);
	        strQuery.append("select cm_codename from cmm0020       \n");
	        strQuery.append(" where cm_macode='REQUEST'            \n");
	        strQuery.append("   and cm_micode=?                    \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, etcData.get("ReqCD"));
	        rs = pstmt.executeQuery();
	        if (rs.next()) strRequest = rs.getString("cm_codename");
	        rs.close();
	        pstmt.close();

	        
        	strQuery.setLength(0);
        	strQuery.append("insert into cmr1000 ");
        	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, ");
        	strQuery.append("CR_PASSOK,CR_PASSCD,CR_NOAUTO,CR_BEFJOB,CR_EDITOR,CR_SAYU,CR_DOCNO) values ( ");
        	strQuery.append("?, ?, ?, '00000', sysdate, '0', ?, ?, '0', ?, ?, 'N', ?, ?, '') ");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("SysCd"));
        	pstmt.setString(pstmtcount++, etcData.get("SysGb"));
        	pstmt.setString(pstmtcount++, strTeam);
        	pstmt.setString(pstmtcount++, etcData.get("ReqCD")); 
        	pstmt.setString(pstmtcount++, strRequest);
        	pstmt.setString(pstmtcount++, etcData.get("gbncd"));
        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        	pstmt.setString(pstmtcount++, etcData.get("Sayu").replaceAll("'", "''"));
        	
        	pstmt.executeUpdate();        	
        	pstmt.close();
        	
        	
        	for (i=0;i<chkInList.size();i++){
            	strQuery.setLength(0);
            	strQuery.append("insert into cmr1200 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SVRNAME,CR_SERVICE,CR_XAYN,CR_TIMEOUT,  \n");
            	strQuery.append("CR_GBNCD,CR_PROCCD,CR_SYSCD,CR_ITEMID,CR_EDITOR,CR_STATUS,     \n");
            	strQuery.append("CR_SYSSTEP,CR_ETC) values (                                           \n");
            	strQuery.append("?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, '0', 0, ?)                      \n");
            	
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn,strQuery.toString());
            	
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, i + 1);
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("svrname"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("service"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("xacd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("timeout"));            	
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("gbncd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("passcd"));
            	pstmt.setString(pstmtcount++, etcData.get("SysCd"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("cr_itemid"));
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkInList.get(i).get("etc").replaceAll("'", "''"));
            	////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();            	
        	}
    		for (i=0;i<ConfList.size();i++){
	        	if (ConfList.get(i).get("cm_congbn").equals("1") || ConfList.get(i).get("cm_congbn").equals("2") ||
	        		ConfList.get(i).get("cm_congbn").equals("3") ||	ConfList.get(i).get("cm_congbn").equals("4") ||
	        		ConfList.get(i).get("cm_congbn").equals("5") || ConfList.get(i).get("cm_congbn").equals("6")) {
	        		
		        	strQuery.setLength(0);
		        	strQuery.append("insert into cmr9900                                               \n");
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
	        	    rData2 = (ArrayList<HashMap<String, Object>>) ConfList.get(i).get("arysv");
					pstmt.setString(++pstmtcount, (String) rData2.get(0).get("SvUser"));
					rData2 = null;
	        	    if (ConfList.get(i).get("cm_gubun").equals("C")){
	        	    	pstmt.setString(++pstmtcount,"3");
	        	    }
	        	    else if (ConfList.get(i).get("cm_gubun").equals("R")){
	        	    	pstmt.setString(++pstmtcount,"8"); 
	        	    }
	        	    else{
	        	    	pstmt.setString(++pstmtcount, (String) ConfList.get(i).get("cm_gubun"));
	        	    }
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
		        	
		        }
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
			
        	
        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, '000000', '', '9', ?, '1' ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmtcount = 1;
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
        	
        	pstmt.executeUpdate();
        	pstmt.close();
        	
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
					ecamsLogger.error("## Cmr0700.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}				
			}
			ecamsLogger.error("## Cmr0700.request_Check_In() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0700.request_Check_In() SQLException END ##");			
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
					ecamsLogger.error("## Cmr0700.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}				
			}			
			ecamsLogger.error("## Cmr0700.request_Check_In() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0700.request_Check_In() Exception END ##");				
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
					ecamsLogger.error("## Cmr0700.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	
	public String pgmCheck(String SysCd,String RsrcName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
						
			strQuery.append("select count(*) as cnt from cmr0020               \n");
			strQuery.append(" where cr_syscd=? and upper(cr_rsrcname)=upper(?) \n");						
	        pstmt = conn.prepareStatement(strQuery.toString());	
	        pstmt.setString(1, SysCd);        		
	        pstmt.setString(2, RsrcName);        	     	
	        rs = pstmt.executeQuery();
	        if (rs.next() == true) {
	        	if (rs.getInt("cnt") > 0){
	        		retMsg = "0";
	        	}
	        	else{
	        		retMsg = "1";
	        	}
	        }
	        rs.close();
	        pstmt.close();
	        conn.close();
			conn = null;
			pstmt = null;
			rs = null;
	        
	        return retMsg;	        
	        
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0700.pgmCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0700.pgmCheck() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0700.pgmCheck() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0700.pgmCheck() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0700.pgmCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of pgmCheck() method statement
	
	
	public String request_Deploy(HashMap<String,String> etcData, ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
			
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
			
	        String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
	        Cmr0200 cmr0200 = new Cmr0200();
	        
        	String retMsg = "";
        	
            conn.setAutoCommit(false);
            AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

			pstmtcount = 1;
			strQuery.setLength(0);
        	strQuery.append("insert into cmr1000														\n");
        	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD, 	\n");
        	strQuery.append(" CR_QRYCD,CR_PASSCD,CR_BEFJOB,CR_EMGCD,CR_EDITOR,CR_SAYU,  				\n");
        	strQuery.append(" CR_ECLIPSE,CR_ITSMID,CR_ITSMTITLE, CR_PASSOK, CR_PRCREQ)					\n");
        	strQuery.append("values (?,?,?,?,sysdate,'0',?,?,?,  ?,?,?,?,  'N',?,?, ?,? ) 				\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt = new LoggableStatement(conn,strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("cr_syscd"));
        	pstmt.setString(pstmtcount++, etcData.get("cr_sysgb"));
        	pstmt.setString(pstmtcount++, etcData.get("cr_jobcd"));
        	pstmt.setString(pstmtcount++, strTeam);
        	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));

           	//pstmt.setString(pstmtcount++, etcData.get("PassCd"));
        	pstmt.setString(pstmtcount++, etcData.get("Sayu"));
        	pstmt.setString(pstmtcount++, "N");
        	pstmt.setString(pstmtcount++, etcData.get("EmgCd"));
        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        	pstmt.setString(pstmtcount++, etcData.get("ReqSayu"));
 
        	pstmt.setString(pstmtcount++, etcData.get("cc_srid"));
        	pstmt.setString(pstmtcount++, etcData.get("cc_title"));
        	
        	pstmt.setString(pstmtcount++, etcData.get("Deploy"));
        	pstmt.setString(pstmtcount++, etcData.get("AplyDate"));       	
        	
        	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();
        	
        	retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}

        	if (etcData.get("cc_srid") != null) {
            	strQuery.setLength(0);
            	strQuery.append("update cmc0010 set cc_status=?  \n");
            	strQuery.append(" where cc_srid=?                \n");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	//pstmt = new LoggableStatement(conn,strQuery.toString());
            	if (etcData.get("ReqCD").equals("03")) {
            		pstmt.setString(pstmtcount++, "305");
            	} else {
            		pstmt.setString(pstmtcount++, "501");
            	}
            	pstmt.setString(pstmtcount++, etcData.get("cc_srid"));
            	pstmt.executeUpdate();
            	pstmt.close();
        	}
 
			pstmtcount = 1;
			strQuery.setLength(0);
        	strQuery.append("insert into cmr1300 \n");
        	strQuery.append("(CR_ACPTNO,CR_STATUS,CR_JOBMSG,CR_EDITOR) \n");
        	strQuery.append("values (?,'0',?,?) 				\n");

        	pstmt = conn.prepareStatement(strQuery.toString());
        	//pstmt = new LoggableStatement(conn,strQuery.toString());
        	pstmt.setString(pstmtcount++, AcptNo);
        	pstmt.setString(pstmtcount++, etcData.get("cr_jobmsg"));
        	pstmt.setString(pstmtcount++, etcData.get("UserID"));
        
        	pstmt.executeUpdate();
        	
        	
        	
        	conn.commit();

        	conn.close();
        	
        	autoseq = null;
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
					ecamsLogger.error("## Cmr0700.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0700.request_Deploy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0700.request_Deploy() SQLException END ##");
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
					ecamsLogger.error("## Cmr0700.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0700.request_Deploy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0700.request_Deploy() Exception END ##");
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
					ecamsLogger.error("## Cmr0700.request_Deploy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}//end of Cmr0700 class statement
