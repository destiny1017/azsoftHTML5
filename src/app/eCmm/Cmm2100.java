
/*****************************************************************************************
	1. program ID	: eCmm2100.java
	2. create date	:
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: eCmm2100
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


public class Cmm2100{
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
	* */

    public Object[] get_sql_Qry(String Cbo_Find, String Txt_Find, String strStD, String strEdD) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  	rst	 	= null;
        Object[]		 rtObj		  = null;
        Integer           Cnt         = 0;
		int				fcnt		=0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_acptno,a.cm_gbncd,to_char(a.cm_acptdate,'yyyy-mm-dd')as acptdate,a.cm_title, \n");
			strQuery.append("       a.cm_editor,a.cm_contents,a.cm_deptcd,a.cm_notiyn,b.cm_username, \n");
			strQuery.append("		nvl(a.cm_eddate,'') CM_EDDATE, nvl(a.cm_stdate,'') CM_STDATE  \n");
			strQuery.append("from cmm0200 a,cmm0040 b 						\n");
			strQuery.append("where a.cm_gbncd='1' 							\n");
			if (Cbo_Find.equals("00")){
				;
			}
			else if (Cbo_Find.equals("01")){
				strQuery.append("and instr(a.cm_title,?) > 0 \n");
			}
			else if (Cbo_Find.equals("02")){
		    	strQuery.append("and (instr(a.cm_title,?) > 0 or \n");
		    	strQuery.append("instr(a.cm_contents,?) > 0) \n");
			}
			else if (Cbo_Find.equals("03")){
				strQuery.append("  and to_char(a.CM_ACPTDATE,'yyyymmdd')>=?  	\n");
				strQuery.append("  and to_char(a.CM_ACPTDATE,'yyyymmdd')<=?  	\n");
			} else if (Cbo_Find.equals("03")){
				strQuery.append("and rownum<6                         \n");
			}
			strQuery.append("and cm_editor=b.cm_userid \n");
			strQuery.append("Order by cm_acptno desc  \n");

			pstmt = conn.prepareStatement(strQuery.toString());
	//		pstmt = new LoggableStatement(conn,strQuery.toString());
			if (Cbo_Find.equals("00")){
				;
			}
			else if (Cbo_Find.equals("01")){
				pstmt.setString(++Cnt, Txt_Find);
			}
			else if (Cbo_Find.equals("02")){
				pstmt.setString(++Cnt, Txt_Find);
				pstmt.setString(++Cnt, Txt_Find);
			}
			else if (Cbo_Find.equals("03")){
	            pstmt.setString(++Cnt, strStD);
	            pstmt.setString(++Cnt, strEdD);
			}

		//	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("rows", Integer.toString(rs.getRow()));    		// NO
				rst.put("CM_ACPTNO", rs.getString("CM_ACPTNO")); 		// 신청번호
				rst.put("CM_GBNCD", rs.getString("CM_GBNCD")); 			// 공지사항
				rst.put("CM_ACPTDATE", rs.getString("acptdate"));		// 입력일자
				rst.put("CM_TITLE", rs.getString("CM_TITLE"));			// 제목
				rst.put("CM_EDITOR", rs.getString("CM_EDITOR"));		// 작성자
				rst.put("CM_CONTENTS", rs.getString("CM_CONTENTS"));	// 내용
				rst.put("CM_NOTIYN", rs.getString("CM_NOTIYN"));		//
				rst.put("CM_DEPTCD", rs.getString("CM_DEPTCD"));		// 작성자조직
				rst.put("CM_USERNAME", rs.getString("CM_USERNAME"));	// 사용자이름
				if(rs.getString("CM_EDDATE") != null && rs.getString("CM_EDDATE") != ""){
					rst.put("CM_EDDATE", rs.getString("CM_EDDATE").substring(0,4)+"-"+rs.getString("CM_EDDATE").substring(4,6)+"-"+rs.getString("CM_EDDATE").substring(6,8));        // 20110206팝업종료일자 2011 + "-" + 02 + "-" + 06
				} else{
					rst.put("CM_EDDATE","");
				}
				if (rs.getString("CM_STDATE") != null && rs.getString("CM_STDATE") != ""){
					rst.put("CM_STDATE", rs.getString("CM_STDATE").substring(0,4)+"-"+rs.getString("CM_STDATE").substring(4,6)+"-"+rs.getString("CM_STDATE").substring(6,8));        // 20110206팝업종료일자 2011 + "-" + 02 + "-" + 06
				}else {
					rst.put("CM_STDATE","");
				}
				strQuery.setLength(0);
				strQuery.append("select count(*) as fcnt from cmm0220 where  \n");
				strQuery.append("cm_acptno= ? \n"); 	//cm_acptno
				strQuery.append("and cm_gbncd='1' \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("CM_ACPTNO"));

				fcnt = 0;
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					fcnt = rs2.getInt("fcnt");
				}

				rs2.close();
				pstmt2.close();

				rst.put("fileCnt", Integer.toString(fcnt));	//첨부파일명
				rtList.add(rst);
				rst = null;
			}
            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;

    		rtObj =  rtList.toArray();

    		rtList = null;

    		return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2100.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2100.sql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2100.sql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2100.sql_Qry() Exception END ##");
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
					ecamsLogger.error("## Cmm2100.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}



	}//end of sql_Qry() method statement

    public Object[] getFileList(String AcptNo) throws SQLException, Exception {
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
			strQuery.append("select CM_ACPTNO,CM_SEQNO,CM_ATTFILE,CM_SVFILE \n");
			strQuery.append("from cmm0220 			\n");
			strQuery.append("where cm_acptno=?  	\n");	//AcptNo
			strQuery.append("and cm_gbncd='1'		\n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.setString(1, AcptNo);
            rs = pstmt.executeQuery();
            rtList.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
				rst.put("cm_acptno", rs.getString("CM_ACPTNO")); 		//신청번호
				rst.put("cm_seqno", rs.getString("CM_SEQNO")); 			//공지사항
				rst.put("orgname", rs.getString("CM_ATTFILE"));			//제목
				rst.put("savename", rs.getString("CM_SVFILE"));			//내용
				strQuery.setLength(0);

				rtList.add(rst);
				rst = null;
			}

            rs.close();
            pstmt.close();
            conn.close();
			conn = null;
			pstmt = null;
			rs = null;
    		rtObj =  rtList.toArray();

    		rtList = null;

    		return rtObj;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm2101.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2101.sql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2101.sql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2101_Qry() Exception END ##");
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
					ecamsLogger.error("## Cmm2101.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement
}//end of Cmm2100 class statement
