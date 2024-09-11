package com.zerobase.orderapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.to.OrderResult;
import com.zerobase.orderapi.domain.form.CancelOrder;
import com.zerobase.orderapi.domain.form.RefundForm;
import com.zerobase.orderapi.domain.order.OrdersDto;
import com.zerobase.orderapi.domain.type.OrderStatus;
import com.zerobase.orderapi.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class OrderControllerTest {
    @MockBean
    private OrderService orderService;
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
    void getOrder() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        OrdersDto OrdersDto = getOrdersDto();

        given(orderService.getOrderById(anyLong(), anyLong()))
                .willReturn(OrdersDto);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/order/customer/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-order",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("id").description("주문 id")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )

                        )
                );

        // openapi3
        mvc.perform(get("/api/order/customer/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(document("get-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("주문 상세정보 확인")
                                .summary("주문 상세정보 확인")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("id").description("주문 id")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )
                                .build()

                        )
                ));
    }

    private static OrdersDto getOrdersDto() {
        OrdersDto ordersDto = OrdersDto.builder()
                .id(1L)
                .sellerId(1L)
                .customerId(1L)
                .storeId(2L)
                .itemId(6L)
                .itemName("샹티크림끌레오르")
                .optionId(11L)
                .optionName("얼그레이샹티크림 끌레오르")
                .price(52000)
                .quantity(2)
                .orderStatus(OrderStatus.ORDERED)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        return ordersDto;
    }

    @Test
    void getOrders() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        List<OrderResult> orderResults = getOrderList();
        Pageable pageable = PageRequest.of(0, 5);
        Page<OrderResult> results = new PageImpl<>(orderResults, pageable, orderResults.size());


        given(orderService.getOrders(anyLong(), any(), any(), any()))
                .willReturn(results);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/order/customer/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("start", "2024-08-26")
                        .param("end", "2024-08-29")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-orders-between-period",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("start").description("검색 원하는 시작 날짜"),
                                        parameterWithName("end").description("검색 원하는 마지막 날짜"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                )
                                , responseFields(
                                        fieldWithPath("content[].orderId").description("주문 id"),
                                        fieldWithPath("content[].itemName").description("주문 아이템 이름"),
                                        fieldWithPath("content[].optionName").description("주문 옵션 이름"),
                                        fieldWithPath("content[].optionPrice").description("주문 옵션 가격"),
                                        fieldWithPath("content[].optionQuantity").description("주문 옵션 수량"),
                                        fieldWithPath("content[].orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("content[].createdAt").description("주문 생성 날짜"),

                                        fieldWithPath("pageable.sort.sorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").description("pageable type")

                                )

                        )
                );

        // openapi3
        mvc.perform(get("/api/order/customer/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("start", "2024-08-26")
                        .param("end", "2024-08-29")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("get-orders-between-period",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("설정한 기간내에 고객이 자신의 주문 내역 확인")
                                .summary("기간별 주문내역 확인")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("start").description("검색 원하는 시작 날짜"),
                                        parameterWithName("end").description("검색 원하는 마지막 날짜"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                )
                                .responseFields(
                                        fieldWithPath("content[].orderId").description("주문 id"),
                                        fieldWithPath("content[].itemName").description("주문 아이템 이름"),
                                        fieldWithPath("content[].optionName").description("주문 옵션 이름"),
                                        fieldWithPath("content[].optionPrice").description("주문 옵션 가격"),
                                        fieldWithPath("content[].optionQuantity").description("주문 옵션 수량"),
                                        fieldWithPath("content[].orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("content[].createdAt").description("주문 생성 날짜"),

                                        fieldWithPath("pageable.sort.sorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").description("pageable type")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void cancelOrder() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        CancelOrder cancelOrder = CancelOrder.builder()
                .itemName("샹티크림끌레오르")
                .optionName("얼그레이샹티크림 끌레오르")
                .cancelTime(LocalDateTime.now())
                .build();
        given(orderService.cancelOrder(anyLong(), anyLong()))
                .willReturn(cancelOrder);
        //when

        //then
        // asciidoc
        mvc.perform(delete("/api/order/customer/order/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cancel-order",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("id").description("취소 원하는 주문 id")
                                )
                                , responseFields(
                                        fieldWithPath("itemName").description("주문 취소한 아이템 이름"),
                                        fieldWithPath("optionName").description("주문 취소한 옵션 이름"),
                                        fieldWithPath("cancelTime").description("주문 취소 날짜/시간")

                                )

                        )
                );

        // openapi3
        mvc.perform(delete("/api/order/customer/order/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(document("cancel-order",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객이 주문 id로 주문 취소")
                                .summary("주문 취소")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("id").description("취소 원하는 주문 id")
                                )
                                .responseFields(
                                        fieldWithPath("itemName").description("주문 취소한 아이템 이름"),
                                        fieldWithPath("optionName").description("주문 취소한 옵션 이름"),
                                        fieldWithPath("cancelTime").description("주문 취소 날짜/시간")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void getOrdersByStore() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        List<OrderResult> orderResults = getOrderList();
        Pageable pageable = PageRequest.of(0, 5);
        Page<OrderResult> results = new PageImpl<>(orderResults, pageable, orderResults.size());


        given(orderService.getOrdersByStore(anyLong(), anyLong(), any(), any(), any()))
                .willReturn(results);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/order/seller/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("storeId", "1")
                        .param("start", "2024-08-26")
                        .param("end", "2024-08-29")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-orders-by-store-between-period",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("storeId").description("검색 원하는 스토어 id"),
                                        parameterWithName("start").description("검색 원하는 시작 날짜"),
                                        parameterWithName("end").description("검색 원하는 마지막 날짜"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                )
                                , responseFields(
                                        fieldWithPath("content[].orderId").description("주문 id"),
                                        fieldWithPath("content[].itemName").description("주문 아이템 이름"),
                                        fieldWithPath("content[].optionName").description("주문 옵션 이름"),
                                        fieldWithPath("content[].optionPrice").description("주문 옵션 가격"),
                                        fieldWithPath("content[].optionQuantity").description("주문 옵션 수량"),
                                        fieldWithPath("content[].orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("content[].createdAt").description("주문 생성 날짜"),

                                        fieldWithPath("pageable.sort.sorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").description("pageable type")

                                )

                        )
                );

        // openapi3
        mvc.perform(get("/api/order/seller/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("storeId", "1")
                        .param("start", "2024-08-26")
                        .param("end", "2024-08-29")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("get-orders-by-store-between-period",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("설정한 기간내에 셀러가 자신의 스토어 주문 확인")
                                .summary("기간/스토어별 주문내역 확인")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("storeId").description("검색 원하는 스토어 id"),
                                        parameterWithName("start").description("검색 원하는 시작 날짜"),
                                        parameterWithName("end").description("검색 원하는 마지막 날짜"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                )
                                .responseFields(
                                        fieldWithPath("content[].orderId").description("주문 id"),
                                        fieldWithPath("content[].itemName").description("주문 아이템 이름"),
                                        fieldWithPath("content[].optionName").description("주문 옵션 이름"),
                                        fieldWithPath("content[].optionPrice").description("주문 옵션 가격"),
                                        fieldWithPath("content[].optionQuantity").description("주문 옵션 수량"),
                                        fieldWithPath("content[].orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("content[].createdAt").description("주문 생성 날짜"),

                                        fieldWithPath("pageable.sort.sorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").description("pageable type")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void requestRefund() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        OrdersDto OrdersDto = getOrdersDto();

        given(orderService.requestRefund(anyLong(), anyLong()))
                .willReturn(OrdersDto);
        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/order/customer/refund/{id}", 1)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("request-refund",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("환불 신청원하는 주문 id")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )

                        )
                );

        // openapi3
        mvc.perform(patch("/api/order/customer/refund/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(document("request-refund",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객의 자신의 주문 환불 신청")
                                .summary("환불 신청")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .pathParameters(
                                        parameterWithName("id").description("환불 신청원하는 주문 id")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜"))
                                .build()

                        )
                ));
    }

    @Test
    void cancelRequestRefund() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        OrdersDto OrdersDto = getOrdersDto();

        given(orderService.cancelRequestRefund(anyLong(), anyLong()))
                .willReturn(OrdersDto);
        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/order/customer/refund/cancel/{id}", 1)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("cancel-request-refund",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("환불 신청원하는 주문 id")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )

                        )
                );

        // openapi3
        mvc.perform(patch("/api/order/customer/refund/cancel/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(document("cancel-request-refund",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("고객의 자신의 주문 환불 신청 취소")
                                .summary("환불 신청 취소")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .pathParameters(
                                        parameterWithName("id").description("환불 신청원하는 주문 id")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜"))
                                .build()

                        )
                ));
    }

    @Test
    void approveRequestRefund() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        OrdersDto OrdersDto = getOrdersDto();
        RefundForm form = RefundForm.builder()
                .id(1L)
                .date(LocalDate.now())
                .build();
        given(orderService.approveRequestRefund(anyString(), anyLong(), any()))
                .willReturn(OrdersDto);
        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/order/seller/refund/approve")
                        .header("Authorization", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        ))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("approve-request-refund",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("id").description("환불 수락 원하는 주문 id"),
                                        fieldWithPath("date").description("환불 수락 원하는 주문 날짜")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )

                        )
                );

        // openapi3
        mvc.perform(patch("/api/order/seller/refund/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        ))
                )
                .andExpect(status().isOk())
                .andDo(document("approve-request-refund",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 주문 환불 요청 수락")
                                .summary("환불 요청 수락")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("id").description("환불 수락 원하는 주문 id"),
                                        fieldWithPath("date").description("환불 수락 원하는 주문 날짜")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜"))
                                .build()

                        )
                ));
    }

    @Test
    void rejectRequestRefund() throws Exception {
        //given
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);
        OrdersDto OrdersDto = getOrdersDto();

        given(orderService.rejectRequestRefund(anyLong(), anyLong()))
                .willReturn(OrdersDto);
        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/order/seller/refund/reject/{id}", 1)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reject-request-refund",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("환불 거절 원하는 주문 id")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜")
                                )

                        )
                );

        // openapi3
        mvc.perform(patch("/api/order/seller/refund/reject/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(document("reject-request-refund",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 주문 환불 요청 거절")
                                .summary("환불 요청 거절")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .pathParameters(
                                        parameterWithName("id").description("환불 거절 원하는 주문 id")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("주문 id"),
                                        fieldWithPath("sellerId").description("셀러 id"),
                                        fieldWithPath("customerId").description("주문 고객 id"),
                                        fieldWithPath("storeId").description("스토어 이름"),
                                        fieldWithPath("itemId").description("주문 아이템 id"),
                                        fieldWithPath("itemName").description("주문 아이템 이름"),
                                        fieldWithPath("optionId").description("주문 옵션 id"),
                                        fieldWithPath("optionName").description("주문 옵션 이름"),
                                        fieldWithPath("price").description("주문 옵션 가격"),
                                        fieldWithPath("quantity").description("주문 옵션 수량"),
                                        fieldWithPath("orderStatus").description("주문 상태 - ORDERED : 주문 완료 " +
                                                "    ORDERED_COMPLETED : 주문/정산 완료," +
                                                "    REFUND_REQUEST : 환불 신청," +
                                                "    REFUND_APPROVED : 환불 승인," +
                                                "    REFUND_REJECTED : 환불 거절"),
                                        fieldWithPath("createdAt").description("주문 생성 날짜"),
                                        fieldWithPath("modifiedAt").description("주문 수정 날짜"))
                                .build()

                        )
                ));
    }

    private List<OrderResult> getOrderList() {
        List<OrderResult> results = new ArrayList<>();
        results.add(OrderResult.builder()
                .orderId(3L)
                .itemName("샹티크림끌레오르")
                .optionName("얼그레이샹티크림 끌레오르")
                .optionPrice(26000)
                .optionQuantity(2)
                .orderStatus(OrderStatus.ORDERED)
                .createdAt(LocalDateTime.now())
                .build()
        );

        results.add(OrderResult.builder()
                .orderId(4L)
                .itemName("샹티크림끌레오르")
                .optionName("블루베리샹티크림 끌레오르")
                .optionPrice(26000)
                .optionQuantity(1)
                .orderStatus(OrderStatus.ORDERED)
                .createdAt(LocalDateTime.now())
                .build()
        );

        return results;
    }
}