package com.github.sql.analytic.session;

public class ParamNamePosition {
	private String name;
	private int position;
	
	public ParamNamePosition(String name, int position) {
		super();
		this.name = name;
		this.position = position;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

}
