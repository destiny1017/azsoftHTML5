package html.app.mypage;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import app.common.UserInfo;
import app.common.SysInfo;
import app.eCmd.Cmd1300;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/mypage/UserConfig")
public class UserConfig extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	Cmd1300 cmd1300= new Cmd1300();
	
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
				case "UserInfo" :
					response.getWriter().write( checkAdmin(request) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSelSysname(request) );
					break;
				case "Cmd1300" :
					response.getWriter().write( getGrid1(request) );
					break;
				case "Cmd1300_1" :
					response.getWriter().write( getUpdateDir(request) );
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


	private String checkAdmin(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String getSelSysname(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		String secuYn = null;
		secuYn = ParsingCommon.parsingRequestJsonParamToString(request, "SecuYn");
		return gson.toJson(sysinfo.getSysInfo(userId, secuYn, "SEL", "N", ""));
	}
	
	private String getGrid1(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(cmd1300.getSql_Qry(userId));
	}
	
	private String getUpdateDir(HttpServletRequest request) throws SQLException, Exception {
		String userId = null;
		userId = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		String selSysCd = null;
		selSysCd = ParsingCommon.parsingRequestJsonParamToString(request, "selSysCd");
		String txtDir = null;
		txtDir = ParsingCommon.parsingRequestJsonParamToString(request, "txtDir");
		return gson.toJson(cmd1300.getTblUpdate(userId, selSysCd, txtDir));
	}
}
