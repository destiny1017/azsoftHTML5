package html.app.main;

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

import app.eCmr.Cmr3200;
import html.app.common.ParsingCommon;

/**
 * Servlet implementation class eCAMSMainServlet
 */
@WebServlet("/webPage/main/eCAMSMainServlet")
public class eCAMSMainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Gson gson = new Gson();
	LoginManager loginManager = LoginManager.getInstance();
	Cmr3200 cmr3200 = new Cmr3200();
	
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
				case "GETAPPLYLIST":
					response.getWriter().write( getMainApplyList(request) );
					break;
				case "GETAPPLYLIPIE":
					response.getWriter().write( getMainApplyPie(request) );
					break;
				case "GETPRGPIE":
					response.getWriter().write( getMainPrgPie(request) );
					break;
				case "GETBARCHART":
					response.getWriter().write( getMainBar(request) );
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
	
	private String getMainApplyList(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> applyInfo = new HashMap<String, String>();
		applyInfo = ParsingCommon.parsingRequestJsonParamToHashMap(request, "applyInfo");
		return gson.toJson(cmr3200.get_SelectList_HtmlMain(applyInfo));
	}
	
	private String getMainApplyPie(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> applyInfo = new HashMap<String, String>();
		String closeSwStr = ParsingCommon.parsingRequestJsonParamToString(request, "pieCloseSw"); 
		applyInfo = ParsingCommon.parsingRequestJsonParamToHashMap(request, "applyInfo");
		boolean closeSw =  closeSwStr.equals("Y") ? true : false;
		return gson.toJson(cmr3200.getMainAppiPie(applyInfo,closeSw));
	}
	
	private String getMainPrgPie(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> applyInfo = new HashMap<String, String>();
		String closeSwStr = ParsingCommon.parsingRequestJsonParamToString(request, "piePCloseSw"); 
		applyInfo = ParsingCommon.parsingRequestJsonParamToHashMap(request, "applyInfo");
		boolean closeSw =  closeSwStr.equals("Y") ? true : false;
		return gson.toJson(cmr3200.getMainPrgPie(applyInfo,closeSw));
	}
	
	private String getMainBar(HttpServletRequest request) throws SQLException, Exception {
		HashMap<String, String> applyInfo = new HashMap<String, String>();
		applyInfo = ParsingCommon.parsingRequestJsonParamToHashMap(request, "applyInfo");
		return gson.toJson(cmr3200.getMainBar(applyInfo));
	}

}
