
The directory contains information about value sets that are used by the Vocabulary Validator which is part of the Reference C-CDA validator.
The information presented here would be useful for vendors setting up their local versions of the validator. 

1. The value_set_list_with_code_count_version.xsls file provides a list of all the value sets configured within the C-CDA validator
and the sources from where they were obtained. Except a few value sets as identified, the rest of them are available for download from VSAC.

2. About 6 value sets were hand created by the ONC contractor team using publicly available information. These value sets are published
under the folder ValueSetsHandCreatedBySITE. While ONC is not the authoritative source for any of these value sets, these were hand created
by the team to proceed with certification testing with the best available public information. 
Vendors using these value sets should refer back to the authoritative sources published in both the word document (ValueSets_Instructions) and the Excel file (value_set_list_with_code_count.xslx) 
to ensure they are in compliance with licensing agreements and laws for reusing the data.

3. There are a couple of value stes that vendors would have to hand create if they can obtain the data using the links provided in the instructions document.
In order to do so, the ValueSet_format_Template.xls file would be necessary and hence it has been provided. 

4. Each value set in the value_set_list_with_code_count_version.xlsx has been annotated with how many codes are present based on the ONC contractor 
team's configuration. This could be used a reference to validate that vendors have the same number of codes for each value set and identify 
any discrepancies to improve the value sets if they change over time.

5. The Code Systems.xlsx file contains the code systems loaded and the reference files and columns used to load the data into the vocabulary 
validator.


