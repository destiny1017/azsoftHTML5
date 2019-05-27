package html.app.administrator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import app.common.CodeInfo;
import app.common.SysInfo;
import app.eCmm.Cmm0400;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/UserInfo")
public class UserInfo extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeinfo = new CodeInfo();
	SysInfo sysinfo = new SysInfo();
	Cmm0400 cmm0400 = new Cmm0400();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(paramMap) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSysInfo(paramMap) );
					break;
				case "SysInfo_1" :
					response.getWriter().write( getJobInfo(paramMap) );
					break;
				case "Cmm0400" :
					response.getWriter().write( getUserInfo(paramMap) );
					break;
				case "Cmm0400_1" :
					response.getWriter().write( getListDuty(paramMap) );
					break;
				case "Cmm0400_2" :
					response.getWriter().write( getjobList(paramMap) );
					break;
				case "Cmm0400_3" :
					response.getWriter().write( getUserRgtDept(paramMap) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			requestType = null;
		}
		
	}


	private String getCodeInfo(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("POSITION,DUTY,RGTCD","SEL","N"));
	}
	
	private String getSysInfo(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(sysinfo.getSysInfo("","N","SEL","N",""));
	}
	
	private String getJobInfo(HashMap paramMap) throws SQLException, Exception {
		String sysCd = null;
		sysCd = (String)paramMap.get("sysCd");
		return gson.toJson(sysinfo.getJobInfo("",sysCd,"N","N","","CD"));
	}
	
	private String getUserInfo(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("userId");
		String userName = null;
		userName = (String)paramMap.get("userName");
		System.out.println("Ω√¿€"+userId+","+userName);
		return gson.toJson(cmm0400.getUserInfo(userId,userName));
	}
	
	private String getListDuty(HashMap paramMap) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = (String)paramMap.get("txtUserId");
		return gson.toJson(cmm0400.getUserRGTCD(txtUserId));
	}
	
	private String getjobList(HashMap paramMap) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = (String)paramMap.get("txtUserId");
		return gson.toJson(cmm0400.getUserJobList("USER", txtUserId));
	}
	
	private String getUserRgtDept(HashMap paramMap) throws SQLException, Exception {
		String txtUserId = null;
		txtUserId = (String)paramMap.get("txtUserId");
		return gson.toJson(cmm0400.getUserRgtDept(txtUserId));
	}
}
