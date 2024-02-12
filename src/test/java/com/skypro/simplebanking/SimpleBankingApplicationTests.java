package com.skypro.simplebanking;

import com.skypro.simplebanking.entity.User;
import com.skypro.simplebanking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SimpleBankingApplicationTests {
    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    public class AdminUserCreationIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Test
        public void testAdminCreatesUser_Success() throws Exception {
            // Mock запроса на создание нового пользователя администратором
            mockMvc.perform(post("/user")
                            .header("X-SECURITY-ADMIN-KEY", "admin_token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\": \"newUser\", \"password\": \"newPassword\"}"))
                    .andExpect(status().isOk()); // Ожидаемый успешный HTTP-статус

            // Проверка, что пользователь был создан и добавлен в репозиторий
            Optional<User> newUser = userRepository.findByUsername("newUser");
            assertTrue(newUser.isPresent());
        }

        @Test
        public void testAdminCreatesUser_Unauthorized() throws Exception {
            // Попытка создания пользователя без ключа администратора
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\": \"newUser\", \"password\": \"newPassword\"}"))
                    .andExpect(status().isForbidden()); // Ожидаемый HTTP-статус 403 (доступ запрещен)
        }
    }

    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    public class WithdrawalIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testWithdrawFromAccount() throws Exception {
            // Моделирование запроса на снятие средств со счета
            mockMvc.perform(post("/account/withdraw/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"amount\": 50}"))
                    .andExpect(status().isOk()); // Ожидаемый успешный HTTP-статус
        }

        @Test
        public void testWithdrawInvalidAmount() throws Exception {
            // Попытка снятия отрицательной суммы или суммы, превышающей баланс
            mockMvc.perform(post("/account/withdraw/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"amount\": -50}"))
                    .andExpect(status().isBadRequest()); // Ожидаемый HTTP-статус 400 (неверный запрос)
        }
    }


    @RunWith(SpringRunner.class)
    @SpringBootTest
    @AutoConfigureMockMvc
    @Testcontainers
    public class TransferIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void testTransferBetweenAccounts() throws Exception {
            // Моделирование запроса на перевод средств между счетами
            mockMvc.perform(post("/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountId\": 1, \"toUserId\": 2, \"toAccountId\": 3, \"amount\": 50}"))
                    .andExpect(status().isOk()); // Ожидаемый успешный HTTP-статус
        }

        @Test
        public void testTransferWithWrongCurrency() throws Exception {
            // Попытка перевода средств между счетами с разными валютами
            mockMvc.perform(post("/transfer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"fromAccountId\": 1, \"toUserId\": 2, \"toAccountId\": 4, \"amount\": 50}"))
                    .andExpect(status().isBadRequest()); // Ожидаемый HTTP-статус 400 (неверный запрос)
        }
    }


}
