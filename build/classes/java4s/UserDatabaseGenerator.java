package course;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import org.bson.Document;

public class UserDatabaseGenerator {

	
	public static void databaseGenerator() throws IOException {
		
		 ArrayList<String> user = new ArrayList<String>();
		 ArrayList<String> matches = new ArrayList<String>();
		 ArrayList<String> websites = new ArrayList<String>();

		 File fileMatches = new File("D:/workspace/matchNames.txt");
		 File fileWebsites = new File("D:/workspace/websites.txt");
		 File fileuser = new File("D:/workspace/user.txt");
			if (!fileuser.exists()) {
				fileuser.createNewFile();
			}
			FileWriter fw = new FileWriter(fileuser.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);


		 
		 FileReader fr=new FileReader(fileMatches);
		 FileReader fr1=new FileReader(fileWebsites);

		 BufferedReader br = new BufferedReader(fr);
		 BufferedReader br1 = new BufferedReader(fr1);

		 
		 String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				matches.add(sCurrentLine);
			}
			while ((sCurrentLine = br1.readLine()) != null) {
				websites.add(sCurrentLine);
			}
			
		 
		 for (int i=0;i<1000;i++){
			 int index=1000+i;
			user.add("user"+index) ;
		 }
		System.out.println(user.get(100));
		System.out.println(matches.get(4));
		System.out.println(websites.get(4));
		
		for (String i: user){
			for(String j:matches){
				for(String k:websites){
				String[] players=j.split("vs");
				int index;
				Random randomno = new Random();
				if ( randomno.nextInt(1000)% 2 == 0)
					index = 0;
				else
					index = 1;
				String websiteUserId=k.substring(0, 3)+i.substring(4, 7);
				int bet= randomno.nextInt(5000-500) + 500;
					bw.write(i+"::"+j+"::"+players[index]+"::"+k+"::"+websiteUserId+"::"+bet);
					bw.newLine();
				}
			}
		}
bw.close();
br.close();
br1.close();
	}
	
	public static void main(String[] args) throws IOException {
		databaseGenerator();
	}
}
