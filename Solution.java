import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Solution {

	static int GOLD_AMOUNT;							    // W
	static int MAX_LEVEL_ALLOWED;						// n
	static int NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL;    // k 
	static Hero[] heros= new Hero[500];
	
	// dynamic approach using knapsack
	public static void dynamicApproach(int k, int W, Hero[] heros) {         
		
		long start = System.nanoTime();
		
		int n = heros.length;
		int[][] V = new int[n + 1][W + 1];
		for (int i = 0; i <= W; i++) {
			V[0][i] = 0;
		}
		for (int i = 0; i <= n; i++) {
			V[i][0] = 0;
		}

		for (int i = 1; i <= n; i++) {
			for (int w = 1; w <= W; w++) {

				int index = findPosition(n, k, i);

				if (heros[i - 1].getPrice() <= w) {
					V[i][w] = Math.max(V[i - 1][w],
							heros[i - 1].getAttackPoint() + V[index - 1][w - heros[i - 1].getPrice()]);
				} else {
					V[i][w] = V[i - 1][w];
				}

			}
		}
		
		int t = W;
		int result = V[n][W];
		int i = n;
		
		System.out.println("Heros: ");
		while (t > 0 && i > 0) {
			if (V[i - 1][t] == result) {
				i--;
				continue;
			} else {

				System.out.println("-"+heros[i - 1].getName() + "(" + heros[i - 1].getTypeName() + ", " + heros[i - 1].getPrice() + " Gold, "
						+ heros[i - 1].getAttackPoint() + " Attack)");
				result = result - heros[i - 1].getAttackPoint();
				t = t - heros[i - 1].getPrice();
				i = findPosition(n, k, i) - 1;
			}
		}
		long end = System.nanoTime();
		System.out.println("Details :");
		System.out.println("Total Attack Point:" + V[n][W]);
		System.out.println("Total Gold Spent :" + (W - t));
		System.out.println("Execution Time :" + (end - start) + " ns");
		System.out.println(" ");

	}
	
	
	// greedy approach
	public static void greedyApproach(int W, int n, int k,Hero[] heros) {  
		long start = System.nanoTime();
		Hero[] tempPlayer = new Hero[n * k];
		int totalRating = 0;
		int totalPrice = 0;
		tempPlayer = heros;
		for (int i = 0; i < tempPlayer.length; i++)
			tempPlayer[i].setRaitio((double) tempPlayer[i].getAttackPoint() / (double) tempPlayer[i].getPrice());
		sortForGreedy(tempPlayer);
		int temp[] = new int[n];
		for (int i = 0; i < temp.length; i++)
			temp[i] = 0;
		int count = 0;
		
		System.out.println("Heros:");
		for (int i = 0; i < tempPlayer.length; i++) {
			if (count == n) {
				break;

			} else {
				int count2 = 0;
				for (int j = 0; j < temp.length; j++) {
					if (tempPlayer[i].getType() != temp[j])
						count2++;
				}
				if (count2 == temp.length && totalPrice <= W) {
					totalPrice += tempPlayer[i].getPrice();
					if (totalPrice > W) {
						totalPrice -= tempPlayer[i].getPrice();
					} else {
						System.out.println("-"+tempPlayer[i].getName()+"(" +tempPlayer[i].getTypeName()+", "+tempPlayer[i].getPrice()+" Gold, "+ tempPlayer[i].getAttackPoint()+" Attack)");
						totalRating += tempPlayer[i].getAttackPoint();
						temp[count] = tempPlayer[i].getType();
						count++;

					}

				}

			}

		}
		long end = System.nanoTime();
		
		System.out.println("Details :");
		System.out.println("Total Attack Point:" + totalRating);
		System.out.println("Total Gold Spent :" + totalPrice);
		System.out.println("Execution Time :" + (end - start) + " ns");
		System.out.println(" ");

	}
	
	// random approach
	public static void randomApproach(int W, int n, int k,Hero[] heros) {
		long start = System.nanoTime();
		Hero[] randomHeros=new Hero[n];
		Random rnd = new Random();
		int totalPrice=0;
		int totalRating=0;
		int y;
		
		while (true) {
			
			int t=rnd.nextInt(n);
			if(t==0) {continue;}
			int x=0;
			for (int i = 0; i <t ; i++) {
				int index=x+rnd.nextInt(k);
				randomHeros[i]=heros[index];
				totalPrice=totalPrice+heros[index].getPrice();
				totalRating=totalRating+heros[index].getAttackPoint();
				x=x+k;
			}
			if (totalPrice < W) {
				y = t;
				break;
			}
			
		}
		long end = System.nanoTime();
		System.out.println("Heros:");
		
		for (int i = 0; i <y ; i++) {
			System.out.println("-"+randomHeros[i].getName()+"(" +randomHeros[i].getTypeName()+", "+randomHeros[i].getPrice()+" Gold, "+ randomHeros[i].getAttackPoint()+" Attack)");
		}
		
		
		System.out.println("Details :");
		System.out.println("Total Attack Point:" + totalRating);
		System.out.println("Total Gold Spent :" + totalPrice);
		System.out.println("Execution Time :" + (end - start) + " ns");
		System.out.println(" ");
		
	}
	
	// ---------Helper Functions are below--------------
	
	private static int findPosition(int n, int k, int i) {

		int arr[] = new int[n];

		if (i == 0) {
			return 1;
		}

		arr[0] = 1;
		for (int j = 1; j < n; j++) {
			arr[j] = arr[j - 1] + k;
		}

		int count = 0;
		for (int j = 0; j < arr.length; j++) {
			if (arr[j] > i) {
				break;
			}
			count++;
		}
		return arr[count - 1];

	}
	
	// reads and returns desired heros (to n level and first k heros each level)
	public static Hero[] readAndReturnHeros(int n, int k) throws IOException {

		String line = "";
		String splitBy = ",";
 
		BufferedReader br = new BufferedReader(
				new FileReader("input_1.csv", StandardCharsets.UTF_8));  //path

		int c = 0;
		br.readLine();
		while ((line = br.readLine()) != null) {
			String[] parsedLine = line.split(splitBy);
			String x=parsedLine[1];
			switch (parsedLine[1].toLowerCase()) {
			case "pawn":
				parsedLine[1] = "1";
				break;
			case "rook":
				parsedLine[1] = "2";
				break;
			case "archer":
				parsedLine[1] = "3";
				break;
			case "knight":
				parsedLine[1] = "4";
				break;
			case "bishop":
				parsedLine[1] = "5";
				break;
			case "war_ship":
				parsedLine[1] = "6";
				break;
			case "siege":
				parsedLine[1] = "7";
				break;
			case "queen":
				parsedLine[1] = "8";
				break;
			case "king":
				parsedLine[1] = "9";
				break;

			}

			heros[c] = new Hero(parsedLine[0], Integer.parseInt(parsedLine[1]), Integer.parseInt(parsedLine[2]),
					Integer.parseInt(parsedLine[3]),x);
			c++;

		}

		Hero[] tempHeros = new Hero[n * k];
		int count2 = 0;
		for (int i = 1; i <= n; i++) {
			int count = 0;
			for (int l = 0; l < heros.length; l++) {
				if (heros[l].getType() == i) {
					if (count >= k)
						break;
					tempHeros[count2] = heros[l];
					count2++;
					count++;
				}

			}

		}

		return tempHeros;
	}
	
	// greedy sorting process items 
	private static void sortForGreedy(Hero[] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				if (arr[i].getRaitio() > arr[j].getRaitio()) {
					Hero temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}

	}
	
	
	
	// main
	public static void main(String[] args) throws IOException {
		
		// inputs processing
		Scanner scan = new Scanner(System.in);
		System.out.print("Please enter the GOLD_AMOUNT :");
		GOLD_AMOUNT=scan.nextInt();
		System.out.print("Please enter the MAX_LEVEL_ALLOWED:");
		MAX_LEVEL_ALLOWED=scan.nextInt();
		System.out.print("Please enter the NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL:");
		NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL=scan.nextInt();
		scan.close();
		System.out.println();
		
		// reading process
		heros=readAndReturnHeros(MAX_LEVEL_ALLOWED, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL);
		
		// Outputs of Program
		System.out.println("=================== Trial #1 ====================");
		System.out.println("**User's Dynamic Programming Aprroach Results ");
		dynamicApproach(NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL,GOLD_AMOUNT,heros.clone());
		
		
		System.out.println("**Computer's Greedy Aprroach Results ");
		greedyApproach(GOLD_AMOUNT,MAX_LEVEL_ALLOWED,NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL,heros.clone());
		System.out.println();
		
		System.out.println("=================== Trial #2 ====================");
		System.out.println("**User's Dynamic Programming Aprroach Results ");
		dynamicApproach(NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL,GOLD_AMOUNT,heros.clone());
		
		
		System.out.println("**Computer's Random Aprroach Results  ");
		randomApproach(GOLD_AMOUNT,MAX_LEVEL_ALLOWED,NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL,heros.clone());

	}
	

}

 // Extra class are below

class Hero {

	private String name;
	private int type;
	private int attackPoint;
	private int price;
	private double raitio;
	private String typeName;

	public Hero(String name, int type,int price, int attackPoint,String typeName) {
		super();
		this.name = name;
		this.type = type;
		this.attackPoint = attackPoint;
		this.price = price;
		this.typeName=typeName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAttackPoint() {
		return attackPoint;
	}

	public void setAttackPoint(int attackPoint) {
		this.attackPoint = attackPoint;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	public double getRaitio() {
		return raitio;
	}

	public void setRaitio(double raitio) {
		this.raitio = raitio;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	

}


