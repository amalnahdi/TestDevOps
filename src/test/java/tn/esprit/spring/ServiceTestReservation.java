package tn.esprit.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.DAO.Entities.Chambre;
import tn.esprit.spring.DAO.Entities.Etudiant;
import tn.esprit.spring.DAO.Entities.Reservation;
import tn.esprit.spring.DAO.Entities.TypeChambre;
import tn.esprit.spring.DAO.Repositories.ChambreRepository;
import tn.esprit.spring.DAO.Repositories.EtudiantRepository;
import tn.esprit.spring.DAO.Repositories.ReservationRepository;
import tn.esprit.spring.Services.Reservation.ReservationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ServiceTestReservation {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private Etudiant etudiant;
    private Chambre chambre;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setIdReservation("2023/2024-Bloc A-1-123456789");
        reservation.setEstValide(true);
        reservation.setAnneeUniversitaire(LocalDate.now());

        etudiant = new Etudiant();
        etudiant.setCin(123456789);
        reservation.getEtudiants().add(etudiant);

        chambre = new Chambre();
        chambre.setNumeroChambre(1L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setIdChambre(1L);
    }

    @Test
    void testAddOrUpdate() {
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        Reservation result = reservationService.addOrUpdate(reservation);
        assertEquals(reservation, result);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testFindAll() {
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        when(reservationRepository.findAll()).thenReturn(reservations);

        List<Reservation> result = reservationService.findAll();
        assertEquals(1, result.size());
        assertEquals(reservation, result.get(0));
        verify(reservationRepository).findAll();
    }

    @Test
    void testFindById() {
        when(reservationRepository.findById(reservation.getIdReservation())).thenReturn(Optional.of(reservation));
        Reservation result = reservationService.findById(reservation.getIdReservation());
        assertEquals(reservation, result);
        verify(reservationRepository).findById(reservation.getIdReservation());
    }

    @Test
    void testDeleteById() {
        doNothing().when(reservationRepository).deleteById(reservation.getIdReservation());
        reservationService.deleteById(reservation.getIdReservation());
        verify(reservationRepository).deleteById(reservation.getIdReservation());
    }

    @Test
    void testAnnulerReservation() {
        when(reservationRepository.findByEtudiantsCinAndEstValide(etudiant.getCin(), true)).thenReturn(reservation);
        when(chambreRepository.findByReservationsIdReservation(reservation.getIdReservation())).thenReturn(chambre);
        doNothing().when(chambreRepository).save(chambre);
        doNothing().when(reservationRepository).delete(reservation);

        String result = reservationService.annulerReservation(etudiant.getCin());
        assertEquals("La réservation " + reservation.getIdReservation() + " est annulée avec succés", result);
        verify(reservationRepository).delete(reservation);
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant() {
        when(chambreRepository.findByNumeroChambre(chambre.getNumeroChambre())).thenReturn(chambre);
        when(etudiantRepository.findByCin(etudiant.getCin())).thenReturn(etudiant);
        when(chambreRepository.countReservationsByIdChambreAndReservationsAnneeUniversitaireBetween(anyLong(), any(), any()))
                .thenReturn(0); // Assuming the room is available

        Reservation result = reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(chambre.getNumeroChambre(), etudiant.getCin());
        assertNotNull(result);
        assertTrue(result.isEstValide());
        verify(reservationRepository).save(result);
        verify(chambreRepository).save(chambre);
    }

    @Test
    void testGetReservationParAnneeUniversitaire() {
        LocalDate startDate = LocalDate.of(2023, 9, 15);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        when(reservationRepository.countByAnneeUniversitaireBetween(startDate, endDate));

        long result = reservationService.getReservationParAnneeUniversitaire(startDate, endDate);
        assertEquals(5L, result);
        verify(reservationRepository).countByAnneeUniversitaireBetween(startDate, endDate);
    }

    @Test
    void testAnnulerReservations() {
        LocalDate startDate = LocalDate.of(2023, 9, 15);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        when(reservationRepository.findByEstValideAndAnneeUniversitaireBetween(true, startDate, endDate)).thenReturn(List.of(reservation));

        reservationService.annulerReservations();
        verify(reservationRepository).save(reservation);
        assertFalse(reservation.isEstValide());
    }
}
