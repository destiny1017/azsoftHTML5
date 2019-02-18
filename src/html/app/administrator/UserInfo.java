package html.app.administrator;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(request) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSysInfo(request) );
					break;
				case "SysInfo_1" :
					response.getWriter().write( getJobInfo(request) );
					break;
				case "Cmm0400" :
					response.getWriter().write( getUserInfo(request) );
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


	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("POSITION,DUTY,RGTCD","SEL","N"));
	}
	
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(sysinfo.getSysInfo("","N","SEL","N",""));
	}
	
	private String getJobInfo(HttpServletRequest request) throws SQLException, Exception {
		String sysCd = null;
		sysCd = ParsingCommon.parsingRequestJsonParamToString(request, "sysCd");
		return gson.toJson(sysinfo.getJobInfo("",sysCd,"N","N","","CD"));
	}
	
	private String getUserInfo(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		
		return gson.toJson(cmm0400.getUserInfo(userId,"CD"));
	}
}
