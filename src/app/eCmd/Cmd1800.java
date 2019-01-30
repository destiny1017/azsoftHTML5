package app.eCmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
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

public class Cmd1800 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public boolean genFile(String ItemId,String Version,String RsrcName,String pathStr) throws Exception {

		String			  tmpPath = "";
		String			  strBinPath = "";
		File shfile=null;
		String  shFileName = "";
		String	fileName = "";
		OutputStreamWriter writer = null;
		String[] strAry = null;
		Runtime  run = null;
		Process p = null;

		try {

			SystemPath		  cTempGet	  = new SystemPath();
			tmpPath = cTempGet.getTmpDir("99");
			strBinPath = cTempGet.getTmpDir("14");
			cTempGet = null;

			shFileName = tmpPath + "/" + ItemId +"_Cmd1800.sh";
			fileName = pathStr + "/" + RsrcName;

			shfile = new File(pathStr);
			shfile.mkdirs();
			shfile = null;
			shfile = new File(shFileName);

			if( !(shfile.isFile()) )              //File이 없으면
			{
				shfile.createNewFile();          //File 생성
			}

			writer = new OutputStreamWriter( new FileOutputStream(shFileName));
			writer.write("cd "+strBinPath +"\n");
			writer.write("./FileRead_cmr0025 " + ItemId + " " + fileName + " " + Version + "\n");
			writer.write("exit $?\n");
			writer.close();

			strAry = new String[3];
			strAry[0] = "chmod";
			strAry[1] = "777";
			strAry[2] = shFileName;

			run = Runtime.getRuntime();
			//ecamsLogger.debug("====chmod===="+"chmod "+"777 "+shFileName+" \n");
			p = run.exec(strAry);
			p.waitFor();

			//ecamsLogger.debug("====chmod return===="+Integer.toString(p.exitValue())+" \n");

			run = Runtime.getRuntime();
			strAry = new String[2];
			strAry[0] = "/bin/sh";
			strAry[1] = shFileName;
			p = run.exec(strAry);
			p.waitFor();

			//ecamsLogger.debug("====return===="+Integer.toString(p.exitValue())+" \n");
			if (p.exitValue() != 0) {
				shfile.delete();
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				return false;
				//throw new Exception("해당 소스 생성  실패. run=["+shFileName +"]" + " return=[" + p.exitValue() + "]" );
			}else{
				shfile.delete();
				if (strAry != null) strAry = null;
				if (run != null) run = null;
				if (p != null) p = null;
				return true;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1800.getFileText() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1800.getFileText() Exception END ##");
			throw exception;
		}finally{
			if (shfile != null) shfile = null;
			if (strAry != null) strAry = null;
			if (run != null) run = null;
			if (p != null) p = null;
		}
	}

	public Object[] getFileList(String SysCd,String SysGb,String DsnCd,String RsrcCd,String RsrcName,String sysAllFlag) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		int				  pstmtcount  = 1;

		try {
			conn = connectionContext.getConnection();

			strQuery.append("select a.CR_RSRCNAME,a.CR_STORY,a.CR_LSTVER,TO_CHAR(a.CR_LASTDATE,'yyyy-mm-dd') as LASTDT, \n");
			strQuery.append("a.CR_SYSCD,a.CR_RSRCCD,a.CR_LANGCD,a.CR_DSNCD,a.CR_JOBCD,a.CR_FILESIZE,a.CR_FILEDATE,a.CR_ITEMID,a.CR_STATUS, \n");
			strQuery.append("c.CM_CODENAME as CODENAME, d.CM_USERNAME, e.CM_DIRPATH, f.CM_CODENAME as JAWON,b.CM_INFO,g.cm_jobname \n");
			strQuery.append("from cmm0020 f,cmm0070 e,cmm0040 d,cmm0020 c,cmm0036 b,cmr0020 a ,cmm0102 g \n");
			strQuery.append("where a.cr_syscd = ? and \n");

			if (sysAllFlag.equals("n")){
				if (!RsrcName.equals("")){
					if (!RsrcName.equals("*")){
						if (RsrcName.indexOf(",") >= 0){
							strQuery.append(" a.cr_dsncd= ? and \n");
							strQuery.append(" a.cr_rsrcname= ? and \n");
						}
						else{
							strQuery.append(" a.cr_rsrcname like ? and \n");
						}
					}
				}

				if (!DsnCd.equals("")){
					if (!DsnCd.substring(0, 1).equals("F")){
						strQuery.append(" a.cr_dsncd= ? and \n");
					}
					else{
						strQuery.append(" e.cm_dirpath like ? and a.cr_rsrccd = ? and \n");
					}
				}
			}
			strQuery.append(" to_number(a.cr_lstver) > 0  and \n");
			strQuery.append(" substr(b.cm_info,12,1)='1' and \n");
			strQuery.append(" b.cm_closedt is null and        \n");
			strQuery.append("a.cr_editor=d.cm_userid and a.cr_jobcd = g.cm_jobcd and \n");
			strQuery.append("c.cm_macode='CMR0020' and a.cr_status=c.cm_micode and \n");
			strQuery.append("f.cm_macode='JAWON' and a.cr_rsrccd=f.cm_micode and \n");
			strQuery.append("a.cr_syscd=e.cm_syscd and a.cr_dsncd=e.cm_dsncd and \n");
			strQuery.append("a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());
            pstmtcount  = 1;
            pstmt.setString(pstmtcount++, SysCd);
            if (sysAllFlag.equals("n")){
				if (!RsrcName.equals("")){
					if (!RsrcName.equals("*")){
						if (RsrcName.indexOf(",") >= 0){
							pstmt.setString(pstmtcount++, RsrcName.substring(0, 5));
							pstmt.setString(pstmtcount++, RsrcName.substring(6));
						}
						else{
							pstmt.setString(pstmtcount++, "%"+RsrcName.replaceAll("[*]","")+"%");
						}
					}
				}
				if (!DsnCd.equals("")){
					if (!DsnCd.substring(0, 1).equals("F")){
						pstmt.setString(pstmtcount++, DsnCd);
					}
					else{
						pstmt.setString(pstmtcount++, DsnCd.substring(1)+ "%");
						pstmt.setString(pstmtcount++, RsrcCd);
					}
				}
            }
            //ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();
			while (rs.next()){
				rst = new HashMap<String,String>();
				//rst.put("ID", Integer.toString(rs.getRow()));
				rst.put("cm_dirpath",rs.getString("cm_dirpath"));
				rst.put("cr_rsrcname",rs.getString("cr_rsrcname"));
				//rst.put("cr_story",rs.getString("cr_story"));
				rst.put("jobname", rs.getString("cm_jobname"));
				rst.put("jawon",rs.getString("jawon"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("lastdt",rs.getString("lastdt"));
				rst.put("codename",rs.getString("codename"));
				rst.put("sysgb",SysGb);
				rst.put("cr_syscd",rs.getString("cr_syscd"));
				rst.put("cr_rsrccd",rs.getString("cr_rsrccd"));
				rst.put("cr_langcd",rs.getString("cr_langcd"));
				rst.put("cr_dsncd",rs.getString("cr_dsncd"));
				rst.put("cr_jobcd",rs.getString("cr_jobcd"));
				//rst.put("cr_filesize",rs.getString("cr_filesize"));
				//rst.put("cr_filedate",rs.getString("cr_filedate"));
				rst.put("cr_itemid",rs.getString("cr_itemid"));
				rst.put("cr_status",rs.getString("cr_status"));
				rst.put("cm_info",rs.getString("cm_info"));
				rst.put("makeflag","0");
				rst.put("errflag","0");
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
			ecamsLogger.error("## Cmd1800.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmd1800.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmd1800.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmd1800.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmd1800.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}
}
