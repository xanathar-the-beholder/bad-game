package modularity.zub;

public class ModA implements Contract {

	private ModD d;

	public ModA(ModD d) {
		this.d = d;
	}
	
	@Override
	public int j(int a) {
		return d.a(a);
	}

}
