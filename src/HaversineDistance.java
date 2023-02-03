public class HaversineDistance {
	// Approximate Earth radius in meters
	private static final int EARTH_RADIUS = 6371000;

	// Compute distance between two geo-locations in meters
	public static double distance(double startLat, double startLong, double endLat, double endLong) {
		double startLat2=startLat;
		double startLong2=startLong;
		double endLat2=endLat;
		double endLong2=endLong;

		double dLat  = Math.toRadians((endLat2 - startLat2));
		double dLong = Math.toRadians((endLong2 - startLong2));
		
		startLat2 = Math.toRadians(startLat2);
		endLat2   = Math.toRadians(endLat2);
		
		double a = haversine(dLat) + Math.cos(startLat2) * Math.cos(endLat2) * haversine(dLong);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return EARTH_RADIUS * c;
	}
	
	private static double haversine(double val) {
        return Math.pow(Math.sin(val/2), 2);
    }
}
