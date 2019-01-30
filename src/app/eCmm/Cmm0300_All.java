/*****************************************************************************************
	1. program ID	: Cmm0300_All.java
	2. create date	: 2008.12.08
	3. auth		    : is.choi
	4. update date	: 2008.12.24
	5. auth		    : No Name
	6. description	: [관리자] -> [결재정보] -> [결재정보전체조회]
*****************************************************************************************/

package app.eCmm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import app.common.LoggableStatement;

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
public class Cmm0300_All{


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
    public Object[] getSvrInfo(String SysCd,String SvrCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select a.cm_svrcd,a.cm_svrip,a.cm_svrusr,a.cm_seqno,  \n");
			strQuery.append("       b.cm_codename,a.cm_svrname                     \n");
			strQuery.append("  from cmm0031 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			if (SvrCd != "" && SvrCd != null) strQuery.append("and a.cm_svrcd=?    \n");
			strQuery.append("   and b.cm_macode='SERVERCD'                         \n");
			strQuery.append("   and b.cm_micode=a.cm_svrcd                         \n");
			strQuery.append("order by a.cm_svrcd,a.cm_svrip                        \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            if (SvrCd != "" && SvrCd != null) pstmt.setString(2, SvrCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_svrcd"));
				rst.put("cm_svrip", rs.getString("cm_svrip"));
				rst.put("cm_svrusr", rs.getString("cm_svrusr"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_seqno", Integer.toString(rs.getInt("cm_seqno")));
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

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getSvrInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_All.getSvrInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getSvrInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_All.getSvrInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0300_All.getSvrInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public Object[] getProgInfo(String SysCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select b.cm_micode,b.cm_codename                      \n");
			strQuery.append("  from cmm0036 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null          \n");
			strQuery.append("   and b.cm_macode='JAWON'                            \n");
			strQuery.append("   and b.cm_micode=a.cm_rsrccd                        \n");
			strQuery.append("order by b.cm_codename                                \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
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

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getProgInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_All.getProgInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getProgInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_All.getProgInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0300_All.getProgInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public Object[] getDirInfo(String SysCd) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select b.cm_micode,b.cm_codename,a.cm_dirpath         \n");
			strQuery.append("  from cmm0039 a,cmm0020 b                            \n");
			strQuery.append(" where a.cm_syscd=?                                   \n");
			strQuery.append("   and b.cm_macode='SYSDIR'                           \n");
			strQuery.append("   and b.cm_micode=a.cm_dircd                         \n");
			strQuery.append("order by a.cm_dircd                                   \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, SysCd);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
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

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getDirInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_All.getDirInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.getDirInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_All.getDirInfo() Exception END ##");
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
					ecamsLogger.error("## Cmm0300_All.getDirInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String sysCopy(HashMap<String,String> etcData,ArrayList<HashMap<String,String>> svrList,ArrayList<HashMap<String,String>> dirList) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			boolean           isrtSw      = false;
			String            strSysCd    = etcData.get("cm_syscd");
			String[] toSys = etcData.get("copysys").split(",");
			int               parmCnt     = 0;
			int               i           = 0;
			int               j           = 0;

			for (i=0;toSys.length>i;i++) {
				//시스템정보의 속성복사
				if (etcData.get("copy").equals("Y")) {
					for (j=0;svrList.size()>j;j++) {
						strQuery.setLength(0);
						strQuery.append("update cmm0030 set cm_sysinfo=?    \n");
						strQuery.append(" where cm_syscd=?                  \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, etcData.get("info"));
						pstmt.setString(2, toSys[i]);
						pstmt.executeUpdate();
						pstmt.close();
					}
				}

				String[] strRsrc = etcData.get("prog").split(",");

				for (j=0;strRsrc.length>j;j++) {
					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("delete cmm0032                          \n");
					strQuery.append(" where cm_syscd=? and cm_rsrccd=?       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();

					strQuery.setLength(0);
					strQuery.append("delete cmm0037                          \n");
					strQuery.append(" where cm_syscd=? and cm_rsrccd=?       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();

					strQuery.setLength(0);
					strQuery.append("delete cmm0036                          \n");
					strQuery.append(" where cm_syscd=? and cm_rsrccd=?       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();

					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmm0036                     \n");
					strQuery.append(" (cm_syscd,cm_rsrccd,cm_prcstep,        \n");
					strQuery.append("  cm_stepsta,cm_info,cm_vercnt,         \n");
					strQuery.append("  cm_basedir,cm_creatdt,cm_lastdt,      \n");
					strQuery.append("  cm_editor)                            \n");
					strQuery.append("(select ?,cm_rsrccd,cm_prcstep,         \n");
					strQuery.append("  cm_stepsta,cm_info,cm_vercnt,         \n");
					strQuery.append("  cm_basedir,cm_creatdt,cm_lastdt,      \n");
					strQuery.append("  ?                                     \n");
					strQuery.append("   from cmm0036                         \n");
					strQuery.append("  where cm_syscd=? and cm_rsrccd=?)     \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(++parmCnt, toSys[i]);
					pstmt.setString(++parmCnt, etcData.get("userid"));
					pstmt.setString(++parmCnt, strSysCd);
					pstmt.setString(++parmCnt, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();

					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmm0032                     \n");
					strQuery.append(" (cm_syscd,cm_rsrccd,cm_langcd)         \n");
					strQuery.append("(select ?, cm_rsrccd, cm_langcd         \n");
					strQuery.append("   from cmm0032                         \n");
					strQuery.append("  where cm_syscd=? and cm_rsrccd=?)     \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, strSysCd);
					pstmt.setString(3, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();

					parmCnt = 0;
					strQuery.setLength(0);
					strQuery.append("insert into cmm0037                     \n");
					strQuery.append(" (CM_SYSCD,CM_RSRCCD,CM_SAMERSRC,       \n");
					strQuery.append("  CM_SAMENAME,CM_BASEDIR,CM_SAMEDIR,    \n");
					strQuery.append("  CM_BASENAME,CM_FACTCD)                \n");
					strQuery.append("(select ?,CM_RSRCCD,CM_SAMERSRC,        \n");
					strQuery.append("  CM_SAMENAME,CM_BASEDIR,CM_SAMEDIR,    \n");
					strQuery.append("  CM_BASENAME,CM_FACTCD                 \n");
					strQuery.append("   from cmm0037                         \n");
					strQuery.append("  where cm_syscd=? and cm_rsrccd=?)     \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(++parmCnt, toSys[i]);
					pstmt.setString(++parmCnt, strSysCd);
					pstmt.setString(++parmCnt, strRsrc[j]);
					pstmt.executeUpdate();
					pstmt.close();
				}

				for (j=0;svrList.size()>j;j++) {
					strQuery.setLength(0);
					isrtSw = true;
					int seqNo = 0;
					strQuery.append("select cm_seqno from cmm0031            \n");
					strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
					strQuery.append("   and cm_svrip=? and cm_svrusr=?       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, toSys[i]);
					pstmt.setString(2, svrList.get(j).get("cm_micode"));
					pstmt.setString(3, svrList.get(j).get("cm_svrip"));
					pstmt.setString(4, svrList.get(j).get("cm_svrusr"));
					rs = pstmt.executeQuery();
					if (rs.next()) {
						isrtSw = false;
						seqNo = rs.getInt("cm_seqno");
					}
					rs.close();
					pstmt.close();

					if (seqNo == 0) {
						strQuery.setLength(0);
						strQuery.append("select nvl(max(cm_seqno),0)+1 max       \n");
						strQuery.append("  from cmm0031                          \n");
						strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, toSys[i]);
						pstmt.setString(2, svrList.get(j).get("cm_micode"));
						rs = pstmt.executeQuery();
						if (rs.next()) {
							seqNo = rs.getInt("max");
						}
						rs.close();
						pstmt.close();
					}
					strQuery.setLength(0);
					parmCnt = 0;
					if (isrtSw == true) {
						strQuery.append("insert into cmm0031                     \n");
						strQuery.append("   (CM_SYSCD,CM_SEQNO,CM_SVRCD,CM_SVRIP,\n");
						strQuery.append("    CM_SVRNAME,CM_VOLPATH,CM_CREATDT,   \n");
						strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_AGENT,     \n");
						strQuery.append("    CM_PORTNO,CM_SYSOS,cm_cmpsvr,       \n");
						strQuery.append("    cm_dbusr,cm_dbpass,CM_SVRUSE,       \n");
						strQuery.append("    CM_DIR,CM_PERMISSION,CM_SVRSTOP,    \n");
						strQuery.append("    CM_GRPID,CM_FTPUSR,CM_FTPPASS,      \n");
						strQuery.append("    CM_FORTIDIR,CM_SVRUSR)              \n");
						strQuery.append("(select ?, ?, CM_SVRCD,                 \n");
						strQuery.append("    CM_SVRIP,CM_SVRNAME,CM_VOLPATH,     \n");
						strQuery.append("    SYSDATE,SYSDATE,?,CM_AGENT,         \n");
						strQuery.append("    CM_PORTNO,CM_SYSOS,cm_cmpsvr,       \n");
						strQuery.append("    cm_dbusr,cm_dbpass,CM_SVRUSE,       \n");
						strQuery.append("    CM_DIR,CM_PERMISSION,CM_SVRSTOP,    \n");
						strQuery.append("    CM_GRPID,CM_FTPUSR,CM_FTPPASS,      \n");
						strQuery.append("    CM_FORTIDIR,CM_SVRUSR               \n");
						strQuery.append("  from cmm0031                          \n");
						strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
						strQuery.append("   and cm_seqno=?)                      \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(++parmCnt, toSys[i]);
						pstmt.setInt(++parmCnt, seqNo);
						pstmt.setString(++parmCnt, etcData.get("userid"));
						pstmt.setString(++parmCnt, strSysCd);
						pstmt.setString(++parmCnt, svrList.get(j).get("cm_micode"));
						pstmt.setInt(++parmCnt, Integer.parseInt(svrList.get(j).get("cm_seqno")));

					} else {
						strQuery.append("update cmm0031 set                      \n");
						//strQuery.append("   (CM_SYSCD,CM_SEQNO,CM_SVRCD,CM_SVRIP,\n");
						strQuery.append("    (CM_SVRNAME,CM_VOLPATH,             \n");
						strQuery.append("    CM_LASTUPDT,CM_EDITOR,CM_AGENT,     \n");
						strQuery.append("    CM_PORTNO,CM_SYSOS,cm_cmpsvr,       \n");
						strQuery.append("    cm_dbusr,cm_dbpass,CM_SVRUSE,       \n");
						strQuery.append("    CM_DIR,CM_PERMISSION,CM_SVRSTOP,    \n");
						strQuery.append("    CM_GRPID,CM_FTPUSR,CM_FTPPASS,      \n");
						strQuery.append("    CM_FORTIDIR) =                      \n");
						strQuery.append("(select CM_SVRNAME,CM_VOLPATH,          \n");
						strQuery.append("    SYSDATE,?,CM_AGENT,                 \n");
						strQuery.append("    CM_PORTNO,CM_SYSOS,cm_cmpsvr,       \n");
						strQuery.append("    cm_dbusr,cm_dbpass,CM_SVRUSE,       \n");
						strQuery.append("    CM_DIR,CM_PERMISSION,CM_SVRSTOP,    \n");
						strQuery.append("    CM_GRPID,CM_FTPUSR,CM_FTPPASS,      \n");
						strQuery.append("    CM_FORTIDIR                         \n");
						strQuery.append("  from cmm0031                          \n");
						strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
						strQuery.append("   and cm_seqno=?)                      \n");
						strQuery.append("where cm_syscd=? and cm_svrcd=?         \n");
						strQuery.append("  and cm_seqno=?                        \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(++parmCnt, etcData.get("userid"));
						pstmt.setString(++parmCnt, strSysCd);
						pstmt.setString(++parmCnt, svrList.get(j).get("cm_micode"));
						pstmt.setInt(++parmCnt, Integer.parseInt(svrList.get(j).get("cm_seqno")));
						pstmt.setString(++parmCnt, toSys[i]);
						pstmt.setString(++parmCnt, svrList.get(j).get("cm_micode"));
						pstmt.setInt(++parmCnt, seqNo);

					}
					////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					pstmt.executeUpdate();
					pstmt.close();

					//cmm0038복사여부
					if (etcData.get("item").equals("Y")) {
						strQuery.setLength(0);
						parmCnt = 0;
						strQuery.append("delete cmm0038                          \n");
						strQuery.append(" where cm_syscd=? and cm_svrcd=?        \n");
						strQuery.append("   and cm_seqno=?                       \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(++parmCnt, toSys[i]);
						pstmt.setString(++parmCnt, svrList.get(j).get("cm_micode"));
						pstmt.setInt(++parmCnt, seqNo);
						pstmt.executeUpdate();
						pstmt.close();

						strQuery.setLength(0);
						parmCnt = 0;
						strQuery.append("insert into cmm0038                     \n");
						strQuery.append(" (CM_SYSCD,CM_SVRCD,CM_SEQNO,CM_RSRCCD, \n");
						strQuery.append("  CM_JOBCD,CM_volPATH,CM_TIME)          \n");
						strQuery.append("(select b.cm_syscd,                     \n");
						strQuery.append("     a.CM_SVRCD,a.CM_SEQNO,a.CM_RSRCCD, \n");
						strQuery.append("     a.CM_JOBCD,a.CM_volPATH,a.CM_TIME  \n");
						strQuery.append("   from cmm0038 a,cmm0036 b             \n");
						strQuery.append(" where a.cm_syscd=? and a.cm_svrcd=?    \n");
						strQuery.append("   and a.cm_seqno=? and b.cm_syscd=?    \n");
						strQuery.append("   and a.cm_rsrccd=b.cm_rsrccd)         \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(++parmCnt, strSysCd);
						pstmt.setString(++parmCnt, svrList.get(j).get("cm_micode"));
						pstmt.setInt(++parmCnt, Integer.parseInt(svrList.get(j).get("cm_seqno")));
						pstmt.setString(++parmCnt, toSys[i]);
						pstmt.executeUpdate();
						pstmt.close();
					}
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.sysCopy() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_All.sysCopy() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.sysCopy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_All.sysCopy() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_All.sysCopy() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

    public String sysInfo_Close(String SysCd,String UserId) throws SQLException, Exception {
    	Connection        conn        = null;
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("update cmm0030 set cm_closedt=SYSDATE,          \n");
			strQuery.append("   cm_lastupdt=SYSDATE                          \n");
			strQuery.append("where cm_syscd=?                                \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;


			return "OK";

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.sysInfo_Close() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm0300_All.sysInfo_Close() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm0300_All.sysInfo_Close() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm0300_All.sysInfo_Close() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm0300_All.sysInfo_Close() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of sysInfo_Close

}//end of Cmm0300_All class statement
