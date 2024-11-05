package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.RestControllers.ReservationRestController;
import tn.esprit.spring.Services.Reservation.IReservationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ControllerTestReservation {
    @Mock
    private IReservationService reservationService;

    @InjectMocks
    private ReservationRestController reservationRestController;

    private Reservation reservation;
    private Etudiant etudiant;


    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-1-123456789");
        reservation.setEstValide(true);
    }

    @Test
    void testAddOrUpdate() {
        when(reservationService.addOrUpdate(reservation)).thenReturn(reservation);
        Reservation result = reservationRestController.addOrUpdate(reservation);
        assertEquals(reservation, result);
        verify(reservationService).addOrUpdate(reservation);
    }

    @Test
    void testFindAll() {
        List<Reservation> reservations = List.of(reservation);
        when(reservationService.findAll()).thenReturn(reservations);
        List<Reservation> result = reservationRestController.findAll();
        assertEquals(1, result.size());
        assertEquals(reservation, result.get(0));
        verify(reservationService).findAll();
    }

    @Test
    void testFindById() {
        when(reservationService.findById(reservation.getIdReservation())).thenReturn(reservation);
        Reservation result = reservationRestController.findById(reservation.getIdReservation());
        assertEquals(reservation, result);
        verify(reservationService).findById(reservation.getIdReservation());
    }

    @Test
    void testDeleteById() {
        doNothing().when(reservationService).deleteById(reservation.getIdReservation());
        reservationRestController.deleteById(reservation.getIdReservation());
        verify(reservationService).deleteById(reservation.getIdReservation());
    }


}
