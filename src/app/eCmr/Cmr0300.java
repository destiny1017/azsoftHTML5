	
	/*****************************************************************************************
		1. program ID	: test_Tree2DAO.java
		2. create date	: 2006.08. 08
		3. auth		    : teok.kang
		4. update date	: 
		5. auth		    : 
		6. description	: test_Tree2DAO
	*****************************************************************************************/
	
	package app.eCmr;
	
	
//	import java.io.BufferedReader;
//	import java.io.BufferedWriter;
//	import java.io.File;
//	import java.io.FileInputStream;
//	import java.io.FileOutputStream;
//	import java.io.FileReader;
//	import java.io.FileWriter;
//	import java.io.InputStreamReader;
//	import java.io.OutputStreamWriter;
	import java.io.StringReader;
	import java.sql.Connection;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	
	import java.util.ArrayList;
	import java.util.HashMap;
	
	
	import org.apache.log4j.Logger;
	
	import com.ecams.common.dbconn.ConnectionContext;
	import com.ecams.common.dbconn.ConnectionResource;
	import com.ecams.common.logger.EcamsLogger;
	
	import app.common.*;
	
	/**
	 * @author bigeyes
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	
	 
	
	public class Cmr0300{
		
	    /**
	 * Logger Class Instance Creation
	 * logger
	 */
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
	/**
	 * 게시판을 조회합니다.(구분값을 주어)
	 * @param  gbn
	 * @return ArrayList
	 * @throws SQLException
	 * @throws Exception
	 */
	
	
	public Object[] getDocList(String prjNo,String docSeq,String UserId,String ChildYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
	
			
			strQuery.append("select distinct a.CR_DOCFILE,to_char(a.CR_LASTDT,'yyyymmdd') as lastdt,	   \n"); 
			strQuery.append("a.CR_LSTVER,a.CR_CCBYN, a.cr_docid,a.cr_prjno, d.cm_codename, a.cr_status,	   \n");
			strQuery.append("c.cr_docseq,e.cm_username, j.CD_DEVHOME as dirpath,a.cr_docsta				   \n"); 
			strQuery.append("from cmr0030 a, cmd0304 b, cmr0031 c,cmm0020 d,cmm0040 e, cmd0300 j 		   \n");
			strQuery.append("where c.cr_prjno= ? 														   \n");
			strQuery.append("And  b.cd_prjno=c.cr_prjno 												   \n");
			strQuery.append("and c.cr_docid=a.cr_docid 													   \n");  
			strQuery.append("AND a.cr_closedt is null and nvl(a.cr_docsta,'0')<>'1'  				       \n");    
			strQuery.append("And d.cm_macode='CMR0020' and d.cm_micode=a.cr_status 						   \n");
			strQuery.append("And a.cr_lstver>0 and a.cr_status='0' 										   \n");
			strQuery.append("and b.CD_PRJUSER= ? 														   \n");
			strQuery.append("AND j.CD_USERID = b.CD_PRJUSER 											   \n");
			strQuery.append("AND j.CD_SYSCD='99999' 													   \n");
			strQuery.append("And a.cr_editor=e.cm_userid 												   \n");
			if (ChildYn.toUpperCase().equals("Y")){
				strQuery.append("and c.cr_docseq in ( SELECT CD_DOCSEQ FROM (SELECT * FROM CMD0303 		   \n");
				strQuery.append("			                         		 WHERE CD_PRJNO = ?) 		   \n");
				strQuery.append("   				  START WITH CD_UPDOCSEQ = ? 						   \n");
				strQuery.append("					  CONNECT BY PRIOR CD_DOCSEQ = CD_UPDOCSEQ 			   \n");
				strQuery.append("					  UNION 											   \n");
				strQuery.append("                     SELECT ? FROM DUAL) 								   \n");
			}
			else{
				strQuery.append("and c.cr_docseq= ? ");
			}
			
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			
			pstmt.setString(1, prjNo);
			pstmt.setString(2, UserId);
			if (ChildYn.toUpperCase().equals("Y")){
				pstmt.setString(3, prjNo);
				pstmt.setString(4, docSeq);
				pstmt.setString(5, docSeq);
			}
			else{
				pstmt.setString(3, docSeq);
			}
			
			
			
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();            
			
			rsval.clear();
			
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_docfile", rs.getString("cr_docfile"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("lastdt", rs.getString("lastdt"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cr_lstver", rs.getString("cr_lstver"));
				rst.put("cr_ccbyn", rs.getString("cr_ccbyn"));
				rst.put("cr_docid", rs.getString("cr_docid"));
				rst.put("cr_docseq", rs.getString("cr_docseq"));
				rst.put("cr_prjno", rs.getString("cr_prjno"));
				rst.put("dirpath", rs.getString("dirpath"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("selected_flag","0");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
		
			rs.close();
			pstmt.close();
			conn.close();
			
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rsval.toArray();
			
			rsval = null;
			
			return rtObj;
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## test_Tree2DAO.TreeList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## test_Tree2DAO.TreeList() Exception END ##");				
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
					ecamsLogger.error("## test_Tree2DAO.TreeList() connection release exception ##");
				ex3.printStackTrace();
				}
			}
			rtObj = null;
		}
		
	}//end of TreeList() method statement		
	
	public Object[] getDocListCond(String prjNo,String gbnCD,String UserId,String keyStr) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
	
			strQuery.append("select distinct a.CR_DOCFILE,to_char(a.CR_LASTDT,'yyyymmdd') as lastdt,	   \n"); 
			strQuery.append("a.CR_LSTVER,a.CR_CCBYN, a.cr_docid,a.cr_prjno, d.cm_codename, a.cr_status,	   \n");
			strQuery.append("c.cr_docseq,e.cm_username, j.CD_DEVHOME as dirpath          				   \n"); 
			strQuery.append("from cmr0030 a, cmd0304 b, cmr0031 c,cmm0020 d,cmm0040 e, cmd0300 j 		   \n");
			strQuery.append("where c.cr_prjno= ? 														   \n");
			strQuery.append("And  b.cd_prjno=c.cr_prjno 												   \n");
			strQuery.append("and c.cr_docid=a.cr_docid 													   \n");  
			strQuery.append("AND a.cr_closedt is null 												       \n");    
			strQuery.append("And d.cm_macode='CMR0020' and d.cm_micode=a.cr_status 						   \n");
			strQuery.append("And a.cr_lstver>0 and a.cr_status='0' 										   \n");
			strQuery.append("AND nvl(a.cr_docsta,'0')<>'1'                                                 \n");
			strQuery.append("and b.CD_PRJUSER= ? 														   \n");
			strQuery.append("AND j.CD_USERID = b.CD_PRJUSER 											   \n");
			strQuery.append("AND j.CD_SYSCD='99999' 													   \n");
			strQuery.append("And a.cr_editor=e.cm_userid 												   \n");
			if (gbnCD.equals("04")){
				strQuery.append("and a.cr_DEVSTEP= (SELECT CM_micode FROM CMM0020 WHERE					   \n");
				strQuery.append("					CM_MACODE='DEVSTEP' AND CM_CODENAME= ? )			   \n");
			}
			else if(gbnCD.equals("05")){
				strQuery.append("and a.CR_DOCFILE like ?												   \n");
			}
			
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, prjNo);
			pstmt.setString(2, UserId);
			if (gbnCD.equals("04")){
				pstmt.setString(3, keyStr.trim());
			}
			else if(gbnCD.equals("05")){
				pstmt.setString(3, "%"+keyStr+ "%");
			}
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();
			
			rsval.clear();
			
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_docfile", rs.getString("cr_docfile"));
				rst.put("cm_username", rs.getString("cm_username"));
				rst.put("lastdt", rs.getString("lastdt"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cr_lstver", rs.getString("cr_lstver"));
				rst.put("cr_ccbyn", rs.getString("cr_ccbyn"));
				rst.put("cr_docid", rs.getString("cr_docid"));
				rst.put("cr_docseq", rs.getString("cr_docseq"));
				rst.put("cr_prjno", rs.getString("cr_prjno"));
				rst.put("dirpath", rs.getString("dirpath"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("selected_flag","0");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rsval.toArray();
			
			rsval = null;
			
			return rtObj;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## test_Tree2DAO.TreeList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## test_Tree2DAO.TreeList() Exception END ##");				
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
					ecamsLogger.error("## test_Tree2DAO.TreeList() connection release exception ##");
						ex3.printStackTrace();
				}
			}
		}
	}//end of TreeList() method statement
	
	public String fileDownChk(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  rsval       = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
	
			strQuery.append("select CR_TEAMCD from cmr9900 \n"); 
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("and   cr_locat = '00' \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();            
			
			rsval = null;
			if (rs.next()){
				rsval = rs.getString("CR_TEAMCD");
			}//end of while-loop statement
				
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rsval;
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0300.fileDownChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0300.fileDownChk() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0300.fileDownChk() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0300.fileDownChk() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0300.fileDownChk() connection release exception ##");
						ex3.printStackTrace();
					}
				}
			}
			
		}//end of TreeList() method statement		
		
	
	public String gyulCheck(String syscd,String reqcd,String ccbyn,String UserID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rsval       = -1;
		UserInfo		  userInfo    = new UserInfo();
		HashMap<String,String>	rData = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
	
			Object[] uInfo = userInfo.getUserInfo(UserID);
			userInfo = null;
	    	rData = (HashMap<String, String>) uInfo[0];
	    	
			strQuery.append("select cm_gubun,cm_position from cmm0060 \n"); 
			strQuery.append("where cm_syscd= ? \n");
			strQuery.append("and cm_reqcd= ? \n");
			if (rData.get("cm_manid").toUpperCase().equals("Y")){
				strQuery.append("and cm_manid='1' \n");
			}
			else{
				strQuery.append("and cm_manid='2' \n");
			}
			rData = null;
			uInfo = null;
			
			if (ccbyn.toUpperCase().equals("Y")){
				strQuery.append("and (cm_rsrccd is null or (cm_rsrccd is not null and cm_rsrccd='Y')) \n");
			}else{
				strQuery.append("and cm_rsrccd is null \n");
			}
			strQuery.append("and cm_gubun<>'1' \n");
			
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, syscd);
			pstmt.setString(2, reqcd);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();            
			
			rsval = -1;
			
			while (rs.next()){
				++rsval;
				if (rs.getString("cm_gubun").equals("4")) {
					if (rs.getString("cm_position").indexOf("16")>=0) {
						rsval = 99;
						break;
					}
				}
			}//end of while-loop statement
		
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			return Integer.toString(rsval);
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0300.gyulCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0300.gyulCheck() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0300.gyulCheck() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0300.gyulCheck() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0300.gyulCheck() connection release exception ##");
						ex3.printStackTrace();
				}
			}
		}
	}//end of TreeList() method statement	
	
	
	public String acptNoChk(String acptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int				  rsval       = -1;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.append("select count(*) as cnt from cmr1000 \n"); 
			strQuery.append("where cr_acptno= ? 				 \n");
			strQuery.append("and cr_qrycd in ('31','32') 		 \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptNo.replaceAll("-", ""));
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();            
			
			rsval = -1;
			
			if (rs.next()){
				rsval = rs.getInt("cnt");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
		
			return Integer.toString(rsval);
		
	} catch (SQLException sqlexception) {
		sqlexception.printStackTrace();
		ecamsLogger.error("## Cmr0300.acptNoChk() SQLException START ##");
		ecamsLogger.error("## Error DESC : ", sqlexception);	
		ecamsLogger.error("## Cmr0300.acptNoChk() SQLException END ##");			
		throw sqlexception;
	} catch (Exception exception) {
		exception.printStackTrace();
		ecamsLogger.error("## Cmr0300.acptNoChk() Exception START ##");				
		ecamsLogger.error("## Error DESC : ", exception);	
		ecamsLogger.error("## Cmr0300.acptNoChk() Exception END ##");				
		throw exception;
	}finally{
		if (strQuery != null) 	strQuery = null;
		if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
		if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		if (conn != null){
			try{
				ConnectionResource.release(conn);
			}catch(Exception ex3){
				ecamsLogger.error("## Cmr0300.acptNoChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of TreeList() method statement	
	
	
	public String request_Check_Out(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData,ArrayList<HashMap<String,Object>> gyulData,HashMap<String,String> ccbynData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		CodeInfo		  codeInfo	  = new CodeInfo();
		String			  AcptNo	  = null;
		int				  i;
		HashMap<String,String>	rData = null;
		StringReader sr = null;
	
		
		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);
			
	        for (i=0;i<chkOutList.size();i++){
	        	strQuery.setLength(0);
	                
	        	strQuery.append("select a.CR_STATUS, a.cr_docfile,b.cm_username,c.cm_codename \n");
				strQuery.append("from cmr0030 a,cmm0040 b,cmm0020 c \n");
				strQuery.append("where cr_docid = ? \n");
				strQuery.append("and a.cr_editor=b.cm_userid \n");
				strQuery.append("and c.cm_macode='CMR0020' and c.cm_micode=a.cr_status \n");
				
				pstmt = conn.prepareStatement(strQuery.toString());
				
				pstmt.setString(1, chkOutList.get(i).get("cr_docid"));
				
				rs = pstmt.executeQuery();
				
				if (rs.next()){
					if (!rs.getString("CR_STATUS").equals("0")){
						throw new Exception(rs.getString("cr_docfile")+" 파일은 " + rs.getString("cm_username") +"님이 " + rs.getString("cm_codename") +" 작업중입니다.");
					}
				}
				rs.close();
				pstmt.close();
			}
			
			
			AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));
			autoseq = null;
			
			strQuery.setLength(0);
			
			strQuery.append("select count(*) as acptnocnt from cmr1000 \n");
			strQuery.append("where cr_acptno= ? \n");
			
			
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()){
				i = rs.getInt("acptnocnt");
			}        	
			
			rs.close();
			pstmt.close();
			
			if (i>0){
				throw new Exception("["+ AcptNo +"]동일한 일련번호로 신청건이 있습니다.");
			}
			
			
			strQuery.setLength(0);
			strQuery.append("insert into cmr1000 ");
			strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, ");
			strQuery.append("CR_PASSOK,CR_PASSCD,CR_EDITOR,CR_SAYU,CR_PRJNO,cr_prjname,cr_docno) values ( ");
			strQuery.append("?, ?, ?, ?, sysdate, '0', ?, ?, '0', ?, ? , ? , ?, ? ,'') ");
			
			
			
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(pstmtcount++, AcptNo);
			pstmt.setString(pstmtcount++, etcData.get("SysCD"));
			pstmt.setString(pstmtcount++, etcData.get("SysGB"));
			pstmt.setString(pstmtcount++, etcData.get("JobCD"));
			
			
			Object[] uInfo = userInfo.getUserInfo(etcData.get("UserID"));
			rData = (HashMap<String, String>) uInfo[0];
			pstmt.setString(pstmtcount++, rData.get("teamcd"));
			rData = null;
			uInfo = null;
			
			pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
			
			uInfo = codeInfo.getCodeInfo("REQUEST", "", "n");
			codeInfo = null;
			for (i=0;i<uInfo.length;i++){
				rData = (HashMap<String, String>) uInfo[i];
				if (rData.get("cm_micode").equals(etcData.get("ReqCD"))){
					pstmt.setString(pstmtcount++, rData.get("cm_codename"));
					rData = null;
					break;
				}
				rData = null;
			}
			uInfo = null;
			
			pstmt.setString(pstmtcount++, etcData.get("UserID"));
			pstmt.setString(pstmtcount++, etcData.get("Sayu"));
			pstmt.setString(pstmtcount++, etcData.get("PrjNo"));
			pstmt.setString(pstmtcount++, etcData.get("PrjName"));        			
			
			pstmt.executeUpdate();
			
			pstmt.close();
			
			for (i=0;i<chkOutList.size();i++){
				strQuery.setLength(0);
				strQuery.append("insert into cmr1100 ");
				strQuery.append("(CR_ACPTNO,CR_SERNO,CR_STATUS,CR_QRYCD,CR_DOCID,CR_PRJNO,CR_VERSION, ");
				strQuery.append("CR_BASENO,CR_PIDUP,CR_PUTCODE,CR_PCDIR,CR_EDITOR,CR_CCBYN,CR_UNITTIT,CR_DOCSEQ ) ");
				strQuery.append("values (?, ?, '0', ?, ?, ? ,? , ? ,'','', ?, ?, ?, ?, ? ) ");
				            	
				pstmtcount = 1;
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setInt(pstmtcount++, i+1);
				pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_docid"));         	
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_prjno"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_lstver"));
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("dirpath"));
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_ccbyn"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("osayu"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_docseq"));
				
				pstmt.executeUpdate();
				pstmt.close();
			        
				strQuery.setLength(0);
				strQuery.append("update cmr0030 set ");
				if (etcData.get("ReqCD").equals("01") || etcData.get("ReqCD").equals("31")){
					strQuery.append("cr_status='4', ");
				}
				else if (etcData.get("ReqCD").equals("03") || etcData.get("ReqCD").equals("33")){
					strQuery.append("cr_status='C', ");
				}
				else if (etcData.get("ReqCD").equals("04") || etcData.get("ReqCD").equals("06")
						 || etcData.get("ReqCD").equals("34") || etcData.get("ReqCD").equals("36")){
					strQuery.append("cr_status='7', ");
				}
				else if (etcData.get("ReqCD").equals("11") || etcData.get("ReqCD").equals("41")){
					strQuery.append("cr_status='6', ");
				}
			
				strQuery.append("cr_editor= ? ");
				strQuery.append("where CR_DOCID= ? ");
				
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmtcount = 1;
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
				pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_docid"));
			
				pstmt.executeUpdate();
				pstmt.close();
			}
				
			if (ccbynData.size() > 0){
				
				strQuery.setLength(0);
				strQuery.append("insert into cmr1002 ");
				strQuery.append("(CR_ACPTNO,CR_STATUS,CR_QRYCD,CR_NUMBER,CR_TITLE,CR_EDITOR,CR_PRCDATE,CR_MD,CR_STORY) ");
				strQuery.append("values ( ? , '0', ?, ?, ?, ?, sysdate, ?, ? ) ");
				
				pstmtcount = 1;
				pstmt = conn.prepareStatement(strQuery.toString());
				
				
				pstmt.setString(pstmtcount++, AcptNo);
				pstmt.setString(pstmtcount++, ccbynData.get("reqgbn"));
				if (ccbynData.get("reqno") == null){
					pstmt.setString(pstmtcount++, "");
				}
				else{
					if (ccbynData.get("reqno").equals("")){
						pstmt.setString(pstmtcount++, "");
					}
					else{
						pstmt.setString(pstmtcount++, ccbynData.get("reqno").replaceAll("-",""));
					}
				}
				pstmt.setString(pstmtcount++, ccbynData.get("title"));
				pstmt.setString(pstmtcount++, etcData.get("UserID"));
				pstmt.setString(pstmtcount++, ccbynData.get("md"));
				sr = new StringReader(ccbynData.get("content"));
				pstmt.setCharacterStream(pstmtcount++, sr, ccbynData.get("content").length());
				
			
				pstmt.executeUpdate();
				pstmt.close();
			}
			Cmr0200 cmr0200 = new Cmr0200();
			String retMsg = null;
        	if (gyulData.size() > 0) {
        		retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("SysCD"),etcData.get("ReqCD"),etcData.get("UserID"),true,gyulData,conn);
        	} else {
        		retMsg = cmr0200.request_Confirm(AcptNo,etcData.get("SysCD"),etcData.get("ReqCD"),etcData.get("UserID"),false,gyulData,conn);
        	}
        	if (!retMsg.equals("OK")) {
        		AcptNo = "ERROR결재정보작성 중 오류가 발생하였습니다.";
        		conn.rollback();
        	} else {
        		conn.commit();
        	}
        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;
        	
			userInfo = null;
			
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
					ecamsLogger.error("## Cmr0300.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}				
			}
			ecamsLogger.error("## Cmr0300.request_Check_Out() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0300.request_Check_Out() SQLException END ##");			
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
					ecamsLogger.error("## Cmr0300.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}				
			}			
			ecamsLogger.error("## Cmr0300.request_Check_Out() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0300.request_Check_Out() Exception END ##");				
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
					ecamsLogger.error("## Cmr0300.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
	}	
	
	public String confSelect(String SysCd,String ReqCd,String RsrcCd,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String            retMsg      = "";
		try {
		    
			conn = connectionContext.getConnection();
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
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());  
			rs = pstmt.executeQuery();
			while (rs.next()){
				retMsg = "N";
				if (!rs.getString("cm_gubun").equals("1") && !rs.getString("cm_gubun").equals("2")) {            		
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
		    rs.close();
		    pstmt.close();
		    conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			
		    return retMsg;
				
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0300.confSelect() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0300.confSelect() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0300.confSelect() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0300.confSelect() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0300.confSelect() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public Object[] getDocFileList(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[]		  rtObj		  = null;
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			conn = connectionContext.getConnection();
			
			strQuery.append("select A.CR_SERNO,A.CR_DOCID,replace(A.CR_PCDIR,'\\','\\\\') as CR_PCDIR2,\n");
			strQuery.append("		A.CR_PCDIR,a.CR_VERSION,c.CR_DOCFILE,c.cr_methcd,c.cr_devstep \n");
			strQuery.append("  from cmr1100 A,CMR0030 C 	\n");
			strQuery.append(" Where A.cr_acptno= ? 		\n");
			strQuery.append("   AND A.cr_qrycd <> '05'	\n");
			strQuery.append("   AND nvl(A.cr_errcd,'ERR1')<>'0000' 	\n");
			strQuery.append("   AND A.cr_prcdate is null 			\n");
			strQuery.append("   AND A.CR_DOCID=C.CR_DOCID 			\n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
	//		pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			
	//	    ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			
			rs = pstmt.executeQuery();
			            
			rsval.clear();
			
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("CR_SERNO", rs.getString("CR_SERNO"));
				rst.put("CR_DOCID", rs.getString("CR_DOCID"));
				rst.put("CR_PCDIR", rs.getString("CR_PCDIR"));
				rst.put("CR_PCDIR2", rs.getString("CR_PCDIR2"));
				rst.put("CR_VERSION", rs.getString("CR_VERSION"));
				rst.put("CR_DOCFILE", rs.getString("CR_DOCFILE"));
				rst.put("cr_methcd", rs.getString("cr_methcd"));
				rst.put("cr_devstep", rs.getString("cr_devstep"));
				rst.put("sendflag","0");
				rst.put("errflag","0");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
		
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rsval.toArray();
			
			rsval = null;
			
			return rtObj;
	} catch (SQLException sqlexception) {
		sqlexception.printStackTrace();
		ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException START ##");
		ecamsLogger.error("## Error DESC : ", sqlexception);	
		ecamsLogger.error("## test_Tree2DAO.TreeList() SQLException END ##");			
		throw sqlexception;
	} catch (Exception exception) {
		exception.printStackTrace();
		ecamsLogger.error("## test_Tree2DAO.TreeList() Exception START ##");				
		ecamsLogger.error("## Error DESC : ", exception);	
		ecamsLogger.error("## test_Tree2DAO.TreeList() Exception END ##");				
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
				ecamsLogger.error("## test_Tree2DAO.TreeList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
		
	}//end of TreeList() method statement	
	
	public String getTmpDirConf(String pCode) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  rtString = null;
		
		try {
			conn = connectionContext.getConnection();
		
			strQuery.append("SELECT CM_DOWNIP,CM_DOWNPORT \n");
			strQuery.append("FROM cmm0012 \n");
			strQuery.append("WHERE cm_stno = 'ECAMS' \n");
			strQuery.append("AND cm_pathcd = ? \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, pCode);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()){
				rtString = rs.getString("CM_DOWNIP")+","+rs.getString("CM_DOWNPORT");
			}//end of while-loop statement
		
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtString;
		
	} catch (SQLException sqlexception) {
		sqlexception.printStackTrace();
		ecamsLogger.error("## SystemPath.getSysPath() SQLException START ##");
		ecamsLogger.error("## Error DESC : ", sqlexception);	
		ecamsLogger.error("## SystemPath.getSysPath() SQLException END ##");			
		throw sqlexception;
	} catch (Exception exception) {
		exception.printStackTrace();
		ecamsLogger.error("## SystemPath.getSysPath() Exception START ##");				
		ecamsLogger.error("## Error DESC : ", exception);	
		ecamsLogger.error("## SystemPath.getSysPath() Exception END ##");				
		throw exception;
	}finally{
		if (strQuery != null) 	strQuery = null;
		if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
		if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		if (conn != null){
			try{
				ConnectionResource.release(conn);
			}catch(Exception ex3){
				ecamsLogger.error("## SystemPath.getSysPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
		
	}//end of SelectUserInfo() method statement
	

	public Boolean setTranResult(String acptNo,String serNo,String ret) throws SQLException, Exception {
		Connection        conn       		 = null;
		PreparedStatement pstmt     	     = null;
		ResultSet         rs         		 = null;
		StringBuffer      strQuery  	     = new StringBuffer();
		ConnectionContext connectionContext  = new ConnectionResource();
		String 			  errCode    		 = "";
		int				  nerrCode 	 		 = 0;
		
		try {
			conn = connectionContext.getConnection();
			
			conn.setAutoCommit(false);
	
			nerrCode = Integer.parseInt(ret);
			
			if (nerrCode == -8){
				errCode = "SVER";
			}
			else if (nerrCode == -7){
				errCode = "EROR";
			}
			else{
				errCode = String.format("%04d", nerrCode);
			}
	
			strQuery.setLength(0);
			strQuery.append("update cmr1100 set cr_errcd = ? \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("and cr_serno= ? \n");
			
			pstmt = conn.prepareStatement(strQuery.toString());
			
			pstmt.setString(1, errCode);
			pstmt.setString(2, acptNo);
			pstmt.setInt(3, Integer.parseInt(serNo));
			
		    pstmt.executeUpdate();
		    pstmt.close();
		    conn.commit();
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
				ex3.printStackTrace();
			}				
		}			
			ecamsLogger.error("## Cmr0300.setTranResult() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmr0300.setTranResult() SQLException END ##");			
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
				ex3.printStackTrace();
			}				
		}			
			ecamsLogger.error("## Cmr0300.setTranResult() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmr0300.setTranResult() Exception END ##");				
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectUserInfo() method statement		
	
	public boolean callCmr9900_Str(String acptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
	
		int				  errcnt = 0;
		String			  szJobCD = "";
	
		try {
			conn = connectionContext.getConnection();
		
			strQuery.setLength(0);
			strQuery.append("select count(*) errcnt from cmr1100 \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("  and cr_errcd != '0000' \n");
			strQuery.append("  and cr_status <> '3' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptNo);
			rs = pstmt.executeQuery();
			errcnt = -1;
			if (rs.next()){
				errcnt = rs.getInt("errcnt");
			}
			
			if (errcnt != 0){
				throw new Exception("정상적으로 송수신하지  못한 파일이 있으니 목록에서 확인, 원인 조치 후 다시처리 하십시오.");
			}
			
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("select cr_team from cmr9900 \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("  and cr_locat = '00' \n");
			strQuery.append("  and cr_status = '0' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptNo);
			rs = pstmt.executeQuery();
			szJobCD = "";
			if (rs.next()){
				szJobCD = rs.getString("cr_team");
			}
			
			if (szJobCD == ""){
				throw new Exception("결재정보 오류입니다.");
			}
			rs.close();
			pstmt.close();
			
			strQuery.setLength(0);
			strQuery.append("Begin CMR9900_STR ( ");
			strQuery.append("?, ?, 'eCAMS자동처리', '9', '', '1' ); End;");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptNo);
			pstmt.setString(2, szJobCD);
			pstmt.executeUpdate();
			pstmt.close();
			
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
				ex3.printStackTrace();
			}				
		}			
		ecamsLogger.error("## Cmr0300.setTranResult() SQLException START ##");
		ecamsLogger.error("## Error DESC : ", sqlexception);	
		ecamsLogger.error("## Cmr0300.setTranResult() SQLException END ##");			
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
				ex3.printStackTrace();
			}				
		}			
		ecamsLogger.error("## Cmr0300.setTranResult() Exception START ##");				
		ecamsLogger.error("## Error DESC : ", exception);	
		ecamsLogger.error("## Cmr0300.setTranResult() Exception END ##");				
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
					ecamsLogger.error("## Cmr0300.setTranResult() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}		
	}//fileStr = fileStream.toString("EUC-KR");
	
}//end of Cmr0300 class statement
