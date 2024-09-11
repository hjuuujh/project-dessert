package com.zerobase.storeapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.zerobase.storeapi.domain.dto.StoreDto;
import com.zerobase.storeapi.service.StoreSearchService;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StoreSearchController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class StoreSearchControllerTest {
    @MockBean
    private StoreSearchService storeSearchService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    public void setUp(WebApplicationContext context,
                      RestDocumentationContextProvider restDocumentation) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void searchStoreByKeyword() throws Exception {
        //given
        List<StoreDto> stores = getStores();
        List<StoreDto> searchedStores = stores.stream().filter(store -> store.getDeletedAt() == null && store.getName().contains("1"))
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<StoreDto> searchStores = new PageImpl<>(searchedStores, pageable, searchedStores.size());

        given(storeSearchService.searchStoreByKeyword(anyString(), any()))
                .willReturn(searchStores);

        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/search")
                        .param("keyword", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-store-by-keyword",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("content[].sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("content[].name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("content[].description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("content[].thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("content[].followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("content[].deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜"),

                                        fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("pageable type")

                                )
                        )
                );
        // openapi3
        mvc.perform(get("/api/store/search")
                        .param("keyword", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-store-by-keyword",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 스토어 검색")
                                .summary("키워드로 스토어 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("content[].sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("content[].name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("content[].description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("content[].thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("content[].followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("content[].deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜"),

                                        fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("pageable type")

                                ).build()
                        )
                ));

    }

    @Test
    void searchStoreByFollow() throws Exception {
        //given
        List<StoreDto> stores = getStores();
        List<StoreDto> searchedStores = stores.stream().filter(store -> store.getDeletedAt() == null && store.getName().contains("1"))
                .sorted(Comparator.comparing(StoreDto::getFollowCount).reversed())
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<StoreDto> searchStores = new PageImpl<>(searchedStores, pageable, searchedStores.size());

        given(storeSearchService.searchStoreByFollowOrder(anyString(), any()))
                .willReturn(searchStores);

        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/search/follow")
                        .param("keyword", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-store-by-follower",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("content[].sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("content[].name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("content[].description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("content[].thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("content[].followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("content[].deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜"),

                                        fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("pageable type")

                                )
                        )
                );
        // openapi3
        mvc.perform(get("/api/store/search/follow")
                        .param("keyword", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-store-by-follower",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 검색 결과를 팔로우 순으로 정렬")
                                .summary("팔로우순 스토어 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("content[].sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("content[].name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("content[].description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("content[].thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("content[].followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("content[].deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜"),

                                        fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비어있는지 여부"),

                                        fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재페이지 번호"),
                                        fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("한 페이지당 조회할 데이터 개수"),
                                        fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("몇번째 데이터인지 (0부터 시작)"),
                                        fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 포함하는지 여부"),
                                        fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징 정보를 안포함하는지 여부"),

                                        fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 개수"),
                                        fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("테이블 총 데이터 개수"),
                                        fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("요청 페이지에서 조회된 데이터 개수"),
                                        fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("한 페이지 당 조회할 데이터 개수"),


                                        fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 됐는지 여부"),
                                        fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 안됐는지 여부"),
                                        fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),

                                        fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("데이터가 비었는지 여부"),
                                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("pageable type")

                                ).build()
                        )
                ));

    }

    @Test
    void searchStore() throws Exception {
        //given
        List<StoreDto> stores = getStores();
        StoreDto storeDto = stores.get(0);

        given(storeSearchService.searchStore(anyLong()))
                .willReturn(storeDto);

        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/search/detail")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-store-detail",
                                requestParameters(
                                        parameterWithName("id").description("상세정보 알고싶은 스토어의 id")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜")

                                )
                        )
                );
        // openapi3
        mvc.perform(get("/api/store/search/detail")
                        .param("id", "1")
                )
                .andExpect(status().isOk())
                .andDo(document("search-store-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("스토어의 상세정보 검색")
                                .summary("스토어 상세정보 검색")
                                .requestParameters(
                                        parameterWithName("id").description("상세 정보를 알고싶은 스토어의 id")
                                ).
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("스토어 아이디"),
                                        fieldWithPath("sellerId").type(JsonFieldType.NUMBER).description("스토어 셀러 아이디"),
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("스토어 이름"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("스토어 설명"),
                                        fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING).description("스토어 썸네일 url"),
                                        fieldWithPath("followCount").type(JsonFieldType.NUMBER).description("스토어 팔로우된 횟수"),
                                        fieldWithPath("deletedAt").type(JsonFieldType.NULL).description("스토어 삭제된 경우 날짜")
                                ).build()
                        )
                ));

    }

    private List<StoreDto> getStores() {
        List<StoreDto> stores = new ArrayList<>();
        stores.add(StoreDto.builder()
                .id(1L)
                .sellerId(1L)
                .thumbnailUrl("썸네일 url")

                .name("매장1")
                .description("매장1 설명")
                .followCount(20)
                .deletedAt(null)
                .build());
        stores.add(StoreDto.builder()
                .id(2L)
                .sellerId(1L)
                .thumbnailUrl("썸네일 url")

                .name("매장2")
                .description("매장1 설명")
                .followCount(10)
                .deletedAt(null)
                .build());
        stores.add(StoreDto.builder()
                .id(3L)
                .sellerId(1L)
                .thumbnailUrl("썸네일 url")

                .name("매장3")
                .description("매장1 설명")
                .followCount(20)
                .deletedAt(null)
                .build());
        stores.add(StoreDto.builder()
                .id(4L)
                .sellerId(1L)
                .thumbnailUrl("썸네일 url")

                .name("매장11")
                .description("매장1 설명")
                .followCount(100)
                .deletedAt(null)
                .build());

        return stores;
    }
}