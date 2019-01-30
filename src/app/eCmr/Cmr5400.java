package app.eCmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import app.common.SystemPath;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

/** 소스비교
 * @author Administrator
 *
 */
public class Cmr5400 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/** 프로그램의 버전별 리스트
	 * @param ItemID : 프로그램ID
	 * @param AcptNo : 신청번호
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] getFileVer(String ItemID,String AcptNo,String Qrycd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int           diffCnt         = 0;
		int 		  cnt 			 = 0;
		boolean       findSw = false;
//		String        strInfo = "";

		try {
			conn = connectionContext.getConnection();

//			strQuery.setLength(0);
//			strQuery.append("select a.cm_info from cmm0036 a,cmr0020 b \n");
//			strQuery.append(" where b.cr_itemid=?                      \n");
//			strQuery.append("   and b.cr_syscd=a.cm_syscd              \n");
//			strQuery.append("   and b.cr_rsrccd=a.cm_rsrccd            \n");
//			pstmt = conn.prepareStatement(strQuery.toString());
//			pstmt.setString(1, ItemID);
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				strInfo = rs.getString("cm_info");
//			}
//			rs.close();
//			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("select b.cr_rsrcname,a.cr_ver,a.cr_acptno,a.cr_qrycd,       \n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi') as date1,\n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi') as date2, \n");
			strQuery.append("       b.cr_lstver,d.cm_username,b.cr_syscd,b.cr_rsrccd,    \n");
			strQuery.append("       h.cm_sysmsg, i.cm_dirpath,b.cr_status,c.cr_sayu      \n");
			strQuery.append("  from cmm0040 d,cmr0020 b,CMR0021 a,cmr1000 c,cmm0030 h,cmm0070 i \n");
			strQuery.append(" where a.cr_itemid= ?                                       \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid and a.cr_acptno=c.cr_acptno  \n");
			strQuery.append("   and c.cr_editor=d.cm_userid and b.cr_syscd=h.cm_syscd    \n");
			strQuery.append("   and b.cr_syscd=i.cm_syscd and b.cr_dsncd=i.cm_dsncd      \n");
			if(!Qrycd.equals("0000"))	strQuery.append("   a.qrycd = ?  			    \n");
			strQuery.append("order by date1 desc                                         \n");
			

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(++cnt, ItemID);
            if(!Qrycd.equals("0000")) pstmt.setString(++cnt, Qrycd);
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

			while (rs.next()){
				findSw = true;
				if (rs.getString("date2").equals("체크인 중")) {
					strQuery.setLength(0);
					strQuery.append("select count(*) cnt from cmr0027    \n");
					strQuery.append(" where cr_acptno=? and cr_itemid=?  \n");
					pstmt2 = conn.prepareStatement(strQuery.toString());
					pstmt2.setString(1, rs.getString("cr_acptno"));
					pstmt2.setString(2, ItemID);
					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						if (rs2.getInt("cnt") == 0) {
							findSw = false;
						}
					}
					rs2.close();
					pstmt2.close();
				}

				if (findSw == true) {
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", ItemID);
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("cr_syscd", rs.getString("cr_syscd"));
					rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					if (!rs.getString("cr_ver").equals("0"))
					   rst.put("cr_ver", rs.getString("cr_ver"));
					rst.put("cr_acptno",rs.getString("cr_acptno"));
					rst.put("DATE1",rs.getString("DATE1"));
					rst.put("DATE2",rs.getString("DATE2"));
					rst.put("cr_lstver",rs.getString("cr_lstver"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_status", rs.getString("cr_status"));
					rst.put("cr_qrycd", rs.getString("cr_qrycd"));
					rst.put("cr_sayu", rs.getString("cr_sayu"));
					if (AcptNo != null && AcptNo != "") {
						//ecamsLogger.debug("++++++++++++diffCnt,AcptNO,AcptNo++ : "+Integer.toString(diffCnt)+","+rs.getString("cr_acptno")+","+AcptNo);
						if (diffCnt == 0) {
							if (rs.getString("cr_acptno").equals(AcptNo)) {
								//ecamsLogger.debug("++++++++++equal 1+++++");
								rst.put("checked", "true");
								++diffCnt;
							} else{
								rst.put("checked", "false");
							}
						} else if (diffCnt == 1) {
							//ecamsLogger.debug("++++++++++equal 2+++++");
							rst.put("checked", "true");
							++diffCnt;
						} else {
							rst.put("checked", "false");
						}
					} else {
						if (rs.getRow() == 1 || rs.getRow() == 2)
							rst.put("checked", "true");
						else
							rst.put("checked", "false");
					}
					rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,12));
					

					rtList.add(rst);
					rst = null;
				}
			}
			rs.close();
			pstmt.close();

			int i = 0;
			diffCnt = 0;
			for (i=0;rtList.size()>i;i++) {
				if (rtList.get(i).get("checked") == "true") ++diffCnt;
				else if (diffCnt == 1) {
					rst = new HashMap<String,String>();
					rst = rtList.get(i);
					rst.put("checked", "true");
					++diffCnt;
					rtList.set(i,rst);
					rst = null;
				}
				if (diffCnt > 1) break;
			}

			if (diffCnt < 2) {
				if (AcptNo != null && AcptNo != "") {
					for (i=0;rtList.size()>i;i++) {
						if (rtList.get(i).get("cr_acptno").equals(AcptNo)) {
							rst = new HashMap<String,String>();
							rst = rtList.get(i);
							rst.put("checked", "true");
							++diffCnt;
							rtList.set(i,rst);
							rst = null;
						} else if (diffCnt == 1) {
							rst = new HashMap<String,String>();
							rst = rtList.get(i);
							rst.put("checked", "true");
							++diffCnt;
							rtList.set(i,rst);
							rst = null;
						}
						if (diffCnt > 1) break;
					}
				}

				if (diffCnt < 2 && rtList.size() > 1) {
					rst = new HashMap<String,String>();
					rst = rtList.get(0);
					rst.put("checked", "true");
					rtList.set(0,rst);
					rst = new HashMap<String,String>();
					rst = rtList.get(1);
					rst.put("checked", "true");
					rtList.set(1,rst);
					rst = null;
				}

			}
			/*
			if (strInfo.substring(44,46).equals("00") && rtList.size()>0) {
				rst = new HashMap<String,String>();
				rst.put("cr_itemid", ItemID);
				rst.put("cm_info", strInfo);
				rst.put("cm_sysmsg", rtList.get(0).get("cm_sysmsg"));
				rst.put("cr_syscd", rtList.get(0).get("cr_syscd"));
				rst.put("cr_rsrccd", rtList.get(0).get("cr_rsrccd"));
				rst.put("cr_rsrcname", rtList.get(0).get("cr_rsrcname"));

				rst.put("cr_ver", "T");
				rst.put("version", "개발서버");

				rst.put("cm_dirpath", rtList.get(0).get("cm_dirpath"));

				rst.put("acptno", "개발서버");
				rst.put("cr_qrycd", "T");
				rst.put("cr_sayu", "개발서버와 소스비교");

				rtList.add(0,rst);
				rst = null;

				if (diffCnt < 2 && rtList.size()>1) {
					rst = new HashMap<String,String>();
					rst = rtList.get(0);
					rst.put("checked", "true");
					rtList.set(0,rst);
					rst = new HashMap<String,String>();
					rst = rtList.get(1);
					rst.put("checked", "true");
					rtList.set(1,rst);
				}
			}
			*/
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList.toArray();

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5400.getFileVer() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5400.getFileVer() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5400.getFileVer() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5400.getFileVer() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)     try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null)  try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5400.getFileVer() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	/** 파일 비교
	 * @param ItemID
	 * @param befAcpt
	 * @param befQry
	 * @param aftAcpt
	 * @param aftQry
	 * @param chkFg
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Object[] fileDiffInf(String ItemID,String befAcpt,String befQry,String aftAcpt,String aftQry,boolean chkFg) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int               i           = 0;
		boolean           findSw      = false;

		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cr_rsrcname,c.cr_ver ver,a.cr_itemid,           \n");
			strQuery.append("       b.cm_dirpath,d.cm_info,c.cr_md5sum                \n");
			strQuery.append("  from cmr1010 a,cmm0070 b,cmr0025 c,cmm0036 d           \n");
			strQuery.append(" where a.cr_baseitem=? and a.cr_acptno= ?                \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
			strQuery.append("   and a.cr_itemid=c.cr_itemid                           \n");
			strQuery.append("   and a.cr_acptno=c.cr_acptno                           \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
			strQuery.append("   and substr(d.cm_info,10,1)='0'                        \n");
			strQuery.append("   and substr(d.cm_info,27,1)='0'                        \n");
			strQuery.append("   and substr(d.cm_info,12,1)='1'                        \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			pstmt.setString(1, ItemID);
			pstmt.setString(2, befAcpt);
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cr_itemid", rs.getString("cr_itemid"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_befver", rs.getString("ver"));
				rst.put("cm_info", rs.getString("cm_info"));
				rst.put("cr_md5sum", rs.getString("cr_md5sum"));
				rst.put("diff", "삭제");
				rst.put("ColorSw", "D");
				rtList.add(rst);
				rst = null;
			}
			rs.close();
			pstmt.close();

			if (aftQry.equals("T")) {
				for (i=0;rtList.size()>i;i++) {
					rst = new HashMap<String,String>();
					rst = rtList.get(i);
					rst.put("cr_aftver", "T");
					rst.put("diff", "개발");
					rst.put("ColorSw", "S");
					rtList.set(i, rst);
					rst = null;
				}
			} else {
				strQuery.setLength(0);
				if ( chkFg ) {
					strQuery.append("select a.cr_rsrcname,'' ver,a.cr_itemid,                 \n");
					strQuery.append("       b.cm_dirpath,d.cm_info,c.cr_md5sum,c.cr_acptno acptno \n");
					strQuery.append("  from cmr1010 a,cmm0070 b,cmr0027 c,cmm0036 d           \n");
					strQuery.append(" where a.cr_acptno= ? and a.cr_baseitem=?                \n");
					strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
					strQuery.append("   and a.cr_itemid=c.cr_itemid                           \n");
					strQuery.append("   and a.cr_acptno=c.cr_acptno and substr(d.cm_info,12,1)='1' \n");
					strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
					strQuery.append("   and substr(d.cm_info,10,1)='0'                        \n");
					strQuery.append("   and substr(d.cm_info,27,1)='0'                        \n");
					strQuery.append("   and substr(d.cm_info,12,1)='1'                        \n");
				} else {
					strQuery.append("select a.cr_rsrcname,c.cr_ver ver,a.cr_itemid,           \n");
					strQuery.append("       b.cm_dirpath,d.cm_info,c.cr_md5sum,'' acptno      \n");
					strQuery.append("  from cmr1010 a,cmm0070 b,cmr0025 c,cmm0036 d           \n");
					strQuery.append(" where a.cr_acptno= ? and a.cr_baseitem=?                \n");
					strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
					strQuery.append("   and a.cr_itemid=c.cr_itemid                           \n");
					strQuery.append("   and a.cr_acptno=c.cr_acptno and substr(d.cm_info,12,1)='1' \n");
					strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_rsrccd=d.cm_rsrccd \n");
					strQuery.append("   and substr(d.cm_info,10,1)='0'                        \n");
					strQuery.append("   and substr(d.cm_info,27,1)='0'                        \n");
					strQuery.append("   and substr(d.cm_info,12,1)='1'                        \n");
				}
	            pstmt = conn.prepareStatement(strQuery.toString());
				//pstmt =  new LoggableStatement(conn, strQuery.toString());
	            pstmt.setString(1, aftAcpt);
	            pstmt.setString(2, ItemID);
				//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	            rs = pstmt.executeQuery();
	            int svIdx         = 0;
				while (rs.next()){
					findSw = false;
					rst = new HashMap<String,String>();
					for (i=0;rtList.size()>i;i++) {
						if (rtList.get(i).get("cr_itemid").equals(rs.getString("cr_itemid"))) {
							if (rtList.get(i).get("cr_md5sum").equals(rs.getString("cr_md5sum"))) {
								rst.put("diff", "일치");
								rst.put("ColorSw", "S");
							} else {
								rst.put("diff", "변경");
								rst.put("ColorSw", "U");
							}
							svIdx = i;
							findSw = true;
							break;
						}
					}
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					if (rs.getString("ver") != null && rs.getString("ver") != "")
					   rst.put("cr_aftver", rs.getString("ver"));
					rst.put("cm_info", rs.getString("cm_info"));
					rst.put("cr_md5sum", rs.getString("cr_md5sum"));
					if (rs.getString("acptno") != null && rs.getString("acptno") != "")
						rst.put("cr_acptno", rs.getString("acptno"));

					if ( !findSw ) {
						rst.put("diff", "신규");
						rst.put("ColorSw", "A");
						rtList.add(rst);
					} else {
						rst.put("cr_befver", rtList.get(svIdx).get("cr_befver"));
						rtList.remove(svIdx);
						rtList.add(svIdx,rst);
					}
					rst = null;
				}
				rs.close();
				pstmt.close();
			}
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5400.fileDiffInf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5400.fileDiffInf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5400.fileDiffInf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5400.fileDiffInf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5400.fileDiffInf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getDiffSingle(String ItemId,String Version,String AcptNo,String GbnCd) throws Exception {

		String			  tmpPath = "";
//		String			  strBinPath = "";
//		String			  fileStr = "";
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		ArrayList<HashMap<String, String>>   rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;

		File shfile=null;
		String  shFileName = "";
		String	fileName = "";
		
		BufferedReader in1  = null;
		String	readline1 = "";
		String strParm = "";
		int ret;
		int	linecnt;
		//int nCnt;

		try {

			conn = connectionContext.getConnection();
			SystemPath		  cTempGet	  = new SystemPath();
			Cmr0200 cmr0200 = new Cmr0200();
			tmpPath = cTempGet.getTmpDir_conn("99",conn);
			cTempGet = null;

			shFileName = tmpPath + "/" + ItemId +"_gensrc.sh";
			fileName = tmpPath + "/" + ItemId +"_gensrc.file";
			
			if (Version == null || Version == "") {
				if (AcptNo.substring(4,6).equals("04")) {
					strQuery.setLength(0);
					strQuery.append("select cr_status,cr_prcdate,cr_version from cmr1010      \n");
					strQuery.append(" where cr_acptno=? and cr_itemid=?                       \n");
					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt.setString(1, AcptNo);
					pstmt.setString(2, ItemId);
					rs = pstmt.executeQuery();
					if (rs.next()) {
						if (rs.getString("cr_prcdate") != null && !rs.getString("cr_status").equals("3")) {
							Version = Integer.toString(rs.getInt("cr_version"));
						}
					}
					rs.close();
					pstmt.close();
				}
			}
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;

			if (!Version.equals("")){
				strParm = "./ecams_gensrc CMR0025 " + ItemId + " " + ItemId +"_gensrc.file" + " " + Version;
			}
			else{
				strParm = "./ecams_gensrc CMR0027 " + ItemId + " " + ItemId +"_gensrc.file" + " " + AcptNo;
			}
			ret = cmr0200.execShell(shFileName, strParm, false);
			if (ret != 0) {
				throw new Exception("해당 소스 생성  실패. run=["+strParm +"]" + " return=[" + ret + "]" );
			}
			cmr0200 = null;
			//fileStream = new ByteArrayOutputStream();
			//shfile = new File(fileName);

//			fileStr = "";
			linecnt = 1;
			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"MS949"));
			while( (readline1 = in1.readLine()) != null ){
				strQuery.append(readline1+"\n");
				rst = new HashMap<String, String>();
				rst.put("lineno",Integer.toString(linecnt));
				if (GbnCd.equals("B")){
					rst.put("file1", readline1);
				}
				else{
					rst.put("file2", readline1);
				}
				rtList.add(rst);
				rst = null;
			}
			in1.close();

			shfile = new File(fileName);
			shfile.delete();

			//end of while-loop statement

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5400.getDiffSingle() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5400.getDiffSingle() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5400.getDiffSingle() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5400.getDiffSingle() Exception END ##");
			throw exception;
		}finally{
			if (shfile != null) shfile = null;
			if (strQuery != null) 	strQuery = null;
			if (rtList !=  null)	rtList = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5400.getDiffSingle() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
	
	public Object[] getCboSet() throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();


		try {

			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			
				strQuery.append("select cm_micode,cm_codename from cmm0020           \n");
				strQuery.append("where cm_macode='REQUEST'				             \n");
				strQuery.append("and cm_micode in ('03','04','05','16','07')	     \n");
				strQuery.append("and cm_closedt is null                	             \n");

			
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
			
			//ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();
			rst = new HashMap<String,String>();
			rst.put("cm_micode", "0000");
			rst.put("cm_codename", "전체");
			rtList.add(rst);
			rst = null;
			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_micode", rs.getString("cm_micode"));
				rst.put("cm_codename", rs.getString("cm_codename"));
				rtList.add(rst);
				rst = null;
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
			ecamsLogger.error("## Cmr5400.getCboSet() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5400.getCboSet() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5400.getCboSet() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5400.getCboSet() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5400.getCboSet() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
