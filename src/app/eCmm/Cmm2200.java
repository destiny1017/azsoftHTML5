
/*****************************************************************************************
	1. program ID	: eCmm2200.java
	2. create date	:
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: eCmm2200
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


public class Cmm2200{
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

    public ArrayList<HashMap<String, String>> sql_Qry(int Cbo_Find, String Txt_Find) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.CM_ACPTNO,a.CM_GBNCD,a.CM_ACPTDATE,a.CM_TITLE,a.CM_EDITOR,a.CM_CONTENTS,b.CM_USERNAME \n");
			strQuery.append("from cmm0200 a,cmm0040 b				\n");
			strQuery.append("where a.cm_gbncd='2' 					\n");
			switch (Cbo_Find) {
				case 0:
					break;
				case 1:
					strQuery.append("and b.cm_username=?  \n");
					break;
			    case 2:
			    	strQuery.append("and instr(a.cm_title,?) > 0 \n");
			    	break;
			    case 3:
			    	strQuery.append("and (instr(a.cm_title,?) > 0 or \n");
			    	strQuery.append("instr(a.cm_contents,?) > 0) \n");
			    	break;
			}
			strQuery.append("and rtrim(a.cm_editor)=rtrim(b.cm_userid) \n");
			strQuery.append("Order by cm_acptno desc  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

			switch (Cbo_Find) {
				case 0:
					break;
				case 1:
					pstmt.setString(1, Txt_Find);
					break;
				case 2:
					pstmt.setString(1, Txt_Find);
					break;
				case 3:
					pstmt.setString(1, Txt_Find);
					pstmt.setString(2, Txt_Find);
					break;
			}
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
            while (rs.next()){
            	rst = new HashMap<String, String>();
            	rst.put("rows", Integer.toString(rs.getRow()));    		//NO
				rst.put("CM_ACPTNO", rs.getString("CM_ACPTNO")); 		//신청번호
				rst.put("CM_GBNCD", rs.getString("CM_GBNCD")); 			//Q/A:2
				rst.put("CM_ACPTDATE", rs.getString("CM_ACPTDATE"));	//입력일자
				rst.put("CM_TITLE", rs.getString("CM_TITLE"));			//제목
				rst.put("CM_EDITOR", rs.getString("CM_EDITOR"));		//작성자
				rst.put("CM_CONTENTS", rs.getString("CM_CONTENTS"));	//내용
				rst.put("CM_USERNAME", rs.getString("CM_USERNAME"));	//사용자이름


				strQuery.setLength(0);
				strQuery.append("select CM_ATTFILE,CM_SVFILE from cmm0220 where  \n");
				strQuery.append("cm_acptno= ? \n"); 					//cm_acptno
				pstmt2 = conn.prepareStatement(strQuery.toString());
				pstmt2.setString(1, rs.getString("CM_ACPTNO"));
				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					rst.put("CM_ATTFILE", rs2.getString("CM_ATTFILE"));	//첨부파일명
					rst.put("CM_SVFILE", rs2.getString("CM_SVFILE"));	//입력날짜
				}else{
					rst.put("CM_ATTFILE", "");	//첨부파일명
					rst.put("CM_SVFILE", "");	//입력날짜
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
			ecamsLogger.error("## Cmm2200.sql_Qry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm2200.sql_Qry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm2200.sql_Qry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm2200.sql_Qry() Exception END ##");
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
					ecamsLogger.error("## Cmm2200.sql_Qry() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sql_Qry() method statement
}//end of Cmm2200 class statement
