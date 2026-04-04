package src.controller.rental.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateRentalRequest {
    @NotNull(message = "id cannot be null")
    @Min(1)
    int id;
    @Min(1)
    @NotNull(message = "customer id cannot be null")
    private int customerEntityId;// not taken as input !!
    @Min(1)
    @NotNull(message = "car id cannot be null")
    private int carEntityId;// not taken as input !!


    @Min(1)
    @NotNull(message = "payment details id cannot be null")
    private int paymentDetailsEntityId; // not taken as input !!

    @NotNull(message = "start date cannot be null")
    private LocalDate startDate;// not taken as input !!

    @NotNull(message = "end date cannot be null")
    private LocalDate endDate;// not taken as input !!

    private LocalDate returnDate;// not taken as input !!

    private Integer startKilometer;

    private Integer endKilometer;

    private Integer discountEntityId;// not taken as input !!

    @NotNull
    private int rentalStatusId;

    @NotNull(message = "is active cannot be null")
    private boolean isActive;

}
