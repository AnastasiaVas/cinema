package com.test.cinema.dto;

import com.test.cinema.dto.checks.CreateChecks;
import com.test.cinema.dto.checks.UpdateChecks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @NotNull(message = "id should be filled on update",
             groups = UpdateChecks.class)
    @Null(message = "id shouldn't be filled on create",
            groups = CreateChecks.class)
    private Integer id;

    @NotNull(message = "movieId should be filled on create",
             groups = CreateChecks.class)
    private Integer movieId;

    @NotNull(message = "ticketQuantity should be filled on create",
            groups = CreateChecks.class)
    @Min(value = 1, message = "ticket quantity should be a at least 1",
            groups = {CreateChecks.class, UpdateChecks.class})
    private Integer ticketQuantity;

    @NotNull(message = "orderNumber should be filled on create",
            groups = CreateChecks.class)
    private Long orderNumber;
}
