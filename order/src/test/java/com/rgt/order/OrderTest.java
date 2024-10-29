package com.rgt.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        // 1. 주문 생성
        String orderJson = "{\"food\":\"피자\",\"quantity\":2,\"seatNum\":3}";

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isCreated());

        // 2. 대시보드에서 주문 목록 확인
        mockMvc.perform(get("/allOrders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].food").value("피자")) // 첫 번째 주문의 메뉴가 '피자'인지 확인
                .andExpect(jsonPath("$[0].quantity").value(2)); // 수량이 2인지 확인
    }
	
}
