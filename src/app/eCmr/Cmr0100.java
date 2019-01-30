package app.eCmr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import app.common.AutoSeq;
import app.common.LoggableStatement;
//import app.common.StreamGobbler;
import app.common.UserInfo;
import app.common.SysInfo;
import app.common.SystemPath;
//import app.common.eCAMSInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/**
 * <PRE>
 * 1. ClassName	:
 * 2. FileName		: Cmr0100.java
 * 3. Package		: app.eCmr
 * 4. Commnet		:
 * 5. 작성자			: Administrator
 * 6. 작성일			: 2010. 12. 8. 오전 9:12:49
 * </PRE>
 */
public class Cmr0100 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();


	/**
	 * <PRE>
	 * 1. MethodName	: getTmpDirConf
	 * 2. ClassName		: Cmr0100
	 * 3. Commnet			: cmm0012테이블에서 경로 조회하기 테스트
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 8. 오전 9:13:23
	 * </PRE>
	 * 		@return Object[]
	 * 		@param pCode : 경로구분코드값(cmm0012테이블의 cm_pathcd)
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
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
		    rtList.clear();

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
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getTmpDirConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getTmpDirConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getTmpDirConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getTmpDirConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getTmpDirConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}//end of getTmpDirConf() method statement

	public Object[] getChartData() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		try {
			
			conn = connectionContext.getConnection();
			
			//{country:"USA",visits:4252}
			//[{country:"USA",visits:4252},{country:"China",visits:1882},{country:"Japan",visits:1809},{country:"Germany",visits:1322},{country:"UK",visits:1122},
			//{country:"France",visits:1114},{country:"India",visits:984},{country:"Spain",visits:711},{country:"Netherlands",visits:665},{country:"Russia",visits:580},{country:"South Korea",visits:443},{country:"Canada",visits:441},{country:"Brazil",visits:395},{country:"Italy",visits:386},{country:"Australia",visits:384},{country:"Taiwan",visits:338},{country:"Poland",visits:328}];
			
			rst = new HashMap<String,String>();
			rst.put("country", "USA");
			rst.put("visits", "4252");
			rtList.add(rst);
			rst = null;

			rst = new HashMap<String,String>();
			rst.put("country", "China");
			rst.put("visits", "1882");
			rtList.add(rst);
			rst = null;


			rst = new HashMap<String,String>();
			rst.put("country", "Japan");
			rst.put("visits", "1809");
			rtList.add(rst);
			rst = null;

			rst = new HashMap<String,String>();
			rst.put("country", "Germany");
			rst.put("visits", "1322");
			rtList.add(rst);
			rst = null;

			rst = new HashMap<String,String>();
			rst.put("country", "UK");
			rst.put("visits", "1122");
			rtList.add(rst);
			rst = null;

			rst = new HashMap<String,String>();
			rst.put("country", "France");
			rst.put("visits", "1114");
			rtList.add(rst);
			rst = null;
			
//			rs.close();
//			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getTmpDirConf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getTmpDirConf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getTmpDirConf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getTmpDirConf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## SystemPath.getTmpDirConf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
	
	public String getDsnCD(String sysCD,String Path) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  rtString = null;

		try {
			conn = connectionContext.getConnection();

			strQuery.append("SELECT cm_dsncd \n");
			strQuery.append("FROM cmm0070 \n");
			strQuery.append("WHERE cm_syscd = ? \n");
			strQuery.append("AND cm_dirpath = ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, sysCD);
			pstmt.setString(2, Path);

            rs = pstmt.executeQuery();

            rtString = "";

			if (rs.next()){
				rtString = rs.getString("cm_dsncd");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtString;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getDsnCD() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getDsnCD() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getDsnCD() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getDsnCD() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getDsnCD() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}


	}//end of SelectUserInfo() method statement

	public Boolean chk_Resouce(String syscd,String Rsrccd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  rtnval	  = 0;
		try {

			conn = connectionContext.getConnection();
			strQuery.append("select count(cm_rsrccd) as rowcnt from cmm0036 ");
			strQuery.append("where cm_syscd= ? ");
			strQuery.append("and   cm_rsrccd= ? ");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, syscd);
            pstmt.setString(2, Rsrccd);
            //pstmt.setInt(2, cnt);


            rs = pstmt.executeQuery();

			while (rs.next()){
				rtnval = rs.getInt("rowcnt");
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			if (rtnval > 0){
				return true;
			}
			else{
				return false;
			}

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.chk_Resouce() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.chk_Resouce() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.chk_Resouce() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.chk_Resouce() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.chk_Resouce() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	public Object[] getDownFileList(String UserId,ArrayList<HashMap<String,String>> fileList,String ReqCd,String SysCd,String ReqSayu,boolean localSw) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		String            strDevHome  = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  svrList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		String            baseAcpt = "";

		try {
			conn = connectionContext.getConnection();

			String            strVolPath = "";
			String            strDirPath = "";
			
			//로컬 프로그램에 필요한 정보 조회 시작
			strQuery.setLength(0);
			//strQuery.append("select cd_devhome from cmd0200               \n");
			strQuery.append("select cd_devhome from cmd0300               \n");
			strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, UserId);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strDevHome = rs.getString("cd_devhome")+"\\";
			}
			rs.close();
			pstmt.close();
			svrList = new SysInfo().getHomePath_conn(SysCd, conn);
			//로컬 프로그램에 필요한 정보 조회 끝

			for (int i=0;i<fileList.size();i++){
				if (!ReqCd.equals("02")) {
					strQuery.setLength(0);
					strQuery.append("select a.cr_acptno from cmr0021 a, \n");
					strQuery.append("   (select max(cr_prcdate) maxacpt \n");
					strQuery.append("      from cmr0021                 \n");
					strQuery.append("     where cr_itemid=?) b          \n");
					strQuery.append(" where a.cr_itemid=?               \n");
					strQuery.append("   and a.cr_prcdate=b.maxacpt      \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_itemid"));
		            pstmt.setString(2, fileList.get(i).get("cr_itemid"));
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();
					if (rs.next()){
						baseAcpt = rs.getString("cr_acptno");
					}
					rs.close();
					pstmt.close();
				} else {
					baseAcpt = fileList.get(i).get("selAcptno");
				}

				if (baseAcpt == null && baseAcpt == "") {
					throw new Exception("체크아웃 기준이 되는 신청기록이 존재하지 않습니다.("+fileList.get(0).get("cr_rsrcname")+")");
				}
				rst = new HashMap<String,String>();
				rst.put("cm_dirpath",fileList.get(i).get("cm_dirpath"));
				if ( fileList.get(i).get("cm_info").substring(44, 45).equals("1") ) {
					rst.put("view_dirpath", fileList.get(i).get("view_dirpath").replaceAll("\\\\\\\\", "\\\\") );
				} else {
					rst.put("view_dirpath", fileList.get(i).get("view_dirpath") );
				}
				rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
				rst.put("jobname", fileList.get(i).get("jobname"));
				rst.put("jawon", fileList.get(i).get("jawon"));
				rst.put("cr_lstver",fileList.get(i).get("cr_lstver"));
				rst.put("cr_itemid",fileList.get(i).get("cr_itemid"));
				rst.put("sysgb", fileList.get(i).get("sysgb"));
				rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
				rst.put("cr_rsrccd",fileList.get(i).get("cr_rsrccd"));
				rst.put("cr_langcd",fileList.get(i).get("cr_langcd"));
				rst.put("cr_dsncd",fileList.get(i).get("cr_dsncd"));
				rst.put("cr_jobcd",fileList.get(i).get("cr_jobcd"));
				rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
				rst.put("cm_info",fileList.get(i).get("cm_info"));
				rst.put("pcdir",fileList.get(i).get("pcdir"));
				rst.put("localdir",fileList.get(i).get("localdir"));
				rst.put("cm_username",fileList.get(i).get("cm_username"));
				rst.put("lastdt",fileList.get(i).get("lastdt"));
				rst.put("pcdir1",fileList.get(i).get("pcdir1"));
				rst.put("cr_lstusr",fileList.get(i).get("cr_lstusr"));
				rst.put("cr_pgmtype",fileList.get(i).get("cr_pgmtype"));
				rst.put("pgmtype",fileList.get(i).get("pgmtype"));
				rst.put("selected", "1");
				rst.put("chk_selected", "0");
				rst.put("cr_editcon",ReqSayu);
				rst.put("cr_story",fileList.get(i).get("cr_story"));
				rtList.add(rst);
				rst = null;

				
				//기준프로그램에 포함되는 서브프로그램 조회 쿼리
				strQuery.setLength(0);
				strQuery.append("select a.cr_rsrccd,a.cr_rsrcname,a.cr_jobcd,a.cr_itemid, \n");
				strQuery.append("       TO_CHAR(nvl(a.cr_lstdat,a.CR_LASTDATE),'yyyy-mm-dd') as LASTDT,     \n");
				if (ReqCd.equals("02")){ strQuery.append("f.cr_version ver, ");
				} else strQuery.append("a.cr_lstver ver, ");
				strQuery.append("       a.cr_langcd,a.cr_dsncd,b.cm_dirpath,a.cr_lstusr, \n");
				strQuery.append("       C.CM_CODENAME as jawon, d.CM_INFO,f.cr_befver,g.cm_jobname,i.cm_username,f.cr_story  \n");
				strQuery.append("  from cmm0040 i,cmr0020 a,cmm0070 b,CMM0020 C,CMM0036 d,cmr0020 e,cmr1010 f,cmm0102 g,cmm0037 h \n");
				strQuery.append(" where f.cr_acptno =? \n");
				strQuery.append("   and f.cr_baseitem=?  \n");
				strQuery.append("   and f.cr_itemid<>f.cr_baseitem \n");
				strQuery.append("   and a.cr_itemid=f.cr_itemid \n");
				strQuery.append("   and a.cr_status='0' and a.cr_lstver>0 \n");
				strQuery.append("   and a.cr_syscd=d.cm_syscd \n");
				strQuery.append("   and substr(d.cm_info,2,1)='1' \n");
				strQuery.append("   and a.cr_rsrccd=d.cm_rsrccd      \n");
				strQuery.append("   and d.cm_closedt is null   \n");
				strQuery.append("   and C.CM_MACODE='JAWON' and C.CM_MICODE=A.CR_RSRCCD \n");
				strQuery.append("   and b.cm_syscd = a.cr_syscd and a.cr_dsncd = b.cm_dsncd    \n");
				strQuery.append("   and a.cr_syscd = h.cm_syscd  \n");
				strQuery.append("   and h.cm_rsrccd = e.cr_rsrccd  \n");
				strQuery.append("   and a.cr_rsrccd = h.cm_samersrc \n");
				strQuery.append("   and h.cm_rsrccd <> h.cm_samersrc \n");
				strQuery.append("   and e.cr_itemid = f.cr_baseitem   \n");
				strQuery.append("   and g.cm_jobcd = a.cr_jobcd \n");
				strQuery.append("   and nvl(a.cr_lstusr,a.cr_editor)=i.cm_userid \n");
				//pstmt = conn.prepareStatement(strQuery.toString());
				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, baseAcpt);
	            pstmt.setString(2, fileList.get(i).get("cr_itemid"));
	            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();

				while (rs.next()){
					rst = new HashMap<String,String>();
					rst.put("cm_dirpath",rs.getString("cm_dirpath"));
					rst.put("view_dirpath",rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
					rst.put("jobname", rs.getString("cm_jobname"));
					rst.put("jawon", rs.getString("jawon"));
					rst.put("cr_lstver",rs.getString("ver"));
					rst.put("cr_itemid",rs.getString("cr_itemid"));
					rst.put("sysgb", fileList.get(i).get("sysgb"));
					rst.put("cr_syscd", fileList.get(i).get("cr_syscd"));
					rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
					rst.put("cr_langcd",rs.getString("cr_langcd"));
					rst.put("cr_dsncd",rs.getString("cr_dsncd"));
					rst.put("cr_jobcd",rs.getString("cr_jobcd"));
					rst.put("baseitemid",fileList.get(i).get("cr_itemid"));
					rst.put("cm_info",rs.getString("cm_info"));
					rst.put("pcdir",fileList.get(i).get("pcdir"));
					rst.put("localdir",fileList.get(i).get("localdir"));
					rst.put("cr_lstusr",rs.getString("cr_lstusr"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("lastdt",rs.getString("lastdt"));
					rst.put("cr_story",rs.getString("cr_story"));
					rst.put("cr_editcon",ReqSayu);
					rst.put("selected", "1");
					rst.put("chk_selected", "0");
					if ( rs.getString("cm_info").substring(44, 45).equals("1") && strDevHome != null ) {
						for (int j=0;svrList.size()>j;j++) {
							if (svrList.get(j).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
								strVolPath = svrList.get(j).get("cm_volpath");
								strDirPath = rs.getString("cm_dirpath");
								if (strVolPath != null && strVolPath != "") {
									if(strDirPath.length() >= strVolPath.length()){
										if (strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
											rst.put("pcdir1", strDevHome + strDirPath.substring(strVolPath.length()).replace("/","\\"));
										}
									}
								} else {
									rst.put("pcdir1", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
								}
							}
						}
						strVolPath = rst.get("pcdir1").replaceAll("\\\\\\\\", "\\\\");
						rst.put("pcdir1", strVolPath );
						rst.put("view_dirpath", strVolPath );
						rst.put("localdir",  strVolPath );
					}
					rtList.add(rst);
					rst = null;
				}//end of while-loop statement
				rs.close();
				pstmt.close();
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getDownFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getDownFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getDownFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getDownFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getDownFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	public Object[] getAnalFileList(String UserId,ArrayList<HashMap<String,String>> fileList,String ReqCd,String SysCd,String srID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList2	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList3	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int i = 0;
		int j = 0;
		boolean errSw = false;
		try {
			conn = connectionContext.getConnection();

			rtList.clear();
			rtList = sameModule(fileList,SysCd,UserId,srID,conn);
			if (rtList.size()>0) {
				if (rtList.get(0).get("cr_itemid").equals("ERROR")) {
					errSw = true;
					rtList2 = rtList;
				}
				if (!errSw) {
					strQuery.setLength(0);
					rtList2.clear();
					strQuery.append("select a.cr_syscd,a.cr_rsrccd,a.cr_itemid,b.cm_info,a.cr_isrid,\n");
					strQuery.append("       c.cm_dirpath,d.cm_username,e.cm_codename,a.cr_rsrcname  \n");
					strQuery.append("  from cmm0020 e,cmm0040 d,cmr0020 a,cmm0036 b,cmm0070 c       \n");
					strQuery.append(" where a.cr_syscd=? and a.cr_lstver>0           \n");
					strQuery.append("   and a.cr_status not in ('9','3','0')         \n");
					strQuery.append("   and (nvl(a.cr_isrid,'NULL')<>? or            \n");
					strQuery.append("        a.cr_editor<>?)                         \n");
					strQuery.append("   and a.cr_syscd=b.cm_syscd                    \n");
					strQuery.append("   and a.cr_rsrccd=b.cm_rsrccd                  \n");
					strQuery.append("   and a.cr_syscd=c.cm_syscd                    \n");
					strQuery.append("   and a.cr_dsncd=c.cm_dsncd                    \n");
					strQuery.append("   and (substr(b.cm_info,4,1)='1' or            \n");
					strQuery.append("        substr(b.cm_info,9,1)='1')              \n");
					strQuery.append("   and a.cr_editor=d.cm_userid                  \n");
					strQuery.append("   and e.cm_macode='CMR0020'                    \n");
					strQuery.append("   and e.cm_micode=a.cr_status                  \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt =  new LoggableStatement(conn, strQuery.toString());		            
					pstmt.setString(1, SysCd);
					pstmt.setString(2, srID);
					pstmt.setString(3, UserId);		
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						rst = new HashMap<String,String>();
						rst.put("cr_itemid",rs.getString("cr_itemid"));
						rst.put("cm_info",rs.getString("cm_info"));
						rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
						rst.put("cm_dirpath",rs.getString("cm_dirpath"));
						rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
						rst.put("cr_syscd",rs.getString("cr_syscd"));
						rst.put("editor",rs.getString("cm_username"));
						rst.put("status",rs.getString("cm_codename"));
						rst.put("cc_srid",rs.getString("cr_isrid"));
						rtList2.add(rst);
						rst = null;
					}
					rs.close();
					pstmt.close();

					if (rtList2.size()>0) {
						rtList3 = sameModule(rtList2,SysCd,UserId,srID,conn);
						if (rtList3.size()>0) {
							if (rtList3.get(0).get("cr_itemid").equals("ERROR")) {
								errSw = true;
								rtList2 = rtList3;
							}
						}
					}
					if (!errSw) {
						rtList2.clear();
						for (i=0;rtList.size()>i;i++) {
							for (j=0;rtList3.size()>j;j++) {
								if (rtList.get(i).get("cr_itemid").equals(rtList3.get(j).get("cr_itemid"))) {
									rst = new HashMap<String,String>();
									rst.put("outdir", rtList.get(i).get("basedir"));
									rst.put("outname", rtList.get(i).get("basename"));
									rst.put("moddir", rtList.get(i).get("cm_dirpath"));
									rst.put("modname", rtList.get(i).get("cr_rsrcname"));
									rst.put("befdir", rtList3.get(j).get("basedir"));
									rst.put("befname", rtList3.get(j).get("basename"));
									rst.put("editor", rtList3.get(j).get("editor"));
									rst.put("status", rtList3.get(j).get("status"));
									rst.put("srid", rtList3.get(j).get("srid"));
									rtList2.add(rst);
									rst = null;
								}
							}
						}
					}
				}
			} 
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rtList2.toArray();
			rtList = null;
			rtList2 = null;
			rtList3 = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getAnalFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getAnalFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getAnalFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getAnalFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rtList2 != null)  rtList2 = null;
			if (rtList3 != null)  rtList3 = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getAnalFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public ArrayList<HashMap<String,String>> sameModule(ArrayList<HashMap<String,String>> fileList,String sysCD,String UserId,String srID,Connection conn) throws SQLException, Exception {
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();

		ResultSet         rs          = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs2         = null;
		PreparedStatement pstmt2      = null;
		HashMap<String, String>		rst		  = null;
		HashMap<String, String>		tmpObj      = null;
		String            retMsg      = "";
		String            strRsrcName = "";
		String            strExeName  = ""; 
		String            strDirPath  = "";
		String            strSameName = "";
		int               i = 0;
		int               parmCnt = 0;
		boolean           ErrSw   = false;
		

		try {
			Cmr0200  cmr0200 = new Cmr0200();
			rtList.clear();
			for (i=0;fileList.size()>i;i++) {				
	        	parmCnt = 0;
	        	if (fileList.get(i).get("cm_info").substring(3,4).equals("1")) {
	        	    strRsrcName = fileList.get(i).get("cr_rsrcname");
	        	    strExeName = fileList.get(i).get("cr_rsrcname").substring(0,fileList.get(i).get("cr_rsrcname").lastIndexOf("."));
					strQuery.setLength(0);
					strQuery.append("select b.cm_samename,b.cm_samersrc,b.cm_basedir,         \n");
					strQuery.append("       b.cm_samedir,b.cm_basename,b.cm_cmdyn,a.cm_info   \n");
					strQuery.append("  from cmm0036 a,cmm0037 b                               \n");
					strQuery.append(" where b.cm_syscd=? and b.cm_rsrccd=?                    \n");
					strQuery.append("   and b.cm_factcd='04'                                  \n");
					strQuery.append("   and b.cm_syscd=a.cm_syscd                             \n");
					strQuery.append("   and b.cm_samersrc=a.cm_rsrccd                         \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					//pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_syscd"));
			        pstmt.setString(2, fileList.get(i).get("cr_rsrccd"));

		            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			        rs = pstmt.executeQuery();

			        while (rs.next()) {
			            ErrSw = false;
			            
			        	if (rs.getString("cm_samename").indexOf("?#")>=0) {
			        		tmpObj = new HashMap<String,String>();
			        		tmpObj.put("rsrcname",fileList.get(i).get("cr_rsrcname"));
			        		tmpObj.put("dirpath",fileList.get(i).get("cm_dirpath"));
			        		tmpObj.put("samename",rs.getString("cm_samename"));

			        		strSameName = cmr0200.nameChange(tmpObj,conn);
			        		if (strSameName.equals("ERROR")) {
			        			rtList.clear();
			        			
			        			strSameName = "["+strRsrcName+"]에 대한 동시적용모듈정보가 정확하지 않습니다.";
								rst = new HashMap<String,String>();
								rst.put("cr_itemid","ERROR");
								rst.put("cm_dirpath",strSameName);
								rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
								rtList.add(rst);
								rst = null;
								ErrSw = true;
			        		}
			        	} else {
			        		strSameName = rs.getString("cm_samename").replace("*", strExeName);
			        	}
			        	strDirPath = fileList.get(i).get("cm_dirpath");
			        	if (rs.getString("cm_basedir") != null) {
			        		if (!rs.getString("cm_basedir").equals("cm_samedir")){
			        			if (rs.getString("cm_basedir").equals("*")) strDirPath = rs.getString("cm_samedir");
			        			else if (rs.getString("cm_samedir").indexOf("?#")>=0) {
					        		tmpObj = new HashMap<String,String>();
					        		tmpObj.put("rsrcname",fileList.get(i).get("cr_rsrcname"));
					        		tmpObj.put("dirpath",fileList.get(i).get("cm_dirpath"));
					        		tmpObj.put("samename",rs.getString("cm_samedir"));

					        		retMsg = cmr0200.nameChange(tmpObj,conn);
					        		if (retMsg.equals("ERROR")) {
					        			rtList.clear();
					        			retMsg = "["+strRsrcName+"]에 대한 동시적용모듈정보가 정확하지 않습니다.";
										rst = new HashMap<String,String>();
										rst.put("cr_itemid","ERROR");
										rst.put("cm_dirpath",retMsg);
										rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
										rtList.add(rst);
										rst = null;
										ErrSw = true;
					        		}
			        			} else {
			        				strDirPath = strDirPath.replace(rs.getString("cm_basedir"), rs.getString("cm_samedir"));
			        			}
			        		}
			        	}
			        	//ecamsLogger.error("*** samedir, samename ++++"+strWork3 + ", "+ strDirPath);
			        	if (ErrSw == false) {
				        	parmCnt = 0;
				        	strQuery.setLength(0);
							strQuery.append("select b.cm_dirpath,a.cr_itemid,a.cr_rsrcname  \n");
						   	strQuery.append("  from cmm0070 b,cmr0020 a             \n");
						   	strQuery.append(" where a.cr_syscd=? and a.cr_rsrccd=?  \n");
						   	strQuery.append("   and upper(a.cr_rsrcname)= upper(?)  \n");
						   	strQuery.append("   and upper(b.cm_dirpath)=upper(?)    \n");
						   	strQuery.append("   and a.cr_status<>'9'                \n");
						   	strQuery.append("   and a.cr_syscd=b.cm_syscd           \n");
						   	strQuery.append("   and a.cr_dsncd=b.cm_dsncd           \n");
						    pstmt2 = conn.prepareStatement(strQuery.toString());
		//					pstmt2 =  new LoggableStatement(conn, strQuery.toString());
				            pstmt2.setString(++parmCnt, fileList.get(i).get("cr_syscd"));
				            pstmt2.setString(++parmCnt, rs.getString("cm_samersrc"));
						   	pstmt2.setString(++parmCnt,strSameName);
						   	pstmt2.setString(++parmCnt,strDirPath);
		//		            //ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
				            rs2 = pstmt2.executeQuery();

				            if (rs2.next()) {
				            	rst = new HashMap<String,String>();
								rst.put("basedir",fileList.get(i).get("cm_dirpath"));
								rst.put("basename",fileList.get(i).get("cr_rsrcname"));
								if (fileList.get(i).get("editor") != null) {
									rst.put("editor", fileList.get(i).get("editor"));
									rst.put("status", fileList.get(i).get("status"));
									rst.put("srid", fileList.get(i).get("cc_srid"));
								}
								rst.put("baseitem",fileList.get(i).get("cr_itemid"));
								rst.put("cr_itemid",rs2.getString("cr_itemid"));
								rst.put("cm_dirpath",rs2.getString("cm_dirpath"));
								rst.put("cr_rsrcname",rs2.getString("cr_rsrcname"));
								rtList.add(rst);
								rst = null;
				            }
				            rs2.close();
				            pstmt2.close();
			        	}
			        }
			        rs.close();
			        pstmt.close();
				}
				if (ErrSw == false && fileList.get(i).get("cm_info").substring(8,9).equals("1")) {		
				   	strQuery.setLength(0);
					strQuery.append("select b.cm_dirpath,a.cr_itemid,a.cr_rsrcname   \n");
				   	strQuery.append("  from cmm0070 b,cmr0020 a,cmd0011 c            \n");
				   	strQuery.append(" where c.cd_itemid=?                            \n");
				   	strQuery.append("   and c.cd_prcitem=a.cr_itemid                 \n");	
				   	strQuery.append("   and a.cr_status<>'9'                         \n");
				   	strQuery.append("   and a.cr_syscd=b.cm_syscd                    \n");
				   	strQuery.append("   and a.cr_dsncd=b.cm_dsncd                    \n");			   	
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt =  new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_itemid"));
		            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
		            rs = pstmt.executeQuery();

		            while (rs.next()) {
		            	rst = new HashMap<String,String>();
						rst.put("basedir",fileList.get(i).get("cm_dirpath"));
						rst.put("basename",fileList.get(i).get("cr_rsrcname"));
						rst.put("baseitem",fileList.get(i).get("cr_itemid"));
						rst.put("cr_itemid",rs.getString("cr_itemid"));
						rst.put("cm_dirpath",rs.getString("cm_dirpath"));
						rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
						if (fileList.get(i).get("editor") != null) {
							rst.put("editor", fileList.get(i).get("editor"));
							rst.put("status", fileList.get(i).get("status"));
							rst.put("srid", fileList.get(i).get("cc_srid"));
						}
						rtList.add(rst);
						rst = null;
		            }
		            pstmt.close();
		            rs.close();
				}
			}
			rs = null;
			pstmt = null;

			//ecamsLogger.error("+++++++++CHECK-IN LIST E N D+++");
			//rtList = null;

			return rtList;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr100.sameModule() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr100.sameModule() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr100.sameModule() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr100.sameModule() Exception END ##");
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)	rtObj = null;
			if (rtList != null) rtList = null;
		}
	}
	/**
	 * <PRE>
	 * 1. MethodName	: getFileList
	 * 2. ClassName		: Cmr0100
	 * 3. Commnet			:
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 23. 오후 2:43:59
	 * </PRE>
	 * 		@return Object[]
	 * 		@param UserId
	 * 		@param SysCd
	 * 		@param SysGb
	 * 		@param DsnCd
	 * 		@param RsrcCd
	 * 		@param RsrcName
	 * 		@param ReqCd
	 * 		@param JobCd
	 * 		@param UpLowSw
	 * 		@param selfSw
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Object[] getFileList(HashMap<String, String> etcData) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		String            strDevHome  = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  svrList	  = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			int				  pstmtcount  = 1;
			String			  szDsnCD = "";
			String			  strRsrcCd = "";
			String            strRsrc[] = null;
			String            strVolPath = "";
			String            strDirPath = "";
			int               i = 0;
			String UserId = etcData.get("userid");
			String SysCd = etcData.get("syscd");
			String SysGb = etcData.get("sysgb");
			String DsnCd = etcData.get("dsncd");
			String RsrcCd = etcData.get("rsrccd");
			String RsrcName = etcData.get("rsrcname");
			String ReqCd = etcData.get("reqcd");
			
			//로컬 프로그램에 필요한 로직. 홈디렉토리 
			SysInfo sysinfo = new SysInfo();
			strQuery.setLength(0);
			//strQuery.append("select cd_devhome from cmd0200               \n");
			strQuery.append("select cd_devhome from cmd0300               \n");
			strQuery.append(" where cd_syscd=? and cd_userid=?            \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, SysCd);
			pstmt.setString(2, UserId);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strDevHome = rs.getString("cd_devhome")+"\\";
			}
			rs.close();
			pstmt.close();

			svrList = sysinfo.getHomePath_conn(SysCd, conn);
			sysinfo = null;

			if (DsnCd  != null && !DsnCd.equals("")){
				if (DsnCd.length() == 7 && DsnCd.indexOf("/") < 0){
					szDsnCD = DsnCd;
				}
			}
			strQuery.setLength(0);
			strQuery.append("select cm_rsrccd from cmm0036                     \n");
			strQuery.append(" where cm_syscd=? and cm_closedt is null          \n");
			strQuery.append("   and substr(cm_info, 2, 1)='1'                  \n");
			strQuery.append("   and substr(cm_info, 26, 1)='0'                 \n");
			strQuery.append("   and cm_rsrccd not in (select cm_samersrc       \n");
			strQuery.append("                           from cmm0037           \n");
			strQuery.append("                          where cm_syscd=?       \n");
			strQuery.append("                            and cm_rsrccd<>cm_samersrc) \n");
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
			strQuery.setLength(0);
			strQuery.append("select /*+ ALL ROWS */                                    \n");
			strQuery.append("       a.CR_RSRCNAME,a.CR_STORY,a.CR_LSTVER,a.cr_acptno,  \n");
			strQuery.append("       TO_CHAR(a.CR_LSTDAT,'yyyy-mm-dd') as LASTDT,       \n");
			strQuery.append("       a.CR_SYSCD,a.CR_RSRCCD,a.CR_LANGCD,a.CR_DSNCD,     \n");
			strQuery.append("       a.CR_JOBCD,a.CR_FILESIZE,a.CR_FILEDATE,a.cr_nomodify,\n");
			strQuery.append("       a.CR_ITEMID,a.CR_STATUS,c.CM_CODENAME as CODENAME, \n");
			strQuery.append("       d.CM_USERNAME,e.CM_DIRPATH, f.CM_CODENAME as JAWON,\n");
			strQuery.append("       b.cm_info,g.cm_jobname,a.cr_lstusr                 \n");
			strQuery.append("  from cmm0020 f,cmm0070 e,cmm0040 d,cmm0020 c,cmm0036 b, \n");
			strQuery.append("       cmr0020 a ,cmm0102 g,cmm0044 h \n");
			strQuery.append(" where a.cr_syscd = ? and                               \n");
            if (!RsrcCd.equals("") && !RsrcCd.equals("0000")){
				strQuery.append(" a.cr_rsrccd=? and                                    \n");
			}
			strQuery.append("a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd and   \n");
			strQuery.append("h.cm_closedt is null and h.cm_userid=? and            \n");
			if (!RsrcName.equals("")){
				if (!RsrcName.equals("*")){
					if (RsrcName.indexOf(",") >= 0){
						strQuery.append(" a.cr_dsncd= ? and \n");
					}
					strQuery.append(" ( upper(a.cr_rsrcname) like upper(?) or upper(a.cr_story) like upper(?) ) and   \n");
				}
			}
			strQuery.append("a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd and        \n");
			if (null != DsnCd && !DsnCd.equals("")){
				if ( DsnCd.substring(0, 1).equals("G") ){//하위폴더 아니면서  제일상위 폴더 클릭 했을때.
					strQuery.append(" e.cm_dirpath = ? and \n");
				} else if (!DsnCd.substring(0, 1).equals("F")){
					strQuery.append(" a.cr_dsncd= ? and                           \n");
				} else{
					strQuery.append("( e.cm_dirpath = ? or e.cm_dirpath like ?) and \n");
				}
			}
			if (ReqCd.equals("03")){
				strQuery.append(" a.cr_status='3' and                             \n");
				strQuery.append(" to_number(a.cr_lstver)=0  and                   \n");
			}
			else if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("08")){
			/*	strQuery.append(" substr(b.cm_info,2,1)='1' and substr(b.cm_info,26,1)='0' and \n");
				strQuery.append(" b.cm_closedt is null and                                    \n");
				strQuery.append(" b.cm_rsrccd not in (select cm_samersrc from cmm0037         \n");
				strQuery.append("                      where cm_syscd=? and cm_factcd='04')   \n"); */
				if (RsrcCd.equals("0000") || RsrcCd.equals("") || RsrcCd == null) {
					strQuery.append("a.cr_rsrccd in (");
					for (i=0;strRsrc.length>i;i++) {
						if (i>0) strQuery.append(", ? ");
						else strQuery.append("? ");
					}
					strQuery.append(") and                                             \n");
				}
				strQuery.append("a.cr_status not in('9') and                           \n");
				/*if (ReqCd.equals("01") || ReqCd.equals("08")){
					strQuery.append(" to_number(a.cr_lstver)>0  and \n");
				}
				else */
				if (ReqCd.equals("02")){
					strQuery.append(" to_number(a.cr_lstver)>1  and                     \n");
				}
			}
			strQuery.append("nvl(a.cr_lstusr,a.cr_editor)=d.cm_userid and               \n");
			strQuery.append("a.cr_jobcd = g.cm_jobcd and                                \n");
			strQuery.append("c.cm_macode='CMR0020' and a.cr_status=c.cm_micode and      \n");
			strQuery.append("f.cm_macode='JAWON' and a.cr_rsrccd=f.cm_micode and        \n");
			strQuery.append("a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd          \n");

            //pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(pstmtcount++, SysCd);
            if (RsrcCd != null && !RsrcCd.equals("") && !RsrcCd.equals("0000")) pstmt.setString(pstmtcount++, RsrcCd);
            pstmt.setString(pstmtcount++, UserId);
			if (!RsrcName.equals("")){
				if (!RsrcName.equals("*")){
					if (RsrcName.indexOf(",") >= 0 && RsrcName.length()>5){
						pstmt.setString(pstmtcount++, RsrcName.substring(0,5));
						pstmt.setString(pstmtcount++, RsrcName.substring(6));
						pstmt.setString(pstmtcount++, RsrcName.substring(6));
					}
					else{
						pstmt.setString(pstmtcount++, "%"+RsrcName.replaceAll("[*]","")+"%");
						pstmt.setString(pstmtcount++, "%"+RsrcName.replaceAll("[*]","")+"%");
					}
				}
			}
			if (null != DsnCd && !DsnCd.equals("")){
				if ( DsnCd.substring(0, 1).equals("G") ){
					pstmt.setString(pstmtcount++, DsnCd.substring(1).substring(0, DsnCd.substring(1).length()-1));
				} else if (!DsnCd.substring(0, 1).equals("F")){
					pstmt.setString(pstmtcount++, szDsnCD);
				} else{
					pstmt.setString(pstmtcount++, DsnCd.substring(1).substring(0, DsnCd.substring(1).length()-1));
					pstmt.setString(pstmtcount++, DsnCd.substring(1)+ "%");
				}
			}
            if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("08")) {
            	if (RsrcCd.equals("0000") || RsrcCd.equals("") || RsrcCd == null) {
	            	for (i=0;strRsrc.length>i;i++) {
	            		pstmt.setString(pstmtcount++, strRsrc[i]);
	            	}
            	}
            }
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				rst.put("cr_story",rs.getString("cr_story"));
				rst.put("jobname", rs.getString("cm_jobname"));
				rst.put("jawon",rs.getString("jawon"));
				if (ReqCd.equals("02")){
					rst.put("cr_lstver","sel");
				}
				else{
					rst.put("cr_lstver",rs.getString("cr_lstver"));
				}
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("codename",rs.getString("codename"));
				rst.put("sysgb",SysGb);
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_langcd",rs.getString("cr_langcd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				/*
				rst.put("cr_filesize",rs.getString("cr_filesize"));
				rst.put("cr_filedate",rs.getString("cr_filedate"));
				*/
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("baseitemid",rs.getString("cr_itemid"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("selAcptno",rs.getString("cr_acptno"));
				rst.put("cr_lstusr", rs.getString("cr_lstusr"));

				if (rs.getString("cr_nomodify") != null)
					rst.put("cr_nomodify",rs.getString("cr_nomodify"));
				else
					rst.put("cr_nomodify","0");
			
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("selected_flag","0");
				rst.put("view_dirpath",rs.getString("cm_dirpath"));
				if (rs.getString("cm_info").substring(44, 45).equals("1")) {
					if (strDevHome != null) {
						for (i=0;svrList.size()>i;i++) {
							if (svrList.get(i).get("cm_rsrccd").equals(rs.getString("cr_rsrccd"))) {
								strVolPath = svrList.get(i).get("cm_volpath");
								strDirPath = rs.getString("cm_dirpath");
								if (strVolPath != null && strVolPath != "") {
									if(strDirPath.length() >= strVolPath.length()){
										if (strDirPath.substring(0,strVolPath.length()).equals(strVolPath)) {
											rst.put("pcdir", strDevHome + strDirPath.substring(strVolPath.length()).replace("/","\\"));
										}
									}
								} else {
									rst.put("pcdir", strDevHome + rs.getString("cm_dirpath").replace("/", "\\"));
								}
							}
						}
						if (rst.get("pcdir") != null && rst.get("pcdir") != "") {
							strVolPath = rst.get("pcdir").replaceAll("\\\\\\\\", "\\\\");
							rst.put("pcdir1", strVolPath );
							rst.put("localdir", strVolPath.replaceAll("\\\\", "\\\\\\\\") );
							rst.put("view_dirpath", strVolPath);
						} else {
							rst.put("view_dirpath", "[관리자연락]홈디렉토리설정오류");
							rst.put("selected_flag","1");
						}
					} else {
						rst.put("view_dirpath", "사용자환경설정에서 홈디렉토리설정요망");
						rst.put("selected_flag","1");
					}
				}
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;


			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getFileList_excel(ArrayList<HashMap<String,String>> fileList,String SysCd,String SysGb,String ReqCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		int				  filecnt = 0;
		int				  cmr0020cnt = 0;

		try {
			conn = connectionContext.getConnection();

			rtList.clear();
			for (int i=1;i<fileList.size();i++){
				strQuery.setLength(0);
				pstmtcount = 1;
				strQuery.append("select a.CR_RSRCNAME,a.CR_STORY,a.CR_LSTVER,         \n");
				strQuery.append("       TO_CHAR(a.CR_LASTDATE,'yyyy-mm-dd') as LASTDT,\n");
				strQuery.append("       a.cr_nomodify,a.CR_SYSCD,a.CR_RSRCCD,         \n");
				strQuery.append("       a.CR_DSNCD,a.CR_JOBCD,            \n");
				strQuery.append("       a.CR_FILESIZE,a.CR_FILEDATE,a.CR_ITEMID,      \n");
				strQuery.append("       a.CR_STATUS,c.CM_CODENAME as CODENAME,        \n");
				strQuery.append("       d.CM_USERNAME,e.CM_DIRPATH,f.CM_CODENAME as JAWON,\n");
				strQuery.append("       b.cm_info,g.cm_jobname           \n");
				strQuery.append("  from cmm0020 f,cmm0070 e,cmm0040 d,cmm0020 c,      \n");
				strQuery.append("       cmm0036 b,cmr0020 a ,cmm0102 g                \n");
				strQuery.append("where a.cr_syscd = ? and \n");
				strQuery.append(" a.cr_rsrcname= ? and \n");

				if (ReqCd.equals("03")){
					strQuery.append(" a.cr_status='3' and \n");
					strQuery.append(" to_number(a.cr_lstver)=0  and \n");
				}
				else if (ReqCd.equals("01") || ReqCd.equals("02") || ReqCd.equals("08")){
					strQuery.append(" substr(b.cm_info,2,1)='1' and substr(b.cm_info,26,1)='0' and \n");
					strQuery.append(" b.cm_closedt is null and \n");
					strQuery.append(" a.cr_status not in('C','9') and \n");
					if (ReqCd.equals("01") || ReqCd.equals("08")){
						strQuery.append(" to_number(a.cr_lstver)>0  and \n");
					}
					else if (ReqCd.equals("02")){
						strQuery.append(" to_number(a.cr_lstver)>1  and \n");
					}
				}
				strQuery.append("a.cr_editor=d.cm_userid and a.cr_jobcd = g.cm_jobcd and \n");
				strQuery.append("c.cm_macode='CMR0020' and a.cr_status=c.cm_micode and \n");
				strQuery.append("f.cm_macode='JAWON' and a.cr_rsrccd=f.cm_micode and \n");
				strQuery.append("a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd and \n");
				strQuery.append("a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd   \n");
				strQuery.append("order by a.cr_rsrcname                              \n");

	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());

	            pstmt.setString(pstmtcount++, SysCd);
	            pstmt.setString(pstmtcount++, fileList.get(i).get("cr_rsrcname"));

				////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());

	            rs = pstmt.executeQuery();

	            cmr0020cnt = 0;

				while (rs.next()){
					++cmr0020cnt;
					if (cmr0020cnt > 1) {
						rst = new HashMap<String,String>();
						rst = rtList.get(rtList.size()-1);
						rst.put("errmsg", "파일명중복");
						rtList.set(rtList.size()-1, rst);
					}
					rst = new HashMap<String,String>();
					rst.put("ID", Integer.toString(++filecnt));
					rst.put("linenum",Integer.toString(i));
					rst.put("cmr0020cnt","1");
					rst.put("cm_dirpath",rs.getString("cm_dirpath"));
					rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
					rst.put("jobname", rs.getString("cm_jobname"));
					rst.put("jawon",rs.getString("jawon"));
					if (ReqCd.equals("02")){
						rst.put("cr_lstver","sel");
					}
					else{
						rst.put("cr_lstver",rs.getString("cr_lstver"));
					}
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("lastdt",rs.getString("lastdt"));
					rst.put("codename",rs.getString("codename"));
					rst.put("sysgb",SysGb);
					rst.put("cr_syscd",rs.getString("cr_syscd"));
					rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
					rst.put("cr_dsncd",rs.getString("cr_dsncd"));
					rst.put("cr_jobcd",rs.getString("cr_jobcd"));
					if (rs.getString("cr_nomodify") != null)
						rst.put("cr_nomodify",rs.getString("cr_nomodify"));
					else
						rst.put("cr_nomodify","0");
					rst.put("baseitemid",rs.getString("cr_itemid"));
					rst.put("cr_itemid",rs.getString("cr_itemid"));
					rst.put("cr_status",rs.getString("cr_status"));
					rst.put("cm_info",rs.getString("cm_info"));
					rst.put("selected_flag","0");
					if (cmr0020cnt > 1) rst.put("errmsg", "파일명중복");
					else if (rs.getString("cr_status").equals("0")){
						rst.put("errmsg", "정상");
					} else rst.put("errmsg",rs.getString("codename"));
					rst.put("selected_flag","0");
					rtList.add(rst);
					rst = null;
				}//end of while-loop statement

				rs.close();
				pstmt.close();

				if (filecnt == 0) {
					rst = new HashMap<String,String>();
					rst.put("linenum",Integer.toString(i));
					rst.put("cr_rsrcname",fileList.get(i).get("cr_rsrcname"));
					rst.put("cmr0020cnt","0");
					rst.put("errmsg", "원장없음");
					rst.put("selected_flag","0");
					rtList.add(rst);
					rst = null;
				}
			}

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			rtObj =  rtList.toArray();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getFileList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String request_Check_Out(ArrayList<HashMap<String,String>> chkOutList,HashMap<String,String> etcData, ArrayList<HashMap<String,Object>> ConfList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		AutoSeq			  autoseq	  = new AutoSeq();
		UserInfo		  userInfo	  = new UserInfo();
		HashMap<String, String>			  rst		  = null;
		//CodeInfo		  codeInfo	  = new CodeInfo();
		String			  AcptNo	  = null;
		int				  i;
		int				  j;
		//HashMap<String,String>	rData = null;

		try {
			conn = connectionContext.getConnection();
			int totcnt = 0;
			for (i=0;i<chkOutList.size();i++){
				//ecamsLogger.error(chkOutList.get(i).get("selected"));
				if (chkOutList.get(i).get("baseitemid").equals(chkOutList.get(i).get("cr_itemid"))) {
	        		for (j=i+1;chkOutList.size()>j;j++) {
	        			if (chkOutList.get(j).get("baseitemid").equals(chkOutList.get(i).get("cr_itemid"))) {
	        				rst = new HashMap<String, String>();
	        				rst = chkOutList.get(j);
	        				rst.put("selected", chkOutList.get(i).get("selected"));
	        				chkOutList.set(j, rst);
	        			} else break;
	        		}
	        	}
	        }
	        for (i=0;i<chkOutList.size();i++){
	        	if (chkOutList.get(i).get("baseitemid").equals(chkOutList.get(i).get("cr_itemid"))) {
	        		++totcnt;
		        	strQuery.setLength(0);
		        	strQuery.append("select cr_status,cr_editor,cr_story,cr_rsrcname from cmr0020 \n");
		        	strQuery.append("where cr_itemid = ?  \n");
		        	strQuery.append("and cr_status <> '0' \n");

		        	pstmt = conn.prepareStatement(strQuery.toString());

		        	pstmt.setString(1, chkOutList.get(i).get("cr_itemid"));

		        	rs = pstmt.executeQuery();

		        	while (rs.next()){
		        		throw new Exception(rs.getString("cr_rsrcname")+" 파일은 " + rs.getString("cr_editor") +"님이 Check-Out 하셨습니다.");
		        	}

		        	rs.close();
		        	pstmt.close();
	        	}
	        }

	        String strTeam = userInfo.getUserInfo_sub(conn,etcData.get("UserID"),"cm_project");
	        userInfo = null;

	        Cmr0200 cmr0200 = new Cmr0200();

	        int wkC = totcnt/300;
	        int wkD = totcnt%300;
	        if (wkD>0) wkC = wkC + 1;
            String svAcpt[] = null;
            svAcpt = new String [wkC];
            for (j=0;wkC>j;j++) {
            	do {
    		        AcptNo = autoseq.getSeqNo(conn,etcData.get("ReqCD"));

    		        i = 0;
    		        strQuery.setLength(0);
    		        strQuery.append("select count(*) as cnt from cmr1000 \n");
    	        	strQuery.append(" where cr_acptno= ?                 \n");

    	        	pstmt = conn.prepareStatement(strQuery.toString());
    	        	pstmt.setString(1, AcptNo);

    	        	rs = pstmt.executeQuery();

    	        	if (rs.next()){
    	        		i = rs.getInt("cnt");
    	        	}
    	        	rs.close();
    	        	pstmt.close();
    	        } while(i>0);
            	svAcpt[j] = AcptNo;
            }
        	int    seq = 0;
        	int    reqcnt = 0;
        	String retMsg = "";
            autoseq = null;
            conn.setAutoCommit(false);
        	boolean insSw = false;
        	for (i=0 ; i<chkOutList.size() ; i++){
        		insSw = false;

        		if (i == 0) insSw = true;
        		else {
        			wkC = reqcnt%300;
        			if (wkC == 0) insSw = true;
        		}

        		if (insSw == true) {
        			if (reqcnt>=300) {
        				retMsg = cmr0200.request_Confirm(AcptNo,chkOutList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
        				if (!retMsg.equals("OK")) {
        					conn.rollback();
        					conn.close();
        					throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
        				}
        			}
        			wkC = reqcnt/300;
        			AcptNo = svAcpt[wkC];

        			strQuery.setLength(0);
                	strQuery.append("insert into cmr1000 ");
                	strQuery.append("(CR_ACPTNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_ACPTDATE,CR_STATUS,CR_TEAMCD,CR_QRYCD, \n");
                	strQuery.append("CR_PASSOK,CR_PASSCD,CR_SAYU,CR_EMGCD,CR_EDITOR,CR_ITSMID,CR_ITSMTITLE,CR_ECLIPSE) values \n");
                	strQuery.append("(?,?,?,?,sysdate,'0',?,?,  '0',?,?,'0',?,?,?,?)");
                	pstmt = conn.prepareStatement(strQuery.toString());
                	//pstmt = new LoggableStatement(conn, strQuery.toString());
                	pstmtcount = 1;
                	pstmt.setString(pstmtcount++, AcptNo);//CR_ACPTNO
                	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_syscd"));//CR_SYSCD
                	pstmt.setString(pstmtcount++, chkOutList.get(0).get("sysgb"));//CR_SYSGB
                	pstmt.setString(pstmtcount++, chkOutList.get(0).get("cr_jobcd"));//CR_JOBCD

                	pstmt.setString(pstmtcount++, strTeam);//CR_TEAMCD
                	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));//CR_QRYCD


                	pstmt.setString(pstmtcount++, etcData.get("Sayu"));//CR_PASSCD
                	pstmt.setString(pstmtcount++, etcData.get("Sayu"));//CR_SAYU
                	pstmt.setString(pstmtcount++, etcData.get("UserID"));//CR_EDITOR
                	pstmt.setString(pstmtcount++, etcData.get("srid"));//CR_ITSMID
                	pstmt.setString(pstmtcount++, etcData.get("cc_reqtitle"));//CR_ITSMTITLE
                	pstmt.setString(pstmtcount++, etcData.get("ckoutpos"));//CR_ECLIPSE

                	//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
                	pstmt.executeUpdate();

                	pstmt.close();
                	seq = 0;
        		}

        		strQuery.setLength(0);
            	strQuery.append("insert into cmr1010 ");
            	strQuery.append("(CR_ACPTNO,CR_SERNO,CR_SYSCD,CR_SYSGB,CR_JOBCD,CR_STATUS,CR_QRYCD, ");
            	strQuery.append("CR_RSRCCD,CR_LANGCD,CR_DSNCD,CR_DSNCD2,CR_RSRCNAME,CR_RSRCNAM2,CR_CHGCD,CR_TSTCHG,");
            	strQuery.append("CR_VERSION,CR_EDITOR,CR_BASENO,CR_BASEITEM,CR_ITEMID,CR_EDITCON,CR_SVRYN,CR_VERYN,CR_STORY) values ( ");
            	strQuery.append("?, ?, ?, ?, ?, '0', ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Y','N',?)");

            	pstmtcount = 1;
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setInt(pstmtcount++, ++seq);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_syscd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("sysgb"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_jobcd"));

            	pstmt.setString(pstmtcount++, etcData.get("ReqCD"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrccd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_langcd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_dsncd"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("pcdir1"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_rsrcname"));
            	if (chkOutList.get(i).get("chkrstcd") != null) {
            		if (chkOutList.get(i).get("chkrstcd").equals("L")) {
            			pstmt.setString(pstmtcount++, "7");
                		pstmt.setString(pstmtcount++, "7");
            		} else if (chkOutList.get(i).get("chkrstcd").equals("M")) {
            			pstmt.setString(pstmtcount++, "M");
                		pstmt.setString(pstmtcount++, "M");
            		} else {
            			pstmt.setString(pstmtcount++, "0");
                		pstmt.setString(pstmtcount++, "0");
            		}
            	} else {
            		pstmt.setString(pstmtcount++, "0");
            		pstmt.setString(pstmtcount++, "0");
            	}
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_lstver"));
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, AcptNo);
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("baseitemid"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));
            	pstmt.setString(pstmtcount++,etcData.get("Sayu"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_story"));

            	pstmt.executeUpdate();
            	pstmt.close();

            	strQuery.setLength(0);
            	strQuery.append("update cmr0020 set ");
            	strQuery.append("cr_status='4', ");
            	strQuery.append("cr_editor= ?   ");
            	//20140723 neo. cr_lstusr 값 업데이트. 
            	//strQuery.append("cr_lstusr= ? ");
            	strQuery.append("where cr_itemid= ? ");
            	pstmt = conn.prepareStatement(strQuery.toString());
            	pstmtcount = 1;
            	pstmt.setString(pstmtcount++, etcData.get("UserID"));
            	pstmt.setString(pstmtcount++, chkOutList.get(i).get("cr_itemid"));

            	pstmt.executeUpdate();
            	pstmt.close();

            	if (chkOutList.get(i).get("baseitemid").equals(chkOutList.get(i).get("cr_itemid"))) {
        			++reqcnt;
        		}
        	}
 
        	retMsg = cmr0200.request_Confirm(AcptNo,chkOutList.get(0).get("cr_syscd"),etcData.get("ReqCD"),etcData.get("UserID"),true,ConfList,conn);
			if (!retMsg.equals("OK")) {
				conn.rollback();
				conn.close();
				throw new Exception("결재정보등록 중 오류가 발생하였습니다. 관리자에게 연락하여 주시기 바랍니다.");
			}
        	conn.commit();
        	conn.close();

        	rs = null;
    		pstmt = null;
    		conn = null;

        	return AcptNo;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					conn.rollback();
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0100.request_Check_Out() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.request_Check_Out() SQLException END ##");
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
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
			ecamsLogger.error("## Cmr0100.request_Check_Out() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.request_Check_Out() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.request_Check_Out() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getLinkList(String UserId,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			pstmtcount = 1;
			strQuery.setLength(0);
			strQuery.append("select /*+ ALL ROWS */                                    \n");
			strQuery.append("       a.CR_RSRCNAME,a.CR_STORY,a.CR_LSTVER,a.cr_acptno,  \n");
			strQuery.append("       TO_CHAR(a.CR_LASTDATE,'yyyy-mm-dd') as LASTDT,     \n");
			strQuery.append("       a.CR_SYSCD,a.CR_RSRCCD,a.CR_LANGCD,a.CR_DSNCD,     \n");
			strQuery.append("       a.CR_JOBCD,a.CR_FILESIZE,a.CR_FILEDATE,a.cr_nomodify,\n");
			strQuery.append("       a.CR_ITEMID,a.CR_STATUS,c.CM_CODENAME as CODENAME, \n");
			strQuery.append("       d.CM_USERNAME,e.CM_DIRPATH, f.CM_CODENAME as JAWON,\n");
			strQuery.append("       b.CM_INFO,g.cm_jobname                             \n");
			strQuery.append("  from cmm0020 f,cmm0070 e,cmm0040 d,cmm0020 c,cmm0036 b, \n");
			strQuery.append("       cmr0020 a,cmm0102 g,cmm0044 h,cmr1090 i            \n");
			strQuery.append(" where i.cr_acptno=? and i.cr_syscd=a.cr_syscd            \n");
			strQuery.append("   and a.cr_lstver>0                                      \n"); //and i.cr_itemid=a.cr_itemid
			strQuery.append("   and a.cr_syscd=h.cm_syscd and a.cr_jobcd=h.cm_jobcd    \n");
			strQuery.append("   and h.cm_closedt is null and h.cm_userid=?             \n");
			strQuery.append("   and a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd    \n");
			strQuery.append("   and a.cr_editor=d.cm_userid and a.cr_jobcd = g.cm_jobcd\n");
			strQuery.append("   and c.cm_macode='CMR0020' and a.cr_status=c.cm_micode  \n");
			strQuery.append("   and f.cm_macode='JAWON' and a.cr_rsrccd=f.cm_micode    \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd  \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(pstmtcount++, AcptNo);
            pstmt.setString(pstmtcount++, UserId);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				/*
				rst.put("cr_story",rs.getString("cr_story"));
				*/
				rst.put("jobname", rs.getString("cm_jobname"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("codename",rs.getString("codename"));
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_langcd",rs.getString("cr_langcd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));

				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("selAcptno",rs.getString("cr_acptno"));
				if (rs.getString("cr_nomodify") != null)
					rst.put("cr_nomodify",rs.getString("cr_nomodify"));
				else
					rst.put("cr_nomodify","0");
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("selected_flag","0");
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement

			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("delete cmr1090 where cr_acptno=?  \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.executeUpdate();

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			rtObj =  rtList.toArray();
			rtList = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getLinkList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getLinkList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getLinkList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getLinkList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtObj != null)  rtObj = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.getLinkList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	} //getLinkList

	public String svrFileMake(String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            strErMsg    = "0";
		String            shFileName  = "";
		String            strParm     = "";
		int               ret         = 0;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			//ecamsLogger.debug("+++++++++prcCd========"+prcCd);

			conn = connectionContext.getConnection();
			
			//로컬로 파일을 전송 하는 프로그램이 아닐경우 CMR1010에 0000 완료 표시 한다.
			strQuery.setLength(0);
			strQuery.append("update cmr1010 set cr_status = '9' , cr_putcode='0000',cr_pid='0000',cr_sysstep=0  \n");
			strQuery.append(" where cr_acptno=? and cr_chgcd = '7'   \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			pstmt.executeUpdate();
			pstmt.close();
			
			//CMR1010 테이블에서 cr_chgcd <> ='7' 아닐경우만 한다. 체크아웃 해줘야 하니깐. 7:레파지토리 데이터 무시!
			strQuery.setLength(0);
			strQuery.append("delete cmr1011                                \n");
			strQuery.append(" where cr_acptno=? and cr_prcsys='SYSFMK'     \n");
			strQuery.append("   and cr_serno in                            \n");
			strQuery.append("    (select cr_serno from cmr1010             \n");
			strQuery.append("      where cr_acptno=?                       \n");
			strQuery.append("        and nvl(cr_putcode,'ERR1')<>'0000'    \n");
			strQuery.append("        and cr_chgcd <> '7')   \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, AcptNo);
			pstmt.executeUpdate();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("update cmr1010 set cr_putcode='',cr_pid='',cr_sysstep=0  \n");
			strQuery.append(" where cr_acptno=? and nvl(cr_putcode,'ERR1')<>'0000'    \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.executeUpdate();
			pstmt.close();
            
			Cmr0200 cmr0200 = new Cmr0200();
			shFileName = AcptNo +"filemake.sh";
			strParm = "./ecams_acct "+ AcptNo;
			ret = cmr0200.execShell(shFileName, strParm, false);
			if (ret != 0) {
				strErMsg = "파일생성작업에 실패하였습니다. [" + strParm + "]" + "ret=["+ret+"]";
				return strErMsg;
			}

			//ecamsLogger.debug("++++ run ecams_acct ++++ecams_acct " + AcptNo + "]");
			strQuery.setLength(0);
	        strQuery.append("select count(*) as cnt                     \n");
	        strQuery.append("  from cmr9900                             \n");
	        strQuery.append(" where cr_acptno=? and cr_locat='00'       \n");
	        strQuery.append("   and cr_team='SYSFMK' and cr_status='0'  \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1, AcptNo);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt("cnt") > 0) strErMsg = "파일생성 작업 중 오류가 발생한 건이 있습니다. 확인 후 조치하시기 바랍니다.";
			}
			rs.close();
			pstmt.close();
	        conn.close();
			rs = null;
			pstmt = null;
			conn = null;

	        return strErMsg;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.svrFileMake() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.svrFileMake() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.svrFileMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.svrFileMake() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.svrFileMake() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of svrFileMake() method statement

	public int execShell(String shFile,String parmName) throws Exception {
		SystemPath		   cTempGet	  	= new SystemPath();
		String			   tmpPath 		= "";
		String			   strBinPath 	= "";
		File 			   shfile		= null;
		String  		   shFileName 	= "";
		OutputStreamWriter writer 		= null;
		String[] 		   strAry 		= null;
		Runtime  		   run 			= null;
		Process 		   p 			= null;

		try {
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");



			shFileName = tmpPath + "/" + shFile;
			shfile = new File(shFileName);

			if( !(shfile.isFile()) )              //File이 없으면
			{
				shfile.createNewFile();          //File 생성
			}

			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write("./ecams_batexec \"cd " + strBinPath+ ";"+parmName +" >err.log\"\n");
			writer.write("exit $?\n");
			writer.close();

			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;

			run = Runtime.getRuntime();

			p = run.exec(strAry);
			p.waitFor();

			run = Runtime.getRuntime();

			strAry = new String[2];

			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;

			p = run.exec(strAry);
			p.waitFor();

			if (p.exitValue() != 0) {
				//shfile.delete();
			}else{
				shfile.delete();
			}

			return p.exitValue();

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0200.execShell() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0200.execShell() Exception END ##");
			throw exception;
		}finally{
			//fileStr = fileStream.toString("EUC-KR");
		}

	}//execShell

	public Object[] getProgFileList(String AcptNo,String fileGb) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		//String           strDevHome   = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			
			SystemPath cTempGet = new SystemPath();
			String tmpPath = cTempGet.getTmpDir_conn("99",conn);
			String reqPath = cTempGet.getTmpDir_conn("77",conn);
			
			String ckFile = "";
			String ckinFile = "";
			File upfile=null;
			File upfile2 = null;
			
			reqPath = reqPath + "/" + AcptNo;
			
            strQuery.setLength(0);
			strQuery.append("update cmr1010 set cr_putcode=''                   \n");
			strQuery.append(" where cr_acptno=?                                 \n");
			strQuery.append("   and cr_serno in (select a.cr_serno              \n");
			strQuery.append("                      from cmr1010 a,cmm0036 b     \n");
			strQuery.append("                     where a.cr_acptno=?           \n");
			strQuery.append("                       and a.cr_syscd=b.cm_syscd   \n");
			strQuery.append("                       and a.cr_rsrccd=b.cm_rsrccd \n");
			strQuery.append("                       and substr(b.cm_info,45,1)='1' \n");
			strQuery.append("                       and substr(b.cm_info,24,1)='1') \n");
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, AcptNo);
            pstmt.setString(2, AcptNo);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            pstmt.executeUpdate();
            pstmt.close();
            
            
			strQuery.setLength(0);
			strQuery.append("select A.CR_SERNO,a.cr_rsrcname,a.cr_dsncd2, \n");
			strQuery.append("   replace(a.cr_dsncd2,'\\','\\\\') as dir2, \n");
			strQuery.append("   c.cm_info,nvl(a.cr_chgcd,'0') cr_chgcd,   \n");
			strQuery.append("   a.cr_editor,a.cr_itemid                   \n");
			strQuery.append("  from cmr1010 A,CMM0036 C                   \n");
			strQuery.append(" Where A.cr_acptno=?  		                  \n");
			strQuery.append("   and nvl(a.cr_putcode,'ERR1')<>'0000'      \n");
			strQuery.append("   AND a.cr_syscd=c.cm_syscd 	              \n");
			strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd	              \n");
			strQuery.append("   and a.cr_rsrccd=c.cm_rsrccd	              \n");
			//neo. 처리팩터 로컬에서개발[45] 체크되어 있는것만 조회
			strQuery.append("   and substr(c.cm_info,45,1)='1'            \n");
			if (fileGb.equals("G")) {
			    strQuery.append("      and substr(c.cm_info,3,1)='0'      \n");
			    strQuery.append("      and nvl(a.cr_chgcd,'0')<>'7'       \n");
			}
            pstmt = conn.prepareStatement(strQuery.toString());
            //pstmt = new LoggableStatement(conn,strQuery.toString());
            pstmt.setString(1, AcptNo);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rsval.clear();
			while (rs.next()){
				if (AcptNo.substring(4,6).equals("07") && rs.getString("cm_info").substring(52,53).equals("1")) {
					ckFile = tmpPath + "/" + rs.getString("cr_itemid")+ "." + rs.getString("cr_editor") + ".checkin";
					
					upfile = new File(ckFile);
					if (upfile.isFile()) {
						upfile = new File(reqPath);
						upfile.mkdirs();
						
						upfile = new File(ckFile);
						ckinFile = reqPath + "/" + rs.getString("cr_rsrcname") + "." + AcptNo + "." + rs.getString("cr_serno");
						upfile2 = new File(ckinFile);
						upfile.renameTo(upfile2);
						continue;
					} else {
						ckinFile = reqPath + "/" + rs.getString("cr_rsrcname") + "." + AcptNo + "." + rs.getString("cr_serno");
						upfile2 = new File(ckinFile);
						
						if (upfile2.isFile()) continue;
					}
				}
				rst = new HashMap<String, String>();
				rst.put("cr_serno", rs.getString("CR_SERNO"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cr_acptno", AcptNo);
				rst.put("cm_dirpath", rs.getString("cr_dsncd2"));
				rst.put("pcdir", rs.getString("dir2"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cr_chgcd", rs.getString("cr_chgcd"));
				if (fileGb.equals("G")) {
					rst.put("backup", "");
					if (!rs.getString("cr_chgcd").equals("M")) {						
						if (rs.getString("cm_info").substring(29,30).equals("1")) {
							rst.put("backup", rs.getString("dir2")+"\\\\"+rs.getString("cr_rsrcname")+"."+AcptNo);
						}
					} 
				}
				rst.put("sendflag","0");
				rst.put("errflag","0");
				//ecamsLogger.error("+++ rst ==="+rst.toString());
				rsval.add(rst);
				rst = null;
				if (fileGb.equals("G")) {
					if (rs.getString("cr_chgcd").equals("M")) {
						rst = new HashMap<String, String>();
						rst.put("cr_serno", rs.getString("CR_SERNO"));
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("cr_acptno", AcptNo);
						rst.put("cm_dirpath", rs.getString("cr_dsncd2"));
						rst.put("pcdir", rs.getString("dir2"));
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("backup", rs.getString("dir2")+"\\\\"+rs.getString("cr_rsrcname")+".mine");
						rst.put("cr_chgcd", "0");
						rst.put("sendflag","0");
						rst.put("errflag","0");
						//ecamsLogger.error("+++ rst ==="+rst.toString());
						rsval.add(rst);
						rst = null;
					}
				}
			}//end of while-loop statement

			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			if (rsval.size() == 0 ) {
				boolean ret = callCmr9900_Str(AcptNo);

				if (ret == false) {
					rst = new HashMap<String, String>();
					rst.put("cr_rsrcname", "ERROR");
					rsval.add(rst);
					rst = null;
				}
			}

			return rsval.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getProgFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.getProgFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.getProgFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.getProgFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr0100.getProgFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of getProgFileList() method statement

	public Boolean setTranResult(String acptNo,String serNo,String ret) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String 			  errCode = "";
		int				  nerrCode  = 0;
		try {
			conn = connectionContext.getConnection();

			try {
				nerrCode = Integer.parseInt(ret);
				if (nerrCode == -8){
					errCode = "SVER";
				}
				else if (nerrCode == -7){
					errCode = "EROR";
				}
				else{
					errCode = String.format("%04d", nerrCode);
				}
			} catch (NumberFormatException e) {
		        errCode = "SRER";
		    }

			strQuery.setLength(0);
			strQuery.append("update cmr1010 set cr_putcode = ? \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("and cr_serno= ? \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

			pstmt.setString(1, errCode);
			pstmt.setString(2, acptNo);
			pstmt.setInt(3, Integer.parseInt(serNo));

			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            pstmt.executeUpdate();
            pstmt.close();

            conn.close();
            conn = null;
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.setTranResult() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.setTranResult() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.setTranResult() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.setTranResult() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.setTranResult() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

		return true;
	}//end of setTranResult() method statement
	
	public Boolean setTranResult_dummy(String acptNo,String serNo,String ret) throws Exception {
		
		try {
			
			ecamsLogger.error("############  acptNo:"+acptNo);
			
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.setTranResult_dummy() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.setTranResult_dummy() Exception END ##");
			throw exception;
		}finally{
		}
		return true;
	}//end of setTranResult_dummy() method statement
	
	
	public Boolean callCmr9900_Str(String acptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		int				  errcnt = 0;
		String			  szJobCD = "";

		try {
			
			conn = connectionContext.getConnection();
			
			strQuery.setLength(0);
			strQuery.append("select count(*) errcnt          \n");
			strQuery.append("  from cmr1010 a,cmm0036 b      \n");
			strQuery.append(" where a.cr_acptno= ?           \n");
			strQuery.append("  and a.cr_status <> '3'        \n");
			strQuery.append("  and a.cr_syscd=b.cm_syscd     \n");
			strQuery.append("  and a.cr_rsrccd=b.cm_rsrccd   \n");
			//neo. 로컬파일만 조회 되도록 [45] 체크
			strQuery.append("  and substr(b.cm_info,45,1)='1'\n");
			//체크아웃 또는 이전버전체크아웃 일때는 처리팩터에서 체크아웃(무)는 쿼리에서 제외한다.
			if ( acptNo.substring(4, 6).equals("01") || acptNo.substring(4, 6).equals("02") ) {
				strQuery.append(" and substr(b.cm_info,3,1)='0' \n");
				//로컬로파일 다운로드 안하는거 제외
				strQuery.append(" and a.cr_chgcd <> '7' \n");
			} else {
				strQuery.append(" and substr(b.cm_info,53,1)='0' \n");
			}
			
			strQuery.append("  and nvl(a.cr_putcode,'ERR1') != '0000' \n");
			//pstmt = conn.prepareStatement(strQuery.toString());
			pstmt = new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, acptNo);
			ecamsLogger.error( ((LoggableStatement)pstmt).getQueryString() );
			rs = pstmt.executeQuery();
			errcnt = -1;
			if (rs.next()){
				errcnt = rs.getInt("errcnt");
			}

			if (errcnt != 0){
				throw new Exception("정상적으로 송수신하지  못한 파일이 있으니 목록에서 확인, 원인 조치 후 다시처리 하십시오.");
			}

			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select cr_team from cmr9900 \n");
			strQuery.append("where cr_acptno= ? \n");
			strQuery.append("  and cr_locat = '00' \n");
			strQuery.append("  and cr_status = '0' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, acptNo);
			rs = pstmt.executeQuery();
			szJobCD = "";
			if (rs.next()){
				szJobCD = rs.getString("cr_team");
			} else {
				throw new Exception("결재정보 오류입니다.[callCmr9900_Str]. 관리자에게 문의해 주시기 바랍니다.");
			}
			rs.close();
			pstmt.close();

        	strQuery.setLength(0);
        	strQuery.append("Begin CMR9900_STR ( ");
        	strQuery.append("?, ?, 'eCAMS자동처리', '9', '', '1' ); End;");
        	pstmt = conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1, acptNo);
        	pstmt.setString(2, szJobCD);
        	pstmt.executeUpdate();
        	pstmt.close();

        	conn.close();
        	rs = null;
        	pstmt = null;
        	conn = null;
        	
        	return true;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.callCmr9900_Str() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.callCmr9900_Str() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.callCmr9900_Str() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.callCmr9900_Str() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.callCmr9900_Str() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of callCmr9900_Str() method statement


	/** 형상관리 vs 개발영역  소스비교 체크
	 * @param fileList : 대상리스트
	 * @param userid : 사용자ID
	 * @return 대상리스트
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] File_Compare(ArrayList<HashMap<String,String>> fileList, String userid) throws SQLException, Exception
	{
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		Runtime                  run         = null;
		Process                  p           = null;
		HashMap<String, String>	 rst         = null;
		ArrayList<HashMap<String, String>>	 rtList = new ArrayList<HashMap<String, String>>();
		OutputStreamWriter       writer = null;

		try {

			conn = connectionContext.getConnection();

			String svrip = "";
			String portno = "";

			strQuery.setLength(0);
			strQuery.append("select cm_svrip, cm_portno \n");
			strQuery.append("  from cmm0031 \n");
			strQuery.append(" where cm_syscd= ? \n");
			strQuery.append("   and cm_svrcd= '01' \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, fileList.get(0).get("cr_syscd"));
			rs = pstmt.executeQuery();
			if (rs.next()){
				svrip = rs.getString("cm_svrip");
				portno = rs.getString("cm_portno");
			}
			rs.close();
			pstmt.close();
			if (svrip == "" || portno == ""){
				conn.close();
				throw new Exception("개발서버 정보가 없습니다.[ERROR]:ip["+svrip+"] port["+portno+"]");
			}

			SystemPath systemPath = new SystemPath();
			String binpath = systemPath.getTmpDir_conn("14",conn);
			String shFileName = systemPath.getTmpDir_conn("99",conn) + "/File_Compare_chk.sh";
			String exFilechk = systemPath.getTmpDir_conn("99",conn) + "/File_exists_chk.sh";
			systemPath = null;
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			String[] chkAry;
			File shfile = new File(shFileName);
			if( !shfile.isFile() ) shfile.createNewFile();//File이 없으면 File 생성

			File exfile = new File(exFilechk);
			if ( !exfile.isFile() ) exfile.createNewFile();

			for(int i=0 ; i<fileList.size() ; i++){
				rst = new HashMap<String,String>();
				rst = fileList.get(i);

				writer = new OutputStreamWriter( new FileOutputStream(exFilechk));
				writer.write("cd "+binpath +"\n");
				writer.write("./zen " + svrip + " " + portno + " 0 I " + fileList.get(i).get("view_dirpath") + "/" + fileList.get(i).get("cr_rsrcname") + "\n");
				writer.write("exit $?\n");
				writer.close();

				chkAry = new String[3];
				chkAry[0] = "chmod";
				chkAry[1] = "777";
				chkAry[2] = exFilechk;
				run = Runtime.getRuntime();
				p = run.exec(chkAry);
				chkAry = null;
				p.waitFor();

				run = Runtime.getRuntime();
				chkAry = new String[2];
				chkAry[0] = "/bin/sh";
				chkAry[1] = exFilechk;
				p = run.exec(chkAry);
				chkAry = null;
				p.waitFor();

				if (p.exitValue() != 0) {
					rst.put("result", "개발소스없음");
				} else if(fileList.get(i).get("cm_info").substring(44,45).equals("0") &&
					fileList.get(i).get("cm_info").substring(2,3).equals("0") &&
					fileList.get(i).get("cm_info").substring(15,16).equals("1")){

					run = null;
					p = null;
					//[45] 로컬에서개발 0 이고, [03] 체크아웃(무) 0 이며, [16] 개발영역과소스비교 1 일때

					writer = new OutputStreamWriter( new FileOutputStream(shFileName));
					writer.write("cd "+binpath +"\n");
					writer.write("./ecams_diffck " + fileList.get(i).get("cr_itemid") + " " + userid +" DEV" + "\n");
					writer.write("exit $?\n");
					writer.close();
					writer = null;

					chkAry = new String[3];
					chkAry[0] = "chmod";
					chkAry[1] = "777";
					chkAry[2] = shFileName;
					run = Runtime.getRuntime();
					p = run.exec(chkAry);
					chkAry = null;
					p.waitFor();

					run = Runtime.getRuntime();
					chkAry = new String[2];
					chkAry[0] = "/bin/sh";
					chkAry[1] = shFileName;
					p = run.exec(chkAry);
					chkAry = null;
					p.waitFor();

					if (p.exitValue() == 0) {
						rst.put("result", "소스일치");
					} else if (p.exitValue() == 127) {
						rst.put("result", "비교실패");
					} else if (p.exitValue() == 3) {
						rst.put("result", "개발서버 정보오류");
					} else{
						rst.put("result", "소스불일치");
					}
					p = null;
					/*chkAry = new String[6];
					chkAry[0] = "/bin/sh";
					chkAry[1] = binpath;
					chkAry[2] = "ecams_diffck";
					chkAry[3] = fileList.get(i).get("cr_itemid");
					chkAry[4] = userid;
					chkAry[5] = "TEST";

					run = Runtime.getRuntime();
					p = run.exec(chkAry);
					p.waitFor();

					if (p.exitValue() == 0) {
						rst.put("result", "정상");
					}else{
						rst.put("result", "Test System과 내용이다릅니다.");
					}
					*/
				}else if (fileList.get(i).get("cm_info").substring(2,3).equals("1")){
					rst.put("result", "체크아웃안함");
				}else {
					rst.put("result", "비교대상아님");
				}
				rtList.add(rst);
				rst = null;
				writer = null;
				run = null;
				p = null;
			}
			if (shfile != null) shfile.delete();
			if (exfile != null) exfile.delete();

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			if (p != null) p = null;
			if (run != null) run = null;
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.File_Compare() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.File_Compare() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			if (p != null) p = null;
			if (run != null) run = null;
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.File_Compare() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.File_Compare() Exception END ##");
			throw exception;
		} finally {
			if (p != null) p = null;
			if (run != null) run = null;
			if (fileList != null) fileList = null;
			if (rtList != null) rtList = null;
			if (rst != null) rst = null;
			if (rs != null) rs = null;
			if (pstmt != null) pstmt = null;
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.File_Compare() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of File_Compare() method statement


	/** 형상관리 vs 개발영역(서버 또는 PC) 소스비교 체크
	 * @param fileList : 대상리스트
	 * @param userid : 사용자ID
	 * @return 대상리스트
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] checkin_File_Compare(ArrayList<HashMap<String,String>> fileList, String userid) throws SQLException, Exception
	{
		Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();

		Runtime                  run         = null;
		Process                  p           = null;
		HashMap<String, String>	 rst         = null;
		ArrayList<HashMap<String, String>>	 rtList = new ArrayList<HashMap<String, String>>();
		OutputStreamWriter       writer = null;

		try {

			conn = connectionContext.getConnection();

			SystemPath systemPath = new SystemPath();
			String binpath = systemPath.getTmpDir_conn("14",conn);
			String shFileName = systemPath.getTmpDir_conn("99",conn) + "/File_Compare_chk.sh";
			systemPath = null;
			conn.close();
			conn = null;

			String[] chkAry;
			File shfile = new File(shFileName);
			if( !shfile.isFile() ) shfile.createNewFile();//File이 없으면 File 생성

			for(int i=0 ; i<fileList.size() ; i++)
			{
				rst = new HashMap<String,String>();
				rst = fileList.get(i);

				if (fileList.get(i).get("cr_lstver").equals("0")){
					rst.put("result", "비교대상제외[신규]");
				}else if(fileList.get(i).get("cm_info").substring(2,3).equals("0") &&
					fileList.get(i).get("cm_info").substring(9,10).equals("0")){

					run = null;
					p = null;
					//[03] 체크아웃(무) 0 이며, [10]	바이너리 0 일때

					writer = new OutputStreamWriter( new FileOutputStream(shFileName));
					writer.write("cd "+binpath +"\n");
					if(fileList.get(i).get("cm_info").substring(44,45).equals("1")){
						writer.write("./ecams_diffck " + fileList.get(i).get("cr_itemid") + " " + userid +" LOCAL" + "\n");
					}else{
						writer.write("./ecams_diffck " + fileList.get(i).get("cr_itemid") + " " + userid +" DEV" + "\n");
					}
					writer.write("exit $?\n");
					writer.close();
					writer = null;

					chkAry = new String[3];
					chkAry[0] = "chmod";
					chkAry[1] = "777";
					chkAry[2] = shFileName;
					run = Runtime.getRuntime();
					p = run.exec(chkAry);
					chkAry = null;
					p.waitFor();

					run = Runtime.getRuntime();
					chkAry = new String[2];
					chkAry[0] = "/bin/sh";
					chkAry[1] = shFileName;
					p = run.exec(chkAry);
					chkAry = null;
					p.waitFor();

					if (p.exitValue() == 0) {
						rst.put("result", "소스일치");
					} else if (p.exitValue() == 127) {
						rst.put("result", "비교실패");
					} else if (p.exitValue() == 3) {
						rst.put("result", "개발서버 정보오류");
					} else{
						rst.put("result", "소스불일치");
					}
					p = null;
					/*chkAry = new String[6];
					chkAry[0] = "/bin/sh";
					chkAry[1] = binpath;
					chkAry[2] = "ecams_diffck";
					chkAry[3] = fileList.get(i).get("cr_itemid");
					chkAry[4] = userid;
					chkAry[5] = "TEST";

					run = Runtime.getRuntime();
					p = run.exec(chkAry);
					p.waitFor();

					if (p.exitValue() == 0) {
						rst.put("result", "정상");
					}else{
						rst.put("result", "Test System과 내용이다릅니다.");
					}
					*/
				}else if (fileList.get(i).get("cm_info").substring(2,3).equals("1")){
					rst.put("result", "비교대상제외[비체크아웃대상]");
				}else if (fileList.get(i).get("cm_info").substring(9,10).equals("1")){
					rst.put("result", "비교대상제외[바이너리]");
				}else {
					rst.put("result", "비교대상제외");
				}
				rtList.add(rst);
				rst = null;
				writer = null;
				run = null;
				p = null;
			}
			//if (shfile != null) shfile.delete();

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			if (p != null) p = null;
			if (run != null) run = null;
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.checkin_File_Compare() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.checkin_File_Compare() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			if (p != null) p = null;
			if (run != null) run = null;
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.checkin_File_Compare() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.checkin_File_Compare() Exception END ##");
			throw exception;
		} finally {
			if (p != null) p = null;
			if (run != null) run = null;
			if (fileList != null) fileList = null;
			if (rtList != null) rtList = null;
			if (rst != null) rst = null;
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.checkin_File_Compare() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of checkin_File_Compare() method statement

	public Object[] diffList(String UserId,ArrayList<HashMap<String,String>> fileList) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int i = 0;
		boolean sameSw = false;
		String  shFileName = "";
		String	fileName = "";
		String	outName = "";
		String  strParm = "";
		String  strMd5sum = "";

		File shfile=null;
		BufferedReader in1 = null;
		String	readline1 = "";
		int     ret = 0;
		try {
			conn = connectionContext.getConnection();
			SystemPath cTempGet = new SystemPath();
			String tmpPath = cTempGet.getTmpDir_conn("99",conn);
			rtList.clear();
			for (i=0;fileList.size()>i;i++) {
				fileName = tmpPath + "/" + fileList.get(i).get("cr_itemid") +"."+UserId;
				shfile = new File(fileName);

				sameSw = true;
				if(shfile.isFile()) {              //File존재여부
					outName = fileName + ".md5";
					Cmr0200 cmr0200 = new Cmr0200();
					strParm = "./ecams_md5sum " + fileName + " >"+outName;
					shFileName = UserId+"_"+ fileList.get(i).get("cr_itemid") + "apcmd.sh";
					ret = cmr0200.execShell(shFileName, strParm, false);
					if (ret != 0) {
						throw new Exception("MD5SUM추출실패. run=["+strParm +"]" + " return=[" + ret + "]" );
					}
					in1 = new BufferedReader(new InputStreamReader(new FileInputStream(outName), "MS949"));
	
					while( (readline1 = in1.readLine()) != null ){
						strMd5sum = readline1;
					}
					in1.close();
	
					in1 = null;
//					strQuery.setLength(0);
//					strQuery.append("select b.cr_md5sum                              \n");
//					strQuery.append("  from cmr1000 a,cmr1010 b                      \n");
//					strQuery.append(" where a.cr_syscd=? and a.cr_qrycd='07'         \n");
//					strQuery.append("   and a.cr_status<>'3'                         \n");
//					strQuery.append("   and a.cr_prcdate is not null                 \n");
//					strQuery.append("   and a.cr_acptno=b.cr_acptno                  \n");
//					strQuery.append("   and b.cr_itemid=?                            \n");
//					strQuery.append("   and b.cr_qrycd in ('03','04')                \n");
//					strQuery.append("   and b.cr_status<>'3'                         \n");
//					strQuery.append("   and b.cr_prcdate is not null                 \n");
//					strQuery.append(" order by a.cr_prcdate desc                     \n");
//					//pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn, strQuery.toString());		            
//					pstmt.setString(1, fileList.get(i).get("cr_syscd"));	            
//					pstmt.setString(2, fileList.get(i).get("cr_itemid"));
					strQuery.setLength(0);
					strQuery.append("select cr_md5sum from cmr0020 where cr_itemid = ? \n");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, fileList.get(i).get("cr_itemid"));
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if ( rs.getString("cr_md5sum") == null || rs.getString("cr_md5sum") == "" ){
							throw new Exception("파일비교 실패. ERROR:MD5SUM 데이터가 없습니다.\nRepository 확인 필요.\n대상파일명=["+fileList.get(i).get("cr_rsrcname")+"]  ID=["+fileList.get(i).get("cr_itemid")+"]");
						} else if (!rs.getString("cr_md5sum").equals(strMd5sum)) {
							sameSw = false;
						}
					} else {
						throw new Exception("파일비교 실패. ERROR:프로그램에 대한 정보가 없습니다.\n대상파일명=["+fileList.get(i).get("cr_rsrcname")+"]  ID=["+fileList.get(i).get("cr_itemid")+"]");
					}
					rs.close();
					pstmt.close();
				}
				if ( !sameSw ) {
					rst = new HashMap<String,String>();
					rst = fileList.get(i);
					rst.put("selected", "0");
					rtList.add(rst);
					rst = null;
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			//ecamsLogger.error("+++ rtList +++"+rtList.toString());
			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr0100.diffList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr0100.diffList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr0100.diffList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr0100.diffList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)  rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr0100.diffList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
