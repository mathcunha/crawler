package br.mia.unifor.crawler.parser.statments;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.mia.unifor.crawler.parser.CrawlerParserYml;

public class IncludeStatement implements Statement {

	private List<Statement> statements;
	
	public IncludeStatement(List<Statement> list) {
		
		int index = -1;
		Statement statement = list.get(list.size()-2);
		String line = statement.toString();
		String FILE = "file:";
		String file = null;
	
		if (-1 != (index = line.indexOf(FILE))) {
			if (line.substring(0, index).trim().length() == 0) {				
				file = new String(line.substring(index + FILE.length())
						.trim());
			}
		}
		
		try {
			load(file);
		} catch (IOException e) {
			Logger.getAnonymousLogger().log(Level.SEVERE, "erro ao carregar o arquivo", e);
		}
	}
	
	private void load(String fileName) throws IOException{
		InputStream input = this.getClass()
				.getResourceAsStream(fileName);
		if(input == null){			
			input = new FileInputStream(fileName);
		}
		CrawlerParserYml lOnagaParserYml = new CrawlerParserYml(input);

		Scanner scanner = new Scanner(lOnagaParserYml.processLineByLine());
		statements = new ArrayList<Statement>();

		while (scanner.hasNextLine()) {
			statements.add(new StringStatement(scanner.nextLine()));
		}
	}

	public IncludeStatement(String fileName) throws IOException {
		load(fileName);
	}

	public String toFile() {
		StringBuffer buffer = new StringBuffer();

		for (Statement statement : statements) {
			buffer = buffer.append(statement.toFile());
		}

		return buffer.toString();
	}

	public List<Statement> getStatements() {
		return statements;
	}

}
