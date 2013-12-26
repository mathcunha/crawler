package br.mia.unifor.crawler.parser.statments;

import java.util.ArrayList;
import java.util.List;

public class ForStatement implements Statement {
	
	private List<Statement> statements = new ArrayList<Statement>();
	public static final String ITEMS = "list:";
	public static final String VAR = "var:";
	public static final String COUNT = "count";
	public static final String COUNT_STATE = COUNT+":";
	public static final String STATE = "statement:";
	private int count;
	private String[] items;
	private String var;

	public ForStatement(List<Statement> list) {
		
		parse(loadValues(list));

	}
	
	private List<Statement> loadValues (List<Statement> list){
		List<Statement> lStatements = new ArrayList<Statement>();

		items = null;
		var = "item";
		count = 0;
		int index = 0;
		boolean statementOpenned = false;

		for (int i = list.size() - 2; i >= 0; i--) {
			Statement statement = list.get(i);
			String line;

			if (statement instanceof StringStatement)
				line = statement.toString();
			else{
				lStatements.add(statement);
				continue;
			}

			if (-1 != (index = line.indexOf(COUNT_STATE))) {
				if (line.substring(0, index).trim().length() == 0) {
					statementOpenned = false;
					count = new Integer(line.substring(index + COUNT_STATE.length())
							.trim());
				}
			} else if (-1 != (index = line.indexOf(VAR))) {
				if (line.substring(0, index).trim().length() == 0) {
					statementOpenned = false;
					var = new String(line.substring(index + VAR.length())
							.trim());
				}
			} else if (-1 != (index = line.indexOf(ITEMS))) {
				if (line.substring(0, index).trim().length() == 0) {
					statementOpenned = false;
					items = new String(line.substring(index + ITEMS.length())
							.trim()).split(",");
				}
			} else if (-1 != (index = line.indexOf(STATE))) {
				statementOpenned = line.substring(0, index).trim().length() == 0;
			} else if (statementOpenned) {
				lStatements.add(statement);
			} else {
				throw new IllegalStateException("not in the correct statement");
			}
		}
		return lStatements;
	}
	
	public void parse(List<Statement> lStatements){
		
		for (String item : items) {
			for (int i = 0; i < lStatements.size(); i++) {
				Statement statement = lStatements.get(i);
				String linha;

				if (statement instanceof StringStatement) {
					linha = statement.toString();
				}else {
					if(lStatements.addAll(i, statement.getStatements())){
						if(statement.getStatements().size() > 0){
							lStatements.remove(i + statement.getStatements().size());
							i--;
						}else{
							this.statements.add(statement);
						}
						
					}
					continue;
				}

				int index = linha.indexOf("$[");
				String strParsed = new String(linha);
				while (index != -1) {
					int indexEnd = strParsed.indexOf("]");
					String method = strParsed.substring(index + "$[".length(),
							indexEnd);

					String value = null;
					if (method.equals(var)) {
						value = item.trim();
					} else if (method.equals(COUNT)) {
						value = count + "";
					}
					if(value != null){
						strParsed = strParsed.substring(0, index)
						+ value.toString()
						+ strParsed.substring(indexEnd + 1);
						
						index = strParsed.indexOf("$[");
					}else{
						index = strParsed.indexOf("$[", index+1);
					}
					

					
				}
				
				this.statements.add(new StringStatement(strParsed));
			}
			count++;
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for (Statement statement : statements) {
			buffer = buffer.append(statement);
		}
		
		return buffer.toString();
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
