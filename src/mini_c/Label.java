package mini_c;

/** étiquette */
public class Label {
	
	private static int next = 0; // Creates a counter
	
	final String name;
	
	Label() {
		next++;
		this.name = "L" + next; 	// Labels variables like L1, L2, L..., Ln
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		Register that = (Register)o;
		return this.name.equals(that.name);
	}
	@Override
	public String toString() {
		return this.name;
	}

}
