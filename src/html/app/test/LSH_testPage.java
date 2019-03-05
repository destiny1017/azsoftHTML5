package html.app.test;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import app.eCmm.Cmm0500;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class LSH_testPage
 */
@WebServlet("/webPage/test/LSH_testPage")
public class LSH_testPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmm0500 cmm0500 = new Cmm0500();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "getMenuList":
					response.getWriter().write( getUserInfoChk(request) );
					break;
				case "getLowMenuList":
					response.getWriter().write( getLowMenuList(request) );
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
	
	private String getUserInfoChk(HttpServletRequest request) throws SQLException, Exception {
		String tmp = null;
		tmp = ParsingCommon.parsingRequestJsonParamToString(request, "temp");
		return gson.toJson(cmm0500.getMenuList(tmp));
	}
	
	private String getLowMenuList(HttpServletRequest request) throws SQLException, Exception {
		String tmp = null;
		tmp = ParsingCommon.parsingRequestJsonParamToString(request, "Cbo_Menu");
		return gson.toJson(cmm0500.getLowMenuList(tmp));
	}
}
