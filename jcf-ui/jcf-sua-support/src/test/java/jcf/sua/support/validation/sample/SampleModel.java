package jcf.sua.support.validation.sample;

import org.hibernate.validator.constraints.NotEmpty;

public class SampleModel {
	private String type;

	private String id;

	private String name;

	private int num;

	private double num2;

	@NotEmpty
	private String test;

	@NotEmpty
	private String test2;

	public SampleModel() {
	}

	public SampleModel(String type, String id, String name, int num) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public double getNum2() {
		return num2;
	}

	public void setNum2(double num2) {
		this.num2 = num2;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getTest2() {
		return test2;
	}

	public void setTest2(String test2) {
		this.test2 = test2;
	}

}
