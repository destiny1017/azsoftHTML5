package html.app.administrator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ecams.service.list.LoginManager;
import com.google.gson.Gson;

import app.eCmm.Cmm1000;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/HolidayInfo")
public class HolidayInfo extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmm1000 cmm1000 = new Cmm1000();
	
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
				case "GETHOLIDAY" :
					response.getWriter().write( getHoliDay(request));
					break;
				case "CHKHOLIDAY" :
					response.getWriter().write( checkHoliday(request));
					break;
				case "ADDHOLIDAY" :
					response.getWriter().write( addHoliday(request));
					break;
				case "DELHOLIDAY" :
					response.getWriter().write( delHoliday(request));
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}

	private String getHoliDay(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> holiInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "holiInfoData");
		return gson.toJson(cmm1000.getHoliDay(holiInfoMap.get("year")));
	}
	
	private String checkHoliday(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> checkInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "checkInfoData");
		System.out.println("checkHoliday method");
		return gson.toJson(cmm1000.chkHoliDay(checkInfoMap.get("date")));
	}
	
	private String addHoliday(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> addHoliInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "addHoliInfoData");
		
		
		
		return gson.toJson(cmm1000.addHoliday(	addHoliInfoMap.get("date"), 
												addHoliInfoMap.get("holidaygbn"), 
												addHoliInfoMap.get("holidaycase"), 
												Integer.parseInt(addHoliInfoMap.get("updtSW")))
							);
	}
	
	private String delHoliday(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> delHoliInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "delHoliInfoData");
		return gson.toJson(cmm1000.delHoliday(delHoliInfoMap.get("date")));
	}
}
