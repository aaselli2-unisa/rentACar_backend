package src.controller.payment.detail.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePaymentDetailsRequest {
    @NotNull(message = "Id cannot be null")
    private int id;
    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Amount cannot be less than 0")
    private double amount;

}
