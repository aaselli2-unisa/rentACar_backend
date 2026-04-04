package src.controller.payment.type.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import src.service.payment.type.model.DefaultPaymentType;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentTypeRequest {
    @NotBlank(message = "Payment type name cannot be blank")
    @Size(min = 2, max = 20)
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Payment type must consist of letters only.")
    private String paymentTypeEntityName;

    @NotNull
    @Pattern(regexp = "^[A-Z-_]+$", message = "Payment type must consist of uppercase letters only, with no spaces.")
    private DefaultPaymentType paymentType;

    @NotNull
    private boolean isActive;

}
