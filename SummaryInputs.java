import java.util.Scanner;

public class SummaryInputs {

	public static void main(String[] args) {
		System.out.println("Enter starting date(YYYY-MM-DD):");
		Scanner sc = new Scanner(System.in);
		String startingDate = sc.nextLine();
		System.out.println("Enter ending date(YYYY-MM-DD):");
		String endingDate = sc.nextLine();
		System.out.println("Name the file of the output");
		String outputFile = sc.nextLine();
		
		MiniMeToyCarSummary ms = new MiniMeToyCarSummary();
		ms.getSummary(startingDate, endingDate,outputFile);
		sc.close();

	}

}
