package html.app.dev;

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
import app.common.SystemPath;
import app.common.UserInfo;
import app.eCmr.Cmr1100;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class eCmr1100
 */
@WebServlet("/webPage/dev/DistributeStatus")
public class DistributeStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	CodeInfo codeinfo = new CodeInfo();
	SystemPath syspath = new SystemPath();
	SysInfo sysinfo = new SysInfo();
	Cmr1100 cmr1100 = new Cmr1100();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "UserInfochk":
					response.getWriter().write( getUserInfoChk(paramMap) );
					break;
				case "CodeInfoSet":
					response.getWriter().write( getCodeInfo(paramMap) );
					break;
				case "SystemPathSet":
					response.getWriter().write( getSyspath(paramMap) );
					break;
				case "getSysInfo":
					response.getWriter().write( getSysInfo(paramMap) );
					break;
				case "getFileList":
					response.getWriter().write( getFileList(paramMap) );
					break;
				default : 
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			requestType = null;
		}
	}
	
	private String getUserInfoChk(HashMap paramMap) throws SQLException, Exception {
		String user = null;
		user = (String)paramMap.get("UserId");
		return gson.toJson(userinfo.getUserInfo(user));
	}
	
	private String getCodeInfo(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("REQUEST","all","n"));
	}
	
	private String getSyspath(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(syspath.getTmpDir("99"));
	}
	
	private String getSysInfo(HashMap paramMap) throws SQLException, Exception {
		String user = null;
		user = (String)paramMap.get("UserId");
		return gson.toJson(sysinfo.getSysInfo(user, "Y","ALL","N",""));
	}
	
	private String getFileList (HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String>				 childrenMap = null;
		childrenMap = ParsingCommon.parsingRequestJsonParamToHashMap( (String)paramMap.get("prjData").toString() );
		return gson.toJson( cmr1100.getFileList(childrenMap) );
	}
}
