/*****************************************************************************************
	1. program ID	: Cmm0300_Copy.java
	2. create date	: 2008.12. 08
	3. auth		    : is.choi
	4. update date	: 2008.12.24
	5. auth		    : No Name
	6. description	: [관리자] -> [결재정보] -> [결재복사]
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import app.common.LoggableStatement;
import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmm0300_Copy{


    /**
     * Logger Class Instance Creation
     * logger
     */

    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * confCopy
	 * @param HashMap, ArrayList
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
    public String confCopy(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> confList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			String strSysCd = etcData.get("cm_syscd");
			String[] toSys = etcData.get("copysys").split(",");
			int i = 0;
			int j = 0;

			for (i=0;toSys.length>i;i++) {
				if (!strSysCd.equals(toSys[i]) || !etcData.get("cm_reqcd").equals(etcData.get("cm_toreq"))) {
					strQuery.setLength(0);
					strQuery.append("delete cmm0060                         \n");
					strQuery.append(" where cm_syscd=?                      \n");
					strQuery.append("   and cm_reqcd=decode(?,'ALL',cm_reqcd,?) \n");
					if (!etcData.get("cm_manid").equals("9") && !etcData.get("cm_reqcd").equals("ALL")) {
						strQuery.append("and cm_manid=?                     \n");
					}
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, etcData.get("cm_toreq"));
					pstmt.setString(3, etcData.get("cm_toreq"));
					if (!etcData.get("cm_manid").equals("9") && !etcData.get("cm_reqcd").equals("ALL")) pstmt.setString(4, etcData.get("cm_manid"));
					pstmt.executeUpdate();
					pstmt.close();
					
					if (etcData.get("cm_reqcd").equals("ALL")) {
						strQuery.setLength(0);
						strQuery.append("insert into cmm0060                \n");
						strQuery.append(" (cm_REQCD,cm_SEQNO,cm_syscd,      \n");
						strQuery.append("  cm_MANID,cm_NAME,CM_GUBUN,       \n");
						strQuery.append("  CM_COMMON,CM_BLANK,CM_HOLIDAY,   \n");
						strQuery.append("  CM_EMG,CM_EMG2,CM_ORGSTEP,       \n");
						strQuery.append("  CM_POSITION,CM_JOBCD,CM_RSRCCD,  \n");
						strQuery.append("  CM_PGMTYPE,CM_PRCSW)             \n");
						strQuery.append("(select cm_reqcd,cm_seqno, ?,      \n");
						strQuery.append("  cm_MANID,cm_NAME,CM_GUBUN,       \n");
						strQuery.append("  CM_COMMON,CM_BLANK,CM_HOLIDAY,   \n");
						strQuery.append("  CM_EMG,CM_EMG2,CM_ORGSTEP,       \n");
						strQuery.append("  CM_POSITION,CM_JOBCD,CM_RSRCCD,  \n");
						strQuery.append("  CM_PGMTYPE,CM_PRCSW              \n");
						strQuery.append("  from cmm0060                     \n");
						strQuery.append(" where cm_syscd=?                  \n");
						if (!etcData.get("cm_manid").equals("9") && !etcData.get("cm_reqcd").equals("ALL")) {
							strQuery.append("   and cm_manid=?              \n");
						}
						strQuery.append(")                                  \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(1, toSys[i]);
						pstmt.setString(2, strSysCd);
						if (!etcData.get("cm_manid").equals("9") && !etcData.get("cm_reqcd").equals("ALL")) 
							pstmt.setString(3, etcData.get("cm_manid"));
						//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						pstmt.executeUpdate();
						pstmt.close();
					} else {
						for (j=0;confList.size()>j;j++) {
							strQuery.setLength(0);
							strQuery.append("insert into cmm0060                \n");
							strQuery.append(" (cm_REQCD,cm_SEQNO,cm_syscd,      \n");
							strQuery.append("  cm_MANID,cm_NAME,CM_GUBUN,       \n");
							strQuery.append("  CM_COMMON,CM_BLANK,CM_HOLIDAY,   \n");
							strQuery.append("  CM_EMG,CM_EMG2,CM_ORGSTEP,       \n");
							strQuery.append("  CM_POSITION,CM_JOBCD,CM_RSRCCD,  \n");
							strQuery.append("  CM_PGMTYPE,CM_PRCSW)             \n");
							strQuery.append("(select ?, cm_seqno, ?,            \n");
							strQuery.append("  cm_MANID,cm_NAME,CM_GUBUN,       \n");
							strQuery.append("  CM_COMMON,CM_BLANK,CM_HOLIDAY,   \n");
							strQuery.append("  CM_EMG,CM_EMG2,CM_ORGSTEP,       \n");
							strQuery.append("  CM_POSITION,CM_JOBCD,CM_RSRCCD,  \n");
							strQuery.append("  CM_PGMTYPE,CM_PRCSW              \n");
							strQuery.append("  from cmm0060                     \n");
							strQuery.append(" where cm_syscd=? and cm_reqcd=?   \n");
							strQuery.append("   and cm_manid=? and cm_seqno=?)  \n");
							pstmt = conn.prepareStatement(strQuery.toString());
							//pstmt = new LoggableStatement(conn,strQuery.toString());
							pstmt.setString(1, etcData.get("cm_toreq"));
							pstmt.setString(2, toSys[i]);
							pstmt.setString(3, strSysCd);
							pstmt.setString(4, etcData.get("cm_reqcd"));
							pstmt.setString(5, confList.get(j).get("cm_manid"));
							pstmt.setInt(6, Integer.parseInt(confList.get(j).get("cm_seqno")));
							////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
							pstmt.executeUpdate();
							pstmt.close();
						}
					}
				}
			}
			conn.close();
			conn = null;
			pstmt = null;

			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Copy.confCopy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_Copy.confCopy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_Copy.confCopy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_Copy.confCopy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_Copy.confCopy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}//end of Cmm0300_Copy class statement
