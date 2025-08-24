
package dev.com.soat.autorepairshop.infrastructure.adapter;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para EmailAdapter")
class EmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailAdapter emailAdapter;

    private String from;
    private String to;
    private String subject;
    private String message;

    @BeforeEach
    void setUp() {
        from = "from@test.com";
        to = "to@test.com";
        subject = "Test Subject";
        message = "Test Message";
    }

    @Test
    @DisplayName("Deve enviar email com sucesso quando dados são válidos")
    void send_shouldSendEmail_whenDataIsValid() {
        // given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // when/then
        assertDoesNotThrow(() ->
                emailAdapter.send(from, to, subject, message, true)
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com texto plano quando isHtml é false")
    void send_shouldSendPlainTextEmail_whenIsHtmlIsFalse() {
        // given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // when/then
        assertDoesNotThrow(() ->
                emailAdapter.send(from, to, subject, message, false)
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com caracteres especiais corretamente")
    void send_shouldHandleSpecialCharacters() {
        // given
        String messageWithSpecialChars = "Teste com acentuação: à á ã ç";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // when/then
        assertDoesNotThrow(() ->
                emailAdapter.send(from, to, subject, messageWithSpecialChars, true)
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}