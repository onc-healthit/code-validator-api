package org.sitenv.vocabularies.watchdog;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sitenv.vocabularies.engine.ValidationEngine;

public class DirectoryWatcher {
	
    
    public static void main(String[] args) throws IOException {
        
       ValidationEngine.initialize("/Users/chris/Development/code_repository/");
       
       System.out.println("Validate Code: C-D1619 " + ValidationEngine.validateCode("SNOMED", "C-D1619"));
       
       System.out.println("Validate Code: P1-55336 " + ValidationEngine.validateCode("SNOMED", "P1-55336"));
       
       System.out.println("Validate Code (should fail): C-ABCDE " + ValidationEngine.validateCode("SNOMED", "C-ABCDE"));
       
       System.out.println("Validate Code (should fail): F1-FNAED " + ValidationEngine.validateCode("SNOMED", "F1-FNAED"));
       
       System.out.println("Validate Display Name: Moxostoma carinatum (organism) " + ValidationEngine.validateDisplayName("SNOMED", "Moxostoma carinatum (organism)"));
       
       System.out.println("Validate Display Name: Duodenal ampulla structure (body structure) " + ValidationEngine.validateDisplayName("SNOMED", "Duodenal ampulla structure (body structure)"));
       
       System.out.println("Validate Display Name (should fail): Moxostoma " + ValidationEngine.validateDisplayName("SNOMED", "Moxostoma"));
       
       System.out.println("Validate Display Name (should fail): Duodenal " + ValidationEngine.validateDisplayName("SNOMED", "Duodenal"));
    }

}
