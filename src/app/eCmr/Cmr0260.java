/*****************************************************************************************
	1. program ID	: Cmr0260.java
	2. create date	: 2012.10.15
	3. auth		    : no name
	4. update date	:
	5. auth		    :
	6. description	: 산출물등록 사전점검
*****************************************************************************************/
package app.eCmr;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.common.LoggableStatement;
import app.common.SystemPath;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author no name
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmr0260{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/** 등록된 산출물조회
	 * @param etcData [AcptNo:신청번호 , UserId:로그인사용자ID]
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getDocList(HashMap<String,String> etcData) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			SystemPath systempath = new SystemPath();
			String tmpDir = systempath.getTmpDir_conn("22", conn);
			systempath = null;

//			CR_ACPTNO         CHAR (12) NOT NULL,
//			CR_STATUS         CHAR (1),
//			CR_DOCTYPE        VARCHAR2 (5)  NOT NULL,
//			CR_DOCNAME        VARCHAR2 (400),
//			CR_DOCFILENM      VARCHAR2 (200)
//			CR_QAMSG          VARCHAR2 (1000),
//			CR_RESULTMSG      VARCHAR2 (1000),
//
			strQuery.setLength(0);
			strQuery.append("select cr_status,cr_serno,cr_doctype,cr_docname,cr_docfilenm,cr_qamsg,cr_resultmsg \n");
			strQuery.append("  from cmr2600 \n");
			strQuery.append(" where cr_acptno=? \n");
			strQuery.append(" order by cr_serno \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, etcData.get("AcptNo"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("cr_serno", rs.getString("cr_serno"));
				rst.put("cr_doctype", rs.getString("cr_doctype"));
				rst.put("cr_docname", rs.getString("cr_docname"));
				rst.put("cr_docfilenm", rs.getString("cr_docfilenm"));
				rst.put("cr_qamsg", rs.getString("cr_qamsg"));
				rst.put("cr_resultmsg", rs.getString("cr_resultmsg"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;
			//ecamsLogger.error(rsval.toString());

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.getDocList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.getDocList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.getDocList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.getDocList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)  rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.getDocList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDocList() method statement


	public String setQaMSG(ArrayList<HashMap<String,String>> QaMSG,String AcptNo) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			for (int i=0 ; i<QaMSG.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("update cmr2600 set cr_qamsg=? \n");
				strQuery.append(" where cr_acptno=? \n");
				strQuery.append("   and cr_doctype=? \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, QaMSG.get(i).get("cr_qamsg"));
				pstmt.setString(2, AcptNo);
				pstmt.setString(3, QaMSG.get(i).get("cr_doctype"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
			}
            conn.close();
            pstmt = null;
            conn = null;

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setQaMSG() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.setQaMSG() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setQaMSG() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.setQaMSG() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.setQaMSG() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setQaMSG() method statement


	public String setResultMSG(ArrayList<HashMap<String,String>> ResultMSG,String AcptNo) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			for (int i=0 ; i<ResultMSG.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("update cmr2600 set cr_resultmsg=? \n");
				strQuery.append(" where cr_acptno=? \n");
				strQuery.append("   and cr_doctype=? \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ResultMSG.get(i).get("cr_resultmsg"));
				pstmt.setString(2, AcptNo);
				pstmt.setString(3, ResultMSG.get(i).get("cr_doctype"));
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
			}
            conn.close();
            pstmt = null;
            conn = null;

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setResultMSG() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.setResultMSG() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setResultMSG() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.setResultMSG() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.setResultMSG() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setResultMSG() method statement

	public String setDocfile(ArrayList<HashMap<String,Object>> addDocFiles, String UserId, String AcptNo) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try{
			conn = connectionContext.getConnection();

	    	for(int i=0 ; i<addDocFiles.size() ; i++){
				strQuery.setLength(0);
				strQuery.append("select cr_doctype \n");
				strQuery.append("  from cmr2600 \n");
				strQuery.append(" where cr_acptno=? \n");
				strQuery.append("   and cr_doctype=? \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, addDocFiles.get(i).get("type").toString());
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()){
					rs.close();
					pstmt.close();

		    		strQuery.setLength(0);
		        	strQuery.append("update cmr2600 set CR_STATUS='0', CR_DOCNAME=?, \n");
		        	strQuery.append("                   CR_DOCFILENM=?, CR_EDITOR=? \n");
					strQuery.append(" where cr_acptno=? \n");
					strQuery.append("   and cr_doctype=? \n");
		        	//pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, addDocFiles.get(i).get("name").toString());
					pstmt.setString(2, addDocFiles.get(i).get("fileNM").toString());
					pstmt.setString(3, UserId);
					pstmt.setString(4, AcptNo);
					pstmt.setString(5, addDocFiles.get(i).get("type").toString());

				} else{
					rs.close();
					pstmt.close();

		    		strQuery.setLength(0);
		        	strQuery.append("insert into cmr2600 ");
		        	strQuery.append(" (CR_ACPTNO,CR_SERNO,CR_STATUS,CR_DOCTYPE,CR_DOCNAME,CR_DOCFILENM,CR_EDITOR) \n");
		        	strQuery.append("values (? , ? , '0' , ? , ? ,? , ?) \n");
		        	//pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt = new LoggableStatement(conn,strQuery.toString());
		        	pstmt.setString(1, AcptNo);
		        	pstmt.setInt(2, Integer.parseInt(addDocFiles.get(i).get("serno").toString()));
		        	pstmt.setString(3, addDocFiles.get(i).get("type").toString());
		        	pstmt.setString(4, addDocFiles.get(i).get("name").toString());
		        	pstmt.setString(5, addDocFiles.get(i).get("fileNM").toString());
		        	pstmt.setString(6, UserId);
				}
	        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
	    	}
	    	conn.close();
	    	
	    	return "OK";

		}  catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocfile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.setDocfile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocfile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.setDocfile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs != null)  try{rs.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.setDocfile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//setDocfile

	public String setDocfile(ArrayList<HashMap<String,Object>> addDocFiles, String UserId, String AcptNo, Connection conn) throws SQLException, Exception
	{
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		try{

	    	for(int i=0 ; i<addDocFiles.size() ; i++){
	    		strQuery.setLength(0);
				strQuery.append("select cr_doctype \n");
				strQuery.append("  from cmr2600 \n");
				strQuery.append(" where cr_acptno=? \n");
				strQuery.append("   and cr_doctype=? \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, AcptNo);
				pstmt.setString(2, addDocFiles.get(i).get("type").toString());
				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if (rs.next()){
					rs.close();
					pstmt.close();

		    		strQuery.setLength(0);
		        	strQuery.append("update cmr2600 set CR_STATUS='0', CR_DOCNAME=?, \n");
		        	strQuery.append("                   CR_DOCFILENM=?, CR_EDITOR=? \n");
					strQuery.append(" where cr_acptno=? \n");
					strQuery.append("   and cr_doctype=? \n");
		        	//pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, addDocFiles.get(i).get("name").toString());
					pstmt.setString(2, addDocFiles.get(i).get("fileNM").toString());
					pstmt.setString(3, UserId);
					pstmt.setString(4, AcptNo);
					pstmt.setString(5, addDocFiles.get(i).get("type").toString());

				} else{
		    		strQuery.setLength(0);
		        	strQuery.append("insert into cmr2600 ");
		        	strQuery.append(" (CR_ACPTNO,CR_SERNO,CR_STATUS,CR_DOCTYPE,CR_DOCNAME,CR_DOCFILENM,CR_EDITOR) \n");
		        	strQuery.append("values (? , ? , '0' , ? , ? ,? , ?) \n");

		        	//pstmt = conn.prepareStatement(strQuery.toString());
		        	pstmt = new LoggableStatement(conn,strQuery.toString());
		        	pstmt.setString(1, AcptNo);
		        	pstmt.setInt(2, Integer.parseInt(addDocFiles.get(i).get("serno").toString()));
		        	pstmt.setString(3, addDocFiles.get(i).get("type").toString());
		        	pstmt.setString(4, addDocFiles.get(i).get("name").toString());
		        	pstmt.setString(5, addDocFiles.get(i).get("fileNM").toString());
		        	pstmt.setString(6, UserId);
				}
	        	ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        	pstmt.executeUpdate();
	        	pstmt.close();
	    	}

	    	return "OK";

		}  catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocfile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.setDocfile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocfile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.setDocfile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)  try{rs.close();}catch (Exception ex1){ex1.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//setDocfile

	public String delDocFile(HashMap<String,String> etcData,String UserId,String AcptNo) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			File docfilenm = new File(etcData.get("fileNM"));
			if (!docfilenm.exists()){
				docfilenm = null;
				conn = null;
				throw new Exception("삭제대상 산출물["+etcData.get("fileNM")+"]이 존재하지 않습니다.\n 관리자에게 연락하여 주시기 바랍니다.[delDocFile]");
			}
//			strQuery.setLength(0);
//			strQuery.append("delete from cmr2600 \n");
//			strQuery.append(" where cr_acptno=? \n");
//			strQuery.append("   and cr_docfilenm=? \n");
//			strQuery.append("   and cr_doctype=? \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn, strQuery.toString());
//			pstmt.setString(1, AcptNo);
//			pstmt.setString(2, etcData.get("fileNM"));
//			pstmt.setString(3, etcData.get("type"));
//			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//        	if (pstmt.executeUpdate() > 0){
//        		if (docfilenm!=null) docfilenm.delete();
//        	}
//        	pstmt.close();

			strQuery.setLength(0);
			strQuery.append("update cmr2600 set cr_status='8', cr_docname='' ,cr_docfilenm='', cr_editor=? \n");
			strQuery.append(" where cr_acptno=? \n");
			strQuery.append("   and cr_docfilenm=? \n");
			strQuery.append("   and cr_doctype=? \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, UserId);
			pstmt.setString(2, AcptNo);
			pstmt.setString(3, etcData.get("fileNM"));
			pstmt.setString(4, etcData.get("type"));
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();

        	if (docfilenm!=null) docfilenm.delete();//물리적인 파일 삭제

        	pstmt.close();
            conn.close();

            pstmt = null;
            conn = null;

			return etcData.get("type");

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.delDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.delDocFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.delDocFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.delDocFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.delDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delDocFile() method statement

	public String getDocStatus(String AcptNo) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            reStr       = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select cr_status \n");
			strQuery.append("  from cmr2600 \n");
			strQuery.append(" where cr_acptno=? \n");
			strQuery.append(" order by cr_serno \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, AcptNo);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			while (rs.next()){
//				if(rs.getString("cr_status").equals("0")){//QA 점검 진행 중
//				} else if (rs.getString("cr_status").equals("3")){//QA가 사용자에게 재등록 요청한 상태
//				} else if (rs.getString("cr_status").equals("9")){//QA 점검완료
//				}
				if (reStr.length() == 0) reStr = rs.getString("cr_status");
				else reStr = reStr + "," + rs.getString("cr_status");
			}
			rs.close();
			pstmt.close();
            conn.close();
            rs = null;
            pstmt = null;
            conn = null;

			return reStr;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.getDocStatus() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.getDocStatus() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.getDocStatus() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.getDocStatus() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.getDocStatus() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDocStatus() method statement

	public String setDocStatus(String UserId, String AcptNo, String Status) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			String url = "";
			String acptdate = "";
			String editor = "";
			String qauserid = "";

			if (Status.equals("0")){//재등록완료시 재등록통보한 QA id 조회
				strQuery.setLength(0);
				strQuery.append("select distinct cr_editor \n");
				strQuery.append("from cmr2600 where cr_acptno = ? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
				rs = pstmt.executeQuery();
				if (rs.next()){
					qauserid = rs.getString("cr_editor");
				}
				rs.close();
				pstmt.close();
			}

			strQuery.setLength(0);
			strQuery.append("update cmr2600 set cr_status=?, cr_editor=? \n");
			strQuery.append(" where cr_acptno=? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, Status);
			pstmt.setString(2, UserId);
			pstmt.setString(3, AcptNo);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
        	pstmt.executeUpdate();
        	pstmt.close();

            if (!Status.equals("9")){
    			strQuery.setLength(0);
    			strQuery.append("select to_char(cr_acptdate,'yyyy/mm/dd') acptdate, cr_editor \n");
    			strQuery.append("from cmr1000 where cr_acptno = ? \n");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			pstmt.setString(1, AcptNo);
    			rs = pstmt.executeQuery();
    			if (rs.next()){
    				acptdate = rs.getString("acptdate");
    				editor = rs.getString("cr_editor");
    			}
    			rs.close();
    			pstmt.close();

            	//형상관리 URL 조회
    			strQuery.setLength(0);
    			strQuery.append("select cm_url \n");
    			strQuery.append("  from cmm0010 \n");
    			strQuery.append(" where cm_stno='ECAMS' \n");
    			pstmt = conn.prepareStatement(strQuery.toString());
    			rs = pstmt.executeQuery();
    			if (rs.next()){
    				//재등록완료[0]시 재등록통보한 QA에게 보내는 메시지이므로 QA id로 셋팅
    				if (Status.equals("0")) url = rs.getString("cm_url")+"/ecams_win_flex/eCmr0250.jsp?ReqNo="+AcptNo+"&UserId="+qauserid;
    				//재등록통보[3]시 재등록할 신청자에게 보내는 메시지이므로 신청자editor로 셋팅
    				else url = rs.getString("cm_url")+"/ecams_win_flex/eCmr0250.jsp?ReqNo="+AcptNo+"&UserId="+editor;
    				//http://192.168.12.53:8080/ecams_win_flex/eCmr0250.jsp?ReqNo=201204000620&UserId=MASTER
    			}
    			rs.close();
    			pstmt.close();

                strQuery.setLength(0);
                if (Status.equals("0")){//산출물 재등록완료 통보 (신청자[UserId]->QA)
                	strQuery.append("INSERT INTO CMR9910 \n");
                	strQuery.append("  (cr_conusr, cr_senddate, cr_status, cr_acptno, cr_sendusr,cr_sgnmsg,cr_title,cr_linkurl)   \n");
                	strQuery.append("  (select distinct ?, SYSDATE, '0', ?, ?,                                                    \n");
                	strQuery.append("        '1. 신청일 : ' || ? || chr(13) ||                                                      \n");
                	strQuery.append("        '2. 신청번호 : ' || substr(?,1,4) || '-' || substr(?,5,2)                              \n");
                	strQuery.append("                       || '-' || substr(?,7,6) || Chr(13) ||                                 \n");
                	strQuery.append("        '3. 신청내용 : 체크인신청' || Chr(13) ||                                                 \n");
                	strQuery.append("                       '      에 대한 산출물 재등록 완료가 되었습니다.' || Chr(13) ||                 \n");
                	strQuery.append("                       '      형상관리시스템(eCAMS)에 접속하여 확인하여 주시기 바랍니다.',              \n");
                	strQuery.append("                       '형상관리시스템 산출물 재등록 완료', ?                                    \n");
                	strQuery.append("   FROM CMM0040 A,CMM0043 B                                                                  \n");
                	strQuery.append("  WHERE B.CM_USERID=A.CM_USERID                                                              \n");
                	strQuery.append("    AND A.CM_ACTIVE='1'                                                                      \n");
                	strQuery.append("    AND B.CM_USERID = ?)                                                                     \n");
                } else if (Status.equals("3")){//산출물 재등록요청 통보 (QA[UserId]->신청자)
                	strQuery.append("INSERT INTO CMR9910 \n");
                	strQuery.append("  (cr_conusr, cr_senddate, cr_status, cr_acptno, cr_sendusr,cr_sgnmsg,cr_title,cr_linkurl)   \n");
                	strQuery.append("  (select distinct ?, SYSDATE, '0', ?, ?,                                                    \n");
                	strQuery.append("        '1. 신청일 : ' || ? || chr(13) ||                                                      \n");
                	strQuery.append("        '2. 신청번호 : ' || substr(?,1,4) || '-' || substr(?,5,2)                              \n");
                	strQuery.append("                       || '-' || substr(?,7,6) || Chr(13) ||                                 \n");
                	strQuery.append("        '3. 신청내용 : 체크인신청' || Chr(13) ||                                                 \n");
                	strQuery.append("                       '      에 대한 산출물 재등록 요청이 있습니다.' || Chr(13) ||                 \n");
                	strQuery.append("                       '      형상관리시스템(eCAMS)에 접속하여 산출물을 재등록하여 주시기 바랍니다.',  \n");
                	strQuery.append("                       '형상관리시스템 산출물 재등록 요청', ?                                    \n");
                	strQuery.append("   FROM CMM0040 A,CMM0043 B                                                                  \n");
                	strQuery.append("  WHERE B.CM_USERID=A.CM_USERID                                                              \n");
                	strQuery.append("    AND A.CM_ACTIVE='1'                                                                      \n");
                	strQuery.append("    AND B.CM_USERID = ?)                                                                     \n");
                }
    			//pstmt = conn.prepareStatement(strQuery.toString());
    			pstmt = new LoggableStatement(conn,strQuery.toString());
    			if (Status.equals("0")) pstmt.setString(1, qauserid);//QA
    			else pstmt.setString(1, editor);//신청자
    			pstmt.setString(2, AcptNo);
    			//if (Status.equals("0")) pstmt.setString(3, UserId);
    			//else pstmt.setString(3, UserId);//QA
    			pstmt.setString(3, UserId);
    			pstmt.setString(4, acptdate);
    			pstmt.setString(5, AcptNo);
    			pstmt.setString(6, AcptNo);
    			pstmt.setString(7, AcptNo);
    			pstmt.setString(8, url);
    			pstmt.setString(9, UserId);
    			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            	pstmt.executeUpdate();
            	pstmt.close();
	        }
            conn.close();
            pstmt = null;
            conn = null;

			return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocStatus() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0260.setDocStatus() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0260.setDocStatus() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0260.setDocStatus() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0260.setDocStatus() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of setDocStatus() method statement

}//end of Cmr0260 class statement
