
package com.fraudwatch.fraudruleengine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudCheckRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @Valid
    @NotNull(message = "Customer details are required")
    private Customer customer;

    @Valid
    @NotNull(message = "Device details are required")
    private Device device;

    @Valid
    private Merchant merchant;

    @Valid
    @NotNull(message = "Payment details are required")
    private Payment payment;

    @Valid
    private Address address;

    @Valid
    private Location location;

    @Valid
    private Authentication authentication;

    private Map<String, Object> metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        @NotBlank(message = "Customer ID is required")
        private String id;

        @Email(message = "Invalid email format")
        private String email;

        private String phone;

        @Min(value = 0, message = "Account age must be positive")
        private Integer accountAgeDays;

        @Min(value = 0, message = "Failed login attempts must be positive")
        private Integer failedLoginAttempts;

        private Boolean isVerified;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Device {
        @NotBlank(message = "Device ID is required")
        private String id;

        private String fingerprint;

        private String userAgent;

        @NotBlank(message = "IP address is required")
        @Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$|^(([0-9a-fA-F]{1,4}):){7}[0-9a-fA-F]{1,4}$", message = "Invalid IP address")
        private String ipAddress;

        private Boolean isEmulator;

        private Boolean isRooted;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Merchant {
        @NotBlank(message = "Merchant ID is required")
        private String id;

        private String name;

        private String country;

        @Min(value = 0, message = "Risk score must be between 0 and 100")
        @Max(value = 100, message = "Risk score must be between 0 and 100")
        private Integer riskScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment {
        @NotBlank(message = "Payment ID is required")
        private String id;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
        private String currency;

        private String cardHash;

        @Pattern(regexp = "^[0-9]{4}$", message = "Last four digits must be 4 numbers")
        private String lastFour;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod;

        @Min(value = 0, message = "Failed attempts must be positive")
        private Integer failedAttempts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;

        private String city;

        private String state;

        private String postalCode;

        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        @Min(value = -90, message = "Latitude must be between -90 and 90")
        @Max(value = 90, message = "Latitude must be between -90 and 90")
        private Double latitude;

        @Min(value = -180, message = "Longitude must be between -180 and 180")
        @Max(value = 180, message = "Longitude must be between -180 and 180")
        private Double longitude;

        private String country;

        private Boolean isVpn;

        private Boolean isProxy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authentication {
        private String method;

        private Boolean isMfaEnabled;

        private Boolean isMfaVerified;
    }
}

