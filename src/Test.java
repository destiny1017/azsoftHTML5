import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Test {
	public static void main(String[] args) {
		
		
		ArrayList<HashMap<String, String>> tt = new ArrayList<>();
		HashMap<String, String> test = new HashMap<>();
		test.put("test", "test2");
		tt.add(test);
		
		Object[] objArr = tt.toArray();;
		ArrayList<Object> list = new ArrayList<>(Arrays.asList(objArr));
		
		
		for(Object obj:list) {
			HashMap<String, String> map = (HashMap<String, String>) obj;
			System.out.println(map.get("test"));
		}
		
		System.out.println(list.get(0));
		
	}
}
