package app.eCmm;

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
//import app.common.LoggableStatement;
import app.common.SystemPath;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmm1501 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	public String getReqInf(String acptNo) throws Exception {

		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		String            retDate     = null;
		ConnectionContext connectionContext = new ConnectionResource();

		try {
			conn = connectionContext.getConnection();
			strQuery.append("select to_char(sysdate,'yyyymmdd') acpt,             \n");
			strQuery.append("       (select to_char(max(cr_prcdate),'yyyymmdd')   \n");
			strQuery.append("         from cmr1011                                \n");
			strQuery.append("        where cr_acptno=?) prcdt,                    \n");
			strQuery.append("       (select to_char(max(cr_confdate),'yyyymmdd')  \n");
			strQuery.append("         from cmr9900                                \n");
			strQuery.append("        where cr_acptno=?) confdt                    \n");
			strQuery.append("  from cmr1000 a                                     \n");
			strQuery.append(" where a.cr_acptno=?                                 \n");
	        pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt = new LoggableStatement(conn,strQuery.toString());
			pstmt.setString(1,acptNo);
			pstmt.setString(2,acptNo);
			pstmt.setString(3,acptNo);
	        ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
	        rs = pstmt.executeQuery();
			if (rs.next()){
				if (rs.getString("prcdt") != null) retDate = rs.getString("prcdt");
				else if (rs.getString("confdt") != null) retDate = rs.getString("confdt");
				else retDate = rs.getString("acpt");
			}//end of while-loop statement
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return retDate;


		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmm1501.getReqInf() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmm1501.getReqInf() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmm1501.getReqInf() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmm1501.getReqInf() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmm1501.getReqInf() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
	public Object[] getLogView(String acptNo,String strDate) throws Exception {
		SystemPath		  cTempGet	  = new SystemPath();
		String			  logPath = "";
		String			  fileName1 = "";
		String			  fileName2 = "";
		String  		  szAcptNo = "";
		String			  szDate = "";
		StringBuffer      strQuery1    = new StringBuffer();
		BufferedReader    in1  = null;
		String			  readline1 = "";
		File			  chkFile;
		HashMap<String, String>			  rst		  = null;
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		Object[]		  rtObj		  = null;

		try {
			if (acptNo == null){
				szAcptNo = "";
			}else if (acptNo == ""){
				szAcptNo = "";
			}else{
				szAcptNo = acptNo;
			}

			szDate = strDate.replaceAll("-", "");
			logPath = cTempGet.getTmpDir("12");
			fileName1 = logPath+"/"+szDate + ".log";
			fileName2 = logPath+"/TransList."+szDate + ".log";

			rst = new HashMap<String,String>();

			chkFile = new File(fileName1);

			if (chkFile.exists()){
				strQuery1.setLength(0);

				in1 = new BufferedReader(new InputStreamReader(new FileInputStream(chkFile),"MS949"),5120);

				while( (readline1 = in1.readLine()) != null ){
					if (szAcptNo.equals("")){
						strQuery1.append(readline1+"\n");
					}
					else{
						if (readline1.indexOf(szAcptNo) != -1){
							strQuery1.append(readline1+"\n");
						}
					}
				}
				in1.close();

				rst.put("file1", strQuery1.toString());
			}else{
				rst.put("file1", fileName1+" 파일 없음");
			}


			chkFile = new File(fileName2);


			if (chkFile.exists()){
				strQuery1.setLength(0);

				in1 = new BufferedReader(new InputStreamReader(new FileInputStream(chkFile),"MS949"),1024);

				while( (readline1 = in1.readLine()) != null ){
					if (szAcptNo.equals("")){
						strQuery1.append(readline1+"\n");
					}
					else{
						if (readline1.indexOf(szAcptNo) != -1){
							strQuery1.append(readline1+"\n");
						}
					}
				}
				in1.close();

				rst.put("file2", strQuery1.toString());
			}
			else{
				rst.put("file2", fileName2+" 파일 없음");
			}

			rtList.add(rst);
			rst = null;


			rtObj =  rtList.toArray();

			rtList = null;

			return rtObj;

		} catch (Exception exception) {
			chkFile = null;
			in1.close();
			if ((exception instanceof sun.io.MalformedInputException)){
				throw new Exception("로그확인 실패 하였습니다.");
			}else{
				exception.printStackTrace();
				ecamsLogger.error("## Cmr5300.getFileText() Exception START ##");
				ecamsLogger.error("## Error DESC : ", exception);
				ecamsLogger.error("## Cmr5300.getFileText() Exception END ##");
				throw exception;
			}
		}finally{
			if (strQuery1 != null) 	strQuery1 = null;
			if (rtObj != null)	rtObj = null;
		}
	}//end of while-loop statement
}
