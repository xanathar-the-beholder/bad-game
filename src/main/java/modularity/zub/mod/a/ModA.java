package modularity.zub.mod.a;

import modularity.zub.contract.Contract;
import modularity.zub.mod.d.ModD;

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
