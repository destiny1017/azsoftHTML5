package html.app.fileupload.vo;


import java.io.InputStream;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties({"content"})
public class FileMeta {
	
	

	private String fileName;
	private String fileSize;
	private String fileType;
	private String noticeAcptno;
	
	
	private InputStream content;
	
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public InputStream getContent(){
		return this.content;
	}
	public void setContent(InputStream content){
		this.content = content;
	}
	public String getNoticeAcptno() {
		return noticeAcptno;
	}
	public void setNoticeAcptno(String noticeAcptno) {
		this.noticeAcptno = noticeAcptno;
	}
	@Override
	public String toString() {
		return "FileMeta [fileName=" + fileName + ", fileSize=" + fileSize + ", fileType=" + fileType
				+ ", noticeAcptno=" + noticeAcptno + ", content=" + content + "]";
	}
	
	
}
