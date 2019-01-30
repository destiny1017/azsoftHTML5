package app.eCmr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import app.common.LoggableStatement;
import app.common.SystemPath;
import app.file.FileMake;

import com.ecams.common.dbconn.ConnectionContext;
import com.ecams.common.dbconn.ConnectionResource;
import com.ecams.common.logger.EcamsLogger;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class Cmr5200 {

	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();

//	private List<String> differences;
	
    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";
    
	
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

			ecamsLogger.error("###   getDiffAry   start   ###\n");
			
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

			in1 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName1),"MS949"));//MS949 UTF-8
			in2 = new BufferedReader(new InputStreamReader(new FileInputStream(fileName2),"MS949"));

			rtList.clear();
			linecnt = 1;
			
			while( ( (readline1 = in1.readLine()) != null ) && ((readline2 = in2.readLine()) != null ))
			{
				rst = new HashMap<String, String>();
				rst.put("lineno",Integer.toString(linecnt));
				
//				readline1 = readline1.replaceAll("[\r\n]", "");
//				readline2 = readline2.replaceAll("[\r\n]", "");
//				
//				readline1 = readline1.replaceAll("[\n]", "");
//				readline2 = readline2.replaceAll("[\n]", "");
				
//				readline1 = readline1.replace(System.getProperty("line.separator"), "");
//				readline2 = readline2.replace(System.getProperty("line.separator"), "");

				if (readline1 != null && readline1 != "" && readline1.length()>0 ){
//					ecamsLogger.error("#### readline1:"+readline1);
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
				if (readline2 != null && readline2 != "" && readline2.length()>0 ){
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
				readline1 = "";
				readline2 = "";
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

			ecamsLogger.error("###   getDiffAry   end   ###\n");
			
			
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

//	File original = new File("/SW2/nice/bin/install.js_2.txt");
//  File revised = new File("/SW2/nice/bin/install.js_3.txt");
    
//	String original1 = "/SW2/nice/bin/install.js_2.txt";
//  String revised1 = "/SW2/nice/bin/install.js_3.txt";
    
//	public Object[] getDiff_java(String UserID,String ItemID1,String ver1,String ItemID2,String ver2) throws SQLException,IOException, Exception {
//		Connection        conn        = null;
//		ConnectionContext connectionContext = new ConnectionResource();
//		PreparedStatement pstmt       = null;
//		ResultSet         rs          = null;
//		StringBuffer      strQuery    = new StringBuffer();
//		ArrayList<HashMap<String, String>>   rtList = new ArrayList<HashMap<String, String>>();
//		HashMap<String, String>			  rst		  = null;
//		FileOutputStream  fw = null;
//		Blob blob = null;
//		
//		try {
//
//			ecamsLogger.error("###   getDiff_java   start   ###\n");
//			
//			conn = connectionContext.getConnection();
//			
//			String diffResultsPath = "";
//			SystemPath systemPath = new SystemPath();
//			//소스비교결과[88] + 사용자ID 를 이용해서 디렉토리 설정
//			diffResultsPath = systemPath.getTmpDir_conn("88",conn) + "/" + UserID;
//			systemPath = null;
//			
//			//임시저장할 폴더 없으면 만들기
//			File fileFolder = new File(diffResultsPath);
//			if ( !fileFolder.exists() ){
//				fileFolder.mkdirs();
//			}
//			fileFolder = null;
//			
//			//original 파일 만들기
//			String originalFileName = ItemID1 + "." + ver1;
//			File originalFile = new File(diffResultsPath+"/"+originalFileName);
//			
//			if ( !originalFile.exists() || originalFile.length()==0 ){
//				originalFile.delete();
//				originalFile.createNewFile();
//				
//				//DB에서 blob 쿼리하기
//				strQuery.setLength(0);
//				strQuery.append("select cr_file from cmr0025 where cr_itemid = ? and cr_ver=?  ");
//				pstmt = conn.prepareStatement(strQuery.toString());
////				pstmt = new LoggableStatement(conn, strQuery.toString());
//				pstmt.setString(1, ItemID1);
//				pstmt.setInt(2, Integer.parseInt(ver1) );
////				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//				rs = pstmt.executeQuery();
//				if ( rs.next() ){
//					fw = new FileOutputStream(originalFile);
//					blob = rs.getBlob("cr_file");
//					fw.write( Gzip.getDecompressedByte( blob.getBytes(1, (int) blob.length()) ) );
//					if(fw.getChannel().size() <1){
//						throw new IOException("FILE SIZE ERROR[1]");
//					}
//					blob = null;
//					fw.flush();
//					fw.close();
//				}
//				rs.close();
//				pstmt.close();
//				
////				oracle.sql.BLOB blob = ((OracleResultSet)rs).getBLOB(1);
////				BufferedInputStream bis = new BufferedInputStream(blob.getBinaryStream());
////				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(originalFileName),4096);
////				int readBlob = bis.read();
////				while (readBlob != -1)
////				{
////					bos.write(readBlob);
////					readBlob = bis.read();
////				}
////				bis.close();
////				bos.close();
////				bis = null;
////				bos = null;
//			}
//			originalFile = null;
//			
//			
//			//revised 파일 만들기
//			String revisedFileName = ItemID2 + "." + ver2;
//			File revisedFile = new File(diffResultsPath+"/"+revisedFileName);
//			
//			if ( !revisedFile.exists() || revisedFile.length()==0 ){
//				revisedFile.delete();
//				revisedFile.createNewFile();
//				
//				//DB에서 blob 쿼리하기
//				strQuery.setLength(0);
//				strQuery.append("select cr_file from cmr0025 where cr_itemid = ? and cr_ver=?  ");
//				pstmt = conn.prepareStatement(strQuery.toString());
////				pstmt = new LoggableStatement(conn, strQuery.toString());
//				pstmt.setString(1, ItemID2);
//				pstmt.setInt(2, Integer.parseInt(ver2) );
////				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
//				rs = pstmt.executeQuery();
//				if ( rs.next() ){
//					fw = new FileOutputStream(revisedFile);
//					blob = rs.getBlob("cr_file");
//					fw.write( Gzip.getDecompressedByte( blob.getBytes(1, (int) blob.length()) ) );
//					if(fw.getChannel().size() <1){
//						throw new IOException("FILE SIZE ERROR[2]");
//					}
//					blob = null;
//					fw.flush();
//					fw.close();
//				}
//				rs.close();
//				pstmt.close();
//			}
//			revisedFile = null;
//			
//			conn.close();
//			rs = null;
//			pstmt = null;
//			conn = null;
//			int	linecnt = 0;
//			String readline1 = ""; 
//			String readline2 = "";
//			
//	        List<String> originalFileLines = fileToLines(diffResultsPath+"/"+originalFileName);
//	        List<String> revisedFileLines = fileToLines(diffResultsPath+"/"+revisedFileName);
//	        int file1TotalLine = originalFileLines.size();
//	        int file2TotalLine = revisedFileLines.size();
//	        List<String> originalLine = new ArrayList<String>();
//	        List<String> revisedLine = new ArrayList<String>();
//	        
////	        ecamsLogger.error("###  file1TotalLine:"+file1TotalLine);
//	        
//	        while ( true )
//	        {
//				rst = new HashMap<String, String>();
//				rst.put("lineno",Integer.toString(linecnt+1));
//				
//				if ( file1TotalLine > linecnt ) readline1 = originalFileLines.get(linecnt).toString();
//				else readline1 = "";
//					
//				if ( file2TotalLine > linecnt ) readline2 = revisedFileLines.get(linecnt).toString();
//				else readline2 = "";
//				
//				rst.put("file1", readline1);
//				rst.put("file2", readline2);
//				
//				originalLine = Arrays.asList(readline1);
//				revisedLine  = Arrays.asList(readline2);
//				
////				System.out.println( "#" + linecnt + "	" + readline1 );
////				System.out.println( "#" + linecnt + "	" + readline2 );
//
//				for ( Delta delta : getDeltas(originalLine,revisedLine) ) {
//	            	
////					System.out.println( "%%% getType : "+delta.getType().toString() + "\n");
//
//					if ( readline1.length() == 0 && readline2.length() > 0 ) {
//						rst.put("file1diff", "I " );
//						rst.put("file2diff", "I " );
//					} else if ( readline2.length() == 0 && readline1.length() > 0 ){
//						rst.put("file1diff", "D " );
//						rst.put("file2diff", "D " );
//					}
//					else if ( delta.getType().toString().equals("CHANGE") ){
//						rst.put("file1diff", "RO" );
//						rst.put("file2diff", "RN" );
//					} else if ( delta.getType().toString().equals("INSERT") ){
//						rst.put("file1diff", "I " );
//						rst.put("file2diff", "I " );
//					} else if ( delta.getType().toString().equals("DELETE") ){
//						rst.put("file1diff", "D " );
//						rst.put("file2diff", "D " );
//					}
//					
//	            }
//				
//				rtList.add(rst);
//				rst = null;
//				linecnt++;
//				readline1 = "";
//				readline2 = "";
//				if ( originalLine != null ) originalLine = null;
//				if ( revisedLine != null ) revisedLine = null;
//				
//				if ( linecnt > 20000  ) break;
//				if ( file1TotalLine <= linecnt &&  file2TotalLine <= linecnt ) break;
//			}
//	        
//			if ( originalLine != null ) originalLine = null;
//			if ( revisedLine != null ) revisedLine = null;
//	        if ( originalFileLines != null ) originalFileLines = null;
//	        if ( revisedFileLines != null ) revisedFileLines = null;
//	        
//	        ecamsLogger.error("###   getDiff_java   end   ###\n");
//	        
//	        
//			return rtList.toArray();
//
//		} catch (SQLException sqlexception) {
//			sqlexception.printStackTrace();
//			ecamsLogger.error("## Cmr5200.getDiffAry() SQLException START ##");
//			ecamsLogger.error("## Error DESC : ", sqlexception);
//			ecamsLogger.error("## Cmr5200.getDiffAry() SQLException END ##");
//			throw sqlexception;
//		} catch (Exception exception) {
//			exception.printStackTrace();
//			ecamsLogger.error("## Cmr5200.getDiffAry() Exception START ##");
//			ecamsLogger.error("## Error DESC : ", exception);
//			ecamsLogger.error("## Cmr5300.getDiffAry() Exception END ##");
//			throw exception;
//		}finally{
//			if (rtList != null)	rtList = null;
//			if (conn != null){
//				try{
//					conn.close();
//				}catch(Exception ex3){
//					ex3.printStackTrace();
//				}
//			}
//
//		}
//	}
	public boolean getSourceExists(String path, String fileName) throws Exception {
		
		try {
			
			
			return true;
			
		} catch (Exception exception) {
			exception.printStackTrace();
			ecamsLogger.error("## Cmr5200.getDiffAry() Exception START ##");
			ecamsLogger.error("## Error DESC : ", exception);
			ecamsLogger.error("## Cmr5300.getDiffAry() Exception END ##");
			throw exception;
		} finally {
			
		}
	}
	
	
	public Object[] getDiff_java(String UserId,String ItemID1,String ver1,String ItemID2,String ver2) throws SQLException,IOException, Exception {
		Connection        conn        = null;
		ConnectionContext connectionContext = new ConnectionResource();
		PreparedStatement pstmt       = null;
		ResultSet         rs          = null;
		StringBuffer      strQuery    = new StringBuffer();
		ArrayList<HashMap<String, String>>   rtList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>   rtList1 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>   rtList2 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>>   tmpList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String>			  rst		  = null;
//		FileOutputStream  fw = null;
		FileMake filemake = new FileMake();
//		Blob blob = null;
		
		try {

			ecamsLogger.error("###   getDiff_java_test   start   ###\n");
			
			conn = connectionContext.getConnection();
			
			String diffResultsPath = "";
			SystemPath systemPath = new SystemPath();
			//소스비교결과[88] + 사용자ID 를 이용해서 디렉토리 설정
			diffResultsPath = systemPath.getTmpDir_conn("88",conn) + "/" + UserId;
			systemPath = null;
			
			//임시저장할 폴더 없으면 만들기
			File fileFolder = new File(diffResultsPath);
			if ( !fileFolder.exists() ){
				fileFolder.mkdirs();
			}
			fileFolder = null;
			
			//original 파일 만들기
			String originalFileName = ItemID1 + "." + ver1;
			File originalFile = new File(diffResultsPath+"/"+originalFileName);
			
			if ( !originalFile.exists() || originalFile.length()==0 ){
				originalFile.delete();
				originalFile.createNewFile();
				
				//DB에서 blob 쿼리하기
				strQuery.setLength(0);
				strQuery.append("select cr_file from cmr0025 where cr_itemid = ? and cr_ver=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemID1);
				pstmt.setInt(2, Integer.parseInt(ver1) );
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					
					if ( !filemake.fileMake(originalFileName, diffResultsPath, rs.getBlob("cr_file"), "04") ){
						throw new IOException("FILE MAKE ERROR[1]");
					}
					
					/*
					fw = new FileOutputStream(originalFile);
					blob = rs.getBlob("cr_file");
					fw.write( Gzip.getDecompressedByte( blob.getBytes(1, (int) blob.length()) ) );
					if(fw.getChannel().size() <1){
						throw new IOException("FILE SIZE ERROR[1]");
					}
					blob = null;
					fw.flush();
					fw.close();
					*/
				}
				rs.close();
				pstmt.close();
			}
			originalFile = null;
			
			
			//revised 파일 만들기
			String revisedFileName = ItemID2 + "." + ver2;
			File revisedFile = new File(diffResultsPath+"/"+revisedFileName);
			
			if ( !revisedFile.exists() || revisedFile.length()==0 ){
				revisedFile.delete();
				revisedFile.createNewFile();
				
				//DB에서 blob 쿼리하기
				strQuery.setLength(0);
				strQuery.append("select cr_file from cmr0025 where cr_itemid = ? and cr_ver=?  ");
				pstmt = conn.prepareStatement(strQuery.toString());
//				pstmt = new LoggableStatement(conn, strQuery.toString());
				pstmt.setString(1, ItemID2);
				pstmt.setInt(2, Integer.parseInt(ver2) );
//				ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
				rs = pstmt.executeQuery();
				if ( rs.next() ){
					
					if ( !filemake.fileMake(revisedFileName, diffResultsPath, rs.getBlob("cr_file"), "04") ){
						throw new IOException("FILE MAKE ERROR[2]");
					}
					/*
					fw = new FileOutputStream(revisedFile);
					blob = rs.getBlob("cr_file");
					fw.write( Gzip.getDecompressedByte( blob.getBytes(1, (int) blob.length()) ) );
					if(fw.getChannel().size() <1){
						throw new IOException("FILE SIZE ERROR[2]");
					}
					blob = null;
					fw.flush();
					fw.close();
					*/
				}
				rs.close();
				pstmt.close();
			}
			revisedFile = null;
			conn.close();
			rs = null;
			pstmt = null;
			conn = null;
			//비교대상 파일 생성 완료 !
			
			
			
			//###   file diff Start   ###
			
			List<String> originalFileLines = fileToLines(diffResultsPath+"/"+originalFileName);
			List<String> revisedFileLines = fileToLines(diffResultsPath+"/"+revisedFileName);

	        Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
	        
	        for ( Delta delta : (List<Delta>) patch.getDeltas() ) {
            	
//	        	System.out.println("## delta.toString():"+delta.toString());
				System.out.println("## readline1:"+delta.getOriginal().toString());
				System.out.println("## readline2:"+delta.getRevised().toString());
				
				rst = new HashMap<String, String>();
				rst.put("getType", delta.getType().toString());
				rst.put("position_1", delta.getOriginal().toString().split(",")[0].toString());
				rst.put("size_1", delta.getOriginal().toString().split(",")[1].toString());
				rst.put("position_2", delta.getRevised().toString().split(",")[0].toString());
				rst.put("size_2", delta.getRevised().toString().split(",")[1].toString());
				tmpList.add(rst);
				rst = null;
	        }
	        
	        int tmpListSize = tmpList.size();
	        int originalFileLinesSize = originalFileLines.size();
	        int revisedFileLinesSize = revisedFileLines.size();
	        int i,j = 0;
	        
	        
	        //1) old 버전 리턴파일에 add
        	for ( j=0 ; j<originalFileLinesSize ; j++ ) {
        		
				rst = new HashMap<String, String>();
				rst.put("file1", originalFileLines.get(j) );
				rtList1.add(rst);
				rst = null;
        		
        	}
	        //2) new 버전 리턴파일에 add
        	for ( j=0 ; j<revisedFileLinesSize ; j++ ) {
        		
				rst = new HashMap<String, String>();
				rst.put("file2", revisedFileLines.get(j) );
				rtList2.add(rst);
				rst = null;
        		
        	}
        	
        	int setIndex1 = 0, setIndex2 = 0;
        	int tmpSize1= 0, tmpSize2 = 0;
        	int cntAddLine1 = 0, cntAddLine2 = 0;
        	
        	//3) 비교결과  리턴파일에 세팅
            for ( i=0 ; i<tmpListSize ; i++ ) {

//            	System.out.println("###1 getType:"+tmpList.get(i).get("getType"));
            	
            	
				//1) old 파일의 위치에 세팅(position 이용)
				if ( tmpList.get(i).get("getType").equals("CHANGE") ){
					
					setIndex1 = Integer.parseInt( tmpList.get(i).get("position_1") ) + cntAddLine1;
					tmpSize1 = Integer.parseInt( tmpList.get(i).get("size_1") );
					
					for ( j=0 ; j<tmpSize1 ; j++ ){
						
						rst = new HashMap<String, String>();
						rst = rtList1.get( setIndex1 + j );
						rst.put("file1diff", "RO" );
						rtList1.set( setIndex1 + j, rst );
						rst = null;
						
					}
				
					setIndex2 = Integer.parseInt( tmpList.get(i).get("position_2") ) + cntAddLine2;
					tmpSize2 = Integer.parseInt( tmpList.get(i).get("size_2") );
					
					for ( j=0 ; j<tmpSize2 ; j++ ){
						
						rst = new HashMap<String, String>();
						rst = rtList2.get( setIndex2 + j );
						rst.put("file2diff", "RN" );
						rtList2.set( setIndex2 + j, rst );
						rst = null;
						
					}
					
					
					// old vs new 가  size가 틀린경우  라인을 추가 삭제 해줘야함
					if ( tmpSize1 > tmpSize2 ){
						
						for ( j=0 ; j<(tmpSize1-tmpSize2) ; j++ ){
							rst = new HashMap<String, String>();
							rst.put("file2", "" );
							rtList2.add( setIndex2 + ( j + (tmpSize1-tmpSize2) ) , rst );
							rst = null;
							++cntAddLine2;
						}
						
					} else if ( tmpSize1 < tmpSize2 ){
						
						for ( j=0 ; j<(tmpSize2-tmpSize1) ; j++ ){
							rst = new HashMap<String, String>();
							rst.put("file1", "" );
							rtList1.add( setIndex2 + ( j + (tmpSize2-tmpSize1) ) , rst );
							rst = null;
							++cntAddLine1;
						}
						
					}
					
				} else if ( tmpList.get(i).get("getType").equals("INSERT") ){
					
					setIndex1 = Integer.parseInt( tmpList.get(i).get("position_1") ) + cntAddLine1;
					tmpSize1 = Integer.parseInt( tmpList.get(i).get("size_2") );
					
//					System.out.println("###1 position_1:"+ tmpList.get(i).get("position_1") );
//					System.out.println("###1 size_1:"+ tmpList.get(i).get("size_1") );
					
					for ( j=0 ; j<tmpSize1 ; j++ ){
						rst = new HashMap<String, String>();
						rst.put("file1diff", "I " );
						rtList1.add( setIndex1 + j, rst );
						rst = null;
						++cntAddLine1;
					}
					
					setIndex2 = Integer.parseInt( tmpList.get(i).get("position_2") ) + cntAddLine2;
					tmpSize2 = Integer.parseInt( tmpList.get(i).get("size_2") );
					
//					System.out.println("###1 position_1:"+ tmpList.get(i).get("position_2") );
//					System.out.println("###1 size_2:"+ tmpList.get(i).get("size_2") );
					
					for ( j=0 ; j<tmpSize2 ; j++ ){
						rst = new HashMap<String, String>();
						rst = rtList2.get( setIndex2 + j );
						rst.put("file2diff", "I " );
						rtList2.set( setIndex2 + j, rst );
						rst = null;
					}
					
				} else if ( tmpList.get(i).get("getType").equals("DELETE") ){
					
					setIndex1 = Integer.parseInt( tmpList.get(i).get("position_1") ) + cntAddLine1;
					tmpSize1 = Integer.parseInt( tmpList.get(i).get("size_1") );
					
					for ( j=0 ; j<tmpSize1 ; j++ ){
						rst = new HashMap<String, String>();
						rst = rtList1.get( setIndex1 + j );
						rst.put("file1diff", "D " );
						rtList1.set(setIndex1 + j, rst);
						rst = null;
					}

					setIndex2 = Integer.parseInt( tmpList.get(i).get("position_2") ) + cntAddLine2;
					tmpSize2 = Integer.parseInt( tmpList.get(i).get("size_1") );
					
					for ( j=0 ; j<tmpSize2 ; j++ ){
						rst = new HashMap<String, String>();
						rst.put("file2diff", "D " );
						rtList2.add( setIndex2 + j, rst );
						rst = null;
						++cntAddLine2;
					}
					
				}
				
            }

	        if ( originalFileLines != null ) originalFileLines = null;
	        if ( revisedFileLines != null ) revisedFileLines = null;

	        
	        // old + new 합치기
//	        System.out.println("###  rtList1:"+rtList1.size() + ", rtList2:" + rtList2.size());
	        setIndex1 = rtList1.size();
	        if ( rtList1.size() > rtList2.size() ) setIndex1 = rtList2.size();
	        
	        for ( i=0 ; i<setIndex1 ; i++ ){
	        	rst = new HashMap<String, String>();
				rst.put("lineno",Integer.toString( i+1 ) );
				rst.put("file1", rtList1.get(i).get("file1") );
				rst.put("file2", rtList2.get(i).get("file2") );
				rst.put("file1diff", rtList1.get(i).get("file1diff") );
				rst.put("file2diff", rtList2.get(i).get("file2diff") );
				rtList.add(rst);
				rst = null;
	        }
	        
	        ecamsLogger.error("###   getDiff_java_test   end   ###\n");
	        
	        if ( tmpList != null ) tmpList = null;
	        if ( rtList1 != null ) rtList1 = null;
	        if ( rtList2 != null ) rtList2 = null;
	        
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
			if ( rst != null )     rst = null;
			if ( tmpList != null ) tmpList = null;
	        if ( rtList1 != null ) rtList1 = null;
	        if ( rtList2 != null ) rtList2 = null;
			if ( rtList  != null ) rtList = null;
			
			if ( strQuery != null ) strQuery = null;
			if ( pstmt != null ) pstmt = null;
			if ( rs != null )    rs    = null;
			if (conn != null){
				try{
					conn.close();
				}catch(Exception ex3){
					ex3.printStackTrace();
				}
			}
		}
	}

    private List<Delta> getDeltas(List<String> original,List<String> revised) throws IOException {

        final Patch patch = DiffUtils.diff(original, revised);

        return patch.getDeltas();
    }
	/*
	public List<Chunk> getChangesFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.CHANGE);
    }

    public List<Chunk> getInsertsFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.INSERT);
    }

    public List<Chunk> getDeletesFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.DELETE);
    }
    private List<Chunk> getChunksByType(Delta.TYPE type) throws IOException {
        final List<Chunk> listOfChanges = new ArrayList<Chunk>();
        final List<Delta> deltas = getDeltas1();
        for (Delta delta : deltas) {
            if (delta.getType() == type) {
                listOfChanges.add(delta.getRevised());
            }
        }
        return listOfChanges;
    }
    private List<Delta> getDeltas1() throws IOException {

        final List<String> originalFileLines = fileToLines(original);
        final List<String> revisedFileLines = fileToLines(revised);

        final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);

        return patch.getDeltas();
    }
    */

    private List<String> fileToLines(String file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String line;

        //BOM 찾기
        FileInputStream fileInputStream = new FileInputStream( file );
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream,"UTF8"));
        boolean utf8YN = false;
        for (String s = ""; (s = bufferedReader.readLine()) != null;) {
        	if ( s.startsWith(UTF8_BOM) ){
        		utf8YN = true;
        	}
        	break;
        }
        bufferedReader.close();
        bufferedReader = null;
        fileInputStream.close();
        fileInputStream = null;
        
        String charSet = "";
        if ( utf8YN ) charSet = "UTF-8";
        else charSet = "MS949";
        
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charSet));
        
//        int i=0;
        while ((line = in.readLine()) != null) {
//        	++i;
            lines.add(line);
        }
//        ecamsLogger.error("#########  i:"+ Integer.toString(i) );
        return lines;
    }
    private List<String> fileToLines(File file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String line;
        final BufferedReader in = new BufferedReader(new FileReader(file));
        
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }

        return lines;
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
//		int               diffCnt     = 0;
		PreparedStatement pstmt2      = null;
		ResultSet         rs2         = null;
//		boolean       findSw = false;
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
			strQuery.append("select b.cr_rsrcname,a.cr_ver,a.cr_acptno,c.cr_sayu,c.cr_qrycd,    \n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi') as date1,       \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi') as date2,        \n");
			strQuery.append("       b.cr_lstver,d.cm_username,b.cr_syscd,b.cr_rsrccd,           \n");
			strQuery.append("       h.cm_sysmsg,i.cm_dirpath, b.cr_status,e.cm_codename         \n");
			strQuery.append("  from cmm0020 e,cmm0040 d,cmr0020 b,CMR0025 a,cmr1000 c,cmm0030 h,cmm0070 i \n");
			strQuery.append(" where a.cr_itemid= ?                                              \n");
			strQuery.append("  and a.cr_itemid=b.cr_itemid and a.cr_acptno=c.cr_acptno          \n");
			strQuery.append("  and c.cr_editor=d.cm_userid and b.cr_syscd=h.cm_syscd            \n");
			strQuery.append("  and b.cr_syscd=i.cm_syscd and b.cr_dsncd  = i.cm_dsncd           \n");
			strQuery.append("  and e.cm_macode='REQUEST' and e.cm_micode=c.cr_qrycd             \n");
			strQuery.append("union                                                              \n");
			strQuery.append("select a.cr_rsrcname,b.cr_befver cr_ver,c.cr_acptno,c.cr_sayu,c.cr_qrycd, \n");
			strQuery.append("       to_char(c.cr_acptdate,'yyyy/mm/dd hh24:mi') as date1,       \n");
			strQuery.append("       to_char(c.cr_prcdate,'yyyy/mm/dd hh24:mi') as date2,        \n");
			strQuery.append("       a.cr_lstver,d.cm_username,a.cr_syscd,a.cr_rsrccd,     \n");
			strQuery.append("       h.cm_sysmsg, i.cm_dirpath, a.cr_status,e.cm_codename  \n");
			strQuery.append("  from cmm0020 e,cmm0040 d,cmr0020 a,cmr1000 c,cmr1010 b,cmm0030 h,cmm0070 i \n");
			strQuery.append(" where a.cr_itemid=? and decode(c.cr_qrycd,'03',a.cr_testver,a.cr_realver)>0 \n");
			strQuery.append("   and a.cr_itemid=b.cr_itemid                              \n");
			strQuery.append("   and b.cr_status<>'3'                                     \n");
			strQuery.append("   and b.cr_prcdate is not null                             \n");
			strQuery.append("   and b.cr_prcdate>(select min(cr_prcdate) from cmr0025    \n");
			strQuery.append("                      where cr_itemid=b.cr_itemid)          \n");
			strQuery.append("   and b.cr_acptno=c.cr_acptno                              \n");
			strQuery.append("   and c.cr_qrycd in ('03','04')                            \n");
			strQuery.append("   and c.cr_status<>'3' and c.cr_prcdate is not null        \n");
			strQuery.append("   and c.cr_editor=d.cm_userid and a.cr_syscd=h.cm_syscd    \n");
			strQuery.append("   and a.cr_syscd=i.cm_syscd and a.cr_dsncd=i.cm_dsncd      \n");
			strQuery.append("   and e.cm_macode='REQUEST' and e.cm_micode=c.cr_qrycd     \n");
			strQuery.append("order by date1 desc                                         \n");
            pstmt = conn.prepareStatement(strQuery.toString());
			pstmt =  new LoggableStatement(conn, strQuery.toString());

            pstmt.setString(1, ItemID);
            pstmt.setString(2, ItemID);
            ecamsLogger.error(((LoggableStatement)pstmt).getQueryString());
            rs = pstmt.executeQuery();

            rtList.clear();

			while (rs.next()){
//				findSw = true;
				rst = new HashMap<String,String>();
				rst.put("cr_itemid", ItemID);
				rst.put("cm_info", strInfo);
				rst.put("cm_sysmsg", rs.getString("cm_sysmsg"));
				rst.put("cr_syscd", rs.getString("cr_syscd"));
				rst.put("cr_rsrccd", rs.getString("cr_rsrccd"));
				rst.put("cr_rsrcname", rs.getString("cr_rsrcname"));
				rst.put("cr_ver", rs.getString("cr_ver"));
				rst.put("version", rs.getString("cr_ver"));
				rst.put("cr_acptno",rs.getString("cr_acptno"));
				rst.put("DATE1",rs.getString("DATE1"));
				rst.put("DATE2",rs.getString("DATE2"));
				rst.put("cr_lstver",rs.getString("cr_lstver"));
				rst.put("cm_username",rs.getString("cm_username"));
				rst.put("cm_dirpath", rs.getString("cm_dirpath"));
				rst.put("cr_status", rs.getString("cr_status"));
				rst.put("cr_qrycd", rs.getString("cr_qrycd"));
				rst.put("cm_codename", rs.getString("cm_codename"));

				rst.put("acptno", rs.getString("cr_acptno").substring(0,4)+"-"+rs.getString("cr_acptno").substring(4,6)+"-"+rs.getString("cr_acptno").substring(6,12));
				rst.put("checked", "false");
				if (rs.getString("cr_sayu") != null){
					rst.put("cr_sayu", rs.getString("cr_sayu"));
				}
				else{
					rst.put("cr_sayu", "");
				}
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