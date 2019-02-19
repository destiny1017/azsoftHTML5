package html.app.report;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
import com.google.gson.Gson;

import app.common.SysInfo;
import app.common.UserInfo;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/report/PrgListReport")
public class PrgListReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	SysInfo sysinfo = new SysInfo();
	
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
					response.getWriter().write( isAdmin(request) );
					break;
				case "getSysInfo" :
					response.getWriter().write( getSysInfo(request) );
					break;
				case "getSysInfo2" :
					response.getWriter().write( getSysInfo(request) );
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
	
	private String isAdmin(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		return gson.toJson(userinfo.isAdmin(user));
	}
	
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		String user = null;
		String tmp = null;
		user = ParsingCommon.parsingRequestJsonParamToString(request, "UserId");
		tmp = ParsingCommon.parsingRequestJsonParamToString(request, "tmp");
		return gson.toJson(sysinfo.getSysInfo(user,tmp,"","N","04"));
	}
}
