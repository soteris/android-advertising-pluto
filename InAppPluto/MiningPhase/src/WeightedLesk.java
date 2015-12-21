/**
 * norm(frequency(word2) * LESK(word1, word2))
 * @author soteris
 *
 */
public class WeightedLesk {

	public static double calcRelatedness(String category,
			String opportunityName) {
		// TODO Auto-generated method stub
		double res = 0.0;
		
		switch (opportunityName) {
		case "workout":
			res = WeightedLesk.getHardCodedWorkout(category);
			break;
		case "vehicle":
			res = WeightedLesk.getHardCodedVehicle(category);
		default:
			break;
		}
		
		return res;
	}

	/**
	 * Results from http://ws4jdemo.appspot.com/?mode=w&s1=&w1=workout&s2=&w2=reference
	 * @param category
	 * @return
	 */
	private static double getHardCodedVehicle(String category) {
		double res = 0.0;
		
		switch (category) {
		case "sports":
			res = 0;
			break;
		case "transportation":
			res = 0.8170;
			break;
		case "education":
			res = 0.0599;
			break;
		case "photography":
			res = 0;
			break;
		case "business":
			res = 0.0437;
			break;
		case "app_widgets":
			res = 0.0035;
			break;
		case "tools":
			res = 0;
			break;
		case "medical":
			res = 0.0044;
			break;
		case "social":
			res = 0.0010;
			break;
		case "lifestyle":
			res = 0.1291;
			break;
		case "travel_and_local":
			res = 0.0657;
			break;
		case "weather":
			res = 0.0023;
			break;
		case "libraries_and_demo":
			res = 0;
			break;
		case "media_and_video":
			res = 0;
			break;
		case "productivity":
			res = 0;
			break;
		case "communication":
			res = 0;
			break;
		case "game":
			res = 0;
			break;
		case "app_wallpaper":
			res = 0;
			break;
		case "entertainment":
			res = 0.0005;
			break;
		case "personalization":
			res = 0;
			break;
		case "music_and_audio":
			res = 0.0012;
			break;
		case "finance":
			res = 0.3533;
			break;
		case "shopping":
			res = 0.0177;
			break;
		case "news_and_magazines":
			res = 0.0603;
			break;
		case "health_and_fitness":
			res = 0.0076;
			break;
		case "comics":
			res = 0.0006;
			break;
		case "books_and_reference":
			res = 0.0014;
			break;
		default:
			break;
		}
		
		return res;
	}

	/**
	 * Results from http://ws4jdemo.appspot.com/?mode=w&s1=&w1=workout&s2=&w2=reference
	 * @param category
	 * @return
	 */
	private static double getHardCodedWorkout(String category) {
		// TODO Auto-generated method stub
		double res = 0.0;
		
		switch (category) {
		case "sports":
			res = 0.0035;
			break;
		case "transportation":
			res = 0.0010;
			break;
		case "education":
			res = 0.0067;
			break;
		case "photography":
			res = 0;
			break;
		case "business":
			res = 0.0042;
			break;
		case "app_widgets":
			res = 0.1232;
			break;
		case "tools":
			res = 0.0049;
			break;
		case "medical":
			res = 0.0733;
			break;
		case "social":
			res = 0.0232;
			break;
		case "lifestyle":
			res = 0.0005;
			break;
		case "travel_and_local":
			res = 0.0130;
			break;
		case "weather":
			res = 0.0014;
			break;
		case "libraries_and_demo":
			res = 0;
			break;
		case "media_and_video":
			res = 0.0006;
			break;
		case "productivity":
			res = 0.0003;
			break;
		case "communication":
			res = 0;
			break;
		case "game":
			res = 0.0003;
			break;
		case "app_wallpaper":
			res = 0.0006;
			break;
		case "entertainment":
			res = 0.0015;
			break;
		case "personalization":
			res = 0;
			break;
		case "music_and_audio":
			res = 0.0013;
			break;
		case "finance":
			res = 0.0248;
			break;
		case "shopping":
			res = 0.0052;
			break;
		case "news_and_magazines":
			res = 0.1338;
			break;
		case "health_and_fitness":
			res = 1;
			break;
		case "comics":
			res = 0.0035;
			break;
		case "books_and_reference":
			res = 0.0035;
			break;
		default:
			break;
		}
		
		return res;
	}

}
