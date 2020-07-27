import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Envelope;

public class GetClass3 {
	
	private static BlockingQueue<String> readerBuffer = new LinkedBlockingQueue<>();
	private static Map<Long,Value> cacheCrossWalk = new ConcurrentHashMap<Long,Value>();
	private static BlockingQueue<String> writterBuffer = new LinkedBlockingQueue<>();
 
	public static void main(String[] args) throws IOException, TimeoutException {
		// TODO Auto-generated method stub

	new Thread(mqReader).start();
	new Thread(mqProcesor).start();
	new Thread(mqWritter).start();
	//new Thread(mqProcesor).start();


 
}

	static Runnable mqReader = () -> {
		  try {
			  System.out.println("*** Reader Thread start"+Thread.currentThread().getName()+"RabbitMQ Initialization");
			  ConnectionFactory factory = new ConnectionFactory();
				factory.setAutomaticRecoveryEnabled(false);
				  factory.setHost("localhost");
				  factory.setUsername("guest");
				  factory.setPassword("guest");
				  com.rabbitmq.client.Connection connection = factory.newConnection();
			  Channel channel = connection.createChannel();
			  channel.queueDeclare("Singleton_test_claims_Queue",true, false, false, null);	 
			  
			  channel.basicConsume("Singleton_test_claims_Queue", true, new DefaultConsumer(channel) {
			 
				  
				  
				  @Override
				     public void handleDelivery(
				        String consumerTag,
				        Envelope envelope, 
				        AMQP.BasicProperties properties, 
				        byte[] body) throws IOException {
					  
					 
					  try {
						readerBuffer.put(new String(body, "UTF-8"));
					} catch (UnsupportedEncodingException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				     } });
	            
				 
		} catch (IOException | TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	
			
	
	};

	static Runnable mqProcesor = () -> {
	
		
		try {
			System.out.println("*** Processor Thread start"+Thread.currentThread().getName()+"Reading Continously from Reader Buffer");
			while(true)  
						{
						String message =  readerBuffer.take();
					            		
					String[] messageSplit =  message.split(",");
					
					String claim_id = messageSplit[0];
					long lkp_claim_id = Long.valueOf(messageSplit[0]);
					String orignial_claim_id = messageSplit[1].substring(0, 7);
					String i_bill_amount = messageSplit[2].replace("$", "").trim();
					double lkp_bill_amount = Double.valueOf(i_bill_amount);
					String i_disallowed_amt = messageSplit[4].replace("$", "").trim();
					double lkp_disallowed_amt = Double.valueOf(i_disallowed_amt);
					
					Value amounts = lookup(lkp_claim_id,lkp_bill_amount,lkp_disallowed_amt);
					Double v_bill_amount = amounts.getBill_amount();
					Double v_disallowed_amt = amounts.getdisallowed_amt();
					String o_bill_amount = String.format("%.2f",v_bill_amount);
					double v_deductible_amt = v_bill_amount.doubleValue() - v_disallowed_amt.doubleValue();
					String o_deductible_amt = String.format("%.2f",v_deductible_amt);
					String updFlag = amounts.getupdateFlag();
					int claim_explanation_code1 = Integer.parseInt(messageSplit[11]);
					int claim_explanation_code2 = Integer.parseInt(messageSplit[12]);
					int claim_explanation_code3 = Integer.parseInt(messageSplit[13]);
					int claim_explanation_code4 = Integer.parseInt(messageSplit[14]);
					int claim_explanation_code5 = Integer.parseInt(messageSplit[15]);
					int v_claim_explanation_code = claim_explanation_code1 > claim_explanation_code2 ? claim_explanation_code1:claim_explanation_code2;
					int v_claim_explanation_code2 = v_claim_explanation_code > claim_explanation_code3 ? v_claim_explanation_code:claim_explanation_code3;
					int v_claim_explanation_code3 = v_claim_explanation_code2 > claim_explanation_code4 ? v_claim_explanation_code2:claim_explanation_code4;
					int v_claim_explanation_code4 = v_claim_explanation_code3 > claim_explanation_code5 ? v_claim_explanation_code3:claim_explanation_code5;
					String approved_units = messageSplit[5];
					int v_approved_Units = approved_units == null || approved_units.equals("") ? v_claim_explanation_code4 : Integer.parseInt(approved_units);
					String o_approved_Units = String.valueOf(v_approved_Units);
					String value_code_1 = messageSplit[6];
					String value_code_2 = messageSplit[7];
					String value_code_3 = messageSplit[8];
					String v_Value_Code = value_code_1 == null || value_code_1.equals("") ? value_code_2 : value_code_1;
					String v_Value_code2 = v_Value_Code == null || v_Value_Code.equals("") ? value_code_3 : v_Value_Code;
					String member_clientid = messageSplit[9];
					String provider_clientid = messageSplit[10];
					
					DateFormat parser = new SimpleDateFormat("MM/dd/yy");
					SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					
					Date datebill_from_dt = (Date) parser.parse(messageSplit[16]);
					String bill_from_dt = newFormat.format(datebill_from_dt);
					
					Date datebill_to_dt= (Date) parser.parse(messageSplit[17]);
					String bill_to_dt = newFormat.format(datebill_to_dt);
					
					String diag_cd1 = messageSplit[18];
					String diag_cd2 = messageSplit[20];
					String diag_cd3 = messageSplit[21];
					String diag_cd4 = messageSplit[22];
					String diag_cd5 = messageSplit[23];
					String v_diag_cd = diag_cd1 == null || diag_cd1.equals("") ? diag_cd2 : diag_cd1;
					String v_diag_cd2 = v_diag_cd == null || v_diag_cd.equals("") ? diag_cd3 : v_diag_cd;
					String v_diag_cd3 = v_diag_cd2 == null || v_diag_cd2.equals("") ? diag_cd4 : v_diag_cd2;
					String v_diag_cd4 = v_diag_cd3 == null || v_diag_cd3.equals("") ? diag_cd5 : v_diag_cd3; 
					String diagcd_description = messageSplit[19];
					
					Date dateProccd= (Date) parser.parse(messageSplit[24]);
					
					String proccd_dt = newFormat.format(dateProccd);

					String newMessage = claim_id + "," + orignial_claim_id + ","+o_bill_amount+ ","+o_deductible_amt+","+o_approved_Units+","+v_Value_code2 +","+member_clientid+","+provider_clientid+","+bill_from_dt+","+bill_to_dt+","+v_diag_cd4+","+diagcd_description+","+proccd_dt+","+updFlag+","+i_bill_amount;
					System.out.println(newMessage);
								
					
							//update flag
					
					
					writterBuffer.put(newMessage);
						
						} 
	 
			} catch (InterruptedException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
				
		
		};

		
		synchronized static Value lookup(long lkp_claim_id, double lkp_bill_amount,double disallowed_amt) {
			// TODO Auto-generated method stub
			Value newValue = null;
			String updateFlag = "";
			if (cacheCrossWalk.containsKey(lkp_claim_id)){
				
				Value existingValue = cacheCrossWalk.get(lkp_claim_id);
				double newBill_Amount = existingValue.getBill_amount() + lkp_bill_amount;
				double newDeductible_amt = existingValue.getdisallowed_amt() + disallowed_amt;
				updateFlag = "Y";
				newValue = new Value(newBill_Amount,newDeductible_amt,updateFlag);
				cacheCrossWalk.put(lkp_claim_id,newValue);
			return 	newValue;
			
			} else {
				updateFlag = "N";
				newValue = new Value(lkp_bill_amount,disallowed_amt,updateFlag);
				cacheCrossWalk.put(lkp_claim_id, newValue);
				
				return newValue;
			}
			
			
		}
		
		
	static Runnable mqWritter = () -> {
			
			try {
				
				System.out.println("*** Writter Thread start"+Thread.currentThread().getName()+"PublishMessage");
				
				ConnectionFactory factory = new ConnectionFactory();
				  factory.setHost("localhost");
				  factory.setUsername("guest");
				  factory.setPassword("guest");
				  
				
				  Connection connection = factory.newConnection();
				
				 Channel channel = connection.createChannel();
				  channel.queueDeclare("Singleton_claims_processed_Queue",true, false, false, null);
				
				  
				  while(true) {
						
					  String writeMessage = writterBuffer.take();
			
						channel.basicPublish("", "Singleton_claims_processed_Queue", null, writeMessage.getBytes());
				}
				  
				  
				
				
			} catch (InterruptedException | IOException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		};		
		


	
}