package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import src.controller.image.ImageController;
import src.core.config.AppConfig;
import src.core.config.SecurityConfig;
import src.core.security.JwtService;
import src.repository.image.BrandImageEntity;
import src.repository.image.CarImageEntity;
import src.service.image.brand.BrandImageService;
import src.service.image.car.CarImageService;
import src.service.image.user.UserImageService;
import src.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for {@link ImageController}.
 *
 * Image upload endpoints accept multipart data and delegate to Cloudinary.
 * All three endpoints (car, user, brand image) must be restricted to ADMIN:
 *  - Car image → identified by license plate
 *  - User image → identified by email address
 *  - Brand image → identified by brand name
 *
 * Covered gaps (from ANALISI_SICUREZZA_2304): Point 4 — endpoint auth not tested
 * (existing FileUploadSecurityTest only covers service-layer content-type validation).
 */
@WebMvcTest(ImageController.class)
@Import({SecurityConfig.class, AppConfig.class})
@ActiveProfiles("test")
@DisplayName("ImageController – security tests")
class ImageControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private UserService userService;
    @MockBean private CarImageService carImageService;
    @MockBean private UserImageService userImageService;
    @MockBean private BrandImageService brandImageService;

    // ======================================================================
    //  Unauthenticated access — must return 401 for all upload endpoints
    // ======================================================================

    @Nested
    @DisplayName("Unauthenticated access – must return 401")
    class UnauthenticatedAccess {

        @Test
        @DisplayName("POST /api/v1/images/car returns 401 without token")
        void uploadCarImage_noAuth_returns401() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/car")
                            .file(minimalJpeg("image"))
                            .param("licensePlate", "AB123CD"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/images/user returns 401 without token")
        void uploadUserImage_noAuth_returns401() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/user")
                            .file(minimalJpeg("image"))
                            .param("emailAddress", "user@example.com"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/v1/images/brand returns 401 without token")
        void uploadBrandImage_noAuth_returns401() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/brand")
                            .file(minimalJpeg("image"))
                            .param("brandName", "Toyota"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ======================================================================
    //  Non-ADMIN roles — must be forbidden with 403
    //  A CUSTOMER uploading images for arbitrary license plates or email
    //  addresses would bypass ownership and allow data tampering.
    // ======================================================================

    @Nested
    @DisplayName("CUSTOMER role – must be forbidden (403) on all upload endpoints")
    class CustomerRoleForbidden {

        @Test
        @DisplayName("POST /api/v1/images/car returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void uploadCarImage_customerRole_returns403() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/car")
                            .file(minimalJpeg("image"))
                            .param("licensePlate", "AB123CD"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/images/user returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void uploadUserImage_customerRole_returns403() throws Exception {
            // CUSTOMER could otherwise overwrite another user's profile image
            // by providing a different emailAddress parameter.
            mockMvc.perform(multipart("/api/v1/images/user")
                            .file(minimalJpeg("image"))
                            .param("emailAddress", "victim@example.com"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/images/brand returns 403 for CUSTOMER")
        @WithMockUser(roles = "CUSTOMER")
        void uploadBrandImage_customerRole_returns403() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/brand")
                            .file(minimalJpeg("image"))
                            .param("brandName", "Toyota"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("EMPLOYEE role – must be forbidden (403) on all upload endpoints")
    class EmployeeRoleForbidden {

        @Test
        @DisplayName("POST /api/v1/images/car returns 403 for EMPLOYEE")
        @WithMockUser(roles = "EMPLOYEE")
        void uploadCarImage_employeeRole_returns403() throws Exception {
            mockMvc.perform(multipart("/api/v1/images/car")
                            .file(minimalJpeg("image"))
                            .param("licensePlate", "AB123CD"))
                    .andExpect(status().isForbidden());
        }
    }

    // ======================================================================
    //  ADMIN role — happy paths (service layer mocked)
    // ======================================================================

    @Nested
    @DisplayName("ADMIN role – must have upload access")
    class AdminRoleAccess {

        @Test
        @DisplayName("POST /api/v1/images/car returns 200 for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void uploadCarImage_adminRole_succeeds() throws Exception {
            CarImageEntity mockEntity = mock(CarImageEntity.class);
            when(mockEntity.getId()).thenReturn(1);
            when(carImageService.create(any(), anyString())).thenReturn(mockEntity);

            mockMvc.perform(multipart("/api/v1/images/car")
                            .file(minimalJpeg("image"))
                            .param("licensePlate", "AB123CD"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/v1/images/brand returns 200 for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void uploadBrandImage_adminRole_succeeds() throws Exception {
            BrandImageEntity mockBrandEntity = mock(BrandImageEntity.class);
            when(mockBrandEntity.getId()).thenReturn(2);
            when(brandImageService.create(any(), anyString())).thenReturn(mockBrandEntity);

            mockMvc.perform(multipart("/api/v1/images/brand")
                            .file(minimalJpeg("image"))
                            .param("brandName", "Toyota"))
                    .andExpect(status().isOk());
        }
    }

    // ---- helpers -----------------------------------------------------------

    private static MockMultipartFile minimalJpeg(String partName) {
        // Minimal JPEG magic bytes — enough to form a valid multipart request
        // for security (auth/authz) testing. Image processing is mocked.
        byte[] jpegMagic = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        return new MockMultipartFile(partName, "test.jpg", "image/jpeg", jpegMagic);
    }
}
