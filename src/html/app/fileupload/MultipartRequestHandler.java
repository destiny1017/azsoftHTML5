package html.app.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import app.common.SystemPath;
import html.app.fileupload.vo.FileMeta;

// 요청 객체에서 업로드 된 파일을 가져옵니다.
public class MultipartRequestHandler {

	private static String noticeAcptno = "";
	private static SystemPath systemPath = new SystemPath();
	// Servlet API를 사용하여 업로드 된 모든 파일을 가져옵니다. 
	// 참고로 Servlet 3.0과 함께 작동합니다.
	public static List<FileMeta> uploadByJavaServletAPI(HttpServletRequest request) throws IOException, ServletException{
		
		List<FileMeta> files = new LinkedList<FileMeta>();
		
		// 1. Get all parts
		Collection<Part> parts = request.getParts();
		
		// 2. Get paramter "twitter"
		String noticeAcptno = request.getParameter("noticeAcptno");
		// 3. Go over each part
		FileMeta temp = null;
		for(Part part:parts){	

			// 3.1 if part is multiparts "file"
			if(part.getContentType() != null){
				
				// 3.2 Create a new FileMeta object
				temp = new FileMeta();
				temp.setFileName(getFilename(part));
				temp.setFileSize(part.getSize()/1024 +" Kb");
				temp.setFileType(part.getContentType());
				temp.setContent(part.getInputStream());
				temp.setNoticeAcptno(noticeAcptno);
				// 3.3 Add created FileMeta object to List<FileMeta> files
				files.add(temp);
			}
		}
		return files;
	}
	
	// Apache FileUpload lib를 사용하여 업로드 된 모든 파일을 가져옵니다.
	public static List<FileMeta> uploadByApacheFileUpload(HttpServletRequest request) throws Exception{
		List<FileMeta> files = new LinkedList<FileMeta>();
		
		// 1. Check request has multipart content
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileMeta temp = null;
		// 2. If yes (it has multipart "files")
		if(isMultipart){

			// 2.1 instantiate Apache FileUpload classes
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024*1024*1024*1024*1);
			// 임시파일 저장위치 설정
			/*File tmpFolder = new File("C:\\tmpFileupload");
			if(!tmpFolder.exists()) {
				tmpFolder.mkdirs();
			}
			factory.setRepository(tmpFolder);*/
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");
			// 2.2 Parse the request
			try {
				
				// 2.3 Get all uploaded FileItem
				List<FileItem> items = upload.parseRequest(request);
				
				
				for(FileItem item:items) {
			        if(item.getFieldName().equals("noticeAcptno")) {
			        	noticeAcptno = item.getString();
			        }
				}
				
				
				// 2.4 Go over each FileItem
				for(FileItem item:items){
					// 2.5 if FileItem is not of type "file"
				    if (item.isFormField()) {
				    	// 2.6 Search for "twitter" parameter
				        /*if(item.getFieldName().equals("noticeAcptno")) {
				        	if(!noticeAcptno.equals(item.getString())) {
				        		fileIndex = 0;
				        	}
				        	noticeAcptno = item.getString();
				        	System.out.println("noticeAcptno : " + noticeAcptno);
				        }*/
				        	
				        
				    } else {
				       
				    	// 2.7 Create FileMeta object
				    	temp = new FileMeta();
						temp.setFileName(item.getName());
						temp.setContent(item.getInputStream());
						temp.setFileType(item.getContentType());
						temp.setFileSize(item.getSize()/1024+ "Kb");
						
						// 파일 올라가는 디렉토리 없으면 생성
						String fileUploadDir = systemPath.getTmpDir("01");
						
						File fileUploadFolder = new File(fileUploadDir + "\\" + noticeAcptno);	
						if(!fileUploadFolder.exists()) {
							fileUploadFolder.mkdirs();
						}
						
						String fileName = item.getName();
						if(fileName.indexOf("\\") > 0) {
							fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
						}
						System.out.println("fileName : " + fileName);
						// 경로 나중에 db에서 받아와서 셋팅
						String fileFullName = fileUploadDir + "\\" + noticeAcptno + "\\" + fileName;
						// 파일 저장
						item.write(new File(fileFullName));
						
				    	// 2.7 Add created FileMeta object to List<FileMeta> files
						files.add(temp);
				    }
				    
				}
				
				// 2.8 Set "twitter" parameter 
				for(FileMeta fm:files){
					fm.setNoticeAcptno(noticeAcptno);
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
		return files;
	}

	
	// this method is used to get file name out of request headers
	private static String getFilename(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}
}
