# Paraphrasing-Tool
Paraphrasing tool that takes in any text file and generates a new file by replacing original content with matching synonyms.    
Users can change the threshold which determines how much influence the tool will have on the original text.  
By default the program will only analyze words that have a length of more than 4 characters.  
  
Performance was improved by about 60% from original implementation, due to introducing Java Concurrency and Hash Table.
Jsoup was utitlized for extracting data from thesaurus.com 
