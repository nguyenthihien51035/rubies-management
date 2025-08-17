package com.example.rubiesmanagement.form.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserStatusForm {
    @NotNull(message = "Trạng thái hoạt động không được để trống")
    private Boolean active;
}
