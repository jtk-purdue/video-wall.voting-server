import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.junit.Test;


public class ListManagerTest {
	
	@Test
	public void testListManager() {
		assertEquals(new ListManager(false).getListSize(),0);
	}
	
	@Test
	public void testListManagerBoolean() {
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter("ListItems.txt",true));
			writer.write("Garbage 1\n");
			writer.write("stuff 2\n");
			writer.write("AAAAAAAAAAAAA\n");
			writer.close();
			assertEquals(new ListManager(true).getListSize(),3);
		}
		catch(Exception e) {
			fail("Exception caught");
		}
	}

	@Test
	public void testListManagerVoteItemArray() {
		//VoteItem [] array = new VoteItem[5];
		//array[1]=new VoteItem("String 1");
		//array[2]=new VoteItem("String 2");
		//array[3]=new VoteItem("String 3");
		//array[4]=null;
		//ListManager lm=new ListManager(array);
		//assertEquals(lm.getListSize(),3);
		//assertTrue(lm.get(0).name.equals("String 1"));
	}
	
	@Test
	public void testAdd() {
		ListManager lm = new ListManager(false);
		try {
			//BufferedWriter writer=new BufferedWriter(new FileWriter("ListItems.txt"));
			Scanner s=new Scanner(new File("ListItems.txt"));
			//lm.add("String 1");
			//lm.add("String 2");
			//lm.add("String 3");
			String contents="";
			while(s.hasNext()) {
				contents+=s.nextLine()+"\n";
			}
			assertTrue(contents.equals("String 1\nString 2\nString 3\n"));
			assertTrue(lm.get(0).name.equals("String 1"));
			assertTrue(lm.get(1).name.equals("String 2"));
			assertTrue(lm.get(2).name.equals("String 3"));
			
		}
		catch(Exception e) {
			fail("Exception caught");
		}
	}
	
	@Test
	public void testFileSync() {
		ListManager lm = new ListManager(false);
		try {
			Scanner s;
			//lm.add("String 1");
			//lm.add("String 2");
			//lm.add("String 3");
			String contents="";
			BufferedWriter writer=new BufferedWriter(new FileWriter("ListItems.txt",true));
			writer.write("Garbage 1\n");
			writer.close();
			lm.fileSync();
			s=new Scanner(new File("ListItems.txt"));
			while(s.hasNext()) {
				contents+=s.nextLine()+"\n";
			}
			assertTrue(contents.equals("String 1\nString 2\nString 3\n"));
			
		}
		catch(Exception e) {
			fail("Exception caught");
		}
	}
	
	@Test
	public void testListSync(){
		ListManager lm = new ListManager(false);
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter("ListItems.txt",true));
			writer.write("Garbage 1\n");
			writer.write("stuff 2\n");
			writer.write("AAAAAAAAAAAAA\n");
			writer.close();
			lm.listSync();
			String contents="";
			for(int i=0;i<lm.getListSize();i++) {
				contents+=lm.get(i).name+"\n";
			}
			assertTrue(contents.equals("Garbage 1\nstuff 2\nAAAAAAAAAAAAA\n"));
			
		}
		catch(Exception e){
			fail("Exception caught");
		}
	}
	
	@Test
	public void testRemove(){
		ListManager lm = new ListManager(false);
		try {
			Scanner s;
			//lm.add("String 1");
			//lm.add("String 2");
			//lm.add("String 3");
			String contents="";
			assertTrue(lm.remove("String 2"));
			assertTrue(!lm.remove("Gibberish"));
			s=new Scanner(new File("ListItems.txt"));
			while(s.hasNext()) {
				contents+=s.nextLine()+"\n";
			}
			assertTrue(contents.equals("String 1\nString 3\n"));
			assertTrue(lm.get(0).name.equals("String 1"));
			assertTrue(lm.get(1).name.equals("String 3"));
			
		}
		catch(Exception e) {
			fail("Exception caught");
		}
	}
	
	@Test
	public void testFind() {
		ListManager lm = new ListManager(false);
		//lm.add("String 1");
		//lm.add("String 2");
		//lm.add("String 3");
		assertEquals(lm.find("String 1"),0);
		assertEquals(lm.find("Not There"),-1);
	}
	
	@Test
	public void testClearVote() {
		ListManager lm = new ListManager(false);
		//lm.add("String 1");
		lm.get(0).vote=10;
		assertTrue(lm.clearVote("String 1"));
		assertEquals(lm.get(0).vote,0,0);
	}
	
	@Test
	public void testClearVotes() {
		ListManager lm = new ListManager(false);
		//lm.add("String 1");
		//lm.add("String 2");
		lm.get(0).vote=10;
		lm.get(1).vote=20;
		lm.clearVotes();
		assertEquals(lm.get(0).vote,0,0);
		assertEquals(lm.get(1).vote,0,0);
	}

}
