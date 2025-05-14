package tests;
import model.*;
import java.time.LocalDate;





public class PersonneTest {
	public static void main(String[] args) {
		LocalDate date = LocalDate.of(1995, 5, 14);
		LocalDate date2 = LocalDate.of(1995, 5, 14);
		
        Personne p1 = new Personne("guillarem", "Arno", date, Nationalite.FRANCAIS, 21);
        Personne p2 = new Personne("zdzadaz", "Arzadazno", date2, Nationalite.FRANCAIS, 21);


        System.out.println(p1);
        System.out.println(p2);
    }
}