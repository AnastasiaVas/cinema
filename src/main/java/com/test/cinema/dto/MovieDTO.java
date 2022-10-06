package com.test.cinema.dto;

import com.test.cinema.dto.checks.CreateChecks;
import com.test.cinema.dto.checks.UpdateChecks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    @NotNull(message = "id should be filled on update",
            groups = UpdateChecks.class)
    @Null(message = "id shouldn't be filled on create",
            groups = CreateChecks.class)
    private Integer id;

    @NotNull(message = "name should be filled on create",
            groups = CreateChecks.class)
    private String name;

    @NotNull(message = "release year should be filled on create",
            groups = CreateChecks.class)
    private Integer releaseYear;
    private String description;
}
