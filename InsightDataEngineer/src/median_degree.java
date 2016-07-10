import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import org.json.JSONException;

public class median_degree {

	private Date currentTime =null;
	private Date max_span=null;
	private Map<edge,Date> liveEdges= new HashMap<edge,Date>();
	private Map<String,Integer> userConnect= new HashMap<String,Integer>();
	private BufferedWriter bw; 
	private BufferedReader br;
	
	public static void main(String[] args) throws IOException, JSONException, ParseException {
		median_degree md = new median_degree();
		md.collectData();
	}
	
	/*Fetch data from input file*/
	public void collectData() throws IOException, JSONException, ParseException{
		String line;	
		Reader reader = new FileReader("./venmo_input/venmo-trans.txt");
		Writer writer = new FileWriter("./venmo_output/output.txt");
		br = new BufferedReader(reader);
		bw = new BufferedWriter(writer);
		
		while((line=br.readLine())!=null){
			JSONObject jobj = new JSONObject(line);
			processData(jobj);		
		}
		bw.close();
	}
	
	/*Processing each of Json Object of Transaction*/
	public void processData(JSONObject jobj) throws JSONException,IOException,ParseException{
		if(jobj.getString("actor")!=null && jobj.getString("target")!=null){
			String actor= jobj.getString("actor");
			String target=jobj.getString("target");
			String createdAt = jobj.getString("created_time");
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setLenient(true);
			currentTime= sdf.parse(createdAt);
			
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
			getMedian();	
		}		
	}
	
	/*Increment total connected edges with specific user*/
	public void updateCount(String userName) {
		if(userConnect.containsKey(userName))
			userConnect.put(userName, userConnect.get(userName)+1);
		else
			userConnect.put(userName, 1);
	}

	/*Decrement total connected edges with specific user*/
	public void deleteCount(String userName) {
		if(userConnect.containsKey(userName)){
			userConnect.put(userName, userConnect.get(userName)-1);			
			if(userConnect.get(userName)<1)
				userConnect.remove(userName);
		}
	}	
	
	/*remove edges that are older then 60 seconds*/
	@SuppressWarnings("unchecked")
	public void removeOld( Date max_span){	
		Iterator itr = liveEdges.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<edge,Date> pair = (Map.Entry<edge,Date>)itr.next();
			Date userTime = (Date)pair.getValue();
			if(((max_span.getTime()-userTime.getTime())/1000)>60){
				edge e= (edge)pair.getKey();
				deleteCount(e.getActor());
				deleteCount(e.getTarget());
				itr.remove();
			}
		}	
	}

	/*calculating median*/
	public void getMedian() throws IOException{
		double med_val=0.0;
		int middle =0;
		List<Integer> median = new ArrayList<Integer>();
		for(Map.Entry<String, Integer> entry : userConnect.entrySet()){
			median.add(entry.getValue());
		}		
		Collections.sort(median);
		middle = median.size()/2;
		
		if((middle%2)==1)
			med_val=(double)median.get(middle);
		else
			med_val=(double)(median.get(middle-1)+median.get(middle))/2;
		
		bw.write((new BigDecimal(med_val).setScale(2,BigDecimal.ROUND_FLOOR)).toString() + "\n");
		
		//System.out.println(	new BigDecimal(med_val).setScale(2,BigDecimal.ROUND_FLOOR));	
		//System.out.println("---");
	}

}
