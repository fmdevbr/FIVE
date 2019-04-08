/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.classification.google;

import java.io.File;	
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class GoogleParameters {

	/**
	This creates the output mp3 file based off the text contents and the language.
	@param destiantion - the requestesd name for the output mp3 file
	       language - the language code of the text (en, es, zh, etc) 
		   snippets - a list of parameter strings that ar eeach less than or
					  equal to 100 characters
	**/
	private static void makeAudio(String destination, String language,
			List<String> snippets) {
		try {
			//byte array that is 1 MB of the file
			byte[] buffer = new byte[1 << 20];
			//This is the output stream that will create the mp3 and store it in the directory
			//after the program runs. notice that there is no second parameter so it overwrites
			//the previous mp3 with the same name if it exists prior to this function call
			OutputStream os = new FileOutputStream(new File(destination+ ".mp3"));
			//should append to the output file or not
			boolean shouldAppend = true;
			//input stream where you get the mp3 from
			InputStream in = null;
			for (String snippet : snippets) {
				//create a URLConnection with the language and snippet of text with spaces being '+' 
				URLConnection connection = new URL("http://www.translate.google.com/translate_tts?tl="+ language + "&q=" + snippet).openConnection();
				//simulate a browser because Google doesn't let you get the mp3 without being able to identify it first 
				connection.setRequestProperty("User-Agent",
				"Chrome (compatible; MSIE 6.0; Windows 7; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
				//connect to the connection
				connection.connect();
				//get the data from the connection
				in = connection.getInputStream();
				int count;
				//while there is data from the input, 
				while ((count = in.read(buffer)) != -1) {
					//write to the output file
					os.write(buffer, 0, count);
					os.flush();
				}
				//close the input stream
				in.close();
				//set os to a fileoutputstream that will append to the destination.mp3 file
				//you only need to do this once since you reuse the same reference
				if (shouldAppend) {
					os = new FileOutputStream(new File(destination + ".mp3"),true);
					//no longer need to specify that os is FileOutputStream that appends to 
					//the file
					shouldAppend = false;
				}
			}
			//close the input and output streams
			in.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	This takes in a string of text, converts it to URL parameter form by replacing all the spaces with '+', and separates them into an ArrayList of strings with each string in the ArrayList having a max of 100 characters including the '+'. 
	@param text - A string that you want to be converted to a mp3 using Google's text to speech
	**/
	private static ArrayList<String> getParams(String text) {
		//replace all the spaces with '+' 
		String paramText = text.replaceAll(" ", "+");
		//starting index of the current substring in relation to the string passed in
		int startIndex = 0;
		//list of strings that will contain strings that are <=100 characters 
		ArrayList<String> result = new ArrayList<String>();
		//keep looping while the startIndex variable is not the last character in the string
		while (startIndex < text.length() - 1) {
			//get the end index of the substring, which is one plus the index of the 
			//last character that is <=100 characters away from the start of the substring 
			//that is not a '+' (index + 1 because the endIndex paramter of the 
			//String.substring(int,int) is non-inclusive  
			int endSnippetIndex = getSnippetEnd(paramText, startIndex);
			//Grab the snippet from the text parameter using the start and end index you found
			String snippet = paramText.substring(startIndex, endSnippetIndex);
			//ensure that the snippet of text has at least 2 characters 
			//(sometimes there may be a '+' at the beginning of the substring)
			if (snippet.length() >= 2) {
				//if the first character is a '+', remove it
				if (snippet.substring(0, 1).equals("+")) {
					snippet = snippet.substring(1);
				}
			}
			//add the snippet to the results array
			result.add(snippet);
			//set the startIndex to the endSnippet 
			startIndex = endSnippetIndex;
		}
		//once the text has been converted into a list of <=100 character snippets, return the list
		return result;
	}

	private static int getSnippetEnd(String text, int startIndex) {
		//set the substring; if there is still 100 characters left in the origial text starting from the 
		//startIndex, set the substring to be 100 cahracters. otherwise, just get the rest of the string 
		//that is left starting at the startIndex
		String subtext = (text.substring(startIndex).length() > 100) ? 
		                   text.substring(startIndex, startIndex + 100) : 
		                   text.substring(startIndex);
		//the end index initially set as the length of the substring (you dont subtract by 1 because this is used 
		//as the 2nd parameter of the String.substring(int,int) method used in getParams(String) to create the 
		//substring to add to the list
		int end = subtext.length();
		//if there is <=100 characters in the substring, you are done. Otherise, you have to make sure that you 
		//aren't in the middle of the word. IF you are, then decrement the 'end' index until you reach a '+' sigh. 
		//Then simply remove the '+' sign, and you will not have any problems with word being cut off in the middle
		//whlie parsing
		if (text.substring(startIndex).length() > 100) {
				//while the last character is not a + sign, remove the last character of the substring
				while (!subtext.substring(subtext.length() - 1, subtext.length()).equals("+")) {
					subtext = subtext.substring(0, subtext.length() - 1);
					end--;
				}
				// get rid of the last plus sign
				subtext = subtext.substring(0, subtext.length() - 1);
				end--;
		}
		//return the end index of the substtring and offset it by the starting index
		return end + startIndex;
	}
	public static void textToSpeech(String text, String language, String outputName){
		//convert the string to have '+' where there are spaces and create 100 charcter 
		//or less snippets based off the string
		ArrayList<String> paramSnippets = getParams(text);
		//print out the snippets
		for (String snippet : paramSnippets) {
			System.out.println(snippet);
			System.out.println("------------------------------------------");
		}
		//create the mp3 file that will have an audio version of the text 
		makeAudio(outputName, language, paramSnippets);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "This is a test for google translate text to speech functionality. As you can see, this is over the 100 character limit, but it is no problem if this text is separated into 100 character or less snippets. Also, running this program again with the same destination file name but different text will overwrite the data inside of this mp3 file. I hope you enjoyed this tutorial!";
		textToSpeech(text, "en", "testOne");
	}

}