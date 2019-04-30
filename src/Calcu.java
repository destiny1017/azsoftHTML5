import java.util.stream.Stream;

public class Calcu {
	
	public static void main(String[] args) {
		
		Stream.of("d2","a2","b1","b3","c")
			.sorted((s1,s2)->{
				System.out.printf("sort: %s; %s\n", s1,s2);
				return s1.compareTo(s2);
			})
			.filter(s -> {
				System.out.println("filter: "+s);
				return s.startsWith("a");
			})
			.map(s -> {
				System.out.println("map: "+s);
				return s.toUpperCase();
			})
			.forEach(s -> System.out.println("forEach: "+s));
		
	}
	
	public static String printLine(){
		return "=============================================";
	}

}
