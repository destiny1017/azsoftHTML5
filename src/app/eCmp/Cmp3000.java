package app.eCmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;
import app.common.UserInfo;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmp3000 {

	Logger ecamsLogger = EcamsLogger.getLoggerInstance();

	public Object[] get_rsrcInfo(String syscd) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		ArrayList<HashMap<String, String>> rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst = null;
		Object[] rtObj = null;

		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append(" SELECT c.cm_micode,c.cm_codename from cmm0020 c,cmm0036 a where   \n");
			strQuery.append("a.cm_syscd= ?  and    \n");
			strQuery.append("c.cm_macode='JAWON' and   \n");
			strQuery.append("a.cm_rsrccd=c.cm_micode    \n");
			strQuery.append("order by c.cm_micode,c.cm_codename    \n");
			// pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, syscd);
			// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rsval.clear();
			if(syscd != null){
			rst = new HashMap<String, String>();
			rst.put("cm_micode", "0000");
			rst.put("cm_codename", "전체");
			rsval.add(rst);
			rst = null;
			}
			while (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_UserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3000.get_UserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_UserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3000.get_UserInfo() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rtObj != null)
				rtObj = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## Cmp3000.get_UserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] get_Result(String strSys)throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		ArrayList<HashMap<String, String>> rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst = null;
		Object[] rtObj = null;
		int cnt = 0;
		String			  ColorSw     = "";
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			strQuery.append("SELECT A.CD_SYSCD , A.CD_DSNCD ,A.CD_JOBCD ,A.CD_RSRCCD ,A.CD_RSRCNAME ,A.CD_CREATOR , \n");
			strQuery.append("A.CD_EDITOR,A.CD_CLSEDITOR ,A.CD_REQDOC ,TO_CHAR(A.CD_OPENDATE, 'YYYY-MM-DD') CD_OPENDATE,TO_CHAR(A.CD_CLSDATE, 'YYYY-MM-DD') CD_CLSDATE ,TO_CHAR(A.CD_LASTDATE, 'YYYY-MM-DD') CD_LASTDATE ,A.CD_GUBUN, \n");
			strQuery.append("A.CD_SAYUCD ,I.cm_codename as DIFFRE ,B.CM_USERNAME AS EDITOR,C.CM_USERNAME AS CLSEDITOR,D.CM_JOBNAME,  \n");
			strQuery.append("E.CM_CODENAME,F.CM_SYSMSG,G.CM_DIRPATH,H.CM_CODENAME AS SCMGUBUN  \n");
			strQuery.append("FROM CMD0028 A ,CMM0040 B ,CMM0040 C ,CMM0102 D ,CMM0020 E ,CMM0030 F ,CMM0070 G ,CMM0020 H ,CMM0020 I  \n");
			strQuery.append("WHERE A.CD_EDITOR = B.CM_USERID  AND   \n");
			if (!strSys.equals("00000"))
				strQuery.append("  a.cd_syscd= ?     and   \n");
			strQuery.append("A.CD_CLSEDITOR = C.CM_USERID(+)  \n");
			strQuery.append("AND A.CD_SYSCD = F.CM_SYSCD   \n");
			strQuery.append("AND A.CD_JOBCD = D.CM_JOBCD   \n");
			strQuery.append("AND E.CM_MACODE = 'JAWON'   \n");
			strQuery.append("AND A.CD_RSRCCD = E.CM_MICODE   \n");
			strQuery.append("AND I.CM_MACODE = 'DIFFREJECT'   \n");
			strQuery.append("AND A.CD_SAYUCD = I.CM_MICODE \n");
			strQuery.append("AND A.CD_GUBUN = '1' \n");
			strQuery.append("AND A.CD_SYSCD = G.CM_SYSCD \n");
			strQuery.append("AND A.CD_DSNCD = G.CM_DSNCD \n");
			strQuery.append("AND H.CM_MACODE = 'CMD0028' \n");
			strQuery.append("AND A.CD_GUBUN = H.CM_MICODE \n");
			strQuery.append("UNION ALL \n");
			strQuery.append("SELECT A.CD_SYSCD,A.CD_DSNCD,A.CD_JOBCD,A.CD_RSRCCD,A.CD_RSRCNAME,A.CD_CREATOR,A.CD_EDITOR,A.CD_CLSEDITOR, \n");
			strQuery.append("A.CD_REQDOC,TO_CHAR(A.CD_OPENDATE, 'YYYY-MM-DD') CD_OPENDATE,TO_CHAR(A.CD_CLSDATE, 'YYYY-MM-DD') CD_CLSDATE ,TO_CHAR(A.CD_LASTDATE, 'YYYY-MM-DD') CD_LASTDATE ,A.CD_GUBUN,A.CD_SAYUCD,I.cm_codename as DIFFRE, \n");
			strQuery.append("B.CM_USERNAME AS EDITOR,C.CM_USERNAME AS CLSEDITOR,D.CM_JOBNAME,E.CM_CODENAME,F.CM_SYSMSG, \n");
			strQuery.append("  A.CD_DSNCD AS CM_DIRPATH, H.CM_CODENAME AS SCMGUBUN \n");
			strQuery.append("FROM CMD0028 A,CMM0040 B,CMM0040 C,CMM0102 D,CMM0020 E,CMM0030 F,CMM0020 H,CMM0020 I   \n");
			strQuery.append("WHERE A.CD_EDITOR = B.CM_USERID AND \n");
			if (!strSys.equals("00000"))
				strQuery.append("  a.cd_syscd= ?     and   \n");
			strQuery.append("A.CD_CLSEDITOR = C.CM_USERID(+) \n");
			strQuery.append("AND A.CD_SYSCD = F.CM_SYSCD \n");
			strQuery.append("AND A.CD_JOBCD = D.CM_JOBCD \n");
			strQuery.append("AND E.CM_MACODE = 'JAWON' \n");
			strQuery.append("AND A.CD_RSRCCD = E.CM_MICODE \n");
			strQuery.append("AND I.CM_MACODE = 'DIFFREJECT' \n");
			strQuery.append("AND A.CD_SAYUCD = I.CM_MICODE \n");
			strQuery.append("AND A.CD_GUBUN = '2' \n");
			strQuery.append("AND H.CM_MACODE = 'CMD0028' \n");
			strQuery.append("AND A.CD_GUBUN = H.CM_MICODE \n");
			strQuery.append("ORDER BY CM_DIRPATH, CD_RSRCNAME \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			// pstmt = new LoggableStatement(conn,strQuery.toString());
			if (!strSys.equals("00000")) {
				pstmt.setString(++cnt, strSys);
			}
			if (!strSys.equals("00000")) {
				pstmt.setString(++cnt, strSys);
			}
			// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rsval.clear();
			while (rs.next()) {
				ColorSw = "0";
				rst = new HashMap<String, String>();
				rst.put("cd_syscd", rs.getString("cd_syscd"));
				rst.put("cd_dsncd", rs.getString("cd_dsncd"));
				rst.put("cd_jobcd", rs.getString("cd_jobcd"));
				rst.put("cd_rsrccd", rs.getString("cd_rsrccd"));
				rst.put("cd_rsrcname", rs.getString("cd_rsrcname"));
				rst.put("cd_creator", rs.getString("cd_creator"));
				rst.put("cd_editor", rs.getString("cd_editor"));
				rst.put("cd_clseditor", rs.getString("cd_clseditor"));
				rst.put("cd_reqdoc", rs.getString("cd_reqdoc"));
				rst.put("cd_opendate", rs.getString("cd_opendate"));
				rst.put("cd_clsdate", rs.getString("cd_clsdate"));
				if(rs.getString("cd_clsdate")!= null){
					ColorSw = "3";
				}
				rst.put("colorsw", ColorSw);
				rst.put("cd_lastdate", rs.getString("cd_lastdate"));
				rst.put("cd_gubun", rs.getString("cd_gubun"));
				rst.put("cd_sayucd", rs.getString("cd_sayucd"));
				rst.put("diffre", rs.getString("diffre"));
				rst.put("editor", rs.getString("editor"));
				rst.put("clseditor", rs.getString("clseditor"));
				rst.put("cm_jobname", rs.getString("cm_jobname"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("scmgubun", rs.getString("scmgubun"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_get_Result() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3000.get_Result() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_Result() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3000.get_Result() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rtObj != null)
				rtObj = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## Cmp3000.get_Result() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}// end of get_Result() method statement

	public String InsertData(String gubun,String syscd,String jobcd,String rsrccd,String dirpath,String sayu,String pgmname,String txtsayu,String strUserId)
			throws SQLException,Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		String chk="";
		String cnt="";
		ConnectionContext connectionContext = new ConnectionResource();


		
		try {
			conn = connectionContext.getConnection();
			
			if(gubun.equals("1")){
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr0020   \n");
				strQuery.append("WHERE CR_SYSCD = ?                \n");
				strQuery.append("AND CR_DSNCD    = ?              \n");
				strQuery.append("AND CR_RSRCNAME = ?              \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, syscd);
				pstmt.setString(2, dirpath);
				pstmt.setString(3, pgmname);
				//ecamsLogger.error(((LoggableStatement) pstmt).getQueryString());
				rs = pstmt.executeQuery();
				while(rs.next()){
				cnt=rs.getString("cnt");
				}	
				if(cnt.equals("0")){	
					chk = "1";

					conn.close();
					conn = null;
					
					return chk;   //등록되지않은 프로그램
				}else{
					cnt = "0";
					strQuery.setLength(0);
					strQuery.append("select count(*) as cnt from cmd0028   \n");
					strQuery.append("WHERE Cd_SYSCD = ?                \n");
					strQuery.append("AND Cd_DSNCD    = ?              \n");
					strQuery.append("AND Cd_RSRCNAME = ?              \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					// pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, syscd);
					pstmt.setString(2, dirpath);
					pstmt.setString(3, pgmname);
					// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						cnt = rs.getString("cnt");
					}
					rs = null;
					if(Integer.parseInt(cnt) == 0){					
						strQuery.setLength(0);
						strQuery.append("INSERT INTO CMD0028(CD_GUBUN,CD_SYSCD,CD_DSNCD,CD_RSRCCD,CD_JOBCD,CD_RSRCNAME,CD_CREATOR,CD_EDITOR,CD_REQDOC, CD_OPENDATE,CD_LASTDATE, CD_OPENDAY, CD_SAYUCD)  \n" );
						strQuery.append("VALUES(?,?,?,?,?,?,?,?,?,sysdate,sysdate,to_char(SYSDATE,'yyyymmdd'),?)  \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						// pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(1, gubun);
						pstmt.setString(2, syscd);
						pstmt.setString(3, dirpath);
						pstmt.setString(4, rsrccd);
						pstmt.setString(5, jobcd);
						pstmt.setString(6, pgmname);
						pstmt.setString(7, strUserId);
						pstmt.setString(8, strUserId);
						pstmt.setString(9, txtsayu);
						pstmt.setString(10, sayu);
						//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						rs = pstmt.executeQuery();
						chk="2";//형상관리 미적용 추가
					}else{
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						strQuery.setLength(0);
						strQuery.append("UPDATE  CMD0028 ");
						strQuery.append("SET  CD_CLSDATE  = '',CD_CLSEDITOR = '',CD_LASTDATE = SYSDATE ");
						strQuery.append(", CD_REQDOC   = ? " );
						strQuery.append(", CD_EDITOR   = ? " );
						strQuery.append(", CD_RSRCCD   = ? " );
						strQuery.append(", CD_JOBCD    = ? " );
						strQuery.append(", CD_SAYUCD   = ? " );
						strQuery.append("WHERE CD_SYSCD    = ?" );
						strQuery.append("  AND CD_DSNCD    = ?" );
						strQuery.append("  AND CD_RSRCNAME = ?" );
						pstmt = conn.prepareStatement(strQuery.toString());	
						//pstmt = new LoggableStatement(conn,strQuery.toString());
						pstmt.setString(1, txtsayu);
						pstmt.setString(2, strUserId);
						pstmt.setString(3, rsrccd);
						pstmt.setString(4, jobcd);
						pstmt.setString(5, sayu);
						pstmt.setString(6, syscd);
						pstmt.setString(7, dirpath);
						pstmt.setString(8, pgmname);
						//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
						rs = pstmt.executeQuery();
						chk = "0";
						return chk;   //등록된 프로그램이라 변경
					}
				}
				
			}else{
				cnt = "0";
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmd0028   \n");
				strQuery.append("WHERE Cd_SYSCD = ?                \n");
				strQuery.append("AND Cd_DSNCD    = ?              \n");
				strQuery.append("AND Cd_RSRCNAME = ?              \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				// pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, syscd);
				pstmt.setString(2, dirpath);
				pstmt.setString(3, pgmname);
				// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					cnt = rs.getString("cnt");
				}
				rs = null;
				if (cnt.equals("1")) {
					strQuery.setLength(0);
					strQuery.append("UPDATE  CMD0028 ");
					strQuery.append("SET  CD_CLSDATE  = '',CD_CLSEDITOR = '',CD_LASTDATE = SYSDATE ");
					strQuery.append(", CD_REQDOC   = ? " );
					strQuery.append(", CD_EDITOR   = ? " );
					strQuery.append(", CD_RSRCCD   = ? " );
					strQuery.append(", CD_JOBCD    = ? " );
					strQuery.append(", CD_SAYUCD   = ? " );
					strQuery.append("WHERE CD_SYSCD    = ?" );
					strQuery.append("  AND CD_DSNCD    = ?" );
					strQuery.append("  AND CD_RSRCNAME = ?" );
					pstmt = conn.prepareStatement(strQuery.toString());	
					//pstmt = new LoggableStatement(conn,strQuery.toString());
					pstmt.setString(1, txtsayu);
					pstmt.setString(2, strUserId);
					pstmt.setString(3, rsrccd);
					pstmt.setString(4, jobcd);
					pstmt.setString(5, sayu);
					pstmt.setString(6, syscd);
					pstmt.setString(7, dirpath);
					pstmt.setString(8, pgmname);
					//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					chk = "0";
					return chk;
					 // 등록되지않은 프로그램(형상관리 적용 안된프로그램)
				} else {
				strQuery.setLength(0);
				strQuery.append("INSERT INTO CMD0028(CD_GUBUN,CD_SYSCD,CD_DSNCD,CD_RSRCCD,CD_JOBCD,CD_RSRCNAME,CD_CREATOR,CD_EDITOR,CD_REQDOC, CD_OPENDATE,CD_LASTDATE, CD_OPENDAY, CD_SAYUCD)  \n" );
				strQuery.append("VALUES(?,?,?,?,?,?,?,?,?,sysdate,sysdate,to_char(SYSDATE,'yyyymmdd'),?)  \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				// pstmt = new LoggableStatement(conn,strQuery.toString());
				pstmt.setString(1, gubun);
				pstmt.setString(2, syscd);
				pstmt.setString(3, dirpath);
				pstmt.setString(4, rsrccd);
				pstmt.setString(5, jobcd);
				pstmt.setString(6, pgmname);
				pstmt.setString(7, strUserId);
				pstmt.setString(8, strUserId);
				pstmt.setString(9, txtsayu);
				pstmt.setString(10, sayu);
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				chk="2";//형상관리 미적용 추가
				}
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return chk;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3000.InsertData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3000.InsertData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3000.InsertData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3000.InsertData() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## Cmp3000.InsertData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}// end of InsertData() method statement

	public String DelData(String gubun, String syscd, String dirpath, String pgmname,String strUserId,String txtsayu) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		String chk = "";
		String cnt = "";
		ConnectionContext connectionContext = new ConnectionResource();
		

		try {
			conn = connectionContext.getConnection();

			if (gubun.equals("1")) {
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmr0020   \n");
				strQuery.append("WHERE CR_SYSCD = ?                \n");
				strQuery.append("AND CR_DSNCD    = ?              \n");
				strQuery.append("AND CR_RSRCNAME = ?              \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				// pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, syscd);
				pstmt.setString(2, dirpath);
				pstmt.setString(3, pgmname);
				// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					cnt = rs.getString("cnt");
				}
				if (cnt.equals("0")) {
					chk = "1";
					 // 등록되지않은 프로그램(형상관리 적용된프로그램)
				} else {

					pstmt = new LoggableStatement(conn, strQuery.toString());
					strQuery.setLength(0);
					strQuery.append("UPDATE  CMD0028 ");
					strQuery.append("SET  CD_CLSDATE  = SYSDATE,CD_CLSEDITOR = ? ");
					strQuery.append(", CD_REQDOC   = ? ");
					strQuery.append("WHERE CD_SYSCD    = ?");
					strQuery.append("  AND CD_DSNCD    = ?");
					strQuery.append("  AND CD_RSRCNAME = ?");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, strUserId);
					pstmt.setString(2, txtsayu);
					pstmt.setString(3, syscd);
					pstmt.setString(4, dirpath);
					pstmt.setString(5, pgmname);
					ecamsLogger.error(((LoggableStatement) pstmt).getQueryString());
					rs = pstmt.executeQuery();
					chk = "0";
					 // 등록된 프로그램삭제(형상관리 적용된프로그램)
				}

			} else {
				cnt = "0";
				strQuery.setLength(0);
				strQuery.append("select count(*) as cnt from cmd0028   \n");
				strQuery.append("WHERE Cd_SYSCD = ?                \n");
				strQuery.append("AND Cd_DSNCD    = ?              \n");
				strQuery.append("AND Cd_RSRCNAME = ?              \n");
				pstmt = conn.prepareStatement(strQuery.toString());
				// pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, syscd);
				pstmt.setString(2, dirpath);
				pstmt.setString(3, pgmname);
				// ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					cnt = rs.getString("cnt");
				}
				if (cnt.equals("0")) {
					chk = "3";
					 // 등록되지않은 프로그램(형상관리 적용 안된프로그램)
				} else {

					//pstmt = new LoggableStatement(conn, strQuery.toString());
					strQuery.setLength(0);
					strQuery.append("UPDATE  CMD0028 ");
					strQuery.append("SET  CD_CLSDATE  = SYSDATE,CD_CLSEDITOR = ? ");
					strQuery.append(", CD_REQDOC   = ? ");
					strQuery.append("WHERE CD_SYSCD    = ?");
					strQuery.append("  AND CD_DSNCD    = ?");
					strQuery.append("  AND CD_RSRCNAME = ?");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, strUserId);
					pstmt.setString(2, txtsayu);
					pstmt.setString(3, syscd);
					pstmt.setString(4, dirpath);
					pstmt.setString(5, pgmname);
					//ecamsLogger.error(((LoggableStatement) pstmt).getQueryString());
					rs = pstmt.executeQuery();
					chk = "2";
					// 등록된 프로그램삭제(형상관리 적용안된프로그램)
				}
			}
				
			
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			return chk;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3000.InsertData() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3000.InsertData() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3000.InsertData() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3000.InsertData() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## Cmp3000.InsertData() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}// end of DelData() method statement
	
	public Object[] get_dirInfo(String syscd,String jobcd,String rsrccd, String admin,String strId) throws SQLException, Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strQuery = new StringBuffer();
		ArrayList<HashMap<String, String>> rsval = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> rst = null;
		Object[] rtObj = null;
		int cnt = 0;
		ConnectionContext connectionContext = new ConnectionResource();
		try {
			conn = connectionContext.getConnection();
			if(admin.equals("1")){
				strQuery.append(" select a.cm_dirpath,a.cm_dsncd   \n");
				strQuery.append("from cmm0070 a,cmm0073 b,cmm0072 c   \n");
				strQuery.append("where b.cm_syscd= ? and    \n");
				
				if(!jobcd.equals("0000"))
				strQuery.append("b.cm_jobcd= ? and    \n");
			}else{
				strQuery.append(" select a.cm_dirpath,a.cm_dsncd   \n");
				strQuery.append("from cmm0070 a,cmm0073 b,cmm0072 c,cmm0044 d \n");
				strQuery.append("where d.cm_userid= ? and \n");
				strQuery.append("c.cm_syscd= ? and  \n");
				if(!jobcd.equals("0000"))
				strQuery.append("d.cm_jobcd= ? and  \n");
				strQuery.append("d.cm_syscd=b.cm_syscd and   \n");
				strQuery.append("d.cm_jobcd=b.cm_jobcd and   \n");
			}
			strQuery.append("a.cm_syscd=b.cm_syscd and 	\n");
			strQuery.append("a.cm_dsncd=b.cm_dsncd and 	\n");
			strQuery.append("a.cm_syscd=c.cm_syscd and 	\n");
			strQuery.append("a.cm_dsncd=c.cm_dsncd  	\n");
			if(!rsrccd.equals("0000"))
			strQuery.append("and c.cm_rsrccd= ? \n");
			strQuery.append("order by cm_dirpath   	\n");
					
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt = conn.prepareStatement(strQuery.toString());
			
			if(admin.equals("1")){
				pstmt.setString(++cnt, syscd);
				if(!jobcd.equals("0000"))
				pstmt.setString(++cnt, jobcd);
			}else{
				pstmt.setString(++cnt, strId);
				pstmt.setString(++cnt, syscd);
				if(!jobcd.equals("0000"))
				pstmt.setString(++cnt, jobcd);
			}
			if(!rsrccd.equals("0000"))
			pstmt.setString(++cnt, rsrccd);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
			rs = pstmt.executeQuery();
			rsval.clear();
			
			while (rs.next()) {
				rst = new HashMap<String, String>();
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_dsncd", rs.getString("cm_dsncd"));
				rsval.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();
			conn.close();

			rs = null;
			pstmt = null;
			conn = null;

			rtObj = rsval.toArray();
			rsval.clear();
			rsval = null;

			return rtObj;

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_UserInfo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmp3000.get_UserInfo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmp3000.get_UserInfo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmp3000.get_UserInfo() Exception END ##");
			throw exception;
		} finally {
			if (strQuery != null)
				strQuery = null;
			if (rtObj != null)
				rtObj = null;
			if (rs != null)
				try {
					rs.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception ex2) {
					ex2.printStackTrace();
				}
			if (conn != null) {
				try {
					ConnectionResource.release(conn);
				} catch (Exception ex3) {
					ecamsLogger.error("## Cmp3000.get_UserInfo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}