package jcf.dao.ibatis.typehandler;

public class Type {
	private int id;					// int
	private String name;			// varchar -> varchar2
	private String nickName;		// varchar2
	private String address;		// clob
	private String homeTown;		// char
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getHomeTown() {
		return homeTown;
	}


	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
	}


	public Type() {
	}
	
	public Type(int id, String name, String nickName, String address,
			String homeTown) {
		super();
		this.id = id;
		this.name = name;
		this.nickName = nickName;
		this.address = address;
		this.homeTown = homeTown;
	}
	
	
	public String toString() {
		return "id: " + this.id + ", name: " + this.name + ", nickName: " 
				+ this.nickName + ", address: " + this.address + ", homeTown: " 
				+ this.homeTown;
	}
}
