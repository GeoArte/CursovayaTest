import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WithdrawalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testWithdrawFromAccount() throws Exception {
        mockMvc.perform(post("/account/withdraw/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testWithdrawInvalidAmount() throws Exception {
        mockMvc.perform(post("/account/withdraw/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -50}"))
                .andExpect(status().isBadRequest());
    }
}
