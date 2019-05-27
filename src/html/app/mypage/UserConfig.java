package html.app.mypage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

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
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "UserInfo" :
					response.getWriter().write( checkAdmin(paramMap) );
					break;
				case "SysInfo" :
					response.getWriter().write( getSelSysname(paramMap) );
					break;
				case "Cmd1300" :
					response.getWriter().write( getGrid1(paramMap) );
					break;
				case "Cmd1300_1" :
					response.getWriter().write( getUpdateDir(paramMap) );
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


	private String checkAdmin(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("UserId");
		return gson.toJson(userinfo.isAdmin(userId));
	}
	
	private String getSelSysname(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("UserId");
		String secuYn = null;
		secuYn = (String)paramMap.get("SecuYn");
		return gson.toJson(sysinfo.getSysInfo(userId, secuYn, "SEL", "N", ""));
	}
	
	private String getGrid1(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("UserId");
		return gson.toJson(cmd1300.getSql_Qry(userId));
	}
	
	private String getUpdateDir(HashMap paramMap) throws SQLException, Exception {
		String userId = null;
		userId = (String)paramMap.get("UserId");
		String selSysCd = null;
		selSysCd = (String)paramMap.get("selSysCd");
		String txtDir = null;
		txtDir = (String)paramMap.get("txtDir");
		return gson.toJson(cmd1300.getTblUpdate(userId, selSysCd, txtDir));
	}
}
