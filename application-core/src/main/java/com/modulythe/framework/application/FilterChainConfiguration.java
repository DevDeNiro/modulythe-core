package com.modulythe.framework.application;
// Voir la doc pour httpSecurity pour Oauth

//@Configuration
//@ConditionalOnWebApplication
//@EnableWebSecurity
//@EnableMethodSecurity
//@ConditionalOnProperty(prefix = "ddd.security", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FilterChainConfiguration {

//    private static final Logger LOGGER = LoggerFactory.getLogger(FilterChainConfiguration.class);

    //    @Value("${exception.pattern}")
    private String securityException = "";

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OpaqueTokenInstropector compositeIntrospector) throws Exception {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

        securityException = "Security exception: ";

        http
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(req -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque
                                .introspector(compositeIntrospector)));

        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector compositeIntrospector(OpaqueTokenIntrospector jwtIntrospector,
                                                         OpaqueTokenIntrospector jwkIntrospector) {
        return new DelegatingOpaqueTokenIntrospector(jwtIntrospector, jwkIntrospector);
    }

     */
}
