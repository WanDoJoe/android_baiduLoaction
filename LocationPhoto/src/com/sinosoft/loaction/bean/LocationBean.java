package com.sinosoft.loaction.bean;

import java.io.Serializable;

public class LocationBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String time;
	String locType;
	double latitude;
	double longitude;
	String radius;
	String countryCode;
	String country;
	String cityCode;
	String city;
	String district;
	String street;
	String addrStr;
	String locationDescribe;//详细地址
	String direction;
	String poi;
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocType() {
		return locType;
	}
	public void setLocType(String locType) {
		this.locType = locType;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getAddrStr() {
		return addrStr;
	}
	public void setAddrStr(String addrStr) {
		this.addrStr = addrStr;
	}
	public String getLocationDescribe() {
		return locationDescribe;
	}
	public void setLocationDescribe(String locationDescribe) {
		this.locationDescribe = locationDescribe;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getPoi() {
		return poi;
	}
	public void setPoi(String poi) {
		this.poi = poi;
	}
	@Override
	public String toString() {
		return "{\"time\":\"" + time + "\", \"locType\":\"" + locType
				+ "\", \"latitude\":\"" + latitude + "\", \"longitude\":\"" + longitude
				+ "\",\"radius\":\"" + radius + "\", \"countryCode\":\"" + countryCode
				+ "\",\"country\":\"" + country + "\",\"city\":\"" + city + "\", \"district\":\""
				+ district + "\", \"street\":\"" + street + "\", \"addrStr\":\"" + addrStr
				+ "\",\"locationDescribe\":\"" + locationDescribe + "\", \"direction\":\""
				+ direction + "\",\"poi\":\"" + poi + "\"}";
	}


	
	
}
