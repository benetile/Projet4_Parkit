package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	@Mock
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    
    ParkingSpot parkingSpot;
    
    Ticket ticket;
   
    @BeforeEach
    private void setUpPerTest() {
       try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            ticket = new Ticket();
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

      @Test
      public void processExitingVehicleTest(){
        //ARRANGE
      ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));

      //ACT
        parkingService.processExitingVehicle();

        //ASSERT
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    	
    }
   
    @Test
    public void processInComingTest() throws Exception {
    //ARRANGE
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

    	//ACT
    	parkingService.processIncomingVehicle();
    	ticket.setInTime(new Date(System.currentTimeMillis() - (30*60*1000)));
    	parkingService.processExitingVehicle();

    	//ASSERT
    	verify(parkingSpotDAO,times(1)).getNextAvailableSlot(any(ParkingType.class));
    	assertEquals(0, ticket.getPrice());
    }
    
    @Test
    public void discount_for_RepeatCustomersTest() throws Exception {
    	//ARRANGE
    	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

        //ACT
        //when(ticketDAO.getTicketReccurent((anyString()))).thenReturn(true);
        when(ticketDAO.getTicketReccurent((anyString()))).thenReturn(ticket);


        parkingService.processIncomingVehicle();
    	ticket.setInTime(new Date(System.currentTimeMillis()-(50*60*1000)));
    	parkingService.processExitingVehicle();

    	//ASSERT
    	verify(parkingSpotDAO,times(1)).getNextAvailableSlot(any(ParkingType.class));
    }
    
    @Test
    public void not_Discount_for_theFirstCustomersTest() throws Exception {
        //ARRANGE
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("GHIJKL");
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);

    	//ACT
    	try {
            when(ticketDAO.getTicketReccurent((anyString()))).thenReturn(null);

            parkingService.processIncomingVehicle();
            ticket.setInTime(new Date(System.currentTimeMillis() - (50 * 60 * 1000)));
            parkingService.processExitingVehicle();
        }catch (Exception e){

        }
    	//ASSERT
    	verify(parkingSpotDAO,times(1)).getNextAvailableSlot(any(ParkingType.class));
    }
}
