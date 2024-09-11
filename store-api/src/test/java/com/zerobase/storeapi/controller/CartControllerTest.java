package com.zerobase.storeapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.from.OrderResult;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.domain.redis.form.AddCartResult;
import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import com.zerobase.storeapi.domain.redis.form.DeleteOptionCartForm;
import com.zerobase.storeapi.domain.redis.form.UpdateOptionCartForm;
import com.zerobase.storeapi.service.CartOrderService;
import com.zerobase.storeapi.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class CartControllerTest {
    @MockBean
    private CartService cartService;
    @MockBean
    private CartOrderService cartOrderService;
    @MockBean
    private MemberClient memberClient;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext context,
                      RestDocumentationContextProvider restDocumentation) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void addCart() throws Exception {
        //given
        List<AddItemCartForm.Option> options = new ArrayList<>();
        options.add(AddItemCartForm.Option.builder()
                .id(12L)
                .name("블루베리샹티크림 끌레오르")
                .quantity(2)
                .price(26000)
                .build());
        options.add(AddItemCartForm.Option.builder()
                .id(11L)
                .name("얼그레이샹티크림 끌레오르")
                .quantity(2)
                .price(26000)
                .build());
        AddItemCartForm form = AddItemCartForm.builder()
                .id(6L)
                .storeId(2L)
                .storeName("달달구리해닮")
                .name("샹티크림끌레오르")
                .options(options)
                .build();


        AddCartResult cart = AddCartResult.builder()
                .storeName("달달구리해닮")
                .name("샹티크림끌레오르")
                .price(26000)
                .build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        given(cartService.addCart(anyLong(), any()))
                .willReturn(cart);
        //when

        //then
        // asciidoc
        mvc.perform(post("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("add-cart",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("id").description("아이템 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("storeName").description("스토어 이름"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                , responseFields(
                                        fieldWithPath("storeName").description("스토어 이름"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("price").description("첫번째 옵션 가격")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("add-cart",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 아이템의 옵션을 장바구니에 담음")
                                .summary("장바구니 추가")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("id").description("아이템 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("storeName").description("스토어 이름"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                .responseFields(
                                        fieldWithPath("storeName").description("스토어 이름"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("price").description("첫번째 옵션 가격")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void getCart() throws Exception {
        //given
        Cart cart = Cart.builder()
                .customerId(1L)
                .items(getCartItem())
                .messages(new ArrayList<>())
                .totalPrice(104000).build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        given(cartService.getCart(anyLong()))
                .willReturn(cart);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(document("get-cart",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                , responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )

                        )
                );

        // openapi3
        mvc.perform(get("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andDo(document("get-cart",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 자신이 장바구니에 담은 아이템과 옵션을 확인")
                                .summary("장바구니 확인")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void deleteCartOption() throws Exception {
        //given
        Cart cart = Cart.builder()
                .customerId(1L)
                .items(getCartItem())
                .messages(new ArrayList<>())
                .totalPrice(104000).build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        DeleteOptionCartForm form = DeleteOptionCartForm.builder()
                .optionIds(Arrays.asList(1L,3L,6L))
                .build();

        given(cartService.deleteCartOption(anyLong(), any()))
                .willReturn(cart);
        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("delete-cart-option",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),requestFields(
                                        fieldWithPath("optionIds").description("삭제할 옵션 id 리스트")
                        )
                                , responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )

                        )
                );

        // openapi3
        mvc.perform(patch("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                .content(objectMapper.writeValueAsString(
                        form
                )))
                .andExpect(status().isOk())
                .andDo(document("delete-cart-option",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 자신이 장바구니에 담은 아이템을 옵션 아이디 이용해 삭제 (리스트로 여러개 삭제 가능)")
                                .summary("장바구니 옵션 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ).requestFields(
                                        fieldWithPath("optionIds").description("삭제할 옵션 id 리스트")
                                )
                                .responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )
                                .build()

                        )
                ));

    }

    @Test
    void updateCartOption() throws Exception {
        //given
        Cart cart = Cart.builder()
                .customerId(1L)
                .items(getCartItem())
                .messages(new ArrayList<>())
                .totalPrice(104000).build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        UpdateOptionCartForm form = UpdateOptionCartForm.builder()
                .itemId(6L)
                .optionId(12L)
                .quantity(5)
                .build();
        given(cartService.updateCartOption(anyLong(), any()))
                .willReturn(cart);
        //when

        //then
        // asciidoc
        mvc.perform(put("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-cart-option",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("itemId").description("아이템 id"),
                                        fieldWithPath("optionId").description("옵션 id"),
                                        fieldWithPath("quantity").description("변경원하는 수량")
                                )
                                , responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )

                        )
                );

        // openapi3
        mvc.perform(put("/api/store/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-cart-option",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 자신이 장바구니에 담은 아이템의 옵션 수량을 변경")
                                .summary("장바구니 옵션 변경")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("itemId").description("아이템 id"),
                                        fieldWithPath("optionId").description("옵션 id"),
                                        fieldWithPath("quantity").description("변경원하는 수량")
                                )
                                .responseFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )
                                .build()

                        )
                ));

    }

    @Test
    void orderCart() throws Exception {
        //given
        Cart cart = Cart.builder()
                .customerId(1L)
                .items(getCartItem())
                .messages(new ArrayList<>())
                .totalPrice(104000).build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        List<OrderResult> results = new ArrayList<>();
        results.add(OrderResult.builder()
                .orderId(2L)
                .itemName("샹티크림끌레오르")
                .optionName("얼그레이샹티크림 끌레오르")
                .optionPrice(26000)
                .optionQuantity(2)
                .createdAt(LocalDateTime.now())
                .build());
        given(cartOrderService.orderCart(anyString(), anyLong(), any()))
                .willReturn(results);
        //when

        //then
        // asciidoc
        mvc.perform(post("/api/store/cart/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                cart
                        )))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("order-cart",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )
                                , responseFields(
                                        fieldWithPath("[].orderId").description("주문 id"),
                                        fieldWithPath("[].itemName").description("아이템 이름"),
                                        fieldWithPath("[].optionName").description("아이템 옵션 이름"),
                                        fieldWithPath("[].optionPrice").description("아이템 옵션 가격"),
                                        fieldWithPath("[].optionQuantity").description("아이템 옵션 수량"),
                                        fieldWithPath("[].createdAt").description("주문 날짜/시간")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/store/cart/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                cart
                        )))
                .andExpect(status().isOk())
                .andDo(document("order-cart",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 자신이 장바구니에 담은 아이템 일부 또는 전체를 주문")
                                .summary("장바구니 주문")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("customerId").description("고객 id"),
                                        fieldWithPath("items[].id").description("아이템 id"),
                                        fieldWithPath("items[].storeId").description("스토어 id"),
                                        fieldWithPath("items[].storeName").description("스토어 이름"),
                                        fieldWithPath("items[].sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("items[].name").description("아이템 이름"),
                                        fieldWithPath("items[].options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("items[].options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("items[].options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("items[].options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("messages").description("장바구니에 담은 아이템과 옵션에 변경 사항이 있는 경우 메세지로 알림"),
                                        fieldWithPath("totalPrice").description("담은 총 금액")
                                )
                                .responseFields(
                                        fieldWithPath("[].orderId").description("주문 id"),
                                        fieldWithPath("[].itemName").description("아이템 이름"),
                                        fieldWithPath("[].optionName").description("아이템 옵션 이름"),
                                        fieldWithPath("[].optionPrice").description("아이템 옵션 가격"),
                                        fieldWithPath("[].optionQuantity").description("아이템 옵션 수량"),
                                        fieldWithPath("[].createdAt").description("주문 날짜/시간")
                                )
                                .build()

                        )
                ));

    }


    private List<Cart.Option> getCartOption() {
        List<Cart.Option> cartOptions = new ArrayList<>();
        cartOptions.add(Cart.Option.builder()
                .id(12L)
                .name("블루베리샹티크림 끌레오르")
                .quantity(2)
                .price(26000)
                .build());
        cartOptions.add(Cart.Option.builder()
                .id(11L)
                .name("얼그레이샹티크림 끌레오르")
                .quantity(2)
                .price(26000)
                .build());
        return cartOptions;
    }

    private List<Cart.Item> getCartItem() {

        List<Cart.Item> item = new ArrayList<>();
        item.add(Cart.Item.builder()
                .id(6L)
                .storeId(2L)
                .sellerId(1L)
                .storeName("달달구리해닮")
                .name("샹티크림끌레오르")
                .options(getCartOption())
                .build());
        return item;
    }
}