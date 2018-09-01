package modularity.zub;

public class ModB implements Contract {

	private ModA ma;

	ModB(ModA a) {
		this.ma = a;
	}
	
	@Override
	public int j(int a) {
		return ma.a(a);
	}

}
