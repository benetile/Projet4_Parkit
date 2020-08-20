package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.slf4j.ILoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    
   @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
    	when(inputReaderUtil.readSelection()).thenReturn(1);
    	when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("TOTO");
      //  dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterEach
    private void clean(){

       // dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
        //dataBasePrepareService.clearDataBaseEntries();
    }
    
    @Test
    public void testParkingACar() throws Exception{

       //GIVEN
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
        parkingService.processIncomingVehicle();

        //WHEN
       //TODO:check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = null;
        Boolean result = false ;
        String vehicle = null;
        try {
            ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
           // ticket.setInTime(new Date(System.currentTimeMillis() + 45 * 60* 1000));
            parkingSpotDAO.updateParking(parkingSpot);
            vehicle = ticket.getVehicleRegNumber();
            result = parkingSpotDAO.updateParking(parkingSpot);
        }catch (Exception e){

        }

       //THEN
        assertEquals("TOTO",vehicle);
        assertEquals(true,result);
      }

    @Test
    public void testParkingLotExit() throws Exception{
        //TODO: check that the fare generated and out time are populated correctly in the database
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        Ticket ticket = null;

        try {
            ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());

        }catch (Exception e){

        }
        assertNotNull(ticket);
        assertNotNull(ticket.getOutTime());
        assertNotNull(ticket.getPrice());
    }

}