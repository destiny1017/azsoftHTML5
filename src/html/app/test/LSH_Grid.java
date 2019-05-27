package html.app.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import html.app.common.ParsingCommon;

/**
 * Servlet implementation class LSH_Grid
 */
@WebServlet("/webPage/test/LSH_Grid")
public class LSH_Grid extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	
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
				case "getMenuList":
					response.getWriter().write( getUserInfoChk(paramMap) );
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
		String tmp = (String)paramMap.get("temp");
		//return gson.toJson(cmm0500.getMenuList(tmp));
		return "";
	}

}
