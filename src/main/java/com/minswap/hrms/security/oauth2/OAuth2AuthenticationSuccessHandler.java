package com.minswap.hrms.security.oauth2;

import com.minswap.hrms.configuration.AppConfig;
import com.minswap.hrms.security.TokenProvider;
import com.minswap.hrms.util.CookieUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;

	private final AppConfig appConfig;

	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		OAuth2User user = (OAuth2User) authentication.getPrincipal();
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		String token = tokenProvider.createToken(authentication);

		if (CollectionUtils.isEmpty(authentication.getAuthorities())) {
			authentication.setAuthenticated(false);
			token = null;
		}

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			token = null;
		}

		return UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("token", token)
				.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);

		return appConfig.getAuthorizedRedirectUris()
				.stream()
				.anyMatch(authorizedRedirectUri -> {
					// Only validate host and port. Let the clients use different paths if they want to
					URI authorizedURI = URI.create(authorizedRedirectUri);
					return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
							&& authorizedURI.getPort() == clientRedirectUri.getPort();
				});
	}
}