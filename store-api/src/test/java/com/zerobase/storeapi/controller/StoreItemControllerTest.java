package com.zerobase.storeapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.dto.OptionDto;
import com.zerobase.storeapi.domain.form.item.CreateItem;
import com.zerobase.storeapi.domain.form.item.DeleteItem;
import com.zerobase.storeapi.domain.form.item.UpdateItem;
import com.zerobase.storeapi.domain.form.option.CreateOption;
import com.zerobase.storeapi.domain.type.Category;
import com.zerobase.storeapi.service.StoreItemService;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoreItemController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class StoreItemControllerTest {
    @MockBean
    private MemberClient memberClient;
    @MockBean
    private StoreItemService storeItemService;
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
    void createItem() throws Exception {
        //given
        CreateItem form = CreateItem.builder()
                .storeId(1L)
                .name("디디얌 마카롱 수제 뚱카롱 32종")
                .category(Category.MACARON)
                .thumbnailUrl("https://마카롱.png")
                .description("마카롱에 최고급프랑스산 발효버터를 사용합니다:)")
                .descriptionUrl("https://마카롱.png")
                .options(getCreateOptions())
                .build();
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .storeId(1L)
                .name("디디얌 마카롱 수제 뚱카롱 32종")
                .category(Category.MACARON)
                .thumbnailUrl("https://마카롱.png")
                .description("마카롱에 최고급프랑스산 발효버터를 사용합니다:)")
                .descriptionUrl("https://마카롱.png")
                .options(getOptionDto())
                .orderCount(0)
                .heartCount(0)
                .price(getOptionDto().get(0).getPrice())
                .build();
        given(storeItemService.createItem(anyLong(), any()))
                .willReturn(itemDto);
        //when

        //then
        // asciidoc
        mvc.perform(post("/api/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("create-item",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("아이쳄 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("price").description("아이템 첫번째 옵션 가격"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("orderCount").description("아이템 주문 횟수"),
                                        fieldWithPath("heartCount").description("아이템 찜 횟수")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("create-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어에 아이템과 옵션을 등록")
                                .summary("아이템 등록")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("아이쳄 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("price").description("아이템 첫번째 옵션 가격"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("orderCount").description("아이템 주문 횟수"),
                                        fieldWithPath("heartCount").description("아이템 찜 횟수")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void updateItem() throws Exception {
        //given
        UpdateItem form = UpdateItem.builder()
                .id(1L)
                .storeId(1L)
                .name("디디얌 마카롱 수제 뚱카롱 32종")
                .category(Category.MACARON)
                .thumbnailUrl("https://마카롱.png")
                .description("마카롱에 최고급프랑스산 발효버터를 사용합니다:)")
                .descriptionUrl("https://마카롱.png")
                .options(getCreateOptions())
                .build();
        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .storeId(1L)
                .name("디디얌 마카롱 수제 뚱카롱 32종")
                .category(Category.MACARON)
                .thumbnailUrl("https://마카롱.png")
                .description("마카롱에 최고급프랑스산 발효버터를 사용합니다:)")
                .descriptionUrl("https://마카롱.png")
                .options(getOptionDto())
                .orderCount(0)
                .heartCount(0)
                .price(getOptionDto().get(0).getPrice())
                .build();
        given(storeItemService.updateItem(anyLong(), any()))
                .willReturn(itemDto);
        //when

        //then
        // asciidoc
        mvc.perform(put("/api/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-item",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("id").description("아이템 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("아이템 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("price").description("아이템 첫번째 옵션 가격"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("orderCount").description("아이템 주문 횟수"),
                                        fieldWithPath("heartCount").description("아이템 찜 횟수")
                                )

                        )
                );

        // openapi3
        mvc.perform(put("/api/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어에 아이템과 옵션을 업데이트")
                                .summary("아이템 업데이트")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("id").description("아이템 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("아이쳄 id"),
                                        fieldWithPath("storeId").description("스토어 id"),
                                        fieldWithPath("name").description("아이템 이름"),
                                        fieldWithPath("category").description("아이템 카테고리"),
                                        fieldWithPath("thumbnailUrl").description("아이템 썸네일 이미지 url"),
                                        fieldWithPath("description").description("아이템 설명"),
                                        fieldWithPath("descriptionUrl").description("아이템 설명 이미지 url"),
                                        fieldWithPath("price").description("아이템 첫번째 옵션 가격"),
                                        fieldWithPath("options[].id").description("아이템 옵션 id"),
                                        fieldWithPath("options[].name").description("아이템 옵션 이름"),
                                        fieldWithPath("options[].quantity").description("아이템 옵션 수량"),
                                        fieldWithPath("options[].price").description("아이템 옵션 가격"),
                                        fieldWithPath("orderCount").description("아이템 주문 횟수"),
                                        fieldWithPath("heartCount").description("아이템 찜 횟수")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void deleteItem() throws Exception {
        //given

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        DeleteItem deleteItem = DeleteItem.builder()
                .itemName("디디얌 마카롱 수제 뚱카롱 32종")
                .deletedAt(LocalDateTime.now())
                .build();
        given(storeItemService.deleteItem(anyLong(), anyLong()))
                .willReturn(deleteItem);
        //when

        //then
        // asciidoc
        mvc.perform(delete("/api/store/item")
                        .header("Authorization", "token")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-item",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("id").description("삭제 원하는 아이템 id")
                                ),
                                responseFields(
                                        fieldWithPath("itemName").description("삭제된 아이템 이름"),
                                        fieldWithPath("deletedAt").description("삭제 날짜/시간 정보")

                                )

                        )
                );

        // openapi3
        mvc.perform(delete("/api/store/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andDo(document("delete-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어에서 아이템과 옵션을 삭제")
                                .summary("아이템 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("id").description("삭제 원하는 아이템 id")
                                )
                                .responseFields(
                                        fieldWithPath("itemName").description("삭제된 아이템 이름"),
                                        fieldWithPath("deletedAt").description("삭제 날짜/시간 정보")
                                )
                                .build()

                        )
                ));
    }

    private List<CreateOption> getCreateOptions() {
        List<CreateOption> options = new ArrayList<>();
        options.add(CreateOption.builder()
                .name("조리퐁")
                .quantity(100)
                .price(2500)
                .build());
        options.add(CreateOption.builder()
                .name("딸기바나나")
                .quantity(100)
                .price(2500)
                .build());
        options.add(CreateOption.builder()
                .name("레드벨벳")
                .quantity(100)
                .price(2500)
                .build());
        return options;
    }

    private List<OptionDto> getOptionDto() {
        List<OptionDto> options = new ArrayList<>();
        options.add(OptionDto.builder()
                .id(1L)
                .name("조리퐁")
                .quantity(100)
                .price(2500)
                .build());
        options.add(OptionDto.builder()
                .id(2L)
                .name("딸기바나나")
                .quantity(100)
                .price(2500)
                .build());
        options.add(OptionDto.builder()
                .id(3L)
                .name("레드벨벳")
                .quantity(100)
                .price(2500)
                .build());
        return options;
    }


}