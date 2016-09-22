package java4s;

import org.json.simple.*;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class TennisDAO {
	
	 public JSONArray findCurrentMatches() {
		 /**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			MongoClient mongo = new MongoClient("localhost", 27017);

			/**** Get database ****/
			// if database doesn't exists, MongoDB will create it for you
			DB db = mongo.getDB("players");
		 /**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			DBCollection table = db.getCollection("currentMatches");

			/**** Find and display ****/
			BasicDBObject searchQuery = new BasicDBObject();
			JSONArray jsonarray = new JSONArray();
			DBCursor cursor = table.find();

			while (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				JSONObject jsonobj = new JSONObject();
			    jsonobj.put("name", obj.getString("name"));
			    jsonobj.put("tournament", obj.getString("tournament"));
			    jsonarray.add(jsonobj);
			}
			return jsonarray;
	    }
	 
	 public static JSONArray findPlayerDetails(String match) {
		 /**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			MongoClient mongo = new MongoClient("localhost", 27017);

			/**** Get database ****/
			// if database doesn't exists, MongoDB will create it for you
			DB db = mongo.getDB("players");
			DBCollection table = db.getCollection("PlayerInfo");
			String[] playerList=match.split("vs");
			
			 ArrayList<String> vals = new ArrayList<String>();

			/**** Find and display ****/
			BasicDBObject searchQuery = new BasicDBObject("name",new BasicDBObject("$in",playerList));
			//List<Document> players = null;
			
			DBCursor cursor = table.find(searchQuery);
			JSONArray jsonarray = new JSONArray();
			while (cursor.hasNext()) {
				BasicDBObject obj = (BasicDBObject) cursor.next();
				JSONObject jsonobj = new JSONObject();
			  //  BasicDBList name = (BasicDBList) obj.get("name");
			    jsonobj.put("win", obj.getString("win"));
			    jsonobj.put("loss", obj.getString("loss"));
			    jsonobj.put("lastfive", obj.getString("lastfive"));
			    jsonobj.put("rank", obj.getString("rank"));
			    jsonobj.put("name", obj.getString("name"));
			    String[]lastfive=obj.getString("lastfive").split(",");
			   
			    double lfwin=0.0;
			    for(int j=0;j<lastfive.length;j++){
			    if(lastfive[j].contains("W")){
			    	lfwin++;
			    }
			    }
			    double powerRating=1.5*(Double.parseDouble(obj.getString("win"))*(500-Integer.parseInt(obj.getString("rank")))*(lfwin/5)*100)/500;
			    powerRating=Math.round(powerRating);
			    jsonobj.put("powerrating", powerRating);
			    jsonarray.add(jsonobj);
			}
			return jsonarray;
	    }
	 
	 public static JSONArray findPlayerHeadToHead(String match) {
		 /**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			MongoClient mongo = new MongoClient("localhost", 27017);

			/**** Get database ****/
			// if database doesn't exists, MongoDB will create it for you
			DB db = mongo.getDB("players");
			DBCollection table = db.getCollection("matches");
			String[] playerList=match.split("vs");
			
			 ArrayList<String> vals = new ArrayList<String>();

			// db.matches.aggregate([{$match:{matchName:"Nadal R.vsDjokovic N."}},{$group:{_id:"$winner", no_of_wins:{$sum:1}}}])
				DBObject matchObj  = BasicDBObjectBuilder.start().push("$match")
				                  .add("matchName", match).get();
				DBObject group = BasicDBObjectBuilder.start().push("$group")
				                  .add("_id", "$winner")
				                  .push("no_of_wins").add("$sum", 1).get();
				
				AggregationOutput aggr = table.aggregate( matchObj, group);
						
				
			
			
			JSONArray jsonarray = new JSONArray();
			//System.out.println(aggr.results().toString());
				
			for (DBObject obj : aggr.results()) {
			    String id = (String) obj.get("_id");
			    int noOfWins = Integer.parseInt(obj.get("no_of_wins").toString());
			    JSONObject jsonobj = new JSONObject();
			    jsonobj.put("name", id);
			    jsonobj.put("no_of_wins", noOfWins);		
			    jsonarray.add(jsonobj);
			    }
			
			
			return jsonarray;
	    }
	 
	 public static JSONArray findUserBet(String match,String player) {
		 /**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			MongoClient mongo = new MongoClient("localhost", 27017);

			/**** Get database ****/
			// if database doesn't exists, MongoDB will create it for you
			DB db = mongo.getDB("players");
			DBCollection table = db.getCollection("user");
			String[] playerList=match.split("vs");
			
			 ArrayList<String> vals = new ArrayList<String>();

//db.user.aggregate([{$unwind:"$betdetails"}, {$match:{"betdetails.matchName":"Bautista R.vsGasquet R."}},{$group:{_id:
//{"betplayer":"$betdetails.betplayer","timestamp":"$betdetails.timestamp"},no_of_users:{$sum:1},bet_money:{$sum:"$betdetails.betAmt"}}}])
			 DBObject unwind = new BasicDBObject("$unwind", "$betdetails");
			 DBObject matchObj  = new BasicDBObject("$match", new BasicDBObject("betdetails.matchName", match).append("betdetails.winner", player));
					
			 DBObject group = new BasicDBObject("$group", new BasicDBObject("_id","$betdetails.timestamp")
					 .append("no_of_users", new BasicDBObject("$sum", 1)).append("bet_money", new BasicDBObject("$sum", "$betdetails.betAmt")));
			 
			
		
		
				System.out.println(unwind.toString());
				System.out.println(matchObj.toString());
				System.out.println(group.toString());

			AggregationOutput aggr = table.aggregate(unwind,matchObj, group);
						
				
			
			
		JSONArray jsonarray = new JSONArray();
			//System.out.println(aggr.results().toString());
				
		for (DBObject obj : aggr.results()) {
			    String id = (String) obj.get("_id");
			    int noOfUser = Integer.parseInt(obj.get("no_of_users").toString());
			    int betAmount= Integer.parseInt(obj.get("bet_money").toString());
	    
			    JSONObject jsonobj = new JSONObject();
			    jsonobj.put("time", id);
			    jsonobj.put("no_of_bets", noOfUser);	
			    jsonobj.put("bet_money", betAmount);		

			    jsonarray.add(jsonobj);
			    }
			
			
			return jsonarray;
	    }
	 
	 
	 

	
	
	
	
	
	
}


	
	