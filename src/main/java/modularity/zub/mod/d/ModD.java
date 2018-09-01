package modularity.zub.mod.d;

import modularity.zub.contract.Contract;
import modularity.zub.mod.c.ModC;

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
