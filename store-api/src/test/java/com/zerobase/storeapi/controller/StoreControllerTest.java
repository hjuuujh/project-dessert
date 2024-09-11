package com.zerobase.storeapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.domain.form.store.DeleteStore;
import com.zerobase.storeapi.domain.form.store.RegisterStore;
import com.zerobase.storeapi.domain.form.store.UpdateStore;
import com.zerobase.storeapi.service.StoreService;
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

import java.time.LocalDate;

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

@WebMvcTest(controllers = StoreController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class StoreControllerTest {
    @MockBean
    private StoreService storeService;
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
    void registerStore() throws Exception {
        //given
        RegisterStore form = RegisterStore.builder()
                .name("수제청 청이")
                .description("신선한 과일과 채소에 정성을 더한 핸드메이드 과일청&주스")
                .thumbnailUrl("https://청이.png")
                .build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        StoreDto storeDto = StoreDto.builder()
                .id(1L)
                .sellerId(1L)
                .name("수제청 청이")
                .description("신선한 과일과 채소에 정성을 더한 핸드메이드 과일청&주스")
                .thumbnailUrl("https://청이.png")
                .followCount(0)
                .deletedAt(null)
                .build();

        given(storeService.registerStore(anyLong(), any()))
                .willReturn(storeDto);

        //when

        //then
        // asciidoc
        mvc.perform(post("/api/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("register-store",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("스토어 id"),
                                        fieldWithPath("sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").description("스토어가 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("register-store",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어를 등록")
                                .summary("스토어 등록")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("스토어 id"),
                                        fieldWithPath("sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").description("스토어가 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void updateStore() throws Exception {
        //given
        UpdateStore form = UpdateStore.builder()
                .id(1L)
                .name("수제청 청이")
                .description("신선한 과일과 채소에 정성을 더한 핸드메이드 과일청&주스")
                .thumbnailUrl("https://청이.png")
                .build();

        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);

        StoreDto storeDto = StoreDto.builder()
                .id(1L)
                .sellerId(1L)
                .name("수제청 청이")
                .description("신선한 과일과 채소에 정성을 더한 핸드메이드 과일청&주스")
                .thumbnailUrl("https://청이.png")
                .followCount(0)
                .deletedAt(null)
                .build();

        given(storeService.updateStore(anyLong(), any()))
                .willReturn(storeDto);

        //when

        //then
        // asciidoc
        mvc.perform(put("/api/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-store",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestFields(
                                        fieldWithPath("id").description("수정원하는 스토어의 id"),
                                        fieldWithPath("name").description("변경할 스토어 이름"),
                                        fieldWithPath("description").description("변경할 스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("변경할 스토어 썸네일 url")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("스토어 id"),
                                        fieldWithPath("sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").description("스토어가 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )

                        )
                );

        // openapi3
        mvc.perform(put("/api/store")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("update-store",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어 정보를 변경")
                                .summary("스토어 정보 변경")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestFields(
                                        fieldWithPath("id").description("수정원하는 스토어의 id"),
                                        fieldWithPath("name").description("변경할 스토어 이름"),
                                        fieldWithPath("description").description("변경할 스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("변경할 스토어 썸네일 url")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("스토어 id"),
                                        fieldWithPath("sellerId").description("스토어 셀러 id"),
                                        fieldWithPath("name").description("스토어 이름"),
                                        fieldWithPath("description").description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").description("스토어가 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )
                                .build()

                        )
                ));
    }

    @Test
    void deleteStore() throws Exception {
        //given
        DeleteStore deleteStore = DeleteStore.builder().deletedAt(LocalDate.now()).build();


        given(memberClient.getMemberId(anyString()))
                .willReturn(1L);


        given(storeService.deleteStore(anyLong(), any()))
                .willReturn(deleteStore);

        //when

        //then
        // asciidoc
        mvc.perform(patch("/api/store")
                        .param("id", "1")
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-store",
                                requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                ),
                                requestParameters(
                                        parameterWithName("id").description("삭제 원하는 스토어 id")
                                ),
                                responseFields(
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )
                        )
                );

        // openapi3
        mvc.perform(patch("/api/store").param("id", "1")
                        .param("id", "1")
                        .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andDo(document("delete-store",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("셀러가 자신의 스토어를 소프트 삭제")
                                .summary("스토어 정보 삭제")
                                .requestHeaders(
                                        headerWithName("Authorization").description("JWT Bearer 인증 token")
                                )
                                .requestParameters(
                                        parameterWithName("id").description("삭제 원하는 스토어 id")
                                )
                                .responseFields(
                                        fieldWithPath("deletedAt").description("스토어가 삭제된 경우 날짜 정보")
                                )
                                .build()

                        )
                ));
    }

}