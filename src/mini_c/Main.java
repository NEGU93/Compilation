package mini_c;

import java.io.File;

public class Main {

	public static void main(String[] args) throws Exception {
		String file = args.length > 0 ? args[0] : "test.c";
		java.io.Reader reader = new java.io.FileReader(file);
		Lexer lexer = new Lexer(reader);
		Parser parser = new Parser(lexer);
		File f = (File) parser.parse().value;
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
