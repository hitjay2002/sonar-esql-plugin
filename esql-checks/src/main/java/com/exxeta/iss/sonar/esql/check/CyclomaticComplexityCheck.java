/**
 * 
 */
package com.exxeta.iss.sonar.esql.check;



import java.util.ArrayList;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.exxeta.iss.sonar.esql.api.tree.ProgramTree;
import com.exxeta.iss.sonar.esql.api.visitors.DoubleDispatchVisitorCheck;
import com.exxeta.iss.sonar.esql.api.visitors.EsqlFile;
import com.exxeta.iss.sonar.esql.api.visitors.LineIssue;



/**
 * This java class is created to implement the logic for checking cyclomatic complexity of the code.
 * cyclomatic complexity should be less than the threshold value
 * 
 * @author sapna singh
 *
 */
@Rule(key="CyclomaticComplexity")
public class CyclomaticComplexityCheck extends DoubleDispatchVisitorCheck{
	
	private static final String MESSAGE = "Cyclomatic Complexity is higher then the threshold.";
	
	private static final int DEFAULT_COMPLEXITY_THRESHOLD =10;
	
	 @RuleProperty(
			    key = "maximumCyclomaticComplexity",
			    description = "The maximum authorized cyclomatic complexity.",
			    defaultValue = "" + DEFAULT_COMPLEXITY_THRESHOLD)
	 public int maximumCyclomaticComplexity = DEFAULT_COMPLEXITY_THRESHOLD;
	
	
	
@Override
public void visitProgram(ProgramTree tree) {
	EsqlFile file = getContext().getEsqlFile();
	List<String> lines = CheckUtils.readLines(file);
	int i = 0;
	boolean commentSection = false;
	ArrayList <ArrayList<String>> modules = new ArrayList<>();
	ArrayList<String> moduleLines = new ArrayList<>();
	boolean isInsideModule = false;
	for (String line : lines) {
		i = i + 1;
		
		if (!line.trim().startsWith("--") && !line.trim().startsWith("/*") && !commentSection) {
				if(!isBeginStatement(line) && !isEndStatement(line) && isInsideModule){					
					moduleLines.add(line);
				}
				if(isBeginStatement(line.trim())){
					isInsideModule = true;
					moduleLines = new ArrayList<>();
					moduleLines.add(String.valueOf(i));
					moduleLines.add(line);
				}
				if(isEndStatement(line.trim())){
					moduleLines.add(line);
					isInsideModule=false;
					modules.add(moduleLines);
					
				}
		} else if (line.trim().startsWith("/*") && !commentSection && !line.trim().endsWith("*/")) {
			commentSection = true;
		} else if (commentSection && line.trim().endsWith("*/")) {
			commentSection = false;
		}
	}
		for(ArrayList<String> module : modules){
			
			if(CalculateComplexity(module)>maximumCyclomaticComplexity){
				
				addIssue(new LineIssue(this,  Integer.parseInt(module.get(0)), "Check " + ExtractFunctionProcedureName(module.get(1))+ "\". " + MESSAGE));
				
			}
		}

  }

public static boolean isEndStatement(String s)
{
    String withoutSpace = s.replace(" ", "").toUpperCase();
    return withoutSpace.contains("END;");
}

public static boolean isBeginStatement(String s)
{
    String withoutSpace = s.replace(" ", "").toUpperCase();
    return withoutSpace.startsWith("CREATEPROCEDURE") || withoutSpace.startsWith("CREATEFUNCTION");
}

public static int CalculateComplexity(List<String> lines) {
	
	
	int complexity = 1;
	for (String l : lines) {
		
		String upperCase = l.toUpperCase();
		String quotedContentRemoved = CheckUtils.removeQuotedContent(upperCase);
		
		if (quotedContentRemoved.contains("ELSEIF")) {
			int conditionComplexity = calculateConditionESQLComplexity(quotedContentRemoved);
			complexity += conditionComplexity;
		} else if (quotedContentRemoved.contains("IF ")) {
			int conditionComplexity = calculateConditionESQLComplexity(quotedContentRemoved);
			complexity += conditionComplexity;
		} else if (quotedContentRemoved.contains("FOR "))
			complexity++;
		else if (quotedContentRemoved.contains("WHILE ") && !quotedContentRemoved.contains("END")) {
			int conditionComplexity = calculateConditionESQLComplexity(quotedContentRemoved);
			complexity += conditionComplexity;
		} else if (quotedContentRemoved.contains("WHEN "))
			complexity++;
		else if (quotedContentRemoved.contains("REPEAT"))
			complexity++;
		else if (quotedContentRemoved.contains("LEAVE "))
			complexity++;
		else if (quotedContentRemoved.contains("HANDLER "))
			complexity++;
		else if (quotedContentRemoved.contains("ELSE ") || quotedContentRemoved.endsWith("ELSE")) {
			int conditionComplexity = calculateConditionESQLComplexity(quotedContentRemoved);
			complexity += conditionComplexity;
		}
		
	}
	return complexity;
}
	
	
public static  String ExtractFunctionProcedureName(String declareStatement ){

	 String declareStatementSub = declareStatement.substring(0,declareStatement.indexOf('('));
	 StringBuilder name = new StringBuilder();
	 for(String tmp : declareStatementSub.split(" ")){
		if("create".equalsIgnoreCase(tmp)||"function".equalsIgnoreCase(tmp)||"procedure".equalsIgnoreCase(tmp)){
			name.append(tmp.toUpperCase()); 
		}
		else{
			name.append(tmp);
		}
	}
	 
	 if(name.toString().replace(" ", "").startsWith("CREATEFUNCTION")){
		return "Function \""+name.toString().replace(" ", "").replace("CREATEFUNCTION", ""); 
	 }
	 else if(name.toString().replace(" ", "").startsWith("CREATEPROCEDURE")){
		 return "Procedure \""+name.toString().replace(" ", "").replace("CREATEPROCEDURE", "");
		 
	 }
	   
		return name.toString();
		
}
	 
	
	

	public static int countCharacters(String s, String ch) {
		int cnt = 0;
		String[] lines = s.split(ch);
		cnt = lines.length - 1;
		return cnt;
	}

	protected static int calculatleSimple(String line) {
		int complexity = 0;
		complexity += countCharacters(line, "AND");
		complexity += countCharacters(line, "OR");
		return Math.max(1, complexity);
	}
	

	protected static int calculateConditionESQLComplexity(String line) {
		return calculatleSimple(line);
	}

}

	
	
	
	
	
