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
	
	
	/*
	 * list holds the vote items, alphaList and voteList allow access in different orders without
	 * interfering with any values
	 */
	
	private ArrayList<VoteItem> list;
	private VoteItem alphaList[];
	private VoteItem voteList[];
	
	/*
	 * Initialized the ArrayList to be an empty list with space for 10 items.
	 */
	
	public ListManager() {
		list = new ArrayList<VoteItem>();
		fileSync();
	}
	
	public ListManager(boolean readFromFile) {
		System.out.println("reading from file");
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
	 * only adds an element to the ArrayList if there is no item
	 * with the same name already in the list
	 * 
	 * TODO: maybe find a better way of checking against list
	 */
	public boolean listSync() {
		File f=new File(FILE);

		try{
			Scanner s=new Scanner(f);
			//list=new ArrayList<VoteItem>();
			while(s.hasNextLine()) {
				list.add(new VoteItem(s.nextLine()));
				System.out.println("here");
			}
			s.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("error");
			return false;
		}
		return true;
		
	}
	
	/*
	 * adds a VoteItem to the list with s as the name as long as there
	 * is no item already with the same name
	 */
	public synchronized boolean add(String s) {
		alphaList = new VoteItem[list.size()+1];
		voteList = new VoteItem[list.size()+1];
		
		try{
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE,true));
			writer.write(s);
			writer.newLine();
			writer.close();
		}
		catch(IOException e) {
			return false;
		}
		if(find(s) == -1)
			list.add(new VoteItem(s));
		
		alphaMake();
		voteMake();
		return true;
	}
	
	/*
	 * removes VoteItem with name s from the list
	 * TODO: remove items from arrays as well as list
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
	
	public synchronized boolean vote(String s) {
		int index=find(s);
		if(index!=-1) {
			list.get(index).vote++;
			return true;
		}
		return false;
	}
	
	
	/*
	 * fills and sorts an array with pointers in alphabetical order
	 */
	private void alphaMake(){
		VoteItem tmp;
		for(int i = 0; i < list.size(); i++){
			alphaList[i] = list.get(i);
		}
		
		for(int i = 0; i < list.size() - 1; i++){
			for(int j = 0; j < list.size()- i - 1; j++){
				if(alphaList[j].name.compareToIgnoreCase(alphaList[j+1].name) > 0)
				{
					tmp = alphaList[j];
					alphaList[j] = alphaList[j+1];
					alphaList[j+1] = tmp;
				}
			}
		}
	}
	
	/*
	 * fills the array with all of the list items
	 * note: not sorted here because the votes will all be zero when the array is created
	 * and voteThread takes care of ensuring sorted order
	 */
	private void voteMake(){
		
		for(int i = 0; i < list.size(); i++){
			voteList[i] = list.get(i);
		}
	}
	
	
	/*
	 * Sorts voteList in order of descending vote counts
	 */
	public boolean sortVote(){
		VoteItem tmp;
		boolean changed = false;
		for(int i = 0; i < list.size() - 1; i++){
			for(int j = 0; j < list.size()- i - 1; j++){
				if(voteList[j].vote < voteList[j+1].vote)
				{
					tmp = voteList[j];
					voteList[j] = voteList[j+1];
					voteList[j+1] = tmp;
					changed = true;
				}
			}
		}
		return changed;
	}
	
	/*
	 * returns String representation of the desired voteItems voteCount
	 */
	public String getAlphaVote(int index){
		Float votes = new Float(alphaList[index].vote);
		int iVote= Math.round(votes);
		return iVote+"";
	}
	
	public synchronized void decayAll(){
		for(int i = 0; i < list.size(); i++){
			list.get(i).vote *= .9;
		}
	}
	
}
