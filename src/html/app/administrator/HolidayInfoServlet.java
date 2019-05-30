package html.app.administrator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.eCmm.Cmm1000;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/HolidayInfoServlet")
public class HolidayInfoServlet extends HttpServlet {
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
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(ParsingCommon.getJsonStr(request));
		String requestType	= ParsingCommon.jsonStrToStr( ParsingCommon.jsonEtoStr(jsonElement, "requestType") );
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "GETHOLIDAY" :
					response.getWriter().write( getHoliDay(jsonElement));
					break;
				case "CHKHOLIDAY" :
					response.getWriter().write( checkHoliday(jsonElement));
					break;
				case "ADDHOLIDAY" :
					response.getWriter().write( addHoliday(jsonElement));
					break;
				case "DELHOLIDAY" :
					response.getWriter().write( delHoliday(jsonElement));
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}

	private String getHoliDay(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> holiInfoMap =  new HashMap<String, String>();
		holiInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement,"holiday")); //jsonEtoStr(JsonElement jsonElement, String key)
		return gson.toJson(cmm1000.getHoliDay(holiInfoMap.get("year")));
	}
	
	private String checkHoliday(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> checkInfoMap = new HashMap<String, String>(); 
		checkInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement,"checkInfoData"));
		return gson.toJson(cmm1000.chkHoliDay(checkInfoMap.get("date")));
	}
	
	private String addHoliday(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> addHoliInfoMap = new HashMap<String, String>(); 
		addHoliInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement,"addHoliInfoData"));
		return gson.toJson(cmm1000.addHoliday(	addHoliInfoMap.get("date"), 
												addHoliInfoMap.get("holidaygbn"), 
												addHoliInfoMap.get("holidaycase"), 
												Integer.parseInt(addHoliInfoMap.get("updtSW")))
							);
	}
	
	private String delHoliday(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> delHoliInfoMap = new HashMap<String, String>();  
		delHoliInfoMap = ParsingCommon.jsonStrToMap(ParsingCommon.jsonEtoStr(jsonElement,"delHoliInfoData"));
		return gson.toJson(cmm1000.delHoliday(delHoliInfoMap.get("date")));
	}
	
}
