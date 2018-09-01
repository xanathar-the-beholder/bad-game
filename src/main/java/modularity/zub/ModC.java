package modularity.zub;

public class ModC implements Contract {

	private ModA ma;
	private ModB mb;

	public ModC(ModA ma, ModB mb) {
		this.ma = ma;
		this.mb = mb;
	}
	
	@Override
	public int j(int a) {
		return ma.a(a) + mb.a(a);
	}
	
}
