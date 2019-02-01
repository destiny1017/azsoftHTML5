package app.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SystemPath {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] geteCAMSDir(String pCode) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		try {
			conn = connectionContext.getConnection();
		    rsval.clear();
			String[] dirCd = pCode.split(",");

			strQuery.append("SELECT cm_pathcd,cm_path            \n");
			strQuery.append("  FROM cmm0012                      \n");
			strQuery.append(" WHERE cm_stno = 'ECAMS'            \n");
			strQuery.append("   AND cm_pathcd in (               \n");
			for (int i=0;dirCd.length>i;i++) {
				if (i == 0) strQuery.append("? ");
				else strQuery.append(", ?");
			}
			strQuery.append(")                                   \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			for (int i=0;dirCd.length>i;i++) {
				pstmt.setString(i + 1, dirCd[i]);
			}
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_pathcd", rs.getString("cm_pathcd"));
				if (rs.getString("cm_path").indexOf(".")>0) {
					rst.put("cm_path", rs.getString("cm_path"));
				} else {
					if (rs.getString("cm_path").substring(rs.getString("cm_path").length()-1).equals("/"))
						rst.put("cm_path", rs.getString("cm_path"));
					else
						rst.put("cm_path", rs.getString("cm_path")+"/");
				}
				rsval.add(rst);
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
			ecamsLogger.error("## SystemPath.geteCAMSDir() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.geteCAMSDir() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.geteCAMSDir() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.geteCAMSDir() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.geteCAMSDir() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of geteCAMSDir() method statement

	public String getDevHome(String UserId,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();
			String rtString = "";

			strQuery.append("SELECT cd_devhome FROM cmd0300    \n");
			strQuery.append(" WHERE cd_syscd=? and cd_userid=? \n");
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, UserId);


			//ecamsLogger.debug(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			if (rs.next()){
				rtString = rs.getString("cd_devhome").replace("\\", "\\\\"); //로컬톰켓  막음
				//rtString = rs.getString("cd_devhome").replace("\\", "\\");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rtString;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDevHome() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDevHome() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDevHome() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDevHome() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDevHome() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDevHome() method statement
	public String getTmpDir(String pCode) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();
			String rtString = "";

			strQuery.append("SELECT cm_path \n");
			strQuery.append("FROM cmm0012 \n");
			strQuery.append("WHERE cm_stno = 'ECAMS' \n");
			strQuery.append("AND cm_pathcd = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, pCode);

            rs = pstmt.executeQuery();

			while (rs.next()){
				rtString = rs.getString("cm_path");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

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
	public String geteCAMSInfo() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();


		try {
			conn = connectionContext.getConnection();
			String rtString = "";

			strQuery.append("SELECT cm_ipaddr,cm_port \n");
			strQuery.append("FROM cmm0010 \n");
			strQuery.append("WHERE cm_stno = 'ECAMS' \n");

			pstmt = conn.prepareStatement(strQuery.toString());

            rs = pstmt.executeQuery();

			if (rs.next()){
				rtString = rs.getString("cm_ipaddr") + " " + rs.getString("cm_port")+ " 0";
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return rtString;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.geteCAMSInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.geteCAMSInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.geteCAMSInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.geteCAMSInfo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.geteCAMSInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of geteCAMSInfo() method statement
	public String getTmpDir_conn(String pCode, Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();

		try {
			String rtString = "";
			strQuery.setLength(0);
			strQuery.append("SELECT cm_path FROM cmm0012 \n");
			strQuery.append(" WHERE cm_stno = 'ECAMS' AND cm_pathcd = ? \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, pCode);
            rs = pstmt.executeQuery();

			if (rs.next()){
				rtString = rs.getString("cm_path");
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;

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
		}
	}//end of SelectUserInfo() method statement


	public Object[] getSysPath(String UserID,String syscd,String jobcd,String reqcd,String adminCk) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		CreateXml      ecmmtb      = new CreateXml();
		ArrayList<Document> list = new ArrayList<Document>();
		Object[] returnObjectArray	 = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			Boolean AdminYn= false;

			if (adminCk.toUpperCase().equals("Y") || adminCk.equals("1")){
				//AdminYn = userInfo.isAdmin(UserID);
				AdminYn = userInfo.isAdmin_conn(UserID,conn);
			}

			userInfo = null;

			conn = connectionContext.getConnection();

			if (AdminYn){
				strQuery.append("select a.CM_SEQNO,a.CM_DIRPATH,a.CM_UPSEQ,a.CM_FULLPATH,a.CM_DSNCD \n");
				strQuery.append("from cmm0074 a where \n");
				strQuery.append("a.cm_syscd = ? \n");
				strQuery.append("START WITH a.cm_upseq is null \n");
				strQuery.append("CONNECT BY PRIOR a.cm_seqno = a.cm_upseq \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, syscd);
			} else{
				strQuery.append("select distinct a.CM_SEQNO,a.CM_DIRPATH,a.CM_UPSEQ,a.CM_FULLPATH,a.CM_DSNCD from cmm0074 a \n");
				strQuery.append("where a.cm_syscd = ? \n");
				strQuery.append("START WITH a.cm_dsncd in (  select distinct d.cm_dsncd \n");
				strQuery.append("							 from cmm0044 c,cmm0073 d, cmm0072 e, cmm0036 f \n");
				strQuery.append("					  		 where   c.cm_syscd    = ? \n");
				strQuery.append("					 		 and     c.cm_userid   = ? \n");
				if (!(jobcd.equals("") || jobcd.length() == 0 || jobcd == null)){
					strQuery.append("						 and     rtrim(c.cm_jobcd)= ? \n");
				}
				strQuery.append("					  		 and     c.cm_syscd    = d.cm_syscd \n");
				strQuery.append("					  		 and     c.cm_jobcd    = d.cm_jobcd \n");
				strQuery.append("					  		 and     d.cm_syscd    = e.cm_syscd \n");
				strQuery.append("					  		 and     d.cm_dsncd    = e.cm_dsncd \n");
				strQuery.append("					  		 and     d.cm_syscd    = f.cm_syscd \n");
				if (reqcd.equals("98") || reqcd.equals("99")){
					strQuery.append("					    and     substr(f.cm_info,18,1)='1' 	 \n");
				}
				else if (reqcd.equals("99") == false && reqcd.equals("97") == false){
					strQuery.append("					    and     substr(f.cm_info,2,1)='1' 	 \n");
				}
				strQuery.append("					  		and     f.cm_closedt  is null  ) \n");
				strQuery.append("CONNECT BY PRIOR a.cm_upseq = a.cm_seqno \n");
				strQuery.append("order by a.cm_seqno\n");

				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn,strQuery.toString());

				pstmt.setString(1, syscd);
				pstmt.setString(2, syscd);
				pstmt.setString(3, UserID);
				if (!(jobcd.equals("") || jobcd.length() == 0 || jobcd == null)){
					pstmt.setString(4, jobcd);
				}
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");

			while (rs.next()){
				ecmmtb.addXML(rs.getString("cm_seqno"),rs.getString("cm_seqno"),
						rs.getString("cm_dirpath"),rs.getString("cm_upseq"),
						rs.getString("cm_fullpath"),rs.getString("cm_dsncd"),
						"true",rs.getString("cm_upseq"));
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;


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
			if (returnObjectArray != null)	returnObjectArray = null;
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


	//public Object[] getSysPath(String UserID,String syscd,String sysgb,String sysfc1,String dirbase ,String jobcd,String reqcd,String adminCk) throws SQLException, Exception {
	public Object[] getDirPath(String UserID,String syscd,String jobcd,String reqcd,String adminCk) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		CreateXml         ecmmtb      = new CreateXml();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String[]          pathDepth   = null;
		String            strDir      = null;
		int               upSeq       = 0;
		int               maxSeq      = 0;
		boolean           findSw      = false;
		int               parmCnt     = 0;
		ArrayList<Document> list = new ArrayList<Document>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			Boolean AdminYn= false;

			//if (adminCk.toUpperCase().equals("Y") || adminCk.equals("1")){
				//AdminYn = userInfo.isAdmin(UserID);
				AdminYn = userInfo.isAdmin_conn(UserID,conn);
			//}
			userInfo = null;

			conn = connectionContext.getConnection();
			strQuery.append("select /*+ leading(a) use_nl(a c) use_nl(c d) */ '/' || b.cm_codename || a.cm_dirpath cm_dirpath,a.cm_dsncd, c.cr_rsrccd,b.cm_seqno \n");
			strQuery.append("  from cmm0020 b,cmm0070 a,                                       \n");
			if (!AdminYn){//관리자가 아닐경우
				strQuery.append("   (select cr_syscd,cr_dsncd,cr_rsrccd,cr_itemid from cmr0020 \n");
			    strQuery.append("        where cr_syscd=?) c,                                  \n");
				strQuery.append("      (select j.cr_itemid from cmm0044 k,CMR0023 j            \n");
			    strQuery.append("        where k.cm_syscd=? and k.cm_userid=?                  \n");
			    strQuery.append("         and k.cm_closedt is null                             \n");
			    strQuery.append("         and k.cm_syscd=j.cr_syscd                            \n");
			    strQuery.append("         and k.cm_jobcd=j.cr_jobcd) d,                        \n");
			} else {
				strQuery.append("   (select distinct cr_syscd,cr_dsncd,cr_rsrccd from cmr0020  \n");
			    strQuery.append("        where cr_syscd=?) c,                                  \n");
			}
			strQuery.append("      (select cm_syscd,cm_rsrccd from cmm0036                     \n");
			strQuery.append("        where cm_syscd=? and substr(cm_info,2,1)='1'              \n");
			strQuery.append("          and substr(cm_info,26,1)='0' and cm_closedt is null     \n");
			strQuery.append("        minus                                                     \n");
			strQuery.append("       select cm_syscd,cm_samersrc from cmm0037                   \n");
			strQuery.append("        where cm_syscd=? and cm_rsrccd<>cm_samersrc) e            \n");
			strQuery.append(" where a.cm_syscd=?                                               \n");
			strQuery.append("   and a.cm_syscd=c.cr_syscd and a.cm_dsncd=c.cr_dsncd            \n");
			if (!AdminYn){//관리자가 아닐경우
			   strQuery.append("and c.cr_itemid=d.cr_itemid                                    \n");
			}
			strQuery.append("   and b.cm_macode='JAWON' and b.cm_micode=c.cr_rsrccd            \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd          \n");
			strQuery.append(" group by b.cm_micode,b.cm_codename,a.cm_dirpath,a.cm_dsncd,c.cr_rsrccd,b.cm_seqno      \n");
			strQuery.append(" order by b.cm_seqno,b.cm_micode,b.cm_codename,a.cm_dirpath,a.cm_dsncd,c.cr_rsrccd       \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, syscd);
			if (!AdminYn){//관리자가 아닐경우
				pstmt.setString(++parmCnt, syscd);
			    pstmt.setString(++parmCnt, UserID);
			}
			pstmt.setString(++parmCnt, syscd);
			pstmt.setString(++parmCnt, syscd);
			pstmt.setString(++parmCnt, syscd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","isBranch");
            rsval.clear();
			while (rs.next()){
				pathDepth = rs.getString("cm_dirpath").substring(1).split("/");

				strDir = "/";
				upSeq = 0;
				findSw = false;
				for (int i = 0;pathDepth.length > i;i++) {
					if (strDir.length() > 1 ) {
						strDir = strDir + "/";
					}
					strDir = strDir + pathDepth[i];
					//ecamsLogger.debug("strDir====>" + strDir);
					findSw = false;
					if (rsval.size() > 0) {
						for (int j = 0;rsval.size() > j;j++) {
							if (rsval.get(j).get("cm_fullpath").equals(strDir)) {
								upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));
								findSw = true;
							}
						}
					} else {
						findSw = false;
					}
					if (findSw == false) {
						maxSeq = maxSeq + 1;
						rst = new HashMap<String,String>();
						rst.put("cm_dirpath",pathDepth[i]);
						rst.put("cm_fullpath",strDir);
						rst.put("cm_upseq",Integer.toString(upSeq));
						rst.put("cm_seqno",Integer.toString(maxSeq));
						if (i == (pathDepth.length - 1)) {
						   rst.put("cm_dsncd",rs.getString("cm_dsncd"));
						}
						rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
						rsval.add(maxSeq - 1, rst);
						rst = null;
						upSeq = maxSeq;
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug("rsval.size====>"+ Integer.toString(rsval.size()));
			if (rsval.size() > 0) {
				for (int i = 0;rsval.size() > i;i++) {
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cm_dsncd"),
							rsval.get(i).get("cr_rsrccd"),"true",rsval.get(i).get("cm_upseq"));
				}
			}


			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDirPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDirPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDirPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath() method statement


	public Object[] getDirPath2(String UserID,String syscd,String SvrCd,String SeqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		CreateXml         ecmmtb      = new CreateXml();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String[]          pathDepth   = null;
		String            strDir      = null;
		int               upSeq       = 0;
		int               maxSeq      = 0;
		int               parmCnt     = 0;
		boolean           findSw      = false;
		ArrayList<Document> list = new ArrayList<Document>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			Boolean AdminYn= false;

			//AdminYn = userInfo.isAdmin(UserID);
			AdminYn = userInfo.isAdmin_conn(UserID,conn);
			userInfo = null;

			//conn = connectionContext.getConnection();
			strQuery.append("select a.cm_dirpath cm_dirpath,a.cm_dsncd                         \n");
			strQuery.append("  from cmm0070 a,cmr0020 c                                        \n");
			strQuery.append(" where a.cm_syscd=?                                               \n");
			strQuery.append("   and a.cm_syscd=c.cr_syscd and a.cm_dsncd=c.cr_dsncd            \n");
			if (!AdminYn){//관리자가 아닐경우
			   strQuery.append("   and c.cr_jobcd in (select cm_jobcd from cmm0044             \n");
			   strQuery.append("                       where cm_syscd=? and cm_userid=?        \n");
			   strQuery.append("                         and cm_closedt is null)               \n");
			}
			if (SvrCd != null && SvrCd != "") {
				strQuery.append("and c.cr_rsrccd in (select cm_rsrccd from cmm0038             \n");
				strQuery.append("                     where cm_syscd=? and cm_svrcd=?          \n");
				strQuery.append("                       and cm_seqno=?)                        \n");
			}
			strQuery.append(" group by a.cm_dirpath,a.cm_dsncd                                 \n");
			strQuery.append(" order by a.cm_dirpath,a.cm_dsncd                                 \n");

			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());

			pstmt.setString(++parmCnt, syscd);
			if (!AdminYn){//관리자가 아닐경우
				pstmt.setString(++parmCnt, syscd);
			    pstmt.setString(++parmCnt, UserID);
			}
			if (SvrCd != null && SvrCd != "") {
				pstmt.setString(++parmCnt, syscd);
				pstmt.setString(++parmCnt, SvrCd);
				pstmt.setInt(++parmCnt, Integer.parseInt(SeqNo));
			}
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();

	        ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");
	        rsval.clear();
			while (rs.next()){
				pathDepth = rs.getString("cm_dirpath").substring(1).split("/");

				strDir = "/";
				upSeq = 0;
				findSw = false;
				for (int i = 0;pathDepth.length > i;i++) {
					if (strDir.length() > 1 ) {
						strDir = strDir + "/";
					}
					strDir = strDir + pathDepth[i];
					//ecamsLogger.debug("strDir====>" + strDir);
					findSw = false;
					if (rsval.size() > 0) {
						for (int j = 0;rsval.size() > j;j++) {
							if (rsval.get(j).get("cm_fullpath").equals(strDir)) {
								upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));
								findSw = true;
							}
						}
					} else {
						findSw = false;
					}
					if (findSw == false) {
						maxSeq = maxSeq + 1;

						//ecamsLogger.debug("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
						rst = new HashMap<String,String>();
						rst.put("cm_dirpath",pathDepth[i]);
						rst.put("cm_fullpath",strDir);
						rst.put("cm_upseq",Integer.toString(upSeq));
						rst.put("cm_seqno",Integer.toString(maxSeq));
						if (i == (pathDepth.length - 1)) {
						   rst.put("cm_dsncd",rs.getString("cm_dsncd"));
						}
						rsval.add(maxSeq - 1, rst);
						rst = null;
						upSeq = maxSeq;
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			//ecamsLogger.debug("rsval.size====>"+ Integer.toString(rsval.size()));
			if (rsval.size() > 0) {
				for (int i = 0;rsval.size() > i;i++) {
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cm_dsncd"),
							"true",rsval.get(i).get("cm_upseq"));
				}
			}


			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath2() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDirPath2() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath2() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDirPath2() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDirPath2() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath2() method statement

//	public Object[] getDirPath3(String UserId,String SysCd,String RsrcCd,String Info,String seqNo) throws SQLException, Exception {
//		Connection        conn        = null;
//		PreparedStatement pstmt       = null;
//		ResultSet         rs          = null;
//		CreateXml         ecmmtb      = new CreateXml();
//		StringBuffer      strQuery    = new StringBuffer();
//		UserInfo		  userInfo	  = new UserInfo();
//		//String            strDsn      = null;
//		String            strHome     = "";
//		String[]          pathDepth   = null;
//		String            strDir      = "";
//		int               upSeq       = 0;
//		int               maxSeq      = 100;
//		boolean           findSw      = false;
//		int               parmCnt     = 0;
//		ArrayList<Document> list = new ArrayList<Document>();
//		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String>			  rst		  = null;
//
//		Object[] returnObjectArray = null;
//		ConnectionContext connectionContext = new ConnectionResource();
//
//		Boolean			  AdminYn= false;
//
//		try {
//			conn = connectionContext.getConnection();
//
//			AdminYn = userInfo.isAdmin_conn(UserId,conn);
//			userInfo = null;
//			
//			//conn = connectionContext.getConnection();
//			strQuery.append("select b.cm_dirpath volpath,b.cm_dsncd,e.cm_volpath         \n");
//			strQuery.append("  from cmm0070 b,cmm0031 a,cmm0030 c,cmr0020 d,cmm0038 e ");
//			if (!AdminYn) {
//				strQuery.append(",cmm0044 f \n");
//			}
//			strQuery.append(" where c.cm_syscd=?                                         \n");
//			strQuery.append("   and c.cm_syscd=a.cm_syscd and c.cm_dirbase=a.cm_svrcd    \n");
//			strQuery.append("   and a.cm_syscd=b.cm_syscd and a.cm_closedt is null       \n");
//			strQuery.append("   and a.cm_syscd=e.cm_syscd and a.cm_svrcd=e.cm_svrcd      \n");
//			strQuery.append("   and a.cm_seqno=e.cm_seqno and e.cm_rsrccd=?              \n");
//			if (!AdminYn){
//				strQuery.append("and f.cm_userid=?           \n");
//				strQuery.append("and f.cm_syscd=?           \n");
//				strQuery.append("and f.cm_syscd=d.cr_syscd           \n");
//				strQuery.append("and f.cm_jobcd=d.cr_jobcd           \n");
//			}
//			strQuery.append("   and d.cr_rsrccd=?                                        \n");
//			strQuery.append("   and b.cm_syscd=d.cr_syscd and b.cm_dsncd=d.cr_dsncd      \n");
//			strQuery.append(" group by b.cm_dirpath,b.cm_dsncd,e.cm_volpath              \n");
//			strQuery.append(" order by b.cm_dirpath                                      \n");
//
//			//pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
//			pstmt.setString(++parmCnt, SysCd);
//			pstmt.setString(++parmCnt, RsrcCd);
//			if (!AdminYn) {
//				pstmt.setString(++parmCnt, UserId);
//				pstmt.setString(++parmCnt, SysCd);
//			}
//			pstmt.setString(++parmCnt, RsrcCd);
//
//			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");
//            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//	        rs = pstmt.executeQuery();
//	        rsval.clear();
//	        while (rs.next()){
//	        	strDir = "";
//				if (rs.getString("cm_volpath") != null) {
//					strDir = rs.getString("cm_volpath");
//					if (strDir.length()>0){
//						if (strDir.substring(strDir.length()-1).equals("/")){
//							strDir = strDir.substring(0,strDir.length()-1);
//						}
//					}
//					strHome = strDir;
//					if (rs.getString("volpath").length() >= strHome.length()){
//						if (!rs.getString("volpath").substring(0,strHome.length()).equals(strHome)) {
//							strHome = "";
//						}
//					}
//				} else strHome = "";
//				if(rs.getString("volpath").length() >= strDir.length()){
//					if (rs.getString("volpath").substring(0,strDir.length()).equals(strDir)) {
//						strDir = rs.getString("volpath").replace(strDir, "");
//					} else strDir = "";
//				}
//				if (strDir.length() > 0){
//					pathDepth = strDir.substring(1).split("/");
//
//					strDir = "/";
//					upSeq = Integer.parseInt(seqNo);
//					findSw = false;
//
//					for (int i = 0;pathDepth.length > i;i++) {
//						if (strDir.length() > 1 ) {
//							strDir = strDir + "/";
//						}
//						strDir = strDir + pathDepth[i];
//						//ecamsLogger.error("strDir====>" + strDir);
//						findSw = false;
//						if (rsval.size() > 0) {
//							for (int j = 0;rsval.size() > j;j++) {
//								//ecamsLogger.error("cm_fullpath====>" + rsval.get(j).get("cm_fullpath"));
//								if (rsval.get(j).get("cm_fullpath").equals(strHome+strDir)) {
//									upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));
//
//									//rsval.set(j, element)
//									//rst.put("branch","true");
//									findSw = true;
//								}
//							}
//						} else {
//							findSw = false;
//						}
//						if (findSw == false) {
//							maxSeq = maxSeq + 1;
//
//							//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
//							rst = new HashMap<String,String>();
//							rst.put("cm_dirpath",pathDepth[i]);
//							rst.put("cm_fullpath",strHome+strDir);
//							rst.put("cm_upseq",Integer.toString(upSeq));
//							rst.put("cm_seqno",Integer.toString(maxSeq));
//							rst.put("cr_rsrccd",RsrcCd);
//							if (Info.substring(26,27).equals("0") && rs.getString("volpath").equals(strHome+strDir)){
//								rst.put("cr_dsncd", rs.getString("cm_dsncd"));
//							}else{
//								rst.put("cr_dsncd", "");
//							}
//							if (rs.getString("volpath").equals(strHome+strDir)){
//								rst.put("branch", "false");
//							}
//							rsval.add(maxSeq - 101, rst);
//							rst = null;
//							upSeq = maxSeq;
//						}
//					}
//				}
//			}//end of while-loop statement
//			rs.close();
//			pstmt.close();
//			conn.close();
//
//			rs = null;
//			pstmt = null;
//			conn = null;
//
//			if (rsval.size() > 0) {
//				//ecamsLogger.debug(rsval.toString());
//
//			    String strBran = "";
//				for (int i = 0;rsval.size() > i;i++) {
//					strBran = "false";
//					for (int j=0;rsval.size()>j;j++) {
//						if (i != j) {
//							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
//								strBran = "true";
//								break;
//							}
//						}
//					}
//					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
//							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
//							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cr_dsncd"),
//							rsval.get(i).get("cr_rsrccd"),Info,strBran,rsval.get(i).get("cm_upseq"));
//				}
//			}
//
//
//			list.add(ecmmtb.getDocument());
//
//			returnObjectArray = list.toArray();
//
//			list = null;
//			ecmmtb = null;
//			//ecamsLogger.error(ecmmtb.xml_toStr());
//
//			return returnObjectArray;
//
//		} catch (SQLException sqlexception) {
//			sqlexception.printStackTrace();
//			ecamsLogger.error("## SystemPath.getDirPath3() SQLException START ##");
//			ecamsLogger.error("## Error DESC : ", sqlexception);
//			ecamsLogger.error("## SystemPath.getDirPath3() SQLException END ##");
//			throw sqlexception;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## SystemPath.getDirPath3() Exception START ##");
//			ecamsLogger.error("## Error DESC : ", exception);
//			ecamsLogger.error("## SystemPath.getDirPath3() Exception END ##");
//			throw exception;
//		}finally{
//			if (strQuery != null) 	strQuery = null;
//			if (returnObjectArray != null)	returnObjectArray = null;
//			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (conn != null){
//				try{
//					ConnectionResource.release(conn);
//				}catch(Exception ex3){
//					ecamsLogger.error("## SystemPath.getDirPath3() connection release exception ##");
//					ex3.printStackTrace();
//				}
//			}
//		}
//	}//end of getDirPath3() method statement
	
	
	public List<HashMap<String, String>> getDirPath3(String UserId,String SysCd,String RsrcCd,String Info,String seqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		//String            strDsn      = null;
		String            strHome     = "";
		String[]          pathDepth   = null;
		String            strDir      = "";
		int               upSeq       = 0;
		int               maxSeq      = 100;
		boolean           findSw      = false;
		int               parmCnt     = 0;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();
		
		
		

		Boolean			  AdminYn= false;

		try {
			conn = connectionContext.getConnection();

			AdminYn = userInfo.isAdmin_conn(UserId,conn);
			userInfo = null;
			
			//conn = connectionContext.getConnection();
			strQuery.append("select b.cm_dirpath volpath,b.cm_dsncd,a.cm_volpath\n");
			strQuery.append("  from cmm0070 b,cmm0031 a,cmm0030 c,cmr0020 d \n");
			if (!AdminYn) {
				strQuery.append(",cmm0044 f \n");
			}
			strQuery.append(" where c.cm_syscd=?                                       \n");
			strQuery.append("   and c.cm_syscd=a.cm_syscd and c.cm_dirbase=a.cm_svrcd  \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd and a.cm_closedt is null     \n");
			if (!AdminYn){
				strQuery.append("and f.cm_userid=?           \n");
				strQuery.append("and f.cm_syscd=?           \n");
				strQuery.append("and f.cm_syscd=d.cr_syscd           \n");
				strQuery.append("and f.cm_jobcd=d.cr_jobcd           \n");
			}
			strQuery.append("   and d.cr_rsrccd=?                                        \n");
			strQuery.append("   and b.cm_syscd=d.cr_syscd and b.cm_dsncd=d.cr_dsncd      \n");
			strQuery.append(" group by b.cm_dirpath,b.cm_dsncd,a.cm_volpath              \n");
			strQuery.append(" order by b.cm_dirpath                                      \n");

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (!AdminYn) {
				pstmt.setString(++parmCnt, UserId);
				pstmt.setString(++parmCnt, SysCd);
			}
			pstmt.setString(++parmCnt, RsrcCd);

            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        rsval.clear();
	        while (rs.next()){
	        	strDir = "";
				if (rs.getString("cm_volpath") != null) {
					strDir = rs.getString("cm_volpath");
					if (strDir.length()>0){
						if (strDir.substring(strDir.length()-1).equals("/")){
							strDir = strDir.substring(0,strDir.length()-1);
						}
					}
					strHome = strDir;
					if (rs.getString("volpath").length() >= strHome.length()){
						if (!rs.getString("volpath").substring(0,strHome.length()).equals(strHome)) {
							strHome = "";
						}
					}
				} else strHome = "";
				if(rs.getString("volpath").length() >= strDir.length()){
					if (rs.getString("volpath").substring(0,strDir.length()).equals(strDir)) {
						strDir = rs.getString("volpath").replace(strDir, "");
					} else strDir = "";
				}
				if (strDir.length() > 0){
					pathDepth = strDir.substring(1).split("/");

					strDir = "/";
					upSeq = Integer.parseInt(seqNo);
					findSw = false;

					for (int i = 0;pathDepth.length > i;i++) {
						if (strDir.length() > 1 ) {
							strDir = strDir + "/";
						}
						strDir = strDir + pathDepth[i];
						//ecamsLogger.error("strDir====>" + strDir);
						findSw = false;
						if (rsval.size() > 0) {
							for (int j = 0;rsval.size() > j;j++) {
								//ecamsLogger.error("cm_fullpath====>" + rsval.get(j).get("cm_fullpath"));
								if (rsval.get(j).get("cm_fullpath").equals(strHome+strDir)) {
									upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));

									//rsval.set(j, element)
									//rst.put("branch","true");
									findSw = true;
								}
							}
						} else {
							findSw = false;
						}
						if (findSw == false) {
							maxSeq = maxSeq + 1;

							//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
							rst = new HashMap<String,String>();
							rst.put("cm_dirpath",pathDepth[i]);
							rst.put("cm_fullpath",strHome+strDir);
							rst.put("cm_upseq",Integer.toString(upSeq));
							rst.put("cm_seqno",Integer.toString(maxSeq));
							rst.put("cr_rsrccd",RsrcCd);
							rst.put("imagesrc"			, "/img/folderDefaultClosed.gif");
							if (Info.substring(26,27).equals("0") && rs.getString("volpath").equals(strHome+strDir)){
								rst.put("cr_dsncd", rs.getString("cm_dsncd"));
							}else{
								rst.put("cr_dsncd", "");
							}
							if (rs.getString("volpath").equals(strHome+strDir)){
								rst.put("branch", "false");
							}
							rsval.add(maxSeq - 101, rst);
							rst = null;
							upSeq = maxSeq;
						}
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (rsval.size() > 0) {
				//ecamsLogger.debug(rsval.toString());

			    String strBran = "";
				for (int i = 0;rsval.size() > i;i++) {
					strBran = "false";
					for (int j=0;rsval.size()>j;j++) {
						if (i != j) {
							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
								strBran = "true";
								break;
							}
						}
					}
				}
			}

			return rsval;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath3() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDirPath3() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath3() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDirPath3() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDirPath3() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath3() method statement
	
	
	
	public Object[] getDirPath_Job(String UserId,String ReqCd,String SysCd,String JobCd,String HomePath,String seqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		CreateXml         ecmmtb      = new CreateXml();
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		//String            strDsn      = null;
		String            strHome     = "";
		String[]          pathDepth   = null;
		String            strDir      = "";
		int               upSeq       = 0;
		int               maxSeq      = 100;
		boolean           findSw      = false;
		int               parmCnt     = 0;
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		Boolean			  AdminYn= false;

		try {
			conn = connectionContext.getConnection();

			AdminYn = userInfo.isAdmin_conn(UserId,conn);
			userInfo = null;
			strDir = HomePath;
			//conn = connectionContext.getConnection();
			strQuery.append("select b.cm_dirpath,b.cm_dsncd                              \n");
			strQuery.append("  from cmm0070 b,cmr0020 d                                  \n");
			strQuery.append(" where d.cr_syscd=? and d.cr_jobcd=?                        \n");
			strQuery.append("   and d.cr_status<>'9'                                     \n");
			if (ReqCd.equals("93")) {
				strQuery.append("   and d.cr_devsta='9' and d.cr_teststa<>'9'            \n");
			} else if (ReqCd.equals("94")) {
				strQuery.append("   and d.cr_devsta='9' and d.cr_realsta<>'9'            \n");
			} else {
				strQuery.append("   and d.cr_devsta<>'9'                                 \n");
				strQuery.append("   and d.cr_teststa<>'9'                                \n");
				strQuery.append("   and d.cr_realsta<>'9'                                \n");
			}			
			strQuery.append("   and b.cm_syscd=d.cr_syscd and b.cm_dsncd=d.cr_dsncd      \n");
			strQuery.append(" group by b.cm_dirpath,b.cm_dsncd                           \n");
			strQuery.append(" order by b.cm_dirpath                                      \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			pstmt.setString(++parmCnt, JobCd);

			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_jobcd","isBranch");
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        rsval.clear();
	        while (rs.next()){
				if(rs.getString("cm_dirpath").length() >= HomePath.length()){
					if (rs.getString("cm_dirpath").substring(0,HomePath.length()).equals(HomePath)) {
						strDir = rs.getString("cm_dirpath").replace(HomePath, "");
					} else strDir = "";
				}
				if (strDir.length() > 0){
					pathDepth = strDir.substring(1).split("/");

					strDir = "/";
					upSeq = Integer.parseInt(seqNo);
					findSw = false;

					for (int i = 0;pathDepth.length > i;i++) {
						if (strDir.length() > 1 ) {
							strDir = strDir + "/";
						}
						strDir = strDir + pathDepth[i];
						//ecamsLogger.error("strDir====>" + strDir);
						findSw = false;
						if (rsval.size() > 0) {
							for (int j = 0;rsval.size() > j;j++) {
								//ecamsLogger.error("cm_fullpath====>" + rsval.get(j).get("cm_fullpath"));
								if (rsval.get(j).get("cm_fullpath").equals(HomePath+strDir)) {
									upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));

									//rsval.set(j, element)
									//rst.put("branch","true");
									findSw = true;
								}
							}
						} else {
							findSw = false;
						}
						if (findSw == false) {
							maxSeq = maxSeq + 1;

							//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
							rst = new HashMap<String,String>();
							rst.put("cm_dirpath",pathDepth[i]);
							rst.put("cm_fullpath",HomePath+strDir);
							rst.put("cm_upseq",Integer.toString(upSeq));
							rst.put("cm_seqno",Integer.toString(maxSeq));
							rst.put("cm_jobcd",JobCd);
							if (rs.getString("cm_dirpath").equals(HomePath+strDir)){
								rst.put("cr_dsncd", rs.getString("cm_dsncd"));
							}else{
								rst.put("cr_dsncd", "");
							}
							if (rs.getString("cm_dirpath").equals(HomePath+strDir)){
								rst.put("branch", "false");
							}
							rsval.add(maxSeq - 101, rst);
							rst = null;
							upSeq = maxSeq;
						}
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (rsval.size() > 0) {
				//ecamsLogger.debug(rsval.toString());

			    String strBran = "";
				for (int i = 0;rsval.size() > i;i++) {
					strBran = "false";
					for (int j=0;rsval.size()>j;j++) {
						if (i != j) {
							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
								strBran = "true";
								break;
							}
						}
					}
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cr_dsncd"),
							rsval.get(i).get("cr_rsrccd"),strBran,rsval.get(i).get("cm_upseq"));
				}
			}


			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath3() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDirPath3() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath3() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDirPath3() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDirPath3() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath3() method statement
	public Object[] getOutHomeDirForNT(String UserID,String SysCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		CreateXml         ecmmtb      = new CreateXml();
		int               maxSeq      = 0;
		String            dsncd       = "";
		ArrayList<Document> list = new ArrayList<Document>();


		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");

			strQuery.append("select a.cm_svrname,a.cm_volpath \n");
			strQuery.append("  from cmm0031 a,cmm0036 b,cmm0038 c \n");
			strQuery.append(" where a.cm_syscd = ? \n");
			strQuery.append("   and b.cm_syscd = a.cm_syscd and c.cm_syscd = a.cm_syscd \n");
			strQuery.append("   and a.cm_closedt is null and b.cm_closedt is null \n");
			strQuery.append("   and a.cm_svrcd = '01' and c.cm_svrcd = a.cm_svrcd \n");
			strQuery.append("   and substr(b.cm_info,2,1)='1' \n");
			strQuery.append("   and substr(b.cm_info,26,1)='0' \n");
			strQuery.append("   and c.cm_rsrccd = b.cm_rsrccd \n");
			strQuery.append(" group by a.cm_svrname,a.cm_volpath \n");
			strQuery.append(" order by a.cm_svrname \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            while (rs.next()){
 	        	++maxSeq;
 	        	dsncd = "";
 	        	strQuery.setLength(0);
 				strQuery.append("select cm_dsncd \n");
 				strQuery.append("  from cmm0070 \n");
 				strQuery.append(" where cm_syscd = ? and cm_dirpath = ? \n");
 				pstmt2 = conn.prepareStatement(strQuery.toString());
 	            pstmt2.setString(1, SysCd);
 	            pstmt2.setString(2, rs.getString("cm_volpath"));
 	            rs2 = pstmt2.executeQuery();
 	            if (rs2.next()){
 	            	dsncd = rs2.getString("cm_dsncd");
 	            }
				ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
						rs.getString("cm_svrname"),"0",rs.getString("cm_volpath"),
						dsncd,"true","");
				rs2.close();
				pstmt2.close();
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			//ecamsLogger.error("+++++++==xml end+++++++++++");
			rs = null;
			pstmt = null;
			rs2 = null;
			pstmt2 = null;
			conn = null;

			list.add(ecmmtb.getDocument());
			//ecamsLogger.error("+++++++==return+++++++++++");
			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getOutHomeDirForNT() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getOutHomeDirForNT() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getOutHomeDirForNT() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getOutHomeDirForNT() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getOutHomeDirForNT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getOutHomeDirForNT() method statement

	public Object[] getSubDirPathForNT(String UserId,String SysCd,String FullDirPath,String seqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		CreateXml         ecmmtb      = new CreateXml();
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		//String            strDsn      = null;
		String            strHome     = "";
		String[]          pathDepth   = null;
		String            strDir      = "";
		int               upSeq       = 0;
		int               maxSeq      = 100;
		boolean           findSw      = false;
		int               parmCnt     = 0;
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		Boolean			  AdminYn= false;

		try {
			conn = connectionContext.getConnection();

			AdminYn = userInfo.isAdmin_conn(UserId,conn);
			userInfo = null;

			//conn = connectionContext.getConnection();

			strQuery.append("select b.cm_dirpath volpath,b.cm_dsncd,e.cm_volpath \n");
			strQuery.append("  from cmm0070 b,cmm0031 a,cmm0030 c,cmr0020 d,cmm0038 e ");
			if (!AdminYn) {
				strQuery.append(",cmm0044 f \n");
			}
			strQuery.append(" where c.cm_syscd=?                                         \n");
			strQuery.append("   and c.cm_syscd=a.cm_syscd and c.cm_dirbase=a.cm_svrcd    \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd and a.cm_closedt is null       \n");
			strQuery.append("   and a.cm_syscd=e.cm_syscd and a.cm_svrcd=e.cm_svrcd      \n");
			strQuery.append("   and a.cm_seqno=e.cm_seqno \n");
			if (!AdminYn){
				strQuery.append("and f.cm_userid=?           \n");
				strQuery.append("and f.cm_syscd=?           \n");
				strQuery.append("and f.cm_syscd=d.cr_syscd           \n");
				strQuery.append("and f.cm_jobcd=d.cr_jobcd           \n");
			}
			strQuery.append("   and d.cr_rsrccd=e.cm_rsrccd \n");
			strQuery.append("   and b.cm_syscd=d.cr_syscd and b.cm_dsncd=d.cr_dsncd \n");
			strQuery.append("   and b.cm_dirpath like ? \n");
			strQuery.append("   and b.cm_dirpath <> ? \n");
			strQuery.append(" group by b.cm_dirpath,b.cm_dsncd,e.cm_volpath \n");
			strQuery.append(" order by b.cm_dirpath \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, SysCd);
			if (!AdminYn) {
				pstmt.setString(++parmCnt, UserId);
				pstmt.setString(++parmCnt, SysCd);
			}
			pstmt.setString(++parmCnt, FullDirPath+"\\%");
			pstmt.setString(++parmCnt, FullDirPath);

			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","isBranch");
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        rsval.clear();
	        while (rs.next()){
	        	strDir = "";
				if (rs.getString("cm_volpath") != null) {
					strDir = rs.getString("cm_volpath");
					if (strDir.length()>0){
						if (strDir.substring(strDir.length()-1).equals("\\")){
							strDir = strDir.substring(0,strDir.length()-1);
						}
					}
					strHome = strDir;
					if (rs.getString("volpath").length() >= strHome.length()){
						if (!rs.getString("volpath").substring(0,strHome.length()).equals(strHome)) {
							strHome = "";
						}
					}
				} else strHome = "";
				//System.out.println("volpath:"+rs.getString("volpath")+", strDir:"+strDir);
				if(rs.getString("volpath").length() >= strDir.length()){
					if (rs.getString("volpath").substring(0,strDir.length()).equals(strDir)) {
						strDir = rs.getString("volpath").replace(strDir, "");
					} else strDir = "";
				}
				//System.out.println("strDir:"+strDir);
				if (strDir.length() > 0){
					pathDepth = strDir.substring(1).split("\\\\");

					strDir = "\\";
					upSeq = Integer.parseInt(seqNo);
					findSw = false;

					for (int i = 0;pathDepth.length > i;i++) {
						if (strDir.length() > 1 ) {
							strDir = strDir + "\\";
						}
						strDir = strDir + pathDepth[i];
						//ecamsLogger.error("strDir====>" + strDir);
						findSw = false;
						if (rsval.size() > 0) {
							for (int j = 0;rsval.size() > j;j++) {
								//ecamsLogger.error("cm_fullpath====>" + rsval.get(j).get("cm_fullpath"));
								if (rsval.get(j).get("cm_fullpath").equals(strHome+strDir)) {
									upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));

									//rsval.set(j, element)
									//rst.put("branch","true");
									findSw = true;
								}
							}
						} else {
							findSw = false;
						}
						if (findSw == false) {
							maxSeq = maxSeq + 1;

							//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
							rst = new HashMap<String,String>();
							rst.put("cm_dirpath",pathDepth[i]);
							rst.put("cm_fullpath",strHome+strDir);
							rst.put("cm_upseq",Integer.toString(upSeq));
							rst.put("cm_seqno",Integer.toString(maxSeq));
							if (rs.getString("volpath").equals(strHome+strDir)){
								rst.put("cr_dsncd", rs.getString("cm_dsncd"));
							}else {
								rst.put("cr_dsncd", "");
							}
							if (rs.getString("volpath").equals(strHome+strDir)){
								rst.put("branch", "false");
							}
							rsval.add(maxSeq - 101, rst);
							rst = null;
							upSeq = maxSeq;
						}
					}
				}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (rsval.size() > 0) {
				//ecamsLogger.debug(rsval.toString());

			    String strBran = "";
				for (int i = 0;rsval.size() > i;i++) {
					strBran = "false";
					for (int j=0;rsval.size()>j;j++) {
						if (i != j) {
							if (rsval.get(i).get("cm_seqno").equals(rsval.get(j).get("cm_upseq"))) {
								strBran = "true";
								break;
							}
						}
					}
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cr_dsncd"),
							strBran,rsval.get(i).get("cm_upseq"));
				}
			}

			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getSubDirPathForNT() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getSubDirPathForNT() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getSubDirPathForNT() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getSubDirPathForNT() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getSubDirPathForNT() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getSubDirPathForNT() method statement

//	public Object[] getRsrcPath(String UserID,String SysCd,String SecuYn,String SinCd,String ReqCd) throws SQLException, Exception {
//		Connection        conn        = null;
//		PreparedStatement pstmt       = null;
//		ResultSet         rs          = null;
//		StringBuffer      strQuery    = new StringBuffer();
//		Object[] returnObjectArray	  = null;
//		CreateXml         ecmmtb      = new CreateXml();
//		int               maxSeq      = 0;
//		int               parmCnt     = 0;
//
//		ArrayList<Document> list = new ArrayList<Document>();
//
//
//		ConnectionContext connectionContext = new ConnectionResource();
//
//		try {
//			conn = connectionContext.getConnection();
//			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");
//
//			strQuery.append("select a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath,d.cm_seqno \n");
//			strQuery.append("  from cmm0036 a,cmm0038 c,cmm0031 b,                      \n");
//			strQuery.append("       cmm0020 d,cmm0030 e                                 \n");
//			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null               \n");
//			strQuery.append("   and a.cm_syscd=e.cm_syscd                               \n");
//			if (SinCd.equals("06")) {
//				strQuery.append("and substr(a.cm_info,11,1)='1'                         \n");
//			} else {
//				strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037 \n");
//				strQuery.append("                            where cm_syscd=?               \n");
//				strQuery.append("                              and cm_rsrccd<>cm_samersrc)  \n");
//				if (ReqCd.equals("09"))
//					strQuery.append("and substr(a.cm_info,15,1)='1'                         \n");
//				else {
//					if (!SinCd.equals("03") && !SinCd.equals("04")) {
//						strQuery.append("and substr(a.cm_info,2,1)='1'                      \n");
//					}
//					strQuery.append("and substr(a.cm_info,26,1)='0'                         \n");
//				}
//			}
//			//strQuery.append("   and a.cm_rsrccd=b.cr_rsrccd                             \n");
//
//			strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=a.cm_rsrccd     \n");
//			strQuery.append("   and e.cm_syscd=b.cm_syscd and b.cm_closedt is null      \n");
//			strQuery.append("   and e.cm_dirbase=b.cm_svrcd and b.cm_syscd=c.cm_syscd   \n");
//			strQuery.append("   and b.cm_svrcd=c.cm_svrcd and b.cm_seqno=c.cm_seqno     \n");
//			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd                             \n");
//			strQuery.append(" group by a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath,d.cm_seqno \n");
//			strQuery.append(" order by d.cm_seqno                                       \n");//a.cm_rsrccd
//
//			//pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt = new LoggableStatement(conn,strQuery.toString());
//
//            pstmt.setString(++parmCnt, SysCd);
//            if (!SinCd.equals("06")) {
//            	pstmt.setString(++parmCnt, SysCd);
//            	if (SinCd.equals("03"))
//                	if (ReqCd.equals("05")) pstmt.setString(++parmCnt, SysCd);
//            }
//            
//            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//            rs = pstmt.executeQuery();
//            //ecamsLogger.error("+++++++==query end+++++++++++");
//
//            while (rs.next()){
// 	        	++maxSeq;
//				ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
//						rs.getString("cm_codename"),"0",rs.getString("cm_volpath"),"",
//						rs.getString("cm_rsrccd"),rs.getString("cm_info"),"true","");
//			}//end of while-loop statement
//			rs.close();
//			pstmt.close();
//			conn.close();
//			//ecamsLogger.error("+++++++==xml end+++++++++++");
//			rs = null;
//			pstmt = null;
//			conn = null;
//
//			list.add(ecmmtb.getDocument());
//			//ecamsLogger.error("+++++++==return+++++++++++");
//			returnObjectArray = list.toArray();
//
//			list = null;
//			ecmmtb = null;
//			//ecamsLogger.error(ecmmtb.xml_toStr());
//
//			return returnObjectArray;
//
//
//		} catch (SQLException sqlexception) {
//			sqlexception.printStackTrace();
//			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException START ##");
//			ecamsLogger.error("## Error DESC : ", sqlexception);
//			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException END ##");
//			throw sqlexception;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## SystemPath.getRsrcPath() Exception START ##");
//			ecamsLogger.error("## Error DESC : ", exception);
//			ecamsLogger.error("## SystemPath.getRsrcPath() Exception END ##");
//			throw exception;
//		}finally{
//			if (strQuery != null) 	strQuery = null;
//			if (returnObjectArray != null)	returnObjectArray = null;
//			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
//			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
//			if (conn != null){
//				try{
//					ConnectionResource.release(conn);
//				}catch(Exception ex3){
//					ecamsLogger.error("## SystemPath.getRsrcPath() connection release exception ##");
//					ex3.printStackTrace();
//				}
//			}
//		}
//	}//end of getRsrcPath() method statement
	
	public List<HashMap<String, String>> getRsrcPath(String UserID,String SysCd,String SecuYn,String SinCd,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		int               parmCnt     = 0;

		ConnectionContext connectionContext = new ConnectionResource();
		
		List<HashMap<String, String>> rsrcPathList 	= new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rsrcPathMap 		= null;

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select a.cm_rsrccd,a.cm_info,d.cm_codename,b.cm_volpath,d.cm_seqno,\n");
			strQuery.append("		a.cm_prcstep as id,	 										\n");
			strQuery.append("		'-1' as pid, 												\n");
			strQuery.append("		d.cm_codename as text,										\n");
			strQuery.append("		'' as value 												\n");
			strQuery.append("  from cmm0036 a,cmm0031 b,                      					\n");
			strQuery.append("       cmm0020 d,cmm0030 e                                 		\n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null               		\n");
			strQuery.append("   and a.cm_syscd=e.cm_syscd                               		\n");
			if (SinCd.equals("06")) {
				strQuery.append("and substr(a.cm_info,11,1)='1'                         \n");
			} else {
				strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037 \n");
				strQuery.append("                            where cm_syscd=?               \n");
				strQuery.append("                              and cm_rsrccd<>cm_samersrc)  \n");
				if (ReqCd.equals("09"))
					strQuery.append("and substr(a.cm_info,15,1)='1'                         \n");
				else {
					if (!SinCd.equals("03") && !SinCd.equals("04")) {
						strQuery.append("and substr(a.cm_info,2,1)='1'                      \n");
					}
					strQuery.append("and substr(a.cm_info,26,1)='0'                         \n");
				}
			}
			//strQuery.append("   and a.cm_rsrccd=b.cr_rsrccd                             \n");

			strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=a.cm_rsrccd     \n");
			strQuery.append("   and e.cm_syscd=b.cm_syscd and b.cm_closedt is null      \n");
			strQuery.append("   and e.cm_dirbase=b.cm_svrcd \n");
			//strQuery.append(" group by a.cm_rsrccd,a.cm_info,d.cm_codename,b.cm_volpath,d.cm_seqno \n");
			strQuery.append(" order by a.cm_prcstep                                       \n");//a.cm_rsrccd

			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());

            pstmt.setString(++parmCnt, SysCd);
            if (!SinCd.equals("06")) {
            	pstmt.setString(++parmCnt, SysCd);
            	if (SinCd.equals("03"))
                	if (ReqCd.equals("05")) pstmt.setString(++parmCnt, SysCd);
            }
            
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            //ecamsLogger.error("+++++++==query end+++++++++++");
            
            
            
            rsrcPathMap = new HashMap<String, String>();
        	rsrcPathMap.put("cm_rsrccd"		, "");
        	rsrcPathMap.put("cm_info"		, "");
        	rsrcPathMap.put("cm_codename"	, "");
        	rsrcPathMap.put("cm_volpath"	, "");
        	rsrcPathMap.put("cm_seqno"		, "");
        	rsrcPathMap.put("id"			, "-1");
        	rsrcPathMap.put("pid"			, "-2");
        	rsrcPathMap.put("text"			, "프로그램종류");
        	rsrcPathMap.put("value"			, "");
        	rsrcPathMap.put("imagesrc"			, "/img/folderDefaultClosed.gif");
        	rsrcPathList.add(rsrcPathMap);
        	rsrcPathMap = null;
            
            while (rs.next()){
            	rsrcPathMap = new HashMap<String, String>();
            	rsrcPathMap.put("cm_rsrccd"		, rs.getString("cm_rsrccd"));
            	rsrcPathMap.put("cm_info"		, rs.getString("cm_info"));
            	rsrcPathMap.put("cm_codename"	, rs.getString("cm_codename"));
            	rsrcPathMap.put("cm_volpath"	, rs.getString("cm_volpath"));
            	rsrcPathMap.put("cm_seqno"		, rs.getString("cm_seqno"));
            	rsrcPathMap.put("id"			, rs.getString("id"));
            	rsrcPathMap.put("pid"			, rs.getString("pid"));
            	rsrcPathMap.put("text"			, rs.getString("text"));
            	rsrcPathMap.put("value"			, rs.getString("value"));
            	rsrcPathMap.put("root"			, "true");
            	rsrcPathMap.put("imagesrc"			, "/img/folderDefaultClosed.gif");
            	rsrcPathList.add(rsrcPathMap);
            	rsrcPathMap = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;



			return rsrcPathList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getRsrcPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcPath() method statement
	
	
	public Object[] getRsrcPath_svr(String UserID,String SysCd,String SecuYn,String SinCd,String ReqCd,String svrCD,String seqNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		CreateXml         ecmmtb      = new CreateXml();
		int               maxSeq      = 0;
		int               parmCnt     = 0;

		ArrayList<Document> list = new ArrayList<Document>();


		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");

			strQuery.append("select a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath,d.cm_seqno \n");
			strQuery.append("  from cmm0036 a,cmm0038 c,cmm0031 b,                      \n");
			strQuery.append("       cmm0020 d,cmm0030 e                                 \n");
			strQuery.append(" where e.cm_syscd=? and e.cm_syscd=a.cm_syscd              \n");
			strQuery.append("   and a.cm_closedt is null                                \n");
			strQuery.append("   and a.cm_rsrccd not in (select cm_samersrc from cmm0037 \n");
			strQuery.append("                            where cm_syscd=?               \n");
			strQuery.append("                              and cm_rsrccd<>cm_samersrc)  \n");
			strQuery.append("   and exists (select 1 from cmm0038                       \n");
			strQuery.append("                where cm_syscd=a.cm_syscd                  \n");
			strQuery.append("                  and cm_svrcd=? and cm_seqno=?            \n");
			strQuery.append("                  and cm_rsrccd=a.cm_rsrccd)               \n");
			strQuery.append("   and substr(a.cm_info,2,1)='1'                           \n");
			strQuery.append("   and d.cm_macode='JAWON' and d.cm_micode=a.cm_rsrccd     \n");
			strQuery.append("   and a.cm_syscd=b.cm_syscd and b.cm_closedt is null      \n");
			strQuery.append("   and nvl(e.cm_dirbase,'01')=b.cm_svrcd                   \n");
			strQuery.append("   and b.cm_syscd=c.cm_syscd                               \n");
			strQuery.append("   and b.cm_svrcd=c.cm_svrcd and b.cm_seqno=c.cm_seqno     \n");
			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd                             \n");
			strQuery.append(" group by a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath,d.cm_seqno \n");
			strQuery.append(" order by d.cm_seqno                                       \n");//a.cm_rsrccd

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, svrCD);
            pstmt.setString(++parmCnt, seqNo);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            //ecamsLogger.error("+++++++==query end+++++++++++");

            while (rs.next()){
 	        	++maxSeq;
				ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
						rs.getString("cm_codename"),"0",rs.getString("cm_volpath"),"",
						rs.getString("cm_rsrccd"),rs.getString("cm_info"),"true","");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			//ecamsLogger.error("+++++++==xml end+++++++++++");
			rs = null;
			pstmt = null;
			conn = null;

			list.add(ecmmtb.getDocument());
			//ecamsLogger.error("+++++++==return+++++++++++");
			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getRsrcPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcPath() method statement
	public Object[] getJobPath(String UserID,String SysCd,String SecuYn) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		CreateXml         ecmmtb      = new CreateXml();
		int               maxSeq      = 0;
		int               parmCnt     = 0;
		String            wkDirPath   = "";
		ArrayList<Document> list = new ArrayList<Document>();


		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cm_jobcd","isBranch");

			strQuery.append("select d.cm_jobcd,d.cm_jobname                             \n");
			strQuery.append("  from cmm0034 c,cmm0102 d,cmm0044 a                       \n");
			strQuery.append(" where a.cm_userid=? and a.cm_closedt is null              \n");
			strQuery.append("   and a.cm_syscd=? and a.cm_closedt is null               \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd and a.cm_jobcd=c.cm_jobcd     \n");
			strQuery.append("   and c.cm_jobcd=d.cm_jobcd                               \n");
			strQuery.append(" order by substr(d.cm_jobcd,4),substr(d.cm_jobcd,1,3)      \n");//a.cm_rsrccd

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());

            pstmt.setString(++parmCnt, UserID);
            pstmt.setString(++parmCnt, SysCd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            //ecamsLogger.error("+++++++==query end+++++++++++");

            while (rs.next()){
 	        	++maxSeq;
 	        	wkDirPath = "/ecams/"+rs.getString("cm_jobcd").substring(3)+"/"+rs.getString("cm_jobcd").substring(0,3);
				ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
						rs.getString("cm_jobname"),"0",wkDirPath,"",
						rs.getString("cm_jobcd"),"true","");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			//ecamsLogger.error("+++++++==xml end+++++++++++");
			rs = null;
			pstmt = null;
			conn = null;

			list.add(ecmmtb.getDocument());
			//ecamsLogger.error("+++++++==return+++++++++++");
			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getRsrcPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getRsrcPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getRsrcPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getRsrcPath() method statement
	public Object[] getTopSysPath(String UserID,String progName,boolean upSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[] returnObjectArray	  = null;
		CreateXml         ecmmtb      = new CreateXml();
		int               maxSeq      = 0;
		int               sysSeq      = 0;
		int               svrSeq      = 0;
		int               rsrcSeq      = 0;
		boolean           findSw      = false;
		String            strSysName  = null;
		String            strSvrName  = null;
		String            svSvrName   = null;
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ArrayList<Document> list = new ArrayList<Document>();


		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();

			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_syscd","cr_itemid","cr_rsrccd","cr_lstver","gbncd","isBranch");

			int cnt = 0;
			strQuery.setLength(0);
			strQuery.append("select f.cm_sysmsg,e.cm_svrname,d.cm_seqno,       \n");
			strQuery.append("       a.cm_rsrccd,a.cm_info,d.cm_codename,       \n");
			strQuery.append("       c.cm_volpath,f.cm_syscd                    \n");
			strQuery.append("  from cmm0036 a,cmm0030 f,cmm0044 h,             \n");
			strQuery.append("       (select cr_syscd,cr_rsrccd from cmr0020    \n");
			strQuery.append("         group by cr_syscd,cr_rsrccd) b           \n");
			strQuery.append("       ,cmm0020 d,cmm0038 c,cmm0031 e             \n");
			if (progName != null && progName != "") {
    			strQuery.append(",(select y.cm_syscd,y.cm_dirpath              \n");
    			strQuery.append("    from cmm0070 y,cmr0020 x                  \n");
    			if (upSw == false) {
    				strQuery.append("where upper(x.cr_rsrcname) like upper(?)  \n");
        		} else {
        			strQuery.append("where x.cr_rsrcname like ?                \n");
        		}
    			strQuery.append("  and x.cr_syscd=y.cm_syscd                   \n");
    			strQuery.append("  and x.cr_dsncd=y.cm_dsncd                   \n");
    			strQuery.append("group by y.cm_syscd,y.cm_dirpath) g           \n");

        	}
			strQuery.append(" where f.cm_closedt is null                       \n");
			strQuery.append("   and h.cm_userid=? and h.cm_closedt is null     \n");
			strQuery.append("   and h.cm_syscd=f.cm_syscd                      \n");
			strQuery.append("   and f.cm_syscd=c.cm_syscd                      \n");
			strQuery.append("   and a.cm_rsrccd not in                         \n");
			strQuery.append("      (select cm_samersrc from cmm0037            \n");
			strQuery.append("        where cm_syscd=a.cm_syscd                 \n");
			strQuery.append("          and cm_rsrccd<>cm_samersrc)             \n");
			strQuery.append("   and a.cm_syscd=b.cr_syscd                      \n");
			strQuery.append("   and a.cm_rsrccd=b.cr_rsrccd                    \n");
			strQuery.append("   and d.cm_macode='JAWON'                        \n");
			strQuery.append("   and d.cm_micode=a.cm_rsrccd                    \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd                      \n");
			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd and c.cm_svrcd='01'\n");
			strQuery.append("   and c.cm_syscd=e.cm_syscd                      \n");
			strQuery.append("   and c.cm_svrcd=e.cm_svrcd                      \n");
			strQuery.append("   and c.cm_seqno=e.cm_seqno                      \n");
			if (progName != null && progName != "") {
				strQuery.append("and c.cm_syscd=g.cm_syscd                     \n");
				strQuery.append("and g.cm_dirpath like c.cm_volpath || '%'     \n");
			}
			strQuery.append(" group by f.cm_syscd,e.cm_svrname,d.cm_seqno,a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath,f.cm_sysmsg \n");
			strQuery.append(" order by f.cm_syscd,e.cm_svrname,d.cm_seqno,a.cm_rsrccd     \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            if (progName != null && progName != "") {
            	pstmt.setString(++cnt, "%"+progName+"%");
        	}
            pstmt.setString(++cnt, UserID);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            while (rs.next()){

 	        	//ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");
 	        	findSw = false;
 	        	if (strSysName == null) findSw = true;
 	        	else if (!strSysName.equals(rs.getString("cm_sysmsg"))) findSw = true;

 	        	if (findSw == true) {
 	        		++maxSeq;
 	        		strSysName = rs.getString("cm_sysmsg");
 	        		sysSeq = maxSeq;
 	        		strSvrName = null;
 	        		//ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
 	        		//		strSysName,"0","",rs.getString("cm_syscd"),"",
					//		"","","SYS","true",Integer.toString(sysSeq));
 	        		rst = new HashMap<String,String>();
					rst.put("cm_dirpath",strSysName);
					rst.put("cm_fullpath","");
					rst.put("cm_upseq",Integer.toString(maxSeq));
					rst.put("cm_seqno",Integer.toString(maxSeq));
					rst.put("cm_syscd",rs.getString("cm_syscd"));
					rst.put("cr_rsrccd","");
					rst.put("gbncd", "SYS");
					rst.put("cr_itemid", "");
					rst.put("cr_lstver", "");
					rst.put("bransw", "true");
					rsval.add(rst);
 	        	}

 	        	findSw = false;
 	        	if (strSvrName == null) findSw = true;
 	        	else if (!strSvrName.equals(rs.getString("cm_svrname"))) findSw = true;

 	        	if (findSw == true) {
 	        		++maxSeq;
 	        		strSvrName = rs.getString("cm_svrname");
 	        		svSvrName = strSvrName.replace("_개발", "");
 	        		svSvrName = svSvrName.replace("개발", "");
 	        		svrSeq = maxSeq;
 	        		//ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
 	        		//strSvrName.replace("개발", ""),Integer.toString(sysSeq),rs.getString("cm_syscd"),"","",
 	        		//"","","SVR","true",Integer.toString(sysSeq));
 	        		rst = new HashMap<String,String>();
 	        		rst.put("cm_dirpath",svSvrName);
					rst.put("cm_fullpath","");
					rst.put("cm_upseq",Integer.toString(sysSeq));
					rst.put("cm_seqno",Integer.toString(maxSeq));
					rst.put("cm_syscd",rs.getString("cm_syscd"));
					rst.put("cr_rsrccd","");
					rst.put("gbncd", "SVR");
					rst.put("cr_itemid", "");
					rst.put("cr_lstver", "");
					rst.put("bransw", "true");
					rsval.add(rst);
 	        	}
 	        	++maxSeq;
 	        	rsrcSeq = maxSeq;
				//ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
				//		rs.getString("cm_codename"),"0",rs.getString("cm_volpath"),rs.getString("cm_syscd"),"",
				//		rs.getString("cm_rsrccd"),rs.getString("cm_info"),"RSC","true",Integer.toString(svrSeq));
				rst = new HashMap<String,String>();
				rst.put("cm_dirpath",rs.getString("cm_codename"));
				rst.put("cm_fullpath",rs.getString("cm_volpath"));
				rst.put("cm_upseq",Integer.toString(svrSeq));
				rst.put("cm_seqno",Integer.toString(maxSeq));
				rst.put("cm_syscd",rs.getString("cm_syscd"));
				rst.put("cr_rsrccd",rs.getString("cm_rsrccd"));
				rst.put("gbncd", "RSC");
				rst.put("cr_itemid", "");
				rst.put("cr_lstver", "");
				rst.put("bransw", "true");
				rsval.add(rst);

				cnt = 0;
 	        	strQuery.setLength(0);
 	        	strQuery.append("select a.cr_rsrcname,a.cr_itemid,a.cr_lstver  \n");
 	        	strQuery.append("  from cmm0044 d,cmm0070 b,cmr0020 a,cmm0036 c\n");
 	        	strQuery.append(" where b.cm_syscd=? and b.cm_dirpath=?        \n");
 	        	strQuery.append("   and b.cm_syscd=a.cr_syscd                  \n");
 	        	strQuery.append("   and b.cm_dsncd=a.cr_dsncd                  \n");
 	        	strQuery.append("   and a.cr_lstver>0 and a.cr_status<>'9'     \n");
 	        	strQuery.append("   and d.cm_userid=? and d.cm_closedt is null \n");
 	        	strQuery.append("   and d.cm_syscd=?                           \n");
 	        	strQuery.append("   and d.cm_jobcd=a.cr_jobcd                  \n");
 	        	if (progName != null && progName != "") {
 	        		if (upSw == false) {
 	        			strQuery.append("and upper(a.cr_rsrcname) like upper(?)\n");
 	        		} else {
 	        			strQuery.append("and a.cr_rsrcname like ?              \n");
 	        		}
 	        	}
 	        	strQuery.append("   and (a.cr_rsrccd=? or                      \n");
 				strQuery.append("        a.cr_rsrccd in                        \n");
 				strQuery.append("       (select cm_samersrc from cmm0037       \n");
 				strQuery.append("        where cm_syscd=? and cm_rsrccd=?      \n");
 				strQuery.append("          and cm_rsrccd<>cm_samersrc))        \n");
 				strQuery.append("   and a.cr_syscd=c.cm_syscd                  \n");
 				strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd                \n");
 				strQuery.append("   and substr(c.cm_info,10,1)='0'             \n");
 				strQuery.append("   and substr(c.cm_info,12,1)='1'             \n");
 	        	strQuery.append("order by a.cr_rsrcname                        \n");

 	        	pstmt2 = conn.prepareStatement(strQuery.toString());
 	        	//pstmt2 = new LoggableStatement(conn,strQuery.toString());
 	        	pstmt2.setString(++cnt, rs.getString("cm_syscd"));
 	        	pstmt2.setString(++cnt, rs.getString("cm_volpath"));
 	        	pstmt2.setString(++cnt, UserID);
 	        	pstmt2.setString(++cnt, rs.getString("cm_syscd"));
 	        	if (progName != null && progName != "") {
 	        		pstmt2.setString(++cnt, "%"+progName+"%");
 	        	}
 	        	pstmt2.setString(++cnt, rs.getString("cm_rsrccd"));
 	        	pstmt2.setString(++cnt, rs.getString("cm_syscd"));
 	        	pstmt2.setString(++cnt, rs.getString("cm_rsrccd"));
 	        	//ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
 	        	rs2 = pstmt2.executeQuery();
 	        	while (rs2.next()) {
 	        		++maxSeq;
 	        		rst = new HashMap<String,String>();
					rst.put("cm_dirpath",rs2.getString("cr_rsrcname"));
					rst.put("cm_fullpath",rs.getString("cm_volpath"));
					rst.put("cm_upseq",Integer.toString(rsrcSeq));
					rst.put("cm_seqno",Integer.toString(maxSeq));
					rst.put("cm_syscd",rs.getString("cm_syscd"));
					rst.put("cr_rsrccd",rs.getString("cm_rsrccd"));
					rst.put("cr_lstver",rs2.getString("cr_lstver"));
					rst.put("gbncd", "ITM");
					rst.put("cr_itemid", rs2.getString("cr_itemid"));
					rst.put("bransw", "false");
					rsval.add(rst);
 					//ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
 					//		rs2.getString("cr_rsrcname"),"0",rs.getString("cm_volpath"),rs.getString("cm_syscd"),rs2.getString("cr_itemid"),
 					//		rs.getString("cm_rsrccd"),rs2.getString("cr_lstver"),"ITM","true",Integer.toString(rsrcSeq));
 	        	}
 	        	rs2.close();
 	        	pstmt2.close();
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (rsval.size() > 0) {
				//ecamsLogger.debug(rsval.toString());
				String strLstVer = "";

				for (int i = 0;rsval.size() > i;i++) {
					if (!rsval.get(i).get("gbncd").equals("ITM")) strLstVer = Integer.toString(maxSeq);
					else strLstVer = rsval.get(i).get("cr_lstver");
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),rsval.get(i).get("cm_syscd"),
							rsval.get(i).get("cr_itemid"),rsval.get(i).get("cr_rsrccd"),
							strLstVer,rsval.get(i).get("gbncd"),
							rsval.get(i).get("bransw"),rsval.get(i).get("cm_upseq"));
				}
			}


			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			rsval = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getTopSysPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getTopSysPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getTopSysPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getTopSysPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getTopSysPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTopSysPath() method statement


	public Object[] getDynamicPath(String UserID,String syscd,String rsrcCD,String rootPath,int pathlevel,String adminCk) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		CreateXml         ecmmtb      = new CreateXml();
		String[]          pathDepth   = null;
		int               parmCnt     = 0;
		//HashMap<String, String>			  rst		  = null;
		//Object[]		  rtObj		  = null;
		//ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		ArrayList<Document> list = new ArrayList<Document>();
		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			Boolean AdminYn= false;

			//if (adminCk.toUpperCase().equals("Y") || adminCk.equals("1")){
				//AdminYn = userInfo.isAdmin(UserID);
				AdminYn = userInfo.isAdmin_conn(UserID,conn);
			//}
			userInfo = null;

			//conn = connectionContext.getConnection();


			strQuery.append("select /*+ leading(a) use_nl(a c) use_nl(c d) */ \n");
			strQuery.append("dirpath, rsrccd, max(maxlevel) as maxlevel, nowlevel from ( \n");
			strQuery.append("select distinct '/' || b.cm_codename || decode(?,length(a.cm_dirpath) \n");
			strQuery.append("- length(replace(a.cm_dirpath,'/',''))+1,a.cm_dirpath, \n");
			strQuery.append("substr(a.cm_dirpath,1,instr(a.cm_dirpath,'/',1,? )-1)) as dirpath, c.cr_rsrccd as rsrccd, \n");
			strQuery.append("length(a.cm_dirpath) - length(replace(a.cm_dirpath,'/',''))+1 as maxlevel, ? as nowlevel \n");
			strQuery.append("  from cmm0020 b,cmm0070 a,                                       \n");
			if (!AdminYn){
				strQuery.append("   (select cr_syscd,cr_dsncd,cr_rsrccd,cr_itemid from cmr0020 \n");
			    strQuery.append("        where cr_syscd=?) c,                                  \n");
				strQuery.append("      (select j.cr_itemid from cmm0044 k,CMR0023 j            \n");
			    strQuery.append("        where k.cm_syscd=? and k.cm_userid=?                  \n");
			    strQuery.append("         and k.cm_closedt is null                             \n");
			    strQuery.append("         and k.cm_syscd=j.cr_syscd                            \n");
			    strQuery.append("         and k.cm_jobcd=j.cr_jobcd) d,                        \n");
			} else {
				strQuery.append("   (select distinct cr_syscd,cr_dsncd,cr_rsrccd from cmr0020  \n");
			    strQuery.append("        where cr_syscd=?) c,                                  \n");
			}
			strQuery.append("      (select cm_syscd,cm_rsrccd from cmm0036                     \n");
			strQuery.append("        where cm_syscd=? and substr(cm_info,2,1)='1'              \n");
			strQuery.append("          and substr(cm_info,26,1)='0' and cm_closedt is null     \n");
			strQuery.append("        minus                                                     \n");
			strQuery.append("       select cm_syscd,cm_samersrc from cmm0037                   \n");
			strQuery.append("        where cm_syscd=? and cm_rsrccd<>cm_samersrc) e            \n");
			strQuery.append(" where a.cm_syscd=?                                               \n");
			strQuery.append("   and a.cm_syscd=c.cr_syscd and a.cm_dsncd=c.cr_dsncd            \n");
			if (!AdminYn){
			   strQuery.append("and c.cr_itemid=d.cr_itemid                                    \n");
			}
			strQuery.append("   and b.cm_macode='JAWON' and b.cm_micode=c.cr_rsrccd            \n");
			strQuery.append("   and c.cr_syscd=e.cm_syscd and c.cr_rsrccd=e.cm_rsrccd          \n");
			if (!rsrcCD.equals("")){
				strQuery.append("   and c.cr_rsrccd = ? \n");
			}
			strQuery.append(" and length(a.cm_dirpath) - length(replace(a.cm_dirpath,'/',''))+1 >= ? \n");
			strQuery.append(" ) \n");
			strQuery.append(" where dirpath like ?											\n");
			strQuery.append("group by dirpath, rsrccd, nowlevel \n");
			strQuery.append(" order by rsrccd,length(dirpath) \n");
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setInt(++parmCnt, pathlevel);
			pstmt.setInt(++parmCnt, pathlevel);
			pstmt.setInt(++parmCnt, pathlevel);

			if (!AdminYn){
				pstmt.setString(++parmCnt, syscd);
			} else{
				pstmt.setString(++parmCnt, syscd);
				pstmt.setString(++parmCnt, syscd);
			    pstmt.setString(++parmCnt, UserID);
			}
			pstmt.setString(++parmCnt, syscd);
			pstmt.setString(++parmCnt, syscd);
			pstmt.setString(++parmCnt, syscd);

			if (!rsrcCD.equals("")){
				pstmt.setString(++parmCnt, rsrcCD);
			}

			pstmt.setInt(++parmCnt, pathlevel);
			pstmt.setString(++parmCnt, rootPath+"%");

			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			//ecamsLogger.error("Start Query");
            rs = pstmt.executeQuery();
            //ecamsLogger.error("End Query");

            ecmmtb.init_Xml("ID","cm_dirpath","cm_fullpath","cr_rsrccd","nowlevel","maxlevel","isBranch");
            //rtList.clear();

            //ecamsLogger.error("Make Xml Start");
			while (rs.next()){
				pathDepth = rs.getString("dirpath").substring(1).split("/");

				//ecamsLogger.error(rs.getRow());

				ecmmtb.addXML(rs.getString("rsrccd")+ String.format("%04d",rs.getInt("nowlevel"))+pathDepth[pathDepth.length-1],
						pathDepth[pathDepth.length-1],rs.getString("dirpath"),
						rs.getString("rsrccd"),rs.getString("nowlevel"),rs.getString("maxlevel"),
						"true","");

				//ecamsLogger.error(rs.getRow());
				/*
				rst = new HashMap<String,String>();
				rst.put("ID",rs.getString("rsrccd")+ String.format("%04d",rs.getInt("nowlevel"))+pathDepth[pathDepth.length-1]);
				rst.put("cm_dirpath",pathDepth[pathDepth.length-1]);
				rst.put("cm_fullpath",rs.getString("dirpath"));
				rst.put("cm_dsncd",rs.getString("dsncd"));
				rst.put("cr_rsrccd",rs.getString("rsrccd"));
				rst.put("nowlevel",rs.getString("nowlevel"));
				rst.put("maxlevel",rs.getString("maxlevel"));
				rst.put("isBranch","true");
				rtList.add(rst);
				rst = null;
				*/

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			list.add(ecmmtb.getDocument());

			//ecamsLogger.error("Make Xml End");
			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDynamicPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDynamicPath() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDynamicPath() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDynamicPath() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDynamicPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath() method statement


	public Object[] getDirPath_File(String UserId,String SysCd,String RsrcCd,String seqNo,String maxCnt,String progName,boolean upSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		CreateXml         ecmmtb      = new CreateXml();
		StringBuffer      strQuery    = new StringBuffer();
		UserInfo		  userInfo	  = new UserInfo();
		//String            strDsn      = null;
		String            strHome     = null;
		String[]          pathDepth   = null;
		String            strDir      = null;
		int               upSeq       = 0;
		int               maxSeq      = Integer.parseInt(maxCnt);
		boolean           findSw      = false;
		int               parmCnt     = 0;
		String            strPath     = null;
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		Object[] returnObjectArray = null;
		ConnectionContext connectionContext = new ConnectionResource();

		Boolean			  AdminYn= false;

		try {
			conn = connectionContext.getConnection();

			AdminYn = userInfo.isAdmin_conn(UserId, conn);
			userInfo = null;

			strQuery.append("select b.cm_dirpath volpath,b.cm_dsncd,c.cm_volpath,        \n");
			strQuery.append("       d.cr_itemid,d.cr_lstver ,d.cr_rsrcname,f.cm_info     \n");
			strQuery.append("  from cmm0070 b,cmm0031 a,cmr0020 d,cmm0038 c,cmm0036 f,cmm0044 e \n");
			strQuery.append(" where e.cm_userid=? and e.cm_syscd=?                       \n");
			strQuery.append("   and e.cm_closedt is null and e.cm_syscd=a.cm_syscd       \n");
			strQuery.append("   and a.cm_svrcd='01' and a.cm_closedt is null             \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd and a.cm_svrcd=c.cm_svrcd      \n");
			strQuery.append("   and a.cm_seqno=c.cm_seqno                                \n");
        	strQuery.append("   and (c.cm_rsrccd=? or                                    \n");
			strQuery.append("        c.cm_rsrccd in                                      \n");
			strQuery.append("          (select cm_samersrc from cmm0037                  \n");
			strQuery.append("            where cm_syscd=? and cm_rsrccd=?                \n");
			strQuery.append("              and cm_rsrccd<>cm_samersrc))                  \n");
			strQuery.append("   and c.cm_syscd=f.cm_syscd and c.cm_rsrccd=f.cm_rsrccd    \n");
			strQuery.append("   and substr(f.cm_info,12,1)='1'                           \n");
			strQuery.append("   and c.cm_syscd=d.cr_syscd and c.cm_rsrccd=d.cr_rsrccd    \n");
			strQuery.append("   and d.cr_status<>'9'                                     \n");
			strQuery.append("   and d.cr_jobcd=e.cm_jobcd                                \n");
			if (progName != null && progName != "") {
        		if (upSw == false) {
        			strQuery.append("and upper(d.cr_rsrcname) like upper(?)\n");
        		} else {
        			strQuery.append("and d.cr_rsrcname like ?              \n");
        		}
        	}
			if (!AdminYn) strQuery.append("and d.cr_jobcd=e.cm_jobcd                     \n");
			strQuery.append("   and b.cm_syscd=d.cr_syscd and b.cm_dsncd=d.cr_dsncd      \n");
			strQuery.append(" order by b.cm_dirpath,d.cr_rsrcname                        \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(++parmCnt, UserId);
			pstmt.setString(++parmCnt, SysCd);
			pstmt.setString(++parmCnt, RsrcCd);
			pstmt.setString(++parmCnt, SysCd);
			pstmt.setString(++parmCnt, RsrcCd);
			if (progName != null && progName != "") pstmt.setString(++parmCnt, "%"+progName+"%");
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_syscd","cr_itemid","cr_rsrccd","cr_lstver","cm_info","gbncd","isBranch");
            rsval.clear();
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
	        rsval.clear();
	        while (rs.next()){
	        	findSw = false;
	        	if (rs.getString("cr_lstver").equals("0") && rs.getString("cm_info").substring(44,45).equals("1")) {
	        		strDir = "";
	        	} else if (rs.getString("cm_info").substring(9,10).equals("1") && !rs.getString("cm_info").substring(45,46).equals("1")) {
	        		strDir = "";
	        	} else {
		        	if (strPath == null) findSw = true;
		        	else if (!strPath.equals(rs.getString("volpath"))) findSw = true;
		        	//ecamsLogger.error("cr_rsrcname 2====>" + rs.getString("volpath") + "/" + rs.getString("cr_rsrcname"));
		        	if (findSw == true) {
						if (rs.getString("cm_volpath") != null) {
							strDir = rs.getString("cm_volpath");
							if (strDir.length()>0){
								if (strDir.substring(strDir.length()-1).equals("/")){
									strDir = strDir.substring(0,strDir.length()-1);
								}
							}
							strHome = strDir;
							if (rs.getString("volpath").length() >= strHome.length()){
								if (!rs.getString("volpath").substring(0,strHome.length()).equals(strHome)) {
									strHome = "";
								}
							}
						} else strHome = "";

						strDir = rs.getString("volpath").replace(strDir, "");


						if (strDir.length() > 0){
							pathDepth = strDir.substring(1).split("/");

							strDir = "/";
							upSeq = Integer.parseInt(seqNo);
							findSw = false;

							for (int i = 0;pathDepth.length > i;i++) {
								if (strDir.length() > 1 ) {
									strDir = strDir + "/";
								}
								strDir = strDir + pathDepth[i];
								//ecamsLogger.debug("strDir====>" + strDir);
								findSw = false;
								if (rsval.size() > 0) {
									for (int j = 0;rsval.size() > j;j++) {
										if (rsval.get(j).get("cm_fullpath").equals(strHome+strDir)) {
											upSeq = Integer.parseInt(rsval.get(j).get("cm_seqno"));

											//rsval.set(j, element)
											//rst.put("branch","true");
											findSw = true;
										}
									}
								} else {
									findSw = false;
								}
								if (findSw == false) {
									maxSeq = maxSeq + 1;

									//ecamsLogger.error("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + maxCnt  + "  , " + Integer.toString(maxSeq));
									rst = new HashMap<String,String>();
									rst.put("cm_dirpath",pathDepth[i]);
									rst.put("cm_fullpath",strHome+strDir);
									rst.put("cm_upseq",Integer.toString(upSeq));
									rst.put("cm_seqno",Integer.toString(maxSeq));
									rst.put("cr_rsrccd",RsrcCd);
									rst.put("gbncd", "DIR");
									rst.put("bransw", "true");
									rst.put("cr_itemid", rs.getString("cm_dsncd"));
									if (rs.getString("volpath").equals(strHome+strDir)) rst.put("branch", "false");
									rsval.add(maxSeq - Integer.parseInt(maxCnt) - 1, rst);
									rst = null;
									upSeq = maxSeq;
								}
							}
						}
		        	}
	        	}

	        	if (strDir.length() > 0){
	        		//ecamsLogger.error("cr_rsrcname 3====>" + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq) + "," + rs.getString("volpath") + "/" + rs.getString("cr_rsrcname"));
	        		maxSeq = maxSeq + 1;

					//ecamsLogger.debug("dirpath,full,upseq,seq====>" + pathDepth[i] + "  , " + strDir  + "  , " + Integer.toString(upSeq)  + "  , " + Integer.toString(maxSeq));
					rst = new HashMap<String,String>();
					rst.put("cm_dirpath",rs.getString("cr_rsrcname"));
					rst.put("cm_fullpath",rs.getString("volpath"));
					rst.put("cm_upseq",Integer.toString(upSeq));
					rst.put("cm_seqno",Integer.toString(maxSeq));
					rst.put("cr_rsrccd",RsrcCd);
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_lstver", rs.getString("cr_lstver"));
					rst.put("cm_info",rs.getString("cm_info"));
					rst.put("gbncd", "ITM");
					rst.put("bransw", "false");
					rsval.add(maxSeq - Integer.parseInt(maxCnt) - 1, rst);
					rst = null;
					//upSeq = maxSeq;
					//ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(maxSeq),
					//	rs2.getString("cr_rsrcname"),"0",rs.getString("cm_volpath"),rs2.getString("cr_itemid"),
					//		rs.getString("cm_rsrccd"),rs.getString("cr_lstver"),"true",Integer.toString(rsrcSeq));
	        	}
	        	strPath = rs.getString("volpath");

			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			if (rsval.size() > 0) {
				//ecamsLogger.debug(rsval.toString());

			    //String strBran = "";
				for (int i = 0;rsval.size() > i;i++) {
					ecmmtb.addXML(rsval.get(i).get("cm_seqno"),rsval.get(i).get("cm_seqno"),
							rsval.get(i).get("cm_dirpath"),rsval.get(i).get("cm_upseq"),
							rsval.get(i).get("cm_fullpath"),SysCd,rsval.get(i).get("cr_itemid"),
							rsval.get(i).get("cr_rsrccd"),rsval.get(i).get("cr_lstver"),rsval.get(i).get("cm_info"),
							rsval.get(i).get("gbncd"),rsval.get(i).get("bransw"),rsval.get(i).get("cm_upseq"));
				}
			}


			list.add(ecmmtb.getDocument());

			returnObjectArray = list.toArray();

			list = null;
			ecmmtb = null;
			rsval = null;
			//ecamsLogger.error(ecmmtb.xml_toStr());

			return returnObjectArray;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath_File() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## SystemPath.getDirPath_File() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getDirPath_File() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## SystemPath.getDirPath_File() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (returnObjectArray != null)	returnObjectArray = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getDirPath_File() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getDirPath_File() method statement


	public ArrayList<HashMap<String, String>> getUserPath_conn(String SysCd, Connection conn) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		try {
			rsval.clear();
			strQuery.setLength(0);
			strQuery.append("SELECT a.cm_volpath,b.cm_rsrccd from cmm0038 b,cmm0031 a,cmm0036 c \n");
			strQuery.append(" WHERE c.cm_syscd=? and c.cm_closedt is null                       \n");
			strQuery.append("   and c.cm_syscd=b.cm_syscd and c.cm_rsrccd=b.cm_rsrccd           \n");
			strQuery.append("   and b.cm_syscd=a.cm_syscd and a.cm_svrcd='01'                   \n");
			strQuery.append("   and b.cm_svrcd=a.cm_svrcd and b.cm_seqno=a.cm_seqno             \n");
			strQuery.append("   and a.cm_closedt is null                                        \n");
			strQuery.append(" order by b.cm_rsrccd,b.cm_volpath                                 \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, SysCd);
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String, String>();
				rst.put("cm_rsrccd", rs.getString("cm_rsrccd"));
				if (rs.getString("cm_volpath").substring(rs.getString("cm_volpath").length() - 1).equals("/"))
					rst.put("cm_volpath", rs.getString("cm_volpath"));
				else
					rst.put("cm_volpath", rs.getString("cm_volpath") + "/");
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			rs = null;
			pstmt = null;

			return rsval;

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
		}
	}//end of getUserPath_conn() method statement

	public Object[] getTopPath(String UserID,String SysCd,String SecuYn,String ReqCd,String JobCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		CreateXml         ecmmtb      = new CreateXml();	
		int               maxSeq      = 0;		
		int               svrSeq      = 0;	
		int               parmCnt     = 0;
		boolean           findSw      = false;
		UserInfo 		  userinfo 	  = new UserInfo();
		String            strSvrName  = null;
		String            strDsnCd    = null;
		ArrayList<Document> list = new ArrayList<Document>();
	    		
		ConnectionContext connectionContext = new ConnectionResource();		
		
		try {
			conn = connectionContext.getConnection();
			
			ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");
			boolean adminYn = false;
			if (SecuYn.equals("Y")) {
				//adminYn = userinfo.isAdmin(UserID);
				adminYn = userinfo.isAdmin_conn(UserID,conn);
			} else adminYn = true;
			userinfo = null;
			
			strQuery.append("select e.cm_svrname,d.cm_seqno,a.cm_rsrccd,       \n");
			strQuery.append("       a.cm_info,d.cm_codename,c.cm_volpath       \n");
			strQuery.append("  from cmm0036 a,                                 \n");
			if (adminYn == false) {
				strQuery.append("(select a.cr_rsrccd from cmr0020 a,cmm0044 b  \n");
				strQuery.append("  where b.cm_userid=? and b.cm_syscd= ?       \n");
				if (JobCd != null && JobCd != "") {
					strQuery.append("and b.cm_jobcd=?                          \n");
				}
				strQuery.append("    and b.cm_closedt is null                  \n");
				strQuery.append("    and b.cm_syscd=a.cr_syscd                 \n");
				strQuery.append("    and b.cm_jobcd=a.cr_jobcd                 \n");
				strQuery.append("  group by cr_rsrccd) b                       \n");
			} else {
				strQuery.append("(select cr_rsrccd from cmr0020                \n");
				strQuery.append("  where cr_syscd= ? group by cr_rsrccd) b     \n");
			}
			strQuery.append("       ,cmm0020 d,cmm0038 c,cmm0031 e             \n");
			strQuery.append(" where a.cm_syscd=? and a.cm_closedt is null      \n");
			strQuery.append("   and (a.cm_rsrccd not in                        \n");
			strQuery.append("         (select cm_samersrc from cmm0037         \n");
			strQuery.append("           where cm_syscd=?) or                   \n");
			strQuery.append("        substr(a.cm_info,15,1)='1')               \n");
			strQuery.append("   and a.cm_rsrccd=b.cr_rsrccd                    \n");
			strQuery.append("   and d.cm_macode='JAWON'                        \n");
			strQuery.append("   and d.cm_micode=a.cm_rsrccd                    \n");
			strQuery.append("   and a.cm_syscd=c.cm_syscd                      \n");
			strQuery.append("   and a.cm_rsrccd=c.cm_rsrccd and c.cm_svrcd='01'\n");
			strQuery.append("   and c.cm_syscd=e.cm_syscd                      \n");
			strQuery.append("   and c.cm_svrcd=e.cm_svrcd                      \n");
			strQuery.append("   and c.cm_seqno=e.cm_seqno                      \n");
			strQuery.append(" group by e.cm_svrname,d.cm_seqno,a.cm_rsrccd,a.cm_info,d.cm_codename,c.cm_volpath \n");
			strQuery.append(" order by e.cm_svrname,d.cm_seqno,a.cm_rsrccd     \n");
			
            //pstmt = conn.prepareStatement(strQuery.toString());
            pstmt = new LoggableStatement(conn,strQuery.toString());
            if (adminYn == false) {
            	pstmt.setString(++parmCnt, UserID);
            	if (JobCd != null && JobCd != "") pstmt.setString(++parmCnt, JobCd);
            }
            pstmt.setString(++parmCnt, SysCd);
            pstmt.setString(++parmCnt, SysCd);	
            pstmt.setString(++parmCnt, SysCd);	
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());            
            rs = pstmt.executeQuery();
            while (rs.next()){
            	if (ReqCd.equals("09")) {
            		findSw = true;
            	} else {
            		if (rs.getString("cm_info").substring(1,2).equals("1")) 
            			findSw = true;
            		else findSw = false;
            	}
            	if (findSw == true) {
	 	        	++maxSeq;
	 	        	//ecmmtb.init_Xml("ID","cm_seqno","cm_dirpath","cm_upseq","cm_fullpath","cm_dsncd","cr_rsrccd","cm_info","isBranch");
	 	        	findSw = false;
	 	        	if (strSvrName == null) findSw = true;
	 	        	else if (!strSvrName.equals(rs.getString("cm_svrname"))) findSw = true;
	 	        	
	 	        	if (findSw == true) {
	 	        		strSvrName = rs.getString("cm_svrname");
	 	        		svrSeq = maxSeq;
	 	        		ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(svrSeq),
								strSvrName.replace("_개발", "").replace("개발", ""),"0","","",
								"","","true",Integer.toString(svrSeq));
	 	        	}
	 	        	++maxSeq;
	 	        	strDsnCd = null;
	 	        	strQuery.setLength(0);
	 	        	strQuery.append("select cm_dsncd from cmm0070                \n");
	 	        	strQuery.append(" where cm_syscd=? and cm_dirpath=?          \n");
	 	        	pstmt2 = conn.prepareStatement(strQuery.toString());
	 	        	pstmt2.setString(1, SysCd);
	 	        	pstmt2.setString(2, rs.getString("cm_volpath"));
	 	        	rs2 = pstmt2.executeQuery();
	 	        	if (rs2.next()) {
	 	        		strDsnCd = rs2.getString("cm_dsncd");
	 	        	}
	 	        	rs2.close();
	 	        	pstmt2.close();
	 	        	
					ecmmtb.addXML(Integer.toString(maxSeq),Integer.toString(svrSeq),
							rs.getString("cm_codename"),"0",rs.getString("cm_volpath"),strDsnCd,
							rs.getString("cm_rsrccd"),rs.getString("cm_info"),"true",Integer.toString(svrSeq));
            	}
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			
			rs = null;
			pstmt = null;
			conn = null;
			
			list.add(ecmmtb.getDocument());
			//ecamsLogger.error(ecmmtb.xml_toStr());
			ecmmtb = null;
			
			return list.toArray();
			
			
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## SystemPath.getTopPath() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);	
			ecamsLogger.error("## SystemPath.getTopPath() SQLException END ##");			
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## SystemPath.getTopPath() Exception START ##");				
			ecamsLogger.error("## Error DESC : ", exception);	
			ecamsLogger.error("## SystemPath.getTopPath() Exception END ##");				
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (ecmmtb != null)	ecmmtb = null;
			if (list != null)	list = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getTopPath() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getTopPath() method statement

}
