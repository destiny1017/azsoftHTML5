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
import app.common.CodeInfo;
import app.common.SystemPath;
import app.eCmm.Cmm2100;

import html.app.common.ParsingCommon;

@WebServlet("/webPage/mypage/Notice")
public class Notice extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	UserInfo userinfo = new UserInfo();
	CodeInfo codeinfo = new CodeInfo();
	SystemPath systempath = new SystemPath();
	Cmm2100 cmm2100 = new Cmm2100();
	
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
				case "CodeInfo" :
					response.getWriter().write( getCodeInfo(request) );
					break;
//				case "Cmm2100" :
//					response.getWriter().write( getSqlQry(request) );
//					break;
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
	
	private String getCodeInfo(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(codeinfo.getCodeInfo("FIND","","N"));
	}
	
	/*private String getSqlQry(HttpServletRequest request) throws SQLException, Exception {
	}*/
	
}
