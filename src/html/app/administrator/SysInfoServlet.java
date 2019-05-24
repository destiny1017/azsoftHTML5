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

import app.common.CodeInfo;
import app.common.SysInfo;
import app.eCmm.Cmm0200;
import html.app.common.ParsingCommon;

@WebServlet("/webPage/administrator/SysInfoServlet")
public class SysInfoServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Gson gson = new Gson();
	SysInfo sysInfo = new SysInfo();
	CodeInfo codeInfo = new CodeInfo();
	Cmm0200 cmm0200 = new Cmm0200(); 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap paramMap = ParsingCommon.reqParamToMap(request); 
		String requestType = (String)paramMap.get("requestType");
		
		try {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			
			switch (requestType) {
				case "GETSYSINFOCBO" :
					response.getWriter().write(getSysInfoCbo(paramMap));
					break;
				case "GETJOBLIST" :
					response.getWriter().write(getJobList(paramMap));
					break;
				case "GETJOBINFO" :
					response.getWriter().write(getJobInfo(paramMap));
					break;
				case "GETSYSINFOLIST" :
					response.getWriter().write(getSysInfoList(paramMap));
					break;
				case "FACTUP" :
					response.getWriter().write(updateFactUp(paramMap));
					break;
				case "CLOSESYS" :
					response.getWriter().write(closeSystem(paramMap));
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
	}
	
	private String getSysInfoCbo(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> sysInfoCbo = new HashMap<String, String>();
		sysInfoCbo = ParsingCommon.parsingRequestJsonParamToHashMap((String)paramMap.get("sysInfoCbo").toString());
		return gson.toJson(sysInfo.getSysInfo_Rpt(sysInfoCbo.get("UserId"), 
													sysInfoCbo.get("SelMsg"), 
													sysInfoCbo.get("CloseYn"), 
													sysInfoCbo.get("SysCd")));
	}
	
	private String getJobList(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> jobInfoCbo = new HashMap<String, String>();
		jobInfoCbo = ParsingCommon.parsingRequestJsonParamToHashMap((String)paramMap.get("jobInfoCbo").toString());
		return gson.toJson(codeInfo.getJobCd(jobInfoCbo.get("SelMsg"), jobInfoCbo.get("closeYn")));
	}
	
	private String getJobInfo(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> sysJobInfo = new HashMap<String, String>();
		sysJobInfo = ParsingCommon.parsingRequestJsonParamToHashMap(paramMap.get("sysJobInfo").toString());
		return gson.toJson(sysInfo.getJobInfo(sysJobInfo.get("UserID")
												, sysJobInfo.get("SysCd")
												, sysJobInfo.get("SecuYn")
												, sysJobInfo.get("CloseYn")
												, sysJobInfo.get("SelMsg") == null ? "" : sysJobInfo.get("SelMsg")
												, sysJobInfo.get("sortCd"))	);
	}
	
	private String getSysInfoList(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> sysInfo = new HashMap<String, String>();
		sysInfo = ParsingCommon.parsingRequestJsonParamToHashMap((String)paramMap.get("sysInfo").toString());
		return gson.toJson(cmm0200.getSysInfo_List(Boolean.valueOf(sysInfo.get("clsSw")), sysInfo.get("SysCd")) );
	}
	private String closeSystem(HashMap paramMap) throws SQLException, Exception {
		HashMap<String, String> sysInfo = new HashMap<String, String>();
		sysInfo = ParsingCommon.parsingRequestJsonParamToHashMap((String)paramMap.get("sysInfo").toString());
		return gson.toJson(cmm0200.sysInfo_Close(sysInfo.get("SysCd"), sysInfo.get("UserId")));
	}
	private String updateFactUp(HashMap paramMap) throws SQLException, Exception {
		return gson.toJson(	cmm0200.factUpdt() );
	}
}
