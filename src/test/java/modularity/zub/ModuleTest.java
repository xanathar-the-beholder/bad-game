package modularity.zub;

import org.junit.Test;

public class ModuleTest {

	ModA a = new ModA(new ModD(new ModC(null,new ModB(null))));
	
	@Test
	public void test() {
		a.a(1);
	}
	
}
