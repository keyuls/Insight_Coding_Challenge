import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.*;

public class median_degree {

	static Date currentTime =null;
	static Date max_span=null;
	static Map<edge,Date> liveEdges= new HashMap<edge,Date>();
	static Map<String,Integer> userConnect= new HashMap<String,Integer>();
	 static BufferedWriter bw; 
	
	public static void main(String[] args) throws IOException, JSONException {

		
		Reader reader = new FileReader("insight_testsuite/tests/test-1-venmo-trans/venmo_input/venmo-trans.txt");
		Writer writer = new FileWriter("insight_testsuite/tests/test-1-venmo-trans/venmo_output/output.txt");

		BufferedReader br = new BufferedReader(reader);
		bw = new BufferedWriter(writer);
		
		String line;
		while((line=br.readLine())!=null){
			JSONObject jobj = new JSONObject(line);
			processData(jobj);		
		}
		bw.close();

	}
	
	public static void processData(JSONObject jobj) throws JSONException{
		String actor= jobj.getString("actor");
		String target=jobj.getString("target");
		String createdAt = jobj.getString("created_time");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setLenient(true);
		try {
			currentTime= sdf.parse(createdAt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		if(max_span==null || currentTime.compareTo(max_span)>0){
			max_span=currentTime;
			edge e=new edge(actor, target);
			liveEdges.put(e, currentTime);
			updateCount(actor);
			updateCount(target);
			removeOld(max_span);
			
		}
		else if((currentTime.compareTo(max_span)<=0)  && ((max_span.getTime() - currentTime.getTime()) / 1000) < 60){	
			edge e=new edge(actor, target);
			liveEdges.put(e, currentTime);
			updateCount(actor);
			updateCount(target);
			
		}
		try {
			getMedian();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateCount(String userName) {

		if(userConnect.containsKey(userName)){
			userConnect.put(userName, userConnect.get(userName)+1);
		}
		else
			userConnect.put(userName, 1);
	}
	
	public static void deleteCount(String userName) {

		if(userConnect.containsKey(userName)){
			userConnect.put(userName, userConnect.get(userName)-1);
			
			if(userConnect.get(userName)<1)
				userConnect.remove(userName);
		}
		
	}	
	
	public static void removeOld( Date max_span){
		
		Iterator itr = liveEdges.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry pair = (Map.Entry)itr.next();
			Date userTime = (Date)pair.getValue();
			if(((max_span.getTime()-userTime.getTime())/1000)>60){
				//System.out.println("inside--"+pair.getValue());
				edge e= (edge)pair.getKey();
				deleteCount(e.getActor());
				deleteCount(e.getTarget());
				itr.remove();
			}
		}	
	}
	
	public static void getMedian() throws IOException{
		List<Integer> median = new ArrayList<Integer>();
		for(Map.Entry<String, Integer> entry : userConnect.entrySet()){
			median.add(entry.getValue());
			//System.out.println(entry.getKey()+"--"+entry.getValue());
		}
		
		Collections.sort(median);
		int middle = median.size()/2;
		double med_val=0.0;
		if((middle%2)==1)
			med_val=(double)median.get(middle);
		else
			med_val=(double)(median.get(middle-1)+median.get(middle))/2;

		
		System.out.println(	new BigDecimal(med_val).setScale(2,BigDecimal.ROUND_FLOOR));	
		bw.write((new BigDecimal(med_val).setScale(2,BigDecimal.ROUND_FLOOR)).toString() + "\n");
		//System.out.println("---");
	}

}
