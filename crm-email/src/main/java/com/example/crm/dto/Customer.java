package com.example.crm.dto;

public class Customer {
	private String identity;
	private String fullname;
	private String email;
	private String sms;
	private boolean useEmail;
	private boolean useSms;

	public Customer(String identity, String fullname, String email, String sms, boolean useEmail, boolean useSms) {
		this.identity = identity;
		this.fullname = fullname;
		this.email = email;
		this.sms = sms;
		this.useEmail = useEmail;
		this.useSms = useSms;
	}

	public Customer() {
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public boolean isUseEmail() {
		return useEmail;
	}

	public void setUseEmail(boolean useEmail) {
		this.useEmail = useEmail;
	}

	public boolean isUseSms() {
		return useSms;
	}

	public void setUseSms(boolean useSms) {
		this.useSms = useSms;
	}

	@Override
	public String toString() {
		return "Customer [identity=" + identity + ", fullname=" + fullname + ", email=" + email + ", sms=" + sms
				+ ", useEmail=" + useEmail + ", useSms=" + useSms + "]";
	}

}
