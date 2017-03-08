package mini_c;


public class Main {

	public static void main(String[] args) throws Exception {
		String file = "test.c";
		for (String s: args) {
			if (s.equals("--parse-only"))
				;
			else if (s.equals("--type-only"))
				;
			else
				file = s;
		}
		java.io.Reader reader = new java.io.FileReader(file);
		Lexer lexer = new Lexer(reader);
		Parser parser = new Parser(lexer);
		File f = (File) parser.parse().value;
		RTLfile rfile= f.toRTL();
		ERTLfile erFile = rfile.toERTL();
		erFile.print();
		/*for (Def d: f.l)
			//Interp.functions.put(d.f, d);
		try {
			f.s.accept(new Interp());
		} catch (Error e) {
			System.out.println("error: " + e.toString());
			System.exit(1);
		}*/
	}
}
