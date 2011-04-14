
class VoteItem{
	//may add a trigger number or a trigger array that would coinside with appropriate pages
	String name;
	public float vote;
	String trigger;
	VoteItem(String n, String t)
	{
		name = n;
		vote = 0;
		trigger = t;
	}
}