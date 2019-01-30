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
		String requestType = null;
		requestType = ParsingCommon.parsingRequestJsonParamToString(request, "requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "GETJOBLIST" :
					response.getWriter().write( getJobList(request) );
					break;
				case "DELJOBINFO" :
					response.getWriter().write( delJobInfo(request) );
					break;
				case "SETJOBINFOINDIVIDUAL" :
					response.getWriter().write( setJobInfoIndividual(request) );
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
	private String getJobList(HttpServletRequest request) throws SQLException, Exception {
		return gson.toJson(cmm0100.getJobList());
	}
	
	private String delJobInfo(HttpServletRequest request) throws SQLException, Exception {
		ArrayList<HashMap<String, String>> delJobInfoList = ParsingCommon.parsingRequestJsonParamToArrayList(request, "delInfoDataArr");
		return gson.toJson(cmm0100.delJobInfo(delJobInfoList));
	}
	
	private String setJobInfoIndividual(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> jobInfoMap = ParsingCommon.parsingRequestJsonParamToHashMap(request, "jobInfoData");
		return gson.toJson(cmm0100.setJobInfo_individual(jobInfoMap.get("jobcd"), jobInfoMap.get("jobname")));
	}
	
}
