package com.zerobase.storeapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.zerobase.storeapi.domain.dto.ItemDto;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.form.item.SearchItem;
import com.zerobase.storeapi.domain.type.Category;
import com.zerobase.storeapi.service.StoreItemSearchService;
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

@WebMvcTest(controllers = StoreItemSearchController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class StoreItemSearchServiceTest {
    @MockBean
    private StoreItemSearchService storeItemSearchService;

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
    void searchItemByKeyword() throws Exception {
        //given
        List<Item> items = getItems();
        List<SearchItem> searchItems = items.stream().filter(item -> item.getName().contains("쿠키"))
                .sorted(Comparator.comparing(Item::getOrderCount).reversed())
                .map(SearchItem::from)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SearchItem> result = new PageImpl<>(searchItems, pageable, searchItems.size());

        given(storeItemSearchService.searchItemByKeyword(anyString(), any()))
                .willReturn(result);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/order")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-item-by-keyword",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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
        mvc.perform(get("/api/store/item/search/order")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-item-by-keyword",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 아이템 검색후 주문순으로 정렬")
                                .summary("키워드로 아이템 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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

                                ).build()
                        )
                ));
    }

    @Test
    void searchItemByCategory() throws Exception {
        //given
        List<Item> items = getItems();
        List<SearchItem> searchItems = items.stream().filter(item -> item.getCategory() == Category.COOKIE)
                .map(SearchItem::from)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SearchItem> result = new PageImpl<>(searchItems, pageable, searchItems.size());

        given(storeItemSearchService.searchItemByCategory(anyString(), anyString(), any()))
                .willReturn(result);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/category")
                        .param("keyword", "쿠키")
                        .param("category", "cookie")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-item-by-category",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("category").description("검색 카테고리"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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
        mvc.perform(get("/api/store/item/search/category")
                        .param("keyword", "쿠키")
                        .param("category", "cookie")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-item-by-category",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 아이템 검색후 카테고리로 필터")
                                .summary("카테고리로 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("category").description("검색 카테고리"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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

                                ).build()
                        )
                ));
    }

    @Test
    void searchItemByLowerPrice() throws Exception {
        //given
        List<Item> items = getItems();
        List<SearchItem> searchItems = items.stream().filter(item -> item.getName().contains("쿠키"))
                .sorted(Comparator.comparing(Item::getPrice))
                .map(SearchItem::from)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SearchItem> result = new PageImpl<>(searchItems, pageable, searchItems.size());

        given(storeItemSearchService.searchItemByLowerPrice(anyString(), any()))
                .willReturn(result);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/price/low")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-item-by-low-price",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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
        mvc.perform(get("/api/store/item/search/price/low")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-item-by-low-price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 아이템 검색후 낮은 가격순으로 정렬")
                                .summary("낮은 가격순으로 아이템 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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

                                ).build()
                        )
                ));
    }

    @Test
    void searchItemByHighPrice() throws Exception {
        //given
        List<Item> items = getItems();
        List<SearchItem> searchItems = items.stream().filter(item -> item.getName().contains("쿠키"))
                .sorted(Comparator.comparing(Item::getPrice).reversed())
                .map(SearchItem::from)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SearchItem> result = new PageImpl<>(searchItems, pageable, searchItems.size());

        given(storeItemSearchService.searchItemByHighPrice(anyString(), any()))
                .willReturn(result);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/price/high")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-item-by-high-price",
                                requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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
        mvc.perform(get("/api/store/item/search/price/high")
                        .param("keyword", "쿠키")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-item-by-high-price",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("키워드가 포함된 아이템 검색후 높은 가격순으로 정렬")
                                .summary("높은 가격순으로 아이템 검색")
                                .requestParameters(
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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

                                ).build()
                        )
                ));
    }

    @Test
    void searchStoreItem() throws Exception {
        //given
        List<Item> items = getItems();
        List<SearchItem> searchItems = items.stream().filter(item -> item.getStoreId() == 1L)
                .map(SearchItem::from)
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 5);
        Page<SearchItem> result = new PageImpl<>(searchItems, pageable, searchItems.size());

        given(storeItemSearchService.searchStoreItem(anyLong(), any()))
                .willReturn(result);
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/store")
                        .param("storeId", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-store-item",
                                requestParameters(
                                        parameterWithName("storeId").description("아이템 정보 원하는 스토어 id"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ),
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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
        mvc.perform(get("/api/store/item/search/store")
                        .param("storeId", "1")
                        .param("page", "0")
                        .param("size", "5")
                )
                .andExpect(status().isOk())
                .andDo(document("search-store-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 스토어의 모든 아이템 페이지 형식으로 리턴")
                                .summary("특정 스토어의 아이템 확인")
                                .requestParameters(
                                        parameterWithName("storeId").description("아이템 정보 원하는 스토어 id"),
                                        parameterWithName("page").description("검색 페이지"),
                                        parameterWithName("size").description("검색 사이즈")
                                ).
                                responseFields(
                                        fieldWithPath("content[].itemId").description("아이템 id"),
                                        fieldWithPath("content[].itemName").description("아이템 이름"),
                                        fieldWithPath("content[].storeId").description("스토어 id"),
                                        fieldWithPath("content[].storeName").description("스토어 이름"),
                                        fieldWithPath("content[].thumbnailUrl").description("아이템 썸네일 url"),
                                        fieldWithPath("content[].price").description("아이템 가격"),

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

                                ).build()
                        )
                ));
    }

    @Test
    void searchItem() throws Exception {
        //given
        Item item = getItems().get(0);

        given(storeItemSearchService.searchItem(anyLong()))
                .willReturn(ItemDto.from(item));
        //when

        //then
        // asciidoc
        mvc.perform(get("/api/store/item/search/detail")
                        .param("itemId", "1")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("search-store-item-detail",
                                requestParameters(
                                        parameterWithName("itemId").description("상세 정보 원하는 아이템 id")
                                ),
                        responseFields(
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
        mvc.perform(get("/api/store/item/search/detail")
                        .param("itemId", "1")
                )
                .andExpect(status().isOk())
                .andDo(document("search-store-item-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(ResourceSnippetParameters.builder()
                                .description("특정 아이템의 옵션 포함 상세정보 확인")
                                .summary("특정 스토어의 아이템 확인")
                                .requestParameters(
                                        parameterWithName("itemId").description("상세 정보 원하는 아이템 id")

                                ).
                                responseFields(
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
                                ).build()
                        )
                ));
    }

    private List<Item> getItems() {

        List<Option> options = new ArrayList<>();
        options.add(Option.builder()
                .id(1L)
                .name("조리퐁")
                .quantity(100)
                .price(2500)
                .build());
        options.add(Option.builder()
                .id(2L)
                .name("딸기바나나")
                .quantity(10)
                .price(2500)
                .build());
        options.add(Option.builder()
                .id(3L)
                .name("레드벨벳")
                .quantity(10)
                .price(2500)
                .build());
        List<Item> items = new ArrayList<>();
        items.add(Item.builder()
                .id(1L)
                .name("디디얌 마카롱 수제 뚱카롱 32종")
                .storeId(1L)
                .storeName("디디얌")
                .category(Category.MACARON)
                .thumbnailUrl("https://마카롱.png")
                .options(options)
                .price(2500)
                .orderCount(1)
                .heartCount(6)
                .build());
        items.add(Item.builder()
                .id(2L)
                .name("촉촉한 쿠키 에그타르트")
                .storeId(1L)
                .storeName("디디얌")
                .category(Category.TART)
                .thumbnailUrl("https://에그타르트.png")
                .price(3000)
                .orderCount(2)
                .heartCount(31)
                .build());
        items.add(Item.builder()
                .id(3L)
                .name("새콤달콤😖 레몬케이크")
                .storeId(1L)
                .storeName("디디얌")
                .category(Category.CAKE)
                .thumbnailUrl("https://레몬케이크.png")
                .price(2800)
                .orderCount(4)
                .heartCount(16)
                .build());
        items.add(Item.builder()
                .id(4L)
                .name("꿀고구마 티그레")
                .storeId(2L)
                .storeName("달달구리해닮")
                .category(Category.COOKIE)
                .thumbnailUrl("https://티그레.png")
                .price(17000)
                .orderCount(3)
                .heartCount(61)
                .build());
        items.add(Item.builder()
                .id(5L)
                .name("카라멜버터샌드쿠키세트")
                .storeId(2L)
                .storeName("달달구리해닮")
                .category(Category.COOKIE)

                .thumbnailUrl("https://카라멜버터샌드쿠키세트.png")

                .price(27500)
                .orderCount(6)
                .heartCount(33)
                .build());
        items.add(Item.builder()
                .id(6L)
                .name("샹티크림끌레오르쿠키")
                .storeId(2L)
                .storeName("달달구리해닮")
                .category(Category.COOKIE)

                .thumbnailUrl("https://샹티크림끌레오르.png")

                .price(26000)
                .orderCount(74)
                .heartCount(22)
                .build());
        items.add(Item.builder()
                .id(7L)
                .storeId(3L)
                .storeName("바스켓베이커리")
                .name("노밀가루,글루텐프리 바스켓베이커리 스콘 쿠키")
                .category(Category.SCONE)

                .thumbnailUrl("https://스콘.png")

                .price(5200)
                .orderCount(55)
                .heartCount(11)
                .build());
        items.add(Item.builder()
                .id(8L)
                .storeId(3L)
                .storeName("바스켓베이커리")
                .name("노밀가루 바스켓베이커리 파운드케이크")
                .category(Category.CAKE)

                .thumbnailUrl("https://파운드케이크.png")

                .price(6200)
                .orderCount(32)
                .heartCount(53)
                .build());
        items.add(Item.builder()
                .id(9L)
                .name("노밀가루 바스켓베이커리 쌀피칸쿠키")
                .storeId(3L)
                .storeName("바스켓베이커리")
                .category(Category.BREAD)

                .thumbnailUrl("https://쌀피칸브라우니.png")

                .price(5500)
                .orderCount(21)
                .heartCount(43)
                .build());
        return items;

    }

}