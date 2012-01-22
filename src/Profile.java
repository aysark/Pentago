import java.util.Scanner;

//import java.awt.*;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.*;
//import javax.swing.*;

public class Profile
{
	protected String playerName;

	protected int wins;

	protected int losses;

	protected int ties;

	protected int bestTime;
	protected int moveCount;
	protected int avatar; // 0 = male icon, 1 = female icon..etc

	// creates a new profile
	public Profile(String name, int avatar)
	{
		this.playerName = name;
		this.wins = 0;
		this.losses = 0;
		this.ties = 0;
		this.bestTime = 0;
		this.moveCount=0;

		if (avatar == 0)
			this.avatar = 0;
		else if (avatar == 1)
			this.avatar = 1;
		else if (avatar == 2)
			this.avatar = 2;
		else if (avatar == 3)
			this.avatar = 3;
		else if (avatar == 4)
			this.avatar = 4;
		else if (avatar == 5)
			this.avatar = 5;
		else if (avatar == 6)
			this.avatar = 6;
		else{
			this.avatar=7;
		}

	}
	// loads a profile from txt file
	public Profile(Scanner file)
	{
		this.playerName =  file.nextLine();
		this.wins =  file.nextInt();
		this.losses =  file.nextInt();
		this.ties = file.nextInt();
		this.avatar = file.nextInt();
		this.bestTime = file.nextInt();
		file.nextLine();
	}

	// loads a profile from txt file
	public Profile(String name, int wins, int losses, int ties, int avatar,
			int bestTime)
	{
		this.playerName = name;
		this.wins = wins;
		this.losses = losses;
		this.ties = ties;
		this.avatar = avatar;
		this.bestTime = bestTime;
	}

	public void win()
	{
		this.wins++;
		
	}

	public void loss()
	{
		this.losses++;
	}

	public void tie()
	{
		this.ties++;
	}

	public void bestTime(int currentTime)
	{
		if (this.bestTime > currentTime)
			this.bestTime = currentTime;
	}

	public int winRatio()
	{
		if (this.wins != 0)
		{
			double winRatio =(this.wins*100) / ((this.wins + this.losses + this.ties) *100);
			return (int) winRatio;
		}
		else
		{
			return -1;
		}
	}

	public int hashCode()
	{
		int total = 0;
		char ch;

		for (int index = 0; index < this.playerName.length(); index++)
		{
			ch = this.playerName.charAt(index);
			total = ch + (total * 100);
		}
		return total;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof Profile))
		{
			return false;
		}
		final Profile other = (Profile) obj;

		if (other.playerName.equals(this.playerName))
			return true;
		return false;
	}

	public String winRatioStr()
	{
		String output = "";
		if (this.winRatio() == -1)
		{
			output = "Win Ratio: --%";
		}
		else
		{
			output = "Win Ratio: " + this.winRatio() + "%";
		}
		return output;
	}

	public String totalGames()
	{
		int total = this.wins + this.losses + this.ties;
		String output = "Total Games Played: ";
		if (total == 0)
		{
			return output + 0;
		}
		else
		{
			return output + total;
		}
	}

	public String toString()
	{
		StringBuilder output = new StringBuilder();

		output.append("\r\n"+this.playerName);
		output.append("\r\n");
		output.append(this.wins + " " + this.losses+ " " + this.ties + " "+this.avatar+"\r\n");
		output.append(this.bestTime);
		return output.toString();
	}
}
