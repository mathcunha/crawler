package br.mia.unifor.crawler.parser.statments;

import java.util.ArrayList;
import java.util.List;




public class StringStatement implements Statement {
	private String value;
	
	public StringStatement (String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}

	public String toFile() {
		return value+"\n";
	}

	public List<Statement> getStatements() {
		List<Statement> retorno = new ArrayList<Statement>(1);
		retorno.add(this);
		return retorno;
	}
}
