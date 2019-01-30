
/*****************************************************************************************
	1. program ID	: eCmd2000.java
	2. create date	: 2010.03.
	3. auth		    :
	4. update date	:
	5. auth		    :
	6. description	: eCmd2000 [문서관리]->[프로젝트사용자일괄등록]
*****************************************************************************************/

package app.eCmd;

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
//import app.common.UserInfo;


/**
 * @author bigeyes
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class Cmd2000{
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

    //INSERT
	public String Cmd2000_INSERT(ArrayList<HashMap<String,String>> dataObj,
			HashMap<String,String> PRJUSER) throws SQLException, Exception{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String			  PrjNo       = "";
		String			  UserId      = "";
		String			  PrjName     = "";
		//String			  MethCd      = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			int		    i;

		    // -------  CMD0300 INSERT ------------------------------ //

	        for (i=0;i<dataObj.size();i++)
	        {
				PrjNo       = dataObj.get(i).get("PrjNo");
				UserId      = dataObj.get(i).get("UserId");
				PrjName     = dataObj.get(i).get("PrjName");

				strQuery.setLength(0);
				strQuery.append("SELECT COUNT(*) AS CNT  \n");
				strQuery.append("FROM   CMD0300          \n");
				strQuery.append("WHERE  CD_PRJNO = ?     \n");

				//pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt = conn.prepareStatement(strQuery.toString());
	            pstmt.setString(1, PrjNo);
	            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

	        	if (rs.next())
	        	{
	        		if (rs.getString("cnt").equals("0"))
	        		{
						strQuery.setLength(0);
						strQuery.append("INSERT INTO CMD0300         \n");
		                strQuery.append("           ( CD_PRJNO       \n");
		                strQuery.append("           , CD_PRJNAME     \n");
		                strQuery.append("           , CD_CREATDT     \n");
						strQuery.append("           , CD_LASTDT      \n");
		                strQuery.append("           , CD_STATUS      \n");
		                strQuery.append("           , CD_EDITOR      \n");
		                strQuery.append("           , CD_CREATOR     \n");
		                strQuery.append("           , CD_JOBUSEYN    \n");
	            		strQuery.append("           , CD_METHCD )    \n");
						strQuery.append("VALUES                      \n");
						strQuery.append("           ( ?              \n");
						strQuery.append("           , ?              \n");
						strQuery.append("           , SYSDATE        \n");
						strQuery.append("           , SYSDATE        \n");
						strQuery.append("           , '0'            \n");
						strQuery.append("           , ?              \n");
		                strQuery.append("           , ?              \n");
						strQuery.append("           , 'N'            \n");
	            		strQuery.append("           , '06' )         \n");

						//pstmt2 = new LoggableStatement(conn,strQuery.toString());
						pstmt2 = conn.prepareStatement(strQuery.toString());
			        	pstmt2.setString(1, PrjNo  );
			            pstmt2.setString(2, PrjName);
			            pstmt2.setString(3, PRJUSER.get("Editor") );
			            pstmt2.setString(4, PRJUSER.get("Editor") );
			            //ecamsLogger.error("=== 1 > PrjName" + new String(PrjName.getBytes("MS949")));
			            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			            pstmt2.executeUpdate();
			            pstmt2.close();
	        		}
				}
				rs.close();
				pstmt.close();


			    // -------  CMD0303 INSERT ------------------------------ //
			    strQuery.setLength(0);
	        	strQuery.append("SELECT COUNT(*) AS CNT   \n");
	        	strQuery.append("FROM   CMD0303           \n");
	        	strQuery.append("WHERE  CD_PRJNO = ?      \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
			    pstmt.setString(1, PrjNo);
	        	rs = pstmt.executeQuery();
	        	if (rs.next())
	        	{
	        		if (rs.getString("cnt").equals("0"))
	        		{
	                	strQuery.setLength(0);
	        			strQuery.append("INSERT INTO  CMD0303        \n");
	            		strQuery.append("           ( CD_DIRNAME     \n");
	            		strQuery.append("           , CD_PRJNO       \n");
	            		strQuery.append("           , CD_DOCSEQ      \n");
	            		strQuery.append("           , CD_DIRPATH )   \n");
	            		strQuery.append("VALUES                      \n");
	            		strQuery.append("           ( ?              \n");
	            		strQuery.append("           , ?              \n");
	            		strQuery.append("           , '00001'        \n");
	            		strQuery.append("           , ? )            \n");
	            		//pstmt2 = new LoggableStatement(conn,strQuery.toString());
	            		pstmt2 = conn.prepareStatement(strQuery.toString());
	            		pstmt2.setString(1, PrjName);
	            		pstmt2.setString(2, PrjNo  );
	            		pstmt2.setString(3, PrjName);
	            		//ecamsLogger.error( new String((((LoggableStatement)pstmt2).getQueryString()).getBytes("MS949")));
	            		pstmt2.executeUpdate();
	            		pstmt2.close();
	        		}
	        	}
	        	rs.close();
	        	pstmt.close();

			    // -------  CMD0304 INSERT ------------------------------ //
	        	if (UserId != "" && UserId != null)
	        	{
					strQuery.setLength(0);
					strQuery.append("SELECT COUNT(*) cnt     \n");
					strQuery.append("FROM   CMD0304          \n");
					strQuery.append("WHERE  CD_PRJNO   = ?   \n");
					strQuery.append("AND    CD_PRJUSER = ?   \n");
		        	pstmt = conn.prepareStatement(strQuery.toString());
				    pstmt.setString(1, PrjNo );
				    pstmt.setString(2, UserId);
		        	rs = pstmt.executeQuery();

		        	if (rs.next())
		        	{
		        		if (rs.getString("cnt").equals("0"))
		        		{
		            	    strQuery.setLength(0);
			            	strQuery.append("INSERT INTO  CMD0304      \n");
			            	strQuery.append("           ( CD_PRJNO     \n");
			            	strQuery.append("           , CD_PRJUSER   \n");
			            	//strQuery.append("           , CD_USERTEAM  \n");
			            	strQuery.append("           , CD_PRJJIK    \n");
			            	strQuery.append("           , CD_CREATDT   \n");
			            	strQuery.append("           , CD_EDITOR    \n");
			            	strQuery.append("           , CD_EDITYN    \n");
			            	strQuery.append("           , CD_DOCYN )   \n");
			            	strQuery.append("VALUES                    \n");
			            	strQuery.append("           (  ?           \n");
			            	strQuery.append("            , ?           \n");
			            	//strQuery.append("            , ?           \n");
			            	strQuery.append("            , ?           \n");
			            	strQuery.append("            , SYSDATE     \n");
			            	strQuery.append("            , ?           \n");
			            	strQuery.append("            , ?           \n");
			            	strQuery.append("            , ? )         \n");
			            	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
		                    pstmt2 = conn.prepareStatement(strQuery.toString());
		                    pstmt2.setString(1, PrjNo);
		                    pstmt2.setString(2, UserId);
		                    //pstmt2.setString(3, dataObj.get(i).get("TEAMCD"));
		                    pstmt2.setString(3, dataObj.get(i).get("PrjJik"));
		                    pstmt2.setString(4, PRJUSER.get("Editor"));
		                    if (dataObj.get(i).get("PrjJik").equals("PM") || dataObj.get(i).get("PrjJik").equals("SM"))
		                    	pstmt2.setString(5, "Y");
		                    else
		                    	pstmt2.setString(5, "N");

		                    if (dataObj.get(i).get("PrjJik").equals("RF"))
		                    	pstmt2.setString(6, "N");
		                    else
		                    	pstmt2.setString(6, "Y");
			            }
		        		else
		        		{
		            	    strQuery.setLength(0);
			            	strQuery.append("UPDATE CMD0304               \n");
			            	strQuery.append("SET    CD_PRJJIK  = ?        \n");
			            	strQuery.append("     , CD_CREATDT = SYSDATE  \n");
			            	strQuery.append("     , CD_EDITYN  = ?        \n");
			            	strQuery.append("     , CD_DOCYN   = ?        \n");
			            	strQuery.append("WHERE  CD_PRJNO   = ?        \n");
			            	strQuery.append("AND    CD_PRJUSER = ?        \n");

			            	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
		                    pstmt2 = conn.prepareStatement(strQuery.toString());
		                    pstmt2.setString(1, dataObj.get(i).get("PrjJik"));
		                    if (dataObj.get(i).get("PrjJik").equals("PM") || dataObj.get(i).get("PrjJik").equals("SM"))
		                    	pstmt2.setString(2, "Y");
		                    else
		                    	pstmt2.setString(2, "N");

		                    if (dataObj.get(i).get("PrjJik").equals("RF"))
		                    	pstmt2.setString(3, "N");
		                    else
		                    	pstmt2.setString(3, "Y");
		                    pstmt2.setString(4, PrjNo);
		                    pstmt2.setString(5, UserId);
		        		}
		        		//ecamsLogger.error( new String((((LoggableStatement)pstmt2).getQueryString()).getBytes("MS949")));
	            	    pstmt2.executeUpdate();
	            	    pstmt2.close();

			            rs.close();
			            pstmt.close();
		        	}
			    }
	        	conn.commit();
	        	conn.close();

	        	rs = null;
	        	pstmt = null;
	        	conn = null;
	        }

		    return "0";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0300.Cmd0300_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() SQLException END ##");
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
					ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
				    conn.commit();
				    conn.setAutoCommit(true);
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd2000.Cmd2000_INSERT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}//end of Cmd2000 class statement

