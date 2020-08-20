package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.channels.ScatteringByteChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ibm.icu.util.BytesTrie.Result;
import com.mysql.cj.jdbc.JdbcConnection;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class TicketDaoTest {
	

	
	    DataBasePrepareService dataPrepareService = new DataBasePrepareService();

	    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	    
	    ParkingService parkingService;
	    
		@Mock
		TicketDAO ticketDAO;
	    
	    @Mock
	    InputReaderUtil inputReaderUtil;

	    @Mock
	  	ParkingSpotDAO parkingSpotDAO;
	   
	    @BeforeEach
	    public void setUpPerTest() throws Exception {
	    	   when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
			   when(inputReaderUtil.readSelection()).thenReturn(1);  
			   ticketDAO = new TicketDAO();
			   ticketDAO.dataBaseConfig = dataBaseTestConfig;
				ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
				parkingSpotDAO.dataBaseConfig= dataBaseTestConfig;

			   dataPrepareService.clearDataBaseEntries();
	    }
	    
	    @AfterEach
	    public void cleanUp() {

	    	dataBaseTestConfig.closeConnection(null);
	    }
	  	    
	   @Test
	   public void testThat_TheSavedTicket_exists() {
		   //GIVEN
		   parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		   ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		   when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		   when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

		   //WHEN
		   parkingService.processIncomingVehicle();
		   Ticket ticket = new Ticket();
		   try {
		   	ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

		   }catch (Exception e){

		   }

		   System.out.println(ticket.getVehicleRegNumber());
		   //THEN
		assertEquals("ABCDEF",ticket.getVehicleRegNumber());
	   }
	  
	  @Test
	   public void updateTicketTest() {
		   //ARRANGE

		   parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		   ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		   when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		   parkingService.processIncomingVehicle();
		   parkingService.processExitingVehicle();

		   //ACT
		   Ticket ticket = new Ticket();
			try {
				ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
				//ticketDAO.updateTicket(ticket);
			}catch (Exception e){

			}
			//ASSERT
			assertNotNull(ticket.getOutTime());
	   }
}
