package com.qkmoc.moc.Bean;

public class InfoToAPPBean {
	private int id;
	private String copytext;

	public InfoToAPPBean() {
		super();
	}

	public InfoToAPPBean(int id, String copytext) {
		super();
		this.id = id;
		this.copytext = copytext;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCopytext() {
		return copytext;
	}

	public void setCopytext(String copytext) {
		this.copytext = copytext;
	}
}
