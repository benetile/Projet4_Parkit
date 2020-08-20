package com.parkit.parkingsystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class FareCalculatorService {
	
	//private ParkingService parkingService;
	private TicketDAO ticketDAO;
	
	public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
	}
        public void calculateFare(Ticket ticket, String vehicleRegNumber){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration;
        duration =  (outHour - inHour)/3600000;

        double duree = round(duration,2);
    //    ticket.getVehicleRegNumber();
        double reduction = 1;

       if(vehicleRegNumber!=null)
        {
        	reduction = 0.95;
        }
       if((duree)>0.50)
       {
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(round(duration * Fare.CAR_RATE_PER_HOUR * reduction,3));
                break;
            }
            case BIKE: {
                ticket.setPrice(round(duration* Fare.BIKE_RATE_PER_HOUR * reduction,3));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
       }
       else if(duree<=0.50)
       {
    	   switch (ticket.getParkingSpot().getParkingType()) {
    	   case CAR :{
    		 ticket.setPrice(0);
    		   break;
    	   }
    	   case BIKE: {
    		 ticket.setPrice(0);
    		   break;
    	   }
    	   default: throw new IllegalArgumentException("Unkown Parking Type");
    	   }
       }
    }
}