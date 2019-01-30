package html.app.common;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParsingCommon {
	
	
	public static Gson gson = new Gson();
	
	public static HashMap<String, String> parsingRequestJsonParamToHashMap(HttpServletRequest req, String paramKey) {
		String jsonData = null;
		jsonData 	= req.getParameter(paramKey);
		TypeToken<HashMap<String, String>> typeToken = new TypeToken<HashMap<String, String>>(){};
		HashMap<String, String> mapData 	=  gson.fromJson(jsonData, typeToken.getType());
		return mapData;
	}
	
	public static String parsingRequestJsonParamToString(HttpServletRequest req, String dataTypeParam) {
		String jsonData = null;
		String returnString = null;
		
		jsonData 	= req.getParameter(dataTypeParam);
		returnString 	= (String) gson.fromJson(jsonData, String.class);
		return returnString;
	}
	
	public static ArrayList<HashMap<String, String>> parsingRequestJsonParamToArrayList(HttpServletRequest req, String paramKey) {
		String jsonData = null;
		jsonData 	= req.getParameter(paramKey);
		TypeToken<ArrayList<HashMap<String, String>>> typeToken = new TypeToken<ArrayList<HashMap<String, String>>>(){};
		ArrayList<HashMap<String, String>> dataList 	=  gson.fromJson(jsonData, typeToken.getType());
		return dataList;
	}

}
