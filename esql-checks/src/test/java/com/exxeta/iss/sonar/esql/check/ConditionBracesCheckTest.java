/**
 * This java class is created to implement the logic for checking if braces for conditions are used or not, 
 * if it is not used then it should be inserted.
 * 
 * 
 * @author Prerana Agarkar
 *
 */

package com.exxeta.iss.sonar.esql.check;

import java.io.File;

import org.junit.Test;

import com.exxeta.iss.sonar.esql.checks.verifier.EsqlCheckVerifier;

public class ConditionBracesCheckTest {
  @Test
  public void test() {
		 
		 EsqlCheckVerifier.issues(new ConditionBracesCheck(), new File("src/test/resources/conditionBraces.esql"))
		    .next().atLine(4).withMessage("Use braces for conditions as it gives more readability to code.")
		    .next().atLine(8).withMessage("Use braces for conditions as it gives more readability to code.")
	        .next().atLine(13).withMessage("Use braces for conditions as it gives more readability to code.")
	        .next().atLine(17).withMessage("Use braces for conditions as it gives more readability to code.")
	        .next().atLine(20).withMessage("Use braces for conditions as it gives more readability to code.")
		 .noMore();
	  }
   
    
  }

