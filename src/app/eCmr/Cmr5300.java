package app.eCmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import app.common.LoggableStatement;
import app.common.SystemPath;
import app.file.FileMake;
import app.file.Gzip;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class Cmr5300 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

	/**
	 * <PRE>
	 * 1. MethodName	: getFileText
	 * 2. ClassName		: Cmr5300
	 * 3. Commnet			: 소스의 버전에 맞게 bin/tmp 파일 임시저장 또는 임시저장된 내용을 화면에 출력
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 22. 오전 9:56:53
	 * </PRE>
	 * 		@return String
	 * 		@param ItemId
	 * 		@param Version
	 * 		@param AcptNo
	 * 		@param ynDocFile : 산출물문서 여부(true:산출물)
	 * 		@return
	 * 		@throws Exception
	 */
	public String getFileText(String UserId,String ItemId,String Version,String AcptNo,boolean ynDocFile) throws Exception {
		Connection        conn        		= null;
		PreparedStatement pstmt       		= null;
		ResultSet         rs          		= null;
		StringBuffer      strQuery    		= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		String  shFileName = "";
		String	fileName = "";
		BufferedReader in1 = null;
		String	readline1 = "";
		String  strParm = "";
		int     ret = 0;

		try {
			
			if (Version.equals("")){
				if (AcptNo.substring(4,6).equals("04")) {
					conn = connectionContext.getConnection();
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
					conn.close();
					conn = null;
					pstmt = null;
					rs = null;
				}
			}
			Cmr0200 cmr0200 = new Cmr0200();
			//ecamsLogger.debug("#########      ItemId : " + ItemId + "  Version : " + Version + "  AcptNo : " + AcptNo);
			if (Version.equals("DEV")) {
				strParm = "./ecams_gensrc DEVSVR " + ItemId + " " + ItemId +"_gensrc.file" + " " + Version;
			} else if (!Version.equals("")){
				strParm = "./ecams_gensrc CMR0025 " + ItemId + " " + ItemId +"_gensrc.file" + " " + Version;
			}
			else{
				strParm = "./ecams_gensrc CMR0027 " + ItemId + " " + ItemId +"_gensrc.file" + " " + AcptNo;
			}
			shFileName = UserId+"apcmd.sh";
			ret = cmr0200.execShell(shFileName, strParm, false);
			if (ret != 0) {
				throw new Exception("해당 소스 생성  실패. run=["+strParm +"]" + " return=[" + ret + "]" );
			}

			//8859_1, MS949, UTF-8
			SystemPath cTempGet = new SystemPath();
			String tmpPath = cTempGet.getTmpDir("99");
			fileName = tmpPath + "/" + ItemId +"_gensrc.file";
			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "MS949"));

			strQuery.setLength(0);
			int i = 0;
			String strLine = "";
			while( (readline1 = in1.readLine()) != null ){
				strLine = String.format("%5d", ++i);
				strQuery.append(strLine+" " + readline1+"\n");
			}
			in1.close();

			in1 = null;

			return strQuery.toString();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5300.getFileText() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	        if (exception instanceof sun.io.MalformedInputException){
				in1.close();
				in1 = null;
				return strQuery.toString();
	   		}else{
	   			throw exception;
			}
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getFileText() Exception END ##");
			throw exception;
		}finally{
			if (in1 != null) in1 = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5300.getFileText() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of while-loop statement

	
	/**
	 * <PRE>
	 * 1. MethodName	: getFileText_java
	 * 2. ClassName		: Cmr5300
	 * 3. Commnet			: 소스의 버전에 맞게 bin/tmp 파일 임시저장 또는 임시저장된 내용을 화면에 출력(자바버전)
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 22. 오전 9:56:53
	 * </PRE>
	 * 		@return String
	 * 		@param ItemId
	 * 		@param Version
	 * 		@param AcptNo
	 * 		@param ynDocFile : 산출물문서 여부(true:산출물)
	 * 		@return
	 * 		@throws Exception
	 */
	public String getFileText_java(String UserId,String ItemId,String Version,String AcptNo,boolean ynDocFile) throws Exception {
		Connection        conn        		= null;
		PreparedStatement pstmt       		= null;
		ResultSet         rs          		= null;
		StringBuffer      strQuery    		= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		BufferedReader in1 = null;
		String	readline1 = "";

		try {
			
			conn = connectionContext.getConnection();
			
			if (Version.equals("")){
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
			
			FileMake filemake = new FileMake();
			SystemPath systemPath = new SystemPath();
			//소스비교결과[88] + 사용자ID 를 이용해서 디렉토리 설정
			String tmpPath = systemPath.getTmpDir_conn("88",conn) + "/" + UserId;
			//파일명
			String fileName = "";
			File filez = null;

			//임시저장할 폴더 없으면 만들기
			filez = new File(tmpPath);
			if ( !filez.exists() ){
				filez.mkdirs();
			}
			filez = null;
			
			
			ecamsLogger.error("#########      ItemId : " + ItemId + "  Version : " + Version + "  AcptNo : " + AcptNo);
			if ( "DEV".equals(Version) ) {
				
				//strParm = "./ecams_gensrc DEVSVR " + ItemId + " " + ItemId +"_gensrc.file" + " " + Version;
				//개발서버에서 가져와 파일 다운로드
				//1. itemid 가지고 프로그램명 조회 그리고 개발서버 정보를 조회하고
				//2. zen을 이용해서 파일을 형상서버로 가져오고
				//3. 파일 읽어서 화면에 출력
				String dirPath = "";
				String[] chkAry;
				String RSRCNAME = "";
				fileName = ItemId + "." + Version;
				
				strQuery.setLength(0);
				strQuery.append("SELECT  CR_RSRCNAME \n");
				strQuery.append("       ,(SELECT CM_DIRPATH FROM CMM0070 WHERE CM_SYSCD=CMR0020.CR_SYSCD AND CM_DSNCD=CMR0020.CR_DSNCD) DIRPATH \n");
				strQuery.append("  FROM CMR0020 \n");
				strQuery.append(" WHERE CR_ITEMID = ? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemId);
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					RSRCNAME = rs.getString("CR_RSRCNAME");
					dirPath = rs.getString("DIRPATH");
				} else {
					throw new IOException("FILE INFO NULL ERROR[Cmr5300.getFileText_java CMR0020]");
				}
				rs.close();
				pstmt.close();
				
				
				strQuery.setLength(0);
				strQuery.append("SELECT  DISTINCT \n");
				strQuery.append("        A.CM_SVRIP \n");
				strQuery.append("      , A.CM_PORTNO \n");
				strQuery.append("      , A.CM_SEQNO \n");
				strQuery.append("      , C.CR_RSRCCD \n");
				strQuery.append("      , C.CR_SYSCD \n");
				strQuery.append("      , C.CR_RSRCNAME \n");
				strQuery.append("      , D.CM_DIRPATH \n");
				strQuery.append("  FROM  CMM0031 A \n");
				strQuery.append("      , CMM0038 B \n");
				strQuery.append("      , CMR0020 C \n");
				strQuery.append("      , CMM0070 D \n");
				strQuery.append(" WHERE  C.CR_ITEMID = :szItemID \n");
				strQuery.append("   AND  A.CM_SVRCD  = '01' \n");
				strQuery.append("   AND  A.CM_CLOSEDT IS NULL \n");
				strQuery.append("   AND  A.CM_SVRSTOP = 'N' \n");
				strQuery.append("   AND  C.CR_SYSCD  = A.CM_SYSCD \n");
				strQuery.append("   AND  C.CR_RSRCCD = B.CM_RSRCCD \n");
				strQuery.append("   AND  A.CM_SYSCD = B.CM_SYSCD \n");
				strQuery.append("   AND  A.CM_SVRCD = B.CM_SVRCD \n");
				strQuery.append("   AND  A.CM_SEQNO = B.CM_SEQNO \n");
				strQuery.append("   AND  C.CR_SYSCD = D.CM_SYSCD \n");
				strQuery.append("   AND  C.CR_DSNCD = D.CM_DSNCD \n");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemId);
				pstmt.setInt(2, Integer.parseInt(Version) );
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					
					OutputStreamWriter       writer = null;
					Runtime                  run         = null;
					Process                  p           = null;
					
					String binpath = systemPath.getTmpDir_conn("14",conn);
					String exFilechk = systemPath.getTmpDir_conn("99",conn) + "/File_exists_chk.sh";
					String getFile = systemPath.getTmpDir_conn("99",conn) + "/File_getToServer.sh";
					
					//임시 sh 파일 없음 생성
					filez = new File(exFilechk);
					if ( !filez.exists() ){
						filez.createNewFile();
					}
					filez = null;
					
					writer = new OutputStreamWriter( new FileOutputStream(exFilechk));
					writer.write("cd " + binpath + "\n");
					writer.write("./zen " + rs.getString("CM_SVRIP") + " " + rs.getString("CM_PORTNO") + " 0 I " + dirPath+"/"+RSRCNAME + "\n");
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

					if ( p.exitValue() != 0 ) {
						writer = null;
						run    = null;
						p      = null;
						
						throw new IOException("DEV SERVER, FILE NOT FOUND ERROR[Cmr5300.getFileText_java ZEN I]");
					} else {
						
						//임시 저장 파일 있음 삭제
						filez = new File(tmpPath+"/"+fileName);
						if ( filez.exists() ){
							filez.delete();
						}
						filez = null;
						
						//임시 sh 파일 없음 생성
						filez = new File(getFile);
						if ( !filez.exists() ){
							filez.createNewFile();
						}
						filez = null;
						
						writer = new OutputStreamWriter( new FileOutputStream(getFile));
						writer.write("cd " + binpath + "\n");
						writer.write("./zen " + rs.getString("CM_SVRIP") + " " + rs.getString("CM_PORTNO") + " 0 G " + tmpPath+"/"+fileName + " " + dirPath+"/"+RSRCNAME + "\n");
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

						if ( p.exitValue() != 0 ) {
							writer = null;
							run    = null;
							p      = null;
							
							throw new IOException("DEV SERVER, FILE GET ERROR[Cmr5300.getFileText_java ZEN G]");
						}
						
					}
					
					writer = null;
					run    = null;
					p      = null;
					
					//임시저장되어 있는 파일 삭제
					filez = new File(exFilechk);
					if ( filez.exists() ){
						filez.delete();
					}
					filez = null;
					
					//임시저장되어 있는 파일 삭제
					filez = new File(getFile);
					if ( filez.exists() ){
						filez.delete();
					}
					filez = null;
					
				}
				rs.close();
				pstmt.close();
				
				
			} else if ( !Version.equals("") ){
				
				//파일 만들기
				fileName = ItemId + "." + Version;
				filez = new File(tmpPath + "/" + fileName);
				
				if ( !filez.exists() || filez.length()==0 ){
					filez.delete();
					filez.createNewFile();
					
					//DB에서 blob 쿼리하기
					strQuery.setLength(0);
					strQuery.append("SELECT CR_FILE FROM CMR0025 WHERE CR_ITEMID = ? AND CR_VER = ? ");
					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, ItemId);
					pstmt.setInt(2, Integer.parseInt(Version) );
//					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if ( rs.next() ){
						
						if ( !filemake.fileMake(fileName, tmpPath, rs.getBlob("cr_file"), "04") ){
							throw new IOException("FILE MAKE ERROR[Cmr5300.getFileText_java CMR0025]");
						}
						
					} else {
						throw new IOException("FILE SQL(CMR0025.CR_FILE is null) ERROR[Cmr5300.getFileText_java], itemid:"+ItemId+":Version:"+Version);
					}
					rs.close();
					pstmt.close();
				}
				filez = null;
				
			}
			else{
				
				//파일 만들기
				fileName = ItemId + "." + AcptNo;
				filez = new File(tmpPath + "/" + fileName);
				
				if ( !filez.exists() || filez.length()==0 ){
					filez.delete();
					filez.createNewFile();
					
					//DB에서 blob 쿼리하기
					strQuery.setLength(0);
					strQuery.append("SELECT CR_FILE FROM CMR0027 WHERE CR_ITEMID = ? AND CR_ACPTNO = ? ");
					pstmt = conn.prepareStatement(strQuery.toString());
//					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, ItemId );
					pstmt.setString(2, AcptNo );
//					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if ( rs.next() ){
						
						if ( !filemake.fileMake(fileName, tmpPath, rs.getBlob("cr_file"), "04") ){
							throw new IOException("FILE MAKE ERROR[Cmr5300.getFileText_java CMR0027]");
						}
						
					} else {
						throw new IOException("FILE SQL(CMR0027.CR_FILE is null) ERROR[Cmr5300.getFileText_java], itemid:"+ItemId+":Version:"+Version);
					}
					rs.close();
					pstmt.close();
				}
				filez = null;
				
			}

			
			//8859_1, MS949, UTF-8
			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(tmpPath + "/" + fileName), "MS949"));

			strQuery.setLength(0);
			int i = 0;
			String strLine = "";
			while( (readline1 = in1.readLine()) != null ){
				strLine = String.format("%5d", ++i);
				strQuery.append(strLine+" " + readline1+"\n");
			}
			in1.close();
			
			systemPath = null;
			in1 = null;

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			return strQuery.toString();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText_java() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5300.getFileText_java() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	        if (exception instanceof sun.io.MalformedInputException){
				in1.close();
				in1 = null;
				return strQuery.toString();
	   		}else{
	   			throw exception;
			}
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText_java() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getFileText_java() Exception END ##");
			throw exception;
		}finally{
			if (in1 != null) in1 = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5300.getFileText_java() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of while-loop statement
	
	/**
	 * <PRE>
	 * 1. MethodName	: getFileText_java
	 * 2. ClassName		: Cmr5300
	 * 3. Commnet			: 소스의 버전에 맞게 bin/tmp 파일 임시저장 또는 임시저장된 내용을 화면에 출력(자바버전)
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 22. 오전 9:56:53
	 * </PRE>
	 * 		@return String
	 * 		@param ItemId
	 * 		@param Version
	 * 		@param AcptNo
	 * 		@param ynDocFile : 산출물문서 여부(true:산출물)
	 * 		@return
	 * 		@throws Exception
	 */
	public String getFileText_java_kicc(String UserId,String ItemId,String Version,String AcptNo,boolean ynDocFile) throws Exception {
		Connection        conn        		= null;
		PreparedStatement pstmt       		= null;
		ResultSet         rs          		= null;
		StringBuffer      strQuery    		= new StringBuffer();
		ConnectionContext connectionContext = new ConnectionResource();

		BufferedReader in1 = null;
		String	readline1 = "";

		try {
			
			conn = connectionContext.getConnection();
			
			ecamsLogger.error("#########      ItemId :" + ItemId + ", Version :" + Version + ", AcptNo :" + AcptNo);
			
			if (Version.equals("")){
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
			FileMake filemake = new FileMake();
			SystemPath systemPath = new SystemPath();
			//소스비교결과[88] + 사용자ID 를 이용해서 디렉토리 설정
			String tmpPath = systemPath.getTmpDir_conn("88",conn) + File.separator + UserId;
			//파일명
			String fileName = "";

			File filez = new File(tmpPath);
			//임시저장할 폴더 없으면 만들기
			filez = new File(tmpPath);
			if ( !filez.exists() ){
				filez.mkdirs();
			}
			filez = null;
			
			if ( "DEV".equals(Version) ) {
				
				//strParm = "./ecams_gensrc DEVSVR " + ItemId + " " + ItemId +"_gensrc.file" + " " + Version;
				//개발서버에서 가져와 파일 다운로드
				//1. itemid 가지고 프로그램명 조회 그리고 개발서버 정보를 조회하고
				//2. zen을 이용해서 파일을 형상서버로 가져오고
				//3. 파일 읽어서 화면에 출력
				String dirPath = "";
				String[] chkAry;
				String RSRCNAME = "";
				fileName = ItemId + "." + Version;
				
				strQuery.setLength(0);
				strQuery.append("SELECT  CR_RSRCNAME \n");
				strQuery.append("       ,(SELECT CM_DIRPATH FROM CMM0070 WHERE CM_SYSCD=CMR0020.CR_SYSCD AND CM_DSNCD=CMR0020.CR_DSNCD) DIRPATH \n");
				strQuery.append("  FROM CMR0020 \n");
				strQuery.append(" WHERE CR_ITEMID = ? \n");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemId);
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					RSRCNAME = rs.getString("CR_RSRCNAME");
					dirPath = rs.getString("DIRPATH");
				} else {
					throw new IOException("FILE INFO NULL ERROR[Cmr5300.getFileText_java CMR0020]");
				}
				rs.close();
				pstmt.close();
				
				
				strQuery.setLength(0);
				strQuery.append("SELECT  DISTINCT \n");
				strQuery.append("        A.CM_SVRIP \n");
				strQuery.append("      , A.CM_PORTNO \n");
				strQuery.append("      , A.CM_SEQNO \n");
				strQuery.append("      , C.CR_RSRCCD \n");
				strQuery.append("      , C.CR_SYSCD \n");
				strQuery.append("      , C.CR_RSRCNAME \n");
				strQuery.append("      , D.CM_DIRPATH \n");
				strQuery.append("  FROM  CMM0031 A \n");
				strQuery.append("      , CMM0038 B \n");
				strQuery.append("      , CMR0020 C \n");
				strQuery.append("      , CMM0070 D \n");
				strQuery.append(" WHERE  C.CR_ITEMID = :szItemID \n");
				strQuery.append("   AND  A.CM_SVRCD  = '01' \n");
				strQuery.append("   AND  A.CM_CLOSEDT IS NULL \n");
				strQuery.append("   AND  A.CM_SVRSTOP = 'N' \n");
				strQuery.append("   AND  C.CR_SYSCD  = A.CM_SYSCD \n");
				strQuery.append("   AND  C.CR_RSRCCD = B.CM_RSRCCD \n");
				strQuery.append("   AND  A.CM_SYSCD = B.CM_SYSCD \n");
				strQuery.append("   AND  A.CM_SVRCD = B.CM_SVRCD \n");
				strQuery.append("   AND  A.CM_SEQNO = B.CM_SEQNO \n");
				strQuery.append("   AND  C.CR_SYSCD = D.CM_SYSCD \n");
				strQuery.append("   AND  C.CR_DSNCD = D.CM_DSNCD \n");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemId);
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					
					OutputStreamWriter       writer = null;
					Runtime                  run         = null;
					Process                  p           = null;
					
					String binpath = systemPath.getTmpDir_conn("14",conn);
					String exFilechk = systemPath.getTmpDir_conn("99",conn) + File.separator + "File_exists_chk.sh";
					String getFile = systemPath.getTmpDir_conn("99",conn) + File.separator + "File_getToServer.sh";
					
					//임시 sh 파일 없음 생성
					filez = new File(exFilechk);
					if ( !filez.exists() ){
						filez.createNewFile();
					}
					filez = null;
					
					writer = new OutputStreamWriter( new FileOutputStream(exFilechk));
					writer.write("cd " + binpath + "\n");
					writer.write("./zen " + rs.getString("CM_SVRIP") + " " + rs.getString("CM_PORTNO") + " 0 I " + dirPath+"/"+RSRCNAME + "\n");
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

					if ( p.exitValue() != 0 ) {
						writer = null;
						run    = null;
						p      = null;
						
						throw new IOException("DEV SERVER, FILE NOT FOUND ERROR[Cmr5300.getFileText_java ZEN I]");
					} else {
						
						//임시 저장 파일 있음 삭제
						filez = new File(tmpPath + File.separator + fileName);
						if ( filez.exists() ){
							filez.delete();
						}
						filez = null;
						
						//임시 sh 파일 없음 생성
						filez = new File(getFile);
						if ( !filez.exists() ){
							filez.createNewFile();
						}
						filez = null;
						
						writer = new OutputStreamWriter( new FileOutputStream(getFile));
						writer.write("cd " + binpath + "\n");
						writer.write("./zen " + rs.getString("CM_SVRIP") + " " + rs.getString("CM_PORTNO") + " 0 G " + tmpPath+"/"+fileName + " " + dirPath+"/"+RSRCNAME + "\n");
						writer.write("exit $?\n");
						writer.close();

						chkAry = new String[3];
						chkAry[0] = "chmod";
						chkAry[1] = "777";
						chkAry[2] = getFile;
						run = Runtime.getRuntime();
						p = run.exec(chkAry);
						chkAry = null;
						p.waitFor();

						run = Runtime.getRuntime();
						chkAry = new String[2];
						chkAry[0] = "/bin/sh";
						chkAry[1] = getFile;
						p = run.exec(chkAry);
						chkAry = null;
						p.waitFor();

						if ( p.exitValue() != 0 ) {
							writer = null;
							run    = null;
							p      = null;
							
							throw new IOException("DEV SERVER, FILE GET ERROR[Cmr5300.getFileText_java ZEN G]");
						}
						
					}
					
					writer = null;
					run    = null;
					p      = null;
					
					//임시저장되어 있는 파일 삭제
					filez = new File(exFilechk);
					if ( filez.exists() ){
						filez.delete();
					}
					filez = null;
					
					//임시저장되어 있는 파일 삭제
					filez = new File(getFile);
					if ( filez.exists() ){
						filez.delete();
					}
					filez = null;
					
				}
				rs.close();
				pstmt.close();
				
				
			} else if ( !Version.equals("") ){
				
				//파일 만들기
				fileName = ItemId + "." + Version;
				filez = new File(tmpPath + File.separator + fileName);
				
				if ( !filez.exists() || filez.length()==0 ){
					filez.delete();
					filez.createNewFile();
					
					//DB에서 blob 쿼리하기
					strQuery.setLength(0);
					strQuery.append("SELECT CR_FILE FROM CMR0025 WHERE CR_ITEMID = ? AND CR_VER = ? ");
//					pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, ItemId);
					pstmt.setInt(2, Integer.parseInt(Version) );
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if ( rs.next() ){
						
						if ( !filemake.fileMake(fileName, tmpPath, rs.getBlob("cr_file"), "04") ){
							throw new IOException("FILE MAKE ERROR[Cmr5300.getFileText_java CMR0025]");
						}
						
					} else {
						throw new IOException("FILE SQL(CMR0025.CR_FILE is null) ERROR[Cmr5300.getFileText_java], itemid:"+ItemId+":Version:"+Version);
					}
					rs.close();
					pstmt.close();
				}
				filez = null;
				
			}
			else{
				
				//파일 만들기
				fileName = ItemId + "." + AcptNo;
				filez = new File(tmpPath + File.separator + fileName);
				
				if ( !filez.exists() || filez.length()==0 ){
					filez.delete();
					filez.createNewFile();
					
					//DB에서 blob 쿼리하기
					strQuery.setLength(0);
					strQuery.append("SELECT CR_FILE FROM CMR0027 WHERE CR_ITEMID = ? AND CR_ACPTNO = ? ");
					//pstmt = conn.prepareStatement(strQuery.toString());
					pstmt = new LoggableStatement(conn, strQuery.toString());
					pstmt.setString(1, ItemId );
					pstmt.setString(2, AcptNo );
					ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
					rs = pstmt.executeQuery();
					if ( rs.next() ){
						
						if ( !filemake.fileMake(fileName, tmpPath, rs.getBlob("cr_file"), "04") ){
							throw new IOException("FILE MAKE ERROR[Cmr5300.getFileText_java CMR0027]");
						}
						
					} else {
						throw new IOException("FILE SQL(CMR0027.CR_FILE is null) ERROR[Cmr5300.getFileText_java], itemid:"+ItemId+":Version:"+Version);
					}
					rs.close();
					pstmt.close();
				}
				filez = null;
				
			}

			
			//8859_1, MS949, UTF-8
			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(tmpPath + "/" + fileName), "MS949"));

			strQuery.setLength(0);
			int i = 0;
			String strLine = "";
			while( (readline1 = in1.readLine()) != null ){
//				strLine = String.format("%5d", ++i);
//				strQuery.append(strLine+" " + readline1+"\n");
				strQuery.append(readline1+"\n");
			}
			in1.close();
			
			systemPath = null;
			in1 = null;

			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			
			ecamsLogger.error("tmpPath:"+tmpPath);
			
			return strQuery.toString();

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText_java() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5300.getFileText_java() SQLException END ##");
			throw sqlexception;
		} catch (IOException exception) {
	        exception.printStackTrace();
	        ecamsLogger.error("## Error IOException : ", exception);
	        if (exception instanceof sun.io.MalformedInputException){
				in1.close();
				in1 = null;
				return strQuery.toString();
	   		}else{
	   			throw exception;
			}
	    } catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileText_java() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getFileText_java() Exception END ##");
			throw exception;
		}finally{
			if (in1 != null) in1 = null;
			if (strQuery != null) 	strQuery = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5300.getFileText_java() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}//end of while-loop statement
	
	
	/**
	 * <PRE>
	 * 1. MethodName	: getFileVer
	 * 2. ClassName		: Cmr5300
	 * 3. Commnet			: 소스의 버전별 기본정보 리스트 출력
	 * 4. 작성자				: no name
	 * 5. 작성일				: 2010. 12. 22. 오전 9:58:59
	 * </PRE>
	 * 		@return Object[]
	 * 		@param ItemID
	 * 		@return
	 * 		@throws SQLException
	 * 		@throws Exception
	 */
	public Object[] getFileVer(String ItemID) throws SQLException, Exception {
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
			strQuery.append("select to_char(a.cr_opendate,'yyyy/mm/dd hh24:mi') as opendt, \n");
			strQuery.append("       a.cr_lstver,a.cr_status,a.cr_rsrcname,b.cm_info,       \n");
			strQuery.append("       d.cm_dirpath,g.cm_sysmsg,                              \n");
			strQuery.append("       to_char(sysdate,'yyyy/mm/dd hh24:mi') as nowdt         \n");
			strQuery.append("from cmm0036 b,cmr0020 a ,cmm0070 d,cmm0030 g                 \n");
			strQuery.append("where a.cr_itemid= ?                                          \n");
			strQuery.append("and a.cr_syscd=b.cm_syscd and a.cr_rsrccd=b.cm_rsrccd         \n");
			strQuery.append("and a.cr_syscd=d.cm_syscd and a.cr_dsncd=d.cm_dsncd           \n");
			strQuery.append("and a.cr_syscd=g.cm_syscd                                     \n");

			pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, ItemID);

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			if (rs.next()){
				if (rs.getString("cm_info").substring(11,12).equals("0")){
					throw new Exception("버전관리를 하지 않는 파일입니다. 소스View를 하실수 없습니다.");
				}

				if (rs.getString("cr_lstver").equals("0") && rs.getString("cr_status").equals("3") && rs.getString("cm_info").substring(44,45).equals("1")) {
					throw new Exception("현재 개발 중인 소스 입니다. 체크인 이후에 소스View를 하실수 있습니다.");
				}
                /*
				if (rs.getString("cr_status").equals("A")){
					strQuery.setLength(0);
					strQuery.append("select b.cr_acptno,b.cr_sayu,b.cr_acptdate,c.cm_info \n");
					strQuery.append("  from cmr0027 a,cmr1000 b,cmm0036 c,cmr1010 d \n");
					strQuery.append(" where a.cr_itemid= ? and b.cr_status<>'3' \n");
					strQuery.append("   and a.cr_acptno=b.cr_acptno \n");
					strQuery.append("   and d.cr_acptno=b.cr_acptno \n");
					strQuery.append("   and d.cr_syscd=c.cm_syscd   \n");
					strQuery.append("   and d.cr_rsrccd=c.cm_rsrccd \n");
					strQuery.append(" order by b.cr_acptdate desc   \n");

					pstmt2 = conn.prepareStatement(strQuery.toString());
					//pstmt2 =  new LoggableStatement(conn, strQuery.toString());
					pstmt2.setString(1, ItemID);
					////ecamsLogger.error(((LoggableStatement)pstmt2).getQueryString());
					rs2 = pstmt2.executeQuery();

					if (rs2.next()){
						rst = new HashMap<String,String>();
						rst.put("cr_itemid", ItemID);
						rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
						rst.put("cm_dirpath", rs.getString("cm_dirpath"));
						rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
						rst.put("filedt",rs.getString("opendt"));
						rst.put("cr_acptno",rs2.getString("cr_acptno"));
						if (rs2.getString("cr_sayu") != null) rst.put("cr_sayu", rs2.getString("cr_sayu"));
						rst.put("cr_ver", "");
						rst.put("cm_info", rs.getString("cm_info"));
						rst.put("labelmsg", "체크인 중:" + rs.getString("nowdt")+" / 신청번호:"+rs2.getString("cr_acptno"));
						rtList.add(rst);
						rst = null;
					}

					rs2.close();
					pstmt2.close();
					rs2 = null;
					pstmt2 = null;
				}
				*/
			}
			else{
				throw new Exception("해당 자원의 정보가 없습니다1.");
			}

			rs.close();
			pstmt.close();

			strQuery.setLength(0);
			strQuery.append("SELECT b.cr_acptno,b.cr_ver,                                 \n");
			strQuery.append("       to_char(h.cr_acptdate,'yyyy/mm/dd hh24:mi') as opendt,\n");
			strQuery.append("       a.cr_rsrcname,d.cm_dirpath,                           \n");
			strQuery.append("       g.cm_sysmsg,h.cr_qrycd,c.cr_sayu,e.cm_info            \n");
			strQuery.append("  FROM cmr0025 b,cmr0020 a,cmm0070 d,cmm0030 g,cmr0021 h,cmr1000 c,cmm0036 e \n");
			strQuery.append(" where a.cr_itemid= ?                                        \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid                               \n");
			strQuery.append("   and b.cr_itemid=h.cr_itemid and b.cr_acptno=h.cr_acptno   \n");
			strQuery.append("   and a.cr_syscd=d.cm_syscd and a.cr_dsncd=d.cm_dsncd       \n");
			strQuery.append("   and a.cr_syscd=g.cm_syscd and h.cr_acptno=c.cr_acptno     \n");
			strQuery.append("   and g.cm_syscd=e.cm_syscd and a.cr_rsrccd=e.cm_rsrccd      \n");
			strQuery.append(" order by opendt desc                                         \n");

            pstmt = conn.prepareStatement(strQuery.toString());
			//pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, ItemID);
            //pstmt.setString(2, ItemID);

            ////ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();


            while(rs.next()){
				rst = new HashMap<String,String>();
				rst.put("cr_itemid", ItemID);
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("cr_ver", rs.getString("cr_ver"));
				rst.put("cm_info", rs.getString("cm_info"));

				rst.put("filedt",rs.getString("opendt"));
				rst.put("labelmsg", "체크인:" +rs.getString("opendt")+" / 버전:"+rs.getString("cr_ver"));
				if (rs.getString("cr_sayu") != null) rst.put("cr_sayu",rs.getString("cr_sayu"));
				else rst.put("cr_sayu", "");
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
			ecamsLogger.error("## Cmr5300.getFileList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5300.getFileList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getFileList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getFileList() Exception END ##");
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
					ecamsLogger.error("## Cmr5300.getFileList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}
	}

	public Object[] getReqList(String AcptNo,String UserId) throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>		  rtList	  = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
		ConnectionContext connectionContext = new ConnectionResource();
		boolean           findSw = false;
//		String            strAcpt = AcptNo;
		try {
			conn = connectionContext.getConnection();
			strQuery.setLength(0);

			strQuery.append("select a.cr_rsrcname,a.cr_version,a.cr_itemid,a.cr_befver,\n");
			strQuery.append("       a.cr_qrycd,a.cr_status,b.cm_dirpath,c.cm_info,    \n");
			strQuery.append("       d.cr_rsrcname basename,f.cm_codename,a.cr_confno, \n");
			strQuery.append("       a.cr_prcdate,e.cr_acptno,a.cr_realbefver          \n");
			strQuery.append("  from cmm0020 f,cmm0036 c,cmm0070 b,cmr1010 a,cmr1010 d,cmr1000 e \n");
			strQuery.append(" where e.cr_acptno=?                                     \n");
			strQuery.append("   and d.cr_acptno=?                                     \n");
			strQuery.append("   and e.cr_acptno=a.cr_acptno                           \n");
			strQuery.append("   and a.cr_status<>'3'                                  \n");
			strQuery.append("   and a.cr_qrycd in ('03','04')                         \n");
			strQuery.append("   and a.cr_syscd=b.cm_syscd and a.cr_dsncd=b.cm_dsncd   \n");
			strQuery.append("   and a.cr_syscd=c.cm_syscd and a.cr_rsrccd=c.cm_rsrccd \n");
			strQuery.append("   and substr(c.cm_info,10,1)='0'                        \n");
			strQuery.append("   and substr(c.cm_info,27,1)='0'                        \n");
			strQuery.append("   and substr(c.cm_info,46,1)='0'                        \n");
			strQuery.append("   and substr(c.cm_info,12,1)='1'                        \n");
			strQuery.append("   and a.cr_acptno=d.cr_acptno and a.cr_baseitem=d.cr_itemid\n");
			strQuery.append("   and f.cm_macode='CHECKIN' and f.cm_micode=a.cr_qrycd  \n");
			strQuery.append(" order by a.cr_baseitem,b.cm_dirpath,a.cr_rsrcname       \n");
            //pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());
        	pstmt.setString(1, AcptNo);
        	pstmt.setString(2, AcptNo);
			ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();
            rtList.clear();

			while (rs.next()){
				findSw = true;

				if (findSw == true) {
					rst = new HashMap<String,String>();
					rst.put("cr_itemid", rs.getString("cr_itemid"));
					rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
					rst.put("cm_dirpath", rs.getString("cm_dirpath"));
					if (AcptNo.substring(4,6).equals("03") || AcptNo.substring(4,6).equals("04") || AcptNo.substring(4,6).equals("06")) {
						rst.put("cr_befver", rs.getString("cr_realbefver"));
						rst.put("cr_ver", rs.getString("cr_befver"));
						rst.put("cr_aftver", rs.getString("cr_befver"));
						rst.put("cr_status", "9");
					} else {
						rst.put("cr_befver", rs.getString("cr_befver"));
						rst.put("cr_ver", rs.getString("cr_version"));
						rst.put("cr_aftver", rs.getString("cr_version"));
						rst.put("cr_status", rs.getString("cr_status"));
					}
					rst.put("cm_info", rs.getString("cm_info"));
					rst.put("basename", rs.getString("basename"));
					
					rst.put("cr_qrycd", rs.getString("cr_qrycd"));
					rst.put("cm_codename", rs.getString("cm_codename"));
					findSw = false;
					rtList.add(rst);
					rst = null;
				}
			}
			rs.close();
			pstmt.close();
			conn.close();
			conn = null;
			pstmt = null;
			rs = null;
			return rtList.toArray();
			//end of while-loop statement

		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getReqList() SQLException START ##");
			ecamsLogger.error("## Error DESC : ", sqlexception);
			ecamsLogger.error("## Cmr5300.getReqList() SQLException END ##");
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5300.getReqList() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getReqList() Exception END ##");
			throw exception;
		}finally{
			if (strQuery != null) 	strQuery = null;
			if (rtList != null)	rtList = null;
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (rs2 != null)    try{rs2.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt2 != null) try{pstmt2.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					ecamsLogger.error("## Cmr5300.getReqList() connection release exception ##");
					ex3.printStackTrace();
				}
			}
		}

	}
}
