package app.eCmr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
//import app.common.LoggableStatement;
import org.apache.log4j.Logger;
import app.common.SystemPath;
import app.common.UserInfo;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr5100 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();




	public Object[] getResultList(String acptNo,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String            lstPrcSys   = "";
		try {
			conn = connectionContext.getConnection();

			UserInfo     userinfo = new UserInfo();
			boolean adminYn = userinfo.isAdmin_conn(UserId,conn);

			strQuery.setLength(0);
			//원기록
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_serno,a.cr_editor,b.cm_svrip,       \n");
			strQuery.append("       a.cr_rsrcname,c.cr_svrname,c.cr_prcrst,b.cm_portno,             \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,         \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,                \n");
			strQuery.append("       d.cm_codename as REQUEST, e.cm_codename as SYSGBN,              \n");
			strQuery.append("       g.cm_codename JAWON,f.cr_rsrcname basersrc,                     \n");
			strQuery.append("       a.cr_itemid,a.cr_baseitem,c.cr_rungbn                           \n");
			strQuery.append("  from cmm0031 b,cmm0020 g,cmm0020 e,cmm0020 d,cmr0020 f,              \n");
			strQuery.append("       cmr1011 c,cmr1010 a                                             \n");
			strQuery.append("where a.cr_acptno=?                                                    \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd                    \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                             \n");
			if (adminYn == false) strQuery.append("and c.cr_svrcd<>'90'                             \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and a.cr_serno = c.cr_serno              \n");
			strQuery.append("and (nvl(c.cr_putcode,'NONE')<>'0000' or c.cr_rstfile is not null)     \n");
			strQuery.append("and d.cm_macode =decode(?,'03','CHECKIN','07','CHECKIN','04','CHECKIN','06','CHECKIN','16','CHECKIN','REQUEST') \n");
			strQuery.append("and a.cr_qrycd = d.cm_micode                                           \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode               \n");
			strQuery.append("and g.cm_macode = 'JAWON' and a.cr_rsrccd = g.cm_micode                \n");
			strQuery.append("and a.cr_baseitem=f.cr_itemid                                          \n");
			strQuery.append("UNION                                                                  \n");
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_serno,a.cr_editor,b.cm_svrip,       \n");
			strQuery.append("       a.cr_rsrcname,c.cr_svrname,c.cr_prcrst,b.cm_portno,             \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,         \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,                \n");
			strQuery.append("       d.cm_codename as REQUEST, e.cm_codename as SYSGBN,              \n");
			strQuery.append("       g.cm_codename JAWON,a.cr_rsrcname basersrc,                     \n");
			strQuery.append("       a.cr_itemid,a.cr_baseitem,c.cr_rungbn                           \n");
			strQuery.append("  from cmm0031 b,cmm0020 g,cmm0020 e,cmm0020 d,                        \n");
			strQuery.append("       cmr1011 c,cmr1010 a                                             \n");
			strQuery.append("where a.cr_acptno=?                                                    \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd                    \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                             \n");
			if (adminYn == false) strQuery.append("and c.cr_svrcd<>'90'                             \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and a.cr_serno = c.cr_serno              \n");
			strQuery.append("and (nvl(c.cr_putcode,'NONE')<>'0000' or c.cr_rstfile is not null)     \n");
			strQuery.append("and d.cm_macode =decode(?,'03','CHECKIN','04','07','CHECKIN','CHECKIN','06','CHECKIN','16','CHECKIN','REQUEST') \n");
			strQuery.append("and a.cr_qrycd = d.cm_micode                                           \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode               \n");
			strQuery.append("and g.cm_macode = 'JAWON' and a.cr_rsrccd = g.cm_micode                \n");
			strQuery.append("and a.cr_baseitem is null and a.cr_itemid is null                      \n");
			strQuery.append("UNION                                                                  \n");
			strQuery.append("select a.cr_acptno,a.cr_syscd,0 cr_serno,a.cr_editor,b.cm_svrip,       \n");
			strQuery.append("       '파일송수신결과' cr_rsrcname,c.cr_svrname,c.cr_prcrst,b.cm_portno,\n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,         \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,                \n");
			strQuery.append("       D.CM_CODENAME as REQUEST,e.cm_codename as SYSGBN,               \n");
			strQuery.append("       '' JAWON,'' basersrc,'' cr_itemid,'' cr_baseitem,c.cr_rungbn    \n");
			strQuery.append("from cmm0031 b,cmm0020 e,cmm0020 d,cmr1011 c, cmr1000 a                \n");
			strQuery.append("where a.cr_acptno = ?                                                  \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and C.CR_SERNO=0                         \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd                    \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                             \n");
			if (adminYn == false) strQuery.append("and c.cr_svrcd<>'90'                             \n");
			strQuery.append("and d.cm_macode = 'REQUEST' and A.CR_QRYCD = d.cm_micode               \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode               \n");

			strQuery.append("order by prcdt,cr_svrname,cr_rsrcname                                  \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(pstmtcount++, acptNo);
			pstmt.setString(pstmtcount++, acptNo.substring(4,6));
            pstmt.setString(pstmtcount++, acptNo);
			pstmt.setString(pstmtcount++, acptNo.substring(4,6));
            pstmt.setString(pstmtcount++, acptNo);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String,String>();
				lstPrcSys = rs.getString("cr_prcsys");
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				if (rs.getString("cr_serno").equals("0")) {
					rst.put("SYSGBN",rs.getString("SYSGBN"));
				} else {
					if (rs.getString("cr_rungbn").equals("B")) {
						rst.put("SYSGBN",rs.getString("SYSGBN")+"[파일송수신전]");
					} else {
						rst.put("SYSGBN",rs.getString("SYSGBN")+"[파일송수신후]");
					}
				}
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("JAWON", rs.getString("JAWON"));
				rst.put("cr_svrname",rs.getString("cr_svrname"));
				rst.put("cm_portno",rs.getString("cm_portno"));
				rst.put("basersrc",rs.getString("basersrc"));
				if (rs.getString("cm_svrusr") != null) {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_svrusr"));
				} else {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_portno"));
				}
				rst.put("cr_prcrst",rs.getString("cr_prcrst"));

				if (rs.getString("cr_prcrst") != null){
					if (!rs.getString("cr_prcrst").equals("")){
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020 \n");
						strQuery.append("where cm_macode='ERRACCT' and cm_micode= ? \n");

						pstmt2 = conn.prepareStatement(strQuery.toString());
						pstmt2.setString(1, rs.getString("cr_prcrst"));
						rs2 = pstmt2.executeQuery();
						if (rs2.next()){
							rst.put("prcrstname",rs2.getString("cm_codename"));
						}
						else{
							rst.put("prcrstname",rs.getString("cr_prcrst"));
						}
						rs2.close();
						pstmt2.close();
					}
				}
				else{
					rst.put("prcrstname","");
				}
				if (rs.getString("cr_prcsys").equals("SYSFT")) {
					strQuery.setLength(0);
					strQuery.append("select cr_filename,cr_reldoc from cmr1001     \n");
					strQuery.append(" where cr_acptno=? and cr_seqno=?             \n");
					strQuery.append(" and cr_gubun='FRT'                           \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_acptno"));
					pstmt2.setString(2, Integer.toString(rs.getInt("cr_serno")));
					rs2 = pstmt2.executeQuery();
					if (rs2.next()){
						rst.put("ftfile",rs2.getString("cr_filename"));
						rst.put("ftattf",rs2.getString("cr_reldoc"));
					} else{
						rst.put("ftfile","");
						rst.put("ftattf","");
					}

					rs2.close();
					pstmt2.close();
				} else {
					rst.put("ftfile", "");
					rst.put("ftattf","");
				}

				rst.put("prcdt",rs.getString("prcdt"));
				rst.put("cr_rstfile",rs.getString("cr_rstfile"));
				rst.put("cr_prcsys",rs.getString("cr_prcsys"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("cr_seqno",rs.getString("cr_seqno"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_baseitem",rs.getString("cr_baseitem"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rst = new HashMap<String,String>();
			rst.put("lstprc", lstPrcSys);
			rtList.add(rst);
			rst = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5100.getResultList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5100.getResultList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5100.getResultList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getResultList_Tmax(String acptNo,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String            lstPrcSys   = "";
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,a.cr_syscd,a.cr_serno,a.cr_editor,b.cm_svrip, \n");
			strQuery.append("       a.cr_svrname svrname,c.cr_svrname,c.cr_prcrst,b.cm_portno,\n");;
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,   \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,          \n");
			strQuery.append("       d.cm_codename as REQUEST, e.cm_codename as SYSGBN         \n");
			strQuery.append("  from cmm0031 b,cmm0020 e,cmm0020 d,cmr1011 c,cmr1200 a         \n");
			strQuery.append("where a.cr_acptno=?                                              \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd              \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                       \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and a.cr_serno = c.cr_serno        \n");
			strQuery.append("and (c.cr_putcode is not null or c.cr_rstfile is not null)       \n");
			strQuery.append("and d.cm_macode = 'REQUEST' and substr(a.cr_acptno,5,2) = d.cm_micode \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode        \n");
			strQuery.append("UNION \n");
			strQuery.append("select a.cr_acptno,a.cr_syscd,0 cr_serno,a.cr_editor,b.cm_svrip, \n");
			strQuery.append("       '파일전송결과' svrname,c.cr_svrname,c.cr_prcrst,b.cm_portno,\n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,   \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,          \n");
			strQuery.append("       D.CM_CODENAME as REQUEST,e.cm_codename as SYSGBN          \n");
			strQuery.append("from cmm0031 b,cmm0020 e,cmm0020 d,cmr1011 c, cmr1000 a          \n");
			strQuery.append("where a.cr_acptno = ?                                            \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and C.CR_SERNO=0                   \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd              \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                       \n");
			strQuery.append("and d.cm_macode = 'REQUEST' and A.CR_QRYCD = d.cm_micode         \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode         \n");
			strQuery.append("order by prcdt,cr_svrname,svrname                                \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(pstmtcount++, acptNo);
			pstmt.setString(pstmtcount++, acptNo);
			////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				rst = new HashMap<String,String>();
				lstPrcSys = rs.getString("cr_prcsys");
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("SYSGBN",rs.getString("SYSGBN"));
				rst.put("cr_rsrcname", rs.getString("svrname"));
				//rst.put("JAWON", rs.getString("JAWON"));
				rst.put("cr_svrname",rs.getString("cr_svrname"));
				rst.put("cm_portno",rs.getString("cm_portno"));
				if (rs.getString("cm_svrusr") != null) {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_svrusr"));
				} else {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_portno"));
				}
				rst.put("cr_prcrst",rs.getString("cr_prcrst"));

				if (rs.getString("cr_prcrst") != null){
					if (!rs.getString("cr_prcrst").equals("")){
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020 \n");
						strQuery.append("where cm_macode='ERRACCT' and cm_micode= ? \n");

						pstmt2 = conn.prepareStatement(strQuery.toString());

						pstmt2.setString(1, rs.getString("cr_prcrst"));

						rs2 = pstmt2.executeQuery();

						if (rs2.next()){
							rst.put("prcrstname",rs2.getString("cm_codename"));
						}
						else{
							rst.put("prcrstname",rs.getString("cr_prcrst"));
						}

						rs2.close();
						pstmt2.close();
					}
				}
				else{
					rst.put("prcrstname","");
				}
				rst.put("ftfile", "");
				rst.put("ftattf","");

				rst.put("prcdt",rs.getString("prcdt"));
				rst.put("cr_rstfile",rs.getString("cr_rstfile"));
				rst.put("cr_prcsys",rs.getString("cr_prcsys"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("cr_seqno",rs.getString("cr_seqno"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rst = new HashMap<String,String>();
			rst.put("lstprc", lstPrcSys);
			rtList.add(rst);
			rst = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5100.getResultList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5100.getResultList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5100.getResultList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getResultGbn(String acptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select b.cm_micode,b.cm_codename,substr(a.cr_acptno,5,2) cr_qrycd \n");
			strQuery.append("from cmr9900 a,cmm0020 b                     \n");
			strQuery.append("where a.cr_acptno=?                          \n");
			strQuery.append("and ((a.cr_locat<>'00' and a.cr_teamcd='1'   \n");
			strQuery.append("      and a.cr_confdate is not null) or      \n");
			strQuery.append("     (a.cr_locat='00' and a.cr_teamcd='1'    \n");
			strQuery.append("                      and a.cr_status='0'))  \n");
			strQuery.append("and b.cm_macode='SYSGBN' and b.cm_micode=a.cr_team \n");
			strQuery.append("order by a.cr_confdate,cr_qrycd              \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmtcount = 1;
            pstmt.setString(pstmtcount++, acptNo);
            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rst = new HashMap<String,String>();
			rst.put("cm_micode","ALL");
			rst.put("cm_codename","전체");
			rst.put("cr_qrycd","00");
			rtList.add(rst);
			rst = null;
			
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_micode",rs.getString("cm_micode"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("cr_qrycd", rs.getString("cr_qrycd"));
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
			ecamsLogger.error("## Cmr5100.getResultGbn() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5100.getResultGbn() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultGbn() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5100.getResultGbn() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5100.getResultGbn() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public String getFileText(String rstfile) throws Exception {
		String			  fileStr = "";
		SystemPath		  csysPath    = new SystemPath();
		String			  resultPath  = "";
		String			  fileName = "";

		File readfile=null;
		ByteArrayOutputStream fileStream = null;
		byte[] byteTmpBuf = null;
		FileInputStream fis =null;
		int nCnt;

		try {

			resultPath = csysPath.getTmpDir("11");
			csysPath = null;
			fileName = resultPath + "/" + rstfile;

			fileStream = new ByteArrayOutputStream();
			readfile = new File(fileName);

			byteTmpBuf = new byte[8192];
			fis = new FileInputStream(readfile);

			while( (nCnt=fis.read(byteTmpBuf)) > -1 )
			{
				fileStream.write(byteTmpBuf, 0, nCnt);
			}
			fis.close();
			//fileStr = fileStream.toString("EUC-KR");
			fileStr = fileStream.toString("MS949");


			return fileStr;

			//end of while-loop statement

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getFileText() Exception END ##");
			throw exception;
		}finally{
			if (fileStream != null)	fileStream = null;
			if (fis != null) fis = null;
		}
	}
	public Object[] getResultList_Doc(String acptNo,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2       = null;
		ResultSet         rs2          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;
		String            lstPrcSys   = "";
		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_acptno,c.cr_syscd,a.cr_serno,a.cr_editor,b.cm_svrip, \n");
			strQuery.append("       f.cr_docfile,c.cr_svrname,c.cr_prcrst,b.cm_portno,        \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,   \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,          \n");
			strQuery.append("       d.cm_codename as REQUEST, e.cm_codename as SYSGBN         \n");
			strQuery.append("  from cmr0030 f,cmm0031 b,cmm0020 e,cmm0020 d,cmr1011 c,cmr1100 a \n");
			strQuery.append("where a.cr_acptno=? and a.cr_docid=f.cr_docid                    \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd              \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                       \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and a.cr_serno = c.cr_serno        \n");
			strQuery.append("and (c.cr_putcode is not null or c.cr_rstfile is not null)       \n");
			strQuery.append("and d.cm_macode = 'REQUEST' and substr(a.cr_acptno,5,2) = d.cm_micode \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode        \n");
			strQuery.append("UNION \n");
			strQuery.append("select a.cr_acptno,a.cr_syscd,0 cr_serno,a.cr_editor,b.cm_svrip, \n");
			strQuery.append("       '파일전송결과' cr_docfile,c.cr_svrname,c.cr_prcrst,b.cm_portno,\n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as prcdt,   \n");
			strQuery.append("       c.cr_prcsys,c.cr_seqno,c.cr_rstfile,b.cm_svrusr,          \n");
			strQuery.append("       D.CM_CODENAME as REQUEST,e.cm_codename as SYSGBN          \n");
			strQuery.append("from cmm0031 b,cmm0020 e,cmm0020 d,cmr1011 c, cmr1000 a          \n");
			strQuery.append("where a.cr_acptno = ?                                            \n");
			strQuery.append("and a.cr_acptno = c.cr_acptno and C.CR_SERNO=0                   \n");
			strQuery.append("and c.cr_syscd=b.cm_syscd and c.cr_svrcd=b.cm_svrcd              \n");
			strQuery.append("and c.cr_svrseq=b.cm_seqno                                       \n");
			strQuery.append("and d.cm_macode = 'REQUEST' and A.CR_QRYCD = d.cm_micode         \n");
			strQuery.append("and e.cm_macode = 'SYSGBN' and c.cr_prcsys = e.cm_micode         \n");
			strQuery.append("order by prcdt,cr_svrname,cr_docfile                             \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(pstmtcount++, acptNo);
			pstmt.setString(pstmtcount++, acptNo);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				rst = new HashMap<String,String>();
				lstPrcSys = rs.getString("cr_prcsys");
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("SYSGBN",rs.getString("SYSGBN"));
				rst.put("cr_rsrcname", rs.getString("cr_docfile"));
				//rst.put("JAWON", rs.getString("JAWON"));
				rst.put("cr_svrname",rs.getString("cr_svrname"));
				rst.put("cm_portno",rs.getString("cm_portno"));
				if (rs.getString("cm_svrusr") != null) {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_svrusr"));
				} else {
				   rst.put("resultsvr", rs.getString("cr_svrname")+":"+rs.getString("cm_portno"));
				}
				rst.put("cr_prcrst",rs.getString("cr_prcrst"));

				if (rs.getString("cr_prcrst") != null){
					if (!rs.getString("cr_prcrst").equals("")){
						strQuery.setLength(0);
						strQuery.append("select cm_codename from cmm0020 \n");
						strQuery.append("where cm_macode='ERRACCT' and cm_micode= ? \n");

						pstmt2 = conn.prepareStatement(strQuery.toString());

						pstmt2.setString(1, rs.getString("cr_prcrst"));

						rs2 = pstmt2.executeQuery();

						if (rs2.next()){
							rst.put("prcrstname",rs2.getString("cm_codename"));
						}
						else{
							rst.put("prcrstname",rs.getString("cr_prcrst"));
						}

						rs2.close();
						pstmt2.close();
					}
				}
				else{
					rst.put("prcrstname","");
				}
				rst.put("ftfile", "");
				rst.put("ftattf","");

				rst.put("prcdt",rs.getString("prcdt"));
				rst.put("cr_rstfile",rs.getString("cr_rstfile"));
				rst.put("cr_prcsys",rs.getString("cr_prcsys"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("cr_seqno",rs.getString("cr_seqno"));
				rtList.add(rst);
				rst = null;
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			rst = new HashMap<String,String>();
			rst.put("lstprc", lstPrcSys);
			rtList.add(rst);
			rst = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5100.getResultList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5100.getResultList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5100.getResultList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5100.getResultList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
