package app.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.ecams.common.logger.EcamsLogger;


/**
 * @author neo
 * @since 2017. 03
 *
 */
public class FileMake {
	
	Logger ecamsLogger  = EcamsLogger.getLoggerInstance();
	
    /** 서버에 파일생성 blob -> file
     * @param fileNM(String) : 생성 될 파일명
     * @param path(String) : 생성 될 디렉토리
     * @param blob(Blob) : oracle blob
     * @param sysOSType(String) :  01:unix    03:NT(windows)  04:linux
     * @return boolean : true:정상
     * @throws IOException
     * @throws SQLException 
     */
    public boolean fileMake(String fileNM, String path, Blob blob, String sysOSType) throws IOException, SQLException{
    	
    	try {
    		
			if( path == null || path == "" ){
				throw new IOException("디렉토리정보 오류입니다.[fileMake]");
			}
			
			//디렉토리존재여부 확인
			File filez = new File(path);
			if ( !filez.exists() ){
				filez.mkdirs();
			}
			filez = null;
			
			String pullName = path + "/" + fileNM;
			
			//system OS 맞도록 경로 변경
			if ( "03".equals(sysOSType) ) pullName = pullName.replace("/", "\\");
			
			//파일 만들기
			filez = new File(pullName);
			filez.delete();
			FileOutputStream fw = new FileOutputStream(filez);
			fw.write( Gzip.getDecompressedByte( blob.getBytes(1, (int) blob.length()) ) );
			if(fw.getChannel().size() <1){
				throw new IOException("FILE SIZE ERROR.[fileMake]");
			}
			fw.flush();
			fw.close();
			filez = null;
			

    	} catch (IOException e) {		
			e.printStackTrace();
			ecamsLogger.error("## FileMake.fileMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", e);
			ecamsLogger.error("## FileMake.fileMake() Exception END ##");
			throw e;
		} catch (SQLException e) {		
			e.printStackTrace();
			ecamsLogger.error("## FileMake.fileMake() Exception START ##");
			ecamsLogger.error("## Error DESC : ", e);
			ecamsLogger.error("## FileMake.fileMake() Exception END ##");
			throw e;
		}
    	
		return true;
		
    }
}
