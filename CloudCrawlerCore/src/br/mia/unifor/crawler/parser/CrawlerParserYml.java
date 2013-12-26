package br.mia.unifor.crawler.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

import br.mia.unifor.crawler.parser.statments.ForStatement;
import br.mia.unifor.crawler.parser.statments.IncludeStatement;
import br.mia.unifor.crawler.parser.statments.Statement;
import br.mia.unifor.crawler.parser.statments.StringStatement;

public class CrawlerParserYml {

	final static String FOR_INI = "!foreach";
	final static String INCLUDE_INI = "!include";

	InputStream in;

	public static void main(String... aArgs) throws IOException {
		CrawlerParserYml parser = new CrawlerParserYml(new FileInputStream("C:\\Temp\\template.yml"));
		parser.processLineByLine();
		log("Done.");
	}

	/**
	 * Constructor.
	 * 
	 * @param aFileName
	 *            full name of an existing, readable file.
	 */
	public CrawlerParserYml(InputStream in) {
		this.in = in;
	}

	/**
	 * Template method that calls {@link #processLine(String)}.
	 * 
	 * @throws IOException
	 */
	public InputStream processLineByLine() throws IOException {
		// Note that FileReader is used, not File, since File is not Closeable
		File file = new File(System.currentTimeMillis() + ".yml");
		FileWriter wrt = new FileWriter(file);
		Scanner scanner = new Scanner(in);

		Deque<Integer> pilhaIndex = new ArrayDeque<Integer>();
		Deque<Statement> pilha = new ArrayDeque<Statement>();
		Deque<String> pilhaCommando = new ArrayDeque<String>();
		int statementIndex = -1;

		try {
			// first use a Scanner to get each line
			int index = 0;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (-1 != (index = line.indexOf(FOR_INI))) {
					statementIndex = index;
					pilhaCommando.add(FOR_INI);
					pilhaIndex.add(index);
					pilha.add(new StringStatement(line));
				} else if(-1 != (index = line.indexOf(INCLUDE_INI))){
					statementIndex = index;
					pilhaCommando.add(INCLUDE_INI);
					pilhaIndex.add(index);
					pilha.add(new StringStatement(line));
				}
				else if (!pilhaIndex.isEmpty() && -1 == (index = line
						.indexOf(getSpaceString(statementIndex))) || index > 0) {
					statementIndex = unpack(pilhaIndex, pilha, pilhaCommando,
							statementIndex, line);

				} else {
					pilha.add(new StringStatement(line));
				}
			}

			statementIndex = unpack(pilhaIndex, pilha, pilhaCommando,
					statementIndex, null);

			for (Statement statement : pilha) {
				wrt.write(statement.toFile());
			}

		} finally {
			scanner.close();
			wrt.close();

		}
		log(file.getAbsolutePath());
		return new FileInputStream(file);
	}

	private int unpack(Deque<Integer> pilhaIndex, Deque<Statement> pilha,
			Deque<String> pilhaCommando, int statementIndex, String line) {
		if (!pilhaCommando.isEmpty()) {
			pilha.add(executeCommand(pilha, pilhaCommando.removeLast()));

			if (line != null)
				pilha.add(new StringStatement(line));

			pilhaIndex.removeLast();

			if (!pilhaIndex.isEmpty())
				statementIndex = pilhaIndex.getLast();
		}

		return statementIndex;
	}

	protected Statement executeCommand(Deque<Statement> deque, String command) {
		List<Statement> list = new ArrayList<Statement>();
		Statement line = deque.removeLast();

		if(command != null){
			int index = line.toString().indexOf(command);
	
			while (-1 == index) {
				list.add(line);
				line = deque.removeLast();
				index = line.toString().indexOf(command);
			}
			list.add(line);
		}

		if (FOR_INI.equals(command)) {
			return executeFor(list);
		}else if(INCLUDE_INI.equals(command)){
			return executeInclude(list);
		}
		
		return null;
	}
	
	protected IncludeStatement executeInclude(List<Statement> list) {
		log(list);

		return new IncludeStatement(list);
	}

	protected ForStatement executeFor(List<Statement> list) {
		log(list);

		return new ForStatement(list);
	}

	private String getSpaceString(int count) {
		String retorno = "";
		for (int i = 0; i < count; i++) {
			retorno += " ";
		}

		return retorno;
	}

	protected String betweenQuotationMarks(String value) {
		Scanner scanner = new Scanner(value);
		scanner.useDelimiter("\"");

		return scanner.next();
	}

	private static void log(Object aObject) {
		System.out.println(String.valueOf(aObject));
	}

	private String quote(String aText) {
		String QUOTE = "'";
		return QUOTE + aText + QUOTE;
	}
}