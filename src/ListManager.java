/*
 * ListManager.java
 * Contains a List of VoteItems and will manage storing the list in a file, 
 * as well as the function to keep the data up to date in the file.
 * 
 * Creation: 3/12/2011
 * Author: Tyler Holzer
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ListManager {
	
	private final String FILE = "ListItems.txt";
	private ArrayList<VoteItem> list;
	
	/*
	 * Initialized the ArrayList to be an empty list with space for 10 items.
	 */
	public ListManager() {
		list = new ArrayList<VoteItem>();
		fileSync();
	}
	
	public ListManager(boolean readFromFile) {
		list = new ArrayList<VoteItem>();
		listSync();
	}
	
	/*
	 * Initializes the ArrayList with the items in the provided array
	 * @param list
	 */
	public ListManager(VoteItem [] list) {
		this.list = new ArrayList<VoteItem>(list.length);
		for(int i=0;i<list.length;i++){
			if(list[i]!=null) {
				this.list.add(list[i]);
			}
		}
		fileSync();
	}
	
	/*
	 *  writes the data in the ArrayList into the file
	 *  TODO: Write for minimum file read write.
	 */
	public boolean fileSync() {
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE));
			for(int i=0;i<list.size();i++) {
				writer.write(list.get(i).name);
				writer.newLine();
			}
			writer.close();
		}
		catch(IOException e) {
			return false;
		}
		return true;
	}
	
	/*
	 * writes the data in the file into the ArrayList
	 * NOTE: removes all vote values
	 * TODO: Write a smart function that does not completely rewrite the array list
	 */
	public boolean listSync() {
		File f=new File(FILE);
		try{
			Scanner s=new Scanner(f);
			list=new ArrayList<VoteItem>();
			while(s.hasNextLine()) {
				list.add(new VoteItem(s.nextLine()));
			}
			s.close();
		}
		catch(FileNotFoundException e) {
			return false;
		}
		return true;
		
	}
	
	/*
	 * adds a VoteItem to the list with s as the name
	 */
	public boolean add(String s) {
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE,true));
			writer.write(s);
			writer.newLine();
			writer.close();
		}
		catch(IOException e) {
			return false;
		}
		list.add(new VoteItem(s));
		return true;
	}
	
	/*
	 * removes VoteItem with name s from the list
	 */
	public boolean remove(String s) {
		int index=find(s);
		if(index!=-1) {
			float temp=list.get(index).vote;
			list.remove(index);
			//if fileSync fails reset list
			if(!fileSync()) {
				VoteItem v=new VoteItem(s);
				v.vote=temp;
				list.add(index, v);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public int find(String s) {
		for(int i=0;i<list.size();i++) {
			if(list.get(i).name.equals(s)) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * sets vote value to 0 for item with name s
	 */
	public boolean clearVote(String s) {
		int index = find(s);
		if(index==-1) {
			return false;
		}
		list.get(index).vote=0;
		return true;
	}
	
	/*
	 * sets all items votes to 0
	 */
	public void clearVotes() {
		for(int i=0;i<list.size();i++) {
			list.get(i).vote=0;
		}
	}
	
	public VoteItem get(int i) {
		return list.get(i);
	}
	
	public int getListSize() {
		return list.size();
	}
	
	public boolean vote(String s) {
		int index=find(s);
		if(index!=-1) {
			list.get(index).vote++;
			return true;
		}
		return false;
	}
	
}
