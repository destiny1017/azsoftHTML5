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

import app.common.LoggableStatement;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr5200 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public Object[] getDiffAry(String UserID,String ItemID1,String ver1,String ItemID2,String ver2) throws SQLException, Exception {
		Connection        conn        = null;
		SystemPath		  cTempGet	  = new SystemPath();
		String			  tmpPath = "";
//		String			  strBinPath = "";
		ArrayList<HashMap<String, String>>   rtList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		String  shFileName = "";
		String	fileName = "";
		String	fileName1 = "";
		String	fileName2 = "";

		String readline1 = "";
		String readline2 = "";
		String strParm = "";
		BufferedReader in1  = null;
		BufferedReader in2  = null;

		int	linecnt;
		int ret;
		File shfile=null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {

			conn = connectionContext.getConnection();
			tmpPath = cTempGet.getTmpDir_conn("99",conn);
			conn.close();
			conn = null;
			Cmr0200 cmr0200 = new Cmr0200();
			
			shFileName = UserID+ ItemID1 +"_cmpsrc.sh";
			fileName = UserID+ ItemID1;

			strParm = "./ecams_cmpsrc " + ItemID1 + " " + ver1 +" " + ItemID2 + " " +ver2 + " " + fileName;
			ret = cmr0200.execShell(shFileName, strParm, false);
			if (ret != 0) {
				throw new Exception("해당 소스 생성  실패. run=["+strParm +"]" + " return=[" + ret + "]" );
			}

			fileName1 = tmpPath + "/" + fileName + ".1";
			fileName2 = tmpPath + "/" + fileName + ".2";

			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName1),"MS949"));
			in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName2),"MS949"));

			rtList.clear();
			linecnt = 1;
			while( ((readline1 = in1.readLine()) != null ) && ((readline2 = in2.readLine()) != null ))
			{
				rst = new HashMap<String, String>();
				rst.put("lineno",Integer.toString(linecnt));

				if (readline1 != null){
					if (readline1.substring(0, 2).equals("D ")){
						rst.put("file1diff",readline1.substring(0, 2));
						rst.put("file1", readline1.substring(2));
					}
					else if (readline1.substring(0, 2).equals("I ")){
						rst.put("file1diff",readline1.substring(0, 2));
						rst.put("file1", readline1.substring(2));
					}
					else if (readline1.substring(0, 2).equals("RO")){
						rst.put("file1diff",readline1.substring(0, 2));
						rst.put("file1", readline1.substring(2));
					}
					else if (readline1.substring(0, 2).equals("RN")){
						rst.put("file1diff",readline1.substring(0, 2));
						rst.put("file1", readline1.substring(2));
					}
					else{
						rst.put("file1", readline1);
					}
					//ecamsLogger.error("11111111111111"+readline1);
				}
				if (readline2 != null){
					if (readline2.substring(0, 2).equals("D ")){
						rst.put("file2diff",readline2.substring(0, 2));
						rst.put("file2", readline2.substring(2));
					}
					else if (readline2.substring(0, 2).equals("I ")){
						rst.put("file2diff",readline2.substring(0, 2));
						rst.put("file2", readline2.substring(2));
					}
					else if (readline2.substring(0, 2).equals("RO")){
						rst.put("file2diff",readline2.substring(0, 2));
						rst.put("file2", readline2.substring(2));
					}
					else if (readline2.substring(0, 2).equals("RN")){
						rst.put("file2diff",readline2.substring(0, 2));
						rst.put("file2", readline2.substring(2));
					}
					else{
						rst.put("file2", readline2);
					}
					//ecamsLogger.error("22222222222222"+readline2);
				}
				rtList.add(rst);
				rst = null;
				linecnt++;
			}
			in1.close();
			in2.close();
			in1 = null;
			in2 = null;

			
			shfile = new File(fileName1);
			shfile.delete();
			shfile = null;

			shfile = new File(fileName2);
			shfile.delete();
			shfile = null;

			return rtList.toArray();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getDiffAry() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5200.getDiffAry() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getDiffAry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getDiffAry() Exception END ##");
			throw exception;
		}finally{
			if (shfile != null) shfile.delete();shfile = null;
			if (rtList != null)	rtList = null;
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
			//fileStr = fileStream.toString("EUC-KR");
		}
	}


	public Object[] getFileVer(String ItemID,String AcptNo) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int               diffCnt     = 0;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		boolean       findSw = false;
		String        strInfo = "";

		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select a.cm_info from cmm0036 a,cmr0020 b \n");
			strQuery.append(" where b.cr_itemid=?                      \n");
			strQuery.append("   and b.cr_syscd=a.cm_syscd              \n");
			strQuery.append("   and b.cr_rsrccd=a.cm_rsrccd            \n");
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, ItemID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				strInfo = rs.getString("cm_info");
			}
			rs.close();
			pstmt.close();

			strQuery.setLength(0); 
			strQuery.append("select b.cr_rsrcname,decode(c.cr_qrycd,'03',j.cr_befver,'04',j.cr_befver,j.cr_version) cr_ver,    \n");
			strQuery.append("       a.cr_acptno,c.cr_sayu,c.cr_qrycd,                           \n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as date1,    \n");
			strQuery.append("       to_char(j.cr_prcdate,'yyyy/mm/dd hh24:mi:ss') as date2,     \n");
			strQuery.append("       b.cr_lstver,d.cm_username,b.cr_syscd,b.cr_rsrccd,           \n");
			strQuery.append("       h.cm_sysmsg,i.cm_dirpath, b.cr_status,k.cm_codename         \n");
			strQuery.append("  from cmm0040 d,cmr0020 b,CMR0025 a,cmr1000 c,cmr1010 j,cmm0030 h,cmm0070 i,cmm0020 k \n");
			strQuery.append(" where a.cr_itemid= ?                                              \n");
			strQuery.append("   and a.cr_acptno=c.cr_acptno and c.cr_acptno=j.cr_acptno         \n");
			strQuery.append("   and a.cr_itemid=j.cr_itemid                                     \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid                                     \n");
			strQuery.append("   and c.cr_editor=d.cm_userid and b.cr_syscd=h.cm_syscd           \n");
			strQuery.append("   and b.cr_syscd=i.cm_syscd and b.cr_dsncd  = i.cm_dsncd          \n");
			strQuery.append("   and k.cm_macode='REQUEST' and k.cm_micode=c.cr_qrycd            \n");
			strQuery.append("union                                                              \n");
			strQuery.append("select b.cr_rsrcname,a.cr_ver,a.cr_acptno,j.cr_sayu,j.cr_qrycd,    \n");
			strQuery.append("       to_char(j.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as date1,    \n");
			strQuery.append("       to_char(j.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as date2,    \n");
			strQuery.append("       b.cr_lstver,d.cm_username,b.cr_syscd,b.cr_rsrccd,           \n");
			strQuery.append("       h.cm_sysmsg, i.cm_dirpath, b.cr_status,k.cm_codename        \n");
			strQuery.append("  from cmm0040 d,cmr0020 b,CMR0025 a,cmm0030 h,cmm0070 i,cmr0021 j,cmm0020 k \n");
			strQuery.append(" where a.cr_itemid= ?                                              \n");
			strQuery.append("   and a.cr_itemid=j.cr_itemid and a.cr_acptno=j.cr_acptno         \n");
			strQuery.append("   and a.cr_ver=j.cr_ver                                           \n");
			strQuery.append("   and j.cr_qrycd='16' and substr(j.cr_acptno,7)='000000'          \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid and b.cr_creator=d.cm_userid        \n");
			strQuery.append("   and b.cr_syscd=h.cm_syscd                                       \n");
			strQuery.append("   and b.cr_syscd=i.cm_syscd and b.cr_dsncd=i.cm_dsncd             \n");
			strQuery.append("   and k.cm_macode='REQUEST' and k.cm_micode=j.cr_qrycd            \n");
			strQuery.append("union                                                              \n");
			//strQuery.append("select a.cr_rsrcname,0 cr_ver,c.cr_acptno,c.cr_qrycd,       \n");
			strQuery.append("select a.cr_rsrcname,b.cr_befver cr_ver,c.cr_acptno,c.cr_sayu,c.cr_qrycd,\n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as date1, \n");
			strQuery.append("       '운영배포 중' as date2,                                  \n");
			strQuery.append("       a.cr_lstver,d.cm_username,a.cr_syscd,a.cr_rsrccd,     \n");
			strQuery.append("       h.cm_sysmsg, i.cm_dirpath, a.cr_status,k.cm_codename  \n");
			strQuery.append("  from cmm0040 d,cmr0020 a,cmr1000 c,cmr1010 b,cmm0030 h,cmm0070 i,cmm0020 k \n");
			strQuery.append(" where a.cr_itemid= ? and a.cr_status='7'                   \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid                              \n");
			strQuery.append("   and b.cr_status='0'                                      \n");
			strQuery.append("   and b.cr_acptno=c.cr_acptno                              \n");
			strQuery.append("   and c.cr_qrycd='04'                                      \n");
			strQuery.append("   and c.cr_editor=d.cm_userid and a.cr_syscd=h.cm_syscd    \n");
			strQuery.append("   and a.cr_syscd=i.cm_syscd and a.cr_dsncd=i.cm_dsncd      \n");
			strQuery.append("   and k.cm_macode='REQUEST' and k.cm_micode=c.cr_qrycd     \n");
			strQuery.append("union                                                       \n");
			//strQuery.append("select a.cr_rsrcname,0 cr_ver,c.cr_acptno,c.cr_qrycd,       \n");
			strQuery.append("select a.cr_rsrcname,b.cr_befver cr_ver,c.cr_acptno,c.cr_sayu,c.cr_qrycd,\n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi:ss') as date1, \n");
			strQuery.append("       '테스트배포 중' as date2,                           	 \n");
			strQuery.append("       a.cr_lstver,d.cm_username,a.cr_syscd,a.cr_rsrccd,    \n");
			strQuery.append("       h.cm_sysmsg, i.cm_dirpath,a.cr_status,k.cm_codename  \n");
			strQuery.append("  from cmm0040 d,cmr0020 a,cmr1000 c,cmm0030 h,cmm0070 i,   \n");
			strQuery.append("       cmr1010 b,cmm0020 k                                  \n");
			strQuery.append(" where a.cr_itemid= ? and a.cr_status in ('A','B')          \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid                              \n");
			strQuery.append("   and b.cr_acptno=c.cr_acptno                              \n");
			strQuery.append("   and c.cr_qrycd='03'                                      \n");
			strQuery.append("   and c.cr_editor=d.cm_userid and a.cr_syscd=h.cm_syscd    \n");
			strQuery.append("   and a.cr_syscd=i.cm_syscd and a.cr_dsncd=i.cm_dsncd      \n");
			strQuery.append("   and k.cm_macode='REQUEST' and k.cm_micode=c.cr_qrycd     \n");
			strQuery.append("order by date1 desc                                         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, ItemID);
            pstmt.setString(2, ItemID);
            pstmt.setString(3, ItemID);
            pstmt.setString(4, ItemID);
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
				findSw = true;
				if (findSw == true) {
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", ItemID);
					rst.put("cm_info", strInfo);
					rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
					rst.put("cr_syscd", rs.getString("cr_syscd"));
					rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
					rst.put("cr_qrycd", rs.getString("cr_qrycd"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_codename", rs.getString("cm_codename"));

					if (rs.getInt("cr_ver") == 0) {
						rst.put("cr_ver", rs.getString("cr_acptno"));
						rst.put("version", "진행중");
					} else {
						rst.put("cr_ver", rs.getString("cr_ver"));
						rst.put("version", rs.getString("cr_ver"));
					}
					rst.put("cr_acptno",rs.getString("cr_acptno"));
					rst.put("DATE1",rs.getString("DATE1"));
					rst.put("DATE2",rs.getString("DATE2"));
					rst.put("cr_lstver",rs.getString("cr_lstver"));
					rst.put("cm_username",rs.getString("cm_username"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					rst.put("cr_status", rs.getString("cr_status"));

					if (rs.getString("cr_qrycd").equals("16")) rst.put("acptno", "최초이행");
					else rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,12));

					ecamsLogger.error("++++++++++++AcptNo check"+AcptNo);

					if (AcptNo != null && AcptNo != "") {
						ecamsLogger.error("++++++++++++diffCnt,AcptNO,AcptNo++ : "+Integer.toString(diffCnt)+","+rs.getString("cr_acptno")+","+AcptNo);
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
					if (rs.getString("cr_sayu") != null){
						rst.put("cr_sayu", rs.getString("cr_sayu"));
					}
					else{
						rst.put("cr_sayu", "");
					}
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
				}
				if (diffCnt > 1) break;
			}

			if (diffCnt < 2) {
				if (AcptNo != null && AcptNo != "") {
					if (AcptNo.substring(4,6).equals("03")) {
						strQuery.setLength(0);
						strQuery.append("select a.cr_confno from cmr1010 a,cmr1010 b  \n");
						strQuery.append(" where b.cr_itemid=? and b.cr_acptno=?       \n");
						strQuery.append("   and b.cr_baseno=a.cr_acptno               \n");
						pstmt = conn.prepareStatement(strQuery.toString());
						pstmt.setString(1, ItemID);
						pstmt.setString(2, AcptNo);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							AcptNo = rs.getString("cr_confno");
						} else AcptNo = null;
						rs.close();
						pstmt.close();
					} else AcptNo = null;
				}
				if (AcptNo != null && AcptNo != "") {
					for (i=0;rtList.size()>i;i++) {
						if (rtList.get(i).get("cr_acptno").equals(AcptNo)) {
							rst = new HashMap<String,String>();
							rst = rtList.get(i);
							rst.put("checked", "true");
							++diffCnt;
							rtList.set(i,rst);
						} else if (diffCnt == 1) {
							rst = new HashMap<String,String>();
							rst = rtList.get(i);
							rst.put("checked", "true");
							++diffCnt;
							rtList.set(i,rst);
						}
						if (diffCnt > 1) break;
					}
				}

				if (diffCnt < 2 && rtList.size()>1) {
					diffCnt = 1;
					rst = new HashMap<String,String>();
					rst = rtList.get(0);
					rst.put("checked", "true");
					rtList.set(0,rst);
					rst = new HashMap<String,String>();
					rst = rtList.get(1);
					rst.put("checked", "true");
					rtList.set(1,rst);
					++diffCnt;
				}

			}
			if (strInfo.substring(44,46).equals("00") && rtList.size()>0) {
				/*
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
				rst.put("cr_sayu", "개발서버와 소스비교");

				rtList.add(0,rst);
				rst = null;
				*/
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
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;

			return rtList.toArray();

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5200.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5200.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr5200.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}


	public Object[] getSvrList(String sysCd,String rsrcCd) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
//		Object[]		  rtObj		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select a.cm_svrcd,a.cm_seqno,a.cm_svrname,c.cm_codename \n");
			strQuery.append("from cmm0031 a,cmm0038 b,cmm0020 c \n");
			strQuery.append("where a.cm_syscd= ? \n");
			strQuery.append("and a.cm_closedt is null and a.cm_cmpsvr='Y' \n");
			strQuery.append("and a.cm_syscd=b.cm_syscd and a.cm_svrcd=b.cm_svrcd \n");
			strQuery.append("and a.cm_seqno=b.cm_seqno \n");
			strQuery.append("and b.cm_rsrccd= ? \n");
			strQuery.append("and c.cm_macode='SERVERCD' and c.cm_micode=a.cm_svrcd \n");
			strQuery.append("order by a.cm_svrcd \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmt.setString(1, sysCd);
            pstmt.setString(2, rsrcCd);
            rs = pstmt.executeQuery();

			rst = new HashMap<String,String>();
			rst.put("cm_svrcd", "00");
			rst.put("cm_seqno", "00");
			rst.put("cm_svrname", "00");
			rst.put("cm_codename","00");
			rst.put("combolabel", "선택하세요.");
			rtList.add(rst);
			rst = null;

			while (rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cm_svrcd", rs.getString("cm_svrcd"));
				rst.put("cm_seqno", rs.getString("cm_seqno"));
				rst.put("cm_svrname", rs.getString("cm_svrname"));
				rst.put("cm_codename",rs.getString("cm_codename"));
				rst.put("combolabel", rs.getString("cm_svrname")+"["+rs.getString("cm_codename")+"]");
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

			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getSvrList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5200.getSvrList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getSvrList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5200.getSvrList() Exception END ##");
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
					ecamsLogger.error("## Cmr5200.getSvrList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}

	public String getCheckInAcptNo(String ItemID) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();
		String			  rtString    = "";

		try {
			conn = connectionContext.getConnection();

			strQuery.setLength(0);
			strQuery.append("select max(a.cr_acptno) cr_acptno       \n");
			strQuery.append("from cmr0027 a, cmr0020 b               \n");
			strQuery.append("where a.cr_itemid= ?                    \n");
			strQuery.append("and  a.cr_itemid= b.cr_itemid           \n");
			strQuery.append("and ((b.cr_status='7' and substr(a.cr_acptno,5,2)='04') or        \n");
			strQuery.append("     (b.cr_status in ('A','B') and substr(a.cr_acptno,5,2)='03')) \n");

            pstmt = conn.prepareStatement(strQuery.toString());
            pstmt.setString(1, ItemID);
            rs = pstmt.executeQuery();
			if (rs.next()){
				rtString = rs.getString("cr_acptno");
			}
			rs.close();
			pstmt.close();
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;


			return rtString;
			//end of while-loop statement


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getCheckInAcptNo() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5200.getCheckInAcptNo() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getCheckInAcptNo() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5200.getCheckInAcptNo() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5200.getCheckInAcptNo() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

}
