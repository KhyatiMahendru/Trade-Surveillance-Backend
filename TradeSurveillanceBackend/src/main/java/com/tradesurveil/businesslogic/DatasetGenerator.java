/**
 * 
 */
package com.tradesurveil.businesslogic;

import java.sql.Timestamp;
import java.util.*;

import com.tradesurveil.bean.TradeForDataGen;

/**
 * @author Kryselle
 *
 */
public class DatasetGenerator {

	List<String> brokerList = generateBrokerList();
	List<String> traderList = generateTraderList();
	List<String> securityNameList = generateSecurityNameList();
	List<String> securityTypeList = generateSecurityTypeList();
	List<String> tradeTypes = generateTradeTypes();
	Map<String, Double> marketPrice = initialMarketPrice();
	static Timestamp timestamp = Timestamp.valueOf("2020-10-05 09:00:00");
	Timestamp closingTime = Timestamp.valueOf("2020-10-05 15:00:00");
	
	public List<TradeForDataGen> generateTrades() {
		List<TradeForDataGen> tradeList = new ArrayList<TradeForDataGen>();
		return tradeList;
	}
	
	public List<TradeForDataGen> generateFrontRunningTrades() {
		List<TradeForDataGen> tradeList = new ArrayList<TradeForDataGen>();
		return tradeList;
	}
	
	public List<TradeForDataGen> generateRandomTrades(int minNumberOfTrades, int maxNumberOfTrades) {
		
		List<TradeForDataGen> tradeList= new ArrayList<TradeForDataGen>();
		int randomNumber = (int) ((Math.random() * (maxNumberOfTrades - minNumberOfTrades)) + minNumberOfTrades);
		
		for (int i = 0; i < randomNumber; i++) {
			TradeForDataGen trade = new TradeForDataGen();
			trade.setType(tradeTypes.get(generateRandomNumber(0, tradeTypes.size() - 1)));
			
			//HARDCODED TIMESTAMP INCREASE RANGE OF 10s
			timestamp = new Timestamp(timestamp.getTime() + generateRandomNumber(4, 10) * 1000);
			if(timestamp.compareTo(closingTime) >= 0)
				return tradeList;
			trade.setTimestamp(timestamp);
			
			trade.setSecurityName(securityNameList.get(generateRandomNumber(0, securityNameList.size() - 1)));
			trade.setSecurityType(securityTypeList.get(generateRandomNumber(0, securityTypeList.size() - 1)));
			trade.setBrokerName(brokerList.get(generateRandomNumber(0, brokerList.size() - 1)));
			trade.setTraderName(traderList.get(generateRandomNumber(0, traderList.size() - 1)));
			
			//HARDCODED QUANTITY RANGE of 100
			trade.setQuantity(generateRandomNumber(1, 100));
			
			//HARDCODED MARKET PRICE TO INCREASE PROPORTIONALLY TO QUANTITY IN CASE OF BUY AND DECREASE IN CASE OF SELL WITHIN 5 RUPEE RANGE
			double currentMarketPrice = marketPrice.get(trade.getSecurityName()+"-"+trade.getSecurityType());
			double newMarketPrice = currentMarketPrice;
			if (trade.getType() == "buy") {
				double increase = ((double) trade.getQuantity())/100 * 5;
				newMarketPrice = currentMarketPrice + increase;
			}
			else {
				double decrease = ((double) trade.getQuantity())/100 * 5;
				newMarketPrice = currentMarketPrice - decrease;
			}
			trade.setPrice(Math.round(newMarketPrice * 100.0) / 100.0);
			
			
			//code for front running 
			if(trade.getPrice()*trade.getQuantity()>=1475000) 
			{
				TradeForDataGen trade2 = new TradeForDataGen();
				TradeForDataGen trade3 = new TradeForDataGen();
				
				if(trade.getTraderName()!="Citi Group")
				{
					if(trade.getType() == "buy")
					{
						
						trade2.setType("buy");
						Timestamp timestamp2 = new Timestamp(timestamp.getTime() - generateRandomNumber(1, 3) * 1000);
						trade2.setTimestamp(timestamp2);
						trade2.setSecurityType(trade.getSecurityType());
						trade2.setTraderName("Citi Group");
						trade2.setBrokerName(brokerList.get(generateRandomNumber(0, brokerList.size() - 1)));
						trade2.setQuantity(trade.getQuantity());
						trade2.setSecurityName(trade.getSecurityName());
						trade2.setPrice(trade.getPrice());
						
						trade3.setType("sell");
						timestamp = new Timestamp(timestamp.getTime() + generateRandomNumber(1, 3) * 1000);
						trade3.setTimestamp(timestamp);
						trade3.setSecurityType(trade.getSecurityType());
						trade3.setTraderName("Citi Group");
						trade3.setBrokerName(brokerList.get(generateRandomNumber(0, brokerList.size() - 1)));
						trade3.setQuantity(trade.getQuantity());
						trade3.setSecurityName(trade.getSecurityName());
						double increase = ((double) trade.getQuantity())/100 * 5;
						trade3.setPrice(trade.getPrice() + increase);
						newMarketPrice = currentMarketPrice + increase;
						
						
						tradeList.add(trade2);
						tradeList.add(trade);
						tradeList.add(trade3);
						
						
					}
					else if(trade.getType() == "sell")
					{
						trade2.setType("sell");
						Timestamp timestamp2 = new Timestamp(timestamp.getTime() - generateRandomNumber(1, 3) * 1000);
						trade2.setTimestamp(timestamp2);
						trade2.setSecurityType(trade.getSecurityType());
						trade2.setTraderName("Citi Group");
						trade2.setBrokerName(trade.getBrokerName());
						trade2.setQuantity(trade.getQuantity());
						trade2.setSecurityName(trade.getSecurityName());
						trade2.setPrice(trade.getPrice());
						
						trade3.setType("buy");
						timestamp = new Timestamp(timestamp.getTime() + generateRandomNumber(1, 3) * 1000);
						trade3.setTimestamp(timestamp);
						trade3.setSecurityType(trade.getSecurityType());
						trade3.setTraderName("Citi Group");
						trade3.setBrokerName(trade.getBrokerName());
						trade3.setQuantity(trade.getQuantity());
						trade3.setSecurityName(trade.getSecurityName());
						double decrease = ((double) trade.getQuantity())/100 * 5;
						trade3.setPrice(trade.getPrice() - decrease);
						newMarketPrice = currentMarketPrice - decrease;
						
						tradeList.add(trade2);
						tradeList.add(trade);
						tradeList.add(trade3);
					}
				}
				marketPrice.replace(trade.getSecurityName()+"-"+trade.getSecurityType(), newMarketPrice);
				
			}
			else {
			marketPrice.replace(trade.getSecurityName()+"-"+trade.getSecurityType(), newMarketPrice);
			tradeList.add(trade);
			}
		}
		
		return tradeList;
		
	}
	
	public int generateRandomNumber(int min, int max) {
		double random = Math.random() * (max - min) + min;
		return (int) Math.round(random);
	}
	
	public List<String> generateBrokerList() {
		List<String> brokerList = new ArrayList<String>();
		brokerList.add("Citi Velocity");
		brokerList.add("Kotak Securities");
		brokerList.add("ICICI Direct");
		brokerList.add("Sharekhan");
		brokerList.add("Zerodha");
		return brokerList;
	}
	
	public List<String> generateTraderList() {
		List<String> traderList = new ArrayList<String>();
		traderList.add("Citi Group");
		traderList.add("Raytheon Technologies");
		traderList.add("LVMH Moët Hennessy Louis Vuitton");
		traderList.add("Alibaba Group");
		return traderList;
	}
	
	public List<String> generateSecurityNameList() {
		List<String> securityNameList = new ArrayList<String>();
		securityNameList.add("Apple");
		securityNameList.add("Facebook");
		securityNameList.add("Walmart");
		return securityNameList;
	}
	
	public List<String> generateSecurityTypeList() {
		List<String> securityTypeList = new ArrayList<String>();
		securityTypeList.add("ES");
		securityTypeList.add("Futures");
		securityTypeList.add("Call");
		securityTypeList.add("Put");
		return securityTypeList;
	}
	
	public List<String> generateTradeTypes() {
		List<String> tradeTypes = new ArrayList<String>();
		tradeTypes.add("buy");
		tradeTypes.add("sell");
		return tradeTypes;
	}
	
	public Map<String, Double> initialMarketPrice() {
		
		Map<String, Double> initialMarketPrice = new HashMap<String, Double>();
		
		initialMarketPrice.put("Apple-ES", 8074.27);
		initialMarketPrice.put("Apple-Futures",8074.27);
		initialMarketPrice.put("Apple-Call", 8074.27);
		initialMarketPrice.put("Apple-Put", 8074.27);
		
		initialMarketPrice.put("Facebook-ES", 18444.31);
		initialMarketPrice.put("Facebook-Futures", 18444.31);
		initialMarketPrice.put("Facebook-Call", 18444.31);
		initialMarketPrice.put("Facebook-Put", 18444.31);
		
		initialMarketPrice.put("Walmart-ES", 10074.60);
		initialMarketPrice.put("Walmart-Futures", 10074.60);
		initialMarketPrice.put("Walmart-Call", 10074.60);
		initialMarketPrice.put("Walmart-Put", 10074.60);
		
		return initialMarketPrice;
	}
	
	public static void main(String args[]) {
		DatasetGenerator datasetGenerator = new DatasetGenerator();
		List<TradeForDataGen> tradeList = new ArrayList<TradeForDataGen>();
		tradeList = datasetGenerator.generateRandomTrades(90, 100);
		for(TradeForDataGen trade: tradeList) {
			System.out.println(trade.getType() + "\t" + trade.getTimestamp() + "\t" + trade.getSecurityName() + "\t" + trade.getSecurityType() + "\t" + trade.getBrokerName() + "\t" + trade.getTraderName() + "\t" + trade.getPrice() + "\t" + trade.getQuantity());
		}
	}
	
}