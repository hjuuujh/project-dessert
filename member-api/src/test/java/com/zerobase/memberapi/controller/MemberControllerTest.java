package com.zerobase.memberapi.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.memberapi.domain.member.dto.CustomerDto;
import com.zerobase.memberapi.domain.member.dto.SellerDto;
import com.zerobase.memberapi.domain.member.form.SignIn;
import com.zerobase.memberapi.domain.member.form.SignUp;
import com.zerobase.memberapi.security.TokenProvider;
import com.zerobase.memberapi.service.CustomerService;
import com.zerobase.memberapi.service.SellerService;
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

import java.util.Arrays;
import java.util.HashSet;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
@ExtendWith(RestDocumentationExtension.class)
@MockBean(JpaMetamodelMappingContext.class) // @EnableJpaAuditing 사용위해
class MemberControllerTest {
    @MockBean
    private CustomerService customerService;
    @MockBean
    private SellerService sellerService;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private ValidationErrorResponse validationErrorResponse;


    @Autowired
    private MockMvc mvc;

    // json parsing 위함
    // JSON 컨텐츠를 Java 객체로 deserialization 하거나 Java 객체를 JSON으로 serialization 할 때 사용하는 Jackson 라이브러리의 클래스
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
    void registerCustomer() throws Exception {
        //given
        CustomerDto customerDto = CustomerDto.builder()
                .id(1L)
                .email("user1@gmail.com")
                .name("user1")
                .phone("01012345678")
                .roles(Arrays.asList("ROLE_CUSTOMER"))
                .followList(new HashSet<>())
                .heartList(new HashSet<>())
                .balance(0)
                .build();

        SignUp form = SignUp.builder()
                .email("user1@gmail.com")
                .name("user1")
                .phone("01012345678")
                .password("qwerty")
                .roles(Arrays.asList("ROLE_CUSTOMER"))
                .build();

        given(customerService.registerCustomer(any()))
                .willReturn(customerDto);
        //when

        //then
        // asciidoc
        mvc.perform(post("/api/member/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("customer-signup", requestFields(
                                        fieldWithPath("email").description("사용자 email"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("phone").description("사용자 번호"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("roles").description("사용자 역할")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("사용자 id"),
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("phone").description("사용자 번호"),
                                        fieldWithPath("roles").description("사용자 역할 (고객)"),
                                        fieldWithPath("balance").description("잔여 포인트"),
                                        fieldWithPath("followList").description("팔로우 스토어 리스트"),
                                        fieldWithPath("heartList").description("좋아요 아이템 리스트")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/member/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("customer-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("role이 고객인 경우의 회원가입")
                                        .summary("고객의 화원 가입")
                                        .requestFields(
                                                fieldWithPath("email").description("사용자 email"),
                                                fieldWithPath("name").description("사용자 이름"),
                                                fieldWithPath("phone").description("사용자 번호"),
                                                fieldWithPath("password").description("사용자 비밀번호"),
                                                fieldWithPath("roles").description("사용자 역할")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("사용자 id"),
                                                fieldWithPath("email").description("사용자 이메일"),
                                                fieldWithPath("name").description("사용자 이름"),
                                                fieldWithPath("phone").description("사용자 번호"),
                                                fieldWithPath("roles").description("사용자 역할 (고객)"),
                                                fieldWithPath("balance").description("잔여 포인트"),
                                                fieldWithPath("followList").description("팔로우 스토어 리스트"),
                                                fieldWithPath("heartList").description("좋아요 아이템 리스트")
                                        )
                                        .build()

                        )
                ));
    }

    @Test
    void registerSeller() throws Exception {
        //given
        SellerDto sellerDto = SellerDto.builder()
                .id(2L)
                .email("user2@gmail.com")
                .name("user2")
                .phone("01012345678")
                .roles(Arrays.asList("ROLE_SELLER"))
                .income(0)
                .build();

        SignUp form = SignUp.builder()
                .email("user2@gmail.com")
                .name("user2")
                .phone("01012345678")
                .password("qwerty")
                .roles(Arrays.asList("ROLE_SELLER"))
                .build();

        given(sellerService.registerSeller(any()))
                .willReturn(sellerDto);
        //when

        //then
        // asciidoc
        mvc.perform(post("/api/member/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("seller-signup",
                                requestFields(
                                        fieldWithPath("email").description("사용자 email"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("phone").description("사용자 번호"),
                                        fieldWithPath("password").description("사용자 비밀번호"),
                                        fieldWithPath("roles").description("사용자 역할")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("사용자 id"),
                                        fieldWithPath("email").description("사용자 이메일"),
                                        fieldWithPath("name").description("사용자 이름"),
                                        fieldWithPath("phone").description("사용자 번호"),
                                        fieldWithPath("roles").description("사용자 역할 (고객)"),
                                        fieldWithPath("income").description("수입")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/member/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("seller-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("role이 셀러인 경우의 회원가입")
                                        .summary("셀러의 회원가입")
                                        .requestFields(
                                                fieldWithPath("email").description("사용자 email"),
                                                fieldWithPath("name").description("사용자 이름"),
                                                fieldWithPath("phone").description("사용자 번호"),
                                                fieldWithPath("password").description("사용자 비밀번호"),
                                                fieldWithPath("roles").description("사용자 역할")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("사용자 id"),
                                                fieldWithPath("email").description("사용자 이메일"),
                                                fieldWithPath("name").description("사용자 이름"),
                                                fieldWithPath("phone").description("사용자 번호"),
                                                fieldWithPath("roles").description("사용자 역할 (고객)"),
                                                fieldWithPath("income").description("수입")
                                        )
                                        .build()

                        )
                ));
    }

    @Test
    void signInCustomer() throws Exception {
        //given
        CustomerDto customerDto = CustomerDto.builder()
                .id(1L)
                .email("user1@gmail.com")
                .name("user1")
                .phone("01012345678")
                .roles(Arrays.asList("ROLE_CUSTOMER"))
                .followList(new HashSet<>())
                .heartList(new HashSet<>())
                .balance(0)
                .build();

        given(customerService.signInMember(any()))
                .willReturn(customerDto);

        given(tokenProvider.generateToken(anyLong(), anyString(), any()))
                .willReturn("token");

        SignIn form = SignIn.builder()
                .email("user1@gmail.com")
                .password("qwerty")
                .build();

        //when

        //then
        // asciidoc
        mvc.perform(post("/api/member/signin/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("customer-signin",
                                requestFields(
                                        fieldWithPath("email").description("사용자 email"),
                                        fieldWithPath("password").description("사용자 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("token").description("로그인 성공해 얻은 토큰")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/member/signin/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("customer-signin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("role이 고객인 경우의 로그인")
                                        .summary("고객의 로그인")
                                        .requestFields(
                                                fieldWithPath("email").description("사용자 email"),
                                                fieldWithPath("password").description("사용자 비밀번호")
                                        )
                                        .responseFields(
                                                fieldWithPath("token").description("로그인 성공해 얻은 토큰")
                                        )
                                        .build()

                        )
                ));
    }

    @Test
    void signInSeller() throws Exception {
        //given
        SellerDto sellerDto = SellerDto.builder()
                .id(2L)
                .email("user2@gmail.com")
                .name("user2")
                .phone("01012345678")
                .roles(Arrays.asList("ROLE_SELLER"))
                .income(0)
                .build();

        given(sellerService.signInMember(any()))
                .willReturn(sellerDto);

        given(tokenProvider.generateToken(anyLong(), anyString(), any()))
                .willReturn("token");

        SignIn form = SignIn.builder()
                .email("user2@gmail.com")
                .password("qwerty")
                .build();

        //when

        //then
        // asciidoc
        mvc.perform(post("/api/member/signin/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("seller-signin",
                                requestFields(
                                        fieldWithPath("email").description("사용자 email"),
                                        fieldWithPath("password").description("사용자 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("token").description("로그인 성공해 얻은 토큰")
                                )

                        )
                );

        // openapi3
        mvc.perform(post("/api/member/signin/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                form
                        )))
                .andExpect(status().isOk())
                .andDo(document("seller-signin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .description("role이 셀러인 경우의 로그인")
                                        .summary("셀러의 로그인")
                                        .requestFields(
                                                fieldWithPath("email").description("사용자 email"),
                                                fieldWithPath("password").description("사용자 비밀번호")
                                        )
                                        .responseFields(
                                                fieldWithPath("token").description("로그인 성공해 얻은 토큰")
                                        )
                                        .build()

                        )
                ));
    }
}