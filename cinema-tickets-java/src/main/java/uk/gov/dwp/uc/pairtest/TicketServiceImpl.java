package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;


public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private TicketPaymentService paymentService = new TicketPaymentServiceImpl();
    private SeatReservationService seatReservationService = new SeatReservationServiceImpl();

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

    /*
    - There are 3 types of tickets i.e. Infant, Child, and Adult.
	- The ticket prices are based on the type of ticket (see table below).
	- The ticket purchaser declares how many and what type of tickets they want to buy.
	- Multiple tickets can be purchased at any given time.
	- Only a maximum of 20 tickets that can be purchased at a time.
	- Infants do not pay for a ticket and are not allocated a seat. They will be sitting on an Adult's lap.
	- Child and Infant tickets cannot be purchased without purchasing an Adult ticket.
    * */

        if (ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException();
        }

        int adultCount = 0, childCount = 0, infantCount = 0;
        for(TicketTypeRequest ticketTypeRequest : ticketTypeRequests){
            if(ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.ADULT))
                adultCount = ticketTypeRequest.getNoOfTickets();
            if(ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.CHILD))
                childCount = ticketTypeRequest.getNoOfTickets();
            if(ticketTypeRequest.getTicketType().equals(TicketTypeRequest.Type.INFANT))
                infantCount = ticketTypeRequest.getNoOfTickets();
        }
        final int totalTickets = adultCount + childCount + infantCount;
        if (adultCount == 0 || accountId <= 0 || totalTickets > 20) {
            throw new InvalidPurchaseException();
        }
        final int amountToPay = adultCount * 20 + childCount * 10;
        paymentService.makePayment(accountId, amountToPay);

        final int seats = adultCount + childCount;
        seatReservationService.reserveSeat(accountId, seats);
    }


}
