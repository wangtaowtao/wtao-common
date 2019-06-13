package com.eyaoshun.common.util;

public class LocationUtils {
	private static final double YAOSHUNFS_LON = 116.083279;//房山仓库经度
	private static final double YAOSHUNFS_LAT = 39.656327;//房山仓库纬度
	private static final double YAOSHUNSH_LON = 116.264104;//沙河仓库经度
	private static final double YAOSHUNSH_LAT = 40.135753;//沙河仓库纬度
	private static final double EARTH_RADIUS = 6378.137;//赤道半径(单位Km)    
    
    /**  
     * 转化为弧度(rad)  
     * */    
    private static double rad(double d)    
    {    
       return d * Math.PI / 180.0;    
    }    
    /**  
     * 基于googleMap中的算法得到两经纬度之间的距离,计算精度与谷歌地图的距离精度差不多，相差范围在0.2米以下  
     * @param lon1 第一点的精度  
     * @param lat1 第一点的纬度  
     * @param lon2 第二点的精度  
     * @param lat2 第二点的纬度  
     * @return 返回的距离，单位km  
     * */    
    public static double getDistance(double lon1,double lat1,double lon2, double lat2)    
    {    
    	double radLat1 = rad(lat1);    
        double radLat2 = rad(lat2);    
        double a = radLat1 - radLat2;    
        double b = rad(lon1) - rad(lon2);    
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)    
                + Math.cos(radLat1) * Math.cos(radLat2)    
                * Math.pow(Math.sin(b / 2), 2)));    
        s = s * EARTH_RADIUS;    
        s = Math.round(s * 10000d) / 10000d;    
        return s;
    }
    
    
    /**
     * @Description 根据尧舜两个仓库的位置,判断送货地址,取最小的配送距离
     * @param lon
     * @param lat
     * @return double
     */
     
    public static double getFeightDistance(double lon,double lat) {
    	double distanceFS = getDistance(YAOSHUNFS_LON, YAOSHUNFS_LAT, lon, lat);
    	double distanceSH = getDistance(YAOSHUNSH_LON, YAOSHUNSH_LAT, lon, lat);
    	return distanceFS - distanceSH >= 0 ? distanceSH : distanceFS;
    	
    }
    
    //外包所用高德地图接口如下
    /*public List<Map<String, String>> getDistance(String ad1, String ad2) {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("key","0b8b9baab6ed1da36f635eb93bfd0052");	
		parameterMap.put("address",ad1+"|"+ad2);
		parameterMap.put("batch",true);
		String content = WebUtils.post("http://restapi.amap.com/v3/geocode/geo?", parameterMap);
		Map<String, Object> data = JsonUtils.toObject(content, new TypeReference<Map<String, Object>>() {
		});
		
		if (!StringUtils.equals(String.valueOf(data.get("info")), "OK")) {
			return Collections.emptyList();
		}
		return (List<Map<String, String>>) data.get("geocodes");
	}*/
    
    
    
    public static void main(String[] args) {
    	double distance = getDistance(116.338991,39.836817,121.361704,31.212498);//北京青秀城-上海虹桥机场
    	System.out.println(distance);
    	
	}
    
    
}

