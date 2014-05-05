package com.gmail.listselect;

public class MyListItem {

	private String name;
	private boolean isChecked = false;

	public boolean isChecked() {
		return isChecked;
	}

	public void setIsChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getTitle() {
		return name;
	}

	public void setTitle(String name) {
		this.name = name;
	}
}
