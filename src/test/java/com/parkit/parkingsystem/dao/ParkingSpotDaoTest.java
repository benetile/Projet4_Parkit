package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mysql.cj.xdevapi.PreparableStatement;
import com.parkit.parkingsystem.ParkingServiceTest;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import edu.umd.cs.findbugs.annotations.DischargesObligation;
import org.slf4j.ILoggerFactory;

import javax.annotation.meta.When;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDaoTest {


	public DataBasePrepareService dataPrepareService = new DataBasePrepareService();

	public DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	@Mock
	TicketDAO ticketDAO;

	@Mock
	ParkingSpotDAO parkingSpotDAO;

	@Mock
	InputReaderUtil inputReaderUtil;

	ParkingService parkingService;

	@BeforeEach
	public void setUp() throws Exception {
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;

		dataPrepareService.clearDataBaseEntries();
	}

	@AfterEach
	public void clean(){

		dataBaseTestConfig.closeConnection(null);
	}

	@Test
	public void getNextAvailableSlotTest(){
		//ARRANGE
		parkingService = new ParkingService(inputReaderUtil,parkingSpotDAO,ticketDAO);
		ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,false);
		parkingService.processIncomingVehicle();
		Ticket ticket = null;
		Boolean result = false;
		//ACT
		try {
			ticket = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
			result = parkingSpotDAO.updateParking(parkingSpot);
		}catch (Exception e){

		}
		//ASSERT
		assertEquals(true,result);
	}

}
