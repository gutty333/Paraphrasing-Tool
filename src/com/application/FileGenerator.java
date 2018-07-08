package com.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FileGenerator implements Runnable
{
	private static Queue<String> originalWords = new ArrayDeque<>();
	public static String inputName;
	public static String outputName;
	
	// method for reading in the contents of the file
	public static void readFile()
	{
		try(Scanner reader = new Scanner(new File(inputName)))
		{
			while (reader.hasNext())
			{
				// getting the current possible word
				String currentWord = reader.next();
				
				StringBuilder newWord = new StringBuilder();
				
				// validating the string
				for (Character current: currentWord.toCharArray())
				{
					if (Character.isLetter(current))
					{
						newWord.append(current);
					}
					else
					{
						// case conditions when multiple words are read
						// or other special cases such as apostrophes 
						if (newWord.length() > 0)
						{
							originalWords.add(newWord.toString());
						}
						
						originalWords.add(current.toString());
						
						// resetting the new word
						newWord.setLength(0);
					}
				}
				
				// storing the word
				if (newWord.length() > 0)
				{
					originalWords.add(newWord.toString());
					newWord.setLength(0);
				}
				
				// adding space separator
				originalWords.add(" ");
			}
		} 
		catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	// method for writing the content to our output file
	public static void writeFile()
	{
		try(PrintWriter writer = new PrintWriter(new File(outputName)))
		{
			while (!originalWords.isEmpty())
			{
				// threshold condition to limit which words we replace
				// good range is avoid words with lengths less than 3 or 4
				if (originalWords.peek().length() <= 4)
				{
					writer.print(originalWords.remove());
				}
				else
				{
					// section on replacing the original word with a possible synonym
					String result = getSynonym();
					
					// writing to file
					writer.print(result);
				}
			}
		} 
		catch (FileNotFoundException e)
		{
			System.out.println(e.getMessage());
		} 
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}


	// method to analyze the current word and replace it with a random synonym
	private static String getSynonym() throws IOException
	{
		String result = originalWords.remove();
		
		// recording previous state such as capital letters
		boolean capital = Character.isUpperCase(result.charAt(0));
		
		// section on replacing with possible synonyms
		List<String> sysnonymsList = new ArrayList<>();
		
		// utilizing data from public thesaurus
		String url = "http://www.thesaurus.com/browse/" + result;
		Document document = Jsoup.connect(url).get();
		Elements content = document.getElementsByClass("css-1hn7aky e1s2bo4t1");
		
		// collecting the synonyms
		for (Element current : content)
		{
			sysnonymsList.add(current.text());
		}
		
		// either leaving the original in place or selecting a random synonym
		if (sysnonymsList.size() > 0)
		{
			int random = (int) (Math.random() * sysnonymsList.size());
			result = sysnonymsList.get(random);
		}
		
		// check capital letter signal
		if (capital)
		{
			result = result.substring(0,1).toUpperCase() + result.substring(1);
		}
		
		return result;
	}

	@Override
	public void run()
	{
		readFile();
		writeFile();
		
	}
}
