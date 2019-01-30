/*****************************************************************************************
	1. program ID	: Cmd0100.java
	2. create date	: 2006.08. 08
	3. auth		    : is.choi
	4. update date	: 2009.03
	5. auth		    : No Name
	6. description	: 1. USER INFO.
*****************************************************************************************/

package app.eCmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import app.common.UserInfo;
import app.common.LoggableStatement;
import app.thread.ThreadPool;

/**
 * @author bigeyes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Cmd0100{

    /**
     * Logger Class Instance Creation
     * logger
     */
    Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * USER NAME
	 * @param user_id
	 * @return String
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getDir(String UserID,String SysCd,String SecuYn,String RsrcCd,String JobCd,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray = null;

		String   strSelMsg   = "";
		String   strJob[] = null;
		if (JobCd != null && JobCd != "") {
			strJob = JobCd.split(",");
		} else {
			JobCd = "";
		}
	    int      i = 0;
	    int      parmCnt = 0;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}
			//JobCd =JobCd.replace(",", "','");
			strQuery.append("select a.cm_dsncd,a.cm_dirpath                         \n");
			strQuery.append(" from cmm0070 a,cmm0072 c,cmm0073 b                    \n");
			strQuery.append("where b.cm_syscd=?                                     \n");
			if (!SecuYn.equals("Y")) {
				strQuery.append("  and b.cm_jobcd in (select cm_jobcd from cmm0044      \n");
				strQuery.append("                      where cm_userid=? and cm_syscd=? \n");
				strQuery.append("                        and cm_closedt is null         \n");
				if (JobCd.length()>0) {
					strQuery.append("                        and cm_jobcd in ( \n");
					for (i=0;strJob.length>i;i++) {
						if (i>0) strQuery.append(", ? ");
						else strQuery.append("? ");
					}
					strQuery.append("))                                                \n");
				} else {
					strQuery.append(")                                                 \n");
				}
			} else if (JobCd.length()>0) {
				strQuery.append("and b.cm_jobcd in ( \n");
				for (i=0;strJob.length>i;i++) {
					if (i>0) strQuery.append(", ? ");
					else strQuery.append("? ");
				}
				strQuery.append(")                                                  \n");
			}
			strQuery.append("  and b.cm_syscd=a.cm_syscd and b.cm_dsncd=a.cm_dsncd  \n");
			strQuery.append("  and c.cm_rsrccd=?                                    \n");
			strQuery.append("  and c.cm_syscd=a.cm_syscd and c.cm_dsncd=a.cm_dsncd  \n");
			strQuery.append("  and a.cm_clsdt is null                               \n");
			strQuery.append("group by a.cm_dirpath,a.cm_dsncd                       \n");
			strQuery.append("order by a.cm_dirpath                                  \n");

            pstmt = conn.prepareStatement(strQuery.toString());
 //           pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(++parmCnt, SysCd);
            if (!SecuYn.equals("Y")) {
            	pstmt.setString(++parmCnt, UserID);
            	pstmt.setString(++parmCnt, SysCd);
            }
            if (JobCd.length()>0) {
	            for (i=0;strJob.length>i;i++) {
	            	pstmt.setString(++parmCnt, strJob[i]);
	            }
            }
            pstmt.setString(++parmCnt, RsrcCd);
  //          ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rsval.clear();

			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_dsncd", "0000");
				   rst.put("cm_dirpath", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dsncd", rs.getString("cm_dsncd"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval.clear();
			rsval = null;

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getDir() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDir() method statement

	public Object[] getDir_Tmax(String UserID,String SysCd,String RsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select substr(b.cm_dirpath,1,instr(b.cm_dirpath,'/',-1,1)-1) volpath \n");
			strQuery.append(" from cmm0070 b,cmm0072 a                              \n");
			strQuery.append("where a.cm_syscd=? and a.cm_rsrccd=?                   \n");
			strQuery.append("  and a.cm_syscd=b.cm_syscd and a.cm_dsncd=b.cm_dsncd  \n");
			strQuery.append("  and b.cm_dirpath like '%/src/serviceModule/%'        \n");
			strQuery.append(" group by substr(b.cm_dirpath,1,instr(b.cm_dirpath,'/',-1,1)-1) \n");
			strQuery.append(" order by substr(b.cm_dirpath,1,instr(b.cm_dirpath,'/',-1,1)-1) \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, RsrcCd);

            rs = pstmt.executeQuery();

            rsval.clear();
            while (rs.next() == true){
            	rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath", rs.getString("volpath"));
				rsval.add(rst);
				rst = null;
            }
            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;

    		returnObjectArray = rsval.toArray();

    		rsval = null;
    		//ecamsLogger.debug(rsval.toString());
    		return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir_Tmax() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getDir_Tmax() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir_Tmax() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getDir_Tmax() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getDir_Tmax() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of getDir_Tmax() method statement


	public String getDir_Tmax2(String UserID,String SysCd,String RsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String pt = "";
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select a.cm_volpath volpath1						    \n");
			strQuery.append(" from cmm0038 a,cmm0031 b                              \n");
			strQuery.append("where b.cm_syscd=? and b.cm_svrcd='01'                 \n");
			strQuery.append("  and b.cm_syscd=a.cm_syscd and b.cm_svrcd=a.cm_svrcd  \n");
			strQuery.append("  and b.cm_seqno=a.cm_seqno and a.cm_rsrccd=?          \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, RsrcCd);

            rs = pstmt.executeQuery();

            if (rs.next()){
            	pt = rs.getString("cm_volpath");
            }

            rs.close();
            pstmt.close();
            conn.close();

			rs = null;
			pstmt = null;
			conn = null;

    		return pt;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir_Tmax() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getDir_Tmax() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getDir_Tmax() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getDir_Tmax() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getDir_Tmax() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDir_Tmax() method statement

	public String cmm0070_Insert(String UserID,String SysCd,String RsrcName,String RsrcCd,String JobCd,String DirPath,boolean tmaxFg,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		PreparedStatement pstmt3       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

	    String            strDsnCD    = null;
	    String            strBaseDir  = "";
	    String            retMsg      = "";
	    String            strChgDir   = "";

		try {
			strBaseDir = DirPath;
			/*strQuery.setLength(0);
			strQuery.append("select count(*) cnt from cmm0036    \n");
			strQuery.append(" where cm_syscd=?                   \n");
			strQuery.append("   and cm_rsrccd=?                  \n");
			strQuery.append("   and substr(cm_info,58,1)='1'     \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, RsrcCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            if (rs.next()) {
            	if (rs.getInt("cnt")>0) {
            		strChgDir = "/ecams/"+JobCd.substring(3)+"/"+JobCd.substring(0,3);
            		strBaseDir = DirPath.replace("/ecams", strChgDir);
            	}
            }
            rs.close();
            pstmt.close();
            */
			strQuery.setLength(0);
			strQuery.append("select cm_dsncd from cmm0070                           \n");
			strQuery.append(" where cm_syscd=?                                      \n");
			strQuery.append(" and cm_dirpath=?                                      \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, strBaseDir);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            if (rs.next() == false){
            	strDsnCD = "0000001";
            	strQuery.setLength(0);
        		strQuery.append("select lpad(to_number(max(cm_dsncd)) + 1,7,'0') max  \n");
    			strQuery.append("from cmm0070                                         \n");
    			strQuery.append("where cm_syscd=?                                     \n");

    			pstmt2 = conn.prepareStatement(strQuery.toString());
    			pstmt2.setString(1, SysCd);
                rs2 = pstmt2.executeQuery();
                if (rs2.next()) {
                	if (rs2.getString("max") != null){
                		strDsnCD = rs2.getString("max");
                	}
                	strQuery.setLength(0);
            		strQuery.append("insert into cmm0070                               \n");
            		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_DIRPATH,CM_EDITOR,CM_OPENDT,CM_LASTUPDT) \n");
            		strQuery.append("  values                                          \n");
            		strQuery.append("  (?, ?, ?, ?, SYSDATE, SYSDATE)             \n");
            		pstmt3 = conn.prepareStatement(strQuery.toString());
            		pstmt3.setString(1, SysCd);
            		pstmt3.setString(2, strDsnCD);
            		pstmt3.setString(3, strBaseDir);
            		pstmt3.setString(4, UserID);
            		pstmt3.executeUpdate();
            		pstmt3.close();

              		retMsg = strDsnCD;

              		strQuery.setLength(0);
            		strQuery.append("insert into cmm0072                               \n");
            		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_RSRCCD) values            \n");
            		strQuery.append("  (?, ?, ?)                                       \n");
            		pstmt3 = conn.prepareStatement(strQuery.toString());
            		pstmt3.setString(1, SysCd);
            		pstmt3.setString(2, strDsnCD);
            		pstmt3.setString(3, RsrcCd);
            		pstmt3.executeUpdate();
            		pstmt3.close();

            		int i=0;
            		String JobCdzip[] = JobCd.split(",");
            		for (i=0 ; i<JobCdzip.length ; i++){
                  		strQuery.setLength(0);
                		strQuery.append("insert into cmm0073                               \n");
                		strQuery.append("  (CM_SYSCD,CM_DSNCD,CM_JOBCD) values            \n");
                		strQuery.append("  (?, ?, ?)                                       \n");
                		pstmt3 = conn.prepareStatement(strQuery.toString());
                		pstmt3.setString(1, SysCd);
                		pstmt3.setString(2, strDsnCD);
                		pstmt3.setString(3, JobCdzip[i].toString());
                		pstmt3.executeUpdate();
                		pstmt3.close();
            		}
            		JobCdzip = null;
                }
                rs2.close();
                pstmt2.close();

            }
            else {
            	retMsg = rs.getString("cm_dsncd");
            }
            rs.close();
            pstmt.close();

            rs = null;
            pstmt = null;
            rs2 = null;
            pstmt2 = null;
            pstmt3 = null;

            return retMsg;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmm0070_Insert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.cmm0070_Insert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmm0070_Insert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.cmm0070_Insert() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (pstmt3 != null)  try{pstmt3.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of cmm0070_Insert() method statement
	
	
	public Object[] getRsrcOpen(String SysCd,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}
			strQuery.setLength(0);
			strQuery.append("select a.cm_micode,a.cm_codename,b.cm_info,d.cm_volpath,b.cm_exename \n");
			strQuery.append(", decode((select count(*) from cmm0037 where cm_syscd = b.cm_syscd and cm_samersrc = a.cm_micode and cm_factcd='04'), 0, 'N', 'Y') as moduleyn \n");
			strQuery.append("  from cmm0020 a,cmm0036 b,cmm0031 c,cmm0038 d          \n");
			strQuery.append(" where b.cm_syscd=?                                     \n");
			strQuery.append("   and b.cm_closedt is null                             \n");
	//		strQuery.append("   and substr(b.cm_info,26,1)='0'                       \n");
	//		strQuery.append("   and substr(b.cm_info,51,1)='0'                       \n");
			strQuery.append("   and a.cm_macode='JAWON' and a.cm_micode=b.cm_rsrccd  \n");
			strQuery.append("   and b.cm_syscd=d.cm_syscd and b.cm_rsrccd=d.cm_rsrccd\n");
			strQuery.append("   and d.cm_svrcd='01'                                  \n");
			strQuery.append("   and d.cm_syscd=c.cm_syscd and d.cm_svrcd=c.cm_svrcd  \n");
			strQuery.append("   and d.cm_seqno=c.cm_seqno and c.cm_closedt is null   \n");
			strQuery.append(" order by a.cm_seqno                                    \n");

	        //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, SysCd);
	        ecamsLogger.error( ((LoggableStatement)pstmt).getQueryString() );
	        rs = pstmt.executeQuery();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rst.put("cm_info", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("modsw", "N");
				rst.put("moduleyn", rs.getString("moduleyn"));
				if (rs.getString("cm_exename") != null && rs.getString("cm_exename") != "") {
					if (rs.getString("cm_exename").substring(rs.getString("cm_exename").length() - 1).equals(","))
						rst.put("cm_exename", rs.getString("cm_exename"));
					else rst.put("cm_exename", rs.getString("cm_exename")+",");
				} else rst.put("cm_exename", "");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getRsrcOpen() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getRsrcOpen() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getRsrcOpen() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getRsrcOpen() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getRsrcOpen() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of SelectRsrcOpen() method statement

	public Object[] getRsrcOpen_base(String SysCd,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}
			strQuery.setLength(0);
			strQuery.append("select a.cm_micode,a.cm_codename,b.cm_info,d.cm_volpath,b.cm_exename \n");
			strQuery.append("  from cmm0020 a,cmm0036 b,cmm0031 c,cmm0038 d,cmm0030 e\n");
			strQuery.append(" where b.cm_syscd=?                                     \n");
			strQuery.append("   and b.cm_syscd = e.cm_syscd                          \n");
			strQuery.append("   and b.cm_closedt is null                             \n");
			strQuery.append("   and e.cm_closedt is null                             \n");
			strQuery.append("   and a.cm_macode='JAWON' and a.cm_micode=b.cm_rsrccd  \n");
			strQuery.append("   and b.cm_syscd=d.cm_syscd and b.cm_rsrccd=d.cm_rsrccd\n");
			strQuery.append("   and d.cm_svrcd=e.cm_dirbase                          \n");
			strQuery.append("   and d.cm_syscd=c.cm_syscd and d.cm_svrcd=c.cm_svrcd  \n");
			strQuery.append("   and d.cm_seqno=c.cm_seqno and c.cm_closedt is null   \n");
			strQuery.append(" order by a.cm_seqno                                    \n");
	        //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt = new LoggableStatement(conn,strQuery.toString());
	        pstmt.setString(1, SysCd);
	        ecamsLogger.error( ((LoggableStatement)pstmt).getQueryString() );
	        rs = pstmt.executeQuery();
			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rst.put("cm_info", "");
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cm_volpath", rs.getString("cm_volpath"));
				rst.put("modsw", "N");
				if (rs.getString("cm_exename") != null && rs.getString("cm_exename") != "") {
					if (rs.getString("cm_exename").substring(rs.getString("cm_exename").length() - 1).equals(","))
						rst.put("cm_exename", rs.getString("cm_exename"));
					else rst.put("cm_exename", rs.getString("cm_exename")+",");
				} else rst.put("cm_exename", "");
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getRsrcOpen_base() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getRsrcOpen_base() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getRsrcOpen_base() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getRsrcOpen_base() Exception END ##");
			throw exception;
		}finally{
			if (rsval != null)	rsval = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getRsrcOpen_base() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcOpen_base() method statement
	
	public Object[] getLang(String SysCd,String RsrcCd,String SelMsg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		String            strSelMsg   = "";
		Object[] returnObjectArray = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			if (SelMsg != "") {
				if (SelMsg.equals("ALL")) strSelMsg = "전체";
				else if (SelMsg.equals("SEL")) strSelMsg = "선택하세요";
			}

			strQuery.append("select a.cm_micode,a.cm_codename             \n");
			strQuery.append("  from cmm0020 a,cmm0032 b                   \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?        \n");
			strQuery.append("   and a.cm_macode='LANGUAGE'                \n");
			strQuery.append("   and a.cm_micode=b.cm_langcd               \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, RsrcCd);
	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        rsval.clear();

			while (rs.next()){
				if (rs.getRow() == 1 && strSelMsg != "") {
				   rst = new HashMap<String, String>();
				   rst.put("ID", "0");
				   rst.put("cm_micode", "0000");
				   rst.put("cm_codename", strSelMsg);
				   rsval.add(rst);
				   rst = null;
				}
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();


			rs = null;
			pstmt = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getLang() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getLang() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getLang() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getLang() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getLang() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getLang() method statement



	//public Object[] getOpenList(String UserId,String SysCd,String RsrcCd,String Isrid,String Isrsub,String RsrcName,boolean SecuSw) throws SQLException, Exception {
	public Object[] getOpenList(String SRid,String UserId,String SysCd,String RsrcCd,String RsrcName,boolean SecuSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();

		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		Object[] returnObjectArray = null;
		int               parmCnt  = 0;
		String			  strRsrcCd = "";
		String            strRsrc[] = null;
		int               i = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			UserInfo userinfo = new UserInfo();
			if (userinfo.isAdmin(UserId)) SecuSw = true;
			else SecuSw = false;

			strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			strQuery.append("   and substr(cm_info, 26, 1)='0'                 \n");
			strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?        \n");
			strQuery.append("                            and cm_factcd='04')   \n");
			strQuery.append(" order by cm_rsrccd 							   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, SysCd);
            pstmt.setString(2, SysCd);

            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	if (strRsrcCd != "") strRsrcCd = strRsrcCd + ",";
            	strRsrcCd = strRsrcCd + rs.getString("cm_rsrccd");
            }
			rs.close();
			pstmt.close();

			strRsrc = strRsrcCd.split(",");

			rsval.clear();
			strQuery.setLength(0);
			strQuery.append("select a.cr_syscd,a.cr_dsncd,a.cr_rsrccd,a.cr_langcd, \n");
			strQuery.append("       a.cr_rsrcname,a.cr_story,a.cr_jobcd,a.cr_isrid,\n");
			strQuery.append("       to_char(a.cr_lastdate,'yyyy-mm-dd') lastdt,    \n");
			strQuery.append("       a.cr_itemid,a.cr_jobcd,f.cm_jobname,           \n");
			strQuery.append("       e.cm_dirpath,b.cm_sysgb,c.cm_codename jawon \n");
			strQuery.append("  from cmm0102 f,cmm0070 e,cmm0020 c,       \n");
			strQuery.append("       cmm0030 b,cmr0020 a                  \n");
			if (SecuSw == false) {
				   strQuery.append(",cmm0044 h                                     \n");
			}
			strQuery.append(" where a.cr_syscd=?                                   \n");
			strQuery.append("   and a.cr_editor=?                                  \n");
			strQuery.append("   and a.cr_status='3'                                \n");
			if (SRid != null && SRid != "") {
				strQuery.append("and a.cr_isrid=?                                  \n");
			}
			if (SecuSw == false) {
				strQuery.append("and h.cm_userid=? and h.cm_closedt is null        \n");
				strQuery.append("and a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd \n");
			}
			if (RsrcCd != "" && RsrcCd != null) {
				strQuery.append("and a.cr_rsrccd=?                                   \n");
			} else {
				strQuery.append("and a.cr_rsrccd in (");
				for (i=0;strRsrc.length>i;i++) {
					if (i>0) strQuery.append(", ? ");
					else strQuery.append("? ");
				}
				strQuery.append(")                                                    \n");
			}
			if (RsrcName != "" && RsrcName != null) {
				strQuery.append("and upper(a.cr_rsrcname) like upper(?)               \n");
			}
			strQuery.append("   and a.cr_syscd=b.cm_syscd                             \n");
			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd   \n");
			strQuery.append("   and a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd   \n");
			strQuery.append("   and a.cr_jobcd=f.cm_jobcd                             \n");

	        //pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt =  new LoggableStatement(conn, strQuery.toString());

	        pstmt.setString(++parmCnt, SysCd);
	        pstmt.setString(++parmCnt, UserId);
	        
	        if (SRid != null && SRid != "") pstmt.setString(++parmCnt, SRid);
	        if (SecuSw == false) {
	        	pstmt.setString(++parmCnt,UserId);
	        }
	        if (RsrcCd != "" && RsrcCd != null) {
	        	pstmt.setString(++parmCnt, RsrcCd);
	        } else {
            	for (i=0;strRsrc.length>i;i++) {
            		pstmt.setString(++parmCnt, strRsrc[i]);
            	}
	        }
	        if (RsrcName != "" && RsrcName != null) {
	        	pstmt.setString(++parmCnt, "%"+RsrcName+"%");
			}
	        ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID",Integer.toString(rs.getRow()));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("jawon_code",rs.getString("jawon"));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_lastdate",rs.getString("lastdt"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				rst.put("cm_jobname",rs.getString("cm_jobname"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_isrid",rs.getString("cr_isrid"));
				rst.put("enabled", "1");
				rst.put("selected", "0");

				strQuery.setLength(0);
				strQuery.append("select count(*) cnt from cmr1010     \n");
				strQuery.append(" where cr_baseitem=?                 \n");
				strQuery.append("   and cr_status='9'                 \n");
				pstmt2 = conn.prepareStatement(strQuery.toString());
		        pstmt2.setString(1, rs.getString("cr_itemid"));
		        rs2 = pstmt2.executeQuery();
				if (rs2.next()){
					if (rs2.getInt("cnt") > 0) {
						rst.put("enabled", "0");
					}
				}
				rs2.close();
				pstmt2.close();

				rsval.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			returnObjectArray = rsval.toArray();
			rsval = null;
			//ecamsLogger.debug(rsval.toString());
			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getOpenList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.getOpenList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getOpenList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.getOpenList() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getOpenList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getOpenList() method statement

	public String Program_Name_Check(String txtProg, String Jobcd, String Rsrccd) throws Exception {
		try{
			String strCheck = "";
			/*프로그램명명규칙
			1. 온라인서비스 :o,배치서비스:b,OLB:l,Demon:e
			DBIO:d, BAM:m, deferred Demon:s
			2. 업무4자리
			15. oltp,mod,batch,demon : c,r,u,d,a
			    dbio : c,r,u,s,m
			*/
			//ecamsLogger.error(Rsrccd+"/"+txtProg+"/"+txtProg.substring(13,14)+"/"+Jobcd);

			if(Rsrccd.equals("01")){
				//서비스
				if(!txtProg.substring(0,4).equals("fc_o")){
					strCheck = "FAIL온라인서비스는 프로그램명이 'fc_o'로 시작해야합니다.";
				}else if(txtProg.length() < 18){
					strCheck = "FAIL확장자를 뺀 프로그램명이 18자리여야 합니다.";
				}else if(!txtProg.substring(17,18).equals("c") && !txtProg.substring(17,18).equals("r")
					&& !txtProg.substring(17,18).equals("u") && !txtProg.substring(17,18).equals("d") && !txtProg.substring(17,18).equals("a")){
					strCheck = "FAILTR특성이 c,r,u,d,a에 속하지않습니다.";
				}
			}else if(Rsrccd.equals("02")){
				//배치
				if(!txtProg.substring(0,1).equals("b")){
					strCheck = "FAIL배치서비스 는 프로그램명이 'b'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("c") && !txtProg.substring(14,15).equals("r")
					&& !txtProg.substring(14,15).equals("u") && !txtProg.substring(14,15).equals("d") && !txtProg.substring(14,15).equals("a")){
					strCheck = "FAILTR특성이 c,r,u,d,a에 속하지않습니다.";
				}
			}else if(Rsrccd.equals("06")){
				//OLB
				if(!txtProg.substring(0,1).equals("l")){
					strCheck = "FAILOLB는 프로그램명이 'l'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}
			}else if(Rsrccd.equals("07")){
				//데몬
				if(!txtProg.substring(0,1).equals("e")){
					strCheck = "FAILDEMON은 프로그램명이 'e'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("c") && !txtProg.substring(14,15).equals("r")
					&& !txtProg.substring(14,15).equals("u") && !txtProg.substring(14,15).equals("d") && !txtProg.substring(14,15).equals("a")){
					strCheck = "FAILTR특성이 c,r,u,d,a에 속하지않습니다.";
				}
			}else if(Rsrccd.equals("17")){
				//deferred Demon
				if(!txtProg.substring(0,1).equals("s")){
					strCheck = "FAILDEMON은 프로그램명이 's'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("c") && !txtProg.substring(14,15).equals("r")
					&& !txtProg.substring(14,15).equals("u") && !txtProg.substring(14,15).equals("d") && !txtProg.substring(14,15).equals("a")){
					strCheck = "FAILTR특성이 c,r,u,d,a에 속하지않습니다.";
				}
			}else if(Rsrccd.equals("38")){//DAM(MR,MU)
				if(!txtProg.substring(0,1).equals("d")){
					strCheck = "FAILDBIO는 프로그램명이 'd'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("r") && !txtProg.substring(14,15).equals("u")){
					strCheck = "FAILTR특성이 r또는u로 끝나야합니다.";
				}
			}else if(Rsrccd.equals("39")){//DAM(RS,RM,RU)
				if(!txtProg.substring(0,1).equals("d")){
					strCheck = "FAILDBIO는 프로그램명이 'd'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("s") && !txtProg.substring(14,15).equals("m") && !txtProg.substring(14,15).equals("w")){
					strCheck = "FAILTR특성이 s 또는 m 또는 w로 끝나야합니다.";
				}
			}else if(Rsrccd.equals("40")){//DAM(CM)
				if(!txtProg.substring(0,1).equals("d")){
					strCheck = "FAILDBIO는 프로그램명이 'd'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("c")){
					strCheck = "FAILTR특성이 c로 끝나야합니다.";
				}
			}else if(Rsrccd.equals("03") || Rsrccd.equals("04")){
				//모듈,모듈(BAT)
				if(!txtProg.substring(0,1).equals("m") && !txtProg.substring(0,1).equals("x") && !txtProg.substring(0,1).equals("y")){
					strCheck = "FAILBAM은 프로그램명이 'm','x','y'로 시작해야합니다.";
				}else if(txtProg.length() < 15){
					strCheck = "FAIL확장자를 뺀 프로그램명이 15자리여야 합니다.";
				}else if(!txtProg.substring(14,15).equals("c") && !txtProg.substring(14,15).equals("r")
					&& !txtProg.substring(14,15).equals("u") && !txtProg.substring(14,15).equals("d") && !txtProg.substring(14,15).equals("a")){
					strCheck = "FAILTR특성이 c,r,u,d,a에 속하지않습니다.";
				}
			}

			//ecamsLogger.error("11111"+txtProg.toUpperCase().substring(1,4));
			//ecamsLogger.error("\n22222"+Jobcd.toUpperCase());

			if(strCheck == ""){
				strCheck = "OKOK";
			}

			return strCheck;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.Program_Name_Check() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.Program_Name_Check() Exception END ##");
			throw exception;
		}finally{

		}

	}//end of Program_Name_Check() method statement

	HashMap<String, String> Program_NammingRule(HashMap<String, String> rst) throws Exception
	{
		try{
			String strCheck = "";
			String strRsrcCd = "";
			String strJobCd = "";
			String strRsrcName = "";

			/*프로그램명명규칙
			0. .h 끝나면 헤더파일임
			1. 온라인서비스 :o,배치서비스:b,OLB:l,Demon:e
			DBIO:d, BAM:m, deferred Demon:s
			2. 업무4자리
			15. oltp,mod,batch,demon : c,r,u,d,a
			    dbio : c,r,u,s,m ,x
			*/
			//ecamsLogger.error(Rsrccd+"/"+txtProg+"/"+txtProg.substring(13,14)+"/"+Jobcd);
			if (rst.get("filename").substring(0,3).equals("fc_")) {
				strRsrcCd = "01";
				strRsrcName = rst.get("filename").substring(3);
			}else if(strRsrcName.lastIndexOf(".") >-1){
				strRsrcName = strRsrcName.substring(0,strRsrcName.lastIndexOf("."));
			}else{
				strRsrcName = rst.get("filename");
			}

			if (rst.get("filename").lastIndexOf(".h")>0) {
				strJobCd = "";
				strRsrcCd = "21"; //Header
			}else if (strRsrcName.length()<15) {
				strCheck = "자릿수";
			}else {
				strJobCd = strRsrcName.substring(1,4).toUpperCase();
				//ecamsLogger.error(strJobCd);
				if (strRsrcCd.equals("01")) {
					if (!strRsrcName.substring(0,1).equals("o")) {
						strCheck = "모듈구분";
					}
				} else {
					if (strRsrcName.substring(0,1).equals("b")) strRsrcCd = "02";      //BATCH
					else if (strRsrcName.substring(0,1).equals("l")) strRsrcCd = "06"; //OLB
					else if (strRsrcName.substring(0,1).equals("e")) strRsrcCd = "07"; //Demon
					else if (strRsrcName.substring(0,1).equals("s")) strRsrcCd = "17"; //Deferred Demon
					else if (strRsrcName.substring(0,4).equals("mood")) strRsrcCd = "10"; //모듈(mood)
					else if (strRsrcName.substring(0,1).equals("m") || strRsrcName.substring(0,1).equals("x") || strRsrcName.substring(0,1).equals("y")) {
						if (rst.get("filename").lastIndexOf(".c")>0) {
							strRsrcCd = "03";//모듈
						} else if (strRsrcName.substring(0,1).equals("m")) {
							strRsrcCd = "05";//모듈(BAT_m)
						} else{
							strRsrcCd = "04";//모듈(BAT)
						}
					} else if (strRsrcName.substring(0,1).equals("d")) {
						if (strRsrcName.substring(14,15).equals("c")) strRsrcCd = "40"; //DAM(CM);
						else if (strRsrcName.substring(14,15).equals("r") || strRsrcName.substring(14,15).equals("u")) strRsrcCd = "38"; //DAM(MR,MU);
						else if (strRsrcName.substring(14,15).equals("s") || strRsrcName.substring(14,15).equals("m") || strRsrcName.substring(14,15).equals("w")) strRsrcCd = "39"; //DAM(RS,RM,RU)
						else strCheck = "TR특성";
					} else {
						strCheck = "모듈구분";
					}
				}
				if (strCheck.length() == 0 &&
					(strRsrcCd.equals("01") || strRsrcCd.equals("02") || strRsrcCd.equals("03") || strRsrcCd.equals("04") ||
							 strRsrcCd.equals("05") ||strRsrcCd.equals("07") || strRsrcCd.equals("17"))) {
					if (!strRsrcName.substring(14,15).equals("c") && !strRsrcName.substring(14,15).equals("r")
						&& !strRsrcName.substring(14,15).equals("u") && !strRsrcName.substring(14,15).equals("d")
						&& !strRsrcName.substring(14,15).equals("a")) {
						strCheck = "TR특성";
					}
				}
			}

			//ecamsLogger.error("11111"+txtProg.toUpperCase().substring(1,4));
			//ecamsLogger.error("\n22222"+Jobcd.toUpperCase());

			if(strCheck.length() == 0){
				strCheck = "정상";
				rst.put("error", "");
			} else {
				rst.put("error", "1");
				rst.put("enable1", "0");
				rst.put("selected", "0");
			}
			rst.put("errmsg", strCheck);
			rst.put("rsrccd", strRsrcCd);
			rst.put("jobcd", strJobCd);

			return rst;

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.Program_NammingRule() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.Program_NammingRule() Exception END ##");
			throw exception;
		}finally{
			if (rst != null) rst = null;
		}
	}//end of Program_NammingRule() method statement

	public Object[] pgmCheck(HashMap<String, String> etcData) throws SQLException, Exception {
	//public Object[] pgmCheck(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String ProgTit,
    //			String DirPath,String CM_info,boolean dirSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            BaseItem    = "";
		Object[] returnObjectArray = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String  noName  = "";
		String  strWork1 = "";
		String  noNameAry[] = null;
		int j = 0;

		try {
			conn = connectionContext.getConnection();
			
			String UserId = etcData.get("userid");
			String SysCd = etcData.get("syscd");
			String DsnCd = etcData.get("dsncd");
			String RsrcName = etcData.get("rsrcname");
			String RsrcCd = etcData.get("rsrccd");
			String JobCd = etcData.get("jobcd");
			String ProgTit = etcData.get("story");
			String DirPath = etcData.get("dirpath");
			String CM_info = etcData.get("cminfo");
			String SRid = etcData.get("srid");
			boolean dirSw = false;
			
			strQuery.setLength(0);
			strQuery.append("select cm_noname from cmm0010 where cm_stno='ECAMS' and cm_noname is not null  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				noName = rs.getString("cm_noname");
			}
			rs.close();
			pstmt.close();
			
			/*  프로그램명 특수문자 확인     */
			if (noName.length()>0) {
				if (noName.length()>0) {
					for (j=0;noName.length()>j;j++) {
						if (!noName.substring(j,j+1).trim().equals("")) {
							if (strWork1.length()>0) strWork1 = strWork1 + ";";
							strWork1 = strWork1 + noName.substring(j,j+1);
						}
					}
					noNameAry = strWork1.split(";");
				}
				for (j=0;noNameAry.length>j;j++) {
					if (RsrcName.indexOf(noNameAry[j])>=0){
						retMsg = "프로그램명에 ["+noNameAry[j] + "] 사용이 불가합니다.";
						break;
					}
				}
                if (retMsg.equals("0")) {
					for (j=0;noNameAry.length>j;j++) {
						if (DirPath.indexOf(noNameAry[j])>=0){
							retMsg = "경로명에 ["+noNameAry[j] + "] 사용이 불가합니다.";
							break;
						}
					}
                }
			}
			if (retMsg.equals("0")) {
				if (dirSw == true) {
					int x = DirPath.lastIndexOf("/");
					String strPath = "";
					if (x >= 0) strPath = DirPath.substring(x);
					else strPath = DirPath;
	
					if (!strPath.equals(RsrcName)) {
						DirPath = DirPath + "/" + RsrcName;
	
						String strDsn = cmm0070_Insert(UserId,SysCd,RsrcName,RsrcCd,JobCd,DirPath,true,conn);
						if (strDsn == null || strDsn == "") {
							retMsg = "디렉토리 등록 처리 중 오류가 발생하였습니다. ["+DirPath+"/"+RsrcName+"]";
						} else DsnCd = strDsn;
					}
				}
			}
			if (retMsg.equals("0")) {
				strQuery.setLength(0);
				strQuery.append("select a.cr_status,a.cr_lstver,a.cr_editor,b.cm_codename \n");
				strQuery.append("  from cmm0020 b,cmr0020 a                               \n");
				strQuery.append(" where a.cr_syscd=? and a.cr_dsncd=?                     \n");
				strQuery.append("   and a.cr_rsrcname=?                                   \n");
				strQuery.append("   and b.cm_macode='CMR0020' and b.cm_micode=a.cr_status \n");

		        pstmt = conn.prepareStatement(strQuery.toString());
		        pstmt.setString(1, SysCd);
		        pstmt.setString(2, DsnCd);
		        pstmt.setString(3, RsrcName);
		        rs = pstmt.executeQuery();
		        rsval.clear();
		        if (rs.next() == true) {
/*		        	if (rs.getString("cr_status").equals("3") && !rs.getString("cr_editor").equals(UserId))
		        		retMsg = "다른 사용자가 신규등록한 프로그램ID입니다.";
		        	else if (rs.getString("cr_status").equals("3")) retMsg = "0";
		        	else retMsg = "기 등록하여 운영 중인 프로그램ID입니다.";
*/
		        	if (rs.getString("cr_status").equals("3")) retMsg = "0";
		        	else retMsg = "기 등록하여 운영 중인 프로그램ID입니다.";
		        
		        }
		        rs.close();
		        pstmt.close();

			}
	        if (retMsg.equals("0")) {
	        	retMsg = null;
	        	rst = new HashMap<String, String>();
	        	rst.put("userid",UserId);
	        	rst.put("syscd",SysCd);
	        	rst.put("dsncd",DsnCd);
	        	rst.put("rsrcname",RsrcName);
	        	rst.put("rsrccd",RsrcCd);
	        	rst.put("jobcd",JobCd);
	        	rst.put("story",ProgTit);
	        	rst.put("baseitem","");
	        	rst.put("info",CM_info);
	        	rst.put("srid", SRid);
	        	retMsg = cmr0020_Insert(rst,conn);
	        	rst = null;
	        	//retMsg = cmr0020_Insert(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,ProgTit,"",CM_info,conn);

	        	if (retMsg.substring(0,1).equals("0")) {
	        		BaseItem = retMsg.substring(1);
	        		retMsg = "0";

	        		//retMsg = moduleChk(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,ProgTit,DirPath,BaseItem,CM_info);

	        		strQuery.setLength(0);
	    			strQuery.append("select a.cr_syscd,a.cr_dsncd,a.cr_rsrccd,a.cr_langcd,a.cr_rsrcname, \n");
	    			strQuery.append("       a.cr_story,to_char(a.cr_lastdate,'yyyy-mm-dd') lastdt,       \n");
	    			strQuery.append("       a.cr_jobcd,f.cm_jobname,e.cm_dirpath,a.cr_jobcd,             \n");
	    			strQuery.append("       c.cm_codename jawon,b.cm_sysgb,a.cr_isrid                    \n");
	    			strQuery.append("  from cmm0102 f,cmm0070 e,cmm0020 c,cmm0030 b,cmr0020 a            \n");
	    			strQuery.append(" where a.cr_itemid=?                                                \n");
	    			strQuery.append("   and a.cr_syscd=b.cm_syscd                                        \n");
	    			strQuery.append("   and c.cm_macode='JAWON' and c.cm_micode=a.cr_rsrccd              \n");
	    			strQuery.append("   and a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd              \n");
	    			strQuery.append("   and a.cr_jobcd=f.cm_jobcd                                        \n");

	    	        pstmt = conn.prepareStatement(strQuery.toString());
	    	        //pstmt =  new LoggableStatement(conn, strQuery.toString());
	    	        pstmt.setString(1, BaseItem);
	    	        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	    	        rs = pstmt.executeQuery();

	    			if (rs.next()){
	    				rst = new HashMap<String, String>();
	    				rst.put("ID","ADD");
	    				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
	    				rst.put("cr_story",rs.getString("cr_story"));
	    				rst.put("cm_jobname",rs.getString("cm_jobname"));
	    				rst.put("jawon_code",rs.getString("jawon"));
	    				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
	    				rst.put("cr_lastdate",rs.getString("lastdt"));
	    				rst.put("cr_syscd",rs.getString("cr_syscd"));
	    				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
	    				rst.put("cm_sysgb",rs.getString("cm_sysgb"));
	    				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
	    				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
	    				rst.put("cr_isrid",rs.getString("cr_isrid"));
	    				rst.put("cr_itemid",BaseItem);

	    				rst.put("enabled", "1");
	    				rst.put("selected", "0");
	    				rsval.add(rst);
	    				rst = null;
	    			}//end of while-loop statement
	    			rs.close();
	    			pstmt.close();
	        	} else {
		        	rst = new HashMap<String, String>();
					rst.put("ID", retMsg);
					rsval.add(rst);
					rst = null;
	        	}
	        } else {
	        	rst = new HashMap<String, String>();
				rst.put("ID", retMsg);
				rsval.add(rst);
				rst = null;
	        }

			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug(rsval.toString());
			returnObjectArray = rsval.toArray();
			rsval = null;

			return returnObjectArray;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.pgmCheck() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.pgmCheck() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.pgmCheck() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.pgmCheck() Exception END ##");
			throw exception;
		}finally{
			if (returnObjectArray != null)	returnObjectArray = null;
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.pgmCheck() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of pgmCheck() method statement
	public String moduleChk(String UserId,String SysCd,String DsnCd,String RsrcName,String RsrcCd,String JobCd,String ProgTit,String DirPath,String BaseItem,String CM_info)
		throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            strBefDsn   = null;
		String            strAftDsn   = null;
		String            strWork1    = null;
		String            strWork3    = null;
		String            strDevPath  = null;
		String            strRsrcCd   = null;
		String            strRsrcName = null;
		int               j           = 0;
		ArrayList<String>	qryAry = null;
		int					nRet1 = 0;
		int					nRet2 = 0;
		int					qryFlag = 0;
		HashMap<String, String> rst = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
			strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
			strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
			strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
			strQuery.append("   and b.cm_factcd='04'                                  \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
			strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(1, SysCd);
	        pstmt.setString(2, RsrcCd);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	        	strBefDsn = "";
	        	strAftDsn = "";

	        	if (rs.getString("cm_basedir") != null) strBefDsn = rs.getString("cm_basedir");
	        	if (RsrcName.indexOf(".") > -1) {
	        		strWork1 = RsrcName.substring(0,RsrcName.indexOf("."));
	        	}
	        	else strWork1 = RsrcName;
	        	//ecamsLogger.debug("+++++++++++++++cm_basedir,strWork1=========>"+strBefDsn+ ","+strWork1);
	        	if (!rs.getString("cm_basename").equals("*")) {
	        		strWork3 = rs.getString("cm_basename");
	        		while (strWork3 == "") {
	        			j = strWork3.indexOf("*");
	        			if (j > -1) {
	        				//strWork2 = strWork3.substring(0, j);
	        				strWork3 = strWork3.substring(j + 1);
	        				if (strWork3.equals("*")) strWork3 = "";
	        			} else {
	        				//strWork2 = strWork3;
	        				strWork3 = "";
	        			}
	        			if (strWork3 == "") break;
	        		}
	        	}
	        	strQuery.setLength(0);
	        	strQuery.append("select \n");
	        	if (rs.getString("cm_cmdyn").equals("Y")) {
	        		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
			   		qryAry = new ArrayList<String>();
			   		nRet1 = 0;
			   		nRet2 = 0;

			   		while( (nRet2 = strWork1.indexOf("'")) != -1){
			   			if (qryFlag == 0){
			   				strQuery.append(strWork1.substring(0, nRet2)+ " \n");
			   				strWork1 = strWork1.substring(nRet2+1);
			   				qryFlag = 1;
			   			}
			   			else{
			   				qryAry.add(strWork1.substring(0, nRet2));
			   				strWork1 = strWork1.substring(nRet2+1);
			   				strQuery.append(" ? \n");
			   				qryFlag = 0;
			   			}
			   		}
			   		strQuery.append(strWork1+ " \n");
	        	}
	        	else{
	        		strQuery.append(" ? \n");
	        	}
	        	strQuery.append("as relatId  from dual \n");

	        	//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
	        	pstmt2 = conn.prepareStatement(strQuery.toString());

			   	nRet1 = 1;
				if (rs.getString("cm_cmdyn").equals("Y")){
	        		for (nRet2 = 0;nRet2<qryAry.size();nRet2++){
	        			pstmt2.setString(nRet1++,qryAry.get(nRet2));
	        		}
	        	}
	        	else{
			   		strWork1 = rs.getString("cm_samename").replace("*",strWork1);
			   		pstmt2.setString(nRet1++,strWork1);
	        	}

				//ecamsLogger.debug(((LoggableStatement)pstmt2).getQueryString());

    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strWork1 = rs2.getString("relatId");
    	        }
    	        else{
    	        	strWork1 = "";
    	        }
    	        rs2.close();
    	        pstmt2.close();

	        	strRsrcName = strWork1;
	        	strRsrcCd = rs.getString("cm_samersrc");
	        	if (rs.getString("cm_samedir") != null) strAftDsn = rs.getString("cm_samedir");

	        	if (rs.getString("cm_samersrc").equals("52")) {
	        		strQuery.setLength(0);
	        		strQuery.append("select a.cm_volpath  from cmm0038 a,cmm0031 b  \n");
	        		strQuery.append(" where a.cm_syscd=? and a.cm_svrcd='01'        \n");
	        		strQuery.append("   and a.cm_rsrccd=? and a.cm_syscd=b.cm_syscd \n");
	        		strQuery.append("   and a.cm_svrcd=b.cm_svrcd and a.cm_seqno=b.cm_seqno  \n");
	        		strQuery.append("   and b.cm_closedt is null                    \n");
	        		pstmt2 = conn.prepareStatement(strQuery.toString());
	        		pstmt2.setString(1, SysCd);
	        		pstmt2.setString(2, strRsrcCd);
	    	        rs2 = pstmt2.executeQuery();
	    	        if (rs2.next()) {
	    	        	strDevPath = rs2.getString("cm_volpath");
	    	        }

	    	        rs2.close();
	    	        pstmt2.close();
	        	} else strDevPath = DirPath.replace(strBefDsn, strAftDsn);
	        	//ecamsLogger.debug("+++++++++++++++strRsrcCd,strDevPath=========>"+strRsrcCd+ ","+strDevPath);

	        	strQuery.setLength(0);
        		strQuery.append("select cm_dsncd from cmm0070            \n");
        		strQuery.append(" where cm_syscd=? and cm_dirpath=?      \n");
        		pstmt2 = conn.prepareStatement(strQuery.toString());
        		pstmt2.setString(1, SysCd);
        		pstmt2.setString(2, strDevPath);
    	        rs2 = pstmt2.executeQuery();
    	        if (rs2.next()) {
    	        	strAftDsn = rs2.getString("cm_dsncd");
    	        } else {
    	        	strAftDsn = cmm0070_Insert(UserId,SysCd,strRsrcName,strRsrcCd,JobCd,strDevPath,false,conn);
    	        }
    	        rs2.close();
    	        pstmt2.close();

    	        if (retMsg == "0") {
    	        	//ecamsLogger.debug("+++++++++++++++rsrcname,BaseItem=========>"+strRsrcName+","+BaseItem);
    	        	//retMsg = cmr0020_Insert(UserId,SysCd,strAftDsn,strRsrcName,strRsrcCd,JobCd,ProgTit,BaseItem,CM_info,conn) ;
    	        		   //cmr0020_Insert(UserId,SysCd,DsnCd,RsrcName,RsrcCd,JobCd,ProgTit,"",CM_info,conn);
    	        	rst = new HashMap<String, String>();
    	        	rst.put("userid",UserId);
    	        	rst.put("syscd",SysCd);
    	        	rst.put("dsncd",strAftDsn);
    	        	rst.put("rsrcname",strRsrcName);
    	        	rst.put("rsrccd",strRsrcCd);
    	        	rst.put("jobcd",JobCd);
    	        	rst.put("story",ProgTit);
    	        	rst.put("baseitem",BaseItem);
    	        	rst.put("info",CM_info);
    	        	retMsg = cmr0020_Insert(rst,conn);
    	        	rst = null;
    	        }
	        }
	        rs.close();
	        pstmt.close();
	        conn.close();


			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

	        return retMsg;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.moduleChk() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.moduleChk() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.moduleChk() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.moduleChk() Exception END ##");
			throw exception;
		}finally{
			if (strQuery !=  null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.moduleChk() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of moduleChk() method statement


	//cmr0020_Insert(etcData.get("userid"),etcData.get("syscd"),rtList.get(i).get("cm_dsncd"),rtList.get(i).get("filename"),etcData.get("rsrccd"),etcData.get("jobcd"),rtList.get(i).get("story"),etcData.get("pgmtype"),"",conn);
	public String cmr0020_Insert(HashMap<String,String> openInfo,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retMsg      = "0";
		String            strItemId   = "";
		boolean           insFg       = true;
		int 		      parmCnt     = 0;
//		String            strGrpCd    = "";

		try {
			if (openInfo.get("info").substring(26,27).equals("1")) {
				if (openInfo.get("grpcd") == null || openInfo.get("grpcd") == "") {
					Connection        connPfm        = null;
					ConnectionContext connectionContextPfm;
					String pfmstring = "";
					pfmstring = "PFM"+openInfo.get("syscd").substring(2,5);
					//ecamsLogger.error("pfmstring connection ready "+pfmstring);
					connectionContextPfm = new ConnectionResource(false, pfmstring);
					connPfm = connectionContextPfm.getConnection();

					strQuery.setLength(0);
					strQuery.append("SELECT RESOURCE_GROUP FROM DEV_RESOURCE \n");
					strQuery.append(" WHERE PHYSICAL_NAME=?                  \n");
					pstmt = connPfm.prepareStatement(strQuery.toString());
					pstmt.setString(1, openInfo.get("rsrcname"));
					rs = pstmt.executeQuery();
					if (rs.next()) {
//						strGrpCd = rs.getString("RESOURCE_GROUP");
					} else {
						retMsg = "프레임워크 그룹코드 추출에 실패하였습니다.";
					}
					//ecamsLogger.error("pfmstring connection success "+pfmstring);
					rs.close();
					pstmt.close();
					connPfm.close();

					connPfm = null;
					if (!retMsg.equals("0")) {
						return retMsg;
					}
				} else {
//					strGrpCd = openInfo.get("grpcd");
				}
			}

			//conn.setAutoCommit(false);
			strQuery.setLength(0);
			strQuery.append("select cr_itemid     \n");
			strQuery.append("  from cmr0020       \n");
			strQuery.append(" where cr_syscd=?    \n");
		    strQuery.append("   and cr_dsncd=?    \n");
			strQuery.append("   and cr_rsrcname=? \n");

	        pstmt = conn.prepareStatement(strQuery.toString());
	        pstmt.setString(++parmCnt, openInfo.get("syscd"));
	        pstmt.setString(++parmCnt, openInfo.get("dsncd"));
	        pstmt.setString(++parmCnt, openInfo.get("rsrcname"));
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	        	insFg = false;
	        	strItemId = rs.getString("cr_itemid");
	        }
	        rs.close();
	        pstmt.close();

	        parmCnt = 0;
	        strQuery.setLength(0);
	        if (insFg == true) {

        		strQuery.append("insert into cmr0020 (CR_ITEMID,CR_SYSCD,CR_DSNCD,CR_RSRCNAME,  \n");
        		strQuery.append("   CR_RSRCCD,CR_JOBCD,CR_LANGCD,CR_STATUS,CR_CREATOR,CR_STORY, \n");
        		//14.07.17 siruen. CR_LSTUSR 값 세팅
        		strQuery.append("   CR_OPENDATE,CR_LASTDATE,CR_LSTVER,CR_EDITOR,CR_LSTUSR,CR_NOMODIFY,CR_ISRID) \n");
        		strQuery.append("values                                                         \n");
        		strQuery.append("  (lpad(ITEMID_SEQ.nextval,12,'0'),?,?,?,  ?,?,'','3',?,?,  SYSDATE,SYSDATE,0,?,?,'0',?)  \n");

        		pstmt = conn.prepareStatement(strQuery.toString());
        		//pstmt = new LoggableStatement(conn,strQuery.toString());
          	    pstmt.setString(++parmCnt, openInfo.get("syscd"));
          	    pstmt.setString(++parmCnt, openInfo.get("dsncd"));
          	    pstmt.setString(++parmCnt, openInfo.get("rsrcname"));

          	    pstmt.setString(++parmCnt, openInfo.get("rsrccd"));
          	    pstmt.setString(++parmCnt, openInfo.get("jobcd"));
          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	    pstmt.setString(++parmCnt, openInfo.get("story"));

          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	    //14.07.17 siruen. CR_LSTUSR 값 세팅
          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	  
          	    pstmt.setString(++parmCnt, openInfo.get("srid"));

          		pstmt.executeUpdate();
    	        pstmt.close();

	        } else {

        		strQuery.append("update cmr0020 set CR_RSRCCD=?,CR_JOBCD=?,          \n");
        		strQuery.append("                   CR_STATUS='3',                   \n");
        		strQuery.append("                   CR_CREATOR=?,CR_STORY=?,         \n");
        		strQuery.append("                   CR_OPENDATE=SYSDATE,             \n");
        		strQuery.append("                   CR_LASTDATE=SYSDATE,CR_LSTVER=0, \n");
        		//20140723 neo. CR_LSTUSR 값 셋팅 추가.
        		strQuery.append("                   CR_EDITOR=?,CR_LSTUSR=?,CR_NOMODIFY=0\n");
        		strQuery.append("                   ,CR_ISRID=?        			     \n");
        		strQuery.append("where cr_itemid=?                                   \n");

        		pstmt = conn.prepareStatement(strQuery.toString());
        		//pstmt = new LoggableStatement(conn,strQuery.toString());
        		parmCnt = 0;
          	    pstmt.setString(++parmCnt, openInfo.get("rsrccd"));
          	    pstmt.setString(++parmCnt, openInfo.get("jobcd"));
          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	    pstmt.setString(++parmCnt, openInfo.get("story"));
          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	    //20140723 neo. CR_LSTUSR 값 셋팅 추가.
          	    pstmt.setString(++parmCnt, openInfo.get("userid"));
          	    pstmt.setString(++parmCnt, openInfo.get("srid"));
          	    
          	    pstmt.setString(++parmCnt, strItemId);
          	    //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
          	    pstmt.executeUpdate();
    	        pstmt.close();
	        }

	        if (insFg == true) {
	        	strQuery.setLength(0);
				strQuery.append("select cr_itemid from cmr0020                  \n");
				strQuery.append(" where cr_syscd=?                              \n");
			    strQuery.append("   and cr_dsncd=?                              \n");
				strQuery.append("   and cr_rsrcname=?                           \n");

				parmCnt= 0;
		        pstmt = conn.prepareStatement(strQuery.toString());
          	    pstmt.setString(++parmCnt, openInfo.get("syscd"));
          	    pstmt.setString(++parmCnt, openInfo.get("dsncd"));
          	    pstmt.setString(++parmCnt, openInfo.get("rsrcname"));
		        rs = pstmt.executeQuery();

		        if (rs.next()) {
		        	strItemId = rs.getString("cr_itemid");
		        }
		        rs.close();
		        pstmt.close();
	        }
      		retMsg = "0" + strItemId;

      		rs = null;
      		pstmt = null;
	        return retMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmr0020_Insert() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.cmr0020_Insert() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmr0020_Insert() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.cmr0020_Insert() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
		}
	}//end of cmr0020_Insert() method statement


	public String cmr0020_Delete(String UserId,ArrayList<HashMap<String,String>> dataList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs          = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean           findSw      = false;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			conn.setAutoCommit(false);

			String ItemId = "";
			for (int i=0 ; i<dataList.size() ; i++){
				ItemId = dataList.get(i).get("cr_itemid");
				findSw = false;
				strQuery.setLength(0);
	        	strQuery.append("select cr_status from cmr0020   \n");
	        	strQuery.append(" where cr_itemid=?                 \n");
	        	strQuery.append("   and cr_status='3'               \n");
	        	pstmt = conn.prepareStatement(strQuery.toString());
	        	pstmt.setString(1, ItemId);
	        	rs = pstmt.executeQuery();
	        	if (rs.next()) {
	        		if (!rs.getString("cr_status").equals("3")) findSw = true;
	        	}
	        	rs.close();
	        	pstmt.close();

	        	if (findSw == false) {
					strQuery.setLength(0);
					strQuery.append("select a.cr_itemid                     \n");
					strQuery.append("  from cmr1010 a,cmr0020 b             \n");
					strQuery.append(" where a.cr_baseitem=?                 \n");
					strQuery.append("   and a.cr_itemid<>?                  \n");
					strQuery.append("   and a.cr_itemid=b.cr_itemid         \n");
					strQuery.append("   and b.cr_lstver=0                   \n");
					strQuery.append("  group by a.cr_itemid                 \n");
					strQuery.append(" minus                                 \n");
					strQuery.append("select a.cr_itemid                     \n");
					strQuery.append("  from cmr1010 a,cmr1010 b             \n");
					strQuery.append(" where a.cr_baseitem=?                 \n");
					strQuery.append("   and a.cr_itemid=b.cr_itemid         \n");
					strQuery.append("   and b.cr_baseitem<>a.cr_baseitem    \n");
					strQuery.append("  group by a.cr_itemid                 \n");
			        pstmt = conn.prepareStatement(strQuery.toString());
			        //pstmt = new LoggableStatement(conn,strQuery.toString());
			        pstmt.setString(1, ItemId);
			        pstmt.setString(2, ItemId);
			        pstmt.setString(3, ItemId);
			        //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();

			        while(rs.next()) {
			        	strQuery.setLength(0);
			        	strQuery.append("delete cmr0020 where cr_itemid=?  ");
			        	pstmt2 = conn.prepareStatement(strQuery.toString());
			        	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
			        	pstmt2.setString(1, rs.getString("cr_itemid"));
			        	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			      		pstmt2.executeUpdate();
			      		pstmt2.close();

			        	strQuery.setLength(0);
			        	strQuery.append("delete cmr1010 where cr_itemid=?  ");
			        	pstmt2 = conn.prepareStatement(strQuery.toString());
			        	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
			        	pstmt2.setString(1, rs.getString("cr_itemid"));
			        	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
			      		pstmt2.executeUpdate();
			      		pstmt2.close();
			        }
			        rs.close();
			        pstmt.close();


			        if (!cmr0020_Delete_sub(UserId,ItemId,conn).equals("0")){
    					conn.rollback();
    					conn.close();
    					throw new Exception("cmr0020_Delete_sub 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			        }
	        	}
			}

	  		conn.commit();
	  		conn.close();

	  		rs = null;
	  		pstmt = null;
	  		pstmt2 = null;
	  		conn = null;

			return "0";


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
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
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0100.cmr0020_Delete() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.cmr0020_Delete() SQLException END ##");
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
					ecamsLogger.error("## Cmr0200.request_Check_In() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmd0100.cmr0020_Delete() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.cmr0020_Delete() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.cmr0020_Delete() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of cmr0020_Delete() method statement

	/**
	 * <PRE>
	 * 1. MethodName	: cmr0020_Delete_sub
	 * 2. ClassName		: Cmd0100
	 * 3. Commnet			: cmr0022,cmr1010,cmr0020 테이블에서 관련 프로그램삭제
	 * 4. 작성자				: Administrator
	 * 5. 작성일				: 2010. 12. 8. 오전 11:08:08
	 * </PRE>
	 * 		@return String
	 * 		@param UserId
	 * 		@param ItemId : 프로그램ID
	 * 		@param conn : Connection
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public String cmr0020_Delete_sub(String UserId,String ItemId,Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		boolean            findSw      = false;

		try {
			strQuery.setLength(0);
			strQuery.append("select cr_status from cmr0020   \n");
        	strQuery.append(" where cr_itemid=?                 \n");
        	strQuery.append("   and cr_status='3'               \n");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, ItemId);
        	rs = pstmt.executeQuery();
        	if (rs.next()) {
        		if (!rs.getString("cr_status").equals("3")) findSw = true;
        	}
        	rs.close();
        	pstmt.close();

        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, ItemId);
        	rs = pstmt.executeQuery();
        	if (rs.next()) {
        		if (!rs.getString("cr_status").equals("3")) findSw = true;
        	}
        	rs.close();
        	pstmt.close();

        	ecamsLogger.error("findSw:"+findSw + ", ItemId:" + ItemId);

        	if ( !findSw ) {
		       /* strQuery.setLength(0);
		        strQuery.append("delete cmr0022 where cr_baseitem=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();*/
        		strQuery.setLength(0);
		        strQuery.append("delete cmr0027 where cr_itemid=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();

        		strQuery.setLength(0);
		        strQuery.append("delete cmr0025 where cr_itemid=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();

        		strQuery.setLength(0);
		        strQuery.append("delete cmr0021 where cr_itemid=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();

		        strQuery.setLength(0);
		        strQuery.append("delete cmr1010 where cr_itemid=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();

		        strQuery.setLength(0);
		        strQuery.append("delete cmr0028 where cr_itemid=?  "); //2013.10.08 추가
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();
		  		
		        strQuery.setLength(0);
		        strQuery.append("delete cmr0020 where cr_itemid=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
		  	    pstmt.setString(1, ItemId);
		  		pstmt.executeUpdate();
		  		pstmt.close();
        	}

        	rs = null;
        	pstmt = null;
			return "0";



		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmr0020_Delete_sub() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd0100.cmr0020_Delete_sub() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.cmr0020_Delete_sub() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd0100.cmr0020_Delete_sub() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null)	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}

		}
	}//end of cmr0020_Delete_sub() method statement
	
	public Object[] getTmpDirConf(String pCode) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		
		try {
			conn = connectionContext.getConnection();
		    
			strQuery.append("SELECT a.CM_IPADDR,a.CM_PORT,b.CM_PATH \n");
			strQuery.append("FROM cmm0012 b,cmm0010 a               \n");
			strQuery.append("WHERE a.cm_stno = 'ECAMS'              \n");
			strQuery.append("  AND a.cm_stno=b.cm_stno              \n");
			strQuery.append("  AND cm_pathcd = ?                    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, pCode);
            rs = pstmt.executeQuery();
			if (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_ipaddr", rs.getString("cm_ipaddr"));
				rst.put("cm_port", rs.getString("cm_port"));
				rst.put("cm_path", rs.getString("cm_path"));
				rtList.add(rst);
			}
			
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
			return rtList.toArray();
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getTmpDirConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0100.getTmpDirConf() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getTmpDirConf() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0100.getTmpDirConf() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getTmpDirConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTmpDirConf() method statement
	
	/**  dir명령으로 만들어진 파일 읽어서 파일리스트 리턴
	 * @param homeDir : 사용자홈디렉토리
	 * @param outputfile : dir 후 결과파일 경로+명
	 * @param Syscd : 시스템코드
	 * @return
	 * @throws Exception
	 */
	public Object[] getPCFileList(String userHomeDir,String outputfile,String Syscd,String subDirYN,String selectDir,String strUserId) throws SQLException,Exception {

		Connection        conn        = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst	  = null;
		String            dirpath     = "";
		String            fullpath    = "";
		String            SvrHomePath = "";
		//String            filename    = "";
        String            str         = null;
        BufferedReader    in          = null;
        File              mFile       = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		
		try {
			//outputfile = "C:\\temp\\eCmd0102_BK06168566.filelist";//TEST시 사용
			
			mFile = new File(outputfile);
			
	        if (!mFile.isFile() || !mFile.exists()) {
				throw new Exception("디렉토리추출을 위한 작업에 실패하였습니다 [작성된 파일 없음]");
				
	        } else {
	        	
	        	conn = connectionContext.getConnection();
	        	
	        	//등록 될 홈경로 조회(CMM0031 테이블에서 CM_VOLPATH 그리고 로컬사용 사용 체크된 서버정보)
	        	strQuery.setLength(0);
            	strQuery.append("select b.cm_volpath \n");
            	strQuery.append("  from cmm0030 a, cmm0031 b \n");
            	strQuery.append(" where a.cm_syscd = ? \n");
            	strQuery.append("   and b.cm_syscd = a.cm_syscd \n");
            	strQuery.append("   and b.cm_svrcd = decode( a.cm_dirbase, null , '01' , a.cm_dirbase ) \n");
            	//strQuery.append("   and substr(b.cm_svruse,2,1) ='1' \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn,strQuery.toString());
	            pstmt.setString(1, Syscd);
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            if ( rs.next() ){
	            	SvrHomePath = rs.getString("cm_volpath");
	            	if ( SvrHomePath.lastIndexOf("/") == SvrHomePath.length()-1 ){
	            		SvrHomePath = SvrHomePath.substring(0, SvrHomePath.length()-1);
	            	}
	            }
	            rs.close();
	            pstmt.close();
	        	
	        	
	        	ThreadPool pool = new ThreadPool(10);
	        	int svCnt       = 0;
	        	
	            in = new BufferedReader(new FileReader(mFile));

	            fullpath = "";
	            dirpath = "";
	            rsval.clear();
	            
	            if (!subDirYN.equals("S")){//선택된 폴더에 있는 파일만 검색
	            	//ecamsLogger.error("dirpath[0]:"+dirpath+",userHomeDir:"+userHomeDir+",selectDir:"+selectDir + ",SvrHomePath:" + SvrHomePath);
            		selectDir = selectDir.replace("\\\\","\\");
            		dirpath = SvrHomePath + selectDir.replace(userHomeDir, "/").replace("\\", "/").replace("//", "/");//풀경로-홈경로
            		if ( dirpath.lastIndexOf("/") == dirpath.length()-1 ){
            			dirpath = dirpath.substring(0, dirpath.length()-1);
	            	}
            		dirpath = dirpath.replace("//", "/");
            		selectDir = selectDir.replace("\\\\","\\");
            		ecamsLogger.error("dirpath[1]:"+dirpath+",userHomeDir:"+userHomeDir+",selectDir:"+selectDir + ",SvrHomePath:" + SvrHomePath);
//            		/home/ecamst/ecamsdevc:/eCAMS/ecamsweb/webapps/ROOT/ecams_main,
            		//userHomeDir:c:\eCAMS,
            		//selectDir:c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main,
            		//SvrHomePath:/home/ecamst/ecamsdev
            		while ((str = in.readLine()) != null) {
		            	
		                if (str.length() > 0) {
		                		
							rst = new HashMap<String, String>();
							rst.put("userid", strUserId);
							rst.put("syscd", Syscd);
							rst.put("dirpath", dirpath);//풀path - 기준path 값
							rst.put("filename", str);//파일명
							rst.put("svrchg", "N");
							rst.put("pcdir", selectDir);
							++svCnt;
							pool.assign(new svrOpen_thread_file(rst,rsval,conn));
							rst = null;
							if (svCnt%10 == 0){
								Thread.sleep(10);
							}
		                }
		            }
		            if (svCnt>0) pool.complete();
		            
	            } else{//선택된 폴더에 하위폴더포함 해서 풀경로+파일 검색
	
		            while ((str = in.readLine()) != null) {
		            	
		                if (str.length() > 0) {
		                		
	                		//풀경로비교 해서 대상 경로 추출
	                		if (!fullpath.equals(str.substring(0, str.lastIndexOf("\\")))){
	                			fullpath = str.substring(0, str.lastIndexOf("\\"));//풀경로추출
	                			dirpath = SvrHomePath + fullpath.replace(userHomeDir, "/").replace("\\", "/").replace("//", "/");//풀경로-홈경로
	                			ecamsLogger.error("dirpath[2]:"+dirpath+",userHomeDir:"+userHomeDir+",fullpath:"+fullpath + ",SvrHomePath:" + SvrHomePath);
	                		}
//	                		c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main\ecams_aprving.jsp
//	                		c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main\eCAMSBase_test.jsp
//	                		c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main\faq_list.jsp
//	                		c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main\20140721\ecams_aprving.jsp
//	                		c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main\20140721\faq_list.jsp
//	                		
	                		//dirpath[2]:/home/ecamst/ecamsdev/c/://e/C/A/M/S//e/c/a/m/s/w/e/b//w/e/b/a/p/p/s//R/O/O/T//e/c/a/m/s/_/m/a/i/n/,
	                		//userHomeDir:,
	                		//fullpath:c:\eCAMS\ecamsweb\webapps\ROOT\ecams_main,
	                		//SvrHomePath:/home/ecamst/ecamsdev
							rst = new HashMap<String, String>();
							rst.put("userid", strUserId);
							rst.put("syscd", Syscd);
							rst.put("dirpath", dirpath);//풀path - 기준path 값
							rst.put("filename", str.substring(str.lastIndexOf("\\")+1));//파일명
							rst.put("svrchg", "N");
							rst.put("pcdir", fullpath);
							++svCnt;
							pool.assign(new svrOpen_thread_file(rst,rsval,conn));
							rst = null;
							if (svCnt%10 == 0){
								Thread.sleep(10);
							}
		                }
		            }
		            if (svCnt>0) pool.complete();
		        }
	            if (in != null) in.close();//파일 BufferedReader 제거
	            //if (mFile.isFile() && mFile.exists()) mFile.delete();//임시작업파일 삭제
	        }
	        rs.close();
	        pstmt.close();
	        conn.close();
	        rs = null;
	        pstmt = null;
	        conn = null;
	        
//	        ecamsLogger.error(rsval.toString());
	        
	        return rsval.toArray();
	        
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getPCFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## Cmd0100.getPCFileList() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd0100.getPCFileList() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## Cmd0100.getPCFileList() Exception END ##");				
			throw exception;
		}finally{
			if (in != null) in = null;
	        if (mFile != null) mFile = null;
	        if (rsval != null) rsval = null;
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmd0100.getPCFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
}//end of Cmd0100 class statement
