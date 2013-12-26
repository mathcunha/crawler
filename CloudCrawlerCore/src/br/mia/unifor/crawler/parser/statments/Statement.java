package br.mia.unifor.crawler.parser.statments;

import java.util.List;

public interface Statement {
	
	public String toFile();
	
	public List<Statement> getStatements();

}
