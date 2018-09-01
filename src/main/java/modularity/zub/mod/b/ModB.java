package modularity.zub.mod.b;

import modularity.zub.contract.Contract;
import modularity.zub.mod.a.ModA;

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
