The directory contains information about value sets that are used by the Vocabulary Validator which is part of the Reference C-CDA validator. The information presented here would be useful for vendors setting up their local versions of the validator. 

1. The value_set_list_with_code_count_version.xlsx file provides a list of all the value sets configured within the C-CDA validator and the sources from where they were obtained. Except a few value sets as identified, the rest of them are available for download from VSAC.

2. About 4 value sets were hand created by the ONC contractor team using publicly available information. These value sets are published
under the folder ValueSetsHandCreatedBySITE. While ONC is not the authoritative source for any of these value sets, these were hand created by the team to proceed with certification testing with the best available public information. Vendors using these value sets should refer back to the authoritative sources published in both the word document (ValueSets_Instructions) and the Excel file (value_set_list_with_code_count.xlsx) to ensure they are in compliance with licensing agreements and laws for reusing the data.

3. There are a couple of value sets that vendors would have to hand create if they can obtain the data using the links provided in the instructions document. In order to do so, the ValueSet_format_Template.xls file would be necessary has been provided. 

4. Each value set in the value_set_list_with_code_count_version.xlsx has been annotated with how many codes are present based on the ONC contractor team's configuration. This could be used aa a reference to validate that vendors have the same number of codes for each value set and identify any discrepancies to improve the value sets if they change over time.

5. The Code Systems.xlsx file contains the code systems loaded and the reference files and columns used to load the data into the vocabulary validator.

Example File Structure
Note: This is for structural reference only. The filenames provided may not match the latest version of the C-CDA validator.

.
|-- code_respository
    |-- CDT
        |-- c_CDT2016ExcelMasterForASCII_revised2015May12_DBCQ.xlsx
    |-- CPT
        |-- LONGULT.txt
        |-- MEDU.txt
        |-- SHORTU.txt
    |-- HCPCS
        |-- HCPC2018_CONTR_ANWEB.txt
    |-- ICD9CM_DX
        |-- CMS32_DESC_LONG_SHORT_DX.txt
    |-- ICD9CM_SG
        |-- CMS32_DESC_LONG_SHORT_SG.txt
    |-- ICD10CM
        |-- icd10cm_order_2017.txt
    |-- ICD10PCS
        |-- icd10pcs_order_2017.txt
    |-- LOINC
        |-- loinc.csv
    |-- RXNORM
        |-- RXNCONSO.RRF
    |-- SNOMED-CT
        |-- sct2_Description_Snapshot-en_US1000124_20170901.txt
|-- valueset_repository
    |-- VSAC
        |-- {place .xlsx files here from ValueSet Authority Center}
