package mini_c;

// TODO: check if there are error's which could say the line and that
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
		/** RTL */
		RTLfile rfile= f.toRTL();
		/** ERTL */
		ERTLfile erFile = new ERTLfile();
		erFile.convertRTLfile(rfile);
		erFile.print();
		/** LTL */
		LTLfile ltlfile = new LTLfile();
		ltlfile.convertERTLfile(erFile);
		ltlfile.print();
		/** asm */
		X86_64 asm = ltlfile.linearize();
		String asmCode = "asm.o";
		asm.print();
		//asm.printToFile(asmCode);
	}
}

/* Typical code you don't want to delete in case it is usefull later but you never use it
		for (Def d: f.l)
			//Interp.functions.put(d.f, d);
		try {
			f.s.accept(new Interp());
		} catch (Error e) {
			System.out.println("error: " + e.toString());
			System.exit(1);
		}*/