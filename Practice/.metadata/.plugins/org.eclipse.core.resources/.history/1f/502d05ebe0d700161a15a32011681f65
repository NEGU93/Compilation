package mini_python;

public class Main {

	public static void main(String[] args) throws Exception {
		String file = args.length > 0 ? args[0] : "test.py";
		java.io.Reader reader = new java.io.FileReader(file);
		Lexer lexer = new MyLexer(reader);
		MyParser parser = new MyParser(lexer);
		File f = (File) parser.parse().value;
		for (Def d: f.l)
			Interp.functions.put(d.f, d);
		try {
			f.s.accept(new Interp());
		} catch (Error e) {
			System.out.println("error: " + e.toString());
			System.exit(1);
		}
	}

}
