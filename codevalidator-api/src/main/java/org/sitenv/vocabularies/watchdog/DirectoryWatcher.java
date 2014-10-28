package org.sitenv.vocabularies.watchdog;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sitenv.vocabularies.engine.ValidationEngine;

public class DirectoryWatcher {
	
    
    public static void main(String[] args) throws IOException {
        
       ValidationEngine.initialize("/Users/chris/Development/code_repository/");
       
       System.out.println("Validate Code: 233604007 " + ValidationEngine.validateCodeByCodeSystemName("SNOMED-CT", "233604007"));
       
       System.out.println("Validate Code: 233604007 " + ValidationEngine.validateCode("2.16.840.1.113883.6.96", "233604007"));
       
       System.out.println("Validate Code (should fail): F1-FNAED " + ValidationEngine.validateCodeByCodeSystemName("SNOMED-CT", "F1-FNAED"));
       
       System.out.println("Validate Display Name: Moxostoma carinatum (organism) " + ValidationEngine.validateDisplayNameByCodeSystemName("SNOMED-CT", "Moxostoma carinatum (organism)"));
       
       System.out.println("Validate Display Name: Duodenal ampulla structure (body structure) " + ValidationEngine.validateDisplayNameByCodeSystemName("SNOMED-CT", "Duodenal ampulla structure (body structure)"));
       
       System.out.println("Validate Display Name (should fail): Moxostoma " + ValidationEngine.validateDisplayNameByCodeSystemName("SNOMED-CT", "Moxostoma"));
       
       System.out.println("Validate Display Name (should fail): Duodenal " + ValidationEngine.validateDisplayNameByCodeSystemName("SNOMED-CT", "Duodenal"));
    }

}
