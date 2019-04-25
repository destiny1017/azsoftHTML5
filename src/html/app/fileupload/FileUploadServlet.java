package html.app.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.fasterxml.jackson.databind.ObjectMapper;

import app.common.SystemPath;
import html.app.fileupload.vo.FileMeta;

//this to be used with Java Servlet 3.0 API
@MultipartConfig 
@WebServlet("/webPage/fileupload/upload")
public class FileUploadServlet extends HttpServlet {
	
	/*
	 * FileUploadServlet.java는 클라이언트 요청을 수신하는 서블릿입니다.
	 * 파일 업로드 요청을 처리하는 doPost ()와 파일 다운로드 요청을 처리하는 doGet ()의 두 가지 메소드가 있습니다.
	 * doPost () 응답 내용은 JSON 형식입니다.
	 */
	private static final long serialVersionUID = 1L;

	// this will store uploaded files
	
	/***************************************************
	 * URL: /upload
	 * doPost(): upload the files and other parameters
	 ****************************************************/
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException{
	    
		// 1. Upload File Using Java Servlet API
		//files.addAll(MultipartRequestHandler.uploadByJavaServletAPI(request));			
		
		List<FileMeta> files = new LinkedList<FileMeta>();
		try {
			// 1. Upload File Using Apache FileUpload
			files.addAll(MultipartRequestHandler.uploadByApacheFileUpload(request));
			
			// 2. Set response type to json
			response.setContentType("application/json");
			
			// 3. Convert List<FileMeta> into JSON format
	    	ObjectMapper mapper = new ObjectMapper();
	    	// 4. Send resutl to client
	    	System.out.println("files : " + files.toString());
	    	mapper.writeValue(response.getOutputStream(), files);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	// 파일 다운로드
	/***************************************************
	 * URL: /upload?f=value
	 * doGet(): get file of index "f" from List<FileMeta> as an attachment
	 ****************************************************/
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException{
		/*// 1. Get f from URL upload?f="?"
		 String value = request.getParameter("f");
		 
		 // 2. Get the file of index "f" from the list "files"
		 FileMeta getFile = files.get(Integer.parseInt(value));
		 
		 try {	
			 	// 3. Set the response content type = file content type 
			 	response.setContentType(getFile.getFileType());
			 	
			 	// 4. Set header Content-disposition
			 	response.setHeader("Content-disposition", "attachment; filename=\""+getFile.getFileName()+"\"");
			 	
			 	// 5. Copy file inputstream to response outputstream
		        InputStream input 	= getFile.getContent();
		        OutputStream output = response.getOutputStream();
		        byte[] buffer = new byte[1024*10];
		        
		        for (int length = 0; (length = input.read(buffer)) > 0;) {
		            output.write(buffer, 0, length);
		        }
		        
		        output.close();
		        input.close();
		 }catch (IOException e) {
				e.printStackTrace();
		 }*/
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		SystemPath systemPath = new SystemPath();
		
		// 1. Get f from URL upload?f="?"
		 String fileName = request.getParameter("f");
		 String noticeAcptno = request.getParameter("noticeAcptno");
		 
		 System.out.println("download file name = " + fileName);

		 try {	
			 String noticeUploadDir =  systemPath.getTmpDir("01");
			 // 3. Set the response content type = file content type 
		 	
		 	// 4. Set header Content-disposition
		 	response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\"");
		 	
		 	// 5. Copy file inputstream to response outputstream
	        FileInputStream input 	= new FileInputStream(noticeUploadDir+"\\"+noticeAcptno+"\\"+fileName); 
	        OutputStream output = response.getOutputStream();
	        byte[] buffer = new byte[1024*10];
	        
	        for (int length = 0; (length = input.read(buffer)) > 0;) {
	            output.write(buffer, 0, length);
	        }
	        
	        output.close();
	        input.close();
		 }catch (Exception e) {
				e.printStackTrace();
		 }
	
	}
}
