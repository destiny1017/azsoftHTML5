
/*****************************************************************************************
	1. program ID	: eCmm2201.java
	2. create date	:
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: Cmm2201
*****************************************************************************************/

package app.eCmm;

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




/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm2201{
    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * project 등록
	 * @param  pPrjno,pDocname,pDocPath
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */

    public ArrayList<HashMap<String, String>> sql_Qry(String AcptNo,String RsrcName) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;	//cmm0200
		PreparedStatement pstmt2      = null;	//cmm0220
		ResultSet         rs          = null;	//cmm0200
		ResultSet         rs2         = null; 	//cmm0220
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select CM_ACPTNO,CM_GBNCD,CM_ACPTDATE,CM_TITLE,CM_EDITOR,CM_CONTENTS \n");
			strQuery.append("from cmm0200 			\n");
			strQuery.append("where cm_acptno=?  	\n");	//AcptNo
			strQuery.append("and cm_gbncd='2'		\n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.setString(1, AcptNo);
            rs = pstmt.executeQuery();
            rsval.clear();
            if (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("CM_ACPTNO", rs.getString("CM_ACPTNO")); 		//신청번호
				rst.put("CM_GBNCD", rs.getString("CM_GBNCD")); 			//Q/A게시판
				rst.put("CM_TITLE", rs.getString("CM_TITLE"));			//제목
				rst.put("CM_CONTENTS", rs.getString("CM_CONTENTS"));	//내용
				rst.put("CM_EDITOR", rs.getString("CM_EDITOR"));		//사용자ID

				strQuery.setLength(0);
				strQuery.append("select CM_ATTFILE from cmm0220 where  \n");
				strQuery.append("cm_acptno= ? \n"); 					//cm_acptno
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("CM_ACPTNO"));
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					rst.put("CM_ATTFILE", rs2.getString("CM_ATTFILE"));	//첨부파일명
				}else{
					rst.put("CM_ATTFILE", "");
				}
				rs2.close();
				pstmt2.close();

				rsval.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2201.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2201.sql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2201.sql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2201() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2201.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement


    public ArrayList<HashMap<String, String>> update_Qry(String AcptNo,String UserID, String Title,String Txt_Body,String attfile) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		PreparedStatement 	pstmt2      = null;
		PreparedStatement 	pstmt3      = null;
		ResultSet         	rs          = null;
		ResultSet         	rs2         = null;
		ResultSet         	rs3         = null;

		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst	  = null;
		int               rtn_cnt     = 0;
		int               rtn_cnt2    = 0;
		int				  Cnt         = 0;
		int 			  SeqNo       = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			//ecamsLogger.error("ABCDEFG12 : "+AcptNo+",  "+UserID+", "+ Title+", "+Txt_Body+", "+attfile);
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt \n");
			strQuery.append("from cmm0200 			\n");
			strQuery.append("where cm_acptno = ?  	\n");	//AcptNo
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			if (AcptNo == null) pstmt.setString(1, "");
			else pstmt.setString(1, AcptNo);
			rs = pstmt.executeQuery();
            rsval.clear();
            if (rs.next()){
            	strQuery.setLength(0);
				if (rs.getString("cnt").equals("0")){
					strQuery.append("select to_char(SYSDATE,'yyyymm') AS YYYYMM, max(substr(cm_acptno,7,6)) as max from cmm0200 \n");
					strQuery.append("where substr(cm_acptno,1,6)=to_char(SYSDATE, 'yyyymm') \n");
					pstmt3 = conn.prepareStatement(strQuery.toString());
					rs3 = pstmt3.executeQuery();
					if (rs3.next()){
						if (rs3.getString("max") == null) SeqNo = 0;
						else SeqNo = Integer.parseInt(rs3.getString("max"));
					}

					rs3.close();
					pstmt3.close();

					SeqNo = SeqNo + 1;
					AcptNo = rs3.getString("YYYYMM") + "000000".substring(0,6-Integer.toString(SeqNo).length()) + Integer.toString(SeqNo);

					//ecamsLogger.error("CMM0220_UPDATE11111111 : " + SeqNo);
					//ecamsLogger.error("CMM0220_UPDATE11111111 : " + AcptNo);

					strQuery.setLength(0);
					strQuery.append("INSERT INTO CMM0200 (cm_acptno,cm_gbncd,cm_acptdate,cm_title,cm_editor,cm_contents) \n");
					strQuery.append("values ( 	\n");
					strQuery.append("?, 		\n");				//AcptNo
					strQuery.append("'2', 		\n");				//2:Q/A
					strQuery.append("SYSDATE, 	\n");				//cm_acptdate
					strQuery.append("?, 		\n"); 				//cm_title
					strQuery.append("?, 		\n"); 				//cm_editor
					strQuery.append("?)	 		\n"); 				//cm_contents


				}else{
					strQuery.append("UPDATE CMM0200 SET \n");
					strQuery.append("cm_title = ?, \n"); 			//cm_title
					strQuery.append("cm_editor = ?, \n");        	//cm_editor
					strQuery.append("cm_contents = ? \n");        	//cm_contents
					strQuery.append("Where cm_acptno = ? \n");     	//cm_acptno
				}

				pstmt2 = conn.prepareStatement(strQuery.toString());
	            Cnt = 0;
	            if (rs.getString("cnt").equals("0")){
	            	pstmt2.setString(++Cnt, AcptNo);
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setString(++Cnt, Txt_Body);

	            }else{
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setString(++Cnt, Txt_Body);
	            	pstmt2.setString(++Cnt, AcptNo);
	            }
	            rtn_cnt = pstmt2.executeUpdate();
	            pstmt2.close();
	            //ecamsLogger.error("CMM0220_UPDATE : " + rtn_cnt);

				rst = new HashMap<String, String>();
				rst.put("ID","CMM0200_UPDATE");
				rst.put("result",Integer.toString(rtn_cnt));
				rst.put("TASK","update_Qry CMM0200");
				rsval.add(rst);
				rst = null;

				//첨부파일 신규or수정
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmm0220 where  \n");
				strQuery.append("cm_acptno= ? \n"); 					//cm_acptno
				pstmt2 = conn.prepareStatement(strQuery.toString());
				if (AcptNo == null){
					pstmt2.setString(1, "");
				}else{
					pstmt2.setString(1, AcptNo);
				}

				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					strQuery.setLength(0);
					if (rs2.getString("cnt").equals("0")){
						strQuery.append("INSERT INTO CMM0220 (cm_acptno,cm_seqno,cm_gbncd,cm_attfile,cm_svfile) \n");
						strQuery.append("values ( 	\n");
						strQuery.append("?, 		\n"); 				//cm_acptno
						strQuery.append("'1', 		\n"); 				//cm_seqno
						strQuery.append("'2', 		\n"); 				//cm_gbncd
						strQuery.append("?, 		\n"); 				//cm_attfile
						strQuery.append("?) 		\n"); 				//cm_svfile
					}else{
						strQuery.append("UPDATE CMM0220 SET \n");
						strQuery.append("cm_attfile = ? \n"); 			//cm_attfile
						strQuery.append("Where cm_acptno = ? \n");     	//cm_acptno
					}
					pstmt3 = conn.prepareStatement(strQuery.toString());
					//pstmt3 = new LoggableStatement(conn,strQuery.toString());
					Cnt = 0;
					if (rs2.getString("cnt").equals("0")){
						pstmt3.setString(++Cnt, AcptNo);
						pstmt3.setString(++Cnt, attfile);
						pstmt3.setString(++Cnt, AcptNo);
					}else{
						pstmt3.setString(++Cnt, attfile);
						pstmt3.setString(++Cnt, AcptNo);
					}
		            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rtn_cnt2 = pstmt3.executeUpdate();
					//ecamsLogger.error("CMM0220_UPDATE : " + rtn_cnt2);
					rst = new HashMap<String, String>();
					rst.put("ID","CMM0220_UPDATE");
					rst.put("result",Integer.toString(rtn_cnt2));
					rst.put("TASK","update_Qry CMM0220");
					rsval.add(rst);
					rst = null;
				}
				rs2.close();
				pstmt2.close();
			}
            rs.close();
            pstmt.close();

            conn.commit();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

            return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2201.update_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2201.update_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2201.update_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## update_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2201.update_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of update_Qry() method statement



    public ArrayList<HashMap<String, String>> delete_Qry(String AcptNo,String UserID, String Title,String Txt_Body,String attfile) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst	  = null;
		int               rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

			strQuery.append("delete from CMM0220 where cm_acptno = ? and cm_gbncd= '2' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
	        rtn_cnt = pstmt.executeUpdate();
	        pstmt.close();

			rst = new HashMap<String, String>();
			rst.put("ID","CMM0220_DELETE");
			rst.put("result",Integer.toString(rtn_cnt));
			rst.put("TASK","delete_Qry CMM0220");
			rsval.add(rst);
			rst = null;

			strQuery.setLength(0);
			strQuery.append("delete from CMM0200 where cm_acptno = ? and cm_gbncd= '2' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
	        rtn_cnt = pstmt.executeUpdate();
	        pstmt.close();

			rst = new HashMap<String, String>();
			rst.put("ID","CMM0200_DELETE");
			rst.put("result",Integer.toString(rtn_cnt));
			rst.put("TASK","delete_Qry CMM0200");
			rsval.add(rst);
			rst = null;

			conn.close();
			conn = null;
			pstmt = null;

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2201.delete_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2201.delete_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2201.delete_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## delete_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rsval != null)	rsval = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2201.delete_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of delete_Qry() method statement
}//end of Cmm2201 class statement
