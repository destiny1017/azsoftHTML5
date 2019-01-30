/*****************************************************************************************
	1. program ID	: FtpHandler.java
	2. create date	: 2008.09. 21
	3. auth		    : no name
	4. update date	: 
	5. auth		    : 
	6. description	: FtpHandler
*****************************************************************************************/

package app.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTP;

import org.apache.log4j.Logger;
import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

public class FtpHandler {
	
	Logger ecamsLogger = EcamsLogger.getLoggerInstance();
	
	private static String sServer = ""; //서버 아이피
	private static int iPort = 21;
	private static String sId = ""; //사용자 아이디
	private static String sPassword = ""; //비밀번호
	private static String clientPath = "";
	private static String serverPath = "";
	private static int errCnt = 0;
	FTPClient ftpClient;
 
	// 서버로 연결
	private void connect() {
		try {
			ftpClient.connect(sServer, iPort);
			int reply;
			//연결 시도후, 성공했는지 응답 코드 확인
			reply = ftpClient.getReplyCode();
	    
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				ecamsLogger.error("서버로부터 연결을 거부당했습니다");
			}
			
		}catch (IOException ioe) {
			if(ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch(IOException f) {
					++errCnt;
				}
			}
			ecamsLogger.error("서버에 연결할 수 없습니다");
		}
	}
   
	// 계정과 패스워드로 로그인
	private boolean login() {
		
		try {
			this.connect();
			return ftpClient.login(sId, sPassword);
		}catch (IOException ioe) {
			++errCnt;
			ecamsLogger.error("서버에 로그인 하지 못했습니다");
		}
		return false;
	}
	
	// 서버로부터 로그아웃 => 090922 현재 테스트 필요함.
	private boolean logout() {
	
		try {
			return ftpClient.logout();
		}catch (IOException ioe) {
			++errCnt;
			ecamsLogger.error("로그아웃이 하지 못했습니다");
		}
		return false;
	}
	   
	// FTP의 ls 명령, 모든 파일 리스트를 가져온다
	private FTPFile[] list(String tagetPath) {
	
		FTPFile[] files = null;
		try {
			files = this.ftpClient.listFiles(tagetPath);
			//"파일이름: " + file.getName() + " 사이즈 : " + file.getSize()
			return files;
		}catch (IOException ioe) {
			++errCnt;
			ecamsLogger.error("서버로부터 파일 리스트를 가져오지 못했습니다");
		}
		return null;
	}
	
	// 파일을 전송(서버 -> 로컬PC) 받는다
	private boolean get(String targetfullName, String name) {
		
		boolean flag = false;
		FileOutputStream output = null;
		File local = null;
		
		try {
			//받는 파일 생성 이 위치에 이 이름으로 파일 생성된다
		    local = new File(name);//DownDir + name
		    output = new FileOutputStream(local);
		    
		}catch (FileNotFoundException fnfe) {
			++errCnt;
			ecamsLogger.error("다운로드할 디렉토리가 없습니다");
			return flag;
		}
		
		//File file = new File(targetfullName);
		try {
			ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
			if (ftpClient.retrieveFile(targetfullName, output)) {
				flag = true;
			}
			output.close();
		}catch (IOException ioe) {
			++errCnt;
			ecamsLogger.error("파일을 다운로드 하지 못했습니다");
		}
		return flag;
	}
	
	// 파일을 전송(로컬PC -> 서버) 한다
	private boolean put(String fullName, String targetfullName) {
	
		boolean flag = false;
		InputStream input = null;
		File localFile = null;
		
		try {
			localFile = new File(fullName);
			input = new FileInputStream(localFile);
		}catch(FileNotFoundException e) {
			++errCnt;
			e.printStackTrace();
			return flag;
		}
		
		try {
			//targetfullName(경로+파일명)으로 파일 업로드
			//서버에 업로드할 동일한 파일명이 존재하면 덮어쓴다
			//if(ftpClient.storeFile(targetfullName, input)) {
			
			//서버에 업로드할 동일한 파일명이 존재하면 flase 리턴
			if(ftpClient.appendFile(targetfullName, input)) {
				flag = true;
			}else{
				ecamsLogger.error("서버에 동일한 파일이 존재합니다");
				flag = false;
			}
			input.close();
		}catch(IOException e) {
			++errCnt;
			ecamsLogger.error("파일을 전송하지 못했습니다");
			e.printStackTrace();
			return flag;
		}
		return flag;
	}
	
	// 서버 디렉토리 이동
	private void cd(String path) {
	    
		try {
			ftpClient.changeWorkingDirectory(path);
		}catch (IOException ioe) {
			++errCnt;
			ecamsLogger.error("폴더를 이동하지 못했습니다");
		}
	}
	
	// 서버로부터 연결을 닫기
	private void disconnect() {
	    
		try {
			ftpClient.disconnect();
		}catch (IOException ioe) {
			ioe.printStackTrace();
			++errCnt;
			ecamsLogger.error("FTP disconnect an error");
		}
	}
	
	// FTP 파일 타입 설정
	private void setFileType(int FileType) {
		try {
			ftpClient.setFileType(FileType);
		}catch(Exception e) {
			++errCnt;
			ecamsLogger.error("파일 타입을 설정하지 못했습니다");
		}      
	}
	
	// UpdateCmr1100
	public boolean UpdateCmr1100(Connection _conn,String AcptNo, 
			String SerNo) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		try {
        	strQuery.setLength(0);
        	strQuery.append("Update Cmr1100 set cr_errcd='0000' ");
            strQuery.append("where cr_acptno=? ");//AcptNo
            strQuery.append("  and CR_SERNO=? ");//serno 일련번호
        	pstmt = _conn.prepareStatement(strQuery.toString());
        	pstmt.setString(1,AcptNo);
        	pstmt.setString(2,SerNo);
        	pstmt.executeUpdate();
        	pstmt.close();
        	pstmt=null;
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			++errCnt;
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			++errCnt;
			throw exception;
		}
		return true;
	}
	
	// UpdateCmr9900_STR
	public boolean UpdateCmr9900_STR(Connection _conn,String AcptNo,
			String ConMSG,String SinCd) throws SQLException, Exception {
		PreparedStatement pstmt       = null;
		StringBuffer      strQuery    = new StringBuffer();

		try {
			strQuery.setLength(0);
			strQuery.append("Begin CMR9900_STR ( ");
			strQuery.append("?,?,'eCAMS자동처리','9',?,'1' ); End;");//AcptNo,ConMSG,SinCd
			pstmt = _conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, AcptNo);
			pstmt.setString(2, ConMSG);
			pstmt.setString(3, SinCd);
			pstmt.executeUpdate();
			pstmt.close();
        	
		} catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			++errCnt;
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			++errCnt;
			throw exception;
		}
		return true;
	}
	
	// FTP Up Load Handler GbnCd == cm_pathcd(cmm0012),Acptno == 신청번호,tblNm == 테이블명
	public int FtpUpLoad(String GbnCd,String AcptNo,String tblNm) 
		throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		try{
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select cm_path,cm_downip,cm_downusr,cm_downpass from cmm0012 ");
			strQuery.append("where cm_stno = 'ECAMS' ");
			strQuery.append("  and cm_pathcd = ? ");	//cm_pathcd
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, GbnCd);
    		rs = pstmt.executeQuery();
			if (rs.next()){
				sServer = rs.getString("cm_downip");
				sId = rs.getString("cm_downusr");
				sPassword = rs.getString("cm_downpass");
				serverPath = rs.getString("cm_path");
			}else {
				throw new Exception("코드["+ GbnCd +"]에 해당되는 정보가 없습니다.");
			}
			rs.close();
			pstmt.close();
			
			ftpClient = new FTPClient();
			//ftpClient.setControlEncoding("euc-kr");
			
			// 서버 연결
			connect();
			
			// 서버 로그인
			login();
			
			// 파일 타입 설정
			setFileType(FTP.BINARY_FILE_TYPE);//BINARY_FILE_TYPE 설
			
			if (tblNm.equals("Cmr1100")){//Cmr1100 == 산출물 관련 신청건
				strQuery.setLength(0);
				strQuery.append("Select a.cr_serno,a.cr_docid,REPLACE(a.cr_pcdir,'\','\\') as pcdir,a.cr_version,b.cr_docfile ");
				strQuery.append("from cmr1100 a, cmr0030 b ");
				strQuery.append("where a.cr_acptno = ? ");
				strQuery.append("  and b.cr_docid = a.cr_docid ");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
				rs = pstmt.executeQuery();
				String docid = "";
				String targetfullName = "";
				int version = 0;
				while (rs.next()){
					docid = rs.getString("cr_docid");
					version = Integer.parseInt(rs.getString("cr_version"));
					clientPath = rs.getString("pcdir") + "\\" + rs.getString("cr_docfile");
					targetfullName = serverPath + "/" + docid.substring(6,10) + "/" + docid.substring(0,6) + "/" + docid + "." + Integer.toString(version);
					
					ecamsLogger.debug("filename cmr1100   " +   clientPath + "\n");
					ecamsLogger.error("local_file cmr1100   " + targetfullName + "\n");
					
					if (put(clientPath,targetfullName))					
						//cmr1100 cr_errcd='0000' Handler
						UpdateCmr1100(conn,AcptNo,rs.getString("cr_serno"));
				}
				rs.close();
				pstmt.close();
				
				//cmr9900_STR  PROCEDURE Handler
				if (errCnt == 0)
					UpdateCmr9900_STR(conn,AcptNo,"SYSDUP",AcptNo.substring(6,8));
			}
			// 연결 종료
			disconnect();
			
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
		}catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			++errCnt;
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			++errCnt;
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					++errCnt;
					ex3.printStackTrace();
				}
			}
		}
		return errCnt;
	}
	
	public int FtpDownLoad(String GbnCd,String AcptNo,String tblNm) 
		throws SQLException, Exception {
		Connection        conn        = null;
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		
		ConnectionContext connectionContext = new ConnectionResource();
		try{
			conn = connectionContext.getConnection();
			strQuery.setLength(0);
			strQuery.append("select cm_path,cm_downip,cm_downusr,cm_downpass from cmm0012 ");
			strQuery.append("where cm_stno = 'ECAMS' ");
			strQuery.append("  and cm_pathcd = ? ");	//cm_pathcd
			pstmt = conn.prepareStatement(strQuery.toString());
			pstmt.setString(1, GbnCd);
			rs = pstmt.executeQuery();
			if (rs.next()){
				sServer = rs.getString("cm_downip");
				sId = rs.getString("cm_downusr");
				sPassword = rs.getString("cm_downpass");
				serverPath = rs.getString("cm_path");
			}else {
				throw new Exception("코드["+ GbnCd +"]에 해당되는 정보가 없습니다.");
			}
			rs.close();
			pstmt.close();
			
			ftpClient = new FTPClient();
			
			// 서버 연결
			connect();
			
			// 서버 로그인
			login();
			
			// 파일 타입 설정
			setFileType(FTP.BINARY_FILE_TYPE);//BINARY_FILE_TYPE 설 
			
			if (tblNm.equals("Cmr1100")){//Cmr1100 == 산출물 관련 신청건
				strQuery.setLength(0);
				strQuery.append("SELECT b.cr_docfile, a.cr_docid, a.cr_pcdir, a.cr_version ");
				strQuery.append("from cmr1100 a, cmr0030 b ");
				strQuery.append("where a.cr_acptno = ? ");
				strQuery.append("  and b.cr_docid = a.cr_docid ");
				pstmt = conn.prepareStatement(strQuery.toString());
				pstmt.setString(1, AcptNo);
				rs = pstmt.executeQuery();
				String docid = "";
				String targetfullName = "";
				int version = 0;
				while (rs.next()){
					docid = rs.getString("cr_docid");
					version = Integer.parseInt(rs.getString("cr_version"));
					clientPath = rs.getString("cr_pcdir") + "\\" + rs.getString("cr_docfile");
					targetfullName = serverPath + "/" + docid.substring(6,10) + "/" + docid.substring(0,6) + "/" + docid + "." + Integer.toString(version);
					
					if (get(targetfullName,clientPath))
						//cmr1100 cr_errcd='0000' Handler
						UpdateCmr1100(conn,AcptNo,rs.getString("cr_serno"));
				}
				if (errCnt == 0)
					//cmr9900_STR  PROCEDURE Handler
					UpdateCmr9900_STR(conn,AcptNo,"SYSDDN",AcptNo.substring(6,8));
			}
			// 연결 종료
			disconnect();
			
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			
		}catch (SQLException sqlexception) {
			sqlexception.printStackTrace();
			++errCnt;
			throw sqlexception;
		} catch (Exception exception) {
			exception.printStackTrace();
			++errCnt;
			throw exception;
		}finally{
			if (rs != null)     try{rs.close();}catch (Exception ex){ex.printStackTrace();}
			if (pstmt != null)  try{pstmt.close();}catch (Exception ex2){ex2.printStackTrace();}
			if (conn != null){
				try{
					ConnectionResource.release(conn);
				}catch(Exception ex3){
					++errCnt;
					ex3.printStackTrace();
				}
			}
		}
		return errCnt;
	}

}