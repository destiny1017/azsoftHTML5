
/*****************************************************************************************
	1. program ID	: eCmm21301.java
	2. create date	:
	3. auth		    :
	4. update date	: 09.04.15
	5. auth		    : No Name
	6. description	: eCmm2301
*****************************************************************************************/

package app.eCmm;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

//import app.common.LoggableStatement;
import app.common.SystemPath;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;




/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmm2301{
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

    public Object[] get_sql_Qry(String AcptNo,String Kind) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;	//cmm0200
		ResultSet         rs          = null;	//cmm0200
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;			  	rst	 	= null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {

			conn = connectionContext.getConnection();
			if(Kind.equals("2")){
			strQuery.append("select CM_ACPTNO,CM_GBNCD,CM_ACPTDATE,CM_TITLE,CM_EDITOR,CM_CONTENTS,CM_DEPTCD,CM_NOTIYN \n");
			strQuery.append("from cmm0200 			\n");
			strQuery.append("where cm_acptno=?  	\n");	//AcptNo
			strQuery.append("and cm_gbncd='2'		\n");
			}
			else{
			strQuery.append("select CM_ACPTNO,CM_GBNCD,CM_LASTDATE,CM_ANSTIT,CM_EDITOR,CM_ANSCONT,CM_SEQNO\n");
			strQuery.append("from cmm0210 			\n");
			strQuery.append("where cm_acptno=?  	\n");	//AcptNo
			strQuery.append("and cm_gbncd='3'		\n");
			}

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

			pstmt.setString(1, AcptNo);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
            rst = new HashMap<String, String>();
            if(Kind.equals("2")){
	            if (rs.next()){
					rst.put("CM_ACPTNO", rs.getString("CM_ACPTNO")); 		//신청번호
					rst.put("CM_GBNCD", rs.getString("CM_GBNCD")); 			//공지사항
					rst.put("CM_TITLE", rs.getString("CM_TITLE"));			//제목
					rst.put("CM_CONTENTS", rs.getString("CM_CONTENTS"));	//내용
					rst.put("CM_NOTIYN", rs.getString("CM_NOTIYN"));		//팝업공지구분 Y or N
					rst.put("CM_EDITOR", rs.getString("CM_EDITOR"));		//사용자ID
					rst.put("CM_DEPTCD", rs.getString("CM_DEPTCD"));
				}
            }
            else{
            	if (rs.next()){
    				rst.put("CM_ACPTNO", rs.getString("CM_ACPTNO")); 		//신청번호
    				rst.put("CM_GBNCD", rs.getString("CM_GBNCD")); 			//공지사항
    				rst.put("CM_TITLE", rs.getString("CM_ANSTIT"));			//제목
    				rst.put("CM_CONTENTS", rs.getString("CM_ANSCONT"));	//내용
    				rst.put("CM_EDITOR", rs.getString("CM_EDITOR"));		//사용자ID
    				rst.put("CM_SEQNO", rs.getString("CM_SEQNO"));
    			}
            }

            rs.close();
            pstmt.close();

            if (Kind == "2") {
				strQuery.setLength(0);
				strQuery.append("select count(*) as filecnt \n");
				strQuery.append("from cmm0220 			\n");
				strQuery.append("where cm_acptno=?  	\n");	//AcptNo
				strQuery.append("and cm_gbncd in('2','3')\n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());

				pstmt.setString(1, AcptNo);

				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

	            rs = pstmt.executeQuery();

	            if (rs.next()){
					rst.put("filecnt", rs.getString("filecnt")); 		//신청번호
	            }
	            rs.close();
	            pstmt.close();
            }
	        conn.close();
            rs = null;
            pstmt = null;
            conn = null;

			rtList.add(rst);
			rst = null;

			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;


			return rtObj;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2301.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.sql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2301.sql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2301_Qry() Exception END ##");
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
					ecamsLogger.error("## Cmm2301.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement


    public String get_update_Qry(String AcptNo,String UserID, String Title,String Txt_Body, String Kind) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		PreparedStatement 	pstmt2       = null;
		PreparedStatement 	pstmt3      = null;
		ResultSet         	rs          = null;
		ResultSet         	rs3         = null;

		StringBuffer      strQuery    = new StringBuffer();

		int				  Cnt         = 0;
		int 			  SeqNo       = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			//ecamsLogger.error("ABCDEFG12 : "+AcptNo+",  "+UserID+", "+ Title+", "+Txt_Body+", "+NotiYN);
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt \n");
			strQuery.append("from cmm0200 			\n");
			strQuery.append("where cm_acptno = ?  	\n");	//AcptNo
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			if (AcptNo == null){
				pstmt.setString(1, "");
			}
			else{
				pstmt.setString(1, AcptNo);
			}
			rs = pstmt.executeQuery();

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

					SeqNo = SeqNo + 1;
					AcptNo = rs3.getString("YYYYMM") + "000000".substring(0,6-Integer.toString(SeqNo).length()) + Integer.toString(SeqNo);

					rs3.close();
					pstmt3.close();

				//	ecamsLogger.error("CMM0220_UPDATE11111111 : " + AcptNo);

					strQuery.setLength(0);
					strQuery.append("INSERT INTO CMM0200 (cm_acptno,cm_gbncd,cm_acptdate,cm_title,cm_editor,cm_contents,cm_notiyn) \n");
					strQuery.append("values ( 	\n");
					strQuery.append("?, 		\n");				//AcptNo
					strQuery.append("'2', 		\n");				//1:공지사항 2:Q&A
					strQuery.append("SYSDATE, 	\n");				//cm_acptdate
					strQuery.append("?, 		\n"); 				//cm_title
					strQuery.append("?, 		\n"); 				//cm_editor
					strQuery.append("?, 		\n"); 				//cm_contents
					strQuery.append("'N') 		\n"); 				//cm_notiyn
					//strQuery.append("?)			\n");
				}else{
					if(Kind.equals("2")){
						strQuery.append("UPDATE CMM0200 SET \n");
						strQuery.append("cm_title = ?, \n"); 			//cm_title
						strQuery.append("cm_editor = ?, \n");        	//cm_editor
						strQuery.append("cm_contents = ?, \n");        	//cm_contents
						strQuery.append("cm_notiyn = 'N'  \n");        	//cm_notiyn
						//strQuery.append("cm_deptcd = ?		\n");
						strQuery.append("Where cm_acptno = ? \n");     	//cm_acptno
					}
					else{
						strQuery.append("UPDATE CMM0210 SET \n");
						strQuery.append("cm_anstit = ?, \n"); 			//cm_title
						strQuery.append("cm_editor = ?, \n");        	//cm_editor
						strQuery.append("cm_anscont = ?  \n");        	//cm_contents
						//strQuery.append("cm_deptcd = ?		\n");
						strQuery.append("Where cm_acptno = ? \n");     	//cm_acptno
					}
				}
				pstmt2 = conn.prepareStatement(strQuery.toString());

				//ecamsLogger.error(strQuery.toString());

				Cnt = 0;
	            if (rs.getString("cnt").equals("0")){
	            	pstmt2.setString(++Cnt, AcptNo);
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setString(++Cnt, Txt_Body);
	            	//pstmt2.setString(++Cnt, DEPTCD);

	            }else{
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setString(++Cnt, Txt_Body);
	            	//pstmt2.setString(++Cnt, DEPTCD);
	                pstmt2.setString(++Cnt, AcptNo);

	            }
	            pstmt2.executeUpdate();
	            pstmt2.close();
	    //        ecamsLogger.error("CMM0220_UPDATE : " + rtn_cnt);
			}

            rs.close();
            pstmt.close();

            conn.commit();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;


            return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2301.update_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.update_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2301.update_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## update_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.update_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of update_Qry() method statement


    public String get_reply_Qry(String AcptNo,int SeqNo,String UserID, String Title,String Txt_Body) throws SQLException, Exception {
		Connection        	conn        = null;
		PreparedStatement 	pstmt       = null;
		PreparedStatement 	pstmt2       = null;
		PreparedStatement 	pstmt3      = null;
		ResultSet         	rs          = null;
		ResultSet         	rs3         = null;

		StringBuffer      strQuery    = new StringBuffer();

		int				  Cnt         = 0;
		//int 			  SeqNo       = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {

			//ecamsLogger.error("ABCDEFG12 : "+AcptNo+",  "+UserID+", "+ Title+", "+Txt_Body+", "+NotiYN);
			conn = connectionContext.getConnection();
			strQuery.append("select count(*) as cnt \n");
			strQuery.append("from cmm0210 			\n");
			strQuery.append("where cm_acptno = ?  	\n");	//AcptNo
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

			pstmt.setString(1, AcptNo);
			rs = pstmt.executeQuery();

            if (rs.next()){
            	strQuery.setLength(0);
				if (rs.getString("cnt").equals("0")){
					strQuery.append("select to_char(SYSDATE,'yyyymm') AS YYYYMM, max(substr(cm_acptno,7,6)) as max from cmm0210 \n");
					strQuery.append("where substr(cm_acptno,1,6)=to_char(SYSDATE, 'yyyymm') \n");



					pstmt3 = conn.prepareStatement(strQuery.toString());
					rs3 = pstmt3.executeQuery();
					if (rs3.next()){
						if (rs3.getString("max") == null) SeqNo = 0;
						else SeqNo = Integer.parseInt(rs3.getString("max"));
					}

					SeqNo = SeqNo + 1;
					//AcptNo = rs3.getString("YYYYMM") + "000000".substring(0,6-Integer.toString(SeqNo).length()) + Integer.toString(SeqNo);

					rs3.close();
					pstmt3.close();

					//ecamsLogger.error("CMM0220_UPDATE11111111 : " + SeqNo);
					//ecamsLogger.error("CMM0220_UPDATE11111111 : " + AcptNo);

					strQuery.setLength(0);
					//strQuery.append("INSERT INTO CMM0210(CM_ACPTNO, CM_GBNCD, CM_ANSTIT, CM_ANSCONT, CM_EDITOR, CM_LASTDATE, CM_DEPTCD) \n");
					strQuery.append("INSERT INTO CMM0210(CM_ACPTNO, CM_GBNCD, CM_ANSTIT, CM_ANSCONT, CM_EDITOR, CM_LASTDATE, CM_SEQNO) \n");
					strQuery.append("values(	\n");
					strQuery.append("?, \n");				//AcptNo
					strQuery.append("'3', \n");				//1:공지사항 2:Q&A
					strQuery.append("?, \n"); 				//cm_title
					strQuery.append("?, \n"); 				//cm_contents
					strQuery.append("?, \n"); 				//cm_editor
					strQuery.append("SYSDATE, \n");				//cm_acptdate
					strQuery.append("? \n");				//cm_seqno
					strQuery.append(")		\n");
					//strQuery.append("?)		\n");				//cm_acptdate
					//strQuery.append("INSERT INTO CMM0210 (cm_acptno,cm_gbncd,cm_anstit,cm_anscont,cm_editor,cm_lastdate,cm_deptcd) values (?, '2', ?, ?, ?, SYSDATE,'aaa')");


				}else{
					strQuery.append("UPDATE CMM0210 SET \n");
					strQuery.append("cm_anstit = ?, \n"); 			//cm_title
					strQuery.append("cm_editor = ?, \n");        	//cm_editor
					strQuery.append("cm_anscont = ?, \n");        	//cm_contents
					strQuery.append("cm_seqno = ?    \n");        	//cm_seqno
					//strQuery.append("cm_deptcd = ?		\n");
					strQuery.append("Where cm_acptno = ? \n");     	//cm_acptno

				}


				//pstmt2 = new LoggableStatement(conn,strQuery.toString());
				pstmt2 = conn.prepareStatement(strQuery.toString());

	            if (rs.getString("cnt").equals("0")){
	            	pstmt2.setString(++Cnt, AcptNo);
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, Txt_Body);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setInt(++Cnt, SeqNo);
	            	//AcptNo.pstmt2.setString(++Cnt, DEPTCD);

	            }else{
	            	pstmt2.setString(++Cnt, Title);
	            	pstmt2.setString(++Cnt, UserID);
	            	pstmt2.setString(++Cnt, Txt_Body);
	            	pstmt2.setInt(++Cnt, SeqNo);
	            	//pstmt2.setString(++Cnt, DEPTCD);
	            	pstmt2.setString(++Cnt, AcptNo);

	            }
	            //ecamsLogger.error("Txt_Body : " + Txt_Body);
	            ////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
	            pstmt2.executeUpdate();
	            pstmt2.close();
	            //ecamsLogger.error("CMM0220_UPDATE : " + rtn_cnt);
			}

            rs.close();
            pstmt.close();

            conn.commit();
            conn.close();

            rs = null;
            pstmt = null;
            conn = null;


            return AcptNo;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2301.reply_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.reply_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2301.reply_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## reply_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.reply_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}



    public Object[] get_delete_Qry(String AcptNo, String Kind) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;			  	rst	 	= null;
		int               rtn_cnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();

		if(Kind.equals("2")){
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
			rtList.add(rst);
			rst = null;
	        }
	    else{
	    	strQuery.setLength(0);
			strQuery.append("delete from CMM0210 where cm_acptno = ? and cm_gbncd= '3' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
	        rtn_cnt = pstmt.executeUpdate();
	        pstmt.close();

	       	rst = new HashMap<String, String>();
			rst.put("ID","CMM0210_DELETE");
			rst.put("result",Integer.toString(rtn_cnt));
			rst.put("TASK","delete_Qry CMM0210");
			rtList.add(rst);
			rst = null;
	        }

			strQuery.setLength(0);
			strQuery.append("delete from CMM0220 where cm_acptno = ? and cm_gbncd= ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, Kind);
	        rtn_cnt = pstmt.executeUpdate();
	        pstmt.close();

			rst = new HashMap<String, String>();
			rst.put("ID","CMM0220_DELETE");
			rst.put("result",Integer.toString(rtn_cnt));
			rst.put("TASK","delete_Qry CMM0220");
			rtList.add(rst);
			rst = null;

			pstmt.close();
			conn.close();

			pstmt = null;
			conn = null;


			rtObj =  rtList.toArray();
			rtList.clear();
			rtList = null;


			return rtObj;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2301.delete_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.delete_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2301.delete_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## delete_Qry() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.delete_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement


	public String setDocFile(ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			for (int i=0;i<fileList.size();i++){
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMM0220 (cm_acptno,cm_gbncd,cm_seqno,cm_attfile,cm_svfile) values ( \n");
				strQuery.append(" ? , ? , ? , ? , ? ) \n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());

				//ecamsLogger.error(fileList.get(i).get("acptno"));
				//ecamsLogger.error(fileList.get(i).get("filegb"));
				//ecamsLogger.error(Integer.parseInt(fileList.get(i).get("seq")));
				//ecamsLogger.error(fileList.get(i).get("realName"));
				//ecamsLogger.error(fileList.get(i).get("saveName"));

				pstmt.setString(1, fileList.get(i).get("acptno"));
				pstmt.setString(2, fileList.get(i).get("filegb"));
				pstmt.setInt(3, Integer.parseInt(fileList.get(i).get("seq")));
				pstmt.setString(4, fileList.get(i).get("realName"));
				pstmt.setString(5, fileList.get(i).get("saveName"));
				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            pstmt.executeUpdate();
	            pstmt.close();
			}

			pstmt.close();

			conn.commit();
			conn.close();

			pstmt = null;
			conn = null;


			return "ok";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.setDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2301.setDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.setDocFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.setDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2301.setDocFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2301.setDocFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.setDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectUserInfo() method statement


	public String removeDocFile(HashMap<String,String> fileData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  realPath = "";


		try {
			SystemPath gPath = new SystemPath();
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			strQuery.setLength(0);
			strQuery.append("delete from CMM0220 \n");
			strQuery.append("where cm_acptno = ? \n");
			strQuery.append("and cm_gbncd = '2' \n");
			strQuery.append("and cm_seqno = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());


			pstmt.setString(1, fileData.get("cm_acptno"));
			pstmt.setInt(2,Integer.parseInt(fileData.get("cm_seqno")));

            pstmt.executeUpdate();
            pstmt.close();
            pstmt = null;

            realPath = gPath.getTmpDir_conn("23",conn);
            gPath = null;
            realPath = realPath + "/" + fileData.get("cm_acptno").substring(0, 4) + "/" + fileData.get("savename");

            File delFile = new File(realPath);
            delFile.delete();

            conn.commit();
            conn.close();
            conn = null;


    		return "ok";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.removeDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2301.removeDocFile() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2301.removeDocFile() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.removeDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmm2301.removeDocFile() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2301.removeDocFile() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm2301.removeDocFile() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectUserInfo() method statement

}//end of Cmm2101 class statement
