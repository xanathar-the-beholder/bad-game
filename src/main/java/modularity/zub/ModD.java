package modularity.zub;

public class ModD implements Contract {

	private ModC c;

	public ModD(ModC c) {
		this.c = c;	
	}
	
	@Override
	public int j(int a) {
		return c.j(a);
	}
}
