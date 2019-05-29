import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Test {
	static Gson gson = new Gson();
	public static void main(String[] args) {
		String str = "[{\"MACODE\":\"SYSGB\",\"SelMsg\":\"\",\"closeYn\":\"N\"},{\"MACODE\":\"SYSINFO\",\"SelMsg\":\"\",\"closeYn\":\"N\"},{\"MACODE\":\"SERVERCD\",\"SelMsg\":\"\",\"closeYn\":\"N\"}]";
		TypeToken<ArrayList<HashMap<String, String>>> typeToken = new TypeToken<ArrayList<HashMap<String, String>>>(){};
		ArrayList<HashMap<String, String>> dataList 	=  gson.fromJson(str, typeToken.getType());
		System.out.println(dataList);
		
	}
	
}
