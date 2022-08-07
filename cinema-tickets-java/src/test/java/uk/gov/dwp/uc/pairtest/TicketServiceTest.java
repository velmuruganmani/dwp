package uk.gov.dwp.uc.pairtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService seatReservationService;


    @Test
    public void testPurchaseTicketsWithValidRequest(){
        TicketTypeRequest t1 =  new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);
        TicketTypeRequest t2 =  new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5);
        TicketTypeRequest t3 =  new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        doNothing().when(paymentService).makePayment(1234L, 250);
        doNothing().when(seatReservationService).reserveSeat(1234L, 15);

        ticketServiceImpl.purchaseTickets(1234L, t1, t2, t3);

        verify(paymentService, times(1)).makePayment(1234L, 250);
        verify(seatReservationService, times(1)).reserveSeat(1234L, 15);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsWhenTicketCountMoreThan20(){
        TicketTypeRequest t1 =  new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10);
        TicketTypeRequest t2 =  new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest t3 =  new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        ticketServiceImpl.purchaseTickets(1234L, t1, t2, t3);

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsWithoutAdultTicket(){
        TicketTypeRequest t2 =  new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest t3 =  new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        ticketServiceImpl.purchaseTickets(1234L, t2, t3);

    }

    @Test(expected = InvalidPurchaseException.class)
    public void testPurchaseTicketsWhenRequestIsEmpty(){
        TicketTypeRequest t2 =  new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest t3 =  new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        ticketServiceImpl.purchaseTickets(1234L);

    }
}
