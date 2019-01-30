
/*****************************************************************************************
	1. program ID	: UserInfo.java
	2. create date	: 2008.04. 08
	3. auth		    : teok.kang
	4. update date	: 
	5. auth		    : 
	6. description	: UserInfo 
*****************************************************************************************/

package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class EdCnt{
	
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
    
	/**
	 * 사용자 정보 조회합니다.
	 * @param  UserID
	 * @return ArrayList<HashMap<String,String>>
	 * @throws SQLException
	 * @throws Exception
	 */
    
    public boolean chkForti(String acptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				 chkcnt;
		try {
			conn = connectionContext.getConnection();
			
			strQuery.append("SELECT COUNT(*) as cnt \n");
			strQuery.append("FROM CMR9900 \n");
			strQuery.append("WHERE	CR_ACPTNO = ? \n");
			strQuery.append("AND	cr_team = 'SYSFT' \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());	
			
			pstmt.setString(1, acptNo);
			
			rs = pstmt.executeQuery();
			
			chkcnt = 0;
			
			if (rs.next()){
				chkcnt = rs.getInt("cnt");				
			}
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			if (chkcnt > 0){
				return true;
			}
			else{
				return false;
			}
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## EdCnt.chkForti() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## EdCnt.chkForti() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## EdCnt.chkForti() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## EdCnt.chkForti() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## EdCnt.chkForti() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}			
    }
	
	public int getEdCnt(String acptNo, String prcSys) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				edcnt;
		int				edcnttmp1;
		int				edcnttmp2;
		try {
			conn = connectionContext.getConnection();
			
			if (acptNo == null || prcSys == null){
				return -1;
			}
			
			if (prcSys.toUpperCase().equals("SYSCB")||prcSys.toUpperCase().equals("SYSFT")||prcSys.toUpperCase().equals("SYSED")){
				return -1;
			}
				
			if (prcSys.toUpperCase().equals("SYSCB") && chkForti(acptNo)){
				strQuery.append("SELECT COUNT(*) as cnt \n");
				strQuery.append("FROM 	CMR1000 A, CMR1010 B, CMM0031 C, CMM0038 D, CMM0036 E \n");
				strQuery.append("WHERE	B.CR_ACPTNO = ? \n");
				strQuery.append("AND		B.CR_PRCDATE IS  NULL \n");
				strQuery.append("AND		B.CR_ACPTNO = A.CR_ACPTNO \n");
				strQuery.append("AND		B.CR_SYSCD	= C.CM_SYSCD \n");
				strQuery.append("AND     ((A.CR_QRYCD  = '04' AND  C.CM_SVRCD  = '03') OR \n");
				strQuery.append("		 (A.CR_QRYCD  = '03' AND  C.CM_SVRCD  = '13')) \n");		
				strQuery.append("AND		C.CM_CLOSEDT IS NULL \n");
				strQuery.append("AND		C.CM_SYSCD	= D.CM_SYSCD \n");
				strQuery.append("AND		C.CM_SVRCD	= D.CM_SVRCD \n");
				strQuery.append("AND		C.CM_SEQNO	= D.CM_SEQNO \n");
				strQuery.append("AND		B.CR_RSRCCD	= D.CM_RSRCCD \n");
				strQuery.append("AND     D.CM_SYSCD  = E.CM_SYSCD \n");
				strQuery.append("AND     D.CM_RSRCCD = E.CM_RSRCCD \n");
				strQuery.append("AND     (SUBSTR(E.CM_INFO,1,1) = '1' OR SUBSTR(E.CM_INFO,25,1) = '1') \n");		
				
				pstmt = conn.prepareStatement(strQuery.toString());	
				
				pstmt.setString(1, acptNo);
				
				rs = pstmt.executeQuery();
				
				edcnttmp1 = 0;
				
				if (rs.next()){
					edcnttmp1 = rs.getInt("cnt");					
				}
				
				rs.close();
				pstmt.close();
				
				strQuery.setLength(0);
				strQuery.append("select sum(cnt) as misnus \n");
				strQuery.append("				from ( \n");
				strQuery.append("				select b.cr_jobcd,b.cr_rsrccd ,count(*)-1 as cnt \n");
				strQuery.append("						FROM 	CMR1000 A, CMR1010 B, CMM0031 C, CMM0038 D, CMM0036 E \n");
				strQuery.append("						WHERE	B.CR_ACPTNO = ? \n");
				strQuery.append("						AND		B.CR_PRCDATE IS  NULL \n");
				strQuery.append("						AND		B.CR_ACPTNO = A.CR_ACPTNO \n");
				strQuery.append("						AND		B.CR_SYSCD	= C.CM_SYSCD \n");
				strQuery.append("						AND     ((A.CR_QRYCD  = '04' AND  C.CM_SVRCD  = '03') OR \n");
				strQuery.append("								 (A.CR_QRYCD  = '03' AND  C.CM_SVRCD  = '13')) \n");
				strQuery.append("						AND		C.CM_CLOSEDT IS NULL \n");
				strQuery.append("						AND		C.CM_SYSCD	= D.CM_SYSCD \n");
				strQuery.append("						AND		C.CM_SVRCD	= D.CM_SVRCD \n");
				strQuery.append("						AND		C.CM_SEQNO	= D.CM_SEQNO \n");
				strQuery.append("						AND		B.CR_RSRCCD	= D.CM_RSRCCD \n");
				strQuery.append("						AND     D.CM_SYSCD  = E.CM_SYSCD \n");
				strQuery.append("						AND     D.CM_RSRCCD = E.CM_RSRCCD \n");
				strQuery.append("						AND     SUBSTR(E.CM_INFO,1,1) = '1' \n");
				strQuery.append("						AND     SUBSTR(E.CM_INFO,16,1) = '1' \n");
				strQuery.append("				group by b.cr_jobcd,b.cr_rsrccd \n");
				strQuery.append("			) \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());	
				
				pstmt.setString(1, acptNo);
				
				
				rs = pstmt.executeQuery();
				edcnttmp2 = 0;
				
				if (rs.next()){
					edcnttmp2 = rs.getInt("misnus");					
				}
				
				rs.close();
				pstmt.close();
				
				edcnt = edcnttmp1 - edcnttmp2;
				
			}
			else{
				strQuery.append("SELECT COUNT(*) as cnt \n");
				strQuery.append("FROM 	CMR1000 A, CMR1010 B, CMM0031 C, CMM0038 D \n");
				strQuery.append("WHERE	B.CR_ACPTNO = ? \n");
				strQuery.append("AND		B.CR_PRCDATE IS  NULL \n");
				strQuery.append("AND		B.CR_ACPTNO = A.CR_ACPTNO \n");
				strQuery.append("AND		B.CR_SYSCD	= C.CM_SYSCD \n");
				strQuery.append("AND     ((A.CR_QRYCD  = '04' AND  C.CM_SVRCD  = decode(?,'SYSED','05','03')) OR \n");
				strQuery.append("			(A.CR_QRYCD  = '03' AND  C.CM_SVRCD  = decode(?,'SYSED','15','13'))) \n");
				strQuery.append("AND		C.CM_CLOSEDT IS NULL \n");
				strQuery.append("AND		B.CR_STATUS		 <> '3' \n");
				strQuery.append("AND		C.CM_SYSCD	= D.CM_SYSCD \n");
				strQuery.append("AND		C.CM_SVRCD	= D.CM_SVRCD \n");
				strQuery.append("AND		C.CM_SEQNO	= D.CM_SEQNO \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());	
				
				pstmt.setString(1, acptNo);
				pstmt.setString(2, prcSys);
				pstmt.setString(3, prcSys);
				
				rs = pstmt.executeQuery();
				
				edcnt = 0;
				
				if (rs.next()){
					edcnt = rs.getInt("cnt");					
				}
				
				rs.close();
				pstmt.close();
				
			}
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			return edcnt;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## EdCnt.getEdCnt() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## EdCnt.getEdCnt() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## EdCnt.getEdCnt() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## EdCnt.getEdCnt() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## EdCnt.getEdCnt() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of SelectEdCnt() method statement	

}//end of EdCnt class statement
