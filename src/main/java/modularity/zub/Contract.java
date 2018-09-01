package modularity.zub;

public interface Contract {

	default int a(int a) {
		return b(a)+1;
	}

	default int b(int a) {
		return c(a)+1;
	}

	default int c(int a) {
		return d(a)+1;
	}
	
	default int d(int a) {
		return e(a)+1;
	}
	
	default int e(int a) {
		return f(a)+1;
	}
	
	default int f(int a) {
		return g(a)+1;
	}
	
	default int g(int a) {
		return h(a)+1;
	}
	
	default int h(int a) {
		return i(a)+1;
	}
	
	default int i(int a) {
		return j(a)+1;
	}
	
	int j(int a);
}
