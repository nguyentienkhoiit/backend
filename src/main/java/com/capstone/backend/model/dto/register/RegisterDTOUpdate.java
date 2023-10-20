package com.capstone.backend.model.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDTOUpdate {

    @Schema(example = "1", description = "UserId must be integer")
    @NotNull(message = "UserId is mandatory")
    Long id;

    @Schema(example = "Nguyen", description = "Length greater than 3")
    @NotBlank(message = "Firstname is mandatory")
    @Length(min = 2, message = "Length greater than 1")
    String firstname;

    @Schema(example = "Nguyen", description = "Length greater than 3")
    @NotBlank(message = "Lastname is mandatory")
    @Length(min = 2, message = "Length greater than 1")
    String lastname;

    @Schema(description = "Include 10 or 11 digit", example = "0123456789")
    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone just include 10 or 11 digit")
    String phone;

    @NotNull(message = "Gender is mandatory")
    Boolean gender;

    @Schema(description = "School name", example = "Trung học phổ thông B")
    @NotBlank(message = "School name is mandatory")
    String school;

    @Schema(example = "Hà Nội", description = "Length greater than 4")
    @NotBlank(message = "Address is mandatory")
    @Length(min = 5, message = "Address has length greater than 4")
    String province;

    @Schema(example = "Bình Lục", description = "Length greater than 4")
    @NotBlank(message = "Address is mandatory")
    @Length(min = 5, message = "Address has length greater than 4")
    String district;

    @Schema(example = "Ngọc Lũ", description = "Length greater than 4")
    @NotBlank(message = "Address is mandatory")
    @Length(min = 5, message = "Address has length greater than 4")
    String village;

    @Schema(example = "1", description = "ClassId must be integer but optional")
    Long classId;

    @Schema(example = "2017-01-13", description = "date of birth")
    @NotNull(message = "Date of birth is not nul")
    LocalDate dateOfBirth;
}
