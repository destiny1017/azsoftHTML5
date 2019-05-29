package html.app.dev;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import app.eCmd.Cmd0100;
import app.common.PrjInfo;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/dev/ProgRegister")
public class ProgRegister extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	Cmd0100 cmd0100  = new Cmd0100();
	PrjInfo prjinfo  = new PrjInfo();
	
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
			case "getRsrc" :
				response.getWriter().write( getRsrcOpen(jsonElement) );
				break;
			case "getDir" :
				response.getWriter().write( getDir(jsonElement) );
				break;
			case "getSrId" :
				response.getWriter().write( getSrId(jsonElement) );
				break;
			case "getOpenList" :
				response.getWriter().write( getOpenList(jsonElement) );
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
		
/*
	private String getSysInfo(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> getSysInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "sysData");
		return gson.toJson( sysInfo.getSysInfo(
								getSysInfoMap.get("UserId"), 
								"", 
								getSysInfoMap.get("SelMsg"), 
								getSysInfoMap.get("CloseYn"), 
								getSysInfoMap.get("ReqCd")
							));
	}
	*/
	private String getRsrcOpen(JsonElement jsonElement) throws SQLException, Exception {
		
		HashMap<String, String> DataMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "Data") );
		return gson.toJson( cmd0100.getRsrcOpen(
								DataMap.get("cm_syscd"),
								DataMap.get("SelMsg")
								
							));
	}

	private String getDir(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> DataMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "Data") );
		return gson.toJson( cmd0100.getDir(
								DataMap.get("UserID"),
								DataMap.get("SysCd"),
								DataMap.get("SecuYn"),
								DataMap.get("RsrcCd"),
								DataMap.get("JobCd"),
								DataMap.get("SelMsg")
							));
	}

	private String getSrId(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> DataMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "Data") );
		return gson.toJson( prjinfo.getPrjList(DataMap
							));
	}
	
	private String getOpenList(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> DataMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "Data") );
		Boolean secusw = false;
		if("true".equals(DataMap.get("SecuSw"))){
			secusw = true;
		}
		return gson.toJson( cmd0100.getOpenList(
								DataMap.get("SRid"),
								DataMap.get("UserId"),
								DataMap.get("SysCd"),
								DataMap.get("RsrcCd"),
								DataMap.get("RsrcName"),
								secusw
							));
	}
}
