package src.controller;

import lombok.Builder;

@Builder(builderMethodName = "tResponseBuilder")
public record TResponse<T>(
        T response //-> We send the DTO object here.
) {

}