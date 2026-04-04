package src.controller.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditCardInformation {

    @NotNull(message = "Card number cannot be null")
    @NotBlank(message = "Card number cannot be blank")
    @Size(min = 16, max = 16, message = "Card number must be 16 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "Card number must consist of digits only.")
    private String cardNumber;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name/surname must consist of letters only.")
    @Size(min = 2, max = 20)
    private String cardOwnerName;

    @NotNull(message = "Surname cannot be null")
    @NotBlank(message = "Surname cannot be blank")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Name/surname must consist of letters only.")
    @Size(min = 2, max = 20)
    private String cardOwnerSurname;

    @NotNull(message = "Expiration date cannot be null")
    @NotBlank(message = "Expiration date cannot be blank")
    private LocalDate expirationDate;

    @NotNull(message = "CVC cannot be null")
    @NotBlank(message = "CVC cannot be blank")
    @Size(min = 3, max = 3, message = "CVC must be 3 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "CVC must consist of digits only.")
    private String cvc;
}
