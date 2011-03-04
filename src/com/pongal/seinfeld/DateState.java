package com.pongal.seinfeld;

import com.pongal.seinfeld.data.Date;

public class DateState {

	private Status status;
	private Date date;

	public DateState(Date date) {
		this.status = Status.NORMAL;
		this.date = date;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void toggleSelected() {
		status = status.toggle();
	}

	@Override
	public String toString() {
		return "DateState [data=" + date + ", selected=" + status
				+ "]";
	}

	public static enum Status {
		DISABLED {
			Status toggle() {
				return DISABLED;
			}
		},
		SELECTED {
			Status toggle() {
				return NORMAL;
			}
		},
		NORMAL {
			Status toggle() {
				return SELECTED;
			}
		};

		abstract Status toggle();
	}

}
