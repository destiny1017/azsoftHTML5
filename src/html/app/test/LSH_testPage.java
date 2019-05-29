package html.app.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "getMenuList":
					response.getWriter().write( getUserInfoChk(jsonElement) );
					break;
				case "getLowMenuList":
					response.getWriter().write( getLowMenuList(jsonElement) );
					break;
				case "setMenuList":
					response.getWriter().write( setMenuList(jsonElement) );
					break;
				case "getMenuAllList":
					response.getWriter().write( getMenuAllList(jsonElement) );
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
	
	private String getUserInfoChk(JsonElement jsonElement) throws SQLException, Exception {
		String tmp = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "temp") );
		return gson.toJson(cmm0500.getMenuList(tmp));
	}
	
	private String getLowMenuList(JsonElement jsonElement) throws SQLException, Exception {
		String tmp = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "Cbo_Menu") );
		return gson.toJson(cmm0500.getLowMenuList(tmp));
	}
	
	private String setMenuList(JsonElement jsonElement) throws SQLException, Exception {
		String menucd = ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "menucd") );
		String selectLabel = ParsingCommon.jsonStrToStr(ParsingCommon.jsonEtoStr(jsonElement, "selectLabel")  );
		ArrayList<HashMap<String, String>> tmpList = ParsingCommon.jsonArrToArr(ParsingCommon.jsonEtoStr(jsonElement, "tmpList"));
		return gson.toJson(cmm0500.setMenuList(selectLabel, menucd, tmpList));
	}
	
	private String getMenuAllList(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(cmm0500.getMenuAllList());
	}
}
