package html.app.modal;

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

import app.common.CodeInfo;
import app.eCmm.Cmm0100;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/modal/TaskInfo")
public class TaskInfo extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	CodeInfo codeInfo = new CodeInfo();
	Cmm0100 cmm0100 = new Cmm0100();
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
				case "GETJOBLIST" :
					response.getWriter().write( getJobList(jsonElement) );
					break;
				case "DELJOBINFO" :
					response.getWriter().write( delJobInfo(jsonElement) );
					break;
				case "SETJOBINFOINDIVIDUAL" :
					response.getWriter().write( setJobInfoIndividual(jsonElement) );
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	//
	private String getJobList(JsonElement jsonElement) throws SQLException, Exception {
		return gson.toJson(cmm0100.getJobList());
	}
	
	private String delJobInfo(JsonElement jsonElement) throws SQLException, Exception {
		ArrayList<HashMap<String, String>> delJobInfoList = ParsingCommon.jsonArrToArr( ParsingCommon.jsonEtoStr(jsonElement, "delInfoDataArr") );
		return gson.toJson(cmm0100.delJobInfo(delJobInfoList));
	}
	
	private String setJobInfoIndividual(JsonElement jsonElement) throws SQLException, Exception {
		HashMap<String, String> jobInfoMap = ParsingCommon.jsonStrToMap( ParsingCommon.jsonEtoStr(jsonElement, "jobInfoData") );
		return gson.toJson(cmm0100.setJobInfo_individual(jobInfoMap.get("jobcd"), jobInfoMap.get("jobname")));
	}
	
}
