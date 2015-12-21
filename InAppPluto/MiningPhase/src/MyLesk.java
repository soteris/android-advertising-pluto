
public class MyLesk {

	public static double calcRelatedness(String category,
			String opportunityName) {
		// TODO Auto-generated method stub
		double res = 0.0;
		
		switch (opportunityName) {
		case "workout":
			res = MyLesk.getHardCodedWorkout(category);
			break;
		case "vehicle":
			res = MyLesk.getHardCodedVehicle(category);
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
			res = 68;
			break;
		case "transportation":
			res = 132;
			break;
		case "education":
			res = 73;
			break;
		case "photography":
			res = 72;
			break;
		case "business":
			res = 81;
			break;
		case "app_widgets":
			res = 49;
			break;
		case "tools":
			res = 99;
			break;
		case "medical":
			res = 37;
			break;
		case "social":
			res = 56;
			break;
		case "lifestyle":
			res = 37;
			break;
		case "travel_and_local":
			res = 103;
			break;
		case "weather":
			res = 58;
			break;
		case "libraries_and_demo":
			res = 52;
			break;
		case "media_and_video":
			res = 46;
			break;
		case "productivity":
			res = 26;
			break;
		case "communication":
			res = 88;
			break;
		case "game":
			res = 49;
			break;
		case "app_wallpaper":
			res = 22;
			break;
		case "entertainment":
			res = 40;
			break;
		case "personalization":
			res = 0;
			break;
		case "music_and_audio":
			res = 52;
			break;
		case "finance":
			res = 43;
			break;
		case "shopping":
			res = 17;
			break;
		case "news_and_magazines":
			res = 89;
			break;
		case "health_and_fitness":
			res = 46;
			break;
		case "comics":
			res = 43;
			break;
		case "books_and_reference":
			res = 104;
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
			res = 94;
			break;
		case "transportation":
			res = 77;
			break;
		case "education":
			res = 79;
			break;
		case "photography":
			res = 65;
			break;
		case "business":
			res = 94;
			break;
		case "app_widgets":
			res = 32;
			break;
		case "tools":
			res = 85;
			break;
		case "medical":
			res = 38;
			break;
		case "social":
			res = 51;
			break;
		case "lifestyle":
			res = 31;
			break;
		case "travel_and_local":
			res = 108;
			break;
		case "weather":
			res = 62;
			break;
		case "libraries_and_demo":
			res = 34;
			break;
		case "media_and_video":
			res = 36;
			break;
		case "productivity":
			res = 24;
			break;
		case "communication":
			res = 75;
			break;
		case "game":
			res = 68;
			break;
		case "app_wallpaper":
			res = 15;
			break;
		case "entertainment":
			res = 48;
			break;
		case "personalization":
			res = 0;
			break;
		case "music_and_audio":
			res = 96;
			break;
		case "finance":
			res = 54;
			break;
		case "shopping":
			res = 12;
			break;
		case "news_and_magazines":
			res = 68;
			break;
		case "health_and_fitness":
			res = 33;
			break;
		case "comics":
			res = 46;
			break;
		case "books_and_reference":
			res = 113;
			break;
		default:
			break;
		}
		
		return res;
	}
}
