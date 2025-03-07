package com.lehdonjoki.listoo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {

  @Schema(
      description = "Refresh token for generating new access token",
      example = "eyJhbGciOiJIUzI1...")
  @NotBlank(message = "Refresh token is required")
  private String refreshToken;
}
