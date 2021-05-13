package it.polito.tdp.borders.model;

public class Border {
	private Country state1;
	private Country state2;
	
	public Border(Country state1, Country state2) {
		this.state1 = state1;
		this.state2 = state2;
	}

	public Country getState1() {
		return state1;
	}

	public void setState1(Country state1) {
		this.state1 = state1;
	}

	public Country getState2() {
		return state2;
	}

	public void setState2(Country state2) {
		this.state2 = state2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state1 == null) ? 0 : state1.hashCode());
		result = prime * result + ((state2 == null) ? 0 : state2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Border other = (Border) obj;
		if (state1 == null) {
			if (other.state1 != null)
				return false;
		} else if (!state1.equals(other.state1))
			return false;
		if (state2 == null) {
			if (other.state2 != null)
				return false;
		} else if (!state2.equals(other.state2))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.state1.getName() +" - " +this.state2.getName();
	}
}
