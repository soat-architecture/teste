package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.shared.DateUtils;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * A generic response DTO that wraps API responses with standard information.
 *
 * @param <T> The type of data being wrapped
 */
public record DefaultResponseDTO<T>(
        int status,
        T data,
        String timestamp
) {
    private static final String DEFAULT_SUCCESSFULLY_RESPONSE = "Successfully in the request. No given response.";
    private static final String DEFAULT_LOGIN_SUCCESSFULLY_RESPONSE = "Login with successfully. No given response.";
    private static final String DEFAULT_LOGOUT_SUCCESSFULLY_RESPONSE = "Logout with successfully. No given response.";

    /**
     * Creates a response with status and data, automatically setting the current timestamp
     *
     * @param status HTTP status code
     * @param data Response payload
     */
    public DefaultResponseDTO(int status, T data) {
        this(status, data, DateUtils.toStringFormated(LocalDateTime.now()));
    }

    /**
     * Creates a successful response (HTTP 200) with the given data
     *
     * @param data Response payload
     * @return A DefaultDTO instance with status 200
     * @param <T> Type of the response data
     */
    public static <T> DefaultResponseDTO<T> success(T data) {
        return new DefaultResponseDTO<>(HttpStatus.OK.value(), data);
    }

    /**
     * Creates a response for a newly created resource (HTTP 201) with the given data
     *
     * @param data Response payload
     * @return A DefaultDTO instance with status 201
     * @param <T> Type of the response data
     */
    public static <T> DefaultResponseDTO<T> created(T data) {
        return new DefaultResponseDTO<>(HttpStatus.CREATED.value(), data);
    }

    /**
     * Creates a response for a newly created resource (HTTP 201) with the no given data
     *
     * @return A DefaultDTO instance with status 201
     */
    public static DefaultResponseDTO<DefaultDataDTO> created() {
        final var data = new DefaultDataDTO(DEFAULT_SUCCESSFULLY_RESPONSE);
        return new DefaultResponseDTO<>(HttpStatus.CREATED.value(), data);
    }

    /**
     * Creates a response for a newly created resource (HTTP 204) with the no given data
     *
     * @return A DefaultDTO instance with status 204
     */
    public static DefaultResponseDTO<DefaultDataDTO> ok() {
        final var data = new DefaultDataDTO(DEFAULT_SUCCESSFULLY_RESPONSE);
        return new DefaultResponseDTO<>(HttpStatus.OK.value(), data);
    }

    /**
     * Creates a response for a newly created resource (HTTP 204) with the no given data
     *
     * @return A DefaultDTO instance with status 204
     */
    public static DefaultResponseDTO<DefaultDataDTO> login() {
        final var data = new DefaultDataDTO(DEFAULT_LOGIN_SUCCESSFULLY_RESPONSE);
        return new DefaultResponseDTO<>(HttpStatus.OK.value(), data);
    }

    /**
     * Creates a response for a newly created resource (HTTP 204) with the no given data
     *
     * @return A DefaultDTO instance with status 204
     */
    public static DefaultResponseDTO<DefaultDataDTO> logout() {
        final var data = new DefaultDataDTO(DEFAULT_LOGOUT_SUCCESSFULLY_RESPONSE);
        return new DefaultResponseDTO<>(HttpStatus.OK.value(), data);
    }
}
