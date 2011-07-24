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
	
	private static final String file = "ListItems.txt";
	private static String readFile="ListItems.txt";
	private static String nothingString = "Nothing to vote on!";
	
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
	
	public ListManager(boolean readFromFile) {
		list = new ArrayList<VoteItem>();
		if(readFromFile)
			listSync();
		else {
			fileSync();
			checkEmpty();
		}
	}
	
	public ListManager(String fileName) {
		readFile=fileName;
		listSync();
	}
	
	public void checkEmpty() {
		if(list.size()==0) {
			add(nothingString,"0", nothingString); 
		}
	}
	
	/*
	 * Initializes the ArrayList with the items in the provided array
	 * @param list
	 */
	public synchronized void add(VoteItem [] list) {
		for(int i=0;i<list.length;i++){
			if(list[i]!=null) {
				if(find(list[i].name)==-1)
					this.list.add(list[i]);
			}
		}
		alphaList = new VoteItem[this.list.size()];
		voteList = new VoteItem[this.list.size()];
		fileSync();
		alphaMake();
		voteMake();
	}
	
	public void setFileName(String fileName) {
		readFile=fileName;
		listSync();
	}
	
	/*
	 *  writes the data in the ArrayList into the file
	 *  TODO: Write for minimum file read write.
	 */
	public boolean fileSync() {
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for(int i=0;i<list.size();i++) {
				writer.write(list.get(i).name + "\n" + list.get(i).trigger + "\n" + list.get(i).id);
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
	public synchronized boolean listSync() {
		File f=new File(readFile);
		list=new ArrayList<VoteItem>();

		try{
			Scanner s=new Scanner(f);
			//list=new ArrayList<VoteItem>();
			while(s.hasNextLine()) {
				list.add(new VoteItem(s.nextLine(), s.nextLine(), s.nextLine()));
			}
			s.close();
		}
		catch(FileNotFoundException e) {
			System.out.println("error");
			return false;
		}
		readFile="ListItems.txt";
		fileSync();
		checkEmpty();
		return true;
		
	}
	
	/*
	 * adds a VoteItem to the list with s as the name as long as there
	 * is no item already with the same name
	 */
	public synchronized boolean add(String s, String t, String i) {
		alphaList = new VoteItem[list.size()+1];
		voteList = new VoteItem[list.size()+1];
		
		if(find(s) != -1)
			return false;
		
		if(list.size() == 1 && !s.equals(nothingString)){
			if(find(nothingString)!= -1){
				remove(nothingString);
			}
		}
		
		list.add(new VoteItem(s, t, i));
		try{
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(s + "\n" + t);
			writer.newLine();
			writer.close();
		}
		catch(IOException e) {
			return false;
		}
		
		alphaMake();
		voteMake();
		return true;
	}
	
	/*
	 * removes VoteItem with name s from the list
	 * TODO: remove items from arrays as well as list
	 */
	public synchronized boolean remove(String s) {
		int index=find(s);
		if(index!=-1) {
			float temp=list.get(index).vote;
			String trigger=list.get(index).trigger;
			String id = list.get(index).id;
			list.remove(index);
			//if fileSync fails reset list
			if(!fileSync()) {
				VoteItem v=new VoteItem(s, trigger, id);
				v.vote=temp;
				list.add(index, v);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public int find(String id) {
		for(int i=0;i<list.size();i++) {
			if(list.get(i).id.equals(id)) {
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
	
	public synchronized boolean vote(String s, Float value) {
		int index=find(s);
		if(index!=-1) {
			list.get(index).vote+= value;
			return true;
		}
		return false;
	}
	
	public synchronized void unVote(String s, Float value){
		int index=find(s);
		if(index!=-1) {
			list.get(index).vote-=value;
		}
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
	
	public VoteItem getVote(int i){
		return voteList[i];
	}
	
	public synchronized void decayAll(){
		for(int i = 0; i < list.size(); i++){
			list.get(i).vote *= .9;
		}
	}
	
	
	public int nowPlayingIndex(){
		for(int i=0; i< list.size(); i++){
			if(voteList[0] == alphaList[i])
				return i;
		}
		return -1;
	}
	
}
