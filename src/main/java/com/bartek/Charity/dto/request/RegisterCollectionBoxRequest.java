package com.bartek.Charity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCollectionBoxRequest {
        @NotBlank(message = "Identifier is required")
        @Size(min = 3, max = 20, message = "Identifier must be between 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Identifier can only contain letters, numbers, hyphens, and underscores")
        private String identifier;
}
