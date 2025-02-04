/**
 * PrjListTab 화면 서블릿
 * <pre>
 * <b>History</b>
 * 	작성자: 이용문
 * 	버전 : 1.0
 *  수정일 : 2019-02-07
 */

package html.app.srcommon;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.*;

import app.common.CodeInfo;
import app.common.PrjInfo;
import app.common.TeamInfo;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/srcommon/PrjListTab")
public class PrjListTab extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	TeamInfo teamInfo = new TeamInfo();
	CodeInfo codeInfo = new CodeInfo();
	PrjInfo  prjInfo  = new PrjInfo();
	
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
				case "SET_TEAM_INFO" :
					response.getWriter().write( setTeamInfo(jsonElement) );
					break;
				case "SET_CAT_TYPE" :
					response.getWriter().write( setCatType(jsonElement) );
					break;
				case "GET_PRJ_INFO" :
					response.getWriter().write( getPrjInfo(jsonElement) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private String setTeamInfo(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> teamInfoMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "teamInfoData") );
		return gson.toJson( teamInfo.getTeamInfoGrid2(
								teamInfoMap.get("SelMsg"), 
								teamInfoMap.get("cm_useyn"), 
								teamInfoMap.get("gubun"), 
								teamInfoMap.get("itYn")
							));
	}
	
	private String setCatType(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> catTypeInfoMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "catTypeInfoData") );
		return gson.toJson( codeInfo.getCodeInfo(
								catTypeInfoMap.get("MACODE"), 
								catTypeInfoMap.get("SelMsg"), 
								catTypeInfoMap.get("closeYn")
							));
	}
	
	private String getPrjInfo(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> prjInfoDataMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "prjInfoData") );
		return gson.toJson( prjInfo.getPrjList(prjInfoDataMap));
	}
}
